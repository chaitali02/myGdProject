/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.intelligence;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.expressions.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeDomain;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.DQIntelligence;
import com.inferyx.framework.domain.DQRecExec;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.ResultType;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.CheckType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SaveMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.MessageStatus;
import com.inferyx.framework.service.MetadataServiceImpl;

/**
 * @author Ganesh
 *
 */
@Component
public class DQRecOperator {

	static final Logger logger = Logger.getLogger(DQRecOperator.class);
	
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private SparkExecutor<?> sparkExecutor;
	@Autowired
	private MetadataServiceImpl metadataServiceImpl;

	public List<DQIntelligence> genRecommendation(DQRecExec dqRecExec, ExecParams execParams, RunMode runMode, String samplePercent) throws Exception {
		try {
			List<DQIntelligence> recommendationList = new ArrayList<>();
			try {
				synchronized (dqRecExec.getUuid()) {
					dqRecExec = (DQRecExec) commonServiceImpl.setMetaStatus(dqRecExec, MetaType.dqrecExec,
							Status.Stage.RUNNING);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			String appUuid = commonServiceImpl.getApp().getUuid();
			float minThreshold = 0.0F;
			minThreshold = Float.parseFloat(commonServiceImpl.getConfigValue("framework.dataqual.sample.score.minthreshold"));
			
			MetaIdentifier execDependsOnMI = dqRecExec.getDependsOn().getRef();
			Datapod sourceDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(execDependsOnMI.getUuid(), execDependsOnMI.getVersion(), execDependsOnMI.getType().toString(), "N");
			
			Datasource sourceDS = commonServiceImpl.getDatasourceByObject(sourceDp);
			
			//***************** getting source data *****************//
			ExecContext execContext = commonServiceImpl.getExecContext(runMode);
			IExecutor exec = execFactory.getExecutor(execContext.toString());
			ResultSetHolder rsHolder = exec.executeSqlByDatasource(dqRecExec.getExec(), sourceDS, appUuid);
			if(rsHolder.getType().equals(ResultType.resultset)) {
				rsHolder = sparkExecutor.convertResultsetToDataframe(rsHolder);
			}
			
			List<DataQual> latestDQList = metadataServiceImpl.getAllDQByDatapod(sourceDp.getUuid());
			
			if(StringUtils.isBlank(samplePercent)) {
				samplePercent = commonServiceImpl.getConfigValue("framework.dataqual.sample.percent");
			} 
			
			//***************** gettting sample *****************//
			Dataset<Row> sampleDf = null;
			if(samplePercent.contains("%")) {
				String samplePerc = samplePercent.substring(0, samplePercent.lastIndexOf("%"));
				double sample = Double.parseDouble(samplePerc);
				Dataset<Row>[] splits = rsHolder.getDataFrame().randomSplit(new double[] {sample/100, (100 - sample)/100}, 12345);
				sampleDf = splits[0];
			} else {
				sampleDf = rsHolder.getDataFrame().limit(Integer.parseInt(samplePercent));
			}

			//***************** setting default properties *****************//
			String defaultPath = commonServiceImpl.getConfigValue("framework.schema.Path");
			defaultPath = defaultPath.endsWith("/") ? defaultPath : defaultPath.concat("/");
			String filePath = String.format("%s/%s/%s/", sourceDp.getUuid(), sourceDp.getVersion(), dqRecExec.getVersion());
			String tempTableName = String.format("%s_%s_%s", sourceDp.getUuid().replaceAll("-", "_"), sourceDp.getVersion(), dqRecExec.getVersion());
			
			String sampleFilePathUrl = defaultPath.concat(filePath).concat("sample");
			ResultSetHolder sampleHolder = new ResultSetHolder();
			sampleHolder.setDataFrame(sampleDf);
			sampleHolder.setType(ResultType.dataframe);

			//***************** saving sample *****************//
			String sampleTempTableName = String.format("%s_%s", tempTableName, "sample");
			sampleHolder = save(sampleHolder, sampleFilePathUrl, sampleTempTableName, true);
			sampleHolder.setCountRows(sampleHolder.getDataFrame().count());
			
			sampleHolder.setTableName(sampleTempTableName);
			
			//***************** check domain for recommendation *****************//
			recommendationList.addAll(domainCheck(sampleHolder, defaultPath, filePath, tempTableName, sourceDp, latestDQList, minThreshold));

			//***************** check domain for recommendation *****************//
			recommendationList.addAll(nullCheck(sampleHolder, defaultPath, filePath, tempTableName, sourceDp, latestDQList, minThreshold));
			
			dqRecExec.setIntelligenceResult(recommendationList);
			
			synchronized (dqRecExec.getUuid()) {
				dqRecExec = (DQRecExec) commonServiceImpl.setMetaStatus(dqRecExec, MetaType.dqrecExec,
						Status.Stage.COMPLETED);
			}
			
			List<String> tempTableList = new ArrayList<>();
			tempTableList.add(sampleTempTableName);
			sparkExecutor.dropTempTable(tempTableList);
			return recommendationList;
		}catch (Exception e) {
			e.printStackTrace();
			// Set status to FAILED
			try {
				synchronized (dqRecExec.getUuid()) {
					dqRecExec = (DQRecExec) commonServiceImpl.setMetaStatus(dqRecExec, MetaType.dqrecExec,
							Status.Stage.FAILED);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dqrecExec, dqRecExec.getUuid(), dqRecExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Data Qual recommendation failed ...", dependsOn);
			throw new java.lang.Exception((message != null) ? message : "Data Qual recommendation failed ...");
		}
	}

	public List<DQIntelligence> domainCheck(ResultSetHolder rsHolder, String defaultPath, String filePath,
			String defaultTempTableName, Datapod datapod, List<DataQual> latestDQList, float minThreshold)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NullPointerException, ParseException, IOException {
		String appUuid = commonServiceImpl.getApp().getUuid();

		List<String> tempTableList = new ArrayList<>();
		
		String[] dfColumns = rsHolder.getDataFrame().columns();
		List<AttributeDomain> attrDomainList = metadataServiceImpl.getDomainByApp(appUuid);

		//***************** applying regex for each domain vs each column of sample *****************//
		if(attrDomainList != null && !attrDomainList.isEmpty()) {
			StringBuilder regextQuery = new StringBuilder();
			for(AttributeDomain domain : attrDomainList) { 
				regextQuery.append("SELECT ");
				regextQuery.append("'").append(domain.getName()).append("'").append(" AS ").append("domain_name").append(", ");
				regextQuery.append("'").append(domain.getUuid()).append("'").append(" AS ").append("domain_uuid").append(", ");
				regextQuery.append("'").append(datapod.getUuid()).append("'").append(" AS ").append("datapod_uuid").append(", ");
				
				int i = 0;
				for(String colName : dfColumns) {
					String colCheck = colName.concat(" RLIKE (").concat("'").concat(domain.getRegEx()).concat("') ");
					regextQuery.append(caseWrapper(colCheck, colName));
					if(i < dfColumns.length - 1) {
						regextQuery.append(", ");
					}
					i++;
				}
				regextQuery.append(" FROM ").append(rsHolder.getTableName()).append(" ");
				regextQuery.append(" UNION ALL ");
			}
			
			String sql = regextQuery.substring(0, regextQuery.lastIndexOf(" UNION ALL "));
			String domainTempTableName = defaultTempTableName.concat("_").concat("domain_union");
			sparkExecutor.executeAndRegisterByTempTable(sql, domainTempTableName, true, appUuid).getDataFrame();
			
			tempTableList.add(domainTempTableName);
			
			//***************** calculating score for each column formula: ((no. of 'Y' in a column)/sampleTotalRows) *****************//
			String colScoreTempTable = defaultTempTableName.concat("_").concat("score");		
			tempTableList.add(colScoreTempTable);
			
			StringBuilder outerSqlBuilder = new StringBuilder("SELECT * FROM (");
			StringBuilder scoreCountBuilder = new StringBuilder();
			long sampleTotalRows = rsHolder.getCountRows();
			for(AttributeDomain domain : attrDomainList) {
				if(sampleTotalRows > 0) {
					for(String colName : dfColumns) {					
						scoreCountBuilder.append("SELECT domain_name, domain_uuid, datapod_uuid, "
								+ "(COUNT(" + colName + ")/" + sampleTotalRows + ") * 100 AS score_count, '"
								+ colName + "' AS score_column FROM " + domainTempTableName + " WHERE domain_name = '"
								+ domain.getName() + "' AND " + colName + " = 'Y' GROUP BY domain_name, score_column, domain_uuid, datapod_uuid");
						
						scoreCountBuilder.append(" UNION ALL ");
					}
				}
			}
			
//			minThreshold = 0.0f;
			String scoreSql = scoreCountBuilder.substring(0, scoreCountBuilder.lastIndexOf(" UNION ALL "));
			outerSqlBuilder.append(scoreSql).append(") WHERE score_count >= "+minThreshold);
			
			ResultSetHolder scoreCountHolder = sparkExecutor.executeAndRegisterByTempTable(outerSqlBuilder.toString(), colScoreTempTable, true, appUuid);
//			scoreCountHolder.getDataFrame().show(false);
			
			//***************** saving score *****************//
			String scoreFilePathUrl = defaultPath.concat(filePath).concat("score");
			save(scoreCountHolder, scoreFilePathUrl, null, false);
			
			List<DQIntelligence> domainRecommendation = getCheckTypeListForDomain(scoreCountHolder, attrDomainList, datapod, latestDQList);
			
			//***************** finding max score of a columns *****************//
//			String maxScoreCountSql = "SELECT domain_name, domain_uuid, datapod_uuid, score_count, score_column FROM "
//					+ colScoreTempTable+" WHERE score_count = (SELECT MAX(score_count) FROM "+colScoreTempTable+")";
//			sparkExecutor.executeAndRegisterByTempTable(maxScoreCountSql, null, false, appUuid);
//			maxScoreHolder.getDataFrame().show(false);
			
//			domainUnion.getDataFrame().show(Integer.parseInt(""+domainUnion.getCountRows()), false);
			
			//***************** dropping temporary tables created *****************//
			sparkExecutor.dropTempTable(tempTableList);
			return domainRecommendation;
		}
		return new ArrayList<>();
	}
	
	public Dataset<Row> addRowNumberToDF(Dataset<Row> df){
		return df.withColumn("rowNum", functions.row_number().over(Window.orderBy(df.columns()[df.columns().length-1])));
	}
	
	private String caseWrapper(String check, String colName) {
		StringBuilder caseBuilder = new StringBuilder(" CASE WHEN ").append(check).append(" THEN 'Y' ELSE 'N' END AS ").append(colName)
				.append(" ");
		return caseBuilder.toString();
	}
	
	public List<DQIntelligence> getCheckTypeListForDomain(ResultSetHolder rsHolder, List<AttributeDomain> attrDomainList,
			Datapod datapod, List<DataQual> latestDQList) {
		List<DQIntelligence> checkTypeList = new ArrayList<>();
		
		Dataset<Row> df = rsHolder.getDataFrame();
		Row[] rows = (Row[]) df.head(Integer.parseInt(""+df.count()));

		for(Row row : rows) {
			try {
				if(row.getDouble(3) > 0.0) {
					//********* 0: domain_name, 1: domain_uuid, 2: datapod_uuid, 3: score_count, 4: score_column *********//
		
					DQIntelligence dqIntelligence = new DQIntelligence();
					
					dqIntelligence.setCheckType(CheckType.DOMAIN);
					dqIntelligence.setSampleScore(Float.parseFloat(row.getDouble(3)+""));
					
					//********* setting attribute name *********//
					dqIntelligence.setAttributeName(getAttributeName(datapod, row.getString(4)));
					
					//********* setting check value  *********//
					for(AttributeDomain attributeDomain : attrDomainList) {
						if(attributeDomain.getUuid().equals(row.get(1).toString())) {
							MetaIdentifier ref = attributeDomain.getRef(MetaType.domain);
							ref.setName(attributeDomain.getName());
							dqIntelligence.setCheckValue(new MetaIdentifierHolder(ref));
							
							break;
						}
					}
					
					//********* setting dq creation check  *********//
					dqIntelligence.setCreated(getDQCreationCheck(latestDQList, row.getString(2)));
				
					checkTypeList.add(dqIntelligence);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		return checkTypeList;
	}
	
	public AttributeRefHolder getAttributeName(Datapod datapod, String attrName) {
		for(Attribute attribute : datapod.getAttributes()) {
			if(attribute.getName().equals(attrName)) {
				AttributeRefHolder attrRefHolder = new AttributeRefHolder();
				attrRefHolder.setRef(datapod.getRef(MetaType.datapod));
				attrRefHolder.setAttrId(attribute.getAttributeId().toString());
				attrRefHolder.setAttrName(attribute.getName());				
				return attrRefHolder;
			}
		}
		
		return null;
	}
	
	public boolean getDQCreationCheck(List<DataQual> latestDQList, String datapodUuid) {
		if(latestDQList != null && !latestDQList.isEmpty()) {
			for(DataQual dq : latestDQList) {
				if(dq.getDependsOn().getRef().getUuid().equals(datapodUuid)) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}

		return false;
	}
	
	public ResultSetHolder save(ResultSetHolder rsHolder, String filePathUrl, String tempTableName, boolean registerTempTable) throws IOException {		
		try {
			return sparkExecutor.registerAndPersistDataframe(rsHolder, null, SaveMode.OVERWRITE.toString(), filePathUrl, tempTableName, "true", registerTempTable);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param rsHolder
	 * @param defaultPath
	 * @param filePath
	 * @param tempTableName
	 * @param datapod
	 * @param latestDQList
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private List<DQIntelligence> nullCheck(ResultSetHolder rsHolder, String defaultPath,
			String filePath, String defaultTempTableName, Datapod datapod, List<DataQual> latestDQList, float minThreshold) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		
		List<String> tempTableList = new ArrayList<>();
		String appUuid = commonServiceImpl.getApp().getUuid();
		
//		rsHolder.getDataFrame().show(false);
		
		boolean anyNull = false;
		StringBuilder nullCheckBuilder = new StringBuilder("SELECT ");	
		
		for(Attribute attribute : datapod.getAttributes()) {
			if(attribute.getNullFlag() != null && attribute.getNullFlag().equalsIgnoreCase("Y")) {
				String check = attribute.getName().concat(" IS NOT NULL ");
				nullCheckBuilder.append(caseWrapper(check, attribute.getName()));
				
				nullCheckBuilder.append(", ");
				
				anyNull = true;
			} 
		}
		
		if(anyNull) {
			String tempSql = nullCheckBuilder.substring(0, nullCheckBuilder.lastIndexOf(","));
			nullCheckBuilder = new StringBuilder(tempSql);
			nullCheckBuilder.append(" FROM ").append(rsHolder.getTableName());
			
			String nullCheckTempTableName = defaultTempTableName.concat("_").concat("null_check");
			
			tempTableList.add(nullCheckTempTableName);
			
			ResultSetHolder nullCheckHolder = sparkExecutor.executeAndRegisterByTempTable(nullCheckBuilder.toString(), nullCheckTempTableName, true, appUuid);
//			nullCheckHolder.getDataFrame().show(false);

			long sampleTotalRows = rsHolder.getCountRows();
			if(nullCheckHolder.getCountRows() > 0) {
				StringBuilder outerSqlBuilder = new StringBuilder("SELECT * FROM (");
				StringBuilder scoreBuilder = new StringBuilder();
				String[] dfCols = nullCheckHolder.getDataFrame().columns();				
				
				int j = 0;
				for(String col : dfCols) {
					scoreBuilder.append("SELECT ");
					scoreBuilder.append("'").append(datapod.getUuid()).append("' AS ").append("datapod_uuid").append(", ");					
					scoreBuilder.append(" '").append(col).append("' AS ").append("null_col").append(", ");
					scoreBuilder.append("(COUNT(").append(col).append(") / "+sampleTotalRows+") * 100 AS ").append("score_count");
					scoreBuilder.append(" FROM ").append(nullCheckTempTableName);
					scoreBuilder.append(" WHERE ").append(col).append(" = 'N'").append(" GROUP BY datapod_uuid, null_col");
					
					if(j < dfCols.length - 1) {
						scoreBuilder.append(" UNION ALL ");
					}
					j++;
				}
				
				outerSqlBuilder.append(scoreBuilder.toString()).append(") WHERE score_count >= ").append(minThreshold);
				ResultSetHolder distinctValHolder = sparkExecutor.executeAndRegisterByTempTable(outerSqlBuilder.toString(), null, false, appUuid);
//				distinctValHolder.getDataFrame().show(false);
				
				return getCheckTypeListForNullCheck(distinctValHolder, datapod, latestDQList);
			}
			
			//***************** dropping temporary tables created *****************//
			sparkExecutor.dropTempTable(tempTableList);
		}
		
		return new ArrayList<>();
	}
	
	public List<DQIntelligence> getCheckTypeListForNullCheck(ResultSetHolder rsHolder, Datapod datapod, List<DataQual> latestDQList) {
		//********* 0: datapod_uuid, 1: null_col, 2: score_count *********//
		
		List<DQIntelligence> checkTypeList = new ArrayList<>();
		
		Dataset<Row> df = rsHolder.getDataFrame();
		Row[] rows = (Row[]) df.head(Integer.parseInt(""+df.count()));

		for (Row row : rows) {
			try {
				DQIntelligence dqIntelligence = new DQIntelligence();
				dqIntelligence.setCheckType(CheckType.NULL);
				dqIntelligence.setSampleScore(row.getDouble(2));

				//***************** setting attribute name *****************//
					dqIntelligence.setAttributeName(getAttributeName(datapod, row.getString(1)));
				
				//***************** setting check value  *****************//
				MetaIdentifierHolder valueCheckHolder = new MetaIdentifierHolder(new MetaIdentifier(MetaType.simple, null, null));
				valueCheckHolder.setValue(CheckType.NULL.toString());
				dqIntelligence.setCheckValue(valueCheckHolder);
						
				
				//***************** setting dq creation check  *****************//
				dqIntelligence.setCreated(getDQCreationCheck(latestDQList, row.getString(1)));
				
				checkTypeList.add(dqIntelligence);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		return checkTypeList;
	}
}

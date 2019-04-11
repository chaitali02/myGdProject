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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.expressions.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.AttributeDomain;
import com.inferyx.framework.domain.DataQualExec;
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
public class DQIntelligenceOperator {

	static final Logger logger = Logger.getLogger(DQIntelligenceOperator.class);
	
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private SparkExecutor<?> sparkExecutor;
	@Autowired
	private MetadataServiceImpl metadataServiceImpl;

	public List<Map<String, String>> genIntelligence(DataQualExec dqExec, ExecParams execParams, RunMode runMode) throws Exception {
		try {
			List<Map<String, String>> recommendationList = new ArrayList<>();
			try {
				synchronized (dqExec.getUuid()) {
					dqExec = (DataQualExec) commonServiceImpl.setMetaStatus(dqExec, MetaType.dqExec,
							Status.Stage.RUNNING);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			String appUuid = commonServiceImpl.getApp().getUuid();
			
			MetaIdentifier execDependsOnMI = dqExec.getDependsOn().getRef();
			Datapod sourceDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(execDependsOnMI.getUuid(), execDependsOnMI.getVersion(), execDependsOnMI.getType().toString(), "N");
			
			Datasource sourceDS = commonServiceImpl.getDatasourceByObject(sourceDp);
			
			ExecContext execContext = commonServiceImpl.getExecContext(runMode);
			IExecutor exec = execFactory.getExecutor(execContext.toString());
			ResultSetHolder rsHolder = exec.executeSqlByDatasource(dqExec.getExec(), sourceDS, appUuid);
			if(rsHolder.getType().equals(ResultType.resultset)) {
				rsHolder = sparkExecutor.convertResultsetToDataframe(rsHolder);
			}
			
			rsHolder.getDataFrame().printSchema();
			
			String sampleSize = commonServiceImpl.getConfigValue("framework.dataqual.sample.rows");
			
			Dataset<Row> sampleDf = null;
			if(sampleSize.contains("%")) {
				String samplePerc = sampleSize.substring(0, sampleSize.lastIndexOf("%"));
				double sample = Double.parseDouble(samplePerc);
				Dataset<Row>[] splits = rsHolder.getDataFrame().randomSplit(new double[] {sample/100, (100 - sample)/100}, 12345);
				sampleDf = splits[0];
			} else {
				sampleDf = rsHolder.getDataFrame().limit(Integer.parseInt(sampleSize));
			}

			String defaultPath = commonServiceImpl.getConfigValue("framework.schema.Path");
			defaultPath = defaultPath.endsWith("/") ? defaultPath : defaultPath.concat("/");
			String filePath = String.format("%s/%s/%s/", sourceDp.getUuid(), sourceDp.getVersion(), dqExec.getVersion());
			String tempTableName = String.format("%s_%s_%s", sourceDp.getUuid().replaceAll("-", "_"), sourceDp.getVersion(), dqExec.getVersion());
			
			String sampleFilePathUrl = defaultPath.concat(filePath).concat("sample");
			ResultSetHolder sampleHolder = new ResultSetHolder();
			sampleHolder.setDataFrame(sampleDf);
			sampleHolder.setType(ResultType.dataframe);
			
			String sampleTempTableName = String.format("%s_%s", tempTableName, "sample");
			sampleHolder = save(sampleHolder, sampleFilePathUrl, sampleTempTableName, true);
			sampleHolder.setCountRows(sampleHolder.getDataFrame().count());
			
			sampleHolder.setTableName(sampleTempTableName);
			//check domain for recommendation
			recommendationList.addAll(domainCheck(sampleHolder, defaultPath, filePath, tempTableName));
			
			synchronized (dqExec.getUuid()) {
				dqExec = (DataQualExec) commonServiceImpl.setMetaStatus(dqExec, MetaType.dqExec,
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
				synchronized (dqExec.getUuid()) {
					dqExec = (DataQualExec) commonServiceImpl.setMetaStatus(dqExec, MetaType.dqExec,
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
			dependsOn.setRef(new MetaIdentifier(MetaType.dqExec, dqExec.getUuid(), dqExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Data Qual execution failed.", dependsOn);
			throw new java.lang.Exception((message != null) ? message : "Data Qual execution failed.");
		}
	}

	public List<Map<String, String>> domainCheck(ResultSetHolder rsHolder, String defaultPath, String filePath, String defaultTempTableName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException{
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
			sparkExecutor.executeAndRegisterByTempTable(sql, domainTempTableName, true, appUuid);
			
			tempTableList.add(domainTempTableName);
			
			//***************** calculating score for each column formula: ((no. of 'Y' in a column)/sampleTotalRows) *****************//
			String colScoreTempTable = defaultTempTableName.concat("_").concat("score");		
			tempTableList.add(colScoreTempTable);
			
			StringBuilder scoreCountBuilder = new StringBuilder();
			long sampleTotalRows = rsHolder.getCountRows();
			for(AttributeDomain domain : attrDomainList) {
				if(sampleTotalRows > 0) {
					for(String colName : dfColumns) {					
						scoreCountBuilder.append("SELECT domain_name, "
								+ "COUNT(" + colName + ")/" + sampleTotalRows + " AS score_count, '"
								+ colName + "' AS score_column FROM " + domainTempTableName + " WHERE domain_name = '"
								+ domain.getName() + "' AND " + colName + " = 'Y' GROUP BY domain_name, score_column");
						
						scoreCountBuilder.append(" UNION ALL ");
					}
				}
			}
			
			String scoreSql = scoreCountBuilder.substring(0, scoreCountBuilder.lastIndexOf(" UNION ALL "));
			ResultSetHolder scoreCountHolder = sparkExecutor.executeAndRegisterByTempTable(scoreSql, colScoreTempTable, true, appUuid);
//			scoreCountHolder.getDataFrame().show(false);
			
			//***************** saving score *****************//
			String scoreFilePathUrl = defaultPath.concat(filePath).concat("score");
			save(scoreCountHolder, scoreFilePathUrl, null, false);
			
			List<Map<String, String>> domainRecommendation = getCheckTypeList(scoreCountHolder);
			
			//***************** finding max score of a columns *****************//
			String maxScoreCountSql = "SELECT domain_name, score_count, score_column FROM "
					+ colScoreTempTable+" WHERE score_count = (SELECT MAX(score_count) FROM "+colScoreTempTable+")";
			sparkExecutor.executeAndRegisterByTempTable(maxScoreCountSql, null, false, appUuid);
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
	
	public List<Map<String, String>> getCheckTypeList(ResultSetHolder rsHolder) {
		List<Map<String, String>> checkTypeList = new ArrayList<>();
		
		Dataset<Row> df = rsHolder.getDataFrame();
		Row[] rows = (Row[]) df.head(Integer.parseInt(""+df.count()));
		
		for(Row row : rows) {
			Map<String, String> rowMap = new LinkedHashMap<>();
			rowMap.put("attributeName", row.get(2).toString());
			rowMap.put("checkType", CheckType.DOMAIN.toString());
			rowMap.put("checkValue", row.get(0).toString());
			checkTypeList.add(rowMap);
		}
		
		return checkTypeList;
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
}

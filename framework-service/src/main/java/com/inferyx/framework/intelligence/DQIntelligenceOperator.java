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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.AttributeDomain;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataQual;
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

	public List<Object> execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {

		DataQualExec dqExec = (DataQualExec) baseExec;
		try {
			List<Object> recommendationList = new ArrayList<>();
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
			DataQual dataQual = (DataQual) commonServiceImpl.getOneByUuidAndVersion(execDependsOnMI.getUuid(), execDependsOnMI.getVersion(), execDependsOnMI.getType().toString(), "N");
			
			MetaIdentifier srcDependsOnMI = dataQual.getDependsOn().getRef();
			Datapod sourceDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(srcDependsOnMI.getUuid(), srcDependsOnMI.getVersion(), srcDependsOnMI.getType().toString(), "N");
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
			String filePath = String.format("%s/%s/%s/", dataQual.getUuid(), dataQual.getVersion(), dqExec.getVersion());
			String tempTableName = String.format("%s_%s_%s", dataQual.getUuid().replaceAll("-", "_"), dataQual.getVersion(), dqExec.getVersion());
			
			String sampleFilePathUrl = defaultPath.concat(filePath).concat("sample");
			ResultSetHolder sampleHolder = new ResultSetHolder();
			sampleHolder.setDataFrame(sampleDf);
			sampleHolder.setType(ResultType.dataframe);
			
			String sampleTempTableName = String.format("%s_%s", tempTableName, "sample");
			sampleHolder = save(sampleHolder, sampleFilePathUrl, sampleTempTableName, true);
			
			sampleHolder.setTableName(sampleTempTableName);
			//check domain for recommendation
			recommendationList.addAll(domainCheck(sampleHolder, defaultPath, filePath, tempTableName));
			
			synchronized (dqExec.getUuid()) {
				dqExec = (DataQualExec) commonServiceImpl.setMetaStatus(dqExec, MetaType.dqExec,
						Status.Stage.COMPLETED);
			}
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

	public List<Object> domainCheck(ResultSetHolder rsHolder, String defaultPath, String filePath, String defaultTempTableName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException{
		String appUuid = commonServiceImpl.getApp().getUuid();
		
		List<Object> domainRecommendation = new ArrayList<>();
		
		String[] dfColumns = rsHolder.getDataFrame().columns();
		List<AttributeDomain> attrDomainList = metadataServiceImpl.getDomainByApp(appUuid);
				
		StringBuilder regextQuery = new StringBuilder();
		for(AttributeDomain domain : attrDomainList) { 
			regextQuery.append("SELECT ");
//			regextQuery.append(i).append(" AS ").append("rowId").append(", ");
			regextQuery.append("'").append(domain.getName()).append("'").append(" AS ").append("domain_name").append(", ");
			
			int i = 0;
			for(String colName : dfColumns) {
				String colCheck = colName.concat(" RLIKE (").concat("'").concat(domain.getRegEx()).concat("') ");
				regextQuery.append(caseWrapper(colCheck, colName));
//				regextQuery.append("CASE WHEN ");
//				regextQuery.append(colName).append(" RLIKE (").append("'").append(domain.getRegEx()).append("'").append(")");
//				regextQuery.append(" ").append(" THEN 'Y' ").append(" ELSE 'N' ").append(" END ");
//				regextQuery.append(" AS ").append(colName);
				if(i < dfColumns.length - 1) {
					regextQuery.append(", ");
				}
				i++;
			}
			regextQuery.append(" FROM ").append(rsHolder.getTableName()).append(" ");
//			domainMap.put(domain.getName(), sparkExecutor.executeAndRegisterByTempTable(regextQuery.toString(), null, false, appUuid));
//			regextQuery.append(" GROUP BY domain_name ");
			regextQuery.append(" UNION ALL ");
		}
		
		String sql = regextQuery.substring(0, regextQuery.lastIndexOf(" UNION ALL "));
		String domainTempTableName = defaultTempTableName.concat("_").concat("domain_union");
		ResultSetHolder domainUnion = sparkExecutor.executeAndRegisterByTempTable(sql, domainTempTableName, true, appUuid);
		
		Map<String, ResultSetHolder> scodeCountMap = new LinkedHashMap<>();
		for(AttributeDomain domain : attrDomainList) {
			String totalRowsSql = "SELECT * FROM "+domainTempTableName+" WHERE domain_name = '"+domain.getName()+"'";
			ResultSetHolder totalRowsCountHolder = sparkExecutor.executeAndRegisterByTempTable(totalRowsSql, domainTempTableName, false, appUuid);
			long totalRows = totalRowsCountHolder.getCountRows();

			if(totalRows > 0) {
				String colScoreTempTable = defaultTempTableName.concat("_").concat(domain.getName()).concat("_score");
				StringBuilder scoreCountBuilder = new StringBuilder();
				for(String colName : dfColumns) {
					scoreCountBuilder.append("SELECT domain_name, (COUNT(" + colName + ")/" + totalRows + ") AS score_count, '"
									+ colName + "' AS score_column FROM " + domainTempTableName + " WHERE domain_name = '"
									+ domain.getName() + "' AND " + colName + " = 'Y' GROUP BY domain_name, score_column");
					scoreCountBuilder.append(" UNION ALL ");
				}
				
				String scoreSql = scoreCountBuilder.substring(0, scoreCountBuilder.lastIndexOf(" UNION ALL "));
				ResultSetHolder scoreCountHolder = sparkExecutor.executeAndRegisterByTempTable(scoreSql, colScoreTempTable, true, appUuid);
				scodeCountMap.put(domain.getName(), scoreCountHolder);
//				scoreCountHolder.getDataFrame().show(false);
			}
		}
		
		Map<String, ResultSetHolder> maxScoreHolderMap = new LinkedHashMap<>();
		for(String key : scodeCountMap.keySet()) {
			ResultSetHolder scoreCountHolder = scodeCountMap.get(key);
			String maxScoreCount = "SELECT domain_name, MAX(score_count) AS max_score, score_column FROM "
					+ scoreCountHolder.getTableName()+" GROUP BY domain_name, score_column";
			scoreCountHolder.getDataFrame().sqlContext()
					.sql("SELECT domain_name, MAX(score_count) AS max_score, score_column FROM "
							+ scoreCountHolder.getTableName()+" GROUP BY domain_name, score_column")
					.show(false);
			ResultSetHolder maxScoreHolder = sparkExecutor.executeAndRegisterByTempTable(maxScoreCount, null, false, appUuid);
			maxScoreHolder.getDataFrame().show(false);
			maxScoreHolderMap.put(key, maxScoreHolder);
		}
		
		domainUnion.getDataFrame().show(Integer.parseInt(""+domainUnion.getCountRows()), false);
		return domainRecommendation;
	}
	
	private String caseWrapper(String check, String colName) {
		StringBuilder caseBuilder = new StringBuilder(" CASE WHEN ").append(check).append(" THEN 'Y' ELSE 'N' END AS ").append(colName)
				.append(" ");
		return caseBuilder.toString();
	}
	
//	public double getScoreCount(ResultSetHolder scoreCountHolder) {
//		
//	}
	
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

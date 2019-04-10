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
			
			String sampleSize = commonServiceImpl.getConfigValue("framework.dataqual.sample.rows");
			
			Dataset<Row> sampleDf = null;
			if(sampleSize.contains("%")) {
				String samplePerc = sampleSize.substring(0, sampleSize.lastIndexOf("%"));
				double sample = Double.parseDouble(samplePerc);
				Dataset<Row>[] splits = rsHolder.getDataFrame().randomSplit(new double[] {sample, (100 - sample)}, 12345);
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
			int i = 0;
			for(String colName : dfColumns) {
				regextQuery.append(i).append(" AS ").append("rowId").append(", ");
				regextQuery.append(domain.getName()).append(" AS ").append("domain_name").append(", ");
				regextQuery.append("CASE WHEN (");
				regextQuery.append("REGEXP_EXTRACT(").append(colName).append(", ").append(domain.getRegEx()).append(")");
				regextQuery.append(" NOT LIKE ''").append(") ").append(" THEN 'Y' ").append(" ELSE 'N' ").append(" END ");
				regextQuery.append(" AS ").append(colName);
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
		ResultSetHolder domainUnion = sparkExecutor.executeAndRegisterByTempTable(sql, domainTempTableName, true, appUuid);
		
		return domainRecommendation;
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

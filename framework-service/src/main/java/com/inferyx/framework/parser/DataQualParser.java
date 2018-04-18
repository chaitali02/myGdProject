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
package com.inferyx.framework.parser;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.DQOperator;
import com.inferyx.framework.service.CommonServiceImpl;

@Component
public class DataQualParser {
	
	@Autowired
	protected MetadataUtil daoRegister;
	@Autowired
	protected DQOperator dqOperator;
	/*@Autowired
	private HiveContext hiveContext;*/
	@Autowired
	private ExecutorFactory execFactory; 
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(DataQualParser.class);
	
	/*public void parseDQ(String dqUUID, String dqVersion, Set<MetaIdentifier> usedRefKeySet) throws Exception {
		// Fetch DQ
		DataQual dq = new DataQual();
		// Get tablename
		String tableName = null;
		// Get attributeName
		String attributeName = null;
		// Send to DQOperator to generate case statements and tableName
		String sql = dqOperator.generateSql(dq, tableName, attributeName, null, null, null, usedRefKeySet);
		logger.info(" Inside DataQual Parser  for SQL : " + sql);
		if (StringUtils.isBlank(sql)) {
			return;
		}
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		//DataFrame df = sqlContext.sql(sql);
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		ResultSetHolder rsHolder = exec.executeSql(sql);
		DataFrame df = rsHolder.getDataFrame();
		Row[] rows = df.head(30);
		String[] columns = df.columns();
		*//**** Get sql and update in ruleexec - START ****//*
		hiveContext.registerDataFrameAsTable(df, tableName);
		//persistDataStore(df, tableName, null,filePath, datapodKey, ruleExec, rule);
	}*/
}

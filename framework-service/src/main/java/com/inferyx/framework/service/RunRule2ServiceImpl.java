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
package com.inferyx.framework.service;

import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseRule;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;

public class RunRule2ServiceImpl extends RunBaseRuleService {

	static final Logger logger = Logger.getLogger(RunRule2ServiceImpl.class);	

	/**
	 * Persist Datastore for rule
	 * @param df
	 * @param tableName
	 * @param filePath
	 * @param resultRef
	 * @throws Exception
	 */
	@Override
	protected void persistDatastore(String tableName, String filePath, MetaIdentifierHolder resultRef,MetaIdentifier datapodKey, long countRows, RunMode runMode) throws Exception {
		/*DataStore ds = new DataStore();
		ds.setCreatedBy(baseRuleExec.getCreatedBy());*/
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodKey.getUuid(), datapodKey.getVersion(), datapodKey.getType().toString());		
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, tableName, datapodKey, baseRuleExec.getRef(ruleExecType), baseRuleExec.getAppInfo(), 
				baseRuleExec.getCreatedBy(), SaveMode.Append.toString(), resultRef, countRows, 
				commonServiceImpl.getPersistMode(datapod), null);
		/*dataStoreServiceImpl.persistDataStore(df, tableName, null, filePath, baseRuleExec.getDependsOn().getRef(), baseRuleExec.getRef(ruleExecType),
				ConstantsUtil.PERSIST_MODE_MEMORY_ONLY,baseRuleExec.getAppInfo(),SaveMode.Append.toString(), resultRef,ds);*/
	}
	
	@Override
	protected String getFileName(BaseRule baseRule, BaseRuleExec baseRuleExec, MetaIdentifier datapodKey) {
		return String.format("/%s/%s/%s", datapodKey.getUuid(), datapodKey.getVersion(),
				baseRuleExec.getVersion());
	}

}

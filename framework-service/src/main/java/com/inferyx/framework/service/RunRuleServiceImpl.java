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

import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.enums.RunMode;

public class RunRuleServiceImpl extends RunBaseRuleService {

	static final Logger logger = Logger.getLogger(RunRuleServiceImpl.class);	

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
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, tableName, datapodKey, baseRuleExec.getRef(ruleExecType), baseRuleExec.getAppInfo(), baseRuleExec.getCreatedBy(), SaveMode.Append.toString(), resultRef, countRows, ConstantsUtil.PERSIST_MODE_MEMORY_ONLY);
		/*dataStoreServiceImpl.persistDataStore(df, tableName, null, filePath, baseRuleExec.getDependsOn().getRef(), baseRuleExec.getRef(ruleExecType),
				ConstantsUtil.PERSIST_MODE_MEMORY_ONLY,baseRuleExec.getAppInfo(),SaveMode.Append.toString(), resultRef,ds);*/
	}
}

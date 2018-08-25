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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.BaseRule;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.executor.ExecContext;

public class RunProfileServiceImpl extends RunBaseRuleService {

	static final Logger logger = Logger.getLogger(RunProfileServiceImpl.class);

	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
		public void uncaughtException(Thread th, Throwable ex) {
			logger.info("Uncaught exception: " + ex);
		}
	};
	
	/**
	 * Create file name
	 * @param baseGroupExec
	 * @param baseRule
	 * @param baseRuleExec
	 * @param datapodKey
	 * @return
	 */
	@Override
	protected String getFileName(BaseRule baseRule, BaseRuleExec baseRuleExec, MetaIdentifier datapodKey) {
		return String.format("/%s/%s/%s", datapodKey.getUuid(), datapodKey.getVersion(),
				baseRuleExec.getVersion());
	}
	
	/**
	 * Create table name
	 * @param baseGroupExec
	 * @param baseRule
	 * @param baseRuleExec
	 * @param datapodKey
	 * @return
	 * @throws JsonProcessingException 
	 */
	@Override
	protected String getTableName(BaseRule baseRule, BaseRuleExec baseRuleExec, MetaIdentifier datapodKey, ExecContext execContext) throws JsonProcessingException {
		Datapod dp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodKey.getUuid(), datapodKey.getVersion(), MetaType.datapod.toString());
		Datasource datasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(dp.getDatasource().getRef().getUuid(), dp.getDatasource().getRef().getVersion(), MetaType.datasource.toString());
		if (/*execContext == null || execContext.equals(ExecContext.spark) ||*/ datasource.getType().equals(ExecContext.FILE.toString()) /*|| execContext.equals(ExecContext.livy_spark)*/) {
			return String.format("%s_%s_%s", datapodKey.getUuid().replace("-", "_"),
					datapodKey.getVersion(), baseRuleExec.getVersion());
		}
		if (dp != null) {
			return datasource.getDbname() + "." + dp.getName();
		}
		return null;
	}
	
}

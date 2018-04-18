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
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.executor.ExecContext;

public class RunDataQualServiceImpl extends RunBaseRuleService {
	
	
	static final Logger logger = Logger.getLogger(RunDataQualServiceImpl.class);
	
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	/**
	 * Create file name
	 * @param baseGroupExec
	 * @param baseRule
	 * @param baseRuleExec
	 * @param datapodKey
	 * @return
	 */
	@Override
	protected String getFileName(BaseRuleGroupExec baseGroupExec, BaseRule baseRule, BaseRuleExec baseRuleExec, MetaIdentifier datapodKey) {
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
	 */
	@Override
	protected String getTableName(BaseRuleGroupExec baseGroupExec, BaseRule baseRule, BaseRuleExec baseRuleExec, MetaIdentifier datapodKey, ExecContext execContext) {
		if (execContext == null || execContext.equals(ExecContext.spark) || execContext.equals(ExecContext.FILE) || execContext.equals(ExecContext.livy_spark)) {
			return String.format("%s_%s_%s", datapodKey.getUuid().replace("-", "_"),
					datapodKey.getVersion(), baseRuleExec.getVersion());
		}
		Datapod dp = null;
		try {
			dp = (Datapod) commonServiceImpl.getLatestByUuid(datapodKey.getUuid(), MetaType.datapod.toString());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if (dp != null) {
			return datasource.getDbname() + "." + dp.getName();
		}
		return null;
	}
	
}

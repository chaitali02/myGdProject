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

import com.inferyx.framework.domain.BaseRule;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.MetaIdentifier;

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
	protected String getFileName(BaseRule baseRule, BaseRuleExec baseRuleExec, MetaIdentifier datapodKey) {
		return String.format("/%s/%s/%s", datapodKey.getUuid(), datapodKey.getVersion(),
				baseRuleExec.getVersion());
	}

	/********************** UNUSED **********************/
//	/**
//	 * Create table name
//	 * @param baseGroupExec
//	 * @param baseRule
//	 * @param baseRuleExec
//	 * @param datapodKey
//	 * @return
//	 * @throws JsonProcessingException 
//	 */
//	@Override
//	protected String getTableName(BaseRule baseRule, BaseRuleExec baseRuleExec, MetaIdentifier datapodKey, ExecContext execContext, RunMode runMode) throws JsonProcessingException {
//		if(datapodKey.getType().equals(MetaType.rule)) {
//			return String.format("%s_%s_%s", baseRule.getUuid().replace("-", "_"), baseRule.getVersion(), baseRuleExec.getVersion());
//
//		}
//	    else if (execContext == null /*|| execContext.equals(ExecContext.spark)*/ || runMode.equals(RunMode.ONLINE) && execContext.equals(ExecContext.FILE) 
//				/*|| execContext.equals(ExecContext.livy_spark)*/) {
//			return String.format("%s_%s_%s", baseRule.getUuid().replace("-", "_"), baseRule.getVersion(), baseRuleExec.getVersion());
//		}
//		Datapod dp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodKey.getUuid(), datapodKey.getVersion(), MetaType.datapod.toString());
//		Datasource datasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(dp.getDatasource().getRef().getUuid(), dp.getDatasource().getRef().getVersion(), MetaType.datasource.toString());
//		if (/*execContext == null || execContext.equals(ExecContext.spark) ||*/ datasource.getType().equals(ExecContext.FILE.toString()) || runMode.equals(RunMode.ONLINE) /*|| execContext.equals(ExecContext.livy_spark)*/) {
//			return String.format("%s_%s_%s", datapodKey.getUuid().replace("-", "_"),
//					datapodKey.getVersion(), baseRuleExec.getVersion());
//		}
//		logger.info("datasource.getType() : " + datasource.getType());
//		if (dp != null) {
//			return datasource.getDbname() + "." + dp.getName();
//		} 
//		return null;
//	}
	
}

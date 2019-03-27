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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.enums.RunMode;

/**
 * Defines how rule should be created, parsed and executed
 * @author joy
 *
 */
public class BaseRuleBridgeService {
	
	@Autowired
	RuleTemplate baseRuleServiceImpl;
	
	static final Logger logger = Logger.getLogger(BaseRuleBridgeService.class);

	/**
	 * 
	 */
	public BaseRuleBridgeService() {
	}
	
	public BaseRuleExec create(String uuid, String version, MetaType type, MetaType execType, BaseRuleExec inputBaseRuleExec, 
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {
		return baseRuleServiceImpl.create(uuid, version, type, execType, inputBaseRuleExec, refKeyMap, datapodList, dagExec, runMode);
	}
	
	public BaseRuleExec parse(String uuid, String version, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams) throws Exception {
		return baseRuleServiceImpl.parse(uuid, version, refKeyMap, otherParams, null, null, null);
	}
	
	public BaseRuleExec execute(String uuid, String version, MetaType type, MetaType execType, 
			ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec, BaseRuleGroupExec baseGroupExec, List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
		if (baseRuleExec == null) {
			return null;
		}
		return baseRuleServiceImpl.execute(type, execType, metaExecutor, baseRuleExec, null, taskList, execParams, runMode);
	}
	
	public BaseRuleExec createAndParse (String uuid, String version, MetaType type, MetaType execType, BaseRuleExec inputBaseRuleExec, 
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {
		BaseRuleExec baseRuleExec = baseRuleServiceImpl.create(uuid, version, type, execType, inputBaseRuleExec, refKeyMap, datapodList, dagExec, runMode);
		if (baseRuleExec == null) {
			logger.info(" no Exec. So cannot proceed to parse ");
			return null;
		}
		HashMap<String, String> otherParams = dagExec.getExecParams().getOtherParams();
		return baseRuleServiceImpl.parse(uuid, version, refKeyMap, otherParams, null, null, null);
	}
	
	public BaseRuleExec parseAndExecute(String uuid, String version, MetaType type, MetaType execType, 
			ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec, BaseRuleGroupExec baseGroupExec, List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
		HashMap<String, String> otherParams = execParams.getOtherParams();
		baseRuleExec = baseRuleServiceImpl.parse(uuid, version, null, otherParams, null, null, null);
		if (baseRuleExec == null) {
			logger.info(" no Exec. So cannot proceed to execute ");
			return null;
		}
		return baseRuleServiceImpl.execute(type, execType, metaExecutor, baseRuleExec, null, taskList, execParams, runMode);
	}

}

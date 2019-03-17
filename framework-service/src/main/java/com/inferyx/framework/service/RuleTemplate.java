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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DQInfo;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.Rule2Info;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRule;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.Config;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.factory.RuleExecFactory;
import com.inferyx.framework.factory.RunRuleFactory;
import com.inferyx.framework.operator.IExecutable;
import com.inferyx.framework.operator.IParsable;
import com.inferyx.framework.register.DatapodRegister;

/**
 * @author joy
 *
 */
public abstract class RuleTemplate implements IExecutable, IParsable {
	
	@Autowired
	protected CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	protected DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	protected ExecutorFactory execFactory;
	@Autowired
	protected HDFSInfo hdfsInfo;
	@Resource(name="taskThreadMap")
	protected ConcurrentHashMap taskThreadMap;
	@Autowired
	RuleExecFactory ruleExecFactory;
	@Autowired
	protected RunRuleFactory runBaseFactory;
	@Autowired
	private SessionHelper sessionHelper;
	@Autowired
	private DatapodRegister datapodRegister;
	@Autowired
	ConnectionFactory connFactory;
	@Autowired
	Engine engine;
	@Autowired
	Helper helper;
	@Autowired
	ExecutorServiceImpl executorServiceImpl;
	@Autowired
	DQInfo dqInfo;
	@Autowired
	MetadataServiceImpl metadataServiceImpl;
	@Autowired
	Rule2Info rule2Info;
	

	static final Logger logger = Logger.getLogger(RuleTemplate.class);

	/**
	 * 
	 */
	public RuleTemplate() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Creates exec if it is not alReady present. 
	 * 
	 * 1. Checks whether uuid, type, execType, and corresponding baseRule is present
	 * 2. If any of the above is not present then returns null, aborts
	 * 3. If exec is not alReady present then created and saves exec
	 * 4. Checks whether its status is RUNNING, COMPLETED, TERMINATING or PAUSE. 
	 * 5. If status is one of #1 then control returns without doing anything
	 * 6. Else status is set to PENDING
	 * Note - Setting of status happens in a synchronized block with the exec uuid as key 
	 * and data is fetched from mongo and then set after adding status based on some prerequisites 
	 * checked in commonServiceImpl.setMetaStatus   
	 * @param uuid
	 * @param version
	 * @param type
	 * @param execType
	 * @param inputBaseRuleExec
	 * @param refKeyMap
	 * @param datapodList
	 * @param dagExec
	 * @return inputBaseRuleExec
	 * @throws Exception
	 */
	public BaseRuleExec create(String uuid, String version, MetaType type, MetaType execType, BaseRuleExec inputBaseRuleExec, 
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception {
		logger.info("Inside BaseRuleExec.create ");
		List<Status> statusList = null;
		BaseRule baseRule = null;
		if (StringUtils.isBlank(uuid)) {
			logger.info(" Nothing to create exec upon. Aborting ... ");
			return null;
		}
		baseRule = (BaseRule) commonServiceImpl.getOneByUuidAndVersion(uuid, version, type.toString(), "N");
		if (baseRule == null || type == null || execType == null) {
			logger.info(" Nothing to create exec upon. Aborting ... ");
			return null;
		}
		MetaIdentifierHolder baseRuleMeta = new MetaIdentifierHolder(new MetaIdentifier(type, baseRule.getUuid(), baseRule.getVersion()));
		if (inputBaseRuleExec == null) {
			inputBaseRuleExec = ruleExecFactory.getRuleExec(execType);
			inputBaseRuleExec.setDependsOn(baseRuleMeta);
			inputBaseRuleExec.setBaseEntity();
			inputBaseRuleExec.setName(baseRule.getName());
			inputBaseRuleExec.setAppInfo(baseRule.getAppInfo());
			synchronized (inputBaseRuleExec.getUuid()) {
				commonServiceImpl.save(execType.toString(), inputBaseRuleExec);
			}
		}
		statusList = inputBaseRuleExec.getStatusList();
		if (Helper.getLatestStatus(statusList) != null 
				&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.RUNNING, new Date())) 
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.COMPLETED, new Date())) 
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.TERMINATING, new Date()))
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.PAUSE, new Date())))) {
			logger.info(" This process is RUNNING or has been COMPLETED previously or is TERMINATING or is On Hold. Hence it cannot be rerun. ");
			return inputBaseRuleExec;
		}
		
		synchronized (inputBaseRuleExec.getUuid()) {
			inputBaseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(inputBaseRuleExec, execType, Status.Stage.PENDING);
		}
		return inputBaseRuleExec;
	}
	
	public abstract BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception;
	
	/**
	 * Defines sample execute. Should be overridden if required.
	 * @param uuid
	 * @param version
	 * @param type
	 * @param execType
	 * @param metaExecutor
	 * @param baseRuleExec
	 * @param baseGroupExec
	 * @param taskList
	 * @return BaseRuleExec
	 * @throws Exception
	 */
	public abstract BaseRuleExec execute(ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec, MetaIdentifier datapodKey, List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception;
	
	/**
	 * Defines sample execute. Should be overridden if required.
	 * @param uuid
	 * @param version
	 * @param type
	 * @param execType
	 * @param metaExecutor
	 * @param baseRuleExec
	 * @param baseGroupExec
	 * @param taskList
	 * @return BaseRuleExec
	 * @throws Exception
	 */

//	public BaseRuleExec execute(MetaType type, MetaType execType, 
//			ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec, MetaIdentifier datapodKey, List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
//			return execute(type,execType,metaExecutor,baseRuleExec,datapodKey,taskList,execParams,runMode,"N");
//		}
	
	/**
	 * 
	 * @param configVal
	 * @param uuid
	 * @return
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws JsonProcessingException 
	 */
	public MetaIdentifier getSummaryOrDetail(String configName, String uuid) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		List<Config> appConfigList = metadataServiceImpl.getAppConfigByCurrentApp();
		Config resultConfig = null;
		Datapod targetDp = null;
		Datapod infoDatapod = null;
		for (Config config : appConfigList) {
			if (config.getConfigName().equals(configName)) {
				resultConfig = config;
				break;
			}
		}
		if (StringUtils.isNotBlank(uuid)) {
			infoDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(uuid, null, MetaType.datapod.toString(), "N");
		}
		
		if (resultConfig == null) {
			targetDp = infoDatapod; 
		} else {
			targetDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(resultConfig.getConfigVal(), null, MetaType.datapod.toString(), "N");
			if (targetDp == null) {
				targetDp = infoDatapod;
			}
		}
		
		if (targetDp == null) {
			return null;
		}
		
		return new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion());
	}

	public BaseRuleExec execute(MetaType type, MetaType execType,  
			ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec, 
			MetaIdentifier targetDatapodKey, List<FutureTask<TaskHolder>> taskList, 
			ExecParams execParams, RunMode runMode) throws Exception {
		logger.info("Inside BaseRuleExec.execute ");
		BaseRule baseRule = null;
		
		if (baseRuleExec == null) {
			logger.info(" Nothing to create exec upon. Aborting ... ");
			return null;
		}
		baseRule = (BaseRule) commonServiceImpl.getOneByUuidAndVersion(baseRuleExec.getDependsOn().getRef().getUuid(), baseRuleExec.getDependsOn().getRef().getVersion(), type.toString(), "N");
		if (baseRule == null || type == null || execType == null) {
			logger.info(" Nothing to create exec upon. Aborting ... ");
			return null;
		}
		/*synchronized (baseRuleExec.getUuid()) {
			baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, execType, Status.Stage.RUNNING);
		}*/
		logger.info(" After status set to RUNNING for baseRuleExec : " + baseRuleExec.getUuid());
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();	// Check and remove

		/*// get datapod from baseRuleExec
		if(datapodKey == null && (baseRuleExec.getRefKeyList() != null)) {
			for(MetaIdentifier mi : baseRuleExec.getRefKeyList()) {
				if(mi.getType().equals(MetaType.datapod)) {
					datapodKey = mi;
					break;
				}
			}
		}*/
		
		RunBaseRuleService runBaseRuleService = runBaseFactory.getRuleService(type);	// Get this from a factory
		runBaseRuleService.setDataStoreServiceImpl(dataStoreServiceImpl);
		runBaseRuleService.setHdfsInfo(hdfsInfo);
		runBaseRuleService.setBaseRule(baseRule);
		runBaseRuleService.setExecFactory(execFactory);
		runBaseRuleService.setAuthentication(authentication);
		runBaseRuleService.setBaseRuleExec(baseRuleExec);
		runBaseRuleService.setCommonServiceImpl(commonServiceImpl);
		runBaseRuleService.setName(execType+"_"+baseRuleExec.getUuid()+"_"+baseRuleExec.getVersion());
		runBaseRuleService.setDatapodKey(targetDatapodKey);
		runBaseRuleService.setRuleExecType(execType);
		runBaseRuleService.setSessionContext(sessionHelper.getSessionContext());
		runBaseRuleService.setRunMode(runMode);
		runBaseRuleService.setDatapodRegister(datapodRegister);
		runBaseRuleService.setConnFactory(connFactory);
		runBaseRuleService.setEngine(engine);
		runBaseRuleService.setHelper(helper);
		runBaseRuleService.setExecutorServiceImpl(executorServiceImpl);
		runBaseRuleService.setExecParams(execParams);
		runBaseRuleService.setDatasource(getDatasource(baseRule));
		runBaseRuleService.setSummaryDatapodKey(getTargetSummaryDp());

		if (metaExecutor == null) {
			runBaseRuleService.execute();
		} else {
			FutureTask<TaskHolder> futureTask = new FutureTask<TaskHolder>(runBaseRuleService);
			metaExecutor.execute(futureTask);
			taskList.add(futureTask);
			taskThreadMap.put(execType+"_"+baseRuleExec.getUuid()+"_"+baseRuleExec.getVersion(), futureTask);
			logger.info(" taskThreadMap while creating baseRule : " + taskThreadMap);
		}
		return baseRuleExec;
	}

	public Datasource getDatasource(BaseRule baseRule)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return commonServiceImpl.getDatasourceByApp();
	}
	
	protected MetaIdentifier getTargetSummaryDp() throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return null;
	}
	
	protected MetaIdentifier getTargetResultDp() throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return null;
	}
	
	/**
	 * 
	 * @param baseRuleExecUUID
	 * @param baseRuleExecVersion
	 * @param ruleExecType
	 * @return
	 * @throws Exception
	 */
	public Status restart (String baseRuleExecUUID, String baseRuleExecVersion, MetaType ruleExecType) throws Exception {
		Status operatorLeastSigStatus = null;
		BaseRuleExec baseRuleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(baseRuleExecUUID, baseRuleExecVersion, ruleExecType.toString(), "N");
		operatorLeastSigStatus = helper.getLatestStatus(baseRuleExec.getStatusList());
		if(Helper.getLatestStatus(baseRuleExec.getStatusList()).equals(new Status(Status.Stage.FAILED, new Date()))
				||Helper.getLatestStatus(baseRuleExec.getStatusList()).equals(new Status(Status.Stage.KILLED, new Date()))){
			operatorLeastSigStatus = new Status(Status.Stage.READY, new Date());
			logger.info("RuleExec " + baseRuleExecUUID + " FAILED/KILLED. So proceeding ... ");
			if (Helper.isStatusPresent(new Status(Status.Stage.READY, new Date()), baseRuleExec.getStatusList())) {
				commonServiceImpl.setMetaStatus(baseRuleExec, ruleExecType, Status.Stage.READY);
			} else {
				commonServiceImpl.setMetaStatus(baseRuleExec, ruleExecType, Status.Stage.STARTING);
				operatorLeastSigStatus = new Status(Status.Stage.STARTING, new Date());
			}
		}
		logger.info("Status of baseruleexec " + baseRuleExecUUID + " = " + operatorLeastSigStatus.getStage().toString());
		return operatorLeastSigStatus;
	}

}

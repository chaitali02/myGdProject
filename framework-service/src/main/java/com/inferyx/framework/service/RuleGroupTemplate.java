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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.BaseRuleGroup;
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.factory.RuleExecFactory;
import com.inferyx.framework.factory.RuleServiceFactory;
import com.inferyx.framework.factory.RunRuleFactory;
import com.inferyx.framework.operator.IExecutable;
import com.inferyx.framework.operator.IParsable;

/**
 * @author joy
 *
 */
public abstract class RuleGroupTemplate implements IExecutable, IParsable {
	
	private static final String GET = "get";
	private static final String SET = "set";
	
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
	protected RunRuleFactory runBaseFactory;
	@Autowired
	protected ThreadPoolTaskExecutor metaExecutor;
	@Autowired
	protected ThreadPoolTaskExecutor metaGroupExecutor;
	@Autowired
	private RuleServiceFactory serviceFactory;
	@Autowired
	private RuleExecFactory metaExecFactory;
	@Autowired
	private SessionHelper sessionHelper;
	@Autowired
	RunBaseRuleService runBaseRuleService;
	
	/**
	 * @return the commonServiceImpl
	 */
	public CommonServiceImpl<?> getCommonServiceImpl() {
		return commonServiceImpl;
	}

	/**
	 * @param commonServiceImpl the commonServiceImpl to set
	 */
	public void setCommonServiceImpl(CommonServiceImpl<?> commonServiceImpl) {
		this.commonServiceImpl = commonServiceImpl;
	}

	/**
	 * @return the dataStoreServiceImpl
	 */
	public DataStoreServiceImpl getDataStoreServiceImpl() {
		return dataStoreServiceImpl;
	}

	/**
	 * @param dataStoreServiceImpl the dataStoreServiceImpl to set
	 */
	public void setDataStoreServiceImpl(DataStoreServiceImpl dataStoreServiceImpl) {
		this.dataStoreServiceImpl = dataStoreServiceImpl;
	}

	/**
	 * @return the execFactory
	 */
	public ExecutorFactory getExecFactory() {
		return execFactory;
	}

	/**
	 * @param execFactory the execFactory to set
	 */
	public void setExecFactory(ExecutorFactory execFactory) {
		this.execFactory = execFactory;
	}

	/**
	 * @return the hdfsInfo
	 */
	public HDFSInfo getHdfsInfo() {
		return hdfsInfo;
	}

	/**
	 * @param hdfsInfo the hdfsInfo to set
	 */
	public void setHdfsInfo(HDFSInfo hdfsInfo) {
		this.hdfsInfo = hdfsInfo;
	}

	/**
	 * @return the taskThreadMap
	 */
	public ConcurrentHashMap getTaskThreadMap() {
		return taskThreadMap;
	}

	/**
	 * @param taskThreadMap the taskThreadMap to set
	 */
	public void setTaskThreadMap(ConcurrentHashMap taskThreadMap) {
		this.taskThreadMap = taskThreadMap;
	}

	/**
	 * @return the runBaseFactory
	 */
	public RunRuleFactory getRunBaseFactory() {
		return runBaseFactory;
	}

	/**
	 * @param runBaseFactory the runBaseFactory to set
	 */
	public void setRunBaseFactory(RunRuleFactory runBaseFactory) {
		this.runBaseFactory = runBaseFactory;
	}

	/**
	 * @return the metaExecutor
	 */
	public ThreadPoolTaskExecutor getMetaExecutor() {
		return metaExecutor;
	}

	/**
	 * @param metaExecutor the metaExecutor to set
	 */
	public void setMetaExecutor(ThreadPoolTaskExecutor metaExecutor) {
		this.metaExecutor = metaExecutor;
	}

	/**
	 * @return the metaGroupExecutor
	 */
	public ThreadPoolTaskExecutor getMetaGroupExecutor() {
		return metaGroupExecutor;
	}

	/**
	 * @param metaGroupExecutor the metaGroupExecutor to set
	 */
	public void setMetaGroupExecutor(ThreadPoolTaskExecutor metaGroupExecutor) {
		this.metaGroupExecutor = metaGroupExecutor;
	}

	/**
	 * @return the serviceFactory
	 */
	public RuleServiceFactory getServiceFactory() {
		return serviceFactory;
	}

	/**
	 * @param serviceFactory the serviceFactory to set
	 */
	public void setServiceFactory(RuleServiceFactory serviceFactory) {
		this.serviceFactory = serviceFactory;
	}

	/**
	 * @return the metaExecFactory
	 */
	public RuleExecFactory getMetaExecFactory() {
		return metaExecFactory;
	}

	/**
	 * @param metaExecFactory the metaExecFactory to set
	 */
	public void setMetaExecFactory(RuleExecFactory metaExecFactory) {
		this.metaExecFactory = metaExecFactory;
	}

	/**
	 * @return the sessionHelper
	 */
	public SessionHelper getSessionHelper() {
		return sessionHelper;
	}

	/**
	 * @param sessionHelper the sessionHelper to set
	 */
	public void setSessionHelper(SessionHelper sessionHelper) {
		this.sessionHelper = sessionHelper;
	}

	static final Logger logger = Logger.getLogger(RuleGroupTemplate.class);

	/**
	 * 
	 */
	public RuleGroupTemplate() {
		// TODO Auto-generated constructor stub
	}
	
	public BaseRuleGroupExec create(String baseGroupUUID, 
			String baseGroupVersion, 
			MetaType groupType, 
			MetaType groupExecType, 
			MetaType ruleType, 
			MetaType ruleExecType,
			ExecParams execParams,
			List<String> datapodList, 
			BaseRuleGroupExec baseGroupExec, 
			DagExec dagExec) throws Exception {
		BaseRuleGroup baseGroup = null;
		MetaIdentifier ruleGroupExecMeta = null;
		List<MetaIdentifierHolder> appInfo = null;
		MetaIdentifierHolder ruleGroupRef = null;
		baseGroup = (BaseRuleGroup) commonServiceImpl.getOneByUuidAndVersion(baseGroupUUID,baseGroupVersion, groupType.toString()); 
		RuleTemplate baseRuleService = serviceFactory.getRuleService(ruleType);
			
		Map<String, MetaIdentifier> refKeyMap = new HashMap<>();
		if (baseGroupExec == null) {
			baseGroupExec = metaExecFactory.getGroupExec(groupExecType);
			baseGroupExec.setBaseEntity();
			appInfo = baseGroup.getAppInfo();
			baseGroupExec.setAppInfo(appInfo);
			baseGroupExec.setName(baseGroup.getName());
			ruleGroupRef = new MetaIdentifierHolder(new MetaIdentifier(groupType, baseGroup.getUuid(), baseGroup.getVersion()));
			baseGroupExec.setDependsOn(ruleGroupRef);
			if (baseGroupExec.getExecList() == null || baseGroupExec.getExecList().isEmpty()) {
				baseGroupExec.setExecList(new ArrayList<>());
			}
		}
		ruleGroupExecMeta = new MetaIdentifier(groupExecType, baseGroupExec.getUuid(), baseGroupExec.getVersion());
		// Get refKeyList and form refKeyMap
		if (execParams != null && execParams.getRefKeyList() != null && !execParams.getRefKeyList().isEmpty()) {
			for (MetaIdentifier refKey : execParams.getRefKeyList()) {
				refKeyMap.put(refKey.getType().toString().concat("_").concat(refKey.getUuid()), refKey);
			}
		}
		if (baseGroup.getRuleInfo() == null || baseGroup.getRuleInfo().isEmpty()) {
			return null;
		}
		
		List<Status> statusList = baseGroupExec.getStatusList();
		
		if (Helper.getLatestStatus(statusList) != null
				&& Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.READY, new Date()))) {
			logger.info(" If status is in READY state then no need to start and parse again. ");
			return baseGroupExec;
		}
		
		if (Helper.getLatestStatus(statusList) != null 
				&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.RUNNING, new Date())) 
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.COMPLETED, new Date())) 
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.PAUSE, new Date())))) {
			logger.info(" This process is RUNNING or has been COMPLETED previously or is On Hold. Hence it cannot be rerun. ");
			return baseGroupExec;
		}
		
		synchronized (baseGroupExec.getUuid()) {
			baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType, Status.Stage.PENDING);
		}
		
		BaseRuleExec baseRuleExec = null;
		List<BaseRuleExec> ruleExecList = new ArrayList<>();
		if (baseGroupExec.getExecList() == null) {
			baseGroupExec.setExecList(new ArrayList<>());
		}
		List<MetaIdentifierHolder> ruleExecMetaList = baseGroupExec.getExecList();
		Map<String, BaseRuleExec> ruleExecHolderMap = new HashMap<>();
		BaseRuleExec ruleExec1 = null;
		if (ruleExecMetaList != null && !ruleExecMetaList.isEmpty()) {
			for (MetaIdentifierHolder ruleExecMeta1 : ruleExecMetaList) {
				ruleExec1 = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExecMeta1.getRef().getUuid(), ruleExecMeta1.getRef().getVersion(), ruleExecMeta1.getRef().getType().toString(), "N");
				ruleExecHolderMap.put(ruleExec1.getDependsOn().getRef().getUuid(), ruleExec1);
			}
		}
		// First generate rule Sqls 
		for (MetaIdentifierHolder ruleMeta : baseGroup.getRuleInfo()) {
			Thread.sleep(1000);
			try {
				baseRuleExec = ruleExecHolderMap.get(ruleMeta.getRef().getUuid());
				if (baseRuleExec == null) {
					baseRuleExec = metaExecFactory.getRuleExec(ruleExecType);
					baseRuleExec.setDependsOn(ruleMeta);
					baseRuleExec.setName(ruleMeta.getRef().getName());
					baseRuleExec.setBaseEntity();
				}
				
				if(baseRuleExec.getDependsOn() != null && baseRuleExec.getDependsOn().getRef().getVersion() == null) {
					MetaIdentifier dependsOnMI = baseRuleExec.getDependsOn().getRef();
					BaseEntity baseEntity = (BaseEntity) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(), dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");
					dependsOnMI.setVersion(baseEntity.getVersion());
					baseRuleExec.setDependsOn(new MetaIdentifierHolder(dependsOnMI));
				}
				
				ruleExecList.add(baseRuleExec);
				MetaIdentifierHolder ruleRef = new MetaIdentifierHolder(new MetaIdentifier(ruleExecType, baseRuleExec.getUuid(), baseRuleExec.getVersion()));			
				baseGroupExec.getExecList().add(ruleRef);
				baseRuleExec = baseRuleService.create(ruleMeta.getRef().getUuid(), ruleMeta.getRef().getVersion(), ruleType, ruleExecType, baseRuleExec, refKeyMap, datapodList, dagExec);
			} catch (Exception e) {
				synchronized (baseGroupExec.getUuid()) {
					baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType, Status.Stage.FAILED);
				}
				e.printStackTrace();
//				throw new RuntimeException();
			}
		} // After adding all ruleexecs
		
		for (BaseRuleExec ruleExec2 : ruleExecList) {
			Status FAILEDStatus = new Status(Status.Stage.FAILED, new Date());
			if (ruleExec2.getStatusList() != null && Helper.getLatestStatus(ruleExec2.getStatusList()).equals(FAILEDStatus)) {
				synchronized (baseGroupExec.getUuid()) {
					baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType, Status.Stage.FAILED);
				}
				throw new RuntimeException();
			}
		}
		// Populate ruleExecList
		synchronized (baseGroupExec.getUuid()) {
			BaseRuleGroupExec ruleGroupExec1 = (BaseRuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(ruleGroupExecMeta.getUuid(), ruleGroupExecMeta.getVersion(), ruleGroupExecMeta.getType().toString(), "N");
			if (ruleGroupExec1 == null) {
				ruleGroupExec1 = baseGroupExec;
			}
			ruleGroupExec1.setExecList(baseGroupExec.getExecList());
			commonServiceImpl.save(groupExecType.toString(), ruleGroupExec1);
		}
		return baseGroupExec;
	}
	
	public BaseRuleGroupExec parse(String execUuid, 
								String execVersion, 
								MetaType groupType, 
								MetaType groupExecType, 
								MetaType ruleType, 
								MetaType ruleExecType,
								Map<String, MetaIdentifier> refKeyMap, 
								HashMap<String, String> otherParams, 
								List<String> datapodList, 
								DagExec dagExec, 
								RunMode runMode) throws Exception {
		BaseRuleGroupExec baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, groupExecType.toString());
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType, Status.Stage.STARTING);
		}
		BaseRuleExec ruleExec = null;
		RuleTemplate baseRuleService = serviceFactory.getRuleService(ruleType);
		if (baseGroupExec == null) {
			return null;
		}
		List<MetaIdentifierHolder> ruleExecList = baseGroupExec.getExecList();
		if (ruleExecList == null || ruleExecList.isEmpty()) {
			return null;
		}
//		HashMap<String, String> otherParams = null;
//		if(dagExec != null)
//			otherParams = dagExec.getExecParams().getOtherParams();
		for (MetaIdentifierHolder ruleExecMeta : ruleExecList) {
			ruleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExecMeta.getRef().getUuid(), ruleExecMeta.getRef().getVersion(), ruleExecType.toString());
			ruleExec = baseRuleService.parse(ruleExec.getUuid(), ruleExec.getVersion(), refKeyMap, otherParams, datapodList, dagExec, runMode);
		}
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType, Status.Stage.READY);
		}
		return baseGroupExec;
	}
	
	
	
	/**
	 * Execute a group. This can be overridden
	 * @param baseGroupUUID
	 * @param baseGroupVersion
	 * @param groupType
	 * @param groupExecType
	 * @param ruleType
	 * @param execParams
	 * @param baseGroupExec
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public MetaIdentifier execute(String baseGroupUUID, String baseGroupVersion, MetaType groupType, MetaType groupExecType, MetaType ruleType, MetaType ruleExecType, ExecParams execParams, BaseRuleGroupExec baseGroupExec, RunMode runMode) throws Exception {
		BaseRuleGroup baseGroup = (BaseRuleGroup) commonServiceImpl.getOneByUuidAndVersion(baseGroupExec.getDependsOn().getRef().getUuid(), baseGroupExec.getDependsOn().getRef().getVersion(), groupType.toString(), "N");
		List<FutureTask> taskList = new ArrayList<FutureTask>();
		RuleTemplate baseRuleService = serviceFactory.getRuleService(ruleType);
//		baseGroup.setInParallel("false");

		/*List<BaseRuleExec> ruleExecList = new ArrayList<>();
		List<Status> statusList = baseGroupExec.getStatusList();
		String outputThreadName = null;*/
		synchronized (baseGroupExec.getUuid()) {
			baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType, Status.Stage.RUNNING);
		}

		
		RunBaseGroupService runRuleGroup = runBaseFactory.getGroupService(groupType);	// Get this from a factory
		runRuleGroup.setMetaExecutor(metaExecutor);
		runRuleGroup.setTaskThreadMap(taskThreadMap);
		runRuleGroup.setName(groupExecType+"_"+baseGroupExec.getUuid()+"_"+baseGroupExec.getVersion());
		runRuleGroup.setCommonServiceImpl(commonServiceImpl);
		runRuleGroup.setBaseGroup(baseGroup);
		runRuleGroup.setBaseGroupExec(baseGroupExec);
		runRuleGroup.setBaseRuleService(baseRuleService);
		runRuleGroup.setRuleType(ruleType);
		runRuleGroup.setGroupExecType(groupExecType);
		runRuleGroup.setExecType(ruleExecType);
		runRuleGroup.setSessionContext(sessionHelper.getSessionContext());
		runRuleGroup.setRunMode(runMode);

		logger.info("Executing baseGroupExec : " + baseGroupExec.getName());

		FutureTask<TaskHolder> futureTask = new FutureTask<>(runRuleGroup);
		metaGroupExecutor.execute(futureTask);
		taskList.add(futureTask);
		taskThreadMap.put(groupExecType+"_"+baseGroupExec.getUuid()+"_"+baseGroupExec.getVersion(), futureTask);
		for(MetaIdentifierHolder execHolder : baseGroupExec.getExecList()) {
			BaseRuleExec baseRuleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(execHolder.getRef().getUuid(), execHolder.getRef().getVersion(), execHolder.getRef().getType().toString());
			//BaseRule baseRule = (BaseRule) commonServiceImpl.getLatestByUuid(baseRuleExec.getDependsOn().getRef().getUuid(), ruleType.toString());
			logger.info("group_sql: "+baseRuleExec.getExec());
			/*IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
			ResultSetHolder rsHolder = exec.executeSql(baseRuleExec.getExec());
			DataFrame df = rsHolder.getDataFrame();
			Dataset dataset = commonServiceImpl.getOneByUuidAndVersion(baseRule.get, version, type)
			MetaIdentifierHolder datapodKey = baseRule.get
			String tableName = runBaseRuleService.getTableName(baseGroupExec, baseRule, baseRuleExec, datapodKey);
			df.registerTempTable(tableName);
			logger.info("temp table is registered: "+tableName);*/
		}
		// Blocking to get the result back
		logger.info(" Blocking to wait till execution of group : " + baseGroupExec.getUuid() + ":" + groupExecType);
		try{
			if (futureTask != null) {
				futureTask.get();
			}
		} catch (InterruptedException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		logger.info("Removing group " + baseGroupExec.getUuid() + ":" + groupExecType + " from taskThreadMap ");
		taskThreadMap.remove(groupExecType+"_"+baseGroupExec.getUuid()+"_"+baseGroupExec.getVersion());
		return baseGroupExec.getRef(groupExecType);
	}
	
	public Status restart (String baseGroupExecUUID, String baseGroupExecVersion, MetaType groupExecType, MetaType ruleExecType) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Status operatorLeastSigStatus = null;
		BaseRuleGroupExec baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(baseGroupExecUUID, baseGroupExecVersion, groupExecType.toString(), "N");
		operatorLeastSigStatus = Helper.getLatestStatus(baseGroupExec.getStatusList());
		if(Helper.getLatestStatus(baseGroupExec.getStatusList()).equals(new Status(Status.Stage.FAILED, new Date())) 
				||Helper.getLatestStatus(baseGroupExec.getStatusList()).equals(new Status(Status.Stage.ABORTED, new Date()))
				||Helper.getLatestStatus(baseGroupExec.getStatusList()).equals(new Status(Status.Stage.KILLED, new Date()))){
			logger.info("BaseGroupExec " + baseGroupExecUUID + " FAILED/KILLED. So proceeding ... ");
			operatorLeastSigStatus = new Status(Status.Stage.READY, new Date());
			for (MetaIdentifierHolder ruleExecRefHolder : baseGroupExec.getExecList()) {
				logger.info("Checking restart of ruleexec " + ruleExecRefHolder.getRef().getUuid());
				BaseRuleExec baseRuleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExecRefHolder.getRef().getUuid(), ruleExecRefHolder.getRef().getVersion(), ruleExecType.toString(), "N");
				Object obj = (CommonServiceImpl.class.getMethod(GET + Helper.getServiceClass(Helper.getMetaTypeByExecType(ruleExecType))).invoke(commonServiceImpl));
				Status status = (Status) obj.getClass().getMethod("restart", BaseExec.class).invoke(obj, baseRuleExec);
				operatorLeastSigStatus = new Status(Helper.getPriorStatus(operatorLeastSigStatus.getStage(), status.getStage()), new Date());
			}
		}
		logger.info("Status of basegroupexec " + baseGroupExecUUID + " = " + operatorLeastSigStatus.getStage().toString());
		try {
			commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType, operatorLeastSigStatus.getStage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*if (Helper.isStatusPresent(new Status(Status.Stage.READY, new Date()), baseGroupExec.getStatusList())) {
			commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType, Status.Stage.READY);
		} else {
			commonServiceImpl.setMetaStatus(baseExec, meta.getType(), Status.Stage.PENDING);
			operatorLeastSigStatus = new Status(Status.Stage.PENDING, new Date());
		}*/
		return operatorLeastSigStatus;
	}
}

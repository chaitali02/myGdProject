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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseRuleGroup;
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.BaseRule;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;

/**
 * @author joy
 *
 */
public class RunBaseGroupService implements Callable<TaskHolder> {
	
	protected ThreadPoolTaskExecutor metaExecutor;
	protected CommonServiceImpl<?> commonServiceImpl;
	protected ConcurrentHashMap taskThreadMap;
	protected String name;
	protected BaseRuleGroup baseGroup;
	protected BaseRuleGroupExec baseGroupExec;
	protected RuleTemplate baseRuleService;
	protected MetaType ruleType;
	protected MetaType execType;
	protected MetaType groupExecType;
	protected SessionContext sessionContext;
	protected RunMode runMode;
	
	static final Logger logger = Logger.getLogger(RunBaseGroupService.class);

	/**
	 * 
	 */
	public RunBaseGroupService() {
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * @return the baseGroup
	 */
	public BaseRuleGroup getBaseGroup() {
		return baseGroup;
	}



	/**
	 * @param baseGroup the baseGroup to set
	 */
	public void setBaseGroup(BaseRuleGroup baseGroup) {
		this.baseGroup = baseGroup;
	}



	/**
	 * @return the baseGroupExec
	 */
	public BaseRuleGroupExec getBaseGroupExec() {
		return baseGroupExec;
	}



	/**
	 * @param baseGroupExec the baseGroupExec to set
	 */
	public void setBaseGroupExec(BaseRuleGroupExec baseGroupExec) {
		this.baseGroupExec = baseGroupExec;
	}



	/**
	 * @return the baseRuleService
	 */
	public RuleTemplate getBaseRuleService() {
		return baseRuleService;
	}



	/**
	 * @param baseRuleService the baseRuleService to set
	 */
	public void setBaseRuleService(RuleTemplate baseRuleService) {
		this.baseRuleService = baseRuleService;
	}



	/**
	 * @return the ruleType
	 */
	public MetaType getRuleType() {
		return ruleType;
	}



	/**
	 * @param ruleType the ruleType to set
	 */
	public void setRuleType(MetaType ruleType) {
		this.ruleType = ruleType;
	}



	/**
	 * @return the execType
	 */
	public MetaType getExecType() {
		return execType;
	}



	/**
	 * @param execType the execType to set
	 */
	public void setExecType(MetaType execType) {
		this.execType = execType;
	}



	/**
	 * @return the groupExecType
	 */
	public MetaType getGroupExecType() {
		return groupExecType;
	}



	/**
	 * @param groupExecType the groupExecType to set
	 */
	public void setGroupExecType(MetaType groupExecType) {
		this.groupExecType = groupExecType;
	}


	/**
	 * @return the sessionContext
	 */
	public SessionContext getSessionContext() {
		return sessionContext;
	}


	/**
	 * @param sessionContext the sessionContext to set
	 */
	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}


	/**
	 * @return the runMode
	 */
	public RunMode getRunMode() {
		return runMode;
	}


	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}


	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public TaskHolder call() throws Exception {
		return execute();
	}
	
	public TaskHolder execute() throws Exception {
		logger.info("Inside execute of RunBaseGroupService");
		MetaIdentifier groupExecMeta = null;
		BaseRuleExec baseRuleExec = null;
		FrameworkThreadLocal.getSessionContext().set(sessionContext);
		List<FutureTask<TaskHolder>> taskList = new ArrayList<FutureTask<TaskHolder>>();
		do {
				for (MetaIdentifierHolder ruleExecHolder : baseGroupExec.getExecList()) {
					
					try {
						baseRuleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExecHolder.getRef().getUuid(),ruleExecHolder.getRef().getVersion(), execType.toString());
						logger.info("Executing ruleExec : " + ruleExecHolder.getRef().getUuid() + ":" + ruleExecHolder.getRef().getName());
						BaseRule baseRule = (BaseRule) commonServiceImpl.getLatestByUuid(baseRuleExec.getDependsOn().getRef().getUuid(), ruleType.toString());
						logger.info("Executing rule : " + baseRule.getUuid() + ":" + baseRule.getName());
		
						if (baseGroup.getInParallel() != null && baseGroup.getInParallel().equalsIgnoreCase("true")) {
							baseRuleExec = baseRuleService.execute(baseRule.getUuid(), baseRule.getVersion(), metaExecutor, baseRuleExec, baseGroupExec, null, taskList, null, runMode);
						} else {
							baseRuleExec = baseRuleService.execute(baseRule.getUuid(), baseRule.getVersion(), null, baseRuleExec, baseGroupExec, null, null, null, runMode);
						}
					} catch (Exception e) {
						synchronized (baseGroupExec.getUuid()) {
							baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType, Status.Stage.Failed);
						}
						e.printStackTrace();
						//	throw new RuntimeException();
					}
				}	// End For
				Thread.sleep(10000);
		} while(!waitAndComplete(baseGroup, baseGroupExec, taskList));
		groupExecMeta = baseGroupExec.getRef(groupExecType);
		TaskHolder taskHolder = new TaskHolder(name, groupExecMeta);
		return taskHolder;
	}
	
	
	public Boolean waitAndComplete (BaseRuleGroup baseGroup, BaseRuleGroupExec baseGroupExec, List<FutureTask<TaskHolder>> taskList) throws Exception {
		logger.info(" Inside waitAndComplete for RunBaseGroupService ");
		String outputThreadName = null;
		boolean completed = true;
		boolean killed = false;
		boolean failed = false;
		boolean onHold = false;
		boolean resume = false;
		TaskHolder taskHolder = null;
		try {
			// Collect results and clean map
				//if (ruleGroup.getInParallel() != null && ruleGroup.getInParallel().equalsIgnoreCase("true")) {
					logger.info(" Tasklist size before get : " + taskList.size());
					for (FutureTask<TaskHolder> futureTask : taskList) {
			            try {
			            	taskHolder = futureTask.get();
			            	if (taskHolder != null) {
			            		outputThreadName = taskHolder.getName();
			            	}
			                logger.info("Thread " + outputThreadName + " completed ");
			                taskThreadMap.remove(outputThreadName);
			            } catch (InterruptedException e) {
			                e.printStackTrace();
			            } catch (ExecutionException | CancellationException e) {
			                e.printStackTrace();
			            }catch (Exception e) {
			                e.printStackTrace();
			            }
			        }
					
				//}
				baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(baseGroupExec.getUuid(), baseGroupExec.getVersion(), groupExecType.toString());
				Status.Stage latestStatus = Helper.getLatestStatus(baseGroupExec.getStatusList()).getStage();
				Status status=commonServiceImpl.getGroupStatus(baseGroupExec,groupExecType, execType);
				synchronized (baseGroupExec.getUuid()) {
					 if(status.getStage().equals(Status.Stage.Killed)){
						 baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType,Status.Stage.Terminating);
						 baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType,Status.Stage.Killed);
					 }
					 if(!latestStatus.equals(status.getStage())){
						 baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType,status.getStage());
						 return true;  
					 }
					
				}
		} catch (Exception e) {
			synchronized (baseGroupExec.getUuid()) {
				baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType,Status.Stage.Failed);
			}
			e.printStackTrace();
			return true;
//			throw new RuntimeException();
		}
			return false;
	}	// End waitAndComplete


}

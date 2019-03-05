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

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;

/**
 * @author joy
 *
 */
public class BaseRuleExecTemplate {
	
	@Resource(name="taskThreadMap")
	protected ConcurrentHashMap<?, ?> taskThreadMap;
	@Autowired
	protected CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(BaseRuleExecTemplate.class);
	
	/**
	 * 
	 */
	public BaseRuleExecTemplate() {
	}

	/**
	 * Sends kill message to kill a rule. 
	 * First, it sets the status to TERMINATING in case the status is RUNNING.
	 * Then, it tries to kill the thread. 
	 * Then, it sets the status to KILLED if the latest status is TERMINATING, whether or not it was able to kill the thread.
	 * Even if it was not able to kill a thread (because the thread COMPLETED or cancelled in between), if the status was TERMINATING, the status shall change to KILLED. 
	 * @param uuid
	 * @param version
	 * @param execType
	 */
	public void kill (String uuid, String version, MetaType execType) {
		BaseRuleExec baseRuleExec = null;
		try {
			baseRuleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, execType.toString(), "N");
		} catch (JsonProcessingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if (baseRuleExec == null) {
			logger.info("RuleExec not found. Exiting...");
			return;
		}
		
		try {
			logger.info("Before kill - Rule - " + baseRuleExec.getUuid());
			synchronized (baseRuleExec.getUuid()) {
				baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, execType, Status.Stage.TERMINATING);
				if (!Helper.getLatestStatus(baseRuleExec.getStatusList()).equals(new Status(Status.Stage.TERMINATING, new Date()))) {
					logger.info("Latest Status is not in TERMINATING. Exiting...");
					return;
				}
			}
			@SuppressWarnings("unchecked")
			FutureTask<TaskHolder> futureTask = (FutureTask<TaskHolder>) taskThreadMap.get(execType+"_"+baseRuleExec.getUuid()+"_"+baseRuleExec.getVersion());
			if (futureTask != null) {
				futureTask.cancel(true);
			}
			synchronized (baseRuleExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseRuleExec, execType, Status.Stage.KILLED);
			}
		} catch (Exception e) {
			logger.info("FAILED to kill. uuid : " + uuid + " version : " + version);
			try {
				synchronized (baseRuleExec.getUuid()) {
					commonServiceImpl.setMetaStatus(baseRuleExec, execType, Status.Stage.KILLED);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			taskThreadMap.remove(execType+"_"+baseRuleExec.getUuid()+"_"+baseRuleExec.getVersion());
			e.printStackTrace();
		}
	}
	
	/**
	 * Set status of BaseRuleExec to PAUSE if status is KILLED, PENDING, or RESUME 
	 * @param uuid
	 * @param version
	 * @param execType
	 */
	public void PAUSE (String uuid, String version, MetaType execType) {
		BaseRuleExec baseRuleExec = null;
		try {
			baseRuleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, execType.toString());
		} catch (JsonProcessingException e2) {
			e2.printStackTrace();
		}
		if (baseRuleExec == null) {
			logger.info("BaseRuleExec not found. Exiting...");
			return;
		}
		// Pre conditions for PAUSE shall be determined by the setMetaStatus
		try {
			synchronized (baseRuleExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseRuleExec, execType, Status.Stage.PAUSE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set status of BaseRuleExec to RESUME if status is PAUSE
	 * @param uuid
	 * @param version
	 * @param execType
	 */
	public void RESUME (String uuid, String version, MetaType execType) {
		BaseRuleExec baseRuleExec = null;
		try {
			baseRuleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, execType.toString());
		} catch (JsonProcessingException e2) {
			e2.printStackTrace();
		}
		if (baseRuleExec == null) {
			logger.info("BaseRuleExec not found. Exiting...");
			return;
		}
		// Pre conditions for RESUME shall be determined by the setMetaStatus
		try {
			synchronized (baseRuleExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseRuleExec, execType, Status.Stage.RESUME);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

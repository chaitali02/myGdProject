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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Batch;
import com.inferyx.framework.domain.BatchExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Status.Stage;
import com.inferyx.framework.enums.RunMode;

/**
 * @author Ganesh
 *
 */
@Service
public class BatchServiceImpl {

	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private DagServiceImpl dagServiceImpl;
	@Autowired
	private DagExecServiceImpl dagExecServiceImpl;
	
	static final Logger logger = Logger.getLogger(BatchServiceImpl.class);
	
	public BatchExec create(String batchUuid, String batchVersion, ExecParams execParams, BatchExec batchExec, RunMode runMode) throws Exception {
		try {
			Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchUuid, batchVersion, MetaType.batch.toString());
			if(batchExec == null) {
				MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.batch, batchUuid, batchUuid));
				batchExec = new BatchExec();
				batchExec.setDependsOn(dependsOn);
				batchExec.setBaseEntity();
			}
			
			batchExec.setExecParams(execParams);
			
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			List<Status> statusList = batchExec.getStatusList();
			if (statusList == null) {
				statusList = new ArrayList<Status>();
			}
			batchExec.setName(batch.getName());
			batchExec.setAppInfo(batch.getAppInfo());	
			
			commonServiceImpl.save(MetaType.batchExec.toString(), batchExec);
			
			batchExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
	
			batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.NotStarted);
		} catch (Exception e) {
			logger.error(e);
			batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.Failed);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable batch.");
			throw new RuntimeException((message != null) ? message : "Can not create executable batch.");
		}			
		return batchExec;
	}

	public BatchExec execute(String batchUuid, String batchVersion, BatchExec batchExec, ExecParams execParams, String type, RunMode runMode) throws Exception {
		Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchUuid, batchVersion, MetaType.batch.toString());
		List<MetaIdentifierHolder> execList = new ArrayList<>();
		for(MetaIdentifierHolder metaMI : batch.getMetaList()) {
			switch(metaMI.getRef().getType()) {
				case dag : execList.add(dagServiceImpl.submitDag(metaMI.getRef().getUuid(), metaMI.getRef().getVersion(), execParams, type, runMode));
					break;
			default:
				break;
			}
			
		}
		
		batchExec.setExecList(execList);
		batchExec = checkBatchStatus(batchExec);
		return batchExec;
	}
	
	public BatchExec checkBatchStatus(BatchExec batchExec) throws Exception {
		List<MetaIdentifierHolder> execList = batchExec.getExecList();
		boolean areAllCompleted = false;
		Stage batchStatus = Helper.getLatestStatus(batchExec.getStatusList()).getStage();
		boolean isKilled = false;
		boolean isFailed = false;
		boolean isCompleted = false;
		boolean isAnyOneTerminating = false;
		boolean isAnyOneInProgress = false;
		
		do {
			for(int i=0; i<execList.size(); i++) {
				MetaIdentifier execMI = execList.get(i).getRef();				
				Status latestStatus = checkStatusByExec(execMI);
				
				if(latestStatus.getStage().equals(Status.Stage.InProgress)) {
					isAnyOneInProgress = true;
				}
				
				if(latestStatus.getStage().equals(Status.Stage.Terminating)) {
					isAnyOneTerminating = true;
				}
				
				if(latestStatus.getStage().equals(Status.Stage.Completed)) {
					areAllCompleted = true;
					isCompleted = true;
				} else if(latestStatus.getStage().equals(Status.Stage.Killed)) {
					areAllCompleted = true;
					isKilled = true;
				} else if(latestStatus.getStage().equals(Status.Stage.Failed)) {
					areAllCompleted = true;
					isFailed = true;
				} else {
					areAllCompleted = false;
				}				
			}
			
			if(isAnyOneInProgress && Helper.getLatestStatus(batchExec.getStatusList()).getStage().equals(Status.Stage.NotStarted)) {
				batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.InProgress);
			}
			
			if(isAnyOneTerminating) {
				batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.Terminating);
			}
			
			if(areAllCompleted) {
				if(isFailed) {
					batchStatus = Status.Stage.Failed;
				} else if(isKilled) {
					batchStatus = Status.Stage.Killed;
				} else if(isCompleted) {
					batchStatus = Status.Stage.Completed;
				}
				batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, batchStatus);
			}
		} while(!areAllCompleted);
		
		return batchExec;
	}
	
	public Status checkStatusByExec(MetaIdentifier execMI) throws JsonProcessingException {		
		switch(execMI.getType()) {
			case dagExec: 
				DagExec baseExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(execMI.getUuid(), execMI.getVersion(), execMI.getType().toString());
				return Helper.getLatestStatus(baseExec.getStatusList());
				
			default: return null;				
		}
	}

	public BatchExec kill(String execUuid, String execVersion) throws Exception {
		BatchExec batchExec = (BatchExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.batchExec.toString());
		for(MetaIdentifierHolder execHolder : batchExec.getExecList()) {
			switch(execHolder.getRef().getType()) {
				case dagExec: dagExecServiceImpl.kill(execHolder.getRef().getUuid(), execHolder.getRef().getVersion(), null, null);
					break;
				default: return null;	
			}
		}		
		return checkBatchStatus(batchExec);
	}

	public BatchExec restart(String execUuid, String execVersion, RunMode runMode) throws Exception {
		BatchExec batchExec = (BatchExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.batchExec.toString());
		for(MetaIdentifierHolder execHolder : batchExec.getExecList()) {
			switch(execHolder.getRef().getType()) {
				case dagExec: dagServiceImpl.restart(execHolder.getRef().getUuid(), execHolder.getRef().getVersion(), runMode);
					break;
					
				default: return null;	
			}
		}		
		return checkBatchStatus(batchExec);
	}
}

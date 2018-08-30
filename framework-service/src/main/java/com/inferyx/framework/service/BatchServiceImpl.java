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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.SessionHelper;
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
	@Autowired
	private ThreadPoolTaskExecutor batchExecutor;
	@Resource(name="batchThreadMap")
	ConcurrentHashMap<String, FutureTask<String>> batchThreadMap;
	@Autowired
	private SessionHelper sessionHelper;
	
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
	
	public BatchExec submitBatch(String batchUuid, String batchVersion, BatchExec batchExec, ExecParams execParams, String type, RunMode runMode) throws Exception {
		Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchUuid, batchVersion, MetaType.batch.toString());
		RunBatchServiceImpl runBatchServiceImpl = new RunBatchServiceImpl();
		
		//Check if DAG is ready for execution
		Status.Stage stg = Helper.getLatestStatus(batchExec.getStatusList()).getStage();		
		if (stg.equals(Status.Stage.InProgress) || stg.equals(Status.Stage.Completed) || stg.equals(Status.Stage.OnHold)) {
			logger.info("BatchExec is already in InProgress/Completed/OnHold status. Aborting execution....");
			throw new Exception("BatchExec is already in InProgress/Completed/OnHold status. Aborting execution....");
		}
					
		// Populate ParseRunDagServiceImpl		
		runBatchServiceImpl.setBatchExec(batchExec);
		runBatchServiceImpl.setBatchUuid(batchUuid);
		runBatchServiceImpl.setBatchVersion(batchVersion);
		runBatchServiceImpl.setExecParams(execParams);
		runBatchServiceImpl.setRunMode(runMode);
		runBatchServiceImpl.setType(type);
		runBatchServiceImpl.setBatchServiceImpl(this);
		runBatchServiceImpl.setCommonServiceImpl(commonServiceImpl);
		runBatchServiceImpl.setDagServiceImpl(dagServiceImpl);
		runBatchServiceImpl.setSessionContext(sessionHelper.getSessionContext());
		
		if(batch.getInParallel() != null && !batch.getInParallel().isEmpty() && batch.getInParallel().equalsIgnoreCase("true")) {
			FutureTask<String> futureTask = new FutureTask<String>(runBatchServiceImpl);
			batchExecutor.execute(futureTask);
			logger.info("Thread watch : BatchExec : " + batchExec.getUuid() + " started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
			batchThreadMap.put("Batch_" + batchExec.getUuid(), futureTask);
			Thread.sleep(1000);
		} else {
			runBatchServiceImpl.execute();
		}
		
		return batchExec;
	}

	/********************** UNUSED **********************/
/*	public BatchExec execute(String batchUuid, String batchVersion, BatchExec batchExec, ExecParams execParams, String type, RunMode runMode) throws Exception {
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
		commonServiceImpl.save(MetaType.batchExec.toString(), batchExec);
		batchExec = checkBatchStatus(batchExec);
		return batchExec;
	}*/
	
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
			
			if(isAnyOneInProgress) {
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
			
			case batchExec: 	
				BatchExec batchExec = (BatchExec) commonServiceImpl.getOneByUuidAndVersion(execMI.getUuid(), execMI.getVersion(), execMI.getType().toString());
				return Helper.getLatestStatus(batchExec.getStatusList());
				
			default: return null;				
		}
	}

	public BatchExec kill(String execUuid, String execVersion) throws Exception {
		BatchExec batchExec = (BatchExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.batchExec.toString());
		for(MetaIdentifierHolder execHolder : batchExec.getExecList()) {
			switch(execHolder.getRef().getType()) {
				case dagExec: 
					try {
						dagExecServiceImpl.kill(execHolder.getRef().getUuid(), execHolder.getRef().getVersion(), null, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
					
				default: 
			}
		}		
		return checkIndvExecKillStatus(batchExec);
	}
	
	public BatchExec submitKill(String execUuid, String execVersion) throws Exception {
		BatchExec batchExec = (BatchExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.batchExec.toString());
		RunBatchServiceImpl runBatchServiceImpl = new RunBatchServiceImpl();
		runBatchServiceImpl.setBatchExec(batchExec);
		runBatchServiceImpl.setDagExecServiceImpl(dagExecServiceImpl);
		runBatchServiceImpl.setBatchServiceImpl(this);
		runBatchServiceImpl.setCommonServiceImpl(commonServiceImpl);
		runBatchServiceImpl.setDagServiceImpl(dagServiceImpl);
		runBatchServiceImpl.setSessionContext(sessionHelper.getSessionContext());		
		runBatchServiceImpl.kill();
		Thread.sleep(1000);
		
		return batchExec;
	}
	
	public BatchExec submitRestart(String execUuid, String execVersion, RunMode runMode) throws Exception {
		BatchExec batchExec = (BatchExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.batchExec.toString());
		RunBatchServiceImpl runBatchServiceImpl = new RunBatchServiceImpl();
		runBatchServiceImpl.setBatchExec(batchExec);
		runBatchServiceImpl.setDagExecServiceImpl(dagExecServiceImpl);
		runBatchServiceImpl.setBatchServiceImpl(this);
		runBatchServiceImpl.setCommonServiceImpl(commonServiceImpl);
		runBatchServiceImpl.setDagServiceImpl(dagServiceImpl);
		runBatchServiceImpl.setSessionContext(sessionHelper.getSessionContext());
		runBatchServiceImpl.setRunMode(runMode);
		runBatchServiceImpl.restart();
		Thread.sleep(1000);
		
		return batchExec;
	}
	
	public BatchExec checkIndvExecKillStatus(BatchExec batchExec) throws Exception {
		boolean areAllExecKilled = false;
		boolean isAnyoneFailed = false;
		do {			
			for(MetaIdentifierHolder execHolder : batchExec.getExecList()) {
				Status latestStatus = checkStatusByExec(execHolder.getRef());
				
				if(latestStatus.getStage().equals(Status.Stage.Terminating)) {
					MetaIdentifier batchExecMI = new MetaIdentifier(MetaType.batchExec, batchExec.getUuid(), batchExec.getVersion());
					Status batchExecLatestStatus = checkStatusByExec(batchExecMI);
					
					if(batchExecLatestStatus.getStage().equals(Status.Stage.InProgress)) {
						batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.Terminating);
					}
				} else if(latestStatus.getStage().equals(Status.Stage.Killed)) {
					areAllExecKilled = true;
					MetaIdentifier batchExecMI = new MetaIdentifier(MetaType.batchExec, batchExec.getUuid(), batchExec.getVersion());
					Status batchExecLatestStatus = checkStatusByExec(batchExecMI);
					
					if(batchExecLatestStatus.getStage().equals(Status.Stage.InProgress)) {
						batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.Terminating);
					}
				} else if(latestStatus.getStage().equals(Status.Stage.Failed)) {
					areAllExecKilled = true;
					isAnyoneFailed = true;
				} else if(latestStatus.getStage().equals(Status.Stage.NotStarted) || latestStatus.getStage().equals(Status.Stage.InProgress)) {
					kill(batchExec.getUuid(), batchExec.getVersion());
					areAllExecKilled = false;
				}
			}
		} while(!areAllExecKilled);	
		
		if(areAllExecKilled) {
			if(isAnyoneFailed) {
				batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.Failed);
			} else {
				batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.Killed);
			}
		}
		return batchExec;
	}

	public BatchExec restart(String execUuid, String execVersion, RunMode runMode) throws Exception {
		BatchExec batchExec = (BatchExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.batchExec.toString());		
		for(MetaIdentifierHolder execHolder : batchExec.getExecList()) {
			switch(execHolder.getRef().getType()) {
				case dagExec:
					try {
						dagServiceImpl.restart(execHolder.getRef().getUuid(), execHolder.getRef().getVersion(), runMode);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
					
				default:	
			}
		}	
		return checkIndvExecRestartStatus(batchExec, runMode);
	}
	
	public BatchExec checkIndvExecRestartStatus(BatchExec batchExec, RunMode runMode) throws Exception {
		boolean areAllExecRestarted = false;
		boolean isAnyoneFailed = false;
		
		do {
			for(MetaIdentifierHolder execHolder : batchExec.getExecList()) {
				Status latestStatus = checkStatusByExec(execHolder.getRef());
				
				if(latestStatus.getStage().equals(Status.Stage.Killed)) {
					try {
						restart(batchExec.getUuid(), batchExec.getVersion(), runMode);
					} catch (Exception e) {
						e.printStackTrace();
						areAllExecRestarted = false;
						isAnyoneFailed = true;
					}
				} else if(latestStatus.getStage().equals(Status.Stage.InProgress)) {
					areAllExecRestarted = true;

					MetaIdentifier batchExecMI = new MetaIdentifier(MetaType.batchExec, batchExec.getUuid(), batchExec.getVersion());
					Status batchExecLatestStatus = checkStatusByExec(batchExecMI);
					
					if(batchExecLatestStatus.getStage().equals(Status.Stage.Killed)) {
						batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.InProgress);
					}
				} else if(latestStatus.getStage().equals(Status.Stage.Failed)) {
					areAllExecRestarted = true;
					isAnyoneFailed = true;
				}
			}
		} while(!areAllExecRestarted);
		
		if(areAllExecRestarted) {
			if(isAnyoneFailed) {
				batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.Failed);
			} else {
				batchExec = checkBatchStatus(batchExec);
			}			
		}
		return batchExec;
	}
}

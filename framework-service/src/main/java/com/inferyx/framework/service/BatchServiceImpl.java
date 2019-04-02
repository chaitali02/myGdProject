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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import com.inferyx.framework.domain.Notification;
import com.inferyx.framework.domain.SenderInfo;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Status.Stage;
import com.inferyx.framework.domain.User;
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
	@Autowired
	private FrameworkThreadServiceImpl frameworkThreadServiceImpl;
	@Autowired
	private NotificationServiceImpl notificationServiceImpl;
	
	static final Logger logger = Logger.getLogger(BatchServiceImpl.class);
	
	public BatchExec create(String batchUuid, String batchVersion, ExecParams execParams, BatchExec batchExec, RunMode runMode) throws Exception {
		try {
			Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchUuid, batchVersion, MetaType.batch.toString(), "N");
			if(batchExec == null) {
				MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.batch, batchUuid, batchVersion));
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
	
			batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.PENDING);
			batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.STARTING);
			batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.READY);

		} catch (Exception e) {
			logger.error(e);
			batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.FAILED);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.batchExec, batchExec.getUuid(), batchExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable batch.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Can not create executable batch.");
		}			
		return batchExec;
	}
	
	
	
	public BatchExec submitBatch(String batchUuid, String batchVersion, BatchExec batchExec, ExecParams execParams, String type, RunMode runMode) throws Exception {
		Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchUuid, batchVersion, MetaType.batch.toString());
		RunBatchServiceImpl runBatchServiceImpl = new RunBatchServiceImpl();
		
		//Check if DAG is READY for execution
		Status.Stage stg = Helper.getLatestStatus(batchExec.getStatusList()).getStage();		
		if (stg.equals(Status.Stage.RUNNING) || stg.equals(Status.Stage.COMPLETED) || stg.equals(Status.Stage.PAUSE)) {
			logger.info("BatchExec is alReady in RUNNING/COMPLETED/PAUSE status. Aborting execution....");
			throw new Exception("BatchExec is alReady in RUNNING/COMPLETED/PAUSE status. Aborting execution....");
		}
		
		User user = (User) commonServiceImpl.getOneByUuidAndVersion(batchExec.getCreatedBy().getRef().getUuid(), batchExec.getCreatedBy().getRef().getVersion(), batchExec.getCreatedBy().getRef().getType().toString());
		frameworkThreadServiceImpl.setSession(user.getName(), batch.getAppInfo().get(0));
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
	
	public BatchExec checkBatchStatus(BatchExec batchExec) throws Exception {
		Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchExec.getDependsOn().getRef().getUuid(), batchExec.getDependsOn().getRef().getVersion(), batchExec.getDependsOn().getRef().getType().toString());
		List<MetaIdentifierHolder> execList = batchExec.getExecList();
		boolean areAllCOMPLETED = false;
		Stage batchStatus = Helper.getLatestStatus(batchExec.getStatusList()).getStage();
		boolean isKILLED = false;
		boolean isFAILED = false;
		boolean isCOMPLETED = false;
		Map<Stage, Boolean> statusMap = new HashMap<>();
		
		do {
			for(int i=0; i<execList.size(); i++) {
				MetaIdentifier execMI = execList.get(i).getRef();				
				Status latestStatus = checkStatusByExec(execMI);
				
				if(latestStatus.getStage().equals(Status.Stage.COMPLETED)) {
					isCOMPLETED = true;
					statusMap.put(Stage.COMPLETED, true);
				} else if(latestStatus.getStage().equals(Status.Stage.RUNNING)) {
					statusMap.put(Stage.RUNNING, true);
					MetaIdentifier batchExecMI = new MetaIdentifier(MetaType.batchExec, batchExec.getUuid(), batchExec.getVersion());
					Status batchExecLatestStatus = checkStatusByExec(batchExecMI);
					
					if(!batchExecLatestStatus.getStage().equals(Status.Stage.RUNNING)) {
						batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.RUNNING);
					}
				} else if(latestStatus.getStage().equals(Status.Stage.TERMINATING)) {
					statusMap.put(Stage.TERMINATING, true);
					MetaIdentifier batchExecMI = new MetaIdentifier(MetaType.batchExec, batchExec.getUuid(), batchExec.getVersion());
					Status batchExecLatestStatus = checkStatusByExec(batchExecMI);
					
					if(!batchExecLatestStatus.getStage().equals(Status.Stage.TERMINATING)) {
						batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.TERMINATING);
					}
				} else if(latestStatus.getStage().equals(Status.Stage.PENDING)) {
					statusMap.put(Stage.PENDING, true);
				} else if(latestStatus.getStage().equals(Status.Stage.KILLED)) {
					isKILLED = true;
					statusMap.put(Stage.KILLED, true);
				} else if(latestStatus.getStage().equals(Status.Stage.FAILED)) {
					isFAILED = true;
					statusMap.put(Stage.FAILED, true);
				} 			
			}
			
			if((statusMap.get(Stage.PENDING) != null && statusMap.get(Stage.PENDING).equals(true)) 
					|| (statusMap.get(Stage.RUNNING) != null && statusMap.get(Stage.RUNNING).equals(true))
					|| (statusMap.get(Stage.TERMINATING) != null && statusMap.get(Stage.TERMINATING).equals(true))) {
				areAllCOMPLETED = false;
			} else if((statusMap.get(Stage.COMPLETED) != null && statusMap.get(Stage.COMPLETED).equals(true)) 
					|| (statusMap.get(Stage.FAILED) != null && statusMap.get(Stage.FAILED).equals(true))
					|| (statusMap.get(Stage.KILLED) != null && statusMap.get(Stage.KILLED).equals(true))) {
				areAllCOMPLETED = true;
			}
			statusMap = resetMap(statusMap);
			if(isFAILED && batch.getInParallel() != null && !batch.getInParallel().isEmpty() && batch.getInParallel().equalsIgnoreCase("false")) {
				batchExec = kill(batchExec.getUuid(), batchExec.getVersion());
			}
			
			if(areAllCOMPLETED) {
				if(isFAILED) {
					batchStatus = Status.Stage.FAILED;
				} else if(isKILLED) {
					batchStatus = Status.Stage.KILLED;
				} else if(isCOMPLETED) {
					batchStatus = Status.Stage.COMPLETED;
				}
				batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, batchStatus);
			}
			Thread.sleep(10000);
		} while(!areAllCOMPLETED);
		
		return batchExec;
	}
	
	public Map<Stage, Boolean> resetMap(Map<Stage, Boolean> statusMap) {
		statusMap.put(Stage.COMPLETED, false);
		statusMap.put(Stage.RUNNING, false);
		statusMap.put(Stage.TERMINATING, false);
		statusMap.put(Stage.PENDING, false);
		statusMap.put(Stage.KILLED, false);
		statusMap.put(Stage.FAILED, false);		
		return statusMap;
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
		synchronized (batchExec.getUuid()) {
			batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.READY);
		}
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
		boolean areAllExecKILLED = false;
		boolean isAnyoneFAILED = false;
		do {			
			for(MetaIdentifierHolder execHolder : batchExec.getExecList()) {
				Status latestStatus = checkStatusByExec(execHolder.getRef());
				
				if(latestStatus.getStage().equals(Status.Stage.TERMINATING)) {
					MetaIdentifier batchExecMI = new MetaIdentifier(MetaType.batchExec, batchExec.getUuid(), batchExec.getVersion());
					Status batchExecLatestStatus = checkStatusByExec(batchExecMI);
					
					if(batchExecLatestStatus.getStage().equals(Status.Stage.RUNNING)) {
						batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.TERMINATING);
					}
				} else if(latestStatus.getStage().equals(Status.Stage.KILLED)) {
					areAllExecKILLED = true;
					MetaIdentifier batchExecMI = new MetaIdentifier(MetaType.batchExec, batchExec.getUuid(), batchExec.getVersion());
					Status batchExecLatestStatus = checkStatusByExec(batchExecMI);
					
					if(batchExecLatestStatus.getStage().equals(Status.Stage.RUNNING)) {
						batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.TERMINATING);
					}
				} else if(latestStatus.getStage().equals(Status.Stage.FAILED)) {
					areAllExecKILLED = true;
					isAnyoneFAILED = true;
				} else if(latestStatus.getStage().equals(Status.Stage.PENDING) || latestStatus.getStage().equals(Status.Stage.RUNNING)) {
					kill(batchExec.getUuid(), batchExec.getVersion());
					areAllExecKILLED = false;
				}
			}
		} while(!areAllExecKILLED);	
		
		if(areAllExecKILLED) {
			if(isAnyoneFAILED) {
				batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.FAILED);
			} else {
				batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.KILLED);
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
		boolean isAnyoneFAILED = false;
		
		do {
			for(MetaIdentifierHolder execHolder : batchExec.getExecList()) {
				Status latestStatus = checkStatusByExec(execHolder.getRef());
				
				if(latestStatus.getStage().equals(Status.Stage.KILLED)) {
					try {
						restart(batchExec.getUuid(), batchExec.getVersion(), runMode);
					} catch (Exception e) {
						e.printStackTrace();
						areAllExecRestarted = false;
						isAnyoneFAILED = true;
					}
				} else if(latestStatus.getStage().equals(Status.Stage.RUNNING)) {
					areAllExecRestarted = true;

					MetaIdentifier batchExecMI = new MetaIdentifier(MetaType.batchExec, batchExec.getUuid(), batchExec.getVersion());
					Status batchExecLatestStatus = checkStatusByExec(batchExecMI);
					
					if(batchExecLatestStatus.getStage().equals(Status.Stage.KILLED)) {
						batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.RUNNING);
					}
				} else if(latestStatus.getStage().equals(Status.Stage.FAILED)) {
					areAllExecRestarted = true;
					isAnyoneFAILED = true;
				}
			}
			Thread.sleep(5000);
		} while(!areAllExecRestarted);
		
		if(areAllExecRestarted) {
			if(isAnyoneFAILED) {
				batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.FAILED);
			} else {
				batchExec = checkBatchStatus(batchExec);
			}			
		}
		return batchExec;
	}



	/**
	 * @param senderInfo
	 * @param batch
	 * @param batchExec
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public boolean sendSuccessNotification(SenderInfo senderInfo, Batch batch, BatchExec batchExec) throws FileNotFoundException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		logger.info("sending success notification...");
		Notification notification = new Notification();

		String subject = commonServiceImpl.getConfigValue("framework.email.subject");
		subject = MessageFormat.format(subject, "SUCCESS", "Batch", batch.getName(), "COMPLETED");
		notification.setSubject(subject);

		String roleUuid = sessionHelper.getSessionContext().getRoleInfo().getRef().getUuid();
		String appUuid = sessionHelper.getSessionContext().getAppInfo().getRef().getUuid();
		
		String contextPath = commonServiceImpl.getConfigValue("framework.webserver.contextpath");
		if(contextPath.startsWith("")) {
			contextPath = "";
		} else {
			contextPath = contextPath.startsWith("/") ? contextPath : "/".concat(contextPath);
			contextPath = contextPath.endsWith("/") ? contextPath.substring(contextPath.lastIndexOf("/")) : contextPath;	
		}
		
		String resultUrl = commonServiceImpl.getConfigValue("framework.url.batch.result");
		resultUrl = MessageFormat.format(resultUrl, commonServiceImpl.getConfigValue("framework.webserver.host"),
				commonServiceImpl.getConfigValue("framework.webserver.port"), contextPath, batchExec.getUuid(), batchExec.getVersion(),
				batch.getName(), roleUuid, appUuid);

		String message = commonServiceImpl.getConfigValue("framework.email.body");
		message = MessageFormat.format(message, resultUrl);
		notification.setMessage(message);
		notification.setSenderInfo(senderInfo);
		return notificationServiceImpl.prepareAndSendNotification(notification);
	}



	/**
	 * @param senderInfo
	 * @param batch
	 * @param batchExec
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public boolean sendFailureNotification(SenderInfo senderInfo, Batch batch, BatchExec batchExec) throws FileNotFoundException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		logger.info("sending fail notification...");
		Notification notification = new Notification();
		
		String subject = commonServiceImpl.getConfigValue("framework.email.subject");
		subject = MessageFormat.format(subject, "FAILURE", "Batch", batch.getName(), "FAILED");
		notification.setSubject(subject);

		String roleUuid = sessionHelper.getSessionContext().getRoleInfo().getRef().getUuid();
		String appUuid = sessionHelper.getSessionContext().getAppInfo().getRef().getUuid();

		String contextPath = commonServiceImpl.getConfigValue("framework.webserver.contextpath");
		if(contextPath.startsWith("")) {
			contextPath = "";
		} else {
			contextPath = contextPath.startsWith("/") ? contextPath : "/".concat(contextPath);
			contextPath = contextPath.endsWith("/") ? contextPath.substring(contextPath.lastIndexOf("/")) : contextPath;	
		}
		
		String resultUrl = commonServiceImpl.getConfigValue("framework.url.batch.result");
		resultUrl = MessageFormat.format(resultUrl, commonServiceImpl.getConfigValue("framework.webserver.host"),
				commonServiceImpl.getConfigValue("framework.webserver.port"), contextPath, batchExec.getUuid(), batchExec.getVersion(),
				batch.getName(), roleUuid, appUuid);
		
		String message = commonServiceImpl.getConfigValue("framework.email.body");
		message = MessageFormat.format(message, resultUrl);
		notification.setMessage(message);
		
		notification.setSenderInfo(senderInfo);
		return notificationServiceImpl.prepareAndSendNotification(notification);
	}
}

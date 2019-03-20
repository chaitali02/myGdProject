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

import org.apache.log4j.Logger;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Batch;
import com.inferyx.framework.domain.BatchExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.SenderInfo;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
/**
 * @author Ganesh
 *
 */
public class RunBatchServiceImpl implements Callable<String> {

	private CommonServiceImpl<?> commonServiceImpl;
	private DagServiceImpl dagServiceImpl;
	private BatchServiceImpl batchServiceImpl;
	private String batchUuid;
	private String batchVersion;
	private ExecParams execParams;
	private String type;
	private RunMode runMode;
	private BatchExec batchExec;
	private SessionContext sessionContext;
	private DagExecServiceImpl dagExecServiceImpl;
	

	static Logger logger = Logger.getLogger(RunBatchServiceImpl.class);
	

	/**
	 * @Ganesh
	 *
	 * @return the dagExecServiceImpl
	 */
	public DagExecServiceImpl getDagExecServiceImpl() {
		return dagExecServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param dagExecServiceImpl the dagExecServiceImpl to set
	 */
	public void setDagExecServiceImpl(DagExecServiceImpl dagExecServiceImpl) {
		this.dagExecServiceImpl = dagExecServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the sessionContext
	 */
	public SessionContext getSessionContext() {
		return sessionContext;
	}

	/**
	 * @Ganesh
	 *
	 * @param sessionContext the sessionContext to set
	 */
	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	/**
	 * @Ganesh
	 *
	 * @return the commonServiceImpl
	 */
	public CommonServiceImpl<?> getCommonServiceImpl() {
		return commonServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param commonServiceImpl the commonServiceImpl to set
	 */
	public void setCommonServiceImpl(CommonServiceImpl<?> commonServiceImpl) {
		this.commonServiceImpl = commonServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the dagServiceImpl
	 */
	public DagServiceImpl getDagServiceImpl() {
		return dagServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param dagServiceImpl the dagServiceImpl to set
	 */
	public void setDagServiceImpl(DagServiceImpl dagServiceImpl) {
		this.dagServiceImpl = dagServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the batchServiceImpl
	 */
	public BatchServiceImpl getBatchServiceImpl() {
		return batchServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param batchServiceImpl the batchServiceImpl to set
	 */
	public void setBatchServiceImpl(BatchServiceImpl batchServiceImpl) {
		this.batchServiceImpl = batchServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the batchUuid
	 */
	public String getBatchUuid() {
		return batchUuid;
	}

	/**
	 * @Ganesh
	 *
	 * @param batchUuid the batchUuid to set
	 */
	public void setBatchUuid(String batchUuid) {
		this.batchUuid = batchUuid;
	}

	/**
	 * @Ganesh
	 *
	 * @return the batchVersion
	 */
	public String getBatchVersion() {
		return batchVersion;
	}

	/**
	 * @Ganesh
	 *
	 * @param batchVersion the batchVersion to set
	 */
	public void setBatchVersion(String batchVersion) {
		this.batchVersion = batchVersion;
	}

	/**
	 * @Ganesh
	 *
	 * @return the execParams
	 */
	public ExecParams getExecParams() {
		return execParams;
	}

	/**
	 * @Ganesh
	 *
	 * @param execParams the execParams to set
	 */
	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}

	/**
	 * @Ganesh
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @Ganesh
	 *
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @Ganesh
	 *
	 * @return the runMode
	 */
	public RunMode getRunMode() {
		return runMode;
	}

	/**
	 * @Ganesh
	 *
	 * @param runMode the runMode to set
	 */
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

	/**
	 * @Ganesh
	 *
	 * @return the batchExec
	 */
	public BatchExec getBatchExec() {
		return batchExec;
	}

	/**
	 * @Ganesh
	 *
	 * @param batchExec the batchExec to set
	 */
	public void setBatchExec(BatchExec batchExec) {
		this.batchExec = batchExec;
	}

	@Override
	public String call() throws Exception {
		try {
			FrameworkThreadLocal.getSessionContext().set(sessionContext);
			batchExec = execute();
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
		return null;
	}

	public BatchExec execute() throws Exception {
		Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchUuid, batchVersion, MetaType.batch.toString(), "N");
		try {
			List<MetaIdentifierHolder> execList = new ArrayList<>();
			synchronized (batchExec.getUuid()) {
				for(MetaIdentifierHolder metaMI : batch.getPipelineInfo()) {
					switch(metaMI.getRef().getType()) {
						case dag : execList.add(dagServiceImpl.submitDag(metaMI.getRef().getUuid(), metaMI.getRef().getVersion(), execParams, type, runMode));
							break;
					default:
						break;
					}				
				}
			}
			
			batchExec.setExecList(execList);
			commonServiceImpl.save(MetaType.batchExec.toString(), batchExec);
			Thread.sleep(5000);
			batchExec = batchServiceImpl.checkBatchStatus(batchExec);
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.batchExec, batchExec.getUuid(), batchExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Batch execution FAILED.", dependsOn);
			throw new Exception((message != null) ? message : "Batch execution FAILED.");
		} finally {
			if(Helper.getPropertyValue("framework.email.enable").equalsIgnoreCase("Y")) {
				SenderInfo senderInfo = batch.getSenderInfo();
				if(senderInfo != null) {
					Status latestStatus = Helper.getLatestStatus(batchExec.getStatusList());
					if(latestStatus.getStage().equals(Status.Stage.COMPLETED) && senderInfo.getEmailTo().size() > 0 && senderInfo.getNotifyOnSuccess().equalsIgnoreCase("Y")) {
						synchronized (batchExec.getUuid()) {
							batchServiceImpl.sendSuccessNotification(senderInfo, batch, batchExec);
						}
					} else if(latestStatus.getStage().equals(Status.Stage.FAILED) && senderInfo.getEmailTo().size() > 0 && senderInfo.getNotifyOnFailure().equalsIgnoreCase("Y")) {
						batchServiceImpl.sendFailureNotification(senderInfo, batch, batchExec);
					}
				}
			}
		}
		
		return batchExec;
	}
	
	public BatchExec restart() throws Exception {		
		return batchServiceImpl.restart(batchExec.getUuid(), batchExec.getVersion(), runMode);
	}
	
	public BatchExec kill() throws Exception {		
		return batchServiceImpl.kill(batchExec.getUuid(), batchExec.getVersion());
	}
	
}

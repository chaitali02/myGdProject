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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.SenderInfo;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.StageExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;

public class RunDagServiceImpl implements Callable<String> {
	
	DagExec dagExec;
	Dag dag;
	DagServiceImpl dagServiceImpl;
	DagExecServiceImpl dagExecServiceImpl;
	BatchExecServiceImpl btchServ;
	ConcurrentHashMap<?, ?> taskThreadMap;
	CommonServiceImpl<?> commonServiceImpl;
	protected SessionContext sessionContext;
	protected RunMode runMode;
	
	static final Logger logger = Logger.getLogger(RunDagServiceImpl.class);
		
	/**
	 * @return the dagExec
	 */
	public DagExec getDagExec() {
		return dagExec;
	}

	/**
	 * @param dagExec the dagExec to set
	 */
	public void setDagExec(DagExec dagExec) {
		this.dagExec = dagExec;
	}

	/**
	 * @return the dag
	 */
	public Dag getDag() {
		return dag;
	}

	/**
	 * @param dag the dag to set
	 */
	public void setDag(Dag dag) {
		this.dag = dag;
	}

	/**
	 * @return the dagServiceImpl
	 */
	public DagServiceImpl getDagServiceImpl() {
		return dagServiceImpl;
	}

	/**
	 * @param dagServiceImpl the dagServiceImpl to set
	 */
	public void setDagServiceImpl(DagServiceImpl dagServiceImpl) {
		this.dagServiceImpl = dagServiceImpl;
	}
	
	/**
	 * @return the dagExecServiceImpl
	 */
	public DagExecServiceImpl getDagExecServiceImpl() {
		return dagExecServiceImpl;
	}

	/**
	 * @param dagExecServiceImpl the dagExecServiceImpl to set
	 */
	public void setDagExecServiceImpl(DagExecServiceImpl dagExecServiceImpl) {
		this.dagExecServiceImpl = dagExecServiceImpl;
	}

	/**
	 * @return the btchServ
	 */
	public BatchExecServiceImpl getBtchServ() {
		return btchServ;
	}

	/**
	 * @param btchServ the btchServ to set
	 */
	public void setBtchServ(BatchExecServiceImpl btchServ) {
		this.btchServ = btchServ;
	}

	/**
	 * @return the taskThreadMap
	 */
	public ConcurrentHashMap<?, ?> getTaskThreadMap() {
		return taskThreadMap;
	}

	/**
	 * @param taskThreadMap the taskThreadMap to set
	 */
	public void setTaskThreadMap(ConcurrentHashMap<?, ?> taskThreadMap) {
		this.taskThreadMap = taskThreadMap;
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

	@Override
	public String call() throws Exception {
		return parseAndExecute();
	}
	
	@SuppressWarnings("finally")
	public String parseAndExecute() throws Exception {
		try {
			logger.info(" Inside RunDagServiceImpl.parseAndExecute ");
			logger.info("Thread watch : DagExec : " + dagExec.getUuid() + " RunDagServiceImpl status RUN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
			logger.info(" sessionContext " + sessionContext.getUserInfo());
			FrameworkThreadLocal.getSessionContext().set(sessionContext);
			logger.info(" After set sessionContext ");
			//Check if parsing has happ or not. If not then parse.
			dagServiceImpl.setRunMode(runMode);
			if (Helper.getLatestStatus(dagExec.getStatusList()).getStage().equals(Status.Stage.PENDING) 
					|| Helper.getLatestStatus(dagExec.getStatusList()).getStage().equals(Status.Stage.STARTING)) {
				// Parse to create SQL
				logger.info(" Before parse ");
				synchronized (dagExec.getUuid()) {
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.STARTING);
				}
				dagExec = dagServiceImpl.parseDagExec(dag, dagExec);
				logger.info(" After parse ");
				//dagExecServiceImpl.save(dagExec);
				if (Helper.getLatestStatus(dagExec.getStatusList()).getStage() != Status.Stage.STARTING) {
					logger.info("DagExec not in STARTING stage after parse. Aborting Dag processing ... ");
					return "Dag_"+dagExec.getUuid();
				}
				synchronized (dagExec.getUuid()) {
					commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.READY);
				}
			}
			synchronized (dagExec.getUuid()) {
				commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.RUNNING);
			}

			// Execute the object
			logger.info(" Before  createDagExecBatch");
			dagExec = btchServ.createDagExecBatch(dag, dagExec, runMode);
			
			
			//dagExec = dagExecServiceImpl.findOneByUuidAndVersion(dagExec.getUuid(), dagExec.getVersion());
			dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(dagExec.getUuid(), dagExec.getVersion(), MetaType.dagExec.toString(), "N");
			List<StageExec> dagExecStgs = DagExecUtil.castToStageExecList(dagExec.getStages());
			boolean setCOMPLETEDStatus = true;
			
			
			for (StageExec stageExec : dagExecStgs) {
				Status latestStatus = Helper.getLatestStatus(stageExec.getStatusList());
				logger.info("After dag exec latestStatus : " + latestStatus.getStage().toString() + " for stage exec : " + stageExec.getStageId());
				if (latestStatus.getStage().equals(Status.Stage.FAILED)) {
					synchronized (dagExec.getUuid()) {
						commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.FAILED);
					}
					setCOMPLETEDStatus = false;
					break;
				} else if (latestStatus.getStage().equals(Status.Stage.KILLED)) {
					synchronized (dagExec.getUuid()) {
						commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.TERMINATING);
						commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.KILLED);
					}
					setCOMPLETEDStatus = false;
					break;
				} else if (latestStatus.getStage().equals(Status.Stage.ABORTED)) {
					synchronized (dagExec.getUuid()) {
						commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.ABORTED);
					}
					setCOMPLETEDStatus = false;
					break;
				} else if (!latestStatus.getStage().equals(Status.Stage.COMPLETED)) {
					setCOMPLETEDStatus = false;
					break;
				}
			}
			
			if (setCOMPLETEDStatus) {
				logger.info("DagExec COMPLETED");
				synchronized (dagExec.getUuid()) {
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.COMPLETED);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dagExec, dagExec.getUuid(), dagExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Pipeline execution FAILED.", dependsOn);
			throw new Exception((message != null) ? message : "Pipeline execution FAILED.");
		} finally {			
			taskThreadMap.remove("Dag_"+dagExec.getUuid());
			logger.info("Thread watch : DagExec : " + dagExec.getUuid() + " RunDagServiceImpl complete >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
			
			if(Helper.getPropertyValue("framework.email.enable").equalsIgnoreCase("Y")) {
				SenderInfo senderInfo = dag.getSenderInfo();
				if(senderInfo != null) {	
					Status latestStatus = Helper.getLatestStatus(dagExec.getStatusList());
					if(latestStatus.getStage().equals(Status.Stage.COMPLETED) && senderInfo.getEmailTo().size() > 0 && senderInfo.getNotifyOnSuccess().equalsIgnoreCase("Y")) {
						synchronized(dagExec.getUuid()) {
							dagServiceImpl.sendSuccessNotification(senderInfo, dag, dagExec);
						}
					} else if(latestStatus.getStage().equals(Status.Stage.FAILED) && senderInfo.getEmailTo().size() > 0 && senderInfo.getNotifyOnFailure().equalsIgnoreCase("Y")) {
						dagServiceImpl.sendFailureNotification(senderInfo, dag, dagExec);
					}
				}	
			}
			
			return "Dag_"+dagExec.getUuid();
		}
	}

}

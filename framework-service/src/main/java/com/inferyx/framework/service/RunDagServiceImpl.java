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
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.StageExec;
import com.inferyx.framework.domain.Status;

public class RunDagServiceImpl implements Callable<String> {
	
	DagExec dagExec;
	Dag dag;
	DagServiceImpl dagServiceImpl;
	DagExecServiceImpl dagExecServiceImpl;
	BatchExecServiceImpl btchServ;
	ConcurrentHashMap taskThreadMap;
	CommonServiceImpl<?> commonServiceImpl;
	protected SessionContext sessionContext;
	protected Mode runMode;
	
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
	public Mode getRunMode() {
		return runMode;
	}

	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(Mode runMode) {
		this.runMode = runMode;
	}

	@Override
	public String call() throws Exception {
		return parseAndExecute();
	}
	
	@SuppressWarnings("finally")
	public String parseAndExecute() throws Exception {
		try {

			FrameworkThreadLocal.getSessionContext().set(sessionContext);
			//Check if parsing has happ or not. If not then parse.
			dagServiceImpl.setRunMode(runMode);
			if (Helper.getLatestStatus(dagExec.getStatusList()).getStage().equals(Status.Stage.NotStarted)) {
				// Parse to create SQL
				dagExec = dagServiceImpl.parseDagExec(dag, dagExec);
				//dagExecServiceImpl.save(dagExec);
				commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
			}
			
			synchronized (dagExec.getUuid()) {
				commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.InProgress);
			}

			// Execute the object
			dagExec = btchServ.createDagExecBatch(dag, dagExec, runMode);
			
			
			//dagExec = dagExecServiceImpl.findOneByUuidAndVersion(dagExec.getUuid(), dagExec.getVersion());
			dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(dagExec.getUuid(), dagExec.getVersion(), MetaType.dagExec.toString());
			List<StageExec> dagExecStgs = DagExecUtil.castToStageExecList(dagExec.getStages());
			boolean setCompletedStatus = true;
			
			
			for (StageExec stageExec : dagExecStgs) {
				Status latestStatus = Helper.getLatestStatus(stageExec.getStatusList());
				logger.info("After dag exec latestStatus : " + latestStatus.getStage().toString() + " for stage exec : " + stageExec.getStageId());
				if (latestStatus.getStage().equals(Status.Stage.Failed)) {
					synchronized (dagExec.getUuid()) {
						commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.Failed);
					}
					setCompletedStatus = false;
					break;
				} else if (latestStatus.getStage().equals(Status.Stage.Killed)) {
					synchronized (dagExec.getUuid()) {
						commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.Terminating);
						commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.Killed);
					}
					setCompletedStatus = false;
					break;
				} else if (!latestStatus.getStage().equals(Status.Stage.Completed)) {
					setCompletedStatus = false;
					break;
				}
			}
			
			if (setCompletedStatus) {
				logger.info("DagExec completed");
				synchronized (dagExec.getUuid()) {
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.Completed);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Pipeline execution failed.");
			throw new Exception((message != null) ? message : "Pipeline execution failed.");
		}finally {			
			taskThreadMap.remove("Dag_"+dagExec.getUuid());
			return "Dag_"+dagExec.getUuid();
		}
	}

}

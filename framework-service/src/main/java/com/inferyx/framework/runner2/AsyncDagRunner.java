/**
 * 
 */
package com.inferyx.framework.runner2;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.StageExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.BatchExecServiceImpl;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DagExecServiceImpl;
import com.inferyx.framework.service.MessageStatus;
import com.inferyx.framework.service2.DagServiceImpl2;

/**
 * @author joy
 *
 */
@Service
public class AsyncDagRunner {
	
//	DagExec dagExec;
//	Dag dag;
	@Autowired
	DagServiceImpl2 dagServiceImpl2;
	@Autowired
	DagExecServiceImpl dagExecServiceImpl;
	@Autowired
	BatchExecServiceImpl btchServ;
	@Resource(name="taskThreadMap")
	ConcurrentHashMap<String, FutureTask<String>> taskThreadMap;
	@Autowired
	CommonServiceImpl commonServiceImpl;
	
//	protected SessionContext sessionContext;
//	protected RunMode runMode;
	
	static final Logger logger = Logger.getLogger(AsyncDagRunner.class);

	/**
	 * 
	 */
	public AsyncDagRunner() {
		// TODO Auto-generated constructor stub
	}
	
	@Async("dagExecutor")
	public String parseAndExecute(Dag dag, DagExec dagExec, SessionContext sessionContext, RunMode runMode) throws Exception {
		try {
			logger.info(" Inside AsyncDagRunner.parseAndExecute ");
			logger.info("Thread watch : DagExec : " + dagExec.getUuid() + " RunDagServiceImpl status RUN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
			FrameworkThreadLocal.getSessionContext().set(sessionContext);
			//Check if parsing has happ or not. If not then parse.
//			dagServiceImpl2.setRunMode(runMode);	// MARK IT - DONT SET RUNMODE HERE
			if (Helper.getLatestStatus(dagExec.getStatusList()).getStage().equals(Status.Stage.PENDING)) {
				// Parse to create SQL
				dagExec = dagServiceImpl2.parseDagExec(dag, dagExec);
				//dagExecServiceImpl.save(dagExec);
				commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
			}
			
			synchronized (dagExec.getUuid()) {
				commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.RUNNING);
			}

			// Execute the object
			logger.info("Before Executing dag >>>>>>>>>>>>>>>>> " + dag.getName() + ": Exec :" + dagExec.getUuid());
			dagExec = btchServ.createDagExecBatch(dag, dagExec, runMode);
			logger.info("After Executing dag >>>>>>>>>>>>>>>>> " + dag.getName() + ": Exec :" + dagExec.getUuid());
			
			
			//dagExec = dagExecServiceImpl.findOneByUuidAndVersion(dagExec.getUuid(), dagExec.getVersion());
			dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(dagExec.getUuid(), dagExec.getVersion(), MetaType.dagExec.toString());
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
			}catch (Exception e2) {
				// TODO: handle exception
			}
			
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dagExec, dagExec.getUuid(), dagExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Pipeline execution FAILED.", dependsOn);
			throw new Exception((message != null) ? message : "Pipeline execution FAILED.");
		}finally {			
			taskThreadMap.remove("Dag_"+dagExec.getUuid());
			logger.info("Thread watch : DagExec : " + dagExec.getUuid() + " RunDagServiceImpl complete >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
			return "Dag_"+dagExec.getUuid();
		}
	}
	
}

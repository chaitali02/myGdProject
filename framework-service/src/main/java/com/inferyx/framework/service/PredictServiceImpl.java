/**
 * 
 */
package com.inferyx.framework.service;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;

/**
 * @author joy
 *
 */
public class PredictServiceImpl {
	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Resource(name="taskThreadMap")
	protected ConcurrentHashMap<?, ?> taskThreadMap;
	
	static final Logger logger = Logger.getLogger(PredictServiceImpl.class);

	/**
	 * 
	 */
	public PredictServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Sends kill message to kill an ingest rule. 
	 * First, it sets the status to terminating in case the status is In Progress.
	 * Then, it tries to kill the thread. 
	 * Then, it sets the status to Killed if the latest status is Terminating, whether or not it was able to kill the thread.
	 * Even if it was not able to kill a thread (because the thread completed or cancelled in between), if the status was Terminating, the status shall change to Killed. 
	 * @param uuid
	 * @param version
	 * @param execType
	 */
	public void kill (String uuid, String version, MetaType execType) {
		BaseExec baseExec = null;
		try {
			baseExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, execType.toString(), "N");
		} catch (JsonProcessingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if (baseExec == null) {
			logger.info("IngestExec not found. Exiting...");
			return;
		}
		if (!Helper.getLatestStatus(baseExec.getStatusList()).equals(new Status(Status.Stage.InProgress, new Date()))) {
			logger.info("Latest Status is not in InProgress. Exiting...");
		}
		try {
			logger.info("Before kill - predict - " + baseExec.getUuid());
			synchronized (baseExec.getUuid()) {
				baseExec = (BaseExec) commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.Terminating);
				if (!Helper.getLatestStatus(baseExec.getStatusList()).equals(new Status(Status.Stage.Terminating, new Date()))) {
					logger.info("Latest Status is not in Terminating. Exiting...");
					return;
				}
			}
			@SuppressWarnings("unchecked")
			FutureTask<TaskHolder> futureTask = (FutureTask<TaskHolder>) taskThreadMap.get(execType+"_"+baseExec.getUuid()+"_"+baseExec.getVersion());
				futureTask.cancel(true);
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.Killed);
			}
		} catch (Exception e) {
			logger.info("Failed to kill. uuid : " + uuid + " version : " + version);
			try {
				synchronized (baseExec.getUuid()) {
					commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.Killed);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			taskThreadMap.remove(execType+"_"+baseExec.getUuid()+"_"+baseExec.getVersion());
			e.printStackTrace();
		}
	}
	
	/**
	 * Set status of BaseExec to Resume if status is OnHold
	 * @param uuid
	 * @param version
	 * @param execType
	 */
	public void resume (String uuid, String version, MetaType execType) {
		BaseExec baseExec = null;
		try {
			baseExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, execType.toString());
		} catch (JsonProcessingException e2) {
			e2.printStackTrace();
		}
		if (baseExec == null) {
			logger.info("BaseExec not found. Exiting...");
			return;
		}
		// Pre conditions for Resume shall be determined by the setMetaStatus
		try {
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.Resume);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set status of BaseExec to OnHold if status is Killed, notStarted, or Resume 
	 * @param uuid
	 * @param version
	 * @param execType
	 */
	public void onHold (String uuid, String version, MetaType execType) {
		BaseExec baseExec = null;
		try {
			baseExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, execType.toString());
		} catch (JsonProcessingException e2) {
			e2.printStackTrace();
		}
		if (baseExec == null) {
			logger.info("BaseExec not found. Exiting...");
			return;
		}
		// Pre conditions for OnHold shall be determined by the setMetaStatus
		try {
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.OnHold);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

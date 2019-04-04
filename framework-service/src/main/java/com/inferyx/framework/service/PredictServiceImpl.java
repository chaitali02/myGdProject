/**
 * 
 */
package com.inferyx.framework.service;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.inferyx.framework.domain.MetaType;

/**
 * @author joy
 *
 */
public class PredictServiceImpl {
	
	
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
	 * First, it sets the status to TERMINATING in case the status is RUNNING.
	 * Then, it tries to kill the thread. 
	 * Then, it sets the status to KILLED if the latest status is TERMINATING, whether or not it was able to kill the thread.
	 * Even if it was not able to kill a thread (because the thread COMPLETED or cancelled in between), if the status was TERMINATING, the status shall change to KILLED. 
	 * @param uuid
	 * @param version
	 * @param execType
	 */
	
	
	/**************************Unused****************************/
	public void kill (String uuid, String version, MetaType execType) {
	/*	BaseExec baseExec = null;
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
		if (!Helper.getLatestStatus(baseExec.getStatusList()).equals(new Status(Status.Stage.RUNNING, new Date()))) {
			logger.info("Latest Status is not in RUNNING. Exiting...");
		}
		try {
			logger.info("Before kill - predict - " + baseExec.getUuid());
			synchronized (baseExec.getUuid()) {
				baseExec = (BaseExec) commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.TERMINATING);
				if (!Helper.getLatestStatus(baseExec.getStatusList()).equals(new Status(Status.Stage.TERMINATING, new Date()))) {
					logger.info("Latest Status is not in TERMINATING. Exiting...");
					return;
				}
			}
			@SuppressWarnings("unchecked")
			FutureTask<TaskHolder> futureTask = (FutureTask<TaskHolder>) taskThreadMap.get(execType+"_"+baseExec.getUuid()+"_"+baseExec.getVersion());
				futureTask.cancel(true);
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.KILLED);
			}
		} catch (Exception e) {
			logger.info("FAILED to kill. uuid : " + uuid + " version : " + version);
			try {
				synchronized (baseExec.getUuid()) {
					commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.KILLED);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			taskThreadMap.remove(execType+"_"+baseExec.getUuid()+"_"+baseExec.getVersion());
			e.printStackTrace();
		}
	}
	*/
	/**
	 * Set status of BaseExec to RESUME if status is PAUSE
	 * @param uuid
	 * @param version
	 * @param execType
	 */
		/**************************Unused****************************/
	/*public void RESUME (String uuid, String version, MetaType execType) {
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
		// Pre conditions for RESUME shall be determined by the setMetaStatus
		try {
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.RESUME);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
	/**
	 * Set status of BaseExec to PAUSE if status is KILLED, PENDING, or RESUME 
	 * @param uuid
	 * @param version
	 * @param execType
	 */
	/**************************Unused****************************/
		/*public void PAUSE (String uuid, String version, MetaType execType) {
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
		// Pre conditions for PAUSE shall be determined by the setMetaStatus
		try {
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.PAUSE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/


  }
}

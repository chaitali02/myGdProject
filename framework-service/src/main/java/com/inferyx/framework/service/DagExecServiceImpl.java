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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IDagDao;
import com.inferyx.framework.dao.IDagExecDao;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DagStatusHolder;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Stage;
import com.inferyx.framework.domain.StageExec;
import com.inferyx.framework.domain.StageStatusHolder;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Task;
import com.inferyx.framework.domain.TaskExec;
import com.inferyx.framework.domain.TaskStatusHolder;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.register.GraphRegister;

@Service
public class DagExecServiceImpl {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IDagExecDao iDagExec;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	private BatchExecServiceImpl btchServ;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	ExecutorFactory execFactory;
	@Autowired
	DagServiceImpl dagServiceImpl;
	@Autowired
	DataQualGroupServiceImpl dataQualGroupServiceImpl;
	@Autowired
	LoadExecServiceImpl loadExecServiceImpl;
	@Autowired
	MapExecServiceImpl mapExecServiceImpl;
	@Autowired
	DataQualServiceImpl dataQualServiceImpl;
	@Autowired
	DataQualExecServiceImpl dataQualExecServiceImpl;
	@Autowired
	DataQualGroupExecServiceImpl dataQualGroupExecServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	IDagDao iDagDao;
	@Resource(name="taskThreadMap")
	ConcurrentHashMap<?, ?> taskThreadMap;
	@Autowired
	SessionContext sessionContext;
 
	ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

	MetaIdentifierHolder location = new MetaIdentifierHolder();
	static final Logger logger = Logger.getLogger(DagExecServiceImpl.class);
	
	public List<DagExec> findLatestDagExec(String dagUUID, String dagVersion) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDagExec.findLatestDagExec(appUuid, dagUUID, dagVersion);
	}

	public List<DagExec> findDagExecByDatapod(String datapodUUID) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDagExec.findOneByDatapod(appUuid, datapodUUID);
	}

	public List<DagExec> findDagExecByDag(String dagUUID) throws JsonProcessingException {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		List<DagExec> dagexec = iDagExec.findAllByDag(appUuid, dagUUID);
		List<DagExec> DagExec = new ArrayList<DagExec>();
		for (DagExec Dagexec : dagexec) {
			if (Dagexec.getCreatedBy() != null) {
				String createdByRefUuid = Dagexec.getCreatedBy().getRef().getUuid();
				User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
				Dagexec.getCreatedBy().getRef().setName(user.getName());
			}
			DagExec.add(Dagexec);
		}
		return DagExec;
	}
	
	public String kill(String uuid, String version, String stageId, String taskId) throws JsonProcessingException {
		String returnStr = null;
		if (StringUtils.isBlank(stageId)) {
			return btchServ.killDag(uuid, version);
		}
		if (StringUtils.isBlank(taskId)) {
			returnStr = btchServ.killStage(uuid, version, stageId);
			synchronized (uuid) {
				checkStageAndKillDag(uuid, version);
			}
			return returnStr;
		}
		returnStr = btchServ.killTask(uuid, version, stageId, taskId);
		synchronized (uuid) {
			checkTaskAndKillStage(uuid, version, stageId);
			checkStageAndKillDag(uuid, version);
		}
		return returnStr;
	}
	
	public void checkTaskAndKillStage(String uuid, String version, String stageId) {
		DagExec dagExec = null;
		Status taskStatus = null;
		boolean isTaskKILLED = false;
		boolean isTaskFAILED = false;
		boolean isTaskCOMPLETED = false;
		boolean isTaskNotCOMPLETED = true;
		StageExec stageExec = null;
		try {
			while (isTaskNotCOMPLETED) {
				//dagExec = findOneByUuidAndVersion(uuid, version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
				Thread.sleep(5000);
				// Fetch stages
				stageExec = getStageExec(dagExec, stageId);
				if (stageExec == null) {
					logger.info("StageExec does not exist. Aborting ... ");
					return;
				}
				// fetch tasks
				List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
				if (taskExecList == null || taskExecList.isEmpty()) {
					logger.info("No tasks in stage. Aborting ... ");
					return;
				}
				isTaskNotCOMPLETED = false;
				for (TaskExec taskExec : taskExecList) {
					// Check task status
					taskStatus = Helper.getLatestStatus(taskExec.getStatusList());
					if (taskStatus == null) {
						logger.info("No status in task. Continuing with next ... ");
					}
					if (taskStatus.getStage().equals(Status.Stage.KILLED)) {
						isTaskKILLED = true;
					} else if (taskStatus.getStage().equals(Status.Stage.FAILED)) {
						isTaskFAILED = true;
					} else if (taskStatus.getStage().equals(Status.Stage.COMPLETED)) {
						isTaskCOMPLETED = true;
					} else if (!taskStatus.getStage().equals(Status.Stage.PENDING)) {
						isTaskNotCOMPLETED = true;
					}
				}
			}
			if (isTaskKILLED) {
//				synchronized (uuid) {
					commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.TERMINATING, stageExec.getStageId());
					commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.KILLED, stageExec.getStageId());
	//			}
			} else if (isTaskFAILED) {
		//		synchronized (uuid) {
					commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.FAILED, stageExec.getStageId());
			//	}
			} else if (isTaskCOMPLETED) {
				//synchronized (uuid) {
					commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.COMPLETED, stageExec.getStageId());
				//}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void checkStageAndKillDag(String uuid, String version) {
		DagExec dagExec = null;
		Status stageStatus = null;
		boolean isStageKILLED = false;
		boolean isStageFAILED = false;
		boolean isStageCOMPLETED = false;
		boolean isStageNotCOMPLETED = true;
		try {
			while (isStageNotCOMPLETED) {
				//dagExec = findOneByUuidAndVersion(uuid, version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
				Thread.sleep(10000);
				// Fetch stages
				List<StageExec> dagExecStgs = DagExecUtil.castToStageExecList(dagExec.getStages());
				if (dagExecStgs == null || dagExecStgs.isEmpty()) {
					logger.info("No stages in Dag. Aborting ... ");
					return;
				}
				isStageNotCOMPLETED = false;
				for (StageExec stageExec : dagExecStgs) {
					// Check stage status
					stageStatus = Helper.getLatestStatus(stageExec.getStatusList());
					if (stageStatus == null) {
						logger.info("No status in stage. Continuing with next ... ");
					}
					if (stageStatus.getStage().equals(Status.Stage.KILLED)) {
						isStageKILLED = true;
					} else if (stageStatus.getStage().equals(Status.Stage.FAILED)) {
						isStageFAILED = true;
					} else if (stageStatus.getStage().equals(Status.Stage.COMPLETED)) {
						isStageCOMPLETED = true;
					}  else if (!stageStatus.getStage().equals(Status.Stage.PENDING)){
						isStageNotCOMPLETED = true;
					}
				}
			}
		
			if (isStageKILLED) {
			//	synchronized (uuid) {
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.TERMINATING);
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.KILLED);
			//	}
			} else if (isStageFAILED) {
			//	synchronized (uuid) {
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.FAILED);
			//	}
			} else if (isStageCOMPLETED) {
			//	synchronized (uuid) {
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.COMPLETED);
			//	}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public BaseEntity setTaskPause(String uuid, String version, String stageId, String taskId) throws JsonProcessingException, JSONException, ParseException {

		DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
		for(int i=0;i<dagExec.getStages().size();i++){
			if(dagExec.getStages().get(i).getStageId().equals(stageId)){
				for(int j=0;j<dagExec.getStages().get(i).getTasks().size();j++){
					if(dagExec.getStages().get(i).getTasks().get(j).getTaskId().equals(taskId)){
						Status PAUSEStatus = new Status(Status.Stage.PAUSE, new Date());
						List<Status> statusList =dagExec.getStages().get(i).getTasks().get(j).getStatusList();
						statusList.remove(PAUSEStatus);
						statusList.add(PAUSEStatus);
						dagExec.getStages().get(i).getTasks().get(j).setStatusList(statusList);
					}
				}
			}
		}
		return (BaseEntity) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
	}
	public BaseEntity setTaskResume(String uuid, String version, String stageId, String taskId) throws JsonProcessingException, JSONException, ParseException {

		DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
		for(int i=0;i<dagExec.getStages().size();i++){
			if(dagExec.getStages().get(i).getStageId().equals(stageId)){
				int lastStatus = dagExec.getStages().get(i).getStatusList().size() - 1;
				if(dagExec.getStages().get(i).getStatusList().get(lastStatus).getStage().equals(Status.Stage.PAUSE)) {
					logger.info("Stage is PAUSE, can-not start the task.");
				}else {
					for(int j=0;j<dagExec.getStages().get(i).getTasks().size();j++){
						if(dagExec.getStages().get(i).getTasks().get(j).getTaskId().equals(taskId)){
							Status onRESUMEStatus = new Status(Status.Stage.RESUME, new Date());
							List<Status> statusList =dagExec.getStages().get(i).getTasks().get(j).getStatusList();
							statusList.remove(onRESUMEStatus);
							statusList.add(onRESUMEStatus);
							dagExec.getStages().get(i).getTasks().get(j).setStatusList(statusList);
						}
					}
				}
				
			}
		}
		return (BaseEntity) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
	}

	public List<String> fetchAllTaskThread() {
		return btchServ.fetchAllTaskThread();
	}

	
	public void setStageKILLED(String uuid, String version, String stageId) throws JsonProcessingException, JSONException, ParseException {
		//DagExec dagexec  = iDagExec.findOneByUuidAndVersion(appUuid, uuid, version);
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());

		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());
		Date date = new Date();

		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {
				List<Status> stageStatusList = stageExec.getStatusList();
				if (!Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.RUNNING)) {
					break;
				}
				com.inferyx.framework.domain.Status status = new Status(
						com.inferyx.framework.domain.Status.Stage.TERMINATING, date);
				
				for (TaskExec taskExec : DagExecUtil.castToTaskExecList(stageExec.getTasks())) {
					setTaskKILLED(uuid, version, stageId, taskExec.getTaskId());
				}
				if (!Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.COMPLETED)) {
					status = new Status(
							com.inferyx.framework.domain.Status.Stage.KILLED, date);
					stageStatusList.add(status);
					stageExec.setStatusList(stageStatusList);
				}
			}
		}
	}
	
	public void setTaskKILLED(String Uuid, String version, String stageId, String taskId) throws JsonProcessingException, JSONException, ParseException {
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(Uuid, version, MetaType.dagExec.toString());

		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());
		Date date = new Date();
		List<Status> statusList = dagexec.getStatusList();

		boolean isTaskKILLED = false;
		
		if (!Helper.getLatestStatus(statusList).getStage().equals(Status.Stage.RUNNING)) {
			return;
		}
		
		logger.info("Before adding terminated status to DAG list");
		statusList.add(new Status(
									com.inferyx.framework.domain.Status.Stage.TERMINATING, date));

		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {
				List<Status> stageStatusList = stageExec.getStatusList();
				//Collections.sort(stageStatusList);
				if (!Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.RUNNING)) {
					break;
				}
				com.inferyx.framework.domain.Status status = new Status(
						com.inferyx.framework.domain.Status.Stage.TERMINATING, date);
				stageStatusList.add(status);
				List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
				for (TaskExec taskExec : taskExecList) {
					if (taskExec.getTaskId().equals(taskId)) {
						List<Status> taskStatusList = taskExec.getStatusList();
						//Collections.sort(taskStatusList);
						if (Helper.getLatestStatus(taskStatusList).equals(new Status(Status.Stage.RUNNING, new Date()))) {
							isTaskKILLED = true;
							status = new Status(
									com.inferyx.framework.domain.Status.Stage.TERMINATING, date);
							taskStatusList.add(status);
							logger.info("Starting to kill operator");
							for(MetaIdentifierHolder operatorInfo : taskExec.getOperators().get(0).getOperatorInfo()) {
								commonServiceImpl.kill(operatorInfo.getRef().getType()
														, operatorInfo.getRef().getUuid()
														, operatorInfo.getRef().getVersion());
							}
							logger.info("After operator is KILLED");
							status = new Status(
									com.inferyx.framework.domain.Status.Stage.KILLED, new Date());
							taskStatusList.add(status);
							taskExec.setStatusList(taskStatusList);
						}
					}
				}
				stageExec.setTasks(DagExecUtil.convertToTaskList(taskExecList));
				if (!Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.COMPLETED)) {
					status = new Status(
							com.inferyx.framework.domain.Status.Stage.KILLED, date);
					stageStatusList.add(status);
					stageExec.setStatusList(stageStatusList);
				}
				
			}
		}
		dagexec.setStages(DagExecUtil.convertToStageList(stageExecList));
		if (isTaskKILLED) {
			//Collections.sort(statusList);
			if (!Helper.getLatestStatus(statusList).getStage().equals(Status.Stage.COMPLETED)) {
				com.inferyx.framework.domain.Status status = new Status(
						com.inferyx.framework.domain.Status.Stage.KILLED, date);
				statusList.add(status);
				dagexec.setStatusList(statusList);
			}
		}
		//iDagExec.save(dagexec);
		commonServiceImpl.save(MetaType.dagExec.toString(), dagexec);
	}

	public TaskExec getTaskExec(String Uuid, String version, String stageId, String taskId) throws JsonProcessingException {
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(Uuid, version, MetaType.dagExec.toString());

		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());

		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {
				List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
				for (TaskExec taskExec : taskExecList) {
					if (taskExec.getTaskId().equals(taskId)) {
						return taskExec;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param Uuid
	 * @param version
	 * @return
	 * @throws JsonProcessingException
	 */
	public DagExec getDagExec(String Uuid, String version) throws JsonProcessingException {
		return (DagExec) commonServiceImpl.getOneByUuidAndVersion(Uuid, version, MetaType.dagExec.toString());
	}
	
	/**
	 * 
	 * @param Uuid
	 * @param version
	 * @param stageId
	 * @return
	 * @throws JsonProcessingException
	 */
	public StageExec getStageExec(String Uuid, String version, String stageId) throws JsonProcessingException {
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(Uuid, version, MetaType.dagExec.toString());

		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());

		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {
				return stageExec;
			}
		}
		return null;
	}
	
	
	public TaskExec getTaskExec(DagExec dagexec, String stageId, String taskId) {
		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());
		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {
				List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
				for (TaskExec taskExec : taskExecList) {
					if (taskExec.getTaskId().equals(taskId)) {
						return taskExec;
					}
				}
			}
		}
		return null;
	}

	public StageExec getStageExec(DagExec dagexec, String stageId) {
		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());
		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {
				return stageExec;
			}
		}
		return null;
	}
	
	public void setStageExec(DagExec dagexec, StageExec stageExec) {
		List<Stage> stageList = dagexec.getStages();
		for (int i = 0; i < stageList.size(); i++) {
			Stage stage = stageList.get(i);
			if (stageExec.getStageId().equals(stage.getStageId())) {
				stageList.remove(i);
				stageList.add(i, stageExec);
				return;
			}
		}
		return;
	}


	public void setTaskExec(DagExec dagexec, TaskExec taskExec) {
		List<Stage> stageList = dagexec.getStages();
		for (int i = 0; i < stageList.size(); i++) {
			Stage stage = stageList.get(i);
			List<Task> taskList = stage.getTasks();
			for (int j = 0; j < taskList.size(); j++) {
				Task task = taskList.get(j);
				if (taskExec.getTaskId().equals(task.getTaskId())) {
					taskList.remove(j);
					taskList.add(j, taskExec);
					return;
				}
			}
		}
		return;
	}

	
	public void setTaskRUNNING(String Uuid, String version, String stageId, String taskId) throws JsonProcessingException, JSONException, ParseException {
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(Uuid, version, MetaType.dagExec.toString());

		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());
		Date date = new Date();
		List<Status> statusList = dagexec.getStatusList();

		// Collections.sort(statusList);
		if (statusList.get(0).getStage().equals(Status.Stage.PENDING) && 
				!Helper.getLatestStatus(statusList).equals(new Status(
					com.inferyx.framework.domain.Status.Stage.RUNNING, date))) {
			com.inferyx.framework.domain.Status status = new Status(
					com.inferyx.framework.domain.Status.Stage.RUNNING, date);
			statusList.add(status);
			dagexec.setStatusList(statusList);
		}

		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {
				List<Status> stageStatusList = stageExec.getStatusList();
				// Collections.sort(stageStatusList);
				if (stageStatusList.get(0).getStage().equals(Status.Stage.PENDING) && 
						!Helper.getLatestStatus(stageStatusList).equals(new Status(
								com.inferyx.framework.domain.Status.Stage.RUNNING, date))) {
					com.inferyx.framework.domain.Status status = new Status(
							com.inferyx.framework.domain.Status.Stage.RUNNING, date);
					stageStatusList.add(status);
					stageExec.setStatusList(stageStatusList);
				}
				List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
				for (TaskExec taskExec : taskExecList) {
					if (taskExec.getTaskId().equals(taskId)) {
						List<Status> taskStatusList = taskExec.getStatusList();
					//	Collections.sort(taskStatusList);
						if (taskStatusList.get(0).getStage().equals(Status.Stage.PENDING)) {

							com.inferyx.framework.domain.Status status = new Status(
									com.inferyx.framework.domain.Status.Stage.RUNNING, date);
							taskStatusList.add(status);
							taskExec.setStatusList(taskStatusList);
						}
					}
				}
				stageExec.setTasks(DagExecUtil.convertToTaskList(taskExecList));
			}
		}
		dagexec.setStages(DagExecUtil.convertToStageList(stageExecList));
		//iDagExec.save(dagexec);
		commonServiceImpl.save(MetaType.dagExec.toString(), dagexec);
	}

	public void setTaskResult(String Uuid, String version, String stageId, String taskId,
			MetaIdentifierHolder resultRef) throws JsonProcessingException, JSONException, ParseException {
		//DagExec dagexec = iDagExec.findOneByUuidAndVersion(appUuid, Uuid, version);
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(Uuid, version, MetaType.dagExec.toString());
		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());
		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {

				List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
				for (TaskExec taskExec : taskExecList) {
					if (taskExec.getTaskId().equals(taskId)) {
						taskExec.setResult(resultRef);
					}
				}
				stageExec.setTasks(DagExecUtil.convertToTaskList(taskExecList));
			}
		}
		dagexec.setStages(DagExecUtil.convertToStageList(stageExecList));
		//iDagExec.save(dagexec);
		commonServiceImpl.save(MetaType.dagExec.toString(), dagexec);
	}

	public void setTaskComplete(String Uuid, String version, String stageId, String taskId) throws JsonProcessingException, JSONException, ParseException {
		{
			//DagExec dagexec = iDagExec.findOneByUuidAndVersion(appUuid, Uuid, version);
			DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(Uuid, version, MetaType.dagExec.toString());
			boolean checkAllStageStatus = true;
			boolean checkAllTaskStatus = true;
			List<Status> operatorStatusList = null;			
			List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());
			// Loop all stages
			for (StageExec stageExec : stageExecList) {
				if (stageExec.getStageId().equals(stageId)) {
					List<TaskExec> dagTaskExecs = DagExecUtil.castToTaskExecList(stageExec.getTasks());
					// Loop all tasks
					for (TaskExec indvTaskExec : dagTaskExecs) {
						List<Status> taskStatusList = indvTaskExec.getStatusList();
						if (indvTaskExec.getTaskId().equals(taskId)) {
							// Check the latest task status and decide
							// Get operator statusList
							operatorStatusList = new ArrayList<>();
							for(MetaIdentifierHolder operatorInfo : indvTaskExec.getOperators().get(0).getOperatorInfo()) {
								List<Status> tempOperatorStatusList = commonServiceImpl.getAllStatusForExec(operatorInfo.getRef());
								Status operatorStatus = Helper.getLatestStatus(tempOperatorStatusList);
								if(operatorStatus.getStage().equals(Status.Stage.KILLED)) {
									operatorStatusList.addAll(tempOperatorStatusList);
									break;
								} else {
									operatorStatusList.addAll(tempOperatorStatusList);
								}
							}
							Status operatorStatus = Helper.getLatestStatus(operatorStatusList);
							com.inferyx.framework.domain.Status status = null;
							if (operatorStatus.getStage().equals(Status.Stage.KILLED)) {
								status = new Status(com.inferyx.framework.domain.Status.Stage.KILLED, new Date());
							} else {
								status = new Status(com.inferyx.framework.domain.Status.Stage.COMPLETED, new Date());
							}
							/*com.inferyx.framework.domain.Status status = new Status(
									com.inferyx.framework.domain.Status.Stage.COMPLETED, new Date());*/
							taskStatusList.add(status);
							indvTaskExec.setStatusList(taskStatusList);
						}
						boolean istaskCOMPLETED = false;						
						// Loop all tasks status list
						if (Helper.getLatestStatus(taskStatusList).getStage().equals(com.inferyx.framework.domain.Status.Stage.COMPLETED)) {
							istaskCOMPLETED = true;
						} else {
							istaskCOMPLETED = false;
						}
						if (!istaskCOMPLETED) {
							checkAllTaskStatus = false;
						}
						stageExec.setTasks(DagExecUtil.convertToTaskList(dagTaskExecs));
					}
					if (checkAllTaskStatus) {
						List<Status> stageStatusList = stageExec.getStatusList();
						com.inferyx.framework.domain.Status status = new Status(
								com.inferyx.framework.domain.Status.Stage.COMPLETED, new Date());
						stageStatusList.add(status);
						stageExec.setStatusList(stageStatusList);
					} else if (!checkAllTaskStatus) {
						checkAllStageStatus = false;
					}

				} else {
					// Set checkAllStageStatus flag for current stage
					List<Status> stageStatusList2 = stageExec.getStatusList();
					if (!checkStatusCOMPLETED(stageStatusList2)) {
						checkAllStageStatus = false;
					}
				}
				dagexec.setStages(DagExecUtil.convertToStageList(stageExecList));
			}

			// Set top level status
			if (checkAllStageStatus) {
				List<Status> dagExecStatusList = dagexec.getStatusList();
				com.inferyx.framework.domain.Status status = new Status(
						com.inferyx.framework.domain.Status.Stage.COMPLETED, new Date());
				dagExecStatusList.add(status);
				dagexec.setStatusList(dagExecStatusList);
			}
			//iDagExec.save(dagexec);
			commonServiceImpl.save(MetaType.dagExec.toString(), dagexec);
		}
	}
	
	public void setStageComplete (String uuid, String version, String stageId) throws JsonProcessingException, JSONException, ParseException {
		DagExec dagexec = iDagExec.findOneByUuidAndVersion(uuid, version);
		boolean allTasksComplete = true;
		@SuppressWarnings("unused")
		boolean taskFAILED = false;
		@SuppressWarnings("unused")
		boolean taskKILLED = false;
		@SuppressWarnings("unused")
		boolean taskPAUSE = false;
		List<Status> stageStatusList = null;
		List<Status> taskStatusList = null;
		Status taskStatus = null;
		
		StageExec stageExec = getStageExec(dagexec, stageId);
		stageStatusList = stageExec.getStatusList();
		// Traverse all tasks
		
		for (TaskExec taskExec : DagExecUtil.castToTaskExecList(stageExec.getTasks())){
			taskStatusList = taskExec.getStatusList();
			taskStatus = Helper.getLatestStatus(taskStatusList); 
			if (taskStatus.getStage().equals(Status.Stage.FAILED)) {
				setStatus(stageStatusList, new Status (Status.Stage.FAILED, new Date()));
				taskFAILED = true;
				
				allTasksComplete = false;
				break;
			}
			if (taskStatus.getStage().equals(Status.Stage.KILLED)) {
				setStatus(stageStatusList, new Status (Status.Stage.KILLED, new Date()));
				taskKILLED = true;
				allTasksComplete = false;
				break;
			}
			if (taskStatus.getStage().equals(Status.Stage.PAUSE)) {
				setStatus(stageStatusList, new Status (Status.Stage.PAUSE, new Date()));
				taskPAUSE = true;
				allTasksComplete = false;
				break;
			}
		}
		if (allTasksComplete) {
			setStatus(stageStatusList, new Status (Status.Stage.COMPLETED, new Date()));
		}		
		DagExecUtil.replaceInStageList(dagexec.getStages(), stageExec);
		setDagStatus(dagexec);
		//iDagExec.save(dagexec);
		commonServiceImpl.save(MetaType.dagExec.toString(), dagexec);
	}
	
	public void setDagStatus(DagExec dagexec) {		
		boolean allStagesComplete = true;
		boolean stageFAILED = false;
		boolean stageKILLED = false;
		@SuppressWarnings("unused")
		boolean stagePAUSE = false;
		List<Status> stageStatusList = null;
		List<Status> dagStatusList = null;
		Status stageStatus = null;
		
		if (dagexec == null || dagexec.getStages() == null || dagexec.getStages().isEmpty()) {
			return;
		}
		dagStatusList = dagexec.getStatusList();
		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());

		// Loop all stages
		for (StageExec stageExec : stageExecList) {
			stageStatusList = stageExec.getStatusList();
			stageStatus = Helper.getLatestStatus(stageStatusList);
			if (stageStatus.getStage().equals(Status.Stage.FAILED)) {
				setStatus(dagStatusList, new Status (Status.Stage.FAILED, new Date()));
				stageFAILED = true;
				allStagesComplete = false;
				break;
			}
			if (stageStatus.getStage().equals(Status.Stage.KILLED)) {
				setStatus(dagStatusList, new Status (Status.Stage.KILLED, new Date()));
				stageKILLED = true;
				allStagesComplete = false;
				break;
			}
			if (stageStatus.getStage().equals(Status.Stage.PAUSE)) {
				stagePAUSE = true;
				allStagesComplete = false;
				break;
			}
			if (!stageStatus.getStage().equals(Status.Stage.COMPLETED)) {
				allStagesComplete = false;
				break;
			}
		}
		
		if (allStagesComplete) {
			setStatus(dagStatusList, new Status (Status.Stage.COMPLETED, new Date()));
		} else if (stageFAILED) {
			setStatus(dagStatusList, new Status (Status.Stage.FAILED, new Date()));
		} else if (stageKILLED) {
			setStatus(dagStatusList, new Status (Status.Stage.KILLED, new Date()));
		}
	}
	
	public void setStatus (List<Status> statusList, Status status) {
		if (Helper.getLatestStatus(statusList).equals(status)) {
			statusList.remove(statusList.size()-1);
		}
		statusList.add(status);
	}

	public void setTaskStatus(String uuid, String version, String stageId, String taskId, com.inferyx.framework.domain.Status.Stage status ) throws JsonProcessingException, JSONException, ParseException {
		{
			//DagExec dagexec = iDagExec.findOneByUuidAndVersion(uuid, version);
			DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			boolean checkAllStageStatus = true;
			boolean checkAllTaskStatus = true;
			
			List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());

			// Loop all stages
			for (StageExec stageExec : stageExecList) {

				if (stageExec.getStageId().equals(stageId)) {

					List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());

					// Loop all tasks
					for (TaskExec taskExec : taskExecList) {

						List<Status> taskStatusList = taskExec.getStatusList();

						if (taskExec.getTaskId().equals(taskId)) {
							com.inferyx.framework.domain.Status taskStatus = new Status(status, new Date());
							if (Helper.getLatestStatus(taskStatusList).equals(taskStatus)) {
								taskStatusList.remove(taskStatusList.size()-1);
							}
							taskStatusList.add(taskStatus);
							taskExec.setStatusList(taskStatusList);
							logger.info("Task status : " + taskExec.getTaskId() + " : " + taskStatus.getStage().toString());
						}
						boolean istaskCOMPLETED = false;
						boolean istaskFAILED = false;

						// Loop all tasks status list
						for (Status taskStatus : taskStatusList) {
							if (taskStatus.getStage().equals(com.inferyx.framework.domain.Status.Stage.COMPLETED)) {
								istaskCOMPLETED = true;
								break;
							} else {
								istaskCOMPLETED = false;
							}

							if (taskStatus.getStage().equals(com.inferyx.framework.domain.Status.Stage.FAILED)) {
								istaskFAILED = true;
								break;
							} else {
								istaskFAILED = false;
							}
							
						}
						
						if (!istaskCOMPLETED && !istaskFAILED) {
							checkAllTaskStatus = false;
						}
						stageExec.setTasks(DagExecUtil.convertToTaskList(taskExecList));
					}
					if (checkAllTaskStatus) {
						List<Status> stageStatusList = stageExec.getStatusList();
						com.inferyx.framework.domain.Status taskStatus = new Status(status, new Date());
						stageStatusList.add(taskStatus);
						stageExec.setStatusList(stageStatusList);
					} else if (!checkAllTaskStatus) {
						checkAllStageStatus = false;
					}

				} else {
					// Set checkAllStageStatus flag for current stage
					List<Status> stageStatusList = stageExec.getStatusList();
					if (!checkStatusCOMPLETED(stageStatusList) || !checkStatusFAILED(stageStatusList) || !checkStatusPAUSE(stageStatusList)) {
						checkAllStageStatus = false;
					}
				}
				dagexec.setStages(DagExecUtil.convertToStageList(stageExecList));
			}
			// Set top level status
			if (checkAllStageStatus) {
				List<Status> dagExecStatusList = dagexec.getStatusList();
				com.inferyx.framework.domain.Status taskStatus = new Status(status, new Date());
				dagExecStatusList.add(taskStatus);
				dagexec.setStatusList(dagExecStatusList);
			}
			// Save the dagexec
			//iDagExec.save(dagexec);
			commonServiceImpl.save(MetaType.dagExec.toString(), dagexec);
		}
	}	
	
	public boolean checkStatusCOMPLETED(List<Status> statusList) {
		if (Helper.getLatestStatus(statusList).getStage().equals(Status.Stage.COMPLETED)) {
			return true;
		}
		return false;
	}
	
	public boolean checkStatusKILLED(List<Status> statusList) {
		if (Helper.getLatestStatus(statusList).getStage().equals(Status.Stage.KILLED)) {
			return true;
		}
		return false;
	}

	public boolean checkStatusFAILED(List<Status> statusList) {
		if (Helper.getLatestStatus(statusList).getStage().equals(Status.Stage.FAILED)) {
			return true;
		}
		return false;
	}
	
	public boolean checkStatusRESUME(List<Status> statusList) {
		if (Helper.getLatestStatus(statusList).getStage().equals(Status.Stage.RESUME)) {
			return true;
		}
		return false;
	}
	
	public boolean checkStatusPAUSE(List<Status> statusList) {
		if (Helper.getLatestStatus(statusList).getStage().equals(Status.Stage.PAUSE)) {
			return true;
		}
		return false;
	}
	
	public boolean checkStatus(List<Status> statusList, Status.Stage stage) {
		if (Helper.getLatestStatus(statusList).getStage().equals(stage)) {
			return true;
		}
		return false;
	}
	
	public com.inferyx.framework.domain.Status.Stage getStageStatus(String uuid, String version, String stageId) throws JsonProcessingException {
		//DagExec dagexec = iDagExec.findOneByUuidAndVersion(uuid, version);
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());
		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {
				List<Status> statusList = stageExec.getStatusList();
				//Collections.sort(statusList);
				return Helper.getLatestStatus(statusList).getStage();
			}
		}
		return null;
	}
	
	
	public List<Status> getStageStatusList(String uuid, String version, String stageId) throws JsonProcessingException {
		//DagExec dagexec = iDagExec.findOneByUuidAndVersion(uuid, version);
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());
		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {
				return stageExec.getStatusList();
			}
		}
		return null;
	}
	
	public com.inferyx.framework.domain.Status.Stage getTaskStatus(String uuid, String version, String stageId, String taskId) throws JsonProcessingException {
		//DagExec dagexec = iDagExec.findOneByUuidAndVersion(uuid, version);
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());
		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {
				List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());			
				for (TaskExec taskExec : taskExecList) {
					if (taskExec.getTaskId().equals(taskId)) {
						List<Status> statusList = taskExec.getStatusList();
						//Collections.sort(statusList);
						return Helper.getLatestStatus(statusList).getStage();
					}
				}
			}
		}
		return null;
	}
	
	public String checkTaskDepStatus(Dag dag, String Uuid, String version, String stageId, String taskId) throws JsonProcessingException {
		boolean isDependencyCOMPLETED = false;
//		Status.Stage latestStatus = null;
		
		//DagExec dagexec = iDagExec.findOneByUuidAndVersion(uuid, version);
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(Uuid, version, MetaType.dagExec.toString());
		TaskExec depTaskExec = getTaskExec(dagexec, stageId, taskId);
		List<String> depTaskIds = depTaskExec.getDependsOn();
		if (depTaskIds == null || depTaskIds.isEmpty()) {
			return Status.Stage.COMPLETED.toString();
		}
		StageExec stageExec = getStageExec(dagexec, stageId);
		List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
		for (TaskExec taskExec : taskExecList) {
			if (depTaskIds.contains(taskExec.getTaskId())) {
//				latestStatus = Helper.getLatestStatus(taskExec.getStatusList()).getStage();
				if (checkStatusCOMPLETED(taskExec.getStatusList())) {
					isDependencyCOMPLETED = true;
				} else if (checkStatusKILLED(taskExec.getStatusList()) || checkStatusKILLED(stageExec.getStatusList()) || checkStatusKILLED(dagexec.getStatusList())) {
					return Status.Stage.KILLED.toString();
				} else if (checkStatusFAILED(taskExec.getStatusList()) || checkStatusFAILED(stageExec.getStatusList()) || checkStatusFAILED(dagexec.getStatusList())) {
					return Status.Stage.FAILED.toString();
				} else if (checkStatus(taskExec.getStatusList(), Status.Stage.ABORTED) 
							|| checkStatus(stageExec.getStatusList(), Status.Stage.ABORTED) 
							|| checkStatus(dagexec.getStatusList(), Status.Stage.ABORTED)) {
					return Status.Stage.ABORTED.toString();
				}  else if (checkStatusPAUSE(taskExec.getStatusList()) || checkStatusPAUSE(stageExec.getStatusList()) || checkStatusPAUSE(dagexec.getStatusList())) {
					return Status.Stage.PAUSE.toString();
				}  else if (checkStatusRESUME(taskExec.getStatusList()) || checkStatusRESUME(stageExec.getStatusList()) || checkStatusRESUME(dagexec.getStatusList())) {
					return Status.Stage.RESUME.toString();
				} else {
					// Task not complete. return false
					return Status.Stage.PENDING.toString();
				}
			}
		}		
		if (isDependencyCOMPLETED) {
			return Status.Stage.COMPLETED.toString();
		}
		return Status.Stage.COMPLETED.toString();
	}
	
	
	public String checkStageDepStatus(Dag dag, String Uuid, String version, String stageId) throws JsonProcessingException {
		boolean isDependencyCOMPLETED = false;
		//DagExec dagexec = iDagExec.findOneByUuidAndVersion(uuid, version);
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(Uuid, version, MetaType.dagExec.toString());
		// Dag dag = (Dag) daoRegister.getRefObject(dagexec.getDag());
		List<StageExec> listStageExecs = DagExecUtil.castToStageExecList(dagexec.getStages());

		HashMap<String, String> stageStatus = new HashMap<>();

		List<String> stageDependsOn = new ArrayList<String>();
		for (StageExec stageExec : listStageExecs) {
			List<Status> statusList = stageExec.getStatusList();
			Stage stage = DagExecUtil.getStageFromDag(dag, stageExec.getStageId());
			if (checkStatusCOMPLETED(statusList)) {
				stageStatus.put(stageExec.getStageId().toString(), Status.Stage.COMPLETED.toString());
			} else if (checkStatusKILLED(statusList)) {
				stageStatus.put(stageExec.getStageId().toString(), Status.Stage.KILLED.toString());
			}  else if (checkStatusFAILED(statusList)) {
				stageStatus.put(stageExec.getStageId().toString(), Status.Stage.FAILED.toString());
			} else {
				stageStatus.put(stageExec.getStageId().toString(), Status.Stage.PENDING.toString());
			}

			if (stageExec.getStageId().equals(stageId)) {
				stageDependsOn = stage.getDependsOn();
			}
			
		}
		
		if (stageDependsOn == null || stageDependsOn.size() == 0) {
			// There is no dependency, so execute this stage 
			return Status.Stage.COMPLETED.toString();
		}
		
		for (String stageDepId : stageDependsOn) {
			String value = (String) stageStatus.get(stageDepId.toString());
			if (value.equalsIgnoreCase(Status.Stage.COMPLETED.toString())) {
				isDependencyCOMPLETED = true;
			} else if (value.equalsIgnoreCase(Status.Stage.KILLED.toString())) {
				return Status.Stage.KILLED.toString();
			} else if (value.equalsIgnoreCase(Status.Stage.FAILED.toString())) {
				return Status.Stage.FAILED.toString();
			} else {
				// Stage not complete. return false
				return Status.Stage.PENDING.toString();
			}
		}
		
		if (isDependencyCOMPLETED) {
			return Status.Stage.COMPLETED.toString();
		} else {
			return Status.Stage.PENDING.toString();
		}

	}

	public List<DagExec> findDagExecStatus(String uuid, String version) {
		Query query = new Query();
		query.addCriteria(Criteria.where("uuid").is(uuid).andOperator(Criteria.where("version").is(version)));
		query.fields().include("status");
		query.with(new Sort(new Order(Direction.DESC, "createdOn")));
		return mongoTemplate.find(query, DagExec.class);
	}

	public List<StageExec> findDagExecStageStatus(String uuid, String version, String stageId) {
		List<StageExec> dagObj = null;

		Aggregation stageAgg = newAggregation(
				match(Criteria.where("uuid").is(uuid).andOperator(Criteria.where("version").is(version),
						Criteria.where("stages.tasks.taskRef.stageId").is(stageId))),
				project("stages"));

		AggregationResults<DagExec> groupResults = mongoTemplate.aggregate(stageAgg, "dagexec", DagExec.class);
		List<DagExec> result = groupResults.getMappedResults();
		for (int j = 0; j < result.size(); j++) {
			dagObj = DagExecUtil.castToStageExecList(result.get(j).getStages());
		}
		return dagObj;

	}

	public List<TaskExec> findDagExecTaskStatus(String uuid, String version, String taskId) {
		List<StageExec> dagObj = null;
		List<TaskExec> dagTasksObj = null;
		Aggregation taskAgg = newAggregation(
				match(Criteria.where("uuid").is(uuid).andOperator(Criteria.where("version").is(version),
						Criteria.where("stages.tasks.taskRef.taskId").is(taskId))),
				project("stages"));

		AggregationResults<DagExec> groupResults = mongoTemplate.aggregate(taskAgg, "dagexec", DagExec.class);
		List<DagExec> result = groupResults.getMappedResults();
		for (int j = 0; j < result.size(); j++) {
			dagObj = DagExecUtil.castToStageExecList(result.get(j).getStages());
			for (int i = 0; i < dagObj.size(); i++) {
				dagTasksObj = DagExecUtil.castToTaskExecList(dagObj.get(i).getTasks());
			}
		}
		return dagTasksObj;
	}
	
	public DagStatusHolder getStatusByDagExec(String dagExecUuid) throws JsonProcessingException {
		//DagExec dagExec = findLatestByUuid(dagExecUuid);
		DagExec dagExec = (DagExec) commonServiceImpl.getLatestByUuid(dagExecUuid, MetaType.dagExec.toString());
		DagStatusHolder dagStatusHolder = new DagStatusHolder();		
		MetaIdentifierHolder dagRef = new MetaIdentifierHolder();
		MetaIdentifier dagRefMeta = dagExec.getDependsOn();
		dagRef.setRef(dagRefMeta);
		dagStatusHolder.setDependsOn(dagRef);
		dagStatusHolder.setStatus(dagExec.getStatusList());
		HashMap<String,StageStatusHolder> stageStatusHolderMap = new HashMap<String,StageStatusHolder>();		
		for(int i=0; i<dagExec.getStages().size();i++)
		{
			HashMap<String,StageStatusHolder> stageMap = new HashMap<>();				
			StageStatusHolder stageStatusHolder = new StageStatusHolder();
			MetaIdentifierHolder stageRef = new MetaIdentifierHolder();
			MetaIdentifier stageRefMeta = dagExec.getDependsOn();
			stageRef.setRef(stageRefMeta);			
			stageStatusHolder.setDependsOn(stageRef);
			stageStatusHolder.setStageId(dagExec.getStages().get(i).getStageId());
			stageStatusHolder.setStatus(dagExec.getStages().get(i).getStatusList());	
			HashMap<String,TaskStatusHolder> taskStatusHolderMap = new HashMap<String,TaskStatusHolder>();
			
			for(int j=0; j<dagExec.getStages().get(i).getTasks().size();j++)
			{			
				HashMap<String,TaskStatusHolder> taskMap = new HashMap<>();				
				TaskStatusHolder taskStatusHolder = new TaskStatusHolder();
				MetaIdentifierHolder taskRef = new MetaIdentifierHolder();
				MetaIdentifier taskRefMeta = dagExec.getDependsOn();
				taskRef.setRef(taskRefMeta);
				taskStatusHolder.setDependsOn(taskRef);
				taskStatusHolder.setStageId(dagExec.getStages().get(i).getStageId());
				taskStatusHolder.setTaskId(dagExec.getStages().get(i).getTasks().get(j).getTaskId());
				taskStatusHolder.setStatus(dagExec.getStages().get(i).getTasks().get(j).getStatusList());
				taskMap.put(dagExec.getStages().get(i).getTasks().get(j).getTaskId(), taskStatusHolder);				
				taskStatusHolderMap.put(dagExec.getStages().get(i).getTasks().get(j).getTaskId(), taskStatusHolder);
			}	
			
			stageStatusHolder.setTasks(taskStatusHolderMap);
			stageStatusHolderMap.put(dagExec.getStages().get(i).getStageId(), stageStatusHolder);
			stageMap.put(dagExec.getStages().get(i).getStageId(), stageStatusHolder);			
		}		
		dagStatusHolder.setStages(stageStatusHolderMap);		
		return dagStatusHolder;
	}

	public DagExec setStagePause(String uuid, String version, String stageId) throws Exception {

		DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());		
		for(int i=0;i<dagExec.getStages().size();i++){
			if(dagExec.getStages().get(i).getStageId().equals(stageId)){
				Status stagePAUSEStatus = new Status(Status.Stage.PAUSE, new Date());
				List<Status> stageStatusList = dagExec.getStages().get(i).getStatusList();
				stageStatusList.remove(stagePAUSEStatus);
				stageStatusList.add(stagePAUSEStatus);
				dagExec.getStages().get(i).setStatusList(stageStatusList);
				for(int j=0;j<dagExec.getStages().get(i).getTasks().size();j++){
					int lastStatus = dagExec.getStages().get(i).getTasks().get(j).getStatusList().size() - 1;
					if(dagExec.getStages().get(i).getTasks().get(j).getStatusList().get(lastStatus).getStage().equals(Status.Stage.PENDING)) {
						logger.info("Stage "+ dagExec.getStages().get(i).getStageId() +" task(s) PENDING.");//raise code 
						MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
						dependsOn.setRef(new MetaIdentifier(MetaType.dagExec, dagExec.getUuid(), dagExec.getVersion()));
						commonServiceImpl.sendResponse("400", MessageStatus.WARNING.toString(), "Stage "+ dagExec.getStages().get(i).getStageId() +" task(s) PENDING.", dependsOn);
						throw new Exception("Stage "+ dagExec.getStages().get(i).getStageId() +" task(s) PENDING.");
					}else{
						Status taskPAUSEStatus = new Status(Status.Stage.PAUSE, new Date());
						List<Status> taskStatusList =dagExec.getStages().get(i).getTasks().get(j).getStatusList();
						taskStatusList.remove(taskPAUSEStatus);
						taskStatusList.add(taskPAUSEStatus);
						dagExec.getStages().get(i).getTasks().get(j).setStatusList(taskStatusList);
					}
						
				}
			}
		}		
		return (DagExec) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
	}
	
	public DagExec setStageFAILED(String uuid, String version, String stageId) throws JsonProcessingException, JSONException, ParseException {
		
		List<Status> dagStatusList = null;
		Status FAILEDStatus = new Status(Status.Stage.FAILED, new Date());
		DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
		for(int i=0;i<dagExec.getStages().size();i++){
			if(dagExec.getStages().get(i).getStageId().equals(stageId)){
				List<Status> stageStatusList = dagExec.getStages().get(i).getStatusList();
				if (!Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.PENDING) 
						&& !Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.RUNNING)) {
					logger.info("Stage can't fail ");
					continue;
				}
					
				if (Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.FAILED)) {
					stageStatusList.remove(stageStatusList.size()-1);
				}
				stageStatusList.add(FAILEDStatus);
				dagStatusList = dagExec.getStatusList();
				if (Helper.getLatestStatus(dagStatusList).getStage().equals(Status.Stage.FAILED)) {
					dagStatusList.remove(dagStatusList.size()-1);
				}
				dagStatusList.add(FAILEDStatus);
			}
		}		
		return (DagExec) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
	}
	
	public DagExec setStageResume(String uuid, String version, String stageId) throws Exception {

		DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
		int lastDagStatus = dagExec.getStatusList().size() - 1;
		if(dagExec.getStatusList().get(lastDagStatus).getStage().equals(Status.Stage.PAUSE)) {
			
		} else {
			for(int i=0;i<dagExec.getStages().size();i++){
				if(dagExec.getStages().get(i).getStageId().equals(stageId)){
					Status stagePAUSEStatus = new Status(Status.Stage.RESUME, new Date());
					List<Status> stageStatusList = dagExec.getStages().get(i).getStatusList();
					stageStatusList.remove(stagePAUSEStatus);
					stageStatusList.add(stagePAUSEStatus);
					dagExec.getStages().get(i).setStatusList(stageStatusList);
					
					for(int j=0;j<dagExec.getStages().get(i).getTasks().size();j++){
						int lastStatus = dagExec.getStages().get(i).getTasks().get(j).getStatusList().size() - 1;
						if(dagExec.getStages().get(i).getTasks().get(j).getStatusList().get(lastStatus).getStage().equals(Status.Stage.PENDING)) {
							logger.info("Stage "+ dagExec.getStages().get(i).getStageId() +" task(s) PENDING.");
							MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
							dependsOn.setRef(new MetaIdentifier(MetaType.dagExec, dagExec.getUuid(), dagExec.getVersion()));
							commonServiceImpl.sendResponse("400", MessageStatus.WARNING.toString(), "Stage "+ dagExec.getStages().get(i).getStageId() +" task(s) PENDING.", dependsOn);
							throw new Exception("Stage "+ dagExec.getStages().get(i).getStageId() +" task(s) PENDING.");
						}else{
							Status taskPAUSEStatus = new Status(Status.Stage.RESUME, new Date());
							List<Status> taskStatusList =dagExec.getStages().get(i).getTasks().get(j).getStatusList();
							taskStatusList.remove(taskPAUSEStatus);
							taskStatusList.add(taskPAUSEStatus);
							dagExec.getStages().get(i).getTasks().get(j).setStatusList(taskStatusList);
						}
							
					}
				}
			}
			return (DagExec) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
		}
		return null;
	}
	
	public String setStatusList(String uuid, String version, String stageId, String taskId, String status) throws JsonProcessingException, Exception {
		if (!StringUtils.isBlank(status)) {
			if (!StringUtils.isBlank(taskId) && !StringUtils.isBlank(stageId)) {
				if(status.equalsIgnoreCase(Status.Stage.RESUME.toString())){
					return objectWriter.writeValueAsString(setTaskResume(uuid, version, stageId, taskId));
				}
				if(status.equalsIgnoreCase(Status.Stage.PAUSE.toString())){
					return objectWriter.writeValueAsString(setTaskPause(uuid, version, stageId, taskId));
				}
				if(status.equalsIgnoreCase(Status.Stage.KILLED.toString())) {
					return objectWriter.writeValueAsString(kill(uuid, version, stageId, taskId));
				}
			} else {
				logger.info("can not perform the "+status.toUpperCase()+" operation.");
			}
			if (!StringUtils.isBlank(stageId) && StringUtils.isBlank(taskId)) {
				if(status.equalsIgnoreCase(Status.Stage.RESUME.toString())){
					return objectWriter.writeValueAsString(setStageResume(uuid, version, stageId));
				}
				if(status.equalsIgnoreCase(Status.Stage.PAUSE.toString())){
					return objectWriter.writeValueAsString(setStagePause(uuid, version, stageId));
				}
				if(status.equalsIgnoreCase(Status.Stage.KILLED.toString())){
					return objectWriter.writeValueAsString(kill(uuid, version, stageId, null));
				}
			} else {
				logger.info("can not perform the "+status.toUpperCase()+" operation.");
			}
			if (StringUtils.isBlank(stageId) && StringUtils.isBlank(taskId)) {
				if(status.equalsIgnoreCase(Status.Stage.RESUME.toString())){
					return objectWriter.writeValueAsString(setDAGResume(uuid, version));
				}
				if(status.equalsIgnoreCase(Status.Stage.PAUSE.toString())){
					return objectWriter.writeValueAsString(setDAGPause(uuid, version));
				}
				if(status.equalsIgnoreCase(Status.Stage.KILLED.toString())) {
					return objectWriter.writeValueAsString(kill(uuid, version, null, null));
				}
			} else {
				logger.info("can not perform the "+status.toUpperCase()+" operation.");
			}
		} else {
			logger.info("Empty status, can not perform the operation.");
		}
		return null;
	}
	
	public DagExec setDAGResume(String uuid, String version) throws Exception {

		DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			Status stageStatus = new Status(Status.Stage.RESUME, new Date());
			List<Status> dagStatusList = dagExec.getStatusList();
			dagStatusList.remove(stageStatus);
			dagStatusList.add(stageStatus);
		for (int i = 0; i < dagExec.getStages().size(); i++) {
				Status stagePAUSEStatus = new Status(Status.Stage.RESUME, new Date());
				List<Status> stageStatusList = dagExec.getStages().get(i).getStatusList();
				stageStatusList.remove(stagePAUSEStatus);
				stageStatusList.add(stagePAUSEStatus);
				dagExec.getStages().get(i).setStatusList(stageStatusList);
				for (int j = 0; j < dagExec.getStages().get(i).getTasks().size(); j++) {
					int lastDag = dagExec.getStages().get(i).getTasks().get(j).getStatusList().size() - 1;
					if (dagExec.getStages().get(i).getTasks().get(j).getStatusList().get(lastDag).getStage()
							.equals(Status.Stage.PENDING)) {
						logger.info("Stage "+ dagExec.getStages().get(i).getStageId() +" task(s) PENDING.");//raise code 
						MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
						dependsOn.setRef(new MetaIdentifier(MetaType.dagExec, dagExec.getUuid(), dagExec.getVersion()));
						commonServiceImpl.sendResponse("400", MessageStatus.WARNING.toString(), "Stage "+ dagExec.getStages().get(i).getStageId() +" task(s) PENDING.", dependsOn);
						throw new Exception("Stage "+ dagExec.getStages().get(i).getStageId() +" task(s) PENDING.");
					} else {
						Status taskPAUSEStatus = new Status(Status.Stage.RESUME, new Date());
						List<Status> taskStatusList = dagExec.getStages().get(i).getTasks().get(j).getStatusList();
						taskStatusList.remove(taskPAUSEStatus);
						taskStatusList.add(taskPAUSEStatus);
						dagExec.getStages().get(i).getTasks().get(j).setStatusList(taskStatusList);
					}

				}
		}
		return (DagExec) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
	}
	
	public DagExec setDAGPause(String uuid, String version) throws Exception {

		DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
		Status stageStatus = new Status(Status.Stage.RESUME, new Date());
		List<Status> dagStatusList = dagExec.getStatusList();
		dagStatusList.remove(stageStatus);
		dagStatusList.add(stageStatus);
		for (int i = 0; i < dagExec.getStages().size(); i++) {
			
				Status stagePAUSEStatus = new Status(Status.Stage.PAUSE, new Date());
				List<Status> stageStatusList = dagExec.getStages().get(i).getStatusList();
				stageStatusList.remove(stagePAUSEStatus);
				stageStatusList.add(stagePAUSEStatus);
				dagExec.getStages().get(i).setStatusList(stageStatusList);
				for (int j = 0; j < dagExec.getStages().get(i).getTasks().size(); j++) {
					int lastDag = dagExec.getStages().get(i).getTasks().get(j).getStatusList().size() - 1;
					if (dagExec.getStages().get(i).getTasks().get(j).getStatusList().get(lastDag).getStage()
							.equals(Status.Stage.PENDING)) {
						logger.info("Stage "+ dagExec.getStages().get(i).getStageId() +" task(s) PENDING.");//raise code 
						MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
						dependsOn.setRef(new MetaIdentifier(MetaType.dagExec, dagExec.getUuid(), dagExec.getVersion()));
						commonServiceImpl.sendResponse("400", MessageStatus.WARNING.toString(), "Stage "+ dagExec.getStages().get(i).getStageId() +" task(s) PENDING.", dependsOn);
						throw new Exception("Stage "+ dagExec.getStages().get(i).getStageId() +" task(s) PENDING.");
					} else {
						Status taskPAUSEStatus = new Status(Status.Stage.PAUSE, new Date());
						List<Status> taskStatusList = dagExec.getStages().get(i).getTasks().get(j).getStatusList();
						taskStatusList.remove(taskPAUSEStatus);
						taskStatusList.add(taskPAUSEStatus);
						dagExec.getStages().get(i).getTasks().get(j).setStatusList(taskStatusList);
					}
				}
		}
		return (DagExec) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
	}
	
	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion, String type) throws Exception {
		DagExec exec = (DagExec)commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, type);
		return exec.getDependsOn();
	}
}

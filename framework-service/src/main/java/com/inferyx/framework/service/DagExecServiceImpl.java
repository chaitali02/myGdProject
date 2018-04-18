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
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.dao.IDagDao;
import com.inferyx.framework.dao.IDagExecDao;
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
	private IDagExecDao dagExecDao;
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
	ConcurrentHashMap taskThreadMap;
	@Autowired
	MetadataUtil daoRegister;
	@Autowired
	SessionContext sessionContext;
 
	ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

	/********************** UNUSED **********************/
	/*public DagExec findLatest() {
		DagExec dagexec=null;
		if(iDagExec.findLatest(new Sort(Sort.Direction.DESC, "version"))!=null){
			dagexec=resolveName(iDagExec.findLatest(new Sort(Sort.Direction.DESC, "version")));
		}
		return dagexec ;
	}*/
	
	
	MetaIdentifierHolder location = new MetaIdentifierHolder();
	static final Logger logger = Logger.getLogger(DagExecServiceImpl.class);
	
	public List<DagExec> findLatestDagExec(String dagUUID, String dagVersion) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDagExec.findLatestDagExec(appUuid, dagUUID, dagVersion);
	}

	/********************** UNUSED **********************/
	/*public List<DagExec> findAll() {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDagExec.findAll(appUuid);
	}*/

	public List<DagExec> findDagExecByDatapod(String datapodUUID) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDagExec.findOneByDatapod(appUuid, datapodUUID);
	}

	/*
	 * public List<DagExec> findDagExecByDag(String dagUUID) { return
	 * iDagExec.findOneByDag(dagUUID); }
	 */

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

	/********************** UNUSED **********************/
	/*public DagExec save(DagExec dagExec) throws Exception {

		if (dagExec.getAppInfo() == null) {
			MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
			List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
			metaIdentifierHolderList.add(meta);
			dagExec.setAppInfo(metaIdentifierHolderList);
		}
		
		dagExec.setBaseEntity();
		DagExec dagexecDet = iDagExec.save(dagExec);
		registerGraph.updateGraph((Object) dagexecDet, MetaType.dagExec);
		return dagexecDet;
	}*/

	/********************** UNUSED **********************/
	/*public DagExec findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDagExec.findOneById(appUuid, id);
		} else
			return iDagExec.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public List<DagExec> test(String param1, String param2) {
		return iDagExec.test(param1, param2);
	}*/

	/********************** UNUSED **********************/
	/*public DagExec findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDagExec.findAllByUuid(appUuid, uuid);

	}*/

	/********************** UNUSED **********************/
	/*public DagExec findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iDagExec.findOneByUuidAndVersion(appUuid, uuid, version);
		} else

			return iDagExec.findOneByUuidAndVersion(uuid, version);

	}*/

	/********************** UNUSED **********************/
	/*public DagExec findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDagExec.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iDagExec.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		DagExec dagexc = iDagExec.findOneById(appUuid, Id);
		dagexc.setActive("N");
		iDagExec.save(dagexc);
//		String ID = dagexc.getId();
//		iDagExec.delete(ID);
//		dagexc.exportBaseProperty();
	}*/

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
		boolean isTaskKilled = false;
		boolean isTaskFailed = false;
		boolean isTaskCompleted = false;
		boolean isTaskNotCompleted = true;
		StageExec stageExec = null;
		try {
			while (isTaskNotCompleted) {
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
				isTaskNotCompleted = false;
				for (TaskExec taskExec : taskExecList) {
					// Check task status
					taskStatus = Helper.getLatestStatus(taskExec.getStatusList());
					if (taskStatus == null) {
						logger.info("No status in task. Continuing with next ... ");
					}
					if (taskStatus.getStage().equals(Status.Stage.Killed)) {
						isTaskKilled = true;
					} else if (taskStatus.getStage().equals(Status.Stage.Failed)) {
						isTaskFailed = true;
					} else if (taskStatus.getStage().equals(Status.Stage.Completed)) {
						isTaskCompleted = true;
					} else if (!taskStatus.getStage().equals(Status.Stage.NotStarted)) {
						isTaskNotCompleted = true;
					}
				}
			}
			if (isTaskKilled) {
//				synchronized (uuid) {
					commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Terminating, stageExec.getStageId());
					commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Killed, stageExec.getStageId());
	//			}
			} else if (isTaskFailed) {
		//		synchronized (uuid) {
					commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Failed, stageExec.getStageId());
			//	}
			} else if (isTaskCompleted) {
				//synchronized (uuid) {
					commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Completed, stageExec.getStageId());
				//}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void checkStageAndKillDag(String uuid, String version) {
		DagExec dagExec = null;
		Status stageStatus = null;
		boolean isStageKilled = false;
		boolean isStageFailed = false;
		boolean isStageCompleted = false;
		boolean isStageNotCompleted = true;
		try {
			while (isStageNotCompleted) {
				//dagExec = findOneByUuidAndVersion(uuid, version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
				Thread.sleep(10000);
				// Fetch stages
				List<StageExec> dagExecStgs = DagExecUtil.castToStageExecList(dagExec.getStages());
				if (dagExecStgs == null || dagExecStgs.isEmpty()) {
					logger.info("No stages in Dag. Aborting ... ");
					return;
				}
				isStageNotCompleted = false;
				for (StageExec stageExec : dagExecStgs) {
					// Check stage status
					stageStatus = Helper.getLatestStatus(stageExec.getStatusList());
					if (stageStatus == null) {
						logger.info("No status in stage. Continuing with next ... ");
					}
					if (stageStatus.getStage().equals(Status.Stage.Killed)) {
						isStageKilled = true;
					} else if (stageStatus.getStage().equals(Status.Stage.Failed)) {
						isStageFailed = true;
					} else if (stageStatus.getStage().equals(Status.Stage.Completed)) {
						isStageCompleted = true;
					}  else if (!stageStatus.getStage().equals(Status.Stage.NotStarted)){
						isStageNotCompleted = true;
					}
				}
			}
		
			if (isStageKilled) {
			//	synchronized (uuid) {
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.Terminating);
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.Killed);
			//	}
			} else if (isStageFailed) {
			//	synchronized (uuid) {
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.Failed);
			//	}
			} else if (isStageCompleted) {
			//	synchronized (uuid) {
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.Completed);
			//	}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public void kill (String uuid, String version) {
//		MetaIdentifier dagExecIdentifier = new MetaIdentifier(MetaType.dagExec, uuid, version);
//		DagExec dagExec = (DagExec) daoRegister.getRefObject(dagExecIdentifier);
//		if (dagExec == null) {
//			logger.info("RuleExec not found. Exiting...");
//			return;
//		}
//		if (!Helper.getLatestStatus(dagExec.getStatusList()).equals(new Status(Status.Stage.InProgress, new Date()))) {
//			logger.info("Latest Status is not in InProgress. Exiting...");
//		}
//		try {
//			commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.Terminating);
//			FutureTask futureTask = (FutureTask) taskThreadMap.get("Rule_" + dagExec.getUuid());
//			futureTask.cancel(true);
//			taskThreadMap.remove("Rule_" + dagExec.getUuid());
//			commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.Killed);
//		} catch (Exception e) {
//			logger.info("Failed to kill. uuid : " + uuid + " version : " + version);			
//			e.printStackTrace();
//		}
//	}
	
	public DagExec setTaskOnHold(String uuid, String version, String stageId, String taskId) throws JsonProcessingException, JSONException, ParseException {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		DagExec dagExec=null;		
		if (appUuid != null) {
			if(StringUtils.isBlank(version)) {
				//dagExec = iDagExec.findOneByUuidAndVersion(appUuid,uuid,version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			}
			else {
				//dagExec = findLatestByUuid(uuid);
				dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
			}
		} else {
			if(StringUtils.isBlank(version)) {
				//dagExec = iDagExec.findOneByUuidAndVersion(uuid,version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			}
			else {
				//dagExec = findLatestByUuid(uuid);
				dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
			}
		}
		for(int i=0;i<dagExec.getStages().size();i++){
			if(dagExec.getStages().get(i).getStageId().equals(stageId)){
				for(int j=0;j<dagExec.getStages().get(i).getTasks().size();j++){
					if(dagExec.getStages().get(i).getTasks().get(j).getTaskId().equals(taskId)){
						Status onHoldStatus = new Status(Status.Stage.OnHold, new Date());
						List<Status> statusList =dagExec.getStages().get(i).getTasks().get(j).getStatusList();
						statusList.remove(onHoldStatus);
						statusList.add(onHoldStatus);
						dagExec.getStages().get(i).getTasks().get(j).setStatusList(statusList);
					}
				}
			}
		}
		//return iDagExec.save(dagExec);
		return (DagExec) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
	}
	public DagExec setTaskResume(String uuid, String version, String stageId, String taskId) throws JsonProcessingException, JSONException, ParseException {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		DagExec dagExec=null;		
		if (appUuid != null) {
			if(StringUtils.isBlank(version)) {
				//dagExec = iDagExec.findOneByUuidAndVersion(appUuid,uuid,version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			}
			else {
				//dagExec = findLatestByUuid(uuid);
				dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
			}
		} else {
			if(StringUtils.isBlank(version)) {
				//dagExec = iDagExec.findOneByUuidAndVersion(uuid,version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			}
			else {
				//dagExec = findLatestByUuid(uuid);
				dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
			}
		}
		for(int i=0;i<dagExec.getStages().size();i++){
			if(dagExec.getStages().get(i).getStageId().equals(stageId)){
				int lastStatus = dagExec.getStages().get(i).getStatusList().size() - 1;
				if(dagExec.getStages().get(i).getStatusList().get(lastStatus).toString().toLowerCase().equals(Status.Stage.OnHold.toString().toLowerCase())) {
					logger.info("Stage is OnHold, can-not start the task.");
				}else {
					for(int j=0;j<dagExec.getStages().get(i).getTasks().size();j++){
						if(dagExec.getStages().get(i).getTasks().get(j).getTaskId().equals(taskId)){
							Status onResumeStatus = new Status(Status.Stage.Resume, new Date());
							List<Status> statusList =dagExec.getStages().get(i).getTasks().get(j).getStatusList();
							statusList.remove(onResumeStatus);
							statusList.add(onResumeStatus);
							dagExec.getStages().get(i).getTasks().get(j).setStatusList(statusList);
						}
					}
				}
				
			}
		}
		//return iDagExec.save(dagExec);
		return (DagExec) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
	}

	public List<String> fetchAllTaskThread() {
		return btchServ.fetchAllTaskThread();
	}

	/********************** UNUSED **********************/
	/*public List<DagExec> findAllLatest() {
		{
			// String appUuid =
			// securityServiceImpl.getAppInfo().getRef().getUuid();;
			Aggregation dagexcAggr = newAggregation(group("uuid").max("version").as("version"));
			AggregationResults<DagExec> dagexcResults = mongoTemplate.aggregate(dagexcAggr, "dagexec", DagExec.class);
			List<DagExec> dagexcList = dagexcResults.getMappedResults();

			// Fetch the relation details for each id@Autowired
			List<DagExec> result = new ArrayList<DagExec>();
			for (DagExec s : dagexcList) {
				DagExec dagExecLatest;
				String appUuid = (securityServiceImpl.getAppInfo() != null
						&& securityServiceImpl.getAppInfo().getRef() != null)
								? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
				if (appUuid != null) {
					dagExecLatest = iDagExec.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
				} else {
					dagExecLatest = iDagExec.findOneByUuidAndVersion(s.getId(), s.getVersion());
				}
				// logger.debug("datapodLatest is " + datapodLatest.getName());
				if (dagExecLatest != null) {
					result.add(dagExecLatest);
				}
			}
			return result;
		}
	}
*/

	/********************** UNUSED 
	 * @throws JsonProcessingException 
	 * @throws ParseException 
	 * @throws JSONException **********************/
	/*public List<DagExec> findAllLatestActive() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Aggregation dagExecAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<DagExec> dagExecResults = mongoTemplate.aggregate(dagExecAggr, "dagexec", DagExec.class);
		List<DagExec> dagExecList = dagExecResults.getMappedResults();

		// Fetch the dagexec details for each id
		List<DagExec> result = new ArrayList<DagExec>();
		for (DagExec s : dagExecList) {
			DagExec dagExecLatest;
			if (appUuid != null) {
				dagExecLatest = iDagExec.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			} else {
				dagExecLatest = iDagExec.findOneByUuidAndVersion(s.getId(), s.getVersion());
			}
			if (dagExecLatest != null) {
				result.add(dagExecLatest);
			}
		}
		return result;
	}
	*/
	public void setStageKilled(String uuid, String version, String stageId) throws JsonProcessingException, JSONException, ParseException {
		//DagExec dagexec  = iDagExec.findOneByUuidAndVersion(appUuid, uuid, version);
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());

		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());
		Date date = new Date();

		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {
				List<Status> stageStatusList = stageExec.getStatusList();
				if (!Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.InProgress)) {
					break;
				}
				com.inferyx.framework.domain.Status status = new Status(
						com.inferyx.framework.domain.Status.Stage.Terminating, date);
				
				for (TaskExec taskExec : DagExecUtil.castToTaskExecList(stageExec.getTasks())) {
					setTaskKilled(uuid, version, stageId, taskExec.getTaskId());
				}
				if (!Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.Completed)) {
					status = new Status(
							com.inferyx.framework.domain.Status.Stage.Killed, date);
					stageStatusList.add(status);
					stageExec.setStatusList(stageStatusList);
				}
			}
		}
	}
	
	public void setTaskKilled(String Uuid, String version, String stageId, String taskId) throws JsonProcessingException, JSONException, ParseException {
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(Uuid, version, MetaType.dagExec.toString());

		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());
		Date date = new Date();
		List<Status> statusList = dagexec.getStatusList();

		boolean isTaskkilled = false;
		
		if (!Helper.getLatestStatus(statusList).getStage().equals(Status.Stage.InProgress)) {
			return;
		}
		
		logger.info("Before adding terminated status to DAG list");
		statusList.add(new Status(
									com.inferyx.framework.domain.Status.Stage.Terminating, date));

		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {
				List<Status> stageStatusList = stageExec.getStatusList();
				//Collections.sort(stageStatusList);
				if (!Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.InProgress)) {
					break;
				}
				com.inferyx.framework.domain.Status status = new Status(
						com.inferyx.framework.domain.Status.Stage.Terminating, date);
				stageStatusList.add(status);
				List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
				for (TaskExec taskExec : taskExecList) {
					if (taskExec.getTaskId().equals(taskId)) {
						List<Status> taskStatusList = taskExec.getStatusList();
						//Collections.sort(taskStatusList);
						if (Helper.getLatestStatus(taskStatusList).equals(new Status(Status.Stage.InProgress, new Date()))) {
							isTaskkilled = true;
							status = new Status(
									com.inferyx.framework.domain.Status.Stage.Terminating, date);
							taskStatusList.add(status);
							logger.info("Starting to kill operator");
							commonServiceImpl.kill(taskExec.getOperators().get(0).getOperatorInfo().getRef().getType()
													, taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid()
													, taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion()); 
							logger.info("After operator is killed");
							status = new Status(
									com.inferyx.framework.domain.Status.Stage.Killed, new Date());
							taskStatusList.add(status);
							taskExec.setStatusList(taskStatusList);
						}
					}
				}
				stageExec.setTasks(DagExecUtil.convertToTaskList(taskExecList));
				if (!Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.Completed)) {
					status = new Status(
							com.inferyx.framework.domain.Status.Stage.Killed, date);
					stageStatusList.add(status);
					stageExec.setStatusList(stageStatusList);
				}
				
			}
		}
		dagexec.setStages(DagExecUtil.convertToStageList(stageExecList));
		if (isTaskkilled) {
			//Collections.sort(statusList);
			if (!Helper.getLatestStatus(statusList).getStage().equals(Status.Stage.Completed)) {
				com.inferyx.framework.domain.Status status = new Status(
						com.inferyx.framework.domain.Status.Stage.Killed, date);
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

	
	public void setTaskInProgress(String Uuid, String version, String stageId, String taskId) throws JsonProcessingException, JSONException, ParseException {
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(Uuid, version, MetaType.dagExec.toString());

		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagexec.getStages());
		Date date = new Date();
		List<Status> statusList = dagexec.getStatusList();

		// Collections.sort(statusList);
		if (statusList.get(0).getStage().equals(Status.Stage.NotStarted) && 
				!Helper.getLatestStatus(statusList).equals(new Status(
					com.inferyx.framework.domain.Status.Stage.InProgress, date))) {
			com.inferyx.framework.domain.Status status = new Status(
					com.inferyx.framework.domain.Status.Stage.InProgress, date);
			statusList.add(status);
			dagexec.setStatusList(statusList);
		}

		for (StageExec stageExec : stageExecList) {
			if (stageExec.getStageId().equals(stageId)) {
				List<Status> stageStatusList = stageExec.getStatusList();
				// Collections.sort(stageStatusList);
				if (stageStatusList.get(0).getStage().equals(Status.Stage.NotStarted) && 
						!Helper.getLatestStatus(stageStatusList).equals(new Status(
								com.inferyx.framework.domain.Status.Stage.InProgress, date))) {
					com.inferyx.framework.domain.Status status = new Status(
							com.inferyx.framework.domain.Status.Stage.InProgress, date);
					stageStatusList.add(status);
					stageExec.setStatusList(stageStatusList);
				}
				List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
				for (TaskExec taskExec : taskExecList) {
					if (taskExec.getTaskId().equals(taskId)) {
						List<Status> taskStatusList = taskExec.getStatusList();
					//	Collections.sort(taskStatusList);
						if (taskStatusList.get(0).getStage().equals(Status.Stage.NotStarted)) {

							com.inferyx.framework.domain.Status status = new Status(
									com.inferyx.framework.domain.Status.Stage.InProgress, date);
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

	@SuppressWarnings("unchecked")
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
							operatorStatusList = commonServiceImpl.getAllStatusForExec(indvTaskExec.getOperators().get(0).getOperatorInfo().getRef());
							Status operatorStatus = Helper.getLatestStatus(operatorStatusList);
							com.inferyx.framework.domain.Status status = null;
							if (operatorStatus.getStage().equals(Status.Stage.Killed)) {
								status = new Status(
										com.inferyx.framework.domain.Status.Stage.Killed, new Date());
							} else {
								status = new Status(
										com.inferyx.framework.domain.Status.Stage.Completed, new Date());
							}
							/*com.inferyx.framework.domain.Status status = new Status(
									com.inferyx.framework.domain.Status.Stage.Completed, new Date());*/
							taskStatusList.add(status);
							indvTaskExec.setStatusList(taskStatusList);
						}
						boolean istaskCompleted = false;						
						// Loop all tasks status list
						if (Helper.getLatestStatus(taskStatusList).getStage().equals(com.inferyx.framework.domain.Status.Stage.Completed)) {
							istaskCompleted = true;
						} else {
							istaskCompleted = false;
						}
						if (!istaskCompleted) {
							checkAllTaskStatus = false;
						}
						stageExec.setTasks(DagExecUtil.convertToTaskList(dagTaskExecs));
					}
					if (checkAllTaskStatus) {
						List<Status> stageStatusList = stageExec.getStatusList();
						com.inferyx.framework.domain.Status status = new Status(
								com.inferyx.framework.domain.Status.Stage.Completed, new Date());
						stageStatusList.add(status);
						stageExec.setStatusList(stageStatusList);
					} else if (!checkAllTaskStatus) {
						checkAllStageStatus = false;
					}

				} else {
					// Set checkAllStageStatus flag for current stage
					List<Status> stageStatusList2 = stageExec.getStatusList();
					if (!checkStatusCompleted(stageStatusList2)) {
						checkAllStageStatus = false;
					}
				}
				dagexec.setStages(DagExecUtil.convertToStageList(stageExecList));
			}

			// Set top level status
			if (checkAllStageStatus) {
				List<Status> dagExecStatusList = dagexec.getStatusList();
				com.inferyx.framework.domain.Status status = new Status(
						com.inferyx.framework.domain.Status.Stage.Completed, new Date());
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
		boolean taskFailed = false;
		boolean taskKilled = false;
		boolean taskOnHold = false;
		List<Status> stageStatusList = null;
		List<Status> taskStatusList = null;
		Status taskStatus = null;
		
		StageExec stageExec = getStageExec(dagexec, stageId);
		stageStatusList = stageExec.getStatusList();
		// Traverse all tasks
		
		for (TaskExec taskExec : DagExecUtil.castToTaskExecList(stageExec.getTasks())){
			taskStatusList = taskExec.getStatusList();
			taskStatus = Helper.getLatestStatus(taskStatusList); 
			if (taskStatus.getStage().equals(Status.Stage.Failed)) {
				setStatus(stageStatusList, new Status (Status.Stage.Failed, new Date()));
				taskFailed = true;
				
				allTasksComplete = false;
				break;
			}
			if (taskStatus.getStage().equals(Status.Stage.Killed)) {
				setStatus(stageStatusList, new Status (Status.Stage.Killed, new Date()));
				taskKilled = true;
				allTasksComplete = false;
				break;
			}
			if (taskStatus.getStage().equals(Status.Stage.OnHold)) {
				setStatus(stageStatusList, new Status (Status.Stage.OnHold, new Date()));
				taskOnHold = true;
				allTasksComplete = false;
				break;
			}
		}
		if (allTasksComplete) {
			setStatus(stageStatusList, new Status (Status.Stage.Completed, new Date()));
		}		
		DagExecUtil.replaceInStageList(dagexec.getStages(), stageExec);
		setDagStatus(dagexec);
		//iDagExec.save(dagexec);
		commonServiceImpl.save(MetaType.dagExec.toString(), dagexec);
	}
	
	public void setDagStatus(DagExec dagexec) {		
		boolean allStagesComplete = true;
		boolean stageFailed = false;
		boolean stageKilled = false;
		boolean stageOnHold = false;
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
			if (stageStatus.getStage().equals(Status.Stage.Failed)) {
				setStatus(dagStatusList, new Status (Status.Stage.Failed, new Date()));
				stageFailed = true;
				allStagesComplete = false;
				break;
			}
			if (stageStatus.getStage().equals(Status.Stage.Killed)) {
				setStatus(dagStatusList, new Status (Status.Stage.Killed, new Date()));
				stageKilled = true;
				allStagesComplete = false;
				break;
			}
			if (stageStatus.getStage().equals(Status.Stage.OnHold)) {
				stageOnHold = true;
				allStagesComplete = false;
				break;
			}
			if (!stageStatus.getStage().equals(Status.Stage.Completed)) {
				allStagesComplete = false;
				break;
			}
		}
		
		if (allStagesComplete) {
			setStatus(dagStatusList, new Status (Status.Stage.Completed, new Date()));
		} else if (stageFailed) {
			setStatus(dagStatusList, new Status (Status.Stage.Failed, new Date()));
		} else if (stageKilled) {
			setStatus(dagStatusList, new Status (Status.Stage.Killed, new Date()));
		}
	}
	
	public void setStatus (List<Status> statusList, Status status) {
		if (Helper.getLatestStatus(statusList).equals(status)) {
			statusList.remove(statusList.size()-1);
		}
		statusList.add(status);
	}

	/********************** UNUSED 
	 * @throws JsonProcessingException 
	 * @throws ParseException 
	 * @throws JSONException **********************/
	/*public DagExec getOneByUuidAndVersion(String uuid, String version) {

		return iDagExec.findOneByUuidAndVersion(uuid, version);
	}*/

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
						boolean istaskCompleted = false;
						boolean istaskFailed = false;

						// Loop all tasks status list
						for (Status taskStatus : taskStatusList) {
							if (taskStatus.getStage().equals(com.inferyx.framework.domain.Status.Stage.Completed)) {
								istaskCompleted = true;
								break;
							} else {
								istaskCompleted = false;
							}

							if (taskStatus.getStage().equals(com.inferyx.framework.domain.Status.Stage.Failed)) {
								istaskFailed = true;
								break;
							} else {
								istaskFailed = false;
							}
							
						}
						
						if (!istaskCompleted && !istaskFailed) {
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
					if (!checkStatusCompleted(stageStatusList) || !checkStatusFailed(stageStatusList) || !checkStatusOnHold(stageStatusList)) {
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
	
	public boolean checkStatusCompleted(List<Status> statusList) {
		if (Helper.getLatestStatus(statusList).getStage().equals(Status.Stage.Completed)) {
			return true;
		}
		return false;
	}
	
	public boolean checkStatusKilled(List<Status> statusList) {
		if (Helper.getLatestStatus(statusList).getStage().equals(Status.Stage.Killed)) {
			return true;
		}
		return false;
	}

	public boolean checkStatusFailed(List<Status> statusList) {
		if (Helper.getLatestStatus(statusList).getStage().equals(Status.Stage.Failed)) {
			return true;
		}
		return false;
	}
	
	public boolean checkStatusOnHold(List<Status> statusList) {
		if (Helper.getLatestStatus(statusList).getStage().equals(Status.Stage.OnHold)) {
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
		boolean isDependencyCompleted = false;
		Status.Stage latestStatus = null;
		
		//DagExec dagexec = iDagExec.findOneByUuidAndVersion(uuid, version);
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(Uuid, version, MetaType.dagExec.toString());
		TaskExec depTaskExec = getTaskExec(dagexec, stageId, taskId);
		List<String> depTaskIds = depTaskExec.getDependsOn();
		if (depTaskIds == null || depTaskIds.isEmpty()) {
			return "Completed";
		}
		StageExec stageExec = getStageExec(dagexec, stageId);
		List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
		for (TaskExec taskExec : taskExecList) {
			if (depTaskIds.contains(taskExec.getTaskId())) {
				latestStatus = Helper.getLatestStatus(taskExec.getStatusList()).getStage();
				if (checkStatusCompleted(taskExec.getStatusList())) {
					isDependencyCompleted = true;
				} else if (checkStatusKilled(taskExec.getStatusList())) {
					return "Killed";
				} else if (checkStatusFailed(taskExec.getStatusList())) {
					return "Failed";
				} else {
					// Task not complete. return false
					return "NotCompleted";
				}
			}
		}		
		if (isDependencyCompleted) {
			return "Completed";
		}
		return "Completed";
	}
	
	/*public String checkTaskDepStatus(Dag dag, String Uuid, String version, String stageId, String taskId) {
		DagExec dagexec;
		boolean isDependencyCompleted = false;
		
		if (securityServiceImpl.getAppInfo() != null) {
			String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
			dagexec = iDagExec.findOneByUuidAndVersion(appUuid, Uuid, version);
		} else {
			dagexec = iDagExec.findOneByUuidAndVersion(Uuid, version);
		}
		// Dag dag = (Dag) daoRegister.getRefObject(dagexec.getDag());
		List<StageExec> listStageExecs = DagExecUtil.castToStageExecList(dagexec.getStages());

		HashMap stageStatus = new HashMap();
		HashMap taskStatus = new HashMap();

		List<String> stageDependsOn = new ArrayList<String>();
		List<String> taskDependsOn = new ArrayList<String>();
		for (StageExec stageExec : listStageExecs) {
			List<Status> statusList = stageExec.getStatusList();
			Stage stage = DagExecUtil.getStageFromDag(dag, stageExec.getStageId());
			if (checkStatusCompleted(statusList)) {
				stageStatus.put(stageExec.getStageId().toString(), "Completed");
			} else if (checkStatusKilled(statusList)) {
				stageStatus.put(stageExec.getStageId().toString(), "Killed");
			}  else if (checkStatusFailed(statusList)) {
				stageStatus.put(stageExec.getStageId().toString(), "Failed");
			} else {
				stageStatus.put(stageExec.getStageId().toString(), "NotCompleted");
			}

			if (stageExec.getStageId().equals(stageId)) {
				stageDependsOn = stage.getDependsOn();
			}

			List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
			for (TaskExec taskExec : taskExecList) {
				List<Status> taskStatusList = taskExec.getStatusList();
				String taskKey = stageExec.getStageId() + "_" + taskExec.getTaskId();
				if (checkStatusCompleted(taskStatusList)) {
					taskStatus.put(taskKey, "Completed");
				} else if (checkStatusKilled(taskStatusList)) {
					taskStatus.put(taskKey, "Killed");
				}  else if (checkStatusFailed(taskStatusList)) {
					taskStatus.put(taskKey, "Failed");
				} else {
					taskStatus.put(taskKey, "NotCompleted");
				}
				if (stageExec.getStageId().equals(stageId)
						&& taskExec.getTaskId().equals(taskId)) {
					Task task = DagExecUtil.getTaskFromStage(stage, taskId);
					taskDependsOn = task.getDependsOn();// whether to have
														// dependsOn in DAGExec
				}
			}
		}

		if (stageDependsOn.size() == 0 && taskDependsOn.size() == 0) {
			// There is no dependency, so execute this stage task
			return "Completed";
		}

		for (String stageDepId : stageDependsOn) {
			String value = (String) stageStatus.get(stageDepId.toString());
			if (value.equalsIgnoreCase("Completed")) {
				isDependencyCompleted = true;
			} else if (value.equalsIgnoreCase("Killed")) {
				return "Killed";
			} else if (value.equalsIgnoreCase("Failed")) {
				return "Failed";
			} else {
				// Stage not complete. return false
				return "NotCompleted";
			}
		}
		for (String taskDepId : taskDependsOn) {
			String value = (String) taskStatus.get(stageId + "_" + taskDepId);
			if (value.equalsIgnoreCase("Completed")) {
				isDependencyCompleted = true;
			} else if (value.equalsIgnoreCase("Killed")) {
				return "Killed";
			} else if (value.equalsIgnoreCase("Failed")) {
				return "Failed";
			} else {
				// Task not complete. return false
				return "NotCompleted";
			}
		}
		if (isDependencyCompleted) {
			return "Completed";
		} else {
			return "NotCompleted";
		}
	}
*/	
	public String checkStageDepStatus(Dag dag, String Uuid, String version, String stageId) throws JsonProcessingException {
		boolean isDependencyCompleted = false;
		//DagExec dagexec = iDagExec.findOneByUuidAndVersion(uuid, version);
		DagExec dagexec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(Uuid, version, MetaType.dagExec.toString());
		// Dag dag = (Dag) daoRegister.getRefObject(dagexec.getDag());
		List<StageExec> listStageExecs = DagExecUtil.castToStageExecList(dagexec.getStages());

		HashMap stageStatus = new HashMap();

		List<String> stageDependsOn = new ArrayList<String>();
		for (StageExec stageExec : listStageExecs) {
			List<Status> statusList = stageExec.getStatusList();
			Stage stage = DagExecUtil.getStageFromDag(dag, stageExec.getStageId());
			if (checkStatusCompleted(statusList)) {
				stageStatus.put(stageExec.getStageId().toString(), "Completed");
			} else if (checkStatusKilled(statusList)) {
				stageStatus.put(stageExec.getStageId().toString(), "Killed");
			}  else if (checkStatusFailed(statusList)) {
				stageStatus.put(stageExec.getStageId().toString(), "Failed");
			} else {
				stageStatus.put(stageExec.getStageId().toString(), "NotCompleted");
			}

			if (stageExec.getStageId().equals(stageId)) {
				stageDependsOn = stage.getDependsOn();
			}
			
		}
		
		if (stageDependsOn == null || stageDependsOn.size() == 0) {
			// There is no dependency, so execute this stage 
			return "Completed";
		}
		
		for (String stageDepId : stageDependsOn) {
			String value = (String) stageStatus.get(stageDepId.toString());
			if (value.equalsIgnoreCase("Completed")) {
				isDependencyCompleted = true;
			} else if (value.equalsIgnoreCase("Killed")) {
				return "Killed";
			} else if (value.equalsIgnoreCase("Failed")) {
				return "Failed";
			} else {
				// Stage not complete. return false
				return "NotCompleted";
			}
		}
		
		if (isDependencyCompleted) {
			return "Completed";
		} else {
			return "NotCompleted";
		}

	}
	
/*	public boolean checkTaskDepStatus(Dag dag, String Uuid, String version, String stageId, String taskId) {
		boolean isDependencyCompleted = false;
		DagExec dagexec;

		if (securityServiceImpl.getAppInfo() != null) {
			String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
			dagexec = iDagExec.findOneByUuidAndVersion(appUuid, Uuid, version);
		} else {
			dagexec = iDagExec.findOneByUuidAndVersion(Uuid, version);
		}
		// Dag dag = (Dag) daoRegister.getRefObject(dagexec.getDag());
		List<StageExec> listStageExecs = DagExecUtil.castToStageExecList(dagexec.getStages());

		HashMap stageStatus = new HashMap();
		HashMap taskStatus = new HashMap();

		List<String> stageDependsOn = new ArrayList<String>();
		List<String> taskDependsOn = new ArrayList<String>();

		for (StageExec stageExec : listStageExecs) {
			List<Status> statusList = stageExec.getStatusList();
			Stage stage = DagExecUtil.getStageFromDag(dag, stageExec.getStageId());
			if (checkStatusCompleted(statusList)) {
				stageStatus.put(stageExec.getStageId().toString(), "Completed");
			} else {
				stageStatus.put(stageExec.getStageId().toString(), "NotCompleted");
			}

			if (stageExec.getStageId().equals(stageId)) {
				stageDependsOn = stage.getDependsOn();
			}

			List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
			for (TaskExec taskExec : taskExecList) {
				List<Status> taskStatusList = taskExec.getStatusList();
				String taskKey = stageExec.getStageId() + "_" + taskExec.getTaskId();
				if (checkStatusCompleted(taskStatusList)) {
					taskStatus.put(taskKey, "Completed");
				} else {
					taskStatus.put(taskKey, "NotCompleted");
				}
				if (stageExec.getStageId().equals(stageId)
						&& taskExec.getTaskId().equals(taskId)) {
					Task task = DagExecUtil.getTaskFromStage(stage, taskId);
					taskDependsOn = task.getDependsOn();// whether to have
														// dependsOn in DAGExec
				}
			}
		}

		if (stageDependsOn.size() == 0 && taskDependsOn.size() == 0) {
			// There is no dependency, so execute this stage task
			return true;
		}

		for (String stageDepId : stageDependsOn) {
			String value = (String) stageStatus.get(stageDepId.toString());
			if (value.equalsIgnoreCase("Completed")) {
				isDependencyCompleted = true;
			} else {
				// Stage not complete. return false
				return false;
			}
		}
		for (String taskDepId : taskDependsOn) {
			String value = (String) taskStatus.get(stageId + "_" + taskDepId);
			if (value.equalsIgnoreCase("Completed")) {
				isDependencyCompleted = true;
			} else {
				// Task not complete. return false
				return false;
			}
		}

		return isDependencyCompleted;
	}
*/
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

	/********************** UNUSED **********************/
	/*public List<Map<String, Object>> getTaskResults(String dagExecUUID, String dagExecVersion) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		OrderKey dagExeckey = new OrderKey();
		List<Map<String, Object>> data = new ArrayList<>();
		dagExeckey.setUUID(dagExecUUID);
		dagExeckey.setVersion(dagExecVersion);
		// Bhanu - JIRA FW-3 - Remove cache
		// DagExec dagExec = ((Loader)loader).getCachDagExec().get(dagExeckey);
		//DagExec dagExec = findOneByUuidAndVersion(dagExecUUID, dagExecVersion);
		DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(dagExecUUID, dagExecVersion, MetaType.dagExec.toString());

		// String filePath =
		// dagExec.getStages().get(0).getTasks().get(0).getOutput();
		// String tableName = Helpers.generateTableName(filePath);

		// Datastore implementation

		String dataStoreUUID = null;
		String dataStoreVersion = null;
		for (int i = 0; i < dagExec.getStages().size(); i++) {
			for (int j = 0; j < dagExec.getStages().get(i).getTasks().size(); j++) {
				location = ((TaskExec)dagExec.getStages().get(i).getTasks().get(j)).getResult();
				dataStoreUUID = location.getRef().getUuid();
				dataStoreVersion = location.getRef().getVersion();
				// dataStore =
				// dataStoreServiceImpl.findOneByUuidAndVersion(locUUID,
				// locVer.toString());
			}
		}
		logger.info(
				"Extracting DataStore UUID :: " + dataStoreUUID + " And DataStore version " + dataStoreVersion);

		String tableName = dataStoreServiceImpl.getTableNameByDatastore(dataStoreUUID, dataStoreVersion);

		//DataFrame df = sqlContext.sql(String.format("select * from %s", tableName));
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		data = exec.executeAndFetch(String.format("select * from %s", tableName), null);
//		DataFrame df = rsHolder.getDataFrame();
//		Row[] rows = df.head(20);
//		String[] columns = df.columns();
//		for (Row row : rows) {
//			Map<String, Object> object = new HashMap<String, Object>(columns.length);
//			for (String column : columns) {
//				object.put(column, row.getAs(column));
//			}
//			data.add(object);
//		}
		return data;
	}*/

	/********************** UNUSED **********************/
	/*public List<Map<String, Object>> getTaskResults(String dagExecUUID, String dagExecVersion, String stageId,
			String taskId, String format, String download, HttpServletResponse response, int rowLimit) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		OrderKey dagExeckey = new OrderKey();
		List<Map<String, Object>> data = new ArrayList<>();
		dagExeckey.setUUID(dagExecUUID);
		dagExeckey.setVersion(dagExecVersion);
		//DagExec dagExec = findOneByUuidAndVersion(dagExecUUID, dagExecVersion);
		DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(dagExecUUID, dagExecVersion, MetaType.dagExec.toString());

		// String filePath =
		// dagExec.getStages().get(Integer.parseInt(stageId)).getTasks().get(Integer.parseInt(taskId)).getOutput();
		// String tableName = Helpers.generateTableName(filePath);

		// datastore implementation
		String dataStoreUUID = null;
		String dataStoreVersion = null;

		location = ((TaskExec)dagExec.getStages().get(Integer.parseInt(stageId)).getTasks().get(Integer.parseInt(taskId)))
				.getResult();
		dataStoreUUID = location.getRef().getUuid();
		dataStoreVersion = location.getRef().getVersion();

		logger.info("Extracting DataStore UUID :: " + dataStoreUUID + " And DataStore version " + dataStoreVersion);

		String tableName = dataStoreServiceImpl.getTableNameByDatastore(dataStoreUUID, dataStoreVersion);
		//DataFrame df = sqlContext.sql(String.format("select * from %s", tableName));
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		ResultSetHolder rsHolder = exec.executeSql(String.format("select * from %s", tableName));
		DataFrame df = rsHolder.getDataFrame();
		if (download.equalsIgnoreCase("n")) {

			Row[] rows = df.head(500);
			String[] columns = df.columns();
			for (Row row : rows) {
				Map<String, Object> object = new HashMap<String, Object>(columns.length);
				for (String column : columns) {
					object.put(column, row.getAs(column));
				}
				data.add(object);
			}
		}
		if (download.equalsIgnoreCase("y") && format.equalsIgnoreCase("excel")) {
			response.setContentType("application/xml charset=utf-16");
			response.setHeader("Content-type", "application/xml");
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet(MetaType.datapod.toString());
			ArrayList<String> al = null;
			ArrayList<ArrayList<String>> arlist = new ArrayList<ArrayList<String>>();
//			
//			 * DataStore dds = findDataStoreByMeta(uuid, version); String dtn =
//			 * getTableName(ds.getUuid(), dds.getVersion()); System.out.println(
//			 * "Datastore - Table name:"+dtn);
//			 
			// DataFrame df = sqlContext.sql("select * from "+tn);
			Row[] drows = df.head(rowLimit);
			// System.out.println("Rows"+df.count());
			String[] dcolumns = df.columns();
			try {
				al = new ArrayList<>();
				for (String column : dcolumns) {
					al.add(column);
				}
				arlist.add(al);
				for (Row row : drows) {
					al = new ArrayList<>();
					for (int i = 0; i < dcolumns.length; i++) {
						al.add(String.valueOf(row.get(i)));

						for (int k = 0; k < arlist.size(); k++) {
							ArrayList<String> ardata = (ArrayList<String>) arlist.get(k);
							HSSFRow hssfRow = sheet.createRow((short) k);

							for (int p = 0; p < ardata.size(); p++) {
								HSSFCell cell = hssfRow.createCell((short) p);
								cell.setCellValue(ardata.get(p).toString());
							}
						}
					}
					arlist.add(al);

				}
				response.addHeader("Content-Disposition", "attachment; filename=Dag.xlsx");
				ServletOutputStream sos = response.getOutputStream();
				workbook.write(sos);
				sos.write(workbook.getBytes());
				sos.close();
			}
			catch (IOException e1) {
				e1.printStackTrace();
				logger.info("exception caught while download file");
			}
		}
		return data;
	}*/

	/********************** UNUSED **********************/
	/*public String getTableName(String dagExecUUID, String dagExecVersion, String stageId, String taskId) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		OrderKey dagExecKey = new OrderKey();
		dagExecKey.setUUID(dagExecUUID);
		dagExecKey.setVersion(dagExecVersion);
		//DagExec dagexec = iDagExec.findOneByUuidAndVersion(uuid, version);
		DagExec dagExec  = (DagExec) commonServiceImpl.getOneByUuidAndVersion(dagExecUUID, dagExecVersion, MetaType.dagExec.toString());
		// String filePath =
		// dagExec.getStages().get(Integer.parseInt(stageId)).getTasks().get(Integer.parseInt(taskId)).getOutput();
		// String tableName = Helpers.generateTableName(filePath);

		// datastore implementation
		String dataStoreUUID = null;
		String dataStoreVersion = null;
		location = ((TaskExec)dagExec.getStages().get(Integer.parseInt(stageId)).getTasks().get(Integer.parseInt(taskId)))
				.getResult();
		dataStoreUUID = location.getRef().getUuid();
		dataStoreVersion = location.getRef().getVersion();

		
		 * location =
		 * dagExec.getStages().get(Integer.parseInt(stageId)).getTaskExecs().get
		 * (Integer.parseInt(taskId)).getResult(); dataStoreUUID
		 * =location.getRef().getUuid(); dataStoreVersion =
		 * location.getRef().getVersion();
		 

		logger.info("Extracting DataStore UUID :: " + dataStoreUUID + " And DataStore version " + dataStoreVersion);
		String tableName = dataStoreServiceImpl.getTableNameByDatastore(dataStoreUUID, dataStoreVersion);
		return tableName;
	}*/

	/********************** UNUSED **********************/
	/*public List<DagExec> resolveName(List<DagExec> dagExec) {
		List<DagExec> dagExecList = new ArrayList<>();
		for (DagExec dExec : dagExec) {
			String createdByRefUuid = dExec.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			dExec.getCreatedBy().getRef().setName(user.getName());
			List<MetaIdentifierHolder> appList = dExec.getAppInfo();
			for (int i = 0; i < appList.size(); i++) {
				String appuuid = dExec.getAppInfo().get(i).getRef().getUuid();
				Application app = applicationServiceImpl.findLatestByUuid(appuuid);
				dExec.getAppInfo().get(i).getRef().setName(app.getName());
			}
			dagExecList.add(dExec);
		}
		return dagExecList;
	}*/

	/********************** UNUSED **********************/
	/*public DagExec resolveName(DagExec dagExec) {
		
	try {
		String createdByRefUuid = dagExec.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		dagExec.getCreatedBy().getRef().setName(user.getName());
		if (dagExec.getAppInfo() != null) {
			for (int i = 0; i < dagExec.getAppInfo().size(); i++) {
				String appUuid = dagExec.getAppInfo().get(i).getRef().getUuid();
				Application application = applicationServiceImpl.findLatestByUuid(appUuid);
				String appName = application.getName();
				dagExec.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		

		String dependsOnUuid=dagExec.getDependsOn().getUuid();
		Dag dag=dagServiceImpl.findLatestByUuid(dependsOnUuid);
		dagExec.getDependsOn().setName(dag.getName());
		
		for(int i=0;i<dagExec.getStages().size();i++){
		for(int j=0;j<dagExec.getStages().get(i).getTasks().size();j++){
		
		MetaType type=dagExec.getStages().get(i).getTasks().get(j).getOperators().get(0).getOperatorInfo().getRef().getType();
		if(type.toString().equals(MetaType.dqExec.toString())){
		String dqgroupUuid=dagExec.getStages().get(i).getTasks().get(j).getOperators().get(0).getOperatorInfo().getRef().getUuid();
		DataQualExec dataQualExec=dataQualExecServiceImpl.findLatestByUuid(dqgroupUuid);
		dagExec.getStages().get(i).getTasks().get(j).getOperators().get(0).getOperatorInfo().getRef().setName(dataQualExec.getName());
		}
		else if(type.toString().equals(MetaType.loadExec.toString())){
		String dqgroupUuid=dagExec.getStages().get(i).getTasks().get(j).getOperators().get(0).getOperatorInfo().getRef().getUuid();
		LoadExec loadExec=loadExecServiceImpl.findLatestByUuid(dqgroupUuid);
		dagExec.getStages().get(i).getTasks().get(j).getOperators().get(0).getOperatorInfo().getRef().setName(loadExec.getName());
		}
		else if(type.toString().equals(MetaType.mapExec.toString())){
		String dqgroupUuid=dagExec.getStages().get(i).getTasks().get(j).getOperators().get(0).getOperatorInfo().getRef().getUuid();
		MapExec mapExec=mapExecServiceImpl.findLatestByUuid(dqgroupUuid);
		dagExec.getStages().get(i).getTasks().get(j).getOperators().get(0).getOperatorInfo().getRef().setName(mapExec.getName());
		}
		else if(type.toString().equals(MetaType.dqgroupExec.toString())){
		String dqgroupUuid=dagExec.getStages().get(i).getTasks().get(j).getOperators().get(0).getOperatorInfo().getRef().getUuid();
		DataQualGroupExec dataQualGroupExec=dataQualGroupExecServiceImpl.findLatestByUuid(dqgroupUuid);
		dagExec.getStages().get(i).getTasks().get(j).getOperators().get(0).getOperatorInfo().getRef().setName(dataQualGroupExec.getName());
		}
		}
		}
		
		
	
	} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
			
		return dagExec;
	}*/

	/********************** UNUSED **********************/
	/*public List<DagExec> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iDagExec.findAllVersion(appUuid, uuid);
		} else
			return iDagExec.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public DagExec getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iDagExec.findAsOf(appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
		} else
			return iDagExec.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(DagExec dagExec) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();
		DagExec dagExecNew = new DagExec(dagExec);
		dagExecNew.setName(dagExec.getName() + "_copy");
		dagExecNew.setActive(dagExec.getActive());
		dagExecNew.setDesc(dagExec.getDesc());
		dagExecNew.setTags(dagExec.getTags());
		dagExecNew.setDependsOn(dagExec.getDependsOn());
		dagExecNew.setStatusList(dagExec.getStatusList());
		dagExecNew.setExecParams(dagExec.getExecParams());
		dagExecNew.setStages(dagExec.getStages());
		save(dagExecNew);
		ref.setType(MetaType.dagExec);
		ref.setUuid(dagExecNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> dagExecList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity dagExec : dagExecList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = dagExec.getId();
			String uuid = dagExec.getUuid();
			String version = dagExec.getVersion();
			String name = dagExec.getName();
			String desc = dagExec.getDesc();
			String published=dagExec.getPublished();
			MetaIdentifierHolder createdBy = dagExec.getCreatedBy();
			String createdOn = dagExec.getCreatedOn();
			String[] tags = dagExec.getTags();
			String active = dagExec.getActive();
			List<MetaIdentifierHolder> appInfo = dagExec.getAppInfo();
			baseEntity.setId(id);
			baseEntity.setUuid(uuid);
			baseEntity.setVersion(version);
			baseEntity.setName(name);
			baseEntity.setDesc(desc);
			baseEntity.setCreatedBy(createdBy);
			baseEntity.setCreatedOn(createdOn);
			baseEntity.setPublished(published);
			baseEntity.setTags(tags);
			baseEntity.setActive(active);
			baseEntity.setAppInfo(appInfo);
			baseEntityList.add(baseEntity);
		}
		return baseEntityList;
	}
*/
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

	public DagExec setStageOnHold(String uuid, String version, String stageId) throws Exception {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		DagExec dagExec=null;		
		if (appUuid != null) {
			if(StringUtils.isBlank(version)) {
				//dagExec = iDagExec.findOneByUuidAndVersion(appUuid,uuid,version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			}
			else {
				//dagExec = findLatestByUuid(uuid);
				dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
			}
		} else {
			if(StringUtils.isBlank(version)){
				//dagExec = iDagExec.findOneByUuidAndVersion(appUuid,uuid,version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			}
			else {
				//dagExec = findLatestByUuid(uuid);
				dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
			}
		}
		
		for(int i=0;i<dagExec.getStages().size();i++){
			if(dagExec.getStages().get(i).getStageId().equals(stageId)){
				Status stageOnHoldStatus = new Status(Status.Stage.OnHold, new Date());
				List<Status> stageStatusList = dagExec.getStages().get(i).getStatusList();
				stageStatusList.remove(stageOnHoldStatus);
				stageStatusList.add(stageOnHoldStatus);
				dagExec.getStages().get(i).setStatusList(stageStatusList);
				for(int j=0;j<dagExec.getStages().get(i).getTasks().size();j++){
					int lastStatus = dagExec.getStages().get(i).getTasks().get(j).getStatusList().size() - 1;
					if(dagExec.getStages().get(i).getTasks().get(j).getStatusList().get(lastStatus).getStage().equals(Status.Stage.NotStarted)) {
						logger.info("Task not started.");//raise code 
						throw new Exception("Task not started.");
					}else{
						Status taskOnHoldStatus = new Status(Status.Stage.OnHold, new Date());
						List<Status> taskStatusList =dagExec.getStages().get(i).getTasks().get(j).getStatusList();
						taskStatusList.remove(taskOnHoldStatus);
						taskStatusList.add(taskOnHoldStatus);
						dagExec.getStages().get(i).getTasks().get(j).setStatusList(taskStatusList);
					}
						
				}
			}
		}		
		//return iDagExec.save(dagExec);
		return (DagExec) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
	}
	
	public DagExec setStageFailed(String uuid, String version, String stageId) throws JsonProcessingException, JSONException, ParseException {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		DagExec dagExec=null;
		List<Status> dagStatusList = null;
		Status failedStatus = new Status(Status.Stage.Failed, new Date());
		
		if (appUuid != null) {
			if(StringUtils.isBlank(version)){
				//dagExec = iDagExec.findOneByUuidAndVersion(appUuid,uuid,version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			}
			else {
				//dagExec = findLatestByUuid(uuid);
				dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
			}
		} else {
			if(StringUtils.isBlank(version)){
				//dagExec = iDagExec.findOneByUuidAndVersion(appUuid,uuid,version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			}
			else {
				//dagExec = findLatestByUuid(uuid);
				dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
			}
		}
		
		for(int i=0;i<dagExec.getStages().size();i++){
			if(dagExec.getStages().get(i).getStageId().equals(stageId)){
				List<Status> stageStatusList = dagExec.getStages().get(i).getStatusList();
				if (!Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.NotStarted) 
						&& !Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.InProgress)) {
					logger.info("Stage can't fail ");
					continue;
				}
					
				if (Helper.getLatestStatus(stageStatusList).getStage().equals(Status.Stage.Failed)) {
					stageStatusList.remove(stageStatusList.size()-1);
				}
				stageStatusList.add(failedStatus);
				dagStatusList = dagExec.getStatusList();
				if (Helper.getLatestStatus(dagStatusList).getStage().equals(Status.Stage.Failed)) {
					dagStatusList.remove(dagStatusList.size()-1);
				}
				dagStatusList.add(failedStatus);
			}
		}		
		//return iDagExec.save(dagExec);
		return (DagExec) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
	}
	
	public DagExec setStageResume(String uuid, String version, String stageId) throws Exception {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		DagExec dagExec=null;		
		if (appUuid != null) {
			if(StringUtils.isBlank(version)){
				//dagExec = iDagExec.findOneByUuidAndVersion(appUuid,uuid,version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			}
			else {
				//dagExec = findLatestByUuid(uuid);
				dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
			}
		} else {
			if(StringUtils.isBlank(version)){
				//dagExec = iDagExec.findOneByUuidAndVersion(appUuid,uuid,version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			}
			else {
				//dagExec = findLatestByUuid(uuid);
				dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
			}
		}
		int lastDagStatus = dagExec.getStatusList().size() - 1;
		if(dagExec.getStatusList().get(lastDagStatus).toString().toLowerCase().equals(Status.Stage.OnHold.toString().toLowerCase())) {
			
		}else {
			for(int i=0;i<dagExec.getStages().size();i++){
				if(dagExec.getStages().get(i).getStageId().equals(stageId)){
					Status stageOnHoldStatus = new Status(Status.Stage.Resume, new Date());
					List<Status> stageStatusList = dagExec.getStages().get(i).getStatusList();
					stageStatusList.remove(stageOnHoldStatus);
					stageStatusList.add(stageOnHoldStatus);
					dagExec.getStages().get(i).setStatusList(stageStatusList);
					
					for(int j=0;j<dagExec.getStages().get(i).getTasks().size();j++){
						int lastStatus = dagExec.getStages().get(i).getTasks().get(j).getStatusList().size() - 1;
						if(dagExec.getStages().get(i).getTasks().get(j).getStatusList().get(lastStatus).getStage().equals(Status.Stage.NotStarted)) {
							logger.info("Task not started.");
							throw new Exception("Task not started.");
						}else{
							Status taskOnHoldStatus = new Status(Status.Stage.Resume, new Date());
							List<Status> taskStatusList =dagExec.getStages().get(i).getTasks().get(j).getStatusList();
							taskStatusList.remove(taskOnHoldStatus);
							taskStatusList.add(taskOnHoldStatus);
							dagExec.getStages().get(i).getTasks().get(j).setStatusList(taskStatusList);
						}
							
					}
				}
			}
			//return iDagExec.save(dagExec);
			return (DagExec) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
		}
		return null;
	}
	
	public String setStatusList(String uuid, String version, String stageId, String taskId, String status) throws JsonProcessingException, Exception {
		if (!StringUtils.isBlank(status)) {
			if (!StringUtils.isBlank(taskId) && !StringUtils.isBlank(stageId)) {
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.Resume.toString().toLowerCase())){
					return objectWriter.writeValueAsString(setTaskResume(uuid, version, stageId, taskId));
				}
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.OnHold.toString().toLowerCase())){
					return objectWriter.writeValueAsString(setTaskOnHold(uuid, version, stageId, taskId));
				}
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.Killed.toString().toLowerCase())) {
					return objectWriter.writeValueAsString(kill(uuid, version, stageId, taskId));
				}
			}else
				logger.info("can not perform the "+status.toUpperCase()+" operation");
			if (!StringUtils.isBlank(stageId) && StringUtils.isBlank(taskId)) {
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.Resume.toString().toLowerCase())){
					return objectWriter.writeValueAsString(setStageResume(uuid, version, stageId));
				}
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.OnHold.toString().toLowerCase())){
					return objectWriter.writeValueAsString(setStageOnHold(uuid, version, stageId));
				}
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.Killed.toString().toLowerCase())){
					return objectWriter.writeValueAsString(kill(uuid, version, stageId, null));
				}
			}else
				logger.info("can not perform the "+status.toUpperCase()+" operation");
			if (StringUtils.isBlank(stageId) && StringUtils.isBlank(taskId)) {
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.Resume.toString().toLowerCase())){
					return objectWriter.writeValueAsString(setDAGResume(uuid, version));
				}
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.OnHold.toString().toLowerCase())){
					return objectWriter.writeValueAsString(setDAGOnHold(uuid, version));
				}
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.Killed.toString().toLowerCase())) {
					return objectWriter.writeValueAsString(kill(uuid, version,null,null));
				}
			}else
				logger.info("can not perform the "+status.toUpperCase()+" operation");
		} else {
			logger.info("Empty status, can not perform the operation.");
		}
		return null;
	}
	
	public DagExec setDAGResume(String uuid, String version) throws Exception {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
				DagExec dagExec=null;		
				if (appUuid != null) {
					if(StringUtils.isBlank(version)){
						//dagExec = iDagExec.findOneByUuidAndVersion(appUuid,uuid,version);
						dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
					}
					else {
						//dagExec = findLatestByUuid(uuid);
						dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
					}
				} else {
					if(StringUtils.isBlank(version)){
						//dagExec = iDagExec.findOneByUuidAndVersion(appUuid,uuid,version);
						dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
					}
					else{
						//dagExec = findLatestByUuid(uuid);
						dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
					}
				}
			Status stageStatus = new Status(Status.Stage.Resume, new Date());
			List<Status> dagStatusList = dagExec.getStatusList();
			dagStatusList.remove(stageStatus);
			dagStatusList.add(stageStatus);
		for (int i = 0; i < dagExec.getStages().size(); i++) {
				Status stageOnHoldStatus = new Status(Status.Stage.Resume, new Date());
				List<Status> stageStatusList = dagExec.getStages().get(i).getStatusList();
				stageStatusList.remove(stageOnHoldStatus);
				stageStatusList.add(stageOnHoldStatus);
				dagExec.getStages().get(i).setStatusList(stageStatusList);
				for (int j = 0; j < dagExec.getStages().get(i).getTasks().size(); j++) {
					int lastDag = dagExec.getStages().get(i).getTasks().get(j).getStatusList().size() - 1;
					if (dagExec.getStages().get(i).getTasks().get(j).getStatusList().get(lastDag).getStage()
							.equals(Status.Stage.NotStarted)) {
						throw new Exception("Task not started.");
					} else {
						Status taskOnHoldStatus = new Status(Status.Stage.Resume, new Date());
						List<Status> taskStatusList = dagExec.getStages().get(i).getTasks().get(j).getStatusList();
						taskStatusList.remove(taskOnHoldStatus);
						taskStatusList.add(taskOnHoldStatus);
						dagExec.getStages().get(i).getTasks().get(j).setStatusList(taskStatusList);
					}

				}
		}
		//return iDagExec.save(dagExec);
		return (DagExec) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
	}
	
	public DagExec setDAGOnHold(String uuid, String version) throws Exception {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		DagExec dagExec=null;		
		if (appUuid != null) {
			if(StringUtils.isBlank(version)){
				//dagExec = iDagExec.findOneByUuidAndVersion(appUuid,uuid,version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			}
			else {
				//dagExec = findLatestByUuid(uuid);
				dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
			}
		} else {
			if(StringUtils.isBlank(version)){
				//dagExec = iDagExec.findOneByUuidAndVersion(appUuid,uuid,version);
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			}
			else {
				//dagExec = findLatestByUuid(uuid);
				dagExec = (DagExec) commonServiceImpl.getLatestByUuid(uuid, MetaType.dagExec.toString());
			}
		}
		Status stageStatus = new Status(Status.Stage.Resume, new Date());
		List<Status> dagStatusList = dagExec.getStatusList();
		dagStatusList.remove(stageStatus);
		dagStatusList.add(stageStatus);
		for (int i = 0; i < dagExec.getStages().size(); i++) {
			
				Status stageOnHoldStatus = new Status(Status.Stage.OnHold, new Date());
				List<Status> stageStatusList = dagExec.getStages().get(i).getStatusList();
				stageStatusList.remove(stageOnHoldStatus);
				stageStatusList.add(stageOnHoldStatus);
				dagExec.getStages().get(i).setStatusList(stageStatusList);
				for (int j = 0; j < dagExec.getStages().get(i).getTasks().size(); j++) {
					int lastDag = dagExec.getStages().get(i).getTasks().get(j).getStatusList().size() - 1;
					if (dagExec.getStages().get(i).getTasks().get(j).getStatusList().get(lastDag).getStage()
							.equals(Status.Stage.NotStarted)) {
						throw new Exception("Task not started.");
					} else {
						Status taskOnHoldStatus = new Status(Status.Stage.OnHold, new Date());
						List<Status> taskStatusList = dagExec.getStages().get(i).getTasks().get(j).getStatusList();
						taskStatusList.remove(taskOnHoldStatus);
						taskStatusList.add(taskOnHoldStatus);
						dagExec.getStages().get(i).getTasks().get(j).setStatusList(taskStatusList);
					}
				}
		}
		//return iDagExec.save(dagExec);
		return (DagExec) commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
	}
}

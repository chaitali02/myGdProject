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
package com.inferyx.framework.service2;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.dao.IDagDao;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSetHolder;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.SimulateExec;
import com.inferyx.framework.domain.Stage;
import com.inferyx.framework.domain.StageExec;
import com.inferyx.framework.domain.StageRef;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Task;
import com.inferyx.framework.domain.TaskExec;
import com.inferyx.framework.domain.TaskOperator;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.factory2.ExecFactory2;
import com.inferyx.framework.factory2.SystemOperatorFactory2;
import com.inferyx.framework.operator.IOperator;
import com.inferyx.framework.register.GraphRegister;
import com.inferyx.framework.service.ApplicationServiceImpl;
import com.inferyx.framework.service.BatchExecServiceImpl;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.CustomOperatorServiceImpl;
import com.inferyx.framework.service.DagExecServiceImpl;
import com.inferyx.framework.service.DagServiceImpl;
import com.inferyx.framework.service.DataQualGroupServiceImpl;
import com.inferyx.framework.service.DataQualServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.GraphServiceImpl;
import com.inferyx.framework.service.LoadServiceImpl;
import com.inferyx.framework.service.MapServiceImpl;
import com.inferyx.framework.service.MessageStatus;
import com.inferyx.framework.service.ModelServiceImpl;
import com.inferyx.framework.service.ProfileGroupServiceImpl;
import com.inferyx.framework.service.ProfileServiceImpl;
import com.inferyx.framework.service.ReconGroupServiceImpl;
import com.inferyx.framework.service.ReconServiceImpl;
import com.inferyx.framework.service.RegisterService;
import com.inferyx.framework.service.RuleGroupServiceImpl;
import com.inferyx.framework.service.RuleServiceImpl;
import com.inferyx.framework.service.RunDagServiceImpl;
import com.inferyx.framework.service.SecurityServiceImpl;

@Service
public class DagServiceImpl2 {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IDagDao iDagDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private BatchExecServiceImpl btchServ;
	@Autowired
	private DagExecServiceImpl dagExecServiceImpl;
	@Autowired
	private MapServiceImpl mapServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private ThreadPoolTaskExecutor dagExecutor;
	@Resource(name="taskThreadMap")
	ConcurrentHashMap<String, FutureTask<String>> taskThreadMap;
	@Autowired
	private SessionHelper sessionHelper;
	private RunMode runMode;
	@Autowired
	private Helper helper;
	@Autowired
	private ExecFactory2 execFactory2;
	@Autowired
	private SystemOperatorFactory2 systemOperatorFactory2;
	
	static final Logger logger = Logger.getLogger(DagServiceImpl.class);
	ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
	
	public RunMode getRunMode() {
		return runMode;
	}

	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

	/**
	 * Overloaded submitDag with input as dagRef
	 * 
	 * @param dagRef
	 * @return
	 * @throws Exception
	 */
	public MetaIdentifierHolder submitDag(MetaIdentifier dagRef, ExecParams execParams, RunMode runMode) throws Exception {
		Dag dag = null;
		if (dagRef == null) {
			logger.info("No ref object. Aborting submitDAG");
			return null;
		}
		if (dagRef.getVersion() == null) {
			dag = (Dag) commonServiceImpl.getLatestByUuid(dagRef.getUuid(), MetaType.dag.toString());
		} else {
			dag = (Dag) commonServiceImpl.getOneByUuidAndVersion(dagRef.getUuid(), dagRef.getVersion(), MetaType.dag.toString());
		}
		return submitDag(dag, null, execParams, runMode);
	}

	/**
	 * To handle old style submitDag
	 * 
	 * @param dag
	 * @return
	 * @throws Exception
	 */
	public MetaIdentifierHolder submitDag(@RequestBody Dag dag, RunMode runMode) throws Exception {
		return submitDag(dag, null, null, runMode);
	}

	/**
	 * Used when trying to run a particular version of DAG meta. If version is not
	 * specified then it may run with the latest version for the DAG UUID. Picks up
	 * the DAG with the supplied UUID and version and submits the DAG. Also accepts
	 * an execParam to run
	 * 
	 * @param uuid
	 * @param version
	 * @param execParams
	 * @return
	 * @throws Exception
	 */
	public MetaIdentifierHolder submitDag(String uuid, String version, ExecParams execParams, String metaType, RunMode runMode)
			throws Exception {
		Dag dag = null;
		DagExec dagExec = null;
		if (StringUtils.isBlank(uuid)) {
			logger.info("No DAG UUID. Aborting submitDAG.");
			throw new Exception("No DAG UUID. Aborting submitDAG.");			
		}
		if (StringUtils.isBlank(version)) {
			return submitDag(uuid, execParams, metaType, runMode);
		} else if (StringUtils.isBlank(metaType) || metaType.equalsIgnoreCase(MetaType.dag.toString())) {
			dag = (Dag) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dag.toString());
		} else if (metaType.equalsIgnoreCase(MetaType.dagExec.toString())){
			//dagExec = dagExecServiceImpl.findOneByUuidAndVersion(uuid, version);
			dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString());
			dag = (Dag) commonServiceImpl.getOneByUuidAndVersion(dagExec.getDependsOn().getUuid(), dagExec.getDependsOn().getVersion(), MetaType.dag.toString());
		} else {
			logger.info("Invalid metaType. Aborting submitDAG.");
			throw new Exception("Invalid metaType. Aborting submitDAG.");			
		}
		if (dag == null) {
			logger.info("No referenced DAG. Aborting submitDAG.");
			throw new Exception("No referenced DAG. Aborting submitDAG.");			
		}
		return submitDag(dag, dagExec, execParams, runMode);
	}

	public List<MetaIdentifierHolder> submitDagWithParamset(String dagUuid, String version, ExecParams execParams, RunMode runMode)
			throws Exception {
		Dag dag = null;
		if (StringUtils.isBlank(dagUuid)) {
			logger.info("No DAG UUID. Aborting submitDAG");
			return null;
		}
		if (StringUtils.isBlank(version)) {
			return submitDagWithParamset(dag, execParams, runMode);
		} else {
			dag = (Dag) commonServiceImpl.getOneByUuidAndVersion(dagUuid, version, MetaType.dag.toString());
		}
		if (dag == null) {
			logger.info(" No referenced DAG. Aborting submitDAG. ");
			return null;
		}
		return submitDagWithParamset(dag, execParams, runMode);
	}

	/**
	 * Used when only DAGUUID is available. It picks up the latest version of DAG
	 * meta for the UUID and submits the DAG. Also accepts an execParam to run
	 * 
	 * @param dagUuid
	 * @param execParams
	 * @return
	 * @throws Exception
	 */
	public MetaIdentifierHolder submitDag(String dagUuid, ExecParams execParams, String inputType, RunMode runMode) throws Exception {
		Dag dag = null;
		DagExec dagExec = null;
		if (dagUuid == null) {
			logger.info("No DAG UUID. Aborting submitDAG");
			return null;
		}
		if (inputType == null || inputType.equalsIgnoreCase(MetaType.dag.toString())) {
			dag = (Dag) commonServiceImpl.getLatestByUuid(dagUuid, MetaType.dag.toString());
		} else {
			//dagExec = dagExecServiceImpl.findLatestByUuid(dagUuid);
			dagExec = (DagExec) commonServiceImpl.getLatestByUuid(dagUuid, MetaType.dagExec.toString());
			if (dagExec == null) {
				logger.info(" No referenced DAGExec. Aborting submitDAG. ");
				return null;
			}
			dag = (Dag) commonServiceImpl.getLatestByUuid(dagExec.getDependsOn().getUuid(), MetaType.dag.toString());
		}
		if (dag == null) {
			logger.info(" No referenced DAG. Aborting submitDAG. ");
			return null;
		}
		return submitDag(dag, dagExec, execParams, runMode);
	}

	public List<MetaIdentifierHolder> submitDagWithParamset(@RequestBody Dag dag, ExecParams execParams, RunMode runMode)
			throws Exception {
		MetaIdentifierHolder mHolder = new MetaIdentifierHolder();
		MetaIdentifier mIdentifier = new MetaIdentifier();

		if (execParams == null) {
			execParams = new ExecParams();
			execParams.setRefKeyList(new ArrayList<>());
		}

		DagExec dagExec = null;
		List<DagExec> dagExecList = new ArrayList<>();
		List<MetaIdentifierHolder> dagExecMetas = new ArrayList<>();

		if (execParams != null) {
			if (execParams.getParamInfo() != null && !execParams.getParamInfo().isEmpty()) {
				for (ParamSetHolder paramSetHolder : execParams.getParamInfo()) {
					execParams.setParamSetHolder(paramSetHolder);
					// Create object
					dagExec = createDAGExec(dag, execParams);
					dagExec.setExecParams(execParams); // Set execParams in DAGExec
					commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
					//dagExecServiceImpl.save(dagExec);
	
					// Parse to create SQL
					dagExec = parseDagExec(dag, dagExec);
	
					commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
					//dagExecServiceImpl.save(dagExec);
					dagExecList.add(dagExec);
				}
			} else if (execParams.getParamListInfo() != null && !execParams.getParamListInfo().isEmpty()) {
				for (ParamListHolder paramListHolder : execParams.getParamListInfo()) {
					execParams.setParamListHolder(paramListHolder);
					// Create object
					dagExec = createDAGExec(dag, execParams);
					dagExec.setExecParams(execParams); // Set execParams in DAGExec
					commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
					//dagExecServiceImpl.save(dagExec);
	
					// Parse to create SQL
					dagExec = parseDagExec(dag, dagExec);
	
					commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
					//dagExecServiceImpl.save(dagExec);
					dagExecList.add(dagExec);
				}
			}
		} 

		for (@SuppressWarnings("unused") DagExec createdDagExec : dagExecList) {
			// Execute the object
			dagExec = btchServ.createDagExecBatch(dag, dagExec, runMode);

			HashMap<String, Object> respMapObj = new HashMap<String, Object>();
			respMapObj.put("dagExecUuid", dagExec.getUuid());
			respMapObj.put("dagExecVersion", dagExec.getVersion());
			mIdentifier.setType(MetaType.dagExec);
			mIdentifier.setUuid(dagExec.getUuid());
			mIdentifier.setVersion(dagExec.getVersion());
			mHolder.setRef(mIdentifier);
			dagExecMetas.add(mHolder);
		}
		return dagExecMetas;
	}
	
	public void kill(String uuid, String version, String stageId, String taskId) throws JsonProcessingException {
		btchServ.killTask(uuid, version, stageId, taskId);
	}

	/**
	 * Submit DAG with full DAG body and execParams. A reference to the DAG should
	 * already exist in meta, though it shall act more as a template rather than an
	 * exact replica of what is sent here. Also accepts an execParam to run
	 * 
	 * @param dag
	 * @param execParams
	 * @return
	 * @throws Exception
	 */
	public MetaIdentifierHolder submitDag(@RequestBody Dag dag, DagExec dagExec, ExecParams execParams, RunMode runMode)
			throws Exception {
		MetaIdentifierHolder mHolder = new MetaIdentifierHolder();
		MetaIdentifier mIdentifier = new MetaIdentifier();
		RunDagServiceImpl parseRunDagServiceImpl = new RunDagServiceImpl();

		if (execParams == null) {
			execParams = new ExecParams();
			execParams.setRefKeyList(new ArrayList<>());
		}

		if (dagExec == null) {
			// Create object
			dagExec = createDAGExec(dag, execParams);
			commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
			//dagExecServiceImpl.save(dagExec);
		}
		
		//Check if DAG is ready for execution
		Status.Stage stg = Helper.getLatestStatus(dagExec.getStatusList()).getStage();		
		if (stg.equals(Status.Stage.InProgress) || stg.equals(Status.Stage.Completed) || stg.equals(Status.Stage.OnHold)) {
			logger.info("DAGExec is already in InProgress/Completed/OnHold status. Aborting execution.");
			throw new Exception("DAGExec is already in InProgress/Completed/OnHold status. Aborting execution.");
		}
					
		// Populate ParseRunDagServiceImpl
		parseRunDagServiceImpl.setBtchServ(btchServ);
		parseRunDagServiceImpl.setDag(dag);
		parseRunDagServiceImpl.setDagExec(dagExec);
		parseRunDagServiceImpl.setDagExecServiceImpl(dagExecServiceImpl);
//		parseRunDagServiceImpl.setDagServiceImpl(this);
		parseRunDagServiceImpl.setTaskThreadMap(taskThreadMap);
		parseRunDagServiceImpl.setCommonServiceImpl(commonServiceImpl);
		parseRunDagServiceImpl.setSessionContext(sessionHelper.getSessionContext());
		parseRunDagServiceImpl.setRunMode(runMode);
		
		FutureTask<String> futureTask = new FutureTask<String>(parseRunDagServiceImpl);
		dagExecutor.execute(futureTask);
		logger.info("Thread watch : DagExec : " + dagExec.getUuid() + " started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
		taskThreadMap.put("Dag_" + dagExec.getUuid(), futureTask);
		Thread.sleep(1000);

		/*if (dagExec == null) {
			// Create object
			dagExec = createDAGExec(dag, execParams);
			dagExec.setExecParams(execParams); // Set execParams in DAGExec
			dagExecServiceImpl.save(dagExec);
			// Parse to create SQL
			dagExec = parseDagExec(dag, dagExec);
			dagExecServiceImpl.save(dagExec);
		}*/

		// Execute the object
		//dagExec = btchServ.createDagExecBatch(dag, dagExec);

		HashMap<String, Object> respMapObj = new HashMap<String, Object>();
		respMapObj.put("dagExecUuid", dagExec.getUuid());
		respMapObj.put("dagExecVersion", dagExec.getVersion());
		mIdentifier.setType(MetaType.dagExec);
		mIdentifier.setUuid(dagExec.getUuid());
		mIdentifier.setVersion(dagExec.getVersion());
		mHolder.setRef(mIdentifier);
		return mHolder;
	}


	public DagExec createDAGExec(Dag dag, ExecParams execParams) throws JsonProcessingException {
		DagExec dagExec = new DagExec(dag);
		dagExec.setName(dag.getName());
		dagExec.setBaseEntity();
		MetaIdentifier dagRef = new MetaIdentifier(MetaType.dag, dag.getUuid(), dag.getVersion());
		// Loop in Stages
		List<Stage> dagStages = dag.getStages();
		List<String> dependsOn = dagStages.get(0).getDependsOn();
		List<StageExec> dagExecStages = createDagExecStages(dagStages, dependsOn, dagRef, execParams);
		dagExec.setStages(DagExecUtil.convertToStageList(dagExecStages));
		// dagExec.setName("sys_" + dagExec.getUuid());
		dagExec.setAppInfo(dag.getAppInfo());
		// Set DagExec Status
		dagExec.setStatusList(DagExecUtil.createInitialStatus(dagExec.getStatusList()));
		return dagExec;
	}

	/*
	 * In this method we are getting the dag stages and Making a copy of each stage
	 * for dagexec stage Creating a list of stages and then returning to called
	 * dagexec The task list for each stage is prepared by a calling function
	 */
	private List<StageExec> createDagExecStages(List<Stage> dagStages, List<String> dependson, MetaIdentifier dagRef,
			ExecParams execParams) throws JsonProcessingException {
		List<StageExec> stageExecs = new ArrayList<>();
		List<String> activeStages = null;
		if (execParams != null) {
			activeStages = execParams.getStageInfo();
		}
		for (Stage indvStg : dagStages) {
			// Set common attr for stage
			StageExec stageExec = new StageExec(indvStg);
			@SuppressWarnings("unused")
			StageRef stageRef = new StageRef(dagRef, indvStg.getDependsOn(), indvStg.getStageId(), null);

			if (activeStages != null && !activeStages.isEmpty() && !activeStages.contains(indvStg.getStageId())) {
				// Set stage status to inactive
				stageExec.setStatusList(DagExecUtil.createInitialInactiveStatus(stageExec.getStatusList()));
				continue;
			} else {
				// Set stage status to Not Started
				stageExec.setStatusList(DagExecUtil.createInitialStatus(stageExec.getStatusList()));
			}

			// Add tasks for current stage
			List<Task> dagTasks = indvStg.getTasks();
			List<TaskExec> dagExectasks = createDagExecTasks(dagTasks, indvStg.getDependsOn(), indvStg, dagRef,
					execParams);
			stageExec.setTasks(DagExecUtil.convertToTaskList(dagExectasks));
			stageExecs.add(stageExec);

		}
		return stageExecs;
	}

	@SuppressWarnings("unused")
	private List<TaskExec> createDagExecTasks(List<Task> dagTasks, List<String> dependsOn, Stage indvStg,
			MetaIdentifier dagRef, ExecParams execParams) throws JsonProcessingException {
		List<TaskExec> taskExecs = new ArrayList<>();
		java.util.Map<String, MetaIdentifier> refKeys = null;
		MetaIdentifier mapRef = null, sourceRef = null, targetRef = null, sourceAttrRef = null, condRef = null,
				targetAttrRef = null, miHolderRef = null, groupByRef = null, loadRef = null, secondaryDagRef = null;
		MetaIdentifier execIdentifier = null;

		// Convert refKeyList to HashMap
		List<MetaIdentifier> inputRefKeyList = null;
		if (execParams == null) {
			inputRefKeyList = execParams.getRefKeyList();
			execParams = new ExecParams();
		} else {
			execParams.setRefKeyList(new ArrayList<>());
		}
		java.util.Map<String, MetaIdentifier> inputRefKeys = new HashMap<>();
		if (inputRefKeyList != null && inputRefKeyList.size() > 0) {
			for (MetaIdentifier inputRefKey : inputRefKeyList) {
				inputRefKeys.put(inputRefKey.getUuid(), inputRefKey);
			}
		}
		// Iterate the task and set it
		for (Task indvTask : dagTasks) {
			TaskExec taskExec = new TaskExec(indvTask);
			taskExec.getOperators().get(0).setOperatorParams(indvTask.getOperators().get(0).getOperatorParams());
			refKeys = DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()); // Get
																						// refKeys
																						// placeholder
																						// for
			// refKey preparation

			// Set Tasks status to Not Started
			taskExec.setStatusList(DagExecUtil.createInitialStatus(taskExec.getStatusList()));

			// Traverse task & Populate RefKeys
			if (indvTask.getOperators() != null && indvTask.getOperators().get(0).getOperatorInfo() != null) {
				BaseExec baseExec = execFactory2.getExec(indvTask.getOperators().get(0).getOperatorInfo().getRef().getType(), indvTask.getOperators().get(0).getOperatorInfo().getRef());
				taskExec.getOperators().get(0).setOperatorInfo(baseExec.getMetaIdentifierHolder(indvTask.getOperators().get(0).getOperatorInfo().getRef().getType()));
				try {
					commonServiceImpl.save(helper.getExecType(indvTask.getOperators().get(0).getOperatorInfo().getRef().getType()).toString(), baseExec);
				} catch (JSONException | ParseException e) {
					e.printStackTrace();
				}
			}
			
			execParams.setRefKeyList(DagExecUtil.convertRefKeyMapToList(refKeys));

			taskExecs.add(taskExec);
		}
		// For GC - START
		mapRef = null;
		sourceRef = null;
		targetRef = null;
		sourceAttrRef = null;
		condRef = null;
		targetAttrRef = null;
		miHolderRef = null;
		groupByRef = null;
		loadRef = null;
		// For GC - END
		return taskExecs;

	}

	/**
	 * 
	 * @param dag
	 * @param dagExec
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unused", "unlikely-arg-type", "unchecked" })
	public DagExec parseDagExec(Dag dag, DagExec dagExec) throws Exception {

		mapServiceImpl.setRunMode(runMode);
		if (dagExec == null) {
			logger.info("Nothing to parse. Aborting parseDagExec");
			return null;
		}
		ExecParams execParams = dagExec.getExecParams();
		if (execParams == null) {
			execParams = new ExecParams();
			execParams.setRefKeyList(new ArrayList<>());
			dagExec.setExecParams(execParams);
		}
		List<StageExec> dagExecStgs = DagExecUtil.castToStageExecList(dagExec.getStages());

		List<String> datapodList = new ArrayList<String>();
		HashMap<String, String> otherParams = null;
		if (execParams.getOtherParams() == null) {
			otherParams = new HashMap<>();
			execParams.setOtherParams(otherParams);
		}
		// Get the dag - START
		MetaIdentifier dagRef = new MetaIdentifier(MetaType.dag, dagExec.getUuid(), dagExec.getVersion());
		// Dag dag = (Dag) daoRegister.getRefObject(dagRef);
		// Get the dag - END
		for (StageExec indvDagExecStg : dagExecStgs) {

			List<TaskExec> dagExecTasks = DagExecUtil.castToTaskExecList(indvDagExecStg.getTasks());
			Stage stage = DagExecUtil.getStageFromDag(dag, indvDagExecStg.getStageId());
			if (indvDagExecStg != null && indvDagExecStg.getStatusList() != null
					&& indvDagExecStg.getStatusList().contains(Status.Stage.Inactive)) {
				continue; // If inactive stage then move to next stage (don't
							// consider inactive stage)
			}
			for (TaskExec indvExecTask : dagExecTasks) {

				otherParams = execParams.getOtherParams();
				Task indvTask = DagExecUtil.getTaskFromStage(stage, indvExecTask.getTaskId());
				logger.info("Parsing task : " + indvTask.getTaskId() + ":" + indvTask.getName() + ":" + indvTask.getOperators().get(0).getOperatorInfo().getRef().getType());
				logger.info(" OtherParams : " + otherParams);
				MetaIdentifier ref = indvTask.getOperators().get(0).getOperatorInfo().getRef();
				List<TaskOperator> operatorList = new ArrayList<>();
				TaskOperator operator = new TaskOperator();
				MetaIdentifierHolder operatorInfo = new MetaIdentifierHolder();
				operator.setOperatorInfo(operatorInfo);
				operatorList.add(operator);
				indvExecTask.setOperators(operatorList);
				StringBuilder builder = null;
				if (indvTask.getOperators().get(0).getOperatorParams() != null
						&& indvTask.getOperators().get(0).getOperatorParams().containsKey(MetaType.paramset.toString())
						&& execParams.getParamSetHolder() == null) {
					List<ParamSetHolder> paramSetHolderList = (List<ParamSetHolder>) indvTask.getOperators().get(0)
							.getOperatorParams().get(MetaType.paramset.toString());
					List<ParamSetHolder> paramSetHolders = new ArrayList<>();
					ObjectMapper mapper = new ObjectMapper();
					for(Object obj : paramSetHolderList) {
						paramSetHolders.add(mapper.convertValue(obj, ParamSetHolder.class));
					}
					if (paramSetHolderList != null && !paramSetHolderList.isEmpty()) {
						execParams.setParamInfo(paramSetHolders);
						execParams.setParamSetHolder(paramSetHolders.get(0));
					}
				}
				if (indvTask.getOperators().get(0).getOperatorParams() != null
						&& indvTask.getOperators().get(0).getOperatorParams().containsKey(MetaType.paramlist.toString())
						&& execParams.getParamListHolder() == null) {
					List<ParamListHolder> paramListHolderList = (List<ParamListHolder>) indvTask.getOperators().get(0).getOperatorParams().get(MetaType.paramlist.toString());
					List<ParamListHolder> paramListHolders = new ArrayList<>();
					ObjectMapper mapper = new ObjectMapper();
					for(Object obj : paramListHolderList) {
						paramListHolders.add(mapper.convertValue(obj, ParamListHolder.class));
					}
					if (paramListHolderList != null && !paramListHolderList.isEmpty()) {
						execParams.setParamListInfo(paramListHolders);
						execParams.setParamListHolder(paramListHolders.get(0));
					}
				}
				operator.setOperatorParams(indvTask.getOperators().get(0).getOperatorParams());
				// Have few parts in common area
				java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
						.convertRefKeyListToMap(execParams.getRefKeyList());
				BaseExec baseExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(indvExecTask.getOperators().get(0).getOperatorInfo().getRef().getUuid(), 
																						indvExecTask.getOperators().get(0).getOperatorInfo().getRef().getVersion(), 
																						indvExecTask.getOperators().get(0).getOperatorInfo().getRef().getType().toString());

				try {
					// If conditions with parse goes here - START
					IOperator operator2 = systemOperatorFactory2.getOperator(indvTask.getOperators().get(0).getOperatorInfo().getRef().getType());
					baseExec = operator2.parse(baseExec, execParams, runMode);
					commonServiceImpl.save(indvExecTask.getOperators().get(0).getOperatorInfo().getRef().getType().toString(), baseExec);
					
					execParams.setOtherParams((HashMap<String, String>)Helper.mergeMap(otherParams, execParams.getOtherParams()));
					// If conditions with parse goes here - END	
					logger.info(" otherParams : " + otherParams);
					logger.info(" execParams.getOtherParams() : " + execParams.getOtherParams());
					baseExec.setRefKeyList(execParams.getRefKeyList());
					if (baseExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
						throw new Exception();
					}
				} catch (Exception e) {
					Status failedStatus = new Status(Status.Stage.Failed, new Date());
					List<Status> statusList = indvExecTask.getStatusList();
					if (statusList == null) {
						statusList = new ArrayList<Status>();
					}
					statusList.remove(failedStatus);
					statusList.add(failedStatus);
					e.printStackTrace();
					String message = null;
					try {
						message = e.getMessage();
					}catch (Exception e2) {
						// TODO: handle exception
					}
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Pipeline execution failed.");
					throw new Exception((message != null) ? message : "Pipeline execution failed.");
				} finally {
					commonServiceImpl.save(helper.getExecType(ref.getType()).toString(), baseExec);
				}
				
				// Set Stage and Task status
				List<Status> statusList = new ArrayList<Status>();
				Status status = new Status(Status.Stage.NotStarted, new Date());
				statusList.add(status);
				indvExecTask.setStatusList(statusList);
			}
			indvDagExecStg.setTasks(DagExecUtil.convertToTaskList(dagExecTasks));
		}
		dagExec.setStages(DagExecUtil.convertToStageList(dagExecStgs));
		return dagExec;
	}

	public Dag findDagByDagExec(String dagExecUuid) throws JsonProcessingException {
		//DagExec dagExec = dagExecServiceImpl.findLatestByUuid(dagExecUuid);
		DagExec dagExec = (DagExec) commonServiceImpl.getLatestByUuid(dagExecUuid, MetaType.dagExec.toString());
		return (Dag) commonServiceImpl.getOneByUuidAndVersion(dagExec.getDependsOn().getUuid(), dagExec.getDependsOn().getVersion(), MetaType.dag.toString());
	}

	
	public void restart(String dagExecUuid,String dagExecVersion, RunMode runMode) throws Exception{
		DagExec dagExec=prepareDagExec(dagExecUuid, dagExecVersion,null,MetaType.dagExec.toString());
		submitDag(dagExec.getUuid(), dagExec.getVersion(),null,MetaType.dagExec.toString(), runMode);		
	}
	
	public DagExec prepareDagExec(String uuid, String version, ExecParams execParams, String metaType)throws Exception{
		DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, metaType);
		for(int i=0;i<dagExec.getStages().size();i++){
			if(Helper.getLatestStatus(dagExec.getStages().get(i).getStatusList()).equals(new Status(Status.Stage.Failed, new Date()))
					||Helper.getLatestStatus(dagExec.getStages().get(i).getStatusList()).equals(new Status(Status.Stage.Killed, new Date()))){
				for(int j=0;j<dagExec.getStages().get(i).getTasks().size();j++){
					if(Helper.getLatestStatus(dagExec.getStages().get(i).getTasks().get(j).getStatusList()).equals(new Status(Status.Stage.Failed, new Date()))
							||Helper.getLatestStatus(dagExec.getStages().get(i).getTasks().get(j).getStatusList()).equals(new Status(Status.Stage.Killed, new Date()))){
						TaskExec taskExec = dagExecServiceImpl.getTaskExec(dagExec,dagExec.getStages().get(i).getStageId(), dagExec.getStages().get(i).getTasks().get(j).getTaskId());
						commonServiceImpl.setMetaStatusForTask(dagExec, taskExec, Status.Stage.NotStarted,dagExec.getStages().get(i).getStageId(), dagExec.getStages().get(i).getTasks().get(j).getTaskId());
					}	
				}
			}
		}
		return dagExec;
	}
	
	@SuppressWarnings("unchecked")
	public List<BaseEntity> getDagTemplates(String type, String templateFlg) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException{
		List<BaseEntity> dagTemplateBE  = new ArrayList<>();
		
		List<Dag> dagTemplateList = (List<Dag>) commonServiceImpl.findAllLatest(MetaType.dag);
			for(Dag dag : dagTemplateList)
				if(dag.getTemplateFlg().equalsIgnoreCase(templateFlg))
					dagTemplateBE.add(dag);

		return commonServiceImpl.resolveBaseEntityList(commonServiceImpl.getBaseEntityList(dagTemplateBE));
	}
}
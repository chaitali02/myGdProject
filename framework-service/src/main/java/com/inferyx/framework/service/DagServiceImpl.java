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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.dao.IDagDao;
import com.inferyx.framework.domain.AttributeMap;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DashboardExec;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.IngestExec;
import com.inferyx.framework.domain.IngestGroupExec;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Notification;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSetHolder;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.ReportExec;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.SenderInfo;
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
import com.inferyx.framework.register.GraphRegister;

@Service
public class DagServiceImpl {
	
	private static final String GET = "get";
//	private static final String SET = "set";

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
	private LoadServiceImpl loadServiceImpl;
	@Autowired
	private DagExecServiceImpl dagExecServiceImpl;
	@Autowired
	private MapServiceImpl mapServiceImpl;
	@Autowired
	private RuleServiceImpl ruleServiceImpl;
	@Autowired
	private RuleGroupServiceImpl ruleGroupServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private DataQualServiceImpl dataQualServiceImpl;
	@Autowired
	private DataQualGroupServiceImpl dataQualGroupServiceImpl;
	@Autowired
	private ProfileServiceImpl profileServiceImpl;
	@Autowired
	private ProfileGroupServiceImpl profileGroupServiceImpl;
	@Autowired
	private ModelServiceImpl modelServiceImpl;
	@Autowired
	private ThreadPoolTaskExecutor dagExecutor;
	@Resource(name="taskThreadMap")
	ConcurrentHashMap<String, FutureTask<String>> taskThreadMap;
	@Autowired
	private SessionHelper sessionHelper;
	private RunMode runMode;
	@Autowired
	private ReconServiceImpl reconServiceImpl;
	@Autowired
	private ReconGroupServiceImpl reconGroupServiceImpl;
	@Autowired
	private GraphServiceImpl graphServiceImpl;
	@Autowired
	private CustomOperatorServiceImpl operatorServiceImpl;
	@Autowired
	private Helper helper;
	@Autowired
	private IngestServiceImpl ingestServiceImpl;
	@Autowired
	private IngestGroupServiceImpl ingestGroupServiceImpl;
	@Autowired
	private ReportServiceImpl reportServiceImpl;
	@Autowired
	private DashboardServiceImpl dashboardServiceImpl;
	@Autowired
	private NotificationServiceImpl notificationServiceImpl; 
	@Autowired
	private Rule2ServiceImpl rule2ServiceImpl;
	
	static final Logger logger = Logger.getLogger(DagServiceImpl.class);
	ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
	
	public RunMode getRunMode() {
		return runMode;
	}

	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

	
	/********************** UNUSED **********************/
	/*public Dag findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid != null) {
			return iDagDao.findOneById(appUuid, id);
		}
		return iDagDao.findOne(id);
	}*/

	/*
	 * public Dag update(Dag dag) throws IOException { dag.exportBaseProperty(); Dag
	 * dagDet = iDagDao.save(dag); registerService.createGraph(); return dagDet; }
	 */

	/********************** UNUSED **********************/
	/*public boolean isExists(String id) {
		return iDagDao.exists(id);
	}*/

	

	/********************** UNUSED **********************/
	/*public List<Dag> test(String param1) {
		return iDagDao.test(param1);
	}*

	/********************** UNUSED **********************/
	/*public Dag getOneByUuidAndVersion(String uuid, String version) {

		return iDagDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Dag findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid == null) {
			return iDagDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iDagDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

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
			dag = (Dag) commonServiceImpl.getLatestByUuid(dagRef.getUuid(), MetaType.dag.toString(), "N");
		} else {
			dag = (Dag) commonServiceImpl.getOneByUuidAndVersion(dagRef.getUuid(), dagRef.getVersion(), MetaType.dag.toString(), "N");
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
					execParams.setCurrParamSet(paramSetHolder);
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
	 * alReady exist in meta, though it shall act more as a template rather than an
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
			long startTime = System.currentTimeMillis();
			dagExec = createDAGExec(dag, execParams);
			logger.info("Time taken to create dag exec : " + ((System.currentTimeMillis() - startTime)/1000));
			commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
			//dagExecServiceImpl.save(dagExec);
		}
		
		//Check if DAG is READY for execution
		Status.Stage stg = Helper.getLatestStatus(dagExec.getStatusList()).getStage();		
		if (stg.equals(Status.Stage.RUNNING) || stg.equals(Status.Stage.COMPLETED) || stg.equals(Status.Stage.PAUSE)) {
			logger.info("DAGExec is alReady in RUNNING/COMPLETED/PAUSE status. Aborting execution.");
			throw new Exception("DAGExec is alReady in RUNNING/COMPLETED/PAUSE status. Aborting execution.");
		}
					
		// Populate ParseRunDagServiceImpl
		parseRunDagServiceImpl.setBtchServ(btchServ);
		parseRunDagServiceImpl.setDag(dag);
		parseRunDagServiceImpl.setDagExec(dagExec);
		parseRunDagServiceImpl.setDagExecServiceImpl(dagExecServiceImpl);
		parseRunDagServiceImpl.setDagServiceImpl(this);
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
	
	
	/********************** UNUSED 
	 * @throws Exception **********************/
	/*public Dag getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid != null) {
			return iDagDao.findAsOf(appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
		} else
			return iDagDao.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}*/

	public DagExec createDAGExec(Dag dag, ExecParams execParams) throws Exception {
		DagExec dagExec = new DagExec(dag);
		dagExec.setExecParams(execParams);
		dagExec.setName(dag.getName());
		dagExec.setBaseEntity();
		dagExec.setExecCreated("N");
		dagExec.setStatusList(DagExecUtil.createInitialStatus(dagExec.getStatusList()));
		commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
		List<String> datapodList = new ArrayList<>();
		MetaIdentifier dagRef = new MetaIdentifier(MetaType.dag, dag.getUuid(), dag.getVersion());
		// Loop in Stages
		List<Stage> dagStages = dag.getStages();
		List<String> dependsOn = dagStages.get(0).getDependsOn();
		List<StageExec> dagExecStages = createDagExecStages(dagExec, datapodList, dagStages, dependsOn, dagRef, execParams);
		dagExec.setStages(DagExecUtil.convertToStageList(dagExecStages));
		// dagExec.setName("sys_" + dagExec.getUuid());
		dagExec.setAppInfo(dag.getAppInfo());
		dagExec.setExecCreated("Y");
		// Set DagExec Status
		return dagExec;
	}

	/*
	 * In this method we are getting the dag stages and Making a copy of each stage
	 * for dagexec stage Creating a list of stages and then returning to called
	 * dagexec The task list for each stage is prepared by a calling function
	 */
	private List<StageExec> createDagExecStages(DagExec dagExec, List<String> datapodList, List<Stage> dagStages, List<String> dependson, MetaIdentifier dagRef,
			ExecParams execParams) throws Exception {
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
				// Set stage status to PENDING
				stageExec.setStatusList(DagExecUtil.createInitialStatus(stageExec.getStatusList()));
			}

			// Add tasks for current stage
			List<Task> dagTasks = indvStg.getTasks();
			List<TaskExec> dagExectasks = createDagExecTasks(dagExec, datapodList, dagTasks, indvStg.getDependsOn(), indvStg, dagRef,
					execParams);
			stageExec.setTasks(DagExecUtil.convertToTaskList(dagExectasks));
			stageExecs.add(stageExec);

		}
		return stageExecs;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private List<TaskExec> createDagExecTasks(DagExec dagExec, List<String> datapodList, List<Task> dagTasks, List<String> dependsOn, Stage indvStg,
			MetaIdentifier dagRef, ExecParams execParams) throws Exception {
		List<TaskExec> taskExecs = new ArrayList<>();
		java.util.Map<String, MetaIdentifier> refKeys = null;
		MetaIdentifier mapRef = null, sourceRef = null, targetRef = null, sourceAttrRef = null, condRef = null,
				targetAttrRef = null, miHolderRef = null, groupByRef = null, loadRef = null, secondaryDagRef = null;
		MetaIdentifier execIdentifier = null;

		// Convert refKeyList to HashMap
		List<MetaIdentifier> inputRefKeyList = null;
		if (execParams != null) {
			inputRefKeyList = execParams.getRefKeyList();
		} else {
			execParams = new ExecParams();
			execParams.setRefKeyList(new ArrayList<>());
			dagExec.setExecParams(execParams);
		}
		java.util.Map<String, MetaIdentifier> inputRefKeys = new HashMap<>();
		if (inputRefKeyList != null && inputRefKeyList.size() > 0) {
			for (MetaIdentifier inputRefKey : inputRefKeyList) {
				inputRefKeys.put(inputRefKey.getUuid(), inputRefKey);
			}
		}
		HashMap<String, String> otherParams = null;
		
		// Iterate the task and set it
		for (Task indvTask : dagTasks) {
			ExecParams taskExecParams = createChildParams(execParams);
			otherParams = taskExecParams.getOtherParams();
			TaskExec taskExec = null;
			if(indvTask.getOperators() != null && indvTask.getOperators().get(0).getOperatorInfo() != null ) {
				MetaIdentifier taskMI = indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef();
				if(taskMI.getType().equals(MetaType.train)
						|| taskMI.getType().equals(MetaType.rule)) {					
					if(indvTask.getOperators().get(0).getOperatorParams() != null && !indvTask.getOperators().get(0).getOperatorParams().isEmpty()) {
						HashMap<?, ?> operatorParams = indvTask.getOperators().get(0).getOperatorParams();
						ExecParams trainExecParams = new ObjectMapper().convertValue(operatorParams.get("EXEC_PARAMS"), ExecParams.class);
						if(trainExecParams.getParamInfo() != null) {
							taskExec = new TaskExec(indvTask);
							List<MetaIdentifierHolder> operatorInfoTemp = new ArrayList<>();
							for(int i =0; i<trainExecParams.getParamInfo().size(); i++) {	
								operatorInfoTemp.add(indvTask.getOperators().get(0).getOperatorInfo().get(0));
							}
							taskExec.getOperators().get(0).setOperatorInfo(operatorInfoTemp);
							taskExec.getOperators().get(0).setOperatorParams(indvTask.getOperators().get(0).getOperatorParams());
							// Set Tasks status to PENDING
							taskExec.setStatusList(DagExecUtil.createInitialStatus(taskExec.getStatusList()));
							taskExecs.add(taskExec);
						} else {
							taskExec = new TaskExec(indvTask);
							taskExec.getOperators().get(0).setOperatorParams(indvTask.getOperators().get(0).getOperatorParams());
							taskExecs.add(taskExec);
							// Set Tasks status to PENDING
							taskExec.setStatusList(DagExecUtil.createInitialStatus(taskExec.getStatusList()));
						}
					}  else {
						taskExec = new TaskExec(indvTask);
						taskExec.getOperators().get(0).setOperatorParams(indvTask.getOperators().get(0).getOperatorParams());
						taskExecs.add(taskExec);
						// Set Tasks status to PENDING
						taskExec.setStatusList(DagExecUtil.createInitialStatus(taskExec.getStatusList()));
					} 
					
					
				}  else {
					taskExec = new TaskExec(indvTask);
					taskExec.getOperators().get(0).setOperatorParams(indvTask.getOperators().get(0).getOperatorParams());
					taskExecs.add(taskExec);
					// Set Tasks status to PENDING
					taskExec.setStatusList(DagExecUtil.createInitialStatus(taskExec.getStatusList()));
				}
			} else {
				taskExec = new TaskExec(indvTask);
				taskExec.getOperators().get(0).setOperatorParams(indvTask.getOperators().get(0).getOperatorParams());
				taskExecs.add(taskExec);
				// Set Tasks status to PENDING
				taskExec.setStatusList(DagExecUtil.createInitialStatus(taskExec.getStatusList()));
			}
			
			refKeys = DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()); // Get
																						// refKeys
																						// placeholder
																						// for
			// refKey preparation

			BaseExec baseExec = null;
			try {
				baseExec = (BaseExec) commonServiceImpl.createAndSetOperator(helper.getExecType(indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef().getType()), 
						indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef(), 
						taskExec, 
						0);
				commonServiceImpl.save(helper.getExecType(indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef().getType()).toString(), baseExec);
			} catch(Exception e) {
				logger.error(indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef() + " could not be created ");
				e.printStackTrace();
				continue;
			}
			MetaIdentifier ref = indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef();

			// Below code need reformatting, the setting should be done
			// while doing getter action

			// Traverse task & Populate RefKeys
			if (indvTask.getOperators() != null && indvTask.getOperators().get(0).getOperatorInfo() != null) {
				if (indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef().getType().equals(MetaType.map) || indvTask
						.getOperators().get(0).getOperatorInfo().get(0).getRef().getType().equals(MetaType.mapiter)) {
					mapRef = indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef();
					MetaIdentifier mapIdentifier = commonServiceImpl.populateRefKeys(refKeys, mapRef, inputRefKeys);
					Map map = (Map) commonServiceImpl.getOneByUuidAndVersion(mapIdentifier.getUuid(), mapIdentifier.getVersion(), mapIdentifier.getType().toString(), "N");

					// Setting the Version for Map Object
					sourceRef = map.getSource().getRef();
					targetRef = map.getTarget().getRef();
//					daoRegister.getRefObject(commonServiceImpl.populateRefKeys(refKeys, sourceRef, inputRefKeys));
//					daoRegister.getRefObject(commonServiceImpl.populateRefKeys(refKeys, targetRef, inputRefKeys));
					BaseEntity srcBase = (BaseEntity)commonServiceImpl.getOneByUuidAndVersion(sourceRef.getUuid(), sourceRef.getVersion(), sourceRef.getType().toString(), "N");
					BaseEntity tgtBase = (BaseEntity)commonServiceImpl.getOneByUuidAndVersion(targetRef.getUuid(), targetRef.getVersion(), targetRef.getType().toString(), "N");
					sourceRef.setVersion(srcBase.getVersion());
					targetRef.setVersion(tgtBase.getVersion());

					sourceRef = commonServiceImpl.populateRefKeys(refKeys, sourceRef, inputRefKeys);
					targetRef = commonServiceImpl.populateRefKeys(refKeys, targetRef, inputRefKeys);

					
					for (AttributeMap attrMap : map.getAttributeMap()) {
						targetAttrRef = attrMap.getTargetAttr().getRef();
//						daoRegister.getRefObject(commonServiceImpl.populateRefKeys(refKeys, targetAttrRef, inputRefKeys));
						sourceAttrRef = attrMap.getSourceAttr().getRef();
//						daoRegister.getRefObject(commonServiceImpl.populateRefKeys(refKeys, sourceAttrRef, inputRefKeys));
						
						BaseEntity targetAttrBase = (BaseEntity) commonServiceImpl.getOneByUuidAndVersion(targetAttrRef.getUuid(), targetAttrRef.getVersion(), targetAttrRef.getType().toString(), "N");
						BaseEntity sourceAttrBase = (BaseEntity) commonServiceImpl.getOneByUuidAndVersion(sourceAttrRef.getUuid(), sourceAttrRef.getVersion(), sourceAttrRef.getType().toString(), "N");
						
						targetAttrRef.setVersion(targetAttrBase.getVersion());
						sourceAttrRef.setVersion(sourceAttrBase.getVersion());
						
						targetAttrRef = commonServiceImpl.populateRefKeys(refKeys, targetAttrRef, inputRefKeys);
						sourceAttrRef = commonServiceImpl.populateRefKeys(refKeys, sourceAttrRef, inputRefKeys);
					}
					baseExec = mapServiceImpl.create(ref.getUuid(), ref.getVersion(), (MapExec) baseExec, dagExec, 
							datapodList, refKeys, otherParams, taskExecParams, RunMode.BATCH);
				} else if (indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef().getType().equals(MetaType.load)) {// MetaType
																														// load
					loadRef = indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef();
					baseExec = loadServiceImpl.create(loadRef.getUuid(), loadRef.getVersion(), taskExecParams, null, (LoadExec)baseExec);

				} else if (indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef().getType().equals(MetaType.dag)) {// MetaType
																														// dag
					secondaryDagRef = indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef();
					commonServiceImpl.populateRefKeys(refKeys, secondaryDagRef, inputRefKeys); // PopuPopulatelate
																				// refKeys
				} else if (ref.getType().equals(MetaType.rule)) {
					baseExec = ruleServiceImpl.create(ref.getUuid(), ref.getVersion(), (RuleExec) baseExec, refKeys, taskExecParams, datapodList, dagExec);
				} else if (ref.getType().equals(MetaType.rulegroup)) {
					baseExec = ruleGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), null,
							datapodList, (RuleGroupExec)baseExec, dagExec);
				} else if (ref.getType().equals(MetaType.dq)) {
					baseExec = dataQualServiceImpl.create(ref.getUuid(),ref.getVersion(),(DataQualExec) baseExec,refKeys,datapodList,dagExec);
				} else if (ref.getType().equals(MetaType.dqgroup)) {
					baseExec = dataQualGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), null,
							datapodList, (DataQualGroupExec)baseExec, dagExec);
				} else if (ref.getType().equals(MetaType.profile)) {
					baseExec = profileServiceImpl.create(ref.getUuid(),ref.getVersion(),(ProfileExec)baseExec,refKeys,datapodList,dagExec);
				} else if (ref.getType().equals(MetaType.profilegroup)) {
					baseExec = profileGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), null,
							datapodList, dagExec, (ProfileGroupExec) baseExec);
				} else if (ref.getType().equals(MetaType.load)) {
					baseExec = loadServiceImpl.create(ref.getUuid(), ref.getVersion(), taskExecParams, null, (LoadExec)baseExec);
				} else if (ref.getType().equals(MetaType.train)) {
					Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
					Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), train.getDependsOn().getRef().getVersion(), train.getDependsOn().getRef().getType().toString(), "N");
					baseExec = modelServiceImpl.create(train, model, taskExecParams, null,
							(TrainExec)baseExec);
				}  else if (ref.getType().equals(MetaType.predict)) {
					Predict predict = (Predict) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
					baseExec = modelServiceImpl.create(predict, taskExecParams, null, (PredictExec) baseExec);
				} else if (ref.getType().equals(MetaType.simulate)) {
					Simulate simulate = (Simulate) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString());
					baseExec = modelServiceImpl.create(simulate, taskExecParams, null, (SimulateExec) baseExec);
				} else if (ref.getType().equals(MetaType.recon)) {
					baseExec = reconServiceImpl.create(ref.getUuid(), ref.getVersion(), (ReconExec) baseExec, refKeys, datapodList, dagExec);
				} else if (ref.getType().equals(MetaType.recongroup)) {
					baseExec = reconGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), null,
							datapodList, (ReconGroupExec) baseExec, dagExec);
				} else if (ref.getType().equals(MetaType.operator)) {
					ExecParams operatorExecParams = commonServiceImpl.getExecParams(taskExec.getOperators().get(0));
					operatorExecParams.setParamListInfo(replaceParams(execParams.getParamListInfo(), operatorExecParams.getParamListInfo()));
					operatorExecParams.setOtherParams((HashMap<String, String>) Helper.mergeMap(otherParams, operatorExecParams.getOtherParams()));
					baseExec = operatorServiceImpl.create((OperatorExec)baseExec, operatorExecParams, runMode);
					logger.info("operatorExecParams.getOtherParams : " + operatorExecParams.getOtherParams());
				} else if (ref.getType().equals(MetaType.graphpod)) {
					baseExec = graphServiceImpl.create(baseExec, taskExecParams, runMode);
				} else if (ref.getType().equals(MetaType.ingest)) {
					baseExec = ingestServiceImpl.create(ref.getUuid(), ref.getVersion(), MetaType.ingest, MetaType.ingestExec, (IngestExec) baseExec, refKeys, datapodList, dagExec);
				} else if (ref.getType().equals(MetaType.ingestgroup)) {
					baseExec = ingestGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), execParams, datapodList, (IngestGroupExec)baseExec, dagExec);
				} else if (ref.getType().equals(MetaType.report)) {
					baseExec = reportServiceImpl.create(ref.getUuid(), ref.getVersion(), execParams, (ReportExec) baseExec, RunMode.BATCH);
				} else if (ref.getType().equals(MetaType.dashboard)) {
					baseExec = dashboardServiceImpl.create(ref.getUuid(), ref.getVersion(), (DashboardExec) baseExec, execParams, RunMode.BATCH);
				} else if (ref.getType().equals(MetaType.rule2)) {
					baseExec = rule2ServiceImpl.create(ref.getUuid(), ref.getVersion(), (RuleExec) baseExec, refKeys, taskExecParams, datapodList, dagExec);
				}  
			}
			/*try {
				baseExec = (BaseExec) commonServiceImpl.createAndSetOperator(helper.getExecType(indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef().getType()), 
											indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef(), 
											taskExec, 
											0);
				commonServiceImpl.save(helper.getExecType(indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef().getType()).toString(), baseExec);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException | InterruptedException | JSONException | ParseException e) {
				logger.error(indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef() + " could not be created ");
				e.printStackTrace();
			}*/
			execParams.setRefKeyList(DagExecUtil.convertRefKeyMapToList(refKeys));
			execParams = includeInParentParams(execParams, taskExecParams);
			
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

	@SuppressWarnings({ "unused", "unlikely-arg-type", "unchecked" })
	public DagExec parseDagExec(Dag dag, DagExec dagExec) throws Exception {
		if (dagExec == null) {
			logger.info("Nothing to parse. Aborting parseDagExec");
			return null;
		}
		ExecParams execParams = dagExec.getExecParams();
		
		// Retrieve all params
		if (execParams.getParamListInfo() != null && !execParams.getParamListInfo().isEmpty()) {
			List<ParamListHolder> paramListHolders = new ArrayList<>();
			for (ParamListHolder paramListHolder : execParams.getParamListInfo()) {
				ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(paramListHolder.getRef().getUuid(), paramListHolder.getRef().getVersion(), MetaType.paramlist.toString(), "N");
				for (Param param : paramList.getParams()) {
					ParamListHolder newParamListHolder = new ParamListHolder();
					newParamListHolder.setParamId(param.getParamId());
					newParamListHolder.setParamName(param.getParamName());
					newParamListHolder.setParamType(param.getParamType());
					newParamListHolder.setParamValue(param.getParamValue());
					newParamListHolder.setRef(param.getParamRef());
					paramListHolders.add(newParamListHolder);
				}
			}
			execParams.setParamListInfo(paramListHolders);
		}
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
		
		// Get ParamList Holder if available
		List<ParamListHolder> appParamListHolder = commonServiceImpl.getAppParamList();
		if (dag.getParamList() != null) {
			List<ParamListHolder> paramListHolderList = null;//getDagParamList(dag);
			List<ParamListHolder> paramListHolders = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
			/*for(Object obj : paramListHolderList) {
				paramListHolders.add(mapper.convertValue(obj, ParamListHolder.class));
			}*/
//			if (paramListHolderList != null && !paramListHolderList.isEmpty()) {
			if (execParams.getParamListInfo() != null && !execParams.getParamListInfo().isEmpty()) {
				execParams.setParamListInfo(replaceParams(appParamListHolder, execParams.getParamListInfo()));
			} else {
				execParams.setParamListInfo(appParamListHolder);
			}
//			}
		}
		// Dag dag = (Dag) daoRegister.getRefObject(dagRef);
		// Get the dag - END
		for (StageExec indvDagExecStg : dagExecStgs) {
			
			/*synchronized (dagExec.getUuid()) {
				commonServiceImpl.setMetaStatusForStage(dagExec, indvDagExecStg, Status.Stage.PENDING, indvDagExecStg.getStageId());
			}*/

			List<TaskExec> dagExecTasks = DagExecUtil.castToTaskExecList(indvDagExecStg.getTasks());
			Stage stage = DagExecUtil.getStageFromDag(dag, indvDagExecStg.getStageId());
			if (indvDagExecStg != null && indvDagExecStg.getStatusList() != null
					&& indvDagExecStg.getStatusList().contains(Status.Stage.Inactive)) {
				continue; // If inactive stage then move to next stage (don't
							// consider inactive stage)
			}
			synchronized (dagExec.getUuid()) {
				commonServiceImpl.setMetaStatusForStage(dagExec, indvDagExecStg, Status.Stage.STARTING, indvDagExecStg.getStageId());
			}
			for (TaskExec indvExecTask : dagExecTasks) {
				Thread.sleep(10);
				ExecParams taskExecParams = createChildParams(execParams);
				otherParams = taskExecParams.getOtherParams();
				Task indvTask = DagExecUtil.getTaskFromStage(stage, indvExecTask.getTaskId());
				logger.info("Parsing task : " + indvTask.getTaskId() + ":" + indvTask.getName() + ":" + indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef().getType());
				logger.info(" OtherParams : " + otherParams);
				
				
				synchronized (dagExec.getUuid()) {
					commonServiceImpl.setMetaStatusForTask(dagExec, indvExecTask, Status.Stage.STARTING, indvDagExecStg.getStageId(), indvExecTask.getTaskId());
				}
				
				List<TaskOperator> operatorList = new ArrayList<>();
				TaskOperator operator = new TaskOperator();
				for(int i=0; i<indvTask.getOperators().get(0).getOperatorInfo().size(); i++) {
					MetaIdentifier ref = indvTask.getOperators().get(0).getOperatorInfo().get(i).getRef();
					MetaIdentifierHolder operatorInfo = new MetaIdentifierHolder();
					List<MetaIdentifierHolder> operatorInfoList = new ArrayList<>();
					operatorInfoList.add(operatorInfo);
					operator.setOperatorInfo(operatorInfoList);
					StringBuilder builder = null;
					if (indvTask.getOperators().get(0).getOperatorParams() != null
							&& indvTask.getOperators().get(0).getOperatorParams().containsKey(MetaType.paramset.toString())
							&& taskExecParams.getCurrParamSet() == null) {
						List<ParamSetHolder> paramSetHolderList = (List<ParamSetHolder>) indvTask.getOperators().get(0)
								.getOperatorParams().get(MetaType.paramset.toString());
						List<ParamSetHolder> paramSetHolders = new ArrayList<>();
						ObjectMapper mapper = new ObjectMapper();
						for(Object obj : paramSetHolderList) {
							paramSetHolders.add(mapper.convertValue(obj, ParamSetHolder.class));
						}
						if (paramSetHolderList != null && !paramSetHolderList.isEmpty()) {
							taskExecParams.setParamInfo(paramSetHolders);
							taskExecParams.setCurrParamSet(paramSetHolders.get(0));
						}
					}
					if (indvTask.getOperators().get(0).getOperatorParams() != null
							&& indvTask.getOperators().get(0).getOperatorParams().containsKey(MetaType.paramlist.toString())) {
//							&& taskExecParams.getParamListHolder() == null) {
						List<ParamListHolder> paramListHolderList = (List<ParamListHolder>) indvTask.getOperators().get(0).getOperatorParams().get(MetaType.paramlist.toString());
						List<ParamListHolder> paramListHolders = new ArrayList<>();
						ObjectMapper mapper = new ObjectMapper();
						for(Object obj : paramListHolderList) {
							paramListHolders.add(mapper.convertValue(obj, ParamListHolder.class));
						}
						if (paramListHolderList != null && !paramListHolderList.isEmpty()) {
							taskExecParams.setParamListInfo(paramListHolders);
							taskExecParams.setParamListHolder(paramListHolders.get(0));
						}
					}
					operator.setOperatorParams(indvTask.getOperators().get(0).getOperatorParams());
					// Have few parts in common area
					java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
							.convertRefKeyListToMap(taskExecParams.getRefKeyList());
					
//					BaseExec baseExec = (BaseExec) commonServiceImpl.createAndSetOperator(helper.getExecType(ref.getType()), ref, indvExecTask, i);
					MetaIdentifier execRef = indvExecTask.getOperators().get(0).getOperatorInfo().get(i).getRef();
					BaseExec baseExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(execRef.getUuid(), execRef.getVersion(), execRef.getType().toString(), "N"); 
					baseExec.setExecParams(taskExecParams);

					try {
						// If conditions with parse goes here - START
						if (ref.getType().equals(MetaType.map)) {
							baseExec = mapServiceImpl.generateSql(ref.getUuid(), ref.getVersion(), (MapExec) baseExec, dagExec, 
									datapodList, refKeyMap, otherParams, taskExecParams, RunMode.BATCH);
						} else if (ref.getType().equals(MetaType.rule)) {
//							baseExec = ruleServiceImpl.create(ref.getUuid(), ref.getVersion(), (RuleExec) baseExec, refKeyMap, taskExecParams, datapodList, dagExec);
							baseExec = ruleServiceImpl.parse(baseExec.getUuid(), baseExec.getVersion(), refKeyMap, otherParams, datapodList, dagExec, RunMode.ONLINE);
						} else if (ref.getType().equals(MetaType.rulegroup)) {
//							baseExec = ruleGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), null,
//									datapodList, (RuleGroupExec)baseExec, dagExec);
							baseExec = ruleGroupServiceImpl.parse(baseExec.getUuid(), baseExec.getVersion(), refKeyMap, otherParams, datapodList, dagExec, RunMode.ONLINE);
						} else if (ref.getType().equals(MetaType.dq)) {
//							baseExec = dataQualServiceImpl.create(ref.getUuid(),ref.getVersion(),(DataQualExec) baseExec,refKeyMap,datapodList,dagExec);
							baseExec =  (DataQualExec)dataQualServiceImpl.parse(baseExec.getUuid(), baseExec.getVersion(), refKeyMap, otherParams, datapodList, dagExec, runMode);
						} else if (ref.getType().equals(MetaType.dqgroup)) {
//							baseExec = dataQualGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), null,
//									datapodList, (DataQualGroupExec)baseExec, dagExec);
							baseExec = dataQualGroupServiceImpl.parse(indvExecTask.getOperators().get(0).getOperatorInfo().get(0).getRef(),refKeyMap, otherParams, datapodList, dagExec, runMode);
						} else if (ref.getType().equals(MetaType.profile)) {
//							baseExec = profileServiceImpl.create(ref.getUuid(),ref.getVersion(),(ProfileExec)baseExec,refKeyMap,datapodList,dagExec);
							baseExec =  (ProfileExec)profileServiceImpl.parse(baseExec.getUuid(), baseExec.getVersion(), refKeyMap, otherParams, datapodList, dagExec, runMode);
						} else if (ref.getType().equals(MetaType.profilegroup)) {
//							baseExec = profileGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), null,
//									datapodList, dagExec, (ProfileGroupExec) baseExec);
							baseExec = (ProfileGroupExec) profileGroupServiceImpl.parse(baseExec.getUuid(),baseExec.getVersion(),refKeyMap, otherParams, datapodList, dagExec, runMode);
						} /*else if (ref.getType().equals(MetaType.load)) {
							baseExec = loadServiceImpl.create(ref.getUuid(), ref.getVersion(), taskExecParams, null, (LoadExec)baseExec);
						} else if (ref.getType().equals(MetaType.train)) {
							Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
							Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), train.getDependsOn().getRef().getVersion(), train.getDependsOn().getRef().getType().toString(), "N");
							baseExec = modelServiceImpl.create(train, model, taskExecParams, null,
									(TrainExec)baseExec);
						}  else if (ref.getType().equals(MetaType.predict)) {
							Predict predict = (Predict) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
							baseExec = modelServiceImpl.create(predict, taskExecParams, null, (PredictExec) baseExec);
						} else if (ref.getType().equals(MetaType.simulate)) {
							Simulate simulate = (Simulate) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString());
							baseExec = modelServiceImpl.create(simulate, taskExecParams, null, (SimulateExec) baseExec);
						} */else if (ref.getType().equals(MetaType.recon)) {
							baseExec = reconServiceImpl.create(ref.getUuid(), ref.getVersion(), (ReconExec) baseExec, refKeyMap, datapodList, dagExec);
							baseExec = (ReconExec) reconServiceImpl.parse(baseExec.getUuid(), baseExec.getVersion(), refKeyMap, otherParams, datapodList, dagExec, runMode);
						} else if (ref.getType().equals(MetaType.recongroup)) {
							baseExec = reconGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), null,
									datapodList, (ReconGroupExec) baseExec, dagExec);
							baseExec = reconGroupServiceImpl.parse(baseExec.getUuid(), baseExec.getVersion(), refKeyMap, otherParams, datapodList, dagExec, runMode);
						} else if (ref.getType().equals(MetaType.operator)) {
							ExecParams operatorExecParams = commonServiceImpl.getExecParams(indvExecTask.getOperators().get(0));
							operatorExecParams.setParamListInfo(replaceParams(execParams.getParamListInfo(), operatorExecParams.getParamListInfo()));
							operatorExecParams.setOtherParams((HashMap<String, String>) Helper.mergeMap(otherParams, operatorExecParams.getOtherParams()));
//							baseExec = operatorServiceImpl.create((OperatorExec)baseExec, operatorExecParams, runMode);
							logger.info("operatorExecParams.getOtherParams : " + operatorExecParams.getOtherParams());
							operatorServiceImpl.parse((OperatorExec)baseExec, operatorExecParams, runMode);
							logger.info("operatorExecParams.getOtherParams : otherParams 1 : " + operatorExecParams.getOtherParams() + ":" + otherParams);
							otherParams = (HashMap<String, String>) Helper.mergeMap(operatorExecParams.getOtherParams(), otherParams);
							logger.info("operatorExecParams.getOtherParams : otherParams 2 : " + operatorExecParams.getOtherParams() + ":" + otherParams);
							/*if (indvTask.getDependsOn().size() > 0) {
								operatorExecParams.setOtherParams((HashMap<String, String>)helper.mergeMap(otherParams, operatorExecParams.getOtherParams()));
								indvExecTask.getOperators().get(0).getOperatorParams().put(ConstantsUtil.EXEC_PARAMS, operatorExecParams);
							}*/
						} else if (ref.getType().equals(MetaType.graphpod)) {
//							baseExec = graphServiceImpl.create(baseExec, taskExecParams, runMode);
							baseExec = reconGroupServiceImpl.parse(baseExec.getUuid(), baseExec.getVersion(), refKeyMap, otherParams, datapodList, dagExec, runMode);
						} else if (ref.getType().equals(MetaType.ingest)) {
//							baseExec = ingestServiceImpl.create(ref.getUuid(), ref.getVersion(), MetaType.ingest, MetaType.ingestExec, (IngestExec) baseExec, refKeyMap, datapodList, dagExec);
							baseExec = ingestServiceImpl.parse(baseExec.getUuid(), baseExec.getVersion(), refKeyMap, otherParams, datapodList, dagExec, RunMode.BATCH);
						} else if (ref.getType().equals(MetaType.ingestgroup)) {
//							baseExec = ingestGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), execParams, datapodList, (IngestGroupExec)baseExec, dagExec);
							baseExec = ingestGroupServiceImpl.parse(baseExec.getUuid(), baseExec.getVersion(), refKeyMap, otherParams, datapodList, dagExec, RunMode.BATCH);
						} else if (ref.getType().equals(MetaType.report)) {
//							baseExec = reportServiceImpl.create(ref.getUuid(), ref.getVersion(), execParams, (ReportExec) baseExec, RunMode.BATCH);
							baseExec = reportServiceImpl.parse(baseExec.getUuid(), baseExec.getVersion(), refKeyMap, otherParams, dagExec, runMode);
						}  else if (ref.getType().equals(MetaType.dashboard)) {
//							baseExec = dashboardServiceImpl.create(ref.getUuid(), ref.getVersion(), (DashboardExec) baseExec, execParams, RunMode.BATCH);
							baseExec = dashboardServiceImpl.parse(baseExec.getUuid(), baseExec.getVersion(), execParams, refKeyMap, otherParams, datapodList, dagExec, runMode);
						} else if (ref.getType().equals(MetaType.rule2)) {
							baseExec = rule2ServiceImpl.parse(baseExec.getUuid(), baseExec.getVersion(), refKeyMap, otherParams, datapodList, dagExec, RunMode.BATCH);
						}
						taskExecParams.setOtherParams((HashMap<String, String>)Helper.mergeMap(otherParams, taskExecParams.getOtherParams()));
						// If conditions with parse goes here - END	
						logger.info(" otherParams : " + otherParams);
						logger.info(" taskExecParams.getOtherParams() : " + taskExecParams.getOtherParams());
						baseExec.setRefKeyList(taskExecParams.getRefKeyList());
						if (Helper.getLatestStatus(baseExec.getStatusList()).getStage() == Status.Stage.FAILED) {
							throw new Exception("DAG failed. So cannot proceed ... ");
						}
					} catch (Exception e) {
						Status FAILEDStatus = new Status(Status.Stage.FAILED, new Date());
						List<Status> statusList = indvExecTask.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						statusList.remove(FAILEDStatus);
						statusList.add(FAILEDStatus);
						indvExecTask.setStatusList(statusList);
						e.printStackTrace();
						String message = null;
						try {
							message = e.getMessage();
						}catch (Exception e2) {
							// TODO: handle exception
						}
						// Set stageExec and DagExec to FAILED
						indvDagExecStg.setTasks(DagExecUtil.convertToTaskList(dagExecTasks));
						statusList = indvDagExecStg.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						statusList.remove(FAILEDStatus);
						statusList.add(FAILEDStatus);
						indvDagExecStg.setStatusList(statusList);
						dagExec.setStages(DagExecUtil.convertToStageList(dagExecStgs));
						
						statusList = dagExec.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						statusList.remove(FAILEDStatus);
						statusList.add(FAILEDStatus);
						dagExec.setStatusList(statusList);
						commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
						MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
						dependsOn.setRef(new MetaIdentifier(MetaType.dagExec, dagExec.getUuid(), dagExec.getVersion()));
						logger.error((message != null) ? message : "Pipeline execution FAILED.", e);
						commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Pipeline execution FAILED.", dependsOn);
						throw new Exception((message != null) ? message : "Pipeline execution FAILED.", e);
					} finally {
						commonServiceImpl.save(helper.getExecType(ref.getType()).toString(), baseExec);
					}
				}	
				//operatorList.add(operator);
				//indvExecTask.setOperators(operatorList);
				// Set Stage and Task status
				/*List<Status> statusList = new ArrayList<Status>();
				Status status = new Status(Status.Stage.PENDING, new Date());
				statusList.add(status);
				indvExecTask.setStatusList(statusList);*/
				execParams = includeInParentParams(execParams, taskExecParams);
				synchronized (dagExec.getUuid()) {
					dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(dagExec.getUuid(), dagExec.getVersion(), MetaType.dagExec.toString(), "N");
					commonServiceImpl.setMetaStatusForTask(dagExec, indvExecTask, Status.Stage.READY, indvDagExecStg.getStageId(), indvExecTask.getTaskId());
				}
			}
			indvDagExecStg.setTasks(DagExecUtil.convertToTaskList(dagExecTasks));
			synchronized (dagExec.getUuid()) {
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(dagExec.getUuid(), dagExec.getVersion(), MetaType.dagExec.toString(), "N");
				commonServiceImpl.setMetaStatusForStage(dagExec, indvDagExecStg, Status.Stage.READY, indvDagExecStg.getStageId());
			}
		}
		dagExec.setStages(DagExecUtil.convertToStageList(dagExecStgs));
		return dagExec;
	}

	/********************** UNUSED **********************/
	/*public OrderKey getLatestMetaKey(Set<OrderKey> keySet, OrderKey key) {
		if (key.getVersion() != null) {
			System.out.println(String.format("getLatestMetaKey: existing %s  %s", key.getUUID(), key.getVersion()));
			return key;
		}
		OrderKey latestKey = Helper.getFirstKey(keySet, key.getUUID());
		System.out.println(String.format("getLatestMetaKey: latest key %s  %s", key.getUUID(), key.getVersion()));
		return latestKey;
	}*/


	public Dag findDagByDagExec(String dagExecUuid) throws JsonProcessingException {
		//DagExec dagExec = dagExecServiceImpl.findLatestByUuid(dagExecUuid);
		DagExec dagExec = (DagExec) commonServiceImpl.getLatestByUuid(dagExecUuid, MetaType.dagExec.toString());
		return (Dag) commonServiceImpl.getOneByUuidAndVersion(dagExec.getDependsOn().getUuid(), dagExec.getDependsOn().getVersion(), MetaType.dag.toString());
	}

	/*public Dag setDAGPAUSE(String uuid, String version, String stageId) throws Exception {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		Dag dag = null;
		if (appUuid != null) {
			if (StringUtils.isBlank(version))
				dag = iDagDao.findOneByUuidAndVersion(appUuid, uuid, version);
			else
				dag = findLatestByUuid(uuid);

		} else {
			if (StringUtils.isBlank(version))
				dag = iDagDao.findOneByUuidAndVersion(uuid, version);
			else
				dag = findLatestByUuid(uuid);

		}
		for (int i = 0; i < dag.getStages().size(); i++) {
			if (dag.getStages().get(i).getStageId().equals(stageId)) {
				Status stagePAUSEStatus = new Status(Status.Stage.PAUSE, new Date());
				List<Status> stageStatusList = dag.getStages().get(i).getStatusList();
				stageStatusList.remove(stagePAUSEStatus);
				stageStatusList.add(stagePAUSEStatus);
				dag.getStages().get(i).setStatusList(stageStatusList);
				for (int j = 0; j < dag.getStages().get(i).getTasks().size(); j++) {
					int lastDag = dag.getStages().get(i).getTasks().get(j).getStatusList().size() - 1;
					if (dag.getStages().get(i).getTasks().get(j).getStatusList().get(lastDag).getStage()
							.equals(Status.Stage.PENDING)) {
						throw new Exception("Task PENDING.");
					} else {
						Status taskPAUSEStatus = new Status(Status.Stage.PAUSE, new Date());
						List<Status> taskStatusList = dag.getStages().get(i).getTasks().get(j).getStatusList();
						taskStatusList.remove(taskPAUSEStatus);
						taskStatusList.add(taskPAUSEStatus);
						dag.getStages().get(i).getTasks().get(j).setStatusList(taskStatusList);
					}
				}
			}
		}

		return iDagDao.save(dag);
	}

	public Dag setDAGRESUME(String uuid, String version, String stageId) throws Exception {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		Dag dag = null;
		if (appUuid != null) {
			if (StringUtils.isBlank(version))
				dag = iDagDao.findOneByUuidAndVersion(appUuid, uuid, version);
			else
				dag = findLatestByUuid(uuid);

		} else {
			if (StringUtils.isBlank(version))
				dag = iDagDao.findOneByUuidAndVersion(uuid, version);
			else
				dag = findLatestByUuid(uuid);

		}
		for (int i = 0; i < dag.getStages().size(); i++) {
			if (dag.getStages().get(i).getStageId().equals(stageId)) {
				Status stagePAUSEStatus = new Status(Status.Stage.RESUME, new Date());
				List<Status> stageStatusList = dag.getStages().get(i).getStatusList();
				stageStatusList.remove(stagePAUSEStatus);
				stageStatusList.add(stagePAUSEStatus);
				dag.getStages().get(i).setStatusList(stageStatusList);
				for (int j = 0; j < dag.getStages().get(i).getTasks().size(); j++) {
					int lastDag = dag.getStages().get(i).getTasks().get(j).getStatusList().size() - 1;
					if (dag.getStages().get(i).getTasks().get(j).getStatusList().get(lastDag).getStage()
							.equals(Status.Stage.PENDING)) {
						throw new Exception("Task PENDING.");
					} else {
						Status taskPAUSEStatus = new Status(Status.Stage.RESUME, new Date());
						List<Status> taskStatusList = dag.getStages().get(i).getTasks().get(j).getStatusList();
						taskStatusList.remove(taskPAUSEStatus);
						taskStatusList.add(taskPAUSEStatus);
						dag.getStages().get(i).getTasks().get(j).setStatusList(taskStatusList);
					}

				}
			}
		}

		return iDagDao.save(dag);
	}*/

	/*public String setStatus(String uuid, String version, String stageId, String taskId, String status) throws JsonProcessingException, Exception {
		if (!StringUtils.isBlank(status)) {
			if (!StringUtils.isBlank(taskId) && !StringUtils.isBlank(stageId)) {
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.RESUME.toString().toLowerCase())){
					return objectWriter.writeValueAsString(dagExecServiceImpl.setStageRESUME(uuid, version, stageId));
				}
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.PAUSE.toString().toLowerCase())){
					return objectWriter.writeValueAsString(dagExecServiceImpl.setTaskRESUME(uuid, version, stageId, taskId));
				}
			}else
				logger.info("Empty stageId, can not perform the operation.");
			if (!StringUtils.isBlank(stageId) && StringUtils.isBlank(taskId)) {
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.RESUME.toString().toLowerCase())){
					return objectWriter.writeValueAsString(dagExecServiceImpl.setStageRESUME(uuid, version, stageId));
				}
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.PAUSE.toString().toLowerCase())){
					return objectWriter.writeValueAsString(dagExecServiceImpl.setStagePAUSE(uuid, version, stageId));
				}
			}else
				logger.info("Empty stageId, can not perform the operation.");
			if (StringUtils.isBlank(stageId) && StringUtils.isBlank(taskId)) {
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.RESUME.toString().toLowerCase())){
					return objectWriter.writeValueAsString(setDAGRESUME(uuid, version, stageId));
				}
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.PAUSE.toString().toLowerCase())){
					return objectWriter.writeValueAsString(setDAGPAUSE(uuid, version, stageId));
				}
			}
		} else {
			logger.info("Empty status, can not perform the operation.");
		}
		return null;

	}*/
	
	public void restart(String dagExecUuid,String dagExecVersion, RunMode runMode) throws Exception{
		DagExec dagExec=prepareDagExec(dagExecUuid, dagExecVersion,null,MetaType.dagExec.toString());
		submitDag(dagExec.getUuid(), dagExec.getVersion(),null,MetaType.dagExec.toString(), runMode);		
	}
	
	public DagExec prepareDagExec(String uuid, String version, ExecParams execParams, String metaType)throws Exception{
		DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, metaType);
		logger.info("Before starting to set status to READY ");
		synchronized (dagExec.getUuid()) {
			if(Helper.getLatestStatus(dagExec.getStatusList()).equals(new Status(Status.Stage.FAILED, new Date()))
					||Helper.getLatestStatus(dagExec.getStatusList()).equals(new Status(Status.Stage.KILLED, new Date()))) {
				logger.info("DagExec FAILED/KILLED. So proceeding ... ");
			for(int i=0;i<dagExec.getStages().size();i++){
				StageExec stageExec = dagExecServiceImpl.getStageExec(dagExec,dagExec.getStages().get(i).getStageId());
				if(Helper.getLatestStatus(dagExec.getStages().get(i).getStatusList()).equals(new Status(Status.Stage.FAILED, new Date()))
						||Helper.getLatestStatus(dagExec.getStages().get(i).getStatusList()).equals(new Status(Status.Stage.KILLED, new Date()))){
					logger.info("StageExec FAILED/KILLED. So proceeding ... ");
					for(int j=0;j<dagExec.getStages().get(i).getTasks().size();j++){
						if(Helper.getLatestStatus(dagExec.getStages().get(i).getTasks().get(j).getStatusList()).equals(new Status(Status.Stage.FAILED, new Date()))
								||Helper.getLatestStatus(dagExec.getStages().get(i).getTasks().get(j).getStatusList()).equals(new Status(Status.Stage.KILLED, new Date()))){
							logger.info("TaskExec FAILED/KILLED. So proceeding ... ");
							TaskExec taskExec = dagExecServiceImpl.getTaskExec(dagExec,dagExec.getStages().get(i).getStageId(), dagExec.getStages().get(i).getTasks().get(j).getTaskId());
							for (TaskOperator taskOperator : taskExec.getOperators()) {
								MetaIdentifier meta = taskOperator.getOperatorInfo().get(0).getRef();
								BaseExec baseExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(meta.getUuid(), meta.getVersion(), meta.getType().toString(), "N");
								if (meta.getType().toString().contains("group")) {
									Object obj = (CommonServiceImpl.class.getMethod(GET + Helper.getServiceClass(Helper.getMetaTypeByExecType(meta.getType())))
												.invoke(commonServiceImpl));
									Status restartStatus = (Status) obj.getClass().getMethod("restart", BaseExec.class).invoke(obj, baseExec);
								} else {
									if (Helper.isStatusPresent(new Status(Status.Stage.READY, new Date()), baseExec.getStatusList())) {
										commonServiceImpl.setMetaStatus(baseExec, meta.getType(), Status.Stage.READY);
										logger.info(String.format(" Setting operatorexec %s to READY ", baseExec.getUuid()));
									} else {
										commonServiceImpl.setMetaStatus(baseExec, meta.getType(), Status.Stage.PENDING);
										logger.info(String.format(" Setting operatorexec %s to READY ", baseExec.getUuid()));
									}
								}
							} // End for operator
							
							if (Helper.isStatusPresent(new Status(Status.Stage.READY, new Date()), taskExec.getStatusList())) {
								taskExec = (TaskExec) commonServiceImpl.setMetaStatusForTask(dagExec, taskExec, Status.Stage.READY, dagExec.getStages().get(i).getStageId(), dagExec.getStages().get(i).getTasks().get(j).getTaskId());
								logger.info(String.format(" Setting taskexec %s to READY ", taskExec.getTaskId()));
							} else {
								taskExec = (TaskExec) commonServiceImpl.setMetaStatusForTask(dagExec, taskExec, Status.Stage.STARTING, dagExec.getStages().get(i).getStageId(), dagExec.getStages().get(i).getTasks().get(j).getTaskId());
								logger.info(String.format(" Setting taskexec %s to STARTING ", taskExec.getTaskId()));
							}
							dagExec.getStages().get(i).getTasks().remove(j);
							dagExec.getStages().get(i).getTasks().add(j, taskExec); 
						}	
					} // End tasks
					if (Helper.isStatusPresent(new Status(Status.Stage.READY, new Date()), stageExec.getStatusList())) {
						stageExec = (StageExec) commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.READY, stageExec.getStageId());
						logger.info(String.format(" Setting stageexec %s to READY ", stageExec.getStageId()));
					} else {
						stageExec = (StageExec) commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.STARTING, stageExec.getStageId());
						logger.info(String.format(" Setting stageexec %s to STARTING ", stageExec.getStageId()));
					}
					dagExec.getStages().remove(i);
					dagExec.getStages().add(i, stageExec);
				}
			} // End stages
				if (Helper.isStatusPresent(new Status(Status.Stage.READY, new Date()), dagExec.getStatusList())) {
					dagExec = (DagExec) commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.READY);
					logger.info(String.format(" Setting dagexec %s to READY ", dagExec.getUuid()));
				} else {
					dagExec = (DagExec) commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.STARTING);
					logger.info(String.format(" Setting dagexec %s to STARTING ", dagExec.getUuid()));
				}
			}
		}
		logger.info("After setting status to READY or STARTING ");
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
	
	/**
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	// Get paramList from appInfo and populate param
	public List<ParamListHolder> getDagParamList(Dag dag) throws JsonProcessingException {
		List<ParamListHolder> paramHolderList = null;
		ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(dag.getParamList().getRef().getUuid(), dag.getParamList().getRef().getVersion(), dag.getParamList().getRef().getType().toString());
		ParamListHolder paramListHolder = null;
		if (paramList != null && paramList.getParams() != null && !paramList.getParams().isEmpty()) {
			paramHolderList = new ArrayList<>();
			for (Param param : paramList.getParams()) {
				paramListHolder = new ParamListHolder();
				paramListHolder.setParamId(param.getParamId());
				paramListHolder.setParamName(param.getParamName());
				paramListHolder.setParamType(param.getParamType());
				paramListHolder.setParamValue(param.getParamValue());
				paramListHolder.setRef(param.getParamRef());
				paramHolderList.add(paramListHolder);
			}
		}
		return paramHolderList;
	}
	
	/**
	 * 
	 * @param execParams
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public ExecParams createChildParams (ExecParams execParams) throws CloneNotSupportedException {
		if (execParams == null) {
			return null;
		}
		return (ExecParams) execParams.clone();
	}
	
	/**
	 * 
	 * @param parentExecParams
	 * @param childExecParams
	 * @return
	 */
	public ExecParams includeInParentParams (ExecParams parentExecParams, ExecParams childExecParams) {
		if (childExecParams == null) {
			return parentExecParams;
		}
		if (parentExecParams == null) {
			parentExecParams = new ExecParams();
		}
		parentExecParams.setRefKeyList(childExecParams.getRefKeyList());
		if (childExecParams.getOtherParams() != null) {
			if (parentExecParams.getOtherParams() != null) {
				parentExecParams.getOtherParams().putAll(childExecParams.getOtherParams());
			} else {
				parentExecParams.setOtherParams(childExecParams.getOtherParams());
			}
		}
		return parentExecParams;
	}
	
	/**
	 * 
	 * @param parentParamHolderList
	 * @param childParamHolderList
	 * @return
	 */
	public List<ParamListHolder> replaceParams (List<ParamListHolder> parentParamHolderList, List<ParamListHolder> childParamHolderList) {
		Boolean matchFound = Boolean.FALSE;
		if (parentParamHolderList == null || parentParamHolderList.isEmpty()) {
			return childParamHolderList;
		}
		if (childParamHolderList == null || childParamHolderList.isEmpty()) {
			return parentParamHolderList;
		}
		for (ParamListHolder paramHolder : parentParamHolderList) {
			matchFound = Boolean.FALSE;
			for (ParamListHolder childParamHolder : childParamHolderList) {
				if (childParamHolder.getParamName().equals(paramHolder.getParamName())) {
					matchFound = Boolean.TRUE;
					break;
				}
			}
			if (!matchFound) {
				childParamHolderList.add(paramHolder);
			}
		}
		return childParamHolderList;
	}

	/**
	 * @param senderInfo
	 * @param dag
	 * @param dagExec
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public boolean sendSuccessNotification(SenderInfo senderInfo, Dag dag, DagExec dagExec)
			throws FileNotFoundException, IOException {
		logger.info("sending success notification...");
		Notification notification = new Notification();

		String subject = Helper.getPropertyValue("framework.email.subject");
		subject = MessageFormat.format(subject, "SUCCESS", "Dag", dag.getName(), "COMPLETED");
		notification.setSubject(subject);

		String roleUuid = sessionHelper.getSessionContext().getRoleInfo().getRef().getUuid();
		String appUuid = sessionHelper.getSessionContext().getAppInfo().getRef().getUuid();

		String contextPath = Helper.getPropertyValue("framework.webserver.contextpath");
		if(contextPath.startsWith("")) {
			contextPath = "";
		} else {
			contextPath = contextPath.startsWith("/") ? contextPath : "/".concat(contextPath);
			contextPath = contextPath.endsWith("/") ? contextPath.substring(contextPath.lastIndexOf("/")) : contextPath;	
		}
		
		String resultUrl = Helper.getPropertyValue("framework.url.dag.result");
		resultUrl = MessageFormat.format(resultUrl, Helper.getPropertyValue("framework.webserver.host"),
				Helper.getPropertyValue("framework.webserver.port"), contextPath, dagExec.getUuid(), dagExec.getVersion(),
				MetaType.dagExec.toString().toLowerCase(), roleUuid, appUuid);

		String message = Helper.getPropertyValue("framework.email.body");
		message = MessageFormat.format(message, resultUrl);
		notification.setMessage(message);
		notification.setSenderInfo(senderInfo);
		return notificationServiceImpl.prepareAndSendNotification(notification);
	}

	/**
	 * @param senderInfo
	 * @param dag
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public boolean sendFailureNotification(SenderInfo senderInfo, Dag dag, DagExec dagExec) throws FileNotFoundException, IOException {
		logger.info("sending fail notification...");
		Notification notification = new Notification();
		
		String subject = Helper.getPropertyValue("framework.email.subject");
		subject = MessageFormat.format(subject, "FAILURE", "Dag", dag.getName(), "FAILED");
		notification.setSubject(subject);

		String roleUuid = sessionHelper.getSessionContext().getRoleInfo().getRef().getUuid();
		String appUuid = sessionHelper.getSessionContext().getAppInfo().getRef().getUuid();

		String contextPath = Helper.getPropertyValue("framework.webserver.contextpath");
		if(contextPath.startsWith("")) {
			contextPath = "";
		} else {
			contextPath = contextPath.startsWith("/") ? contextPath : "/".concat(contextPath);
			contextPath = contextPath.endsWith("/") ? contextPath.substring(contextPath.lastIndexOf("/")) : contextPath;	
		}
		
		String resultUrl = Helper.getPropertyValue("framework.url.dag.result");
		resultUrl = MessageFormat.format(resultUrl, Helper.getPropertyValue("framework.webserver.host"),
				Helper.getPropertyValue("framework.webserver.port"), contextPath, dagExec.getUuid(), dagExec.getVersion(),
				MetaType.dagExec.toString().toLowerCase(), roleUuid, appUuid);

		String message = Helper.getPropertyValue("framework.email.body");
		message = MessageFormat.format(message, resultUrl);
		notification.setMessage(message);
		
		notification.setSenderInfo(senderInfo);
		return notificationServiceImpl.prepareAndSendNotification(notification);
	}
	
}
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
import com.inferyx.framework.domain.AttributeMap;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Operator;
import com.inferyx.framework.domain.OperatorExec;
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
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.register.GraphRegister;

@Service
public class DagServiceImpl {

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
	private MetadataUtil daoRegister;
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
	private Mode runMode;
	@Autowired
	private ReconServiceImpl reconServiceImpl;
	@Autowired
	private ReconGroupServiceImpl reconGroupServiceImpl;
	@Autowired
	private OperatorServiceImpl operatorServiceImpl;
	
	static final Logger logger = Logger.getLogger(DagServiceImpl.class);
	ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
	
	public Mode getRunMode() {
		return runMode;
	}

	public void setRunMode(Mode runMode) {
		this.runMode = runMode;
	}

	/********************** UNUSED **********************/
	/*public Dag findLatest() {
		return resolveName(iDagDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

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

	/********************** UNUSED **********************/
	/*public Dag Save(Dag dag) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		dag.setAppInfo(metaIdentifierHolderList);
		dag.setBaseEntity();
		Dag dagDet = iDagDao.save(dag);
		registerGraph.updateGraph((Object) dagDet, MetaType.dag);
		return dagDet;
	}*/

	/********************** UNUSED **********************/
	/*public List<Dag> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid == null) {
			return iDagDao.findAll();
		}
		return iDagDao.findAll(appUuid);
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
	/*public void delete(String Id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		Dag dag = iDagDao.findOne(Id);
		dag.setActive("N");
		iDagDao.save(dag);

//		
//		 String ID = dag.getId(); iDagDao.delete(appUuid, ID);
//		 dag.exportBaseProperty();
//		 
	}*/

	/********************** UNUSED **********************/
	/*public List<Dag> test(String param1) {
		return iDagDao.test(param1);
	}*/

	/********************** UNUSED **********************/
	/*public List<Dag> findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDagDao.findAllByUuid(appUuid, uuid);

	}*/

	/********************** UNUSED **********************/
	/*public Dag findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid != null) {
			return iDagDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} else
			return iDagDao.findOneByUuidAndVersion(uuid, version);

	}
*/
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

	/********************** UNUSED **********************/
	/*public List<Dag> findAllLatest() {
		// String appUuid =
		// securityServiceImpl.getAppInfo().getRef().getUuid();;
		Aggregation dagAggr = newAggregation(group("uuid").max("version").as("version"));
		AggregationResults<Dag> dagResults = mongoTemplate.aggregate(dagAggr, "dag", Dag.class);
		List<Dag> dagList = dagResults.getMappedResults();

		// Fetch the datapod details for each id
		List<Dag> result = new ArrayList<Dag>();
		for (Dag s : dagList) {
			Dag dagLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid()
							: null;
			if (appUuid != null) {
				// String appUuid =
				// securityServiceImpl.getAppInfo().getRef().getUuid();;
				dagLatest = iDagDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			} else {
				dagLatest = iDagDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
			}
			// logger.debug("datapodLatest is " + datapodLatest.getName());
			if (dagLatest != null) {
				result.add(dagLatest);
			}
		}
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<Dag> findAllLatestActive() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		// String appUuid =
		// securityServiceImpl.getAppInfo().getRef().getUuid();;
		Aggregation dagAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Dag> dagResults = mongoTemplate.aggregate(dagAggr, "dag", Dag.class);
		List<Dag> dagList = dagResults.getMappedResults();

		// Fetch the datapod details for each id
		List<Dag> result = new ArrayList<Dag>();
		for (Dag s : dagList) {
			Dag dagLatest;
			if (appUuid != null) {
				dagLatest = iDagDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			} else {
				dagLatest = iDagDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
			}
			if (dagLatest != null) {
				result.add(dagLatest);
			}
		}
		return result;
	}
*/
	/**
	 * Overloaded submitDag with input as dagRef
	 * 
	 * @param dagRef
	 * @return
	 * @throws Exception
	 */
	public MetaIdentifierHolder submitDag(MetaIdentifier dagRef, ExecParams execParams, Mode runMode) throws Exception {
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
	public MetaIdentifierHolder submitDag(@RequestBody Dag dag, Mode runMode) throws Exception {
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
	public MetaIdentifierHolder submitDag(String uuid, String version, ExecParams execParams, String metaType, Mode runMode)
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

	public List<MetaIdentifierHolder> submitDagWithParamset(String dagUuid, String version, ExecParams execParams, Mode runMode)
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
	public MetaIdentifierHolder submitDag(String dagUuid, ExecParams execParams, String inputType, Mode runMode) throws Exception {
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

	public List<MetaIdentifierHolder> submitDagWithParamset(@RequestBody Dag dag, ExecParams execParams, Mode runMode)
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

		if (execParams != null && execParams.getParamInfo() != null && !execParams.getParamInfo().isEmpty()) {
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
	public MetaIdentifierHolder submitDag(@RequestBody Dag dag, DagExec dagExec, ExecParams execParams, Mode runMode)
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
		parseRunDagServiceImpl.setDagServiceImpl(this);
		parseRunDagServiceImpl.setTaskThreadMap(taskThreadMap);
		parseRunDagServiceImpl.setCommonServiceImpl(commonServiceImpl);
		parseRunDagServiceImpl.setSessionContext(sessionHelper.getSessionContext());
		parseRunDagServiceImpl.setRunMode(runMode);
		
		FutureTask<String> futureTask = new FutureTask<String>(parseRunDagServiceImpl);
		dagExecutor.execute(futureTask);
		taskThreadMap.put("Dag_" + dagExec.getUuid(), futureTask);

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
	 * @throws JsonProcessingException **********************/
	/*public Dag resolveName(Dag dag) throws JsonProcessingException {

		if (dag == null) {
			return null;
		}

		if (dag.getCreatedBy() != null) {
			String createdByRefUuid = dag.getCreatedBy().getRef().getUuid();
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			dag.getCreatedBy().getRef().setName(user.getName());
		}
		if (dag.getAppInfo() != null) {
			for (int i = 0; i < dag.getAppInfo().size(); i++) {
				String appUuid = dag.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				dag.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		for (int i = 0; i < dag.getStages().size(); i++) {
			for (int j = 0; j < dag.getStages().get(i).getTasks().size(); j++) {
				for (int k = 0; k < dag.getStages().get(i).getTasks().get(j).getOperators().size(); k++) {
					MetaType operatorInfoRefType = dag.getStages().get(i).getTasks().get(j).getOperators().get(k)
							.getOperatorInfo().getRef().getType();
					String operatorInfoRefUuid = dag.getStages().get(i).getTasks().get(j).getOperators().get(k)
							.getOperatorInfo().getRef().getUuid();
					if (operatorInfoRefType.toString().equalsIgnoreCase(MetaType.map.toString())) {
						Map mapDO = (Map) commonServiceImpl.getLatestByUuid(operatorInfoRefUuid, MetaType.map.toString());
						String mapName = mapDO.getName();
						dag.getStages().get(i).getTasks().get(j).getOperators().get(k).getOperatorInfo().getRef()
								.setName(mapName);
					}
					if (operatorInfoRefType.toString().equalsIgnoreCase(MetaType.load.toString())) {
						Load loadDO = (Load) commonServiceImpl.getLatestByUuid(operatorInfoRefUuid, MetaType.load.toString());
						String loadName = loadDO.getName();
						dag.getStages().get(i).getTasks().get(j).getOperators().get(k).getOperatorInfo().getRef()
								.setName(loadName);
					}
					if (operatorInfoRefType == MetaType.dq) {
						DataQual dataQualDO = (DataQual) commonServiceImpl.getLatestByUuid(operatorInfoRefUuid, MetaType.dq.toString());
						String dataQualName = dataQualDO.getName();
						dag.getStages().get(i).getTasks().get(j).getOperators().get(k).getOperatorInfo().getRef()
								.setName(dataQualName);
					}
					if (operatorInfoRefType == MetaType.dqgroup) {
						DataQualGroup dataQualGroupDO = (DataQualGroup) commonServiceImpl.getLatestByUuid(operatorInfoRefUuid, MetaType.dqgroup.toString());
						String dqGroupName = dataQualGroupDO.getName();
						dag.getStages().get(i).getTasks().get(j).getOperators().get(k).getOperatorInfo().getRef()
								.setName(dqGroupName);
					}
				}
			}

		}
		return dag;
	}*/

	/********************** UNUSED **********************/
	/*public List<Dag> findDagbyDatapod(String datapodUUID) throws JsonProcessingException {
		List<com.inferyx.framework.domain.Map> mapList = mapServiceImpl.findMapByDatapod(datapodUUID);
		List<Dag> result = null;
		result = new ArrayList<>();
		for (com.inferyx.framework.domain.Map m : mapList) {
			String mapUUID = m.getUuid();
			Aggregation dagAggr = newAggregation(
					match(Criteria.where("stages.tasks.operators.operatorInfo.ref.uuid").is(mapUUID)),
					group("uuid").max("version").as("version"));
			AggregationResults<Dag> dagResults = mongoTemplate.aggregate(dagAggr, "dag", Dag.class);
			List<Dag> dagList = dagResults.getMappedResults();
			for (Dag d : dagList) {
				Dag dagLatest = (Dag) commonServiceImpl.getOneByUuidAndVersion(d.getId(), d.getVersion(), MetaType.dag.toString());
				result.add(dagLatest);
			}
		}
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<Dag> resolveName(List<Dag> dag) {
		List<Dag> dagList = new ArrayList<Dag>();
		for (Dag d : dag) {
			String createdByRefUuid = d.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			d.getCreatedBy().getRef().setName(user.getName());
			dagList.add(d);
		}
		return dagList;
	}*/

	/********************** UNUSED **********************/
	/*public List<Dag> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid != null) {
			return iDagDao.findAllVersion(appUuid, uuid);
		} else
			return iDagDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public Dag getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid != null) {
			return iDagDao.findAsOf(appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
		} else
			return iDagDao.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/*public MetaIdentifierHolder saveAs(Dag dag) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();
		Dag dagNew = new Dag();
		dagNew.setName(dag.getName() + "_copy");
		dagNew.setActive(dag.getActive());
		dagNew.setDesc(dag.getDesc());
		dagNew.setTags(dag.getTags());
		dagNew.setStages(dag.getStages());
		Save(dagNew);
		ref.setType(MetaType.dag);
		ref.setUuid(dagNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

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

	/**
	 * Utility to populate refkeys given the data structure and a ref object
	 * 
	 * @param refKeyMap
	 * @param ref
	 */
	public static MetaIdentifier populateRefKeys(java.util.Map<String, MetaIdentifier> refKeyMap, MetaIdentifier ref,
			java.util.Map<String, MetaIdentifier> inputRefKeyMap) {
		if (ref == null) {
			return null;
		}
		if (refKeyMap == null) {
			refKeyMap = new HashMap<>();
		}
		if (inputRefKeyMap != null && inputRefKeyMap.containsKey(ref.getUuid())
				&& inputRefKeyMap.get(ref.getUuid()).getVersion() != null) {
			refKeyMap.put(ref.getType() + "_" + ref.getUuid(), inputRefKeyMap.get(ref.getUuid()));
			return inputRefKeyMap.get(ref.getUuid());
		} else if (refKeyMap.containsKey(ref.getType() + "_" + ref.getUuid())
				&& refKeyMap.get(ref.getType() + "_" + ref.getUuid()).getVersion() != null) {
			return refKeyMap.get(ref.getType() + "_" + ref.getUuid());
		} // else
		refKeyMap.put(ref.getType() + "_" + ref.getUuid(), ref);
		return ref;

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
		if (execParams != null) {
			inputRefKeyList = execParams.getRefKeyList();
		} else {
			execParams = new ExecParams();
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

			// Below code need reformatting, the setting should be done
			// while doing getter action

			// Traverse task & Populate RefKeys
			if (indvTask.getOperators() != null && indvTask.getOperators().get(0).getOperatorInfo() != null) {
				if (indvTask.getOperators().get(0).getOperatorInfo().getRef().getType().equals(MetaType.map) || indvTask
						.getOperators().get(0).getOperatorInfo().getRef().getType().equals(MetaType.mapiter)) {

					mapRef = indvTask.getOperators().get(0).getOperatorInfo().getRef();
					Map map = (Map) daoRegister.getRefObject(populateRefKeys(refKeys, mapRef, inputRefKeys));

					// Setting the Version for Map Object
					sourceRef = map.getSource().getRef();
					targetRef = map.getTarget().getRef();
					daoRegister.getRefObject(populateRefKeys(refKeys, sourceRef, inputRefKeys));
					daoRegister.getRefObject(populateRefKeys(refKeys, targetRef, inputRefKeys));

					for (AttributeMap attrMap : map.getAttributeMap()) {
						targetAttrRef = attrMap.getTargetAttr().getRef();
						daoRegister.getRefObject(populateRefKeys(refKeys, targetAttrRef, inputRefKeys));
						sourceAttrRef = attrMap.getSourceAttr().getRef();
						daoRegister.getRefObject(populateRefKeys(refKeys, sourceAttrRef, inputRefKeys));
					}
				} else if (indvTask.getOperators().get(0).getOperatorInfo().getRef().getType().equals(MetaType.load)) {// MetaType
																														// load
					loadRef = indvTask.getOperators().get(0).getOperatorInfo().getRef();

				} else if (indvTask.getOperators().get(0).getOperatorInfo().getRef().getType().equals(MetaType.dag)) {// MetaType
																														// dag
					secondaryDagRef = indvTask.getOperators().get(0).getOperatorInfo().getRef();
					populateRefKeys(refKeys, secondaryDagRef, inputRefKeys); // PopuPopulatelate
																				// refKeys
				} else if (indvTask.getOperators().get(0).getOperatorInfo().getRef().getType().equals(MetaType.dq)) {// MetaType

					DataQualExec dataqualExec = new DataQualExec();
					dataqualExec.setBaseEntity();
				} else if (indvTask.getOperators().get(0).getOperatorInfo().getRef().getType().equals(MetaType.rule)) {// MetaType

					RuleExec ruleExec = new RuleExec();
					ruleExec.setBaseEntity();
					/*
					 * execIdentifier = new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(),
					 * ruleExec.getVersion());
					 * taskExec.getOperators().get(0).getOperatorInfo().setRef(execIdentifier);
					 */
				} else if (indvTask.getOperators().get(0).getOperatorInfo().getRef().getType()
						.equals(MetaType.rulegroup)) {// MetaType

					RuleGroupExec ruleGroupExec = new RuleGroupExec();
					ruleGroupExec.setBaseEntity();
					/*
					 * execIdentifier = new MetaIdentifier(MetaType.rulegroupExec,
					 * ruleGroupExec.getUuid(), ruleGroupExec.getVersion());
					 * taskExec.getOperators().get(0).getOperatorInfo().setRef(execIdentifier);
					 */
				} else if (indvTask.getOperators().get(0).getOperatorInfo().getRef().getType()
						.equals(MetaType.train)) {
					TrainExec trainExec = new TrainExec();
					trainExec.setBaseEntity();
				} else if (indvTask.getOperators().get(0).getOperatorInfo().getRef().getType()
						.equals(MetaType.predict)) {
					PredictExec predictExec = new PredictExec();
					predictExec.setBaseEntity();
				} else if (indvTask.getOperators().get(0).getOperatorInfo().getRef().getType()
						.equals(MetaType.simulate)) {
					SimulateExec simulateExec = new SimulateExec();
					simulateExec.setBaseEntity();
				} else if (indvTask.getOperators().get(0).getOperatorInfo().getRef().getType()
						.equals(MetaType.recon)) {
					ReconExec reconExec = new ReconExec();
					reconExec.setBaseEntity();
				} else if (indvTask.getOperators().get(0).getOperatorInfo().getRef().getType()
						.equals(MetaType.recongroup)) {
					ReconGroupExec reconGroupExec = new ReconGroupExec();
					reconGroupExec.setBaseEntity();
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

	@SuppressWarnings({ "unused", "unlikely-arg-type", "unchecked" })
	public DagExec parseDagExec(Dag dag, DagExec dagExec) throws Exception {
		mapServiceImpl.setRunMode(runMode);
		if (dagExec == null) {
			System.out.println("Nothing to parse. Aborting parseDagExec");
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
		HashMap<String, String> otherParams = new HashMap<>();
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
				Task indvTask = DagExecUtil.getTaskFromStage(stage, indvExecTask.getTaskId());
				MetaIdentifier ref = indvTask.getOperators().get(0).getOperatorInfo().getRef();
				List<Operator> operatorList = new ArrayList<>();
				Operator operator = new Operator();
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
				operator.setOperatorParams(indvTask.getOperators().get(0).getOperatorParams());
				if (ref.getType().equals(MetaType.map)) {
					java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
							.convertRefKeyListToMap(execParams.getRefKeyList());
					MapExec mapExec = new MapExec();
					MetaIdentifierHolder mapRef = new MetaIdentifierHolder();
					mapRef.setRef(new MetaIdentifier(MetaType.map, ref.getUuid(), ref.getVersion()));
					mapExec.setDependsOn(mapRef);
					mapExec.setBaseEntity();

					try {
						MetaIdentifier mapExecIdentifier = new MetaIdentifier(MetaType.mapExec, mapExec.getUuid(),
								mapExec.getVersion());
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(mapExecIdentifier);
						mapExec = mapServiceImpl.generateSql(ref.getUuid(), ref.getVersion(), mapExec, dagExec, stage,
								indvExecTask, datapodList, refKeyMap, otherParams, execParams, Mode.BATCH);
						mapExec.setRefKeyList(execParams.getRefKeyList());
						builder = new StringBuilder(mapExec.getExec());
						if (mapExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
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
						//mapExecServiceImpl.save(mapExec);
						commonServiceImpl.save(MetaType.mapExec.toString(), mapExec);
					}
				} else if (ref.getType().equals(MetaType.rule)) {
					java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
							.convertRefKeyListToMap(execParams.getRefKeyList());
					RuleExec ruleExec = new RuleExec();
					MetaIdentifier ruleIdentifier = new MetaIdentifier(MetaType.rule, ref.getUuid(), ref.getVersion());
					ruleExec.setDependsOn(new MetaIdentifierHolder(ruleIdentifier));
					ruleExec.setName(ref.getName());
					ruleExec.setBaseEntity();
					try {
						/*ruleExec = ruleServiceImpl.create(ref.getUuid(), ref.getVersion(), refKeyMap, execParams,
								datapodList, dagExec, ruleExec);*/
						ruleExec = ruleServiceImpl.create(ref.getUuid(), ref.getVersion(), ruleExec, refKeyMap, execParams, datapodList, dagExec);
						ruleExec = ruleServiceImpl.parse(ruleExec.getUuid(), ruleExec.getVersion(), refKeyMap, datapodList, dagExec, runMode);
						ruleExec.setRefKeyList(execParams.getRefKeyList());
						MetaIdentifier ruleExecIdentifier = new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(),
								ruleExec.getVersion());
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(ruleExecIdentifier);
						builder = new StringBuilder(ruleExec.getExec());
						if (ruleExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
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
					} finally {
						//ruleExecServiceImpl.save(ruleExec);
						commonServiceImpl.save(MetaType.ruleExec.toString(), ruleExec);
					}
				} else if (ref.getType().equals(MetaType.rulegroup)) {
					java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
							.convertRefKeyListToMap(execParams.getRefKeyList());
					RuleGroupExec ruleGroupExec = new RuleGroupExec();
					MetaIdentifier ruleGroupExecIdentifier = new MetaIdentifier(MetaType.rulegroup, ref.getUuid(),
							ref.getVersion());
					ruleGroupExec.setDependsOn(new MetaIdentifierHolder(ruleGroupExecIdentifier));
					ruleGroupExec.setName(ref.getName());
					ruleGroupExec.setBaseEntity();
					ruleGroupExecIdentifier = new MetaIdentifier(MetaType.rulegroupExec, ruleGroupExec.getUuid(),
							ruleGroupExec.getVersion());
					try {
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(ruleGroupExecIdentifier);
//						MetaIdentifier ruleGroupExecMeta = ruleGroupServiceImpl.execute(ref.getUuid(), ref.getVersion(),
//								execParams, ruleGroupExec);
						ruleGroupExec = ruleGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), null,
								datapodList, ruleGroupExec, dagExec);
						ruleGroupExec = ruleGroupServiceImpl.parse(ruleGroupExec.getUuid(), ruleGroupExec.getVersion(), refKeyMap, datapodList, dagExec, runMode);
						
						//ruleGroupExec = (RuleGroupExec) daoRegister.getRefObject(ruleGroupExecMeta);
						if (ruleGroupExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
							throw new Exception();
						}
					} catch (Exception e) {
						logger.error("Exception while creating ruleGroupExec : " + ruleGroupExec);
						Status failedStatus = new Status(Status.Stage.Failed, new Date());
						List<Status> statusList = indvExecTask.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						statusList.remove(failedStatus);
						statusList.add(failedStatus);
						e.printStackTrace();
					}
				} else if (ref.getType().equals(MetaType.dq)) {
					java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
							.convertRefKeyListToMap(execParams.getRefKeyList());
					DataQualExec dqExec = new DataQualExec();
					dqExec.setDependsOn(
							new MetaIdentifierHolder(new MetaIdentifier(MetaType.dq, ref.getUuid(), ref.getVersion())));
					dqExec.setName(ref.getName());
					dqExec.setBaseEntity();
					try {
						MetaIdentifier dqExecIdentifier = new MetaIdentifier(MetaType.dqExec, dqExec.getUuid(),
								dqExec.getVersion());
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(dqExecIdentifier);
						/*dqExec = dataQualServiceImpl.create(ref.getUuid(), ref.getVersion(), dqExec, refKeyMap,
								datapodList, dagExec);*/
						dqExec = dataQualServiceImpl.create(ref.getUuid(),ref.getVersion(),dqExec,refKeyMap,datapodList,dagExec);
						dqExec =  (DataQualExec)dataQualServiceImpl.parse(dqExec.getUuid(), dqExec.getVersion(), refKeyMap, datapodList, dagExec, runMode);
						dqExec.setRefKeyList(execParams.getRefKeyList());
						builder = new StringBuilder(dqExec.getExec());
						if (dqExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
							throw new Exception();
						}
					} catch (Exception e) {
						Status failedStatus = new Status(Status.Stage.Failed, new Date());
						List<Status> statusList = indvExecTask.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						if (Helper.getLatestStatus(statusList).equals(failedStatus)) {
							statusList.remove(statusList.size() - 1);
						}
						statusList.add(failedStatus);
						e.printStackTrace();
					} finally {
						//dataQualExecServiceImpl.save(dqExec);
						commonServiceImpl.save(MetaType.dqExec.toString(), dqExec);
					}
				} else if (ref.getType().equals(MetaType.dqgroup)) {
					/*
					 * java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
					 * .convertRefKeyListToMap(indvExecTask.getRefKeyList());
					 */
					java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
							.convertRefKeyListToMap(execParams.getRefKeyList());
					DataQualGroupExec dqGroupExec = new DataQualGroupExec();
					MetaIdentifierHolder dqGroupRef = new MetaIdentifierHolder(
							new MetaIdentifier(MetaType.dqgroup, ref.getUuid(), ref.getVersion()));
					dqGroupExec.setDependsOn(dqGroupRef);
					dqGroupExec.setName(ref.getName());
					dqGroupExec.setBaseEntity();
					try {
						MetaIdentifier dqGroupExecIdentifier = new MetaIdentifier(MetaType.dqgroupExec,
								dqGroupExec.getUuid(), dqGroupExec.getVersion());
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(dqGroupExecIdentifier);
						/*dqGroupExec = dataQualGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), null,
								datapodList, dqGroupExec, dagExec);*/
						dqGroupExec = dataQualGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), null,
								datapodList, dqGroupExec, dagExec);
						dqGroupExec = dataQualGroupServiceImpl.parse(dqGroupExecIdentifier,refKeyMap, datapodList, dagExec, runMode);
						
						if (dqGroupExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
							throw new Exception();
						}
					} catch (Exception e) {
						Status failedStatus = new Status(Status.Stage.Failed, new Date());
						List<Status> statusList = indvExecTask.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						if (Helper.getLatestStatus(statusList).equals(failedStatus)) {
							statusList.remove(statusList.size() - 1);
						}
						statusList.add(failedStatus);
						e.printStackTrace();
					} finally {
						//dataQualGroupExecServiceImpl.save(dqGroupExec);
						commonServiceImpl.save(MetaType.dqgroupExec.toString(), dqGroupExec);
					}
				} else if (ref.getType().equals(MetaType.profile)) {
					java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
							.convertRefKeyListToMap(execParams.getRefKeyList());
					ProfileExec profileExec = new ProfileExec();
					MetaIdentifierHolder profileRef = new MetaIdentifierHolder();
					profileRef.setRef(new MetaIdentifier(MetaType.profile, ref.getUuid(), ref.getVersion()));
					profileExec.setDependsOn(profileRef);
					profileExec.setName(ref.getName());
					profileExec.setBaseEntity();
					try {
						MetaIdentifier profileExecIdentifier = new MetaIdentifier(MetaType.profileExec,
								profileExec.getUuid(), profileExec.getVersion());
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(profileExecIdentifier);
					/*	profileExec = profileServiceImpl.create(ref.getUuid(), ref.getVersion(), null, refKeyMap,
								datapodList, dagExec);*/
						profileExec = profileServiceImpl.create(ref.getUuid(),ref.getVersion(),profileExec,refKeyMap,datapodList,dagExec);
						profileExec =  (ProfileExec)profileServiceImpl.parse(profileExec.getUuid(), profileExec.getVersion(), refKeyMap, datapodList, dagExec, runMode);
				
						profileExec.setRefKeyList(execParams.getRefKeyList());
						builder = new StringBuilder(profileExec.getExec());
						if (profileExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
							throw new Exception();
						}
					} catch (Exception e) {
						Status failedStatus = new Status(Status.Stage.Failed, new Date());
						List<Status> statusList = indvExecTask.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						if (Helper.getLatestStatus(statusList).equals(failedStatus)) {
							statusList.remove(statusList.size() - 1);
						}
						statusList.add(failedStatus);
						e.printStackTrace();
					} finally {
						//profileExecServiceImpl.save(profileExec);
						commonServiceImpl.save(MetaType.profileExec.toString(), profileExec);
					}
				} else if (ref.getType().equals(MetaType.profilegroup)) {
					java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
							.convertRefKeyListToMap(execParams.getRefKeyList());
					ProfileGroupExec profileGroupExec = new ProfileGroupExec();
					MetaIdentifierHolder profileGroupRef = new MetaIdentifierHolder(
							new MetaIdentifier(MetaType.profilegroup, ref.getUuid(), ref.getVersion()));
					profileGroupExec.setDependsOn(profileGroupRef);
					profileGroupExec.setName(ref.getName());
					profileGroupExec.setBaseEntity();
					try {
						MetaIdentifier profileGroupExecIdentifier = new MetaIdentifier(MetaType.profilegroupExec,
								profileGroupExec.getUuid(), profileGroupExec.getVersion());
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(profileGroupExecIdentifier);
						/*profileGroupExec = profileGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), execParams,
								datapodList, dagExec, profileGroupExec);
					*/	
						profileGroupExec = profileGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), null,
								datapodList, dagExec,profileGroupExec);
						profileGroupExec = (ProfileGroupExec) profileGroupServiceImpl.parse(profileGroupExecIdentifier.getUuid(),profileGroupExecIdentifier.getVersion(),refKeyMap, datapodList, dagExec, runMode);
						if (profileGroupExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
							throw new Exception();
						}
					} catch (Exception e) {
						Status failedStatus = new Status(Status.Stage.Failed, new Date());
						List<Status> statusList = indvExecTask.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						if (Helper.getLatestStatus(statusList).equals(failedStatus)) {
							statusList.remove(statusList.size() - 1);
						}
						statusList.add(failedStatus);
						e.printStackTrace();
					} finally {
						//profileGroupExecServiceImpl.save(profileGroupExec);
						commonServiceImpl.save(MetaType.profilegroupExec.toString(), profileGroupExec);
					}
				} /*else if (ref.getType().equals(MetaType.model)) {
					ModelExec modelExec = new ModelExec();
					MetaIdentifierHolder modelRef = new MetaIdentifierHolder();
					modelRef.setRef(new MetaIdentifier(MetaType.model, ref.getUuid(), ref.getVersion()));
					modelExec.setDependsOn(modelRef);
					modelExec.setBaseEntity();
					try {
						MetaIdentifier modelExecIdentifier = new MetaIdentifier(MetaType.modelExec, modelExec.getUuid(),
								modelExec.getVersion());
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(modelExecIdentifier);
						modelExec = modelServiceImpl.create(ref.getUuid(), ref.getVersion(), execParams, null,
								modelExec);
						if (modelExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
							throw new Exception();
						}
					} catch (Exception e) {
						Status failedStatus = new Status(Status.Stage.Failed, new Date());
						List<Status> statusList = indvExecTask.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						if (Helper.getLatestStatus(statusList).equals(failedStatus)) {
							statusList.remove(statusList.size() - 1);
						}
						statusList.add(failedStatus);
						e.printStackTrace();
						throw new Exception("Model creation failed");
					} finally {
						//modelExecServiceImpl.save(modelExec);
						commonServiceImpl.save(MetaType.modelExec.toString(), modelExec);
					}
				}*/ else if (ref.getType().equals(MetaType.load)) {
					java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
							.convertRefKeyListToMap(execParams.getRefKeyList());
					LoadExec loadExec = new LoadExec();
					MetaIdentifierHolder loadRef = new MetaIdentifierHolder();
					loadRef.setRef(new MetaIdentifier(MetaType.load, ref.getUuid(), ref.getVersion()));
					loadExec.setDependsOn(loadRef);
					loadExec.setBaseEntity();

					try {
						MetaIdentifier loadExecIdentifier = new MetaIdentifier(MetaType.loadExec, loadExec.getUuid(),
								loadExec.getVersion());
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(loadExecIdentifier);
						loadExec = loadServiceImpl.create(ref.getUuid(), ref.getVersion(), execParams, null, loadExec);
						//loadExec.setRefKeyList(execParams.getRefKeyList());
						//builder = new StringBuilder(loadExec.getExec());
						if (loadExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
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
					} finally {
						//loadExecServiceImpl.save(loadExec);
						commonServiceImpl.save(MetaType.loadExec.toString(), loadExec);
					}
				} else if (ref.getType().equals(MetaType.train)) {
					TrainExec trainExec = new TrainExec();
					MetaIdentifierHolder trainRef = new MetaIdentifierHolder();
					trainRef.setRef(new MetaIdentifier(MetaType.train, ref.getUuid(), ref.getVersion()));
					trainExec.setDependsOn(trainRef);
					trainExec.setBaseEntity();
					try {
						MetaIdentifier trainExecIdentifier = new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(),
								trainExec.getVersion());
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(trainExecIdentifier);
						Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString());
						Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), train.getDependsOn().getRef().getVersion(), train.getDependsOn().getRef().getType().toString());
						trainExec = modelServiceImpl.create(train, model, execParams, null,
								trainExec);
						if (trainExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
							throw new Exception();
						}
					} catch (Exception e) {
						Status failedStatus = new Status(Status.Stage.Failed, new Date());
						List<Status> statusList = indvExecTask.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						if (Helper.getLatestStatus(statusList).equals(failedStatus)) {
							statusList.remove(statusList.size() - 1);
						}
						statusList.add(failedStatus);
						e.printStackTrace();
						throw new Exception("Train exec creation failed");
					} finally {
						//modelExecServiceImpl.save(modelExec);
						commonServiceImpl.save(MetaType.trainExec.toString(), trainExec);
					}
				} else if (ref.getType().equals(MetaType.predict)) {
					PredictExec predictExec = new PredictExec();
					MetaIdentifierHolder predictRef = new MetaIdentifierHolder();
					predictRef.setRef(new MetaIdentifier(MetaType.predict, ref.getUuid(), ref.getVersion()));
					predictExec.setDependsOn(predictRef);
					predictExec.setBaseEntity();
					try {
						MetaIdentifier predictExecIdentifier = new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(),
								predictExec.getVersion());
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(predictExecIdentifier);
						Predict predict = (Predict) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString());
						predictExec = modelServiceImpl.create(predict, execParams, null, predictExec);
						if (predictExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
							throw new Exception();
						}
					} catch (Exception e) {
						Status failedStatus = new Status(Status.Stage.Failed, new Date());
						List<Status> statusList = indvExecTask.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						if (Helper.getLatestStatus(statusList).equals(failedStatus)) {
							statusList.remove(statusList.size() - 1);
						}
						statusList.add(failedStatus);
						e.printStackTrace();
						throw new Exception("Predict exec creation failed");
					} finally {
						//modelExecServiceImpl.save(modelExec);
						commonServiceImpl.save(MetaType.predictExec.toString(), predictExec);
					}
				} else if (ref.getType().equals(MetaType.simulate)) {
					SimulateExec simulateExec = new SimulateExec();
					MetaIdentifierHolder simulateRef = new MetaIdentifierHolder();
					simulateRef.setRef(new MetaIdentifier(MetaType.simulate, ref.getUuid(), ref.getVersion()));
					simulateExec.setDependsOn(simulateRef);
					simulateExec.setBaseEntity();
					try {
						MetaIdentifier simulateExecIdentifier = new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(),
								simulateExec.getVersion());
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(simulateExecIdentifier);
						Simulate simulate = (Simulate) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString());
						simulateExec = modelServiceImpl.create(simulate, execParams, null, simulateExec);
						if (simulateExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
							throw new Exception();
						}
					} catch (Exception e) {
						Status failedStatus = new Status(Status.Stage.Failed, new Date());
						List<Status> statusList = indvExecTask.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						if (Helper.getLatestStatus(statusList).equals(failedStatus)) {
							statusList.remove(statusList.size() - 1);
						}
						statusList.add(failedStatus);
						e.printStackTrace();
						throw new Exception("Simulate exec creation failed");
					} finally {
						//modelExecServiceImpl.save(modelExec);
						commonServiceImpl.save(MetaType.simulateExec.toString(), simulateExec);
					}
				} else if (ref.getType().equals(MetaType.recon)) {
					java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
							.convertRefKeyListToMap(execParams.getRefKeyList());
					ReconExec reconExec = new ReconExec();
					MetaIdentifier reconIdentifier = new MetaIdentifier(MetaType.recon, ref.getUuid(), ref.getVersion());
					reconExec.setDependsOn(new MetaIdentifierHolder(reconIdentifier));
					reconExec.setName(ref.getName());
					reconExec.setBaseEntity();
					try {
						reconExec = reconServiceImpl.create(ref.getUuid(), ref.getVersion(), reconExec, refKeyMap, datapodList, dagExec);
						reconExec = (ReconExec) reconServiceImpl.parse(reconExec.getUuid(), reconExec.getVersion(), refKeyMap, datapodList, dagExec, runMode);
						reconExec.setRefKeyList(execParams.getRefKeyList());
						MetaIdentifier reconExecIdentifier = new MetaIdentifier(MetaType.reconExec, reconExec.getUuid(),
								reconExec.getVersion());
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(reconExecIdentifier);
						builder = new StringBuilder(reconExec.getExec());
						if (reconExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
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
					} finally {
						//ruleExecServiceImpl.save(ruleExec);
						commonServiceImpl.save(MetaType.reconExec.toString(), reconExec);
					}
				} else if (ref.getType().equals(MetaType.recongroup)) {
					java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
							.convertRefKeyListToMap(execParams.getRefKeyList());
					ReconGroupExec reconGroupExec = new ReconGroupExec();
					MetaIdentifier reconGroupExecIdentifier = new MetaIdentifier(MetaType.recongroup, ref.getUuid(),
							ref.getVersion());
					reconGroupExec.setDependsOn(new MetaIdentifierHolder(reconGroupExecIdentifier));
					reconGroupExec.setName(ref.getName());
					reconGroupExec.setBaseEntity();
					reconGroupExecIdentifier = new MetaIdentifier(MetaType.recongroupExec, reconGroupExec.getUuid(),
							reconGroupExec.getVersion());
					try {
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(reconGroupExecIdentifier);
						reconGroupExec = reconGroupServiceImpl.create(ref.getUuid(), ref.getVersion(), null,
								datapodList, reconGroupExec, dagExec);
						reconGroupExec = reconGroupServiceImpl.parse(reconGroupExec.getUuid(), reconGroupExec.getVersion(), refKeyMap, datapodList, dagExec, runMode);
						
						if (reconGroupExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
							throw new Exception();
						}
					} catch (Exception e) {
						logger.error("Exception while creating reconGroupExec : " + reconGroupExec);
						Status failedStatus = new Status(Status.Stage.Failed, new Date());
						List<Status> statusList = indvExecTask.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						statusList.remove(failedStatus);
						statusList.add(failedStatus);
						e.printStackTrace();
					}
				} else if (ref.getType().equals(MetaType.operatortype)) {
					logger.info("Inside parseDagExec : MetaType operatorType ");
					java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
							.convertRefKeyListToMap(execParams.getRefKeyList());
					OperatorExec operatorExec = new OperatorExec();
					MetaIdentifier operatorTypeIdentifier = new MetaIdentifier(MetaType.operatortype, ref.getUuid(),
							ref.getVersion());
					operatorExec.setDependsOn(new MetaIdentifierHolder(operatorTypeIdentifier));
					operatorExec.setName(ref.getName());
					operatorExec.setBaseEntity();
					MetaIdentifier operatorExecIdentifier = new MetaIdentifier(MetaType.operatorExec, operatorExec.getUuid(),
							operatorExec.getVersion());
					try {
						indvExecTask.getOperators().get(0).getOperatorInfo().setRef(operatorExecIdentifier);
						operatorExec = operatorServiceImpl.create(ref.getUuid(), ref.getVersion(), MetaType.operatortype, MetaType.operatorExec, operatorExec, 
								refKeyMap, datapodList, dagExec);
						commonServiceImpl.save(MetaType.operatorExec.toString(), operatorExec);
//						operatorExec = reconGroupServiceImpl.parse(operatorExec.getUuid(), operatorExec.getVersion(), refKeyMap, datapodList, dagExec, runMode);
						
						if (operatorExec.getStatusList().contains(new Status(Status.Stage.Failed, new Date()))) {
							throw new Exception();
						}
					} catch (Exception e) {
						logger.error("Exception while creating operatorExec : " + operatorExec);
						Status failedStatus = new Status(Status.Stage.Failed, new Date());
						List<Status> statusList = indvExecTask.getStatusList();
						if (statusList == null) {
							statusList = new ArrayList<Status>();
						}
						statusList.remove(failedStatus);
						statusList.add(failedStatus);
						e.printStackTrace();
					}
				}


				/*if (builder == null) {
					continue;
				}
*/
				// Set Exec attr
				//logger.info("sql: "+builder.toString());

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

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> dagList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for (BaseEntity dag : dagList) {
			BaseEntity baseEntity = new BaseEntity();
			String id = dag.getId();
			String uuid = dag.getUuid();
			String version = dag.getVersion();
			String name = dag.getName();
			String desc = dag.getDesc();
			String published=dag.getPublished();
			MetaIdentifierHolder createdBy = dag.getCreatedBy();
			String createdOn = dag.getCreatedOn();
			String[] tags = dag.getTags();
			String active = dag.getActive();
			List<MetaIdentifierHolder> appInfo = dag.getAppInfo();
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
	}*/

	public Dag findDagByDagExec(String dagExecUuid) throws JsonProcessingException {
		//DagExec dagExec = dagExecServiceImpl.findLatestByUuid(dagExecUuid);
		DagExec dagExec = (DagExec) commonServiceImpl.getLatestByUuid(dagExecUuid, MetaType.dagExec.toString());
		return (Dag) commonServiceImpl.getOneByUuidAndVersion(dagExec.getDependsOn().getUuid(), dagExec.getDependsOn().getVersion(), MetaType.dag.toString());
	}

	/*public Dag setDAGOnHold(String uuid, String version, String stageId) throws Exception {
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
				Status stageOnHoldStatus = new Status(Status.Stage.OnHold, new Date());
				List<Status> stageStatusList = dag.getStages().get(i).getStatusList();
				stageStatusList.remove(stageOnHoldStatus);
				stageStatusList.add(stageOnHoldStatus);
				dag.getStages().get(i).setStatusList(stageStatusList);
				for (int j = 0; j < dag.getStages().get(i).getTasks().size(); j++) {
					int lastDag = dag.getStages().get(i).getTasks().get(j).getStatusList().size() - 1;
					if (dag.getStages().get(i).getTasks().get(j).getStatusList().get(lastDag).getStage()
							.equals(Status.Stage.NotStarted)) {
						throw new Exception("Task not started.");
					} else {
						Status taskOnHoldStatus = new Status(Status.Stage.OnHold, new Date());
						List<Status> taskStatusList = dag.getStages().get(i).getTasks().get(j).getStatusList();
						taskStatusList.remove(taskOnHoldStatus);
						taskStatusList.add(taskOnHoldStatus);
						dag.getStages().get(i).getTasks().get(j).setStatusList(taskStatusList);
					}
				}
			}
		}

		return iDagDao.save(dag);
	}

	public Dag setDAGResume(String uuid, String version, String stageId) throws Exception {
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
				Status stageOnHoldStatus = new Status(Status.Stage.Resume, new Date());
				List<Status> stageStatusList = dag.getStages().get(i).getStatusList();
				stageStatusList.remove(stageOnHoldStatus);
				stageStatusList.add(stageOnHoldStatus);
				dag.getStages().get(i).setStatusList(stageStatusList);
				for (int j = 0; j < dag.getStages().get(i).getTasks().size(); j++) {
					int lastDag = dag.getStages().get(i).getTasks().get(j).getStatusList().size() - 1;
					if (dag.getStages().get(i).getTasks().get(j).getStatusList().get(lastDag).getStage()
							.equals(Status.Stage.NotStarted)) {
						throw new Exception("Task not started.");
					} else {
						Status taskOnHoldStatus = new Status(Status.Stage.Resume, new Date());
						List<Status> taskStatusList = dag.getStages().get(i).getTasks().get(j).getStatusList();
						taskStatusList.remove(taskOnHoldStatus);
						taskStatusList.add(taskOnHoldStatus);
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
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.Resume.toString().toLowerCase())){
					return objectWriter.writeValueAsString(dagExecServiceImpl.setStageResume(uuid, version, stageId));
				}
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.OnHold.toString().toLowerCase())){
					return objectWriter.writeValueAsString(dagExecServiceImpl.setTaskResume(uuid, version, stageId, taskId));
				}
			}else
				logger.info("Empty stageId, can not perform the operation.");
			if (!StringUtils.isBlank(stageId) && StringUtils.isBlank(taskId)) {
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.Resume.toString().toLowerCase())){
					return objectWriter.writeValueAsString(dagExecServiceImpl.setStageResume(uuid, version, stageId));
				}
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.OnHold.toString().toLowerCase())){
					return objectWriter.writeValueAsString(dagExecServiceImpl.setStageOnHold(uuid, version, stageId));
				}
			}else
				logger.info("Empty stageId, can not perform the operation.");
			if (StringUtils.isBlank(stageId) && StringUtils.isBlank(taskId)) {
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.Resume.toString().toLowerCase())){
					return objectWriter.writeValueAsString(setDAGResume(uuid, version, stageId));
				}
				if(status.toLowerCase().equalsIgnoreCase(Status.Stage.OnHold.toString().toLowerCase())){
					return objectWriter.writeValueAsString(setDAGOnHold(uuid, version, stageId));
				}
			}
		} else {
			logger.info("Empty status, can not perform the operation.");
		}
		return null;

	}*/
	
	public void restart(String dagExecUuid,String dagExecVersion, Mode runMode) throws Exception{
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

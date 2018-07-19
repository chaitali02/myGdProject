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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.apache.spark.ml.param.ParamMap;
import org.codehaus.jettison.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.SimulateExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Task;
import com.inferyx.framework.domain.TaskExec;
import com.inferyx.framework.domain.TaskOperator;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;

@Service
public class TaskServiceImpl implements Callable<String> {	
	
	private DagExecServiceImpl dagExecServiceImpl;
	private CommonServiceImpl<?> commonServiceImpl;
	private LoadServiceImpl loadServiceImpl;
	private DatapodServiceImpl datapodServiceImpl;
	private CustomOperatorServiceImpl operatorServiceImpl;
	private DagServiceImpl dagServiceImpl;
	private HDFSInfo hdfsInfo;
	private MapServiceImpl mapServiceImpl;
	private DataStoreServiceImpl dataStoreServiceImpl;
	private ParamSetServiceImpl paramSetServiceImpl;
	private String dagExecUUID;	 	
	private String stageId;
    private String taskId;
	private List<String> dependsOn;
//	private String taskExec;
	private OrderKey datapodKey;
	private String filePath;
	private String dagExecVer;
	private DataStoreServiceImpl iDataStore;
	private MetaIdentifierHolder operatorInfo;
	private String operatorType;
	private Task indvTask;
	private boolean killThread = false;
//	private static HashMap<String,Integer> rowSeqMap = new HashMap<String,Integer>();
//	private static HashMap<String,Integer> colSeqMap = new HashMap<String,Integer>();
//	private static HashMap<Integer,String> rowSeqFinalMap = new HashMap<Integer,String>();
//	private static HashMap<Integer,String> colSeqFinalMap = new HashMap<Integer,String>();
//	private Dataset<Row> dfTask;
	private ExecParams execParams;
	private Dag dag;
	private DataSourceFactory datasourceFactory;
	private ExecutorFactory execFactory;
	private DataQualServiceImpl dataqualServiceImpl;
	private DataQualExecServiceImpl dataqualExecServiceImpl;
	private DataQualGroupServiceImpl dataqualGroupServiceImpl;
	private DataQualGroupExecServiceImpl dataqualGroupExecServiceImpl;
	private RuleServiceImpl ruleServiceImpl;
	private RuleExecServiceImpl ruleExecServiceImpl;
	private RuleGroupServiceImpl ruleGroupServiceImpl;
	private RuleGroupExecServiceImpl ruleGroupExecServiceImpl;

	private ProfileServiceImpl profileServiceImpl;
	private ProfileExecServiceImpl profileExecServiceImpl;
	private ProfileGroupServiceImpl profileGroupServiceImpl;
	private ProfileGroupExecServiceImpl profileGroupExecServiceImpl;

	private ModelServiceImpl modelServiceImpl;
	private ModelExecServiceImpl modelExecServiceImpl;
	private String name;
	private RunMode runMode;
	private SessionContext sessionContext;
	private ReconServiceImpl reconServiceImpl;
	private ReconGroupServiceImpl reconGroupServiceImpl;
	
	static final Logger logger = Logger.getLogger(TaskServiceImpl.class);
	
	/**
	 * @Ganesh
	 *
	 * @return the reconServiceImpl
	 */
	public ReconServiceImpl getReconServiceImpl() {
		return reconServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param reconServiceImpl the reconServiceImpl to set
	 */
	public void setReconServiceImpl(ReconServiceImpl reconServiceImpl) {
		this.reconServiceImpl = reconServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the reconGroupServiceImpl
	 */
	public ReconGroupServiceImpl getReconGroupServiceImpl() {
		return reconGroupServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param reconGroupServiceImpl the reconGroupServiceImpl to set
	 */
	public void setReconGroupServiceImpl(ReconGroupServiceImpl reconGroupServiceImpl) {
		this.reconGroupServiceImpl = reconGroupServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the paramSetServiceImpl
	 */
	public ParamSetServiceImpl getParamSetServiceImpl() {
		return paramSetServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param paramSetServiceImpl the paramSetServiceImpl to set
	 */
	public void setParamSetServiceImpl(ParamSetServiceImpl paramSetServiceImpl) {
		this.paramSetServiceImpl = paramSetServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the sessionContext
	 */
	public SessionContext getSessionContext() {
		return sessionContext;
	}

	/**
	 * @Ganesh
	 *
	 * @param sessionContext the sessionContext to set
	 */
	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	public ExecutorFactory getExecFactory() {
		return execFactory;
	}

	public void setExecFactory(ExecutorFactory execFactory) {
		this.execFactory = execFactory;
	}	
	
	public DataStoreServiceImpl getDataStoreServiceImpl() {
		return dataStoreServiceImpl;
	}

	public void setDataStoreServiceImpl(DataStoreServiceImpl dataStoreServiceImpl) {
		this.dataStoreServiceImpl = dataStoreServiceImpl;
	}
	public DataSourceFactory getDatasourceFactory() {
		return datasourceFactory;
	}

	public void setDatasourceFactory(DataSourceFactory datasourceFactory) {
		this.datasourceFactory = datasourceFactory;
	}

	public MetadataUtil getDaoRegister() {
		return daoRegister;
	}

	public void setDaoRegister(MetadataUtil daoRegister) {
		this.daoRegister = daoRegister;
	}

	private MetadataUtil daoRegister;		
		
	public CustomOperatorServiceImpl getOperatorServiceImpl() {
		return operatorServiceImpl;
	}

	public void setOperatorServiceImpl(CustomOperatorServiceImpl operatorServiceImpl) {
		this.operatorServiceImpl = operatorServiceImpl;
	}

	public DatapodServiceImpl getDatapodServiceImpl() {
		return datapodServiceImpl;
	}

	public void setDatapodServiceImpl(DatapodServiceImpl datapodServiceImpl) {
		this.datapodServiceImpl = datapodServiceImpl;
	}

	public ProfileServiceImpl getProfileServiceImpl() {
		return profileServiceImpl;
	}

	public void setProfileServiceImpl(ProfileServiceImpl profileServiceImpl) {
		this.profileServiceImpl = profileServiceImpl;
	}

	public ProfileExecServiceImpl getProfileExecServiceImpl() {
		return profileExecServiceImpl;
	}

	public void setProfileExecServiceImpl(ProfileExecServiceImpl profileExecServiceImpl) {
		this.profileExecServiceImpl = profileExecServiceImpl;
	}

	public ProfileGroupServiceImpl getProfileGroupServiceImpl() {
		return profileGroupServiceImpl;
	}

	public void setProfileGroupServiceImpl(ProfileGroupServiceImpl profileGroupServiceImpl) {
		this.profileGroupServiceImpl = profileGroupServiceImpl;
	}

	public ProfileGroupExecServiceImpl getProfileGroupExecServiceImpl() {
		return profileGroupExecServiceImpl;
	}

	public void setProfileGroupExecServiceImpl(ProfileGroupExecServiceImpl profileGroupExecServiceImpl) {
		this.profileGroupExecServiceImpl = profileGroupExecServiceImpl;
	}

	public String getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(String operatorType) {
		this.operatorType = operatorType;
	}

	public boolean isKillThread() {
		return killThread;
	}

	public void setKillThread(boolean killThread) {
		this.killThread = killThread;
	}

	public LoadServiceImpl getLoadServiceImpl() {
		return loadServiceImpl;
	}

	public void setLoadServiceImpl(LoadServiceImpl loadServiceImpl) {
		this.loadServiceImpl = loadServiceImpl;
	}

	public Task getIndvTask() {
		return indvTask;
	}

	public void setIndvTask(Task indvTask) {
		this.indvTask = indvTask;
	}

	public MetaIdentifierHolder getOperatorInfo() {
		return operatorInfo;
	}

	public void setOperatorInfo(MetaIdentifierHolder operatorInfo) {
		this.operatorInfo = operatorInfo;
	}

	public CommonServiceImpl<?> getCommonServiceImpl() {
		return commonServiceImpl;
	}

	public void setCommonServiceImpl(CommonServiceImpl<?> commonServiceImpl) {
		this.commonServiceImpl = commonServiceImpl;
	}

	public DagExecServiceImpl getDagExecServiceImpl() {
		return dagExecServiceImpl;
	}

	public void setDagExecServiceImpl(DagExecServiceImpl dagExecServiceImpl) {
		this.dagExecServiceImpl = dagExecServiceImpl;
	}

	public DagServiceImpl getDagServiceImpl() {
		return dagServiceImpl;
	}

	public void setDagServiceImpl(DagServiceImpl dagServiceImpl) {
		this.dagServiceImpl = dagServiceImpl;
	}
	
	public HDFSInfo getHdfsInfo() {
		return hdfsInfo;
	}

	public void setHdfsInfo(HDFSInfo hdfsInfo) {
		this.hdfsInfo = hdfsInfo;
	}

	public DataStoreServiceImpl getiDataStore() {
		return iDataStore;
	}

	public void setiDataStore(DataStoreServiceImpl iDataStore) {
		this.iDataStore = iDataStore;
	}

	public String getDagExecUUID() {
		return dagExecUUID;
	}

	public void setDagExecUUID(String dagExecUUID) {
		this.dagExecUUID = dagExecUUID;
	}

	public String getStageId() {
		return stageId;
	}

	public void setStageId(String stageId) {
		this.stageId = stageId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public List<String> getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(List<String> dependsOn) {
		this.dependsOn = dependsOn;
	}

	public OrderKey getDatapodKey() {
		return datapodKey;
	}

	public void setDatapodKey(OrderKey datapodKey) {
		this.datapodKey = datapodKey;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public MapServiceImpl getMapServiceImpl() {
		return mapServiceImpl;
	}

	public void setMapServiceImpl(MapServiceImpl mapServiceImpl) {
		this.mapServiceImpl = mapServiceImpl;
	}

	public String getDagExecVer() {
		return dagExecVer;
	}

	public void setDagExecVer(String dagExecVer) {
		this.dagExecVer = dagExecVer;
	}
	
	public ExecParams getExecParams() {
		return execParams;
	}

	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}

	public Dag getDag() {
		return dag;
	}

	public void setDag(Dag dag) {
		this.dag = dag;
	}

	public DataQualServiceImpl getDataqualServiceImpl() {
		return dataqualServiceImpl;
	}

	public void setDataqualServiceImpl(DataQualServiceImpl dataqualServiceImpl) {
		this.dataqualServiceImpl = dataqualServiceImpl;
	}

	public DataQualExecServiceImpl getDataqualExecServiceImpl() {
		return dataqualExecServiceImpl;
	}

	public void setDataqualExecServiceImpl(DataQualExecServiceImpl dataqualExecServiceImpl) {
		this.dataqualExecServiceImpl = dataqualExecServiceImpl;
	}

	public DataQualGroupServiceImpl getDataqualGroupServiceImpl() {
		return dataqualGroupServiceImpl;
	}

	public void setDataqualGroupServiceImpl(DataQualGroupServiceImpl dataqualGroupServiceImpl) {
		this.dataqualGroupServiceImpl = dataqualGroupServiceImpl;
	}
	
	public DataQualGroupExecServiceImpl getDataqualGroupExecServiceImpl() {
		return dataqualGroupExecServiceImpl;
	}

	public void setDataqualGroupExecServiceImpl(DataQualGroupExecServiceImpl dataqualGroupExecServiceImpl) {
		this.dataqualGroupExecServiceImpl = dataqualGroupExecServiceImpl;
	}

	public RuleServiceImpl getRuleServiceImpl() {
		return ruleServiceImpl;
	}

	public void setRuleServiceImpl(RuleServiceImpl ruleServiceImpl) {
		this.ruleServiceImpl = ruleServiceImpl;
	}

	public RuleExecServiceImpl getRuleExecServiceImpl() {
		return ruleExecServiceImpl;
	}

	public void setRuleExecServiceImpl(RuleExecServiceImpl ruleExecServiceImpl) {
		this.ruleExecServiceImpl = ruleExecServiceImpl;
	}

	public RuleGroupServiceImpl getRuleGroupServiceImpl() {
		return ruleGroupServiceImpl;
	}

	public void setRuleGroupServiceImpl(RuleGroupServiceImpl ruleGroupServiceImpl) {
		this.ruleGroupServiceImpl = ruleGroupServiceImpl;
	}

	public RuleGroupExecServiceImpl getRuleGroupExecServiceImpl() {
		return ruleGroupExecServiceImpl;
	}

	public void setRuleGroupExecServiceImpl(RuleGroupExecServiceImpl ruleGroupExecServiceImpl) {
		this.ruleGroupExecServiceImpl = ruleGroupExecServiceImpl;
	}

	public ModelServiceImpl getModelServiceImpl() {
		return modelServiceImpl;
	}

	public void setModelServiceImpl(ModelServiceImpl modelServiceImpl) {
		this.modelServiceImpl = modelServiceImpl;
	}

	public ModelExecServiceImpl getModelExecServiceImpl() {
		return modelExecServiceImpl;
	}

	public void setModelExecServiceImpl(ModelExecServiceImpl modelExecServiceImpl) {
		this.modelExecServiceImpl = modelExecServiceImpl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	/**
	 * Execute dag
	 * @param dagResultRef
	 * @throws Exception 
	 */
	private MetaIdentifierHolder executeDagTask(MetaIdentifierHolder dagResultRef) throws Exception {
		TaskOperator operator = indvTask.getOperators().get(0);
		Dag dag = (Dag) commonServiceImpl.getLatestByUuid(operator.getOperatorInfo().getRef().getUuid(), MetaType.dag.toString());
		dagResultRef = dagServiceImpl.submitDag(dag, runMode);
		return dagResultRef;
		
	}// End executeDagTask
	
	/**
	 * Execute load, map and matrixmult operation
	 * @param dagResultRef
	 * @param dfTask
	 * @throws Exception 
	 */
	private String executeTask() throws Exception {
		FrameworkThreadLocal.getSessionContext().set(sessionContext);
		TaskOperator operator = indvTask.getOperators().get(0);
		String datapodTableName  = null;
		DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(dagExecUUID, dagExecVer, MetaType.dagExec.toString());
		TaskExec taskExec = dagExecServiceImpl.getTaskExec(dagExec, stageId, taskId);
		Map<String, String> internalVarMap = loadInternalParams(taskExec, dagExecUUID, dagExecVer, stageId, taskId);
		if (datapodKey != null) {
			datapodTableName = String.format("%s_%s_%s", datapodKey.getUUID().replace("-", "_"),datapodKey.getVersion(), dagExecVer);
		} else {
			datapodTableName = "";
		}
		Status failedStatus = new Status(Status.Stage.Failed, new Date());
		if(operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.load)){
			try {
				Load load = (Load) commonServiceImpl.getOneByUuidAndVersion(operator.getOperatorInfo().getRef().getUuid(), operator.getOperatorInfo().getRef().getVersion(), MetaType.load.toString());
				LoadExec loadExec = (LoadExec) daoRegister.getRefObject(dagExecServiceImpl.getTaskExec(dagExecUUID, dagExecVer, stageId, taskId).getOperators().get(0).getOperatorInfo().getRef());
				Datasource datasource = commonServiceImpl.getDatasourceByApp();
				if(!datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
						&& !datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
					MetaIdentifier targetMI = load.getTarget().getRef();
					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetMI.getUuid(), targetMI.getVersion(), targetMI.getType().toString());
					datapodTableName = datasource.getDbname()+"."+datapod.getName();
				}
				
				loadServiceImpl.executeSql(loadExec, dagExecVer, datapodTableName, datapodKey, runMode);
				taskExec.getOperators().get(0).getOperatorInfo().setRef(new MetaIdentifier(MetaType.loadExec, loadExec.getUuid(), loadExec.getVersion()));
				commonServiceImpl.save(MetaType.dagExec.toString(), dagExec);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

		} else if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.map)) {
			try {
				MapExec mapExec = (MapExec) daoRegister.getRefObject(dagExecServiceImpl.getTaskExec(dagExecUUID, dagExecVer, stageId, taskId).getOperators().get(0).getOperatorInfo().getRef());
				mapServiceImpl.executeSql(mapExec, datapodKey, runMode);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.dq)) {
			try {
				
				//DataQualExec dataqualExec = dataqualExecServiceImpl.findOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion());
				DataQualExec dataqualExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), MetaType.dqExec.toString());
				//ExecParams execParams = commonServiceImpl.getExecParams(taskExec.getOperators().get(0));
				dataqualExec = dataqualServiceImpl.execute(dataqualExec.getDependsOn().getRef().getUuid(), dataqualExec.getDependsOn().getRef().getVersion(), dataqualExec, null, execParams, runMode);
				if (Helper.getLatestStatus(dataqualExec.getStatusList()).equals(failedStatus)) {
					throw new Exception("DQ failed");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}  else if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.dqgroup)) {
			try {
				//DataQualGroupExec dataqualGroupExec = dataqualGroupExecServiceImpl.findOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion());
				DataQualGroupExec dataqualGroupExec = (DataQualGroupExec) commonServiceImpl.getOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), MetaType.dqgroupExec.toString());
				dataqualGroupServiceImpl.execute(dataqualGroupExec.getDependsOn().getRef().getUuid(), dataqualGroupExec.getDependsOn().getRef().getVersion(), null, dataqualGroupExec, runMode);
				if (Helper.getLatestStatus(dataqualGroupExec.getStatusList()).equals(new Status (Status.Stage.Failed, new Date()))) {
					throw new RuntimeException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.rule)) {
			logger.info("Going to ruleServiceImpl.execute");
			try {
				//RuleExec ruleExec = ruleExecServiceImpl.findOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion());
				RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), MetaType.ruleExec.toString());
				//ExecParams execParams = commonServiceImpl.getExecParams(taskExec.getOperators().get(0));
				internalVarMap.put("$CURRENT_TASK_OBJ_VERSION", ruleExec.getVersion());
				execParams.setInternalVarMap(internalVarMap);
				ruleServiceImpl.execute(null, ruleExec, null, null, execParams, runMode);
				// ruleServiceImpl.execute(ruleExec.getDependsOn().getRef().getUuid(), ruleExec.getDependsOn().getRef().getVersion(), ruleExec, null, null, null);
				if (Helper.getLatestStatus(ruleExec.getStatusList()).equals(new Status(Status.Stage.Failed, new Date()))) {
					throw new Exception();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}  else if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.rulegroup)) {
			try {
				//RuleGroupExec ruleGroupExec = ruleGroupExecServiceImpl.findOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion());
				RuleGroupExec ruleGroupExec = (RuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), MetaType.rulegroupExec.toString());
				internalVarMap.put("$CURRENT_TASK_OBJ_VERSION", ruleGroupExec.getVersion());
				execParams.setInternalVarMap(internalVarMap);
				ruleGroupServiceImpl.execute(ruleGroupExec.getDependsOn().getRef().getUuid(), ruleGroupExec.getDependsOn().getRef().getVersion(), execParams, ruleGroupExec, runMode);
				if (Helper.getLatestStatus(ruleGroupExec.getStatusList()).equals(new Status(Status.Stage.Failed, new Date()))) {
					throw new Exception();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}  else if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.profile)) {
			try {
				//ProfileExec profileExec = profileExecServiceImpl.findOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion());
				ProfileExec profileExec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), MetaType.profileExec.toString());
				//ExecParams execParams = commonServiceImpl.getExecParams(taskExec.getOperators().get(0));
				profileServiceImpl.execute(profileExec.getDependsOn().getRef().getUuid(), profileExec.getDependsOn().getRef().getVersion(), profileExec, null, null, execParams, runMode);
			} catch (ParseException | JSONException e) {
				e.printStackTrace();
				throw e;
			}
		}  else if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.profilegroup)) {
			try {
				//ProfileGroupExec profileGroupExec = profileGroupExecServiceImpl.findOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion());
				ProfileGroupExec profileGroupExec = (ProfileGroupExec) commonServiceImpl.getOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), MetaType.profilegroupExec.toString());
				profileGroupServiceImpl.execute(profileGroupExec.getDependsOn().getRef().getUuid(), profileGroupExec.getDependsOn().getRef().getVersion(), null, profileGroupExec, runMode);
				if (Helper.getLatestStatus(profileGroupExec.getStatusList()).equals(new Status(Status.Stage.Failed, new Date()))) {
					throw new Exception();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}  else if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.train)) {
			try {
				//ModelExec modelExec = modelExecServiceImpl.findOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion());
				TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), MetaType.trainExec.toString());
				internalVarMap.put("$CURRENT_TASK_OBJ_VERSION", trainExec.getVersion());
				execParams.setInternalVarMap(internalVarMap);
				Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainExec.getDependsOn().getRef().getUuid(), trainExec.getDependsOn().getRef().getVersion(), MetaType.train.toString());
				Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), train.getDependsOn().getRef().getVersion(), MetaType.model.toString());
				ParamMap paramMap = paramSetServiceImpl.getParamMapCombined(execParams, train.getUuid(), train.getVersion());
				ExecParams execParams = commonServiceImpl.getExecParams(operator);
				modelServiceImpl.train(train, model, trainExec, execParams, paramMap, runMode);
				if (Helper.getLatestStatus(trainExec.getStatusList()).equals(new Status(Status.Stage.Failed, new Date()))) {
					throw new Exception();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}else if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.simulate)) {
			try {
				//ModelExec modelExec = modelExecServiceImpl.findOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion());
				SimulateExec simulateExec = (SimulateExec) commonServiceImpl.getOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), MetaType.simulateExec.toString());
				internalVarMap.put("$CURRENT_TASK_OBJ_VERSION", simulateExec.getVersion());
				execParams.setInternalVarMap(internalVarMap);
				Simulate simulate = (Simulate) commonServiceImpl.getOneByUuidAndVersion(simulateExec.getDependsOn().getRef().getUuid(), simulateExec.getDependsOn().getRef().getVersion(), MetaType.simulate.toString());
				//Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(simulate.getDependsOn().getRef().getUuid(), simulate.getDependsOn().getRef().getVersion(), MetaType.model.toString());
				//ParamMap paramMap = paramSetServiceImpl.getParamMapCombined(execParams, model.getUuid(), model.getVersion());
				ExecParams execParams = commonServiceImpl.getExecParams(operator);
				modelServiceImpl.simulate(simulate, execParams, simulateExec, runMode);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.predict)) {
			try {
				//ModelExec modelExec = modelExecServiceImpl.findOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion());
				PredictExec predictExec = (PredictExec) commonServiceImpl.getOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), MetaType.predictExec.toString());
				internalVarMap.put("$CURRENT_TASK_OBJ_VERSION", predictExec.getVersion());
				execParams.setInternalVarMap(internalVarMap);
				Predict predict = (Predict) commonServiceImpl.getOneByUuidAndVersion(predictExec.getDependsOn().getRef().getUuid(), predictExec.getDependsOn().getRef().getVersion(), MetaType.predict.toString());
				//Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), train.getDependsOn().getRef().getVersion(), MetaType.model.toString());
				//ParamMap paramMap = paramSetServiceImpl.getParamMapCombined(execParams, model.getUuid(), model.getVersion());
				modelServiceImpl.predict(predict, execParams, predictExec, runMode);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.recon)) {
			logger.info("Going to reconServiceImpl.execute");
			try {
				ReconExec reconExec = (ReconExec) commonServiceImpl.getOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), MetaType.reconExec.toString());
				//ExecParams execParams = commonServiceImpl.getExecParams(taskExec.getOperators().get(0));
				reconServiceImpl.execute(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), null, reconExec, null, null, execParams, runMode);
				
				if (Helper.getLatestStatus(reconExec.getStatusList()).equals(new Status(Status.Stage.Failed, new Date()))) {
					throw new Exception();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}  else if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.recongroup)) {
			try {
				ReconGroupExec reconGroupExec = (ReconGroupExec) commonServiceImpl.getOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), MetaType.recongroupExec.toString());
				reconGroupServiceImpl.execute(reconGroupExec.getDependsOn().getRef().getUuid(), reconGroupExec.getDependsOn().getRef().getVersion(), execParams, reconGroupExec, runMode);
				if (Helper.getLatestStatus(reconGroupExec.getStatusList()).equals(new Status(Status.Stage.Failed, new Date()))) {
					throw new Exception();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} 	 else if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.operator)) {
			logger.info("Going to operatorServiceImpl.execute");
			try {
				OperatorExec operatorExec = (OperatorExec) commonServiceImpl.getOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), MetaType.operatorExec.toString());
//				HashMap<String, String> otherParams = execParams.getOtherParams();
				ExecParams operatorExecParams = commonServiceImpl.getExecParams(taskExec.getOperators().get(0));
				Helper.mergeMap(operatorExecParams.getOtherParams(), execParams.getOtherParams());
				operatorServiceImpl.execute((BaseExec) operatorExec, operatorExecParams, runMode);
				
				if (Helper.getLatestStatus(operatorExec.getStatusList()).equals(new Status(Status.Stage.Failed, new Date()))) {
					throw new Exception();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}  // End else
		return datapodTableName;
	}// End executeTask
	
	/**
	 * 
	 * @param taskExec
	 * @param dagExecUUID
	 * @param dagExecVer
	 * @param stageId
	 * @param taskId
	 */
	private java.util.Map<String, String> loadInternalParams(TaskExec taskExec, String dagExecUUID, String dagExecVer,
			String stageId, String taskId) {
		java.util.Map<String, String> internalVarMap = new HashMap<>();
		if (internalVarMap == null || internalVarMap.isEmpty()) {
			internalVarMap = new HashMap<>();
			execParams.setInternalVarMap(internalVarMap);
		}
		internalVarMap.put("$CURRENT_DAG_EXEC_UUID", dagExecUUID);
		internalVarMap.put("$CURRENT_DAG_EXEC_VERSION", dagExecVer);
		internalVarMap.put("$CURRENT_STAGE_ID", stageId);
		internalVarMap.put("$CURRENT_TASK_ID", taskId);
		internalVarMap.put("$CURRENT_TASK_TYPE", taskExec.getOperators().get(0).getOperatorInfo().getRef().getType().toString());
		internalVarMap.put("$CURRENT_TASK_OBJ_UUID", taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid());
		/*internalVarMap.put("$CURRENT_TASK_OBJ_VERSION", taskId);*/
		return internalVarMap;
	}


	@Override
	public String call() throws JsonProcessingException, JSONException, java.text.ParseException {
		logger.info("Thread watch : DagExec : " + dagExecUUID + " StageExec : " + stageId + " taskExec : " + indvTask.getTaskId() + " status RUN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
		FrameworkThreadLocal.getSessionContext().set(sessionContext);
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
	    com.inferyx.framework.domain.Status.Stage taskStatus = null;
		//DagExec dagExec = dagExecServiceImpl.findOneByUuidAndVersion(dagExecUUID, dagExecVer);
	    DagExec dagExec = null;
		try {
			dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(dagExecUUID, dagExecVer, MetaType.dagExec.toString());
		} catch (JsonProcessingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		TaskExec taskExec = dagExecServiceImpl.getTaskExec(dagExec, stageId, taskId);
		//Start the task execution
		if (!killThread 
				&& !dagExecServiceImpl.getStageStatus(dagExecUUID, dagExecVer, stageId).equals(Status.Stage.OnHold) 
				&& !dagExecServiceImpl.getTaskStatus(dagExecUUID, dagExecVer, stageId, taskId).equals(Status.Stage.OnHold)) {

			//Set Task to InProgress
			synchronized (dagExecUUID) {
				try {
					commonServiceImpl.setMetaStatusForTask(dagExec, taskExec, Status.Stage.InProgress, stageId, taskId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			logger.info("Setting InProgress status stageId: " + stageId + " and taskId: " + taskId);

			// DataFrame dfTask = null;
			String datapodTableName  = null;
			//Execute Task SQL
			if (operatorInfo.getRef()!=null && operatorInfo.getRef().getType().equals(MetaType.dag)){
				try {
					resultRef = executeDagTask(resultRef);
				} catch (Exception e) {
					e.printStackTrace();
					synchronized (dagExecUUID) {
						try {
							commonServiceImpl.setMetaStatusForTask(dagExec, taskExec, Status.Stage.Failed, stageId, taskId);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}else{
				try {
					FrameworkThreadLocal.getSessionContext().set(sessionContext);
					datapodTableName = executeTask();
				} catch (Exception e) {
					logger.info("Task failed : " + taskId);
					e.printStackTrace();
					synchronized (dagExecUUID) {
						taskStatus = com.inferyx.framework.domain.Status.Stage.Failed;
						try {
							commonServiceImpl.setMetaStatusForTask(dagExec, taskExec, Status.Stage.Failed, stageId, taskId);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			
			if(!killThread){

				// Set the result after Task execution
				synchronized (dagExecUUID) {
					dagExecServiceImpl.setTaskResult(dagExecUUID, dagExecVer, stageId, taskId,resultRef);
				}
			    
				//Set Task to Complete
				synchronized (dagExecUUID) {
					if (taskStatus != com.inferyx.framework.domain.Status.Stage.Failed) {
						try {
							Object execObj=commonServiceImpl.getOneByUuidAndVersion(taskExec.getOperators().get(0).getOperatorInfo().getRef().getUuid(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getVersion(), taskExec.getOperators().get(0).getOperatorInfo().getRef().getType().toString());
							@SuppressWarnings("unchecked")
							List<com.inferyx.framework.domain.Status> status = (List<Status>) execObj.getClass().getMethod("getStatusList").invoke(execObj);
							logger.info(Helper.getLatestStatus(status).getStage());
							commonServiceImpl.setMetaStatusForTask(dagExec, taskExec,Helper.getLatestStatus(status).getStage(), stageId, taskId);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				logger.info("Setting Completed status for taskId: " + taskId);

				logger.info("Execution end dataframe is register as table name " + datapodTableName);

			} else if(killThread){
				synchronized (dagExecUUID) {
					dagExecServiceImpl.setTaskKilled(dagExecUUID, dagExecVer, stageId, taskId);
				}
			}
			} else {
				if(killThread){
					synchronized (dagExecUUID) {
						dagExecServiceImpl.setTaskKilled(dagExecUUID, dagExecVer, stageId, taskId);
					}
				} 
				while (dagExecServiceImpl.getStageStatus(dagExecUUID, dagExecVer, stageId).equals(Status.Stage.OnHold) 
				|| dagExecServiceImpl.getTaskStatus(dagExecUUID, dagExecVer, stageId, taskId).equals(Status.Stage.OnHold)) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (dagExecServiceImpl.getStageStatus(dagExecUUID, dagExecVer, stageId).equals(Status.Stage.Resume) 
				|| dagExecServiceImpl.getTaskStatus(dagExecUUID, dagExecVer, stageId, taskId).equals(Status.Stage.Resume)) {
					FrameworkThreadLocal.getSessionContext().set(sessionContext);
					call();
				}
			logger.info("This is not valid for Execution");
		}
		logger.info("Thread watch : DagExec : " + dagExecUUID + " StageExec : " + stageId + " taskExec : " + indvTask.getTaskId() + " complete >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
		return name;
	}// End thread run
	
}
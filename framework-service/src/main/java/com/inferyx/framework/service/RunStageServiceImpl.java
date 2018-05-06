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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DQInfo;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.common.ProfileInfo;
import com.inferyx.framework.common.ReconInfo;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Operator;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.Recon;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.RunStatusHolder;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Stage;
import com.inferyx.framework.domain.StageExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Task;
import com.inferyx.framework.domain.TaskExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.factory.DataSourceFactory;

public class RunStageServiceImpl implements Callable<String> {
	
	private DagExecServiceImpl dagExecServiceImpl;
	private CommonServiceImpl<?> commonServiceImpl;
	private LoadServiceImpl loadServiceImpl;
	private DatapodServiceImpl datapodServiceImpl;
	private OperatorServiceImpl operatorServiceImpl;
	private DagServiceImpl dagServiceImpl;
	private HDFSInfo hdfsInfo;
	private MapServiceImpl mapServiceImpl;
	private DataStoreServiceImpl dataStoreServiceImpl;
	private ParamSetServiceImpl paramSetServiceImpl;
	private String dagExecUUID;	 	
	private String stageId;
	private List<String> dependsOn;
	private String dagExecVer;
	private DataStore dataStore;
	private DataStoreServiceImpl iDataStore;
	private boolean killThread= false;
	private static HashMap<String,Integer> rowSeqMap = new HashMap<String,Integer>();
	private static HashMap<String,Integer> colSeqMap = new HashMap<String,Integer>();
	private static HashMap<Integer,String> rowSeqFinalMap = new HashMap<Integer,String>();
	private static HashMap<Integer,String> colSeqFinalMap = new HashMap<Integer,String>();
	private Dataset<Row> dfTask;
	private ExecParams execParams;
	private Dag dag;
	private DataSourceFactory datasourceFactory;
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
	ThreadPoolTaskExecutor taskExecutor;

	private ModelServiceImpl modelServiceImpl;
	private ModelExecServiceImpl modelExecServiceImpl;
	private String name;
	private StageExec stageExec;
	ConcurrentHashMap taskThreadMap;
	DQInfo dqInfo;
	MetadataUtil daoRegister;
	DagExec dagExec;
	Stage stage;
	private RunMode runMode;
	private SessionContext sessionContext;
	private ReconServiceImpl reconServiceImpl;
	private ReconGroupServiceImpl reconGroupServiceImpl;
	ProfileInfo profileInfo;
	ReconInfo reconInfo;
	

	/**
	 * @Ganesh
	 *
	 * @return the profileInfo
	 */
	public ProfileInfo getProfileInfo() {
		return profileInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @param profileInfo the profileInfo to set
	 */
	public void setProfileInfo(ProfileInfo profileInfo) {
		this.profileInfo = profileInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @return the reconInfo
	 */
	public ReconInfo getReconInfo() {
		return reconInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @param reconInfo the reconInfo to set
	 */
	public void setReconInfo(ReconInfo reconInfo) {
		this.reconInfo = reconInfo;
	}

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

	public DagExecServiceImpl getDagExecServiceImpl() {
		return dagExecServiceImpl;
	}

	public void setDagExecServiceImpl(DagExecServiceImpl dagExecServiceImpl) {
		this.dagExecServiceImpl = dagExecServiceImpl;
	}

	public CommonServiceImpl<?> getCommonServiceImpl() {
		return commonServiceImpl;
	}

	public void setCommonServiceImpl(CommonServiceImpl<?> commonServiceImpl) {
		this.commonServiceImpl = commonServiceImpl;
	}

	public LoadServiceImpl getLoadServiceImpl() {
		return loadServiceImpl;
	}

	public void setLoadServiceImpl(LoadServiceImpl loadServiceImpl) {
		this.loadServiceImpl = loadServiceImpl;
	}

	public DatapodServiceImpl getDatapodServiceImpl() {
		return datapodServiceImpl;
	}

	public void setDatapodServiceImpl(DatapodServiceImpl datapodServiceImpl) {
		this.datapodServiceImpl = datapodServiceImpl;
	}

	public OperatorServiceImpl getOperatorServiceImpl() {
		return operatorServiceImpl;
	}

	public void setOperatorServiceImpl(OperatorServiceImpl operatorServiceImpl) {
		this.operatorServiceImpl = operatorServiceImpl;
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

	public MapServiceImpl getMapServiceImpl() {
		return mapServiceImpl;
	}

	public void setMapServiceImpl(MapServiceImpl mapServiceImpl) {
		this.mapServiceImpl = mapServiceImpl;
	}

	public DataStoreServiceImpl getDataStoreServiceImpl() {
		return dataStoreServiceImpl;
	}

	public void setDataStoreServiceImpl(DataStoreServiceImpl dataStoreServiceImpl) {
		this.dataStoreServiceImpl = dataStoreServiceImpl;
	}

	public ParamSetServiceImpl getParamSetServiceImpl() {
		return paramSetServiceImpl;
	}

	public void setParamSetServiceImpl(ParamSetServiceImpl paramSetServiceImpl) {
		this.paramSetServiceImpl = paramSetServiceImpl;
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

	public List<String> getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(List<String> dependsOn) {
		this.dependsOn = dependsOn;
	}

	public String getDagExecVer() {
		return dagExecVer;
	}

	public void setDagExecVer(String dagExecVer) {
		this.dagExecVer = dagExecVer;
	}

	public DataStore getDataStore() {
		return dataStore;
	}

	public void setDataStore(DataStore dataStore) {
		this.dataStore = dataStore;
	}

	public DataStoreServiceImpl getiDataStore() {
		return iDataStore;
	}

	public void setiDataStore(DataStoreServiceImpl iDataStore) {
		this.iDataStore = iDataStore;
	}

	public boolean isKillThread() {
		return killThread;
	}

	public void setKillThread(boolean killThread) {
		this.killThread = killThread;
	}

	public static HashMap<String, Integer> getRowSeqMap() {
		return rowSeqMap;
	}

	public static void setRowSeqMap(HashMap<String, Integer> rowSeqMap) {
		RunStageServiceImpl.rowSeqMap = rowSeqMap;
	}

	public static HashMap<String, Integer> getColSeqMap() {
		return colSeqMap;
	}

	public static void setColSeqMap(HashMap<String, Integer> colSeqMap) {
		RunStageServiceImpl.colSeqMap = colSeqMap;
	}

	public static HashMap<Integer, String> getRowSeqFinalMap() {
		return rowSeqFinalMap;
	}

	public static void setRowSeqFinalMap(HashMap<Integer, String> rowSeqFinalMap) {
		RunStageServiceImpl.rowSeqFinalMap = rowSeqFinalMap;
	}

	public static HashMap<Integer, String> getColSeqFinalMap() {
		return colSeqFinalMap;
	}

	public static void setColSeqFinalMap(HashMap<Integer, String> colSeqFinalMap) {
		RunStageServiceImpl.colSeqFinalMap = colSeqFinalMap;
	}

	public Dataset<Row> getDfTask() {
		return dfTask;
	}

	public void setDfTask(Dataset<Row> dfTask) {
		this.dfTask = dfTask;
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

	public DataSourceFactory getDatasourceFactory() {
		return datasourceFactory;
	}

	public void setDatasourceFactory(DataSourceFactory datasourceFactory) {
		this.datasourceFactory = datasourceFactory;
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

	public ThreadPoolTaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

	public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
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

	public StageExec getStageExec() {
		return stageExec;
	}

	public void setStageExec(StageExec stageExec) {
		this.stageExec = stageExec;
	}

	public ConcurrentHashMap getTaskThreadMap() {
		return taskThreadMap;
	}

	public void setTaskThreadMap(ConcurrentHashMap taskThreadMap) {
		this.taskThreadMap = taskThreadMap;
	}

	public DQInfo getDqInfo() {
		return dqInfo;
	}

	public void setDqInfo(DQInfo dqInfo) {
		this.dqInfo = dqInfo;
	}

	public MetadataUtil getDaoRegister() {
		return daoRegister;
	}

	public void setDaoRegister(MetadataUtil daoRegister) {
		this.daoRegister = daoRegister;
	}

	public DagExec getDagExec() {
		return dagExec;
	}

	public void setDagExec(DagExec dagExec) {
		this.dagExec = dagExec;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
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

	static final Logger logger = Logger.getLogger(RunStageServiceImpl.class);
	
	@Override
	public String call() throws Exception {
		FrameworkThreadLocal.getSessionContext().set(sessionContext);
		checkDependencyStatus();
		
		return execute();
	}
	
	public void checkDependencyStatus() throws JsonProcessingException {
		FrameworkThreadLocal.getSessionContext().set(sessionContext);
		boolean checkDependencyStatus = false;
		boolean checkDependencyFailed = false;
		boolean checkDependencyKilled = false;
		String dependencyStatus = null;
		
		while(!checkDependencyStatus){
			dependencyStatus = dagExecServiceImpl.checkStageDepStatus(dag,dagExecUUID,dagExecVer,stageId);
			logger.info("Stage dependencyStatus : " + stageId + " : " + dependencyStatus);
			if (StringUtils.isBlank(dependencyStatus) || dependencyStatus.equalsIgnoreCase("NotCompleted")) {
				checkDependencyStatus = false;
			} else if (dependencyStatus.equalsIgnoreCase("Killed")) {
				checkDependencyKilled = true;
				break;
			} else {
				checkDependencyStatus = true;
			}
			if(killThread){
				// Thread received Kill request. Stop proceeding
				break;
			}
			if(!checkDependencyStatus){
				try {
					Thread.sleep(10000); //  Should be parameterized in a class
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String execute() throws Exception {
		FrameworkThreadLocal.getSessionContext().set(sessionContext);
		List<Status> stageStatusList = stageExec.getStatusList();
		
		List<TaskExec> dagTaskExecs = DagExecUtil.castToTaskExecList(stageExec.getTasks());
		List<FutureTask> taskList = new ArrayList<FutureTask>();
		Status.Stage stageStatus = Helper.getLatestStatus(stageStatusList).getStage();
		List<TaskExec> depTaskExecs = new ArrayList<>();
		
		try {
			logger.info("Setting status to In Progress for stage : " + stageId);
			synchronized (dagExec.getUuid()) {
				commonServiceImpl.setMetaStatusForStage (dagExec, stageExec, Status.Stage.InProgress, stageExec.getStageId());
			}
		} catch (Exception e1) {
			logger.error("Stage Exec in progress status could not be set ");
			e1.printStackTrace();
		}
		
		do {
			//Start the tasks which has on dependency
			for (TaskExec indvTaskExec : dagTaskExecs) {
	
				OrderKey datapodKey = null;
				
				Task indvTask = DagExecUtil.getTaskFromStage(stage, indvTaskExec.getTaskId());
				
	//			Status taskStatus = Helper.getLatestStatus(indvTaskExec.getStatus());
	//			if (taskStatus.equals(new Status(Status.Stage.InProgress, new Date())) 
	//					|| taskStatus.equals(new Status(Status.Stage.Completed, new Date()))
	//					|| taskStatus.equals(new Status(Status.Stage.OnHold, new Date()))) {
	//				continue;
	//			}
	
				//Fetch from mongo instead of udta panchi.
				com.inferyx.framework.domain.Status.Stage taskStatus = dagExecServiceImpl.getTaskStatus(dagExecUUID, dagExecVer, stageId, indvTaskExec.getTaskId());			
				/*if (taskStatus.equals(Status.Stage.InProgress) 
						|| taskStatus.equals(Status.Stage.Completed)
						|| taskStatus.equals(Status.Stage.OnHold)) {
					continue;
				}*/
				if (!taskStatus.equals(Status.Stage.NotStarted)
						&& !taskStatus.equals(Status.Stage.InProgress)) {
					continue;
				}
				// Check if task has any dependents
				if (indvTaskExec.getDependsOn() != null 
						&& !indvTaskExec.getDependsOn().isEmpty()) {
					depTaskExecs.add(indvTaskExec);	// Put in dependency list. Shall be handled in a separate loop
					continue;
				}
	
				datapodKey = fetchDatapodKey(indvTask);
				// Fetch datapod key
				MetaIdentifierHolder operationInfoHolder = indvTask.getOperators().get(0)
						.getOperatorInfo();
				if (datapodKey == null) {
					if (operationInfoHolder != null && operationInfoHolder.getRef() != null
						&& !(operationInfoHolder.getRef().getType().equals(MetaType.dag) 
							|| operationInfoHolder.getRef().getType().equals(MetaType.dqgroup) 
							|| operationInfoHolder.getRef().getType().equals(MetaType.profilegroup) 
							|| operationInfoHolder.getRef().getType().equals(MetaType.rulegroup) 
							|| operationInfoHolder.getRef().getType().equals(MetaType.rule) 
							|| operationInfoHolder.getRef().getType().equals(MetaType.train) 
							|| operationInfoHolder.getRef().getType().equals(MetaType.predict) 
							|| operationInfoHolder.getRef().getType().equals(MetaType.simulate) 
							|| operationInfoHolder.getRef().getType().equals(MetaType.recon) 
							|| operationInfoHolder.getRef().getType().equals(MetaType.recongroup)
							|| operationInfoHolder.getRef().getType().equals(MetaType.operatortype))) {
					continue;
					}
				}
				logger.info("Calling task id : " + indvTask.getTaskId());
				// Set task and submit for execution
				setTaskAndSubmit(indvTaskExec, datapodKey, indvTask, operationInfoHolder, taskList, runMode);
			
			}	// For all taskExecs
			
			//Start the tasks which has dependencies 
			boolean allDependenciesAddressed = false;
			if (depTaskExecs == null || depTaskExecs.isEmpty()) {
				allDependenciesAddressed = true;
			}
			
			HashSet<String> dependentTaskRunningLog = new HashSet<>();
			
			while(!allDependenciesAddressed) {
				allDependenciesAddressed = true; // Set it to true so that any failure in checkDependencyStatus may set that to false
				try {
					logger.info("Tasks waiting for dependents ");
					Thread.sleep(10000); //  Should be parameterized in a class
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (TaskExec indvTaskExec : depTaskExecs) {
					
					if (dependentTaskRunningLog.contains(dagExec.getUuid() + "_" + stageId + "_" + indvTaskExec.getTaskId())) {
						continue;
					}
	
					boolean checkDependencyStatus = false;
					boolean checkDependencyFailed = false;
					boolean checkDependencyKilled = false;
					String dependencyStatus = null;
					OrderKey datapodKey = null;
					
					Task indvTask = DagExecUtil.getTaskFromStage(stage, indvTaskExec.getTaskId());
					
	//				Status taskStatus = Helper.getLatestStatus(indvTaskExec.getStatus());				
	//				if (taskStatus.equals(new Status(Status.Stage.InProgress, new Date())) 
	//						|| taskStatus.equals(new Status(Status.Stage.Completed, new Date()))
	//						|| taskStatus.equals(new Status(Status.Stage.OnHold, new Date()))) {
	//					continue;
	//				}
	
					//Fetch from mongo instead of udta panchi.				
					com.inferyx.framework.domain.Status.Stage taskStatus = dagExecServiceImpl.getTaskStatus(dagExecUUID, dagExecVer, stageId, indvTaskExec.getTaskId());			
					/*if (taskStatus.equals(Status.Stage.InProgress) 
							|| taskStatus.equals(Status.Stage.Completed)
							|| taskStatus.equals(Status.Stage.OnHold)) {
						continue;
					}*/
					if (!taskStatus.equals(Status.Stage.NotStarted)
							&& !taskStatus.equals(Status.Stage.InProgress)) {
						continue;
					}
					
					// If not checkdependency status then continue after setting allDependenciesAddressed to false
					dependencyStatus = dagExecServiceImpl.checkTaskDepStatus(dag,dagExecUUID,dagExecVer,stageId,indvTaskExec.getTaskId());
					logger.info("Task dependencyStatus : " + indvTaskExec.getTaskId() + " : " + dependencyStatus);
					if (StringUtils.isBlank(dependencyStatus) || dependencyStatus.equalsIgnoreCase("NotCompleted")) {
						checkDependencyStatus = false;
					} else if (dependencyStatus.equalsIgnoreCase("Killed")) {
						checkDependencyKilled = true;
						checkDependencyStatus = true;
						break;
					} else if (dependencyStatus.equalsIgnoreCase("Failed")) {
						checkDependencyFailed = true;
						checkDependencyStatus = true;
						break;
					} else {
						checkDependencyStatus = true;
					}
					
					if (checkDependencyKilled) {
						synchronized (dagExecUUID) {
							try {
								logger.info("Setting status to Killed for stage : " + stageId);
								commonServiceImpl.setMetaStatusForTask(dagExec, indvTaskExec, Status.Stage.Killed, stageId, indvTaskExec.getTaskId());
								continue;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
					if (checkDependencyFailed) {
						synchronized (dagExecUUID) {
							try {
								logger.info("Setting status to Failed for stage : " + stageId);
								commonServiceImpl.setMetaStatusForTask(dagExec, indvTaskExec, Status.Stage.Failed, stageId, indvTaskExec.getTaskId());
								continue;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					if (!checkDependencyStatus) {
						allDependenciesAddressed = false;
						logger.info("Task waiting for dependents : " + indvTaskExec.getTaskId());
						continue;
					}
	
					// Start process for Task submission
					datapodKey = fetchDatapodKey(indvTask);
					// Fetch datapod key
					MetaIdentifierHolder operationInfoHolder = indvTask.getOperators().get(0)
							.getOperatorInfo();
					if (datapodKey == null) {
						if (operationInfoHolder != null && operationInfoHolder.getRef() != null
							&& !(operationInfoHolder.getRef().getType().equals(MetaType.dag) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.dqgroup) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.profilegroup) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.rulegroup)
								|| operationInfoHolder.getRef().getType().equals(MetaType.train) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.predict) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.simulate)
								|| operationInfoHolder.getRef().getType().equals(MetaType.recon) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.recongroup)
								|| operationInfoHolder.getRef().getType().equals(MetaType.operatortype))) {
						continue;
						}
					}
					
					logger.info("Calling task id 2 : " + indvTask.getTaskId());
					dependentTaskRunningLog.add(dagExec.getUuid() + "_" + stageId + "_" + indvTaskExec.getTaskId());
					// Set task and submit for execution
					setTaskAndSubmit(indvTaskExec, datapodKey, indvTask, operationInfoHolder, taskList, runMode);
				
				}	// For all taskExecs
			}
			Thread.sleep(5000);
		} while(!waitAndComplete(taskList));
		
		/*String outputThreadName = null;
		for (FutureTask<String> futureTask : taskList) {
			try {
				outputThreadName = futureTask.get();
				logger.info("Thread " + outputThreadName + " completed ");
                taskThreadMap.remove(outputThreadName);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		boolean setCompletedStatus = true;
		boolean setResumeStatus = true;
		// Refresh DagExec from mongo
		dagExec = dagExecServiceImpl.findOneByUuidAndVersion(dagExecUUID, dagExecVer);
		stageExec = dagExecServiceImpl.getStageExec(dagExec, stageExec.getStageId());
		dagTaskExecs = DagExecUtil.castToTaskExecList(stageExec.getTasks());
		
		for (TaskExec indvTaskExec : dagTaskExecs) {
			Status latestStatus = Helper.getLatestStatus(indvTaskExec.getStatus());
			logger.info("After stage exec latestStatus : " + latestStatus.getStage().toString() + " for task exec : " + indvTaskExec.getTaskId());
			if (latestStatus.getStage().equals(Status.Stage.Failed)) {
				synchronized (dagExec.getUuid()) {
					commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Failed, stageExec.getStageId());
				}
				setCompletedStatus = false;
				break;
			} else if (latestStatus.getStage().equals(Status.Stage.Killed)) {
				synchronized (dagExec.getUuid()) {
					commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Terminating, stageExec.getStageId());;
					commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Killed, stageExec.getStageId());
				}
				setCompletedStatus = false;
				break;
			} else if (latestStatus.getStage().equals(Status.Stage.Resume) 
						|| latestStatus.getStage().equals(Status.Stage.OnHold)) {
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
				commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Completed, stageExec.getStageId());;
			}
		}
	*/	
		return name;
	}
	
	
	public Boolean waitAndComplete (List<FutureTask> taskList) throws Exception {
		FrameworkThreadLocal.getSessionContext().set(sessionContext);
		logger.info(" Inside waitAndComplete for RunStageServiceImpl ");
		String outputThreadName = null;
		try {
			// Collect results and clean map
				//if (dataQualGroup.getInParallel() != null && dataQualGroup.getInParallel().equalsIgnoreCase("true")) {
					logger.info(" Tasklist size before get : " + taskList.size());
					for (FutureTask<String> futureTask : taskList) {
			            try {
			            	outputThreadName = futureTask.get();
			                logger.info("Thread " + outputThreadName + " completed ");
			                taskThreadMap.remove(outputThreadName);
			            } catch (InterruptedException e) {
			                e.printStackTrace();
			            } catch (ExecutionException | CancellationException e) {
			                e.printStackTrace();
			            }catch (Exception e) {
			                e.printStackTrace();
			            }
			        }
					
				//}
				//dagExec = dagExecServiceImpl.findOneByUuidAndVersion(dagExecUUID, dagExecVer);
					dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(dagExecUUID, dagExecVer, MetaType.dagExec.toString());
				stageExec = DagExecUtil.getStageExecFromDagExec(dagExec, stageId);
				Status.Stage latestStatus = Helper.getLatestStatus(stageExec.getStatusList()).getStage();
				RunStatusHolder statusHolder = new RunStatusHolder(Boolean.TRUE, 
																	Boolean.FALSE, 
																	Boolean.FALSE, 
																	Boolean.FALSE, 
																	Boolean.FALSE);
				// Update run status for stage
				Helper.updateRunStatus(latestStatus, statusHolder);
				logger.info(" StatusHolder of stageExec : " + statusHolder.getCompleted() + ":" + statusHolder.getKilled() + ":" + statusHolder.getFailed() 
							+ ":" + statusHolder.getOnHold() + ":" + statusHolder.getResume());
				if (statusHolder.getCompleted() && statusHolder.getKilled()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Killed, stageId);
					}
					return true;
				} else if (statusHolder.getCompleted() && statusHolder.getFailed()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Failed, stageId);
					}
					return true;
				} else if (statusHolder.getCompleted()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Completed, stageId);
					}
					return true;
				} 
				
				// Check status of all metas and then decide on the group status  
				statusHolder = new RunStatusHolder(Boolean.TRUE, 
						Boolean.FALSE, 
						Boolean.FALSE, 
						Boolean.FALSE, 
						Boolean.FALSE);
				
				for (TaskExec taskExec : DagExecUtil.castToTaskExecList(stageExec.getTasks())) {
					latestStatus = Helper.getLatestStatus(taskExec.getStatusList()).getStage();
					Helper.updateRunStatus(latestStatus, statusHolder);
				}// End for
				
				logger.info(" StatusHolder of taskExec : " + statusHolder.getCompleted() + ":" + statusHolder.getKilled() + ":" + statusHolder.getFailed() 
				+ ":" + statusHolder.getOnHold() + ":" + statusHolder.getResume());
				
				if (statusHolder.getCompleted() && statusHolder.getKilled()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Killed, stageId);
					}
					return true;
				} else if (statusHolder.getCompleted() && statusHolder.getFailed()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Failed, stageId);
					}
					return true;
				} else if (statusHolder.getCompleted()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Completed, stageId);
					}
					return true;
				} 
		} catch (Exception e) {
			synchronized (dagExecUUID) {
				commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.Failed, stageId);
			}
			e.printStackTrace();
			return true;
//			throw new RuntimeException();
		}
			return false;
	}	// End waitAndComplete

	
	
	/**
	 * Fetch datapodKey if available
	 * @param indvTask
	 * @return
	 * @throws JsonProcessingException 
	 */
	public OrderKey fetchDatapodKey(Task indvTask) throws JsonProcessingException {
		OrderKey datapodKey = null;
		// Fetch datapod key
		MetaIdentifierHolder operationInfoHolder = indvTask.getOperators().get(0)
				.getOperatorInfo();
		if (operationInfoHolder != null && operationInfoHolder.getRef() != null
				&& operationInfoHolder.getRef().getType().equals(MetaType.load)) {

			//Load load = loadServiceImpl.findOneByUuidAndVersion(operationInfoHolder.getRef().getUuid(), operationInfoHolder.getRef().getVersion());
			Load load = (Load) commonServiceImpl.getOneByUuidAndVersion(operationInfoHolder.getRef().getUuid(), operationInfoHolder.getRef().getVersion(), MetaType.load.toString());
			//Datapod dp = datapodServiceImpl.findOneByUuidAndVersion(load.getTarget().getRef().getUuid(), load.getTarget().getRef().getVersion());
			Datapod dp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(load.getTarget().getRef().getUuid(), load.getTarget().getRef().getVersion(), MetaType.datapod.toString());
			return new OrderKey(dp.getUuid(), dp.getVersion());
		} else if (operationInfoHolder != null && operationInfoHolder.getRef() != null
				&& operationInfoHolder.getRef().getType().equals(MetaType.map)) {
			MetaIdentifier mapRef = operationInfoHolder.getRef();
			//com.inferyx.framework.domain.Map map = mapServiceImpl.findLatestByUuid(mapRef.getUuid());
			com.inferyx.framework.domain.Map map = (Map) commonServiceImpl.getLatestByUuid(mapRef.getUuid(), MetaType.map.toString());
			datapodKey = map.getTarget().getRef().getKey();
			if (DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()).get(MetaType.datapod + "_" + datapodKey.getUUID()) != null) {
				datapodKey.setVersion(DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()).get(MetaType.datapod + "_" + datapodKey.getUUID()).getVersion());
			} else {
				Datapod targetDatapod = (Datapod) daoRegister
						.getRefObject(new MetaIdentifier(MetaType.datapod, map.getTarget().getRef().getUuid(), null));
				datapodKey.setVersion(targetDatapod.getVersion());
			}
			return datapodKey;
		} else if (operationInfoHolder != null && operationInfoHolder.getRef() != null
				&& operationInfoHolder.getRef().getType().equals(MetaType.dq)) {
			MetaIdentifier mapRef = operationInfoHolder.getRef();
			//DataQual dataQual = dataqualServiceImpl.findLatestByUuid(mapRef.getUuid());
			DataQual dataQual = (DataQual) commonServiceImpl.getLatestByUuid(mapRef.getUuid(), MetaType.dq.toString());
			Datapod targetDatapod = (Datapod) daoRegister
					.getRefObject(new MetaIdentifier(MetaType.datapod, dqInfo.getDqTargetUUID(), null));
			return new OrderKey(targetDatapod.getUuid(),
					targetDatapod.getVersion());
		}  else if (operationInfoHolder != null && operationInfoHolder.getRef() != null
				&& operationInfoHolder.getRef().getType().equals(MetaType.rule)) {
			MetaIdentifier ruleRef = operationInfoHolder.getRef();
			//Rule rule = ruleServiceImpl.findLatestByUuid(ruleRef.getUuid());
			Rule rule = (Rule) commonServiceImpl.getLatestByUuid(ruleRef.getUuid(), MetaType.rule.toString());
		} else if (operationInfoHolder != null && operationInfoHolder.getRef() != null
				&& operationInfoHolder.getRef().getType().equals(MetaType.profile)) {
			MetaIdentifier mapRef = operationInfoHolder.getRef();
			//Profile profile = profileServiceImpl.findLatestByUuid(mapRef.getUuid());
			Profile profile = (Profile) commonServiceImpl.getLatestByUuid(mapRef.getUuid(), MetaType.profile.toString());
			/*Datapod targetDatapod = (Datapod) daoRegister
					.getRefObject(new MetaIdentifier(MetaType.datapod, "77aefb4c-191c-11e7-93ae-92361f002671", null));*/
			Datapod targetDatapod = (Datapod) daoRegister
					.getRefObject(new MetaIdentifier(MetaType.datapod, profileInfo.getProfileTargetUUID(), null));
			return new OrderKey(targetDatapod.getUuid(),
					targetDatapod.getVersion());
		}  else if (operationInfoHolder != null && operationInfoHolder.getRef() != null
				&& operationInfoHolder.getRef().getType().equals(MetaType.recon)) {
			MetaIdentifier mapRef = operationInfoHolder.getRef();
			Recon recon = (Recon) commonServiceImpl.getLatestByUuid(mapRef.getUuid(), MetaType.recon.toString());
			/*Datapod targetDatapod = (Datapod) daoRegister
					.getRefObject(new MetaIdentifier(MetaType.datapod, "77aefb4c-191c-11e7-93ae-92361f002699", null));*/
			Datapod targetDatapod = (Datapod) daoRegister
					.getRefObject(new MetaIdentifier(MetaType.datapod, reconInfo.getReconTargetUUID(), null));
			return new OrderKey(targetDatapod.getUuid(),
					targetDatapod.getVersion());
		}  
		return null;
	}
	
	/**
	 * This method submits task after setting the appropriate properties
	 * @param indvTaskExec
	 * @param datapodKey
	 * @param indvTask
	 * @param operationInfoHolder
	 * @param taskList
	 * @throws JsonProcessingException 
	 */
	public void setTaskAndSubmit(TaskExec indvTaskExec, OrderKey datapodKey, Task indvTask, MetaIdentifierHolder operationInfoHolder, List<FutureTask> taskList, RunMode runMode) throws JsonProcessingException {
		TaskServiceImpl indivTaskExe = new TaskServiceImpl();
		if (!dagExecServiceImpl.getStageStatus(dagExecUUID, dagExecVer, stageId).equals(Status.Stage.OnHold) 
				&& !dagExecServiceImpl.getTaskStatus(dagExecUUID, dagExecVer, stageId, indvTaskExec.getTaskId()).equals(Status.Stage.OnHold)) {

			//Set Task to InProgress
			synchronized (dagExecUUID) {
				try {
					commonServiceImpl.setMetaStatusForTask(dagExec, indvTaskExec, Status.Stage.InProgress, stageId, indvTaskExec.getTaskId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		logger.info("Map name of uuid_version:" + dagExec.getUuid() + "_" + indvTaskExec.getTaskId());
		String filePath = null;
		indivTaskExe.setName("Task_"+ dagExec.getUuid() + "_" + indvTaskExec.getTaskId());
		if (operationInfoHolder != null && operationInfoHolder.getRef() != null
				&& (operationInfoHolder.getRef().getType().equals(MetaType.map) 
					|| operationInfoHolder.getRef().getType().equals(MetaType.load)
					|| operationInfoHolder.getRef().getType().equals(MetaType.profile)
					|| operationInfoHolder.getRef().getType().equals(MetaType.dq)
					|| operationInfoHolder.getRef().getType().equals(MetaType.recon))) {
			filePath = String.format("/%s/%s/%s", 
					datapodKey.getUUID(), datapodKey.getVersion(), dagExec.getVersion());
		}
		indivTaskExe.setDependsOn(indvTask.getDependsOn());
		indivTaskExe.setDagExecServiceImpl(dagExecServiceImpl);
		indivTaskExe.setDagExecUUID(dagExec.getUuid());
		indivTaskExe.setStageId(stageExec.getStageId());
		indivTaskExe.setTaskId(indvTaskExec.getTaskId());
		indivTaskExe.setIndvTask(indvTask);
		indivTaskExe.setDagExecVer(dagExec.getVersion());
		indivTaskExe.setDatapodKey(datapodKey);
		indivTaskExe.setDaoRegister(daoRegister);
		indivTaskExe.setDatasourceFactory(datasourceFactory);
		indivTaskExe.setDataqualServiceImpl(dataqualServiceImpl);
		indivTaskExe.setDataqualGroupServiceImpl(dataqualGroupServiceImpl);
		indivTaskExe.setDataqualExecServiceImpl(dataqualExecServiceImpl);
		indivTaskExe.setDataqualGroupExecServiceImpl(dataqualGroupExecServiceImpl);
		indivTaskExe.setRuleServiceImpl(ruleServiceImpl);
		indivTaskExe.setRuleExecServiceImpl(ruleExecServiceImpl);
		indivTaskExe.setRuleGroupServiceImpl(ruleGroupServiceImpl);
		indivTaskExe.setRuleGroupExecServiceImpl(ruleGroupExecServiceImpl);
		
		indivTaskExe.setProfileServiceImpl(profileServiceImpl);
		indivTaskExe.setProfileGroupServiceImpl(profileGroupServiceImpl);
		indivTaskExe.setProfileExecServiceImpl(profileExecServiceImpl);
		indivTaskExe.setProfileGroupExecServiceImpl(profileGroupExecServiceImpl);
		indivTaskExe.setModelServiceImpl(modelServiceImpl);
		indivTaskExe.setModelExecServiceImpl(modelExecServiceImpl);
		indivTaskExe.setRunMode(runMode);
		
		DataStore datastore = new DataStore();
		datastore.setCreatedBy(dagExec.getCreatedBy());
		indivTaskExe.setDataStore(datastore);
		
		indivTaskExe.setiDataStore(dataStoreServiceImpl);
		indivTaskExe.setDatapodServiceImpl(datapodServiceImpl);
		indivTaskExe.setOperatorServiceImpl(operatorServiceImpl);
		indivTaskExe.setLoadServiceImpl(loadServiceImpl);
		indivTaskExe.setMapServiceImpl(mapServiceImpl);
		indivTaskExe.setFilePath(filePath);
		indivTaskExe.setDagServiceImpl(dagServiceImpl);
		indivTaskExe.setHdfsInfo(hdfsInfo);
		Operator operator = indvTask.getOperators().get(0); 
		indivTaskExe.setOperatorInfo(operator.getOperatorInfo());
		indivTaskExe.setCommonServiceImpl(commonServiceImpl);
		indivTaskExe.setOperatorType(operator.getOperatorType());
		indivTaskExe.setExecParams(execParams);
		indivTaskExe.setDag(dag);
		indivTaskExe.setDataStoreServiceImpl(dataStoreServiceImpl);
		indivTaskExe.setSessionContext(sessionContext);
		indivTaskExe.setParamSetServiceImpl(paramSetServiceImpl);
		FutureTask<String> futureTask = new FutureTask<String>(indivTaskExe);
		indivTaskExe.setReconServiceImpl(reconServiceImpl);
		indivTaskExe.setReconGroupServiceImpl(reconGroupServiceImpl);
		taskExecutor.execute(futureTask);
		taskList.add(futureTask);
		taskThreadMap.put("Task_" + dagExec.getUuid() + "_" + indvTaskExec.getTaskId(), futureTask);
	}

}

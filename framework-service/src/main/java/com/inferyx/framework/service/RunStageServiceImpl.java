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
import org.apache.spark.sql.Row;import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DQInfo;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.ProfileInfo;
import com.inferyx.framework.common.ReconInfo;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.Ingest;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.TaskOperator;
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
	private CustomOperatorServiceImpl operatorServiceImpl;
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
	@SuppressWarnings("rawtypes")
	ConcurrentHashMap taskThreadMap;
	DQInfo dqInfo;
	DagExec dagExec;
	Stage stage;
	private RunMode runMode;
	private SessionContext sessionContext;
	private ReconServiceImpl reconServiceImpl;
	private ReconGroupServiceImpl reconGroupServiceImpl;
	ProfileInfo profileInfo;
	ReconInfo reconInfo;
	private IngestServiceImpl ingestServiceImpl;
	private IngestExecServiceImpl ingestExecServiceImpl;
	private IngestGroupServiceImpl ingestGroupServiceImpl;
	private ReportServiceImpl reportServiceImpl;
	private DashboardServiceImpl dashboardServiceImpl;
	private Rule2ServiceImpl rule2ServiceImpl;
	
	/**
	 * @Ganesh
	 *
	 * @return the rule2ServiceImpl
	 */
	public Rule2ServiceImpl getRule2ServiceImpl() {
		return rule2ServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param rule2ServiceImpl the rule2ServiceImpl to set
	 */
	public void setRule2ServiceImpl(Rule2ServiceImpl rule2ServiceImpl) {
		this.rule2ServiceImpl = rule2ServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the reportServiceImpl
	 */
	public ReportServiceImpl getReportServiceImpl() {
		return reportServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param reportServiceImpl the reportServiceImpl to set
	 */
	public void setReportServiceImpl(ReportServiceImpl reportServiceImpl) {
		this.reportServiceImpl = reportServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the dashboardServiceImpl
	 */
	public DashboardServiceImpl getDashboardServiceImpl() {
		return dashboardServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param dashboardServiceImpl the dashboardServiceImpl to set
	 */
	public void setDashboardServiceImpl(DashboardServiceImpl dashboardServiceImpl) {
		this.dashboardServiceImpl = dashboardServiceImpl;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the ingestServiceImpl
	 */
	public IngestServiceImpl getIngestServiceImpl() {
		return ingestServiceImpl;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param ingestServiceImpl the ingestServiceImpl to set
	 */
	public void setIngestServiceImpl(IngestServiceImpl ingestServiceImpl) {
		this.ingestServiceImpl = ingestServiceImpl;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the ingestExecServiceImpl
	 */
	public IngestExecServiceImpl getIngestExecServiceImpl() {
		return ingestExecServiceImpl;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param ingestExecServiceImpl the ingestExecServiceImpl to set
	 */
	public void setIngestExecServiceImpl(IngestExecServiceImpl ingestExecServiceImpl) {
		this.ingestExecServiceImpl = ingestExecServiceImpl;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the ingestGroupServiceImpl
	 */
	public IngestGroupServiceImpl getIngestGroupServiceImpl() {
		return ingestGroupServiceImpl;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param ingestGroupServiceImpl the ingestGroupServiceImpl to set
	 */
	public void setIngestGroupServiceImpl(IngestGroupServiceImpl ingestGroupServiceImpl) {
		this.ingestGroupServiceImpl = ingestGroupServiceImpl;
	}

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

	public CustomOperatorServiceImpl getOperatorServiceImpl() {
		return operatorServiceImpl;
	}

	public void setOperatorServiceImpl(CustomOperatorServiceImpl operatorServiceImpl) {
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

	public ConcurrentHashMap<?, ?> getTaskThreadMap() {
		return taskThreadMap;
	}

	public void setTaskThreadMap(ConcurrentHashMap<?, ?> taskThreadMap) {
		this.taskThreadMap = taskThreadMap;
	}

	public DQInfo getDqInfo() {
		return dqInfo;
	}

	public void setDqInfo(DQInfo dqInfo) {
		this.dqInfo = dqInfo;
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
		logger.info("Thread watch : DagExec : " + dagExec.getUuid() + " StageExec : " + stageId + " status RUN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
		FrameworkThreadLocal.getSessionContext().set(sessionContext);
		checkDependencyStatus();
		
		return execute();
	}
	
	public void checkDependencyStatus() throws JsonProcessingException {
		FrameworkThreadLocal.getSessionContext().set(sessionContext);
		boolean checkDependencyStatus = false;
//		boolean checkDependencyFAILED = false;
		@SuppressWarnings("unused")
		boolean checkDependencyKILLED = false;
		String dependencyStatus = null;
		
		while(!checkDependencyStatus){
			dependencyStatus = dagExecServiceImpl.checkStageDepStatus(dag,dagExecUUID,dagExecVer,stageId);
			logger.info("Stage dependencyStatus : " + stageId + " : " + dependencyStatus);
			if (StringUtils.isBlank(dependencyStatus) || dependencyStatus.equalsIgnoreCase(Status.Stage.PENDING.toString())) {
				checkDependencyStatus = false;
			} else if (dependencyStatus.equalsIgnoreCase(Status.Stage.KILLED.toString())) {
				checkDependencyKILLED = true;
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
	
	@SuppressWarnings("rawtypes")
	public String execute() throws Exception {
		logger.info("Thread watch : DagExec : " + dagExec.getUuid() + " StageExec : " + stageId + " start execute in RUN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
		FrameworkThreadLocal.getSessionContext().set(sessionContext);
//		List<Status> stageStatusList = stageExec.getStatusList();
		
		List<TaskExec> dagTaskExecs = DagExecUtil.castToTaskExecList(stageExec.getTasks());
		List<FutureTask> taskList = new ArrayList<FutureTask>();
//		Status.Stage stageStatus = Helper.getLatestStatus(stageStatusList).getStage();
		List<TaskExec> depTaskExecs = new ArrayList<>();
		
		try {
			logger.info("Setting status to RUNNING for stage : " + stageId);
			synchronized (dagExec.getUuid()) {
				commonServiceImpl.setMetaStatusForStage (dagExec, stageExec, Status.Stage.RUNNING, stageExec.getStageId());
			}
		} catch (Exception e1) {
			logger.error("Stage Exec RUNNING status could not be set ");
			e1.printStackTrace();
		}
		
		do {
			//Start the tasks which has on dependency
			for (TaskExec indvTaskExec : dagTaskExecs) {
				OrderKey datapodKey = null;
				
				Task indvTask = DagExecUtil.getTaskFromStage(stage, indvTaskExec.getTaskId());
	//			Status taskStatus = Helper.getLatestStatus(indvTaskExec.getStatus());
	//			if (taskStatus.equals(new Status(Status.Stage.RUNNING, new Date())) 
	//					|| taskStatus.equals(new Status(Status.Stage.COMPLETED, new Date()))
	//					|| taskStatus.equals(new Status(Status.Stage.PAUSE, new Date()))) {
	//				continue;
	//			}
	
				//Fetch from mongo instead of udta panchi.
				com.inferyx.framework.domain.Status.Stage taskStatus = dagExecServiceImpl.getTaskStatus(dagExecUUID, dagExecVer, stageId, indvTaskExec.getTaskId());			
				/*if (taskStatus.equals(Status.Stage.RUNNING) 
						|| taskStatus.equals(Status.Stage.COMPLETED)
						|| taskStatus.equals(Status.Stage.PAUSE)) {
					continue;
				}*/
				
				if (!taskStatus.equals(Status.Stage.READY)) {
					logger.info("Task status for task id " + indvTaskExec.getTaskId() + " is not READY. So going to next task for execution. ");
					continue;
				}
				
				/*if (taskStatus.equals(Status.Stage.RESUME)) {
					indvTaskExec.setDependsOn(new ArrayList<>());
				}*/
				
				// Check if task has any dependents
				if (indvTaskExec.getDependsOn() != null 
						&& !indvTaskExec.getDependsOn().isEmpty()) {
					depTaskExecs.add(indvTaskExec);	// Put in dependency list. Shall be handled in a separate loop
					continue;
				}
	
				datapodKey = fetchDatapodKey(indvTask);
				// Fetch datapod key
				for(MetaIdentifierHolder operationInfoHolder : indvTask.getOperators().get(0).getOperatorInfo()) {
					if (datapodKey == null) {
						if (operationInfoHolder != null && operationInfoHolder.getRef() != null
							&& !(operationInfoHolder.getRef().getType().equals(MetaType.dag) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.dqgroup) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.profilegroup) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.rule) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.rulegroup) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.train) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.predict) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.simulate) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.recon) 
								|| operationInfoHolder.getRef().getType().equals(MetaType.recongroup)
								/*|| operationInfoHolder.getRef().getType().equals(MetaType.operatortype)*/
								|| operationInfoHolder.getRef().getType().equals(MetaType.operator))
								|| operationInfoHolder.getRef().getType().equals(MetaType.ingest)
								|| operationInfoHolder.getRef().getType().equals(MetaType.ingestgroup)
								|| operationInfoHolder.getRef().getType().equals(MetaType.report)
								|| operationInfoHolder.getRef().getType().equals(MetaType.dashboard)
								|| operationInfoHolder.getRef().getType().equals(MetaType.rule2)) {
						continue;
						}
					}
				}
				logger.info("Calling task id : " + indvTask.getTaskId());
				// Set task and submit for execution
				setTaskAndSubmit(indvTaskExec, datapodKey, indvTask, indvTask.getOperators().get(0).getOperatorInfo(), taskList, runMode);
			
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
					logger.info("Checking : "+indvTaskExec.getTaskId() + ":" + indvTaskExec.getName());
					if (dependentTaskRunningLog.contains(dagExec.getUuid() + "_" + stageId + "_" + indvTaskExec.getTaskId())) {
						continue;
					}
	
					boolean checkDependencyStatus = false;
					boolean checkDependencyFAILED = false;
					boolean checkDependencyKILLED = false;
					boolean checkDependencyRESUME = false;
					boolean checkDependencyPAUSE = false;
					boolean checkDependencyABORTED = false;
					String dependencyStatus = null;
					OrderKey datapodKey = null;
					
					Task indvTask = DagExecUtil.getTaskFromStage(stage, indvTaskExec.getTaskId());
					
	//				Status taskStatus = Helper.getLatestStatus(indvTaskExec.getStatus());				
	//				if (taskStatus.equals(new Status(Status.Stage.RUNNING, new Date())) 
	//						|| taskStatus.equals(new Status(Status.Stage.COMPLETED, new Date()))
	//						|| taskStatus.equals(new Status(Status.Stage.PAUSE, new Date()))) {
	//					continue;
	//				}
	
					//Fetch from mongo instead of udta panchi.				
					com.inferyx.framework.domain.Status.Stage taskStatus = dagExecServiceImpl.getTaskStatus(dagExecUUID, dagExecVer, stageId, indvTaskExec.getTaskId());			
					/*if (taskStatus.equals(Status.Stage.RUNNING) 
							|| taskStatus.equals(Status.Stage.COMPLETED)
							|| taskStatus.equals(Status.Stage.PAUSE)) {
						continue;
					}*/
					if (!taskStatus.equals(Status.Stage.STARTING) 
							&& !taskStatus.equals(Status.Stage.READY)
							&& !taskStatus.equals(Status.Stage.RUNNING)
							&& !taskStatus.equals(Status.Stage.RESUME)) {
						continue;
					}
					
					// If not checkdependency status then continue after setting allDependenciesAddressed to false
					dependencyStatus = dagExecServiceImpl.checkTaskDepStatus(dag,dagExecUUID,dagExecVer,stageId,indvTaskExec.getTaskId());
					logger.info("Task dependencyStatus : " + indvTaskExec.getTaskId() + " : " + dependencyStatus);
					if (StringUtils.isBlank(dependencyStatus) 
							|| dependencyStatus.equalsIgnoreCase(Status.Stage.PENDING.toString())
							|| dependencyStatus.equalsIgnoreCase(Status.Stage.STARTING.toString()) 
							|| dependencyStatus.equalsIgnoreCase(Status.Stage.READY.toString())) {
						checkDependencyStatus = false;
					} else if (dependencyStatus.equalsIgnoreCase(Status.Stage.KILLED.toString())) {
						checkDependencyKILLED = true;
						checkDependencyStatus = true;
						break;
					} else if (dependencyStatus.equalsIgnoreCase(Status.Stage.FAILED.toString())) {
						checkDependencyFAILED = true;
						checkDependencyStatus = true;
						break;
					} else if (dependencyStatus.equalsIgnoreCase(Status.Stage.ABORTED.toString())) {
						checkDependencyABORTED = true;
						checkDependencyStatus = true;
						break;
					} else if (dependencyStatus.equalsIgnoreCase(Status.Stage.PAUSE.toString())) {
						checkDependencyPAUSE = true;
						checkDependencyStatus = true;
						break;
					} else if (dependencyStatus.equalsIgnoreCase(Status.Stage.RESUME.toString())) {
						checkDependencyRESUME = true;
						checkDependencyStatus = true;
						break;
					} else {
						checkDependencyStatus = true;
					}
					logger.info(" checkDependencyStatus : checkDependencyKILLED : checkDependencyFAILED : checkDependencyABORTED: checkDependencyPAUSE : checkDependencyRESUME : " 
								+ checkDependencyStatus + ":" + checkDependencyKILLED + ":" + checkDependencyFAILED + ":" + checkDependencyABORTED + ":" + checkDependencyPAUSE + ":" + checkDependencyRESUME);
					if (checkDependencyKILLED) {
						synchronized (dagExecUUID) {
							try {
								logger.info("Setting status to KILLED for stage : " + stageId);
								commonServiceImpl.setMetaStatusForTask(dagExec, indvTaskExec, Status.Stage.KILLED, stageId, indvTaskExec.getTaskId());
								continue;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
					if (checkDependencyFAILED) {
						synchronized (dagExecUUID) {
							try {
								logger.info("Setting status to FAILED for stage : " + stageId);
								commonServiceImpl.setMetaStatusForTask(dagExec, indvTaskExec, Status.Stage.FAILED, stageId, indvTaskExec.getTaskId());
								continue;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
					if (checkDependencyABORTED) {
						synchronized (dagExecUUID) {
							try {
								logger.info("Setting status to FAILED for stage : " + stageId);
								commonServiceImpl.setMetaStatusForTask(dagExec, indvTaskExec, Status.Stage.FAILED, stageId, indvTaskExec.getTaskId());
								continue;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
					if (checkDependencyPAUSE) {
						synchronized (dagExecUUID) {
							try {
								logger.info("Setting status to PAUSE for stage : " + stageId);
								commonServiceImpl.setMetaStatusForTask(dagExec, indvTaskExec, Status.Stage.RESUME, stageId, indvTaskExec.getTaskId());
								continue;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
					if (checkDependencyRESUME) {
						synchronized (dagExecUUID) {
							try {
								logger.info("Setting status to RESUME for stage : " + stageId);
								commonServiceImpl.setMetaStatusForTask(dagExec, indvTaskExec, Status.Stage.RESUME, stageId, indvTaskExec.getTaskId());
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
					for(MetaIdentifierHolder operationInfoHolder : indvTask.getOperators().get(0).getOperatorInfo()) {
						if (datapodKey == null) {
							if (operationInfoHolder != null && operationInfoHolder.getRef() != null
								&& !(operationInfoHolder.getRef().getType().equals(MetaType.dag) 
									|| operationInfoHolder.getRef().getType().equals(MetaType.dqgroup) 
									|| operationInfoHolder.getRef().getType().equals(MetaType.profilegroup) 
									|| operationInfoHolder.getRef().getType().equals(MetaType.rule)
									|| operationInfoHolder.getRef().getType().equals(MetaType.rulegroup)
									|| operationInfoHolder.getRef().getType().equals(MetaType.train) 
									|| operationInfoHolder.getRef().getType().equals(MetaType.predict) 
									|| operationInfoHolder.getRef().getType().equals(MetaType.simulate)
									|| operationInfoHolder.getRef().getType().equals(MetaType.recon) 
									|| operationInfoHolder.getRef().getType().equals(MetaType.recongroup)
									/*|| operationInfoHolder.getRef().getType().equals(MetaType.operatortype)*/
									|| operationInfoHolder.getRef().getType().equals(MetaType.operator))
									|| operationInfoHolder.getRef().getType().equals(MetaType.ingest)
									|| operationInfoHolder.getRef().getType().equals(MetaType.ingestgroup)
									|| operationInfoHolder.getRef().getType().equals(MetaType.report)
									|| operationInfoHolder.getRef().getType().equals(MetaType.dashboard)
									|| operationInfoHolder.getRef().getType().equals(MetaType.rule2)) {
							continue;
							}
						}
					}
					
					logger.info("Calling task id 2 : " + indvTask.getTaskId());
					dependentTaskRunningLog.add(dagExec.getUuid() + "_" + stageId + "_" + indvTaskExec.getTaskId());
					// Set task and submit for execution
					setTaskAndSubmit(indvTaskExec, datapodKey, indvTask, indvTask.getOperators().get(0).getOperatorInfo(), taskList, runMode);
				
				}	// For all taskExecs
			}
			Thread.sleep(5000);
		} while(!waitAndComplete(taskList));
		
		logger.info("Thread watch : DagExec : " + dagExec.getUuid() + " StageExec : " + stageId + " complete >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
		return name;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
			                logger.info("Thread " + outputThreadName + " COMPLETED ");
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
				dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(dagExecUUID, dagExecVer, MetaType.dagExec.toString(), "N");
				stageExec = DagExecUtil.getStageExecFromDagExec(dagExec, stageId);
				Status.Stage latestStatus = Helper.getLatestStatus(stageExec.getStatusList()).getStage();
				RunStatusHolder statusHolder = new RunStatusHolder(Boolean.TRUE, 
																	Boolean.FALSE, 
																	Boolean.FALSE, 
																	Boolean.FALSE, 
																	Boolean.FALSE, 
																	Boolean.FALSE);
				// Update run status for stage
				Helper.updateRunStatus(latestStatus, statusHolder);
				logger.info(" StatusHolder of stageExec : " + statusHolder.getCOMPLETED() + ":" + statusHolder.getKILLED() + ":" + statusHolder.getFAILED() 
							+ ":" + statusHolder.getPAUSE() + ":" + statusHolder.getRESUME() + ":" + statusHolder.getABORTED());
				if (statusHolder.getCOMPLETED() && statusHolder.getKILLED()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.KILLED, stageId);
					}
					return true;
				} else if (statusHolder.getCOMPLETED() && statusHolder.getFAILED()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.FAILED, stageId);
					}
					return true;
				} else if (statusHolder.getCOMPLETED() && statusHolder.getABORTED()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.FAILED, stageId);
					}
					return true;
				} else if (statusHolder.getCOMPLETED()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.COMPLETED, stageId);
					}
					return true;
				} 
				
				// Check status of all metas and then decide on the group status  
				statusHolder = new RunStatusHolder(Boolean.TRUE, 
						Boolean.FALSE, 
						Boolean.FALSE, 
						Boolean.FALSE, 
						Boolean.FALSE, 
						Boolean.FALSE);
				
				for (TaskExec taskExec : DagExecUtil.castToTaskExecList(stageExec.getTasks())) {
					latestStatus = Helper.getLatestStatus(taskExec.getStatusList()).getStage();
					Helper.updateRunStatus(latestStatus, statusHolder);
				}// End for
				
				logger.info(" StatusHolder of taskExec : " + statusHolder.getCOMPLETED() + ":" + statusHolder.getKILLED() + ":" + statusHolder.getFAILED() 
				+ ":" + statusHolder.getPAUSE() + ":" + statusHolder.getRESUME() + ":" + statusHolder.getABORTED());
				
				if (statusHolder.getCOMPLETED() && statusHolder.getKILLED()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.KILLED, stageId);
					}
					return true;
				} else if (statusHolder.getCOMPLETED() && statusHolder.getFAILED()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.FAILED, stageId);
					}
					return true;
				} else if (statusHolder.getFAILED()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.FAILED, stageId);
					}
					return true;					
				} else if (statusHolder.getCOMPLETED() && statusHolder.getABORTED()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.FAILED, stageId);
					}
					return true;
				} else if (statusHolder.getABORTED()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.FAILED, stageId);
					}
					return true;
				} else if (statusHolder.getCOMPLETED()) {
					synchronized (dagExecUUID) {
						commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.COMPLETED, stageId);
					}
					return true;
				} 
		} catch (Exception e) {
			synchronized (dagExecUUID) {
				commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.FAILED, stageId);
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
	@SuppressWarnings("unused")
	public OrderKey fetchDatapodKey(Task indvTask) throws JsonProcessingException {
		OrderKey datapodKey = null;
		// Fetch datapod key
		for(MetaIdentifierHolder operationInfoHolder : indvTask.getOperators().get(0).getOperatorInfo()) {
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
//					Datapod targetDatapod = (Datapod) daoRegister.getRefObject(new MetaIdentifier(MetaType.datapod, map.getTarget().getRef().getUuid(), null));
					Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(map.getTarget().getRef().getUuid(), map.getTarget().getRef().getVersion(), MetaType.datapod.toString(), "N");
					datapodKey.setVersion(targetDatapod.getVersion());
				}
				return datapodKey;
			} else if (operationInfoHolder != null && operationInfoHolder.getRef() != null
					&& operationInfoHolder.getRef().getType().equals(MetaType.dq)) {
				MetaIdentifier mapRef = operationInfoHolder.getRef();
				//DataQual dataQual = dataqualServiceImpl.findLatestByUuid(mapRef.getUuid());
//				DataQual dataQual = (DataQual) commonServiceImpl.getLatestByUuid(mapRef.getUuid(), MetaType.dq.toString());
//				Datapod targetDatapod = (Datapod) daoRegister.getRefObject(new MetaIdentifier(MetaType.datapod, dqInfo.getDqTargetUUID(), null));
//				Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dqInfo.getDqTargetUUID(), null, MetaType.datapod.toString(), "N");
				Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dqInfo.getDq_result_detail(), null, MetaType.datapod.toString(), "N");
				
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
//				Profile profile = (Profile) commonServiceImpl.getLatestByUuid(mapRef.getUuid(), MetaType.profile.toString());
				/*Datapod targetDatapod = (Datapod) daoRegister
						.getRefObject(new MetaIdentifier(MetaType.datapod, "77aefb4c-191c-11e7-93ae-92361f002671", null));*/
//				Datapod targetDatapod = (Datapod) daoRegister.getRefObject(new MetaIdentifier(MetaType.datapod, profileInfo.getProfileTargetUUID(), null));
				Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(profileInfo.getProfileTargetUUID(), null, MetaType.datapod.toString(), "N");
				
				return new OrderKey(targetDatapod.getUuid(),
						targetDatapod.getVersion());
			}  else if (operationInfoHolder != null && operationInfoHolder.getRef() != null
					&& operationInfoHolder.getRef().getType().equals(MetaType.recon)) {
//				MetaIdentifier reconRef = operationInfoHolder.getRef();
//				Recon recon = (Recon) commonServiceImpl.getLatestByUuid(reconRef.getUuid(), MetaType.recon.toString());
				/*Datapod targetDatapod = (Datapod) daoRegister
						.getRefObject(new MetaIdentifier(MetaType.datapod, "77aefb4c-191c-11e7-93ae-92361f002699", null));*/
//				Datapod targetDatapod = (Datapod) daoRegister.getRefObject(new MetaIdentifier(MetaType.datapod, reconInfo.getReconTargetUUID(), null));
				Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(reconInfo.getReconTargetUUID(), null, MetaType.datapod.toString(), "N");
				
				return new OrderKey(targetDatapod.getUuid(),
						targetDatapod.getVersion());
			} else if (operationInfoHolder != null && operationInfoHolder.getRef() != null
					&& operationInfoHolder.getRef().getType().equals(MetaType.ingest)) {
				MetaIdentifier ingestRef = operationInfoHolder.getRef();
				Ingest ingest = (Ingest) commonServiceImpl.getLatestByUuid(ingestRef.getUuid(), MetaType.ingest.toString());
				if(ingest.getTargetDetail().getRef().getUuid() != null) {
					Datapod targetDp = (Datapod) commonServiceImpl.getLatestByUuid(ingest.getTargetDetail().getRef().getUuid(), MetaType.datapod.toString());
					return new OrderKey(targetDp.getUuid(), targetDp.getVersion());
				}
			} 
		}		 
		return null;
	}
	
	/**
	 * This method submits task after setting the appropriate properties
	 * @param indvTaskExec
	 * @param datapodKey
	 * @param indvTask
	 * @param operationInfoHolderList
	 * @param taskList
	 * @throws JsonProcessingException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setTaskAndSubmit(TaskExec indvTaskExec, OrderKey datapodKey, Task indvTask, List<MetaIdentifierHolder> operationInfoHolderList, List<FutureTask> taskList, RunMode runMode) throws JsonProcessingException {
		TaskServiceImpl indivTaskExe = new TaskServiceImpl();
		if (dagExecServiceImpl.getStageStatus(dagExecUUID, dagExecVer, stageId).equals(Status.Stage.READY) 
				&& dagExecServiceImpl.getTaskStatus(dagExecUUID, dagExecVer, stageId, indvTaskExec.getTaskId()).equals(Status.Stage.READY)) {

			//Set Task to RUNNING
			synchronized (dagExecUUID) {
				try {
					commonServiceImpl.setMetaStatusForTask(dagExec, indvTaskExec, Status.Stage.RUNNING, stageId, indvTaskExec.getTaskId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		logger.info("Map name of uuid_version:" + dagExec.getUuid() + "_" + indvTaskExec.getTaskId());
		String filePath = null;
		indivTaskExe.setName("Task_"+ dagExec.getUuid() + "_" + indvTaskExec.getTaskId());
		if (operationInfoHolderList != null && operationInfoHolderList.get(0).getRef() != null
				&& (operationInfoHolderList.get(0).getRef().getType().equals(MetaType.map) 
					|| operationInfoHolderList.get(0).getRef().getType().equals(MetaType.load)
					|| operationInfoHolderList.get(0).getRef().getType().equals(MetaType.profile)
					|| operationInfoHolderList.get(0).getRef().getType().equals(MetaType.dq)
					|| operationInfoHolderList.get(0).getRef().getType().equals(MetaType.recon))) {
			filePath = String.format("/%s/%s/%s", datapodKey.getUUID(), datapodKey.getVersion(), dagExec.getVersion());
		}
		indivTaskExe.setDependsOn(indvTask.getDependsOn());
		indivTaskExe.setDagExecServiceImpl(dagExecServiceImpl);
		indivTaskExe.setDagExecUUID(dagExec.getUuid());
		indivTaskExe.setStageId(stageExec.getStageId());
		indivTaskExe.setTaskId(indvTaskExec.getTaskId());
		indivTaskExe.setIndvTask(indvTask);
		indivTaskExe.setDagExecVer(dagExec.getVersion());
		indivTaskExe.setDatapodKey(datapodKey);
//		indivTaskExe.setDaoRegister(daoRegister);
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
		
		indivTaskExe.setiDataStore(dataStoreServiceImpl);
		indivTaskExe.setDatapodServiceImpl(datapodServiceImpl);
		indivTaskExe.setOperatorServiceImpl(operatorServiceImpl);
		indivTaskExe.setLoadServiceImpl(loadServiceImpl);
		indivTaskExe.setMapServiceImpl(mapServiceImpl);
		indivTaskExe.setFilePath(filePath);
		indivTaskExe.setDagServiceImpl(dagServiceImpl);
		indivTaskExe.setHdfsInfo(hdfsInfo);
		TaskOperator operator = indvTask.getOperators().get(0); 
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
		indivTaskExe.setIngestServiceImpl(ingestServiceImpl);
		indivTaskExe.setIngestExecServiceImpl(ingestExecServiceImpl);
		indivTaskExe.setIngestGroupServiceImpl(ingestGroupServiceImpl);
		indivTaskExe.setReportServiceImpl(reportServiceImpl);
		indivTaskExe.setDashboardServiceImpl(dashboardServiceImpl);
		indivTaskExe.setRule2ServiceImpl(rule2ServiceImpl);
		
		taskExecutor.execute(futureTask);
		logger.info("Thread watch : DagExec : " + dagExec.getUuid() + " StageExec : " + stageId + " taskExec : " + indvTask.getTaskId() + " started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
		taskList.add(futureTask);
		taskThreadMap.put("Task_" + dagExec.getUuid() + "_" + indvTaskExec.getTaskId(), futureTask);
	}

}

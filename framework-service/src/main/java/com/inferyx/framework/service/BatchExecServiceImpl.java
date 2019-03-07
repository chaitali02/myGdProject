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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DQInfo;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.ProfileInfo;
import com.inferyx.framework.common.ReconInfo;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.RunStatusHolder;
import com.inferyx.framework.domain.Stage;
import com.inferyx.framework.domain.StageExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Task;
import com.inferyx.framework.domain.TaskExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;

	@Service
	public class BatchExecServiceImpl {

		public static void main(String[] args) {
		

		}

		@Autowired
		DagExecServiceImpl dagExecServiceImpl;
		@Autowired
		HDFSInfo hdfsInfo;
		@Autowired
		DataStoreServiceImpl dataStoreServiceImpl;
		@Autowired
		CommonServiceImpl<?> commonServiceImpl;
		@Autowired
		DatapodServiceImpl datapodServiceImpl;
		@Autowired
		LoadServiceImpl loadServiceImpl;
		@Autowired
		CustomOperatorServiceImpl operatorServiceImpl;
		@Autowired
		ThreadPoolTaskExecutor stageExecutor;
		@Autowired
		ThreadPoolTaskExecutor taskExecutor;
		@Autowired
		MapServiceImpl mapServiceImpl;
		@Autowired
		DagServiceImpl dagServiceImpl;
		@Autowired
		protected DataSourceFactory datasourceFactory;
		@Autowired
		protected ExecutorFactory executorFactory;
		@Autowired
		private DataQualServiceImpl dataqualServiceImpl;
		@Autowired
		private DataQualExecServiceImpl dataqualExecServiceImpl;
		@Autowired
		private DataQualGroupServiceImpl dataqualGroupServiceImpl;
		@Autowired
		DQInfo dqInfo;
		@Autowired
		private DataQualGroupExecServiceImpl dataqualGroupExecServiceImpl;
		@Autowired
		private RuleServiceImpl ruleServiceImpl;
		@Autowired
		private RuleExecServiceImpl ruleExecServiceImpl;
		@Autowired
		private RuleGroupServiceImpl ruleGroupServiceImpl;
		@Autowired
		private RuleGroupExecServiceImpl ruleGroupExecServiceImpl;
		@Autowired
		private ProfileServiceImpl profileServiceImpl;
		@Autowired
		private ProfileExecServiceImpl profileExecServiceImpl;
		@Autowired
		private ProfileGroupServiceImpl profileGroupServiceImpl;
		@Autowired
		private ProfileGroupExecServiceImpl profileGroupExecServiceImpl;
		@Autowired 
		private ModelServiceImpl modelServiceImpl;
		@Autowired 
		private ModelExecServiceImpl modelExecServiceImpl;
		@Resource(name="taskThreadMap")
		ConcurrentHashMap taskThreadMap;
		@Autowired
		ParamSetServiceImpl paramSetServiceImpl;
		@Autowired
		private ReconServiceImpl reconServiceImpl;
		@Autowired
		private ReconGroupServiceImpl reconGroupServiceImpl;
		@Autowired
		private ProfileInfo profileInfo;
		@Autowired
		private ReconInfo reconInfo;
		@Autowired
		private IngestServiceImpl ingestServiceImpl;
		@Autowired
		private IngestExecServiceImpl ingestExecServiceImpl;
		@Autowired
		private IngestGroupServiceImpl ingestGroupServiceImpl;
		@Autowired
		private ReportServiceImpl reportServiceImpl;
		@Autowired
		private DashboardServiceImpl dashboardServiceImpl;
		
		static final Logger logger = Logger.getLogger(BatchExecServiceImpl.class);
	
		// @Autowired DataStore dataStore;
		static Map<String, TaskServiceImpl> mapTaskThread = new HashMap<String, TaskServiceImpl>();
	
		public String killDag (String uuid, String version) throws JsonProcessingException {
			logger.info("Inside kill dag");
			FutureTask<String> futureTask = null;
			@SuppressWarnings("unused")
			String status = null;
//			DagExec dagExec = (DagExec) daoRegister.getRefObject(new MetaIdentifier(MetaType.dagExec, uuid, version));
			DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString(), "N");
			
			try {
				synchronized (dagExec.getUuid()) {
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.TERMINATING);
				}
				
				if (!Helper.getLatestStatus(dagExec.getStatusList()).getStage().equals(Status.Stage.TERMINATING)) {
					logger.info("Status is not TERMINATING. So cannot proceed to kill ");
					return "Status is not TERMINATING. So cannot proceed to kill ";
				}
				futureTask = (FutureTask<String>) taskThreadMap.get("Dag_" + uuid);
				if (futureTask != null && !futureTask.isDone()) {
					logger.info("Going to kill Dag ###################### " + "Dag_" + uuid);
					futureTask.cancel(true);
					logger.info("Kill Signal sent to Dag Thread");
					status = "Kill Signal sent to Stage Thread";
					taskThreadMap.remove("Dag_" + uuid);
				} else {
					logger.info("DagThread is not alive. It might have COMPLETED its execution alReady");
					status = "DagThread is not alive. It might have COMPLETED its execution alReady";
				}
				while(! (futureTask == null || futureTask.isCancelled() || futureTask.isDone())) {
					logger.info("Sleeping thread Dag : " + "Dag_" + uuid);
					Thread.sleep(1000);
				}
				for (Stage stage : dagExec.getStages()) {
					killStage(uuid, version, stage.getStageId());
				}
				synchronized (dagExec.getUuid()) {
//					dagExec = (DagExec) daoRegister.getRefObject(new MetaIdentifier(MetaType.dagExec, uuid, version));
					dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString(), "N");
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.KILLED);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "Not KILLED.";
		}
	
		public String killStage (String uuid, String version, String stageId) throws JsonProcessingException {
			FutureTask futureTask = null;
			String status = null;
			StageExec stageExec = null;
///			DagExec dagExec = (DagExec) daoRegister.getRefObject(new MetaIdentifier(MetaType.dagExec, uuid, version));
			DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString(), "N");
			if (!taskThreadMap.containsKey("Stage_" + uuid + "_" + stageId)) {
				status = "Thread is not running";
				logger.info(" Stage " + stageId + " is not running. Kill nevertheless. ");
				// Try to set status as KILLED nevertheless
				int count=0;
				for (Stage stage : dagExec.getStages()) {
					
					if (stageId.equals(stage.getStageId())) {
						stageExec = DagExecUtil.convertToStageExec(stage);
						synchronized (uuid) {
							try {
								stageExec = (StageExec) commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.TERMINATING, stageId);
								if (!Helper.getLatestStatus(stageExec.getStatusList()).getStage().equals(Status.Stage.TERMINATING)) {
									logger.info("Stage status : " + Helper.getLatestStatus(stageExec.getStatusList()).getStage());
									logger.info("Status is not TERMINATING. So cannot proceed to kill ");
									break;
								}
								logger.info("Stage set to TERMINATING");
								for (Task task : stage.getTasks()) {
									killTask(uuid, version, stageId, task.getTaskId());
								}
//								 dagExec = (DagExec) daoRegister.getRefObject(new MetaIdentifier(MetaType.dagExec, uuid, version));
								 dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString(), "N");
								 stage=dagExec.getStages().get(count);
//								 count++; -- Commented this portion to fix status bug where COMPLETED stage goes into KILLED.
								 stageExec = DagExecUtil.convertToStageExec(stage);
								commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.KILLED, stageId);
							} catch (Exception e) {
								logger.error("Exception while setting TERMINATING/kill status");
								e.printStackTrace();
							}
						}
					}
					count++;					
				}	// End for
				return status;
			}
			for (Stage stage : dagExec.getStages()) {
				if (stageId.equals(stage.getStageId())) {
					stageExec = DagExecUtil.convertToStageExec(stage);
					logger.info("Going to kill stage ###################### " + "Stage_" + uuid + "_" + stageId);
					try {
						synchronized (dagExec.getUuid()) {
							stageExec = (StageExec)commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.TERMINATING, stageId);
						}
						if (!Helper.getLatestStatus(stageExec.getStatusList()).getStage().equals(Status.Stage.TERMINATING)) {
							logger.info("Stage status : " + Helper.getLatestStatus(stageExec.getStatusList()).getStage());
							logger.info("Status is not TERMINATING. So cannot proceed to kill ");
							break;
						}
						
					
						futureTask = (FutureTask<String>) taskThreadMap.get("Stage_" + uuid + "_" + stageId);
						if (futureTask != null && !futureTask.isDone()) {
							futureTask.cancel(true);
							logger.info("Kill Signal sent to Stage Thread");
							status = "Kill Signal sent to Stage Thread";
							while(! (futureTask == null || futureTask.isCancelled() || futureTask.isDone())) {
								logger.info("Sleeping thread Stage : " + "Stage_" + uuid + "_" + stageId);
								Thread.sleep(1000);
							}
						} else {
							logger.info("StageThread is not alive. It might have COMPLETED its execution alReady");
							status = "StageThread is not alive. It might have COMPLETED its execution alReady";
						}
						taskThreadMap.remove("Stage_" + uuid + "_" + stageId);
						
						for (Task task : stage.getTasks()) {
							try {
								killTask(uuid, version, stageId, task.getTaskId());
							} catch (Exception e) {
								logger.error("Exception while killing task : " + task.getTaskId());						
								e.printStackTrace();
							}
						}
						
						synchronized (dagExec.getUuid()) {
//							dagExec = (DagExec) daoRegister.getRefObject(new MetaIdentifier(MetaType.dagExec, uuid, version));
							dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString(), "N");
							stageExec = dagExecServiceImpl.getStageExec(dagExec, stageId);
							logger.info("Going to set status KILLED for stage");
							commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.KILLED, stageId);
						}
					} catch (Exception e) {
						logger.error("Stage couldn't be KILLED : " + stageId);						
						e.printStackTrace();
					}
					break;
				}
			}
			return status;
		}

		public String killTask(String uuid, String version, String stageId, String taskId) throws JsonProcessingException {
		//	Set<String> strSet = mapTaskThread.keySet();
			FutureTask futureTask = null;
//			DagExec dagExec = (DagExec) daoRegister.getRefObject(new MetaIdentifier(MetaType.dagExec, uuid, version));
			DagExec dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString(), "N");
			String status = null;
		
			TaskExec taskExec = dagExecServiceImpl.getTaskExec(dagExec, stageId, taskId);
			if (!taskThreadMap.containsKey("Task_" + uuid + "_" + taskId)) {
				logger.info("TaskId " + taskId + " is not running. Kill nevertheless. ");
				status = "Thread is not running";
				// Try to set status as KILLED nevertheless
				synchronized (uuid) {
					try {
						taskExec = (TaskExec) commonServiceImpl.setMetaStatusForTask(dagExec, taskExec, Status.Stage.TERMINATING, stageId, taskId);
						for(MetaIdentifierHolder operatorInfo : taskExec.getOperators().get(0).getOperatorInfo()) {
							try {
								commonServiceImpl.kill(operatorInfo.getRef().getType(), operatorInfo.getRef().getUuid(), operatorInfo.getRef().getVersion());
							} catch (Exception e) {
								logger.error("Exception while setting TERMINATING/kill status");
								e.printStackTrace();
							}
						}
//						dagExec = (DagExec) daoRegister.getRefObject(new MetaIdentifier(MetaType.dagExec, uuid, version));
						dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString(), "N");
						taskExec = dagExecServiceImpl.getTaskExec(dagExec, stageId, taskId);
						logger.info("Going to kill task : " + taskId + " : after getting taskExec");
						commonServiceImpl.setMetaStatusForTask(dagExec, taskExec, Status.Stage.KILLED, stageId, taskId);
					} catch (Exception e) {
						logger.error("Exception while setting TERMINATING/kill status");
						e.printStackTrace();
					}
				}
				return status;
			}
			logger.info("Before kill Task");
			try {
				futureTask = (FutureTask<String>) taskThreadMap.get("Task_" + uuid + "_" + taskId);
					logger.info("Going to kill task ###################### " + "Task_" + uuid + "_" + taskId);
					synchronized (uuid) {
						taskExec = (TaskExec) commonServiceImpl.setMetaStatusForTask(dagExec, taskExec, Status.Stage.TERMINATING, stageId, taskId);
					}
					if (futureTask != null && !futureTask.isDone()) {
					futureTask.cancel(true);
					
					logger.info("Kill Signal sent to Task Thread");
					status = "Kill Signal sent to Task Thread";
					while(! (futureTask == null || futureTask.isCancelled() || futureTask.isDone())) {
						logger.info("Sleeping thread Task : " + "Task_" + uuid + "_" + taskId);
						Thread.sleep(1000);
					}
					taskThreadMap.remove("Task_" + uuid + "_" + taskId);
					// Kill operators
					for(MetaIdentifierHolder operatorInfo : taskExec.getOperators().get(0).getOperatorInfo()) {
						commonServiceImpl.kill(operatorInfo.getRef().getType(), operatorInfo.getRef().getUuid(), operatorInfo.getRef().getVersion());
					}
				} else {
					logger.info("TaskThread is not alive. It might have COMPLETED its execution alReady");
					status = "TaskThread is not alive. It might have COMPLETED its execution alReady";
					taskThreadMap.remove("Task_" + uuid + "_" + taskId);
				}
					

					synchronized (uuid) {
					logger.info("Going to kill task : " + taskId);
//					dagExec = (DagExec) daoRegister.getRefObject(new MetaIdentifier(MetaType.dagExec, uuid, version));
					dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dagExec.toString(), "N");
					taskExec = dagExecServiceImpl.getTaskExec(dagExec, stageId, taskId);
					logger.info("Going to kill task : " + taskId + " : after getting taskExec");
					commonServiceImpl.setMetaStatusForTask(dagExec, taskExec, Status.Stage.KILLED, stageId, taskId);
				}
			} catch (Exception e) {
				logger.error("Task couldn't be KILLED : " + taskId);
				e.printStackTrace();
			}

			return status;
			// return strList;
		}

		public List<String> fetchAllTaskThread() {
			Set<String> strSet = taskThreadMap.keySet();
			return new ArrayList<String>(strSet);
		}

		public DagExec createDagExecBatch(Dag dag, DagExec dagExec, RunMode runMode) {
			logger.info("Inside createDagExecBatch");
			List<FutureTask> taskList = new ArrayList<FutureTask>();
			List<StageExec> depStageExecs = new ArrayList<>();
			if (dagExec == null) {
				logger.info("DAGExec is null. Aborting createDagExecBatch.");
				return null;
			}
			ExecParams execParams = dagExec.getExecParams();
			final DagExec dagExecFinal = dagExec;
			Status status = Helper.getLatestStatus(dagExec.getStatusList());
		
			if (status != null && status.equals(new Status(Status.Stage.PAUSE, new Date()))) {
				logger.info("DagExec is set to PAUSE status. So aborting. ");
				return dagExec;
			}

			try {
			List<StageExec> dagExecStgs = DagExecUtil.castToStageExecList(dagExec.getStages());
			do {
				for (int i=0; i<dagExecStgs.size(); i++) {
					StageExec stageExec = dagExecStgs.get(i);
					Stage stage = DagExecUtil.getStageFromDag(dag, stageExec.getStageId());
					status = Helper.getLatestStatus(stageExec.getStatusList());
					StageExec indvStg = stageExec;
					/*if (status != null && status.equals(new Status(Status.Stage.PAUSE, new Date()))) {
						logger.info("StageExec is set to PAUSE status. So continuing with next stage. ");
						continue;
					}
					if (indvStg != null && indvStg.getStatusList() != null && indvStg.getStatusList().contains(Status.Stage.Inactive)) {
						continue;	// If inactive stage then move to next stage (don't consider inactive stage)
					}*/
					if (!status.equals(new Status(Status.Stage.READY, new Date()))) {
						logger.info("StageExec is not set to READY status. So continuing with next stage. ");
						continue;
					}
	
						// Check if stage has any dependents
						if (stageExec.getDependsOn() != null 
								&& !stageExec.getDependsOn().isEmpty()) {
							depStageExecs.add(stageExec);	// Put in dependency list. Shall be handled in a separate loop
							continue;
						}
					
						// Set task and submit for execution
						setStageAndSubmit(dagExec, indvStg, stage, execParams, dag, taskList, runMode);
	
				}	// End for stageExec
			
				// For tasks with dependencies 
				boolean allDependenciesAddressed = false;
				if (depStageExecs == null || depStageExecs.isEmpty()) {
					allDependenciesAddressed = true;
				}
				while(!allDependenciesAddressed) {
					allDependenciesAddressed = true; // Set it to true so that any failure in checkDependencyStatus may set that to false
					try {
						logger.info("Tasks waiting for dependents ");
						Thread.sleep(10000); //  Should be parameterized in a class
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for  (StageExec stageExec : depStageExecs) {
						try {
						boolean checkDependencyStatus = false;
						boolean checkDependencyFAILED = false;
						boolean checkDependencyKILLED = false;
						String dependencyStatus = null;
						OrderKey datapodKey = null;
	
						Stage stage = DagExecUtil.getStageFromDag(dag, stageExec.getStageId());
						
						//Fetch from mongo instead of udta panchi.				
						com.inferyx.framework.domain.Status.Stage stageStatus = dagExecServiceImpl.getStageStatus(dagExec.getUuid(), dagExec.getVersion(), stageExec.getStageId());			
						if (stageStatus.equals(Status.Stage.RUNNING) 
								|| stageStatus.equals(Status.Stage.COMPLETED)
								|| stageStatus.equals(Status.Stage.PAUSE)) {
							continue;
						}
						
						// If not checkdependency status then continue after setting allDependenciesAddressed to false
						dependencyStatus = dagExecServiceImpl.checkStageDepStatus(dag,dagExec.getUuid(),dagExec.getVersion(),stage.getStageId());
						logger.info("Stage dependencyStatus : " + stageExec.getStageId() + " : " + dependencyStatus);
						if (StringUtils.isBlank(dependencyStatus) || dependencyStatus.equalsIgnoreCase(Status.Stage.PENDING.toString())) {
							checkDependencyStatus = false;
						} else if (dependencyStatus.equalsIgnoreCase(Status.Stage.KILLED.toString())) {
							checkDependencyKILLED = true;
							break;
						} else if (dependencyStatus.equalsIgnoreCase(Status.Stage.FAILED.toString())) {
							checkDependencyFAILED = true;
							break;
						} else {
							checkDependencyStatus = true;
						}
						if (!checkDependencyStatus) {
							allDependenciesAddressed = false;
							logger.info("Stage waiting for dependents : " + stageExec.getStageId());
							continue;
						}
					
						if (checkDependencyFAILED) {
							synchronized (dagExec.getUuid()) {
								commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.FAILED, stageExec.getStageId());
								continue;
							}
						}
						
						// Set task and submit for execution
						setStageAndSubmit(dagExec, stageExec, stage, execParams, dag, taskList, runMode);
						} catch (Exception e) {
							e.printStackTrace();
							synchronized (dagExec.getUuid()) {
								commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, Status.Stage.FAILED, stageExec.getStageId());
							}
						}
					}	// For all stageExec
				}	// End While 
				Thread.sleep(5000);
			} while (!waitAndComplete(dagExecFinal, taskList));
			} catch (Exception e) {
				e.printStackTrace();
			}

		
			String outputThreadName = null;
			for (FutureTask<String> futureTask : taskList) {
			    try {
			    	outputThreadName = futureTask.get();
			        logger.info("Thread " + outputThreadName + " COMPLETED ");
			        taskThreadMap.remove(outputThreadName);
			    } catch (InterruptedException e) {
			        e.printStackTrace();
			    } catch (ExecutionException e) {
			        e.printStackTrace();
			    }
			}			
			return dagExec;
		}
		
		/**
		 * 
		 * @param dagExec
		 * @param taskList
		 * @return
		 * @throws Exception
		 */
		public Boolean waitAndComplete (DagExec dagExec, @SuppressWarnings("rawtypes") List<FutureTask> taskList) throws Exception {
			logger.info(" Inside waitAndComplete for Dag ");
			String outputThreadName = null;
			String dagExecUUID = dagExec.getUuid();
			String dagExecVer = dagExec.getVersion();
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
					dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(dagExecUUID, dagExecVer, MetaType.dagExec.toString());
					Status.Stage latestStatus = Helper.getLatestStatus(dagExec.getStatusList()).getStage();
					RunStatusHolder statusHolder = new RunStatusHolder(Boolean.TRUE, 
																		Boolean.FALSE, 
																		Boolean.FALSE, 
																		Boolean.FALSE, 
																		Boolean.FALSE);
					// Update run status for Dag
					Helper.updateRunStatus(latestStatus, statusHolder);
					logger.info(" StatusHolder of dagExec : " + statusHolder.getCOMPLETED() + ":" + statusHolder.getKILLED() + ":" + statusHolder.getFAILED() 
								+ ":" + statusHolder.getPAUSE() + ":" + statusHolder.getRESUME());
					if (statusHolder.getCOMPLETED() && statusHolder.getKILLED()) {
						synchronized (dagExecUUID) {
							commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.KILLED);
						}
						return true;
					} else if (statusHolder.getCOMPLETED() && statusHolder.getFAILED()) {
						synchronized (dagExecUUID) {
							commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.FAILED);
						}
						return true;
					} else if (statusHolder.getCOMPLETED()) {
						synchronized (dagExecUUID) {
							commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.COMPLETED);
						}
						return true;
					} 
					
					// Check status of all metas and then decide on the group status  
					statusHolder = new RunStatusHolder(Boolean.TRUE, 
							Boolean.FALSE, 
							Boolean.FALSE, 
							Boolean.FALSE, 
							Boolean.FALSE);
					
					for (StageExec stageExec : DagExecUtil.castToStageExecList(dagExec.getStages())) {
						latestStatus = Helper.getLatestStatus(stageExec.getStatusList()).getStage();
						Helper.updateRunStatus(latestStatus, statusHolder);
					}// End for
					
					logger.info(" StatusHolder of stageExec : " + statusHolder.getCOMPLETED() + ":" + statusHolder.getKILLED() + ":" + statusHolder.getFAILED() 
					+ ":" + statusHolder.getPAUSE() + ":" + statusHolder.getRESUME());
					
					if (statusHolder.getCOMPLETED() && statusHolder.getKILLED()) {
						synchronized (dagExecUUID) {
							commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.KILLED);
						}
						return true;
					} else if (statusHolder.getCOMPLETED() && statusHolder.getFAILED()) {
						synchronized (dagExecUUID) {
							commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.FAILED);
						}
						return true;
					} else if (statusHolder.getCOMPLETED()) {
						synchronized (dagExecUUID) {
							commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.COMPLETED);
						}
						return true;
					} 
			} catch (Exception e) {
				synchronized (dagExecUUID) {
					commonServiceImpl.setMetaStatus(dagExec, MetaType.dagExec, Status.Stage.FAILED);
				}
				e.printStackTrace();
				return true;
//				throw new RuntimeException();
			}
				return false;
		}	// End waitAndComplete

	
	
		public void setStageAndSubmit(DagExec dagExec, StageExec indvStg, Stage stage, ExecParams execParams, Dag dag, List<FutureTask> taskList, RunMode runMode) {
			RunStageServiceImpl indivStageExe = new RunStageServiceImpl();
			logger.info("Map name of uuid_version:" + dagExec.getUuid() + "_"
					+ indvStg.getStageId());
			indivStageExe.setName("Stage_"+dagExec.getUuid()+"_"+stage.getStageId());
			indivStageExe.setDependsOn(stage.getDependsOn());
			indivStageExe.setDagExecServiceImpl(dagExecServiceImpl);
			indivStageExe.setDagExecUUID(dagExec.getUuid());
			indivStageExe.setStageId(indvStg.getStageId());
			indivStageExe.setDagExecVer(dagExec.getVersion());
//			indivStageExe.setDaoRegister(daoRegister);
			indivStageExe.setDatasourceFactory(datasourceFactory);
			indivStageExe.setDataqualServiceImpl(dataqualServiceImpl);
			indivStageExe.setDataqualGroupServiceImpl(dataqualGroupServiceImpl);
			indivStageExe.setDataqualExecServiceImpl(dataqualExecServiceImpl);
			indivStageExe.setDataqualGroupExecServiceImpl(dataqualGroupExecServiceImpl);
			indivStageExe.setRuleServiceImpl(ruleServiceImpl);
			indivStageExe.setRuleExecServiceImpl(ruleExecServiceImpl);
			indivStageExe.setRuleGroupServiceImpl(ruleGroupServiceImpl);
			indivStageExe.setRuleGroupExecServiceImpl(ruleGroupExecServiceImpl);
		
			indivStageExe.setProfileServiceImpl(profileServiceImpl);
			indivStageExe.setProfileGroupServiceImpl(profileGroupServiceImpl);
			indivStageExe.setProfileExecServiceImpl(profileExecServiceImpl);
			indivStageExe.setProfileGroupExecServiceImpl(profileGroupExecServiceImpl);
			indivStageExe.setModelServiceImpl(modelServiceImpl);
			indivStageExe.setModelExecServiceImpl(modelExecServiceImpl);
			indivStageExe.setRunMode(runMode);
		
			DataStore datastore = new DataStore();
			datastore.setCreatedBy(dagExec.getCreatedBy());
			indivStageExe.setDataStore(datastore);
		
			indivStageExe.setiDataStore(dataStoreServiceImpl);
			indivStageExe.setDatapodServiceImpl(datapodServiceImpl);
			indivStageExe.setOperatorServiceImpl(operatorServiceImpl);
			indivStageExe.setLoadServiceImpl(loadServiceImpl);
			indivStageExe.setMapServiceImpl(mapServiceImpl);
			indivStageExe.setDagServiceImpl(dagServiceImpl);
			indivStageExe.setHdfsInfo(hdfsInfo);
			indivStageExe.setCommonServiceImpl(commonServiceImpl);
			indivStageExe.setExecParams(execParams);
			indivStageExe.setDag(dag);
			indivStageExe.setDataStoreServiceImpl(dataStoreServiceImpl);
			indivStageExe.setTaskExecutor(taskExecutor);
			indivStageExe.setStageExec(indvStg);
			indivStageExe.setTaskThreadMap(taskThreadMap);
			indivStageExe.setDqInfo(dqInfo);
//			indivStageExe.setDaoRegister(daoRegister);
			indivStageExe.setDagExec(dagExec);
			indivStageExe.setStage(stage);
			indivStageExe.setParamSetServiceImpl(paramSetServiceImpl);
			indivStageExe.setSessionContext(FrameworkThreadLocal.getSessionContext().get());
			indivStageExe.setReconServiceImpl(reconServiceImpl);
			indivStageExe.setReconGroupServiceImpl(reconGroupServiceImpl);
			indivStageExe.setProfileInfo(profileInfo);
			indivStageExe.setReconInfo(reconInfo);
			indivStageExe.setIngestServiceImpl(ingestServiceImpl);
			indivStageExe.setIngestExecServiceImpl(ingestExecServiceImpl);
			indivStageExe.setIngestGroupServiceImpl(ingestGroupServiceImpl);
			indivStageExe.setReportServiceImpl(reportServiceImpl);
			indivStageExe.setDashboardServiceImpl(dashboardServiceImpl);
			FutureTask<String> futureTask = new FutureTask<String>(indivStageExe);
			stageExecutor.execute(futureTask);
			logger.info("Thread watch : DagExec : " + dagExec.getUuid() + " StageExec : " + indvStg.getStageId() + " started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
			taskList.add(futureTask);
			taskThreadMap.put("Stage_" + dagExec.getUuid() + "_" + stage.getStageId(), futureTask);
		}

	}

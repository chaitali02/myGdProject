/**
 * 
 */
package com.inferyx.framework.runner2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Stage;
import com.inferyx.framework.domain.StageExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Task;
import com.inferyx.framework.domain.TaskExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DagExecServiceImpl;
import com.inferyx.framework.service.DagServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;

/**
 * @author joy
 *
 */
public class AsyncStageRunner {
	
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private DagExecServiceImpl dagExecServiceImpl;
	@SuppressWarnings("rawtypes")
	ConcurrentHashMap taskThreadMap;
	Stage stage;
	private SessionContext sessionContext;

	static final Logger logger = Logger.getLogger(AsyncStageRunner.class);

	/**
	 * 
	 */
	public AsyncStageRunner() {
		// TODO Auto-generated constructor stub
	}
	
	@Async("stageExecutor")
	public CompletableFuture<String> execute(Dag dag, DagExec dagExec, StageExec stageExec, ExecParams execParams, RunMode runMode) throws InterruptedException, JsonProcessingException {
		CompletableFuture<String> completableFuture = new CompletableFuture<>(); 
		String dagExecUUID = dagExec.getUuid();
		String stageId = stageExec.getStageId();
		logger.info("Thread watch : DagExec : " + dagExec.getUuid() + " StageExec : " + stageId + " status RUN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
		FrameworkThreadLocal.getSessionContext().set(sessionContext);
		
		List<TaskExec> dagTaskExecs = DagExecUtil.castToTaskExecList(stageExec.getTasks());
		List<FutureTask> taskList = new ArrayList<FutureTask>();
//		Status.Stage stageStatus = Helper.getLatestStatus(stageStatusList).getStage();
		List<TaskExec> depTaskExecs = new ArrayList<>();
		
		try {
			logger.info("Setting status to RUNNING for stage : " + stageExec.getStageId());
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
	
				//Fetch from mongo instead of udta panchi.
				com.inferyx.framework.domain.Status.Stage taskStatus = dagExecServiceImpl.getTaskStatus(dagExec.getUuid(), dagExec.getVersion(), stageId, indvTaskExec.getTaskId());			
				
				if (taskStatus.equals(Status.Stage.COMPLETED)
						|| taskStatus.equals(Status.Stage.TERMINATING)
						|| taskStatus.equals(Status.Stage.KILLED) 
						|| taskStatus.equals(Status.Stage.PAUSE)
						|| taskStatus.equals(Status.Stage.FAILED)) {
					continue;	//  Go to next task
				}
				
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
								|| operationInfoHolder.getRef().getType().equals(MetaType.operator))) {
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
					String dependencyStatus = null;
					OrderKey datapodKey = null;
					
					Task indvTask = DagExecUtil.getTaskFromStage(stage, indvTaskExec.getTaskId());
					
					//Fetch from mongo instead of udta panchi.				
					com.inferyx.framework.domain.Status.Stage taskStatus = dagExecServiceImpl.getTaskStatus(dagExec.getUuid(), dagExec.getVersion(), stageId, indvTaskExec.getTaskId());			
					if (!taskStatus.equals(Status.Stage.PENDING)
							&& !taskStatus.equals(Status.Stage.RUNNING)
							&& !taskStatus.equals(Status.Stage.RESUME)) {
						continue;
					}
					
					// If not checkdependency status then continue after setting allDependenciesAddressed to false
					dependencyStatus = dagExecServiceImpl.checkTaskDepStatus(dag,dagExec.getUuid(),dagExec.getVersion(),stageId,indvTaskExec.getTaskId());
					logger.info("Task dependencyStatus : " + indvTaskExec.getTaskId() + " : " + dependencyStatus);
					if (StringUtils.isBlank(dependencyStatus) || dependencyStatus.equalsIgnoreCase(Status.Stage.PENDING.toString())) {
						checkDependencyStatus = false;
					} else if (dependencyStatus.equalsIgnoreCase(Status.Stage.KILLED.toString())) {
						checkDependencyKILLED = true;
						checkDependencyStatus = true;
						break;
					} else if (dependencyStatus.equalsIgnoreCase(Status.Stage.FAILED.toString())) {
						checkDependencyFAILED = true;
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
					logger.info(" checkDependencyStatus : checkDependencyKILLED : checkDependencyFAILED : checkDependencyPAUSE : checkDependencyRESUME : " 
								+ checkDependencyStatus + ":" + checkDependencyKILLED + ":" + checkDependencyFAILED + ":" + checkDependencyPAUSE + ":" + checkDependencyRESUME);
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
									|| operationInfoHolder.getRef().getType().equals(MetaType.operator))) {
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
		
		logger.info("Thread watch : DagExec : " + dagExec.getUuid() + " StageExec : " + stageExec.getStageId() + " complete >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
		completableFuture.complete(stageExec.getName()) ;
		return completableFuture;
	}

	public OrderKey fetchDatapodKey(Task task) {
		return null;
	}
	
	public void setTaskAndSubmit(TaskExec indvTaskExec, OrderKey datapodKey, Task indvTask, List<MetaIdentifierHolder> operationInfoHolderList, List<FutureTask> taskList, RunMode runMode) {
		
	}
	
	public Boolean waitAndComplete(List<FutureTask> taskList) {
		return Boolean.FALSE;
	}
}

/**
 * 
 */
package com.inferyx.framework.service2;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.StageExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.TaskExec;
import com.inferyx.framework.domain2.Events;
import com.inferyx.framework.domain2.Stages;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class RunStateServiceImpl {
	
	@Autowired
	StateMachineBuilderServiceImpl stateMachineBuilder;
	@Autowired
	CommonServiceImpl commonServiceImpl;
	@Resource(name="stateMachineMap")
	ConcurrentHashMap<String, StateMachine<Stages, Events>> stateMachineMap;

	/**
	 * 
	 */
	public RunStateServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param reference
	 * @return
	 */
	public StateMachine<Stages, Events> createStateMachine(String reference) {
		StateMachine<Stages, Events> sm = null;
		try {
			sm = stateMachineBuilder.build();
			stateMachineMap.put(reference, sm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sm;
	}
	
	/**
	 * 
	 * @param reference
	 */
	public void removeStateMachine(String reference) {
		if (stateMachineMap.containsKey(reference)) {
			stateMachineMap.remove(reference);
		}
	}
	
	/**
	 * 
	 * @param reference
	 * @param baseEntity
	 * @param type
	 * @param exec
	 * @param status
	 * @throws Exception
	 */
	public void setStatus(BaseEntity baseEntity, MetaType type, Object exec, Status.Stage status) throws Exception {
		BaseExec baseExec = null;
		DagExec dagExec = null;
		if (exec != null) {
			dagExec = (DagExec) commonServiceImpl.getOneByUuidAndVersion(baseEntity.getUuid(), baseEntity.getVersion(), type.toString());
			if (exec instanceof StageExec) {
				StageExec stageExec = (StageExec)exec;
				commonServiceImpl.setMetaStatusForStage(dagExec, stageExec, status, stageExec.getStageId());
				return;
			} else if (exec instanceof TaskExec) {
				TaskExec taskExec = (TaskExec)exec;
				String stageId = DagExecUtil.getStageExecFromDagExecTask(dagExec, taskExec.getTaskId()).getStageId();
				commonServiceImpl.setMetaStatusForTask(dagExec, taskExec, status, stageId, taskExec.getTaskId());
				return;
			}
		}
		baseExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(baseEntity.getUuid(), baseEntity.getVersion(), type.toString());
		commonServiceImpl.setMetaStatus(baseExec, type, status);
	}
	
	/**
	 * 
	 * @param reference - a reference to the stateMachine - possibly uuid_version
	 * @param baseEntity - Either the baseExec or dagExec for Dag
	 * @param type - exec type
	 * @param exec - stageExec or taskExec for dag, null for rest of baseExecs
	 * @throws Exception
	 */
	public void progress(String reference, BaseEntity baseEntity, MetaType type, Object exec) throws Exception {
		if (!stateMachineMap.containsKey(reference)) {
			throw new Exception("State Machine missing");
		}
		StateMachine<Stages, Events> stateMachine = stateMachineMap.get(reference);
		stateMachine.sendEvent(Events.PROGRESS);
		if (stateMachine.getState().getId().equals(Stages.InProgress)) {
			setStatus(baseEntity, type, exec, Status.Stage.InProgress);
		} else if (exec == null){
			throw new IllegalStateException(String.format("Invalid status In Progress at this state!! for %s ", baseEntity.getName()));
		} else if (exec instanceof StageExec) {
			throw new IllegalStateException(String.format("Invalid status In Progress at this state!! for %s ", ((StageExec)exec).getName()));
		} else if (exec instanceof TaskExec) {
			throw new IllegalStateException(String.format("Invalid status In Progress at this state!! for %s ", ((TaskExec)exec).getName()));
		}
	}

}

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
package com.inferyx.framework.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.ExecutionContext;
import com.inferyx.framework.domain.Key;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Stage;
import com.inferyx.framework.domain.StageExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Task;
import com.inferyx.framework.domain.TaskExec;

public class DagExecUtil {
	static Logger logger=Logger.getLogger(DagExecUtil.class);
	
	public static MetaIdentifier createRefFromKey(Key key, MetaType type){
		return new MetaIdentifier(type, key.getUUID(), key.getVersion(), key.getName());
	}
	
	public static List<Status> createInitialStatus(List<Status> statusList){
		if (statusList == null) {
			statusList = new ArrayList<Status>();
		}
		Status status = new Status(Status.Stage.PENDING, new Date());
		statusList.add(status);
		return statusList;
	}
	
	public static List<Status> createInitialInactiveStatus(List<Status> statusList){
		if (statusList == null) {
			statusList = new ArrayList<Status>();
		}
		Status status = new Status(Status.Stage.Inactive, new Date());
		statusList.add(status);
		return statusList;
	}
	
	public static Stage getStageFromDag(Dag dag, String stageId){
		/*if (!NumberUtils.isDigits(stageId)){
			logger.error("StageId : " + stageId + " is not a valid stageId");
			return null;
		}*/
		for (Stage stage : dag.getStages()) {
			if (StringUtils.isNotBlank(stage.getStageId()) 
					&& stage.getStageId().equalsIgnoreCase(stageId)) {
				return stage;
			}
		}
		return null;
	}
	
	public static StageExec getStageExecFromDagExec(DagExec dagExec, String stageId){
		List<StageExec> stageExecList = DagExecUtil.castToStageExecList(dagExec.getStages());
		if (stageExecList == null || stageExecList.isEmpty()) {
			return null;
		}
		for (StageExec stageExec : stageExecList) {
			if (stageExec == null) {
				continue;
			}
			if (stageExec.getStageId().equals(stageId)) {
				return stageExec;
			}
		}
		return null;
	}
	
	public static Task getTaskFromStage(Stage stage, String taskId){
		/*if (!NumberUtils.isDigits(taskId)){
			logger.error("TaskId : " + taskId + " is not a valid taskId");
			return null;
		}*/
		for (Task task : stage.getTasks()) {
			if (StringUtils.isNotBlank(task.getTaskId()) 
					&& task.getTaskId().equalsIgnoreCase(taskId)) {
				return task;
			}
		}
		return null;
	}
	
	public static TaskExec getTaskExecFromStageExec(StageExec stageExec, String taskId){
		List<TaskExec> taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
		if (taskExecList == null || taskExecList.isEmpty()) {
			return null;
		}
		for (TaskExec taskExec : taskExecList) {
			if (taskExec == null) {
				continue;
			}
			if (taskExec.getTaskId().equals(taskId)) {
				return taskExec;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param dagExec
	 * @param taskId
	 * @return
	 */
	public static TaskExec getTaskExecFromDagExec(DagExec dagExec, String taskId){
		List<TaskExec> taskExecList = null;
		List<StageExec> stageExecList = null;
		stageExecList = DagExecUtil.castToStageExecList(dagExec.getStages());
		for (StageExec stageExec : stageExecList) {
			taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
			if (taskExecList == null || taskExecList.isEmpty()) {
				continue;
			}
			for (TaskExec taskExec : taskExecList) {
				if (taskExec == null) {
					continue;
				}
				if (taskExec.getTaskId().equals(taskId)) {
					return taskExec;
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param dagExec
	 * @param taskId
	 * @return
	 */
	public static StageExec getStageExecFromDagExecTask(DagExec dagExec, String taskId){
		List<TaskExec> taskExecList = null;
		List<StageExec> stageExecList = null;
		stageExecList = DagExecUtil.castToStageExecList(dagExec.getStages());
		for (StageExec stageExec : stageExecList) {
			taskExecList = DagExecUtil.castToTaskExecList(stageExec.getTasks());
			if (taskExecList == null || taskExecList.isEmpty()) {
				continue;
			}
			for (TaskExec taskExec : taskExecList) {
				if (taskExec == null) {
					continue;
				}
				if (taskExec.getTaskId().equals(taskId)) {
					return stageExec;
				}
			}
		}
		return null;
	}
	
	/**
	 * Convert refKeyList to refKeyMap
	 * @param refKeyList
	 * @return
	 */
	public static Map<String, MetaIdentifier> convertRefKeyListToMap (List<MetaIdentifier> refKeyList) {
		Map<String, MetaIdentifier> refKeyMap = new HashMap<>();
		if (refKeyList == null || refKeyList.isEmpty()) {
			return refKeyMap;
		}
		for (MetaIdentifier refKey : refKeyList) {
			refKeyMap.put(refKey.getType()+"_"+refKey.getUuid(), refKey);
		}
		return refKeyMap;
	}
	
	
	/**
	 * Convert refKeyMap to refKeyList
	 * @param refKeyMap
	 * @return
	 */
	public static List<MetaIdentifier> convertRefKeyMapToList (Map<String, MetaIdentifier> refKeyMap) {
		List<MetaIdentifier> refKeyList = new ArrayList<>();
		if (refKeyMap == null || refKeyMap.isEmpty()) {
			return refKeyList;
		}
		for (String refKey : refKeyMap.keySet()) {
			refKeyList.add(refKeyMap.get(refKey));
		}
		return refKeyList;
	}
	
	public static List<StageExec> castToStageExecList(List<Stage> stages) {
		if (stages == null) {
			return null;
		}
		List<StageExec> stageExecList = new ArrayList<>();
		for (Stage stage : stages) {
			stageExecList.add(convertToStageExec(stage));
		}
		return stageExecList;
	}
	
	public static StageExec convertToStageExec(Stage stage) {
		return new StageExec(stage);
	}
	
	public static TaskExec convertToTaskExec(Task task) {
		return new TaskExec(task);
	}
	
	public static List<Stage> convertToStageList(List<StageExec> stageExecs) {
		if (stageExecs == null) {
			return null;
		}
		List<Stage> stageList = new ArrayList<>();
		for (StageExec stageExec : stageExecs) {
			stageList.add(stageExec);
		}
		return stageList;
	}
	
	public static Stage convertToStage(StageExec stageExec) {
		if (stageExec == null) {
			return null;
		}
		return stageExec;
	}
	
	public static void replaceInStageList(List<Stage> stageList, Stage stage) {
		if (stage == null || stageList == null || stageList.isEmpty()) {
			return;
		}
		
		for (int index = 0; index < stageList.size(); index++) {
			Stage stager = stageList.get(index);
			if (stager.getStageId().equals(stage.getStageId())) {
				stageList.remove(index);
				stageList.add(index, stage);
				return;
			}
		}
	}
	
	public static List<TaskExec> castToTaskExecList(List<Task> tasks) {
		if (tasks == null) {
			return null;
		}
		List<TaskExec> taskExecList = new ArrayList<>();
		for (Task task : tasks) {
			taskExecList.add(convertToTaskExec(task));
		}
		return taskExecList;
	}
	
	public static List<Task> convertToTaskList(List<TaskExec> taskExecs) {
		if (taskExecs == null) {
			return null;
		}
		List<Task> taskList = new ArrayList<>();
		for (TaskExec taskExec : taskExecs) {
			taskList.add(taskExec);
		}
		return taskList;
	}
	
	/**
	 *  refKeyList - create new. Add back to execParam's refKeyList after completion
	 *	paramInfo - not required
	 *	filterInfo - not required
	 *	stageInfo - not required
	 *	paramSetHolder  - create new and copy all. Don't need to add back
	 *	paramListInfo - not required
	 *	internalVarMap - not required during parse. Required during execute
	 *	otherParams - append to it
	 *	executionContext - create new and copy all. Don't need to add back for now
	 * @param execParams
	 * @return
	 */
	public static ExecParams cloneTaskExecParams (ExecParams execParams) {
		ExecParams taskExecParams = new ExecParams();
		
		taskExecParams.setRefKeyList(new ArrayList<>());
		taskExecParams.setCurrParamSet(execParams.getCurrParamSet());
		taskExecParams.setParamListHolder(execParams.getParamListHolder());
		taskExecParams.setExecutionContext(new ExecutionContext());
		taskExecParams.setOtherParams(execParams.getOtherParams());
		return taskExecParams;
	}
	
	/**
	 * 
	 * @param execParams
	 * @param sql
	 * @return
	 */
	public static String replaceInternalVarMap(ExecParams execParams, String sql) {
		if (execParams != null 
				&& execParams.getInternalVarMap() != null 
				&& !execParams.getInternalVarMap().isEmpty() 
				&& StringUtils.isNotBlank(sql)) {
			for (String key : execParams.getInternalVarMap().keySet()) {
				sql = sql.replaceAll(key, execParams.getInternalVarMap().get(key));
			}
		}
		return sql;
	}
	
	/*public static fetchRefKeyList (TaskExec taskExec) {
		
	}*/
	
}

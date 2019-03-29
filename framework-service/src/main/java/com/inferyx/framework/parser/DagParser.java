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
package com.inferyx.framework.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.AttributeMap;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Stage;
import com.inferyx.framework.domain.StageExec;
import com.inferyx.framework.domain.Task;
import com.inferyx.framework.domain.TaskExec;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DagExecServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;

public class DagParser {
    Logger logger =Logger.getLogger(DagParser.class);
	@Autowired
	DagExecServiceImpl dagExecServiceImpl;
//	@Autowired
//	private MetadataUtil daoRegister;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	
	public DagExec createDAGExec(Dag dag, ExecParams execParams) throws JsonProcessingException {
		
		// Access each object to initialize their latest version

		DagExec dagExec = new DagExec(dag);
		MetaIdentifier dagRef = new MetaIdentifier(MetaType.dag, dag.getUuid(), dag.getVersion());
		dagExec.setDependsOn(dagRef);
			// Loop in Stages
		List<Stage> dagStages = dag.getStages();
		List<String> dependsOn = dagStages.get(0).getDependsOn();		
		List<StageExec> dagExecStages = createDagExecStages(dagStages, dependsOn, dagRef, execParams);
		dagExec.setStages(DagExecUtil.convertToStageList(dagExecStages));
		dagExec.setBaseEntity();
		dagExec.setName("sys_" + dagExec.getUuid());

		// Set DagExec Status
		dagExec.setStatusList(DagExecUtil.createInitialStatus(dagExec.getStatusList()));

		return dagExec;
	}

	/*
	 * In this method we are getting the dag stages and Making a copy of each
	 * stage for dagexec stage Creating a list of stages and then returning to
	 * called dagexec The task list for each stage is prepared by a calling
	 * function
	 */
	private List<StageExec> createDagExecStages(List<Stage> dagStages,List<String> dependson ,MetaIdentifier dagRef, ExecParams execParams) throws JsonProcessingException {
		List<StageExec> stageExecs = new ArrayList<>();
		List<String> activeStages = null;
		if (execParams != null) {
			activeStages = execParams.getStageInfo();
		}
		for (Stage indvStg : dagStages) {
			// Set common attr for stage
			StageExec stageExec = new StageExec(indvStg);
			//StageRef stageRef = new StageRef(dagRef, indvStg.getDependsOn(), indvStg.getStageId(), null);
			//stageExec.setStage(stageRef);
			
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
			List<TaskExec> dagExectasks = createDagExecTasks(dagTasks, indvStg.getDependsOn(),indvStg, dagRef, execParams);
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
	public static MetaIdentifier populateRefKeys(java.util.Map<String, 
								MetaIdentifier> refKeyMap, 
								MetaIdentifier ref, 
								java.util.Map<String, MetaIdentifier> inputRefKeyMap) {
		if (ref == null) {
			return null;
		}
		if (refKeyMap == null) {
			refKeyMap = new HashMap<>();
		}
		if (inputRefKeyMap != null 
				&& inputRefKeyMap.containsKey(ref.getUuid()) 
				&& inputRefKeyMap.get(ref.getUuid()).getVersion() != null) {
			refKeyMap.put(ref.getType() + "_" + ref.getUuid(), inputRefKeyMap.get(ref.getUuid()));
			return inputRefKeyMap.get(ref.getUuid());
		} else if(refKeyMap.containsKey(ref.getType() + "_" + ref.getUuid()) 
				&&  refKeyMap.get(ref.getType() + "_" + ref.getUuid()).getVersion() != null) {
			return refKeyMap.get(ref.getType() + "_" + ref.getUuid());
		} // else
		refKeyMap.put(ref.getType() + "_" + ref.getUuid(), ref);
		return ref;
		
	}

	@SuppressWarnings("unused")
	private List<TaskExec> createDagExecTasks(List<Task> dagTasks,List<String> dependsOn, Stage indvStg, MetaIdentifier dagRef, ExecParams execParams) throws JsonProcessingException {
		List<TaskExec> taskExecs = new ArrayList<>();
		java.util.Map<String, MetaIdentifier> refKeys = null;
		MetaIdentifier mapRef = null, sourceRef = null, targetRef = null, sourceAttrRef = null, condRef = null,
				targetAttrRef = null, miHolderRef = null, groupByRef = null, loadRef = null, secondaryDagRef = null;
		
		//ObjectMapper mapper = new ObjectMapper();
		// Convert refKeyList to HashMap
		List<MetaIdentifier> inputRefKeyList = null;
		if (execParams != null) {
			inputRefKeyList = execParams.getRefKeyList();
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
			refKeys = DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()); // Get refKeys placeholder for
												// refKey preparation
			// Populate taskRef
			/*TaskRef taskRef = new TaskRef(dagRef,indvTask.getDependsOn(), indvTask.getTaskId(), indvStg.getStageId(), indvTask.getName());
			taskExec.setTask(taskRef);*/

			// Set Tasks status to PENDING
			taskExec.setStatusList(DagExecUtil.createInitialStatus(taskExec.getStatusList()));

			// Below code need reformatting, the setting should be done
			// while doing getter action

			// Traverse task & Populate RefKeys
			if (indvTask.getOperators() != null && indvTask.getOperators().get(0).getOperatorInfo() != null
					&& (indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef().getType().equals(MetaType.map) 
						|| indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef().getType().equals(MetaType.mapiter))) {

				mapRef = indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef();
//				Map map = (Map) daoRegister.getRefObject(populateRefKeys(refKeys, mapRef, inputRefKeys));
				populateRefKeys(refKeys, mapRef, inputRefKeys);
				Map map = (Map) commonServiceImpl.getOneByUuidAndVersion(mapRef.getUuid(), mapRef.getVersion(), mapRef.getType().toString(), "N");
				
				//populateRefKeys(refKeys, mapRef, inputRefKeys);

				// Setting the Version for Map Object
				sourceRef = map.getSource().getRef();
				targetRef = map.getTarget().getRef();
//				daoRegister.getRefObject(populateRefKeys(refKeys, sourceRef, inputRefKeys));
//				daoRegister.getRefObject(populateRefKeys(refKeys, targetRef, inputRefKeys));
				populateRefKeys(refKeys, sourceRef, inputRefKeys);
				populateRefKeys(refKeys, targetRef, inputRefKeys);
				// Insert into refKeys
				/*populateRefKeys(refKeys, sourceRef, inputRefKeys);
				populateRefKeys(refKeys, targetRef, inputRefKeys);*/

				for (AttributeMap attrMap : map.getAttributeMap()) {
					targetAttrRef = attrMap.getTargetAttr().getRef();
//					daoRegister.getRefObject(populateRefKeys(refKeys, targetAttrRef, inputRefKeys));
					//populateRefKeys(refKeys, targetAttrRef, inputRefKeys);
					sourceAttrRef = attrMap.getSourceAttr().getRef();
//					daoRegister.getRefObject(populateRefKeys(refKeys, sourceAttrRef, inputRefKeys));
					//populateRefKeys(refKeys, sourceAttrRef, inputRefKeys);
					
					populateRefKeys(refKeys, targetAttrRef, inputRefKeys);
					populateRefKeys(refKeys, sourceAttrRef, inputRefKeys);
				}

				// Setting the Version for Filter Object
				/*for (MetaIdentifierHolder miHolder : indvTask.getOperators().get(0).getFilterInfo()) {
					miHolderRef = miHolder.getRef();
					daoRegister.getRefObject(populateRefKeys(refKeys, miHolderRef, inputRefKeys));
					//populateRefKeys(refKeys, miHolderRef, inputRefKeys);
				}

				if (map.getGroupBy() != null) {
					List<AttributeRefHolder> groupList = map.getGroupBy();
					for(int i=0; i<groupList.size(); i++)
					{
						groupByRef = groupList.get(i).getRef();
					}					
					daoRegister.getRefObject(populateRefKeys(refKeys, groupByRef, inputRefKeys));
					//populateRefKeys(refKeys, groupByRef, inputRefKeys);
				}*/
				// Setting the Version for Filter Object
				/*for (MetaIdentifierHolder miHolder : indvTask.getOperators().get(0).getFilterInfo()) {
					miHolderRef = miHolder.getRef();
					daoRegister.getRefObject(populateRefKeys(refKeys, miHolderRef, inputRefKeys));
					//populateRefKeys(refKeys, miHolderRef, inputRefKeys);
				}*/

			} else if (indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef().getType().equals(MetaType.load)) {// MetaType load
				loadRef = indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef();
			//	Load load = (Load) daoRegister.getRefObject(populateRefKeys(refKeys, loadRef, inputRefKeys));
				//populateRefKeys(refKeys, loadRef, inputRefKeys);
			} else if (indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef().getType().equals(MetaType.dag)) {// MetaType dag
				secondaryDagRef = indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef();
				populateRefKeys(refKeys, secondaryDagRef, inputRefKeys);	// PopuPopulatelate refKeys
			}
			//taskExec.setRefKeyList(DagExecUtil.convertRefKeyMapToList(refKeys));
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
	

	public OrderKey getLatestMetaKey(Set<OrderKey> keySet, OrderKey key) {
		if (key.getVersion() != null) {
			logger.info(String.format("getLatestMetaKey: existing %s  %s", key.getUUID(), key.getVersion()));
			return key;
		}
		OrderKey latestKey = Helper.getFirstKey(keySet, key.getUUID());
		logger.info(String.format("getLatestMetaKey: latest key %s  %s", key.getUUID(), key.getVersion()));
		return latestKey;
	}

	
}

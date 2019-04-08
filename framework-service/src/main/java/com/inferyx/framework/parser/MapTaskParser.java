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
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.RelationInfo;
import com.inferyx.framework.domain.Stage;
import com.inferyx.framework.domain.Task;
import com.inferyx.framework.domain.TaskExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.operator.MapIterOperator;
import com.inferyx.framework.operator.MapOperator;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;

@Component
public class MapTaskParser extends TaskParser {
	@Autowired
	protected MapOperator mapOperator;
	@Autowired
	protected MapIterOperator mapIterOperator;
	@Autowired
	protected DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	
	private final String WHERE_1_1 = " WHERE (1=1) ";//" WHERE \\(1=1\\) ";
	
	private final String $DAGEXEC_VERSION = "$DAGEXEC_VERSION";
	static final Logger logger = Logger.getLogger(MapTaskParser.class);

	@Override
	public StringBuilder parseTask(DagExec dagExec, Stage stage, TaskExec indvExecTask, List<String> datapodList,
			ExecParams execParams, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
		java.util.Map<String, String> paramValMap = new HashMap<String, String>();
		java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList());
		Task indvTask = DagExecUtil.getTaskFromStage(stage, indvExecTask.getTaskId());
		MetaIdentifier ref = indvTask.getOperators().get(0).getOperatorInfo().get(0).getRef();
		String operatorType = indvTask.getOperators().get(0).getOperatorType();
		HashMap<String, Object> operatorParams = indvTask.getOperators().get(0).getOperatorParams();
		ref = populateRefVersion(ref, refKeyMap);
		
		// Get Datastore info from execParams and populate in otherParams for tablename determination - START
		/*if (execParams != null 
				&& execParams.getDataStoreList() != null 
				&& execParams.getDataStoreList().size() > 0) {
			ObjectMapper mapper = new ObjectMapper();
			List<MetaIdentifier> dataStoreList = execParams.getDataStoreList();
			
				for (MetaIdentifier dataStoreRef : dataStoreList) {
					// Get datastore
					DataStore dataStore = dataStoreServiceImpl.findOneByUuidAndVersion(dataStoreRef.getUuid(), dataStoreRef.getVersion());
					// Get datapod
					String datapodUuid = dataStore.getMetaId().getRef().getUuid();
					String datapodVersion = dataStore.getMetaId().getRef().getVersion();
					String dagExecVersion = dataStore.getExecId().getRef().getVersion();
					String key = "datapodUuid_"+datapodUuid+"_tableName";
					String value = "datapodVersion_"+datapodVersion+"_dagExecVersion_"+dagExecVersion;
					otherParams.put(key, value);
				}
		}*/// End if execParams have dataStoreList
		
		// Get Datastore info from execParams and populate in otherParams for tablename determination - END
		
		/*
		 * MetaType operatorType = ref.getType(); if
		 * (!operatorType.equals(MetaType.map)) { // continue with next task if
		 * not Map as we don't need to // parse further continue; }
		 */
//		Map map = (Map) daoRegister.getRefObject(ref);
		Map map = (Map) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");

		StringBuilder builder = new StringBuilder();
		/*List<AttributeRefHolder> filterInfo = indvTask.getOperators().get(0).getFilterInfo();
		// Get Filter info from execParams and append in FilterInfo - START
		if (execParams != null 
				&& execParams.getFilterInfo() != null 
				&& execParams.getFilterInfo().size() > 0) {
			if (filterInfo == null) {
				filterInfo = execParams.getFilterInfo();
			}else{
				filterInfo.addAll(execParams.getFilterInfo());
			}
		}*/
		// Get Filter info from execParams and append in FilterInfo - END
		// Get source type from map
		MetaType sourceType = getSourceType(map);
		if (sourceType == MetaType.relation) {
			// Generate relation table names
			parseRelDatapodNames(dagExec, indvTask, map, datapodList, refKeyMap, otherParams);
		} else if (sourceType == MetaType.datapod) {
			parseDatapodNames(dagExec, indvTask, map, datapodList, refKeyMap, otherParams);
		} /*else if (sourceType == MetaType.dataset) {
			Dataset dataset = (Dataset) daoRegister.getRefObject(map.getSource().getRef());
			parseRelDatapodNames(dagExec, indvTask, innerMap, datapodList, refKeyMap, otherParams);
		}*/
		
		// Include dagexec version in $DAGEXEC_VERSION
		otherParams.put($DAGEXEC_VERSION, dagExec.getVersion());
		
		if (operatorType == null || operatorType.equals(MetaType.map.toString()) || operatorType.equals(MetaType.matrixmult.toString())) {
			
			builder.append(mapOperator.generateSql(map, refKeyMap, otherParams, execParams, usedRefKeySet, runMode, paramValMap));
		} else {
			otherParams.put("operatorType", operatorType);
			builder.append(mapIterOperator.generateSql(map, refKeyMap, otherParams, operatorParams, execParams, usedRefKeySet, runMode));
		}
		// Include filterSql
		String finalSql = builder.toString();
		int lastIndexWhere = finalSql.lastIndexOf(WHERE_1_1);
		StringBuilder finalSqlBuilder = new StringBuilder(finalSql);
		finalSqlBuilder.replace(lastIndexWhere,WHERE_1_1.length()+lastIndexWhere,WHERE_1_1); 
		//finalSql = finalSql.replaceAll(WHERE_1_1, WHERE_1_1.concat(getFilterSql(filterInfo, refKeyMap, otherParams)));
		finalSql = finalSqlBuilder.toString();
		// Include $DAGEXEC_VERSION
		finalSql = finalSql.replaceAll($DAGEXEC_VERSION, dagExec.getVersion());
		builder = new StringBuilder().append(finalSql);

		return builder;
	}// End method
	
	protected MetaType getSourceType(Map map) {
		if (map == null || map.getSource() == null || map.getSource().getRef() == null) {
			return null;
		}
		return map.getSource().getRef().getType();
	}
	
	protected String getTableFromDatapod(Datapod datapod, Task indvTask, List<String> datapodList, 
											DagExec dagExec, HashMap<String, String> otherParams) throws Exception {
		if (indvTask.getDependsOn().size() > 0 && datapodList.contains(datapod.getUuid())) {
			return String.format("%s_%s_%s", datapod.getUuid().replaceAll("-", "_"), datapod.getVersion(),
					dagExec.getVersion());
		} else if (otherParams.containsKey("datapodUuid_"+datapod.getUuid()+"_tableName")) {
			String datapodVersion = otherParams.get("datapodUuid_"+datapod.getUuid()+"_tableName").split("_")[1];
			String dagExecVersion = otherParams.get("datapodUuid_"+datapod.getUuid()+"_tableName").split("_")[3];
			return String.format("%s_%s_%s", datapod.getUuid().replaceAll("-", "_"), datapodVersion,
					dagExecVersion);
		} else {
			return datapodServiceImpl.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), RunMode.BATCH);
		}
	}
	
	// If Map is dependent on datapod
	public void parseDatapodNames(DagExec dagExec, Task indvTask, Map map, List<String> datapodList,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams) throws Exception {
		Datapod datapod = mapOperator.getDatapodFromMap(map);
		String datapodStr = map.getTarget().getRef().getUuid();
		
		String table = getTableFromDatapod(datapod, indvTask, datapodList, dagExec, otherParams);
		otherParams.put("datapod_".concat(datapod.getUuid()), table);
		logger.info("adding target datapod in parseDatapodNames : " + datapodStr);
		datapodList.add(datapodStr);// Add target datapod in datapodlist
	}

	// If Map is dependent on relation
	public void parseRelDatapodNames(DagExec dagExec, Task indvTask, Map map, List<String> datapodList,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams) throws Exception {
		
		//ExecParams execParams = dagExec.getExecParams();
		Relation relation = mapOperator.getRelationFromMap(map);
		String datapodStr = map.getTarget().getRef().getUuid();
		
		// Get all relation tables
		// Start with main table
//		Datapod fromDatapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(relation.getDependsOn().getRef(), refKeyMap));
		MetaIdentifier fromDatapodRef = TaskParser.populateRefVersion(relation.getDependsOn().getRef(), refKeyMap);
		Datapod fromDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(fromDatapodRef.getUuid(), fromDatapodRef.getVersion(), fromDatapodRef.getType().toString(), "N");

		// Derive table name on the basis of depends on value.
		String table = getTableFromDatapod(fromDatapod, indvTask, datapodList, dagExec, otherParams);
		otherParams.put("relation_".concat(relation.getUuid().concat("_datapod_").concat(fromDatapod.getUuid())), table);
		
		// Do the same for other relation tables
		
		List<RelationInfo> relInfoList = relation.getRelationInfo();
		for (int i = 0; i < relInfoList.size(); i++) {
//			Datapod datapod = (Datapod) daoRegister.getRefObject(populateRefVersion(relInfoList.get(i).getJoin().getRef(), refKeyMap));
			MetaIdentifier ref = populateRefVersion(relInfoList.get(i).getJoin().getRef(), refKeyMap);
			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
			String rightTable = getTableFromDatapod(datapod, indvTask, datapodList, dagExec, otherParams);
			otherParams.put("relation_".concat(relation.getUuid().concat("_datapod_").concat(datapod.getUuid())), rightTable);
		}// End for
		logger.info("adding target datapod in parseRelDatapodNames : " + datapodStr);
		datapodList.add(datapodStr);// Add target datapod in datapodlist
	}
	
	/**
	 * Get datapods with version from dimension
	 * @param dimensionList
	 * @return
	 */
	private java.util.Map<String, List<MetaIdentifier>> getDimensionDefinedTables(List<AttributeRefHolder> dimensionList) {
		List<AttributeRefHolder> filteredDimensionList = null;
		java.util.Map<String, List<MetaIdentifier>> datapodMap = null;
		List<MetaIdentifier> datapodList = null;
		
		if (dimensionList == null || dimensionList.size() <= 0) {
			//System.out.println("No dimensions. Aborting getDimensionDefinedTables");
			return null;
		}
		
		// Filtering dimensions - START
		filteredDimensionList = new ArrayList<>();
		for (AttributeRefHolder dimension : dimensionList) {
			if (dimension.getRef().getType().equals(MetaType.datapod)) {
				filteredDimensionList.add(dimension);
			}
		}// Filtering dimensions - END
		
		// Fetch dataStore list by dimensions
		List<DataStore> dataStoreList = dataStoreServiceImpl.getDataStoreByDim(null, filteredDimensionList);
		
		// Get datapod and its versions from dataStoreList - START
		datapodMap = new HashMap<>();
		for (DataStore dataStore : dataStoreList) {
			if (datapodMap.containsKey(dataStore.getMetaId().getRef().getUuid())) {
				datapodMap.get(dataStore.getMetaId().getRef().getUuid()).add(dataStore.getMetaId().getRef());
			} else {
				datapodList = new ArrayList<>();
				datapodList.add(dataStore.getMetaId().getRef());
				datapodMap.put(dataStore.getMetaId().getRef().getUuid(), datapodList);
			}
		} // Get datapod and its versions from dataStoreList - END
		return datapodMap;
	}// END - Get dimension defined tables
}

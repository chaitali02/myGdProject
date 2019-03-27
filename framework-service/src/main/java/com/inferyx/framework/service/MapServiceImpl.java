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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.RelationInfo;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Task;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SysVarType;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.IExecutable;
import com.inferyx.framework.operator.IParsable;
import com.inferyx.framework.operator.MapOperator;
import com.inferyx.framework.parser.TaskParser;

@Service
public class MapServiceImpl implements IParsable, IExecutable {

	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	HDFSInfo hdfsInfo;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	protected DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	protected MapOperator mapOperator;
	@Autowired
	private MapExecServiceImpl mapExecServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	Engine engine;
	@Autowired
	Helper helper;
	@Autowired
	SessionHelper sessionHelper;
	@Resource(name="taskThreadMap")
	protected ConcurrentHashMap<?, ?> taskThreadMap;
	@Autowired
	private DownloadServiceImpl downloadServiceImpl;
	
	static final Logger logger = Logger.getLogger(MapServiceImpl.class);
	
//	private final String WHERE_1_1 = " WHERE (1=1) ";// " WHERE \\(1=1\\) ";

//	private final String $DAGEXEC_VERSION = "$DAGEXEC_VERSION";
	
	java.util.Map<String, String> requestMap = new HashMap<String, String>();

	/********************** UNUSED **********************/
	/*public Map findLatest() {
		return resolveName(iMapDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public Map findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iMapDao.findOneById(appUuid, id);
		} else
			return iMapDao.findOne(id);
	}*/
	/********************** UNUSED **********************/
	/*public Map getOneByUuidAndVersion(String uuid, String version) {

		return iMapDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Map findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iMapDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iMapDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id) {
		return iMapDao.exists(id);
	}*/

	

	

	/********************** UNUSED **********************/
	/*public List<Map> findAllLatestActive() {
		Aggregation mapAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Map> mapResults = mongoTemplate.aggregate(mapAggr, "map", Map.class);
		List<Map> mapList = mapResults.getMappedResults();

		// Fetch the load details for each id
		List<Map> result = new ArrayList<Map>();
		for (Map m : mapList) {
			Map mapLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				mapLatest = iMapDao.findOneByUuidAndVersion(appUuid, m.getId(), m.getVersion());
			} else {
				mapLatest = iMapDao.findOneByUuidAndVersion(m.getId(), m.getVersion());
			}
			if (mapLatest != null) {
				result.add(mapLatest);
			}
		}
		return result;
	}*/


	/********************** UNUSED **********************/
//	/**
//	 * 
//	 * @param datapod
//	 * @param indvTask
//	 * @param datapodList
//	 * @param dagExec
//	 * @param otherParams
//	 * @return
//	 * @throws Exception
//	 */
//	protected String getTableName(Datapod datapod, 
//			HashMap<String, String> otherParams, MapExec mapExec, RunMode runMode) throws Exception {
//		if (otherParams != null && otherParams.containsKey("datapodUuid_" + datapod.getUuid() + "_tableName")) {
//			return otherParams.get("datapodUuid_" + datapod.getUuid() + "_tableName");
//		} else {
//			try {
//				return datapodServiceImpl.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			} catch(Exception e) {
//				return String.format("%s_%s_%s", datapod.getUuid().replaceAll("-", "_"), datapod.getVersion(), mapExec.getVersion());
//			}
//		}
//	}

	// If Map is dependent on datapod
	public void parseDatapodNames(Datapod datapod, HashMap<String, String> otherParams, MapExec mapExec, RunMode runMode) throws Exception {
//		String tableName = getTableName(datapod, otherParams, mapExec, runMode);
		String tableName = datapodServiceImpl.genTableNameByDatapod(datapod, mapExec.getVersion(), null, otherParams, null, runMode, true);
		otherParams.put("datapodUuid_" + datapod.getUuid() + "_tableName", tableName);
	}

	// If Map is dependent on relation
	public void parseRelDatapodNames(Relation relation, 
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, MapExec mapExec, RunMode runMode) throws Exception {
		// Get all relation tables
		// Start with main table
//		if (otherParams == null) {
//			otherParams = new HashMap<>();
//		}
		
//		Datapod fromDatapod = null;
//		DataSet fromDataset = null;
		if (relation.getDependsOn().getRef().getType() == MetaType.datapod) {
//			fromDatapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(relation.getDependsOn().getRef(), refKeyMap));
			MetaIdentifier ref = TaskParser.populateRefVersion(relation.getDependsOn().getRef(), refKeyMap);
			Datapod fromDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
			parseDatapodNames(fromDatapod, otherParams, mapExec, runMode);
			// Derive table name on the basis of depends on value.
//			String tableName = getTableName(fromDatapod, otherParams, mapExec, runMode);
			String tableName = datapodServiceImpl.genTableNameByDatapod(fromDatapod, mapExec.getVersion(), null, otherParams, null, runMode, true);
			otherParams.put("relation_".concat(relation.getUuid().concat("_datapod_").concat(fromDatapod.getUuid())), tableName);
		} else if (relation.getDependsOn().getRef().getType() == MetaType.dataset) {
//			fromDataset = (DataSet) daoRegister.getRefObject(TaskParser.populateRefVersion(relation.getDependsOn().getRef(), refKeyMap));
			MetaIdentifier ref = TaskParser.populateRefVersion(relation.getDependsOn().getRef(), refKeyMap);
			DataSet fromDataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
			parseDSDatapodNames(fromDataset, refKeyMap, otherParams, mapExec, runMode);
		}

		
		// Do the same for other relation tables

		List<RelationInfo> relInfoList = relation.getRelationInfo();
		Datapod datapod = null;
		DataSet dataset = null;
		for (int i = 0; i < relInfoList.size(); i++) {
			if (relInfoList.get(i).getJoin().getRef().getType() == MetaType.datapod) {
//				datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(relInfoList.get(i).getJoin().getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(relInfoList.get(i).getJoin().getRef(), refKeyMap);
				datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
				
//				String rightTable = getTableName(datapod, otherParams, mapExec, runMode);
				String rightTable = datapodServiceImpl.genTableNameByDatapod(datapod, mapExec.getVersion(), null, otherParams, null, runMode, true);
				otherParams.put("relation_".concat(relation.getUuid().concat("_datapod_").concat(datapod.getUuid())),
						rightTable);
			} else if (relInfoList.get(i).getJoin().getRef().getType() == MetaType.dataset) {
//				dataset = (DataSet) daoRegister.getRefObject(TaskParser.populateRefVersion(relInfoList.get(i).getJoin().getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(relInfoList.get(i).getJoin().getRef(), refKeyMap);
				dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
				
				parseDSDatapodNames(dataset, refKeyMap, otherParams, mapExec, runMode);
			}
		} // End for
	}

	
	public void parseDPNames(Map map, List<String> datapodList,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, MapExec mapExec, RunMode runMode) throws Exception {
		String datapodStr = map.getTarget().getRef().getUuid();
		if (map.getSource().getRef().getType() == MetaType.datapod) {
			Datapod datapod = mapOperator.getDatapodFromMap(map);
			parseDatapodNames(datapod, otherParams, mapExec, runMode);
		} else if (map.getSource().getRef().getType() == MetaType.relation) {
			Relation relation = mapOperator.getRelationFromMap(map);
			parseRelDatapodNames(relation, refKeyMap, otherParams, mapExec, runMode);
		} else if (map.getSource().getRef().getType() == MetaType.dataset) {
			DataSet dataset = mapOperator.getDatasetFromMap(map);
			parseDSDatapodNames(dataset, refKeyMap, otherParams, mapExec, runMode);
		} else if (map.getSource().getRef().getType() == MetaType.rule) {
			Rule rule =  mapOperator.getRuleFromMap(map);
			parseRuleDatapodNames(rule, datapodList, refKeyMap, otherParams, mapExec, runMode);
		}
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(map.getTarget().getRef().getUuid(), map.getTarget().getRef().getVersion(), MetaType.datapod.toString());
		logger.info("adding target datapod in parseDPNames : " + datapodStr);
		otherParams.put("datapodUuid_" + datapodStr + "_tableName", datapodServiceImpl.genTableNameByDatapod(datapod, mapExec.getVersion(), null, otherParams, null, runMode, false));
		datapodList.add(datapodStr);// Add target datapod in datapodlist
	}
	
		// If Map is dependent on rule
		public void parseRuleDatapodNames(Rule rule, List<String> datapodList,
				java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, MapExec mapExec, RunMode runMode) throws Exception {
			if (rule.getSource().getRef().getType() == MetaType.relation) {
//				Relation relation = (Relation) daoRegister.getRefObject(rule.getSource().getRef());
				Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(rule.getSource().getRef().getUuid(), rule.getSource().getRef().getVersion(), rule.getSource().getRef().getType().toString(), "N");
				
				parseRelDatapodNames(relation, refKeyMap, otherParams, mapExec, runMode);
			} else if (rule.getSource().getRef().getType() == MetaType.datapod) {
//				Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(rule.getSource().getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(rule.getSource().getRef(), refKeyMap);
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
				
				parseDatapodNames(datapod, otherParams, mapExec, runMode);
			} else if (rule.getSource().getRef().getType() == MetaType.dataset) {
//				DataSet dataset = (DataSet) daoRegister.getRefObject(TaskParser.populateRefVersion(rule.getSource().getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(rule.getSource().getRef(), refKeyMap);
				DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
				
				parseDSDatapodNames(dataset, refKeyMap, otherParams, mapExec, runMode);
			}
		}

		// If Map is dependent on rule
		public void parseDSDatapodNames(DataSet dataset, 
				java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, MapExec mapExec, RunMode runMode) throws Exception {
			if (dataset.getDependsOn().getRef().getType() == MetaType.relation) {
//				Relation relation = (Relation) daoRegister.getRefObject(dataset.getDependsOn().getRef());
				Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(dataset.getDependsOn().getRef().getUuid(), dataset.getDependsOn().getRef().getVersion(), dataset.getDependsOn().getRef().getType().toString(), "N");
				
				parseRelDatapodNames(relation, refKeyMap, otherParams, mapExec, runMode);
			} else if (dataset.getDependsOn().getRef().getType() == MetaType.datapod) {
//				Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(dataset.getDependsOn().getRef(), refKeyMap));
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dataset.getDependsOn().getRef().getUuid(), dataset.getDependsOn().getRef().getVersion(), dataset.getDependsOn().getRef().getType().toString(), "N");
				
				parseDatapodNames(datapod, otherParams, mapExec, runMode);
			}
		}
	
	/**
	 * Get datapods with version from dimension
	 * 
	 * @param dimensionList
	 * @return
	 */
	/********************** UNUSED **********************/
	/*private java.util.Map<String, List<MetaIdentifier>> getDimensionDefinedTables(
			List<AttributeRefHolder> dimensionList) {
		List<AttributeRefHolder> filteredDimensionList = null;
		java.util.Map<String, List<MetaIdentifier>> datapodMap = null;
		List<MetaIdentifier> datapodList = null;

		if (dimensionList == null || dimensionList.size() <= 0) {
			System.out.println("No dimensions. Aborting getDimensionDefinedTables");
			return null;
		}

		// Filtering dimensions - START
		filteredDimensionList = new ArrayList<>();
		for (AttributeRefHolder dimension : dimensionList) {
			if (dimension.getRef().getType().equals(MetaType.datapod)) {
				filteredDimensionList.add(dimension);
			}
		} // Filtering dimensions - END

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
*/	
		/********************** UNUSED **********************/
	/*protected MetaType getSourceType(Map map) {
		if (map == null || map.getSource() == null || map.getSource().getRef() == null) {
			return null;
		}
		return map.getSource().getRef().getType();
	}*/
	
		
	/**
	 * 
	 * @param execParams
	 * @param baseRuleExec
	 */
	protected void checkInternalVarMap(ExecParams execParams, MapExec mapExec) {
		if (execParams == null) {
			return;
		}
		if ( execParams.getInternalVarMap() == null ) {
			execParams.setInternalVarMap(new HashMap<>());
		}
		
		if (!execParams.getInternalVarMap().containsKey("\\$".concat(SysVarType.exec_version.toString()))) {
			execParams.getInternalVarMap().put("\\$".concat(SysVarType.exec_version.toString()), mapExec.getVersion());
		}
	}
	
	/**
	 * 
	 * @param uuid
	 * @param version
	 * @param mapExec
	 * @param dagExec
	 * @param datapodList
	 * @param refKeyMap
	 * @param otherParams
	 * @param execParams
	 * @param runMode
	 * @return
	 * @throws Exception
	 */
	public MapExec create(String uuid, String version, MapExec mapExec, 
			DagExec dagExec, List<String> datapodList, 
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			ExecParams execParams, RunMode runMode) throws Exception {
		logger.info("Inside MapServiceImpl.create");
		try {
			if (otherParams == null) {
				otherParams = new HashMap<>();
			}
			if (datapodList == null) {
				datapodList = new ArrayList<>();
			}
			Map map = null;
			MetaIdentifierHolder mapRef = new MetaIdentifierHolder();
//			Task indvTask = null;
//			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();

			if (StringUtils.isBlank(version)) {
				//map = iMapDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
				map = (Map) commonServiceImpl.getLatestByUuid(uuid, MetaType.map.toString(), "N");
				version = map.getVersion();
			} else {
				//map = iMapDao.findOneByUuidAndVersion(uuid, version);
				map = (Map) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.map.toString(), "N");
			}
			// Create mapExec
			if (mapExec == null) {
				mapExec = new MapExec();
				mapRef.setRef(new MetaIdentifier(MetaType.map, uuid, version));
				mapExec.setDependsOn(mapRef);
				mapExec.setBaseEntity();
			}
			mapExec.setName(map.getName());
			mapExec.setAppInfo(map.getAppInfo());
			
			/***** This part is very important and populates otherParams based on the resolved table Names (Shall continue staying in MapServiceImpl) - START ******/
//			parseDPNames(map, datapodList, refKeyMap, otherParams, mapExec, runMode);
			/***** This part is very important and populates otherParams based on the resolved table Names (Shall continue staying in MapServiceImpl) - END ******/
			
			Status status = new Status(Status.Stage.PENDING, new Date());
			List<Status> statusList = new ArrayList<>();		
			statusList.add(status);
			//mapExec.setName(map.getName());
			mapExec.setStatusList(statusList);
			commonServiceImpl.save(MetaType.mapExec.toString(), mapExec);
		} catch(Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.mapExec, mapExec.getUuid(), mapExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create mapExec.", dependsOn);
			throw new Exception((message != null) ? message : "Can not create mapExec.");
		}
			return mapExec;
		}

		
	/**
	 * Generate SQL for Map and populate in MapExec
	 * @param uuid
	 * @param version
	 * @param mapExec
	 * @param dagExec
	 * @param stage
	 * @param indvExecTask
	 * @param datapodList
	 * @param refKeyMap
	 * @param otherParams
	 * @return MapExec
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public MapExec generateSql(String uuid, String version, MapExec mapExec, 
			DagExec dagExec, List<String> datapodList, 
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			ExecParams execParams, RunMode runMode) throws Exception {
		logger.info("Inside MapServiceImpl.generateSql");
		try {
			if (otherParams == null) {
				otherParams = new HashMap<>();
			}
			if (datapodList == null) {
				datapodList = new ArrayList<>();
			}
			Map map = null;
			MetaIdentifierHolder mapRef = new MetaIdentifierHolder();
			Task indvTask = null;
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();

			if (StringUtils.isBlank(version)) {
				//map = iMapDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
				map = (Map) commonServiceImpl.getLatestByUuid(uuid, MetaType.map.toString(), "N");
				version = map.getVersion();
			} else {
				//map = iMapDao.findOneByUuidAndVersion(uuid, version);
				map = (Map) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.map.toString(), "N");
			}
			// Create mapExec
			if (mapExec == null) {
				mapExec = new MapExec();
				mapRef.setRef(new MetaIdentifier(MetaType.map, uuid, version));
				mapExec.setDependsOn(mapRef);
				mapExec.setBaseEntity();
			}
			mapExec.setName(map.getName());
			mapExec.setAppInfo(map.getAppInfo());
			
			/***** This part is very important and populates otherParams based on the resolved table Names (Shall continue staying in MapServiceImpl) - START ******/
			parseDPNames(map, datapodList, refKeyMap, otherParams, mapExec, runMode);
			/***** This part is very important and populates otherParams based on the resolved table Names (Shall continue staying in MapServiceImpl) - END ******/
			
//			Status status = new Status(Status.Stage.PENDING, new Date());
			List<Status> statusList = mapExec.getStatusList();
			if (statusList == null || statusList.isEmpty()) {
				logger.error("MapExec does not have status during parse state. Creating new >>>>>>>>>>>>>>> ");
				statusList = new ArrayList<>();
				synchronized (mapExec.getUuid()) {
					commonServiceImpl.setMetaStatus(mapExec, MetaType.mapExec, Status.Stage.PENDING);
				}
				mapExec.setStatusList(statusList);
			}
//			statusList.add(status);
			//mapExec.setName(map.getName());
			commonServiceImpl.save(MetaType.mapExec.toString(), mapExec);
			try {
				logger.info("Before generateSql from MapServiceImpl");
				synchronized (mapExec.getUuid()) {
					commonServiceImpl.setMetaStatus(mapExec, MetaType.mapExec, Status.Stage.STARTING);
				}
				mapExec.setExec(mapOperator.generateSql(map, refKeyMap, otherParams, execParams, usedRefKeySet, runMode));
				synchronized (mapExec.getUuid()) {
					commonServiceImpl.setMetaStatus(mapExec, MetaType.mapExec, Status.Stage.READY);
				}
				// Fetch target datapod
				OrderKey datapodKey = map.getTarget().getRef().getKey();
				// Set target version
				if (execParams != null && DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()).get(MetaType.datapod + "_" + datapodKey.getUUID()) != null) {
					datapodKey.setVersion(DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()).get(MetaType.datapod + "_" + datapodKey.getUUID()).getVersion());
				} else {
					Datapod targetDatapod = (Datapod) commonServiceImpl
							.getOneByUuidAndVersion(map.getTarget().getRef().getUuid(), map.getTarget().getRef().getVersion(), MetaType.datapod.toString());
					datapodKey.setVersion(targetDatapod.getVersion());
				}
				/*String mapTableName = String.format("%s_%s_%s", datapodKey.getUUID().replace("-", "_"), datapodKey.getVersion(), mapExec.getVersion());
				if(execParams != null)
				execParams.getOtherParams().put("datapodUuid_" + datapodKey.getUUID() + "_tableName", mapTableName);
				*/
				String mapTableName = null;
				if(execParams != null) {
					//String mapTableName = String.format("%s_%s_%s", datapodKey.getUUID().replace("-", "_"), datapodKey.getVersion(), mapExec.getVersion());
//					Datasource datasource = commonServiceImpl.getDatasourceByApp();
					Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodKey.getUUID(), 
																					datapodKey.getVersion(), 
																					MetaType.datapod.toString(), "N");
					Datasource datasource = commonServiceImpl.getDatasourceByDatapod(targetDatapod);
					if (/*!engine.getExecEngine().equalsIgnoreCase("livy-spark")
							&& !datasource.getType().equalsIgnoreCase(ExecContext.spark.toString()) 
							&&*/ !datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
						mapTableName = datapodServiceImpl.getTableNameByDatapodKey(datapodKey, runMode);
					}  else {
						mapTableName = String.format("%s_%s_%s", datapodKey.getUUID().replace("-", "_"), datapodKey.getVersion(), mapExec.getVersion());
					}
					execParams.getOtherParams().put("datapodUuid_" + datapodKey.getUUID() + "_tableName", mapTableName);
				}				
				logger.info("Target table in map " + mapExec.getName() + " : " + mapTableName);
			} catch (Exception e) {
				e.printStackTrace();
				Status FAILEDStatus = new Status(Status.Stage.FAILED, new Date());
				if (statusList == null) {
					statusList = new ArrayList<>();
				}
				statusList.remove(FAILEDStatus);
				statusList.add(FAILEDStatus);
			}
			mapExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.mapExec, mapExec.getUuid(), mapExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not generate query.", dependsOn);
			throw new Exception((message != null) ? message : "Can not generate query.");
		}
		/***** Replace internalVarMap - START *****/
		checkInternalVarMap(execParams, mapExec);
		mapExec.setExec(DagExecUtil.replaceInternalVarMap(execParams, mapExec.getExec()));
		/***** Replace internalVarMap - END *****/
		return mapExec;
	}

	/********************** UNUSED 
	 * @throws Exception **********************/
	/*public StringBuilder generateSql(DagExec dagExec, Stage stage, TaskExec indvExecTask, List<String> datapodList,
			ExecParams execParams, HashMap<String, String> otherParams) throws Exception {

		java.util.Map<String, MetaIdentifier> refKeyMap = DagExecUtil
				.convertRefKeyListToMap(execParams.getRefKeyList());
		Task indvTask = DagExecUtil.getTaskFromStage(stage, indvExecTask.getTaskId());
		MetaIdentifier ref = indvTask.getOperators().get(0).getOperatorInfo().getRef();
		String operatorType = indvTask.getOperators().get(0).getOperatorType();
		HashMap<String, Object> operatorParams = indvTask.getOperators().get(0).getOperatorParams();
		
		// Create mapExec
		MapExec mapExec = new MapExec();
		mapExec.setBaseEntity();
		
		ref = TaskParser.populateRefVersion(ref, refKeyMap);

		Map map = (Map) daoRegister.getRefObject(ref);

		StringBuilder builder = new StringBuilder();
		//List<AttributeRefHolder> filterInfo = indvTask.getOperators().get(0).getFilterInfo();
		// Get Filter info from execParams and append in FilterInfo - START
		if (execParams != null && execParams.getFilterInfo() != null && execParams.getFilterInfo().size() > 0) {
			if (filterInfo == null) {
				filterInfo = execParams.getFilterInfo();
			} else {
				filterInfo.addAll(execParams.getFilterInfo());
			}
		}
		// Get Filter info from execParams and append in FilterInfo - END
		parseDPNames(dagExec, indvTask, map, datapodList, refKeyMap, otherParams);
		
		// Include dagexec version in $DAGEXEC_VERSION
		otherParams.put($DAGEXEC_VERSION, dagExec.getVersion());

		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		if (operatorType == null || operatorType.equals(MetaType.map.toString())
				|| operatorType.equals(MetaType.matrixmult.toString())) {
			builder.append(mapOperator.generateSql(map, refKeyMap, otherParams, execParams, usedRefKeySet));
		} else {
			otherParams.put("operatorType", operatorType);
			builder.append(mapIterOperator.generateSql(map, refKeyMap, otherParams, operatorParams, execParams, usedRefKeySet));
		}
		// Include filterSql
		String finalSql = builder.toString();
		// Include $DAGEXEC_VERSION
		finalSql = finalSql.replaceAll($DAGEXEC_VERSION, dagExec.getVersion());
		builder = new StringBuilder().append(finalSql);
		mapExec.setExec(finalSql);
		mapExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		mapExecServiceImpl.save(mapExec);
		MetaIdentifier mapExecIdentifier = new MetaIdentifier(MetaType.mapExec, mapExec.getUuid(), mapExec.getVersion());
		indvExecTask.getOperators().get(0).getOperatorInfo().setRef(mapExecIdentifier);
		
		return builder;
	}// End method
*/	
	public MapExec executeSql(MapExec mapExec, OrderKey datapodKey, RunMode runMode) throws Exception {
		//String sql = null;
		if(mapExec == null)	{
			mapExec = new MapExec();
			mapExec.setBaseEntity();
		}
//		Map map = (Map) daoRegister.getRefObject(mapExec.getDependsOn().getRef());
		Map map = (Map) commonServiceImpl.getOneByUuidAndVersion(mapExec.getDependsOn().getRef().getUuid(), mapExec.getDependsOn().getRef().getVersion(), mapExec.getDependsOn().getRef().getType().toString());
		MetaIdentifierHolder dependsPAUSEer = new MetaIdentifierHolder(new MetaIdentifier(MetaType.map, map.getUuid(), map.getVersion()));
		if (mapExec.getDependsOn() == null) {
			mapExec.setDependsOn(dependsPAUSEer);
		}
		mapExec.setAppInfo(map.getAppInfo());
		try {
			//mapExecServiceImpl.save(mapExec);
			commonServiceImpl.save(MetaType.mapExec.toString(), mapExec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Datapod targetDatapod = (Datapod) daoRegister.getRefObject(new MetaIdentifier(MetaType.datapod, map.getTarget().getRef().getUuid(), null));
		/*OrderKey datapodKey = new OrderKey(targetDatapod.getUuid(),
				targetDatapod.getVersion());*/
		//sql = mapExec.getExec();
		List<java.util.Map<String, Object>> data = new ArrayList<>();
		//mapExec.exportBaseProperty();
		//List<Status> statusList = new ArrayList<>();
		RunMapServiceImpl runMapServiceImpl = new RunMapServiceImpl();
		runMapServiceImpl.setMapExecServiceImpl(mapExecServiceImpl);
//		runMapServiceImpl.setDaoRegister(daoRegister);
		runMapServiceImpl.setData(data);
		runMapServiceImpl.setDataStoreServiceImpl(dataStoreServiceImpl);
		runMapServiceImpl.setHdfsInfo(hdfsInfo);
		runMapServiceImpl.setMap(map);
		runMapServiceImpl.setExecFactory(execFactory);
		runMapServiceImpl.setDatapodKey(datapodKey);
		runMapServiceImpl.setCommonServiceImpl(commonServiceImpl);
		runMapServiceImpl.setRunMode(runMode);
		runMapServiceImpl.setEngine(engine);
		runMapServiceImpl.setHelper(helper);
		runMapServiceImpl.setSecurityServiceImpl(securityServiceImpl);
		runMapServiceImpl.setSessionContext(sessionHelper.getSessionContext());
		runMapServiceImpl.setDatapodServiceImpl(datapodServiceImpl);
		//MetaIdentifier dependsOn = new MetaIdentifier(MetaType.map, map.getUuid(), map.getVersion());
		//Status status = new Status(Status.Stage.PENDING, new Date());
		//statusList.add(status);
		//mapExec.setName(map.getName());
		//mapExec.setStatus(statusList);
		//mapExec.setDependsOn(dependsOn);
		//mapExec.setExec(sql);
		runMapServiceImpl.setMapExec(mapExec);
		//mapExec.setAppInfo(map.getAppInfo());
		// Save MapExec
		//iMapExecDao.save(mapExec);
		runMapServiceImpl.setName(MetaType.mapExec+"_"+mapExec.getUuid()+"_"+mapExec.getVersion());
		runMapServiceImpl.setExecType(MetaType.mapExec);
		runMapServiceImpl.call();
		// Run rule
		/*if (taskExecutor == null) {
			runMapServiceImpl.run();
		} else {
			taskExecutor.execute(runMapServiceImpl);
		}*/
		
		return mapExec;
	}
	
	public List<java.util.Map<String, Object>> getMapResults(String mapExecUUID, String mapExecVersion, int offset, int limit,
			String sortBy, String order, String requestId, RunMode runMode) throws IOException, SQLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException {
		//String appUuid = commonServiceImpl.getApp().getUuid();
		List<java.util.Map<String, Object>> data = new ArrayList<>();
		limit = offset + limit;
		offset = offset + 1;
		MapExec mapExec = (MapExec) commonServiceImpl.getOneByUuidAndVersion(mapExecUUID, mapExecVersion,
				MetaType.mapExec.toString());
		DataStore datastore = dataStoreServiceImpl.getDatastore(mapExec.getResult().getRef().getUuid(),
				mapExec.getResult().getRef().getVersion());
		data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order, null);
		return data;
	}
	
	
	public HttpServletResponse download(String mapExecUuid, String mapExecVersion, String format, int offset,
			int limit, HttpServletResponse response, int rowLimit, String sortBy, String order, String requestId,
			RunMode runMode, Layout layout) throws Exception {
		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
		if(rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of "+maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Requested rows exceeded the limit of "+maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of "+maxRows);
		}
		MapExec mapExec = (MapExec) commonServiceImpl.getOneByUuidAndVersion(mapExecUuid, mapExecVersion, MetaType.mapExec.toString(), "N");
		List<java.util.Map<String, Object>> results = getMapResults(mapExecUuid, mapExecVersion, offset, limit, sortBy, order, requestId, runMode);
		response = downloadServiceImpl.download(format, response, runMode, results,
				new MetaIdentifierHolder(new MetaIdentifier(MetaType.mapExec, mapExecUuid, mapExecVersion)), layout,
				null, false, "framework.file.download.path", null, mapExec.getDependsOn());
		return response;

	}


	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// Validate input
		if (baseExec == null) {
			throw new Exception("No executable, cannot execute. ");
		}
		// Fetch Map
		Map map = (Map) commonServiceImpl.getOneByUuidAndVersion(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), baseExec.getDependsOn().getRef().getType().toString());
		// Fetch target datapod
		OrderKey datapodKey = map.getTarget().getRef().getKey();
		if (DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()).get(MetaType.datapod + "_" + datapodKey.getUUID()) != null) {
			datapodKey.setVersion(DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()).get(MetaType.datapod + "_" + datapodKey.getUUID()).getVersion());
		} else {
			Datapod targetDatapod = (Datapod) commonServiceImpl
					.getOneByUuidAndVersion(map.getTarget().getRef().getUuid(), map.getTarget().getRef().getVersion(), MetaType.datapod.toString());
			datapodKey.setVersion(targetDatapod.getVersion());
		}
		executeSql((MapExec) baseExec, datapodKey, runMode);
		return null;
	}

    
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		generateSql(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), (MapExec) baseExec, null, 
				null, DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(), execParams, runMode);
		return null;
	}
	
	public void restart(String uuid,String version, RunMode runMode) {
		try {
			MapExec mapExec =(MapExec) commonServiceImpl.getLatestByUuid(uuid,MetaType.mapExec.toString());
		    mapExec = generateSql(mapExec.getDependsOn().getRef().getUuid(),mapExec.getDependsOn().getRef().getVersion(), mapExec, null, null, null, null, null, runMode);
			com.inferyx.framework.domain.Map map = (com.inferyx.framework.domain.Map) commonServiceImpl.getLatestByUuid(mapExec.getDependsOn().getRef().getUuid(),MetaType.map.toString());
			OrderKey datapodKey = map.getTarget().getRef().getKey();
			mapExec =executeSql(mapExec, datapodKey, runMode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}

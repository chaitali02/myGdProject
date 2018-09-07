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

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.dao.IMapDao;
import com.inferyx.framework.dao.IMapExecDao;
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
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SysVarType;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.FilterOperator;
import com.inferyx.framework.operator.IExecutable;
import com.inferyx.framework.operator.IParsable;
import com.inferyx.framework.operator.MapIterOperator;
import com.inferyx.framework.operator.MapOperator;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.register.GraphRegister;

@Service
public class MapServiceImpl implements IParsable, IExecutable {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IMapDao iMapDao;
	@Autowired
	IMapExecDao iMapExecDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	FormulaServiceImpl formulaServiceImpl;
	@Autowired
	ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	ConditionServiceImpl conditionServiceImpl;
	/*@Autowired
	GroupServiceImpl groupServiceImpl;*/
	@Autowired
	HDFSInfo hdfsInfo;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	DatasetServiceImpl datasetServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	protected DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	protected MetadataUtil daoRegister;
	@Autowired
	protected MapOperator mapOperator;
	@Autowired
	protected FilterOperator filterOperator;
	@Autowired
	protected MapIterOperator mapIterOperator;
	@Autowired
	private MapExecServiceImpl mapExecServiceImpl;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	private RunMode runMode;
	@Autowired
	Engine engine;
	@Autowired
	Helper helper;
	@Autowired
	SessionHelper sessionHelper;
	static final Logger logger = Logger.getLogger(MapServiceImpl.class);
	
//	private final String WHERE_1_1 = " WHERE (1=1) ";// " WHERE \\(1=1\\) ";

//	private final String $DAGEXEC_VERSION = "$DAGEXEC_VERSION";
	
	java.util.Map<String, String> requestMap = new HashMap<String, String>();

	public RunMode getRunMode() {
		return runMode;
	}


	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

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
	/*public Map findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iMapDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} else {
			return iMapDao.findOneByUuidAndVersion(uuid, version);
		}
	}
*/
	/********************** UNUSED **********************/
	/*public Map getOneByUuidAndVersion(String uuid, String version) {

		return iMapDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public List<Map> findMapByDatapod(String datapodUUID) {
		// return iMapDao.findMapByDatapod(datapodUUID,new
		// Sort(Sort.Direction.DESC, "version"));

		// Need to test below code. Its not working.
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		// String appUuid = "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cb";
		Aggregation mapAggr = newAggregation(match(Criteria.where("target.ref.uuid").is(datapodUUID)),
				group("uuid").max("version").as("version").addToSet("uuid").as("uuid"));
		AggregationResults<Map> mapResults = mongoTemplate.aggregate(mapAggr, "map", Map.class);
		List<Map> mapList = mapResults.getMappedResults();

		// Fetch the datapod details for each id
		List<Map> result = new ArrayList<Map>();
		for (Map s : mapList) {
			Map mapLatest = iMapDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			result.add(mapLatest);
		}
		return result;
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
	/*public Map save(Map map) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		map.setAppInfo(metaIdentifierHolderList);
		map.setBaseEntity();
		Map mapDet = iMapDao.save(map);
		registerGraph.updateGraph((Object) mapDet, MetaType.map);
		return mapDet;
	}*/

	/********************** UNUSED **********************/
	/*public List<Map> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iMapDao.findAll();
		}
		return iMapDao.findAll(appUuid);
	}*/

	/********************** UNUSED **********************/
	/*@SuppressWarnings("unchecked")
	public List<Map> findAllDemo() {
		return (List<Map>) commonServiceImpl.findAll(MetaType.map);
	}*/

	/*public Map update(Map map) throws IOException {
		map.exportBaseProperty();
		Map mapDet = iMapDao.save(map);
		registerService.createGraph();
		return mapDet;
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id) {
		return iMapDao.exists(id);
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Map map = iMapDao.findOneById(appUuid, Id);
		map.setActive("N");
		iMapDao.save(map);
//		String ID = map.getId();
//		iMapDao.delete(ID);
//		map.exportBaseProperty();
	}*/

	/********************** UNUSED **********************/
	/*public List<Map> test(String param1) {
		return iMapDao.test(param1);
	}*/

	/********************** UNUSED **********************/
	/*public Map findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iMapDao.findAllByUuid(appUuid, uuid);

	}*/

	/********************** UNUSED **********************/
	/*public List<Map> findAllLatest() {
		{
			// String appUuid =
			// securityServiceImpl.getAppInfo().getRef().getUuid();
			Aggregation mapAggr = newAggregation(group("uuid").max("version").as("version"));
			AggregationResults<Map> mapResults = mongoTemplate.aggregate(mapAggr, "map", Map.class);
			List<Map> mapList = mapResults.getMappedResults();

			// Fetch the relation details for each id
			List<Map> result = new ArrayList<Map>();
			for (Map s : mapList) {
				Map mapLatest;
				String appUuid = (securityServiceImpl.getAppInfo() != null
						&& securityServiceImpl.getAppInfo().getRef() != null)
								? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
				if (appUuid != null) {
					// String appUuid =
					// securityServiceImpl.getAppInfo().getRef().getUuid();;
					mapLatest = iMapDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
				} else {
					mapLatest = iMapDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
				}
				// logger.debug("datapodLatest is " + datapodLatest.getName());
				if (mapLatest != null) {
					result.add(mapLatest);
				}
			}
			return result;
		}
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
	/*public Map resolveName(Map map) throws JsonProcessingException {
		if (map.getCreatedBy() != null) {
			String createdByRefUuid = map.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			map.getCreatedBy().getRef().setName(user.getName());
		}
		if (map.getAppInfo() != null) {
			for (int i = 0; i < map.getAppInfo().size(); i++) {
				String appUuid = map.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				map.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		String sourceType = map.getSource().getRef().getType().toString();
		if (sourceType.equalsIgnoreCase(MetaType.relation.toString())) {
			String sourceUuid = map.getSource().getRef().getUuid();
			Relation relationDO = relationServiceImpl.findLatestByUuid(sourceUuid);
			String relationName = relationDO.getName();
			map.getSource().getRef().setName(relationName);
		} else if (sourceType.equalsIgnoreCase(MetaType.datapod.toString())) {
			String sourceUuid = map.getSource().getRef().getUuid();
			Datapod datapodDO = datapodServiceImpl.findLatestByUuid(sourceUuid);
			String datapodName = datapodDO.getName();
			map.getSource().getRef().setName(datapodName);
		} else if (sourceType.equalsIgnoreCase(MetaType.dataset.toString())) {
			String sourceUuid = map.getSource().getRef().getUuid();
			Dataset datasetDO = datasetServiceImpl.findLatestByUuid(sourceUuid);
			String datasetName = datasetDO.getName();
			map.getSource().getRef().setName(datasetName);
		}
		String targetUuid = map.getTarget().getRef().getUuid();
		Datapod datapodDO = datapodServiceImpl.findLatestByUuid(targetUuid);
		String targetDatapodName = datapodDO.getName();
		map.getTarget().getRef().setName(targetDatapodName);

		if (map.getGroupBy() != null) {
			List<AttributeRefHolder> groupList = map.getGroupBy();
			for(int i=0; i<groupList.size(); i++)
			{
				String groupUuid = groupList.get(i).getRef().getUuid();
				int attrId = Integer.parseInt(groupList.get(i).getAttrId());
				Datapod datapod = datapodServiceImpl.findLatestByUuid(groupUuid);
				String dpName = datapod.getName();
				String attrName = datapod.getAttributes().get(attrId).getName();
				map.getGroupBy().get(i).getRef().setName(dpName);
				map.getGroupBy().get(i).setAttrName(attrName);
			}			
			//Group groupByDO = groupServiceImpl.findLatestByUuid(groupUuid);
			//String groupName = groupByDO.getName();
			//map.getGroupBy().getRef().setName(groupName);
		} else {
		}

		for (int i = 0; i < map.getAttributeMap().size(); i++) {
			MetaType sourceAttrType = map.getAttributeMap().get(i).getSourceAttr().getRef().getType();
			String sourceAttrUuid = map.getAttributeMap().get(i).getSourceAttr().getRef().getUuid();

			String targetAttrUuid = map.getAttributeMap().get(i).getTargetAttr().getRef().getUuid();
			Datapod targetAttrDatapodDO = datapodServiceImpl.findLatestByUuid(targetAttrUuid);
			String targetAttrDatapodName = targetAttrDatapodDO.getName();
			map.getAttributeMap().get(i).getTargetAttr().getRef().setName(targetAttrDatapodName);
			Integer targetAttributId = Integer.parseInt(map.getAttributeMap().get(i).getTargetAttr().getAttrId());

			List<Attribute> targetAttributeList = targetAttrDatapodDO.getAttributes();
			map.getAttributeMap().get(i).getTargetAttr()
					.setAttrName(targetAttributeList.get(targetAttributId).getName());

			if (sourceAttrType.toString().equalsIgnoreCase(MetaType.datapod.toString())) {
				Datapod sourceAttrDatapodDO = datapodServiceImpl.findLatestByUuid(sourceAttrUuid);
				String sourceDatapodName = sourceAttrDatapodDO.getName();
				map.getAttributeMap().get(i).getSourceAttr().getRef().setName(sourceDatapodName);

				Integer souceAttributId = Integer.parseInt(map.getAttributeMap().get(i).getSourceAttr().getAttrId());
				List<Attribute> datapodAttributeList = sourceAttrDatapodDO.getAttributes();
				map.getAttributeMap().get(i).getSourceAttr()
						.setAttrName(datapodAttributeList.get(souceAttributId).getName());
			}
			if (sourceAttrType.toString().equalsIgnoreCase(MetaType.formula.toString())) {
				Formula formulaDO = formulaServiceImpl.findLatestByUuid(sourceAttrUuid);
				String formulaName = formulaDO.getName();
				map.getAttributeMap().get(i).getSourceAttr().getRef().setName(formulaName);
			}

			if (sourceAttrType.toString().equalsIgnoreCase(MetaType.expression.toString())) {
				Expression expressionDO = expressionServiceImpl.findLatestByUuid(sourceAttrUuid);
				String expressionName = expressionDO.getName();
				map.getAttributeMap().get(i).getSourceAttr().getRef().setName(expressionName);
			}
		}

		return map;
	}
*/
	/********************** UNUSED **********************/
	/*public List<Map> resolveName(List<Map> map) {
		List<Map> mapList = new ArrayList<>();
		for (Map m : map) {
			String createdByRefUuid = m.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			m.getCreatedBy().getRef().setName(user.getName());
			mapList.add(m);
		}
		return mapList;
	}*/

	/********************** UNUSED **********************/
	/*public List<Map> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iMapDao.findAllVersion(appUuid, uuid);
		} else
			return iMapDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public com.inferyx.framework.domain.Map getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iMapDao.findAsOf(appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
		} else
			return iMapDao.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(com.inferyx.framework.domain.Map map) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();
		Map mapNew = new Map();
		mapNew.setName(map.getName()+"_copy");
		mapNew.setActive(map.getActive());		
		mapNew.setDesc(map.getDesc());		
		mapNew.setTags(map.getTags());	
		//mapNew.setGroupBy(map.getGroupBy());
		mapNew.setSource(map.getSource());
		mapNew.setTarget(map.getTarget());
		mapNew.setAttributeMap(map.getAttributeMap());
		save(mapNew);
		ref.setType(MetaType.map);
		ref.setUuid(mapNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/**
	 * 
	 * @param datapod
	 * @param indvTask
	 * @param datapodList
	 * @param dagExec
	 * @param otherParams
	 * @return
	 * @throws Exception
	 */
	protected String getTableName(Datapod datapod, 
			HashMap<String, String> otherParams, MapExec mapExec, RunMode runMode) throws Exception {
		if (otherParams != null && otherParams.containsKey("datapodUuid_" + datapod.getUuid() + "_tableName")) {
			return otherParams.get("datapodUuid_" + datapod.getUuid() + "_tableName");
		} else {
			try {
				dataStoreServiceImpl.setRunMode(runMode);
				return dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			} catch(Exception e) {
				return String.format("%s_%s_%s", datapod.getUuid().replaceAll("-", "_"), datapod.getVersion(), mapExec.getVersion());
			}
		}
	}

	// If Map is dependent on datapod
	public void parseDatapodNames(Datapod datapod, HashMap<String, String> otherParams, MapExec mapExec, RunMode runMode) throws Exception {
		String table = getTableName(datapod, otherParams, mapExec, runMode);
		otherParams.put("datapodUuid_" + datapod.getUuid() + "_tableName", table);
	}

	// If Map is dependent on relation
	public void parseRelDatapodNames(Relation relation, 
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, MapExec mapExec, RunMode runMode) throws Exception {
		// Get all relation tables
		// Start with main table
//		if (otherParams == null) {
//			otherParams = new HashMap<>();
//		}
		
		Datapod fromDatapod = null;
		DataSet fromDataset = null;
		if (relation.getDependsOn().getRef().getType() == MetaType.datapod) {
			fromDatapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(relation.getDependsOn().getRef(), refKeyMap));
			parseDatapodNames(fromDatapod, otherParams, mapExec, runMode);
			// Derive table name on the basis of depends on value.
			String table = getTableName(fromDatapod, otherParams, mapExec, runMode);
			otherParams.put("relation_".concat(relation.getUuid().concat("_datapod_").concat(fromDatapod.getUuid())), table);
		} else if (relation.getDependsOn().getRef().getType() == MetaType.dataset) {
			fromDataset = (DataSet) daoRegister.getRefObject(TaskParser.populateRefVersion(relation.getDependsOn().getRef(), refKeyMap));
			parseDSDatapodNames(fromDataset, refKeyMap, otherParams, mapExec, runMode);
		}

		
		// Do the same for other relation tables

		List<RelationInfo> relInfoList = relation.getRelationInfo();
		Datapod datapod = null;
		DataSet dataset = null;
		for (int i = 0; i < relInfoList.size(); i++) {
			if (relInfoList.get(i).getJoin().getRef().getType() == MetaType.datapod) {
				datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(relInfoList.get(i).getJoin().getRef(), refKeyMap));
				String rightTable = getTableName(datapod, otherParams, mapExec, runMode);
				otherParams.put("relation_".concat(relation.getUuid().concat("_datapod_").concat(datapod.getUuid())),
						rightTable);
			} else if (relInfoList.get(i).getJoin().getRef().getType() == MetaType.dataset) {
				dataset = (DataSet) daoRegister.getRefObject(TaskParser.populateRefVersion(relInfoList.get(i).getJoin().getRef(), refKeyMap));
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
		otherParams.put("datapodUuid_" + datapodStr + "_tableName", getTableName(datapod, otherParams, mapExec, runMode));
		datapodList.add(datapodStr);// Add target datapod in datapodlist
	}
	
		// If Map is dependent on rule
		public void parseRuleDatapodNames(Rule rule, List<String> datapodList,
				java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, MapExec mapExec, RunMode runMode) throws Exception {
			if (rule.getSource().getRef().getType() == MetaType.relation) {
				Relation relation = (Relation) daoRegister.getRefObject(rule.getSource().getRef());
				parseRelDatapodNames(relation, refKeyMap, otherParams, mapExec, runMode);
			} else if (rule.getSource().getRef().getType() == MetaType.datapod) {
				Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(rule.getSource().getRef(), refKeyMap));
				parseDatapodNames(datapod, otherParams, mapExec, runMode);
			} else if (rule.getSource().getRef().getType() == MetaType.dataset) {
				DataSet dataset = (DataSet) daoRegister.getRefObject(TaskParser.populateRefVersion(rule.getSource().getRef(), refKeyMap));
				parseDSDatapodNames(dataset, refKeyMap, otherParams, mapExec, runMode);
			}
		}

		// If Map is dependent on rule
		public void parseDSDatapodNames(DataSet dataset, 
				java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, MapExec mapExec, RunMode runMode) throws Exception {
			if (dataset.getDependsOn().getRef().getType() == MetaType.relation) {
				Relation relation = (Relation) daoRegister.getRefObject(dataset.getDependsOn().getRef());
				parseRelDatapodNames(relation, refKeyMap, otherParams, mapExec, runMode);
			} else if (dataset.getDependsOn().getRef().getType() == MetaType.datapod) {
				Datapod datapod = (Datapod) daoRegister
						.getRefObject(TaskParser.populateRefVersion(dataset.getDependsOn().getRef(), refKeyMap));
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
				map = (Map) commonServiceImpl.getLatestByUuid(uuid, MetaType.map.toString());
				version = map.getVersion();
			} else {
				//map = iMapDao.findOneByUuidAndVersion(uuid, version);
				map = (Map) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.map.toString());
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
			
			Status status = new Status(Status.Stage.NotStarted, new Date());
			List<Status> statusList = new ArrayList<>();		
			statusList.add(status);
			//mapExec.setName(map.getName());
			mapExec.setStatusList(statusList);		
			try {
				mapExec.setExec(mapOperator.generateSql(map, refKeyMap, otherParams, execParams, usedRefKeySet, runMode));
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
																					MetaType.datapod.toString());
					Datasource datasource = commonServiceImpl.getDatasourceByDatapod(targetDatapod);
					if (/*!engine.getExecEngine().equalsIgnoreCase("livy-spark")
							&& !datasource.getType().equalsIgnoreCase(ExecContext.spark.toString()) 
							&&*/ !datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
						mapTableName = dataStoreServiceImpl.getTableNameByDatapod(datapodKey, runMode);
					}  else {
						mapTableName = String.format("%s_%s_%s", datapodKey.getUUID().replace("-", "_"), datapodKey.getVersion(), mapExec.getVersion());
					}
					execParams.getOtherParams().put("datapodUuid_" + datapodKey.getUUID() + "_tableName", mapTableName);
				}				
				logger.info("Target table in map " + mapExec.getName() + " : " + mapTableName);
			} catch (Exception e) {
				e.printStackTrace();
				Status failedStatus = new Status(Status.Stage.Failed, new Date());
				if (statusList == null) {
					statusList = new ArrayList<>();
				}
				statusList.remove(failedStatus);
				statusList.add(failedStatus);
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
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not generate query.");
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
		Map map = (Map) daoRegister.getRefObject(mapExec.getDependsOn().getRef());
		MetaIdentifierHolder dependsOnHolder = new MetaIdentifierHolder(new MetaIdentifier(MetaType.map, map.getUuid(), map.getVersion()));
		if (mapExec.getDependsOn() == null) {
			mapExec.setDependsOn(dependsOnHolder);
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
		runMapServiceImpl.setDaoRegister(daoRegister);
		runMapServiceImpl.setiMapExecDao(iMapExecDao);
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
		//MetaIdentifier dependsOn = new MetaIdentifier(MetaType.map, map.getUuid(), map.getVersion());
		//Status status = new Status(Status.Stage.NotStarted, new Date());
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
		data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order);
		return data;
	}
	
	
	public HttpServletResponse download(String uuid, String version, String format, int offset,
			int limit, HttpServletResponse response, int rowLimit, String sortBy, String order, String requestId,
			RunMode runMode) throws Exception {
		//datastoreServiceImpl.setRunMode(runMode);
		/*DataStore ds = datastoreServiceImpl.findDataStoreByMeta(uuid, version);
		if (ds == null) {
			throw new Exception();
		}*/		
		
		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
		if(rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of "+maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Requested rows exceeded the limit of "+maxRows);
			throw new RuntimeException("Requested rows exceeded the limit of "+maxRows);
		}
		
		List<java.util.Map<String, Object>> results = getMapResults(uuid, version, offset, limit, sortBy, order, requestId, runMode);
		response = commonServiceImpl.download(uuid, version, format, offset, limit, response, rowLimit, sortBy, order, requestId, runMode, results,MetaType.downloadExec,new MetaIdentifierHolder(new MetaIdentifier(MetaType.mapExec,uuid,version)));

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

}

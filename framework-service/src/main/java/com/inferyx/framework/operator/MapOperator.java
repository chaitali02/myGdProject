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
package com.inferyx.framework.operator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.MessageStatus;

@Component
public class MapOperator implements IParsable {
	@Autowired
	protected AttributeMapOperator attributeMapOperator;
	@Autowired
	protected RelationOperator relationOperator;
	@Autowired
	protected GroupOperator groupOperator;
	@Autowired
	protected RuleOperator ruleOperator;
	@Autowired
	protected DatasetOperator datasetOperator;
	@Autowired
	DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	
	
//	private final String INSERT = "INSERT ";
	private final String SELECT = "SELECT ";
	private final String FROM = " FROM ";
	private final String WHERE_1_1 = " WHERE (1=1) ";
	
//	private final String $DAGEXEC_VERSION = "$DAGEXEC_VERSION";
	
	static final Logger logger = Logger.getLogger(MapOperator.class);

	public Relation getRelationFromMap(Map map) throws JsonProcessingException {
//		String uuid = map.getSource().getRef().getKey().getUUID();
//		if (null == map.getSource().getRef().getKey().getVersion()) {
//			return daoRegister.getRelationDao().findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
//		} else {
//			String version = map.getSource().getRef().getKey().getVersion();
//			return daoRegister.getRelationDao().findOneByUuidAndVersion(uuid, version);
//		}
		return (Relation) commonServiceImpl.getOneByUuidAndVersion(map.getSource().getRef().getUuid(), map.getSource().getRef().getVersion(), map.getSource().getRef().getType().toString(), "N");
	}
	
	public Rule getRuleFromMap(Map map) throws JsonProcessingException {
//		String uuid = map.getSource().getRef().getKey().getUUID();
//		if (null == map.getSource().getRef().getKey().getVersion()) {
//			return daoRegister.getiRuleDao().findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
//		} else {
//			String version = map.getSource().getRef().getKey().getVersion();
//			return daoRegister.getiRuleDao().findOneByUuidAndVersion(uuid, version);
//		}
		return (Rule) commonServiceImpl.getOneByUuidAndVersion(map.getSource().getRef().getUuid(), map.getSource().getRef().getVersion(), map.getSource().getRef().getType().toString(), "N");
	}
	
	public DataSet getDatasetFromMap(Map map) throws JsonProcessingException {
//		String uuid = map.getSource().getRef().getKey().getUUID();
//		if (null == map.getSource().getRef().getKey().getVersion()) {
//			return daoRegister.getDatasetDao().findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
//		} else {
//			String version = map.getSource().getRef().getKey().getVersion();
//			return daoRegister.getDatasetDao().findOneByUuidAndVersion(uuid, version);
//		}
		return (DataSet) commonServiceImpl.getOneByUuidAndVersion(map.getSource().getRef().getUuid(), map.getSource().getRef().getVersion(), map.getSource().getRef().getType().toString(), "N");
	}
	
	public Datapod getDatapodFromMap (Map map) throws JsonProcessingException {
//		String uuid = map.getSource().getRef().getKey().getUUID();
//		if (null == map.getSource().getRef().getKey().getVersion()) {
//			return daoRegister.getDatapodDao().findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
//		} else {
//			String version = map.getSource().getRef().getKey().getVersion();
//			return daoRegister.getDatapodDao().findOneByUuidAndVersion(uuid, version);
//		}
		return (Datapod) commonServiceImpl.getOneByUuidAndVersion(map.getSource().getRef().getUuid(), map.getSource().getRef().getVersion(), map.getSource().getRef().getType().toString(), "N");
	}

	public String generateSql(Map map, java.util.Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, ExecParams execParams, Set<MetaIdentifier> usedRefKeySet, RunMode runMode, 
			java.util.Map<String, String> paramValMap) throws Exception {
		Relation relation = null;
		// Target datapod
		StringBuilder builder = new StringBuilder();
		// SELECT
		builder.append(SELECT);
		MetaIdentifierHolder mapSource = map.getSource();
		attributeMapOperator.setRunMode(runMode);
		builder.append(attributeMapOperator.generateSql(map.getAttributeMap(),mapSource, refKeyMap, otherParams, execParams, paramValMap));
		// FROM
		builder.append(FROM);
		logger.info("OtherParams in MapOperator : " + otherParams);
		// If iterator step then ignore FROM and WHERE
		if (otherParams == null || !new Boolean(otherParams.get("iterStep"))) {
			// Append From
			if (map.getSource().getRef().getType() == MetaType.relation) {
				relation = getRelationFromMap(map);
				MetaIdentifier relationRef = new MetaIdentifier(MetaType.relation, relation.getUuid(), relation.getVersion());
				usedRefKeySet.add(relationRef);
				// Append JOIN
				builder.append(relationOperator.generateSql(relation, refKeyMap, otherParams, execParams, usedRefKeySet, runMode, paramValMap));
			} else if (map.getSource().getRef().getType() == MetaType.datapod) {
//				Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(map.getSource().getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(map.getSource().getRef(), refKeyMap);
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
				String table = null;
				if (otherParams == null 
						|| otherParams.get("datapod_".concat(datapod.getUuid())) == null) {
					table = datapodServiceImpl.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), RunMode.BATCH);
				} else {
					String tableKey = "datapod_".concat(datapod.getUuid());
					table = otherParams.get(tableKey);
				}
				logger.info("Source table in map " + map.getName() + " : " + table);
				builder.append(String.format(table, datapod.getName())).append(" ").append(datapod.getName()).append(" ");
			} else if (map.getSource().getRef().getType() == MetaType.dataset) {
//				DataSet dataset = (DataSet) daoRegister.getRefObject(map.getSource().getRef());
				DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(map.getSource().getRef().getUuid(), map.getSource().getRef().getVersion(), map.getSource().getRef().getType().toString(), "N");
				builder.append("(").append(datasetOperator.generateSql(dataset, refKeyMap, otherParams, usedRefKeySet, execParams, RunMode.BATCH, paramValMap)).append(")  ").append(dataset.getName());
			} else if (map.getSource().getRef().getType() == MetaType.rule) {
				Rule rule = (Rule) commonServiceImpl.getOneByUuidAndVersion(map.getSource().getRef().getUuid(), map.getSource().getRef().getVersion(), map.getSource().getRef().getType().toString(), "N");
				builder.append(" (" + ruleOperator.generateSql(rule, refKeyMap, otherParams, usedRefKeySet, null, RunMode.BATCH, paramValMap) + " )  " + rule.getName());
			}
			// Append Filter(s) - WHERE
			builder.append(WHERE_1_1);
		} // End skip for iterator check
		// GROUP BY
	//	builder.append(groupOperator.generateSql(map, refKeyMap, otherParams));
		// Fetch target datapod
		OrderKey datapodKey = map.getTarget().getRef().getKey();
		if (execParams != null && DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()).get(MetaType.datapod + "_" + datapodKey.getUUID()) != null) {
			datapodKey.setVersion(DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()).get(MetaType.datapod + "_" + datapodKey.getUUID()).getVersion());
		} else {
			Datapod targetDatapod = (Datapod) commonServiceImpl
					.getOneByUuidAndVersion(map.getTarget().getRef().getUuid(), map.getTarget().getRef().getVersion(), MetaType.datapod.toString(), "N");
			datapodKey.setVersion(targetDatapod.getVersion());
		}
		return builder.toString();
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		/***************  Initializing paramValMap - START ****************/
		java.util.Map<String, String> paramValMap = null;
		if (execParams.getParamValMap() == null) {
			execParams.setParamValMap(new HashMap<String, java.util.Map<String, String>>());
		}
		if (!execParams.getParamValMap().containsKey(baseExec.getUuid())) {
			execParams.getParamValMap().put(baseExec.getUuid(), new HashMap<String, String>());
		}
		paramValMap = execParams.getParamValMap().get(baseExec.getUuid());
		/***************  Initializing paramValMap - END ****************/
		Map map = (Map) commonServiceImpl.getOneByUuidAndVersion(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), MetaType.map.toString(), "N");
		baseExec.setName(map.getName());
		baseExec.setAppInfo(map.getAppInfo());
		try {
			Status status = new Status(Status.Stage.PENDING, new Date());
			List<Status> statusList = new ArrayList<>();		
			statusList.add(status);
			baseExec.setStatusList(statusList);		
			try {
				baseExec.setExec(generateSql(map, DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(), execParams, usedRefKeySet, runMode, paramValMap));
			} catch (Exception e) {
				Status FAILEDStatus = new Status(Status.Stage.FAILED, new Date());
				statusList.remove(FAILEDStatus);
				statusList.add(FAILEDStatus);
			}
			baseExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.mapExec, baseExec.getUuid(), baseExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not generate query.", dependsOn);
			throw new Exception((message != null) ? message : "Can not generate query.");
		}
		return baseExec;
	}

}

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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;

@Component
public class MapOperator {

	@Autowired
	protected MetadataUtil daoRegister;
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
	
	private final String INSERT = "INSERT ";
	private final String SELECT = "SELECT ";
	private final String FROM = " FROM ";
	private final String WHERE_1_1 = " WHERE (1=1) ";
	
	private final String $DAGEXEC_VERSION = "$DAGEXEC_VERSION";

	public Relation getRelationFromMap(Map map) {
		String uuid = map.getSource().getRef().getKey().getUUID();
		if (null == map.getSource().getRef().getKey().getVersion()) {
			return daoRegister.getRelationDao().findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		} else {
			String version = map.getSource().getRef().getKey().getVersion();
			return daoRegister.getRelationDao().findOneByUuidAndVersion(uuid, version);
		}
	}
	
	public Rule getRuleFromMap(Map map) {
		String uuid = map.getSource().getRef().getKey().getUUID();
		if (null == map.getSource().getRef().getKey().getVersion()) {
			return daoRegister.getiRuleDao().findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		} else {
			String version = map.getSource().getRef().getKey().getVersion();
			return daoRegister.getiRuleDao().findOneByUuidAndVersion(uuid, version);
		}
	}
	
	public DataSet getDatasetFromMap(Map map) {
		String uuid = map.getSource().getRef().getKey().getUUID();
		if (null == map.getSource().getRef().getKey().getVersion()) {
			return daoRegister.getDatasetDao().findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		} else {
			String version = map.getSource().getRef().getKey().getVersion();
			return daoRegister.getDatasetDao().findOneByUuidAndVersion(uuid, version);
		}
	}
	
	public Datapod getDatapodFromMap (Map map) {
		String uuid = map.getSource().getRef().getKey().getUUID();
		if (null == map.getSource().getRef().getKey().getVersion()) {
			return daoRegister.getDatapodDao().findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		} else {
			String version = map.getSource().getRef().getKey().getVersion();
			return daoRegister.getDatapodDao().findOneByUuidAndVersion(uuid, version);
		}
	}

	public String generateSql(Map map, java.util.Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, ExecParams execParams, Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
		Relation relation = null;
		// Target datapod
		StringBuilder builder = new StringBuilder();
		// SELECT
		builder.append(SELECT);
		MetaIdentifierHolder mapSource = map.getSource();
		attributeMapOperator.setRunMode(runMode);
		builder.append(attributeMapOperator.generateSql(map.getAttributeMap(),mapSource, refKeyMap, otherParams, execParams));
		// FROM
		builder.append(FROM);
		// If iterator step then ignore FROM and WHERE
		if (otherParams == null || !new Boolean(otherParams.get("iterStep"))) {
			// Append From
			if (map.getSource().getRef().getType() == MetaType.relation) {
				relation = getRelationFromMap(map);
				MetaIdentifier relationRef = new MetaIdentifier(MetaType.relation, relation.getUuid(), relation.getVersion());
				usedRefKeySet.add(relationRef);
				// Append JOIN
				builder.append(relationOperator.generateSql(relation, refKeyMap, otherParams, execParams, usedRefKeySet, runMode));
			} else if (map.getSource().getRef().getType() == MetaType.datapod) {
				Datapod datapod = (Datapod) daoRegister
						.getRefObject(TaskParser.populateRefVersion(map.getSource().getRef(), refKeyMap));
				String table = null;
				if (otherParams == null 
						|| otherParams.get("datapod_".concat(datapod.getUuid())) == null) {
					table = datastoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), RunMode.BATCH);
				} else {
					String tableKey = "datapod_".concat(datapod.getUuid());
					table = otherParams.get(tableKey);
				}
				builder.append(String.format(table, datapod.getName())).append(" ").append(datapod.getName()).append(" ");
			} else if (map.getSource().getRef().getType() == MetaType.dataset) {
				DataSet dataset = (DataSet) daoRegister.getRefObject(map.getSource().getRef());
				builder.append("(").append(datasetOperator.generateSql(dataset, refKeyMap, otherParams, usedRefKeySet, execParams, RunMode.BATCH)).append(")  ").append(dataset.getName());
			} else if (map.getSource().getRef().getType() == MetaType.rule) {
				Rule rule = (Rule) daoRegister.getRefObject(map.getSource().getRef());
				builder.append(" (" + ruleOperator.generateSql(rule, refKeyMap, otherParams, usedRefKeySet, null, RunMode.BATCH) + " )  " + rule.getName());
			}
			// Append Filter(s) - WHERE
			builder.append(WHERE_1_1);
		} // End skip for iterator check
		// GROUP BY
	//	builder.append(groupOperator.generateSql(map, refKeyMap, otherParams));
		
		// Replace $DAGEXEC_VERSION if that exists
		if (otherParams!= null && otherParams.containsKey($DAGEXEC_VERSION)) { 
			return builder.toString().replaceAll("\"\\$DAGEXEC_VERSION\"", otherParams.get($DAGEXEC_VERSION));
		}
		return builder.toString();
	}

}

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
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FilterInfo;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.RelationInfo;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.DataStoreServiceImpl;
 
@Component
public class RelationOperator {
	
	@Autowired
	protected MetadataUtil daoRegister;
	@Autowired
	protected JoinKeyOperator joinKeyOperator;
	@Autowired
	protected DataStoreServiceImpl dataStoreServiceImpl;
	
	static final Logger logger = Logger.getLogger(RelationOperator.class);
	
//	private final String FROM = " FROM ";
	
	public String generateSql(Relation relation
			, java.util.Map<String, MetaIdentifier> refKeyMap
			, HashMap<String, String> otherParams, ExecParams execParams, Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
		Map<String, Short> datapodTracker = new HashMap<>(); 
		StringBuilder builder = new StringBuilder();
		List<RelationInfo> relInfoList = relation.getRelationInfo();
//		boolean hasDimensions = false;
//		
//		if (execParams != null && execParams.getDimInfo() != null && execParams.getDimInfo().size() > 0) {
//			hasDimensions = true;
//		}
		
		Datapod fromDatapod = (Datapod) daoRegister
				.getRefObject(TaskParser.populateRefVersion(relation.getDependsOn().getRef(), refKeyMap));
		datapodTracker.put(fromDatapod.getUuid(), new Short((short) 1));
		String table = "";
		MetaIdentifier fromDatapodRef = new MetaIdentifier(MetaType.datapod, fromDatapod.getUuid(), fromDatapod.getVersion());
		usedRefKeySet.add(fromDatapodRef);
		logger.info("OtherParams in relationOperator : " + otherParams);
		
/*		DataStore dataStore = getDatastoreByDim(fromDatapod.getUuid(), hasDimensions?execParams.getDimInfo():null); 
		if (dataStore != null && hasDimensions) {	// If we get dataStore by dimension then use that for table name
			table = dataStoreServiceImpl.getTableName(dataStore.getUuid(), dataStore.getVersion());
			dataStore = null;
		} else */
		if (otherParams != null && otherParams.containsKey("datapodUuid_"+fromDatapod.getUuid()+"_tableName")) {
			table = otherParams.get("datapodUuid_"+fromDatapod.getUuid()+"_tableName");
		} else if (otherParams == null 
				|| (otherParams.get("relation_".concat(relation.getUuid().concat("_datapod_").concat(fromDatapod.getUuid()))) == null
				&& otherParams.get("datapodUuid_"+fromDatapod.getUuid()+"_tableName") == null)) {
			table = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(fromDatapod.getUuid(), fromDatapod.getVersion()), runMode);
		} else {
			String tableKey = "relation_".concat(relation.getUuid().concat("_datapod_").concat(fromDatapod.getUuid()));
			table = otherParams.get(tableKey);
			if (StringUtils.isNotBlank(table) && table.contains(",")) {
				String tableArr[] = table.split(",");
				table = populateVerUnion(tableArr);
			}
		}
		builder.append(String.format(table, fromDatapod.getName())).append(" ").append(fromDatapod.getName()).append(" ");
		for (int i = 0; i < relInfoList.size(); i++) {
			Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(relInfoList.get(i).getJoin().getRef(), refKeyMap));
			if (!datapodTracker.containsKey(datapod.getUuid())) {
				datapodTracker.put(datapod.getUuid(), new Short((short) 1));
			} else {
				datapodTracker.put(datapod.getUuid(), (short) (datapodTracker.get(datapod.getUuid()) + 1));
			}
			String rightTable = null;
			MetaIdentifier datapodRef = new MetaIdentifier(MetaType.datapod, datapod.getUuid(), datapod.getVersion());
			usedRefKeySet.add(datapodRef);

			String joinType = relInfoList.get(i).getJoinType();
			List<FilterInfo> joinKey = relInfoList.get(i).getJoinKey();
			/*dataStore = getDatastoreByDim(datapod.getUuid(), hasDimensions?execParams.getDimInfo():null);
			if (dataStore != null && hasDimensions) {	// If we get dataStore by dimension then use that for table name
				rightTable = dataStoreServiceImpl.getTableName(dataStore.getUuid(), dataStore.getVersion());
				dataStore = null;
			} else */
			if (otherParams != null && otherParams.containsKey("datapodUuid_".concat(datapod.getUuid()).concat("_tableName"))) {
				logger.info("datapodUuid_"+datapod.getUuid()+"_tableName : " + otherParams.get("datapodUuid_".concat(datapod.getUuid()).concat("_tableName")));
				String tableKey = "datapodUuid_".concat(datapod.getUuid()).concat("_tableName");
				rightTable = otherParams.get(tableKey);
			} else if (otherParams == null 
					|| (otherParams.get("relation_".concat(relation.getUuid().concat("_datapod_").concat(datapod.getUuid()))) == null  
					&& otherParams.get("datapodUuid_".concat(datapod.getUuid()).concat("_tableName")) == null)) {
				rightTable = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			} else {
				logger.info("datapodUuid_"+datapod.getUuid()+"_tableName : " + otherParams.get("datapodUuid_".concat(datapod.getUuid()).concat("_tableName")));
				String tableKey = "relation_".concat(relation.getUuid().concat("_datapod_").concat(datapod.getUuid()));
				rightTable = otherParams.get(tableKey);
				if (StringUtils.isNotBlank(rightTable) && rightTable.contains(",")) {
					String tableArr[] = rightTable.split(",");
					rightTable = populateVerUnion(tableArr);
				}
			}

			if (joinKey.size() > 0) {
				builder.append(joinType + " JOIN ").append(" ").append(rightTable).append("  ");
				if (datapodTracker.get(datapod.getUuid()) <= 1) {
					builder.append(datapod.getName());
				} else {
					builder.append(datapod.getName()).append("_").append(datapodTracker.get(datapod.getUuid()));
				}
				builder.append(" ").append(" ON ").append(joinKeyOperator.generateSql(joinKey,relation.getDependsOn(), refKeyMap, otherParams, usedRefKeySet, execParams));
			}
		}
		return builder.toString();
	}
	
	/**
	 * Get latest datastore for a combination of datapod uuid and dimension list 
	 * @param metaUuid
	 * @param dimensionList
	 * @return
	 */
	private DataStore getDatastoreByDim(String metaUuid, List<AttributeRefHolder> dimensionList) {
		if (StringUtils.isBlank(metaUuid) || dimensionList == null || dimensionList.isEmpty()) {
			return null;
		}
		// Fetch dataStore list by dimensions
		List<DataStore> dataStoreList = dataStoreServiceImpl.getDataStoreByDim(metaUuid, dimensionList);
		// Return latest dataStorS for the matching dimensions
		return dataStoreList.get(0);
	}
	
	/** 
	 * Utility to create SQL to union all data from all versions of a datapod if multiple versions are chosen from vizpod
	 * @param tableArr
	 * @return
	 */
	private String populateVerUnion(String []tableArr) {
		StringBuilder versionUnion = new StringBuilder();
		int count = 0;
		if (tableArr == null || tableArr.length == 0) {
			return null;
		}
		versionUnion.append(" (");
		for (String table : tableArr) {
			if (count == 0) {
				versionUnion.append(" UNION ALL ");
				++count;
			}
			versionUnion.append("SELECT * FROM ")
						.append(table);
		}
		versionUnion.append(") ");
		return versionUnion.toString();
	}
	
}

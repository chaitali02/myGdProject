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

import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FilterInfo;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.RelationInfo;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
 
@Component
public class RelationOperator {
	@Autowired
	protected CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	protected JoinKeyOperator joinKeyOperator;
	@Autowired
	protected DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	protected DatasetOperator datasetOperator;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	
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
		Datapod fromDatapod = null;
		DataSet fromDataset = null;
		String table = "";
		String dsVersion = null;
		if (relation.getDependsOn().getRef().getType() == MetaType.datapod) {
//			fromDatapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(relation.getDependsOn().getRef(), refKeyMap));
			MetaIdentifier ref = TaskParser.populateRefVersion(relation.getDependsOn().getRef(), refKeyMap);
			fromDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
			datapodTracker.put(fromDatapod.getUuid(), new Short((short) 1));
			MetaIdentifier fromDatapodRef = new MetaIdentifier(MetaType.datapod, fromDatapod.getUuid(), fromDatapod.getVersion());
			usedRefKeySet.add(fromDatapodRef);
		} else if (relation.getDependsOn().getRef().getType() == MetaType.dataset) {
			datapodTracker.put(relation.getDependsOn().getRef().getUuid(), new Short((short) 1));
		}
		logger.info("OtherParams in relationOperator : " + otherParams);
		
/*		DataStore dataStore = getDatastoreByDim(fromDatapod.getUuid(), hasDimensions?execParams.getDimInfo():null); 
		if (dataStore != null && hasDimensions) {	// If we get dataStore by dimension then use that for table name
			table = dataStoreServiceImpl.getTableName(dataStore.getUuid(), dataStore.getVersion());
			dataStore = null;
		} else */
		if (relation.getDependsOn().getRef().getType() == MetaType.datapod 
				&& otherParams != null && otherParams.containsKey("datapodUuid_"+fromDatapod.getUuid()+"_tableName")) {
			table = otherParams.get("datapodUuid_"+fromDatapod.getUuid()+"_tableName");
		} else if (relation.getDependsOn().getRef().getType() == MetaType.dataset 
				&& otherParams != null && otherParams.containsKey("datasetUuid_"+relation.getDependsOn().getRef().getUuid()+"_version")) {
			dsVersion = otherParams.get("datasetUuid_"+relation.getDependsOn().getRef().getUuid()+"_version");
		} else if (relation.getDependsOn().getRef().getType() == MetaType.datapod 
				&& (otherParams == null 
				|| (otherParams.get("relation_".concat(relation.getUuid().concat("_datapod_").concat(fromDatapod.getUuid()))) == null
				&& otherParams.get("datapodUuid_"+fromDatapod.getUuid()+"_tableName") == null))) {
			table = datapodServiceImpl.getTableNameByDatapodKey(new OrderKey(fromDatapod.getUuid(), fromDatapod.getVersion()), runMode);
		} else if (relation.getDependsOn().getRef().getType() == MetaType.datapod ){
			String tableKey = "relation_".concat(relation.getUuid().concat("_datapod_").concat(fromDatapod.getUuid()));
			table = otherParams.get(tableKey);
			if (StringUtils.isNotBlank(table) && table.contains(",")) {
				String tableArr[] = table.split(",");
				table = populateVerUnion(tableArr);
			}
		} 
		if (relation.getDependsOn().getRef().getType() == MetaType.datapod){
			builder.append(String.format(table, fromDatapod.getName())).append(" ").append(fromDatapod.getName()).append(" ");
		} else if (relation.getDependsOn().getRef().getType() == MetaType.dataset){
			fromDataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(relation.getDependsOn().getRef().getUuid()
																				, dsVersion
																				, relation.getDependsOn().getRef().getType().toString());
			builder.append("(").append(datasetOperator.generateSql(fromDataset, refKeyMap, otherParams, usedRefKeySet, execParams, runMode)).append(") ").append(fromDataset.getName()).append(" ");
		}
		for (int i = 0; i < relInfoList.size(); i++) {
			Datapod datapod = null;
			DataSet rightDataset = null;
			String rightTable = null;
			String rightDsVersion = null;
			if (relInfoList.get(i).getJoin().getRef().getType() == MetaType.datapod) {
//				datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(relInfoList.get(i).getJoin().getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(relInfoList.get(i).getJoin().getRef(), refKeyMap);
				datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
				if (!datapodTracker.containsKey(datapod.getUuid())) {
					datapodTracker.put(datapod.getUuid(), new Short((short) 1));
				} else {
					datapodTracker.put(datapod.getUuid(), (short) (datapodTracker.get(datapod.getUuid()) + 1));
				}
				MetaIdentifier datapodRef = new MetaIdentifier(MetaType.datapod, datapod.getUuid(), datapod.getVersion());
				usedRefKeySet.add(datapodRef);
			} if (relInfoList.get(i).getJoin().getRef().getType() == MetaType.dataset) {
				if (!datapodTracker.containsKey(relInfoList.get(i).getJoin().getRef().getUuid())) {
					datapodTracker.put(relInfoList.get(i).getJoin().getRef().getUuid(), new Short((short) 1));
				} else {
					datapodTracker.put(relInfoList.get(i).getJoin().getRef().getUuid(), (short) (datapodTracker.get(relInfoList.get(i).getJoin().getRef().getUuid()) + 1));
				}
			}

			String joinType = relInfoList.get(i).getJoinType();
			List<FilterInfo> joinKey = relInfoList.get(i).getJoinKey();
			/*dataStore = getDatastoreByDim(datapod.getUuid(), hasDimensions?execParams.getDimInfo():null);
			if (dataStore != null && hasDimensions) {	// If we get dataStore by dimension then use that for table name
				rightTable = dataStoreServiceImpl.getTableName(dataStore.getUuid(), dataStore.getVersion());
				dataStore = null;
			} else */
			if (relInfoList.get(i).getJoin().getRef().getType() == MetaType.datapod 
					&& otherParams != null && otherParams.containsKey("datapodUuid_".concat(datapod.getUuid()).concat("_tableName"))) {
				logger.info("datapodUuid_"+datapod.getUuid()+"_tableName : " + otherParams.get("datapodUuid_".concat(datapod.getUuid()).concat("_tableName")));
				String tableKey = "datapodUuid_".concat(datapod.getUuid()).concat("_tableName");
				rightTable = otherParams.get(tableKey);
			} else if (relInfoList.get(i).getJoin().getRef().getType() == MetaType.dataset 
					&& otherParams != null && otherParams.containsKey("datasetUuid_"+relation.getDependsOn().getRef().getUuid()+"_version")) {
				rightDsVersion = otherParams.get("datasetUuid_"+relation.getDependsOn().getRef().getUuid()+"_version");
			} else if (relInfoList.get(i).getJoin().getRef().getType() == MetaType.datapod 
					&& (otherParams == null 
					|| (otherParams.get("relation_".concat(relation.getUuid().concat("_datapod_").concat(datapod.getUuid()))) == null  
					&& otherParams.get("datapodUuid_".concat(datapod.getUuid()).concat("_tableName")) == null))) {
				rightTable = datapodServiceImpl.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			} else if (relInfoList.get(i).getJoin().getRef().getType() == MetaType.datapod) {
				logger.info("datapodUuid_"+datapod.getUuid()+"_tableName : " + otherParams.get("datapodUuid_".concat(datapod.getUuid()).concat("_tableName")));
				String tableKey = "relation_".concat(relation.getUuid().concat("_datapod_").concat(datapod.getUuid()));
				rightTable = otherParams.get(tableKey);
				if (StringUtils.isNotBlank(rightTable) && rightTable.contains(",")) {
					String tableArr[] = rightTable.split(",");
					rightTable = populateVerUnion(tableArr);
				}
			}
			
			Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(relation);
			if (relation.getDependsOn().getRef().getType() == MetaType.datapod) {
				MetaIdentifier joinMI = relInfoList.get(i).getJoin().getRef();
				if(joinMI.getType().equals(MetaType.dataset)) {
					rightDataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(relInfoList.get(i).getJoin().getRef().getUuid()
																			, rightDsVersion
																			, relInfoList.get(i).getJoin().getRef().getType().toString());
					builder.append(joinType + " JOIN ").append(" (").append(datasetOperator.generateSql(rightDataset, refKeyMap, otherParams, usedRefKeySet, execParams, runMode)).append(")  ");
					if (datapodTracker.get(rightDataset.getUuid()) <= 1) {
						builder.append(rightDataset.getName());
					} else {
						builder.append(rightDataset.getName()).append("_").append(datapodTracker.get(rightDataset.getUuid()));
					}
					if (joinKey != null && joinKey.size() > 0) {
						builder.append(" ").append(" ON ").append(joinKeyOperator.generateSql(joinKey,relInfoList.get(i).getJoin(), refKeyMap, otherParams, usedRefKeySet, execParams, false, false, null, mapSourceDS));
					}
				} else {
					builder.append(joinType + " JOIN ").append(" ").append(rightTable).append("  ");
					if (datapodTracker.get(datapod.getUuid()) <= 1) {
						builder.append(datapod.getName());
					} else {
						builder.append(datapod.getName()).append("_").append(datapodTracker.get(datapod.getUuid()));
					}
					if (joinKey != null && joinKey.size() > 0) {
						builder.append(" ").append(" ON ").append(joinKeyOperator.generateSql(joinKey,relation.getDependsOn(), refKeyMap, otherParams, usedRefKeySet, execParams, false, false, null, mapSourceDS));
					}
				}
			} else if (relation.getDependsOn().getRef().getType() == MetaType.dataset) {
				MetaIdentifier joinMI = relInfoList.get(i).getJoin().getRef();
				if(joinMI.getType().equals(MetaType.dataset)) {
					rightDataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(relInfoList.get(i).getJoin().getRef().getUuid()
																			, rightDsVersion
																			, relInfoList.get(i).getJoin().getRef().getType().toString());
					builder.append(joinType + " JOIN ").append(" (").append(datasetOperator.generateSql(rightDataset, refKeyMap, otherParams, usedRefKeySet, execParams, runMode)).append(")  ");
					if (datapodTracker.get(rightDataset.getUuid()) <= 1) {
						builder.append(rightDataset.getName());
					} else {
						builder.append(rightDataset.getName()).append("_").append(datapodTracker.get(rightDataset.getUuid()));
					}
					if (joinKey != null && joinKey.size() > 0) {
						builder.append(" ").append(" ON ").append(joinKeyOperator.generateSql(joinKey,relInfoList.get(i).getJoin(), refKeyMap, otherParams, usedRefKeySet, execParams, false, false, null, mapSourceDS));
					}
				} else if(joinMI.getType().equals(MetaType.datapod)) {
					datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(relInfoList.get(i).getJoin().getRef().getUuid()
							, rightDsVersion
							, relInfoList.get(i).getJoin().getRef().getType().toString());
					if(StringUtils.isNotBlank(rightTable)) {
						builder.append(joinType + " JOIN ").append(" ").append(rightTable).append("  ");
						if (datapodTracker.get(datapod.getUuid()) <= 1) {
							builder.append(datapod.getName());
						} else {
							builder.append(datapod.getName()).append("_").append(datapodTracker.get(datapod.getUuid()));
						}
						if (joinKey != null && joinKey.size() > 0) {
							builder.append(" ").append(" ON ").append(joinKeyOperator.generateSql(joinKey,relInfoList.get(i).getJoin(), refKeyMap, otherParams, usedRefKeySet, execParams, false, false, null, mapSourceDS));
						}
					} else {
						builder.append(joinType + " JOIN ").append(" ");
						builder.append(datapodServiceImpl.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode)).append(" ").append(datapod.getName()).append(" ");

						if (joinKey != null && joinKey.size() > 0) {
							builder.append(" ").append(" ON ").append(joinKeyOperator.generateSql(joinKey, relInfoList.get(i).getJoin(), refKeyMap, otherParams, usedRefKeySet, execParams, false, false, null, mapSourceDS));
						}
					}
				}
			}
			datapod = null;
			rightDataset = null;
			rightTable = null;
			rightDsVersion = null;
		}
		return builder.toString();
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
	
	public String generateSampleDataSql(Relation relation
			, java.util.Map<String, MetaIdentifier> refKeyMap
			, HashMap<String, String> otherParams
			, ExecParams execParams
			, Set<MetaIdentifier> usedRefKeySet
			, int limit
			, RunMode runMode) throws Exception {
		StringBuilder sampleDatSql = new StringBuilder("SELECT * FROM "); 
		String relSql = generateSql(relation, refKeyMap, otherParams, execParams, usedRefKeySet, runMode);
		sampleDatSql.append(relSql);
		sampleDatSql.append(" LIMIT ").append(limit);
		return sampleDatSql.toString();
	}
}

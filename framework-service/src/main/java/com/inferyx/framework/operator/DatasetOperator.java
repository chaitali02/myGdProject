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


import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.domain.AttributeMap;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
	@Component
	public class DatasetOperator {
		
		@Autowired
		AttributeMapOperator attributeMapOperator;
		@Autowired
		RelationOperator relationOperator;
		@Autowired
		MapOperator mapOperator;
		@Autowired
		DataStoreServiceImpl datastoreServiceImpl;
		@Autowired
		private CommonServiceImpl<?> commonServiceImpl;		
		@Autowired
		FilterOperator2 filterOperator2;
		
		static final Logger logger = Logger.getLogger(DatasetOperator.class);
		
		public String generateSql(DataSet dataset, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
								Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception {
			logger.info("Started SQL Generation");
			String sql = generateSelect(dataset, refKeyMap, otherParams, execParams, runMode)
					.concat(getFrom())
					.concat(generateFrom(dataset, refKeyMap, otherParams, usedRefKeySet, runMode))
					.concat(generateWhere())
					.concat(generateFilter(dataset, refKeyMap, otherParams, usedRefKeySet, execParams, runMode))
					.concat(generateGroupBy(dataset, refKeyMap, otherParams, execParams))
					.concat(generateHaving(dataset, refKeyMap, otherParams, usedRefKeySet, execParams, runMode))
					.concat(generateLimit(dataset));
			logger.info("SQL Generated: "+sql);
			logger.info("Completed SQL Generation");
			return sql;
		}

		public String generateSelect(DataSet dataset, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams
									, ExecParams execParams, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
			// Create AttributeMap
			List<AttributeMap> attrMapList = new ArrayList<>();
			AttributeMap attrMap = null; 
			AttributeRefHolder sourceAttr = null;
			for (AttributeSource sourceAttribute : dataset.getAttributeInfo()) {
				sourceAttr = new AttributeRefHolder();
//				if (sourceAttribute.getSourceAttr().getRef().getType() == MetaType.datapod) {
//					sourceAttr.setAttrId(sourceAttribute.getSourceAttr().getAttrId());
//				} else {
//					sourceAttr.setAttrId(sourceAttribute.getAttrSourceId());
//				}
				sourceAttr.setAttrId(sourceAttribute.getSourceAttr().getAttrId());
				sourceAttr.setValue(sourceAttribute.getSourceAttr().getValue());
				sourceAttr.setAttrName(sourceAttribute.getAttrSourceName());
				sourceAttr.setRef(sourceAttribute.getSourceAttr().getRef());
				attrMap = new AttributeMap();
				attrMap.setSourceAttr(sourceAttr);
				attrMap.setAttrMapId(sourceAttribute.getAttrSourceId());
				//attrMap.setDesc(sourceAttribute.getName());
				attrMapList.add(attrMap);
			}
			attributeMapOperator.setRunMode(runMode);
			return ConstantsUtil.SELECT.concat(attributeMapOperator.generateSql(attrMapList, dataset.getDependsOn(), refKeyMap, otherParams, execParams));
		}
		
		public String getFrom() {
			return ConstantsUtil.FROM;
		}
	 	
		public String generateFrom(DataSet dataset, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
			StringBuilder builder = new StringBuilder();
			Relation relation = null;
	
			logger.info("otherParams in datasetOperator : " + otherParams);
			if (dataset.getDependsOn().getRef().getType() == MetaType.relation) {
				usedRefKeySet.add(dataset.getDependsOn().getRef());
//				relation = (Relation) daoRegister.getRefObject(dataset.getDependsOn().getRef());
				MetaIdentifier ref = TaskParser.populateRefVersion(dataset.getDependsOn().getRef(), refKeyMap);
				relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
				builder.append(relationOperator.generateSql(relation, refKeyMap, otherParams, null, usedRefKeySet, runMode));
			} else if (dataset.getDependsOn().getRef().getType() == MetaType.datapod) {
//				Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(dataset.getDependsOn().getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(dataset.getDependsOn().getRef(), refKeyMap);
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
				String table = null;
				/*if (otherParams == null 
						|| otherParams.get("datapod_".concat(datapod.getUuid())) == null) {*/
//					table = datastoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
				if (otherParams != null && otherParams.containsKey("datapodUuid_" + datapod.getUuid() + "_tableName")) {
					return otherParams.get("datapodUuid_" + datapod.getUuid() + "_tableName") + " " + datapod.getName();
				} else {
					try {
						table = datastoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
					} catch(Exception e) {
						table =  String.format("%s_%s_%s", datapod.getUuid().replaceAll("-", "_"), datapod.getVersion(), dataset.getVersion());
					}
				}
				/*} else {
					String tableKey = "datapod_".concat(datapod.getUuid());
					table = otherParams.get(tableKey);
				}*/
				logger.info("Source table in dataset " + dataset.getName() + " : " + table);
				builder.append(String.format(table, datapod.getName())).append("  ").append(datapod.getName()).append(" ");
			} else if (dataset.getDependsOn().getRef().getType() == MetaType.dataset) {
//				DataSet innerDS = (DataSet) daoRegister.getRefObject(dataset.getDependsOn().getRef()); 
                MetaIdentifier ref = TaskParser.populateRefVersion(dataset.getDependsOn().getRef(), refKeyMap);
                DataSet innerDS = (DataSet) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
                builder.append("(").append(generateSql(innerDS, refKeyMap, otherParams, usedRefKeySet, null, runMode)).append(") ").append(innerDS.getName()).append(" ");
            }
			return builder.toString();
		}
		
		public String generateWhere () {
			return ConstantsUtil.WHERE_1_1;
		}
		
		public String generateFilter (DataSet dataset, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception {
			if (dataset.getFilterInfo() != null && !dataset.getFilterInfo().isEmpty()) {
				MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.dataset, dataset.getUuid(), dataset.getVersion()));
				
				Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(dataset);
				String filterStr = filterOperator2.generateSql(dataset.getFilterInfo(), refKeyMap, filterSource, otherParams, usedRefKeySet, execParams, false, false, runMode, mapSourceDS);

				//String filterStr = filterOperator.generateSql(dataset.getFilterInfo(), refKeyMap, otherParams, usedRefKeySet, execParams, false, false, runMode);
				return StringUtils.isBlank(filterStr)?ConstantsUtil.BLANK : filterStr;
			}
			return ConstantsUtil.BLANK;
		} 
		
		public String generateGroupBy (DataSet dataset, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
			MetaIdentifierHolder datasetSource = new MetaIdentifierHolder(dataset.getRef(MetaType.dataset));
			return attributeMapOperator.selectGroupBy(attributeMapOperator.createAttrMap(dataset.getAttributeInfo()), refKeyMap, otherParams, execParams, datasetSource);
		}
		
		public String generateHaving (DataSet dataset, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception {
			if (dataset.getFilterInfo() != null && !dataset.getFilterInfo().isEmpty()) {
				MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.dataset, dataset.getUuid(), dataset.getVersion()));

				Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(dataset);
				String filterStr = filterOperator2.generateSql(dataset.getFilterInfo(), refKeyMap, filterSource, otherParams, usedRefKeySet, execParams, true, true, runMode, mapSourceDS);

//				String filterStr = filterOperator.generateSql(dataset.getFilterInfo(), refKeyMap, otherParams, usedRefKeySet, execParams, true, true, runMode);
				return StringUtils.isBlank(filterStr)?ConstantsUtil.BLANK : ConstantsUtil.HAVING_1_1.concat(filterStr);
			}
			return ConstantsUtil.BLANK;
		}

		private String generateLimit(DataSet dataset) {
			return (dataset.getLimit() > 0) ? (ConstantsUtil.LIMIT + dataset.getLimit() + " ") : "";
		}
		
		public String generateSelectDistinct(DataSet dataset, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams
				, ExecParams execParams, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
			List<AttributeMap> attrMapList = new ArrayList<>();
			AttributeMap attrMap = null; 
			AttributeRefHolder sourceAttr = null;
			for (AttributeSource sourceAttribute : dataset.getAttributeInfo()) {
				sourceAttr = new AttributeRefHolder();
				sourceAttr.setAttrId(sourceAttribute.getSourceAttr().getAttrId());
				sourceAttr.setValue(sourceAttribute.getSourceAttr().getValue());
				sourceAttr.setAttrName(sourceAttribute.getAttrSourceName());
				sourceAttr.setRef(sourceAttribute.getSourceAttr().getRef());
				attrMap = new AttributeMap();
				attrMap.setSourceAttr(sourceAttr);
				attrMap.setAttrMapId(sourceAttribute.getAttrSourceId());
				attrMapList.add(attrMap);
			}
			attributeMapOperator.setRunMode(runMode);
			return ConstantsUtil.SELECT
					.concat(" DISTINCT ")
					.concat("(")
					.concat(attributeMapOperator.generateSql(attrMapList, dataset.getDependsOn(), refKeyMap, otherParams, execParams))
					.concat(") ");
		}
}
	
	
	
	
	
	
	
	
	
	
	
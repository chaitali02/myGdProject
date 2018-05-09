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
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AttributeMap;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OperatorType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.DataStoreServiceImpl;
	@Component
	public class DatasetOperator implements Operator {
		
		@Autowired
		AttributeMapOperator attributeMapOperator;
		@Autowired
		RelationOperator relationOperator;
		@Autowired
		MetadataUtil daoRegister;
		@Autowired
		MapOperator mapOperator;
		@Autowired
		FilterOperator filterOperator;
		@Autowired
		DataStoreServiceImpl datastoreServiceImpl;
		
		public String generateSql(DataSet dataset, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
								Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception {
			return generateSelect(dataset, refKeyMap, otherParams, execParams, runMode)
					.concat(getFrom())
					.concat(generateFrom(dataset, refKeyMap, otherParams, usedRefKeySet, runMode))
					.concat(generateWhere())
					.concat(generateFilter(dataset, refKeyMap, otherParams, usedRefKeySet))
					.concat(generateGroupBy(dataset, refKeyMap, otherParams, execParams));
		}
		
		public String generateSelect(DataSet dataset, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams
									, ExecParams execParams, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
			// Create AttributeMap
			List<AttributeMap> attrMapList = new ArrayList<>();
			AttributeMap attrMap = null; 
			AttributeRefHolder sourceAttr = null;
			for (AttributeSource sourceAttribute : dataset.getAttributeInfo()) {
				sourceAttr = new AttributeRefHolder();
				if (sourceAttribute.getSourceAttr().getRef().getType() == MetaType.datapod) {
					sourceAttr.setAttrId(sourceAttribute.getSourceAttr().getAttrId());
				} else {
					sourceAttr.setAttrId(sourceAttribute.getAttrSourceId());
				}
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
			usedRefKeySet.add(dataset.getDependsOn().getRef());
			if (dataset.getDependsOn().getRef().getType() == MetaType.relation) {
				relation = (Relation) daoRegister.getRefObject(dataset.getDependsOn().getRef()); 
				builder.append(relationOperator.generateSql(relation, refKeyMap, otherParams, null, usedRefKeySet, runMode));
			} else if (dataset.getDependsOn().getRef().getType() == MetaType.datapod) {
				Datapod datapod = (Datapod) daoRegister
						.getRefObject(TaskParser.populateRefVersion(dataset.getDependsOn().getRef(), refKeyMap));
				String table = null;
				/*if (otherParams == null 
						|| otherParams.get("datapod_".concat(datapod.getUuid())) == null) {*/
					table = datastoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
				/*} else {
					String tableKey = "datapod_".concat(datapod.getUuid());
					table = otherParams.get(tableKey);
				}*/
				builder.append(String.format(table, datapod.getName())).append("  ").append(datapod.getName()).append(" ");
			}
			return builder.toString();
		}
		
		public String generateWhere () {
			return ConstantsUtil.WHERE_1_1;
		}
		
		public String generateFilter (DataSet dataset, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
			if (dataset.getFilterInfo() != null && !dataset.getFilterInfo().isEmpty()) {
				return filterOperator.generateSql(dataset.getFilterInfo(), refKeyMap, otherParams, usedRefKeySet);
			}
			return ConstantsUtil.BLANK;
		} 
		
		public String generateGroupBy (DataSet dataset, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
			return attributeMapOperator.selectGroupBy(attributeMapOperator.createAttrMap(dataset.getAttributeInfo()), refKeyMap, otherParams, execParams);
		}

		@Override
		public void execute(OperatorType operatorType, ExecParams execParams, MetaIdentifier execIdentifier,
				Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
			// TODO Auto-generated method stub
			
		}

}
	
	
	
	
	
	
	
	
	
	
	

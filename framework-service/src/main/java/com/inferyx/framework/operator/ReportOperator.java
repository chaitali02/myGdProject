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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AttributeMap;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Report;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.DataStoreServiceImpl;

/**
 * @author Ganesh
 *
 */
public class ReportOperator implements IOperator {

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
	@Autowired
	DatasetOperator datasetOperator;
	
	static final Logger logger = Logger.getLogger(ReportOperator.class);
	
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String generateSql(Report report, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception {
		return generateSelect(report, refKeyMap, otherParams, execParams, runMode)
		.concat(getFrom())
		.concat(generateFrom(report, refKeyMap, otherParams, usedRefKeySet, execParams, runMode))
		.concat(generateWhere())
		.concat(generateFilter(report, refKeyMap, otherParams, usedRefKeySet, execParams))
		.concat(generateGroupBy(report, refKeyMap, otherParams, execParams));
	}

	private String generateSelect(Report report, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, ExecParams execParams, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// Create AttributeMap
		List<AttributeMap> attrMapList = new ArrayList<>();
		AttributeMap attrMap = null; 
		AttributeRefHolder sourceAttr = null;
		for (AttributeSource sourceAttribute : report.getAttributeInfo()) {
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
		return ConstantsUtil.SELECT.concat(attributeMapOperator.generateSql(attrMapList, report.getDependsOn(), refKeyMap, otherParams, execParams));
	}

	public String getFrom() {
		return ConstantsUtil.FROM;
	}

	private String generateFrom(Report report, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception {
		StringBuilder builder = new StringBuilder();
		Relation relation = null;

		logger.info("otherParams in reportOperator : " + otherParams);
		if (report.getDependsOn().getRef().getType() == MetaType.relation) {
			usedRefKeySet.add(report.getDependsOn().getRef());
			relation = (Relation) daoRegister.getRefObject(report.getDependsOn().getRef()); 
			builder.append(relationOperator.generateSql(relation, refKeyMap, otherParams, null, usedRefKeySet, runMode));
		} else if (report.getDependsOn().getRef().getType() == MetaType.datapod) {
			Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(report.getDependsOn().getRef(), refKeyMap));
			String table = null;
			if (otherParams != null && otherParams.containsKey("datapodUuid_" + datapod.getUuid() + "_tableName")) {
				return otherParams.get("datapodUuid_" + datapod.getUuid() + "_tableName") + " " + datapod.getName();
			} else {
				try {
					table = datastoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
				} catch(Exception e) {
					table =  String.format("%s_%s_%s", datapod.getUuid().replaceAll("-", "_"), datapod.getVersion(), report.getVersion());
				}
			}
			logger.info("Source table in report >> " + report.getName() + " : " + table);
			builder.append(String.format(table, datapod.getName())).append("  ").append(datapod.getName()).append(" ");
		} else if (report.getDependsOn().getRef().getType() == MetaType.dataset) {
            DataSet dataset = (DataSet) daoRegister.getRefObject(report.getDependsOn().getRef()); 
            builder.append(datasetOperator.generateSql(dataset, refKeyMap, otherParams, usedRefKeySet, execParams, runMode));
        }
		return builder.toString();
	}

	public String generateWhere () {
		return ConstantsUtil.WHERE_1_1;
	}

	private String generateFilter(Report report, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if (execParams.getFilterInfo() != null && !execParams.getFilterInfo().isEmpty()) {
			return filterOperator.generateSql(execParams.getFilterInfo(), refKeyMap, otherParams, usedRefKeySet, execParams);
		}
		return ConstantsUtil.BLANK;
	}

	private String generateGroupBy(Report report, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return attributeMapOperator.selectGroupBy(attributeMapOperator.createAttrMap(report.getAttributeInfo()), refKeyMap, otherParams, execParams);
	}
}

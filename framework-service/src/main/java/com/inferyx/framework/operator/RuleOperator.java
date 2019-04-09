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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.domain.AttributeMap;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;

@Component
public class RuleOperator implements IParsable, IReferenceable {
	
	@Autowired
	AttributeMapOperator attributeMapOperator;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	RelationOperator relationOperator;
	@Autowired
	DatasetOperator datasetOperator;
	@Autowired
	DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	FilterOperator2 filterOperator2;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	
	
	static final Logger logger = Logger.getLogger(RuleOperator.class);
	
	public String generateSql(Rule rule, java.util.Map<String, MetaIdentifier> refKeyMap,HashMap<String, String> otherParams, 
								Set<MetaIdentifier> usedRefKeySet,	ExecParams execParams, RunMode runMode
								, Map<String, String> paramValMap) throws Exception {
		String sql = generateSelect(rule, refKeyMap, otherParams, execParams, runMode, paramValMap)
				.concat(getFrom())
				.concat(generateFrom(rule, refKeyMap, otherParams, usedRefKeySet, execParams, runMode, paramValMap))
				.concat(generateWhere())
				.concat(generateFilter(rule, refKeyMap, otherParams, usedRefKeySet, execParams, runMode, paramValMap))
				.concat(selectGroupBy(rule, refKeyMap, otherParams, execParams, paramValMap))
				.concat(generateHaving(rule, refKeyMap, otherParams, usedRefKeySet, execParams, runMode, paramValMap));
		return sql;
	}
	
	private List<AttributeMap> createAttrMap (Rule rule, java.util.Map<String, MetaIdentifier> refKeyMap) {
		// Create AttributeMap
		List<AttributeMap> attrMapList = new ArrayList<>();
		AttributeMap attrMap = null; 
		AttributeRefHolder sourceAttr = null;
		for (AttributeSource sourceAttribute : rule.getAttributeInfo()) {
			sourceAttr = new AttributeRefHolder();
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
		return attrMapList;
	}
	
	public String generateSelect(Rule rule, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams, RunMode runMode
			, Map<String, String> paramValMap) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// return ConstantsUtil.SELECT.concat("row_number() over (partition by 1) as rownum, ").concat(attributeMapOperator.generateSql(createAttrMap (rule, refKeyMap), rule.getSource(), refKeyMap, null));
		attributeMapOperator.setRunMode(runMode);
		return ConstantsUtil.SELECT.concat(attributeMapOperator.generateSql(createAttrMap (rule, refKeyMap), rule.getSource(), refKeyMap, otherParams, execParams, paramValMap));
	}
	
	public String getFrom() {
		return ConstantsUtil.FROM;
	}
 	
	public String generateFrom(Rule rule, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
								Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode, Map<String, String> paramValMap) throws Exception {
		StringBuilder builder = new StringBuilder();
		Relation relation = null;
		usedRefKeySet.add(rule.getSource().getRef());
		if (rule.getSource().getRef().getType() == MetaType.relation) {
//			relation = (Relation) daoRegister.getRefObject(rule.getSource().getRef()); 
			relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(rule.getSource().getRef().getUuid(), rule.getSource().getRef().getVersion(), rule.getSource().getRef().getType().toString(), "N");
			builder.append(relationOperator.generateSql(relation, refKeyMap, otherParams, null, usedRefKeySet, runMode, paramValMap));
		} else if (rule.getSource().getRef().getType() == MetaType.datapod) {
//			Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(rule.getSource().getRef(), refKeyMap));
			MetaIdentifier ref = TaskParser.populateRefVersion(rule.getSource().getRef(), refKeyMap);
			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
			
			String table = null;
			if (otherParams == null	|| otherParams.get("datapod_".concat(datapod.getUuid())) == null) {
				table = datapodServiceImpl.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			} else {
				String tableKey = "datapod_".concat(datapod.getUuid());
				table = otherParams.get(tableKey);
			}
			builder.append(String.format(table, datapod.getName())).append("  ").append(datapod.getName()).append(" ");
		} else if (rule.getSource().getRef().getType() == MetaType.dataset) {
//			DataSet dataset = (DataSet) daoRegister.getRefObject(rule.getSource().getRef());
			DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(rule.getSource().getRef().getUuid(), rule.getSource().getRef().getVersion(), rule.getSource().getRef().getType().toString(), "N");
			
			builder.append("(").append(datasetOperator.generateSql(dataset, refKeyMap, otherParams, usedRefKeySet, execParams, runMode, paramValMap)).append(")  ").append(dataset.getName());
		} else if (rule.getSource().getRef().getType() == MetaType.rule) {
//			Rule innerRule = (Rule) daoRegister.getRefObject(rule.getSource().getRef());
			Rule innerRule = (Rule) commonServiceImpl.getOneByUuidAndVersion(rule.getSource().getRef().getUuid(), rule.getSource().getRef().getVersion(), rule.getSource().getRef().getType().toString(), "N");
			
			builder.append("(").append(generateSql(innerRule, refKeyMap, otherParams, usedRefKeySet, execParams, runMode, paramValMap)).append(")  ").append(innerRule.getName());
		}
		return builder.toString();
	}
	
	public String generateWhere () {
		return ConstantsUtil.WHERE_1_1;
	}
	
	public String selectGroupBy (Rule rule, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams
			, Map<String, String> paramValMap) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		MetaIdentifierHolder ruleSource = new MetaIdentifierHolder(rule.getRef(MetaType.rule));
		return attributeMapOperator.selectGroupBy(attributeMapOperator.createAttrMap(rule.getAttributeInfo()), refKeyMap, otherParams, execParams, ruleSource, paramValMap);
	}
	
	public String generateFilter (Rule rule, 
									java.util.Map<String, MetaIdentifier> refKeyMap, 
									HashMap<String, String> otherParams, 
									Set<MetaIdentifier> usedRefKeySet, 
									ExecParams execParams, RunMode runMode, 
									Map<String, String> paramValMap) throws Exception {
		if (rule.getFilterInfo() != null && !rule.getFilterInfo().isEmpty()) {
			MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.rule, rule.getUuid(), rule.getVersion()));
			Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(rule);
			String filter = filterOperator2.generateSql(rule.getFilterInfo(), refKeyMap, filterSource, otherParams, usedRefKeySet, execParams, false, false, runMode, mapSourceDS, paramValMap);
			return filter;
		}
		return ConstantsUtil.BLANK;
	}
	
	public String generateHaving (Rule rule, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode
			, Map<String, String> paramValMap) throws Exception {
		if (rule.getFilterInfo() != null && !rule.getFilterInfo().isEmpty()) {
			MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.rule, rule.getUuid(), rule.getVersion()));
			Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(rule);
			String filterStr = filterOperator2.generateSql(rule.getFilterInfo(), refKeyMap, filterSource, otherParams, usedRefKeySet, execParams, true, true, runMode, mapSourceDS, paramValMap);
			return StringUtils.isBlank(filterStr) ? ConstantsUtil.BLANK : ConstantsUtil.HAVING_1_1.concat(filterStr);
		}
		return ConstantsUtil.BLANK;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		Rule rule = null;
		/***************  Initializing paramValMap - START ****************/
		Map<String, String> paramValMap = null;
		if (execParams.getParamValMap() == null) {
			execParams.setParamValMap(new HashMap<String, Map<String, String>>());
		}
		if (!execParams.getParamValMap().containsKey(baseExec.getUuid())) {
			execParams.getParamValMap().put(baseExec.getUuid(), new HashMap<String, String>());
		}
		paramValMap = execParams.getParamValMap().get(baseExec.getUuid());
		/***************  Initializing paramValMap - END ****************/
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		// List<Status> statusList = null;
		RuleExec ruleExec = (RuleExec) baseExec;
		// rule = iRuleDao.findLatestByUuid(ruleExec.getDependsOn().getRef().getUuid(),
		// new Sort(Sort.Direction.DESC, "version"));
		rule = (Rule) commonServiceImpl.getLatestByUuid(ruleExec.getDependsOn().getRef().getUuid(),
				MetaType.rule.toString());
		ruleExec.setExec(generateSql(rule, DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(), usedRefKeySet, ruleExec.getExecParams(), runMode, paramValMap));
		if(rule.getParamList() != null) {
			MetaIdentifier mi = rule.getParamList().getRef();
			ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(mi.getUuid(), mi.getVersion(), mi.getType().toString());
			usedRefKeySet.add(new MetaIdentifier(MetaType.paramlist, paramList.getUuid(), paramList.getVersion()));
		}
		ruleExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		logger.info("sql_generated: " + ruleExec.getExec());
		synchronized (ruleExec.getUuid()) {
			RuleExec ruleExec1 = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(baseExec.getUuid(), baseExec.getVersion(),
					MetaType.ruleExec.toString());
			ruleExec1.setExec(ruleExec.getExec());
			ruleExec1.setRefKeyList(ruleExec.getRefKeyList());
			// iRuleExecDao.save(ruleExec1);
			commonServiceImpl.save(MetaType.ruleExec.toString(), ruleExec1);
			ruleExec1 = null;
		}
		return ruleExec;
	}

	@Override
	public MetaIdentifier populateRefKeys(Map<String, MetaIdentifier> refKeyMap, MetaIdentifier ref,
			Map<String, MetaIdentifier> inputRefKeyMap) {
		// TODO Auto-generated method stub
		return null;
	}

}

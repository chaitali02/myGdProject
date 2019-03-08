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
import com.inferyx.framework.domain.Criteria;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Key;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.Rule2;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;

@Component
public class Rule2Operator implements IParsable, IReferenceable {
	
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
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	FilterOperator2 filterOperator2;
	
	
	static final Logger logger = Logger.getLogger(Rule2Operator.class);
	
/*	public String generateSql(Rule2 rule2, java.util.Map<String, MetaIdentifier> refKeyMap,HashMap<String, String> otherParams, 
								Set<MetaIdentifier> usedRefKeySet,	ExecParams execParams, RunMode runMode) throws Exception {
		String sql = generateSelect(rule2, refKeyMap, otherParams, execParams, runMode)
				.concat(getFrom())
				.concat(generateFrom(rule2, refKeyMap, otherParams, usedRefKeySet, execParams, runMode))
				.concat(generateWhere())
				.concat(generateFilter(rule2, refKeyMap, otherParams, usedRefKeySet, execParams, runMode))
				.concat(selectGroupBy(rule2, refKeyMap, otherParams, execParams))
				.concat(generateHaving(rule2, refKeyMap, otherParams, usedRefKeySet, execParams, runMode));
		System.out.println(sql);
		return null;
	}
	*/
	public String generateSql(Rule2 rule2, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception{	
		// TODO Auto-generated method stub
		String sql = generateSelect(rule2, refKeyMap, otherParams, execParams, runMode).concat(getFrom())
				.concat(generateFrom(rule2, refKeyMap, otherParams, usedRefKeySet, execParams, runMode))
				.concat(generateWhere())
				.concat(generateFilter(rule2, refKeyMap, otherParams, usedRefKeySet, execParams, runMode));
		System.out.println(sql);
		return sql;
	}
	
	
	private List<AttributeMap> createAttrMap (Rule2 rule2, java.util.Map<String, MetaIdentifier> refKeyMap) {
		// Create AttributeMap
		List<AttributeMap> attrMapList = new ArrayList<>();
		AttributeMap attrMap = null; 
		AttributeRefHolder sourceAttr = null;
	/*	for (AttributeSource sourceAttribute : rule.getAttributeInfo()) {
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
		}*/
		return attrMapList;
	}
	
	public String generateSelect(Rule2 rule2, java.util.Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, ExecParams execParams, RunMode runMode) throws Exception {
		// return ConstantsUtil.SELECT.concat("row_number() over (partition by 1) as
		// rownum, ").concat(attributeMapOperator.generateSql(createAttrMap (rule,
		// refKeyMap), rule.getSource(), refKeyMap, null));
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		String result = null;
		StringBuilder selectbuilder = new StringBuilder();
		StringBuilder withbuilder = new StringBuilder();
		int criteria_id = 1;
		String comma = ", ";
		MetaIdentifierHolder filterSource = new MetaIdentifierHolder(
				new MetaIdentifier(MetaType.rule2, rule2.getUuid(), rule2.getVersion()));
		
		AttributeRefHolder attributeRefHolder = rule2.getEntityId();
		String attrName = null;
		String tablename = null;
		String aliasName = null;
		StringBuilder criteria_indBuilder = new StringBuilder();
		switch (attributeRefHolder.getRef().getType()) {
		case datapod:
			Datapod dp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(attributeRefHolder.getRef().getUuid(),
					attributeRefHolder.getRef().getVersion(), MetaType.datapod.toString());
			attrName = datapodServiceImpl.getAttributeName(attributeRefHolder.getRef().getUuid(),
					Integer.parseInt(attributeRefHolder.getAttrId()));
			tablename = dp.getName();
			
			 aliasName = datastoreServiceImpl.getTableNameByDatapod(new Key(dp.getUuid(), dp.getVersion()), runMode);
			break;
		case dataset:
			DataSet ds = (DataSet) commonServiceImpl.getOneByUuidAndVersion(attributeRefHolder.getRef().getUuid(),
					attributeRefHolder.getRef().getVersion(), MetaType.datapod.toString());
			tablename = ds.getName();
			break;

		case relation:
			Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(
					attributeRefHolder.getRef().getUuid(), attributeRefHolder.getRef().getVersion(),
					MetaType.datapod.toString());
			tablename = relation.getName();
			break;

		}
		Datasource mapSourceDS = commonServiceImpl.getDatasourceByObject(rule2);
		
		withbuilder.append("WITH rule_with_query AS\n" + "(").append(ConstantsUtil.SELECT).append(" * ")
				.append(ConstantsUtil.FROM).append(aliasName).append(" ").append(ConstantsUtil.WHERE_1_1).append(" ");
		
		for (Criteria criteria : rule2.getCriteriaInfo()) {
			attributeMapOperator.setRunMode(runMode);
		
			selectbuilder.append(ConstantsUtil.SELECT);

			selectbuilder.append("'").append(rule2.getName()).append("'").append(" as ").append(" rule_name")
					.append(comma);

			selectbuilder.append("'").append(rule2.getEntityType()).append("'").append(" as ").append(" entity_type")
					.append(comma);

			selectbuilder.append("rule_with_query".concat("." + attrName) ).append(" as ")
					.append(" entity_id").append(comma);

			selectbuilder.append("'").append(criteria_id).append("'").append(" as ").append(" criteria_id")
					.append(comma);

			selectbuilder.append("'").append(criteria.getCriteriaName()).append("'").append(" as ")
					.append(" criteria_name").append(comma);

			String filter = filterOperator2.generateSql(criteria.getCriteriaFilter(), refKeyMap, filterSource,
					otherParams, usedRefKeySet, execParams, false, false, runMode, mapSourceDS);

			filter = filter.replaceAll(tablename, "rule_with_query");
			criteria_indBuilder.append("CASE WHEN ").append(ConstantsUtil.WHERE_1_1).append(filter)
					.append(" THEN 'PASS' ELSE 'FAIL' END ");
			selectbuilder.append(criteria_indBuilder.toString()).append(" as ").append(" criteria_ind").append(comma);
			
			selectbuilder.append(rule2.getVersion()).append(" as ").append(" version").append(" ")
					.append(ConstantsUtil.FROM).append(" rule_with_query  ");

			if(rule2.getCriteriaInfo().size()!=criteria_id)
			selectbuilder.append(ConstantsUtil.UNION_ALL);
			
			
			criteria_id++;

		}
		result.concat(selectbuilder.toString());
		return result;

	}
	
	public String getFrom() {
		return ConstantsUtil.FROM;
	}
 	
	public String generateFrom(Rule2 rule2, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
								Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception {
		StringBuilder builder = new StringBuilder();
		Relation relation = null;

		usedRefKeySet.add(rule2.getSourceInfo().getRef());
		if (rule2.getSourceInfo().getRef().getType() == MetaType.relation) {
//			relation = (Relation) daoRegister.getRefObject(rule.getSource().getRef()); 
			relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(rule2.getSourceInfo().getRef().getUuid(), rule2.getSourceInfo().getRef().getVersion(), rule2.getSourceInfo().getRef().getType().toString(), "N");
			builder.append(relationOperator.generateSql(relation, refKeyMap, otherParams, null, usedRefKeySet, runMode));
		} else if (rule2.getSourceInfo().getRef().getType() == MetaType.datapod) {
//			Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(rule.getSource().getRef(), refKeyMap));
			MetaIdentifier ref = TaskParser.populateRefVersion(rule2.getSourceInfo().getRef(), refKeyMap);
			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
			
			String table = null;
			if (otherParams == null	|| otherParams.get("datapod_".concat(datapod.getUuid())) == null) {
				table = datastoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			} else {
				String tableKey = "datapod_".concat(datapod.getUuid());
				table = otherParams.get(tableKey);
			}
			builder.append(String.format(table, datapod.getName())).append("  ").append(datapod.getName()).append(" ");
		} else if (rule2.getSourceInfo().getRef().getType() == MetaType.dataset) {
//			DataSet dataset = (DataSet) daoRegister.getRefObject(rule.getSource().getRef());
			DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(rule2.getSourceInfo().getRef().getUuid(), rule2.getSourceInfo().getRef().getVersion(), rule2.getSourceInfo().getRef().getType().toString(), "N");
			
			builder.append("(").append(datasetOperator.generateSql(dataset, refKeyMap, otherParams, usedRefKeySet, execParams, runMode)).append(")  ").append(dataset.getName());
		} 
		return builder.toString();
	}
	
	public String generateWhere () {
		return ConstantsUtil.WHERE_1_1;
	}
	
	public String selectGroupBy (Rule2 rule2, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		MetaIdentifierHolder ruleSource = new MetaIdentifierHolder(rule2.getRef(MetaType.rule2));
	
		return null;
	//	commented by vaibhav
	//	return attributeMapOperator.selectGroupBy(attributeMapOperator.createAttrMap(rule.getAttributeInfo()), refKeyMap, otherParams, execParams, ruleSource);
	}
	
	public String generateFilter (Rule2 rule2, 
									java.util.Map<String, MetaIdentifier> refKeyMap, 
									HashMap<String, String> otherParams, 
									Set<MetaIdentifier> usedRefKeySet, 
									ExecParams execParams, RunMode runMode) throws Exception {
		if (rule2.getFilterInfo() != null && !rule2.getFilterInfo().isEmpty()) {
			MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.rule2, rule2.getUuid(), rule2.getVersion()));
			Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(rule2);
			String filter = filterOperator2.generateSql(rule2.getFilterInfo(), refKeyMap, filterSource, otherParams, usedRefKeySet, execParams, false, false, runMode, mapSourceDS);
			return filter;
		}
		return ConstantsUtil.BLANK;
	}
	
	public String generateHaving (Rule2 rule2, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception {
		if (rule2.getFilterInfo() != null && !rule2.getFilterInfo().isEmpty()) {
			MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.rule2, rule2.getUuid(), rule2.getVersion()));
			Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(rule2);
			String filterStr = filterOperator2.generateSql(rule2.getFilterInfo(), refKeyMap, filterSource, otherParams, usedRefKeySet, execParams, true, true, runMode, mapSourceDS);
			return StringUtils.isBlank(filterStr) ? ConstantsUtil.BLANK : ConstantsUtil.HAVING_1_1.concat(filterStr);
		}
		return ConstantsUtil.BLANK;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		Rule2 rule2 = null;
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		// List<Status> statusList = null;
		RuleExec ruleExec = (RuleExec) baseExec;
		// rule = iRuleDao.findLatestByUuid(ruleExec.getDependsOn().getRef().getUuid(),
		// new Sort(Sort.Direction.DESC, "version"));
		rule2 = (Rule2) commonServiceImpl.getLatestByUuid(ruleExec.getDependsOn().getRef().getUuid(),
				MetaType.rule2.toString());
		
	
		ruleExec.setExec(generateSql(rule2, DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(), usedRefKeySet, ruleExec.getExecParams(), runMode));
		if(rule2.getParamList() != null) {
			MetaIdentifier mi = rule2.getParamList().getRef();
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

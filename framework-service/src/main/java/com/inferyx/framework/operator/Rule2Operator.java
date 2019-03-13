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
import com.inferyx.framework.service.DatasetServiceImpl;

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
	DatasetServiceImpl datasetServiceImpl;
	@Autowired
	FilterOperator2 filterOperator2;
	
	
	
	
	static final Logger logger = Logger.getLogger(Rule2Operator.class);
	

	public List<String> generateDetailSql(Rule2 rule2, String withSql, String detailSelectSql, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode, RuleExec ruleExec) throws Exception{	
		// TODO Auto-generated method stub
		return  generateWith(rule2,withSql,detailSelectSql, refKeyMap, otherParams, execParams, runMode,ruleExec);
	}
	
	public String generateSummarySql(Rule2 rule2, List<String> listSql, String tableName, Datapod datapod, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception{	
		// TODO Auto-generated method stub
		String sql = generateSql(tableName,listSql,rule2.getVersion(),datapod);
		return sql;
	}
	
	
	private String generateSql(String tableName,List<String> listSql, String rule2Version, Datapod datapod) {
		String result = "";
		String withSql=listSql.get(2);
		String detailSelectSql=listSql.get(1);
		StringBuilder querybuilder = new StringBuilder();
		querybuilder.append(withSql);
		querybuilder.append(ConstantsUtil.SELECT);
		querybuilder.append("rule_uuid").append(" as ").append("rule_uuid").append(ConstantsUtil.COMMA);
		querybuilder.append(rule2Version).append(" as ").append("rule_version").append(ConstantsUtil.COMMA);
		querybuilder.append("rule_name").append(" as ").append("rule_name").append(ConstantsUtil.COMMA);
		querybuilder.append("entity_type").append(" as ").append("entity_type").append(ConstantsUtil.COMMA);
		querybuilder.append("entity_id").append(" as ").append("entity_id").append(ConstantsUtil.COMMA);
		querybuilder.append("sum(criteria_score)").append(" as ").append("score").append(ConstantsUtil.COMMA);
		querybuilder.append("version").append(" as ").append("version");
		querybuilder.append(ConstantsUtil.FROM);
		querybuilder.append(detailSelectSql + " rule_select_query");
		querybuilder.append(ConstantsUtil.GROUP_BY);
		querybuilder.append("rule_uuid").append(ConstantsUtil.COMMA);
		querybuilder.append("rule_version").append(ConstantsUtil.COMMA);
		querybuilder.append("rule_name").append(ConstantsUtil.COMMA);
		querybuilder.append("entity_type").append(ConstantsUtil.COMMA);
		querybuilder.append("entity_id").append(ConstantsUtil.COMMA);
		querybuilder.append("version");

		result = querybuilder.toString();
		
		
		
		
		return result;
	}

	
	public List<String> generateWith(Rule2 rule2, String withSql, String detailSelectSql, java.util.Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, ExecParams execParams, RunMode runMode, RuleExec ruleExec) throws Exception {
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		String result = "";
		StringBuilder selectbuilder = new StringBuilder();
		StringBuilder withbuilder = new StringBuilder();
		int criteria_id = 0;
		MetaIdentifierHolder filterSource = new MetaIdentifierHolder(
				new MetaIdentifier(MetaType.rule2, rule2.getUuid(), rule2.getVersion()));
		List<String> attributeList = new ArrayList<String>();
		AttributeRefHolder attributeRefHolder = rule2.getEntityId();
		String entityAttrName = null;
		String tablename = null;
		String aliasName = null;
		String filter = "";
		switch (attributeRefHolder.getRef().getType()) {
		case datapod:
			Datapod dp = (Datapod) commonServiceImpl.getLatestByUuid(attributeRefHolder.getRef().getUuid(),
					MetaType.datapod.toString());
			entityAttrName = datapodServiceImpl.getAttributeName(attributeRefHolder.getRef().getUuid(),
					Integer.parseInt(attributeRefHolder.getAttrId()));
			tablename = dp.getName();
			aliasName = datastoreServiceImpl.getTableNameByDatapod(new Key(dp.getUuid(), dp.getVersion()), runMode);
			break;
		case dataset:
			DataSet ds = (DataSet) commonServiceImpl.getLatestByUuid(attributeRefHolder.getRef().getUuid(),
					MetaType.datapod.toString());
			tablename = ds.getName();
			break;
		case relation:
			Relation relation = (Relation) commonServiceImpl.getLatestByUuid(attributeRefHolder.getRef().getUuid(),
					MetaType.datapod.toString());
			if (attributeRefHolder.getRef().getType().equals(MetaType.datapod))
				entityAttrName = datapodServiceImpl.getAttributeName(attributeRefHolder.getRef().getUuid(),
						Integer.parseInt(attributeRefHolder.getAttrId()));
			else if (attributeRefHolder.getRef().getType().equals(MetaType.dataset)) {
				DataSet rel_ds = (DataSet) commonServiceImpl.getLatestByUuid(attributeRefHolder.getRef().getUuid(),
						MetaType.datapod.toString());
				entityAttrName = datasetServiceImpl.getAttributeName(rel_ds, attributeRefHolder.getAttrId());
			}
			tablename = relation.getName();
			break;
		default:
			break;

		}
		Datasource mapSourceDS = commonServiceImpl.getDatasourceByObject(rule2);

		withbuilder.append("WITH rule_with_query AS\n" + "(").append(ConstantsUtil.SELECT).append("_attrList")
				.append(ConstantsUtil.FROM).append(aliasName + " " + tablename)
				.append(ConstantsUtil.WHERE_1_1).append(")");
		selectbuilder.append("(");
		for (Criteria criteria : rule2.getCriteriaInfo()) {
			
			criteria_id++;			
			if (criteria.getActiveFlag().equalsIgnoreCase("N")) {
					continue;
			}
			
			StringBuilder criteria_indBuilder = new StringBuilder();
			StringBuilder criteria_scoreBuilder = new StringBuilder();

			attributeMapOperator.setRunMode(runMode);

			selectbuilder.append(ConstantsUtil.SELECT);

			selectbuilder.append("'").append(rule2.getUuid()).append("'").append(" as ").append("rule_uuid")
					.append(ConstantsUtil.COMMA);

			selectbuilder.append("'").append(rule2.getVersion()).append("'").append(" as ").append("rule_version")
					.append(ConstantsUtil.COMMA);

			selectbuilder.append("'").append(rule2.getName()).append("'").append(" as ").append("rule_name")
					.append(ConstantsUtil.COMMA);

			selectbuilder.append("'").append(rule2.getEntityType()).append("'").append(" as ").append("entity_type")
					.append(ConstantsUtil.COMMA);

			selectbuilder.append("rule_with_query".concat("." + entityAttrName)).append(" as ").append("entity_id")
					.append(ConstantsUtil.COMMA);

			selectbuilder.append("'").append(criteria_id).append("'").append(" as ").append("criteria_id")
					.append(ConstantsUtil.COMMA);

			selectbuilder.append("'").append(criteria.getCriteriaName()).append("'").append(" as ")
					.append("criteria_name").append(ConstantsUtil.COMMA);			
			
			//Calculating criteria_met_ind
			filter = filterOperator2.generateSql(criteria.getCriteriaFilter(), refKeyMap, filterSource, otherParams,
					usedRefKeySet, execParams, false, false, runMode, mapSourceDS);

			filter = filter.replaceAll(tablename, "rule_with_query");
			criteria_indBuilder.append(ConstantsUtil.CASE_WHEN).append("1=1").append(filter)
					.append(" THEN 'PASS' ELSE 'FAIL' END ");
			selectbuilder.append(criteria_indBuilder.toString()).append(" as ").append("criteria_met_ind")
					.append(ConstantsUtil.COMMA);
			
			//Calculating criteria_expr
			filter = "";
			filter = filterOperator2.generateExprSql(criteria.getCriteriaFilter(), refKeyMap, filterSource, otherParams,
					usedRefKeySet, execParams, false, false, runMode, mapSourceDS, attributeList);
			filter = filter.replaceAll(tablename, "rule_with_query");
			filter = filter.replaceAll("'", "''");

			selectbuilder.append(filter).append(" as ").append("criteria_expr").append(ConstantsUtil.COMMA);
			
			//Calculating criteria_score   
			//(CASE WHEN 1=1 filter THEN 1 ELSE 0 END) * criteriaWeight  as  criteria_score
			filter = "";
			filter = filterOperator2.generateSql(criteria.getCriteriaFilter(), refKeyMap, filterSource, otherParams,
					usedRefKeySet, execParams, false, false, runMode, mapSourceDS);

			filter = filter.replaceAll(tablename, "rule_with_query");
			criteria_scoreBuilder.append("(").append(ConstantsUtil.CASE_WHEN).append("1=1").append(filter)
					.append(" THEN 1 ELSE 0 END) * ").append(criteria.getCriteriaWeight());
			filter = "";
			selectbuilder.append(criteria_scoreBuilder.toString()).append(" as ").append("criteria_score")
					.append(ConstantsUtil.COMMA);			
						
	
			selectbuilder.append(ruleExec.getVersion()).append(" as ").append("version").append(" ")
					.append(ConstantsUtil.FROM).append("rule_with_query");

			if (rule2.getCriteriaInfo().size() != criteria_id)
				selectbuilder.append(ConstantsUtil.UNION_ALL);

		}
		selectbuilder.append(" ) ");
		Set<String> attributeSet = new HashSet<String>(); 
		attributeSet.addAll(attributeList);
		StringBuilder attrListBuilder = new StringBuilder();
		//attrListBuilder.append(tablename + "." + entityAttrName);
		
		attributeSet.add(tablename + "." + entityAttrName);
		for (String attrList : attributeSet){
			
			attrListBuilder.append(ConstantsUtil.COMMA).append(attrList);
		}
		List<String> listSql=new ArrayList<String>();
		String withbuilder_new = withbuilder.toString().replaceAll("_attrList", attrListBuilder.toString().substring(1, attrListBuilder.length()));
		withSql=withbuilder_new;
		detailSelectSql=selectbuilder.toString();
		result = result.concat(withbuilder_new + selectbuilder.toString());
		listSql.add(result);
		listSql.add(detailSelectSql);
		listSql.add(withSql);

		return listSql;

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
		
	
		ruleExec.setExec(generateDetailSql(rule2, null, null, DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(), usedRefKeySet, ruleExec.getExecParams(), runMode, ruleExec).get(0));
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

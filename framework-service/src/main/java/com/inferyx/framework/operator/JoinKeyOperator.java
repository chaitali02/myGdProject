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
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FilterInfo;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.FormulaType;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.SourceAttr;
import com.inferyx.framework.enums.OperandType;
import com.inferyx.framework.enums.OperatorType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.MetadataServiceImpl;
import com.inferyx.framework.service.RegisterService;

@Component
public class JoinKeyOperator {
	Logger logger=Logger.getLogger(JoinKeyOperator.class);
	@Autowired protected MetadataUtil daoRegister;
	@Autowired protected FormulaOperator formulaOperator;
	@Autowired protected RegisterService registerService;
	@Autowired MetadataServiceImpl metadataServiceImpl;
	@Autowired protected FunctionOperator functionOperator;
	@Autowired protected CommonServiceImpl<?> commonServiceImpl;
	@Autowired protected DatasetOperator datasetOperator;
	
	public String generateSql(List<FilterInfo> filters, MetaIdentifierHolder filterSource
			, java.util.Map<String, MetaIdentifier> refKeyMap
			, HashMap<String, String> otherParams
			, Set<MetaIdentifier> usedRefKeySet
			, ExecParams execParams
			, Boolean isAggrAllowed
			, Boolean isAggrReqd
			, RunMode runMode, Datasource datasource) throws Exception {
		StringBuilder builder = new StringBuilder();
		StringBuilder filterBuilder = new StringBuilder("");
		builder.append("(").append(" ");

		if (filters == null) {
			return "";
		}

		for (FilterInfo filterInfo : filters) {
			String filter = generateSql(filterInfo, filterSource, refKeyMap, otherParams, usedRefKeySet, execParams, isAggrAllowed, isAggrReqd, runMode, datasource);
			if (StringUtils.isNotBlank(filter)) {
				if (StringUtils.isNotBlank(filterBuilder)) {
					filterBuilder.append(" ").append(filterInfo.getLogicalOperator()).append(" ");
				}
				filterBuilder.append(filter).append(" ");
			} 
		}
		if (StringUtils.isNotBlank(filterBuilder.toString())) {
			builder.append(filterBuilder);
			builder.append(")");
		} else {
			return ConstantsUtil.BLANK;
		}

		logger.info(String.format("Final filter %s", builder.toString()));
		return builder.toString();
	}
	
	
	private String generateSql(FilterInfo filterInfo, MetaIdentifierHolder filterSource
			, java.util.Map<String, MetaIdentifier> refKeyMap
			, HashMap<String, String> otherParams
			, Set<MetaIdentifier> usedRefKeySet
			, ExecParams execParams
			, Boolean isAggrAllowed
			, Boolean isAggrReqd
			, RunMode runMode, Datasource datasource) throws Exception {
		List<String> operandValue = new ArrayList<>(2);
		int aggrCount = 0;
		for (SourceAttr sourceAttr : filterInfo.getOperand()) {
			logger.info(String.format("Processing metaIdentifier %s", sourceAttr.getRef().toString()));
			if (sourceAttr.getRef().getType().equals(MetaType.simple)) {
				if (StringUtils.isBlank(sourceAttr.getValue())) {
					operandValue.add("''");
				} else {
					String value = sourceAttr.getValue();
					if(value != null && sourceAttr.getAttributeType() !=null) {
						String attrType=sourceAttr.getAttributeType();
					    value = attrType.equalsIgnoreCase("integer")? value :"'"+value+"'";
					}
					else if(value != null && sourceAttr.getAttributeType() !=null) {
						boolean isNumber = Helper.isNumber(value);			
						if(!isNumber) {
							value = "'"+value+"'";
						}
					}
				
					operandValue.add(value);
				}
			} else if (sourceAttr.getRef().getType().equals(MetaType.attribute)) {
				if (StringUtils.isBlank(sourceAttr.getValue())) {
					operandValue.add("''");
				} else {
					operandValue.add(sourceAttr.getValue());
				}
			} else if (sourceAttr.getRef().getType().equals(MetaType.paramlist)) {
				String value = metadataServiceImpl.getParamValue(execParams, sourceAttr.getAttributeId(), sourceAttr.getRef());
				if(value != null) {
					boolean isNumber = Helper.isNumber(value);			
					if(!isNumber) {
						value = "'"+value+"'";
					}
				}
				operandValue.add(value);
			} else if (sourceAttr.getRef().getType() == MetaType.dataset) {
				MetaIdentifier filterSourceMI = sourceAttr.getRef();				
				DataSet dataset = (DataSet) daoRegister.getRefObject(TaskParser.populateRefVersion(filterSourceMI, refKeyMap));
				List<AttributeRefHolder> datasetAttributes = registerService.getAttributesByDataset(dataset.getUuid());
				String attrName = datasetAttributes.get(sourceAttr.getAttributeId()).getAttrName();
				operandValue.add(dataset.sql(attrName));
				MetaIdentifier datasetRef = new MetaIdentifier(MetaType.dataset, dataset.getUuid(), dataset.getVersion());
				usedRefKeySet.add(datasetRef);				
			} else if (sourceAttr.getRef().getType() == MetaType.rule) {
				Rule rule = (Rule) daoRegister.getRefObject(TaskParser.populateRefVersion(filterSource.getRef(), refKeyMap));
				List<AttributeRefHolder> datasetAttributes = registerService.getAttributesByRule(rule.getUuid());
				String attrName = datasetAttributes.get(sourceAttr.getAttributeId()).getAttrName();
				operandValue.add(rule.sql(attrName));
				MetaIdentifier ruleRef = new MetaIdentifier(MetaType.rule, rule.getUuid(), rule.getVersion());
				usedRefKeySet.add(ruleRef);
			} else if (sourceAttr.getRef().getType() == MetaType.datapod) {
				Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				operandValue.add(datapod.sql(sourceAttr.getAttributeId()));
				MetaIdentifier datapodRef = new MetaIdentifier(MetaType.datapod, datapod.getUuid(), datapod.getVersion());
				usedRefKeySet.add(datapodRef);
			} else if (sourceAttr.getRef().getType() == MetaType.formula) {
				Formula formulaRef = (Formula) daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				if (formulaRef.getFormulaType().equals(FormulaType.aggr)) {
					aggrCount += 1;
				}
				operandValue.add(formulaOperator.generateSql(formulaRef, refKeyMap, otherParams, execParams, datasource));
				MetaIdentifier formulaRef1 = new MetaIdentifier(MetaType.formula, formulaRef.getUuid(), formulaRef.getVersion());
				usedRefKeySet.add(formulaRef1);
			}	
			 else if (sourceAttr.getRef().getType() == MetaType.function) {
					Function functionRef = (Function) daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
					operandValue.add(functionOperator.generateSql(functionRef, refKeyMap, otherParams, datasource));
					MetaIdentifier functionRef1 = new MetaIdentifier(MetaType.function, functionRef.getUuid(), functionRef.getVersion());
					usedRefKeySet.add(functionRef1);
				}	
		}
		
		// Handling having clause
		if ((!isAggrAllowed && aggrCount > 0) 
				|| (isAggrReqd && aggrCount <= 0)) {
			return null;
		}
		if(filterInfo.getOperator().trim().equalsIgnoreCase("IN") 
				|| filterInfo.getOperator().trim().equalsIgnoreCase("NOT IN")
				|| filterInfo.getOperator().trim().equalsIgnoreCase("EXISTS") 
				|| filterInfo.getOperator().trim().equalsIgnoreCase("NOT EXISTS")) {
			if(filterInfo.getOperand().get(1).getRef().getType().equals(MetaType.dataset)) {
				MetaIdentifier dataSetMI = filterInfo.getOperand().get(1).getRef();
				DataSet dataSet = (DataSet) commonServiceImpl.getOneByUuidAndVersion(dataSetMI.getUuid(), dataSetMI.getVersion(), dataSetMI.getType().toString());
				String subQuery = generateSubqueryByDataset(dataSet, refKeyMap, otherParams, usedRefKeySet, execParams, runMode);
				return generateRHSQuery(operandValue, filterInfo, filterSource, subQuery, dataSet.getName());
			} else {
				return generateRHSOperand(operandValue, filterInfo, filterSource);
			}			
		} else {
			return String.format("(%s %s %s)", operandValue.get(0), filterInfo.getOperator(), operandValue.get(1));
		}		
	}

	public String generateRHSQuery(List<String> operandValue, FilterInfo filterInfo, MetaIdentifierHolder filterSource, String subQuery, String alias) {
		StringBuilder rhsQuery = new StringBuilder();
		rhsQuery.append("SELECT ");
		rhsQuery.append(operandValue.get(1)).append(" ");
		rhsQuery.append(" FROM ");
		rhsQuery.append("( ").append(subQuery).append(" )");
		rhsQuery.append(" ").append(alias);
		return String.format("(%s %s %s)", operandValue.get(0), filterInfo.getOperator(), "("+rhsQuery.toString()+")");
	}
	
	public String generateRHSOperand(List<String> operandValue, FilterInfo filterInfo, MetaIdentifierHolder filterSource) {		
		return String.format("(%s %s %s)", operandValue.get(0), filterInfo.getOperator(), "("+operandValue.get(1)+")");
	}
	
	public String generateSubqueryByDataset(DataSet dataSet
			, java.util.Map<String, MetaIdentifier> refKeyMap
			, HashMap<String, String> otherParams
			, Set<MetaIdentifier> usedRefKeySet
			, ExecParams execParams
			, RunMode runMode) throws Exception {
		return datasetOperator.generateSql(dataSet, refKeyMap, otherParams, usedRefKeySet, execParams, runMode);
	}
}


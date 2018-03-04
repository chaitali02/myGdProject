/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
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
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FilterInfo;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.SourceAttr;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.ParamSetServiceImpl;
import com.inferyx.framework.service.RegisterService;

@Component
public class JoinKeyOperator {
	Logger logger=Logger.getLogger(JoinKeyOperator.class);
	@Autowired protected MetadataUtil daoRegister;
	@Autowired protected FormulaOperator formulaOperator;
	@Autowired protected RegisterService registerService;
	@Autowired protected ParamSetServiceImpl paramSetServiceImpl;
	
	public String generateSql(List<FilterInfo> filters, MetaIdentifierHolder filterSource
			, java.util.Map<String, MetaIdentifier> refKeyMap
			, HashMap<String, String> otherParams
			, Set<MetaIdentifier> usedRefKeySet
			, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder builder = new StringBuilder();
		builder.append("(").append(" ");

		if (filters == null) {
			return "";
		}

		for (FilterInfo filterInfo : filters) {
			builder.append(" ").append(filterInfo.getLogicalOperator()).append(" ");
			builder.append(generateSql(filterInfo,filterSource, refKeyMap, otherParams, usedRefKeySet, execParams)).append(" ");
		}
		builder.append(")");

		logger.info(String.format("Final filter %s", builder.toString()));
		return builder.toString();
	}
	
	
	private String generateSql(FilterInfo filterInfo, MetaIdentifierHolder filterSource
			, java.util.Map<String, MetaIdentifier> refKeyMap
			, HashMap<String, String> otherParams
			, Set<MetaIdentifier> usedRefKeySet
			, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		List<String> operandValue = new ArrayList<>(2);
		for (SourceAttr sourceAttr : filterInfo.getOperand()) {
			logger.info(String.format("Processing metaIdentifier %s", sourceAttr.getRef().toString()));
			if (sourceAttr.getRef().getType() == MetaType.simple) {
				if (StringUtils.isBlank(sourceAttr.getValue()))
					operandValue.add("''");
				else
					operandValue.add(sourceAttr.getValue());
			} else if (sourceAttr.getRef().getType() == MetaType.paramlist) {
				String value = null;
				value = paramSetServiceImpl.getParamValue(execParams, sourceAttr.getAttributeId(), sourceAttr.getRef());
				operandValue.add(value);
			} else if (sourceAttr.getRef().getType() == MetaType.dataset) {
				DataSet dataset = (DataSet) daoRegister.getRefObject(TaskParser.populateRefVersion(filterSource.getRef(), refKeyMap));
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
				operandValue.add(formulaOperator.generateSql(formulaRef, refKeyMap, otherParams, execParams));
				MetaIdentifier formulaRef1 = new MetaIdentifier(MetaType.formula, formulaRef.getUuid(), formulaRef.getVersion());
				usedRefKeySet.add(formulaRef1);
			}			
		}

		return String.format("(%s %s %s)", operandValue.get(0), filterInfo.getOperator(), operandValue.get(1));
	}

}


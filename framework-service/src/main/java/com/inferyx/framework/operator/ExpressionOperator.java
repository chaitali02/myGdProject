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

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.FilterInfo;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.SourceAttr;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.MetadataServiceImpl;
import com.inferyx.framework.service.RegisterService;

@Component
public class ExpressionOperator {
Logger logger=Logger.getLogger(ExpressionOperator.class);
	@Autowired
	protected MetadataUtil daoRegister;
	@Autowired
	protected JoinKeyOperator joinKeyOperator;
	@Autowired
	protected RegisterService registerService;
	@Autowired
	protected FormulaOperator formulaOperator;
	@Autowired
	MetadataServiceImpl metadataServiceImpl;
	@Autowired 
	protected CommonServiceImpl<?> commonServiceImpl;
	
	private final String COMMA = ", ";

	/********************** UNUSED **********************/
/*	public String generateSql(List<AttributeRefHolder> filterIdentifierList,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet,
			ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder builder = new StringBuilder();
		if (filterIdentifierList == null || filterIdentifierList.size() <= 0) {
			return "";
		}
		// Append Filter(s)
		for (int i = 0; i < filterIdentifierList.size(); i++) {
			AttributeRefHolder filterIdentifier = filterIdentifierList.get(i);
			// Determine type of ref. If type is filter then parse filter. If
			// type is datapod then append a clause where column = value
			switch (filterIdentifier.getRef().getType()) {
			case filter:
				OrderKey filterKey = filterIdentifier.getRef().getKey();
				Filter filter = null;
				if (null == filterKey.getVersion()) {
					filter = daoRegister.getFilterDao().findLatestByUuid(filterKey.getUUID(),
							new Sort(Sort.Direction.DESC, "version"));
				} else {
					filter = daoRegister.getFilterDao().findOneByUuidAndVersion(filterKey.getUUID(),
							filterKey.getVersion());
				}
				builder.append(" AND (").append(joinKeyOperator.generateSql(filter.getFilterInfo(),
						filter.getDependsOn(), refKeyMap, otherParams, usedRefKeySet, execParams)).append(")");
				MetaIdentifier filterRef = new MetaIdentifier(MetaType.filter, filter.getUuid(), filter.getVersion());
				usedRefKeySet.add(filterRef);
				break;
			case datapod:
				OrderKey datapodKey = filterIdentifier.getRef().getKey();
				Datapod datapod = null;
				if (null == datapodKey.getVersion()) {
					datapod = daoRegister.getDatapodDao().findLatestByUuid(datapodKey.getUUID(),
							new Sort(Sort.Direction.DESC, "version"));
				} else {
					datapod = daoRegister.getDatapodDao().findOneByUuidAndVersion(datapodKey.getUUID(),
							datapodKey.getVersion());
				}
				builder.append(" AND (").append(generateDatapodFilterSql(datapod, filterIdentifier.getAttrId(),
						filterIdentifier.getValue())).append(")");
				MetaIdentifier datapodRef = new MetaIdentifier(MetaType.datapod, datapod.getUuid(), datapod.getVersion());
				usedRefKeySet.add(datapodRef);
				break;
			default:
				builder.append("");
				break;
			}// End switch
		}
		return builder.toString();
	}*/

	/**
	 * Generate select query with expression with filter
	 * 
	 * @param filterIdentifierList
	 * @param usedRefKeySet 
	 * @return
	 * @throws Exception 
	 */
	public String generateSelectWithFilter(List<AttributeRefHolder> filterIdentifierList, Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception {
		StringBuilder builder = new StringBuilder();
		if (filterIdentifierList == null || filterIdentifierList.isEmpty()) {
			return "";
		}
		for (AttributeRefHolder filterIdentifier : filterIdentifierList) {

			switch (filterIdentifier.getRef().getType()) {
			case expression:
				OrderKey expressionKey = filterIdentifier.getRef().getKey();
				com.inferyx.framework.domain.Expression expression = (Expression) commonServiceImpl.getOneByUuidAndVersion(expressionKey.getUUID(), expressionKey.getVersion(), MetaType.expression.toString());
				builder.append(" (")
						.append(generateSql(expression.getExpressionInfo(), expression.getDependsOn(), null, null, execParams))
						.append(")");
				builder.append(" as ").append(expression.getName()).append(COMMA);
				MetaIdentifier expressionRef = new MetaIdentifier(MetaType.filter, expression.getUuid(), expression.getVersion());
				usedRefKeySet.add(expressionRef);
				break;
			case filter:
				OrderKey filterKey = filterIdentifier.getRef().getKey();
				com.inferyx.framework.domain.Filter filter = (Filter) commonServiceImpl.getOneByUuidAndVersion(filterKey.getUUID(), filterKey.getVersion(), MetaType.filter.toString());
				builder.append(" (")
						.append(joinKeyOperator.generateSql(filter.getFilterInfo(), filter.getDependsOn(), null, null, usedRefKeySet, execParams, true, false, runMode))
						.append(")");
				builder.append(" as ").append(filter.getName()).append(COMMA);
				MetaIdentifier filterRef = new MetaIdentifier(MetaType.filter, filter.getUuid(), filter.getVersion());
				usedRefKeySet.add(filterRef);
				break;
			case datapod:
				OrderKey datapodKey = filterIdentifier.getRef().getKey();
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodKey.getUUID(), datapodKey.getVersion(), MetaType.datapod.toString());
				builder.append(" (").append(generateDatapodFilterSql(datapod, filterIdentifier.getAttrId(),
						filterIdentifier.getValue())).append(")");
				builder.append(" as ").append(datapod.getName()).append("_filter").append(COMMA);
				MetaIdentifier datapodRef = new MetaIdentifier(MetaType.datapod, datapod.getUuid(), datapod.getVersion());
				usedRefKeySet.add(datapodRef);
				break;
			default:
				builder.append("");
				break;
			}// End switch
		} // End for

		return builder.toString();
	}

	private String generateDatapodFilterSql(Datapod datapod, String attributeId, String value) {
		if (!NumberUtils.isDigits(attributeId)) {
			return "";
		}
		if(value != null) {
			boolean isNumber = Helper.isNumber(value);			
			if(!isNumber) {
				if (value.contains(",")) {
					value = value.substring(0, value.length()-1);
					value = "'"+value+"'"+",";
					return String.format("%s IN (%s)", datapod.sql(Integer.parseInt(attributeId)), value);
				} else {				
					value = "'"+value+"'";
				}
			}
		}
		return String.format("%s = %s", datapod.sql(Integer.parseInt(attributeId)), value);
	}

	public String generateSql(List<FilterInfo> expression, MetaIdentifierHolder expressionSource,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder builder = new StringBuilder();
		builder.append("(").append(" ");

		if (expression == null) {
			return "";
		}

		for (FilterInfo expressionInfo : expression) {
			builder.append(" ").append(expressionInfo.getLogicalOperator()).append(" ");
			builder.append(generateSql(expressionInfo, expressionSource, refKeyMap, otherParams, execParams)).append(" ");
		}
		builder.append(")");

		logger.info(String.format("Final filter %s", builder.toString()));
		return builder.toString();
	}

	private String generateSql(FilterInfo expressionInfo, MetaIdentifierHolder filterSource,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		List<String> operandValue = new ArrayList<>(2);
		for (SourceAttr sourceAttr : expressionInfo.getOperand()) {
			logger.info(String.format("Processing metaIdentifier %s", sourceAttr.getRef().toString()));
			if (sourceAttr.getRef().getType() == MetaType.simple) {
				String value = sourceAttr.getValue();
				if(value != null) {
					boolean isNumber = Helper.isNumber(value);			
					if(!isNumber) {
						value = "'"+value+"'";
					}
				}
				operandValue.add(value);
			} else if (sourceAttr.getRef().getType() == MetaType.paramlist && execParams != null && (execParams.getCurrParamSet() != null || execParams.getParamListHolder() != null)) {
				String value = null;
				value = metadataServiceImpl.getParamValue(execParams, sourceAttr.getAttributeId(), sourceAttr.getRef());
				if(value != null) {
					boolean isNumber = Helper.isNumber(value);			
					if(!isNumber) {
						value = "'"+value+"'";
					}
				}
				operandValue.add(value);
			} else if (filterSource.getRef().getType() == MetaType.dataset
					&& sourceAttr.getRef().getType() == MetaType.dataset) {
				DataSet dataset = (DataSet) daoRegister
						.getRefObject(TaskParser.populateRefVersion(filterSource.getRef(), refKeyMap));
				List<AttributeRefHolder> datasetAttributes = registerService.getAttributesByDataset(dataset.getUuid());
				String attrName = datasetAttributes.get(sourceAttr.getAttributeId()).getAttrName();
				operandValue.add(dataset.sql(attrName));
			} else if (sourceAttr.getRef().getType() == MetaType.datapod
					&& filterSource.getRef().getType() == MetaType.relation) {
				Datapod datapod = (Datapod) daoRegister
						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				operandValue.add(datapod.sql(sourceAttr.getAttributeId()));
			} else if (sourceAttr.getRef().getType() == MetaType.datapod) {
				Datapod datapod = (Datapod) daoRegister
						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				operandValue.add(datapod.sql(sourceAttr.getAttributeId()));
			}

			// implementing formula as ref in filter
			if (sourceAttr.getRef().getType() == MetaType.formula) {
				Formula formulaRef = (Formula) daoRegister
						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				operandValue.add(formulaOperator.generateSql(formulaRef, refKeyMap, otherParams, execParams));
			}

		}
		return String.format("(%s %s %s)", operandValue.get(0), expressionInfo.getOperator(), operandValue.get(1));
	}

	public String generateMetCondition(AttributeRefHolder metInfo, AttributeRefHolder filterSource,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String operandValue = null;
		if (metInfo.getRef().getType() == MetaType.simple) {
			String value = metInfo.getValue();
			if(value != null) {
				boolean isNumber = Helper.isNumber(value);			
				if(!isNumber) {
					value = "'"+value+"'";
				}
			}
			operandValue = value;
		} else if (metInfo.getRef().getType() == MetaType.paramlist) {
			String value = null;
			value = metadataServiceImpl.getParamValue(execParams, Integer.parseInt(metInfo.getAttrId()), metInfo.getRef());
			if(value != null) {
				boolean isNumber = Helper.isNumber(value);			
				if(!isNumber) {
					value = "'"+value+"'";
				}
			}
			operandValue = value;
		} else if (metInfo.getRef().getType() == MetaType.formula) {
			Formula formulaRef = (Formula) daoRegister
					.getRefObject(TaskParser.populateRefVersion(metInfo.getRef(), refKeyMap));
			operandValue = formulaOperator.generateSql(formulaRef, refKeyMap, otherParams, execParams);
		}
		return operandValue;
	}

	public String generateNotMetCondition(AttributeRefHolder notMetInfo, AttributeRefHolder filterSource,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String operandValue = null;
		if (notMetInfo.getRef().getType() == MetaType.simple) {
			String value = notMetInfo.getValue();
			if(value != null) {
				boolean isNumber = Helper.isNumber(value);			
				if(!isNumber) {
					value = "'"+value+"'";
				}
			}
			operandValue = value;
		} else if (notMetInfo.getRef().getType() == MetaType.paramlist) {
			String value = null;
			value = metadataServiceImpl.getParamValue(execParams, Integer.parseInt(notMetInfo.getAttrId()), notMetInfo.getRef());
			if(value != null) {
				boolean isNumber = Helper.isNumber(value);			
				if(!isNumber) {
					value = "'"+value+"'";
				}
			}
			operandValue = value;
		} else if (notMetInfo.getRef().getType() == MetaType.formula) {
			Formula formulaRef = (Formula) daoRegister
					.getRefObject(TaskParser.populateRefVersion(notMetInfo.getRef(), refKeyMap));
			operandValue = formulaOperator.generateSql(formulaRef, refKeyMap, otherParams, execParams);
		}
		return operandValue;
	}
	
	
	public String selectGroupBy(List<FilterInfo> expressionInfo, 
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams) throws JsonProcessingException {
		StringBuilder operandValue = new StringBuilder();
		for (FilterInfo expression : expressionInfo) {
			for (SourceAttr sourceAttr : expression.getOperand()) {
				if (sourceAttr.getRef().getType() == MetaType.formula) {
					Formula formulaRef = (Formula) daoRegister
							.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
					operandValue.append(formulaOperator.selectGroupBy(formulaRef, refKeyMap, otherParams));
				}
			}
		}
		return operandValue.toString();
	}
	
	public boolean isGroupBy(List<FilterInfo> expressionInfo, 
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams) throws JsonProcessingException {
		for (FilterInfo expression : expressionInfo) {
			for (SourceAttr sourceAttr : expression.getOperand()) {
				if (sourceAttr.getRef().getType() == MetaType.formula) {
					Formula formulaRef = (Formula) daoRegister
							.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
					if (formulaOperator.isGroupBy(formulaRef, refKeyMap, otherParams)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}

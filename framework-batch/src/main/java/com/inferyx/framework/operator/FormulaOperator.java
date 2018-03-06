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
import java.util.HashMap;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AggregateFunc;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.FormulaType;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.SourceAttr;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.DatasetServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

@Component
public class FormulaOperator {
	
	@Autowired protected MetadataUtil daoRegister;
	@Autowired protected DatasetServiceImpl datasetServiceImpl;
	@Autowired protected FunctionOperator functionOperator;
	@Autowired protected ParamSetServiceImpl paramSetServiceImpl;

	public String generateSql(Formula formula,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		
		boolean pctFormula = false;
		StringBuilder builder = new StringBuilder();
		for (SourceAttr sourceAttr : formula.getFormulaInfo()) {
			if (sourceAttr.getRef().getType() == MetaType.simple) {
				builder.append(sourceAttr.getValue());
			} else if (sourceAttr.getRef().getType() == MetaType.paramlist && execParams != null && execParams.getParamSetHolder() != null) {
				String value = null;
				value = paramSetServiceImpl.getParamValue(execParams, sourceAttr.getAttributeId(), sourceAttr.getRef());
				builder.append(value);
			} 
			if (sourceAttr.getRef().getType() == MetaType.function) {
				Function function = (Function) daoRegister.getRefObject(sourceAttr.getRef());
				builder.append(functionOperator.generateSql(function, refKeyMap, otherParams));
			}
			// implementing nested formula
			if (sourceAttr.getRef().getType() == MetaType.formula) {
				Formula innerFormula = (Formula) daoRegister
						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				builder.append("(" + generateSql(innerFormula, refKeyMap, otherParams, execParams) + ")");
			}

			if (sourceAttr.getRef().getType() == MetaType.datapod) {
				Datapod datapod = (Datapod) daoRegister
						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				
				if (formula.getFormulaType() != null && formula.getFormulaType().equals(FormulaType.percentage)) {

					if (!pctFormula) {
						builder.append("SUM(" + datapod.sql(sourceAttr.getAttributeId()) + ")");
						builder.append(datapod.sql(sourceAttr.getAttributeId()));
						builder.append(" OVER (PARTITION BY " + otherParams.get("partitionBy") + ")");
					} else
						builder.append(datapod.sql(sourceAttr.getAttributeId()));
					pctFormula = true;

				}else {
					builder.append(datapod.sql(sourceAttr.getAttributeId()));
				}
			}
			
			if (sourceAttr.getRef().getType() == MetaType.dataset) {
				DataSet dataset = (DataSet) daoRegister
						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				builder.append(datasetServiceImpl.getAttributeSql(daoRegister, dataset, sourceAttr.getAttributeId()+"")).append(" ").toString();
			}
		}
		if (formula.getFormulaType() != null && formula.getFormulaType().equals(FormulaType.sum_aggr)) {
			builder.insert(0, "SUM(");
			builder.append(") ");

		} 

		if (pctFormula)
			builder.append(" OVER () ");
		else if (!pctFormula && otherParams != null && otherParams.get("pctFormula") != null && otherParams.get("pctFormula").equals("true")) {
			builder.append(" OVER (PARTITION BY " + otherParams.get("partitionBy") + ")");
		}

		System.out.println(String.format("Generalize formula %s", builder.toString()));
		return builder.toString();
	}
	
	public boolean isGroupBy (Formula formula, 
								java.util.Map<String, MetaIdentifier> refKeyMap, 
								HashMap<String, String> otherParam) throws JsonProcessingException {
		if (formula == null) {
			return false;
		}
		for (SourceAttr sourceAttr : formula.getFormulaInfo()) {
			if (sourceAttr.getRef().getType() == MetaType.simple) {
				if (EnumUtils.isValidEnum(AggregateFunc.class, sourceAttr.getValue().toLowerCase())){
					return true;
				}
			}
			if (sourceAttr.getRef().getType() == MetaType.formula) {
				Formula innerFormula = (Formula) daoRegister
						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				if (isGroupBy(innerFormula, refKeyMap, otherParam)) {
					return true;
				}
			}
		}
		return true;
	}
	
	/**
	 * Populate Group by attributes from formula
	 * @param formula
	 * @param refKeyMap
	 * @param otherParams
	 * @return
	 * @throws JsonProcessingException 
	 */
	public String selectGroupBy(Formula formula, 
								java.util.Map<String, MetaIdentifier> refKeyMap, 
								HashMap<String, String> otherParams) throws JsonProcessingException {

		boolean isInAggr = false;
		int countBrOpen = 0;
		StringBuilder builder = new StringBuilder(" ");
		if (formula.getFormulaType() == FormulaType.sum_aggr || formula.getFormulaType() == FormulaType.aggr) {
			return builder.toString();
		}
		for (SourceAttr sourceAttr : formula.getFormulaInfo()) {
			if (sourceAttr.getRef().getType() == MetaType.simple) {
				if (EnumUtils.isValidEnum(AggregateFunc.class, sourceAttr.getValue().toLowerCase())){
					isInAggr = true;
				} else if (isInAggr) {
					for (char i : sourceAttr.getValue().toCharArray()) {
						if (i  == '(') {
							countBrOpen++;
						} else if (i == ')') {
							countBrOpen--;
							if (countBrOpen == 0) {
								isInAggr = false;
								break;
							}
						}
					}
				} // End - if isInAggr
			}// End - Simple
			
			// implementing nested formula
			if (sourceAttr.getRef().getType() == MetaType.formula && !isInAggr) {
				Formula innerFormula = (Formula) daoRegister
						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				builder.append(selectGroupBy(innerFormula, refKeyMap, otherParams));
			}

			if (sourceAttr.getRef().getType() == MetaType.datapod && !isInAggr) {
				Datapod datapod = (Datapod) daoRegister
						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				builder.append(datapod.sql(sourceAttr.getAttributeId())).append(",");
			}
			
			if (sourceAttr.getRef().getType() == MetaType.dataset && !isInAggr) {
				DataSet dataset = (DataSet) daoRegister
						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				builder.append(datasetServiceImpl.getAttributeSql(daoRegister, dataset, sourceAttr.getAttributeId()+"")).append(",");
			}

		}// End for formula
		return builder.substring(0, builder.length()-1).toString();
	}

}
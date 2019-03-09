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

import org.apache.commons.lang3.EnumUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.AggregateFunc;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.FormulaType;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.SourceAttr;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DatasetServiceImpl;
import com.inferyx.framework.service.MetadataServiceImpl;
import com.inferyx.framework.service.ParamListServiceImpl;
import com.inferyx.framework.service.RuleServiceImpl;

@Component
public class FormulaOperator {
	
	@Autowired protected DatasetServiceImpl datasetServiceImpl;
	@Autowired protected FunctionOperator functionOperator;
	@Autowired protected ParamListServiceImpl paramListServiceImpl;
	@Autowired protected RuleServiceImpl ruleServiceImpl;
	@Autowired
	MetadataServiceImpl metadataServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger LOGGER = Logger.getLogger(FormulaOperator.class);
	
	public String generateSql(Formula formula,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams, Datasource datasource) 
					throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return generateSql(formula
				, refKeyMap
				, otherParams
				, execParams
				, datasource
				, new ArrayList<String>());
	}

	public String generateSql(Formula formula
							, java.util.Map<String, MetaIdentifier> refKeyMap
							, HashMap<String, String> otherParams
							, ExecParams execParams
							, Datasource datasource
							, List<String> attributeList) 
					throws JsonProcessingException
						, IllegalAccessException
						, IllegalArgumentException
						, InvocationTargetException
						, NoSuchMethodException
						, SecurityException
						, NullPointerException
						, ParseException {
		
		boolean pctFormula = false;
		StringBuilder builder = new StringBuilder();
//		Datasource source = commonServiceImpl.getDatasourceByApp();
		for (SourceAttr sourceAttr : formula.getFormulaInfo()) {
			builder.append("");
			if (sourceAttr.getRef().getType() == MetaType.simple) {
				if(!sourceAttr.getValue().equals("("))
					builder.append(" ");
				builder.append(sourceAttr.getValue());
			} else if (sourceAttr.getRef().getType() == MetaType.paramlist && execParams != null && (execParams.getCurrParamSet() != null || execParams.getParamListHolder() != null)) {
				builder.append(" ");
				String value = metadataServiceImpl.getParamValue(execParams, sourceAttr.getAttributeId(), sourceAttr.getRef());
				if (value != null) {
					boolean isNumber = Helper.isNumber(value);
					if (!isNumber) {
						value = "'" + value + "'";
					}
				}
				if (datasource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString()) && builder.toString().contains("date_sub")
						&& builder.lastIndexOf(",") != -1) {
					builder.append("INTERVAL " + value + " DAY");
				} else {
					builder.append(value);

				}
			} else if (sourceAttr.getRef().getType() == MetaType.paramlist && execParams == null) {
				builder.append(" ");
//				String value = null;
//				ParamList paramList = (ParamList) daoRegister.getRefObject(sourceAttr.getRef());
//				value = paramListServiceImpl.sql(sourceAttr.getAttributeId(), paramList);
//				builder.append(value);
				String value = metadataServiceImpl.getParamValue(execParams, sourceAttr.getAttributeId(), sourceAttr.getRef());
				if(value != null) {
					boolean isNumber = Helper.isNumber(value);			
					if(!isNumber) {
						value = "'"+value+"'";
					}
				}
				if (datasource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString()) && builder.toString().contains("date_sub(")
						&& builder.lastIndexOf(",") != -1) {
					builder.append("INTERVAL " + value + " DAY");
				} else {
					builder.append(value);

				}
			}  
			if (sourceAttr.getRef().getType() == MetaType.function) {
//				Function function = (Function) daoRegister.getRefObject(sourceAttr.getRef());
				Function function = (Function) commonServiceImpl.getOneByUuidAndVersion(sourceAttr.getRef().getUuid(), sourceAttr.getRef().getVersion(), sourceAttr.getRef().getType().toString(), "N");
						
				builder.append(functionOperator.generateSql(function, refKeyMap, otherParams, datasource, attributeList));
			}
			// implementing nested formula
			if (sourceAttr.getRef().getType() == MetaType.formula) {
				builder.append(" ");
//				Formula innerFormula = (Formula) daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap);
				Formula innerFormula = (Formula)  commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
						
				Datasource datasource2 = commonServiceImpl.getDatasourceByObject(formula);
				builder.append(" (" + generateSql(innerFormula, refKeyMap, otherParams, execParams, datasource2, attributeList) + ") ");
			}

			if (sourceAttr.getRef().getType() == MetaType.datapod) {
				builder.append(" ");
//				Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap);
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
				if (formula.getFormulaType() != null && formula.getFormulaType().equals(FormulaType.percentage)) {

					if (!pctFormula) {
						builder.append(" SUM(" + datapod.sql(sourceAttr.getAttributeId()) + ") ");
						builder.append(datapod.sql(sourceAttr.getAttributeId()));
						builder.append(" OVER (PARTITION BY " + otherParams.get("partitionBy") + ") ");
					} else
						builder.append(datapod.sql(sourceAttr.getAttributeId()));
					pctFormula = true;

				}else {
					builder.append(datapod.sql(sourceAttr.getAttributeId()));
				}
				attributeList.add(datapod.sql(sourceAttr.getAttributeId()));
			}
			
			if (sourceAttr.getRef().getType() == MetaType.dataset) {
				builder.append(" ");
//				DataSet dataset = (DataSet) daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap);
				DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
						
				builder.append(datasetServiceImpl.getAttributeSql(dataset, sourceAttr.getAttributeId()+"")).append(" ").toString();
				attributeList.add(datasetServiceImpl.getAttributeSql(dataset, sourceAttr.getAttributeId()+""));
			}
			
			if (sourceAttr.getRef().getType() == MetaType.rule) {
				builder.append(" ");
//				Rule rule = (Rule) daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap);
				Rule rule = (Rule) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
						
				builder.append(ruleServiceImpl.getAttributeSql(rule, sourceAttr.getAttributeId()+"")).append(" ").toString();
				attributeList.add(ruleServiceImpl.getAttributeSql(rule, sourceAttr.getAttributeId()+""));
			}
		}
		if (formula.getFormulaType() != null && formula.getFormulaType().equals(FormulaType.sum_aggr)) {
			builder.insert(0, " SUM(");
			builder.append(") ");

		} 

		if (pctFormula)
			builder.append(" OVER () ");
		else if (!pctFormula && otherParams != null && otherParams.get("pctFormula") != null && otherParams.get("pctFormula").equals("true")) {
			builder.append(" OVER (PARTITION BY " + otherParams.get("partitionBy") + ") ");
		}

		LOGGER.info(String.format("Generalize formula %s", builder.toString()));
		return builder.toString();
	}
	
//	public String generateSql(Formula formula,
//			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams) 
//					throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
//		
//		boolean pctFormula = false;
//		StringBuilder builder = new StringBuilder();
////		Datasource source = commonServiceImpl.getDatasourceByApp();
//		Datasource datasource = commonServiceImpl.getDatasourceByObject(formula);
//		for (SourceAttr sourceAttr : formula.getFormulaInfo()) {
//			builder.append("");
//			if (sourceAttr.getRef().getType() == MetaType.simple) {
//				if(!sourceAttr.getValue().equals("("))
//					builder.append(" ");
//				builder.append(sourceAttr.getValue());
//			} else if (sourceAttr.getRef().getType() == MetaType.paramlist && execParams != null && (execParams.getCurrParamSet() != null || execParams.getParamListHolder() != null)) {
//				builder.append(" ");
//				String value = metadataServiceImpl.getParamValue(execParams, sourceAttr.getAttributeId(), sourceAttr.getRef());
//				if (value != null) {
//					boolean isNumber = Helper.isNumber(value);
//					if (!isNumber) {
//						value = "'" + value + "'";
//					}
//				}
//				if (datasource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString()) && builder.toString().contains("date_sub")
//						&& builder.lastIndexOf(",") != -1) {
//					builder.append("INTERVAL " + value + " DAY");
//				} else {
//					builder.append(value);
//
//				}
//			} else if (sourceAttr.getRef().getType() == MetaType.paramlist && execParams == null) {
//				builder.append(" ");
////				String value = null;
////				ParamList paramList = (ParamList) daoRegister.getRefObject(sourceAttr.getRef());
////				value = paramListServiceImpl.sql(sourceAttr.getAttributeId(), paramList);
////				builder.append(value);
//				String value = metadataServiceImpl.getParamValue(execParams, sourceAttr.getAttributeId(), sourceAttr.getRef());
//				if(value != null) {
//					boolean isNumber = Helper.isNumber(value);			
//					if(!isNumber) {
//						value = "'"+value+"'";
//					}
//				}
//				if (datasource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString()) && builder.toString().contains("date_sub(")
//						&& builder.lastIndexOf(",") != -1) {
//					builder.append("INTERVAL " + value + " DAY");
//				} else {
//					builder.append(value);
//
//				}
//			}  
//			if (sourceAttr.getRef().getType() == MetaType.function) {
//				Function function = (Function) daoRegister.getRefObject(sourceAttr.getRef());
//				
//				builder.append(functionOperator.generateSql(function, refKeyMap, otherParams, datasource));
//			}
//			// implementing nested formula
//			if (sourceAttr.getRef().getType() == MetaType.formula) {
//				builder.append(" ");
//				Formula innerFormula = (Formula) daoRegister
//						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
//				
//				builder.append(" (" + generateSql(innerFormula, refKeyMap, otherParams, execParams) + ") ");
//			}
//
//			if (sourceAttr.getRef().getType() == MetaType.datapod) {
//				builder.append(" ");
//				Datapod datapod = (Datapod) daoRegister
//						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
//				
//				if (formula.getFormulaType() != null && formula.getFormulaType().equals(FormulaType.percentage)) {
//
//					if (!pctFormula) {
//						builder.append(" SUM(" + datapod.sql(sourceAttr.getAttributeId()) + ") ");
//						builder.append(datapod.sql(sourceAttr.getAttributeId()));
//						builder.append(" OVER (PARTITION BY " + otherParams.get("partitionBy") + ") ");
//					} else
//						builder.append(datapod.sql(sourceAttr.getAttributeId()));
//					pctFormula = true;
//
//				}else {
//					builder.append(datapod.sql(sourceAttr.getAttributeId()));
//				}
//			}
//			
//			if (sourceAttr.getRef().getType() == MetaType.dataset) {
//				builder.append(" ");
//				DataSet dataset = (DataSet) daoRegister
//						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
//				builder.append(datasetServiceImpl.getAttributeSql(daoRegister, dataset, sourceAttr.getAttributeId()+"")).append(" ").toString();
//			}
//			
//			if (sourceAttr.getRef().getType() == MetaType.rule) {
//				builder.append(" ");
//				Rule rule = (Rule) daoRegister
//						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
//				builder.append(ruleServiceImpl.getAttributeSql(daoRegister, rule, sourceAttr.getAttributeId()+"")).append(" ").toString();
//			}
//		}
//		if (formula.getFormulaType() != null && formula.getFormulaType().equals(FormulaType.sum_aggr)) {
//			builder.insert(0, " SUM(");
//			builder.append(") ");
//
//		} 
//
//		if (pctFormula)
//			builder.append(" OVER () ");
//		else if (!pctFormula && otherParams != null && otherParams.get("pctFormula") != null && otherParams.get("pctFormula").equals("true")) {
//			builder.append(" OVER (PARTITION BY " + otherParams.get("partitionBy") + ") ");
//		}
//
//		LOGGER.info(String.format("Generalize formula %s", builder.toString()));
//		return builder.toString();
//	}
	
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
//				Formula innerFormula = (Formula) daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap);
				Formula innerFormula = (Formula) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
						
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
		StringBuilder builder = new StringBuilder("");
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
//				Formula innerFormula = (Formula) daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap);
				Formula innerFormula = (Formula) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
						
				builder.append(selectGroupBy(innerFormula, refKeyMap, otherParams));
			}

			if (sourceAttr.getRef().getType() == MetaType.datapod && !isInAggr) {
//				Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap);
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
						
				builder.append(datapod.sql(sourceAttr.getAttributeId())).append(", ");
			}
			
			if (sourceAttr.getRef().getType() == MetaType.dataset && !isInAggr) {
//				DataSet dataset = (DataSet) daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				MetaIdentifier ref = TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap);
				DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
						
				builder.append(datasetServiceImpl.getAttributeSql(dataset, sourceAttr.getAttributeId()+"")).append(", ");
			}

		}// End for formula
		return builder.substring(0, builder.length()-1).toString();
	}

}
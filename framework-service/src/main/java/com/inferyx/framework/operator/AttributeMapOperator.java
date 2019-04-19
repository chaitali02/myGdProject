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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.AttributeMap;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.FilterInfo;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.FormulaType;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.SourceAttr;
import com.inferyx.framework.domain.SysParamsEnum;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.DatasetServiceImpl;
import com.inferyx.framework.service.MetadataServiceImpl;
import com.inferyx.framework.service.RuleServiceImpl;

@Component
public class AttributeMapOperator {
//
//	@Autowired
//	protected MetadataUtil daoRegister;
	@Autowired
	protected DatapodServiceImpl datapodServiceImpl;
	@Autowired
	protected DatasetServiceImpl datasetServiceImpl;
	@Autowired
	protected JoinKeyOperator joinKeyOperator;
	@Autowired
	protected FormulaOperator formulaOperator;
	@Autowired
	protected ExpressionOperator expressionOperator;
	@Autowired
	protected RuleServiceImpl ruleServiceImpl;
	@Autowired
	protected FunctionOperator functionOperator;
	@Autowired
	MetadataServiceImpl metadataServiceImpl;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	
	private RunMode runMode;
	
	static final Logger logger = Logger.getLogger(AttributeMapOperator.class);

	/**
	 * @Ganesh
	 *
	 * @return the runMode
	 */
	public RunMode getRunMode() {
		return runMode;
	}

	/**
	 * @Ganesh
	 *
	 * @param runMode the runMode to set
	 */
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

	public String generateSql(List<AttributeMap> attrMapList, MetaIdentifierHolder mapSource,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams, 
			Map<String, String> paramValMap) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder builder = new StringBuilder();
		String comma = "";
		
		// add select attribute
		for (AttributeMap attr : attrMapList) {
			builder.append(comma);
			builder.append(mapSql(attr, mapSource, refKeyMap, otherParams, execParams, paramValMap));
			comma = ",";
		}
		
		//replace 
		return builder.toString();
	}


	private boolean getTypeInRef(MetaIdentifier ref, MetaType type) {
		if (ref == null || ref.getType() == null) {
			return false;
		}
		return ref.getType().equals(type);
	}

	private boolean getTypeInSourceAttrs(AttributeRefHolder sourceAttr, MetaType type) {
		if (sourceAttr == null) {
			return false;
		}
		if (getTypeInRef(sourceAttr.getRef(), type)) {
			return true;
		}
		return false;
	}

	public String mapSql(AttributeMap attrMap, MetaIdentifierHolder mapSource,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams, Map<String, String> paramValMap) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder builder = new StringBuilder();
		
		Datapod datapod = null;
		String alias = null;
		try {
			if (attrMap.getTargetAttr() != null && attrMap.getTargetAttr().getRef() != null) { // Set attribute alias as corrs. target attribute
				if(attrMap.getTargetAttr().getRef().getType().equals(MetaType.attribute)
						|| ((attrMap.getTargetAttr().getRef().getType().equals(MetaType.function)
						|| attrMap.getTargetAttr().getRef().getType().equals(MetaType.formula)) 
								&& mapSource.getRef().getType().equals(MetaType.simple))) {
					//special handling for ingest 
					alias = attrMap.getTargetAttr().getValue();
				} else {
//					datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(attrMap.getTargetAttr().getRef(), refKeyMap));
					MetaIdentifier ref = TaskParser.populateRefVersion(attrMap.getTargetAttr().getRef(), refKeyMap);
					datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
					alias = datapod.getAttribute(Integer.parseInt(attrMap.getTargetAttr().getAttrId())).getName();
				}
			} else { // If target attribute is not present, set attribute alias as source attribute alias appended by attributeid (as two source attrs. may have the same name
				alias = attrMap.getSourceAttr().getAttrName();
				//alias = sourceAttrAlias(daoRegister, mapSource, attrMap.getSourceAttr(), refKeyMap, otherParams);
						//.concat(attrMap.getSourceAttr().get(0).getAttributeId().toString());
			} 
			
			if(attrMap.getSourceAttr().getRef().getType().equals(MetaType.function) 
					&& mapSource.getRef().getType().equals(MetaType.simple)) {
				//special handling for ingest 
				Function function = (Function) commonServiceImpl.getOneByUuidAndVersion(attrMap.getSourceAttr().getRef().getUuid()
						, attrMap.getSourceAttr().getRef().getVersion()
						, attrMap.getSourceAttr().getRef().getType().toString()
						, "N");
				Datasource fileDatasource = new Datasource();
				fileDatasource.setType(MetaType.file.toString());
				return builder.append(functionOperator.generateSql(function, refKeyMap, otherParams, fileDatasource)).append(" as ").append(alias).append(" ").toString();
			} else if(attrMap.getSourceAttr().getRef().getType().equals(MetaType.formula) 
					&& mapSource.getRef().getType().equals(MetaType.simple)) {
				//special handling for ingest 
				Formula formula = (Formula) commonServiceImpl.getOneByUuidAndVersion(attrMap.getSourceAttr().getRef().getUuid()
						, attrMap.getSourceAttr().getRef().getVersion()
						, attrMap.getSourceAttr().getRef().getType().toString()
						, "N");
				Datasource fileDatasource = new Datasource();
				fileDatasource.setType(MetaType.file.toString());
				return builder.append(formulaOperator.generateSql(formula, refKeyMap, otherParams, execParams, fileDatasource, paramValMap)).append(" as ").append(alias).append(" ").toString();
			} else if(attrMap.getSourceAttr().getRef().getType().equals(MetaType.attribute)) {
				//special handling for ingest 				
				return builder.append(attrMap.getSourceAttr().getValue()).append(" as ").append(alias).append(" ").toString();
			}
			// handle sysparam
			else if (attrMap.getSourceAttr().getRef().getType().equals(MetaType.simple) && attrMap.getSourceAttr().getValue().toString().equalsIgnoreCase(SysParamsEnum.MAPEXEC_VERSION.toString())) {
				return builder.append("'" + execParams.getSysParams().get(SysParamsEnum.MAPEXEC_VERSION) + "'").append(" as ").append("version").toString();
			}

			else if (attrMap.getSourceAttr().getRef().getType().equals(MetaType.simple) && attrMap.getSourceAttr().getValue().toString().equalsIgnoreCase(SysParamsEnum.DAGEXEC_VERSION.toString())) {
				return builder.append("'" + execParams.getSysParams().get(SysParamsEnum.DAGEXEC_VERSION) + "'").append(" as ").append("version").toString();
			}
			
			else if (attrMap.getSourceAttr().getRef().getType().equals(MetaType.simple)) {
				return builder.append("\'").append(attrMap.getSourceAttr().getValue()).append("\'").append(" as ").append(alias).append(" ").toString();			
			} else if (attrMap.getSourceAttr().getRef().getType().equals(MetaType.paramlist)) {
				String value = metadataServiceImpl.getParamValue(execParams, Integer.parseInt(attrMap.getSourceAttr().getAttrId()), attrMap.getSourceAttr().getRef(), paramValMap);
//				boolean isNumber = Helper.isNumber(value);			
//				if(!isNumber) {
//					value = "'"+value+"'";
//				}
				return builder.append("\"").append(value).append("\"").append(" as ").append(alias).append(" ").toString();
			} 
			builder.append(sourceAttrSql(mapSource, attrMap.getSourceAttr(), refKeyMap, otherParams, execParams, paramValMap));
			if (otherParams != null && otherParams.containsKey("operatorType")
					&& otherParams.get("operatorType").equals(MetaType.mapiter.toString())
					&& !getTypeInSourceAttrs(attrMap.getSourceAttr(), MetaType.formula)) {
				return builder.append(" ").toString();
			}
			
		
		
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException();
		}
		return builder.append(" as ").append(alias).append(" ").toString();

		/*builder.append(" ( CASE ");
		for (SourceAttr sourceAttr : attrMap.getSourceAttr()) {
			builder.append(sourceAttrSql(daoRegister, mapSource, sourceAttr, refKeyMap, otherParams));
		}
		if (attrMap.getDefValue().getRef() != null) {
			builder.append(" ELSE ");
			String def = "0";
			Object object = daoRegister
					.getRefObject(TaskParser.populateRefVersion(attrMap.getDefValue().getRef(), refKeyMap));
			if (object instanceof Formula) {
				def = formulaOperator.generateSql((Formula) object, refKeyMap, otherParams);
			}
			
			if (object instanceof Expression) {
				def = expressionOperator.generateSql(((Expression) object).getExpressionInfo(),
						((Expression) object).getDependsOn(), refKeyMap, otherParams);
			}
			if (object instanceof Condition) {
				def = joinKeyOperator.generateSql(((Condition) object).getConditionInfo(),
						((Condition) object).getDependsOn(), refKeyMap, otherParams);
			}
			if (object instanceof Datapod) {
				def = ((Datapod) object).getName();
				def = def.concat(".").concat(datapodServiceImpl.getAttributeName(((Datapod) object).getUuid(),
						attrMap.getDefValue().getAttributeId()));
			}
			builder.append(def);
		}
		builder.append(" END ) ");*/
	}

	@SuppressWarnings("unlikely-arg-type")
	public String sourceAttrSql(MetaIdentifierHolder mapSource, AttributeRefHolder sourceAttr,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams, 
			Map<String, String> paramValMap) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder builder = new StringBuilder();
//		Object object = daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
		MetaIdentifier ref = TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap);
		Object object = commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
		Object mapSourceObj = commonServiceImpl.getOneByUuidAndVersion(mapSource.getRef().getUuid(), mapSource.getRef().getVersion(), mapSource.getRef().getType().toString(),"N");
		Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(mapSourceObj);
		try {
			if ((mapSource.getRef().getType().equals(MetaType.relation) || mapSource.getRef().getType().equals(MetaType.datapod))  
					&& object instanceof Datapod) {
				Datapod datapod = (Datapod) object;
				return builder.append(datapod.sql(Integer.parseInt(sourceAttr.getAttrId()))).append(" ").toString();
			} else if ((mapSource.getRef().getType().equals(MetaType.relation) || mapSource.getRef().getType().equals(MetaType.dataset))  
					&& (object instanceof DataSet)) {
				DataSet dataset = (DataSet) object;
				return builder.append(datasetServiceImpl.getAttributeSql(dataset, sourceAttr.getAttrId())).append(" ").toString();
			}
			if (mapSource.getRef().getType().equals(MetaType.dataset) && (object instanceof DataSet)) {
//				DataSet dataset = (DataSet) daoRegister.getRefObject(TaskParser.populateRefVersion(mapSource.getRef(), refKeyMap));
				MetaIdentifier mapSourceRef = TaskParser.populateRefVersion(mapSource.getRef(), refKeyMap);
				DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(mapSourceRef.getUuid(), mapSourceRef.getVersion(), mapSourceRef.getType().toString(),"N");
				return builder.append(datasetServiceImpl.getAttributeSql(dataset, sourceAttr.getAttrId())).append(" ").toString();
				
			}
			if (mapSource.getRef().getType().equals(MetaType.rule) && (object instanceof Rule)) {
//				Rule rule = (Rule) daoRegister.getRefObject(TaskParser.populateRefVersion(mapSource.getRef(), refKeyMap));
				MetaIdentifier mapSourceRef = TaskParser.populateRefVersion(mapSource.getRef(), refKeyMap);
				Rule rule = (Rule) commonServiceImpl.getOneByUuidAndVersion(mapSourceRef.getUuid(), mapSourceRef.getVersion(), mapSourceRef.getType().toString(),"N");
				return builder.append(ruleServiceImpl.getAttributeSql(rule, sourceAttr.getAttrId())).append(" ").toString();
			}

			if (object instanceof Expression) {
				// if (sourceAttr.getCondition().getRef() == null) {

				return builder.append("CASE WHEN ")
						.append(expressionOperator.generateSql(((Expression) object).getExpressionInfo(),
								((Expression) object).getDependsOn(), refKeyMap, otherParams, execParams, mapSourceDS, paramValMap))
						.append(" THEN ")
						.append(expressionOperator.generateMetCondition(((Expression) object).getMatch(), null, refKeyMap,
								otherParams, execParams, mapSourceDS, paramValMap))
						.append(" ELSE ").append(expressionOperator
								.generateNotMetCondition(((Expression) object).getNoMatch(), null, refKeyMap, otherParams, execParams, mapSourceDS, paramValMap)).
						append(" END ").append(" ").toString();
				 
				// }
				// Condition condition = (Condition) daoRegister
				// .getRefObject(TaskParser.populateRefVersion(sourceAttr.getCondition().getRef(),
				// refKeyMap));

				// builder.append(" when ")
				// .append(joinKeyOperator.generateSql(condition.getConditionInfo(),
				// condition.getDependsOn(),refKeyMap, otherParams))
				// .append(" then
				// ").append(expressionOperator.generateSql(((Expression)
				// object).getExpressionInfo(),((Expression)
				// object).getDependsOn(),refKeyMap, otherParams)).append(" ");
			}

			if (object instanceof Formula) {
				//if (sourceAttr.getCondition() == null) {
				return formulaOperator.generateSql((Formula) object, refKeyMap, otherParams, execParams, mapSourceDS, paramValMap);
				//}
				/*Condition condition = (Condition) daoRegister
						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getCondition().getRef(), refKeyMap));
				builder.append(" when ")
						.append(joinKeyOperator.generateSql(condition.getConditionInfo(), condition.getDependsOn(),
								refKeyMap, otherParams))
						.append(" then ").append(formulaOperator.generateSql((Formula) object, refKeyMap, otherParams))
						.append(" ");*/
			}
			
			if (object instanceof Function) {
				Function function = (Function) object;
				if (function == null || (function.getInputReq() != null && function.getInputReq().equalsIgnoreCase("Y"))) {
					logger.error("Function with parameters not allowed here");
					try {
						throw new Exception (" Function with parameters not allowed outside Formula ");
					} catch (Exception e) {
						e.printStackTrace();
					}
					return "";
				}
				/*if (function.getInputReq() == null || function.getInputReq().equalsIgnoreCase("N")) {
					functionOperator.setRunMode(runMode);
					if (function.getFunctionInfo().contains("(")) {
						return functionOperator.generateSql((Function) object, refKeyMap, otherParams);
					}
					return functionOperator.generateSql((Function) object, refKeyMap, otherParams);//.concat("()");
				}*/
				if (function.getInputReq() == null || function.getInputReq().equalsIgnoreCase("N")) {
					functionOperator.setRunMode(runMode);
					if (function.getFunctionInfo().contains("(")) {
						return functionOperator.generateSql((Function) object, refKeyMap, otherParams, mapSourceDS);
					}
					return functionOperator.generateSql((Function) object, refKeyMap, otherParams, mapSourceDS);//.concat("()");
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException();
		}
		//logger.info("sourceAttrSql(): query ---> "+builder.toString());
		return builder.toString();
	}
	
	public String getAttributeAlias(AttributeMap attrMap, MetaIdentifierHolder mapSource,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams) throws JsonProcessingException {
		Datapod datapod = null;
		String alias = null;
		if (attrMap.getTargetAttr() != null && attrMap.getTargetAttr().getRef() != null) { // Set attribute alias as corrs. target attribute
//			datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(attrMap.getTargetAttr().getRef(), refKeyMap));
			MetaIdentifier ref = TaskParser.populateRefVersion(attrMap.getTargetAttr().getRef(), refKeyMap);
			datapod = (Datapod)commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
			alias = datapod.getAttribute(Integer.parseInt(attrMap.getTargetAttr().getAttrId())).getName();
		} else { // If target attribute is not present, set attribute alias as source attribute alias appended by attributeid (as two source attrs. may have the same name
			alias = sourceAttrAlias(mapSource, attrMap.getSourceAttr(), refKeyMap, otherParams)
					.concat(attrMap.getSourceAttr().getAttrId());
		} 
		return alias;
	}
	
	/**
	 * Get alias name for source attribute (to be used when target attribute is not present in a query)
	 * @param daoRegister
	 * @param mapSource
	 * @param sourceAttr
	 * @param refKeyMap
	 * @param otherParams
	 * @return
	 * @throws JsonProcessingException 
	 */
	public String sourceAttrAlias(MetaIdentifierHolder mapSource, AttributeRefHolder sourceAttr,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams) throws JsonProcessingException {
//		Object object = daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
		MetaIdentifier ref = TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap);
		Object object = commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
		if (sourceAttr.getRef().getType() == MetaType.simple || sourceAttr.getRef().getType() == MetaType.paramlist) {
			return sourceAttr.getAttrName();
		}
		if ((sourceAttr.getRef().getType() == MetaType.relation || sourceAttr.getRef().getType() == MetaType.datapod) 
				&& (object instanceof Datapod)) {
			Datapod datapod = (Datapod) object;
			return datapod.getAttribute(Integer.parseInt(sourceAttr.getAttrId())).getName();
		}
		if (sourceAttr.getRef().getType() == MetaType.dataset && (object instanceof DataSet)) {
//			DataSet dataset = (DataSet) daoRegister .getRefObject(TaskParser.populateRefVersion(mapSource.getRef(), refKeyMap));
			MetaIdentifier mapSourceRef = TaskParser.populateRefVersion(mapSource.getRef(), refKeyMap);
			DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(mapSourceRef.getUuid(), mapSourceRef.getVersion(), mapSourceRef.getType().toString(),"N");
			return datasetServiceImpl.getAttributeName(dataset, sourceAttr.getAttrId());
		}

		if (object instanceof Expression) {
			if (((Expression) object).getName() == null) {
				return MetaType.expression.toString();
			} else {
				return ((Expression) object).getName();
			}
		}

		if (object instanceof Formula) {
/*			if (sourceAttr.getCondition() == null) {
*/				if (((Formula) object).getName() == null) {
					return MetaType.formula.toString();
				} else {
					return ((Formula) object).getName();
				}
			/*}*/
			/*Condition condition = (Condition) daoRegister
					.getRefObject(TaskParser.populateRefVersion(sourceAttr.getCondition().getRef(), refKeyMap));
			if (((Condition) object).getName() == null) {
				return "Condition";
			} else {
				return ((Condition) object).getName();
			}*/
		}

		return "";
	}
	
	
	public boolean isGroupBy (List<AttributeMap> attrMapList, 
			java.util.Map<String, MetaIdentifier> refKeyMap, 
			HashMap<String, String> otherParam) throws JsonProcessingException {
		if (attrMapList == null || attrMapList.isEmpty()) {
			return false;
		}
		for (AttributeMap attr : attrMapList) {
			if (attr.getSourceAttr().getRef().getType() == MetaType.formula) {
//				Formula formula = (Formula) daoRegister.getRefObject(attr.getSourceAttr().getRef());
				MetaIdentifier ref = TaskParser.populateRefVersion(attr.getSourceAttr().getRef(), refKeyMap);
				Formula formula = (Formula) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(),"N");
				if (formulaOperator.isGroupBy(formula, refKeyMap, otherParam)) {
					return true;
				}
			} else if (attr.getSourceAttr().getRef().getType() == MetaType.expression) {
//				Expression expression = (Expression) daoRegister.getRefObject(attr.getSourceAttr().getRef());
				MetaIdentifier ref = TaskParser.populateRefVersion(attr.getSourceAttr().getRef(), refKeyMap);
				Expression expression = (Expression) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(),"N");
				if (expressionOperator.isGroupBy(expression.getExpressionInfo(), refKeyMap, otherParam)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String selectGroupBy(List<AttributeMap> attrMapList, 
			java.util.Map<String, MetaIdentifier> refKeyMap, 
			HashMap<String, String> otherParams, 
			ExecParams execParams, MetaIdentifierHolder mapSource, 
			Map<String, String> paramValMap) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder groupByStr = new StringBuilder("");// StringBuilder(" GROUP BY ");
		String groupInfo = "";
		boolean isGroupBy = false;
		if (attrMapList == null || attrMapList.isEmpty()) {
			return "";
		}
		/*if (!isGroupBy(attrMapList, refKeyMap, otherParams)) {
			return "";
		}*/
		Object mapSourceObj = commonServiceImpl.getOneByUuidAndVersion(mapSource.getRef().getUuid(), mapSource.getRef().getVersion(), mapSource.getRef().getType().toString(),"N");
		Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(mapSourceObj);
		for (AttributeMap attr : attrMapList) {
			if (attr.getSourceAttr().getRef().getType() == MetaType.datapod 
					|| attr.getSourceAttr().getRef().getType() == MetaType.dataset) {
				groupByStr.append(sourceAttrSql(attr.getSourceAttr(), attr.getSourceAttr(), null, null, execParams, paramValMap)).append(",");
				//groupByStr.append(attr.getSourceAttr().getAttrName()).append(",");
			} else if (attr.getSourceAttr().getRef().getType() == MetaType.expression) {
//				Expression expression = (Expression) daoRegister.getRefObject(attr.getSourceAttr().getRef());
				Expression expression = (Expression) commonServiceImpl.getOneByUuidAndVersion(attr.getSourceAttr().getRef().getUuid(), attr.getSourceAttr().getRef().getVersion(), attr.getSourceAttr().getRef().getType().toString(),"N");
				
				MetaIdentifierHolder exprSource = new MetaIdentifierHolder(expression.getRef(MetaType.expression));
				if(expression.getMatch().getRef().getType() == MetaType.formula  || expression.getNoMatch().getRef().getType() == MetaType.formula) {
//					Formula formula = (Formula) daoRegister.getRefObject(expression.getMatch().getRef());
					Formula formula = (Formula) commonServiceImpl.getOneByUuidAndVersion(expression.getMatch().getRef().getUuid(), expression.getMatch().getRef().getVersion(), expression.getMatch().getRef().getType().toString(),"N");
					if (formula.getFormulaType() == FormulaType.sum_aggr || formula.getFormulaType() == FormulaType.aggr) {
						isGroupBy = true;
					} else {
						groupByStr.append(selectGroupBy(createAttrMapWithSourceAttr(formula.getFormulaInfo()), refKeyMap, otherParams, execParams, exprSource, paramValMap));
					}
				}
				for (FilterInfo filterInfo : expression.getExpressionInfo()) {
					groupInfo = selectGroupBy(createAttrMapWithSourceAttr(filterInfo.getOperand()), refKeyMap, otherParams, execParams, exprSource, paramValMap);
					if (StringUtils.isNotBlank(groupInfo)) {
						groupByStr.append(groupInfo).append(",");
					}
				}
			} else if (attr.getSourceAttr().getRef().getType() == MetaType.formula) {
//				Formula formula = (Formula) daoRegister.getRefObject(attr.getSourceAttr().getRef());
				Formula formula = (Formula) commonServiceImpl.getOneByUuidAndVersion(attr.getSourceAttr().getRef().getUuid(), attr.getSourceAttr().getRef().getVersion(), attr.getSourceAttr().getRef().getType().toString(),"N");
				if (formula.getFormulaType() == FormulaType.sum_aggr || formula.getFormulaType() == FormulaType.aggr) {
					isGroupBy = true;
				} else {
					groupByStr.append(formulaOperator.generateSql(formula, refKeyMap, otherParams, execParams, mapSourceDS, paramValMap)).append(",");
//					groupByStr.append(selectGroupBy(createAttrMapWithSourceAttr(formula.getFormulaInfo()), refKeyMap, otherParams, execParams));
				}
			}
		}
		String groupBySr = groupByStr.toString();
		if (!isGroupBy || StringUtils.isBlank(groupBySr)) {
			return " ";
		}
		return " GROUP BY " + groupBySr.substring(0, groupBySr.length()-1);
	}
	
	public List<AttributeMap> createAttrMap (List<AttributeSource> attributeSourceList) {
		// Create AttributeMap
		List<AttributeMap> attrMapList = new ArrayList<>();
		AttributeMap attrMap = null; 
		AttributeRefHolder sourceAttr = null;
		for (AttributeSource sourceAttribute : attributeSourceList) {
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
	
	public List<AttributeMap> createAttrMapWithSourceAttr (List<SourceAttr> attributeSourceList) {
		// Create AttributeMap
		List<AttributeMap> attrMapList = new ArrayList<>();
		AttributeMap attrMap = null; 
		AttributeRefHolder sourceAttr = null;
		for (SourceAttr sourceAttribute : attributeSourceList) {
			sourceAttr = new AttributeRefHolder();
			sourceAttr.setAttrId(sourceAttribute.getAttributeId()+"");
			sourceAttr.setValue(sourceAttribute.getValue());
			sourceAttr.setAttrName(sourceAttribute.getAttributeName());
			sourceAttr.setRef(sourceAttribute.getRef());
			attrMap = new AttributeMap();
			attrMap.setSourceAttr(sourceAttr);
			attrMap.setAttrMapId(sourceAttribute.getAttributeId()+"");
			//attrMap.setDesc(sourceAttribute.getName());
			attrMapList.add(attrMap);
		}
		return attrMapList;
	}

}
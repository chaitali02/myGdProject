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
package com.inferyx.framework.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.python.antlr.PythonParser.attr_return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.FormulaType;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Vizpod;
import com.inferyx.framework.domain.Vizpod.AttributeDetails;
import com.inferyx.framework.operator.ExpressionOperator;
import com.inferyx.framework.operator.FilterOperator;
import com.inferyx.framework.operator.FormulaOperator;
import com.inferyx.framework.operator.RelationOperator;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.FormulaServiceImpl;
import com.inferyx.framework.service.FunctionServiceImpl;
import com.inferyx.framework.service.RegisterService;

@Component
public class VizpodParser {
   
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;	

	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private MetadataUtil daoRegister;
	@Autowired
	private FormulaOperator formulaOperator;
	@Autowired
	private ExpressionOperator expressionOperator;
	@Autowired
	private RelationOperator relationOperator;
	@Autowired
	private FilterOperator filterOperator;	
	@Autowired
	private FormulaServiceImpl formulaServiceImpl;
	@Autowired
	private FunctionServiceImpl functionServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	
	private final String WHERE_1_1 = " WHERE (1=1) ";

	static final Logger logger = Logger.getLogger(VizpodParser.class);

	public String toSql(Vizpod vizpod, String tableName, Set<MetaIdentifier> usedRefKeySet, Mode runMode,boolean flag) throws Exception {
		// Formation of SQL query
		StringBuilder selectBuilder = new StringBuilder();
		StringBuilder fromBuilder = new StringBuilder();
		StringBuilder whereBuilder = new StringBuilder();
		StringBuilder limitBuilder = new StringBuilder();
		StringBuilder groupByBuilder = new StringBuilder();
		boolean hasFuncInVal = false;
		String result = "";
		String comma = ",";
		String blankSpace = " ";
		selectBuilder.append("SELECT ");
		selectBuilder.append(blankSpace);
		groupByBuilder.append(blankSpace);
		groupByBuilder.append("GROUP BY");
		groupByBuilder.append(blankSpace);

		if ((MetaType.datapod).equals(vizpod.getSource().getRef().getType())) {
			StringBuilder finalBuilder = new StringBuilder();
			/** Add rownumber column - START **/
			/*selectBuilder.append("row_number() over (partition by 1) ").append(" as ").append(" rownum ");
			selectBuilder.append(comma);*/
			/** Add rownumber column - END **/
			
			if(flag)
			{
				if (vizpod.getDetailAttr().size() > 0) {
					for (AttributeDetails detailattrRefHolder : vizpod.getDetailAttr()) {
						String keyAttrName = datapodServiceImpl.getAttributeName(detailattrRefHolder.getRef().getUuid(),
								detailattrRefHolder.getAttributeId());
						selectBuilder.append(keyAttrName).append(" as ").append(keyAttrName).append(" ");
						selectBuilder.append(comma);
					}
				}
			}
					
					if (vizpod.getKeys().size() > 0) {
						for (AttributeDetails attrDet : vizpod.getKeys()) {
							String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
									attrDet.getAttributeId());
							selectBuilder.append(keyAttrName).append(" as ").append(keyAttrName).append(" ");
							selectBuilder.append(comma);
						}
					}
					if (vizpod.getGroups().size() > 0) {
						for (AttributeDetails attrDet : vizpod.getGroups()) {
							String groupAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
									attrDet.getAttributeId());
							selectBuilder.append(groupAttrName).append(" as ").append(groupAttrName).append(" ");
							selectBuilder.append(comma);
						}
					}
					
					if (vizpod.getValues().size() > 0 && flag==false) {
						// String prefix = "";
						for (AttributeDetails attrDet : vizpod.getValues()) {
							if(attrDet.getRef().getType() == MetaType.datapod){
								String valueAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
										attrDet.getAttributeId());
								// builder.append(prefix);
								// prefix = ",";
								if (StringUtils.isNotBlank(attrDet.getFunction())) {
									hasFuncInVal = true;
									selectBuilder.append(attrDet.getFunction()).append("(").append(valueAttrName).append(")")
									.append(" as ").append(attrDet.getFunction()).append("_").append(valueAttrName).append(" ");
								} else {
									selectBuilder.append(valueAttrName).append(" as ").append(valueAttrName).append(" ");
								}
								//selectBuilder.append(comma);
							}
							else if(attrDet.getRef().getType() == MetaType.formula)	{
								//Formula formula = formulaServiceImpl.findLatestByUuid(attrDet.getRef().getUuid());
								Formula formula = (Formula) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(), MetaType.formula.toString());
								 String FormulaSql = formulaOperator.generateSql(formula, null, null, null);						 
								 selectBuilder.append(FormulaSql).append(" as "+formula.getName()+ " ");
								 hasFuncInVal = formulaOperator.isGroupBy(formula, null, null);
								/*for(SourceAttr srcAttr : formula.getFormulaInfo())
								{
									if(srcAttr.getRef().getType() == MetaType.function)
									{	
										Function function = functionServiceImpl.findLatestByUuid(srcAttr.getRef().getUuid());
										selectBuilder.append(function.getFunctionInfo());
										hasFuncInVal = true;
									}
									if(srcAttr.getRef().getType() == MetaType.simple)
									{									
										selectBuilder.append(srcAttr.getValue());
										hasFuncInVal = true;
									}
									if(srcAttr.getRef().getType() == MetaType.datapod)
									{
										String valueAttrName = datapodServiceImpl.getAttributeName(srcAttr.getRef().getUuid(),
												srcAttr.getAttributeId());
										
									}
								}*/					
							}
							selectBuilder.append(comma);
						}
					}
				
			
//				else {
//					
//					
//					String uuid=vizpod.getSource().getRef().getUuid();
//					
//					List<AttributeRefHolder> AttrName=registerService.getAttributesByDatapod(uuid);
//					for(AttributeRefHolder attr : AttrName)
//					{
//						selectBuilder.append(attr.getAttrName()).append(" as ").append(attr.getAttrName()).append(" ");
//						selectBuilder.append(comma);	
//					}
//					System.out.println(AttrName);
//				}
		
			
			
	
			String selectedColumninStr = selectBuilder.length() > 0
					? selectBuilder.substring(0, selectBuilder.length() - 1) : "";
			finalBuilder.append(selectedColumninStr);

			if (StringUtils.isBlank(tableName) && vizpod.getSource().getRef().getType() == MetaType.datapod) {
				DataStore dataStore = dataStoreServiceImpl.findDataStoreByDatapod(vizpod.getSource().getRef().getUuid()).get(0);
				tableName = dataStoreServiceImpl.getTableNameByDatastore(dataStore.getUuid(), dataStore.getVersion(), runMode);
			}
			//Datapod datapod = datapodServiceImpl.findLatestByUuid(vizpod.getSource().getRef().getUuid());
			Datapod datapod = (Datapod) commonServiceImpl.getLatestByUuid(vizpod.getSource().getRef().getUuid(), MetaType.datapod.toString());
			finalBuilder.append("FROM");
			finalBuilder.append(blankSpace);
			finalBuilder.append(tableName).append(" "+datapod.getName());
			finalBuilder.append(blankSpace);
			// append Where
			if (vizpod.getFilterInfo().size() > 0) {
				whereBuilder.append(blankSpace);
				//whereBuilder.append("WHERE");
				whereBuilder.append(WHERE_1_1);
				whereBuilder.append(blankSpace);
				whereBuilder.append(filterOperator.generateSql(vizpod.getFilterInfo(), null, null, usedRefKeySet));

			}
			whereBuilder.append(blankSpace);
			finalBuilder.append(whereBuilder);

			if ((!vizpod.getKeys().isEmpty() && hasFuncInVal) || !vizpod.getGroups().isEmpty()) {
				finalBuilder.append("GROUP BY");
				finalBuilder.append(blankSpace);
				
				if (!vizpod.getKeys().isEmpty()) {
					for (AttributeDetails attrDet : vizpod.getKeys()) {
						String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
								attrDet.getAttributeId());
						finalBuilder.append(keyAttrName);
						finalBuilder.append(comma);
					}
				}

				if (!vizpod.getGroups().isEmpty()) {
					for (AttributeDetails attrDet : vizpod.getGroups()) {
						String groupAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
								attrDet.getAttributeId());
						finalBuilder.append(groupAttrName);
						finalBuilder.append(comma);
					}
				}
				
				// Include only non-aggregate formulae in group by clause 
				if (!vizpod.getValues().isEmpty()) {
					for (AttributeDetails attrDet : vizpod.getValues()) {
						Object object = daoRegister.getRefObject(attrDet.getRef());
						if ((object instanceof Formula) && (((Formula)object).getFormulaType() == FormulaType.aggr)) {
							continue;
						}
						String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
								attrDet.getAttributeId());
						//String datapodName = (datapodServiceImpl.findLatestByUuid(attrDet.getRef().getUuid())).getName();
						String datapodName = ((Datapod) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(), MetaType.datapod.toString())).getName();
						logger.info("datapodName : " + datapodName);
						finalBuilder.append(datapodName + "." + keyAttrName).append(comma);
					}
				}
			}
			result = finalBuilder.length() > 0 ? finalBuilder.substring(0, finalBuilder.length() - 1) : "";
			logger.info(String.format("Final Vizpod filter %s", result));
			// logger.debug(String.format("Final Vizpod filter %s", result));

		} else if ((MetaType.relation).equals(vizpod.getSource().getRef().getType())) {
			Relation relation = daoRegister.getRelationDao().findLatestByUuid(vizpod.getSource().getRef().getUuid(),
					new Sort(Sort.Direction.DESC, "version"));
			
			
			List<AttributeDetails> attrDetList = new LinkedList<>();
			if ( flag && vizpod.getDetailAttr().size() > 0 ) 
				for (AttributeDetails attrDet : vizpod.getDetailAttr()) 
					attrDetList.add(attrDet);
			
			if (vizpod.getKeys().size() > 0 ) 
				for (AttributeDetails attrDet : vizpod.getKeys()) 
					attrDetList.add(attrDet);
			if (vizpod.getGroups().size() > 0) 
				for (AttributeDetails attrDet : vizpod.getGroups()) 
					attrDetList.add(attrDet);			
			if (vizpod.getValues().size() > 0 && flag==false) 
				for (AttributeDetails attrDet : vizpod.getValues()) 
					attrDetList.add(attrDet);
			
			/** Add rownumber column - START 
			selectBuilder.append("row_number() over (partition by 1) ").append(" as ").append(" rownum ");
			selectBuilder.append(comma);
			 Add rownumber column - END **/
	
			// append Select
			
		
			for (AttributeDetails attrDet : attrDetList) {
				if ((MetaType.datapod).equals(attrDet.getRef().getType())) {
					String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
							attrDet.getAttributeId());
					//String datapodName = (datapodServiceImpl.findLatestByUuid(attrDet.getRef().getUuid())).getName();
					String datapodName = ((Datapod) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(), MetaType.datapod.toString())).getName();
					logger.info("datapodName : " + datapodName);
					if (attrDet.getFunction() != null && !attrDet.getFunction().isEmpty()) {
						selectBuilder.append(attrDet.getFunction() + "(" + datapodName + "." + keyAttrName + ")");
						keyAttrName = attrDet.getFunction() + "_" + keyAttrName;
					} else {
						selectBuilder.append(datapodName + "." + keyAttrName);
						//groupByBuilder.append(datapodName + "." + keyAttrName).append(comma);
					}
					selectBuilder.append(" as ").append(keyAttrName).append(" ");
					selectBuilder.append(comma);
				} else if ((MetaType.expression).equals(attrDet.getRef().getType())) {
					Expression expression = daoRegister.getExpressionDao().findLatestByUuid(attrDet.getRef().getUuid(),
							new Sort(Sort.Direction.DESC, "version"));
					selectBuilder.append(expressionOperator.generateSql(expression.getExpressionInfo(),expression.getDependsOn(),null,null, null)).append(" as ")
							.append("expression_" + expression.getName());
					selectBuilder.append(comma);
					if (attrDet.getFunction() == null
							|| (attrDet.getFunction() != null && attrDet.getFunction().isEmpty())) {
						//groupByBuilder.append(expression.getExpressionInfo()).append(comma);
					}
				} else if ((MetaType.formula).equals(attrDet.getRef().getType())) {
					Formula formula = daoRegister.getFormulaDao().findLatestByUuid(attrDet.getRef().getUuid(),
							new Sort(Sort.Direction.DESC, "version"));
					String formulaSql = formulaOperator.generateSql(formula, null, null, null);
					if (attrDet.getFunction() != null && !attrDet.getFunction().isEmpty()) {
						selectBuilder.append(attrDet.getFunction() + "(" + formulaSql + ")");
						//groupByBuilder.append(formulaSql).append(comma);
					} else {
						selectBuilder.append(formulaSql);
						//groupByBuilder.append(formulaSql).append(comma);
					}						
					selectBuilder.append(" as ").append(formula.getName());
					selectBuilder.append(comma);
				}
			}

			// append From
			fromBuilder.append(blankSpace);
			fromBuilder.append("FROM");
			fromBuilder.append(blankSpace);

			// Append Join
			fromBuilder.append(relationOperator.generateSql(relation, null, null, null, usedRefKeySet, runMode));
			fromBuilder.append(blankSpace);
			
			// append Where
			if (vizpod.getFilterInfo().size() > 0) {
				whereBuilder.append(blankSpace);
				//whereBuilder.append("WHERE");
				whereBuilder.append(WHERE_1_1);
				whereBuilder.append(blankSpace);
				whereBuilder.append(filterOperator.generateSql(vizpod.getFilterInfo(), null, null, usedRefKeySet));

			}
			whereBuilder.append(blankSpace);

			// append Group by

			if (vizpod.getKeys().size() > 0 ) {
				for (AttributeDetails attrDet : vizpod.getKeys() ) {
					String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
							attrDet.getAttributeId());
					//String datapodName = (datapodServiceImpl.findLatestByUuid(attrDet.getRef().getUuid())).getName();
					String datapodName = ((Datapod) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(), MetaType.datapod.toString())).getName();
					logger.info("datapodName : " + datapodName);
					groupByBuilder.append(datapodName + "." + keyAttrName).append(comma);
				}
			}			
			
			if (vizpod.getGroups().size() > 0) {
				for (AttributeDetails attrDet : vizpod.getGroups()) {
					String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
							attrDet.getAttributeId());
					//String datapodName = (datapodServiceImpl.findLatestByUuid(attrDet.getRef().getUuid())).getName();
					String datapodName = ((Datapod) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(), MetaType.datapod.toString())).getName();
					logger.info("datapodName : " + datapodName);
					groupByBuilder.append(datapodName + "." + keyAttrName).append(comma);
				}
			}
			
			if(flag)
			{
				if (vizpod.getDetailAttr().size() > 0) {
					for (AttributeDetails detailattrRefHolder : vizpod.getDetailAttr()) {
						String keyAttrName = datapodServiceImpl.getAttributeName(detailattrRefHolder.getRef().getUuid(),
								detailattrRefHolder.getAttributeId());
						String datapodName = ((Datapod) commonServiceImpl.getLatestByUuid(detailattrRefHolder.getRef().getUuid(), MetaType.datapod.toString())).getName();
						logger.info("datapodName : " + datapodName);
						groupByBuilder.append(datapodName + "." + keyAttrName).append(comma);
						
					}
				}
			}
			
			// Include only non-aggregate formulae in group by clause 
			if (vizpod.getValues().size() > 0 && flag==false) {
				for (AttributeDetails attrDet : vizpod.getValues()) {
					Object object = daoRegister.getRefObject(attrDet.getRef());
					if ((object instanceof Formula) && (((Formula)object).getFormulaType() == FormulaType.aggr)) {
						continue;
					}
					String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
							attrDet.getAttributeId());
					//String datapodName = (datapodServiceImpl.findLatestByUuid(attrDet.getRef().getUuid())).getName();
					String datapodName = ((Datapod) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(), MetaType.datapod.toString())).getName();
					logger.info("datapodName : " + datapodName);
					groupByBuilder.append(datapodName + "." + keyAttrName).append(comma);
				}
			}
			
			// Limit Builder
			if (vizpod.getLimit() != null){
				limitBuilder.append(" limit " + vizpod.getLimit());
				limitBuilder.append(blankSpace);
			}
			
			result = selectBuilder.length() > 0 ? selectBuilder.substring(0, selectBuilder.length() - 1) : "";
			result += fromBuilder.length() > 0 ? fromBuilder.substring(0, fromBuilder.length() - 1) : "";
			result += whereBuilder.length() > 0 ? whereBuilder.substring(0, whereBuilder.length() - 1) : "";
			result += groupByBuilder.length() > 0 ? groupByBuilder.substring(0, groupByBuilder.length() - 1) : "";
			result += limitBuilder.length() > 0 ? limitBuilder.substring(0, limitBuilder.length() - 1) : "";
			logger.info(String.format("Final Vizpod filter %s", result));
		}
		return result;
	}
	
}
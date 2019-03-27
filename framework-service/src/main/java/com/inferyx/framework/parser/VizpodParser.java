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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.FormulaType;
import com.inferyx.framework.domain.Key;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Vizpod;
import com.inferyx.framework.domain.Vizpod.AttributeDetails;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.operator.DatasetOperator;
import com.inferyx.framework.operator.ExpressionOperator;
import com.inferyx.framework.operator.FilterOperator2;
import com.inferyx.framework.operator.FormulaOperator;
import com.inferyx.framework.operator.RelationOperator;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;

@Component
public class VizpodParser {

	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	@Autowired
	private FormulaOperator formulaOperator;
	@Autowired
	private ExpressionOperator expressionOperator;
	@Autowired
	private RelationOperator relationOperator;
	@Autowired
	private FilterOperator2 filterOperator2;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private DatasetOperator datasetOperator;

	private final String WHERE_1_1 = " WHERE (1=1) ";

	static final Logger logger = Logger.getLogger(VizpodParser.class);

	public String toSql(Vizpod vizpod, String tableName, Set<MetaIdentifier> usedRefKeySet, boolean allowColNameInFltr,
			RunMode runMode, boolean flag) throws Exception {
		// Formation of SQL query
		StringBuilder selectBuilder = new StringBuilder();
		StringBuilder fromBuilder = new StringBuilder();
		StringBuilder whereBuilder = new StringBuilder();
		StringBuilder limitBuilder = new StringBuilder();
		StringBuilder groupByBuilder = new StringBuilder();
		StringBuilder havingBuilder = new StringBuilder();
		StringBuilder orderByBuilder = new StringBuilder();
		StringBuilder outerSelectBuilder = new StringBuilder();
		StringBuilder outerGroupByBuilder = new StringBuilder();
		boolean hasFuncInVal = false;
		boolean flaghasFuncInVal = false;
		String result = "";
		String comma = ",";
		String blankSpace = " ";
		selectBuilder.append("SELECT ").append(blankSpace);
		outerSelectBuilder.append("SELECT ").append(blankSpace);
		groupByBuilder.append(blankSpace).append("GROUP BY").append(blankSpace);
		// outerGroupByBuilder.append(blankSpace).append("GROUP BY").append(blankSpace);
		// String formulaSql2 = "";
		Datapod datapod = (Datapod) commonServiceImpl.getLatestByUuid(vizpod.getSource().getRef().getUuid(),
				MetaType.datapod.toString(), "N");

		if ((MetaType.datapod).equals(vizpod.getSource().getRef().getType())) {
			StringBuilder finalBuilder = new StringBuilder();
			if (flag) {
				if (vizpod.getDetailAttr().size() > 0) {
					for (AttributeDetails detailattrRefHolder : vizpod.getDetailAttr()) {
						String keyAttrName = datapodServiceImpl.getAttributeName(detailattrRefHolder.getRef().getUuid(),
								detailattrRefHolder.getAttributeId());
						outerSelectBuilder.append(keyAttrName).append(" as ").append(keyAttrName).append(" ");
						outerSelectBuilder.append(comma);
						// selectBuilder.append(keyAttrName).append(" as ").append(keyAttrName).append("
						// ");
						// selectBuilder.append(comma);
					}
				}
			}

			if (vizpod.getKeys().size() > 0) {
				for (AttributeDetails attrDet : vizpod.getKeys()) {

					if (attrDet.getRef().getType() == MetaType.datapod) {
						String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
								attrDet.getAttributeId());
						outerSelectBuilder.append(keyAttrName).append(" as ").append(keyAttrName).append(" ");
						outerSelectBuilder.append(comma);
						// selectBuilder.append(keyAttrName).append(" as ").append(keyAttrName).append("
						// ");
						// selectBuilder.append(comma);
					} else if (attrDet.getRef().getType() == MetaType.formula) {
						Formula formula = (Formula) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(),
								MetaType.formula.toString());
						Datasource vizDS = commonServiceImpl.getDatasourceByObject(vizpod);
						String FormulaSql = formulaOperator.generateSql(formula, null, null, null, vizDS);
						if (StringUtils.isNotBlank(attrDet.getFunction())) {

							outerSelectBuilder.append(attrDet.getFunction()).append("(").append(datapod.getName())
									.append(".").append(formula.getName()).append(")")
									.append(" as " + formula.getName() + " ");
							flaghasFuncInVal = true;
						} else {

							outerSelectBuilder.append(FormulaSql).append(" as " + formula.getName() + " ");
						}
						outerSelectBuilder.append(comma);
						// selectBuilder.append(FormulaSql).append(" as " + formula.getName() + " ");
						flaghasFuncInVal = formulaOperator.isGroupBy(formula, null, null);
					}
				}
			}
			if (vizpod.getGroups().size() > 0) {
				for (AttributeDetails attrDet : vizpod.getGroups()) {
					if (attrDet.getRef().getType() == MetaType.datapod) {
						String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
								attrDet.getAttributeId());
						outerSelectBuilder.append(keyAttrName).append(" as ").append(keyAttrName).append(" ");
						outerSelectBuilder.append(comma);
						flaghasFuncInVal = true;
						// selectBuilder.append(keyAttrName).append(" as ").append(keyAttrName).append("
						// ");
						// selectBuilder.append(comma);
					} else if (attrDet.getRef().getType() == MetaType.formula) {
						Formula formula = (Formula) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(),
								MetaType.formula.toString());
						Datasource vizDS = commonServiceImpl.getDatasourceByObject(vizpod);
						String FormulaSql = formulaOperator.generateSql(formula, null, null, null, vizDS);
						if (StringUtils.isNotBlank(attrDet.getFunction())) {

							outerSelectBuilder.append(attrDet.getFunction()).append("(").append(datapod.getName())
									.append(".").append(formula.getName()).append(")")
									.append(" as " + formula.getName() + " ");
							flaghasFuncInVal = true;
						} else {

							outerSelectBuilder.append(FormulaSql).append(" as " + formula.getName() + " ");
						}
						outerSelectBuilder.append(comma);
						// selectBuilder.append(FormulaSql).append(" as " + formula.getName() + " ");
						flaghasFuncInVal = formulaOperator.isGroupBy(formula, null, null);
					}
				}
			}

			// add all attr to internal select ...
			if (datapod.getAttributes() != null) {
				int i = 0;
				for (Attribute attrDet : datapod.getAttributes()) {

					selectBuilder.append(attrDet.getName()).append(" as ").append(attrDet.getName()).append(" ");
					if (i < datapod.getAttributes().size() - 1)
						selectBuilder.append(comma);
					i++;
				}
			}

			if (vizpod.getValues().size() > 0 && flag == false) {
				for (AttributeDetails attrDet : vizpod.getValues()) {
					if (attrDet.getRef().getType() == MetaType.datapod) {
						String valueAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
								attrDet.getAttributeId());
						if (StringUtils.isNotBlank(attrDet.getFunction())) {
							hasFuncInVal = true;
							flaghasFuncInVal = true;
							outerSelectBuilder.append(attrDet.getFunction()).append("(").append(valueAttrName)
									.append(")").append(" as ").append(valueAttrName).append(" ");
							// selectBuilder.append(attrDet.getFunction()).append("(").append(valueAttrName).append(")")
							// .append(" as ").append(valueAttrName).append(" ");
						} else {
							outerSelectBuilder.append(valueAttrName).append(" as ").append(valueAttrName).append(" ");
							// selectBuilder.append(valueAttrName).append(" as
							// ").append(valueAttrName).append(" ");
						}
					} else if (attrDet.getRef().getType() == MetaType.formula) {
						Formula formula = (Formula) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(),
								MetaType.formula.toString());
						Datasource vizDS = commonServiceImpl.getDatasourceByObject(vizpod);
						String FormulaSql = formulaOperator.generateSql(formula, null, null, null, vizDS);
						if (StringUtils.isNotBlank(attrDet.getFunction())) {

							outerSelectBuilder.append(attrDet.getFunction()).append("(").append(datapod.getName())
									.append(".").append(formula.getName()).append(")")
									.append(" as " + formula.getName() + " ");
							flaghasFuncInVal = true;
						} else {

							outerSelectBuilder.append(FormulaSql).append(" as " + formula.getName() + " ");
						}

						// selectBuilder.append(FormulaSql).append(" as " + formula.getName() + " ");
						flaghasFuncInVal = formulaOperator.isGroupBy(formula, null, null);
					}
					// selectBuilder.append(comma);

					outerSelectBuilder.append(comma);
				}
			}

			String selectedColumninStr = selectBuilder.length() > 0
					? selectBuilder.substring(0, selectBuilder.length() - 1)
					: "";

			finalBuilder.append(selectedColumninStr);
			outerSelectBuilder.deleteCharAt(outerSelectBuilder.lastIndexOf(","));

			if (StringUtils.isBlank(tableName) && vizpod.getSource().getRef().getType() == MetaType.datapod) {
				Key dpKey = new Key();
				dpKey.setUUID(vizpod.getSource().getRef().getUuid());
				dpKey.setVersion(vizpod.getSource().getRef().getVersion());
				
				tableName = datapodServiceImpl.getTableNameByDatapodKey(dpKey,runMode);
//				List<DataStore> listDataStore = dataStoreServiceImpl
//						.findDataStoreByDatapod(vizpod.getSource().getRef().getUuid());
//				if (listDataStore != null && !listDataStore.isEmpty()) {
//					DataStore dataStore = listDataStore.get(0);
//					tableName = dataStoreServiceImpl.getTableNameByDatastoreKey(dataStore.getUuid(),
//							dataStore.getVersion(), runMode);
//				} else {
//					throw new RuntimeException(
//							"No datastore available for datapod " + vizpod.getSource().getRef().getName());
//				}
			}

			finalBuilder.append(" 	FROM");
			finalBuilder.append(blankSpace);
			finalBuilder.append(tableName).append(" " + datapod.getName());
			finalBuilder.append(blankSpace);
			// append Where
			if (vizpod.getFilterInfo().size() > 0) {
				whereBuilder.append(blankSpace);
				whereBuilder.append(WHERE_1_1);
				whereBuilder.append(blankSpace);
				Datasource datasource = commonServiceImpl.getDatasourceByObject(vizpod);
				whereBuilder.append(filterOperator2.generateSql(vizpod.getFilterInfo(), null, null, usedRefKeySet,
						false, false, runMode, datasource));
				if (allowColNameInFltr) {
					Pattern pattern = Pattern.compile("(\\b(\\w+)\\.)(?=([^\"']*[\"'][^\"']*[\"'])*[^\"']*$)");
					Matcher matcher = pattern.matcher(whereBuilder);
					while (matcher.find()) {
						if (!NumberUtils.isCreatable(matcher.group()))
							whereBuilder = new StringBuilder(whereBuilder.toString().replace(matcher.group(), ""));
					}
				}
			}
			whereBuilder.append(blankSpace);
			finalBuilder.append(whereBuilder.toString());

			if ((!vizpod.getKeys().isEmpty() && flaghasFuncInVal) || !vizpod.getGroups().isEmpty()) {
				// finalBuilder.append("GROUP BY");
				// finalBuilder.append(blankSpace);
				if (flaghasFuncInVal) {
					outerGroupByBuilder.append("GROUP BY");
					outerGroupByBuilder.append(blankSpace);
				}
				if (!vizpod.getKeys().isEmpty()) {
					for (AttributeDetails attrDet : vizpod.getKeys()) {

						if (attrDet.getRef().getType().equals(MetaType.formula)) {

							Formula formula = (Formula) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(),
									MetaType.formula.toString());
							if (formula.getFormulaType() == FormulaType.aggr) {
								continue;
							} else {
								outerGroupByBuilder.append(formula.getName()).append(", ");
							}
						} else {
							outerGroupByBuilder.append(attrDet.getAttributeName()).append(", ");
						}

						// String keyAttrName =
						// datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
						// attrDet.getAttributeId());
						// // finalBuilder.append(keyAttrName);
						// // finalBuilder.append(comma);
						// if (flaghasFuncInVal) {
						// outerGroupByBuilder.append(keyAttrName);
						// outerGroupByBuilder.append(comma);
						// }
					}

				}

				if (!vizpod.getGroups().isEmpty()) {
					for (AttributeDetails attrDet : vizpod.getGroups()) {
						String groupAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
								attrDet.getAttributeId());
						// finalBuilder.append(groupAttrName);
						// finalBuilder.append(comma);
						if (flaghasFuncInVal) {
							outerGroupByBuilder.append(groupAttrName);
							outerGroupByBuilder.append(", ");
						}
					}
				}

				// Include only non-aggregate formulae in group by clause
				if (!vizpod.getValues().isEmpty()) {
					for (AttributeDetails attrDet : vizpod.getValues()) {
						// Object object = daoRegister.getRefObject(attrDet.getRef());
						if (StringUtils.isNotBlank(attrDet.getFunction())) {
							continue;
						}
						if (attrDet.getRef().getType().equals(MetaType.formula)) {

							Formula formula = (Formula) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(),
									MetaType.formula.toString());
							if (formula.getFormulaType() == FormulaType.aggr) {
								continue;
							} else {
								outerGroupByBuilder.append(formula.getName()).append(", ");
							}
						} else {
							String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
									attrDet.getAttributeId());
							String datapodName = ((Datapod) commonServiceImpl
									.getLatestByUuid(attrDet.getRef().getUuid(), MetaType.datapod.toString(), "N"))
											.getName();
							logger.info("datapodName : " + datapodName);
							// finalBuilder.append(datapodName + "." + keyAttrName).append(comma);
							outerGroupByBuilder.append(datapodName + "." + keyAttrName).append(", ");
						}
					}
					if (outerGroupByBuilder.toString().endsWith(", ")) {
						outerGroupByBuilder.replace(outerGroupByBuilder.lastIndexOf(","), outerGroupByBuilder.length(),
								"");
					}

					/*
					 * if (outerGroupByBuilder.toString().endsWith(",")) { //
					 * finalBuilder.replace(finalBuilder.length() - 1, finalBuilder.length(), "");
					 * if (flaghasFuncInVal)
					 * outerGroupByBuilder.replace(outerGroupByBuilder.length() - 1,
					 * outerGroupByBuilder.length(), ""); }
					 */
				}

				// Having
				Datasource datasource = commonServiceImpl.getDatasourceByObject(vizpod);
				String havingStr = filterOperator2.generateSql(vizpod.getFilterInfo(), null, null, usedRefKeySet, true,
						true, runMode, datasource);
				if (org.apache.commons.lang3.StringUtils.isNotBlank(havingStr)) {
					finalBuilder.append(ConstantsUtil.HAVING_1_1).append(havingStr);
				}
			}

			orderByBuilder = generateOderBy(vizpod.getSortBy(), vizpod.getSortOrder());
			if (orderByBuilder.length() > 0) {
				// finalBuilder.append(orderByBuilder).append(" ");
				result = outerSelectBuilder.append(" FROM (").append(finalBuilder).append(" ) ")
						.append(datapod.getName()).append(" ").append(outerGroupByBuilder).append(orderByBuilder)
						.append(" ").toString();
			} else {
				result = outerSelectBuilder.append(" FROM (").append(finalBuilder).append(" ) ")
						.append(datapod.getName()).append(" ").append(outerGroupByBuilder).toString();
			}
			logger.info(String.format("Final Vizpod filter %s", result));

		} else if ((MetaType.relation).equals(vizpod.getSource().getRef().getType())) {
			// Relation relation =
			// daoRegister.getRelationDao().findLatestByUuid(vizpod.getSource().getRef().getUuid(),new
			// Sort(Sort.Direction.DESC, "version"));
			Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(
					vizpod.getSource().getRef().getUuid(), vizpod.getSource().getRef().getVersion(),
					vizpod.getSource().getRef().getType().toString(), "N");

			List<AttributeDetails> attrDetList = new LinkedList<>();
			if (flag && vizpod.getDetailAttr().size() > 0)
				for (AttributeDetails attrDet : vizpod.getDetailAttr())
					attrDetList.add(attrDet);

			if (vizpod.getKeys().size() > 0)
				for (AttributeDetails attrDet : vizpod.getKeys())
					attrDetList.add(attrDet);
			if (vizpod.getGroups().size() > 0)
				for (AttributeDetails attrDet : vizpod.getGroups())
					attrDetList.add(attrDet);
			if (vizpod.getValues().size() > 0 && flag == false)
				for (AttributeDetails attrDet : vizpod.getValues())
					attrDetList.add(attrDet);

			// append Select
			for (AttributeDetails attrDet : attrDetList) {
				if ((MetaType.datapod).equals(attrDet.getRef().getType())) {
					String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
							attrDet.getAttributeId());
					String datapodName = ((Datapod) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(),
							MetaType.datapod.toString(), "N")).getName();
					logger.info("datapodName : " + datapodName);
					if (attrDet.getFunction() != null && !attrDet.getFunction().isEmpty()) {
						selectBuilder.append(attrDet.getFunction() + "(" + datapodName + "." + keyAttrName + ")");
						// keyAttrName = attrDet.getFunction() + "_" + keyAttrName;
					} else {
						selectBuilder.append(datapodName + "." + keyAttrName);
					}
					selectBuilder.append(" as ").append(keyAttrName).append(" ");
					selectBuilder.append(comma);
				} else if ((MetaType.expression).equals(attrDet.getRef().getType())) {
					// Expression expression =
					// daoRegister.getExpressionDao().findLatestByUuid(attrDet.getRef().getUuid(),
					// new Sort(Sort.Direction.DESC, "version"));
					Expression expression = (Expression) commonServiceImpl.getOneByUuidAndVersion(
							attrDet.getRef().getUuid(), attrDet.getRef().getVersion(),
							attrDet.getRef().getType().toString(), "N");
					Datasource datasource = commonServiceImpl.getDatasourceByObject(vizpod);
					selectBuilder
							.append(expressionOperator.generateSql(expression.getExpressionInfo(),
									expression.getDependsOn(), null, null, null, datasource))
							.append(" as ").append("expression_" + expression.getName());
					selectBuilder.append(comma);
					if (attrDet.getFunction() == null
							|| (attrDet.getFunction() != null && attrDet.getFunction().isEmpty())) {
					}
				} else if ((MetaType.formula).equals(attrDet.getRef().getType())) {
					// Formula formula =
					// daoRegister.getFormulaDao().findLatestByUuid(attrDet.getRef().getUuid(), new
					// Sort(Sort.Direction.DESC, "version"));
					Formula formula = (Formula) commonServiceImpl.getOneByUuidAndVersion(attrDet.getRef().getUuid(),
							attrDet.getRef().getVersion(), attrDet.getRef().getType().toString(), "N");
					Datasource datasource = commonServiceImpl.getDatasourceByObject(vizpod);
					String formulaSql = formulaOperator.generateSql(formula, null, null, null, datasource);
					if (attrDet.getFunction() != null && !attrDet.getFunction().isEmpty()) {
						selectBuilder.append(attrDet.getFunction() + "(" + formulaSql + ")");
					} else {
						selectBuilder.append(formulaSql);
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
				// whereBuilder.append("WHERE");
				whereBuilder.append(WHERE_1_1);
				whereBuilder.append(blankSpace);
				Datasource datasource = commonServiceImpl.getDatasourceByObject(vizpod);
				whereBuilder.append(filterOperator2.generateSql(vizpod.getFilterInfo(), null, null, usedRefKeySet,
						false, false, runMode, datasource));

				if (allowColNameInFltr) {
					Pattern pattern = Pattern.compile("(\\b(\\w+)\\.)(?=([^\"']*[\"'][^\"']*[\"'])*[^\"']*$)");
					Matcher matcher = pattern.matcher(whereBuilder);
					while (matcher.find()) {
						if (!NumberUtils.isCreatable(matcher.group()))
							whereBuilder = new StringBuilder(whereBuilder.toString().replace(matcher.group(), ""));
					}
				}
			}
			whereBuilder.append(blankSpace);

			// append Group by
			if (vizpod.getKeys().size() > 0) {
				for (AttributeDetails attrDet : vizpod.getKeys()) {

					if (attrDet.getFunction() == null) {
						Object object = commonServiceImpl.getOneByUuidAndVersion(attrDet.getRef().getUuid(),
								attrDet.getRef().getVersion(), attrDet.getRef().getType().toString(), "N");
						if ((object instanceof Formula) && (((Formula) object).getFormulaType() == FormulaType.aggr)) {
							continue;
						}
						String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
								attrDet.getAttributeId());
						String datapodName = ((Datapod) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(),
								MetaType.datapod.toString())).getName();
						logger.info("datapodName : " + datapodName);
						groupByBuilder.append(datapodName + "." + keyAttrName).append(comma);
					}
				}
			}

			if (vizpod.getGroups().size() > 0) {
				for (AttributeDetails attrDet : vizpod.getGroups()) {

					String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
							attrDet.getAttributeId());
					String datapodName = ((Datapod) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(),
							MetaType.datapod.toString())).getName();
					logger.info("datapodName : " + datapodName);
					groupByBuilder.append(datapodName + "." + keyAttrName).append(comma);
				}
			}

			if (flag) {
				if (vizpod.getDetailAttr().size() > 0) {
					for (AttributeDetails detailattrRefHolder : vizpod.getDetailAttr()) {
						String keyAttrName = datapodServiceImpl.getAttributeName(detailattrRefHolder.getRef().getUuid(),
								detailattrRefHolder.getAttributeId());
						String datapodName = ((Datapod) commonServiceImpl
								.getLatestByUuid(detailattrRefHolder.getRef().getUuid(), MetaType.datapod.toString()))
										.getName();
						logger.info("datapodName : " + datapodName);
						groupByBuilder.append(datapodName + "." + keyAttrName).append(comma);

					}
					groupByBuilder.replace(groupByBuilder.length() - 2, groupByBuilder.length(), "");
				}
			}

			// Include only non-aggregate formulae in group by clause
			if (vizpod.getValues().size() > 0 && flag == false) {
				for (AttributeDetails attrDet : vizpod.getValues()) {
					// Object object = daoRegister.getRefObject(attrDet.getRef());

					if (attrDet.getFunction() == null) {
						Object object = commonServiceImpl.getOneByUuidAndVersion(attrDet.getRef().getUuid(),
								attrDet.getRef().getVersion(), attrDet.getRef().getType().toString(), "N");
						if ((object instanceof Formula) && (((Formula) object).getFormulaType() == FormulaType.aggr)) {
							continue;
						}
						String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
								attrDet.getAttributeId());
						String datapodName = ((Datapod) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(),
								MetaType.datapod.toString(), "N")).getName();
						logger.info("datapodName : " + datapodName);
						groupByBuilder.append(datapodName + "." + keyAttrName).append(comma);
					}
				}
			}

			// Having Builder
			Datasource datasource = commonServiceImpl.getDatasourceByObject(vizpod);
			havingBuilder.append(filterOperator2.generateSql(vizpod.getFilterInfo(), null, null, usedRefKeySet, true,
					true, runMode, datasource));
			orderByBuilder = generateOderBy(vizpod.getSortBy(), vizpod.getSortOrder());
			// Limit Builder
			// if (vizpod.getLimit() != null){
			// limitBuilder.append(" limit " + vizpod.getLimit());
			// limitBuilder.append(blankSpace);
			// }

			result = selectBuilder.length() > 0 ? selectBuilder.substring(0, selectBuilder.length() - 1) : "";
			result += fromBuilder.length() > 0 ? fromBuilder.substring(0, fromBuilder.length() - 1) : "";
			result += (whereBuilder.length() > 0 ? whereBuilder.substring(0, whereBuilder.length() - 1) : "");
			result += groupByBuilder.length() > 0 ? groupByBuilder.substring(0, groupByBuilder.length() - 1) : "";
			// result += havingBuilder.length() > 0 ? havingBuilder.substring(0,
			// groupByBuilder.length() - 1) : "";
			result += StringUtils.isBlank(havingBuilder.toString()) ? ConstantsUtil.BLANK
					: ConstantsUtil.HAVING_1_1.concat(havingBuilder.toString());
			result += orderByBuilder.length() > 0 ? orderByBuilder.toString() : "";
			result += limitBuilder.length() > 0 ? limitBuilder.substring(0, limitBuilder.length() - 1) : "";
			logger.info(String.format("Final Vizpod filter %s", result));
		} else if ((MetaType.dataset).equals(vizpod.getSource().getRef().getType())) {
			DataSet dataSet = (DataSet) commonServiceImpl.getOneByUuidAndVersion(vizpod.getSource().getRef().getUuid(),
					vizpod.getSource().getRef().getVersion(), vizpod.getSource().getRef().getType().toString(), "N");
			List<AttributeDetails> attrDetList = new LinkedList<>();
			if (flag && vizpod.getDetailAttr().size() > 0)
				for (AttributeDetails attrDet : vizpod.getDetailAttr())
					attrDetList.add(attrDet);

			if (vizpod.getKeys().size() > 0)
				for (AttributeDetails attrDet : vizpod.getKeys())
					attrDetList.add(attrDet);
			if (vizpod.getGroups().size() > 0)
				for (AttributeDetails attrDet : vizpod.getGroups())
					attrDetList.add(attrDet);
			if (vizpod.getValues().size() > 0 && flag == false)
				for (AttributeDetails attrDet : vizpod.getValues())
					attrDetList.add(attrDet);

			List<AttributeSource> attributeInfo = new ArrayList<>();
			for (AttributeDetails attrDet : attrDetList) {
				for (AttributeSource attributeSource : dataSet.getAttributeInfo()) {
					if (attrDet.getRef().getType().equals(MetaType.datapod)
							|| attrDet.getRef().getType().equals(MetaType.dataset))
						if (attributeSource.getAttrSourceId().equalsIgnoreCase(attrDet.getAttributeId() + "")) {
							if (attrDet.getFunction() == null) {
								attributeInfo.add(attributeSource);
							} else {
								attributeSource.setFunction(attrDet.getFunction());
								attributeInfo.add(attributeSource);
							}

						}
				}
				if (attrDet.getRef().getType() == MetaType.formula) {
					AttributeSource attributeSource = new AttributeSource();
					AttributeRefHolder attributeRefHolder = new AttributeRefHolder();
					attributeRefHolder.setRef(attrDet.getRef());
					attributeRefHolder.setAttrId(String.valueOf(attrDet.getAttributeId()));
					attributeRefHolder.setAttrName(attrDet.getAttributeName());
					attributeSource.setSourceAttr(attributeRefHolder);
					attributeInfo.add(attributeSource);

				}

			}

			/********
			 * following commented code also works but to remove table name from filter
			 * query the custom code is written
			 *******/
			// result = datasetOperator.generateSql(dataSet, null, null, usedRefKeySet,
			// null, runMode);

			/********
			 * following custom code is written specifically to remove table name from
			 * filter query else above commented code can work
			 *******/
			String innerSql = datasetOperator.generateSql(dataSet, null, null, usedRefKeySet, null, runMode);

			dataSet.setAttributeInfo(attributeInfo);

//			String outerSql = datasetOperator.generateSql(dataSet, null, null, usedRefKeySet, null, runMode);

			StringBuilder queryBuilder = new StringBuilder();
			selectBuilder = new StringBuilder(generateSelectForDataSet(dataSet, vizpod));

			// if(formulaSql2 != null && !formulaSql2.isEmpty()) {
			// selectBuilder.append(", ").append(formulaSql2);
			// }
			// fromBuilder.append(" FROM ").append(datasetOperator.generateFrom(dataSet,
			// null, null, usedRefKeySet, runMode));

			fromBuilder.append(" FROM ( ").append(innerSql).append(" ) ").append(dataSet.getName()).append(" ");
			System.out.println(fromBuilder.toString());
			whereBuilder.append(datasetOperator.generateWhere());
			whereBuilder.append(" ")
					.append(datasetOperator.generateFilter(dataSet, null, null, usedRefKeySet, null, null));
			Datasource datasource = commonServiceImpl.getDatasourceByObject(vizpod);

			// List<AttributeRefHolder> tempFormulaInfo =
			// getFilterInfoWithoutFormula(vizpod.getFilterInfo());
			// whereBuilder.append(" ").append(filterOperator2.generateSql(tempFormulaInfo,
			// null, null, usedRefKeySet, false, false, runMode, datasource));
			whereBuilder.append(" ").append(filterOperator2.generateSql(vizpod.getFilterInfo(), null, null,
					usedRefKeySet, false, false, runMode, datasource));
			// if(allowColNameInFltr) {
			Pattern pattern = Pattern.compile("(\\b(\\w+)\\.)(?=([^\"']*[\"'][^\"']*[\"'])*[^\"']*$)");
			Matcher matcher = pattern.matcher(whereBuilder);
			while (matcher.find()) {
				if (!NumberUtils.isCreatable(matcher.group()))
					whereBuilder = new StringBuilder(whereBuilder.toString().replace(matcher.group(), ""));
			}
			// }
			groupByBuilder = new StringBuilder(generateGroupByForDataSet(dataSet, vizpod));
			// groupByBuilder = new StringBuilder(datasetOperator.generateGroupBy(dataSet,
			// null, null, null));
			havingBuilder = new StringBuilder(
					datasetOperator.generateHaving(dataSet, null, null, usedRefKeySet, null, null));
			// List<AttributeRefHolder> formulaFilterInfo =
			// getFilterInfoByFormula(vizpod.getFilterInfo());
			// if(formulaFilterInfo != null && !formulaFilterInfo.isEmpty()) {
			// String filterQuery = filterOperator2.generateSql(vizpod.getFilterInfo(),
			// null, null, usedRefKeySet, true, true, runMode, datasource);
			// if(havingBuilder != null && havingBuilder.length() > 0) {
			// havingBuilder.append(filterQuery);
			// } else {
			// havingBuilder.append(filterQuery);
			// }
			// }

			orderByBuilder = generateOderBy(vizpod.getSortBy(), vizpod.getSortOrder());

			queryBuilder.append(selectBuilder);
			queryBuilder.append(fromBuilder);
			queryBuilder.append(whereBuilder);
			queryBuilder.append(groupByBuilder);
			queryBuilder.append(StringUtils.isBlank(havingBuilder.toString()) ? ConstantsUtil.BLANK
					: ConstantsUtil.HAVING_1_1.concat(havingBuilder.toString()));
			queryBuilder.append(orderByBuilder);
			result = queryBuilder.toString();
		}
		return result;
	}

	private StringBuilder generateOderBy(List<AttributeDetails> sortBy, String sortOrder)
			throws JsonProcessingException {
		StringBuilder orderByBuilder = new StringBuilder();
		if (sortBy != null && !sortBy.isEmpty()) {
			orderByBuilder.append(" ORDER BY ");
			int i = 0;
			for (AttributeDetails attributeDetails : sortBy) {
				orderByBuilder.append(attributeDetails.getAttributeName());
				if (i < sortBy.size() - 1) {
					orderByBuilder.append(", ");
				}
				i++;
			}
			orderByBuilder.append(" ").append(sortOrder);
		}

		return orderByBuilder;
	}

	public List<AttributeRefHolder> getFilterInfoByFormula(List<AttributeRefHolder> filterInfo) {
		if (filterInfo != null) {
			List<AttributeRefHolder> formulaFilterInfo = new ArrayList<>();
			for (AttributeRefHolder attrRefHolder : filterInfo) {
				if (attrRefHolder.getRef().getType().equals(MetaType.formula)) {
					formulaFilterInfo.add(attrRefHolder);
				}
			}
			return formulaFilterInfo;
		}
		return null;
	}

	public List<AttributeRefHolder> getFilterInfoWithoutFormula(List<AttributeRefHolder> filterInfo) {
		if (filterInfo != null) {
			List<AttributeRefHolder> tempFormulaInfo = new ArrayList<>();
			for (AttributeRefHolder attrRefHolder : filterInfo) {
				if (!attrRefHolder.getRef().getType().equals(MetaType.formula)) {
					tempFormulaInfo.add(attrRefHolder);
				}
			}
			return tempFormulaInfo;
		}
		return null;
	}

	public StringBuilder generateSelectForDataSet(DataSet dataSet, Vizpod vizpod)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder selectBuilder = new StringBuilder();
		selectBuilder.append(" SELECT ");
		int i = 0;
		String datasetName = null;

		for (AttributeSource attributeSource : dataSet.getAttributeInfo()) {
			datasetName = dataSet.getName();
			if (attributeSource.getFunction() != null) {
				selectBuilder.append(attributeSource.getFunction()).append("( ").append(datasetName).append(".")
						.append(dataSet.getAttributeName(Integer.parseInt(attributeSource.getAttrSourceId())))
						.append(" )").append(" AS ")
						.append(dataSet.getAttributeName(Integer.parseInt(attributeSource.getAttrSourceId())));

			} else {
				if (attributeSource.getSourceAttr().getRef().getType().equals(MetaType.formula)) {
					// for formula
					if (attributeSource.getAttrSourceId() != null) {
						selectBuilder.append(datasetName).append(".")
								.append(dataSet.getAttributeName(Integer.parseInt(attributeSource.getAttrSourceId())))
								.append(" AS ")
								.append(dataSet.getAttributeName(Integer.parseInt(attributeSource.getAttrSourceId())));
					} else {

						Formula formula = (Formula) commonServiceImpl.getLatestByUuid(
								attributeSource.getSourceAttr().getRef().getUuid(), MetaType.formula.toString());
						Datasource vizDS = commonServiceImpl.getDatasourceByObject(vizpod);
						String FormulaSql = formulaOperator.generateSql(formula, null, null, null, vizDS);

						selectBuilder.append(FormulaSql).append(" as ").append(formula.getName());
					}

				} else {

					selectBuilder.append(datasetName).append(".")
							.append(dataSet.getAttributeName(Integer.parseInt(attributeSource.getAttrSourceId())))
							.append(" AS ")
							.append(dataSet.getAttributeName(Integer.parseInt(attributeSource.getAttrSourceId())));
				}
			}
			if (i < dataSet.getAttributeInfo().size() - 1) {
				selectBuilder.append(", ");
			}
			i++;
		}
		return selectBuilder;

	}

	public StringBuilder generateGroupByForDataSet(DataSet dataSet, Vizpod vizpod)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder groupByBuilder = new StringBuilder();
		groupByBuilder.append(" GROUP BY ");

		boolean isAnyFunction = false;
		for (AttributeSource attributeSource : dataSet.getAttributeInfo()) {
			if (attributeSource.getFunction() != null) {
				isAnyFunction = true;
			} else {
				if (attributeSource.getSourceAttr().getRef().getType().equals(MetaType.formula)) {

					if (attributeSource.getAttrSourceId() != null) {

						groupByBuilder
								.append(dataSet.getAttributeName(Integer.parseInt(attributeSource.getAttrSourceId())))
								.append(", ");
					} else {

						Object object = commonServiceImpl.getOneByUuidAndVersion(
								attributeSource.getSourceAttr().getRef().getUuid(),
								attributeSource.getSourceAttr().getRef().getVersion(),
								attributeSource.getSourceAttr().getRef().getType().toString(), "N");
						if ((object instanceof Formula) && (((Formula) object).getFormulaType() == FormulaType.aggr)) {
							continue;
						} else {

							Formula formula = (Formula) commonServiceImpl.getLatestByUuid(
									attributeSource.getSourceAttr().getRef().getUuid(), MetaType.formula.toString());
							/*
							 * Datasource vizDS = commonServiceImpl.getDatasourceByObject(vizpod); String
							 * FormulaSql = formulaOperator.generateSql(formula, null, null, null, vizDS);
							 */

							groupByBuilder.append(formula.getName()).append(", ");
						}
					}

				} else {
					groupByBuilder.append(dataSet.getAttributeName(Integer.parseInt(attributeSource.getAttrSourceId())))
							.append(", ");
				}
			}
		}
		if (isAnyFunction && groupByBuilder.toString().contains(",")) {
			return new StringBuilder(groupByBuilder.substring(0, groupByBuilder.lastIndexOf(",")));
		} else {
			return new StringBuilder();
		}

	}

}
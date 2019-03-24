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
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Formula;
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
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.DatasetServiceImpl;

@Component
public class VizpodDetailParser {

	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;

	@Autowired
	private RelationOperator relationOperator;
	@Autowired
	private FilterOperator2 filterOperator2;
	@Autowired
	private FormulaOperator formulaOperator;
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
		StringBuilder finalResultBuilder = new StringBuilder();

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

			if (vizpod.getDetailAttr().size() > 0) {
				for (AttributeDetails detailattrRefHolder : vizpod.getDetailAttr()) {
					if (detailattrRefHolder.getRef().getType() == MetaType.datapod) {
						String keyAttrName = datapodServiceImpl.getAttributeName(detailattrRefHolder.getRef().getUuid(),
								detailattrRefHolder.getAttributeId());
						selectBuilder.append(keyAttrName).append(" as ").append(keyAttrName).append(" ");
						selectBuilder.append(comma);
						// selectBuilder.append(keyAttrName).append(" as ").append(keyAttrName).append("
						// ");
						// selectBuilder.append(comma);
					} else if (detailattrRefHolder.getRef().getType() == MetaType.formula) {
						Formula formula = (Formula) commonServiceImpl
								.getLatestByUuid(detailattrRefHolder.getRef().getUuid(), MetaType.formula.toString());
						Datasource vizDS = commonServiceImpl.getDatasourceByObject(vizpod);
						String FormulaSql = formulaOperator.generateSql(formula, null, null, null, vizDS);
						// flaghasFuncInVal = formulaOperator.isGroupBy(formula, null, null);

					
							selectBuilder.append(FormulaSql).append(" as " + formula.getName() + " ");

							selectBuilder.append(comma);
					

						// selectBuilder.append(FormulaSql).append(" as " + formula.getName() + " ");
					}
				}
			}

			finalBuilder.append(selectBuilder);
			finalBuilder.deleteCharAt(finalBuilder.lastIndexOf(","));

			if (StringUtils.isBlank(tableName) && vizpod.getSource().getRef().getType() == MetaType.datapod) {
				List<DataStore> listDataStore = dataStoreServiceImpl
						.findDataStoreByDatapod(vizpod.getSource().getRef().getUuid());
				if (listDataStore != null && !listDataStore.isEmpty()) {
					DataStore dataStore = listDataStore.get(0);
					tableName = dataStoreServiceImpl.getTableNameByDatastoreKey(dataStore.getUuid(),
							dataStore.getVersion(), runMode);
				} else {
					throw new RuntimeException(
							"No datastore available for datapod " + vizpod.getSource().getRef().getName());
				}
			}

			finalBuilder.append(" FROM");
			finalBuilder.append(blankSpace);
			finalBuilder.append(tableName).append(" " + datapod.getName());
			finalBuilder.append(blankSpace);
			// append Where
			if (vizpod.getFilterInfo().size() > 0) {
				whereBuilder.append(blankSpace);
				whereBuilder.append(WHERE_1_1);
				whereBuilder.append(blankSpace);
				
				for (AttributeRefHolder attrDet : vizpod.getFilterInfo()) {
					if (attrDet.getRef().getType() == MetaType.datapod) {
						Datasource datasource = commonServiceImpl.getDatasourceByObject(vizpod);
						List<AttributeRefHolder> filterIdentifierList = new ArrayList<>();
						filterIdentifierList.add(attrDet);
						whereBuilder.append(filterOperator2.generateSql(filterIdentifierList, null, null,
								usedRefKeySet, false, false, runMode, datasource));

						if (allowColNameInFltr) {
							Pattern pattern = Pattern.compile("(\\b(\\w+)\\.)(?=([^\"']*[\"'][^\"']*[\"'])*[^\"']*$)");
							Matcher matcher = pattern.matcher(whereBuilder);
							while (matcher.find()) {
								if (!NumberUtils.isCreatable(matcher.group()))
									whereBuilder = new StringBuilder(
											whereBuilder.toString().replace(matcher.group(), ""));
							}
						}
					} else if (attrDet.getRef().getType() == MetaType.formula) {
						Formula formula = (Formula) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(),
								MetaType.formula.toString());
						Datasource vizDS = commonServiceImpl.getDatasourceByObject(vizpod);
						String FormulaSql = formulaOperator.generateSql(formula, null, null, null, vizDS);
						// flaghasFuncInVal = formulaOperator.isGroupBy(formula, null, null);

						if (StringUtils.isNotBlank(attrDet.getFunction())) {

							whereBuilder.append(" AS " + formula.getName() + "= ");

						} else {

							whereBuilder.append("AND (").append(FormulaSql).append(" = ")
									.append("'" + attrDet.getValue() + "'").append(")");
						}
						whereBuilder.append(comma);
						// selectBuilder.append(FormulaSql).append(" as " + formula.getName() + " ");
					}
				}
			}
			if (whereBuilder.toString().endsWith(",")) {
				whereBuilder.replace(whereBuilder.lastIndexOf(","), whereBuilder.length(), "");
			}
			result = finalBuilder.append(whereBuilder.toString()).toString();

			logger.info(String.format("Final Vizpod filter %s", result));

		} else if ((MetaType.relation).equals(vizpod.getSource().getRef().getType())) {
			// Relation relation =
			// daoRegister.getRelationDao().findLatestByUuid(vizpod.getSource().getRef().getUuid(),new
			// Sort(Sort.Direction.DESC, "version"));
			Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(
					vizpod.getSource().getRef().getUuid(), vizpod.getSource().getRef().getVersion(),
					vizpod.getSource().getRef().getType().toString(), "N");

			List<AttributeDetails> attrDetList = new LinkedList<>();
			if (vizpod.getDetailAttr().size() > 0)
				for (AttributeDetails attrDet : vizpod.getDetailAttr())
					attrDetList.add(attrDet);

			// append Select
			for (AttributeDetails attrDet : attrDetList) {
				if ((MetaType.datapod).equals(attrDet.getRef().getType())) {
					String keyAttrName = datapodServiceImpl.getAttributeName(attrDet.getRef().getUuid(),
							attrDet.getAttributeId());
					String datapodName = ((Datapod) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(),
							MetaType.datapod.toString(), "N")).getName();
					logger.info("datapodName : " + datapodName);

					selectBuilder.append(datapodName + "." + keyAttrName);

					selectBuilder.append(" as ").append(keyAttrName).append(" ");
					selectBuilder.append(comma);

				} else if (attrDet.getRef().getType() == MetaType.formula) {
					Formula formula = (Formula) commonServiceImpl.getLatestByUuid(attrDet.getRef().getUuid(),
							MetaType.formula.toString());
					Datasource vizDS = commonServiceImpl.getDatasourceByObject(vizpod);
					String FormulaSql = formulaOperator.generateSql(formula, null, null, null, vizDS);
					// flaghasFuncInVal = formulaOperator.isGroupBy(formula, null, null);

					selectBuilder.append(FormulaSql).append(" as " + formula.getName() + " ");

					selectBuilder.append(comma);

					// selectBuilder.append(FormulaSql).append(" as " + formula.getName() + " ");
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

				/*if (allowColNameInFltr) {
					Pattern pattern = Pattern.compile("(\\b(\\w+)\\.)(?=([^\"']*[\"'][^\"']*[\"'])*[^\"']*$)");
					Matcher matcher = pattern.matcher(whereBuilder);
					while (matcher.find()) {
						if (!NumberUtils.isCreatable(matcher.group()))
							whereBuilder = new StringBuilder(whereBuilder.toString().replace(matcher.group(), ""));
					}
				}*/
			}
			whereBuilder.append(blankSpace);

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
			// result += groupByBuilder.length() > 0 ? groupByBuilder.substring(0,
			// groupByBuilder.length() - 1) : "";
			// result += havingBuilder.length() > 0 ? havingBuilder.substring(0,
			// groupByBuilder.length() - 1) : "";
			// result += StringUtils.isBlank(havingBuilder.toString()) ? ConstantsUtil.BLANK
			// : ConstantsUtil.HAVING_1_1.concat(havingBuilder.toString());
			/// result += orderByBuilder.length() > 0 ? orderByBuilder.toString() : "";
			result += limitBuilder.length() > 0 ? limitBuilder.substring(0, limitBuilder.length() - 1) : "";
			
			logger.info(String.format("Final Vizpod filter %s", result));
		} else if ((MetaType.dataset).equals(vizpod.getSource().getRef().getType())) {
			DataSet dataSet = (DataSet) commonServiceImpl.getOneByUuidAndVersion(vizpod.getSource().getRef().getUuid(),
					vizpod.getSource().getRef().getVersion(), vizpod.getSource().getRef().getType().toString(), "N");
			tableName = dataSet.getName();

			List<AttributeDetails> attrDetList = new LinkedList<>();
			if (vizpod.getDetailAttr().size() > 0)
				for (AttributeDetails attrDet : vizpod.getDetailAttr())
					attrDetList.add(attrDet);

			List<AttributeSource> attributeInfo = new ArrayList<>();
			for (AttributeDetails attrDet : attrDetList) {
				for (AttributeSource attributeSource : dataSet.getAttributeInfo()) {
					if (attrDet.getRef().getType().equals(MetaType.datapod)
							|| attrDet.getRef().getType().equals(MetaType.dataset)) {
						if (attributeSource.getAttrSourceId().equalsIgnoreCase(attrDet.getAttributeId() + "")) {

							attributeInfo.add(attributeSource);

						}
						
					}
					
				}if (attrDet.getRef().getType() == MetaType.formula) {
					AttributeSource tempAttributeSource = new AttributeSource();
					AttributeRefHolder attributeRefHolder = new AttributeRefHolder();
					attributeRefHolder.setRef(attrDet.getRef());
					attributeRefHolder.setAttrId(String.valueOf(attrDet.getAttributeId()));
					attributeRefHolder.setAttrName(attrDet.getAttributeName());
					tempAttributeSource.setSourceAttr(attributeRefHolder);
					attributeInfo.add(tempAttributeSource);

				}

			}

			/********
			 * following commented code also works but to remove table name from filter
			 * query the custom code is written
			 *******/
			
			String innersql = datasetOperator.generateSql(dataSet, null, null, usedRefKeySet, null, runMode);
			dataSet.setAttributeInfo(attributeInfo);
			outerSelectBuilder = generateSelectForDataSet(dataSet, vizpod);
			whereBuilder.append(datasetOperator.generateWhere());
			Datasource datasource = commonServiceImpl.getDatasourceByObject(vizpod);

			whereBuilder.append(" ").append(filterOperator2.generateSql(vizpod.getFilterInfo(), null, null,
					usedRefKeySet, false, false, runMode, datasource));
			result = outerSelectBuilder.append(" FROM (").append(innersql).append(" )").append(" as ").append(tableName)
					.append(whereBuilder).toString();

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

}
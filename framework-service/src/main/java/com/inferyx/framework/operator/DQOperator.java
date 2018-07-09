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
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatasetServiceImpl;
import com.inferyx.framework.service.MessageStatus;

@Component
public class DQOperator implements IParsable {

	private String BLANK = " ";
	private String CASE_WHEN = " CASE WHEN ";
	private String THEN = " THEN 'Y' ELSE 'N' END as ";
	private String SELECT = " SELECT ";
	private String FROM = " FROM ";
	private String JOIN = " JOIN ";
	private String LEFT_OUTER_JOIN = " LEFT OUTER JOIN ";
	private String BRACKET_OPEN = "( ";
	private String BRACKET_CLOSE = " ) ";
	private String GROUP_BY = " GROUP BY ";
	private String ON = " ON ";
	private String DOT = ".";
	private String IS_NOT_NULL = " IS NOT NULL ";
	private String NULL_CHECK_PASS = "nullCheck_pass";
	private String VALUE_CHECK_PASS = "valueCheck_pass";
	private String RANGE_CHECK_PASS = "rangeCheck_pass";
	private String DATATYPE_CHECK_PASS = "dataTypeCheck_pass";
	private String DATEFORMAT_CHECK_PASS = "dataFormatCheck_pass";
	private String LENGTH_CHECK_PASS = "lengthCheck_pass";
	// private String STDDEV_CHECK_PASS = "stdDevCheck_pass";
	private String REFINT_CHECK_PASS = "refIntegrityCheck_pass";
	private String DUP_CHECK_PASS = "dupCheck_pass";
	private String CUSTOM_CHECK_PASS = "customCheck_pass";
	private String UNDERSCORE = "_";
	private String COMMA = ", ";
	private String COUNT = " COUNT";
	private String HAVING = " HAVING ";
	private String DUP_TABLE = " dupTable ";
	private String ONE = " 1 ";
	private String BETWEEN = " BETWEEN ";
	private String AND = " AND ";
	private String GREATER_THAN = " > ";
	private String LESS_THAN = " < ";
	private String LESS_THAN_EQUALS = " <= ";
	private String SINGLE_QUOTE = "'";
	private String AS = " AS ";
	private final String WHERE_1_1 = " WHERE (1=1) ";
	private String DATAPODUUID = "DatapodUUID";
	private String DATAPODVERSION = "DatapodVersion";
	private String ATTRIBUTE_ID = "AttributeId";
	private String ATTRIBUTE_VAL = "AttributeValue";
	private String ROWKEY = "RowKey";
	private String EMPTY = "";
	private String VERSION = " version ";
	private String DATAPOD_NAME = " datapodname";
	private String ATTRIBUTE_NAME = " attributename";

	@Autowired
	MetadataUtil daoRegister;
	@Autowired
	RelationOperator relationOperator;
	@Autowired
	MapOperator mapOperator;
	@Autowired
	AttributeMapOperator attributeMapOperator;
	@Autowired
	DatasetServiceImpl datasetServiceImpl;
	@Autowired
	FilterOperator filterOperator;
	@Autowired
	DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	static final Logger logger = Logger.getLogger(DQOperator.class);

	public String generateSql(DataQual dataQual, List<String> datapodList, DataQualExec dataQualExec, DagExec dagExec,
			Set<MetaIdentifier> usedRefKeySet, HashMap<String, String> otherParams, RunMode runMode) throws Exception {
		Datapod srcDP = null;
		DataSet dataset = null;
		if (dataQual == null) {
			return null;
		}
		if (dataQual.getDependsOn().getRef().getType() == MetaType.relation) {
			throw new Exception("DQ on relation is not supported");
		}
		if (dataQual.getDependsOn().getRef().getType() == MetaType.datapod) {
			srcDP = (Datapod) daoRegister.getRefObject(dataQual.getDependsOn().getRef());
			if (dataQual.getAttribute() != null) {
				logger.info("getDataQualTableName(srcDP) : " + getTableName(srcDP, datapodList, dagExec, otherParams, runMode));
				dataQual.getAttribute().setAttrName(
						srcDP.getAttribute(Integer.parseInt(dataQual.getAttribute().getAttrId())).getName());
				MetaIdentifier srcDPRef = new MetaIdentifier(MetaType.datapod, srcDP.getUuid(), srcDP.getVersion());
				usedRefKeySet.add(srcDPRef);
				return generateSql(dataQual, getTableName(srcDP, datapodList, dagExec, otherParams, runMode),
						srcDP.getAttribute(Integer.parseInt(dataQual.getAttribute().getAttrId())).getName(),
						datapodList, dataQualExec, dagExec, usedRefKeySet, otherParams, runMode);
			} else {
				return generateSql(dataQual, getTableName(srcDP, datapodList, dagExec, otherParams, runMode), null, datapodList,
						dataQualExec, dagExec, usedRefKeySet, otherParams, runMode);
			}
		}
		if (dataQual.getDependsOn().getRef().getType() == MetaType.dataset) {
			/*
			 * dataset = (Dataset)
			 * daoRegister.getRefObject(dataQual.getAttribute().getRef()); String attr =
			 * datasetServiceImpl.getAttributeSql(daoRegister, dataset,
			 * Integer.parseInt(dataQual.getAttribute().getAttributeId())); if (attr ==
			 * null) { return null; } return generateSql(dataQual, dataset.getName(),
			 * attr.split("\\.")[1]);
			 */
			throw new Exception("DQ on dataset is not supported");
		}
		return null;
	}

	public String getTableName(Datapod datapod, List<String> datapodList, DagExec dagExec, HashMap<String, String> otherParams, RunMode runMode)
			throws Exception {
		logger.info(" OtherParams : datapod : " + otherParams + " : " + datapod.getUuid());
		if (runMode.equals(RunMode.ONLINE) && datapodList != null && datapodList.contains(datapod.getUuid())) {
			return String.format("%s_%s_%s", datapod.getUuid().replaceAll("-", "_"), datapod.getVersion(),
					dagExec.getVersion());
		} else if (otherParams!=null && otherParams.containsKey("datapodUuid_" + datapod.getUuid() + "_tableName")) {
			return otherParams.get("datapodUuid_" + datapod.getUuid() + "_tableName");
		}
		//logger.info(" runMode : " + runMode.toString() + " : datapod : " + datapod.getUuid() + " : datapodList.contains(datapod.getUuid()) : " + datapodList.contains(datapod.getUuid()));
		datastoreServiceImpl.setRunMode(runMode);
		return datastoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()),
				runMode);
	}

	private String generateSelect(DataQual dq, DataQualExec dataQualExec, String tableName, String attributeName)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Datasource dataSource = commonServiceImpl.getDatasourceByApp();

		if (StringUtils.isBlank(tableName)) {
			return "";
		}
		Datapod datapod = (Datapod) daoRegister.getRefObject(dq.getDependsOn().getRef());
		// String select = SELECT.concat("row_number() over (partition by 1) as rownum,
		// ")
		String select = SELECT.concat(tildeSepAttrs(datapod.getName(), getRowKeyList(datapod))).concat(AS)
				.concat(ROWKEY).concat(COMMA).concat(SINGLE_QUOTE).concat(dq.getDependsOn().getRef().getUuid())
				.concat(SINGLE_QUOTE).concat(AS).concat(DATAPODUUID).concat(COMMA).concat(SINGLE_QUOTE)
				.concat(dq.getDependsOn().getRef().getVersion()).concat(SINGLE_QUOTE).concat(AS).concat(DATAPODVERSION)
				.concat(COMMA).concat(SINGLE_QUOTE).concat(datapod.getName()).concat(SINGLE_QUOTE).concat(AS)
				.concat(DATAPOD_NAME).concat(COMMA).concat(SINGLE_QUOTE);
		if (dq.getAttribute() != null) {
			select = select.concat(dq.getAttribute().getAttrId());
		}
		select = select.concat(SINGLE_QUOTE).concat(AS).concat(ATTRIBUTE_ID).concat(COMMA).concat(SINGLE_QUOTE);

		if (dq.getAttribute() != null) {
			select = select.concat(attributeName);
		}
		select = select.concat(SINGLE_QUOTE).concat(AS).concat(ATTRIBUTE_NAME).concat(COMMA);

		if (dq.getAttribute() != null) {
			select = select.concat(" CAST(").concat(datapod.getName()).concat(DOT).concat(attributeName)
					.concat((dataSource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString()) 
							|| dataSource.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) ? " AS CHAR(50)) "
							: (dataSource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())
									? " AS VARCHAR2(70)) "
									: " AS STRING)"));
		} else {
			select = select.concat(SINGLE_QUOTE).concat(SINGLE_QUOTE);
		}
		select = select.concat(AS).concat(ATTRIBUTE_VAL).concat(COMMA)
				.concat(generateCase(dq, tableName, attributeName)).concat(COMMA);
		select = select.concat(dataQualExec.getVersion()).concat(AS).concat(VERSION);
		return select;
	}

	private String generateFrom(DataQual dq, MetaIdentifier ref, List<String> datapodList, DagExec dagExec,
			Set<MetaIdentifier> usedRefKeySet, HashMap<String, String> otherParams, RunMode runMode) throws Exception {
		Datapod srcDP = null;
		String resp = null;
		Relation relation = null;
		DataSet dataSet = null;
		if (ref.getType() == MetaType.datapod) {
			srcDP = (Datapod) daoRegister.getRefObject(ref);
			resp = FROM.concat(getTableName(srcDP, datapodList, dagExec, otherParams, runMode)).concat("  ").concat(srcDP.getName());
		}
		/*
		 * if (ref.getType() == MetaType.relation) { relation = (Relation)
		 * daoRegister.getRefAS CHAR(50))Object(ref); return
		 * FROM.concat(relationOperator.generateSql(relation, null, null,
		 * null)).concat(WHERE_1_1); } if (ref.getType() == MetaType.dataset) { dataSet
		 * = (Dataset) daoRegister.getRefObAS CHAR(50))ject(ref); Map innerMap = (Map)
		 * daoRegister.getRefObject(dataSet.getDependsOn().getRef()); return
		 * FROM.concat("(").concat(mapOperator.generateSql(innerMap, null, null,
		 * null)).concat(") as ").concat(dataSet.getName()).concat(WHERE_1_1); }
		 */
		return resp
				.concat(generateRefIntFrom(dq, getTableName(srcDP, datapodList, dagExec, otherParams, runMode), srcDP.getName(),
						datapodList, dagExec, usedRefKeySet, otherParams, runMode))
				// .concat(generateStddevFrom(dq, getDataQualTableName(srcDP, datapodList,
				// dagExec), srcDP.getName(), datapodList, dagExec))
				.concat(generateDupCheckFrom(dq, getTableName(srcDP, datapodList, dagExec, otherParams, runMode), srcDP.getName(),
						usedRefKeySet));
	}

	/*
	 * private String generateStddevFrom (DataQual dq, String tableName, String
	 * attributeName, List<String> datapodList, DagExec dagExec) { String
	 * stddevFromStr = null; if (dq == null ||
	 * StringUtils.isBlank(dq.getStdDevCheck())) { return EMPTY; } Datapod datapod =
	 * (Datapod) daoRegister.getRefObject(dq.getDependsOn().getRef()); stddevFromStr
	 * = JOIN .concat(BRACKET_OPEN) .concat(SELECT)
	 * .concat(commaSepAttrs(getRowKeyList(datapod))) .concat(COMMA)
	 * .concat("STDDEV_POP(") .concat(dq.getAttribute().getAttrName())
	 * .concat(BRACKET_CLOSE) .concat(AS) .concat(" stddev_pop_alias ")
	 * .concat(FROM) .concat(getDataQualTableName(datapod, datapodList, dagExec))
	 * .concat(AS) .concat(datapod.getName()) .concat(GROUP_BY)
	 * .concat(commaSepAttrsGroup(getRowKeyList(datapod))) .concat(BRACKET_CLOSE)
	 * .concat(AS) .concat(" stddev_alias ") .concat(ON) .concat(BRACKET_OPEN); for
	 * (Attribute attribute : getRowKeyList(datapod)) { stddevFromStr =
	 * stddevFromStr .concat(datapod.getName()) .concat(DOT)
	 * .concat(attribute.getName()) .concat(" = ") .concat(" stddev_alias ")
	 * .concat(DOT) .concat(attribute.getName()) .concat(AND);
	 * 
	 * } stddevFromStr = stddevFromStr.substring(0, stddevFromStr.length() -
	 * AND.length()) .concat(BRACKET_CLOSE); return stddevFromStr; }
	 */

	private boolean getIsKey(Datapod datapod, String keyName) {
		return commaSepAttrs(getRowKeyList(datapod)).contains(keyName);
	}

	private String generateDupCheckFrom(DataQual dq, String tableName, String attributeName,
			Set<MetaIdentifier> usedRefKeySet)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if (dq == null || StringUtils.isBlank(dq.getDuplicateKeyCheck())
				|| dq.getDuplicateKeyCheck().equalsIgnoreCase("N")) {
			return EMPTY;
		}
		Datapod datapod = (Datapod) daoRegister.getRefObject(dq.getDependsOn().getRef());
		usedRefKeySet.add(dq.getDependsOn().getRef());
		boolean isKey = false;
		String dupJoinStr = LEFT_OUTER_JOIN.concat(BRACKET_OPEN).concat(SELECT);
		if (dq.getAttribute() != null) {
			isKey = getIsKey(datapod, dq.getAttribute().getAttrName());
			if (!isKey) {
				dupJoinStr = dupJoinStr.concat(dq.getAttribute().getAttrName()).concat(AS)
						.concat(dq.getAttribute().getAttrName()).concat(COMMA);
			}
			dupJoinStr = dupJoinStr.concat(commaSepAttrs(getRowKeyList(datapod))).concat(COMMA).concat(COUNT)
					.concat(BRACKET_OPEN).concat(dq.getAttribute().getAttrName()).concat(BRACKET_CLOSE).concat(AS)
					.concat(" dup ").concat(FROM).concat(tableName)
					// .concat(AS)
					.concat(" ").concat(datapod.getName()).concat(WHERE_1_1).concat(GROUP_BY);
			if (!isKey) {
				dupJoinStr = dupJoinStr.concat(dq.getAttribute().getAttrName()).concat(COMMA);
			}
			dupJoinStr = dupJoinStr.concat(commaSepAttrsGroup(getRowKeyList(datapod))).concat(HAVING).concat(COUNT)
					.concat(BRACKET_OPEN).concat(dq.getAttribute().getAttrName()).concat(BRACKET_CLOSE)
					.concat(GREATER_THAN).concat(ONE).concat(BRACKET_CLOSE).concat(DUP_TABLE).concat(ON)
					.concat(BRACKET_OPEN);
			for (Attribute attribute : getRowKeyList(datapod)) {
				dupJoinStr = dupJoinStr.concat(datapod.getName()).concat(DOT).concat(attribute.getName()).concat(" = ")
						.concat(DUP_TABLE).concat(DOT).concat(attribute.getName()).concat(AND);

			}
			dupJoinStr = dupJoinStr.substring(0, dupJoinStr.length() - AND.length()).concat(BRACKET_CLOSE);
		} else {
			dupJoinStr = dupJoinStr.concat(commaSepAttrs(getRowKeyList(datapod))).concat(COMMA).concat(COUNT)
					.concat(BRACKET_OPEN).concat(tildeSepAttrs(datapod.getName(), getRowKeyList(datapod)))
					.concat(BRACKET_CLOSE).concat(AS).concat(" dup ").concat(FROM).concat(tableName)
					// .concat(AS)
					.concat(" ").concat(datapod.getName()).concat(WHERE_1_1).concat(GROUP_BY);
			dupJoinStr = dupJoinStr.concat(commaSepAttrsGroup(getRowKeyList(datapod))).concat(HAVING).concat(COUNT)
					.concat(BRACKET_OPEN).concat(tildeSepAttrs(datapod.getName(), getRowKeyList(datapod)))
					.concat(BRACKET_CLOSE).concat(GREATER_THAN).concat(ONE).concat(BRACKET_CLOSE).concat(DUP_TABLE)
					.concat(ON).concat(BRACKET_OPEN);
			for (Attribute attribute : getRowKeyList(datapod)) {
				dupJoinStr = dupJoinStr.concat(datapod.getName()).concat(DOT).concat(attribute.getName()).concat(" = ")
						.concat(DUP_TABLE).concat(DOT).concat(attribute.getName()).concat(AND);

			}
			dupJoinStr = dupJoinStr.substring(0, dupJoinStr.length() - AND.length()).concat(BRACKET_CLOSE);

		}
		return dupJoinStr;
	}

	private String tildeSepAttrs(String tableName, List<Attribute> attrList)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder attrStr = new StringBuilder();
		String attrs = null;
		if (attrList == null || attrList.isEmpty()) {
			return null;
		}
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		if (datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString()))
			attrStr.append("concat('~',");
		else
			attrStr.append("concat_ws('~',");

		for (Attribute attribute : attrList) {
			attrStr.append(tableName).append(".").append(attribute.getName()).append(",");
		}
		attrs = attrStr.toString();
		attrs = attrs.substring(0, attrs.length() - 1);
		attrs = attrs.concat(")");
		return attrs;
	}

	private String generateRefIntFrom(DataQual dq, String tableName, String attributeName, List<String> datapodList,
			DagExec dagExec, Set<MetaIdentifier> usedRefKeySet, HashMap<String, String> otherParams, RunMode runMode) throws Exception {
		String refIntStr = null;
		if (dq == null || dq.getRefIntegrityCheck() == null || dq.getRefIntegrityCheck().getRef() == null) {
			return EMPTY;
		}
		Datapod datapod = (Datapod) daoRegister.getRefObject(dq.getDependsOn().getRef());
		List<Attribute> rowKeyAttrList = null;
		rowKeyAttrList = getRowKeyList(datapod);
		if (rowKeyAttrList == null || rowKeyAttrList.isEmpty()) {
			//System.out.println("Datapod doesnot have row key. Hence aborting ref integrity check. ");
			return EMPTY;
		}
		Datapod datapodRef = (Datapod) daoRegister.getRefObject(dq.getRefIntegrityCheck().getRef());
		usedRefKeySet.add(dq.getRefIntegrityCheck().getRef());
		refIntStr = LEFT_OUTER_JOIN.concat(getTableName(datapodRef, datapodList, dagExec, otherParams, runMode))
				// .concat(AS)
				.concat(" ").concat(datapodRef.getName()).concat("_ref").concat(ON).concat(BRACKET_OPEN);
		for (Attribute attribute : rowKeyAttrList) {
			refIntStr = refIntStr.concat(datapod.getName()).concat(DOT).concat(attribute.getName()).concat(" = ")
					.concat(datapodRef.getName()).concat("_ref").concat(DOT)
					.concat(datapodRef.getAttribute(Integer.parseInt(dq.getRefIntegrityCheck().getAttrId())).getName())
					.concat(AND);

		}
		refIntStr = refIntStr.substring(0, refIntStr.length() - AND.length()).concat(BRACKET_CLOSE);
		return refIntStr;
	}

	private List<Attribute> getRowKeyList(Datapod datapod) {
		List<Attribute> attributeList = new ArrayList<>();
		if (datapod == null) {
			return null;
		}
		for (Attribute attribute : datapod.getAttributes()) {
			if (StringUtils.isNotBlank(attribute.getKey())) {
				attributeList.add(attribute);
			}
		}
		return attributeList;
	}

	private String commaSepAttrs(List<Attribute> attrList) {
		StringBuilder attrStr = new StringBuilder();
		String attrs = null;
		if (attrList == null || attrList.isEmpty()) {
			return null;
		}
		for (Attribute attribute : attrList) {
			attrStr.append(attribute.getName()).append(AS).append(attribute.getName()).append(COMMA);
		}
		attrs = attrStr.toString();
		return attrs.substring(0, attrs.length() - 2);
	}

	private String commaSepAttrsGroup(List<Attribute> attrList) {
		StringBuilder attrStr = new StringBuilder();
		String attrs = null;
		if (attrList == null || attrList.isEmpty()) {
			return null;
		}
		for (Attribute attribute : attrList) {
			attrStr.append(attribute.getName()).append(COMMA);
		}
		attrs = attrStr.toString();
		return attrs.substring(0, attrs.length() - 2);
	}

	public String generateFilter(List<AttributeRefHolder> filterInfo, Set<MetaIdentifier> usedRefKeySet)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if (filterInfo == null || filterInfo.isEmpty()) {
			return "";
		}
		return filterOperator.generateSql(filterInfo, null, null, usedRefKeySet);

	}

	public String generateSql(DataQual dq, String tableName, String attributeName, List<String> datapodList,
			DataQualExec dataQualExec, DagExec dagExec, Set<MetaIdentifier> usedRefKeySet, HashMap<String, String> otherParams, RunMode runMode)
			throws Exception {
		String select = generateSelect(dq, dataQualExec, tableName, attributeName);
		logger.info("Select for dataQual : " + dq.getUuid() + " : " + StringUtils.isBlank(select));
		if (StringUtils.isBlank(select)) {
			return null;
		}
		return select.concat(generateFrom(dq, dq.getDependsOn().getRef(), datapodList, dagExec, usedRefKeySet, otherParams, runMode))
				.concat(WHERE_1_1).concat(generateFilter(dq.getFilterInfo(), usedRefKeySet));
	}

	public String generateCase(DataQual dq, String tableName, String attributeName)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Datasource dataSource = commonServiceImpl.getDatasourceByApp();

		StringBuilder dqBuilder = new StringBuilder();
		String dqString = null;
		String check = null;
		String colName = null;
		Datapod datapod = (Datapod) daoRegister.getRefObject(dq.getDependsOn().getRef());
		String tableAttr = datapod.getName().concat(DOT) + attributeName;
		if (StringUtils.isNotBlank(dq.getNullCheck()) && dq.getNullCheck().equalsIgnoreCase("Y")) {
			check = tableAttr.concat(IS_NOT_NULL);
			colName = NULL_CHECK_PASS;
			dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
		} else {
			dqBuilder.append("'' as ").append(NULL_CHECK_PASS).append(COMMA);
		} // End nullCheck If
		if (dq.getValueCheck() != null && !dq.getValueCheck().isEmpty()) {
			if (dq.getValueCheck().size() == 1) {
				check = tableAttr.concat("='").concat(dq.getValueCheck().get(0)).concat("'");
			} else {
				check = tableAttr.concat(" IN (");
				for (String value : dq.getValueCheck()) {
					check = check.concat("'").concat(value).concat("', ");
				}
				check = check.substring(0, check.length() - 2).concat(") ");
			}
			colName = VALUE_CHECK_PASS;
			dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
		} else {
			dqBuilder.append("'' as ").append(VALUE_CHECK_PASS).append(COMMA);
		} // End valueCheck If
		if (dq.getRangeCheck() != null && !dq.getRangeCheck().isEmpty()) {
			boolean containsLB = dq.getRangeCheck().containsKey("lowerBound")
					&& (dq.getRangeCheck().get("lowerBound") != null);
			boolean containsUB = dq.getRangeCheck().containsKey("upperBound")
					&& (dq.getRangeCheck().get("upperBound") != null);
			String lowerBound = dq.getRangeCheck().get("lowerBound");
			String upperBound = dq.getRangeCheck().get("upperBound");
			colName = RANGE_CHECK_PASS;
			if (containsLB && containsUB) {
				check = tableAttr.concat(BETWEEN).concat("'").concat(lowerBound).concat("'").concat(AND).concat("'")
						.concat(upperBound).concat("'").concat(BLANK);
				dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
			} else if (containsLB) {
				check = tableAttr.concat(GREATER_THAN).concat(lowerBound + "").concat(BLANK);
				dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
			} else if (containsUB) {
				check = tableAttr.concat(LESS_THAN).concat(lowerBound + "").concat(BLANK);
				dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
			} else {
				dqBuilder.append("'' as ").append(RANGE_CHECK_PASS).append(COMMA);
			}
		} else {
			dqBuilder.append("'' as ").append(RANGE_CHECK_PASS).append(COMMA);
		} // End rangeCheck If
		if (StringUtils.isNotBlank(dq.getDataTypeCheck())) {
			check = " CAST(".concat(tableAttr).concat(" AS ").concat(dq.getDataTypeCheck()).concat(") IS NOT NULL")
					.concat(BLANK);
			colName = DATATYPE_CHECK_PASS;
			dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
		} else {
			dqBuilder.append("'' as ").append(DATATYPE_CHECK_PASS).append(COMMA);
		} // End dataTypeCheck If
		if (StringUtils.isNotBlank(dq.getDateFormatCheck())) {
			check = " DATE_FORMAT(".concat(tableAttr).concat(",'").concat(dq.getDateFormatCheck())
					.concat("') IS NOT NULL").concat(BLANK);
			colName = DATEFORMAT_CHECK_PASS;
			dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
		} else {
			dqBuilder.append("'' as ").append(DATEFORMAT_CHECK_PASS).append(COMMA);
		} // End dateFormatCheck If
		if (dq.getLengthCheck() != null && !dq.getLengthCheck().isEmpty()) {
			boolean containsLB = dq.getLengthCheck().containsKey("minLength")
					&& (dq.getLengthCheck().get("minLength") != null);
			boolean containsUB = dq.getLengthCheck().containsKey("maxLength")
					&& (dq.getLengthCheck().get("maxLength") != null);
			Long lowerBound = dq.getLengthCheck().get("minLength");
			Long upperBound = dq.getLengthCheck().get("maxLength");
			colName = LENGTH_CHECK_PASS;
			if (containsLB && containsUB) {
				check = " LENGTH(CAST(".concat(tableAttr)
						.concat((dataSource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString()) 
								|| dataSource.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) ? " AS CHAR(50)) "
								: (dataSource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())
										? " AS VARCHAR2(70)) "
										: " AS STRING)"))
						.concat(BETWEEN).concat(lowerBound + "").concat(AND).concat(upperBound + "").concat(BLANK);
				dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
			} else if (containsLB) {
				check = " LENGTH(CAST(".concat(tableAttr)
						.concat((dataSource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString()) 
								|| dataSource.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) ? " AS CHAR(50)) "
								: (dataSource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())
										? " AS VARCHAR2(70)) "
										: " AS STRING)"))
						.concat(GREATER_THAN).concat(lowerBound + "").concat(BLANK);
				dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
			} else if (containsUB) {
				check = " LENGTH(CAST(".concat(tableAttr)
						.concat((dataSource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString()) 
								|| dataSource.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) ? " AS CHAR(50)) "
								: (dataSource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())
										? " AS VARCHAR2(70)) "
										: " AS STRING)"))
						.concat(LESS_THAN).concat(lowerBound + "").concat(BLANK);
				dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
			} else {
				dqBuilder.append("'' as ").append(LENGTH_CHECK_PASS).append(COMMA);
			}
		} else {
			dqBuilder.append("'' as ").append(LENGTH_CHECK_PASS).append(COMMA);
		} // End lengthCheck If
		if (dq.getRefIntegrityCheck() != null && dq.getRefIntegrityCheck().getRef() != null) {
			Datapod refIntTab = (Datapod) daoRegister.getRefObject(dq.getRefIntegrityCheck().getRef());
			dq.getRefIntegrityCheck().setAttrName(
					refIntTab.getAttribute(Integer.parseInt(dq.getRefIntegrityCheck().getAttrId())).getName());
			check = refIntTab.getName().concat("_ref").concat(DOT).concat(dq.getRefIntegrityCheck().getAttrName())
					.concat(IS_NOT_NULL);
			colName = REFINT_CHECK_PASS;
			dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
		} else {
			dqBuilder.append("'' as ").append(REFINT_CHECK_PASS).append(COMMA);
		} // End stdDevCheck If
		/*
		 * if (StringUtils.isNotBlank(dq.getStdDevCheck())) { check =
		 * " stddev_pop_alias ".concat(GREATER_THAN).concat(dq.getStdDevCheck()).concat(
		 * BLANK); colName = STDDEV_CHECK_PASS; dqBuilder.append(caseWrapper(check,
		 * colName)).append(COMMA); } else {
		 * dqBuilder.append("'' as ").append(STDDEV_CHECK_PASS).append(COMMA); }
		 */ // End stdDevCheck If
		if (StringUtils.isNotBlank(dq.getDuplicateKeyCheck()) && dq.getDuplicateKeyCheck().equalsIgnoreCase("Y")) {
			check = " dup is null or dup ".concat(LESS_THAN_EQUALS).concat("0");
			colName = DUP_CHECK_PASS;
			dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
		} else {
			dqBuilder.append("'' as ").append(DUP_CHECK_PASS).append(COMMA);
		} // End dupCheck If
		if (StringUtils.isNotBlank(dq.getCustomFormatCheck())) {
			check = tableAttr.concat(" RLIKE ( ").concat(dq.getCustomFormatCheck()).concat(BRACKET_CLOSE);
			colName = CUSTOM_CHECK_PASS;
			dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
		} else {
			dqBuilder.append("'' as ").append(CUSTOM_CHECK_PASS).append(COMMA);
		} // End customCheck If

		dqString = dqBuilder.toString();
		return dqString.substring(0, dqString.length() - 2);
	}

	private String caseWrapper(String check, String colName) {
		StringBuilder caseBuilder = new StringBuilder(CASE_WHEN).append(check).append(THEN).append(colName)
				.append(BLANK);
		return caseBuilder.toString();
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(baseExec.getUuid(), baseExec.getVersion(), MetaType.dqExec.toString());
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		DataQual dataQual = (DataQual) commonServiceImpl.getOneByUuidAndVersion(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), MetaType.dq.toString());
		try{
			dataQualExec.setExec(generateSql(dataQual, null, dataQualExec, null, usedRefKeySet, execParams.getOtherParams(), runMode));
			dataQualExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
			
			synchronized (dataQualExec.getUuid()) {
				DataQualExec dataQualExec1 = (DataQualExec) daoRegister.getRefObject(
						new MetaIdentifier(MetaType.dqExec, dataQualExec.getUuid(), dataQualExec.getVersion()));
				dataQualExec1.setExec(dataQualExec.getExec());
				dataQualExec1.setRefKeyList(dataQualExec.getRefKeyList());
				commonServiceImpl.save(MetaType.dqExec.toString(), dataQualExec1);
				dataQualExec1 = null;
			}
			}catch(Exception e){
				commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.Failed);
				e.printStackTrace();
				String message = null;
				try {
					message = e.getMessage();
				}catch (Exception e2) {
					// TODO: handle exception
				}
				commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Failed data quality parsing.");
				throw new Exception((message != null) ? message : "Failed data quality parsing.");
			}
		return dataQualExec;
	}

}

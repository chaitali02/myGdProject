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
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeDomain;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BlankSpaceCheckOptions;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.CaseCheckType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.ThresholdType;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatasetServiceImpl;
import com.inferyx.framework.service.MessageStatus;

@Component
public class DQOperator implements IParsable {

	private String BLANK = " ";
	private String CASE_WHEN = " CASE WHEN ";
	private String SINGLE_QUOTED_Y = "'Y'";
	private String SINGLE_QUOTED_N = "'N'";
	private String ONLY_THEN = " THEN ";
	private String ONLY_WHEN = " WHEN ";
	private String ONLY_ELSE = " ELSE ";
	private String ONLY_END = " END ";
	private String THEN = " THEN 'Y' ELSE 'N' END AS ";
	private String THEN_N_Y = " THEN 'N' ELSE 'Y' END AS ";
	private String THEN_Y_N = " THEN 'Y' ELSE 'N' END AS ";
	private String THEN_1_0 = " THEN 1 ELSE 0 END AS ";
	private String THEN_0_1 = " THEN 0 ELSE 1 END AS ";
	private String SELECT = " SELECT ";
	private String FROM = " FROM ";
//	private String JOIN = " JOIN ";
	private String LEFT_OUTER_JOIN = " LEFT OUTER JOIN ";
	private String BRACKET_OPEN = "( ";
	private String BRACKET_CLOSE = " ) ";
	private String GROUP_BY = " GROUP BY ";
	private String ON = " ON ";
	private String DOT = ".";
	private String IS_NOT_NULL = " IS NOT NULL ";
	private String NULL_CHECK_PASS = "null_check_pass";
	private String VALUE_CHECK_PASS = "value_check_pass";
	private String RANGE_CHECK_PASS = "range_check_pass";
	private String DATATYPE_CHECK_PASS = "dataType_check_pass";
	private String FORMAT_CHECK_PASS = "format_check_pass";
	private String LENGTH_CHECK_PASS = "length_check_pass";
	// private String STDDEV_CHECK_PASS = "stdDevCheck_pass";
	private String REFINT_CHECK_PASS = "ri_check_pass";
	private String DUP_CHECK_PASS = "dup_check_pass";
	private String CUSTOM_CHECK_PASS = "custom_check_pass";
	private String DOMAIN_CHECK_PASS = "domain_check_pass";
	private String BLANK_SPACE_CHECK_PASS = "blankspace_check_pass";
	private String EXPRESSION_CHECK_PASS = "expression_check_pass";
	private String ALL_CHECK_PASS = "all_check_pass";
	private String CASE_CHECK_PASS = "case_check_pass";
	
	private String NULL_CHECK_P = "nullCheck_p";
	private String VALUE_CHECK_P= "valueCheck_p";
	private String RANGE_CHECK_P = "rangeCheck_p";
	private String DATATYPE_CHECK_P = "dataTypeCheck_p";
	private String FORMAT_CHECK_P = "formatCheck_p";
	private String LENGTH_CHECK_P = "lengthCheck_p";
	private String REFINT_CHECK_P = "refIntegrityCheck_p";
	private String DUP_CHECK_P = "dupCheck_p";
	private String CUSTOM_CHECK_P = "customCheck_p";
	private String DOMAIN_CHECK_P = "domainCheck_p";
	private String BLANK_SPACE_CHECK_P = "blankSpaceCheck_p";
	private String EXPRESSION_CHECK_P = "expressionCheck_p";
	private String ALL_CHECK_P = "allCheck_p";
	private String CASE_CHECK_P = "caseCheck_p";
	
	private String NULL_CHECK_F = "nullCheck_f";
	private String VALUE_CHECK_F= "valueCheck_f";
	private String RANGE_CHECK_F = "rangeCheck_f";
	private String DATATYPE_CHECK_F = "dataTypeCheck_f";
	private String FORMAT_CHECK_F = "formatCheck_f";
	private String LENGTH_CHECK_F = "lengthCheck_f";
	private String REFINT_CHECK_F = "refIntegrityCheck_f";
	private String DUP_CHECK_F = "dupCheck_f";
	private String CUSTOM_CHECK_F = "customCheck_f";
	private String DOMAIN_CHECK_F = "domainCheck_f";
	private String BLANK_SPACE_CHECK_F = "blankSpaceCheck_f";
	private String EXPRESSION_CHECK_F = "expressionCheck_f";
	private String ALL_CHECK_F = "allCheck_f";
	private String CASE_CHECK_F = "caseCheck_f";

	private String TOTAL_ROW_COUNT = "total_row_count";
	private String TOTAL_PASS_COUNT = "total_pass_count";
	private String TOTAL_FAIL_COUNT = "total_fail_count";
	private String DQ_RESULT_ALIAS = "dq_result_alias";
	private String DQ_RESULT_READY_ALIAS = "dq_result_ready_alias";
	private String DQ_RESULT_SUM_ALIAS = "dq_result_sum_alias";
	private String DQ_SUMMARY_ALIAS = " dq_summary_alias ";
	private String STD_DEV_FAIL = " STD_DEV_FAIL ";
	private String SCORE = " score ";
	private String RULEUUID = "rule_uuid";
	private String RULEVERSION = "rule_version";
	private String RULENAME = "rule_name";
	
	private String THRESHOLD_TYPE = "threshold_type";
	private String THRESHOLD_LIMIT = "threshold_limit";
	private String THRESHOLD_IND = "threshold_ind";
	private String HIGH = " 'HIGH' ";
	private String MEDIUM = " 'MEDIUM' ";
	private String LOW = " 'LOW' ";
	
//	private String UNDERSCORE = "_";
	private String COMMA = ", ";
	private String COUNT = " COUNT";
	private String SUM = " SUM";
	private String HAVING = " HAVING (1=1) ";
	private String DUP_TABLE = " dupTable ";
	private String ONE = " 1 ";
	private String BETWEEN = " BETWEEN ";
	private String AND = " AND ";
	private String OR = " OR ";
	private String GREATER_THAN = " > ";
	private String LESS_THAN = " < ";
	private String LESS_THAN_EQUALS = " <= ";
	private String EQUAL_TO = " = ";
	private String MINUS = " - ";
	private String DIVIDE_BY = " / ";
	private String MULTIPLY_BY = " * ";
	private String SINGLE_QUOTE = "'";
	private String AS = " AS ";
	private final String WHERE_1_1 = " WHERE (1=1) ";
	private String DATAPODUUID = "datapod_uuid";
	private String DATAPODVERSION = "datapod_version";
	private String ATTRIBUTE_ID = "attribute_id";
	private String ATTRIBUTE_VAL = "attribute_value";
	private String ROWKEY_NAME = "rowkey_name";
	private String ROWKEY_VALUE = "rowkey_value";
	private String EMPTY = "";
	private String VERSION = " version ";
	private String DATAPOD_NAME = " datapod_name";
	private String ATTRIBUTE_NAME = " attribute_name";
	private String CROSS_JOIN = " CROSS JOIN ";
	private String STDDEV = " STDDEV";

	@Autowired
	RelationOperator relationOperator;
	@Autowired
	MapOperator mapOperator;
	@Autowired
	AttributeMapOperator attributeMapOperator;
	@Autowired
	DatasetServiceImpl datasetServiceImpl;
	@Autowired
	DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;

	@Autowired
	FilterOperator2 filterOperator2;
	@Autowired
	ExpressionOperator expressionOperator;
	
	static final Logger logger = Logger.getLogger(DQOperator.class);

	public String generateSql(DataQual dataQual, List<String> datapodList, DataQualExec dataQualExec, DagExec dagExec,  
			Set<MetaIdentifier> usedRefKeySet, HashMap<String, String> otherParams, RunMode runMode) throws Exception {
		logger.info("DQ generateSql otherParams : " + otherParams);
		Datapod srcDP = null;
//		DataSet dataset = null;
		if (dataQual == null) {
			return null;
		}
		if (dataQual.getDependsOn().getRef().getType() == MetaType.relation) {
			throw new Exception("DQ on relation is not supported");
		}
		if (dataQual.getDependsOn().getRef().getType() == MetaType.datapod) {
//			srcDP = (Datapod) daoRegister.getRefObject(dataQual.getDependsOn().getRef());
			srcDP = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dataQual.getDependsOn().getRef().getUuid(), dataQual.getDependsOn().getRef().getVersion(), dataQual.getDependsOn().getRef().getType().toString(), "N");
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
//		Datasource dataSource = commonServiceImpl.getDatasourceByApp();
		Datasource dqDataSource = commonServiceImpl.getDatasourceByObject(dq);


		if (StringUtils.isBlank(tableName)) {
			return "";
		}
//		Datapod datapod = (Datapod) daoRegister.getRefObject(dq.getDependsOn().getRef());
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dq.getDependsOn().getRef().getUuid(), dq.getDependsOn().getRef().getVersion(), dq.getDependsOn().getRef().getType().toString(), "N");
		// String select = SELECT.concat("row_number() over (partition by 1) as rownum,
		// ")
		String select = SELECT
				.concat(SINGLE_QUOTE).concat(dq.getUuid()).concat(SINGLE_QUOTE).concat(AS).concat(RULEUUID).concat(COMMA)
				.concat(SINGLE_QUOTE).concat(dq.getVersion()).concat(SINGLE_QUOTE).concat(AS).concat(RULEVERSION).concat(COMMA)
				.concat(SINGLE_QUOTE).concat(dq.getName()).concat(SINGLE_QUOTE).concat(AS).concat(RULENAME).concat(COMMA)
				.concat(SINGLE_QUOTE).concat(dq.getDependsOn().getRef().getUuid())
				.concat(SINGLE_QUOTE).concat(AS).concat(DATAPODUUID).concat(COMMA).concat(SINGLE_QUOTE)
				.concat(datapod.getVersion()).concat(SINGLE_QUOTE).concat(AS).concat(DATAPODVERSION)
				.concat(COMMA).concat(SINGLE_QUOTE).concat(datapod.getName()).concat(SINGLE_QUOTE).concat(AS)
				.concat(DATAPOD_NAME).concat(COMMA).concat(SINGLE_QUOTE);

		if (dq.getAttribute() != null) {
			select = select.concat(dq.getAttribute().getAttrId());
		}
		else {
			select = select.concat(commaSepAttrIds(getRowKeyList(datapod)));
		}
		select = select.concat(SINGLE_QUOTE).concat(AS).concat(ATTRIBUTE_ID).concat(COMMA).concat(SINGLE_QUOTE);

		if (dq.getAttribute() != null) {
			select = select.concat(attributeName);
		}
		else {
			select = select.concat(commaSepAttrNames(getRowKeyList(datapod)));
		}
		select = select.concat(SINGLE_QUOTE).concat(AS).concat(ATTRIBUTE_NAME).concat(COMMA);
		select = select.concat(SINGLE_QUOTE).concat(commaSepAttrNames(getRowKeyList(datapod))).concat(SINGLE_QUOTE).concat(AS)
						.concat(ROWKEY_NAME).concat(COMMA);
		select = select.concat(tildeSepAttrs(datapod.getName(), getRowKeyList(datapod))).concat(AS)
			.concat(ROWKEY_VALUE).concat(COMMA);
//		select = select.concat(tildeSepAttrs(datapod.getName(), getRowKeyList(datapod))).concat(AS)
//						.concat(ROWKEY_VALUE).concat(COMMA);

		if (dq.getAttribute() != null) {
			select = select.concat(" CAST(").concat(datapod.getName()).concat(DOT).concat(attributeName)
					.concat((dqDataSource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString()) 
							|| dqDataSource.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) ? " AS CHAR(50)) "
							: (dqDataSource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())
									? " AS VARCHAR2(70)) "
									: " AS STRING)"));
		} else {
//			select = select.concat(SINGLE_QUOTE).concat(SINGLE_QUOTE);
			select = select.concat(tildeSepAttrs(datapod.getName(), getRowKeyList(datapod)));
		}
		select = select.concat(AS).concat(ATTRIBUTE_VAL).concat(COMMA)
				.concat(generateCase(dq, tableName, attributeName)).concat(COMMA);
		select = select.concat(dataQualExec.getVersion()).concat(AS).concat(VERSION);
		return select;
	}

	private String generateFrom(DataQual dq, MetaIdentifier ref, String attributeName, List<String> datapodList, DagExec dagExec,
			Set<MetaIdentifier> usedRefKeySet, HashMap<String, String> otherParams, RunMode runMode) throws Exception {
		Datapod srcDP = null;
		String resp = null;
//		Relation relation = null;
//		DataSet dataSet = null;
		if (ref.getType() == MetaType.datapod) {
//			srcDP = (Datapod) daoRegister.getRefObject(ref);
			srcDP = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
			
			resp = FROM.concat(getTableName(srcDP, datapodList, dagExec, otherParams, runMode)).concat("  ").concat(srcDP.getName());
		}
		if (dq.getRefIntegrityCheck() != null 
				&& dq.getRefIntegrityCheck().getDependsOn() != null 
				&& dq.getRefIntegrityCheck().getDependsOn().getRef().getType() == MetaType.relation) {
			resp = FROM;
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
				.concat(generateRefIntFrom(dq, getTableName(srcDP, datapodList, dagExec, otherParams, runMode), attributeName,
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
//		Datapod datapod = (Datapod) daoRegister.getRefObject(dq.getDependsOn().getRef());
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dq.getDependsOn().getRef().getUuid(), dq.getDependsOn().getRef().getVersion(), dq.getDependsOn().getRef().getType().toString(), "N");
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
			dupJoinStr = dupJoinStr.concat(commaSepAttrsGroup(getRowKeyList(datapod))).concat(HAVING).concat(" AND ").concat(COUNT)
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
			dupJoinStr = dupJoinStr.concat(commaSepAttrsGroup(getRowKeyList(datapod))).concat(HAVING).concat(" AND ").concat(COUNT)
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
		if (dq == null || dq.getRefIntegrityCheck() == null || dq.getRefIntegrityCheck().getDependsOn() == null || dq.getRefIntegrityCheck().getDependsOn().getRef() == null) {
			return EMPTY;
		}
//		Datapod datapod = (Datapod) daoRegister.getRefObject(dq.getDependsOn().getRef());
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dq.getDependsOn().getRef().getUuid(), dq.getDependsOn().getRef().getVersion(), dq.getDependsOn().getRef().getType().toString(), "N");
		List<Attribute> rowKeyAttrList = null;
		rowKeyAttrList = getRowKeyList(datapod);
		if (rowKeyAttrList == null || rowKeyAttrList.isEmpty()) {
			//System.out.println("Datapod doesnot have row key. Hence aborting ref integrity check. ");
			return EMPTY;
		}
//		Datapod datapodRef = (Datapod) daoRegister.getRefObject(dq.getRefIntegrityCheck().getRef());
		Datapod datapodRef = null;
		Relation relation = null;
		if (dq.getRefIntegrityCheck().getDependsOn().getRef().getType() == MetaType.datapod) {
			datapodRef = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dq.getRefIntegrityCheck().getDependsOn().getRef().getUuid(), dq.getRefIntegrityCheck().getDependsOn().getRef().getVersion(), dq.getRefIntegrityCheck().getDependsOn().getRef().getType().toString(), "N");
			refIntStr = LEFT_OUTER_JOIN.concat(getTableName(datapodRef, datapodList, dagExec, otherParams, runMode))
					// .concat(AS)
					.concat(" ").concat(datapodRef.getName()).concat("_ref").concat(ON).concat(BRACKET_OPEN);
			refIntStr = refIntStr.concat(datapod.getName()).concat(DOT).concat(attributeName).concat(" = ")
					.concat(datapodRef.getName()).concat("_ref").concat(DOT)
					.concat(datapodRef.getAttribute(Integer.parseInt(dq.getRefIntegrityCheck().getTargetAttr().getAttrId())).getName())
					.concat(BRACKET_CLOSE);
			usedRefKeySet.add(dq.getRefIntegrityCheck().getTargetAttr().getRef());
		} else if (dq.getRefIntegrityCheck().getDependsOn().getRef().getType() == MetaType.relation) {
			relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(dq.getRefIntegrityCheck().getDependsOn().getRef().getUuid(), dq.getRefIntegrityCheck().getDependsOn().getRef().getVersion(), dq.getRefIntegrityCheck().getDependsOn().getRef().getType().toString(), "N");
			refIntStr = BRACKET_OPEN.concat(relationOperator.generateSql(relation, null, otherParams, null, usedRefKeySet, runMode)).concat(BRACKET_CLOSE);
//					.concat(" ").concat(relation.getName()).concat("_ref ");
			MetaIdentifier relationRef = new MetaIdentifier(MetaType.relation, relation.getUuid(), relation.getVersion());
			usedRefKeySet.add(relationRef);
		}

		
		
//		for (Attribute attribute : rowKeyAttrList) {
//			refIntStr = refIntStr.concat(datapod.getName()).concat(DOT).concat(attribute.getName()).concat(" = ")
//					.concat(datapodRef.getName()).concat("_ref").concat(DOT)
//					.concat(datapodRef.getAttribute(Integer.parseInt(dq.getRefIntegrityCheck().getAttrId())).getName())
//					.concat(AND);
//
//		}
//		refIntStr = refIntStr.substring(0, refIntStr.length() - AND.length()).concat(BRACKET_CLOSE);
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

	private String commaSepAttrIds(List<Attribute> attrList) {
		StringBuilder attrStr = new StringBuilder();
		String attrs = null;
		if (attrList == null || attrList.isEmpty()) {
			return null;
		}
		for (Attribute attribute : attrList) {
			attrStr.append(attribute.getAttributeId()).append(COMMA);
		}
		attrs = attrStr.toString();
		return attrs.substring(0, attrs.length() - 2);
	}

	private String commaSepAttrNames(List<Attribute> attrList) {
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

	public String generateFilter(DataQual dq, Set<MetaIdentifier> usedRefKeySet, RunMode runMode)
			throws Exception {
		if (dq.getFilterInfo() == null || dq.getFilterInfo().isEmpty()) {
			return "";
		}
		MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.dq, dq.getUuid(), dq.getVersion()));

		Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(dq);
		String filterStr = filterOperator2.generateSql(dq.getFilterInfo(), null, filterSource, null, usedRefKeySet, null, false, false, runMode, mapSourceDS);

		return filterStr; //filterOperator.generateSql(filterInfo, null, null, usedRefKeySet, false, false, null);

	}

	public String generateSql(DataQual dq, String tableName, String attributeName, List<String> datapodList,
			DataQualExec dataQualExec, DagExec dagExec, Set<MetaIdentifier> usedRefKeySet, HashMap<String, String> otherParams, RunMode runMode)
			throws Exception {
		String select = generateSelect(dq, dataQualExec, tableName, attributeName);
		if (StringUtils.isBlank(select)) {
			return null;
		}
		select = select.concat(generateFrom(dq, dq.getDependsOn().getRef(), attributeName, datapodList, dagExec, usedRefKeySet, otherParams, runMode))
				.concat(WHERE_1_1).concat(generateFilter(dq, usedRefKeySet, runMode));
		select = generateallCheckFlag(select, dq);
		logger.info("Detail SQL for dataQual : " + dq.getUuid() + " : " + StringUtils.isBlank(select));
		return select;
		
	}
	
	public String generateallCheckFlag (String detailSql, DataQual dataQual) {
		StringBuilder sql = new StringBuilder(SELECT)
											.append(RULEUUID).append(COMMA)
											.append(RULEVERSION).append(COMMA)
											.append(RULENAME).append(COMMA)
											.append(DATAPODUUID).append(COMMA)
				  							.append(DATAPODVERSION).append(COMMA)
				  							.append(DATAPOD_NAME).append(COMMA)
				  							.append(ATTRIBUTE_ID).append(COMMA)
				  							.append(ATTRIBUTE_NAME).append(COMMA)
				  							.append(ATTRIBUTE_VAL).append(COMMA)
				  							.append(ROWKEY_NAME).append(COMMA)
				  							.append(ROWKEY_VALUE).append(COMMA)
				  							.append(NULL_CHECK_PASS).append(COMMA)
				  							.append(VALUE_CHECK_PASS).append(COMMA)
				  							.append(RANGE_CHECK_PASS).append(COMMA)
				  							.append(DATATYPE_CHECK_PASS).append(COMMA)
				  							.append(FORMAT_CHECK_PASS).append(COMMA)
				  							.append(LENGTH_CHECK_PASS).append(COMMA)
				  							.append(REFINT_CHECK_PASS).append(COMMA)
				  							.append(DUP_CHECK_PASS).append(COMMA)
				  							.append(CUSTOM_CHECK_PASS).append(COMMA)
				  							.append(DOMAIN_CHECK_PASS).append(COMMA)
				  							.append(BLANK_SPACE_CHECK_PASS).append(COMMA)
				  							.append(EXPRESSION_CHECK_PASS).append(COMMA)
				  							.append(CASE_CHECK_PASS).append(COMMA)
											.append(CASE_WHEN).append(BRACKET_OPEN)
											.append(NULL_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_N).append(OR)
											.append(VALUE_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_N).append(OR)
											.append(RANGE_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_N).append(OR)
											.append(DATATYPE_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_N).append(OR)
											.append(FORMAT_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_N).append(OR)
											.append(LENGTH_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_N).append(OR)
											.append(REFINT_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_N).append(OR)
											.append(DUP_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_N).append(OR)
											.append(CUSTOM_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_N).append(OR)
											.append(DOMAIN_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_N).append(OR)
											.append(BLANK_SPACE_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_N).append(OR)
											.append(EXPRESSION_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_N).append(OR)
											.append(CASE_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_N)
											.append(BRACKET_CLOSE) 
											.append(THEN_N_Y).append(ALL_CHECK_PASS).append(COMMA)
											.append(VERSION).append(FROM).append(BRACKET_OPEN)
											.append(detailSql).append(BRACKET_CLOSE).append(DQ_RESULT_READY_ALIAS);
		
		return sql.toString();
	}
	
	public String generateSummarySql(DataQual dq, List<String> datapodList,
			DataQualExec dataQualExec, DagExec dagExec, MetaIdentifier summaryDpRef, Set<MetaIdentifier> usedRefKeySet, HashMap<String, String> otherParams, RunMode runMode)
			throws Exception {
		// Find result sql
		Datapod summaryDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(summaryDpRef.getUuid(), summaryDpRef.getVersion(), summaryDpRef.getType().toString(), "N");
		String summaryTableName = getTableName(summaryDp, datapodList, dagExec, otherParams, runMode);
		String resSql = dataQualExec.getExec();
		String sql = generateSummarySql4(dq, generateSummarySql3(generateSummarySql2(generateSummarySql1(resSql)), summaryTableName,dq));
		return sql;
	}
	
	public String generateSummarySql4 (DataQual dq, String summarySql3) {
		StringBuilder select = new StringBuilder(SELECT)
				  .append(RULEUUID).append(COMMA)
				  .append(RULEVERSION).append(COMMA)
				  .append(RULENAME).append(COMMA)
				  .append(DATAPODUUID).append(COMMA)
				  .append(DATAPODVERSION).append(COMMA)
				  .append(DATAPOD_NAME).append(COMMA)
				  .append(TOTAL_ROW_COUNT).append(COMMA)
				  .append(TOTAL_PASS_COUNT).append(COMMA)
				  .append(TOTAL_FAIL_COUNT).append(COMMA)
				  .append(generateThresholdSql(dq, STD_DEV_FAIL)).append(COMMA)
				  .append(SCORE).append(COMMA)
				  .append(VERSION).append(COMMA)
				  .append(STD_DEV_FAIL.toLowerCase()).append(FROM).append(BRACKET_OPEN)
				  .append(summarySql3).append(BRACKET_CLOSE).append(DQ_RESULT_SUM_ALIAS);
		

		return select.toString();
	}
	
	public String generateSummarySql3 (String summarySql2, String summaryTableName, DataQual dq) {
		StringBuilder select = new StringBuilder(SELECT)
				  .append(RULEUUID).append(COMMA)
				  .append(RULEVERSION).append(COMMA)
				  .append(RULENAME).append(COMMA)
				  .append(DATAPODUUID).append(COMMA)
				  .append(DATAPODVERSION).append(COMMA)
				  .append(DATAPOD_NAME).append(COMMA)
				  .append(TOTAL_ROW_COUNT).append(COMMA)
				  .append(TOTAL_PASS_COUNT).append(COMMA)
				  .append(BRACKET_OPEN).append(TOTAL_ROW_COUNT).append(MINUS).append(TOTAL_PASS_COUNT).append(BRACKET_CLOSE).append(AS).append(TOTAL_FAIL_COUNT).append(COMMA)
				  .append(BRACKET_OPEN).append(TOTAL_PASS_COUNT).append(DIVIDE_BY).append(TOTAL_ROW_COUNT).append(BRACKET_CLOSE).append(MULTIPLY_BY).append(" 100 ").append(AS).append(SCORE).append(COMMA)
				  .append(VERSION).append(COMMA)
				  .append(STD_DEV_FAIL).append(FROM).append(BRACKET_OPEN)
				  .append(summarySql2).append(BRACKET_CLOSE).append(DQ_RESULT_SUM_ALIAS)
				  .append(CROSS_JOIN).append(BRACKET_OPEN).append(SELECT)
					// added filter by vaibhav
			   	  .append(ConstantsUtil.CASE_WHEN).append(BRACKET_OPEN).append(STDDEV).append(BRACKET_OPEN)
				  .append(TOTAL_FAIL_COUNT).append(BRACKET_CLOSE).append(EQUAL_TO).append("'NaN'").append(OR)
				  .append(STDDEV).append(BRACKET_OPEN).append(TOTAL_FAIL_COUNT).append(BRACKET_CLOSE).append("IS")
				  .append(" NULL").append(BRACKET_CLOSE).append("THEN").append(" 1 ").append("ELSE").append(STDDEV)
				  .append(BRACKET_OPEN).append(TOTAL_FAIL_COUNT).append(BRACKET_CLOSE).append("END")
				  .append(AS).append(STD_DEV_FAIL)
				  .append(FROM).append(summaryTableName).append(ConstantsUtil.WHERE_1_1).append(ConstantsUtil.AND)
				  .append("rule_uuid='" + dq.getUuid() + "'").append(ConstantsUtil.AND)
				  .append("datapod_uuid='" + dq.getDependsOn().getRef().getUuid()+ "'").append(BRACKET_CLOSE).append(DQ_SUMMARY_ALIAS);
		return select.toString();
	}
	
	/**
	 * 
	 * @param dq
	 * @return
	 */
	public String generateThresholdSql(DataQual dq, String failStddev) {
		StringBuilder threshold = new StringBuilder();
		if (dq.getThresholdInfo() == null || StringUtils.isBlank(dq.getThresholdInfo().getLow())) {
			return threshold.append("''").append(AS).append(THRESHOLD_TYPE).append(COMMA)
							.append("''").append(AS).append(THRESHOLD_LIMIT).append(COMMA)
							.append(LOW).append(AS).append(THRESHOLD_IND).toString();
		}
		String failCount = BRACKET_OPEN.concat(TOTAL_ROW_COUNT).concat(MINUS).concat(TOTAL_PASS_COUNT).concat(BRACKET_CLOSE);
		String failPct = BRACKET_OPEN.concat(failCount).concat(DIVIDE_BY).concat(TOTAL_ROW_COUNT).concat(BRACKET_CLOSE).concat(MULTIPLY_BY).concat(" 100 ");
		String thresholdType = null;
		String thresholdLimit = null;
		String thresholdInd = null;
		if (dq.getThresholdInfo().getType().equals(ThresholdType.NUMERIC)) {
			thresholdInd = 
					CASE_WHEN.concat(BRACKET_OPEN).concat(failCount).concat(LESS_THAN_EQUALS).concat(dq.getThresholdInfo().getLow()).concat(BRACKET_CLOSE).concat(ONLY_THEN).concat(LOW)
					 .concat(ONLY_WHEN).concat(BRACKET_OPEN).concat(failCount).concat(LESS_THAN_EQUALS).concat(dq.getThresholdInfo().getMedium()).concat(BRACKET_CLOSE).concat(ONLY_THEN).concat(MEDIUM)
					 .concat(ONLY_ELSE).concat(HIGH).concat(ONLY_END).concat(AS).concat(THRESHOLD_IND);
			thresholdLimit = 
					CASE_WHEN.concat(BRACKET_OPEN).concat(failCount).concat(LESS_THAN_EQUALS).concat(dq.getThresholdInfo().getLow()).concat(BRACKET_CLOSE).concat(ONLY_THEN).concat(dq.getThresholdInfo().getLow())
					 .concat(ONLY_WHEN).concat(BRACKET_OPEN).concat(failCount).concat(LESS_THAN_EQUALS).concat(dq.getThresholdInfo().getMedium()).concat(BRACKET_CLOSE).concat(ONLY_THEN).concat(dq.getThresholdInfo().getMedium())
					 .concat(ONLY_ELSE).concat(dq.getThresholdInfo().getHigh()).concat(ONLY_END).concat(AS).concat(THRESHOLD_LIMIT);
			thresholdType = SINGLE_QUOTE.concat(dq.getThresholdInfo().getType().toString()).concat(SINGLE_QUOTE).concat(AS).concat(THRESHOLD_TYPE);
		} else if (dq.getThresholdInfo().getType().equals(ThresholdType.PERCENTAGE)) {
			thresholdInd = 
					CASE_WHEN.concat(BRACKET_OPEN).concat(failPct).concat(LESS_THAN_EQUALS).concat(dq.getThresholdInfo().getLow()).concat(BRACKET_CLOSE).concat(ONLY_THEN).concat(LOW)
					 .concat(ONLY_WHEN).concat(BRACKET_OPEN).concat(failPct).concat(LESS_THAN_EQUALS).concat(dq.getThresholdInfo().getMedium()).concat(BRACKET_CLOSE).concat(ONLY_THEN).concat(MEDIUM)
					 .concat(ONLY_ELSE).concat(HIGH).concat(ONLY_END).concat(AS).concat(THRESHOLD_IND);
			thresholdLimit = 
					CASE_WHEN.concat(BRACKET_OPEN).concat(failPct).concat(LESS_THAN_EQUALS).concat(dq.getThresholdInfo().getLow()).concat(BRACKET_CLOSE).concat(ONLY_THEN).concat(dq.getThresholdInfo().getLow())
					 .concat(ONLY_WHEN).concat(BRACKET_OPEN).concat(failPct).concat(LESS_THAN_EQUALS).concat(dq.getThresholdInfo().getMedium()).concat(BRACKET_CLOSE).concat(ONLY_THEN).concat(dq.getThresholdInfo().getMedium())
					 .concat(ONLY_ELSE).concat(dq.getThresholdInfo().getHigh()).concat(ONLY_END).concat(AS).concat(THRESHOLD_LIMIT);
			thresholdType = SINGLE_QUOTE.concat(dq.getThresholdInfo().getType().toString()).concat(SINGLE_QUOTE).concat(AS).concat(THRESHOLD_TYPE);
		} else if (dq.getThresholdInfo().getType().equals(ThresholdType.STDDEV)) {
			thresholdInd = 
					CASE_WHEN.concat(BRACKET_OPEN).concat(failCount).concat(LESS_THAN_EQUALS).concat(dq.getThresholdInfo().getLow()).concat(MULTIPLY_BY).concat(failStddev).concat(BRACKET_CLOSE).concat(ONLY_THEN).concat(LOW)
					 .concat(ONLY_WHEN).concat(BRACKET_OPEN).concat(failCount).concat(LESS_THAN_EQUALS).concat(dq.getThresholdInfo().getMedium()).concat(MULTIPLY_BY).concat(failStddev).concat(BRACKET_CLOSE).concat(ONLY_THEN).concat(MEDIUM)
					 .concat(ONLY_ELSE).concat(HIGH).concat(ONLY_END).concat(AS).concat(THRESHOLD_IND);
			thresholdLimit = 
					CASE_WHEN.concat(BRACKET_OPEN).concat(failCount).concat(LESS_THAN_EQUALS).concat(dq.getThresholdInfo().getLow()).concat(MULTIPLY_BY).concat(failStddev).concat(BRACKET_CLOSE).concat(ONLY_THEN).concat(dq.getThresholdInfo().getLow())
					 .concat(ONLY_WHEN).concat(BRACKET_OPEN).concat(failCount).concat(LESS_THAN_EQUALS).concat(dq.getThresholdInfo().getMedium()).concat(MULTIPLY_BY).concat(failStddev).concat(BRACKET_CLOSE).concat(ONLY_THEN).concat(dq.getThresholdInfo().getMedium())
					 .concat(ONLY_ELSE).concat(dq.getThresholdInfo().getHigh()).concat(ONLY_END).concat(AS).concat(THRESHOLD_LIMIT);
			thresholdType = SINGLE_QUOTE.concat(dq.getThresholdInfo().getType().toString()).concat(SINGLE_QUOTE).concat(AS).concat(THRESHOLD_TYPE);
		}  
		
		return threshold.append(thresholdType).append(COMMA)
				.append(thresholdLimit).append(COMMA)
				.append(thresholdInd).toString();
	}
	
//	public String generateSummarySql3 (String summarySql2) {
//		StringBuilder sql = new StringBuilder(SELECT)
//											.append(RULEUUID).append(COMMA)
//											.append(RULEVERSION).append(COMMA)
//											.append(RULENAME).append(COMMA)
//											.append(DATAPODUUID).append(COMMA)
//				  							.append(DATAPODVERSION).append(COMMA)
//				  							.append(DATAPOD_NAME).append(COMMA);
//		sql = sql.append(SUM).append(BRACKET_OPEN).append(ALL_CHECK_PASS).append(BRACKET_CLOSE).append(AS).append(ALL_CHECK_PASS).append(COMMA)
//						.append(COUNT).append(BRACKET_OPEN).append(NULL_CHECK_P).append(BRACKET_CLOSE).append(AS).append(TOTAL_ROW_COUNT).append(COMMA)
//						.append(SUM).append(BRACKET_OPEN).append(ALL_CHECK_PASS).append(BRACKET_CLOSE).append(AS).append(TOTAL_PASS_COUNT).append(COMMA)
//						.append(VERSION).append(FROM).append(BRACKET_OPEN)
//						.append(summarySql2).append(BRACKET_CLOSE).append(DQ_RESULT_READY_ALIAS);
//		sql = sql.append(GROUP_BY)
//					.append(RULEUUID).append(COMMA)
//					.append(RULEVERSION).append(COMMA)
//					.append(RULENAME).append(COMMA)
//					.append(DATAPODUUID).append(COMMA)
//					.append(DATAPODVERSION).append(COMMA)
//					.append(DATAPOD_NAME).append(COMMA)
//					.append(VERSION);
//		
//		return sql.toString();
//	}
	
	
	public String generateSummarySql2 (String summarySql1) {
		StringBuilder sql = new StringBuilder(SELECT)
											.append(RULEUUID).append(COMMA)
											.append(RULEVERSION).append(COMMA)
											.append(RULENAME).append(COMMA)
											.append(DATAPODUUID).append(COMMA)
				  							.append(DATAPODVERSION).append(COMMA)
				  							.append(DATAPOD_NAME).append(COMMA);

		sql = sql.append(COUNT).append(BRACKET_OPEN).append(ALL_CHECK_PASS).append(BRACKET_CLOSE).append(AS).append(TOTAL_ROW_COUNT).append(COMMA)
					.append(SUM).append(BRACKET_OPEN).append(CASE_WHEN).append(ALL_CHECK_PASS).append(EQUAL_TO).append(SINGLE_QUOTED_Y).append("THEN 1 ELSE 0 END").append(BRACKET_CLOSE).append(AS).append(TOTAL_PASS_COUNT).append(COMMA)
					.append(VERSION).append(FROM).append(BRACKET_OPEN)
					.append(summarySql1).append(BRACKET_CLOSE).append(DQ_RESULT_READY_ALIAS);

		sql = sql.append(GROUP_BY)
					.append(RULEUUID).append(COMMA)
					.append(RULEVERSION).append(COMMA)
					.append(RULENAME).append(COMMA)
					.append(DATAPODUUID).append(COMMA)
					.append(DATAPODVERSION).append(COMMA)
					.append(DATAPOD_NAME).append(COMMA)
					.append(VERSION);
		
		return sql.toString();
	}
	
	public String generateSummarySql1 (String resSql) {
		StringBuilder select = new StringBuilder(SELECT)
								.append(RULEUUID).append(COMMA)
								.append(RULEVERSION).append(COMMA)
								.append(RULENAME).append(COMMA)
								.append(DATAPODUUID).append(COMMA)
								.append(DATAPODVERSION).append(COMMA)
								.append(DATAPOD_NAME).append(COMMA)
								.append(ALL_CHECK_PASS).append(COMMA);
		select = select
					.append(generateSummarySql1Case(NULL_CHECK_PASS, SINGLE_QUOTED_Y, NULL_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(NULL_CHECK_PASS, SINGLE_QUOTED_N, NULL_CHECK_F)).append(COMMA)
					.append(generateSummarySql1Case(VALUE_CHECK_PASS, SINGLE_QUOTED_Y, VALUE_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(VALUE_CHECK_PASS, SINGLE_QUOTED_N, VALUE_CHECK_F)).append(COMMA)
					.append(generateSummarySql1Case(RANGE_CHECK_PASS, SINGLE_QUOTED_Y, RANGE_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(RANGE_CHECK_PASS, SINGLE_QUOTED_N, RANGE_CHECK_F)).append(COMMA)
					.append(generateSummarySql1Case(DATATYPE_CHECK_PASS, SINGLE_QUOTED_Y, DATATYPE_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(DATATYPE_CHECK_PASS, SINGLE_QUOTED_N, DATATYPE_CHECK_F)).append(COMMA)
					.append(generateSummarySql1Case(FORMAT_CHECK_PASS, SINGLE_QUOTED_Y, FORMAT_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(FORMAT_CHECK_PASS, SINGLE_QUOTED_N, FORMAT_CHECK_F)).append(COMMA)
					.append(generateSummarySql1Case(LENGTH_CHECK_PASS, SINGLE_QUOTED_Y, LENGTH_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(LENGTH_CHECK_PASS, SINGLE_QUOTED_N, LENGTH_CHECK_F)).append(COMMA)
					.append(generateSummarySql1Case(REFINT_CHECK_PASS, SINGLE_QUOTED_Y, REFINT_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(REFINT_CHECK_PASS, SINGLE_QUOTED_N, REFINT_CHECK_F)).append(COMMA)
					.append(generateSummarySql1Case(DUP_CHECK_PASS, SINGLE_QUOTED_Y, DUP_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(DUP_CHECK_PASS, SINGLE_QUOTED_N, DUP_CHECK_F)).append(COMMA)
					.append(generateSummarySql1Case(CUSTOM_CHECK_PASS, SINGLE_QUOTED_Y, CUSTOM_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(CUSTOM_CHECK_PASS, SINGLE_QUOTED_N, CUSTOM_CHECK_F)).append(COMMA)
					.append(generateSummarySql1Case(DOMAIN_CHECK_PASS, SINGLE_QUOTED_Y, DOMAIN_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(DOMAIN_CHECK_PASS, SINGLE_QUOTED_N, DOMAIN_CHECK_F)).append(COMMA)
					.append(generateSummarySql1Case(BLANK_SPACE_CHECK_PASS, SINGLE_QUOTED_Y, BLANK_SPACE_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(BLANK_SPACE_CHECK_PASS, SINGLE_QUOTED_N, BLANK_SPACE_CHECK_F)).append(COMMA)
					.append(generateSummarySql1Case(EXPRESSION_CHECK_PASS, SINGLE_QUOTED_Y, EXPRESSION_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(EXPRESSION_CHECK_PASS, SINGLE_QUOTED_N, EXPRESSION_CHECK_F)).append(COMMA)
					.append(generateSummarySql1Case(CASE_CHECK_PASS, SINGLE_QUOTED_Y, CASE_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(CASE_CHECK_PASS, SINGLE_QUOTED_N, CASE_CHECK_F)).append(COMMA)
					.append(generateSummarySql1Case(ALL_CHECK_PASS, SINGLE_QUOTED_Y, ALL_CHECK_P)).append(COMMA)
					.append(generateSummarySql1Case(ALL_CHECK_PASS, SINGLE_QUOTED_N, ALL_CHECK_F)).append(COMMA)
					.append(VERSION).append(FROM).append(BRACKET_OPEN)
					.append(resSql).append(BRACKET_CLOSE).append(DQ_RESULT_ALIAS);
		return select.toString();
	}
	
	public String generateSummarySql1Case(String checkField, String checkString, String aliasField) {
		return CASE_WHEN + checkField + " = " + checkString + THEN_1_0 + aliasField;
	}

	public String generateCase(DataQual dq, String tableName, String attributeName)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Datasource dataSource = commonServiceImpl.getDatasourceByApp();

		StringBuilder dqBuilder = new StringBuilder();
		String dqString = null;
		String check = null;
		String colName = null;
//		Datapod datapod = (Datapod) daoRegister.getRefObject(dq.getDependsOn().getRef());
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dq.getDependsOn().getRef().getUuid(), dq.getDependsOn().getRef().getVersion(), dq.getDependsOn().getRef().getType().toString(), "N");
		
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
			colName = FORMAT_CHECK_PASS;
			dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
		} else {
			dqBuilder.append("'' as ").append(FORMAT_CHECK_PASS).append(COMMA);
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
		if (dq.getRefIntegrityCheck() != null && dq.getRefIntegrityCheck().getTargetAttr() != null && 
				dq.getRefIntegrityCheck().getTargetAttr().getRef() != null) {
//			Datapod refIntTab = (Datapod) daoRegister.getRefObject(dq.getRefIntegrityCheck().getRef());
			
			if (dq.getRefIntegrityCheck().getDependsOn().getRef().getType() == MetaType.datapod) {
				Datapod refIntTab = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dq.getRefIntegrityCheck().getTargetAttr().getRef().getUuid(), dq.getRefIntegrityCheck().getTargetAttr().getRef().getVersion(), MetaType.datapod.toString(), "N");
				
				dq.getRefIntegrityCheck().getTargetAttr().setAttrName(
						refIntTab.getAttribute(Integer.parseInt(dq.getRefIntegrityCheck().getTargetAttr().getAttrId())).getName());
				check = refIntTab.getName().concat("_ref").concat(DOT).concat(dq.getRefIntegrityCheck().getTargetAttr().getAttrName())
						.concat(IS_NOT_NULL);
			} else if (dq.getRefIntegrityCheck().getDependsOn().getRef().getType() == MetaType.relation) {
				Relation refIntTab = (Relation) commonServiceImpl.getOneByUuidAndVersion(dq.getRefIntegrityCheck().getDependsOn().getRef().getUuid(), dq.getRefIntegrityCheck().getDependsOn().getRef().getVersion(), dq.getRefIntegrityCheck().getDependsOn().getRef().getType().toString(), "N");
				Datapod targetDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dq.getRefIntegrityCheck().getTargetAttr().getRef().getUuid(), dq.getRefIntegrityCheck().getTargetAttr().getRef().getVersion(), dq.getRefIntegrityCheck().getTargetAttr().getRef().getType().toString(), "N");
				dq.getRefIntegrityCheck().getTargetAttr().setAttrName(
						targetDp.getAttribute(Integer.parseInt(dq.getRefIntegrityCheck().getTargetAttr().getAttrId())).getName());
				check = targetDp.getName().concat(DOT).concat(dq.getRefIntegrityCheck().getTargetAttr().getAttrName())
						.concat(IS_NOT_NULL);
			}
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
		if (dq.getDomainCheck() != null && dq.getDomainCheck().getRef() != null) {
			AttributeDomain attributeDomain = (AttributeDomain) commonServiceImpl.getOneByUuidAndVersion(dq.getDomainCheck().getRef().getUuid()
													, dq.getDomainCheck().getRef().getVersion()
													, dq.getDomainCheck().getRef().getType().toString()
													, "N");
			check = tableAttr.concat(" RLIKE ( '").concat(attributeDomain.getRegEx()).concat("'").concat(BRACKET_CLOSE);
			colName = DOMAIN_CHECK_PASS;
			dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
		} else {
			dqBuilder.append("'' as ").append(DOMAIN_CHECK_PASS).append(COMMA);
		} // End domainCheck If
		if (StringUtils.isNotBlank(dq.getBlankSpaceCheck())) {
			
			if (dq.getBlankSpaceCheck().equals(BlankSpaceCheckOptions.LEADING.toString())) {
				check = tableAttr.concat(" LIKE ' %' ");
			} else if (dq.getBlankSpaceCheck().equals(BlankSpaceCheckOptions.TRAILING.toString())) {
				check = tableAttr.concat(" LIKE '% ' ");
			} else if (dq.getBlankSpaceCheck().equals(BlankSpaceCheckOptions.IN_BETWEEN.toString())) {
				check = tableAttr.concat(" LIKE '% ' ");
			} else if (dq.getBlankSpaceCheck().equals(BlankSpaceCheckOptions.ALL.toString())) {
				check = tableAttr.concat(" LIKE '% ' AND ").concat(tableAttr).concat(" LIKE ' %' ");
			}
			colName = BLANK_SPACE_CHECK_PASS;
			dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
		} else {
			dqBuilder.append("'' as ").append(BLANK_SPACE_CHECK_PASS).append(COMMA);
		} // End blankSpaceCheck If
		if (dq.getExpressionCheck() != null && dq.getExpressionCheck().getRef() != null) {
			Expression expression = (Expression) commonServiceImpl.getOneByUuidAndVersion(dq.getExpressionCheck().getRef().getUuid()
																						, dq.getExpressionCheck().getRef().getVersion()
																						, dq.getExpressionCheck().getRef().getType().toString()
																						, "N");
			MetaIdentifierHolder expressionSource = new MetaIdentifierHolder(dq.getExpressionCheck().getRef());
			check = expressionOperator.generateSql(expression.getExpressionInfo(), expressionSource, null, new HashMap<String, String>(), null, commonServiceImpl.getDatasourceByObject(dq));
			colName = EXPRESSION_CHECK_PASS;
			dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
		} else {
			dqBuilder.append("'' as ").append(EXPRESSION_CHECK_PASS).append(COMMA);
		} // End expressionCheck If		
		
		if(dq.getCaseCheck() != null) {
			check = dq.getAttribute().getAttrName().concat(EQUAL_TO).concat(dq.getCaseCheck().toString()).concat(BRACKET_OPEN).concat(dq.getAttribute().getAttrName()).concat(BRACKET_CLOSE);
			colName = CASE_CHECK_PASS;
			dqBuilder.append(caseWrapper(check, colName)).append(COMMA);
		} else {
			dqBuilder.append("'' AS ").append(CASE_CHECK_PASS).append(COMMA);
		}
		
		dqString = dqBuilder.toString();
		return dqString.substring(0, dqString.length() - 2);
	} // End of caseCheck If

	private String caseWrapper(String check, String colName) {
		StringBuilder caseBuilder = new StringBuilder(CASE_WHEN).append(check).append(THEN).append(colName)
				.append(BLANK);
		return caseBuilder.toString();
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(baseExec.getUuid(), baseExec.getVersion(), MetaType.dqExec.toString(), "N");
		synchronized (dataQualExec.getUuid()) {
			commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.STARTING);
		}
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		DataQual dataQual = (DataQual) commonServiceImpl.getOneByUuidAndVersion(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), MetaType.dq.toString(), "Y");
		try{
			dataQualExec.setExec(generateSql(dataQual, null, dataQualExec, null, usedRefKeySet, execParams.getOtherParams(), runMode));
			dataQualExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
			
			synchronized (dataQualExec.getUuid()) {
				DataQualExec dataQualExec1 = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(dataQualExec.getUuid(), dataQualExec.getVersion(), MetaType.dqExec.toString(), "N");
				
				dataQualExec1.setExec(dataQualExec.getExec());
				dataQualExec1.setRefKeyList(dataQualExec.getRefKeyList());
				commonServiceImpl.save(MetaType.dqExec.toString(), dataQualExec1);
				dataQualExec1 = null;
				commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.READY);
			}
			}catch(Exception e){
				commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.FAILED);
				e.printStackTrace();
				String message = null;
				try {
					message = e.getMessage();
				}catch (Exception e2) {
					// TODO: handle exception
				}
				
				MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
				dependsOn.setRef(new MetaIdentifier(MetaType.dqExec, dataQualExec.getUuid(), dataQualExec.getVersion()));
				commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "READY data quality parsing.", dependsOn);
				throw new Exception((message != null) ? message : "READY data quality parsing.");
			}
		return dataQualExec;
	}

}
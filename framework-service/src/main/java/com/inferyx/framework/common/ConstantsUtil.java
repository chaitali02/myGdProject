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
package com.inferyx.framework.common;

public class ConstantsUtil {
	
	public static final String BLANK = " ";
	public static final String CASE_WHEN = " CASE WHEN ";
	public static final String THEN = " THEN 'Y' ELSE 'N' END as ";
	public static final String SELECT = " SELECT ";
	public static final String FROM = " FROM ";
	public static final String GROUP_BY = " GROUP BY ";
	public static final String HAVING_1_1 = " HAVING (1=1) ";
	public static final String DOT = ".";
	public static final String IS_NOT_NULL = " IS NOT NULL ";
	public static final String NULL_CHECK_PASS = "nullCheck_pass";
	public static final String VALUE_CHECK_PASS = "valueCheck_pass";
	public static final String RANGE_CHECK_PASS = "rangeCheck_pass";
	public static final String DATATYPE_CHECK_PASS = "dataTypeCheck_pass";
	public static final String DATEFORMAT_CHECK_PASS = "dataFormatCheck_pass";
	public static final String LENGTH_CHECK_PASS = "lengthCheck_pass";
	public static final String STDDEV_CHECK_PASS = "stdDevCheck_pass";
	public static final String UNDERSCORE = "_";
	public static final String COMMA = ", ";
	public static final String BETWEEN = " BETWEEN ";
	public static final String AND = " AND ";
	public static final String GREATER_THAN = " > ";
	public static final String LESS_THAN = " < ";
	public static final String SINGLE_QUOTE = "'";
	public static final String AS = " AS ";
	public static final String WHERE_1_1 = " WHERE (1=1) ";
	public static final String CONCAT= " CONCAT";
	public static final String IN= " IN ";
	public static final String DATAPODUUID = "DatapodUUID";
	public static final String DATAPODVERSION = "DatapodVersion";
	public static final String ATTRIBUTE_ID = "AttributeId";
	public static final String ATTRIBUTE_VAL = "AttributeValue";
	public static final String ROWNUM = "RowNumber";
	public static final String UNION_ALL = " UNION ALL ";
	public static final String LIMIT = " LIMIT ";
	// Datastore
	public static final String PERSIST_MODE_MEMORY_ONLY = "MEMORY_ONLY";
	public static final String PERSIST_MODE_DISK_ONLY = "DISK_ONLY";
	public static final String PERSIST_MODE_MEMORY_AND_DISK = "MEMORY_AND_DISK";
	// Operator
	public static final String EXEC_PARAMS = "EXEC_PARAMS";
	public static final String BRACKET_OPEN = "( ";
	public static final String BRACKET_CLOSE = " ) ";
	// Secret
	public static final String SECRET = "s3cret!";
	
}

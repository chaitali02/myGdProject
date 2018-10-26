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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Ingest;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.enums.IngestionType;
import com.inferyx.framework.enums.RunMode;

/**
 * @author Ganesh
 *
 */
@Component
public class IngestOperator {

	@Autowired
	private AttributeMapOperator attributeMapOperator;
	@Autowired
	private FilterOperator filterOperator;
	@Autowired
	FilterOperator2 filterOperator2;
	
	static Logger logger = Logger.getLogger(IngestOperator.class);
	
	public String generateSQL(Ingest ingest, String tableName, String incrColName, String incrLastValue, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception {
		return generateSelect(ingest, refKeyMap, otherParams, execParams, runMode)
				.concat(getFrom())
				.concat(generateFrom(ingest, tableName))
				.concat(generateWhere(ingest, incrColName, incrLastValue))
				.concat(generateFilter(ingest, refKeyMap, otherParams, usedRefKeySet, execParams, runMode))
				.concat(generateGroupBy(ingest, refKeyMap, otherParams, execParams))
				.concat(generateHaving(ingest, refKeyMap, otherParams, usedRefKeySet, execParams, runMode));
		
	}

	public String generateGroupBy(Ingest ingest, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String query = attributeMapOperator.selectGroupBy(ingest.getAttributeMap(), refKeyMap, otherParams, execParams);
		logger.info(query);
		return query;
	}

	public String generateFilter(Ingest ingest, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception {
		if (ingest.getFilterInfo() != null && !ingest.getFilterInfo().isEmpty()) {
			MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.ingest, ingest.getUuid(), ingest.getVersion()));

		    return filterOperator2.generateSql(ingest.getFilterInfo(), refKeyMap, filterSource, otherParams, usedRefKeySet, execParams, false, false, runMode);

			//return filterOperator.generateSql(ingest.getFilterInfo(), refKeyMap, otherParams, usedRefKeySet, execParams, false, false, runMode);
		}
		return ConstantsUtil.BLANK;
	}
	
	public String generateHaving (Ingest ingest, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception {
		if (ingest.getFilterInfo() != null && !ingest.getFilterInfo().isEmpty()) {
			MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.ingest, ingest.getUuid(), ingest.getVersion()));

			String filterStr = filterOperator2.generateSql(ingest.getFilterInfo(), refKeyMap, filterSource, otherParams, usedRefKeySet, execParams, true, true, runMode);

			//String filterStr = filterOperator.generateSql(ingest.getFilterInfo(), refKeyMap, otherParams, usedRefKeySet, execParams, true, true, runMode);
			return StringUtils.isBlank(filterStr)?ConstantsUtil.BLANK : ConstantsUtil.HAVING_1_1.concat(filterStr);
		}
		return ConstantsUtil.BLANK;
	}

	public String generateWhere(Ingest ingest,String incrColName, String incrLastValue) {
		IngestionType ingestionType = Helper.getIngestionType(ingest.getType());
		return " WHERE " + (incrLastValue != null ? incrColName+" > "+incrLastValue : "1=1") 
				+ (!ingestionType.equals(IngestionType.TABLETOTABLE) ? "" : " AND $CONDITIONS");
	}

	public String generateFrom(Ingest ingest, String tableName) {
		return tableName;
	}

	public String getFrom() {
		return  ConstantsUtil.FROM;
	}

	public String generateSelect(Ingest ingest, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, ExecParams execParams, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return ConstantsUtil.SELECT.concat(attributeMapOperator.generateSql(ingest.getAttributeMap(), ingest.getSourceDetail(), refKeyMap, otherParams, execParams));
	}
}

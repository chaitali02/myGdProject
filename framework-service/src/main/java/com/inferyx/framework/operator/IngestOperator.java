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
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.AttributeMap;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Ingest;
import com.inferyx.framework.domain.MetaIdentifier;
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
	
	static Logger logger = Logger.getLogger(IngestOperator.class);
	
	public String generateSQL(Ingest ingest, String tableName, String incrColName, String incrLastValue, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return generateSelect(ingest, refKeyMap, otherParams, execParams, runMode)
				.concat(getFrom())
				.concat(generateFrom(ingest, tableName))
				.concat(generateWhere(ingest, incrColName, incrLastValue))
				.concat(generateFilter(ingest, refKeyMap, otherParams, usedRefKeySet, execParams))
				.concat(generateGroupBy(ingest, refKeyMap, otherParams, execParams));
	}

	private String generateGroupBy(Ingest ingest, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
//		List<AttributeMap> attrMapList = new ArrayList<>();
//		for(AttributeMap attributeMap : ingest.getAttributeMap()) {
//			AttributeMap sourceAttrMap = new AttributeMap();
//			sourceAttrMap.setAttrMapId(attributeMap.getAttrMapId());
//			sourceAttrMap.setSourceAttr(attributeMap.getSourceAttr());
//			attrMapList.add(sourceAttrMap);
//		}
		return attributeMapOperator.selectGroupBy(ingest.getAttributeMap(), refKeyMap, otherParams, execParams);
	}

	private String generateFilter(Ingest ingest, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if (ingest.getFilterInfo() != null && !ingest.getFilterInfo().isEmpty()) {
			return filterOperator.generateSql(ingest.getFilterInfo(), refKeyMap, otherParams, usedRefKeySet, execParams);
		}
		return ConstantsUtil.BLANK;
	}

	private String generateWhere(Ingest ingest,String incrColName, String incrLastValue) {
		IngestionType ingestionType = Helper.getIngestionType(ingest.getType());
		return " WHERE " + (incrLastValue != null ? incrColName+">"+incrLastValue : "1=1") + (ingestionType.equals(IngestionType.FILETOFILE) ? "" : " AND $CONDITIONS");
	}

	private String generateFrom(Ingest ingest, String tableName) {
		return tableName;
	}

	private String getFrom() {
		return  ConstantsUtil.FROM;
	}

	private String generateSelect(Ingest ingest, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, ExecParams execParams, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
//		List<AttributeMap> attrMapList = new ArrayList<>();
//		for(AttributeMap attributeMap : ingest.getAttributeMap()) {
//			AttributeMap sourceAttrMap = new AttributeMap();
//			sourceAttrMap.setAttrMapId(attributeMap.getAttrMapId());
//			sourceAttrMap.setSourceAttr(attributeMap.getSourceAttr());
//			attrMapList.add(sourceAttrMap);
//		}
		return ConstantsUtil.SELECT.concat(attributeMapOperator.generateSql(ingest.getAttributeMap(), ingest.getSourceDetail(), refKeyMap, otherParams, execParams));
	}
}

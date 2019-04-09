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
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Ingest;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.enums.IngestionType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author Ganesh
 *
 */
@Component
public class IngestOperator {

	@Autowired
	private AttributeMapOperator attributeMapOperator;
	@Autowired
	private FilterOperator2 filterOperator2;
	@Autowired
	private DatasetOperator datasetOperator;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	
	static Logger logger = Logger.getLogger(IngestOperator.class);
	
	public String generateSQL(Ingest ingest, String tableName, String incrColName, String incrLastValue, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode, 
			Map<String, String> paramValMap) throws Exception {
		return generateSelect(ingest, refKeyMap, otherParams, execParams, runMode, paramValMap)
				.concat(getFrom())
				.concat(generateFrom(ingest, refKeyMap, otherParams, usedRefKeySet, execParams, runMode, tableName, paramValMap))
				.concat(generateWhere(ingest, incrColName, incrLastValue))
				.concat(generateFilter(ingest, refKeyMap, otherParams, usedRefKeySet, execParams, runMode, paramValMap))
				.concat(generateGroupBy(ingest, refKeyMap, otherParams, execParams, paramValMap))
				.concat(generateHaving(ingest, refKeyMap, otherParams, usedRefKeySet, execParams, runMode, paramValMap));
		
	}

	public String generateGroupBy(Ingest ingest, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, ExecParams execParams, 
			Map<String, String> paramValMap) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		MetaIdentifierHolder ingestSource = new MetaIdentifierHolder(ingest.getRef(MetaType.ingest));
		String query = attributeMapOperator.selectGroupBy(ingest.getAttributeMap(), refKeyMap, otherParams, execParams, ingestSource, paramValMap);
		logger.info("group by: "+query);
		return query;
	}

	public String generateFilter(Ingest ingest, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode, 
			Map<String, String> paramValMap) throws Exception {
		if (ingest.getFilterInfo() != null && !ingest.getFilterInfo().isEmpty()) {
			MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.ingest, ingest.getUuid(), ingest.getVersion()));

			Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(ingest);
		    return filterOperator2.generateSql(ingest.getFilterInfo(), refKeyMap, filterSource, otherParams, usedRefKeySet, execParams, false, false, runMode, mapSourceDS, paramValMap);

			//return filterOperator.generateSql(ingest.getFilterInfo(), refKeyMap, otherParams, usedRefKeySet, execParams, false, false, runMode);
		}
		return ConstantsUtil.BLANK;
	}
	
	public String generateHaving (Ingest ingest, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode, Map<String, String> paramValMap) throws Exception {
		if (ingest.getFilterInfo() != null && !ingest.getFilterInfo().isEmpty()) {
			MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.ingest, ingest.getUuid(), ingest.getVersion()));

			Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(ingest);
			String filterStr = filterOperator2.generateSql(ingest.getFilterInfo(), refKeyMap, filterSource, otherParams, usedRefKeySet, execParams, true, true, runMode, mapSourceDS, paramValMap);

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

	public String generateFrom(Ingest ingest, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode, String tableName, Map<String, String> paramValMap) throws Exception {
		if(ingest.getSourceDetail().getRef().getType().equals(MetaType.dataset)) {
			DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(ingest.getSourceDetail().getRef().getUuid(), ingest.getSourceDetail().getRef().getVersion(), ingest.getSourceDetail().getRef().getType().toString());
			return "( "+datasetOperator.generateSql(dataset, refKeyMap, otherParams, usedRefKeySet, execParams, runMode, paramValMap)+" ) " + dataset.getName();
		} else {
			return tableName;
		}
	}

	public String getFrom() {
		return  ConstantsUtil.FROM;
	}

	public String generateSelect(Ingest ingest, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, ExecParams execParams, RunMode runMode, 
			Map<String, String> paramValMap) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return ConstantsUtil.SELECT.concat(attributeMapOperator.generateSql(ingest.getAttributeMap(), ingest.getSourceDetail(), refKeyMap, otherParams, execParams, paramValMap));
	}
}

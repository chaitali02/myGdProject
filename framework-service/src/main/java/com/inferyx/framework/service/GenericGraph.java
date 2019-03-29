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
package com.inferyx.framework.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataQualGroup;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Edge;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.domain.Vertex;
import com.inferyx.framework.domain.Vizpod;
@Service
public class GenericGraph {
	List<Row> verticesRowList = new ArrayList<Row>();
	List<Row> edgeRowList = new ArrayList<Row>();
	
	Row edgeRow;
	Row edgeRow1;
	String UUID=null;
	String Version=null;
	String Name=null;
	String dataType=null;
	String desc=null;
	String CreatedOn=null;
	String active=null;
	String objectUuid=null;
	Row vertexRow;
	
	@Autowired FunctionServiceImpl functionServiceImpl;
	@Autowired DatasourceServiceImpl datasourceServiceImpl;
	@Autowired RelationServiceImpl relationServiceImpl;
	@Autowired RuleServiceImpl ruleServiceImpl; 
	@Autowired FilterServiceImpl filterServiceImpl;
	@Autowired RuleGroupServiceImpl ruleGroupServiceImpl;
    @Autowired DataQualServiceImpl dataQualServiceImpl;
    @Autowired DataQualGroupServiceImpl dataqualGroupServiceImpl;
    @Autowired MapServiceImpl mapServiceImpl;
    @Autowired DagServiceImpl dagServiceImpl;
    @Autowired DataStoreServiceImpl datastoreServiceImpl;
    @Autowired DatapodServiceImpl datapodServiceImpl;
    @Autowired ApplicationServiceImpl applicationServiceImpl;
    @Autowired FormulaServiceImpl formulaServiceImpl; 
    @Autowired UserServiceImpl userServiceImpl;
    @Autowired LoadServiceImpl loadServiceImpl;
    @Autowired ExpressionServiceImpl expressionServiceImpl;
    @Autowired DataQualGroupServiceImpl dqGroupServiceImpl;
    @Autowired DatasetServiceImpl datasetServiceImpl;
    @Autowired RoleServiceImpl roleServiceImpl;
    @Autowired GroupServiceImpl groupServiceImpl;
    @Autowired LoadExecServiceImpl loadExecServiceImpl;
    @Autowired ProfileServiceImpl profileServiceImpl;
    @Autowired ProfileGroupServiceImpl profileGroupServiceImpl;
    @Autowired ProfileExecServiceImpl profileExecServiceImpl;
    @Autowired MapExecServiceImpl mapExecServiceImpl;
    @Autowired VizpodServiceImpl vizpodServiceImpl;
    @Autowired AlgorithmServiceImpl algorithmServiceImpl;
    @Autowired ModelServiceImpl modelServiceImpl;
    @Autowired ParamListServiceImpl paramlistServiceImpl;
    @Autowired GraphServiceImpl graphServiceImpl;
    
    static final Logger logger = Logger.getLogger(GenericGraph.class);
   
    
    
    public List<Row> saveEdges(String jsonString, List<Row> verticesRowList, List<Row> edgeRowList, Map<String, Row> verticesRowMap, Map<String, Row> edgeRowMap) throws ParseException, JsonProcessingException, JSONException {
    	JSONObject jsonObject = new JSONObject(jsonString);
    	String uuid = jsonObject.optString("uuid");
    	String version = jsonObject.optString("version");
    	String srcUUid = uuid.concat("_").concat(version);
    	parseJsonObject(srcUUid, jsonObject, verticesRowList, edgeRowList, verticesRowMap, edgeRowMap);
    	return null;
    }
    
    
    
    public List<Row> parseJsonObject(String srcUuid, JSONObject jsonObject, List<Row> verticesRowList, List<Row> edgeRowList, Map<String, Row> verticesRowMap, Map<String, Row> edgeRowMap) throws JSONException {
		Iterator<String> iter = jsonObject.keys();
		JSONArray jsonArray = null;
		JSONObject nestedObj = null;
		String value = null;
		boolean findRefs = false;
    	while (iter.hasNext()) {
    		String key = iter.next();
    		logger.info(key);
    		jsonArray = jsonObject.optJSONArray(key);
    		nestedObj = jsonObject.optJSONObject(key);
    		value = jsonObject.optString(key);
    		if (jsonArray != null) {
    			logger.info(" Array in : " + key);
    			if (key.startsWith("ref")) {
    				findRefs = Boolean.TRUE;
    			}
    			parseJsonArray(srcUuid, jsonArray, findRefs, verticesRowList, edgeRowList, verticesRowMap, edgeRowMap);
    			findRefs = Boolean.FALSE;
    		} else if (nestedObj != null) {
    			logger.info(" Nested object in : " + key);
    			if (key.equals("ref")) {
    				String uuid = nestedObj.getString("uuid");
    				String version = nestedObj.optString("version");
    				String type = nestedObj.getString("type");
    				if ((nestedObj.has("attrId") || nestedObj.has("attributeId")) && nestedObj.getString("type").equals(MetaType.datapod.toString())) {
    					continue;
    				} else {
    					createEdge(srcUuid, uuid, version, key, edgeRowList, edgeRowMap);
    					logger.info("Create edge for : " + type + " : " + uuid + " : " + version);
    				}
    			} else {
    				parseJsonObject(srcUuid, nestedObj, verticesRowList, edgeRowList, verticesRowMap, edgeRowMap);
    			}
    		} else if (value != null) {
    			// System.out.println(" String in : " + key);
    			continue;
    		}
    	}
    	return null;
	}
	
	public List<Row> parseJsonArray(String srcUUid, JSONArray jsonArray, boolean findRefs, List<Row> verticesRowList, List<Row> edgeRowList, Map<String, Row> verticesRowMap, Map<String, Row> edgeRowMap) throws JSONException {
		JSONArray nestedArray = null;
		JSONObject nestedObj = null;
		String value = null;
		if (jsonArray == null) {
			return null;
		}
		for( int i = 0; i < jsonArray.length(); i++) {
			nestedArray = jsonArray.optJSONArray(i);
    		nestedObj = jsonArray.optJSONObject(i);
    		value = jsonArray.optString(i);
    		if (nestedArray != null) {
    			logger.info(" Array in : " + i);
    			parseJsonArray(srcUUid, nestedArray, Boolean.FALSE, verticesRowList, edgeRowList, verticesRowMap, edgeRowMap);
    		} else if (nestedObj != null) {
    			logger.info(" Nested object in : " + i);
    			if (findRefs) {
    				String uuid = nestedObj.getString("uuid");
    				String version = nestedObj.optString("version");
    				String type = nestedObj.getString("type");
    				logger.info("Create edge for : " + type + " : " + uuid + " : " + version);
    			}
    			parseJsonObject(srcUUid, nestedObj, verticesRowList, edgeRowList, verticesRowMap, edgeRowMap);
    		} else if (value != null) {
    			// System.out.println(" String in : " + key);
    			continue;
    		}
		}
		return null;
	}

	
	public List<Row> createVertex(Map<String, Object> document,String type, List<Row> verticesRowList, Map<String, Row> verticesRowMap) {
		
		UUID=document.get("uuid").toString().concat("_").concat(document.get("version").toString());
		Version=document.get("version").toString();
		Name=document.get("name").toString();
		CreatedOn=document.get("createdOn").toString();
		active=document.get("active").toString();
		vertexRow = RowFactory.create(UUID,Version,Name,type, null, null, CreatedOn, active);
		
		verticesRowList.add(vertexRow);
		verticesRowMap.put(UUID.concat("_").concat(Version).concat("_").concat(Name).concat("_").concat(type).concat("_").concat(active), vertexRow);
		
		return verticesRowList;
	}

	public List<Row> saveVertex(Map<String, Object> document,String type, List<Row> verticesRowList) {
		
		UUID=document.get("uuid").toString().concat("_").concat(document.get("version").toString());
		Version=document.get("version").toString();
		Name=document.get("name").toString();
		CreatedOn=document.get("createdOn").toString();
		active=document.get("active").toString();		
		vertexRow = RowFactory.create(UUID,Version,Name,type, null, null, CreatedOn, active);
		
		Vertex vertex = new Vertex(UUID, Version, Name, type, null, null, CreatedOn, active, null,null);
		graphServiceImpl.saveVertex(vertex);
		
		verticesRowList.add(vertexRow);
		
		return verticesRowList;
	}
	
	public Row createVertex(String uuid, String version, String name, String nodeType, String createdOn, String active) {
		return RowFactory.create(uuid,version,name,nodeType, null, null, createdOn, active);
	}
	
	public List<Row> createEdge(String srcUuid, String dstUuid, String dstVersion, String name, List<Row> edgeRowList, Map<String, Row> edgeRowMap) {
		return createEdge(srcUuid, dstUuid.concat("_").concat(dstVersion), name, edgeRowList, edgeRowMap);
	}
	
	public List<Row> createEdge(String srcUuid, String dst, String name, List<Row> edgeRowList, Map<String, Row> edgeRowMap) {
		edgeRow = RowFactory.create(srcUuid, dst, name);
		edgeRowMap.put(srcUuid+"_"+dst+"_"+name, edgeRow);
		//Edge edge = new Edge(srcUuid, dst, name);
		edgeRowList.add(edgeRow);
		return edgeRowList;
	}
	
	public List<Row> createAndSaveEdge(String srcUuid, String dstUuid, String dstVersion, String name) {
		return createAndSaveEdge(srcUuid, dstUuid.concat("_").concat(dstVersion), name);
	}

	public List<Row> createAndSaveEdge(String srcUuid, String dst, String name) {
		edgeRow = RowFactory.create(srcUuid, dst, name);
		Edge edge = new Edge(srcUuid, dst, name, null, null);
		graphServiceImpl.saveEdge(edge);
		edgeRowList.add(edgeRow);
		return edgeRowList;
	}


}

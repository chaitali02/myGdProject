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
    		System.out.println(key);
    		jsonArray = jsonObject.optJSONArray(key);
    		nestedObj = jsonObject.optJSONObject(key);
    		value = jsonObject.optString(key);
    		if (jsonArray != null) {
    			System.out.println(" Array in : " + key);
    			if (key.startsWith("ref")) {
    				findRefs = Boolean.TRUE;
    			}
    			parseJsonArray(srcUuid, jsonArray, findRefs, verticesRowList, edgeRowList, verticesRowMap, edgeRowMap);
    			findRefs = Boolean.FALSE;
    		} else if (nestedObj != null) {
    			System.out.println(" Nested object in : " + key);
    			if (key.equals("ref")) {
    				String uuid = nestedObj.getString("uuid");
    				String version = nestedObj.optString("version");
    				String type = nestedObj.getString("type");
    				if ((nestedObj.has("attrId") || nestedObj.has("attributeId")) && nestedObj.getString("type").equals(MetaType.datapod.toString())) {
    					continue;
    				} else {
    					createEdge(srcUuid, uuid, version, key, edgeRowList, edgeRowMap);
    					System.out.println("Create edge for : " + type + " : " + uuid + " : " + version);
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
    			System.out.println(" Array in : " + i);
    			parseJsonArray(srcUUid, nestedArray, Boolean.FALSE, verticesRowList, edgeRowList, verticesRowMap, edgeRowMap);
    		} else if (nestedObj != null) {
    			System.out.println(" Nested object in : " + i);
    			if (findRefs) {
    				String uuid = nestedObj.getString("uuid");
    				String version = nestedObj.optString("version");
    				String type = nestedObj.getString("type");
    				System.out.println("Create edge for : " + type + " : " + uuid + " : " + version);
    			}
    			parseJsonObject(srcUUid, nestedObj, verticesRowList, edgeRowList, verticesRowMap, edgeRowMap);
    		} else if (value != null) {
    			// System.out.println(" String in : " + key);
    			continue;
    		}
		}
		return null;
	}

	/********************** UNUSED **********************/
	/*public List<Row> saveEdges(Map<String, Object> document, String type, List<Row> verticesRowList, List<Row> edgeRowList, Map<String, Row> verticesRowMap, Map<String, Row> edgeRowMap) throws ParseException, JsonProcessingException, JSONException {
		UUID = document.get("uuid").toString().concat("_").concat(document.get("version").toString());
		objectUuid = document.get("uuid").toString();
		Version = document.get("version").toString();
		Name = document.get("name").toString();
		CreatedOn = document.get("createdOn").toString();
		BaseEntity entity = null;

		ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Set<String> keys = document.keySet();
		// System.out.println(keys+"*****");
		List<String> listUuid = new ArrayList<String>();
		List<String> attribute = new ArrayList<String>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<Row> m = new ArrayList<Row>();
		Iterator iter = keys.iterator();
		while (iter.hasNext()) { // traverse document
			String keyValue = (String) iter.next();
			Object aObj = document.get(keyValue);
			// String valueString = document.get(keyValue).toString();

			String stringJson = writer.writeValueAsString(aObj);
			if (stringJson.equals("null") || aObj.toString().equals("")) {
				continue;
			}
			// System.out.println(keyValue+":"+aObj.toString());

			if (aObj.toString().charAt(0) == '[') {
				ArrayList<Object> list = (ArrayList<Object>) document.get(keyValue);
				parseArrayJson(document, list, type, verticesRowList, edgeRowList, verticesRowMap, edgeRowMap, keyValue, writer);
			} else if (aObj.toString().charAt(0) == '{') {
				String replaceString = aObj.toString().replaceAll("/", "");
				JSONObject object = new JSONObject(replaceString);

				String jsonObjectString = object.toString();
				if (jsonObjectString.charAt(1) != '}') {
					String ref = jsonObjectString.substring(2, 5);

					if (!ref.equals("ref") || (jsonObjectString.length() >= 13 && jsonObjectString.substring(2,12).equalsIgnoreCase("refKeyList"))) {
						Iterator itr = object.keys();
						while (itr.hasNext()) {
							String key = itr.next().toString();
							if (key.equals("uuid")) {
								Datapod datapod = datapodServiceImpl.findLatestByUuid(object.getString("uuid"));
								if (datapod != null) {
									createEdge(UUID, datapod.getUuid(), datapod.getVersion(), keyValue, edgeRowList, edgeRowMap);
								}
							} else if (object.getString(key).toString().charAt(0) == '[') {
								JSONArray jsonArray = object.getJSONArray(key);
								ArrayList<Object> objectList = null;
								ObjectMapper mapper = new ObjectMapper();
								try {
									objectList = mapper.readValue(jsonArray.toString(), new TypeReference<ArrayList<Object>>(){});
								} catch (IOException e) {
									e.printStackTrace();
								}
								parseArrayJson(document, objectList, type, verticesRowList, edgeRowList, verticesRowMap, edgeRowMap, keyValue, writer);
							}
						}

					} else {
						Iterator refiter = object.keys();
						Datapod datapod = null;
						while (refiter.hasNext()) {
							String refKeyValue = (String) refiter.next();
							Object aObj1 = object.get(refKeyValue);

							if (!aObj1.equals(null) && refKeyValue.equals("attrId")) {
								JSONObject aOb = (JSONObject) object.get("ref");
								String uuid = aOb.getString("uuid");
								map.put(uuid, (Integer) aObj1);

							}
							if (aObj1 instanceof JSONObject) {
								Iterator itr = ((JSONObject) aObj1).keys();
								if (!aObj1.equals(null) && object.has("attrId") && !refKeyValue.equals("attrId")
										&& !refKeyValue.equals("value")
										&& ((JSONObject) aObj1).getString("type").equals(MetaType.datapod.toString())) {
									String refuuid = ((JSONObject) aObj1).getString("uuid");
									String version = ((JSONObject) aObj1).getString("version");
									Object objattrId = object.get("attrId");
									datapod = datapodServiceImpl.findLatestByUuid(refuuid);
									if (datapod != null) {
										String attrId = datapod.getUuid().toString().concat("_").concat(objattrId
												.toString().concat("_").concat(datapod.getVersion().toString()));
										listUuid.add(attrId);
									}
								}
								listUuid.add(((JSONObject) aObj1).getString("uuid").toString());

								while (itr.hasNext()) {
									String keytype = itr.next().toString();
									Object obj = ((JSONObject) aObj1).get(keytype);

									if (!aObj1.equals(null) && obj.equals(MetaType.simple.toString())) {

										Row targetRow = RowFactory.create(
												keyValue.concat("_").concat(object.get("value").toString()), null,
												object.get("value").toString(), "Value", object.get("value").toString(),
												null, "simple,value", null);
										verticesRowList.add(targetRow);
										verticesRowMap.put(keyValue.concat("_").concat(object.get("value").toString())
												.concat("_").concat("").concat("_")
												.concat(object.get("value").toString())
												.concat("_").concat("Value")
												.concat("_").concat(""), targetRow);
										createEdge(UUID, keyValue.concat("_").concat(object.get("value").toString()), keyValue, edgeRowList, edgeRowMap);
									}
								}
							}

							if (!aObj1.equals(null) && !refKeyValue.equals("attrId") && !refKeyValue.equals("value") && !refKeyValue.equals("attributeId")) {
								String refuuid = ((JSONObject) aObj1).getString("uuid");
								String version = ((JSONObject) aObj1).getString("version");
								String refType = ((JSONObject) aObj1).getString("type");
								
								entity = getEntity(refuuid, version, refType);
								if (entity != null) {
									createEdge(UUID, entity.getUuid(), entity.getVersion(), keyValue, edgeRowList, edgeRowMap);
								}

							}
							if (!aObj1.equals(null) && object.has("attrId") && !refKeyValue.equals("attrId") 
									&& !refKeyValue.equals("attributeId")
									&& !refKeyValue.equals("value")
									&& ((JSONObject) aObj1).getString("type").equals(MetaType.datapod.toString())) {

								String refuuid = ((JSONObject) aObj1).getString("uuid");
								String version = ((JSONObject) aObj1).getString("version");
								Object objattrId = object.get("attrId");
								datapod = datapodServiceImpl.findLatestByUuid(refuuid);
								String attrId = datapod.getUuid().toString().concat("_").concat(
										objattrId.toString().concat("_").concat(datapod.getVersion().toString()));

								int count = Collections.frequency(listUuid, attrId);
								if (count == 1) {
									createEdge(UUID, attrId, keyValue, edgeRowList, edgeRowMap);
								}
							}
						}
					}
				}
			}

		} // End while iter

		return edgeRowList;
	}
	*/

	/********************** UNUSED **********************/
	/*private void parseArrayJson(Map<String, Object> document, 
										ArrayList<Object> list,
										String type, 
										List<Row> verticesRowList, 
										List<Row> edgeRowList, 
										Map<String, Row> verticesRowMap, 
										Map<String, Row> edgeRowMap, 
										String keyValue, 
										ObjectWriter writer) throws ParseException, JsonProcessingException, JSONException {
		BaseEntity entity = null;
		//ArrayList<Object> list = (ArrayList<Object>) document.get(keyValue);
		for (int j = 0; j < list.size(); j++) {
			String json = writer.writeValueAsString(list.get(j));

			if (json.charAt(0) == '{' && !keyValue.equals("status")) {
				// creating node and edge for array
				JSONObject object = new JSONObject(json);
				JSONObject object1 = new JSONObject(object.toString());
				String jsonObjectString = object1.toString();
				String ref = jsonObjectString.substring(2, 5);
				String uuid = null;
				if (!ref.equals("ref")) {
					uuid = document.get("uuid").toString().concat("_").concat(keyValue).concat("_")
							.concat(Integer.toString(j)).concat("_").concat(document.get("version").toString());
					Row targetRow1 = RowFactory.create(uuid, document.get("version").toString(),
							keyValue.concat("_").concat(Integer.toString(j)), keyValue, keyValue, null,
							document.get("createdOn").toString(), document.get("active").toString());
					if (!type.equals(MetaType.datapod.toString())) {
						verticesRowList.add(targetRow1);
						verticesRowMap.put(uuid.concat("_").concat(document.get("version").toString()).concat("_").concat(keyValue.concat("_").concat(Integer.toString(j)))
								.concat("_").concat(keyValue).concat("_").concat(document.get("active").toString()), targetRow1);
						createEdge(UUID, uuid, keyValue, edgeRowList, edgeRowMap);
					}
				}

				if (jsonObjectString.charAt(1) != '}') {

					// check getting object is ref or not
					if (!ref.equals("ref")) {
						Iterator itr = object.keys();

						while (itr.hasNext()) {
							String key = itr.next().toString();
							Object obj2 = object.get(key);
							if (key.equals("uuid")) {
								Datapod datapod = datapodServiceImpl.findLatestByUuid(object.getString("uuid"));
								if (datapod != null) {
									createEdge(UUID, datapod.getUuid(), datapod.getVersion(), keyValue, edgeRowList, edgeRowMap);
								}
							} else if (key.equals("attributeId")) {
								String attrId = document.get("uuid").toString().concat("_")
										.concat(object.getString("attributeId").concat("_")
												.concat(document.get("version").toString()));
								Row targetRow = RowFactory.create(attrId, document.get("version").toString(),
										object.getString("name").toString(), "Attribute",
										object.getString("type").toString(),
										object.getString("desc").toString(),
										document.get("createdOn").toString(),
										document.get("active").toString());
								verticesRowList.add(targetRow);
								verticesRowMap.put(attrId.concat("_").concat(document.get("version").toString()).concat("_")
										.concat(object.getString("name").toString())
										.concat("_").concat(object.getString("type").toString())
										.concat("_").concat(document.get("active").toString()), targetRow);
								createEdge(UUID, attrId, "attribute", edgeRowList, edgeRowMap);
							} else if (key.equals("paramId")) {
								String attrId = document.get("uuid").toString().concat("_")
										.concat(object.getString("paramId").concat("_")
												.concat(document.get("version").toString()));
								try {
									Row targetRow = RowFactory.create(attrId,
											document.get("version").toString(),
											object.getString("paramName").toString(), "Param",
											object.getString("paramType").toString(),
											(object.has("paramValue") ? object.getString("paramValue").toString() : null),
											document.get("createdOn").toString(),
											document.get("active").toString());
									verticesRowList.add(targetRow);
									verticesRowMap.put(attrId.concat("_").concat(document.get("version").toString()).concat("_")
											.concat(object.getString("paramName").toString())
											.concat("_").concat("Param")
											.concat("_").concat(document.get("active").toString()), targetRow);
								} catch (Exception e) {
									e.printStackTrace();
								}
								createEdge(UUID, attrId, key, edgeRowList, edgeRowMap);
							}
							if (!obj2.equals("") && !obj2.equals(null)) {
								// checking inner object is JSONObject
								// or JSONArray
								ifObject(uuid, obj2.toString(), j, key, object, edgeRowList, edgeRowMap);
								ifArray(uuid, obj2.toString(), j, key, object, edgeRowList, edgeRowMap);
							}
						}

					} else {
						Iterator refiter = object.keys();
						Datapod datapod = null;
						while (refiter.hasNext()) {
							String refKeyValue = (String) refiter.next();
							Object aObj1 = object.get(refKeyValue);

							if (!aObj1.equals(null) && refKeyValue.equals("attrId")
									|| refKeyValue.equals("attributeId") && datapod != null) {
								if (datapod != null) {
									String attrId = datapod.getUuid().toString().concat("_").concat(aObj1
											.toString().concat("_").concat(datapod.getVersion().toString()));
									createEdge(UUID, attrId, keyValue, edgeRowList, edgeRowMap);
								}
							}

							if (aObj1 instanceof JSONObject) {
								Iterator itr = ((JSONObject) aObj1).keys();
								while (itr.hasNext()) {
									String keytype = itr.next().toString();
									Object obj = ((JSONObject) aObj1).get(keytype);

									if (!aObj1.equals(null) && obj.equals(MetaType.simple.toString())) {

										Row targetRow = RowFactory.create(
												keyValue.concat("_").concat(object.get("value").toString()),
												null, object.get("value").toString(), "Value",
												object.get("value").toString(), null, "simple,value", null);
										verticesRowList.add(targetRow);
										verticesRowMap.put(keyValue.concat("_").concat(object.get("value").toString())
												.concat("_").concat("").concat("_")
												.concat(object.get("value").toString())
												.concat("_").concat("Value")
												.concat("_").concat(""), targetRow);
										createEdge(UUID, keyValue.concat("_").concat(object.get("value").toString()), keyValue, edgeRowList, edgeRowMap);
									}
								}
							}
							if (!aObj1.equals(null) && !refKeyValue.equals("attrId")
									&& !refKeyValue.equals("value") && !refKeyValue.equals("attributeId")
									&& aObj1 instanceof JSONObject) {
								JSONObject jsonObject = new JSONObject(aObj1.toString());
								String refuuid = jsonObject.getString("uuid");
								String refversion = jsonObject.getString("version");
								String refType = jsonObject.getString("type");

								entity = getEntity(refuuid, refversion, refType);
								if (entity != null) {
									createEdge(UUID, entity.getUuid(), entity.getVersion(), keyValue, edgeRowList, edgeRowMap);
								}
							}
						}
					}

				}
			}
		} // End for
	
	}
	*/

	/********************** UNUSED **********************/
	/*public BaseEntity getEntity (String refuuid, String version, String type) {
		if (StringUtils.isBlank(type) || StringUtils.isBlank(refuuid)) {
			return null;
		}
		if (type.equals(MetaType.user.toString())) {
			if (StringUtils.isBlank(version) || version == "null") {
				return userServiceImpl.findLatestByUuid(refuuid);
			} else {
				return userServiceImpl.findOneByUuidAndVersion(refuuid, version);
			}
		} else if (type.equals(MetaType.datasource.toString())) {
			if (StringUtils.isBlank(version) || version == "null") {
				return datasourceServiceImpl.findLatestByUuid(refuuid);
			} else {
				return datasourceServiceImpl.findOneByUuidAndVersion(refuuid, version);
			}
		} else if (type.equals(MetaType.datapod.toString())) {
			if (StringUtils.isBlank(version) || version == "null") {
				return datapodServiceImpl.findLatestByUuid(refuuid);
			} else {
				return datapodServiceImpl.findOneByUuidAndVersion(refuuid, version);
			}
		} else if (type.equals(MetaType.relation.toString())) {
			if (StringUtils.isBlank(version) || version == "null") {
				return relationServiceImpl.findLatestByUuid(refuuid);
			} else {
				return relationServiceImpl.findOneByUuidAndVersion(refuuid, version);
			}
		} else if (type.equals(MetaType.formula.toString())) {
			if (StringUtils.isBlank(version) || version == "null") {
				return formulaServiceImpl.findLatestByUuid(refuuid);
			} else {
				return formulaServiceImpl.findOneByUuidAndVersion(refuuid, version);
			}
		} else if (type.equals(MetaType.rule.toString()) || version == "null") {
			if (StringUtils.isBlank(version)) {
				return ruleServiceImpl.findLatestByUuid(refuuid);
			} else {
				return ruleServiceImpl.findOneByUuidAndVersion(refuuid, version);
			}
		} else if (type.equals(MetaType.dataset.toString()) || version == "null") {
			if (StringUtils.isBlank(version)) {
				return datasetServiceImpl.findLatestByUuid(refuuid);
			} else {
				return datasetServiceImpl.findOneByUuidAndVersion(refuuid, version);
			}
		} else if (type.equals(MetaType.load.toString())) {
			if (StringUtils.isBlank(version) || version == "null") {
				return loadServiceImpl.findLatestByUuid(refuuid);
			} else {
				return loadServiceImpl.findOneByUuidAndVersion(refuuid, version);
			}
		} else if (type.equals("loadExec")) {
			if (StringUtils.isBlank(version) || version == "null") {
				return loadExecServiceImpl.findLatestByUuid(refuuid);
			} else {
				return loadExecServiceImpl.findOneByUuidAndVersion(refuuid, version);
			}
		} else if (type.equals(MetaType.profileExec.toString()) || version == "null") {
			if (StringUtils.isBlank(version)) {
				return profileExecServiceImpl.findLatestByUuid(refuuid);
			} else {
				return profileExecServiceImpl.findOneByUuidAndVersion(refuuid, version);
			}
		} else if (type.equals(MetaType.mapExec.toString())) {
			if (StringUtils.isBlank(version) || version == "null") {
				return mapExecServiceImpl.findLatestByUuid(refuuid);
			} else {
				return mapExecServiceImpl.findOneByUuidAndVersion(refuuid, version);
			}
		} 
		return null;
	}
	*/
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
		
		Vertex vertex = new Vertex(UUID, Version, Name, type, null, null, CreatedOn, active);
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
		Edge edge = new Edge(srcUuid, dst, name);
		graphServiceImpl.saveEdge(edge);
		edgeRowList.add(edgeRow);
		return edgeRowList;
	}

	/********************** UNUSED **********************/
/*public void ifArray(String uuid,String aObj,int i, String keyValue,JSONObject document, List<Row> edgeRowList, Map<String, Row> edgeRowMap) throws JsonProcessingException, JSONException
{
	BaseEntity entity = null;
	ObjectWriter writer1 = new ObjectMapper().writer().withDefaultPrettyPrinter();
	 if(aObj.toString().charAt(0)=='[')
	    {
	       //	ArrayList<Object> list1=(ArrayList<Object>) document.get(keyValue);
		
		 JSONArray jsonMainArr=document.getJSONArray(keyValue);
		  for (int j = 0; j < jsonMainArr.length(); j++) {
			  Object Obj=jsonMainArr.get(j);
			  if(Obj.toString().charAt(0)=='{'){
	    		JSONObject childJSONObject = jsonMainArr.getJSONObject(j);
	    		 Row row = RowFactory.create(objectUuid.concat("_").concat(keyValue).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version), Version,
	    				 keyValue.concat("_").concat(Integer.toString(j)),  keyValue,
	    				 keyValue, null,
	    				  CreatedOn, null);
	    		    verticesRowList.add(row);
	    		
	    		
	    			edgeRow = RowFactory.create(uuid, objectUuid.concat("_").concat(keyValue).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version), keyValue);
	    			edgeRowList.add(edgeRow);
				String json=childJSONObject.toString();
				
		         
		    if(json.charAt(0)=='{') {
		    	JSONObject object=new JSONObject(json);
		    	JSONObject object1=new JSONObject(object.toString());
		    	
		    String jsonObjectString=object1.toString();
		    if(jsonObjectString.charAt(1)!='}'){
		    String ref=jsonObjectString.substring(2, 5);		
		    if(!ref.equals("ref"))
		    {
		    	Iterator itr=object.keys();
		    	while(itr.hasNext())
		    	{
		    	String key=itr.next().toString();
		    	Object aObj1 = object.get(key);
		    	if(aObj1 instanceof JSONArray)
		    	{
		    		 JSONArray jsonArr=object.getJSONArray(key);
		    		 if(jsonArr.length()>0){
		    			 if(jsonArr.get(0) instanceof JSONObject){
		    		
		    		 Row targetRow = RowFactory.create(objectUuid.concat("_").concat(key).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version), Version,
		    				 key , key,
		    				  key, null,
		    				  CreatedOn, null);
					    verticesRowList.add(targetRow);
		    		
		    		
						edgeRow = RowFactory.create(objectUuid.concat("_").concat(keyValue).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version), objectUuid.concat("_").concat(key).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version), key);
						edgeRowList.add(edgeRow);
   				  //childJSONObject.get("operator").toString();
		    		 for (int l = 0; l < jsonArr.length(); l++) {
		    	
		    		  Row targetRow = RowFactory.create(ObjectUuid.concat("_").concat(key).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Integer.toString(l)).concat("_").concat(Version), Version,
		    				  key , key,
		    				  key, null,
		    				  CreatedOn, null);
					    verticesRowList.add(targetRow);
		    		
		    		
						edgeRow = RowFactory.create(ObjectUuid.concat("_").concat(keyValue).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version), ObjectUuid.concat("_").concat(key).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Integer.toString(l)).concat("_").concat(Version), key.concat("_").concat(Integer.toString(l)));
						edgeRowList.add(edgeRow);
		    		
		    		 JSONArray jsonChildArr=childJSONObject.getJSONArray(key);
		    			for (int k = 0; k < jsonChildArr.length(); k++) {
		    				Object obj=jsonChildArr.get(k);
		    				if(obj instanceof JSONObject){
		    	    		JSONObject subChildJSONObject = jsonChildArr.getJSONObject(k);
		    	    		Iterator itr1=subChildJSONObject.keys();
		    	    		while(itr1.hasNext())
		    	    		{ 
		    	    			String subKey=itr1.next().toString();
		    	    			Object subObject=subChildJSONObject.get(subKey);
		    	    			if(subObject instanceof JSONObject)
		    	    			{
		    	    				Iterator itr2=((JSONObject) subObject).keys();
		    	    				
		    	    				while(itr2.hasNext())
		    	    				{
		    	    					String Key=itr2.next().toString();
		    	    					Object childObject=((JSONObject) subObject).get(Key);
		    	    					if(!childObject.equals(null)&&childObject.equals(MetaType.datapod.toString()))
		    	    					{
		    	    						Datapod datapod =datapodServiceImpl.findLatestByUuid(((JSONObject) subObject).getString("uuid"));
		    	    						if(datapod!=null)
		    	    						{
		    	    							 String attrId =datapod.getUuid().concat("_").concat(
		    	    									 ((JSONObject) subChildJSONObject).get("attributeId").toString()).concat("_").concat(datapod.getVersion());
		    	    				    		
		    	    				    		  Row targetRow1 = RowFactory.create(attrId, datapod.getVersion(),
		    	    				    				  datapod.getName(), "Attribute",
		    	    				    				"Attribute", datapod.getDesc(),
		    	    				    				  datapod.getCreatedOn(), datapod.getActive());
		    	    							verticesRowList.add(targetRow1);
		    	    				    		
		    	    				    		
		    	    								edgeRow = RowFactory.create(objectUuid.concat("_").concat(key).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version), attrId, "attribute");
		    	    								edgeRowList.add(edgeRow);
		    	    						}
		    	    					}
		    	    					if(!childObject.equals(null)&&childObject.toString().charAt(0)=='{')
		    	    					{
		    	    						String Uuid=((JSONObject) childObject).getString("uuid");
		    	    						
		    	    						Load load=loadServiceImpl.findLatestByUuid(Uuid);
		    	    						com.inferyx.framework.domain.Map map=mapServiceImpl.findLatestByUuid(Uuid);
		    	    						DataQualGroup dqgroup=dqGroupServiceImpl.findLatestByUuid(Uuid);
		    	    						if(load!=null){
		    	    							edgeRow = RowFactory.create(objectUuid.concat("_").concat(key).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version), load.getUuid().concat("_").concat(load.getVersion()), key);
	    	    								edgeRowList.add(edgeRow);
		    	    						}
		    	    						if(map!=null){
			    	    				     
		    	    							edgeRow = RowFactory.create(objectUuid.concat("_").concat(key).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version), map.getUuid().concat("_").concat(map.getVersion()), key);
	    	    								edgeRowList.add(edgeRow);
			    	    					}
		    	    						if(dqgroup!=null)
		    	    						{
		    	    							edgeRow = RowFactory.create(objectUuid.concat("_").concat(key).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version), dqgroup.getUuid().concat("_").concat(dqgroup.getVersion()), key);
	    	    								edgeRowList.add(edgeRow);
		    	    						}
		    	    					}
		    	    					
		    	    				}
		    	    			}
		    	    		}
		    	    		
		    	    		
		    				String json1=subChildJSONObject.toString();
		    			//	System.out.println(json1);
		    			}
		    			}
		    	}
		    	}
		    	}
		    	}
		    	
		    	if(aObj1 instanceof JSONObject)
		    	{
		    		if(aObj1.toString().charAt(0)!='}'&&!aObj1.equals(null)){
		    		Iterator itr2=((JSONObject) aObj1).keys();
		    		while(itr2.hasNext())
		    		{
		    			String dagKey=itr2.next().toString();
		    			Object dagObj=((JSONObject) aObj1).get(dagKey);
		    			if(dagObj instanceof JSONObject)
				    	{
		    			if(!dagObj.equals(null))
		    			{
		    				if(((JSONObject) dagObj).getString("type").equals(MetaType.dag.toString()))
		    	   			{
		    	   				String dagUuid=((JSONObject) dagObj).getString("uuid");
		    	   				String dagVersion=((JSONObject) dagObj).getString("version");
		    	   				Dag dag=dagServiceImpl.findOneByUuidAndVersion(dagUuid,dagVersion)
		    	   			 if(dag!=null)
		    	    		 {
		    	    			 createEdge(UUID,dag.getUuid(),dag.getVersion(),dagKey, edgeRowList, edgeRowMap);
		    	    		 }
		    	   			}
		    			}
		    		}
		    	}
		    	}
		    	}
		    	
		    	
		    	
		    	if(key.equals("uuid"))
		    	{
            Datapod datapod=datapodServiceImpl.findLatestByUuid(object.getString("uuid"));
            if(datapod!=null)
            {
            	
         	   createEdge(UUID,datapod.getUuid(),datapod.getVersion(),keyValue, edgeRowList, edgeRowMap);
            }
		    	}
		    	if(key.equals("attributeId"))
		    	{
		    		  String attrId =document.get("uuid").toString().concat("_").concat(
								object.getString("attributeId").concat("_").concat(document.get("version").toString()));
		    		
		    		  Row targetRow = RowFactory.create(attrId, document.get("version").toString(),
		    				  object.getString("name").toString(), "Attribute",
		    				  object.getString("type").toString(), object.getString("desc").toString(),
		    				  document.get("createdOn").toString(), document.get("active").toString());
					verticesRowList.add(targetRow);
		    		
		    		
						edgeRow = RowFactory.create(uuid, attrId, "attribute");
						edgeRowList.add(edgeRow);
		    	}
		    	}
		    	
		    }
		    else {
		    	Iterator refiter = 	object.keys();
		    	Datapod datapod=null;
	    		 Formula formula=null;
	    		 Dataset dataset=null;
		    	while (refiter.hasNext()) {
		    		String refKeyValue = (String)refiter.next();
		    		 Object aObj1 = object.get(refKeyValue);
		    		 
		    		 
		    		 
		    		 if(!aObj1.equals(null)&&refKeyValue.equals("attrId")||refKeyValue.equals("attributeId")&&datapod!=null)
		    		 {
		    			 if(datapod!=null)
		    			 {
		    			    String attrId = datapod.getUuid().toString().concat("_").concat(
		    					 aObj1.toString().concat("_").concat(datapod.getVersion().toString()));
		    				edgeRow = RowFactory.create(objectUuid.concat("_").concat(keyValue).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version), attrId, "attribute");
		    				edgeRowList.add(edgeRow);
		    			 }
		    		 }
		    		 if(object.has("attributeId")&&!aObj1.equals(null)&&refKeyValue.equals("attrId")||refKeyValue.equals("attributeId"))
		    		 {
		    			 if(dataset!=null)
		    			 {
		    				 String a= object.get("attributeId").toString();
	    					 int attrId = Integer.parseInt(a);
	    					 AttributeSource attrSource=dataset.getAttributeInfo().get(attrId);
	    					 AttributeRefHolder attrRefHolder=attrSource.getSourceAttr();
	    					 if(attrRefHolder.getRef().getType().equals(MetaType.function))
	    					 {
	    						 Function function=functionServiceImpl.findLatestByUuid(attrRefHolder.getRef().getUuid());
	    						 createEdge(objectUuid.concat("_").concat(keyValue).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version),function.getUuid(),function.getVersion(),keyValue, edgeRowList, edgeRowMap);
	    					 }
	    					 else
	    						 if(attrRefHolder.getRef().getType().equals(MetaType.formula))
		    					 {
	    							 formula=formulaServiceImpl.findLatestByUuid(attrRefHolder.getRef().getUuid());
		    						 createEdge(objectUuid.concat("_").concat(keyValue).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version),formula.getUuid(),formula.getVersion(),keyValue, edgeRowList, edgeRowMap);
		    					 }else{
	    					 datapod=datapodServiceImpl.findLatestByUuid(attrRefHolder.getRef().getUuid());
	    					 if(datapod!=null){
	    					 String attributeId=datapod.getUuid().concat("_").concat(attrRefHolder.getAttrId()).concat("_").concat(datapod.getVersion());
	    					 edgeRow = RowFactory.create(objectUuid.concat("_").concat(keyValue).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version), attributeId, keyValue);
	 	    				edgeRowList.add(edgeRow);
	    					 }
	    					 }
	    				 }
		    		 }
//		    		 System.out.println(" aObj1 : " + aObj1.toString() + " : refKeyValue : " + refKeyValue);
		    		 if(!aObj1.equals(null)&&!refKeyValue.equals("attrName")&&!refKeyValue.equals("attributeName")&&!refKeyValue.equals("attrId")&&!refKeyValue.equals("attributeId")&&!refKeyValue.equals("value")&&!((JSONObject) aObj1).getString("type").equals(MetaType.simple.toString()))
		    		 {
		    		 JSONObject jsonObject=new JSONObject(aObj1.toString());
		    		 String refuuid=jsonObject.getString("uuid");
		    		 String refversion=jsonObject.getString("version");
		    		 String refType=jsonObject.getString("type");
					entity = getEntity(refuuid, refversion, refType);
					if (entity != null) {
						createEdge(UUID, entity.getUuid(), entity.getVersion(), keyValue, edgeRowList, edgeRowMap);
					}
		    	   }
		    		 
		    		 if(!aObj1.equals(null)&&refKeyValue.equals("value"))
		    		 {
		    			 String value=aObj1.toString().concat("_").concat(Integer.toString(j));
		    			 Row targetRow = RowFactory.create(value,null,
				    			 value, "Value",
				    			 keyValue , null,
			    				  "simple,value",null);
						verticesRowList.add(targetRow);
			    		edgeRow = RowFactory.create(objectUuid.concat("_").concat(keyValue).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version), value, "Value");
							edgeRowList.add(edgeRow);
							edgeRowMap.put(objectUuid.concat("_").concat(keyValue).concat("_").concat(Integer.toString(i)).concat("_").concat(Integer.toString(j)).concat("_").concat(Version).concat("_").concat(value).concat("_").concat("Value"), edgeRow);
		    			
		    		 }
		    		 
		    		 
		    	}
		    }
		   }
		 }
	    }
		 }
	    }
}
*/

	/********************** UNUSED **********************/
/*public void ifObject(String uuid,String aObj,int j, String keyValue,JSONObject object1, List<Row> edgeRowList, Map<String, Row> edgeRowMap) throws JsonProcessingException, JSONException
{
	BaseEntity entity = null;
	 if(aObj.toString().charAt(0)=='{')
	    {
		
	    JSONObject object=new JSONObject(aObj.toString());
	    	
	    String jsonObjectString=object.toString();
	    if(jsonObjectString.charAt(1)!='}'){
	    String ref=jsonObjectString.substring(2, 5);
	  
	
	    if(!ref.equals("ref"))
	    {
	    	Iterator itr=object.keys();
	    	while(itr.hasNext())
	    	{
	    	String key=itr.next().toString();
	    
   		      Object aObj2 = object.get(key);
   		   if(aObj2 instanceof JSONObject)
  		    {
   			if(((JSONObject) aObj2).getString("type").equals(MetaType.dag.toString()))
   			{
   				String dagUuid=((JSONObject) aObj2).getString("uuid");
   				String dagVersion=((JSONObject) aObj2).getString("version");
   				Dag dag=dagServiceImpl.findOneByUuidAndVersion(dagUuid,dagVersion);
   			 if(dag!=null)
    		 {
    			 createEdge(UUID,dag.getUuid(),dag.getVersion(),key, edgeRowList, edgeRowMap);
    		 }
   			}
  		     }
	    	if(key.equals("uuid"))
	    	{
        Datapod datapod=datapodServiceImpl.findLatestByUuid(object.getString("uuid"));
        if(datapod!=null)
        {
     	   createEdge(UUID,datapod.getUuid(),datapod.getVersion(),keyValue, edgeRowList, edgeRowMap);
        }
	    	}
	    	}
	    	
	    }
	    else {
	    	Iterator refiter = 	object.keys();
	    	Datapod datapod=null;
 		    User user=null;
 		    Rule rule=null;
 		 Datasource datasource=null;
 		 DataQual dq=null;
 		 Function function=null;
 		 Expression expr=null;
 		 Formula formula=null;
 		 Dataset dataset=null;
 		 Vizpod vizpod=null;
	    	while (refiter.hasNext()) {
	    		String refKeyValue = (String)refiter.next();
	    		 Object aObj1 = object.get(refKeyValue);
	    		 
	    		 if(aObj1 instanceof JSONObject)
	    		 {
	    			 if(((JSONObject) aObj1).get("type").equals(MetaType.rule.toString()))
	    			 {
	    				 rule=ruleServiceImpl.findLatestByUuid(((JSONObject) aObj1).getString("uuid"));
	    				 if(rule!=null)
	    				 {
	    					 String a= object.get("attrId").toString();
	    					 int attrId = Integer.parseInt(a);
	    					 AttributeSource attrSource=rule.getAttributeInfo().get(attrId);
	    					 AttributeRefHolder attrRefHolder=attrSource.getSourceAttr();
	    					 if(attrRefHolder.getRef().getType().equals(MetaType.function))
	    					 {
	    						 function=functionServiceImpl.findLatestByUuid(attrRefHolder.getRef().getUuid());
	    						 if(function!=null){
	    						 createEdge(UUID,function.getUuid(),function.getVersion(),keyValue, edgeRowList, edgeRowMap);
	    						 }
	    					 }
	    					 if(attrRefHolder.getRef().getType().equals(MetaType.dataset))
	    					 {
	    						 dataset=datasetServiceImpl.findLatestByUuid(attrRefHolder.getRef().getUuid());
	    						 AttributeSource datasetAttr=dataset.getAttributeInfo().get(attrId);
	    						 if(datasetAttr.getSourceAttr().getRef().getType().equals(MetaType.datapod))
	    						 {
	    							 datapod=datapodServiceImpl.findLatestByUuid(datasetAttr.getSourceAttr().getRef().getUuid());
	    							 if(datapod!=null){
	    							 String attributeId=datapod.getUuid().concat("_").concat(datasetAttr.getSourceAttr().getAttrId()).concat("_").concat(datapod.getVersion());
	    	    					 edgeRow = RowFactory.create(uuid, attributeId, keyValue);
	    	 	    				edgeRowList.add(edgeRow);
	    	 	    				edgeRowMap.put(uuid+"_"+attributeId+"_"+keyValue, edgeRow);
	    							 }
	    						
	    						 }
	    						 if(datasetAttr.getSourceAttr().getRef().getType().equals(MetaType.formula))
	    						 {
	    							 formula=formulaServiceImpl.findLatestByUuid(datasetAttr.getSourceAttr().getRef().getUuid());
	    							if(formula!=null){
	    	    					 edgeRow = RowFactory.create(uuid, formula.getUuid().concat("_").concat(formula.getVersion()), keyValue);
	    	 	    				edgeRowList.add(edgeRow);
	    	 	    				edgeRowMap.put(uuid+"_"+formula.getUuid().concat("_").concat(formula.getVersion())+"_"+keyValue, edgeRow);
	    							}
	    						
	    						 }
	    					 }else
	    					 {
	    					 datapod=datapodServiceImpl.findLatestByUuid(attrRefHolder.getRef().getUuid());
	    					 if(datapod!=null){
	    					 String attributeId=datapod.getUuid().concat("_").concat(attrRefHolder.getAttrId()).concat("_").concat(datapod.getVersion());
	    					 edgeRow = RowFactory.create(uuid, attributeId, keyValue);
	 	    				edgeRowList.add(edgeRow);
	 	    				edgeRowMap.put(uuid+"_"+attributeId+"_"+keyValue, edgeRow);
	    					 }
	    					 }
	    				 }
	    			 }
	    			 
	    			 
	    			 if(((JSONObject) aObj1).get("type").equals(MetaType.dataset.toString()))
	    			 {
	    				 dataset=datasetServiceImpl.findLatestByUuid(((JSONObject) aObj1).getString("uuid"));
	    				 if(dataset!=null)
	    				 {
	    					 String a= object.get("attrId").toString();
	    					 int attrId = Integer.parseInt(a);
	    					 AttributeSource attrSource=dataset.getAttributeInfo().get(attrId);
	    					 AttributeRefHolder attrRefHolder=attrSource.getSourceAttr();
	    					 if(attrRefHolder.getRef().getType().equals(MetaType.function))
	    					 {
	    						 function=functionServiceImpl.findLatestByUuid(attrRefHolder.getRef().getUuid());
	    						 createEdge(UUID,function.getUuid(),function.getVersion(),keyValue, edgeRowList, edgeRowMap);
	    					 }
	    					 else
	    						 if(attrRefHolder.getRef().getType().equals(MetaType.formula))
		    					 {
	    							 formula=formulaServiceImpl.findLatestByUuid(attrRefHolder.getRef().getUuid());
		    						 createEdge(UUID,formula.getUuid(),formula.getVersion(),keyValue, edgeRowList, edgeRowMap);
		    					 }else{
	    					 datapod=datapodServiceImpl.findLatestByUuid(attrRefHolder.getRef().getUuid());
	    					 if(datapod!=null){
	    					 String attributeId=datapod.getUuid().concat("_").concat(attrRefHolder.getAttrId()).concat("_").concat(datapod.getVersion());
	    					 edgeRow = RowFactory.create(uuid, attributeId, keyValue);
	 	    				edgeRowList.add(edgeRow);
	    					 }
	    					 }
	    				 }
	    			 }
	    			 
	    		 }
	    		 
	    		 if(!aObj1.equals(null)&&refKeyValue.equals("attrId")||refKeyValue.equals("attributeId"))
	    		 {
	    			 if(datapod!=null){
	    			 String attrId = datapod.getUuid().toString().concat("_").concat(
	    					 aObj1.toString().concat("_").concat(datapod.getVersion().toString()));
	    				edgeRow = RowFactory.create(uuid, attrId, keyValue);
	    				edgeRowList.add(edgeRow);
	    				edgeRowMap.put(uuid+"_"+attrId+"_"+keyValue, edgeRow);
	    			 }
	    			 if(rule!=null)
	    			 {
	    				 
	    			 }
	    		 }
	    		 if(!aObj1.equals(null)&&!refKeyValue.equals("attrId"))
	    		 {
	    		 JSONObject jsonObject=new JSONObject(aObj1.toString());
	    		 String refuuid = jsonObject.getString("uuid");
	    		 String refversion = jsonObject.getString("version");
	    		 String refType = jsonObject.getString("type");
	    		 
	    		 entity = getEntity(refuuid, refversion, refType);
				if (entity != null) {
					createEdge(UUID, entity.getUuid(), entity.getVersion(), keyValue, edgeRowList, edgeRowMap);
				}
	    	 
	    	   }
	    	}
	    }
	   }
	 }
}
*/
}

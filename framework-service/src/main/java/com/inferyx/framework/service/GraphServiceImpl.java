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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.graphframes.GraphFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IEdgeDao;
import com.inferyx.framework.dao.IVertexDao;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Edge;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.FilterInfo;
import com.inferyx.framework.domain.GraphEdge;
import com.inferyx.framework.domain.GraphExec;
import com.inferyx.framework.domain.GraphFilter;
import com.inferyx.framework.domain.GraphMetaIdentifier;
import com.inferyx.framework.domain.GraphMetaIdentifierHolder;
import com.inferyx.framework.domain.Graphpod;
import com.inferyx.framework.domain.GraphpodResult;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.NodeDetails;
import com.inferyx.framework.domain.Property;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.SourceAttr;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Vertex;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.GraphOperator;
import com.inferyx.framework.operator.IExecutable;
import com.inferyx.framework.operator.IParsable;
@Service
public class GraphServiceImpl extends BaseRuleExecTemplate implements IParsable, IExecutable {

	@Autowired
	private MetadataServiceImpl metadataServiceImpl;
	@Autowired
	IVertexDao iVertexDao;
	@Autowired
	IEdgeDao iEdgeDao;
	@Autowired
	LogServiceImpl logServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	GraphOperator graphOperator;
	@Autowired
	protected ExecutorFactory execFactory;
	@Autowired
	protected DataStoreServiceImpl dataStoreServiceImpl;
	@Resource
	protected ConcurrentHashMap graphpodMap;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private SparkExecutor<?> sparkExecutor;

	public LogServiceImpl getLogServiceImpl() {
		return logServiceImpl;
	}

	public void setLogServiceImpl(LogServiceImpl logServiceImpl) {
		this.logServiceImpl = logServiceImpl;
	}

	private static final Logger logger = Logger.getLogger(GraphServiceImpl.class);
	private List<String> keywordList = new ArrayList<>();

	Datapod datapod = new Datapod();
	Session session = new Session();

	Relation relation = new Relation();
	Filter filterDet = new Filter();

	List<Row> verticesRowList = new ArrayList<Row>();
	List<Row> edgeRowList = new ArrayList<Row>();
	List<String> createDet = new ArrayList<String>();
	JavaRDD<Row> verRow = null;
	JavaRDD<Row> edgRow = null;
	JavaRDD<Row> verRow1 = null;
	JavaRDD<Row> edgRow1 = null;
	/*
	 * DataFrame verDF; DataFrame edgDF; DataFrame verDF1; DataFrame edgDF1; static
	 * DataFrame modifiedVertices = null; static DataFrame modifiedEdges = null;
	 */
	static GraphFrame graph;
	List<String> verEdgDet = new ArrayList<String>();
	Row datapodSourceRow;
	Row edgeRow;
	String datapodUUID = null;
	String attrId = null;

	List<Row> rows = new ArrayList<Row>();
	List<Row> edges = new ArrayList<Row>();
	List<Object> targetIds = new ArrayList<Object>();
	NodeDetails nodeDetails = new NodeDetails();
	List<String> rowDetails = new ArrayList<String>();
	List<Map<String, Object>> verlist = new ArrayList<Map<String, Object>>();
	StructType verSchema;
	StructType verSchema1;
	StructType edgSchema;
	StructType edgSchema1;
	MetaIdentifierHolder dependOnValue;
	Set<MetaIdentifierHolder> uniqueJoin = new HashSet<MetaIdentifierHolder>();
	static Map<String, ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();

	String joinType;
	String dependentUuid = null;
	String dependentUuid3 = null;
	String dependentUuid1 = null;
	Row[] edgesDataFrame = null;
	Row[] edgesDataFrame1 = null;
	Row[] edgesDataFrame2 = null;
	Row[] edgesDataFrame4 = null;
	MetaIdentifier joinkeyDet;
	List<List<SourceAttr>> src = new ArrayList<List<SourceAttr>>();
	List<List<FilterInfo>> filter = new ArrayList<List<FilterInfo>>();

	private void populateKeywordList() {
		keywordList.add("stages");
		keywordList.add("tasks");
		keywordList.add("operators");
		keywordList.add("filterInfo");
		keywordList.add("attributeMap");
		keywordList.add("attributes");
		keywordList.add("attributeInfo");
		keywordList.add("expressionInfo");
		keywordList.add("formulaInfo");
		keywordList.add("functionInfo");
		keywordList.add("relationInfo");
		keywordList.add("joinKey");
		keywordList.add("operand");
		keywordList.add("ruleInfo");
		keywordList.add("sectionInfo");
		keywordList.add("params");
		keywordList.add("paramInfo");
		keywordList.add("paramSetVal");
		keywordList.add("features");
		keywordList.add("appInfo");
		keywordList.add("roleInfo");
		keywordList.add("privilegeInfo");
		keywordList.add("operatorInfo");
		keywordList.add("execList");
		keywordList.add("keys");
		keywordList.add("groups");
		keywordList.add("values");
		keywordList.add("groupInfo");
		keywordList.add("refKeyList");
		keywordList.add("featureAttrMap");
		keywordList.add("configInfo");
		keywordList.add("featureInfo");
		keywordList.add("nodeInfo");
		keywordList.add("edgeInfo");
		keywordList.add("nodeProperties");
		keywordList.add("edgeProperties");
		keywordList.add("metaList");
		keywordList.add("pipelineInfo");

		

	}

	/*
	 * @SuppressWarnings("unused") public String getGraphJson(String uuid,String
	 * version, String degree) throws JsonProcessingException,
	 * IllegalAccessException, IllegalArgumentException, InvocationTargetException,
	 * NoSuchMethodException, SecurityException, NullPointerException,
	 * ParseException { String result = null; List<Map<String, Object>> vertexData =
	 * new ArrayList<>(); List<Map<String, Object>> edgesData = new ArrayList<>();
	 * 
	 * List<Map<String, Object>> data = new ArrayList<>(); DataFrame motifs = null;
	 * 
	 * Row[] vertexRow1 = graph.vertices().collect();
	 * 
	 * if(degree.equalsIgnoreCase("2")){ motifs = graph.find(
	 * "(Object)-[relationwithChild]->(Child);(Child)-[relationwithSubChild]->(SubChild)"
	 * ); } if(degree.equalsIgnoreCase("abc")){ motifs = graph.find(
	 * "(Child)-[relationwithSubChild]->(SubChild);(Object)-[relationwithChild]->(Child)"
	 * ); } if(degree.equalsIgnoreCase("3")){ motifs = graph.find(
	 * "(Object)-[relationwithChild]->(Child);(Child)-[relationwithSubChild]->(SubChild);(SubChild)-[relationwithSubsubChild]->(SubsubChild)"
	 * ); } if(degree.equalsIgnoreCase("4")){ motifs = graph.find(
	 * "(Object)-[relationwithChild]->(Child);(Child)-[relationwithSubChild]->(SubChild);"
	 * + "(SubChild)-[relationwithSubsubChild]->(SubsubChild);" +
	 * "(SubsubChild)-[relationwithSubsubsubChild]->(SubsubsubChild)"); }
	 * if(degree.equalsIgnoreCase("1")){ motifs =
	 * graph.find("(Object)-[relationwithChild]->(Child)"); }
	 * if(degree.equalsIgnoreCase("-1")){ motifs =
	 * graph.find("(Child)-[relationwithChild]->(Object)"); }
	 * if(degree.equalsIgnoreCase("-2")){ motifs = graph.find(
	 * "(Child)-[relationwithChild]->(SubChild);(SubChild)-[relationwithSubChild]->(Object)"
	 * ); } if(degree.equalsIgnoreCase("Attribute")){ motifs =
	 * graph.find("(Parent)-[relationwithParent]->(Object)"); }
	 * 
	 * //String uuidNew=uuid.concat("_").concat(version); List<Row> nodeSelection =
	 * motifs.filter("Object.id ='" +uuid+ "'").collectAsList();
	 * graph.vertices().filter("id='"+uuid+"'").show();
	 * graph.edges().filter("src='"+uuid+"'").show();
	 * 
	 * String[] columns2 = motifs.columns();
	 * 
	 * for (Row row : nodeSelection) { java.util.Map<String, Object> object = new
	 * HashMap<String, Object>(); for (String column : columns2) {
	 * object.put(column, row.getAs(column)); } data.add(object); }
	 * 
	 * // logger.info("PRINT DATA ::: ### " + data ); for (Map<String, Object> map :
	 * data) { for (Map.Entry<String, Object> entry : map.entrySet()) { String key =
	 * entry.getKey(); Object value = entry.getValue();
	 * if(key.equalsIgnoreCase("Object") ||key.equalsIgnoreCase("Child") ||
	 * key.equalsIgnoreCase("SubChild") ||
	 * key.equalsIgnoreCase("SubsubChild")||key.equalsIgnoreCase("SubsubsubChild")){
	 * Map<String, Object> vertexData1 = new HashMap<>();
	 * vertexData1.put(key,value); vertexData.add(vertexData1); }
	 * if(key.equalsIgnoreCase("relationwithChild")||key.equalsIgnoreCase(
	 * "relationwithSubChild")||key.equalsIgnoreCase("relationwithSubsubChild")||key
	 * .equalsIgnoreCase("relationwithSubsubsubChild")){ Map<String, Object>
	 * edgesData1 = new HashMap<>(); edgesData1.put(key,value);
	 * edgesData.add(edgesData1); } } }
	 * 
	 * List<String> vertexList = new ArrayList<>(); List<String> edgeList = new
	 * ArrayList<>();
	 * 
	 * for (Map<String, Object> map : vertexData) { for (Map.Entry<String, Object>
	 * entry : map.entrySet()) { String key = entry.getKey(); Object value =
	 * entry.getValue(); vertexList.add(value.toString()); } }
	 * 
	 * for (Map<String, Object> map : edgesData) { for (Map.Entry<String, Object>
	 * entry : map.entrySet()) { String key = entry.getKey(); Object value =
	 * entry.getValue(); edgeList.add(value.toString()); } }
	 * 
	 * List<Map<String,Object>> graphVertex = new ArrayList<>();
	 * List<Map<String,Object>> graphEdge = new ArrayList<>();
	 * 
	 * Set<String> hs = new HashSet<>(); hs.addAll(vertexList); vertexList.clear();
	 * vertexList.addAll(hs); List<String> uuids=new ArrayList<String>();
	 * List<String> dupEdges=new ArrayList<String>(); for(int k=0; k
	 * <vertexList.size();k++){ String total = vertexList.get(k); //
	 * logger.info("hashcode :::" + vertexList.get(k).hashCode()); int hashId =
	 * vertexList.get(k).hashCode(); String modifiedList = total.substring(1,
	 * total.length()-1); String [] retVal = modifiedList.split(",");
	 * 
	 * System.out.println("\n\n"); logger.info("total: "+total);
	 * logger.info("modifiedList: "+modifiedList); System.out.println("\n");
	 * 
	 * Map<String, Object> map = new HashMap<>(); map.put("id", retVal[0]);
	 * map.put("version", retVal[1]); ///If user's name is "user" then resolve it
	 * using method "getLatestByUuidWithoutAppUuid"
	 * if(retVal[2].toLowerCase().equalsIgnoreCase("user".toLowerCase())) { User
	 * user = (User) commonServiceImpl.getLatestByUuidWithoutAppUuid(retVal[0],
	 * MetaType.user.toString()); if(user != null) { String name = user.getName();
	 * map.put("name", name); }else map.put("name", retVal[2]); }else
	 * map.put("name", retVal[2]); /// map.put("nodeType", retVal[3]);
	 * map.put("dataType", retVal[4]); map.put("desc", retVal[5]);
	 * map.put("createdOn", retVal[6]); map.put("active", retVal[7]); //Set metaRef
	 * MetaIdentifier mi = new MetaIdentifier();
	 * mi.setType(Helper.getMetaType(retVal[3])); String[] tokens =
	 * retVal[0].split("_"); mi.setUuid(tokens[0]); mi.setVersion(retVal[1]);
	 * map.put("metaRef",mi); uuids.add(retVal[0]); int
	 * count=Collections.frequency(uuids, retVal[0]); if(count==1){
	 * graphVertex.add(map); } }
	 * 
	 * 
	 * for(int k=0; k <edgeList.size();k++){ String total = edgeList.get(k); String
	 * modifiedList = total.substring(1, total.length()-1); //
	 * logger.info(" modifiedList : " + total); String [] retVal =
	 * modifiedList.split(",");
	 * 
	 * Map<String, Object> map = new HashMap<>(); map.put("src", retVal[0]);
	 * map.put("dst", retVal[1]); map.put("src", retVal[1]); map.put("dst",
	 * retVal[0]); map.put("relationType", retVal[2]);
	 * dupEdges.add(retVal[0].concat("_").concat(retVal[1]).concat(retVal[2])); int
	 * count1=Collections.frequency(dupEdges,
	 * retVal[0].concat("_").concat(retVal[1]).concat(retVal[2])); if(count1==1) {
	 * graphEdge.add(map); } }
	 * 
	 * 
	 * nodeDetails.setNodes(graphVertex); nodeDetails.setLinks(graphEdge);
	 * nodeDetails.setJsonName("graph");
	 * 
	 * try { ObjectWriter writer = new ObjectMapper().writer()
	 * .withDefaultPrettyPrinter(); result = writer.writeValueAsString(nodeDetails);
	 * } catch (IOException e) { e.printStackTrace(); } return result; }
	 */

	/*
	 * @SuppressWarnings("unused") public String getGraphJson(String uuid,String
	 * version, String degree) throws JsonProcessingException,
	 * IllegalAccessException, IllegalArgumentException, InvocationTargetException,
	 * NoSuchMethodException, SecurityException, NullPointerException,
	 * ParseException { String result = null; List<Map<String, Object>> vertexData =
	 * new ArrayList<>(); List<Map<String, Object>> edgesData = new ArrayList<>();
	 * 
	 * List<Map<String, Object>> data = new ArrayList<>(); DataFrame motifs = null;
	 * 
	 * Row[] vertexRow1 = graph.vertices().collect();
	 * 
	 * if(degree.equalsIgnoreCase("2")){ motifs = graph.find(
	 * "(Object)-[relationwithChild]->(Child);(Child)-[relationwithSubChild]->(SubChild)"
	 * ); } if(degree.equalsIgnoreCase("abc")){ motifs = graph.find(
	 * "(Child)-[relationwithSubChild]->(SubChild);(Object)-[relationwithChild]->(Child)"
	 * ); } if(degree.equalsIgnoreCase("3")){ motifs = graph.find(
	 * "(Object)-[relationwithChild]->(Child);(Child)-[relationwithSubChild]->(SubChild);(SubChild)-[relationwithSubsubChild]->(SubsubChild)"
	 * ); } if(degree.equalsIgnoreCase("4")){ motifs = graph.find(
	 * "(Object)-[relationwithChild]->(Child);(Child)-[relationwithSubChild]->(SubChild);"
	 * + "(SubChild)-[relationwithSubsubChild]->(SubsubChild);" +
	 * "(SubsubChild)-[relationwithSubsubsubChild]->(SubsubsubChild)"); }
	 * if(degree.equalsIgnoreCase("1")){ motifs =
	 * graph.find("(Object)-[relationwithChild]->(Child)"); }
	 * if(degree.equalsIgnoreCase("-1")){ motifs =
	 * graph.find("(Child)-[relationwithChild]->(Object)"); }
	 * if(degree.equalsIgnoreCase("-2")){ motifs = graph.find(
	 * "(Child)-[relationwithChild]->(SubChild);(SubChild)-[relationwithSubChild]->(Object)"
	 * ); } if(degree.equalsIgnoreCase("Attribute")){ motifs =
	 * graph.find("(Parent)-[relationwithParent]->(Object)"); }
	 * 
	 * //String uuidNew=uuid.concat("_").concat(version); List<Row> nodeSelection =
	 * motifs.filter("Object.id ='" +uuid+ "'").collectAsList();
	 * graph.vertices().filter("id='"+uuid+"'").show();
	 * graph.edges().filter("src='"+uuid+"'").show();
	 * 
	 * String[] columns2 = motifs.columns();
	 * 
	 * for (Row row : nodeSelection) { java.util.Map<String, Object> object = new
	 * HashMap<String, Object>(); for (String column : columns2) {
	 * object.put(column, row.getAs(column)); } data.add(object); }
	 * 
	 * // logger.info("PRINT DATA ::: ### " + data ); for (Map<String, Object> map :
	 * data) { for (Map.Entry<String, Object> entry : map.entrySet()) { String key =
	 * entry.getKey(); Object value = entry.getValue();
	 * if(key.equalsIgnoreCase("Object") ||key.equalsIgnoreCase("Child") ||
	 * key.equalsIgnoreCase("SubChild") ||
	 * key.equalsIgnoreCase("SubsubChild")||key.equalsIgnoreCase("SubsubsubChild")){
	 * Map<String, Object> vertexData1 = new HashMap<>();
	 * vertexData1.put(key,value); vertexData.add(vertexData1); }
	 * if(key.equalsIgnoreCase("relationwithChild")||key.equalsIgnoreCase(
	 * "relationwithSubChild")||key.equalsIgnoreCase("relationwithSubsubChild")||key
	 * .equalsIgnoreCase("relationwithSubsubsubChild")){ Map<String, Object>
	 * edgesData1 = new HashMap<>(); edgesData1.put(key,value);
	 * edgesData.add(edgesData1); } } }
	 * 
	 * List<String> vertexList = new ArrayList<>(); List<String> edgeList = new
	 * ArrayList<>();
	 * 
	 * for (Map<String, Object> map : vertexData) { for (Map.Entry<String, Object>
	 * entry : map.entrySet()) { String key = entry.getKey(); Object value =
	 * entry.getValue(); vertexList.add(value.toString()); } }
	 * 
	 * for (Map<String, Object> map : edgesData) { for (Map.Entry<String, Object>
	 * entry : map.entrySet()) { String key = entry.getKey(); Object value =
	 * entry.getValue(); edgeList.add(value.toString()); } }
	 * 
	 * List<Map<String,Object>> graphVertex = new ArrayList<>();
	 * List<Map<String,Object>> graphEdge = new ArrayList<>();
	 * 
	 * Set<String> hs = new HashSet<>(); hs.addAll(vertexList); vertexList.clear();
	 * vertexList.addAll(hs); List<String> uuids=new ArrayList<String>();
	 * List<String> dupEdges=new ArrayList<String>(); for(int k=0; k
	 * <vertexList.size();k++){ String total = vertexList.get(k); //
	 * logger.info("hashcode :::" + vertexList.get(k).hashCode()); int hashId =
	 * vertexList.get(k).hashCode(); String modifiedList = total.substring(1,
	 * total.length()-1); String [] retVal = modifiedList.split(",");
	 * 
	 * System.out.println("\n\n"); logger.info("total: "+total);
	 * logger.info("modifiedList: "+modifiedList); System.out.println("\n");
	 * 
	 * Map<String, Object> map = new HashMap<>(); map.put("id", retVal[0]);
	 * map.put("version", retVal[1]); ///If user's name is "user" then resolve it
	 * using method "getLatestByUuidWithoutAppUuid"
	 * if(retVal[2].toLowerCase().equalsIgnoreCase("user".toLowerCase())) { User
	 * user = (User) commonServiceImpl.getLatestByUuidWithoutAppUuid(retVal[0],
	 * MetaType.user.toString()); if(user != null) { String name = user.getName();
	 * map.put("name", name); }else map.put("name", retVal[2]); }else
	 * map.put("name", retVal[2]); /// map.put("nodeType", retVal[3]);
	 * map.put("dataType", retVal[4]); map.put("desc", retVal[5]);
	 * map.put("createdOn", retVal[6]); map.put("active", retVal[7]); //Set metaRef
	 * MetaIdentifier mi = new MetaIdentifier();
	 * mi.setType(Helper.getMetaType(retVal[3])); String[] tokens =
	 * retVal[0].split("_"); mi.setUuid(tokens[0]); mi.setVersion(retVal[1]);
	 * map.put("metaRef",mi); uuids.add(retVal[0]); int
	 * count=Collections.frequency(uuids, retVal[0]); if(count==1){
	 * graphVertex.add(map); } }
	 * 
	 * 
	 * for(int k=0; k <edgeList.size();k++){ String total = edgeList.get(k); String
	 * modifiedList = total.substring(1, total.length()-1); //
	 * logger.info(" modifiedList : " + total); String [] retVal =
	 * modifiedList.split(",");
	 * 
	 * Map<String, Object> map = new HashMap<>(); map.put("src", retVal[0]);
	 * map.put("dst", retVal[1]); map.put("src", retVal[1]); map.put("dst",
	 * retVal[0]); map.put("relationType", retVal[2]);
	 * dupEdges.add(retVal[0].concat("_").concat(retVal[1]).concat(retVal[2])); int
	 * count1=Collections.frequency(dupEdges,
	 * retVal[0].concat("_").concat(retVal[1]).concat(retVal[2])); if(count1==1) {
	 * graphEdge.add(map); } }
	 * 
	 * 
	 * nodeDetails.setNodes(graphVertex); nodeDetails.setLinks(graphEdge);
	 * nodeDetails.setJsonName("graph");
	 * 
	 * try { ObjectWriter writer = new ObjectMapper().writer()
	 * .withDefaultPrettyPrinter(); result = writer.writeValueAsString(nodeDetails);
	 * } catch (IOException e) { e.printStackTrace(); } return result; }
	 */

	/*
	 * @SuppressWarnings("unused") private MetaIdentifierHolder
	 * getDependOnDet(String dependsOnVal) { MetaIdentifierHolder metaHolder = new
	 * MetaIdentifierHolder(); MetaIdentifier mIden = new MetaIdentifier();
	 * mIden.setUuid(dependsOnVal.substring(0,dependsOnVal.lastIndexOf("_")));
	 * mIden.setType(MetaType.datapod); metaHolder.setRef(mIden); return metaHolder;
	 * }
	 * 
	 * 
	 * 
	 * @SuppressWarnings("unused") private Map<String, ArrayList<String>>
	 * addValues(String key, String value) { ArrayList<String> tempList = null;
	 * if(hashMap.containsKey(key)){ tempList=hashMap.get(key); if(tempList == null)
	 * tempList = new ArrayList<String>(); tempList.add(value); } else { tempList =
	 * new ArrayList<>(); tempList.add(value); } hashMap.put(key,tempList); return
	 * hashMap; }
	 */
	/*
	 * public GraphFrame createGraph(List<Row> totalVertexList, List<Row>
	 * totalEdgeList) {
	 * 
	 * List<StructField> verFields = new ArrayList<StructField>(); List<StructField>
	 * edgFields = new ArrayList<StructField>();
	 * 
	 * verFields.add(DataTypes.createStructField("id",DataTypes.StringType,true));
	 * verFields.add(DataTypes.createStructField("version",DataTypes.StringType,
	 * true)); verFields.add(DataTypes.createStructField("name",
	 * DataTypes.StringType,true));
	 * verFields.add(DataTypes.createStructField("nodeType",DataTypes.StringType,
	 * true));
	 * verFields.add(DataTypes.createStructField("dataType",DataTypes.StringType,
	 * true)); verFields.add(DataTypes.createStructField("desc",
	 * DataTypes.StringType,true));
	 * verFields.add(DataTypes.createStructField("createdOn",DataTypes.StringType,
	 * true));
	 * verFields.add(DataTypes.createStructField("active",DataTypes.StringType,
	 * true));
	 * 
	 * verSchema = DataTypes.createStructType(verFields);
	 * 
	 * edgFields.add(DataTypes.createStructField("src", DataTypes.StringType,
	 * true)); edgFields.add(DataTypes.createStructField("dst",
	 * DataTypes.StringType, true));
	 * edgFields.add(DataTypes.createStructField("relationType",
	 * DataTypes.StringType, true));
	 * 
	 * edgSchema = DataTypes.createStructType(edgFields);
	 * 
	 * verRow = javaSparkContext.parallelize(totalVertexList); edgRow =
	 * javaSparkContext.parallelize(totalEdgeList); verDF =
	 * hiveContext.createDataFrame(verRow, verSchema); edgDF =
	 * hiveContext.createDataFrame(edgRow, edgSchema); verDF.cache(); edgDF.cache();
	 * 
	 * modifiedVertices = verDF.cache().unionAll(verDF).distinct(); modifiedEdges =
	 * edgDF.cache().unionAll(edgDF).distinct();
	 * 
	 * hiveContext.registerDataFrameAsTable(modifiedVertices, "vertex");
	 * hiveContext.registerDataFrameAsTable(modifiedEdges, "edge");
	 * 
	 * graph= new GraphFrame(modifiedVertices, modifiedEdges);
	 * 
	 * try { synchronized ("1") { graph.persist(StorageLevel.MEMORY_AND_DISK_SER());
	 * graph.vertices().persist(StorageLevel.MEMORY_AND_DISK_SER());
	 * graph.edges().persist(StorageLevel.MEMORY_AND_DISK_SER()); } } catch
	 * (Exception e) { e.printStackTrace(); }
	 * 
	 * graph.vertices().show(); graph.edges().show();
	 * 
	 * return graph; }
	 * 
	 */

	public void saveVertices(List<Row> verticesr, String rowId) {// Send rowId as null - used for overloading
		Vertex vertex = null;
		List<Vertex> vertices = new ArrayList<>();
		if (verticesr == null || verticesr.isEmpty()) {
			return;
		}
		for (int i = 0; i < verticesr.size(); i++) {
			GraphMetaIdentifierHolder graphMetaIdentifierHolder = (GraphMetaIdentifierHolder) verticesr.get(i).get(8);
			MetaIdentifierHolder appInfo= new MetaIdentifierHolder();
		    List<MetaIdentifierHolder> appInfoList = new ArrayList<MetaIdentifierHolder>();
			if(securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null) {
				 appInfo=(MetaIdentifierHolder) securityServiceImpl.getAppInfo();				
				 appInfoList.add(appInfo);
				 }
			vertex = new Vertex(verticesr.get(i).getString(0), verticesr.get(i).getString(1),
					verticesr.get(i).getString(2), verticesr.get(i).getString(3), verticesr.get(i).getString(4),
					verticesr.get(i).getString(5), verticesr.get(i).getString(6), verticesr.get(i).getString(7),
					graphMetaIdentifierHolder,appInfoList);
			vertices.add(vertex);
			if (i % 10000 == 0) {
				saveVertices(vertices);
				vertices = new ArrayList<>();
			}
		}
		saveVertices(vertices);
	}

	public void saveEdges(List<Row> edgesr, String rowId) {// Send rowId as null - used for overloading
		Edge edge = null;
		List<Edge> edges = new ArrayList<>();
		if (edgesr == null || edgesr.isEmpty()) {
			return;
		}
		for (int i = 0; i < edgesr.size(); i++) {
			GraphMetaIdentifierHolder srcMetaIdentifierHolder = (GraphMetaIdentifierHolder) edgesr.get(i).get(3);
			GraphMetaIdentifierHolder dstMetaIdentifierHolder = (GraphMetaIdentifierHolder) edgesr.get(i).get(4);

			edge = new Edge(edgesr.get(i).getString(0), edgesr.get(i).getString(1), edgesr.get(i).getString(2),
					srcMetaIdentifierHolder, dstMetaIdentifierHolder);
			// System.out.println(edge);
			edges.add(edge);
			if (i % 10000 == 0) {
				saveEdges(edges);
				edges = new ArrayList<>();
			}
		}
		saveEdges(edges);
	}

	public void saveEdges(List<Edge> edges) {
		iEdgeDao.save(edges);
	}

	public void saveVertices(List<Vertex> vertices) {
		iVertexDao.save(vertices);
	}

	public void saveVertex(Vertex vertex) {
/*
		DBObject indexOptions = new BasicDBObject();
		indexOptions.put("uuid", 1);
		CompoundIndexDefinition indexDefinition =new CompoundIndexDefinition(indexOptions);
		
		mongoTemplate.indexOps(Vertex.class).ensureIndex(indexDefinition);*/
		mongoTemplate.save(vertex);
		//iVertexDao.save(vertex);
	}

	public void saveEdge(Edge edge) {
		iEdgeDao.save(edge);
	}

	public List<Vertex> findVertices() {
		return iVertexDao.findAll();
	}

	public long countEdges() {
		return iEdgeDao.count();
	}

	public long countVertices() {
		return iVertexDao.count();
	}

	public void deleteAllVertices() {
		String appUuid = null;
		if (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null) {
			appUuid = (String) securityServiceImpl.getAppInfo().getRef().getUuid();
		}
		// iVertexDao.delete(appUuid);
		Query query = new Query();
		query.addCriteria(Criteria.where("appInfo.ref.uuid").is(appUuid));
		mongoTemplate.remove(query, Vertex.class);
	}
	

	public void deleteAllEdges() {
		String appUuid = null;
		if (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null) {
			appUuid = (String) securityServiceImpl.getAppInfo().getRef().getUuid();
		}
	//	iEdgeDao.deleteAll(appInfo);
		Query query = new Query();
		query.addCriteria(Criteria.where("appInfo.ref.uuid").is(appUuid));
		mongoTemplate.remove(query, Edge.class);
	}

	/*
	 * public void createVnE(String jsonString, List<Row> totalVertexList, List<Row>
	 * totalEdgeList, Map<String, Row> verticesRowMap, Map<String, Row> edgeRowMap,
	 * String type) throws JSONException, ParseException { JSONObject jsonObject =
	 * new JSONObject(jsonString); String srcUuid = jsonObject.optString("uuid");
	 * String version = jsonObject.optString("version"); if (keywordList == null ||
	 * keywordList.isEmpty()) { populateKeywordList(); } List<BaseEntity>
	 * baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(type, null,
	 * null, null, null, null, null, srcUuid, version); String name =
	 * (baseEntityList == null ||
	 * baseEntityList.isEmpty())?"":baseEntityList.get(0).getName(); if
	 * (StringUtils.isBlank(version)) { version = (baseEntityList == null ||
	 * baseEntityList.isEmpty())?"":baseEntityList.get(0).getVersion(); } String
	 * uuid = srcUuid + "_" + version; Row vertexRow =
	 * createVertex(srcUuid+"_"+version, version, name, type, new Date().toString(),
	 * "Y"); totalVertexList.add(vertexRow);
	 * verticesRowMap.put(srcUuid.concat("_").concat(version).concat("_").concat(
	 * name).concat("_").concat(type).concat("_").concat("Y"), vertexRow); Vertex
	 * vertex = new Vertex(srcUuid+"_"+version, version, name, type, null, null, new
	 * Date().toString(), "Y"); saveVertex(vertex); createVnE(jsonObject, vertex,
	 * totalVertexList, totalEdgeList, verticesRowMap, edgeRowMap, null, null); }
	 * 
	 * public void createVnE(JSONObject jsonObject, Vertex srcVertex, List<Row>
	 * totalVertexList, List<Row> totalEdgeList, Map<String, Row> verticesRowMap,
	 * Map<String, Row> edgeRowMap, String position, String parentName) throws
	 * JSONException, ParseException {
	 * 
	 * Iterator<String> iter = jsonObject.keys(); JSONArray jsonArray = null; String
	 * value = null; String name = ""; BaseEntity baseEntity = null; Edge edge =
	 * null; Vertex vertex = null; Row vertexRow = null; Row edgeRow = null; String
	 * childUuid = null; String childVersion = null; String childType = null;
	 * List<BaseEntity> baseEntityList = null; String refName = null; String objUuid
	 * = jsonObject.optString("uuid"); String version =
	 * jsonObject.optString("version"); String type = jsonObject.optString("type");
	 * 
	 * if (version == null || version.equals("null")) { version = null; }
	 * 
	 * //Only applicable for Array if (position != null) { name =
	 * StringUtils.isBlank(parentName)?srcVertex.getName():parentName; //
	 * System.out.println("Creating edge..." + name+ "_" + position); edgeRow =
	 * createEdge (srcVertex.getUuid(), srcVertex.getUuid() + "_" + position, name+
	 * "_" + position, new HashMap<String, Row>()); totalEdgeList.add(edgeRow);
	 * edgeRowMap.put(srcVertex.getUuid()+"_"+srcVertex.getUuid()+"_"+position+"_"+
	 * name+ "_" + position, edgeRow); edge = new Edge(srcVertex.getUuid(),
	 * srcVertex.getUuid()+"_"+position, name+ "_" + position); saveEdge(edge);
	 * vertexRow = createVertex(srcVertex.getUuid() + "_" + position, "", name+ "_"
	 * + position, name, new Date().toString(), "Y");
	 * totalVertexList.add(vertexRow); verticesRowMap.put(srcVertex.getUuid() + "_"
	 * + position.concat("_").concat(name+ "_" +
	 * position).concat("_").concat(name).concat("_").concat("Y"), vertexRow);
	 * vertex = new Vertex(srcVertex.getUuid() + "_" + position, "", name+ "_" +
	 * position, name, null, null, new Date().toString(), "Y"); saveVertex(vertex);
	 * position = null; srcVertex = vertex; }
	 * 
	 * //Loop each property while (iter.hasNext()) { String key = iter.next();
	 * jsonArray = jsonObject.optJSONArray(key); JSONObject childObj =
	 * jsonObject.optJSONObject(key); value = jsonObject.optString(key); if
	 * (jsonArray != null) { if (jsonArray.length() == 0 ||
	 * !keywordList.contains(key)) { continue; } for( int i = 0; i <
	 * jsonArray.length(); i++) { value = jsonArray.optString(i); childObj =
	 * jsonArray.optJSONObject(i); name = key; if (childObj != null)
	 * createVnE(childObj, srcVertex, totalVertexList, totalEdgeList,
	 * verticesRowMap, edgeRowMap, i+"", name); } } else if (childObj != null &&
	 * value.startsWith("{",0)) { if (key.equalsIgnoreCase("ref")) { childUuid =
	 * childObj.optString("uuid"); childVersion = childObj.optString("version");
	 * childType = childObj.optString("type"); if (childVersion == null ||
	 * childVersion.equals("null")) { childVersion = null; }
	 * 
	 * if (!childType.equals("simple")) { baseEntityList =
	 * metadataServiceImpl.getBaseEntityByCriteria(childType, null, null, null,
	 * null, null, null, childUuid, childVersion); if
	 * (StringUtils.isBlank(childVersion)) { childVersion = (baseEntityList == null
	 * || baseEntityList.isEmpty())?"":baseEntityList.get(0).getVersion(); } refName
	 * = (baseEntityList == null ||
	 * baseEntityList.isEmpty())?"":baseEntityList.get(0).getName(); } else {
	 * childVersion = "0"; refName = "simple"; } if (StringUtils.isBlank(refName)) {
	 * refName = childType; } if
	 * (StringUtils.isNotBlank(jsonObject.optString("attrId"))) { if
	 * (StringUtils.isNotBlank(jsonObject.optString("attrName"))) { name = refName +
	 * "_" + jsonObject.getString("attrName"); } else { name = refName + "_" +
	 * jsonObject.getString("attrId"); } } else if
	 * (StringUtils.isNotBlank(jsonObject.optString("attributeId"))) { if
	 * (StringUtils.isNotBlank(jsonObject.optString("attributeName"))) { name =
	 * refName + "_" + jsonObject.getString("attributeName"); } else { name =
	 * refName + "_" + jsonObject.getString("attributeId"); } } else { name =
	 * refName; } edgeRow = createEdge (srcVertex.getUuid(),
	 * childUuid+"_"+childVersion, name, new HashMap<String, Row>());
	 * totalEdgeList.add(edgeRow);
	 * edgeRowMap.put(srcVertex.getUuid()+"_"+childUuid+"_"+childVersion+"_"+name,
	 * edgeRow); edge = new Edge(srcVertex.getUuid(), childUuid+"_"+childVersion,
	 * name); saveEdge(edge); vertexRow = createVertex(childUuid+"_"+childVersion,
	 * childVersion, name, childObj.optString("type"), new Date().toString(), "Y");
	 * totalVertexList.add(vertexRow);
	 * verticesRowMap.put(childUuid.concat("_").concat(childVersion).concat("_").
	 * concat(name).concat("_").concat(childObj.optString("type")).concat("_").
	 * concat("Y"), vertexRow); vertex = new Vertex(childUuid+"_"+childVersion,
	 * childVersion, name, childObj.optString("type"), null, null, new
	 * Date().toString(), "Y"); saveVertex(vertex); continue; } createVnE(childObj,
	 * srcVertex, totalVertexList, totalEdgeList, verticesRowMap, edgeRowMap,
	 * position, null);
	 * 
	 * } // else if (key.startsWith("type")) { ////
	 * System.out.println("Key is a MetaIdentifier"); ////
	 * System.out.println("Creating edge..." + objUuid+"_"+version); //
	 * baseEntityList =
	 * metadataServiceImpl.getBaseEntityByCriteria(jsonObject.optString(key), null,
	 * null, null, null, null, null, objUuid, version); // if (baseEntityList ==
	 * null || baseEntityList.isEmpty()) { // continue; // } // baseEntity =
	 * baseEntityList.get(0); // name =
	 * StringUtils.isBlank(baseEntity.getName())?key:baseEntity.getName(); //
	 * edgeRow = createEdge (srcVertex.getUuid(), objUuid+"_"+version, name, new
	 * HashMap<String, Row>()); // totalEdgeList.add(edgeRow); //
	 * edgeRowMap.put(srcVertex.getUuid()+"_"+objUuid+"_"+version+"_"+name,
	 * edgeRow); // edge = new Edge(srcVertex.getUuid(), objUuid+"_"+version, name);
	 * // saveEdge(edge); // vertexRow = createVertex(objUuid+"_"+version, version,
	 * name, jsonObject.optString("type"), new Date().toString(), "Y"); //
	 * totalVertexList.add(vertexRow); //
	 * verticesRowMap.put(objUuid.concat("_").concat(version).concat("_").concat(
	 * name).concat("_").concat(jsonObject.optString("type")).concat("_").concat("Y"
	 * ), vertexRow); // vertex = new Vertex(objUuid+"_"+version, version, name,
	 * jsonObject.optString("type"), null, null, new Date().toString(), "Y"); //
	 * saveVertex(vertex); // } } }
	 */

	/*
	 * public List<Row> createVertex(Map<String, Object> document,String type,
	 * List<Row> verticesRowList, Map<String, Row> verticesRowMap) {
	 * 
	 * String UUID=document.get("uuid").toString().concat("_").concat(document.get(
	 * "version").toString()); String Version=document.get("version").toString();
	 * String Name=document.get("name").toString(); String
	 * CreatedOn=document.get("createdOn").toString(); String
	 * active=document.get("active").toString(); Row vertexRow =
	 * RowFactory.create(UUID,Version,Name,type, null, null, CreatedOn, active);
	 * 
	 * verticesRowList.add(vertexRow);
	 * verticesRowMap.put(UUID.concat("_").concat(Version).concat("_").concat(Name).
	 * concat("_").concat(type).concat("_").concat(active), vertexRow);
	 * 
	 * return verticesRowList; }
	 */

	public List<Row> saveVertex(Map<String, Object> document, String type, List<Row> verticesRowList) {

		String UUID = document.get("uuid").toString().concat("_").concat(document.get("version").toString());
		String Version = document.get("version").toString();
		String Name = document.get("name").toString();
		String CreatedOn = document.get("createdOn").toString();
		String active = document.get("active").toString();
		Row vertexRow = RowFactory.create(UUID, Version, Name, type, null, null, CreatedOn, active);

		Vertex vertex = new Vertex(UUID, Version, Name, type, null, null, CreatedOn, active, null, null);
		saveVertex(vertex);

		verticesRowList.add(vertexRow);

		return verticesRowList;
	}

	public Row createVertex(String uuid, String version, String name, String nodeType, String createdOn, String active,
			GraphMetaIdentifierHolder graphMetaIdentifierHolder ,List<MetaIdentifierHolder> appInfo) {
		return RowFactory.create(uuid, version, name, nodeType, null, null, createdOn, active,
				graphMetaIdentifierHolder,appInfo);
	}

	/*
	 * public List<Row> createEdge(String srcUuid, String dstUuid, String
	 * dstVersion, String name, List<Row> edgeRowList, Map<String, Row> edgeRowMap)
	 * { return createEdge(srcUuid, dstUuid.concat("_").concat(dstVersion), name,
	 * edgeRowList, edgeRowMap); }
	 * 
	 * public List<Row> createEdge(String srcUuid, String dst, String name,
	 * List<Row> edgeRowList, Map<String, Row> edgeRowMap) { Row edgeRow =
	 * RowFactory.create(srcUuid, dst, name);
	 * edgeRowMap.put(srcUuid+"_"+dst+"_"+name, edgeRow); edgeRowList.add(edgeRow);
	 * return edgeRowList; }
	 */

	public Row createEdge(String srcUuid, String dst, String name, Map<String, Row> edgeRowMap,
			GraphMetaIdentifierHolder srcMetaRef, GraphMetaIdentifierHolder dstMetaRef) {
		Row edgeRow = RowFactory.create(srcUuid, dst, name, srcMetaRef, dstMetaRef);
		edgeRowMap.put(srcUuid + "_" + dst + "_" + name, edgeRow);
		return edgeRow;
	}

	@SuppressWarnings({ "unused", "unchecked", "null" })
	public void createVnE(String jsonString, List<Row> totalVertexList, List<Row> totalEdgeList,
			Map<String, Row> verticesRowMap, Map<String, Row> edgeRowMap, String type,
			GraphMetaIdentifierHolder graphMetaIdentifierHolder) throws JSONException, ParseException,
			JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException {
		JSONObject jsonObject = new JSONObject(jsonString);

		String srcUuid = jsonObject.optString("uuid");
		String srcVersion = jsonObject.optString("version");
		if (keywordList == null || keywordList.isEmpty()) {
			populateKeywordList();
		}
		List<BaseEntity> baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(type, null, null, null, null,
				null, null, srcUuid, null, null);
		String name = (baseEntityList == null || baseEntityList.isEmpty()) ? "" : baseEntityList.get(0).getName();
		String uuid = srcUuid + "_" + srcVersion;
		if (StringUtils.isBlank(name)) {
			String n = jsonObject.optString("name");
			name = n;
		}
		graphMetaIdentifierHolder = new GraphMetaIdentifierHolder();
		GraphMetaIdentifier graphMetaIdentifier = new GraphMetaIdentifier();
		graphMetaIdentifier.setUuid(srcUuid);
		graphMetaIdentifier.setType(type);
		graphMetaIdentifier.setVersion(srcVersion);
		graphMetaIdentifier.setName(name);
		graphMetaIdentifierHolder.setRef(graphMetaIdentifier);
		MetaIdentifierHolder appInfo= new MetaIdentifierHolder();
	    List<MetaIdentifierHolder> appInfoList = new ArrayList<MetaIdentifierHolder>();
		if(securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null) {
			 appInfo=(MetaIdentifierHolder) securityServiceImpl.getAppInfo();
			
			 appInfoList.add(appInfo);
			 }
		
	
		Row vertexRow = createVertex(uuid, "", name, type, new Date().toString(), "Y", graphMetaIdentifierHolder,appInfoList);
		totalVertexList.add(vertexRow);
		verticesRowMap.put(uuid.concat("_").concat(name).concat("_").concat(type).concat("_").concat("Y"), vertexRow);
		Vertex vertex = new Vertex(uuid, "", name, type, null, null, new Date().toString(), "Y",
				graphMetaIdentifierHolder,appInfoList);
		saveVertex(vertex);
		createVnE(jsonObject, vertex, totalVertexList, totalEdgeList, verticesRowMap, edgeRowMap, null, null,
				graphMetaIdentifierHolder);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public void createVnE(JSONObject jsonObject, Vertex srcVertex, List<Row> totalVertexList, List<Row> totalEdgeList,
			Map<String, Row> verticesRowMap, Map<String, Row> edgeRowMap, String position, String parentName,
			GraphMetaIdentifierHolder graphMetaIdentifierHolder) throws JSONException, ParseException,
			JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException {

		Iterator<String> iter = jsonObject.keys();
		JSONArray jsonArray = null;
		String value = null;
		String name = "";
		BaseEntity baseEntity = null;
		Edge edge = null;
		Vertex vertex = null;
		Row vertexRow = null;
		Row edgeRow = null;
		String childUuid = null;
		String childType = null;
		List<BaseEntity> baseEntityList = null;
		String refName = null;
		String objUuid = jsonObject.optString("uuid");
		String type = jsonObject.optString("type");
		GraphMetaIdentifier graphMeta = new GraphMetaIdentifier();
		graphMetaIdentifierHolder = new GraphMetaIdentifierHolder();

		GraphMetaIdentifierHolder srcEdgeMetaRef = new GraphMetaIdentifierHolder();
		GraphMetaIdentifier srcEdgeMetaIdenRef = new GraphMetaIdentifier();

		GraphMetaIdentifierHolder dstEdgeMetaRef = new GraphMetaIdentifierHolder();
		GraphMetaIdentifier dstEdgeMetaIdenRef = new GraphMetaIdentifier();
		/*
		 * GraphMetaIdentifier graphMeta=new GraphMetaIdentifier();
		 */
		// String nme = jsonObject.optString("name");

		// Only applicable for Array
		if (position != null) {// datapods attributes
			name = StringUtils.isBlank(parentName) ? srcVertex.getName() : parentName;
			// System.out.println("Creating edge..." + name+ "_" + position);
			srcEdgeMetaIdenRef.setType(srcVertex.getNodeType());
			srcEdgeMetaIdenRef.setUuid(srcVertex.getUuid());
			srcEdgeMetaIdenRef.setName(srcVertex.getName());
			srcEdgeMetaRef.setRef(srcEdgeMetaIdenRef);

			dstEdgeMetaIdenRef.setType(srcVertex.getNodeType());
			dstEdgeMetaIdenRef.setUuid(srcVertex.getUuid() + "_" + position);
			dstEdgeMetaIdenRef.setName(srcVertex.getName());
			dstEdgeMetaRef.setRef(dstEdgeMetaIdenRef);

			edgeRow = createEdge(srcVertex.getUuid(), srcVertex.getUuid() + "_" + position, name,
					new HashMap<String, Row>(), srcEdgeMetaRef, dstEdgeMetaRef);
			totalEdgeList.add(edgeRow);
			edgeRowMap.put(
					srcVertex.getUuid() + "_" + srcVertex.getUuid() + "_" + position + "_" + name + "_" + position,
					edgeRow);

			edge = new Edge(srcVertex.getUuid(), srcVertex.getUuid() + "_" + position, name, srcEdgeMetaRef,
					dstEdgeMetaRef);
			saveEdge(edge);
			graphMeta.setUuid(srcVertex.getUuid() + "_" + position);
			graphMeta.setType(name.toLowerCase());
			graphMeta.setName(position);
			// this line for attrinuteinfo version=1528799343_first_name
			if (parentName.equalsIgnoreCase("attributeinfo")) {
				graphMeta.setVersion(
						srcVertex.getUuid().substring(srcVertex.getUuid().lastIndexOf("_") + 1) + "_" + position);
			}
			graphMetaIdentifierHolder.setRef(graphMeta);
			MetaIdentifierHolder appInfo= new MetaIdentifierHolder();
		    List<MetaIdentifierHolder> appInfoList = new ArrayList<MetaIdentifierHolder>();
			if (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null) {
				appInfo = (MetaIdentifierHolder) securityServiceImpl.getAppInfo();

				appInfoList.add(appInfo);
			}
			
			vertexRow = createVertex(srcVertex.getUuid() + "_" + position, "", position, parentName,
					new Date().toString(), "Y", graphMetaIdentifierHolder,appInfoList);
			totalVertexList.add(vertexRow);
			verticesRowMap.put(srcVertex.getUuid() + "_" + position.concat("_").concat(name + "_" + position)
					.concat("_").concat(name).concat("_").concat("Y"), vertexRow);
			vertex = new Vertex(srcVertex.getUuid() + "_" + position, "", position, parentName, null, null,
					new Date().toString(), "Y", graphMetaIdentifierHolder,appInfoList);
			saveVertex(vertex);
			position = null;
			srcVertex = vertex;
		}

		// Loop each property
		while (iter.hasNext()) {
			String key = iter.next();
	/*	if (key.equalsIgnoreCase("pipelineInfo")) {
			System.out.println("assssssd");
			}*/
			jsonArray = jsonObject.optJSONArray(key);
			JSONObject childObj = jsonObject.optJSONObject(key);
			value = jsonObject.optString(key);
			if (jsonArray != null) {
				if (jsonArray.length() == 0 || !keywordList.contains(key)) {
					continue;
				}
				for (int i = 0; i < jsonArray.length(); i++) {
					value = jsonArray.optString(i);
					childObj = jsonArray.optJSONObject(i);

					name = key;
					if (childObj != null)

						if (key.equalsIgnoreCase("attributes") || key.equalsIgnoreCase("expressionInfo")
								|| key.equalsIgnoreCase("params") || key.equalsIgnoreCase("functionInfo")
								|| key.equalsIgnoreCase("sectionInfo") || key.equalsIgnoreCase("joinKey")
								|| key.equalsIgnoreCase("attributeMap") || key.equalsIgnoreCase("paramInfo")
								|| key.equalsIgnoreCase("features") || key.equalsIgnoreCase("stages")
								|| key.equalsIgnoreCase("tasks") || key.equalsIgnoreCase("operators")
								|| key.equalsIgnoreCase("featureAttrMap") || key.equalsIgnoreCase("configInfo")
								|| key.equalsIgnoreCase("featureInfo") || key.equalsIgnoreCase("nodeInfo")
								|| key.equalsIgnoreCase("edgeInfo")) {
							String attr = "";
							Map<String, String> map = new HashMap<String, String>();
							map.put("attributes", "name");
							map.put("expressionInfo", "logicalOperator");
							map.put("params", "paramName");
							map.put("functionInfo", "type");
							map.put("sectionInfo", "name");
							map.put("joinKey", "operator");
							map.put("stages", "name");
							map.put("tasks", "name");
							map.put("configInfo", "configName");
							map.put("featureInfo", "featureName");
							map.put("edgeInfo", "edgeName");
							map.put("nodeInfo", "nodeIcon");

							if (map.containsKey(key))
								attr = childObj.optString(map.get(key));
							else {
								map.put("attributeMap", name + "_" + i);
								map.put("joinKey", name);
								map.put("paramInfo", name + "_" + i);
								map.put("features", name + "_" + i);
								map.put("operators", name + "_" + i);
								map.put("featureAttrMap", name + "_" + i);

								if (map.containsKey(key))
									attr = map.get(key);
							}
							if (StringUtils.isEmpty(attr) || attr == null) {
								break;
							}
							createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap, edgeRowMap,
									attr, name, null);
						} else if (key.equalsIgnoreCase("attributeInfo")
								|| key.equalsIgnoreCase("filterInfo"))/* For Dataset/profile/filter */ {
							String refN = childObj.optString("ref");
							String attr = childObj.optString("attrSourceName");// for Prof.rule
							String attr1 = childObj.optString("operator");// for filter

							if (childObj != null && value.startsWith("{", 0) && (StringUtils.isNotBlank(refN))) {
								String attr3 = childObj.optString("ref");
								JSONObject jsonObj4 = new JSONObject(attr3);
								if (jsonObj4 != null && attr3.startsWith("{", 0)) {
									childUuid = jsonObj4.optString("uuid");
									childType = jsonObj4.optString("type");
									if (!childType.equals(MetaType.simple.toString())) {
										baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(childType, null,
												null, null, null, null, null, childUuid, null, null);
										refName = (baseEntityList == null || baseEntityList.isEmpty()) ? ""
												: baseEntityList.get(0).getName();
									} else {
										refName = MetaType.simple.toString();
									}
									if (StringUtils.isBlank(refName)) {
										refName = childType;
									}
								}
								createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap,
										edgeRowMap, refName + "_" + i, name, null);
							} else if (attr != "" && attr != null)
								createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap,
										edgeRowMap, attr, name, null);
							else if (attr1 != "" && attr1 != null)
								createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap,
										edgeRowMap, attr1, name, null);
						} else if (key.equalsIgnoreCase("relationInfo"))/* for relation */ {
							String attr4 = childObj.optString("join");
							JSONObject jsonObj5 = new JSONObject(attr4);
							String attr3 = jsonObj5.optString("ref");
							JSONObject jsonObj4 = new JSONObject(attr3);
							if (jsonObj4 != null && attr3.startsWith("{", 0)) {
								childUuid = jsonObj4.optString("uuid");
								childType = jsonObj4.optString("type");
								if (!childType.equals(MetaType.simple.toString())) {
									/*
									 * baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(childType, null,
									 * null, null, null, null, null, childUuid, null, null); refName =
									 * (baseEntityList == null || baseEntityList.isEmpty()) ? "" :
									 * baseEntityList.get(0).getName();
									 */

									baseEntityList = commonServiceImpl.getResolveNameByUuidandType(childUuid,
											childType);
									refName = (baseEntityList == null || baseEntityList.isEmpty()) ? ""
											: baseEntityList.get(0).getName();
								} else {
									continue;
								}
								if (StringUtils.isBlank(refName)) {
									refName = childType;
								}
							}
							createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap, edgeRowMap,
									refName, name, null);
						}

						else if (key.equalsIgnoreCase("nodeProperties")
								|| key.equalsIgnoreCase("edgeProperties"))/* for nodeProperties */ {
							String attr = childObj.optString("attrId");
							if (childObj != null && value.startsWith("{", 0)) {
								String refN = childObj.optString("ref");
								if (childObj != null && refN.startsWith("{", 0)) {
									JSONObject jsonObjType = new JSONObject(refN);

									childUuid = jsonObjType.optString("uuid");
									childType = jsonObjType.optString("type");

									if (!childType.equals(MetaType.simple.toString())) {

										baseEntityList = commonServiceImpl.getResolveNameByUuidandType(childUuid,
												childType);
										refName = (baseEntityList == null || baseEntityList.isEmpty()) ? ""
												: baseEntityList.get(0).getName();
									}
								}
							}
							createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap, edgeRowMap,
									refName + "_" + attr, name, null);
						}

						/*
						 * else if (key.equalsIgnoreCase("edgeInfo")) for graphpod { String attr4 =
						 * childObj.optString("edgeSource");
						 * 
						 * if(attr4.equalsIgnoreCase("edgeSource")) { JSONObject jsonObj5 = new
						 * JSONObject(attr4); String attr3 = jsonObj5.optString("ref"); JSONObject
						 * jsonObj4 = new JSONObject(attr3); if (jsonObj4 != null &&
						 * attr3.startsWith("{", 0)) { childUuid = jsonObj4.optString("uuid"); childType
						 * = jsonObj4.optString("type"); if
						 * (!childType.equals(MetaType.simple.toString())) {
						 * 
						 * baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(childType, null,
						 * null, null, null, null, null, childUuid, null, null); refName =
						 * (baseEntityList == null || baseEntityList.isEmpty()) ? "" :
						 * baseEntityList.get(0).getName();
						 * 
						 * 
						 * baseEntityList = commonServiceImpl.getResolveNameByUuidandType(childUuid,
						 * childType); refName = (baseEntityList == null || baseEntityList.isEmpty()) ?
						 * "" : baseEntityList.get(0).getName(); } else { continue; } if
						 * (StringUtils.isBlank(refName)) { refName = childType; } } createVnE(childObj,
						 * srcVertex, totalVertexList, totalEdgeList, verticesRowMap, edgeRowMap,
						 * refName, name, null); } String attr5 = childObj.optString("sourceNodeId");
						 * if(attr5.equalsIgnoreCase("sourceNodeId")) { JSONObject jsonObj5 = new
						 * JSONObject(attr4); String refN1 = childObj.optString("attrId"); String attr3
						 * = jsonObj5.optString("ref"); JSONObject jsonObj4 = new JSONObject(attr3); if
						 * (jsonObj4 != null && attr3.startsWith("{", 0)) { childUuid =
						 * jsonObj4.optString("uuid"); childType = jsonObj4.optString("type"); if
						 * (!childType.equals(MetaType.simple.toString())) {
						 * 
						 * baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(childType, null,
						 * null, null, null, null, null, childUuid, null, null); refName =
						 * (baseEntityList == null || baseEntityList.isEmpty()) ? "" :
						 * baseEntityList.get(0).getName();
						 * 
						 * 
						 * baseEntityList = commonServiceImpl.getResolveNameByUuidandType(childUuid,
						 * childType); refName = (baseEntityList == null || baseEntityList.isEmpty()) ?
						 * "" : baseEntityList.get(0).getName(); } else { continue; } if
						 * (StringUtils.isBlank(refName)) { refName = childType; } if (refN1 != null &&
						 * !refN1.equals("null")) { refName = refName + "_" + refN1; } }
						 * createVnE(childObj, srcVertex, totalVertexList, totalEdgeList,
						 * verticesRowMap, edgeRowMap, refName, name, null); } String attr6 =
						 * childObj.optString("targetNodeId");
						 * if(attr6.equalsIgnoreCase("targetNodeId")) { JSONObject jsonObj5 = new
						 * JSONObject(attr4); String refN1 = childObj.optString("attrId"); String attr3
						 * = jsonObj5.optString("ref"); JSONObject jsonObj4 = new JSONObject(attr3); if
						 * (jsonObj4 != null && attr3.startsWith("{", 0)) { childUuid =
						 * jsonObj4.optString("uuid"); childType = jsonObj4.optString("type"); if
						 * (!childType.equals(MetaType.simple.toString())) {
						 * 
						 * baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(childType, null,
						 * null, null, null, null, null, childUuid, null, null); refName =
						 * (baseEntityList == null || baseEntityList.isEmpty()) ? "" :
						 * baseEntityList.get(0).getName();
						 * 
						 * 
						 * baseEntityList = commonServiceImpl.getResolveNameByUuidandType(childUuid,
						 * childType); refName = (baseEntityList == null || baseEntityList.isEmpty()) ?
						 * "" : baseEntityList.get(0).getName(); } else { continue; } if
						 * (StringUtils.isBlank(refName)) { refName = childType; } if (refN1 != null &&
						 * !refN1.equals("null")) { refName = refName + "_" + refN1; } }
						 * createVnE(childObj, srcVertex, totalVertexList, totalEdgeList,
						 * verticesRowMap, edgeRowMap, refName, name, null); } }
						 */

						else if (key.equalsIgnoreCase("ruleInfo") || key.equalsIgnoreCase("pipelineInfo")) {
							// String attrN = childObj.optString("ref");
							if (childObj != null && value.startsWith("{", 0)) {
								String refN = childObj.optString("ref");
								if (childObj != null && refN.startsWith("{", 0)) {
									JSONObject jsonObjType = new JSONObject(refN);
									childUuid = jsonObjType.optString("uuid");
									childType = jsonObjType.optString("type");
									if (!childType.equals(MetaType.simple.toString())) {
										/*
										 * baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(childType, null,
										 * null, null, null, null, null, childUuid, null, null); refName =
										 * (baseEntityList == null || baseEntityList.isEmpty()) ? "" :
										 * baseEntityList.get(0).getName();
										 */

										baseEntityList = commonServiceImpl.getResolveNameByUuidandType(childUuid,
												childType);
										refName = (baseEntityList == null || baseEntityList.isEmpty()) ? ""
												: baseEntityList.get(0).getName();
									} else {
										continue;
										// refName = MetaType.simple.toString();
									}
									if (StringUtils.isBlank(refName)) {
										refName = childType;
									}
								}
								createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap,
										edgeRowMap, refName, name, null);
							}
						} else if (key.equalsIgnoreCase("keys")) /* vizpod */ {
							// String attrN = childObj.optString("ref");
							if (childObj != null && value.startsWith("{", 0)) {
								String refN = childObj.optString("ref");
								String refN1 = childObj.optString("attributeId");
								if (childObj != null && refN.startsWith("{", 0)) {
									JSONObject jsonObjType = new JSONObject(refN);
									childUuid = jsonObjType.optString("uuid");
									childType = jsonObjType.optString("type");
									if (!childType.equals(MetaType.simple.toString())) {
										/*
										 * baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(childType, null,
										 * null, null, null, null, null, childUuid, null, null); refName =
										 * (baseEntityList == null || baseEntityList.isEmpty()) ? "" :
										 * baseEntityList.get(0).getName();
										 */

										baseEntityList = commonServiceImpl.getResolveNameByUuidandType(childUuid,
												childType);
										refName = (baseEntityList == null || baseEntityList.isEmpty()) ? ""
												: baseEntityList.get(0).getName();
									} else {

										continue;
										// refName = MetaType.simple.toString();
									}
									if (StringUtils.isBlank(refName)) {
										refName = childType;
									}
									if (refN1 != null && !refN1.equals("null")) {
										refName = refName + "_" + refN1;
									}
								}
								createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap,
										edgeRowMap, refName, name, null);
							}
						} else if (key.equalsIgnoreCase("values")) /* vizpod */ {
							// String attrN = childObj.optString("ref");
							if (childObj != null && value.startsWith("{", 0)) {
								String refN = childObj.optString("ref");
								String refN1 = childObj.optString("attributeId");
								if (childObj != null && refN.startsWith("{", 0)) {
									JSONObject jsonObjType = new JSONObject(refN);
									childUuid = jsonObjType.optString("uuid");
									childType = jsonObjType.optString("type");
									if (!childType.equals(MetaType.simple.toString())) {
										/*
										 * baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(childType, null,
										 * null, null, null, null, null, childUuid, null, null); refName =
										 * (baseEntityList == null || baseEntityList.isEmpty()) ? "" :
										 * baseEntityList.get(0).getName();
										 */

										baseEntityList = commonServiceImpl.getResolveNameByUuidandType(childUuid,
												childType);
										refName = (baseEntityList == null || baseEntityList.isEmpty()) ? ""
												: baseEntityList.get(0).getName();
									} else {
										continue;
										// refName = MetaType.simple.toString();
									}
									if (StringUtils.isBlank(refName)) {
										refName = childType;
									}
									if (refN1 != null && !refN1.equals("null")) {
										refName = refName + "_" + refN1;
									}
								}
								createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap,
										edgeRowMap, refName, name, null);
							}
						} else if (key.equalsIgnoreCase("groups")) /* vizpod */ {
							// String attrN = childObj.optString("ref");
							if (childObj != null && value.startsWith("{", 0)) {
								String refN = childObj.optString("ref");
								String refN1 = childObj.optString("attributeId");
								if (childObj != null && refN.startsWith("{", 0)) {
									JSONObject jsonObjType = new JSONObject(refN);
									childUuid = jsonObjType.optString("uuid");
									childType = jsonObjType.optString("type");
									if (!childType.equals(MetaType.simple.toString())) {
										/*
										 * baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(childType, null,
										 * null, null, null, null, null, childUuid, null, null); refName =
										 * (baseEntityList == null || baseEntityList.isEmpty()) ? "" :
										 * baseEntityList.get(0).getName();
										 */

										baseEntityList = commonServiceImpl.getResolveNameByUuidandType(childUuid,
												childType);
										refName = (baseEntityList == null || baseEntityList.isEmpty()) ? ""
												: baseEntityList.get(0).getName();
									} else {
										continue;
										// refName = MetaType.simple.toString();
									}
									if (StringUtils.isBlank(refName)) {
										refName = childType;
									}
									if (refN1 != null && !refN1.equals("null")) {
										refName = refName + "_" + refN1;
									}
								}
								createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap,
										edgeRowMap, refName, name, null);
							}
						} else if (key.equalsIgnoreCase("execList")) {
							// String attrN = childObj.optString("ref");
							if (childObj != null && value.startsWith("{", 0)) {
								String refN = childObj.optString("ref");
								if (childObj != null && refN.startsWith("{", 0)) {
									JSONObject jsonObjType = new JSONObject(refN);
									String attr = jsonObjType.optString("name");
									if (attr != null && !attr.equals("null"))
										createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap,
												edgeRowMap, attr, name, null);
								}
							}
						}
						else if (key.equalsIgnoreCase("metaList")) {
							// String attrN = childObj.optString("ref");
							if (childObj != null && value.startsWith("{", 0)) {
								String refN = childObj.optString("ref");
								if (childObj != null && refN.startsWith("{", 0)) {
									JSONObject jsonObjType = new JSONObject(refN);
									
									childType=jsonObjType.optString("type");
									if (!childType.equals(MetaType.simple.toString())) {
										/*
										 * baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(childType, null,
										 * null, null, null, null, null, childUuid, null, null); refName =
										 * (baseEntityList == null || baseEntityList.isEmpty()) ? "" :
										 * baseEntityList.get(0).getName();
										 */

										baseEntityList = commonServiceImpl.getResolveNameByUuidandType(jsonObjType.optString("uuid"),
												jsonObjType.optString("type"));
										refName = (baseEntityList == null || baseEntityList.isEmpty()) ? ""
												: baseEntityList.get(0).getName();
									} else {
										continue;
										// refName = MetaType.simple.toString();
									}
									if (StringUtils.isBlank(refName)) {
										refName = childType;
									}
									
									
									
									if (refName != null && !refName.equals("null"))
										createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap,
												edgeRowMap, refName, name, null);
								}
							}
						}else if (key.equalsIgnoreCase("formulaInfo")) {

							if (childObj != null && value.startsWith("{", 0)) {
								String name1 = childObj.optString("value");
								String refN = childObj.optString("ref");
								String refN1 = childObj.optString("attributeId");

								if (childObj != null && refN.startsWith("{", 0)) {
									JSONObject jsonObjType = new JSONObject(refN);
									childUuid = jsonObjType.optString("uuid");
									childType = jsonObjType.optString("type");

									if (!childType.equals(MetaType.simple.toString())) {
										/*
										 * baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(childType, null,
										 * null, null, null, null, null, childUuid, null, null); refName =
										 * (baseEntityList == null || baseEntityList.isEmpty()) ? "" :
										 * baseEntityList.get(0).getName();
										 */

										baseEntityList = commonServiceImpl.getResolveNameByUuidandType(childUuid,
												childType);
										refName = (baseEntityList == null || baseEntityList.isEmpty()) ? ""
												: baseEntityList.get(0).getName();
									} else {
										continue;
										// refName = MetaType.simple.toString();
									}
									if (refName.equalsIgnoreCase(MetaType.simple.toString())) {
										continue;
										// refName = MetaType.simple.toString();
										// refName = name1;
									}
									if (refN1 != null && !refN1.equals("null")) {
										refName = refName + "_" + refN1;
									}
								}
								GraphMetaIdentifierHolder graphMetaHolder = new GraphMetaIdentifierHolder();
								/*
								 * GraphMetaIdentifier graphMeta=new GraphMetaIdentifier();
								 */ graphMeta.setType(childType);
								graphMeta.setUuid(childUuid);
								graphMeta.setName(refName);
								graphMetaHolder.setRef(graphMeta);
								createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap,
										edgeRowMap, refName, name, graphMetaHolder);
							}
						} else if (key.equalsIgnoreCase("roleInfo")) /* for roleInfo/user */ {
							/*
							 * String attr3 = childObj.optString("ref"); JSONObject jsonObj4 = new
							 * JSONObject(attr3); if (jsonObj4 != null && attr3.startsWith("{", 0)) {
							 * childUuid = jsonObj4.optString("uuid"); childType =
							 * jsonObj4.optString("type"); if
							 * (!childType.equals(MetaType.simple.toString())) { baseEntityList =
							 * metadataServiceImpl.getBaseEntityByCriteria(childType, null, null, null,
							 * null, null, null, childUuid, null, null); refName = (baseEntityList == null
							 * || baseEntityList.isEmpty()) ? "" : baseEntityList.get(0).getName(); } else {
							 * refName = MetaType.simple.toString(); } if (StringUtils.isBlank(refName)) {
							 * refName = childType + "_" + i; } }
							 */
							createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap, edgeRowMap,
									position, name, graphMetaIdentifierHolder);
						}

						else if (key.equalsIgnoreCase("appInfo")
								|| key.equalsIgnoreCase("groupInfo"))/* for appInfo */ {
							/*
							 * String attr3 = childObj.optString("ref"); JSONObject jsonObj4 = new
							 * JSONObject(attr3); if (jsonObj4 != null && attr3.startsWith("{", 0)) {
							 * childUuid = jsonObj4.optString("uuid"); childType =
							 * jsonObj4.optString("type"); if
							 * (!childType.equals(MetaType.simple.toString())) { baseEntityList =
							 * metadataServiceImpl.getBaseEntityByCriteria(childType, null, null, null,
							 * null, null, null, childUuid, null, null); refName = (baseEntityList == null
							 * || baseEntityList.isEmpty()) ? "" : baseEntityList.get(0).getName(); } else {
							 * refName = MetaType.simple.toString(); } if (StringUtils.isBlank(refName)) {
							 * refName = childType; } }
							 */
							createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap, edgeRowMap,
									position, name, null);
						} else if (key.equalsIgnoreCase("refKeyList"))/* for business rule */ {
							String attr = childObj.optString("name");
							if (childObj != null && value.startsWith("{", 0)) {
								JSONObject jsonObjType = new JSONObject(value);
								childUuid = jsonObjType.optString("uuid");
								childType = jsonObjType.optString("type");

								if (!childType.equals(MetaType.simple.toString())) {
									/*
									 * baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(childType, null,
									 * null, null, null, null, null, childUuid, null, null); refName =
									 * (baseEntityList == null || baseEntityList.isEmpty()) ? "" :
									 * baseEntityList.get(0).getName();
									 */

									baseEntityList = commonServiceImpl.getResolveNameByUuidandType(childUuid,
											childType);
									refName = (baseEntityList == null || baseEntityList.isEmpty()) ? ""
											: baseEntityList.get(0).getName();
								}
							}
							if (attr != null && !attr.equals("null")) {
								refName = attr;
							}
							createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap, edgeRowMap,
									refName, name, null);
						}

						else if (key.equalsIgnoreCase("operand"))/* for relation/expression */ {
							if (childObj != null && value.startsWith("{", 0))
								createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap,
										edgeRowMap, position, key, null);
						} else if (childObj != null) {
							String refN = childObj.optString("ref");

							JSONObject jsonObjType = new JSONObject(refN);
							childUuid = jsonObjType.optString("uuid");
							childType = jsonObjType.optString("type");

							baseEntityList = commonServiceImpl.getResolveNameByUuidandType(childUuid, childType);
							refName = (baseEntityList == null || baseEntityList.isEmpty()) ? ""
									: baseEntityList.get(0).getName();

							createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap, edgeRowMap,
									refName, name, null);
						}
				}
			} 
			else if (childObj != null && value.startsWith("{", 0)) {
				/*if (parentName != null) {
					if (parentName.equalsIgnoreCase("nodeProperties")
							|| parentName.equalsIgnoreCase("edgeProperties")) {
						continue;
					}
				}*/
				if (key.equalsIgnoreCase("ref")) {
					
					
					childUuid = childObj.optString("uuid");
					childType = childObj.optString("type");

					if (!childType.equals(MetaType.simple.toString())) {
						/*
						 * baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(childType, null,
						 * null, null, null, null, null, childUuid, null, null); refName =
						 * (baseEntityList == null || baseEntityList.isEmpty()) ? "" :
						 * baseEntityList.get(0).getName();
						 */

						baseEntityList = commonServiceImpl.getResolveNameByUuidandType(childUuid, childType);
						refName = (baseEntityList == null || baseEntityList.isEmpty()) ? ""
								: baseEntityList.get(0).getName();
					} else {
						break;
						// refName = MetaType.simple.toString();
					}
					if (StringUtils.isBlank(refName)) {
						refName = childType;
					}
					if (StringUtils.isNotBlank(jsonObject.optString("attrId"))) {

						if (StringUtils.isNotBlank(jsonObject.optString("attrName"))
								&& jsonObject.optString("attrName") != null) {
							name = refName + "_" + jsonObject.getString("attrName");
						} else {
							name = refName + "_" + jsonObject.getString("attrId");
						}
					} /*
						 * else if (StringUtils.isNotBlank(jsonObject.optString("attributeId"))) { if
						 * (StringUtils.isNotBlank(jsonObject.optString("attributeName"))) { name =
						 * refName + "_" + jsonObject.getString("attributeName"); } else { name =
						 * refName + "_" + jsonObject.getString("attributeId"); } }
						 */else {
						name = refName;
					}
					String version = null;
					if (baseEntityList.isEmpty() == false) {
						version = baseEntityList.get(0).getVersion();
					}
					name = refName;
					srcEdgeMetaRef = new GraphMetaIdentifierHolder();
					srcEdgeMetaIdenRef = new GraphMetaIdentifier();

					dstEdgeMetaRef = new GraphMetaIdentifierHolder();
					dstEdgeMetaIdenRef = new GraphMetaIdentifier();
					srcEdgeMetaIdenRef.setType(srcVertex.getNodeType());
					srcEdgeMetaIdenRef.setUuid(srcVertex.getUuid());
					srcEdgeMetaIdenRef.setName(srcVertex.getName());
					srcEdgeMetaRef.setRef(srcEdgeMetaIdenRef);

					dstEdgeMetaIdenRef.setType(childType);
					dstEdgeMetaIdenRef.setUuid(childUuid);
					dstEdgeMetaIdenRef.setName(name);
					dstEdgeMetaRef.setRef(dstEdgeMetaIdenRef);

					edgeRow = createEdge(srcVertex.getUuid(), childUuid + "_" + version, parentName,
							new HashMap<String, Row>(), srcEdgeMetaRef, dstEdgeMetaRef);
					totalEdgeList.add(edgeRow);
					edgeRowMap.put(srcVertex.getUuid() + "_" + childUuid + "_" + version + "_" + name, edgeRow);
					edge = new Edge(srcVertex.getUuid(), childUuid + "_" + version, parentName, srcEdgeMetaRef,
							dstEdgeMetaRef);
					saveEdge(edge);
					graphMeta.setUuid(childUuid);
					graphMeta.setVersion(version);
					graphMeta.setType(childObj.optString("type"));
					graphMeta.setName(name);
					graphMetaIdentifierHolder.setRef(graphMeta);
					MetaIdentifierHolder appInfo= new MetaIdentifierHolder();
				    List<MetaIdentifierHolder> appInfoList = new ArrayList<MetaIdentifierHolder>();
					if (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null) {
						appInfo = (MetaIdentifierHolder) securityServiceImpl.getAppInfo();

						appInfoList.add(appInfo);
					}
					
					vertexRow = createVertex(childUuid + "_" + version, "", name, parentName, new Date().toString(),
							"Y", graphMetaIdentifierHolder,appInfoList);
					totalVertexList.add(vertexRow);
					verticesRowMap.put(
							childUuid.concat("_").concat(name).concat("_").concat(parentName).concat("_").concat("Y"),
							vertexRow);
					vertex = new Vertex(childUuid + "_" + version, "", name, parentName, null, null,
							new Date().toString(), "Y", graphMetaIdentifierHolder,appInfoList);
					saveVertex(vertex);
					continue;
				}

				createVnE(childObj, srcVertex, totalVertexList, totalEdgeList, verticesRowMap, edgeRowMap, position,
						key, null);
			}
		}
	}

	/**********************************
	 * GraphFrame - START
	 **********************************/

	/**
	 * 
	 * @param uuid
	 * @param version
	 * @param execParams
	 * @param runMode
	 * @return
	 * @throws Exception
	 */
	public BaseExec create(String uuid, String version, ExecParams execParams, RunMode runMode) throws Exception {
		Graphpod graphPod = (Graphpod) commonServiceImpl.getOneByUuidAndVersion(uuid, version,
				MetaType.graphpod.toString());
		GraphExec graphExec = (GraphExec) commonServiceImpl.createExec(MetaType.graphExec,
				new MetaIdentifier(MetaType.graphpod, uuid, version), runMode);
		commonServiceImpl.save(MetaType.graphExec.toString(), graphExec);
		return create(graphExec, execParams, runMode);
	}

	/**
	 * 
	 * @param baseExec
	 * @param execParams
	 * @param runMode
	 * @return
	 * @throws Exception
	 */
	public BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		logger.info("Inside GraphServiceImpl.create ");

		GraphExec graphExec = (GraphExec) baseExec;
		List<Status> statusList = null;
		if (graphExec == null) {
			logger.info(" Nothing to create exec upon. Aborting ... ");
			return null;
		}
		statusList = graphExec.getStatusList();		
		if ( Helper.getLatestStatus(statusList) != null 
				&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.RUNNING, new Date()))
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.COMPLETED, new Date()))
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.TERMINATING, new Date()))
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.PAUSE, new Date())) 
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.READY, new Date())))) {
			logger.info(
					" This process is RUNNING or has been COMPLETED previously or is TERMINATING or is On Hold. Hence it cannot be rerun. ");
			logger.info(" If status is in READY state then no need to start and parse again. ");
			return graphExec;
		}
		logger.info(" Set PENDING status");
		synchronized (graphExec.getUuid()) {
			graphExec = (GraphExec) commonServiceImpl.setMetaStatus(graphExec, MetaType.graphExec,
					Status.Stage.PENDING);
		}
		logger.info(" After Set PENDING status");
		return graphExec;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// Get the exec
		try {
			baseExec = (GraphExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.graphExec,
					Status.Stage.RUNNING);

			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			String graphExecKey = exec.createGraphFrame((GraphExec) baseExec, null);
			DataStore ds = new DataStore();
			ds.setMetaId(new MetaIdentifierHolder(new MetaIdentifier(baseExec.getDependsOn().getRef().getType(),
					baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion())));
			ds.setExecId(new MetaIdentifierHolder(
					new MetaIdentifier(MetaType.graphExec, baseExec.getUuid(), baseExec.getVersion())));
			dataStoreServiceImpl.save(ds);
			baseExec = (GraphExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.graphExec,
					Status.Stage.COMPLETED);
			return graphExecKey;
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			baseExec = (GraphExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.graphExec, Status.Stage.FAILED);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.graphExec, baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Graphpod execution FAILED." ,dependsOn);
			throw new RuntimeException(e);
		}
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		synchronized (baseExec.getUuid()) {
			baseExec = (BaseExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.graphExec, Status.Stage.STARTING);
		}
		baseExec = graphOperator.parse(baseExec, execParams, runMode);
		synchronized (baseExec.getUuid()) {
			baseExec = (BaseExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.graphExec, Status.Stage.READY);
		}
		return baseExec;
	}

	/**
	 * 
	 * @param uuid
	 * @param version
	 * @param degree
	 * @param filterId
	 * @return
	 * @throws Exception
	 */
	/**
	 * @param uuid
	 * @param version
	 * @param degree
	 * @param filterId
	 * @param nodeType
	 * @param execParams
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public Map<String, List<GraphpodResult>> getGraphResults(String uuid, String version, String degree,
			String filterId, String nodeType,ExecParams execParams) throws Exception {

		GraphExec graphExec = (GraphExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version,
				MetaType.graphExec.toString());
		Graphpod graphpod = (Graphpod) commonServiceImpl.getOneByUuidAndVersion(
				graphExec.getDependsOn().getRef().getUuid(), graphExec.getDependsOn().getRef().getVersion(),
				MetaType.graphpod.toString());
		String graphExecKey = null;
		Boolean createGraph = Boolean.FALSE;
		// Get the datastore. If there is no existing datastore then create graph
		DataStore ds = dataStoreServiceImpl.findLatestByMeta(graphpod.getUuid(), graphpod.getVersion());
		// DataStore ds = dataStoreServiceImpl.findLatestByMeta(uuid, version);

		if (ds != null) {
			graphExecKey = ds.getMetaId().getRef().getUuid() + "_" + ds.getMetaId().getRef().getVersion() + "_"
					+ ds.getExecId().getRef().getVersion();
			if (!graphpodMap.containsKey(graphExecKey)) {
				createGraph = Boolean.TRUE;
			}
		} else {
			createGraph = Boolean.TRUE;
		}
		if (createGraph) {
			// Create graph
			RunMode runMode = RunMode.ONLINE;
			ExecParams execParamss = new ExecParams();
			BaseExec baseExec = create(graphExec.getDependsOn().getRef().getUuid(),
					graphExec.getDependsOn().getRef().getVersion(), execParamss, runMode);
			baseExec = parse(baseExec, execParams, runMode);
			graphExecKey = execute(baseExec, execParams, runMode);
		}
		// Get the graphFrame and parse
		GraphFrame graph = (GraphFrame) graphpodMap.get(graphExecKey);
		String defaultName = String.format("%s_%s_%S", graphpod.getUuid().replaceAll("-", "_"), graphpod.getVersion(), graphExec.getVersion());
		sparkExecutor.registerTempTable(graph.edges(), defaultName.concat("_edge"));
		sparkExecutor.registerTempTable(graph.vertices(), defaultName.concat("_node"));
		graph.edges().show(false);
		graph.vertices().show(false);

	/*	Dataset<Row> edgeProperties=graph.edges().toJSON().select("edgeProperties");
		edgeProperties.createTempView("V1").show();
		List<Row> al=edgeProperties.collectAsList();
		int size=al.size();
		List<java.util.Map<String, String>> nodeproper = new ArrayList<>();
		java.util.Map<String, String> object1 = new HashMap<String, String>();
	      for (Row value : al) { 	
	  	    	  System.out.println( value.toString());
	    	 
	           
	    	  String[] words=value.toString().substring(2, value.toString().lastIndexOf("}")).split(",");
	    	  System.out.println(words[0]+""+words[1]);
	    	  for(String val : words) {
	    		 String key_value[]= val.split(":");
		           object1.put(key_value[0], key_value[1]);
		           nodeproper.add(object1);
		       }	    	 
	      }
	      for (java.util.Map<String, String> map : nodeproper) {
				for (java.util.Map.Entry<String, String> entry : map.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					System.out.println(key+":"+value);
					}
					
				}
		*/

		Dataset<Row> motifs = null;
		List<Map<String, Object>> vertexData = new ArrayList<>();
		List<Map<String, Object>> edgesData = new ArrayList<>();
		List<Map<String, Object>> data = new ArrayList<>();
		if (degree.equalsIgnoreCase("1")) {
			motifs = graph.find("(Object)-[relationwithChild]->(Child)");
		} else if (degree.equalsIgnoreCase("2")) {
			motifs = graph.find("(Object)-[relationwithChild]->(Child);(Child)-[relationwithSubChild]->(SubChild)");
		} else if (degree.equalsIgnoreCase("abc")) {
			motifs = graph.find("(Child)-[relationwithSubChild]->(SubChild);(Object)-[relationwithChild]->(Child)");
		} else if (degree.equalsIgnoreCase("3")) {
			motifs = graph.find(
					"(Object)-[relationwithChild]->(Child);(Child)-[relationwithSubChild]->(SubChild);(SubChild)-[relationwithSubsubChild]->(SubsubChild)");
		} else if (degree.equalsIgnoreCase("4")) {
			motifs = graph.find("(Object)-[relationwithChild]->(Child);(Child)-[relationwithSubChild]->(SubChild);"
					+ "(SubChild)-[relationwithSubsubChild]->(SubsubChild);"
					+ "(SubsubChild)-[relationwithSubsubsubChild]->(SubsubsubChild)");
		} else if (degree.equalsIgnoreCase("-1")) {
			motifs = graph.find("(Child)-[relationwithChild]->(Object)");
		} else if (degree.equalsIgnoreCase("-2")) {
			motifs = graph.find("(Child)-[relationwithChild]->(SubChild);(SubChild)-[relationwithSubChild]->(Object)");
		}
		motifs.show(false);
		motifs = motifs
				.filter("relationwithChild.src = '" + filterId + "' or relationwithChild.dst = '" + filterId + "'");

		String[] columns = motifs.columns();
		for (Row row : motifs.collectAsList()) {
			java.util.Map<String, Object> object = new HashMap<String, Object>();
			for (String column : columns) {
				object.put(column, row.getAs(column));
			}
			data.add(object);
		}

		// logger.info("PRINT DATA ::: ### " + data );
		for (Map<String, Object> map : data) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (key.equalsIgnoreCase("Object") || key.equalsIgnoreCase("Child") || key.equalsIgnoreCase("SubChild")
						|| key.equalsIgnoreCase("SubsubChild") || key.equalsIgnoreCase("SubsubsubChild")) {
					Map<String, Object> vertexData1 = new HashMap<>();
					vertexData1.put(key, value);
					vertexData.add(vertexData1);
				}
				if (key.equalsIgnoreCase("relationwithChild") || key.equalsIgnoreCase("relationwithSubChild")
						|| key.equalsIgnoreCase("relationwithSubsubChild")
						|| key.equalsIgnoreCase("relationwithSubsubsubChild")) {
					Map<String, Object> edgesData1 = new HashMap<>();
					edgesData1.put(key, value);
					edgesData.add(edgesData1);
				}
			}
		}

		Set<String> vertexSet = new HashSet<>();
		Set<String> edgeSet = new HashSet<>();

		for (Map<String, Object> map : vertexData) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				Object value = entry.getValue();
				vertexSet.add(value.toString());
			}
		}

		for (Map<String, Object> map : edgesData) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				Object value = entry.getValue();
				edgeSet.add(value.toString());
			}
		}

		logger.info("Printing vertex >>>> ");
		for (String value : vertexSet) {
			logger.info("value : " + value.toString());
		}

		logger.info("Printing edge >>>> ");
		for (String value : edgeSet) {
			logger.info("value : " + value.toString());
		}

		List<Map<String, Object>> graphVertex = new ArrayList<>();
		List<Map<String, Object>> graphEdge = new ArrayList<>();
		List<String> edgeSelectedSourceList = new ArrayList<>();
		List<String> edgeSourceList = new ArrayList<>();
		StringBuilder nodefilter = new StringBuilder();
		StringBuilder edgefilter = new StringBuilder();
		if (execParams != null) {
			GraphFilter graphFilter = execParams.getGraphFilter();
			if (graphFilter.getNodeFilter().size() > 0) {
				for (GraphFilter.NodeFilter nodeFilter : graphFilter.getNodeFilter()) {
					String logicalOperator = nodeFilter.getLogicalOperator();
					String operator = nodeFilter.getOperator();
					Property operand = nodeFilter.getOperand();
					nodefilter.append("  " + logicalOperator + "  " + "get_json_object(nodeProperties,'$."
							+ operand.getPropertyName() + "')  " + operator + "  " + operand.getPropertyValue());
				}
			}

			if (graphFilter.getEdgeFilter().size() > 0) {
				for (GraphFilter.EdgeFilter edgeFilter : graphFilter.getEdgeFilter()) {
					String logicalOperator = edgeFilter.getLogicalOperator();
					String operator = edgeFilter.getOperator();
					Property operand = edgeFilter.getOperand();
					edgeSelectedSourceList.add(edgeFilter.getSource());
					edgefilter.append(" " + logicalOperator + "(edgeSource = '" + edgeFilter.getSource()
							+ "'  and  get_json_object(edgeProperties,'$." + operand.getPropertyName() + "')  "
							+ operator + "  " + operand.getPropertyValue() + ")");
				}
			}
		}
		String restSource = null;
		StringBuilder sourceName = new StringBuilder();

		for (GraphEdge edgename : graphpod.getEdgeInfo()) {
			edgeSourceList.add(edgename.getEdgeSource().getRef().getName());
		}
		ArrayList<String> otherEdgeSourceList = new ArrayList<String>();
		for (String temp : edgeSourceList)
			otherEdgeSourceList.add(!edgeSelectedSourceList.contains(temp) ? temp : "");

		otherEdgeSourceList.removeAll(Collections.singleton(""));
		System.out.println(otherEdgeSourceList);
		// Country IN ('Germany', 'France', 'UK');
		int count2 = 0;
		for (String sour : otherEdgeSourceList) {
			if (count2 == 0) {
				sourceName.append("'" + sour + "'");
			} else {
				sourceName.append(",'" + sour + "'");
			}
			count2++;
		}
		if (otherEdgeSourceList.isEmpty() != true)
			restSource = "(  edgeSource IN (" + sourceName + ") ) OR";
		StructField[] structFields=motifs.schema().fields();
	for(StructField field:structFields) {
		System.out.println(field);
	}
	Dataset<Row> edge_dataset = null ;
	
		// sourceName.append(" ( edgeSource = '"+source1+"' and" );
	if(graphpod.getEdgeInfo().get(0).getHighlightInfo()!=null ) {
		 edge_dataset = motifs.select("relationwithChild.src", "relationwithChild.dst",
				"relationwithChild.edgeName", "relationwithChild.edgeType", "relationwithChild.edgeProperties",
				"relationwithChild.edgeIndex", "relationwithChild.eHpropertyId", "relationwithChild.edgeSource")
				.distinct();
		edge_dataset.show(false);
	}else {
		edge_dataset = motifs.select("relationwithChild.src", "relationwithChild.dst",
				"relationwithChild.edgeName", "relationwithChild.edgeType", "relationwithChild.edgeProperties",
				"relationwithChild.edgeIndex", "relationwithChild.edgeSource")
				.distinct();
		edge_dataset.show(false);
	}
		if (edgefilter.length() > 0 && restSource != null)
			edge_dataset = edge_dataset.filter(restSource + "(" + edgefilter.toString() + ")");
		else if (edgefilter.length() > 0)
			edge_dataset = edge_dataset.filter("(" + edgefilter.toString() + ")");

		edge_dataset.show(false);
		Dataset<Row> node_dataset = null;
		if(graphpod.getNodeInfo().get(0).getHighlightInfo()!=null && graphpod.getNodeInfo().get(0).getNodeBackgroundInfo()!=null  ) {
			node_dataset = motifs
				.select("Object.id", "Object.nodeName", "Object.nodeType", "Object.nodeIcon", "Object.nodeProperties",
						"Object.nBPropertyId", "Object.nHpropertyId", "Object.type", "Object.nodeIndex","Object.nodeSize")
				.union(motifs.select("Child.id", "Child.nodeName", "Child.nodeType", "Child.nodeIcon",
						"Child.nodeProperties", "Child.nBPropertyId", "Child.nHpropertyId", "Child.type",
						"Child.nodeIndex","Child.nodeSize"))
				.distinct();
		}else if(graphpod.getNodeInfo().get(0).getHighlightInfo()!=null && graphpod.getNodeInfo().get(0).getNodeBackgroundInfo()==null)
		{
			node_dataset = motifs
					.select("Object.id", "Object.nodeName", "Object.nodeType", "Object.nodeIcon", "Object.nodeProperties",
						 "Object.nHpropertyId", "Object.type", "Object.nodeIndex","Object.nodeSize")
					.union(motifs.select("Child.id", "Child.nodeName", "Child.nodeType", "Child.nodeIcon",
							"Child.nodeProperties", "Child.type",
							"Child.nodeIndex","Child.nodeSize"))
					.distinct();
		}else if(graphpod.getNodeInfo().get(0).getHighlightInfo()==null && graphpod.getNodeInfo().get(0).getNodeBackgroundInfo()!=null)
		{
			node_dataset = motifs
					.select("Object.id", "Object.nodeName", "Object.nodeType", "Object.nodeIcon", "Object.nodeProperties",
							"Object.nBPropertyId", "Object.nodeIndex","Object.nodeSize")
					.union(motifs.select("Child.id", "Child.nodeName", "Child.nodeType", "Child.nodeIcon",
							"Child.nodeProperties", "Child.nBPropertyId",
							"Child.nodeIndex","Child.nodeSize"))
					.distinct();
		}else {
			
			
			node_dataset = motifs
					.select("Object.id", "Object.nodeName", "Object.nodeType", "Object.nodeIcon", "Object.nodeProperties",
							 "Object.nodeIndex","Object.nodeSize")
					.union(motifs.select("Child.id", "Child.nodeName", "Child.nodeType", "Child.nodeIcon",
							"Child.nodeProperties",
							"Child.nodeIndex","Child.nodeSize"))
					.distinct();
		}
		System.out.println("############   Nodefilter  Filter  String   #####" + nodefilter.toString());
		node_dataset.show(false);
		
		if (nodefilter.length() > 0)
			node_dataset = node_dataset.filter(nodefilter.toString());

		node_dataset.show(false);

		Dataset<Row> result_datset = edge_dataset.join(node_dataset,
				edge_dataset.col("src").equalTo(node_dataset.col("id")));
		result_datset.show(false);
		if (execParams != null)
			// result_datset = result_datset.filter(sb.toString());
			result_datset.show(false);
		// Process and get the desired results
		List<GraphpodResult> result = new ArrayList<>();

		if (!result_datset.collectAsList().isEmpty()) {
			Row[] rows = (Row[]) result_datset.head(Integer.parseInt("" + result_datset.collectAsList().size()));
			String[] resultDatesetColumns = result_datset.columns();
			String[] resultDatasetValue = new String[resultDatesetColumns.length];

			for (Row row : rows) {
				Map<String, String> source = new HashMap<>();
				Map<String, String> target = new HashMap<>();
				int count = 0;
				for (String edgecloumn : resultDatesetColumns) {
					String value1 = row.getAs(edgecloumn).toString();
					resultDatasetValue[count] = value1;
					count++;
				}

				String edge_name = row.getAs(resultDatesetColumns[2]);
				String edge_type = row.getAs(resultDatesetColumns[3]);
				String edge_properties = row.getAs(resultDatesetColumns[4]);
				String edge_index = row.getAs(resultDatesetColumns[5]).toString();
				String edge_propertyID = row.getAs(resultDatesetColumns[6]).toString();
				String relation = null;
				if (edge_properties.contains(","))
					relation = edge_properties.substring(edge_properties.indexOf(':') + 1,
							edge_properties.indexOf(','));
				else
					relation = edge_properties.substring(edge_properties.indexOf(':') + 1,
							edge_properties.indexOf('}'));

				Dataset<Row> srcVertexDf = graph.vertices().filter("id = '" + resultDatasetValue[0] + "'");
				String[] vertexColumns = srcVertexDf.columns();
				Row[] srcrows = (Row[]) srcVertexDf.head(Integer.parseInt("" + srcVertexDf.count()));

				for (Row srcrow : srcrows) {
					for (String cloumn : vertexColumns) {
						if (cloumn.equalsIgnoreCase("nodeName")) {
							String value1 = srcrow.getAs(cloumn).toString();
							source.put("label", value1);
						}
						String value1 = srcrow.getAs(cloumn).toString();
						source.put(cloumn, value1);
					}
				}

				Dataset<Row> dstVertexDf = graph.vertices().filter("id = '" + resultDatasetValue[1] + "'");

				Row[] dstrows = (Row[]) dstVertexDf.head(Integer.parseInt("" + dstVertexDf.count()));
				if (dstVertexDf.count() > 0) {
					for (Row dstrow : dstrows) {
						for (String cloumn : vertexColumns) {

							if (cloumn.equalsIgnoreCase("nodeName")) {
								String value1 = dstrow.getAs(cloumn).toString();
								target.put("label", value1);
							}
							if (dstrow.anyNull() == true) {
								System.out.println("null");
							}

							if (dstrow.anyNull() == false) {
								String value1 = dstrow.getAs(cloumn).toString();
								target.put(cloumn, value1);
							} else {
								continue;
							}

						}
					}
					GraphpodResult graphpodresult = new GraphpodResult(source, target, relation, edge_name, edge_type,
							edge_properties, edge_index, edge_propertyID.toString());
					result.add(graphpodresult);
				} else {
					GraphpodResult graphpodresult = new GraphpodResult(source, null, null, null, null, null, null,
							null);
					result.add(graphpodresult);
				}

			}
		}
		Map<String, List<GraphpodResult>> edgeMap = new HashMap<>();
		edgeMap.put("edges", result);
		// String reslt = mapper.writeValueAsString(result);

		return edgeMap;
	}
	/**********************************
	 * GraphFrame - END
	 **********************************/

	
	public boolean isRefreshed()  {
		
		
		return true;
	}
	
	
	public void setStatus (String type, String uuid, String version,String status){
		if(status.toLowerCase().equalsIgnoreCase(Status.Stage.PAUSE.toString().toLowerCase())){
			super.PAUSE(uuid, version, Helper.getMetaType(type));
		}
		else if(status.toLowerCase().equalsIgnoreCase(Status.Stage.RESUME.toString().toLowerCase())){
			super.RESUME(uuid,version, Helper.getMetaType(type));
		}
		else if(status.toLowerCase().equalsIgnoreCase(Status.Stage.KILLED.toString().toLowerCase())){
		      kill(uuid, version,Helper.getMetaType(type));
		}
		
	}
	
	public void kill (String uuid, String version, MetaType execType) {
		BaseExec baseExec = null;
		try {
			baseExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, execType.toString());
		} catch (JsonProcessingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if (baseExec == null) {
			logger.info("GraphExec not found. Exiting...");
			return;
		}
		if (!Helper.getLatestStatus(baseExec.getStatusList()).equals(new Status(Status.Stage.RUNNING, new Date()))) {
			logger.info("Latest Status is not in RUNNING. Exiting...");
		}
		try {
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.TERMINATING);
			}
			@SuppressWarnings("unchecked")
			FutureTask<TaskHolder> futureTask = (FutureTask<TaskHolder>) taskThreadMap.get(execType+"_"+baseExec.getUuid()+"_"+baseExec.getVersion());
			if(futureTask!=null)	
			futureTask.cancel(true);
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.KILLED);
			}
		} catch (Exception e) {
			logger.info("FAILED to kill. uuid : " + uuid + " version : " + version);
			try {
				synchronized (baseExec.getUuid()) {
					commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.KILLED);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			taskThreadMap.remove(execType+"_"+baseExec.getUuid()+"_"+baseExec.getVersion());
			e.printStackTrace();
		}
	}
	
	
	public void restart(String type, String uuid, String version, ExecParams execParams, RunMode runMode)
			throws Exception {
		BaseExec baseExec = null;
		try {
			baseExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.graphExec.toString());
		} catch (JsonProcessingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}	synchronized (baseExec.getUuid()) {
			baseExec = (BaseExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.graphExec, Status.Stage.READY);
		}
		try {
			// baseExec = (GraphPod) commonServiceImpl.getOneByUuidAndVersion(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), baseExec.getDependsOn().getRef().getType().toString());
			 execute(baseExec, execParams, runMode);
		} catch (Exception e) {
			synchronized (baseExec.getUuid()) {
				try {
					commonServiceImpl.setMetaStatus(baseExec, MetaType.graphExec, Status.Stage.FAILED);
				} catch (Exception e1) {
					e1.printStackTrace();
					String message = null;
					try {
						message = e1.getMessage();
					}catch (Exception e2) {
						// TODO: handle exception
					}
					MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
					dependsOn.setRef(new MetaIdentifier(MetaType.graphExec, uuid, version));
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "GraphExec restart operation FAILED.", dependsOn);
					throw new Exception((message != null) ? message : "GraphExec restart operation FAILED.");
				}
			}
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.graphExec, uuid, version));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "GraphExec restart operation FAILED.", dependsOn);
			throw new Exception((message != null) ? message : "GraphExec restart operation FAILED.");
		}
	}
	

}
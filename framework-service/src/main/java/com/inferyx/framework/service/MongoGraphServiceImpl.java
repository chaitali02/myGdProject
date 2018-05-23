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
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IEdgeDao;
import com.inferyx.framework.dao.IVertexDao;
import com.inferyx.framework.domain.Edge;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.NodeDetail;
import com.inferyx.framework.domain.NodeDetails;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.domain.Vertex;

@Service
public class MongoGraphServiceImpl {

	@Autowired
	private MetadataServiceImpl metadataServiceImpl;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private IEdgeDao iEdgeDao;
	@Autowired
	private IVertexDao iVertexDao;

	private List<String> keywordList = new ArrayList<>();

	public MongoGraphServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	protected  Map<String, Object> getEdgeMap(Edge edge) {
		Map<String, Object> map = new HashMap<>();
		if (edge == null) {
			return null;
		}
		map.put("src", edge.getSrc());
		map.put("dst", edge.getDst());
		map.put("relationType", edge.getRelationType());
		return map;
	}

	
	protected  Map<String, Object> getVertexMap(Vertex vertex) {
		Map<String, Object> map = new HashMap<>();
		if (vertex == null) {
			return null;
		}
		//map.put("id", vertex.getUuid());
		  map.put("version", vertex.getVersion());
	///If user's name is "user" then resolve it using method "getLatestByUuidWithoutAppUuid"	  
		  if(vertex.getName().toLowerCase().equalsIgnoreCase(MetaType.user.toString().toLowerCase())) {
			  User user = null;
			try {
				user = (User) commonServiceImpl.getLatestByUuidWithoutAppUuid(vertex.getUuid(), MetaType.user.toString());
			} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException | NullPointerException
					| ParseException e) {
				e.printStackTrace();
			}
			  if(user != null) {
				  String name = user.getName();
				  map.put("name", name);
			  }else
				  map.put("name", vertex.getName());
		  }else
			  map.put("name", vertex.getName());
	///		  
		  map.put("nodeType", vertex.getNodeType());
		  map.put("dataType", vertex.getDataType());
		  map.put("desc", vertex.getDesc());
		  map.put("createdOn", vertex.getCreatedOn());
		  map.put("parent", vertex.getParent());
		  map.put("active", vertex.getActive());
		  MetaIdentifier mi = new MetaIdentifier();
		  mi.setType(Helper.getMetaType(vertex.getNodeType()));	  
		  String[] tokens =vertex.getUuid().split("_");
		  mi.setUuid(tokens[0]);
		  mi.setVersion(vertex.getVersion());	  
		  map.put("metaRef",mi);
		return map;
	}

	public String getGraphJson(String uuid, String version, String degree) {
		NodeDetails nodeDetails = new NodeDetails();
		String result = null;
		List<Map<String,Object>> graphVertex = new ArrayList<>();
		List<Map<String,Object>> graphEdge = new ArrayList<>();
		Map<String, Edge> edgeMap = new HashMap<>();
		Map<String, Vertex> vertexMap = new HashMap<>();
		List<Edge> edgeList = null;
		List<Vertex> vertexList = null;
		List<String> uuidList = null;
		edgeList = iEdgeDao.findAllBySrc(uuid);

		// Get all dsts from edgeList
		if (edgeList != null) {
			uuidList = new ArrayList<>();
			for (Edge edge : edgeList) {
				edgeMap.put(edge.getSrc()+"_"+edge.getDst(), edge);
			}
			for (String edgeKey : edgeMap.keySet()) {
				Edge edge = edgeMap.get(edgeKey);
				uuidList.add(edge.getDst());
				graphEdge.add(getEdgeMap(edge));
				
			}
		}
		
		uuidList.add(uuid);

		vertexList = iVertexDao.findAllByUuidContaining(uuidList);
		if (vertexList != null) {
			for (Vertex vertex : vertexList) {
				vertexMap.put(vertex.getUuid(), vertex);
			}
			for (String vertexKey : vertexMap.keySet()) {
				Vertex vertex = vertexMap.get(vertexKey);
				graphVertex.add(getVertexMap(vertex));
			}
		} 
		
		nodeDetails.setNodes(graphVertex);
		nodeDetails.setLinks(graphEdge);
		nodeDetails.setJsonName("graph");
	
		try {
		ObjectWriter writer = new ObjectMapper().writer()
		.withDefaultPrettyPrinter();
		result = writer.writeValueAsString(nodeDetails);
		} catch (IOException e) {
		e.printStackTrace();
		}
		return result;

	}
	
	
	
	@SuppressWarnings("null")
	public String getTreeGraphJson(String uuid, String version, String degree) {
		NodeDetail nodeDetail = new NodeDetail();
		String result = null;
		List<Map<String,Object>> graphVertex = new ArrayList<>();
		List<Map<String,Object>> graphEdge = new ArrayList<>();
		Map<String, Edge> edgeMap = new HashMap<>();
		Map<String, Vertex> vertexMap = new HashMap<>();
		List<Edge> edgeList = null;
		List<Vertex> vertexList = null;

		Vertex parentvertex = null;
		List<String> uuidList = null;
		edgeList = iEdgeDao.findAllBySrc(uuid);

		// Get all dsts from edgeList
		if (edgeList != null) {
		uuidList = new ArrayList<>();
		for (Edge edge : edgeList) {
		edgeMap.put(edge.getSrc()+"_"+edge.getDst(), edge);
		}
		for (String edgeKey : edgeMap.keySet()) {
		Edge edge = edgeMap.get(edgeKey);
		uuidList.add(edge.getDst());
		graphEdge.add(getEdgeMap(edge));

		}
		}

		uuidList.add(uuid);
		parentvertex=iVertexDao.findOneByUuid(uuid);

		vertexList = iVertexDao.findAllByUuidContaining(uuidList);
		if (vertexList != null) {

		for (Vertex vertex : vertexList) {

		vertexMap.put(vertex.getUuid(), vertex);
		}
		for (String vertexKey : vertexMap.keySet()) {
		Vertex vertex = vertexMap.get(vertexKey);

		if(!vertex.getUuid().equals(uuid)) {
		//vertex.setParent(parentvertex.getName());
		Map<String, Object> mapresult=getVertexMap(vertex);
	//	mapresult.put("id",mapresult.get("id")+parentvertex.getUuid());
		graphVertex.add(mapresult);
		}
		}
		} 
		nodeDetail.setName(parentvertex.getName());
		//nodeDetail.setParent("null");
		nodeDetail.setActive(parentvertex.getActive());
		nodeDetail.setCreatedOn(parentvertex.getCreatedOn());
		String id=parentvertex.getUuid()+parentvertex.getName();
	//	nodeDetail.setId(id);
		nodeDetail.setNodeType(parentvertex.getNodeType());
		nodeDetail.setDataType(parentvertex.getDataType());
		nodeDetail.setVersion(parentvertex.getVersion());

		nodeDetail.setChildren(graphVertex);
		// nodeDetail.setParent(parentvertex);
		//nodeDetails.setLinks(graphEdge);
		//nodeDetail.setJsonName("graph");

		try {
		ObjectWriter writer = new ObjectMapper().writer()
		.withDefaultPrettyPrinter();
		result = writer.writeValueAsString(nodeDetail);

		} catch (IOException e) {
		e.printStackTrace();
		}
		return result;
	}

}

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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.dao.IEdgeDao;
import com.inferyx.framework.dao.IVertexDao;
import com.inferyx.framework.domain.Edge;
import com.inferyx.framework.domain.GraphMetaIdentifier;
import com.inferyx.framework.domain.GraphMetaIdentifierHolder;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.NodeDetail;
import com.inferyx.framework.domain.NodeDetails;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.domain.Vertex;

@Service
public class MongoGraphServiceImpl {

	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private IEdgeDao iEdgeDao;
	@Autowired
	private IVertexDao iVertexDao;

	public MongoGraphServiceImpl() {
		// TODO Auto-generated constructor stub
	}

  	protected Map<String, Object> getEdgeMap(Edge edge) {
		Map<String, Object> map = new LinkedHashMap<>();
		if (edge == null) {
			return null;
		}
		map.put("src", edge.getSrc());
		/*
		 * map.put("src", edge.getSrcMetaRef().getRef().getUuid());
		 */
		map.put("dst", edge.getDst());
		map.put("relationType", edge.getRelationType());
		return map;
	}

	protected Map<String, Object> getVertexMap(Vertex vertex,String degree) {
		Map<String, Object> map = new LinkedHashMap<>();
		if (vertex == null) {
			return null;
		}
		// map.put("id", vertex.getUuid());
		map.put("version", vertex.getVersion());
		/// If user's name is "user" then resolve it using method
		/// "getLatestByUuidWithoutAppUuid"
		if (vertex.getName().toLowerCase().equalsIgnoreCase(MetaType.user.toString().toLowerCase())) {
			User user = null;
			try {
				user = (User) commonServiceImpl.getLatestByUuidWithoutAppUuid(vertex.getUuid(),
						MetaType.user.toString());
			} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException | NullPointerException
					| ParseException e) {
				e.printStackTrace();
			}
			if (user != null) {
				String name = user.getName();
				map.put("name", name);
			} else
				map.put("name", vertex.getName());
		} else
			map.put("name", vertex.getName());
		
		map.put("nodeType", vertex.getNodeType());
		map.put("dataType", vertex.getDataType());
		map.put("desc", vertex.getDesc());
		map.put("createdOn", vertex.getCreatedOn());
		map.put("parent", vertex.getParent());
		map.put("active", vertex.getActive());
		GraphMetaIdentifierHolder graphmetaholder = new GraphMetaIdentifierHolder();
		GraphMetaIdentifier graphmi = new GraphMetaIdentifier();
		if (degree.equalsIgnoreCase("1")) {
			if (vertex.getGraphMetaHolder() != null) {
				graphmi.setVersion(vertex.getGraphMetaHolder().getRef().getVersion());
				graphmi.setType(vertex.getGraphMetaHolder().getRef().getType());
				graphmi.setUuid(vertex.getGraphMetaHolder().getRef().getUuid());
			} else {
				graphmi.setType(vertex.getNodeType());

				String[] tokens = vertex.getUuid().split("_");
				graphmi.setUuid(tokens[0]);
				graphmi.setVersion(vertex.getVersion());
			}
			// graphmi.setName(vertex.getName());
			graphmetaholder.setRef(graphmi);
			/*
			 * if(graphmetaholder.equals(null) ) { map.put("metaRef",graphmetaholder); }
			 */
		} else {
			if (vertex.getGraphMetaHolder() != null) {
				graphmi.setVersion(vertex.getVersion());
				graphmi.setType(vertex.getGraphMetaHolder().getRef().getType());
				graphmi.setUuid(vertex.getUuid());
				graphmi.setVersion(vertex.getVersion());
			} else {
				graphmi.setType(vertex.getNodeType());

				String[] tokens = vertex.getUuid().split("_");
				graphmi.setUuid(tokens[0]);
				graphmi.setVersion(vertex.getVersion());
			}
			// graphmi.setName(vertex.getName());
			graphmetaholder.setRef(graphmi);
		}
		map.put("metaRef", graphmetaholder);

		return map;
	}

	public String getGraphJson(String uuid, String version, String degree) {
		NodeDetails nodeDetails = new NodeDetails();
		String result = null;
		List<Map<String, Object>> graphVertex = new ArrayList<>();
		List<Map<String, Object>> graphEdge = new ArrayList<>();
		Map<String, Edge> edgeMap = new LinkedHashMap<>();
		Map<String, Vertex> vertexMap = new LinkedHashMap<>();
		List<Edge> edgeList = null;
		List<Vertex> vertexList = null;
		List<String> uuidList = null;
		edgeList = iEdgeDao.findAllBySrc(uuid);

		// Get all dsts from edgeList
		if (edgeList != null) {
			uuidList = new ArrayList<>();
			for (Edge edge : edgeList) {
				edgeMap.put(edge.getSrc() + "_" + edge.getDst(), edge);
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
				graphVertex.add(getVertexMap(vertex,degree));
			}
		}

		nodeDetails.setNodes(graphVertex);
		nodeDetails.setLinks(graphEdge);
		nodeDetails.setJsonName("graph");

		try {
			ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
			result = writer.writeValueAsString(nodeDetails);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;

	}

	
	
	@SuppressWarnings({ "unchecked", "null", "unused" })
	public String getTreeGraphJson(String uuid, String version, String degree) {
		NodeDetail nodeDetail = new NodeDetail();
		String result = null;
		List<Map<String, Object>> graphVertex = new ArrayList<>();
		List<Map<String, Object>> graphEdge = new ArrayList<>();
		Map<String, Edge> edgeMap = new HashMap<>();
		Map<String, Vertex> vertexMap = new HashMap<>();
		Map<String, Vertex> uniqueVertexMap = new HashMap<>();
		Map<String, Edge> uniqueEdgeList = new HashMap<>();

		List<Edge> edgeList = null;
		List<Vertex> vertexList = null;
		List<Vertex> uniqueVertexList =new ArrayList<Vertex>();
		List<Edge> edgeList1 =new ArrayList<Edge>();
		Vertex parentvertex = null;
		List<String> uuidList = null;
		List<String> nodetype = null;
		if (degree.equals("1")) {
			if (!version.equalsIgnoreCase("0")) {
				edgeList = iEdgeDao.findAllBySrc(uuid + "_" + version);
			} else {
				edgeList = iEdgeDao.findAllBySrc(uuid);
			} // Get all dsts from edgeList
			if (edgeList != null) {
				uuidList = new ArrayList<>();
				nodetype = new ArrayList<>();
				for (Edge edge : edgeList) {
					edgeMap.put(edge.getSrc() + "_" + edge.getDst() + "_" + edge.getRelationType(), edge);
				}
				for (String edgeKey : edgeMap.keySet()) {
					Edge edge = edgeMap.get(edgeKey);
					uuidList.add(edge.getDst());
					nodetype.add(edge.getRelationType());
					graphEdge.add(getEdgeMap(edge));
				}
			}
		} else if (degree.equals("-1")) {

			if (!version.equalsIgnoreCase("0")) {
				edgeList = iEdgeDao.findAllByDst(uuid + "_" + version);

				for (Edge edge : edgeList) {
					uniqueEdgeList.put(edge.getDst() + "_" + edge.getSrc() + "_" + edge.getRelationType(), edge);
				}
				edgeList1.addAll(uniqueEdgeList.values());

			} else {
				edgeList1 = iEdgeDao.findAllByDst(uuid);
			}
			// Get all srcs from edgeList
			if (edgeList1 != null) {
				uuidList = new ArrayList<>();
				nodetype = new ArrayList<>();
				for (Edge edge : edgeList1) {
					edgeMap.put(edge.getDst() + "_" + edge.getSrc() + "_" + edge.getRelationType(), edge);
				}
				for (String edgeKey : edgeMap.keySet()) {
					Edge edge = edgeMap.get(edgeKey);
					String srcUuid = edge.getSrc();
					GraphMetaIdentifierHolder srcMetaRef = edge.getSrcMetaRef();
					uuidList.add(edge.getSrc());
					nodetype.add(edge.getSrcMetaRef().getRef().getType());
					edge.setSrc(edge.getDst());
					edge.setDst(srcUuid);
					edge.setSrcMetaRef(edge.getDstMetaRef());
					edge.setDstMetaRef(srcMetaRef);
					graphEdge.add(getEdgeMap(edge));
				}
			}
		}

		// uuidList.add(uuid+"_"+version);
		if (!version.equalsIgnoreCase("0")) {
			parentvertex = iVertexDao.findOneByUuid(uuid + "_" + version);
		} else {
			parentvertex = iVertexDao.findOneByUuid(uuid);

		}

		// vertexList = iVertexDao.findAllByUuidContaining(uuidList);
		if (degree.equalsIgnoreCase("1")) {
			vertexList = iVertexDao.findAllByUuidAndnodeTypeContaining(uuidList, nodetype);
			for (Vertex vertex : vertexList) {
				uniqueVertexMap.put(vertex.getUuid() + "_" + vertex.getNodeType(), vertex);
			}
			uniqueVertexList.addAll(uniqueVertexMap.values());
			
		} else {
			vertexList =  iVertexDao.findAllByUuidAndnodeTypeContaining(uuidList, nodetype);
			for (Vertex vertex : vertexList) {
				uniqueVertexMap.put(vertex.getUuid() + "_" + vertex.getNodeType(), vertex);
			}
			uniqueVertexList.addAll(uniqueVertexMap.values());
			
		}
		
	/*for (String uuid_nodetype : uniqVertexList.keySet()) {
			Vertex vertex = uniqVertexList.get(uuid_nodetype);
			if (vertex != null)
				vertexList1.add(vertex);
		}
		//List<Vertex> vertexList1 = vertexList.stream().distinct().collect(Collectors.toList());
*/
		if (uniqueVertexList != null) {
			for (Vertex vertex : uniqueVertexList) {
				String relationName = null;
				// if(vertex.getNodeType().equalsIgnoreCase("dependsOn") ) {
				// System.out.println("********"+relationName);
				// s }
				if (degree.equalsIgnoreCase("1")) {
					// Edge edgeRelation
					// =iEdgeDao.findOneBySrcAndDst(parentvertex.getUuid(),vertex.getUuid());
					Edge edgeRelation = iEdgeDao.findOneBySrcAndDstAndRelationType(parentvertex.getUuid(),
							vertex.getUuid(), vertex.getNodeType());
					// Added this method for same src ,dst uuid ...
					// Edge edgeRelation
					// =iEdgeDao.findOneBySrcAndDstAndRelationType(parentvertex.getUuid(),vertex.getUuid(),vertex.getNodeType());
					if (edgeRelation != null) {
						relationName = edgeRelation.getRelationType();
						vertex.setNodeType(relationName);
					}
				} else {
					Edge edgeRelation = iEdgeDao.findOneBySrcAndDstAndRelationType(parentvertex.getUuid(),
							vertex.getUuid(), vertex.getNodeType());

					// Edge edgeRelation
					// =iEdgeDao.findOneByDstAndSrcAndRelationType(vertex.getUuid(),parentvertex.getUuid(),vertex.getNodeType());
					if (edgeRelation != null) {
						relationName = edgeRelation.getRelationType();
						vertex.setNodeType(relationName);
					}
				}
				/*
				 * if(vertex.getUuid().equalsIgnoreCase(
				 * "ed47f654-2d4b-483c-971f-804ee88f092f_1488620292") ) {
				 * System.out.println(vertex.getNodeType()); }
				 * if(relationName.equalsIgnoreCase("refIntegrityCheck")
				 * ||vertex.getNodeType().equalsIgnoreCase("dependsOn") ) {
				 * System.out.println("********"+relationName); }
				 */
				vertexMap.put(vertex.getUuid() + "_" + relationName, vertex);
			}
			for (String vertexKey : vertexMap.keySet()) {
				Vertex vertex = vertexMap.get(vertexKey);
				if (!vertex.getUuid().equals(uuid)) {
					// vertex.setParent(parentvertex.getName());
					Map<String, Object> mapresult = getVertexMap(vertex, degree);
					// mapresult.put("id",mapresult.get("id")+parentvertex.getUuid());
					graphVertex.add(mapresult);
				}
			}
		}
		if (parentvertex != null) {
			nodeDetail.setName(parentvertex.getName());
			// nodeDetail.setParent("null");
			nodeDetail.setActive(parentvertex.getActive());
			nodeDetail.setCreatedOn(parentvertex.getCreatedOn());
			String id = parentvertex.getUuid() + parentvertex.getName();
			// nodeDetail.setId(id);
			nodeDetail.setNodeType(parentvertex.getNodeType());
			nodeDetail.setDataType(parentvertex.getDataType());
			nodeDetail.setVersion(parentvertex.getVersion());
			/*
			 * GraphMetaIdentifier graphMeta =new GraphMetaIdentifier();
			 * graphMeta.setUuid(parentvertex.getUuid());
			 * graphMeta.setName(parentvertex.getName());
			 * graphMeta.setType(parentvertex.getNodeType()); GraphMetaIdentifierHolder
			 * graphMetaIdentifierHolder=new GraphMetaIdentifierHolder();
			 * graphMetaIdentifierHolder.setRef(graphMeta);
			 */
			nodeDetail.setMetaRef(parentvertex.getGraphMetaHolder());
			nodeDetail.setChildren(graphVertex);
			// nodeDetail.setParent(parentvertex);
			// nodeDetails.setLinks(graphEdge);
			// nodeDetail.setJsonName("graph");
		}
		try {
			ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
			result = writer.writeValueAsString(nodeDetail);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean isRefreshed(GraphMetaIdentifierHolder graphMetaIdentifier) {		
		List<Edge> edges =iEdgeDao.findEdgeByRef(graphMetaIdentifier.getRef().getUuid(),graphMetaIdentifier.getRef().getUuid()); 		
		return !edges.isEmpty();
	}

	
}
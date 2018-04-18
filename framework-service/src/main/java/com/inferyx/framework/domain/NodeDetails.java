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
package com.inferyx.framework.domain;

import java.util.List;
import java.util.Map;



public class NodeDetails {
	private String jsonName;
	/*private String vertices;
	private String edges;*/
	private List<java.util.Map<String, Object>> nodes;
	private List<java.util.Map<String, Object>> links;
	
	public String getJsonName() {
		return jsonName;
	}
	public void setJsonName(String jsonName) {
		this.jsonName = jsonName;
	}
	public List<java.util.Map<String, Object>> getNodes() {
		return nodes;
	}
	public void setNodes(List<java.util.Map<String, Object>> nodes) {
		this.nodes = nodes;
	}
	/*public List<Vertices> getVertices() {
		return vertices;
	}
	public void setVertices(List<Vertices> vertices) {
		this.vertices = vertices;
	}*/
	public List<Map<String, Object>> getLinks() {
		return links;
	}
	public void setLinks(List<Map<String, Object>> links) {
		this.links = links;
	}
	/*public List<JSONArray> getVertices() {
		return vertices;
	}
	public void setVertices(List<JSONArray> vertices) {
		this.vertices = vertices;
	}
	public List<JSONArray> getEdges() {
		return edges;
	}
	public void setEdges(List<JSONArray> edges) {
		this.edges = edges;
	}*/
	public class Vertices{
		private String name;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getUuid() {
			return uuid;
		}
		public void setUuid(String uuid) {
			this.uuid = uuid;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		private String type;
		private String uuid;
		private String version;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
	public class Edges{
		private String srcId;
		private String targetId;
		private String relationshipType;
		public String getSrcId() {
			return srcId;
		}
		public void setSrcId(String srcId) {
			this.srcId = srcId;
		}
		public String getTargetId() {
			return targetId;
		}
		public void setTargetId(String targetId) {
			this.targetId = targetId;
		}
		public String getRelationshipType() {
			return relationshipType;
		}
		public void setRelationshipType(String relationshipType) {
			this.relationshipType = relationshipType;
		}
	}
}

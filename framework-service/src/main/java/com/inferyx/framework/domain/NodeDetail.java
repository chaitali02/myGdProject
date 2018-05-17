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



public class NodeDetail {
	
	/*private String vertices;
	private String edges;*/
	//private Vertex parent;
	private String parent;
	
	private String dataType;
	private String name;
	private String color;
	private String active;
	private String id;
	private String nodeType;
	
	private String version;
	private String createdOn;
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}

	private List<java.util.Map<String, Object>> children;
	
	
	/*public Vertex getParent() {
		return parent;
	}
	public void setParent(Vertex parent) {
		this.parent = parent;
	}*/
	

	
	public List<java.util.Map<String, Object>> getChildren() {
		return children;
	}
	public void setChildren(List<java.util.Map<String, Object>> children) {
		this.children = children;
	}


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

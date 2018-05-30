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

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vertex")
public class Vertex {
	private String parent;
	private String uuid;
	private String version;
	private String name;
	private String nodeType;
	private String dataType;
	private String desc;
	private String createdOn;
	private String active;
	private GraphMetaIdentifierHolder metaRef;
	
	public GraphMetaIdentifierHolder getGraphMetaHolder() {
		return metaRef;
	}

	public void setGraphMetaHolder(GraphMetaIdentifierHolder metaRef) {
		this.metaRef = metaRef;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "Vertex [parent=" + parent + ", uuid=" + uuid + ", version=" + version + ", name=" + name + ", nodeType="
				+ nodeType + ", dataType=" + dataType + ", desc=" + desc + ", createdOn=" + createdOn + ", active="
				+ active + ", graphMetaHolder=" + metaRef + "]";
	}

	public Vertex(String uuid, String version, String name, String nodeType, String dataType, String desc,
			String createdOn, String active, GraphMetaIdentifierHolder metaRef) {
		super();
		this.uuid = uuid;
		this.version = version;
		this.name = name;
		this.nodeType = nodeType;
		this.dataType = dataType;
		this.desc = desc;
		this.createdOn = createdOn;
		this.active = active;
		this.metaRef = metaRef;
	}

	

	/*public Vertex (String uuid, String version, String name, String nodeType, String dataType, String desc, String createdOn, String active) {
		this.uuid = uuid;
		this.version = version;
		this.name = name;
		this.nodeType = nodeType;
		this.dataType = dataType;
		this.desc = desc;
		this.createdOn = createdOn;
		this.active = active;
	}
*/
	/*@Override
	public String toString() {
		return "Vertex [uuid=" + uuid + ", version=" + version + ", name=" + name + ", nodeType=" + nodeType
				+ ", dataType=" + dataType + ", desc=" + desc + ", createdOn=" + createdOn + ", active=" + active + "]";
	}*/

}

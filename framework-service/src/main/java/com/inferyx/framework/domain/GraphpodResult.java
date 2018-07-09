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

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author vaibhav
 *
 */
@Document(collection = "graphpodresult")
public class GraphpodResult {
	/*GraphpodMetaIdentifierHolder src;
	GraphpodMetaIdentifierHolder dst;
*/
	
	java.util.Map<String, String> source;
	java.util.Map<String, String> target;
	String value;	
	String edgeName;	
	String edgeType;
	String edgeProperties;	
	
	
	public GraphpodResult() {
		// TODO Auto-generated constructor stub
	}
	
	
	public GraphpodResult(Map<String, String> source, Map<String, String> target, String value, String edgeName,
			String edgeType, String edgeProperties) {
		super();
		this.source = source;
		this.target = target;
		this.value = value;
		this.edgeName = edgeName;
		this.edgeType = edgeType;
		this.edgeProperties = edgeProperties;
	}


	public String getEdgeName() {
		return this.edgeName;
	}


	public void setEdgeName(String edgeName) {
		this.edgeName = edgeName;
	}


	public String getEdgeType() {
		return this.edgeType;
	}


	public void setEdgeType(String edgeType) {
		this.edgeType = edgeType;
	}


	public String getEdgeProperties() {
		return this.edgeProperties;
	}


	public void setEdgeProopertes(String edgeProperties) {
		this.edgeProperties = edgeProperties;
	}


	public java.util.Map<String, String> getSource() {
		return this.source;
	}
	
	public void setSource(java.util.Map<String, String> source) {
		this.source = source;
	}
	
	public java.util.Map<String, String> getTarget() {
		return this.target;
	}
	
	public void setTarget(java.util.Map<String, String> target) {
		this.target = target;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	

	
}

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
	
	
//	
//	String src;
//	String dst;
//	
//	String transaction_id;
//	String account_id;
//	
//
//	
//	public String getSrc() {
//		return this.src;
//	}
//	public void setSrc(String src) {
//		this.src = src;
//	}
//	public String getDst() {
//		return this.dst;
//	}
//	public void setDst(String dst) {
//		this.dst = dst;
//	}
//	public String getTransaction_id() {
//		return this.transaction_id;
//	}
//	public void setTransaction_id(String transaction_id) {
//		this.transaction_id = transaction_id;
//	}
//	public String getAccount_id() {
//		return this.account_id;
//	}
//	public void setAccount_id(String account_id) {
//		this.account_id = account_id;
//	}
//	public GraphpodResult(String src, String dst, String transaction_id,
//			String account_id) {
//		super();
//		this.src = src;
//		this.dst = dst;
//		this.transaction_id = transaction_id;
//		this.account_id = account_id;
//	}
//	public GraphpodResult() {
//		super();
//	}
//	@Override
//	public String toString() {
//		return "GraphpodResult [src=" + this.src + ", dst=" + this.dst + ", transaction_id=" + this.transaction_id
//				+ ", account_id=" + this.account_id + "]";
//	}
//	
//	
//	
	
}

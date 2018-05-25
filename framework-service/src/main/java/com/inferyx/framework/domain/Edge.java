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

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "edge")
public class Edge {
	String src;
	String dst;

	String relationType;
	
	private GraphMetaIdentifierHolder srcMetaRef;
	private GraphMetaIdentifierHolder dstMetaRef;

	public GraphMetaIdentifierHolder getSrcMetaRef() {
		return srcMetaRef;
	}
	public void setSrcMetaRef(GraphMetaIdentifierHolder srcMetaRef) {
		this.srcMetaRef = srcMetaRef;
	}
	public GraphMetaIdentifierHolder getDstMetaRef() {
		return dstMetaRef;
	}
	public void setDstMetaRef(GraphMetaIdentifierHolder dstMetaRef) {
		this.dstMetaRef = dstMetaRef;
	}

	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDst() {
		return dst;
	}
	public void setDst(String dst) {
		this.dst = dst;
	}
	public String getRelationType() {
		return relationType;
	}
	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}
	
	/*public Edge (String src, String dst, String relationType) {
		this.src = src;
		this.dst = dst;
		this.relationType = relationType;
	}
	
	
	
	@Override
	public String toString() {
		return "Edge [src=" + src + ", dst=" + dst + ", relationType=" + relationType + "]";
	}*/
	
	
	@Override
	public String toString() {
		return "Edge [src=" + src + ", dst=" + dst + ", relationType=" + relationType + ", srcMetaRef=" + srcMetaRef
				+ ", dstMetaRef=" + dstMetaRef + "]";
	}
	public Edge(String src, String dst, String relationType, GraphMetaIdentifierHolder srcMetaRef,
			GraphMetaIdentifierHolder dstMetaRef) {
		this.src = src;
		this.dst = dst;
		this.relationType = relationType;
		this.srcMetaRef = srcMetaRef;
		this.dstMetaRef = dstMetaRef;
	}
	
}

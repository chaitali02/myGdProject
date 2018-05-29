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
	
	private GraphMetaIdentifierHolder srcInfo;
	private GraphMetaIdentifierHolder dstInfo;

	public GraphMetaIdentifierHolder getSrcMetaRef() {
		return srcInfo;
	}
	public void setSrcMetaRef(GraphMetaIdentifierHolder srcInfo) {
		this.srcInfo = srcInfo;
	}
	public GraphMetaIdentifierHolder getDstMetaRef() {
		return dstInfo;
	}
	public void setDstMetaRef(GraphMetaIdentifierHolder dstInfo) {
		this.dstInfo = dstInfo;
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
		return "Edge [src=" + src + ", dst=" + dst + ", relationType=" + relationType + ", srcInfo=" + srcInfo
				+ ", dstInfo=" + dstInfo + "]";
	}
	public Edge(String src, String dst, String relationType, GraphMetaIdentifierHolder srcInfo,
			GraphMetaIdentifierHolder dstInfo) {
		this.src = src;
		this.dst = dst;
		this.relationType = relationType;
		this.srcInfo = srcInfo;
		this.dstInfo = dstInfo;
	}
	
}

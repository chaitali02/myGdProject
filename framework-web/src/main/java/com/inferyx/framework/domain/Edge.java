package com.inferyx.framework.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "edge")
public class Edge {
	String src;
	String dst;
	String relationType;

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
	
	public Edge (String src, String dst, String relationType) {
		this.src = src;
		this.dst = dst;
		this.relationType = relationType;
	}
	
	@Override
	public String toString() {
		return "Edge [src=" + src + ", dst=" + dst + ", relationType=" + relationType + "]";
	}
	
}

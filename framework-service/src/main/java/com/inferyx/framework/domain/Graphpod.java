package com.inferyx.framework.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "graphpod")
public class Graphpod extends BaseEntity {

	List<GraphNode> nodeInfo;
	List<GraphEdge> edgeInfo;
	/**
	 * @return the nodeInfo
	 */
	public List<GraphNode> getNodeInfo() {
		return nodeInfo;
	}
	/**
	 * @param nodeInfo the nodeInfo to set
	 */
	public void setNodeInfo(List<GraphNode> nodeInfo) {
		this.nodeInfo = nodeInfo;
	}
	/**
	 * @return the edgeInfo
	 */
	public List<GraphEdge> getEdgeInfo() {
		return edgeInfo;
	}
	/**
	 * @param edgeInfo the edgeInfo to set
	 */
	public void setEdgeInfo(List<GraphEdge> edgeInfo) {
		this.edgeInfo = edgeInfo;
	}

}

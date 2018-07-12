/**
 * 
 */
package com.inferyx.framework.domain;

import java.util.List;

/**
 * @author joy
 *
 */

public class GraphEdge {

	String edgeId;
	String edgeType;
	String edgeName;
	MetaIdentifierHolder edgeSource;
	List<AttributeRefHolder> edgeProperties;
	AttributeRefHolder sourceNodeId;
	String sourceNodeType;
	AttributeRefHolder targetNodeId;
	String targetNodeType;
	Highlight highlightInfo;

	/**
	 * @return the edgeId
	 */
	public String getEdgeId() {
		return edgeId;
	}
	/**
	 * @param edgeId the edgeId to set
	 */
	public void setEdgeId(String edgeId) {
		this.edgeId = edgeId;
	}
	/**
	 * @return the edgeType
	 */
	public String getEdgeType() {
		return edgeType;
	}
	/**
	 * @param edgeType the edgeType to set
	 */
	public void setEdgeType(String edgeType) {
		this.edgeType = edgeType;
	}
	/**
	 * @return the edgeName
	 */
	public String getEdgeName() {
		return edgeName;
	}
	/**
	 * @param edgeName the edgeName to set
	 */
	public void setEdgeName(String edgeName) {
		this.edgeName = edgeName;
	}
	/**
	 * @return the edgeSource
	 */
	public MetaIdentifierHolder getEdgeSource() {
		return edgeSource;
	}
	/**
	 * @param edgeSource the edgeSource to set
	 */
	public void setEdgeSource(MetaIdentifierHolder edgeSource) {
		this.edgeSource = edgeSource;
	}
	/**
	 * @return the edgeProperties
	 */
	public List<AttributeRefHolder> getEdgeProperties() {
		return edgeProperties;
	}
	/**
	 * @param edgeProperties the edgeProperties to set
	 */
	public void setEdgeProperties(List<AttributeRefHolder> edgeProperties) {
		this.edgeProperties = edgeProperties;
	}
	/**
	 * @return the sourceNodeId
	 */
	public AttributeRefHolder getSourceNodeId() {
		return sourceNodeId;
	}
	/**
	 * @param sourceNodeId the sourceNodeId to set
	 */
	public void setSourceNodeId(AttributeRefHolder sourceNodeId) {
		this.sourceNodeId = sourceNodeId;
	}
	/**
	 * @return the sourceNodeType
	 */
	public String getSourceNodeType() {
		return sourceNodeType;
	}
	/**
	 * @param sourceNodeType the sourceNodeType to set
	 */
	public void setSourceNodeType(String sourceNodeType) {
		this.sourceNodeType = sourceNodeType;
	}
	/**
	 * @return the targetNodeId
	 */
	public AttributeRefHolder getTargetNodeId() {
		return targetNodeId;
	}
	/**
	 * @param targetNodeId the targetNodeId to set
	 */
	public void setTargetNodeId(AttributeRefHolder targetNodeId) {
		this.targetNodeId = targetNodeId;
	}
	/**
	 * @return the targetNodeType
	 */
	public String getTargetNodeType() {
		return targetNodeType;
	}
	/**
	 * @param targetNodeType the targetNodeType to set
	 */
	public void setTargetNodeType(String targetNodeType) {
		this.targetNodeType = targetNodeType;
	}
	
	/**
	 * @return
	 */
	public Highlight getHighlightInfo() {
		return this.highlightInfo;
	}
	public void setHighlightInfo(Highlight highlightInfo) {
		this.highlightInfo = highlightInfo;
	}

}

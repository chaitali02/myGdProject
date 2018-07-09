/**
 * 
 */
package com.inferyx.framework.domain;

import java.util.List;

/**
 * @author joy
 *
 */
public class GraphNode {
	
	AttributeRefHolder nodeId;
	String nodeType;
	String nodeIcon;
	AttributeRefHolder nodeName;
	MetaIdentifierHolder nodeSource;
	List<AttributeRefHolder> nodeProperties;
	Highlight highlightInfo;
	
	/**
	 * @return the nodeId
	 */
	public AttributeRefHolder getNodeId() {
		return nodeId;
	}
	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(AttributeRefHolder nodeId) {
		this.nodeId = nodeId;
	}
	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return nodeType;
	}
	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	/**
	 * @return the nodeIcon
	 */
	public String getNodeIcon() {
		return nodeIcon;
	}
	/**
	 * @param nodeIcon the nodeIcon to set
	 */
	public void setNodeIcon(String nodeIcon) {
		this.nodeIcon = nodeIcon;
	}
	/**
	 * @return the nodeName
	 */
	public AttributeRefHolder getNodeName() {
		return nodeName;
	}
	/**
	 * @param nodeName the nodeName to set
	 */
	public void setNodeName(AttributeRefHolder nodeName) {
		this.nodeName = nodeName;
	}
	/**
	 * @return the nodeSource
	 */
	public MetaIdentifierHolder getNodeSource() {
		return nodeSource;
	}
	/**
	 * @param nodeSource the nodeSource to set
	 */
	public void setNodeSource(MetaIdentifierHolder nodeSource) {
		this.nodeSource = nodeSource;
	}
	/**
	 * @return the nodeProperties
	 */
	public List<AttributeRefHolder> getNodeProperties() {
		return nodeProperties;
	}
	/**
	 * @param nodeProperties the nodeProperties to set
	 */
	public void setNodeProperties(List<AttributeRefHolder> nodeProperties) {
		this.nodeProperties = nodeProperties;
	}
	
	/**
	 * @return the highlightInfo
	 */
	public Highlight getHighlightInfo() {
		return this.highlightInfo;
	}
	/**
	 * @param highlightInfo the highlightInfo to set
	 */
	public void setHighlightInfo(Highlight highlightInfo) {
		this.highlightInfo = highlightInfo;
	}

}

/**
 * 
 */
package com.inferyx.framework.domain;

import java.util.List;
import java.util.Set;

/**
 * @author joy
 *
 */
public class GraphNode {
	
	
	AttributeRefHolder nodeId;
	String nodeType;
	String nodeIcon;
	AttributeRefHolder nodeName;
	AttributeRefHolder nodeSize;
	MetaIdentifierHolder nodeSource;
	List<AttributeRefHolder> nodeProperties;
	Highlight nodeBackgroundInfo;
	Highlight highlightInfo;
	
	
	/**
	 * @return the nodeId
	 */
	public AttributeRefHolder getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId
	 *            the nodeId to set
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
	 * @param nodeType
	 *            the nodeType to set
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
	 * @param nodeIcon
	 *            the nodeIcon to set
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
	 * @param nodeName
	 *            the nodeName to set
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
	 * @param nodeSource
	 *            the nodeSource to set
	 */
	public void setNodeSource(MetaIdentifierHolder nodeSource) {
		this.nodeSource = nodeSource;
	}




	public List<AttributeRefHolder> getNodeProperties() {
		return this.nodeProperties;
	}

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
	 * @param highlightInfo
	 *            the highlightInfo to set
	 */
	public void setHighlightInfo(Highlight highlightInfo) {
		this.highlightInfo = highlightInfo;
	}



	public AttributeRefHolder getNodeSize() {
		return nodeSize;
	}

	public void setNodeSize(AttributeRefHolder nodeSize) {
		this.nodeSize = nodeSize;
	}

	public Highlight getNodeBackgroundInfo() {
		return this.nodeBackgroundInfo;
	}

	public void setNodeBackgroundInfo(Highlight nodeBackgroundInfo) {
		this.nodeBackgroundInfo = nodeBackgroundInfo;
	}



}

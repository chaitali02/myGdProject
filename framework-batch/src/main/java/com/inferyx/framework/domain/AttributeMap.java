/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.domain;

public class AttributeMap {

	private String attrMapId;
	private AttributeRefHolder sourceAttr;
	private AttributeRefHolder targetAttr;
	//private String desc;
	
	
	public String getAttrMapId() {
		return attrMapId;
	}

	public void setAttrMapId(String attrMapId) {
		this.attrMapId = attrMapId;
	}
	
	public AttributeRefHolder getSourceAttr() {
		return sourceAttr;
	}

	public void setSourceAttr(AttributeRefHolder sourceAttr) {
		this.sourceAttr = sourceAttr;
	}
	
	public AttributeRefHolder getTargetAttr() {
		return targetAttr;
	}

	public void setTargetAttr(AttributeRefHolder targetAttr) {
		this.targetAttr = targetAttr;
	}

}
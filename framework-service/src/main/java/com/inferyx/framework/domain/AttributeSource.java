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

public class AttributeSource {
	
	private String attrSourceId;	
	private String attrSourceName;
	private AttributeRefHolder sourceAttr;
	
	public String getAttrSourceId() {
		return attrSourceId;
	}
	public void setAttrSourceId(String AttrSourceId) {
		this.attrSourceId = AttrSourceId;
	}
	public AttributeRefHolder getSourceAttr() {
		return sourceAttr;
	}
	public void setSourceAttr(AttributeRefHolder sourceAttr) {
		this.sourceAttr = sourceAttr;
	}	
	public String getAttrSourceName() {
		return attrSourceName;
	}
	public void setAttrSourceName(String attrSourceName) {
		this.attrSourceName = attrSourceName;
	}
	
	
	

}

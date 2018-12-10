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

import org.springframework.stereotype.Component;

@Component
public class AttributeRefHolder extends MetaIdentifierHolder {

	private String attrId;
	private String attrName;
	private String attrType;
	private String attrUnitType;

	/**
	 *
	 * @Vaibhav
	 *
	 * @return the attrUnitType
	 */
	public String getAttrUnitType() {
		return attrUnitType;
	}

	/**
	 *
	 * @Vaibhav
	 *
	 * @param attrUnitType the attrUnitType to set
	 */
	public void setAttrUnitType(String attrUnitType) {
		this.attrUnitType = attrUnitType;
	}

	public String getAttrType() {
		return this.attrType;
	}

	public void setAttrType(String attrType) {
		this.attrType = attrType;
	}

	public String getAttrId() {
		return attrId;
	}

	public void setAttrId(String attrId) {
		this.attrId = attrId;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AttributeRefHolder [attrId=" + attrId + ", attrName=" + attrName + ", attrType=" + attrType
				+ ", attrUnitType=" + attrUnitType + "]";
	}

	

}

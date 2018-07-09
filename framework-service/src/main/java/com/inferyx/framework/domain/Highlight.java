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

import java.util.List;
import java.util.Map;


public class Highlight
{

	String type;
	AttributeRefHolder propertyId;
	List<Property> propertyInfo;
	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the propertyId
	 */
	public AttributeRefHolder getPropertyId() {
		return this.propertyId;
	}

	/**
	 * @param propertyId the propertyId to set
	 */
	public void setPropertyId(AttributeRefHolder propertyId) {
		this.propertyId = propertyId;
	}

	
	/**
	 * 
	 */
	public Highlight() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Highlight [type=" + type + ", propertyId=" + propertyId + ", propertyInfo=" + propertyInfo + "]";
	}

	/**
	 * @return the propertyInfo
	 */
	public List<Property> getPropertyInfo() {
		return propertyInfo;
	}

	/**
	 * @param propertyInfo the propertyInfo to set
	 */
	public void setPropertyInfo(List<Property> propertyInfo) {
		this.propertyInfo = propertyInfo;
	}

	


}

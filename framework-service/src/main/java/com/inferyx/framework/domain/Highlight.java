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
	List<java.util.Map<String, String>> propertyGrid;
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
	 * @return the propertyGrid
	 */
	public List<java.util.Map<String, String>> getPropertyGrid() {
		return this.propertyGrid;
	}

	/**
	 * @param propertyGrid the propertyGrid to set
	 */
	public void setPropertyGrid(List<java.util.Map<String, String>> propertyGrid) {
		this.propertyGrid = propertyGrid;
	}

	/**
	 * 
	 */
	public Highlight() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Highlight [type=" + this.type + ", propertyId=" + this.propertyId + ", propertyGrid="
				+ this.propertyGrid + "]";
	}

	/**
	 * @param type
	 * @param propertyId
	 * @param propertyGrid
	 */
	public Highlight(String type, AttributeRefHolder propertyId, List<Map<String, String>> propertyGrid) {
		super();
		this.type = type;
		this.propertyId = propertyId;
		this.propertyGrid = propertyGrid;
	}




}

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

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Ganesh
 *
 */
@Document(collection = "ingestExec")
public class IngestExec extends BaseRuleExec {
	private String lastIncrValue;
	private String location;

	/**
	 *
	 * @Ganesh
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the lastIncrValue
	 */
	public String getLastIncrValue() {
		return lastIncrValue;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param lastIncrValue the lastIncrValue to set
	 */
	public void setLastIncrValue(String lastIncrValue) {
		this.lastIncrValue = lastIncrValue;
	}
	
}

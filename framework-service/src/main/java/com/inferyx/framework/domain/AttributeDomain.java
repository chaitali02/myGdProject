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

@Document(collection = "domain")

public class AttributeDomain extends BaseEntity {	

	private String regEx;

	public String getregEx() {
		return regEx;
	}

	public void setDimInfo(String regEx) {
		this.regEx = regEx;
	}	

	
}

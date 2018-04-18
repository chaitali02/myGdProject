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

import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="import")
public class Import extends BaseEntity {
	private String location;
	String includeDep;
	private List<ImportIdentifierHolder> metaInfo;
	
	
	public Import() {
		super();
	}

	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public List<ImportIdentifierHolder> getMetaInfo() {
		return metaInfo;
	}
	
	public void setMetaInfo(List<ImportIdentifierHolder> metaInfo) {
		this.metaInfo = metaInfo;
	}
	
	public String getIncludeDep() {
		return includeDep;
	}
	public void setIncludeDep(String includeDep) {
		this.includeDep = includeDep;
	}

	@Override
	public String toString() {
		return "Import { location: " + location + ", includeDep: " + includeDep + ", metaInfo: " + metaInfo + "}";
	}
	
}

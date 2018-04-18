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
@Document(collection="export")
public class Export extends BaseEntity{
	private String location;
	String includeDep; 
	private List<MetaIdentifierHolder> metaInfo;
	
	
	public Export() {
		super();
	}

	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public List<MetaIdentifierHolder> getMetaInfo() {
		return metaInfo;
	}
	
	public void setMetaInfo(List<MetaIdentifierHolder> metaInfo) {
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
		return "Export { location: " + location + ", includeDep: " + includeDep + ", metaInfo: " + metaInfo + "}";
		
	}
	
}

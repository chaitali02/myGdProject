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

@Document(collection="filter")
public class Filter extends BaseEntity{
	
	//private String name;
	//private String desc;
	private MetaIdentifierHolder dependsOn;
	private List<FilterInfo> filterInfo;
/*
	public String getName() {
		return name;
	}*/

	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	/*public void setName(String name) {
		this.name = name;
	}*/

	/*public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
*/
	public List<FilterInfo> getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(List<FilterInfo> filterInfo) {
		this.filterInfo = filterInfo;
	}
	
	/*public String sql(MetaIdentifierUtil commonActivity) {
		return FilterInfo.sql(commonActivity, filterInfo);
	}
*/
}
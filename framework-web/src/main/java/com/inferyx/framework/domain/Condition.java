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

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection="condition")
public class Condition extends BaseEntity{
	//private String name;
	//private String desc;	
	private MetaIdentifierHolder dependsOn;
	private List<FilterInfo> conditionInfo;

	/*public String getName() {
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
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}*/

	public List<FilterInfo> getConditionInfo() {
		return conditionInfo;
	}

	public void setConditionInfo(List<FilterInfo> conditionInfo) {
		this.conditionInfo = conditionInfo;
	}
	
/*	public String sql(MetaIdentifierUtil commonActivity) {
		return FilterInfo.sql(commonActivity, getConditionInfo());
	}*/

}
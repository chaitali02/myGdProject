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


@Document(collection="relation")
public class Relation extends BaseEntity{


	//private String name;
	//private String desc;
	
	private MetaIdentifierHolder dependsOn;
	private List<RelationInfo> relationInfo;
	/**
	 * 
	 * @return The name
	 */
	/*public String getName() {
		return name;
	}

	*//**
	 * 
	 * @param name
	 *            The name
	 *//*
	public void setName(String name) {
		this.name = name;
	}

	
	*//**
	 * 
	 * @return The desc
	 *//*
	public String getDesc() {
		return desc;
	}

	*//**
	 * 
	 * @param desc
	 *            The desc
	 *//*
	public void setDesc(String desc) {
		this.desc = desc;
	}
*/
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	public List<RelationInfo> getRelationInfo() {
		return relationInfo;
	}

	public void setRelationInfo(List<RelationInfo> relationInfo) {
		this.relationInfo = relationInfo;
	}

/*	public String sql(Loader loader) {
		return relationInfo.get(0).sql(loader);
	}*/


}
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

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="group")
public class Group extends BaseEntity 
{
	/*private List<MetaIdentifierHolder> roleInfo = new ArrayList<>();*/
	MetaIdentifierHolder roleId;
	MetaIdentifierHolder appId;
	
	public MetaIdentifierHolder getRoleId() {
		return roleId;
	}

	public void setRoleId(MetaIdentifierHolder roleId) {
		this.roleId = roleId;
	}

	public MetaIdentifierHolder getAppId() {
		return appId;
	}

	public void setAppId(MetaIdentifierHolder appId) {
		this.appId = appId;
	}

	@Override
	public String toString() {
		return "Group {\nroleId: " + roleId + ", appId: " + appId + "\n}";
	}

	/*public List<MetaIdentifierHolder> getRoleInfo() {
		return roleInfo;
	}

	public void setRoleInfo(List<MetaIdentifierHolder> roleInfo) {
		this.roleInfo = roleInfo;
	}*/

}

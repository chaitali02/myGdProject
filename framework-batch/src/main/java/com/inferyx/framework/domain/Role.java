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

@Document(collection="role")
public class Role extends BaseEntity{
	
	private List<MetaIdentifierHolder> privilegeInfo;

	public List<MetaIdentifierHolder> getPrivilegeInfo() {
		return privilegeInfo;
	}

	public void setPrivilgeInfo(List<MetaIdentifierHolder> privilegeInfo) {
		this.privilegeInfo = privilegeInfo;
	}
	
}

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

import com.inferyx.framework.enums.ApplicationType;

public class AppRole {
	MetaIdentifierHolder defaultAppId ;
	private ApplicationType applicationType;
	MetaIdentifierHolder appId;
	List<MetaIdentifierHolder> roleInfo;
	
	public AppRole() {
		super();
	}

	public MetaIdentifierHolder getDefaultAppId() {
		return defaultAppId;
	}

	public void setDefaultAppId(MetaIdentifierHolder defaultAppId) {
		this.defaultAppId = defaultAppId;
	}

	public AppRole(MetaIdentifierHolder appId, List<MetaIdentifierHolder> roleInfo) {
		super();
		this.appId = appId;
		this.roleInfo = roleInfo;
	}
	public AppRole(MetaIdentifierHolder appId, List<MetaIdentifierHolder> roleInfo, MetaIdentifierHolder defaultAppId,ApplicationType applicationType ) {
		super();
		this.appId = appId;
		this.roleInfo = roleInfo;
		this.defaultAppId=defaultAppId;
		this.applicationType=applicationType;
	}

	public MetaIdentifierHolder getAppId() {
		return appId;
	}

	public void setAppId(MetaIdentifierHolder appId) {
		this.appId = appId;
	}

	public List<MetaIdentifierHolder> getRoleInfo() {
		return roleInfo;
	}

	public void setRoleInfo(List<MetaIdentifierHolder> roleInfo) {
		this.roleInfo = roleInfo;
	}
	
	public ApplicationType getApplicationType() {
		return this.applicationType;
	}

	public void setApplicationType(ApplicationType applicationType) {
		this.applicationType = applicationType;
	}

	@Override
	public String toString() {
		return "AppRole {\nappId: " + appId + ", roleInfo: " + roleInfo + "\n}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((roleInfo == null) ? 0 : roleInfo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AppRole other = (AppRole) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (roleInfo == null) {
			if (other.roleInfo != null)
				return false;
		} else if (!roleInfo.equals(other.roleInfo))
			return false;
		return true;
	}
}

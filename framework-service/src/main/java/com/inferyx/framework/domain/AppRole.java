package com.inferyx.framework.domain;

import java.util.List;

public class AppRole {

	MetaIdentifierHolder appId;
	List<MetaIdentifierHolder> roleInfo;
	
	public AppRole() {
		super();
	}

	public AppRole(MetaIdentifierHolder appId, List<MetaIdentifierHolder> roleInfo) {
		super();
		this.appId = appId;
		this.roleInfo = roleInfo;
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

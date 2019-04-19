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

import com.inferyx.framework.enums.RunMode;

@Document(collection="session")
public class Session extends BaseEntity
{
	private MetaIdentifierHolder userInfo;
	private List<Status> statusList;
	private MetaIdentifierHolder roleInfo;
	private String sessionId;
	private String ipAddress;
	private RunMode type;
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public MetaIdentifierHolder getRoleInfo() {
		return roleInfo;
	}

	public void setRoleInfo(MetaIdentifierHolder roleInfo) {
		this.roleInfo = roleInfo;
	}

	public List<Status> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}

	public MetaIdentifierHolder getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(MetaIdentifierHolder userInfo) {
		this.userInfo = userInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @return the type
	 */
	public RunMode getType() {
		return type;
	}

	/**
	 * @Ganesh
	 *
	 * @param type the type to set
	 */
	public void setType(RunMode type) {
		this.type = type;
	}
}

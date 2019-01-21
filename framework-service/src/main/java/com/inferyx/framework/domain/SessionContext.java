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

import org.apache.commons.collections.map.MultiValueMap;
//import org.springframework.context.annotation.Scope;
//import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
//@Scope(value="session",  proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionContext {
	
	private MetaIdentifierHolder userInfo;
	private MetaIdentifierHolder appInfo;
	private MetaIdentifierHolder roleInfo;
	private MetaIdentifierHolder sessionInfo;
	private MultiValueMap privInfo;
	
	public MultiValueMap getPrivInfo() {
		return privInfo;
	}

	public void setPrivInfo(MultiValueMap privInfo) {
		this.privInfo = privInfo;
	}

	public MetaIdentifierHolder getSessionInfo() {
		return sessionInfo;
	}
	public void setSessionInfo(MetaIdentifierHolder sessionInfo) {
		this.sessionInfo = sessionInfo;
	}
	public MetaIdentifierHolder getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(MetaIdentifierHolder userInfo) {
		this.userInfo = userInfo;
	}
	public MetaIdentifierHolder getAppInfo() {
		return appInfo;
	}
	public void setAppInfo(MetaIdentifierHolder appInfo) {
		this.appInfo = appInfo;
	}
	public MetaIdentifierHolder getRoleInfo() {
		return roleInfo;
	}
	public void setRoleInfo(MetaIdentifierHolder roleInfo) {
		this.roleInfo = roleInfo;
	}
	@Override
	public String toString() {
		return "SessionContext [userInfo=" + userInfo + ", appInfo=" + appInfo + ", roleInfo=" + roleInfo
				+ ", sessionInfo=" + sessionInfo + "]";
	}

}

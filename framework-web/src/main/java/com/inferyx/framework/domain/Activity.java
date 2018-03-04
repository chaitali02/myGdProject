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

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="activity")
public class Activity extends BaseEntity
{
	private String status;
	private MetaIdentifierHolder userInfo;
	private MetaIdentifierHolder metaInfo;
	private MetaIdentifierHolder sessionInfo;
	private String requestUrl;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public MetaIdentifierHolder getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(MetaIdentifierHolder userInfo) {
		this.userInfo = userInfo;
	}
	public MetaIdentifierHolder getSessionInfo() {
		return sessionInfo;
	}
	public void setSessionInfo(MetaIdentifierHolder sessionInfo) {
		this.sessionInfo = sessionInfo;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public MetaIdentifierHolder getMetaInfo() {
		return metaInfo;
	}
	public void setMetaInfo(MetaIdentifierHolder metaInfo) {
		this.metaInfo = metaInfo;
	}
}

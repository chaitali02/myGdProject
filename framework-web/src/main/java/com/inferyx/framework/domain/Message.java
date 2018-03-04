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
@Document(collection="message")
public class Message extends BaseEntity
{
	private MetaIdentifierHolder activityInfo;
	private String code;
	private String status;
	private String message;
	
	public Message() {
		super();
	}
	
	public Message(MetaIdentifierHolder activityInfo, String code, String status, String message) {
		super();
		this.activityInfo = activityInfo;
		this.code = code;
		this.status = status;
		this.message = message;
	}
	
	public Message(String code, String status, String message) {
		super();
		this.code = code;
		this.status = status;
		this.message = message;
	}
	
	public MetaIdentifierHolder getActivityInfo() {
		return activityInfo;
	}
	
	public void setActivityInfo(MetaIdentifierHolder activityInfo) {
		this.activityInfo = activityInfo;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Message { activityInfo: " + activityInfo + ", code: " + code + ", status: " + status + ", message: "
				+ message + " }";
	}
	
	
}

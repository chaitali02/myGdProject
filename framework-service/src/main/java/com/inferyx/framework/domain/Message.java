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


import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="message")
public class Message extends BaseEntity {
	private MetaIdentifierHolder activityInfo;
	private String code;
	private String status;
	private String message;
	private MetaIdentifierHolder dependsOn;
	
	public Message() {
		super();
	}
	
	/**
	 * @param code
	 * @param status
	 * @param message
	 */
	public Message(String code, String status, String message) {
		super();
		this.code = code;
		this.status = status;
		this.message = message;
	}

	/**
	 * @param activityInfo
	 * @param code
	 * @param status
	 * @param message
	 */
	public Message(MetaIdentifierHolder activityInfo, String code, String status, String message) {
		super();
		this.activityInfo = activityInfo;
		this.code = code;
		this.status = status;
		this.message = message;
	}

	/**
	 * @param code
	 * @param status
	 * @param message
	 * @param dependsOn
	 */
	public Message(String code, String status, String message, MetaIdentifierHolder dependsOn) {
		super();
		this.code = code;
		this.status = status;
		this.message = message;
		this.dependsOn = dependsOn;
	}
	
	/**
	 * @param activityInfo
	 * @param code
	 * @param status
	 * @param message
	 * @param dependsOn
	 */
	public Message(MetaIdentifierHolder activityInfo, String code, String status, String message,
			MetaIdentifierHolder dependsOn) {
		super();
		this.activityInfo = activityInfo;
		this.code = code;
		this.status = status;
		this.message = message;
		this.dependsOn = dependsOn;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the activityInfo
	 */
	public MetaIdentifierHolder getActivityInfo() {
		return activityInfo;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param activityInfo the activityInfo to set
	 */
	public void setActivityInfo(MetaIdentifierHolder activityInfo) {
		this.activityInfo = activityInfo;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the dependsOn
	 */
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param dependsOn the dependsOn to set
	 */
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}
}

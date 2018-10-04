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


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="message")
public class Message extends BaseEntity {
	private MetaIdentifierHolder activityInfo;
	private String code;
	private String status;
	private String message;
	
	private String msgCode;
	private String msgDiscription;
	private Date msgTime;
	
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
	
	/**
	 * @param msgCode
	 * @param msgDiscription
	 * @param msgTime
	 */
	public Message(String msgCode, String msgDiscription, Date msgTime) {
		super();
		this.msgCode = msgCode;
		this.msgDiscription = msgDiscription;
		try {
			this.msgTime = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(msgTime.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the msgCode
	 */
	public String getMsgCode() {
		return msgCode;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param msgCode the msgCode to set
	 */
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the msgDiscription
	 */
	public String getMsgDiscription() {
		return msgDiscription;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param msgDiscription the msgDiscription to set
	 */
	public void setMsgDiscription(String msgDiscription) {
		this.msgDiscription = msgDiscription;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the msgTime
	 */
	public String getMsgTime() {
		String tmp = "";
		if (msgTime != null)
			tmp = msgTime.toString();
		return tmp;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param msgTime the msgTime to set
	 */
	public void setMsgTime(String msgTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		
		//Date tmp = new Date(createdOn);
		try {
			this.msgTime = formatter.parse(msgTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//tmp;
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

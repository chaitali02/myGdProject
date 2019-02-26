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

import java.util.Map;

/**
 * @author Ganesh
 *
 */
public class Notification {
	private String subect;
	private String message;
	private String from;
	private String password;
	private String host;
	private String port;
	private SenderInfo senderInfo;
	private Map<String, Object> otherFromDetails;
	
	/**
	 * @Ganesh
	 *
	 * @return the subect
	 */
	public String getSubect() {
		return subect;
	}
	/**
	 * @Ganesh
	 *
	 * @param subect the subect to set
	 */
	public void setSubect(String subect) {
		this.subect = subect;
	}
	/**
	 * @Ganesh
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @Ganesh
	 *
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @Ganesh
	 *
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}
	/**
	 * @Ganesh
	 *
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	/**
	 * @Ganesh
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @Ganesh
	 *
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @Ganesh
	 *
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @Ganesh
	 *
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @Ganesh
	 *
	 * @return the port
	 */
	public String getPort() {
		return port;
	}
	/**
	 * @Ganesh
	 *
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}
	/**
	 * @Ganesh
	 *
	 * @return the eMailInfo
	 */
	public SenderInfo getSenderInfo() {
		return senderInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @param senderInfo the senderInfo to set
	 */
	public void setSenderInfo(SenderInfo senderInfo) {
		this.senderInfo = senderInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @return the otherFromDetails
	 */
	public Map<String, Object> getOtherFromDetails() {
		return otherFromDetails;
	}
	/**
	 * @Ganesh
	 *
	 * @param otherFromDetails the otherFromDetails to set
	 */
	public void setOtherFromDetails(Map<String, Object> otherFromDetails) {
		this.otherFromDetails = otherFromDetails;
	}	
}

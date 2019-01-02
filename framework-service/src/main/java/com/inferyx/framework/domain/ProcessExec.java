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

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Ganesh
 *
 */
@Document(collection="processexec")
public class ProcessExec extends BaseExec {
	private String type;
	private String pId;
	private Date startTime;
	private Date stopTime;
	/**
	 * @Ganesh
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @Ganesh
	 *
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @Ganesh
	 *
	 * @return the pId
	 */
	public String getpId() {
		return pId;
	}
	/**
	 * @Ganesh
	 *
	 * @param pId the pId to set
	 */
	public void setpId(String pId) {
		this.pId = pId;
	}
	/**
	 * @Ganesh
	 *
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * @Ganesh
	 *
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	/**
	 * @Ganesh
	 *
	 * @return the stopTime
	 */
	public Date getStopTime() {
		return stopTime;
	}
	/**
	 * @Ganesh
	 *
	 * @param stopTime the stopTime to set
	 */
	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}
}

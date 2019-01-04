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

import com.inferyx.framework.enums.ProcessType;

/**
 * @author Ganesh
 *
 */
@Document(collection="processexec")
public class ProcessExec extends BaseExec {
	private ProcessType processType;
	private String pId;
	private Date startTime;
	private Date stopTime;
	
	/**
	 * @Ganesh
	 *
	 * @return the processType
	 */
	public ProcessType getProcessType() {
		return processType;
	}
	/**
	 * @Ganesh
	 *
	 * @param processType the processType to set
	 */
	public void setProcessType(ProcessType processType) {
		this.processType = processType;
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

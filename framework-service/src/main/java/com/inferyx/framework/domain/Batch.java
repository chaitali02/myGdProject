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

@Document(collection = "batch")
public class Batch extends BaseEntity {

//	private List<Schedule> scheduleInfo;
	private List<MetaIdentifierHolder> pipelineInfo;
	private String inParallel;
	private SenderInfo senderInfo;
	
	/**
	 * @Ganesh
	 *
	 * @return the senderInfo
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
	//	/**
//	 * @Ganesh
//	 *
//	 * @return the scheduleInfo
//	 */
//	public  List<Schedule> getScheduleInfo() {
//		return scheduleInfo;
//	}
//	/**
//	 * @Ganesh
//	 *
//	 * @param scheduleInfo the scheduleInfo to set
//	 */
//	public void setScheduleInfo( List<Schedule> scheduleInfo) {
//		this.scheduleInfo = scheduleInfo;
//	}
	/**
	 * @Ganesh
	 *
	 * @return the pipelineInfo
	 */
	public List<MetaIdentifierHolder> getPipelineInfo() {
		return pipelineInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @param pipelineInfo the pipelineInfo to set
	 */
	public void setPipelineInfo(List<MetaIdentifierHolder> pipelineInfo) {
		this.pipelineInfo = pipelineInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @return the inParallel
	 */
	public String getInParallel() {
		return inParallel;
	}
	/**
	 * @Ganesh
	 *
	 * @param inParallel the inParallel to set
	 */
	public void setInParallel(String inParallel) {
		this.inParallel = inParallel;
	}	
}
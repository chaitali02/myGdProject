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

	private List<Schedule> scheduleInfo;
	private List<MetaIdentifierHolder> metaList;
	private String inParallel;
	
	/**
	 * @Ganesh
	 *
	 * @return the scheduleInfo
	 */
	public  List<Schedule> getScheduleInfo() {
		return scheduleInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @param scheduleInfo the scheduleInfo to set
	 */
	public void setScheduleInfo( List<Schedule> scheduleInfo) {
		this.scheduleInfo = scheduleInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @return the metaList
	 */
	public List<MetaIdentifierHolder> getMetaList() {
		return metaList;
	}
	/**
	 * @Ganesh
	 *
	 * @param metaList the metaList to set
	 */
	public void setMetaList(List<MetaIdentifierHolder> metaList) {
		this.metaList = metaList;
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
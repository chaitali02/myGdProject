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
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Ganesh
 *
 */
@Document(collection = "schedule")
public class Schedule extends BaseEntity {
//	private String name;
	private Date startDate;
	private Date endDate;
	private Date nextRunTime;
	private String frequencyType; //Once/Daily/Weekly/Bi-Weekly/Monthly/Quarterly/Yearly
	private List<String> frequencyDetail;//if weekly then days/if monthly then date/if quarterly then quarter
//	private String recurring = "N";
	private MetaIdentifierHolder dependsOn;
	private String scheduleChg;
	
	/**
	 *
	 * @Ganesh
	 *
	 * @return the scheduleChg
	 */
	public String getScheduleChg() {
		return scheduleChg;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param scheduleChg the scheduleChg to set
	 */
	public void setScheduleChg(String scheduleChg) {
		this.scheduleChg = scheduleChg;
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
	/**
	 *
	 * @Ganesh
	 *
	 * @return the nextRunTime
	 */
	public Date getNextRunTime() {
		return nextRunTime;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param nextRunTime the nextRunTime to set
	 */
	public void setNextRunTime(String nextRunTime) {
		if (nextRunTime != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");		
			try {
				this.nextRunTime = formatter.parse(nextRunTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			this.nextRunTime = null;
	}
//	/**
//	 *
//	 * @Ganesh
//	 *
//	 * @return the name
//	 */
//	public String getName() {
//		return name;
//	}
//	/**
//	 *
//	 * @Ganesh
//	 *
//	 * @param name the name to set
//	 */
//	public void setName(String name) {
//		this.name = name;
//	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");		
		try {
			this.startDate = formatter.parse(startDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");		
		try {
			this.endDate = formatter.parse(endDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the frequencyType
	 */
	public String getFrequencyType() {
		return frequencyType;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param frequencyType the frequencyType to set
	 */
	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the frequencyDetail
	 */
	public List<String> getFrequencyDetail() {
		return frequencyDetail;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param frequencyDetail the frequencyDetail to set
	 */
	public void setFrequencyDetail(List<String> frequencyDetail) {
		this.frequencyDetail = frequencyDetail;
	}
//	/**
//	 *
//	 * @Ganesh
//	 *
//	 * @return the recurring
//	 */
//	public String getRecurring() {
//		return recurring;
//	}
//	/**
//	 *
//	 * @Ganesh
//	 *
//	 * @param recurring the recurring to set
//	 */
//	public void setRecurring(String recurring) {
//		this.recurring = recurring;
//	}
}

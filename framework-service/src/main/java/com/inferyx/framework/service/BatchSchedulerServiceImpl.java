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
package com.inferyx.framework.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.demo.DynamicSchedule;
import com.inferyx.framework.domain.Batch;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Schedule;
import com.inferyx.framework.enums.FrequencyType;

/**
 * @author Ganesh
 *
 */
@Service
public class BatchSchedulerServiceImpl {
	@Autowired
	private BatchTriggerServiceImpl batchTriggerServiceImpl;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	
	static Logger logger = Logger.getLogger(BatchSchedulerServiceImpl.class);
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("EEE MMM dd hh:mm:ss z yyyy");
	
	@SuppressWarnings("unchecked")
	public Map<Date, String> getNextBatchExecTime() throws ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Date currDate = simpleDateFormat.parse(new Date().toString());
		Map<Date, String> scheduleMap = new TreeMap<>();
		List<Schedule> scheduleInfo = (List<Schedule>) commonServiceImpl.findAllLatestWithoutAppUuid(MetaType.schedule);
		if(scheduleInfo != null) {
			for(Schedule schedule : scheduleInfo) {
				Date nextRunTime = schedule.getNextRunTime();
				if(nextRunTime != null && nextRunTime.compareTo(currDate) > 0) {
					if(nextRunTime != null) {
						scheduleMap.put(nextRunTime,"tmp");
					}
				}
			}
		}		
		return scheduleMap;
	}

	public Date getNextRunTime(Date startDate, Date endDate, Date previousRunTime, String frequencyType, List<String> frequencyDetail) throws ParseException {
		Date currDate = simpleDateFormat.parse(new Date().toString());
		
		if (startDate.compareTo(currDate) < 0) { //"startDate is after currDate"					
            logger.info("Start date '"+startDate+"' is before current date '"+currDate+"'. Setting to current date");
            return currDate;
        } else {
        		switch(FrequencyType.valueOf(frequencyType.toLowerCase())) {
	        		case ONCE : return startDate;
	        		case HOURLY : return getNextHourlyRunTime(startDate, endDate, previousRunTime);
	        		case DAILY : return getNextDailyRunTime(startDate, endDate, previousRunTime);
	        		case WEEKLY : return getNextWeelyRunTime(startDate, endDate, previousRunTime, frequencyDetail);
	        		case BIWEEKLY : return getNextBiWeeklyRunTime(startDate, endDate, previousRunTime, frequencyDetail);
	        		case MONTHLY : return getNextWeelyRunTime(startDate, endDate, previousRunTime, frequencyDetail);
	        		case QUARTERLY : return getNextQuarterlyRunTime(startDate, endDate, previousRunTime, frequencyDetail);
	        		case YEARLY : return getNextYearlyRunTime(startDate, endDate, previousRunTime);
	        		default : return null;	
        		}
        	} 		
	}

	private Date getNextHourlyRunTime(Date startDate, Date endDate, Date previousRunTime) {
		// TODO Auto-generated method stub
		return null;
	}

	private Date getNextYearlyRunTime(Date startDate, Date endDate, Date previousRunTime) {
		// TODO Auto-generated method stub
		return null;
	}

	private Date getNextQuarterlyRunTime(Date startDate, Date endDate, Date previousRunTime,
			List<String> frequencyDetail) {
		// TODO Auto-generated method stub
		return null;
	}

	private Date getNextBiWeeklyRunTime(Date startDate, Date endDate, Date previousRunTime,
			List<String> frequencyDetail) {
		// TODO Auto-generated method stub
		return null;
	}

	private Date getNextWeelyRunTime(Date startDate, Date endDate, Date previousRunTime, List<String> frequencyDetail) {
		// TODO Auto-generated method stub
		LocalDateTime.from(new Date().toInstant()).plusDays(1);
		
		return null;
	}

	private Date getNextDailyRunTime(Date startDate, Date endDate, Date previousRunTime) {
		if(previousRunTime != null) {
			Date nextRunTime = new Date(previousRunTime.getTime() + (1000 * 60 * 60 * 24));
			if(nextRunTime.compareTo(endDate) <= 0) {
				return nextRunTime;
			} else {
				return null; 
			}
		} else {
			return startDate;
		}
	}
	
	public void setSchedulingTrigger() throws Exception {
		Map<Date, String> scheduleMap = getNextBatchExecTime();
		batchTriggerServiceImpl.setNextExecutionTime(scheduleMap.keySet().toArray(new Date[scheduleMap.keySet().size()])[0]);
	}
	
	public void runBatches() throws Exception {
      System.out.println("Scheduler triggered. Submitting batch...");
      //Set the next time the scheduler to start.
	  Map<Date, String> scheduleMap = getNextBatchExecTime();      
      batchTriggerServiceImpl.setNextExecutionTime(null); //Change null with map position 0 but need to handle nulls.
   }

}

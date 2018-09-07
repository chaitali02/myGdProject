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

/**
 * @author Ganesh
 *
 */
@Service
public class ScheduleServiceImpl {
	@Autowired
	private DynamicSchedule dynamicSchedule;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	
	static Logger logger = Logger.getLogger(ScheduleServiceImpl.class);
	
	@SuppressWarnings("unchecked")
	public Map<Date, List<MetaIdentifierHolder>> getLatestBatch() throws ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Map<Date, List<MetaIdentifierHolder>> scheduleMap = new TreeMap<>();
		List<Schedule> scheduleInfo = (List<Schedule>) commonServiceImpl.findAllLatestWithoutAppUuid(MetaType.schedule);
		if(scheduleInfo != null) {
			for(Schedule schedule : scheduleInfo) {
				List<MetaIdentifierHolder> batchHolder = null;
				Date nextRunTime = getNextRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime(), schedule.getFrequencyType(), schedule.getFrequencyDetail());
				if(nextRunTime != null) {
					Object value = scheduleMap.get(nextRunTime);
					if(value != null) {
						batchHolder = (List<MetaIdentifierHolder>) value;
						batchHolder.add(schedule.getDependsOn());
						scheduleMap.put(nextRunTime, batchHolder);
					} else {
						batchHolder = new ArrayList<>();
						batchHolder.add(schedule.getDependsOn());
						scheduleMap.put(nextRunTime, batchHolder);
					}
				}
			}
		}
		
		return scheduleMap;
	}

	public Date getNextRunTime(Date startDate, Date endDate, Date previousRunTime, String frequencyType, List<String> frequencyDetail) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("EEE MMM dd hh:mm:ss z yyyy");
		Date currDate = simpleDateFormat.parse(new Date().toString());
		
		if (startDate.compareTo(currDate) > 0) { //"startDate is after currDate"					
            logger.info("can not schedule batch because start date '"+startDate+"' is after current date '"+currDate+"'.");
        } else if (startDate.compareTo(currDate) < 0 || startDate.compareTo(currDate) == 0) { //"startDate is before currDate OR startDate is equal to currDate"
           if (endDate.compareTo(currDate) > 0 || endDate.compareTo(currDate) == 0) { //"endDate is after currDate OR endDate is equal to currDate"
        		switch(frequencyType.toLowerCase()) {
	        		case "once" : return startDate;
	        		case "daily" : return getNextDailyRunTime(startDate, endDate, previousRunTime);
	        		case "weekly" : return getNextWeelyRunTime(startDate, endDate, previousRunTime, frequencyDetail);
	        		case "bi-weekly" : return getNextBiWeeklyRunTime(startDate, endDate, previousRunTime, frequencyDetail);
	        		case "monthly" : return getNextWeelyRunTime(startDate, endDate, previousRunTime, frequencyDetail);
	        		case "quarterly" : return getNextQuarterlyRunTime(startDate, endDate, previousRunTime, frequencyDetail);
	        		case "yearly" : return getNextYearlyRunTime(startDate, endDate, previousRunTime);
	        		default : return null;	
        		}
        	}
        } 		
		return currDate;
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
		//LocalDateTime.from(dt.toInstant()).plusDays(1);
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
		Map<Date, List<MetaIdentifierHolder>> scheduleMap = getLatestBatch();
		List<Batch> batchs = new ArrayList<>();
		 for(MetaIdentifierHolder batchHolder : new ArrayList<>(scheduleMap.values()).get(0)) {
			 MetaIdentifier batchMI = batchHolder.getRef();
			 Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchMI.getUuid(), batchMI.getVersion(), batchMI.getType().toString());
			 batchs.add(batch);
		 }
		dynamicSchedule.setNextExecutionTime(scheduleMap.keySet().toArray(new Date[scheduleMap.keySet().size()])[0]/*new Date(new Date().getTime() + (1000 * 20))*/, batchs);
	}
}

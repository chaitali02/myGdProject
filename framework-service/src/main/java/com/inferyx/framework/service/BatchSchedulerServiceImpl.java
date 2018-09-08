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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.BatchExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.Schedule;
import com.inferyx.framework.enums.RunMode;

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
	@Autowired
	private BatchServiceImpl batchServiceImpl;
	@Autowired
	private BatchViewServiceImpl batchViewServiceImpl;
	@Autowired
	private MongoTemplate mongoTemplate;
	
	static Logger logger = Logger.getLogger(BatchSchedulerServiceImpl.class);
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("EEE MMM dd hh:mm:ss z yyyy");
	
	/*@SuppressWarnings("unchecked")
	public Map<Date, String> getNextBatchExecTime() throws ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Date currDate = simpleDateFormat.parse(new Date().toString());
		Map<Date, String> scheduleMap = new TreeMap<>();
		List<Schedule> scheduleInfo = (List<Schedule>) commonServiceImpl.findAllLatestWithoutAppUuid(MetaType.schedule);
		if(scheduleInfo != null) {
			for(Schedule schedule : scheduleInfo) {
				Date nextRunTime = schedule.getNextRunTime();
				if(nextRunTime != null && nextRunTime.compareTo(currDate) > 0) {
					scheduleMap.put(nextRunTime, "tmp");
				} else {
					scheduleMap.put(currDate, "tmp");
				}
			}
		}		
		return scheduleMap;
	}*/

	@SuppressWarnings({ "unchecked", "null" })
	public Map<Date, List<Schedule>> getNextBatchSchedules() throws ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Date currDate = simpleDateFormat.parse(new Date().toString());
		Map<Date, List<Schedule>> scheduleMap = new TreeMap<>();
		List<Schedule> scheduleInfo = (List<Schedule>) commonServiceImpl.findAllLatestWithoutAppUuid(MetaType.schedule);
		if(scheduleInfo != null) {
			for(Schedule schedule : scheduleInfo) {
				Date nextRunTime = schedule.getNextRunTime();
				List<Schedule> nextBatchSchedules = null;
				if(nextRunTime != null /*&& nextRunTime.compareTo(currDate) > 0*/) {
					if(nextRunTime != null) {
						if(scheduleMap.get(nextRunTime) != null) {
							nextBatchSchedules.add(schedule);
							scheduleMap.put(nextRunTime, nextBatchSchedules);
						} else {
							nextBatchSchedules = new ArrayList<>();
							nextBatchSchedules.add(schedule);
							scheduleMap.put(nextRunTime, nextBatchSchedules);
						}
					}
				} else {
					scheduleMap.put(currDate, null);
				}
			}
		}		
		return scheduleMap;
	}
	
	public Date getNextRunTimeBySchedule(Schedule schedule) throws ParseException {
		switch(schedule.getFrequencyType().toUpperCase()) {
    		case "ONCE" : return schedule.getNextRunTime() == null ? schedule.getStartDate() : null;
    		case "HOURLY" : return getNextHourlyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime());
    		case "DAILY" : return getNextDailyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime());
    		case "WEEKLY" : return getNextWeelyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime(), schedule.getFrequencyDetail());
    		case "BIWEEKLY" : return getNextBiWeeklyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime(), schedule.getFrequencyDetail());
    		case "MONTHLY" : return getNextWeelyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime(), schedule.getFrequencyDetail());
    		case "QUARTERLY" : return getNextQuarterlyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime(), schedule.getFrequencyDetail());
    		case "YEARLY" : return getNextYearlyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime());
    		default : return null;	
		}
	}
	
//	public Date getNextRunTime(Date startDate, Date endDate, Date previousRunTime, String frequencyType, List<String> frequencyDetail) throws ParseException {
//		Date currDate = simpleDateFormat.parse(new Date().toString());
//		
//		if (startDate.compareTo(currDate) < 0) { //"startDate is before currDate"					
//            logger.info("Start date '"+startDate+"' is before current date '"+currDate+"'. Setting to current date");
//            return currDate;
//        } else {
//        		switch(frequencyType.toUpperCase()) {
//	        		case "ONCE" : return startDate;
//	        		case "HOURLY" : return getNextHourlyRunTime(startDate, endDate, previousRunTime);
//	        		case "DAILY" : return getNextDailyRunTime(startDate, endDate, previousRunTime);
//	        		case "WEEKLY" : return getNextWeelyRunTime(startDate, endDate, previousRunTime, frequencyDetail);
//	        		case "BIWEEKLY" : return getNextBiWeeklyRunTime(startDate, endDate, previousRunTime, frequencyDetail);
//	        		case "MONTHLY" : return getNextWeelyRunTime(startDate, endDate, previousRunTime, frequencyDetail);
//	        		case "QUARTERLY" : return getNextQuarterlyRunTime(startDate, endDate, previousRunTime, frequencyDetail);
//	        		case "YEARLY" : return getNextYearlyRunTime(startDate, endDate, previousRunTime);
//	        		default : return null;	
//        		}
//        	} 		
//	}

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
		Date nextExecutionTime = getNextBatchExecTime();
		if(nextExecutionTime != null) {
			batchTriggerServiceImpl.setNextExecutionTime(nextExecutionTime);
		} else {
			batchTriggerServiceImpl.setNextExecutionTime(null);
		}
	}
	
	public void runBatches() throws Exception {		
		//Set the next time the scheduler to start.
		System.out.println("nextExecutionTime: "+batchTriggerServiceImpl.getNextExecutionTime());
		System.out.println("lastExecutionTime: "+batchTriggerServiceImpl.getLastExecutionTime());
		Map<Date, List<Schedule>> scheduledBatches = getNextBatchSchedules(); 
		  
		List<Schedule> schedules = scheduledBatches.get(scheduledBatches.keySet().toArray(new Date[scheduledBatches.keySet().size()])[0]);
		if(schedules != null) {
			logger.info("Scheduler triggered. Submitting batch...");
		    for(Schedule schedule : schedules) {
		  	  	MetaIdentifier batchMI = schedule.getDependsOn().getRef();
		  	  	BatchExec batchExec = batchServiceImpl.create(batchMI.getUuid(), batchMI.getVersion(), null, null, RunMode.BATCH);
		    	batchServiceImpl.submitBatch(batchMI.getUuid(), batchMI.getVersion(), batchExec, null, null, RunMode.BATCH);
		    }
		    updateScheduleForNextRunTime(schedules);
		} else if(scheduledBatches != null && schedules == null) {
			logger.info("No batch is scheduled at "+scheduledBatches.keySet().toArray(new Date[scheduledBatches.keySet().size()])[0]);
		}
//		Map<Date, String> scheduleMap = getNextBatchExecTime();
//		batchTriggerServiceImpl.setNextExecutionTime(scheduleMap.keySet().toArray(new Date[scheduleMap.keySet().size()])[0]); //Change null with map position 0 but need to handle nulls.
		setSchedulingTrigger();
   }

	private void updateScheduleForNextRunTime(List<Schedule> schedules) throws Exception {
//		batchViewServiceImpl.save(schedule)
//		BatchView batchView = new BatchView();
//		batchView.setBatchChg("N");
//		batchView.setScheduleInfo(schedules);
//		batchViewServiceImpl.save(batchView);
		for(Schedule schedule : schedules) {
			Date nextExecutionTime = getNextRunTimeBySchedule(schedule);
			if(nextExecutionTime != null) {
				batchViewServiceImpl.save(schedule);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Date getNextBatchExecTime() throws ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Date currDate = simpleDateFormat.parse(new Date().toString());
	/*	Map<Date, String> scheduleMap = new TreeMap<>();
		List<Schedule> scheduleInfo = (List<Schedule>) commonServiceImpl.findAllLatestWithoutAppUuid(MetaType.schedule);
		if(scheduleInfo != null) {
			for(Schedule schedule : scheduleInfo) {
				Date nextRunTime = schedule.getNextRunTime();
				if(nextRunTime != null && nextRunTime.compareTo(currDate) > 0) {
					scheduleMap.put(nextRunTime, "tmp");
				} else {
					scheduleMap.put(currDate, "tmp");
				}
			}
		}	*/	
		
		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("active");
		query.fields().include("name");
		query.fields().include("appInfo");
		query.fields().include("createdBy");
		query.fields().include("createdOn");
		query.fields().include("desc");
		query.fields().include("tags");
		query.fields().include("published");
		query.fields().include("startDate");
		query.fields().include("endDate");
		query.fields().include("nextRunTime");
		query.fields().include("frequencyType");
		query.fields().include("frequencyDetail");
		query.fields().include("dependsOn");
		
		query.addCriteria(Criteria.where("dependsOn.ref.uuid").gte(currDate));		
		
		List<Schedule> schedulerList = mongoTemplate.find(query, Schedule.class);
		
		Set<Date> dateSet = new TreeSet<>();
		for(Schedule schedule : schedulerList) {
			dateSet.add(schedule.getNextRunTime());
		}		
		return !dateSet.isEmpty() ? dateSet.toArray(new Date[dateSet.size()])[0] : null;
	}
	
	/*@SuppressWarnings("unchecked")
	public Map<Date, String> getNextBatchSchedulesx(String executionTime) throws ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Date currDate = simpleDateFormat.parse(new Date().toString());
		Map<Date, String> scheduleMap = new TreeMap<>();
		List<Schedule> scheduleInfo = (List<Schedule>) commonServiceImpl.findAllLatestWithoutAppUuid(MetaType.schedule);
		if(scheduleInfo != null) {
			for(Schedule schedule : scheduleInfo) {
				Date nextRunTime = schedule.getNextRunTime();
				if(nextRunTime != null) {
					scheduleMap.put(nextRunTime, "tmp");
				} else {
					scheduleMap.put(currDate, "tmp");
				}
			}
		}		
		
		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("active");
		query.fields().include("name");
		query.fields().include("appInfo");
		query.fields().include("createdBy");
		query.fields().include("createdOn");
		query.fields().include("desc");
		query.fields().include("tags");
		query.fields().include("published");
		query.fields().include("startDate");
		query.fields().include("endDate");
		query.fields().include("nextRunTime");
		query.fields().include("frequencyType");
		query.fields().include("frequencyDetail");
		query.fields().include("dependsOn");
		
		query.addCriteria(Criteria.where("dependsOn.ref.uuid").gte(executionTime));
		
		
		List<Schedule> paramLists = mongoTemplate.find(query, Schedule.class);
		
		List<Schedule> latestParamList = new ArrayList<>();
		Set<String> uuidSet = new HashSet<>();
		for(Schedule paramList : paramLists) {
			if(!uuidSet.contains(paramList.getUuid())) {
				Schedule latestPL = (Schedule) getLatestByUuid(paramList.getUuid(), MetaType.paramlist.toString(), "N");
				latestParamList.add(latestPL);
				uuidSet.add(paramList.getUuid());
			}
		}		
		return scheduleMap;
	}*/
}

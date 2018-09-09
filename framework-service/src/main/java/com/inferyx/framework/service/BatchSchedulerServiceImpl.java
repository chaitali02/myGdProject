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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.BatchExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
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

	/*@SuppressWarnings({ "unchecked", "null" })
	public Map<Date, List<Schedule>> getNextBatchSchedules() throws ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Date currDate = simpleDateFormat.parse(new Date().toString());
		Map<Date, List<Schedule>> scheduleMap = new TreeMap<>();
		List<Schedule> scheduleInfo = (List<Schedule>) commonServiceImpl.findAllLatestWithoutAppUuid(MetaType.schedule);
		if(scheduleInfo != null) {
			for(Schedule schedule : scheduleInfo) {
				Date nextRunTime = schedule.getNextRunTime();
				List<Schedule> nextBatchSchedules = null;
				if(nextRunTime != null && nextRunTime.compareTo(currDate) > 0) {
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
	}*/
	
	public Date getNextRunTimeBySchedule(Schedule schedule) throws ParseException {
		switch(schedule.getFrequencyType().toUpperCase()) {
    		case "ONCE" : return schedule.getNextRunTime() == null ? schedule.getStartDate() : null;
    		case "HOURLY" : return getNextHourlyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime());
    		case "DAILY" : return getNextDailyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime());
    		case "WEEKLY" : return getNextWeelyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime(), schedule.getFrequencyDetail());
    		case "BIWEEKLY" : return getNextBiWeeklyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime(), schedule.getFrequencyDetail());
    		case "MONTHLY" : return getNextMonthlyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime(), schedule.getFrequencyDetail());
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

	private Date getNextMonthlyRunTime(Date startDate, Date endDate, Date previousRunTime, List<String> frequencyDetail) throws ParseException {
		Set<Date> sortedDates = new TreeSet<>();
		for(String date : frequencyDetail) {
			Date tempDate = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").parse(date+" "+startDate.getHours()+":"+startDate.getMinutes()+":"+startDate.getSeconds());;
			Date actualDate = simpleDateFormat.parse(tempDate.toString());
			sortedDates.add(actualDate);
		}
		if(previousRunTime != null) {
			for(Date nextRunTime : Collections.list(Collections.enumeration(sortedDates))) {
				if(nextRunTime.compareTo(endDate) <= 0 && nextRunTime.compareTo(previousRunTime) > 0) {
					return nextRunTime;
				} 
			}
			return null; 
		} else {
			return Collections.list(Collections.enumeration(sortedDates)).get(0);//sortedDates.toArray(new Date[sortedDates.size()])[0];
		}
	}
	
	private Date getNextHourlyRunTime(Date startDate, Date endDate, Date previousRunTime) {
		if(previousRunTime != null) {
			Date nextRunTime = DateUtils.addHours(previousRunTime, 1);
			if(nextRunTime.compareTo(endDate) <= 0) {
				return nextRunTime;
			} else {
				return null; 
			}
		} else {
			return startDate;
		}
	}

	private Date getNextYearlyRunTime(Date startDate, Date endDate, Date previousRunTime) {
		if(previousRunTime != null) {
			Date nextRunTime = DateUtils.addYears(previousRunTime, 1);
			if(nextRunTime.compareTo(endDate) <= 0) {
				return nextRunTime;
			} else {
				return null; 
			}
		} else {
			return startDate;
		}
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
		List<Schedule> schedules = getNextBatchSchedules(batchTriggerServiceImpl.getNextExecutionTime(), batchTriggerServiceImpl.getLastExecutionTime()); 
		  
		if(schedules != null) {
			logger.info("Scheduler triggered. Submitting batch...");
		    for(Schedule schedule : schedules) {
		  	  	MetaIdentifier batchMI = schedule.getDependsOn().getRef();
		  	  	BatchExec batchExec = batchServiceImpl.create(batchMI.getUuid(), batchMI.getVersion(), null, null, RunMode.BATCH);
		    	batchServiceImpl.submitBatch(batchMI.getUuid(), batchMI.getVersion(), batchExec, null, null, RunMode.BATCH);
		    }
		    updateScheduleForNextRunTime(schedules);
		} else if(schedules == null) {
			logger.info("No batch is scheduled at "+batchTriggerServiceImpl.getNextExecutionTime());
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
				schedule.setNextRunTime(nextExecutionTime.toString());
				batchViewServiceImpl.save(schedule);
			}
		}
	}

	public Date getNextBatchExecTime() throws ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonProcessingException {
		Date currDate = simpleDateFormat.parse(new Date().toString());
		Criteria criteria = new Criteria();
		List<Criteria> criteriaList = new ArrayList<Criteria>();
		
	/*	Query query = new Query();
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
		
		query.addCriteria(Criteria.where("nextRunTime").gte(currDate));	*/
		criteriaList.add(where("nextRunTime").gte(currDate));
		Criteria criteria2 = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
		
		Aggregation ruleExecAggr = newAggregation(match(criteria2), group("uuid").max("version").as("version"));
		AggregationResults ruleExecResults = mongoTemplate.aggregate(ruleExecAggr, MetaType.schedule.toString().toLowerCase(), Schedule.class);
		List<Schedule> schedulerList = ruleExecResults.getMappedResults();		
		Set<Date> dateSet = new TreeSet<>();
		for(Schedule schedule : schedulerList) {
			Schedule schedule2 = (Schedule) commonServiceImpl.getLatestByUuid(schedule.getId(), MetaType.schedule.toString(), "N");
			dateSet.add(schedule2.getNextRunTime());
		}		
		return !dateSet.isEmpty() ? dateSet.toArray(new Date[dateSet.size()])[0] : null;
	}
	
	public List<Schedule> getNextBatchSchedules(Date currentTriggerTime, Date lastExecutionTime) throws ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonProcessingException {
		Date currDate = simpleDateFormat.parse(new Date().toString());
		Criteria criteria = new Criteria();
		List<Criteria> criteriaList = new ArrayList<Criteria>();
		
		/*Query query = new Query();
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
		query.fields().include("dependsOn");*/
		
		if(lastExecutionTime != null) {
/*			query.addCriteria(Criteria.where("nextRunTime").gt(lastExecutionTime)
					.andOperator(Criteria.where("nextRunTime").lte(currDate)));*/
			
			criteriaList.add(where("nextRunTime").gt(lastExecutionTime)
					.andOperator(Criteria.where("nextRunTime").lte(currDate)));
		} else {
	/*		query.addCriteria(Criteria.where("nextRunTime").gte(currentTriggerTime)
					.andOperator(Criteria.where("nextRunTime").lte(currDate)));*/

			criteriaList.add(where("nextRunTime").gte(currentTriggerTime)
					.andOperator(Criteria.where("nextRunTime").lte(currDate)));
		}
		
		
		Criteria criteria2 = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
		
		Aggregation ruleExecAggr = newAggregation(match(criteria2), group("uuid").max("version").as("version"));
		AggregationResults ruleExecResults = mongoTemplate.aggregate(ruleExecAggr, MetaType.schedule.toString().toLowerCase(), Schedule.class);
		List<Schedule> schedulerList = ruleExecResults.getMappedResults();		
		List<Schedule> schedules = new ArrayList<>();
		for(Schedule schedule : schedulerList) {
			Schedule schedule2 = (Schedule) commonServiceImpl.getLatestByUuid(schedule.getId(), MetaType.schedule.toString(), "N");
			schedules.add(schedule2);
		}		
		return schedules;
	}
}

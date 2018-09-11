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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
	private BatchServiceImpl batchServiceImpl;
	@Autowired
	private BatchViewServiceImpl batchViewServiceImpl;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	
	static Logger logger = Logger.getLogger(BatchSchedulerServiceImpl.class);
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("EEE MMM dd HH:mm:ss z yyyy");
	
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
	
	private Date getNextMonthlyRunTime(Date startDate, Date endDate, Date previousRunTime, List<String> frequencyDetail) throws ParseException {
		Set<Date> sortedDates = new TreeSet<>();
		for(String date : frequencyDetail) {
			Date tempDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse(date+" "+startDate.getHours()+":"+startDate.getMinutes()+":"+startDate.getSeconds());;
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
	
	private Date getNextHourlyRunTime(Date startDate, Date endDate, Date previousRunTime) throws ParseException {
		if(previousRunTime != null) {
			Date nextRunTime = DateUtils.addHours(previousRunTime, 1);
			if(nextRunTime.compareTo(endDate) <= 0) {
				return simpleDateFormat.parse(nextRunTime.toString());
			} else {
				return null; 
			}
		} else {
			return startDate;
		}
	}

	private Date getNextYearlyRunTime(Date startDate, Date endDate, Date previousRunTime) throws ParseException {
		if(previousRunTime != null) {
			Date nextRunTime = DateUtils.addYears(previousRunTime, 1);
			if(nextRunTime.compareTo(endDate) <= 0) {
				return simpleDateFormat.parse(nextRunTime.toString());
			} else {
				return null; 
			}
		} else {
			return startDate;
		}
	}

	private Date getNextQuarterlyRunTime(Date startDate, Date endDate, Date previousRunTime, List<String> frequencyDetail) {
		// TODO Auto-generated method stub
		return null;
	}

	private Date getNextBiWeeklyRunTime(Date startDate, Date endDate, Date previousRunTime, List<String> frequencyDetail) throws ParseException {
	
		Date nextRunTime = null;
		Date currDate = simpleDateFormat.parse(new Date().toString());
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
		int weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);

		Date weekStartDate = null;
		Date weekEndDate = null;
		if(weekOfYear%2 == 0) {
			Date tempStartDate = getWeekStartDate(cal.get(Calendar.DAY_OF_WEEK), startDate, currDate);
			tempStartDate.setHours(startDate.getHours());
			tempStartDate.setMinutes(startDate.getMinutes());
			tempStartDate.setSeconds(startDate.getSeconds());
			weekStartDate = simpleDateFormat.parse(tempStartDate.toString());
			
			Date tempEndDate = getWeekStartDate(cal.get(Calendar.DAY_OF_WEEK), startDate, currDate);
			tempEndDate.setHours(startDate.getHours());
			tempEndDate.setMinutes(startDate.getMinutes());
			tempEndDate.setSeconds(startDate.getSeconds());
			weekEndDate = simpleDateFormat.parse(tempEndDate.toString());
			
			
		} else {
			nextRunTime = getNextWeelyRunTime(startDate, endDate, previousRunTime, frequencyDetail);
		}
		
		if(nextRunTime.compareTo(endDate) <= 0) {
			return nextRunTime;
		} else {
			return null; 
		}		
	}
	
	/*public Date getBiWeeklyNextRunTime(Date weekStartDate, Date weekEndDate, Date scheduleStartDate, Date currDate) {
		
	}*/

	private Date getNextWeelyRunTime(Date startDate, Date endDate, Date previousRunTime, List<String> frequencyDetail) throws ParseException {
		//if(previousRunTime != null) {
			Date currDate = simpleDateFormat.parse(new Date().toString());
			Calendar cal = Calendar.getInstance();
			cal.setTime(currDate);
//			System.out.println("WoM: "+cal.get(Calendar.WEEK_OF_MONTH)); //3
//			System.out.println("DoM: "+cal.get(Calendar.DAY_OF_MONTH)); //10
//			System.out.println("DoW: "+cal.get(Calendar.DAY_OF_WEEK)); //2
//			System.out.println("WoY: "+cal.get(Calendar.WEEK_OF_YEAR)); //37
//			System.out.println("FDoW: "+cal.getFirstDayOfWeek());
			
			Date nextRunTime = null;
			for(String day : frequencyDetail) {
				if((Integer.parseInt(day)+1) >= cal.get(Calendar.DAY_OF_WEEK)) {
					Date tempDate = getDateByDayOfWeek((Integer.parseInt(day)+1), startDate, currDate);//new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").parse(date+" "+startDate.getHours()+":"+startDate.getMinutes()+":"+startDate.getSeconds());;
					tempDate.setHours(startDate.getHours());
					tempDate.setMinutes(startDate.getMinutes());
					tempDate.setSeconds(startDate.getSeconds());
					nextRunTime = simpleDateFormat.parse(tempDate.toString());
				}
			}
			
			if(nextRunTime.compareTo(currDate) > 0 && nextRunTime.compareTo(endDate) <= 0) {
				return nextRunTime;
			} else {
				return null; 
			}
//		} else {
//			return startDate;
//		}
	}

	public Date getDateByDayOfWeek(int doW, Date startDate, Date currDate) {
		switch(doW) {
		case 1 : return DateUtils.addDays(currDate, (6-doW)); //SUNDAY
		case 2 : return DateUtils.addDays(currDate, (6-doW)); //MONDAY
		case 3 : return DateUtils.addDays(currDate, (6-doW)); //TEUSDAY
		case 4 : return DateUtils.addDays(currDate, (6-doW)); //WEDNESDAY
		case 5 : return DateUtils.addDays(currDate, (6-doW)); //THURSDAY
		case 6 : return DateUtils.addDays(currDate, (6-doW)); //FRIDAY
		case 7 : return DateUtils.addDays(currDate, (6-doW)); //SATURDAY
		default : return null;
		}
	}
	
	public Date getWeekStartDate(int doW, Date startDate, Date currDate) {
		switch(doW) {
		case 1 : return DateUtils.addDays(currDate, (1-doW)); //SUNDAY
		case 2 : return DateUtils.addDays(currDate, (1-doW)); //MONDAY
		case 3 : return DateUtils.addDays(currDate, (1-doW)); //TEUSDAY
		case 4 : return DateUtils.addDays(currDate, (1-doW)); //WEDNESDAY
		case 5 : return DateUtils.addDays(currDate, (1-doW)); //THURSDAY
		case 6 : return DateUtils.addDays(currDate, (1-doW)); //FRIDAY
		case 7 : return DateUtils.addDays(currDate, (1-doW)); //SATURDAY
		default : return null;
		}
	}
	
	public Date getWeekEndDate(int doW, Date startDate, Date currDate) {
		switch(doW) {
		case 1 : return DateUtils.addDays(currDate, (doW+1)); //SUNDAY
		case 2 : return DateUtils.addDays(currDate, (doW+2)); //MONDAY
		case 3 : return DateUtils.addDays(currDate, (doW+3)); //TEUSDAY
		case 4 : return DateUtils.addDays(currDate, (doW+4)); //WEDNESDAY
		case 5 : return DateUtils.addDays(currDate, (doW+5)); //THURSDAY
		case 6 : return DateUtils.addDays(currDate, (doW+6)); //FRIDAY
		case 7 : return DateUtils.addDays(currDate, (doW+7)); //SATURDAY
		default : return null;
		}
	}
	
	private Date getNextDailyRunTime(Date startDate, Date endDate, Date previousRunTime) throws ParseException {
		if(previousRunTime != null) {
			Date nextRunTime = simpleDateFormat.parse(new Date(previousRunTime.getTime() + (1000 * 60 * 60 * 24)).toString());
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
		try {
			Date nextExecutionTime = getNextBatchExecTime();
			if(nextExecutionTime != null) {
				batchTriggerServiceImpl.setNextExecutionTime(nextExecutionTime);
			} else {
				batchTriggerServiceImpl.setNextExecutionTime(null);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			batchTriggerServiceImpl.setNextExecutionTime(null);
		}
	}
	
	public void runBatches(){		
		//Set the next time the scheduler to start.
		try {
			List<Schedule> schedules = getCurrentSchedules(batchTriggerServiceImpl.getNextExecutionTime(), batchTriggerServiceImpl.getLastExecutionTime()); 
			  
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
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				setSchedulingTrigger();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
   }

	private void updateScheduleForNextRunTime(List<Schedule> schedules) throws Exception {
		for(Schedule schedule : schedules) {
			Date nextExecutionTime = getNextRunTimeBySchedule(schedule);
			if (nextExecutionTime != null) {
				schedule.setNextRunTime(nextExecutionTime.toString());				
			}
			else {
				schedule.setNextRunTime(null);
			}				
			//Create a new schedule object
			schedule.setId(null);
			schedule.setVersion(null);
			batchViewServiceImpl.save(schedule);
		}
	}

	public Date getNextBatchExecTime() throws ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonProcessingException {

		Date currDate = simpleDateFormat.parse(new Date().toString());

//		Criteria criteria = new Criteria();
//		List<Criteria> criteriaList = new ArrayList<Criteria>();		
//		criteriaList.add(where("nextRunTime").gte(currDate));
//		Criteria criteria2 = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
//		Aggregation scheduleAggr = newAggregation(match(criteria2), group("uuid").max("version").as("version"));
//		AggregationResults scheduleAggrResults = mongoTemplate.aggregate(scheduleAggr, MetaType.schedule.toString().toLowerCase(), Schedule.class);
//		List<Schedule> schedulerList = scheduleAggrResults.getMappedResults();		
//		Set<Date> dateSet = new TreeSet<>();
//		for(Schedule schedule : schedulerList) {
//			Schedule schedule2 = (Schedule) commonServiceImpl.getLatestByUuid(schedule.getId(), MetaType.schedule.toString(), "N");
//			dateSet.add(schedule2.getNextRunTime());
//		}		
//		return !dateSet.isEmpty() ? dateSet.toArray(new Date[dateSet.size()])[0] : null;
		
		MatchOperation filterSchedule = match(new Criteria("nextRunTime").gte(currDate));
		GroupOperation groupByUuid = group("uuid").max("version").as("version").max("nextRunTime").as("nextRunTime");
		SortOperation sortByNextRunTime = sort(new Sort(Direction.ASC, "nextRunTime"));
		LimitOperation limitToOnlyFirstDoc = limit(1);
		Aggregation scheduleAggr = newAggregation(filterSchedule, groupByUuid, sortByNextRunTime, limitToOnlyFirstDoc);
		AggregationResults<Schedule> scheduleAggrResults = mongoTemplate.aggregate(scheduleAggr, MetaType.schedule.toString().toLowerCase(), Schedule.class);
		Schedule schedule = (Schedule) scheduleAggrResults.getUniqueMappedResult();	
		return schedule.getNextRunTime();

	}
	
	public List<Schedule> getCurrentSchedules(Date currentTriggerTime, Date lastExecutionTime) throws ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonProcessingException {
		Date currDate = simpleDateFormat.parse(new Date().toString());
		
		if(lastExecutionTime == null) {
			lastExecutionTime = currentTriggerTime; 
		}

		Criteria criteria = new Criteria();
		List<Criteria> criteriaList = new ArrayList<Criteria>();		
		criteriaList.add(where("nextRunTime").gte(lastExecutionTime).andOperator(Criteria.where("nextRunTime").lte(currentTriggerTime)));		
		
		Criteria criteria2 = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
		
		Aggregation ruleExecAggr = newAggregation(match(criteria2), group("uuid").max("version").as("version"));
		AggregationResults ruleExecResults = mongoTemplate.aggregate(ruleExecAggr, MetaType.schedule.toString().toLowerCase(), Schedule.class);
		List<Schedule> schedulerList = ruleExecResults.getMappedResults();		
		List<Schedule> schedules = new ArrayList<>();
		for(Schedule schedule : schedulerList) {
			Schedule schedule2 = (Schedule) commonServiceImpl.getLatestByUuid(schedule.getId(), MetaType.schedule.toString(), "N");
			schedules.add(schedule2);
		}
		
//		MatchOperation scheduleFilter = null;
//		if(lastExecutionTime != null) {
//			scheduleFilter = match(new Criteria("nextRunTime").gt(lastExecutionTime).andOperator(new Criteria("nextRunTime").lte(currDate)));
//		} else {
//			scheduleFilter = match(new Criteria("nextRunTime").gte(currentTriggerTime).andOperator(new Criteria("nextRunTime").lte(currDate)));
//		}
//		
//		GroupOperation schedulerGroupByUuid = group("uuid").max("version").as("version").max("nextRunTime").as("nextRunTime");
//		SortOperation schedulerSortDirection = sort(new Sort(Direction.ASC, "nextRunTime"));
//		Aggregation aggregateOnSchedule = newAggregation(scheduleFilter, schedulerGroupByUuid, schedulerSortDirection);
//		AggregationResults<Schedule> scheduleAggrResult = mongoTemplate.aggregate(aggregateOnSchedule, MetaType.schedule.toString().toLowerCase(), Schedule.class);
//		
//		List<Schedule> schedules = new ArrayList<>();
//		for(Schedule schedule : scheduleAggrResult.getMappedResults()) {
//			Schedule resolvedSchedule = getSchedule(schedule.getId(), schedule.getVersion(), schedule.getNextRunTime());
//			if(resolvedSchedule != null)
//				schedules.add(resolvedSchedule);
//		}
		return schedules;
	}
	
	public Schedule getSchedule(String uuid, String version, Date nextRunTime) {
		Query query = new Query();
		query.fields().exclude("_id");
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
		
		if(uuid != null)
			query.addCriteria(Criteria.where("uuid").is(uuid));
		if(version != null)
			query.addCriteria(Criteria.where("version").is(version));
		if(nextRunTime != null)
			query.addCriteria(Criteria.where("nextRunTime").is(nextRunTime));
		
		List<Schedule> schedules = mongoTemplate.find(query, Schedule.class);
		if(!schedules.isEmpty()) {
			return schedules.get(0);
		} else {
			return null;
		}
	}
	
	public void init() {
		try {
			setSchedulingTrigger();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

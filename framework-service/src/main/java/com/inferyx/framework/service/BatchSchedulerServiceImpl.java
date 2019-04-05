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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
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
import com.inferyx.framework.domain.Batch;
import com.inferyx.framework.domain.BatchExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Schedule;
import com.inferyx.framework.domain.User;
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
	@Autowired
	private SecurityServiceImpl securityServiceImpl;
	@Autowired
	private FrameworkThreadServiceImpl frameworkThreadServiceImpl;
	
	static Logger logger = Logger.getLogger(BatchSchedulerServiceImpl.class);
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("EEE MMM dd HH:mm:ss z yyyy");
	
	public Date getNextRunTimeBySchedule(Schedule schedule) throws ParseException {
		switch(schedule.getFrequencyType().toUpperCase()) {
    		case "ONCE" : return schedule.getNextRunTime() == null ? schedule.getStartDate() : null;
    		case "HOURLY" : return getNextHourlyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime(), schedule.getFrequencyDetail());
    		case "DAILY" : return getNextDailyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime());
    		case "WEEKLY" : return getNextWeelyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime(), schedule.getFrequencyDetail());
    		case "BIWEEKLY" : return getNextBiWeeklyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime(), schedule.getFrequencyDetail());
    		case "MONTHLY" : return getNextMonthlyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime(), schedule.getFrequencyDetail());
    		case "QUARTERLY" : return getNextQuarterlyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime(), schedule.getFrequencyDetail());
    		case "YEARLY" : return getNextYearlyRunTime(schedule.getStartDate(), schedule.getEndDate(), schedule.getNextRunTime());
    		default : return null;	
		}
	}
	
	@SuppressWarnings("deprecation")
	private Date getNextMonthlyRunTime(Date startDate, Date endDate, Date previousRunTime, List<String> frequencyDetail) throws ParseException {
		Date tempCurrDate = new Date();
		Date currDate = simpleDateFormat.parse(tempCurrDate.toString());
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
		Set<Date> sortedDates = new TreeSet<>();
		for(String day : frequencyDetail) {
			Date tempDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse((cal.get(Calendar.MONTH)+1)+"-"+day+"-"+cal.get(Calendar.YEAR)+" "+startDate.getHours()+":"+startDate.getMinutes()+":"+startDate.getSeconds());;
			Date actualDate = simpleDateFormat.parse(tempDate.toString());
			sortedDates.add(actualDate);
		}
//		if(previousRunTime != null) {
			for(Date nextRunTime : Collections.list(Collections.enumeration(sortedDates))) {				
				if(nextRunTime.compareTo(endDate) <= 0 && nextRunTime.compareTo(currDate) >= 0) {
					return nextRunTime;
				} 
			}
			return null; 
//		} else {
//			return Collections.list(Collections.enumeration(sortedDates)).get(0);//sortedDates.toArray(new Date[sortedDates.size()])[0];
//		}
	}
	
	private Date getNextHourlyRunTime(Date startDate, Date endDate, Date previousRunTime, List<String> frequencyDetail)
			throws ParseException {
		Date tempCurrDate = new Date();
		Date nextRunTime = new Date();
		for (String hours : frequencyDetail) {
			startDate.setHours(Integer.parseInt(hours));
			Date date = startDate;
			if (date.compareTo(tempCurrDate) >= 0) {
				nextRunTime = date;
				break;
			}
		}
		if (nextRunTime != null && nextRunTime.compareTo(endDate) <= 0) {
			return nextRunTime;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @param previousRunTime
	 * @return
	 * @throws ParseException
	 */
	private Date getNextYearlyRunTime(Date startDate, Date endDate, Date previousRunTime) throws ParseException {
		Date tempCurrDate = new Date();
		Date currDate = simpleDateFormat.parse(tempCurrDate.toString());
//		if(previousRunTime != null) {
			Date nextRunTime = DateUtils.addYears(startDate.compareTo(currDate) >= 0 ? startDate : currDate, 1);
			if(nextRunTime.compareTo(endDate) <= 0) {
				return simpleDateFormat.parse(nextRunTime.toString());
			} else {
				return null; 
			}
//		} else {
//			return startDate;
//		}
	}

	/**
	 * Shall provide run time after a quarter compared to current data
	 * @param startDate
	 * @param endDate
	 * @param previousRunTime
	 * @return
	 * @throws ParseException
	 */
	private Date getNextQuarterlyRunTime(Date startDate, Date endDate, Date previousRunTime, List<String> frequencyDetail) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("EEE MMM dd HH:mm:ss z yyyy");
		Date tempCurrDate = new Date();
		Date currDate = simpleDateFormat.parse(tempCurrDate.toString());

		Date nextRunTime = null;

		currDate.setHours(startDate.getHours());
		currDate.setMinutes(startDate.getMinutes());
		currDate.setSeconds(startDate.getSeconds());
		
		int currentQuarter = getQuarter(currDate);
		if(frequencyDetail.contains(currentQuarter+"")) {
			nextRunTime = currDate;
		} else {
			nextRunTime = getNextQuarterDate(currDate, currentQuarter, frequencyDetail);
		}
		
		logger.info("Quarterly Next Run Time : " + nextRunTime);
		
		if(nextRunTime!=null && nextRunTime.compareTo(endDate) <= 0 && nextRunTime.compareTo(startDate) >= 0) {
			return nextRunTime;
		} else {
			return null; 
		}
	}

	public Date getNextQuarterDate(Date currDate, int currentQuarter, List<String> frequencyDetail) {
		for(String quarter : frequencyDetail) {
			int qtr = Integer.parseInt(quarter);
			if(qtr > currentQuarter) {
				int result = 3 * (qtr - currentQuarter);
				return DateUtils.addMonths(currDate, result);
			} else {
				 switch(qtr) {
				 case 0 : return DateUtils.addMonths(currDate, 3);
				 case 1 : return DateUtils.addMonths(currDate, 6);
				 case 2 : return DateUtils.addMonths(currDate, 9);
				 case 3 : return DateUtils.addMonths(currDate, 12);
				 }
			}
		}
		return null;
	}
	
	public int getQuarter(Date date) {
		switch(date.getMonth()) {
		case 0 : return 0;
		case 1 : return 0;
		case 2 : return 0;
		
		case 3 : return 1;
		case 4 : return 1;
		case 5 : return 1;

		case 6 : return 2;
		case 7 : return 2;
		case 8 : return 2;
		
		case 9 : return 3;
		case 10 : return 3;
		case 11 : return 3;
		
		default : return -1;
		}
	}
	
	@SuppressWarnings("deprecation")
	private Date getNextBiWeeklyRunTime(Date startDate, Date endDate, Date previousRunTime, List<String> frequencyDetail) throws ParseException {
		Date tempCurrDate = new Date();
		Date currDate = simpleDateFormat.parse(tempCurrDate.toString());
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
	
		cal.set(Calendar.DAY_OF_WEEK, cal.getActualMinimum(Calendar.DAY_OF_WEEK));
		Date firstDayOfTheWeek = cal.getTime();
		firstDayOfTheWeek.setHours(startDate.getHours());
		firstDayOfTheWeek.setMinutes(startDate.getMinutes());
		firstDayOfTheWeek.setSeconds(startDate.getSeconds());
		
		cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
		Date lastDayOfTheWeek = cal.getTime();
		lastDayOfTheWeek.setHours(startDate.getHours());
		lastDayOfTheWeek.setMinutes(startDate.getMinutes());
		lastDayOfTheWeek.setSeconds(startDate.getSeconds());
		

		Date nextRunTime = getBiWeekDate(firstDayOfTheWeek, lastDayOfTheWeek, currDate, frequencyDetail);
		if(nextRunTime.compareTo(endDate) <= 0) {
			return nextRunTime;
		} else {
			return null; 
		}		
	}
	
	public Date getBiWeekDate(Date firstDayOfTheWeek, Date lastDayOfTheWeek, Date currDate, List<String> frequencyDetail) {
		for(String day : frequencyDetail) {
			Date date = DateUtils.addDays(firstDayOfTheWeek, Integer.parseInt(day));
			if(date.compareTo(firstDayOfTheWeek) >= 0 && date.compareTo(lastDayOfTheWeek) <= 0 && date.compareTo(currDate) >= 0) {
				return date;
			} 
		}
		Date nextWeekStartDate = DateUtils.addDays(lastDayOfTheWeek, 1);
		Date nextWeekEndDate = DateUtils.addDays(nextWeekStartDate, 6);
		return getBiWeekDate(nextWeekStartDate, nextWeekEndDate, currDate, frequencyDetail);
	}
	
	@SuppressWarnings("deprecation")
	private Date getNextWeelyRunTime(Date startDate, Date endDate, Date previousRunTime, List<String> frequencyDetail) throws ParseException {
		
		Date tempCurrDate = new Date();
		Date currDate = simpleDateFormat.parse(tempCurrDate.toString());
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
	
		cal.set(Calendar.DAY_OF_WEEK, cal.getActualMinimum(Calendar.DAY_OF_WEEK));
		Date firstDayOfTheWeek = cal.getTime();
		firstDayOfTheWeek.setHours(startDate.getHours());
		firstDayOfTheWeek.setMinutes(startDate.getMinutes());
		firstDayOfTheWeek.setSeconds(startDate.getSeconds());
		
		cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
		Date lastDayOfTheWeek = cal.getTime();
		lastDayOfTheWeek.setHours(startDate.getHours());
		lastDayOfTheWeek.setMinutes(startDate.getMinutes());
		lastDayOfTheWeek.setSeconds(startDate.getSeconds());
		
		Date nextRunTime = null;
		for(String day : frequencyDetail) {
			Date date = DateUtils.addDays(firstDayOfTheWeek, Integer.parseInt(day));
			if(date.compareTo(currDate) >= 0) {
				nextRunTime = date;
				break;
			}
		}		
		
		if( nextRunTime !=null && nextRunTime.compareTo(endDate) <= 0) {
			return nextRunTime;
		} else {
			return null; 
		}
	}

	private Date getNextDailyRunTime(Date startDate, Date endDate, Date previousRunTime) throws ParseException {
		Date tempCurrDate = new Date();
		Date currDate = simpleDateFormat.parse(tempCurrDate.toString());
		
//		if(previousRunTime != null) {
			//Date nextRunTime = simpleDateFormat.parse(DateUtils.addDays(startDate.compareTo(currDate) >= 0 ? startDate : currDate, 1).toString());
			Date nextRunTime = simpleDateFormat.parse(DateUtils.addDays(startDate,startDate.compareTo(currDate) >= 0 ? 0 : 1).toString());

			if(nextRunTime.compareTo(endDate) <= 0) {
				return nextRunTime;
			} else {
				return null; 
			}
//		} else {
//			return startDate;
//		}
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
			//e.printStackTrace();
			logger.info("Nothing to schedule ....!!");
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
			  	  	// as this is a batch, careful about appInfo
					try {
						securityServiceImpl.getAppInfo();
					} catch(Exception e) {
						logger.info("AppInfo not available. Fallback on creating FrameworkThreadLocal as this is a batch");
						Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchMI.getUuid(), batchMI.getVersion(), MetaType.batch.toString());
						User user = (User) commonServiceImpl.getOneByUuidAndVersion(batch.getCreatedBy().getRef().getUuid(), batch.getCreatedBy().getRef().getVersion(), batch.getCreatedBy().getRef().getType().toString());
						frameworkThreadServiceImpl.setSession(user.getName(), batch.getAppInfo().get(0));
					}
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
		
		if(lastExecutionTime == null) {
			lastExecutionTime = currentTriggerTime; 
		}

		Criteria criteria = new Criteria();
		List<Criteria> criteriaList = new ArrayList<Criteria>();		
		criteriaList.add(where("nextRunTime").gte(lastExecutionTime).andOperator(Criteria.where("nextRunTime").lte(currentTriggerTime)));		
		
		Criteria criteria2 = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
		
		Aggregation ruleExecAggr = newAggregation(match(criteria2), group("uuid").max("version").as("version"));
		AggregationResults<Schedule> scheduleAggrResult = mongoTemplate.aggregate(ruleExecAggr, MetaType.schedule.toString().toLowerCase(), Schedule.class);
		List<Schedule> schedulerList = scheduleAggrResult.getMappedResults();		
		List<Schedule> schedules = new ArrayList<>();
		for(Schedule schedule : schedulerList) {
			Schedule schedule2 = (Schedule) commonServiceImpl.getLatestByUuid(schedule.getId(), MetaType.schedule.toString(), "N");
			schedules.add(schedule2);
		}

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

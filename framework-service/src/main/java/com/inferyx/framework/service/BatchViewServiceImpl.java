/**
 * 
 */
package com.inferyx.framework.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IBatchDao;
import com.inferyx.framework.dao.IScheduleDao;
import com.inferyx.framework.domain.Batch;
import com.inferyx.framework.domain.BatchView;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Schedule;
import com.inferyx.framework.register.GraphRegister;

/**
 * @author Ganesh
 *
 */
@Service
public class BatchViewServiceImpl {
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private SecurityServiceImpl securityServiceImpl;
	@Autowired
	private IBatchDao iBatchDao;
	@Autowired
	private IScheduleDao iScheduleDao;   
    @Autowired
    private GraphRegister<?> registerGraph;
    @Autowired
    private BatchSchedulerServiceImpl scheduleServiceImpl;
//    @Autowired
//    private BatchSchedulerServiceImpl batchSchedulerServiceImpl;
	
	static Logger logger = Logger.getLogger(BatchViewServiceImpl.class);
	
	public BatchView findOneByUuidAndVersion(String batchUuid, String batchVersion) throws JsonProcessingException {
		logger.info("Inside findOneByUuidAndVersion.");
		BatchView batchView = new BatchView();
		Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchUuid, batchVersion, MetaType.batch.toString());
		//setting batchView properties specific to baseEntity
		batchView.setId(batch.getId());
		batchView.setUuid(batch.getUuid());
		batchView.setVersion(batch.getVersion());
		batchView.setName(batch.getName());
		batchView.setActive(batch.getActive());
		batchView.setLocked(batch.getLocked());
		batchView.setAppInfo(batch.getAppInfo());
		batchView.setCreatedBy(batch.getCreatedBy());
		batchView.setCreatedOn(batch.getCreatedOn());
		batchView.setDesc(batch.getDesc());
		batchView.setPublished(batch.getPublished());
		batchView.setPublicFlag(batch.getPublicFlag());
		batchView.setTags(batch.getTags());
		
		//setting batchView properties specific to batch
		batchView.setPipelineInfo(batch.getPipelineInfo());
		batchView.setInParallel(batch.getInParallel());
		
		//setting batchView properties specific to schedule 	
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
		query.fields().include("scheduleChg");
		
		query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(batchUuid));
		
		List<Schedule> schedulesList = mongoTemplate.find(query, Schedule.class);
		
		List<Schedule> latestSchedules = new ArrayList<>();
		Set<String> uuidSet = new HashSet<>();
		for(Schedule schedule : schedulesList) {
			if(!uuidSet.contains(schedule.getUuid())) {
				Schedule latestSchedule = (Schedule) commonServiceImpl.getLatestByUuid(schedule.getUuid(), MetaType.schedule.toString(), "N");
				latestSchedules.add(latestSchedule);
				uuidSet.add(schedule.getUuid());
			}
		}
		batchView.setScheduleInfo(latestSchedules);		
		return batchView;
	}

	public Batch save(BatchView batchView) throws Exception {
		//set batch properties and save 
		Batch batch = null;
		if(batchView.getBatchChg().equalsIgnoreCase("Y") && (batchView.getUuid() == null || batchView.getUuid().isEmpty())) {
			batch = new Batch();
			//setting batch baseEntity
			batch.setName(batchView.getName());
			batch.setTags(batchView.getTags());
			batch.setPublicFlag(batchView.getPublicFlag());
			batch.setLocked(batchView.getLocked());
			batch.setBaseEntity();
			
			//setting batch specific properties
			batch.setPipelineInfo(batchView.getPipelineInfo());
			batch.setInParallel(batchView.getInParallel());	
			batch = save(batch);
		} else if(batchView.getBatchChg().equalsIgnoreCase("Y")) {
			batch = setBatchProperties(batchView, null);
			batch = save(batch);
		} else if(batchView.getUuid() != null || !batchView.getUuid().isEmpty()){
			batch = setBatchProperties(batchView, batchView.getVersion());
			batch.setId(batchView.getId());
		}	
		
		boolean setTrigger = false;
		MetaIdentifierHolder scheduleDependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.batch, batch.getUuid(), null));
		//set schedule properties and save 
		for(Schedule schedule : batchView.getScheduleInfo()) {
			if(schedule.getScheduleChg().equalsIgnoreCase("Y") && (schedule.getUuid() == null || schedule.getUuid().isEmpty())) {
				//setting schedule baseEntity
				schedule.setName(schedule.getName());
				schedule.setTags(schedule.getTags());
				schedule.setBaseEntity();
				
				//setting schedule specific properties
				schedule.setStartDate(schedule.getStartDate().toString());
				schedule.setEndDate(schedule.getEndDate().toString());
				Date nextRunTime = scheduleServiceImpl.getNextRunTimeBySchedule(schedule);
				if(nextRunTime != null) {
					schedule.setNextRunTime(nextRunTime.toString());
				}
				schedule.setFrequencyType(schedule.getFrequencyType());
				schedule.setFrequencyDetail(schedule.getFrequencyDetail());
				schedule.setDependsOn(scheduleDependsOn);
				schedule.setScheduleChg(null);
				save(schedule);
				setTrigger = true;
			} else if(schedule.getScheduleChg().equalsIgnoreCase("Y")) {
				schedule = setScheduleProperties(schedule, null);
				Date nextRunTime = scheduleServiceImpl.getNextRunTimeBySchedule(schedule);
				if(nextRunTime != null) {
					schedule.setNextRunTime(nextRunTime.toString());
				}
				schedule.setDependsOn(scheduleDependsOn);
				save(schedule);
				setTrigger = true;
			} /*else {
				schedule = setScheduleProperties(schedule, schedule.getVersion());
			}*/
		}		
		if(setTrigger) {
			scheduleServiceImpl.setSchedulingTrigger();
		}
		return batch;
	}
	
	private Batch setBatchProperties(BatchView batchView, String version) {
		Batch batch = new Batch();
		//setting batch baseEntitiy
		batch.setActive(batchView.getActive());
		batch.setLocked(batchView.getLocked());
		batch.setAppInfo(batchView.getAppInfo());
		batch.setCreatedBy(batchView.getCreatedBy());
//		batch.setCreatedOn(batchView.getCreatedOn());
		batch.setDesc(batchView.getDesc());
		batch.setName(batchView.getName());
		batch.setPublished(batchView.getPublished());
		batch.setPublicFlag(batchView.getPublicFlag());
		batch.setTags(batchView.getTags());
		batch.setUuid(batchView.getUuid());
		if(version != null) {
			batch.setVersion(version);
		} else {
			batch.setVersion(Helper.getVersion());
		}
		
		//setting batch specific properties
		batch.setPipelineInfo(batchView.getPipelineInfo());
		batch.setInParallel(batchView.getInParallel());	
		return batch;
	}
	
	private Schedule setScheduleProperties(Schedule schedule, String version) {
		Schedule schedule2 = new Schedule();
		//setting schedule baseEntitiy
		schedule2.setActive(schedule.getActive());
		schedule2.setLocked(schedule.getLocked());
		schedule2.setAppInfo(schedule.getAppInfo());
		schedule2.setCreatedBy(schedule.getCreatedBy());
//		schedule2.setCreatedOn(schedule.getCreatedOn());
		schedule2.setDesc(schedule.getDesc());
		schedule2.setName(schedule.getName());
		schedule2.setPublished(schedule.getPublished());
		schedule2.setTags(schedule.getTags());
		schedule2.setUuid(schedule.getUuid());
		if(version != null) {
			schedule2.setVersion(version);
		} else {
			schedule2.setVersion(Helper.getVersion());
		}
		
		//setting schedule specific properties
		schedule2.setStartDate(schedule.getStartDate().toString());
		schedule2.setEndDate(schedule.getEndDate().toString());
//		schedule2.setNextRunTime(schedule.getNextRunTime().toString());
		schedule2.setFrequencyType(schedule.getFrequencyType());
		schedule2.setFrequencyDetail(schedule.getFrequencyDetail());
		schedule2.setScheduleChg(null);
		return schedule2;
	}

	public Batch save(Batch batch) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
		List<MetaIdentifierHolder> appInfos = new ArrayList<>();
		appInfos.add(securityServiceImpl.getAppInfo());
		batch.setAppInfo(appInfos);
//		batch.setCreatedOn("");
		batch.setCreatedBy(null);
		batch.setBaseEntity();
		Batch savedBatch = iBatchDao.save(batch);
		registerGraph.updateGraph((Object) savedBatch, MetaType.batch);
		return savedBatch;
	}
	
	public Schedule save(Schedule schedule) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
		List<MetaIdentifierHolder> appInfos = new ArrayList<>();
		appInfos.add(securityServiceImpl.getAppInfo());
		schedule.setAppInfo(appInfos);
//		schedule.setCreatedOn("");
		schedule.setCreatedBy(null);
		schedule.setBaseEntity();
		Schedule savedSchedule = iScheduleDao.save(schedule);
		registerGraph.updateGraph((Object) savedSchedule, MetaType.schedule);
		return savedSchedule;
	}
}

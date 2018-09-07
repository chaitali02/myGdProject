/**
 * 
 */
package com.inferyx.framework.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IBatchDao;
import com.inferyx.framework.dao.IReconDao;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Batch;
import com.inferyx.framework.domain.BatchView;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Recon;
import com.inferyx.framework.domain.Schedule;
import com.inferyx.framework.register.GraphRegister;

/**
 * @author Ganesh
 *
 */
public class BatchViewServiceImpl {
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	IBatchDao iBatchDao;
//	@Autowired
//	private IScheduleDao iScheduleDao;   
    @Autowired
	GraphRegister<?> registerGraph;
	
	static Logger logger = Logger.getLogger(BatchViewServiceImpl.class);
	
	public BatchView findOneByUuidAndVersion(String batchUuid, String batchVersion) throws JsonProcessingException {
		logger.info("Inside findOneByUuidAndVersion.");
		BatchView batchView = new BatchView();
		Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchUuid, batchVersion, MetaType.batch.toString());
		//setting batchView properties specific to baseEntity
		batchView.setUuid(batch.getUuid());
		batchView.setVersion(batch.getVersion());
		batchView.setName(batch.getName());
		batchView.setActive(batch.getActive());
		batchView.setAppInfo(batch.getAppInfo());
		batchView.setCreatedBy(batch.getCreatedBy());
		batchView.setCreatedOn(batch.getCreatedOn());
		batchView.setDesc(batch.getDesc());
		batchView.setPublished(batch.getPublished());
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

	public Batch save(BatchView batchView) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
		//set properties and save batch
		Batch batch = new Batch();
		if(batchView.getBatchChg().equalsIgnoreCase("Y") && (batchView.getUuid() == null || batchView.getUuid().isEmpty())) {
			batch.setName(batchView.getName());
			batch.setBaseEntity();
		} else if(batchView.getBatchChg().equalsIgnoreCase("Y")) {
			batch = setBatchBaseEntity(batchView, null);
		} else {
			batch = setBatchBaseEntity(batchView, batchView.getVersion());
		}
		
		batch.setPipelineInfo(batchView.getPipelineInfo());
		batch.setInParallel(batchView.getInParallel());		
		batch = save(batch);
		
		//set properties and save schedule
		for(Schedule schedule : batchView.getScheduleInfo()) {
			if(schedule.getScheduleChg().equalsIgnoreCase("Y") && (schedule.getUuid() == null || schedule.getUuid().isEmpty())) {
				schedule.setBaseEntity();
			} else if(schedule.getScheduleChg().equalsIgnoreCase("Y")) {
				schedule = setScheduleBaseEntity(schedule, null);
			} else {
				schedule = setScheduleBaseEntity(schedule, schedule.getVersion());
			}
			
			schedule.setStartDate(schedule.getStartDate().toString());
			schedule.setEndDate(schedule.getEndDate().toString());
			schedule.setNextRunTime(schedule.getNextRunTime().toString());
			schedule.setFrequencyType(schedule.getFrequencyType());
			schedule.setFrequencyDetail(schedule.getFrequencyDetail());
			save(schedule);
		}
		
		return batch;
	}
	
	private Batch setBatchBaseEntity(BatchView batchView, String version) {
		Batch batch = new Batch();
		batch.setActive(batchView.getActive());
		batch.setAppInfo(batchView.getAppInfo());
		batch.setCreatedBy(batchView.getCreatedBy());
		batch.setCreatedOn(batchView.getCreatedOn());
		batch.setDesc(batchView.getDesc());
		batch.setName(batchView.getName());
		batch.setPublished(batchView.getPublished());
		batch.setTags(batchView.getTags());
		batch.setUuid(batchView.getUuid());
		if(version != null) {
			batch.setVersion(version);
		} else {
			batch.setVersion(Helper.getVersion());
		}
		return batch;
	}
	
	private Schedule setScheduleBaseEntity(Schedule schedule, String version) {
		
		
		return null;
	}

	public Batch save(Batch batch) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		batch.setAppInfo(metaIdentifierHolderList);
		Batch savedBatch = iBatchDao.save(batch);
		registerGraph.updateGraph((Object) savedBatch, MetaType.batch);
		return savedBatch;
	}
	
	public Schedule save(Schedule schedule) {
//		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
//		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
//		metaIdentifierHolderList.add(meta);
//		schedule.setAppInfo(metaIdentifierHolderList);
//		Schedule savedSchedule = iBatchDao.save(schedule);
//		registerGraph.updateGraph((Object) savedSchedule, MetaType.schedule);
//		return savedSchedule;
		return null;
	}
}

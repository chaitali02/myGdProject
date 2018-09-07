/**
 * 
 */
package com.inferyx.framework.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Batch;
import com.inferyx.framework.domain.BatchView;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Schedule;

/**
 * @author Ganesh
 *
 */
public class BatchViewServiceImpl {
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private MongoTemplate mongoTemplate;
	
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
}

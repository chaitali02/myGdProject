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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaSparkContext;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IMeasureDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.Measure;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class MeasureServiceImpl {

	static final Logger logger = Logger.getLogger(MeasureServiceImpl.class);

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	FormulaServiceImpl formulaServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	IMeasureDao iMeasureDao;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired 
	RegisterService registerService;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;

	/********************** UNUSED **********************/
	/*public Measure findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iMeasureDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iMeasureDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public Measure save(Measure measure) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		measure.setAppInfo(metaIdentifierHolderList);
		measure.setBaseEntity();
		Measure measureDet=iMeasureDao.save(measure);
		registerGraph.updateGraph((Object) measureDet, MetaType.measure);
		return measureDet;
	}*/

	/********************** UNUSED **********************/
	/*public Measure findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		;
		return iMeasureDao.findOneByUuidAndVersion(appUuid, uuid, version);
	}*/

	/*public Measure update(Measure measure) throws IOException {
		measure.exportBaseProperty();
		Measure measureDet=iMeasureDao.save(measure);
		registerService.createGraph();
		return measureDet;
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id) {
		return iMeasureDao.exists(id);
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		;
		Measure measure = iMeasureDao.findOneById(appUuid, id);
		measure.setActive("N");
		iMeasureDao.save(measure);
//		String ID = measure.getId();
//		iMeasureDao.delete(ID);
	}*/

	/********************** UNUSED **********************/
	/*public Measure findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iMeasureDao.findOneById(appUuid, id);
		}
		return iMeasureDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public List<Measure> findAllLatestActive() {
		Aggregation measureAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Measure> measureResults = mongoTemplate.aggregate(measureAggr, "measure", Measure.class);
		List<Measure> measureList = measureResults.getMappedResults();

		// Fetch the load details for each id
		List<Measure> result = new ArrayList<Measure>();
		for (Measure m : measureList) {
			Measure measureLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				measureLatest = iMeasureDao.findOneByUuidAndVersion(appUuid, m.getId(), m.getVersion());
			} else {
				measureLatest = iMeasureDao.findOneByUuidAndVersion(m.getId(), m.getVersion());
			}
			if(measureLatest != null)
			{
			result.add(measureLatest);
			}
		}
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public Measure getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iMeasureDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iMeasureDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Measure measure) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Measure measureNew = new Measure();
		measureNew.setName(measure.getName()+"_copy");
		measureNew.setActive(measure.getActive());		
		measureNew.setDesc(measure.getDesc());		
		measureNew.setTags(measure.getTags());	
		measureNew.setMeasureInfo(measure.getMeasureInfo());
		save(measureNew);
		ref.setType(MetaType.measure);
		ref.setUuid(measureNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> measureList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity measure : measureList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = measure.getId();
			String uuid = measure.getUuid();
			String version = measure.getVersion();
			String name = measure.getName();
			String desc = measure.getDesc();
			String published=measure.getPublished();
			MetaIdentifierHolder createdBy = measure.getCreatedBy();
			String createdOn = measure.getCreatedOn();
			String[] tags = measure.getTags();
			String active = measure.getActive();
			List<MetaIdentifierHolder> appInfo = measure.getAppInfo();
			baseEntity.setId(id);
			baseEntity.setUuid(uuid);
			baseEntity.setVersion(version);
			baseEntity.setName(name);
			baseEntity.setDesc(desc);
			baseEntity.setCreatedBy(createdBy);
			baseEntity.setCreatedOn(createdOn);
			baseEntity.setPublished(published);
			baseEntity.setTags(tags);
			baseEntity.setActive(active);
			baseEntity.setAppInfo(appInfo);
			baseEntityList.add(baseEntity);
		}
		return baseEntityList;
	}*/
}
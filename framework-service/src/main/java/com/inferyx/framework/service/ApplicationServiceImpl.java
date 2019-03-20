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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.inferyx.framework.dao.IApplicationDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ApplicationServiceImpl {
	
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IApplicationDao iApplicationDao;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	
	static final Logger logger = Logger.getLogger(ApplicationServiceImpl.class);

	/********************** UNUSED **********************/
	/*public Application findLatest() {
		return resolveName(iApplicationDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public Application findOneByUuidAndVersion(String uuid,String version){
		return iApplicationDao.findOneByUuidAndVersion(uuid,version);
	}*/

	/********************** UNUSED **********************/
	/*public Application findOneById(String id){
		return iApplicationDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String Id){
		Application application = iApplicationDao.findOne(Id);
		application.setActive("N");
		iApplicationDao.save(application);
		//String ID=application.getId();
		//iApplicationDao.delete(ID);
		//application.exportBaseProperty();
	}*/

	/********************** UNUSED **********************/
	/*public Application save(Application application) throws Exception{
		application.setBaseEntity();
		Application app=iApplicationDao.save(application);
		registerGraph.updateGraph((Object) app, MetaType.application);
		return app;
	}*/

	/********************** UNUSED **********************/
	/*public List<Application> findAllLatestActive() 	
	{ 
		Aggregation appAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<Application> appResults = mongoTemplate.aggregate(appAggr,"application", Application.class);	   
	   List<Application> appList = appResults.getMappedResults();

	   // Fetch the application details for each id
	   List<Application> result=new  ArrayList<Application>();
	   for(Application a : appList)
	   {   		     
		   Application appLatest = iApplicationDao.findOneByUuidAndVersion(a.getId(), a.getVersion());  		   
		   result.add(appLatest);
	   }
	   return result;
	}*/


	/********************** UNUSED **********************/
	/*public Application getAsOf(String uuid, String asOf) {
		return iApplicationDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Application application) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Application appNew = new Application();
		appNew.setName(application.getName()+"_copy");
		appNew.setActive(application.getActive());		
		appNew.setDesc(application.getDesc());		
		appNew.setTags(application.getTags());	
		save(appNew);
		ref.setType(MetaType.application);
		ref.setUuid(appNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> applicationList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity application : applicationList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = application.getId();
			String uuid = application.getUuid();
			String version = application.getVersion();
			String name = application.getName();
			String desc = application.getDesc();
			String published=application.getPublished();
			MetaIdentifierHolder createdBy = application.getCreatedBy();
			String createdOn = application.getCreatedOn();
			String[] tags = application.getTags();
			String active = application.getActive();
			List<MetaIdentifierHolder> appInfo = application.getAppInfo();
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

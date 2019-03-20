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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IConditionDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Condition;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ConditionServiceImpl {
	
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IConditionDao iConDao;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	RelationServiceImpl conditionServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired
	UserServiceImpl userServiceImpl;	
	@Autowired
	SecurityServiceImpl securityServiceImpl;	
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	DatasetServiceImpl datasetServiceImpl;
	@Autowired 
	RegisterService registerService;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(ConditionServiceImpl.class);

	/********************** UNUSED **********************/
	/*public Condition findLatestByUuid(String uuid){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iConDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));	
		}
		return iConDao.findLatestByUuid(appUuid,uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/

	/********************** UNUSED **********************/
	/*public List<Condition> test(String param1) {	
		return iConDao.test(param1);
	}*/

	/********************** UNUSED **********************/
	/*public Condition findOneByUuidAndVersion(String uuid,String version){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
		return iConDao.findOneByUuidAndVersion(appUuid,uuid,version);
		}
		else
			return iConDao.findOneByUuidAndVersion(uuid,version);
	}*/

	/********************** UNUSED **********************/
	/*public Condition getOneByUuidAndVersion(String uuid,String version){
		
		return iConDao.findOneByUuidAndVersion(uuid,version);
	}*/

	/********************** UNUSED **********************/
	/*public Condition findOneById(String id){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iConDao.findOneById(appUuid,id);
		}
		else
			return iConDao.findOne(id);
			
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String Id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Condition condition = iConDao.findOneById(appUuid,Id);		
		condition.setActive("N");
		iConDao.save(condition);
		String ID=condition.getId();
		iConDao.delete(appUuid,ID);
		condition.exportBaseProperty();
	}*/
	
	/********************** UNUSED **********************/
	/*public Condition save(Condition condition) throws Exception{
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		condition.setAppInfo(metaIdentifierHolderList);
		condition.setBaseEntity();
	    Condition condition1=iConDao.save(condition);
	   // registerService.createGraph();
		registerGraph.updateGraph((Object) condition1, MetaType.condition);
		return condition1;
	}*/
	/********************** UNUSED **********************/
	/*public List<Condition> findAllLatestActive() 	
	{	  
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;		
	   Aggregation conditionAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<Condition> conditionResults = mongoTemplate.aggregate(conditionAggr, "condition", Condition.class);	   
	   List<Condition> conditionList = conditionResults.getMappedResults();

	   // Fetch the Condition details for each id
	   List<Condition> result=new  ArrayList<Condition>();
	   for(Condition s : conditionList)
	   {   
		   Condition conditionLatest;
		   if(appUuid != null)
		   {
			   conditionLatest = iConDao.findOneByUuidAndVersion(appUuid,s.getId(),s.getVersion());
		   }
		   else
		   {
			   conditionLatest = iConDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
		   }	
		   if(conditionLatest != null)
		   {
		   result.add(conditionLatest);
		   }
	   }
	   return result;
	}
	*/

	/********************** UNUSED **********************/
	/*public List<Condition> findConditionByRelation(String relationUUID)
	{
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();;
	     		Aggregation conditionAggr = newAggregation(match(Criteria.where("dependsOn.ref.uuid").is(relationUUID)),
				group("uuid").max("version").as("version"));

		// Convert the aggregation result into a List
		AggregationResults<Condition> groupResults = mongoTemplate.aggregate(conditionAggr, "condition", Condition.class);
		List<Condition> conditionList = groupResults.getMappedResults();

		// Fetch the datapod details for each id
		List<Condition> result = new ArrayList<Condition>();
		for (Condition s : conditionList) {
			Condition conditionLatest = iConDao.findOneByUuidAndVersion(appUuid,s.getId(), s.getVersion());
			result.add(conditionLatest);
		}
		return result;
	}*/

	/*public Condition getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iConDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iConDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Condition condition) throws Exception{
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Condition conNew = new Condition();
		conNew.setName(condition.getName()+"_copy");
		conNew.setActive(condition.getActive());		
		conNew.setDesc(condition.getDesc());		
		conNew.setTags(condition.getTags());	
		conNew.setDependsOn(condition.getDependsOn());
		conNew.setConditionInfo(conNew.getConditionInfo());
		save(conNew);
		ref.setType(MetaType.condition);
		ref.setUuid(conNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> conditionList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity condition : conditionList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = condition.getId();
			String uuid = condition.getUuid();
			String version = condition.getVersion();
			String name = condition.getName();
			String desc = condition.getDesc();
			String published=condition.getPublished();
			MetaIdentifierHolder createdBy = condition.getCreatedBy();
			String createdOn = condition.getCreatedOn();
			String[] tags = condition.getTags();
			String active = condition.getActive();
			List<MetaIdentifierHolder> appInfo = condition.getAppInfo();
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
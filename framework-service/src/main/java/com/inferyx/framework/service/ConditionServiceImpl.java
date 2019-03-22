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

	
}
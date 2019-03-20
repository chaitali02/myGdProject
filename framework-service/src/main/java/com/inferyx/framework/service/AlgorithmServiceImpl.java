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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.inferyx.framework.dao.IAlgorithmDao;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class AlgorithmServiceImpl {
	
	@Autowired
	GraphRegister<?> registerGraph;/*
	@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IAlgorithmDao iAlgorithmDao;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	
	static final Logger logger = Logger.getLogger(AlgorithmServiceImpl.class);

	/********************** UNUSED **********************/
	/*public Algorithm findLatest() {
		return resolveName(iAlgorithmDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public Algorithm findLatestByUuid(String uuid){
		return iAlgorithmDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/

	/********************** UNUSED **********************/
	/*public Algorithm findOneById(String id){
		return iAlgorithmDao.findOne(id);
	}*/

	

	/********************** UNUSED **********************/
	/*public List<Algorithm> findAllLatestActive() 	
	{ 
		Aggregation appAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<Algorithm> appResults = mongoTemplate.aggregate(appAggr, "algorithm", Algorithm.class);	   
	   List<Algorithm> appList = appResults.getMappedResults();

	   // Fetch the application details for each id
	   List<Algorithm> result=new  ArrayList<Algorithm>();
	   for(Algorithm a : appList)
	   {   		     
		   Algorithm appLatest = iAlgorithmDao.findOneByUuidAndVersion(a.getId(), a.getVersion());  		   
		   result.add(appLatest);
	   }
	   return result;
	}*/

	
}

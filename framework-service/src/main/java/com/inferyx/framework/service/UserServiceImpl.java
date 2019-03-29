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
import com.inferyx.framework.dao.ISessionDao;
import com.inferyx.framework.dao.IUserDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Group;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Role;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class UserServiceImpl {
	
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IUserDao iUserDao;
	@Autowired
	ISessionDao iSessionDao;
	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired
	DagExecServiceImpl dagExecImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	SessionServiceImpl sessionServiceImpl;
	@Autowired
	RoleServiceImpl roleServiceImpl;
	@Autowired
	ActivityServiceImpl activityServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	GroupServiceImpl groupServiceImpl;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(UserServiceImpl.class);

	
	public User findUserByName(String userName)  {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		User user = null;
//		if(appUuid != null)
//			user=iUserDao.findUserByName(appUuid,userName);
//		else
			user=iUserDao.findUserByName(userName);
	if(user==null)
		return null;
	else 
		return user;
	}
	
	public User findLatestByUsername(String userName) 
	{
		// String appUuid = (securityServiceImpl.getAppInfo() != null &&
		// securityServiceImpl.getAppInfo().getRef() != null
		// )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		// if(appUuid != null)
		// {
		// return iUserDao.findLatestByUsername(appUuid, userName,new
		// Sort(Sort.Direction.DESC, "version"));
		// }
		return iUserDao.findLatestByUsername(userName,new Sort(Sort.Direction.DESC, "version"));
	}
	
	
	public List<Application> findAppByUser(String userName) throws JsonProcessingException {
		List<Application> appList = new ArrayList<Application>();			
			User userDO = findLatestByUsername(userName);
			List<MetaIdentifierHolder> metaList = userDO.getAppInfo();
			for(MetaIdentifierHolder appInfo : metaList) {
				String appUuid = appInfo.getRef().getUuid();
				Application appDO = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				appList.add(appDO);
			}
		return appList;
	}

	
}
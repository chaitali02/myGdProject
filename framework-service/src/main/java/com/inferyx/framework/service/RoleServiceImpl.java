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

import com.inferyx.framework.dao.IRoleDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Privilege;
import com.inferyx.framework.domain.Role;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class RoleServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IRoleDao iRoleDao;
	@Autowired
	MongoTemplate mongoTemplate;
	/*@Autowired
	private UserServiceImpl userServiceImpl;*/
	/*@Autowired
	private PrivilegeServiceImpl privilegeServiceImpl;*/
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	RegisterService registerService;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(RoleServiceImpl.class);

	/********************** UNUSED **********************/
	/*public Role findLatest() {
		return resolveName(iRoleDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/
	
	/********************** UNUSED **********************/
	/*public Role findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iRoleDao.findOneById(appUuid, id);
		}
		return iRoleDao.findOne(id);
	}*/

	
	/********************** UNUSED **********************/
	/*public List<Role> findAllVersion(String roleName) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iRoleDao.findAllVersion(appUuid, roleName);
		}
		return iRoleDao.findAllVersion(roleName);
	}*/

	/********************** UNUSED **********************/
	/*public List<Role> findAllLatestActive() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Aggregation roleAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Role> roleResults = mongoTemplate.aggregate(roleAggr, "role", Role.class);
		List<Role> roleList = roleResults.getMappedResults();

		// Fetch the role details for each id
		List<Role> result = new ArrayList<Role>();
		for (Role r : roleList) {
			Role roleLatest = null;
			if (appUuid != null) {
				roleLatest = iRoleDao.findOneByUuidAndVersion(appUuid, r.getId(), r.getVersion());
			} else {
				roleLatest = iRoleDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if (roleLatest != null) {
				result.add(roleLatest);
			}
		}
		return result;
	}*/
	
	/********************** UNUSED **********************/
	/*public Role getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iRoleDao.findAsOf(appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
		}
		return iRoleDao.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}*/

		
}
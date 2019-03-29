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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.inferyx.framework.dao.IVizpodExecDao;
import com.inferyx.framework.register.GraphRegister;

@Service
public class VizExecServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private IVizpodExecDao iVizpodExec;	
	@Autowired
	private UserServiceImpl userServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	VizpodServiceImpl vizpodServiceImpl;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	
	static final Logger logger = Logger.getLogger(VizExecServiceImpl.class);	


	
}
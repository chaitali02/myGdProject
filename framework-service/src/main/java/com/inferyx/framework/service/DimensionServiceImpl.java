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

import com.inferyx.framework.dao.IDimensionDao;
import com.inferyx.framework.register.GraphRegister;

@Service
public class DimensionServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IDimensionDao  iDimensionDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	FormulaServiceImpl formulaServiceImpl;
	@Autowired
	ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	ConditionServiceImpl conditionServiceImpl;
	/*@Autowired
	GroupServiceImpl groupServiceImpl;*/
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(DimensionServiceImpl.class);

	
	
}
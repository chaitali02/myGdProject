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
import com.inferyx.framework.dao.IExpressionDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ExpressionServiceImpl {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IExpressionDao iExpressionDao;
	@Autowired
	RelationServiceImpl relationServiceImpl;
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
	FormulaServiceImpl formulaServiceImpl;
	@Autowired 
	RegisterService registerService;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(ExpressionServiceImpl.class);

	public List<Expression> findExpressionByRelation(String relationUUID) throws JsonProcessingException{
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		Aggregation expressionAggr = newAggregation(match(Criteria.where("dependsOn.ref.uuid").is(relationUUID)),
				group("uuid").max("version").as("version"));
		AggregationResults<Expression> expressionResults = mongoTemplate.aggregate(expressionAggr, "expression",
				Expression.class);
		List<Expression> expressionList = expressionResults.getMappedResults();

		// Fetch the expression details for each id
		List<Expression> result = new ArrayList<Expression>();
		for (Expression s : expressionList) {
			//Expression expressionLatest = iExpressionDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			Expression expressionLatest = (Expression) commonServiceImpl.getOneByUuidAndVersion(s.getId(), s.getVersion(), MetaType.expression.toString());
			result.add(expressionLatest);
		}
		return result;
	}


	public List<Expression> findExpressionByType(String uuid) throws JsonProcessingException {
		/*String appUuid = (securityServiceImpl.getAppInfo() != null
				&& securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
		Aggregation expressionAggr = newAggregation(match(Criteria.where("dependsOn.ref.uuid").is(uuid)),
				group("uuid").max("version").as("version"));
		AggregationResults<Expression> expressionResults = mongoTemplate.aggregate(expressionAggr, "expression", Expression.class);
		List<Expression> expressionList = expressionResults.getMappedResults();

		// Fetch Expression details for each id
		List<Expression> result = new ArrayList<Expression>();
		for (Expression s : expressionList) {
			//Expression expressionLatest = iExpressionDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			Expression expressionLatest = (Expression) commonServiceImpl.getOneByUuidAndVersion(s.getId(), s.getVersion(), MetaType.expression.toString());
			result.add(expressionLatest);
		}
		return result;
	}


	public List<MetaIdentifierHolder> findExpressionByType2(String uuid) throws JsonProcessingException {
		/*String appUuid = (securityServiceImpl.getAppInfo() != null
				&& securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
		Aggregation expressionAggr = newAggregation(match(Criteria.where("dependsOn.ref.uuid").is(uuid)),
				group("uuid").max("version").as("version"));
		AggregationResults<Expression> expressionResults = mongoTemplate.aggregate(expressionAggr, "expression", Expression.class);
		List<Expression> expressionList = expressionResults.getMappedResults();

		// Fetch Expression details for each id
		List<MetaIdentifierHolder> result = new ArrayList<MetaIdentifierHolder>();
		for (Expression s : expressionList) {
			//Expression expressionLatest = iExpressionDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			Expression expressionLatest = (Expression) commonServiceImpl.getOneByUuidAndVersion(s.getId(), s.getVersion(), MetaType.expression.toString());
			MetaIdentifierHolder expressionMeta = new MetaIdentifierHolder();
			MetaIdentifier expressionInfo = new MetaIdentifier(MetaType.expression,expressionLatest.getUuid(),expressionLatest.getVersion(),expressionLatest.getName());
			expressionMeta.setRef(expressionInfo);
			result.add(expressionMeta);
		}
		return result;
	}
}

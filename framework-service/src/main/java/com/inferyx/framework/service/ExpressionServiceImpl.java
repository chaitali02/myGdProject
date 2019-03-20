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

	/********************** UNUSED **********************/
	/*public Expression findLatest() {
		return resolveName(iExpressionDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public Expression findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iExpressionDao.findOneById(appUuid, id);
		} else
			return iExpressionDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public Expression findOneByUuid(String uid) {
		return iExpressionDao.findOne(uid);
	}*/

	/********************** UNUSED **********************/
	/*public Expression save(Expression expression) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		expression.setAppInfo(metaIdentifierHolderList);
		expression.setBaseEntity();
		Expression expressionSave=iExpressionDao.save(expression);
		registerGraph.updateGraph((Object) expressionSave, MetaType.expression);
		return expressionSave;
	}*/



	/*public Expression update(Expression expression) throws IOException {
		expression.exportBaseProperty();
		Expression expressionDet=iExpressionDao.save(expression);
		registerService.createGraph();
		return expressionDet;
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id) {
		return iExpressionDao.exists(id);
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Expression expression = iExpressionDao.findOneById(appUuid, Id);
		expression.setActive("N");
		iExpressionDao.save(expression);
//		String ID = expression.getId();
//		iExpressionDao.delete(ID);
//		expression.exportBaseProperty();
	}*/

	/********************** UNUSED **********************/
	/*public List<Expression> test(String param1) {
		return iExpressionDao.test(param1);
	}*/

	/********************** UNUSED **********************/
	/*public Expression findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iExpressionDao.findOneByUuidAndVersion(appUuid, uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Expression getOneByUuidAndVersion(String uuid, String version) {
		return iExpressionDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Expression findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iExpressionDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iExpressionDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/
	
	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public List<Expression> findAllLatestActive() {
		Aggregation expressionAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Expression> expressionResults = mongoTemplate.aggregate(expressionAggr, "expression",
				Expression.class);
		List<Expression> expressionList = expressionResults.getMappedResults();

		// Fetch the expression details for each id
		List<Expression> result = new ArrayList<Expression>();
		for (Expression e : expressionList) {
			Expression expressionLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				expressionLatest = iExpressionDao.findOneByUuidAndVersion(appUuid, e.getId(), e.getVersion());
			} else {
				expressionLatest = iExpressionDao.findOneByUuidAndVersion(e.getId(), e.getVersion());
			}
			if(expressionLatest != null)
			{
			result.add(expressionLatest);
			}
		}
		return result;
	}*/

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

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public Expression getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iExpressionDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iExpressionDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

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

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Expression expression) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Expression expNew = new Expression();
		expNew.setName(expression.getName()+"_copy");
		expNew.setActive(expression.getActive());		
		expNew.setDesc(expression.getDesc());		
		expNew.setTags(expression.getTags());	
		expNew.setDependsOn(expression.getDependsOn());		
		expNew.setCondition(expression.getCondition());
		expNew.setPublished(expression.getPublished());
		expNew.setNoMatch(expression.getNoMatch());
		expNew.setMatch(expression.getMatch());
		expNew.setExpressionInfo(expression.getExpressionInfo());
		save(expNew);
		ref.setType(MetaType.expression);
		ref.setUuid(expNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> expressionList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity expression : expressionList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = expression.getId();
			String uuid = expression.getUuid();
			String version = expression.getVersion();
			String name = expression.getName();
			String desc = expression.getDesc();
			String published=expression.getPublished();
			MetaIdentifierHolder createdBy = expression.getCreatedBy();
			String createdOn = expression.getCreatedOn();
			String[] tags = expression.getTags();
			String active = expression.getActive();
			List<MetaIdentifierHolder> appInfo = expression.getAppInfo();
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

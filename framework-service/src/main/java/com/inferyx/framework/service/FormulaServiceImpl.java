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
import com.inferyx.framework.dao.IFormulaDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.FormulaType;
import com.inferyx.framework.domain.FormulaTypeHolder;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class FormulaServiceImpl {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IFormulaDao iFormulaDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;	
	@Autowired
	DatasetServiceImpl datasetServiceImpl;
	@Autowired
	ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(FormulaServiceImpl.class);
	
	/********************** UNUSED **********************/
	/*public Formula findLatest() {
		return resolveName(iFormulaDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public Formula findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iFormulaDao.findOneById(appUuid, id);
		} else
			return iFormulaDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id) {
		return iFormulaDao.exists(id);
	}*/


	/********************** UNUSED **********************/
	/*public Formula getOneByUuidAndVersion(String uuid, String version) {

		return iFormulaDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Formula findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iFormulaDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iFormulaDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public List<Formula> findAllLatestActive() {
		Aggregation formulaAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Formula> formulaResults = mongoTemplate.aggregate(formulaAggr, "formula", Formula.class);
		List<Formula> formulaList = formulaResults.getMappedResults();

		// Fetch the formula details for each id
		List<Formula> result = new ArrayList<Formula>();
		for (Formula f : formulaList) {
			Formula formulaLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				formulaLatest = iFormulaDao.findOneByUuidAndVersion(appUuid, f.getId(), f.getVersion());
			} else {
				formulaLatest = iFormulaDao.findOneByUuidAndVersion(f.getId(), f.getVersion());
			}
			if(formulaLatest != null)
			{
			result.add(formulaLatest);
			}
		}
		return result;
	}*/


    public List<Formula> findFormulaByApp() throws JsonProcessingException{
    	
    	List<Formula> result = new ArrayList<Formula>();
    	String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			Application application= (Application) commonServiceImpl.getOneByUuidAndVersion(appUuid,null,MetaType.application.toString());
			if(application !=null) {
				String uuid=application.getParamList().getRef().getUuid();
				result=findFormulaByType(uuid);
			}
			
		}
    	return result;
    }
    
	public List<Formula> findFormulaByType(String uuid) throws JsonProcessingException {
		/*
		 * String appUuid = (securityServiceImpl.getAppInfo() != null &&
		 * securityServiceImpl.getAppInfo().getRef() != null) ?
		 * securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		 */
		Aggregation formulaAggr = newAggregation(match(Criteria.where("dependsOn.ref.uuid").is(uuid)),
				group("uuid").max("version").as("version"));
		AggregationResults<Formula> formulaResults = mongoTemplate.aggregate(formulaAggr, MetaType.formula.toString().toLowerCase(), Formula.class);
		List<Formula> formulaList = formulaResults.getMappedResults();

		// Fetch formula details for each id
		List<Formula> result = new ArrayList<Formula>();
		for (Formula s : formulaList) {
			// Formula formulaLatest = iFormulaDao.findOneByUuidAndVersion(appUuid,
			// s.getId(), s.getVersion());
			Formula formulaLatest = (Formula) commonServiceImpl.getOneByUuidAndVersion(s.getId(), s.getVersion(),
					MetaType.formula.toString());
			result.add(formulaLatest);
		}
		return result;
	}


	public List<FormulaTypeHolder> findFormulaByType2(String uuid, String[] formulaType) throws JsonProcessingException {
		return findFormulaByType2(uuid, formulaType, "Y");
	}
	
	public List<FormulaTypeHolder> findFormulaByType2(String uuid, String[] formulaType, String resolveFlag) throws JsonProcessingException {
		/*String appUuid = (securityServiceImpl.getAppInfo() != null
				&& securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
		List<Formula> formulaList = new ArrayList<>();
		
		List<Criteria> criteriaList = new ArrayList<>();
		
		
		try {
			if(uuid != null)
				criteriaList.add(Criteria.where("dependsOn.ref.uuid").is(uuid));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		if (formulaType.length == 0) {
			Criteria criteria = new Criteria(); 
			Criteria criteria2 = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
			Aggregation formulaAggr = newAggregation(match(criteria2),
					group("uuid").max("version").as("version"));
			AggregationResults<Formula> formulaResults = mongoTemplate.aggregate(formulaAggr, MetaType.formula.toString().toLowerCase(), Formula.class);
			formulaList.addAll(formulaResults.getMappedResults());
		} else {
			for (String type : formulaType) {	
				Criteria criteria = new Criteria(); 
				criteriaList.add(Criteria.where("formulaType").is(type));	
				Criteria criteria2 = criteria.orOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
				Aggregation formulatype = newAggregation(
						match(criteria2),
						group("uuid").max("version").as("version"));
				AggregationResults<Formula> formulaResults = mongoTemplate.aggregate(formulatype, MetaType.formula.toString().toLowerCase(),
						Formula.class);
				formulaList.addAll(formulaResults.getMappedResults());
			}
		}

		// Fetch formula details for each id
		List<FormulaTypeHolder> result = new ArrayList<FormulaTypeHolder>();
		for (Formula s : formulaList) {
			//Formula formulaLatest = iFormulaDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			Formula formulaLatest = (Formula) commonServiceImpl.getOneByUuidAndVersion(s.getId(), s.getVersion(), MetaType.formula.toString(), resolveFlag);
			FormulaTypeHolder formulaTypeHolder = new FormulaTypeHolder();
			MetaIdentifier formulaInfo = new MetaIdentifier(MetaType.formula,formulaLatest.getUuid(),formulaLatest.getVersion(),formulaLatest.getName());
			formulaTypeHolder.setFormulaType(formulaLatest.getFormulaType());			
			formulaTypeHolder.setRef(formulaInfo);
			result.add(formulaTypeHolder);
		}
		return result;
	}

}
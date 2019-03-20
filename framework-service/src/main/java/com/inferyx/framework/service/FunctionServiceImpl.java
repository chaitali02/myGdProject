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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.inferyx.framework.dao.IFunctionDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class FunctionServiceImpl {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IFunctionDao iFunctionDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;	
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	DatasetServiceImpl datasetServiceImpl;
	@Autowired
	ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
static final Logger logger = Logger.getLogger(FunctionServiceImpl.class);

/********************** UNUSED **********************/
	/*public Function findLatest() {
			return resolveName(iFunctionDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

/********************** UNUSED **********************/
	/*public Function findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iFunctionDao.findOneById(appUuid, id);
		} else
			return iFunctionDao.findOne(id);
	}
*/

/********************** UNUSED **********************/
	/*public Function save(Function function) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		function.setAppInfo(metaIdentifierHolderList);
		function.setBaseEntity();
		Function FunctionDet=iFunctionDao.save(function);
		registerGraph.updateGraph((Object) FunctionDet, MetaType.function);
		return FunctionDet;
	}*/

	/********************** UNUSED **********************/
/*	public Function update(Function function) throws IOException {
		function.setBaseEntity();
		Function functionDet=iFunctionDao.save(function);
		//registerService.createGraph();
		return functionDet;
	}*/

/********************** UNUSED **********************/
	/*public boolean isExists(String id) {
		return iFunctionDao.exists(id);
	}*/

/********************** UNUSED **********************/
	/*public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Function function = iFunctionDao.findOneById(appUuid, Id);
		function.setActive("N");
		iFunctionDao.save(function);
//		String ID = function.getId();
//		iFunctionDao.delete(ID);
//		function.exportBaseProperty();
	}*/

	/********************** UNUSED **********************/
	/*public Function findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iFunctionDao.findOneByUuidAndVersion(appUuid, uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Function getOneByUuidAndVersion(String uuid, String version) {
		return iFunctionDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Function findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iFunctionDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iFunctionDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/



	/********************** UNUSED 
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException **********************/
	/*public List<Function> findAllLatestActive() {
		Aggregation functionAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Function> functionResults = mongoTemplate.aggregate(functionAggr, "function", Function.class);
		List<Function> functionList = functionResults.getMappedResults();

		// Fetch the Function details for each id
		List<Function> result = new ArrayList<Function>();
		for (Function f : functionList) {
			Function functionLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				functionLatest = iFunctionDao.findOneByUuidAndVersion(appUuid, f.getId(), f.getVersion());
			} else {
				functionLatest = iFunctionDao.findOneByUuidAndVersion(f.getId(), f.getVersion());
			}
			if(functionLatest != null)
			{
			result.add(functionLatest);
			}
		}
		return result;
	}*/
	
	public List<Function> resolveName(List<Function> function) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		List<Function> functionList = new ArrayList<Function>();
		for (Function func : function) {
			String createdByRefUuid = func.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuidWithoutAppUuid(createdByRefUuid, MetaType.user.toString());
			func.getCreatedBy().getRef().setName(user.getName());
			functionList.add(func);
		}
		return functionList;
	}

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Function function) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Function functionNew = new Function();
		functionNew.setName(function.getName()+"_copy");
		functionNew.setActive(function.getActive());		
		functionNew.setDesc(function.getDesc());		
		functionNew.setTags(function.getTags());		
		functionNew.setFunctionInfo(function.getFunctionInfo());
		functionNew.setFuncType(function.getFuncType());	
		functionNew.setCategory(function.getCategory());
		save(functionNew);
		ref.setType(MetaType.function);
		ref.setUuid(functionNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/
}

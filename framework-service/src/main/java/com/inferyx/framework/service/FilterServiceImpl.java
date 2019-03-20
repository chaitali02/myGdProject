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
import com.inferyx.framework.dao.IFilterDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class FilterServiceImpl {	
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IFilterDao iFilterDao;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired  UserServiceImpl userServiceImpl;
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
	
	static final Logger logger = Logger.getLogger(FilterServiceImpl.class);

	
	public Filter resolveName(Filter filter) throws JsonProcessingException{		
		if (filter == null) {
			return null;
		}
		
		if(filter.getCreatedBy() != null) {
		String createdByRefUuid = filter.getCreatedBy().getRef().getUuid();
		//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
		filter.getCreatedBy().getRef().setName(user.getName());
		}
		if (filter.getAppInfo() != null) {
			for (int i = 0; i < filter.getAppInfo().size(); i++) {
				if(filter.getAppInfo().get(i)!=null){
				String appUuid = filter.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				filter.getAppInfo().get(i).getRef().setName(appName);
				}
			}
		}
		if(filter.getDependsOn().getRef().getType().equals(MetaType.relation.toString())) {
		String dependsOnRefUuid = filter.getDependsOn().getRef().getUuid();
		//Relation relationDO = relationServiceImpl.findLatestByUuid(dependsOnRefUuid);
		Relation relationDO = (Relation) commonServiceImpl.getLatestByUuid(dependsOnRefUuid, MetaType.relation.toString());
		String relationName = relationDO.getName();
		filter.getDependsOn().getRef().setName(relationName);
		} else if(filter.getDependsOn().getRef().getType().equals(MetaType.datapod.toString())) {
			String dependsOnRefUuid = filter.getDependsOn().getRef().getUuid();
			//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(dependsOnRefUuid);	
			Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(dependsOnRefUuid, MetaType.datapod.toString());
			String datapodName = datapodDO.getName();
			filter.getDependsOn().getRef().setName(datapodName);
		} else if(filter.getDependsOn().getRef().getType().equals(MetaType.dataset.toString())) {
			String dependsOnRefUuid = filter.getDependsOn().getRef().getUuid();
			//Dataset datasetDO = datasetServiceImpl.findLatestByUuid(dependsOnRefUuid);
			DataSet datasetDO = (DataSet) commonServiceImpl.getLatestByUuid(dependsOnRefUuid, MetaType.dataset.toString());
			String datasetName = datasetDO.getName();
			filter.getDependsOn().getRef().setName(datasetName);
		}
		
		for(int i=0;i<filter.getFilterInfo().size();i++){	
			for(int j=0;j<filter.getFilterInfo().get(i).getOperand().size();j++){
				MetaType operandRefType = filter.getFilterInfo().get(i).getOperand().get(j).getRef().getType();
	            String operandRefUuid = filter.getFilterInfo().get(i).getOperand().get(j).getRef().getUuid();
	            
				if(operandRefType.toString().equals(MetaType.datapod.toString())) {
					Integer operandAttributeId = filter.getFilterInfo().get(i).getOperand().get(j).getAttributeId();
					//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(operandRefUuid);
					Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(operandRefUuid, MetaType.datapod.toString());
					String datapodName = datapodDO.getName();
					filter.getFilterInfo().get(i).getOperand().get(j).getRef().setName(datapodName);
					List<Attribute> attributeList = datapodDO.getAttributes();
					filter.getFilterInfo().get(i).getOperand().get(j).setAttributeName(attributeList.get(operandAttributeId).getName());
				}
			}	
		}		
		return filter;
	}

	/********************** UNUSED **********************/
   /*public Filter getOneByUuidAndVersion(String uuid,String version){
	   
     return iFilterDao.findOneByUuidAndVersion(uuid,version);
    }*/

	/********************** UNUSED **********************/
	/*public Filter findOneById(String id){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iFilterDao.findOneById(appUuid,id);
		}
		else
			return iFilterDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public Filter findOneByUuid(String uuid){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iFilterDao.findOneById(appUuid,uuid);
	}*/

	/********************** UNUSED **********************/
	/*public Filter findLatestByUuid(String uuid){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iFilterDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));
		}
		return iFilterDao.findLatestByUuid(appUuid,uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id){
		return iFilterDao.exists(id);
	}*/

	
	/********************** UNUSED **********************/
	/*public Filter findLatest() {
		return resolveName(iFilterDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
/*public List<Filter> findAllLatestActive() 	{	   
   Aggregation filterAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
   AggregationResults<Filter> filterResults = mongoTemplate.aggregate(filterAggr,"filter", Filter.class);	   
   List<Filter> filterList = filterResults.getMappedResults();

   // Fetch the dimension details for each id
   List<Filter> result=new  ArrayList<Filter>();
   for(Filter f : filterList)
   {   
	   Filter filterLatest;
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
			filterLatest = iFilterDao.findOneByUuidAndVersion(appUuid,f.getId(), f.getVersion());
		}
		else
		{
			filterLatest = iFilterDao.findOneByUuidAndVersion(f.getId(), f.getVersion());
		}
		if(filterLatest != null)
		{
		result.add(filterLatest);
		}
   }
   return result;
}*/

	/********************** UNUSED **********************/
/*public Filter getAsOf(String uuid, String asOf) {
	String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
			? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
	if (appUuid != null) {
		return iFilterDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}
	else
		return iFilterDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
}*/
	
}

	
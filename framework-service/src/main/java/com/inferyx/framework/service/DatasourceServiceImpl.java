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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IDatasourceDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.MetaType;

@Service
public class DatasourceServiceImpl {

	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired
	IDatasourceDao iDatasourceDao;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(DatasourceServiceImpl.class);

	
	
	public List<Datasource> getDatasourceByType(String type) throws JsonProcessingException {   
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		MatchOperation filter = null;
		
		if(appUuid != null && !appUuid.isEmpty()) {
			filter = match(new Criteria("type").is(type).orOperator(new Criteria("appInfo.ref.uuid").is(appUuid),new Criteria("publicFlag").is("Y")));
		} else {
			filter = match(new Criteria("appInfo.ref.uuid").is(appUuid).orOperator(new Criteria("publicFlag").is("Y")));
		}
		 
		GroupOperation groupByUuid = group("uuid").max("version").as("version");
		Aggregation scheduleAggr = newAggregation(filter, groupByUuid);
		AggregationResults<Datasource> scheduleAggrResults = mongoTemplate.aggregate(scheduleAggr, MetaType.datasource.toString().toLowerCase(), Datasource.class);
		List<Datasource> datasourceResultList = (List<Datasource>) scheduleAggrResults.getMappedResults();
		List<Datasource> datasourceList = new ArrayList<Datasource>();
		if(datasourceResultList !=null &&  datasourceResultList.size() > 0) {
			for(Datasource datasource :datasourceResultList) {
				Datasource datasourceTemp=(Datasource) commonServiceImpl.getOneByUuidAndVersion(datasource.getId(), datasource.getVersion(), MetaType.datasource.toString());
				datasourceList.add(datasourceTemp);
			}
		}		
		return datasourceList; //iDatasourceDao.findDatasourceByType(appUuid,type);
	} 
	
	public List<Datasource> getDatasourceByParms(String type,String dbname,String host) {   
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();		
		return iDatasourceDao.findDatasourceByParms(appUuid,type,dbname,host);
	}

	

	public List<Datasource> getDatasourceByApp() throws JsonProcessingException {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;	
		Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
		//Datasource datasource = findLatestByUuid(application.getDataSource().getRef().getUuid());
		Datasource datasource = (Datasource) commonServiceImpl.getLatestByUuid(application.getDataSource().getRef().getUuid(), MetaType.datasource.toString());
		return getDatasourceByParms(datasource.getType(),datasource.getDbname(),datasource.getHost());
	}	
}

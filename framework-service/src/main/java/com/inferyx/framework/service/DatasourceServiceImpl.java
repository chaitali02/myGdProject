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
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IDatasourceDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.TrainResult;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class DatasourceServiceImpl {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired
	IDatasourceDao iDatasourceDao;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(DatasourceServiceImpl.class);

	/********************** UNUSED **********************/
	/*public Datasource findLatest() {
		return resolveName(iDatasourceDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public Datasource findLatestByUuid(String uuid){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iDatasourceDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));	
		}
		return iDatasourceDao.findLatestByUuid(appUuid,uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/

	/********************** UNUSED **********************/
	/*public Datasource findOneByUuidAndVersion(String uuid,String version){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iDatasourceDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));	
		}
		return iDatasourceDao.findOneByUuidAndVersion(appUuid,uuid,version);
	}*/

	/********************** UNUSED **********************/
	/*public Datasource findOneById(String id){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iDatasourceDao.findOneById(appUuid,id);
		}
		else
			return iDatasourceDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Datasource datasource = iDatasourceDao.findOneById(appUuid,id);
		datasource.setActive("N");
		iDatasourceDao.save(datasource);
//		String ID=datasource.getId();
//		iDatasourceDao.delete(ID);
//		datasource.exportBaseProperty();		
	}*/

	/********************** UNUSED **********************/
	/*public Datasource save(Datasource datasource) throws Exception{
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		datasource.setAppInfo(metaIdentifierHolderList);
		datasource.setBaseEntity();
		Datasource dataSource=iDatasourceDao.save(datasource);
		registerGraph.updateGraph((Object) dataSource, MetaType.datasource);
		return dataSource;
	}*/

	
	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public List<Datasource> findAllLatestActive() {	   
	   Aggregation datasourceAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<Datasource> datasourceResults = mongoTemplate.aggregate(datasourceAggr,"datasource", Datasource.class);	   
	   List<Datasource> datasourceList = datasourceResults.getMappedResults();

	   // Fetch the datasource details for each id
	   List<Datasource> result=new  ArrayList<Datasource>();
	   for(Datasource d : datasourceList)
	   {   
		   Datasource datasourceLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				datasourceLatest = iDatasourceDao.findOneByUuidAndVersion(appUuid,d.getId(), d.getVersion());
			}
			else
			{
				datasourceLatest = iDatasourceDao.findOneByUuidAndVersion(d.getId(), d.getVersion());
			}
			if(datasourceLatest != null)
			{
			result.add(datasourceLatest);
			}
	   }
	   return result;
	}*/
	
	public List<Datasource> getDatasourceByType(String type) throws JsonProcessingException {   
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		MatchOperation filter = null;
		
		if(appUuid != null && !appUuid.isEmpty() && !type.equalsIgnoreCase("file")) {
			filter = match(new Criteria("type").is(type).andOperator(new Criteria("appInfo.ref.uuid").is(appUuid)));
		} else {
			filter = match(new Criteria("type").is(type).andOperator(new Criteria("publicFlag").is("Y")));
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

	/********************** UNUSED **********************/
	/*public Datasource getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iDatasourceDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iDatasourceDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Datasource datasource) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Datasource datasourceNew = new Datasource();
		datasourceNew.setName(datasource.getName()+"_copy");
		datasourceNew.setActive(datasource.getActive());		
		datasourceNew.setDesc(datasource.getDesc());		
		datasourceNew.setTags(datasource.getTags());	
		datasourceNew.setType(datasource.getType());
		datasourceNew.setAccess(datasource.getAccess());
		datasourceNew.setDriver(datasource.getDriver());
		datasourceNew.setDbname(datasource.getDbname());
		datasourceNew.setHost(datasource.getHost());
		datasourceNew.setPort(datasource.getPort());
		datasourceNew.setUsername(datasource.getUsername());
		datasourceNew.setPassword(datasource.getPassword());
		datasourceNew.setPath(datasource.getPath());
		datasourceNew.setSubType(datasource.getSubType());
		save(datasourceNew);
		ref.setType(MetaType.datasource);
		ref.setUuid(datasourceNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> datasourceList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity datasource : datasourceList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = datasource.getId();
			String uuid = datasource.getUuid();
			String version = datasource.getVersion();
			String name = datasource.getName();
			String desc = datasource.getDesc();
			String published=datasource.getPublished();
			MetaIdentifierHolder createdBy = datasource.getCreatedBy();
			String createdOn = datasource.getCreatedOn();
			String[] tags = datasource.getTags();
			String active = datasource.getActive();
			List<MetaIdentifierHolder> appInfo = datasource.getAppInfo();
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
	

	public List<Datasource> getDatasourceByApp() throws JsonProcessingException {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;	
		Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
		//Datasource datasource = findLatestByUuid(application.getDataSource().getRef().getUuid());
		Datasource datasource = (Datasource) commonServiceImpl.getLatestByUuid(application.getDataSource().getRef().getUuid(), MetaType.datasource.toString());
		return getDatasourceByParms(datasource.getType(),datasource.getDbname(),datasource.getHost());
	}	
}

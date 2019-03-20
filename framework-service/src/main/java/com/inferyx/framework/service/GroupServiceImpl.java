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
import com.inferyx.framework.dao.IGroupDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Group;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Role;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;


@Service
public class GroupServiceImpl {	
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	private IGroupDao iUserGroupDao;	
	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	RegisterService registerService;
    @Autowired
    RoleServiceImpl roleServiceImpl;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;
    
    static final Logger logger = Logger.getLogger(GroupServiceImpl.class);

	/********************** UNUSED **********************/	
    /*public Group findLatest() {
		return resolveName(iUserGroupDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public Group findLatestByUuid(String uuid){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iUserGroupDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));
		}
		return iUserGroupDao.findLatestByUuid(appUuid,uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/

	/********************** UNUSED **********************/
	/*public Group findOneByUuidAndVersion(String uuid,String version){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iUserGroupDao.findOneByUuidAndVersion(appUuid,uuid,version);
	}*/

	/********************** UNUSED **********************/
	/*public Group findOneById(String id){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
		return iUserGroupDao.findOneById(appUuid,id);
		}
		else
			return iUserGroupDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Group userGroup = iUserGroupDao.findOneById(appUuid,id);
		userGroup.setActive("N");
		iUserGroupDao.save(userGroup);
//		String ID=userGroup.getId();
//		iUserGroupDao.delete(ID);		
	}*/


	/********************** UNUSED **********************/
	/*public Group save(Group userGroup) throws Exception{
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		userGroup.setAppInfo(metaIdentifierHolderList);
		userGroup.setBaseEntity();
		Group group=iUserGroupDao.save(userGroup);
		registerGraph.updateGraph((Object) group, MetaType.group);
		return group;
	}*/
	
	/*public List<Group> findAllVersion(String datapodName){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iUserGroupDao.findAllVersion(appUuid,datapodName);
	}*/

	/********************** UNUSED **********************/
	/*public Group getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iUserGroupDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iUserGroupDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Group group) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Group groupNew = new Group();
		groupNew.setName(group.getName()+"_copy");
		groupNew.setActive(group.getActive());		
		groupNew.setDesc(group.getDesc());		
		groupNew.setTags(group.getTags());	
		groupNew.setRoleInfo(group.getRoleInfo());
		save(groupNew);
		ref.setType(MetaType.group);
		ref.setUuid(groupNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/	

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> groupList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity group : groupList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = group.getId();
			String uuid = group.getUuid();
			String version = group.getVersion();
			String name = group.getName();
			String desc = group.getDesc();
			String published=group.getPublished();
			MetaIdentifierHolder createdBy = group.getCreatedBy();
			String createdOn = group.getCreatedOn();
			String[] tags = group.getTags();
			String active = group.getActive();
			List<MetaIdentifierHolder> appInfo = group.getAppInfo();
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
}

/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
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
	/*public Group findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iUserGroupDao.findAllByUuid(appUuid,uuid);	
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
	/*public List<Group> findAll(){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iUserGroupDao.findAll(); 
		}
		return iUserGroupDao.findAll(appUuid);
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
	 /*public Group resolveName(Group usergroup) throws JsonProcessingException{
			if(usergroup.getCreatedBy() != null)
			{
		    String createdByRefUuid = usergroup.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			usergroup.getCreatedBy().getRef().setName(user.getName());
			}
			if (usergroup.getAppInfo() != null) {
				for (int i = 0; i < usergroup.getAppInfo().size(); i++) {
					String appUuid = usergroup.getAppInfo().get(i).getRef().getUuid();
					Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
					String appName = application.getName();
					usergroup.getAppInfo().get(i).getRef().setName(appName);
				}
			}
			for(int i=0;i<usergroup.getRoleInfo().size();i++)
			{
				String uuid=usergroup.getRoleInfo().get(i).getRef().getUuid();
				Role role=roleServiceImpl.findLatestByUuid(uuid);
				usergroup.getRoleInfo().get(i).getRef().setName(role.getName());
			}
			return usergroup;
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
	/*public List<Group> findAllLatest() {
		{	   
			//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
			   Aggregation userGroupAggr = newAggregation(group("uuid").max("version").as("version"));
			   AggregationResults<Group> userGroupResults = mongoTemplate.aggregate(userGroupAggr,"group", Group.class);	   
			   List<Group> userGroupList = userGroupResults.getMappedResults();
			   
			   List<Group> result=new  ArrayList<Group>();
			   for(Group ug : userGroupList)
			   {   
				   Group userGroupLatest;
					String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
					if(appUuid != null)
					{
					//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();;
						//userGroupLatest = iUserGroupDao.findOneByUuidAndVersion(appUuid,ug.getId(), ug.getVersion());
						userGroupLatest = iUserGroupDao.findOneByUuidAndVersion(appUuid,ug.getId(), ug.getVersion());
					}
					else
					{
						userGroupLatest = iUserGroupDao.findOneByUuidAndVersion(ug.getId(), ug.getVersion());
					}
					//logger.debug("datapodLatest is " + datapodLatest.getName());
					if(userGroupLatest != null)
					{
					result.add(userGroupLatest);
					}
			   }
			   return result;
			}
	}*/

	/********************** UNUSED **********************/
	/*public List<Group> findAllLatestActive() 	
	{	   
	   Aggregation userGroupAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<Group> userGroupResults = mongoTemplate.aggregate(userGroupAggr,"group", Group.class);	   
	   List<Group> userGroupList = userGroupResults.getMappedResults();

	   // Fetch the userGroup details for each id
	   List<Group> result=new  ArrayList<Group>();
	   for(Group u : userGroupList)
	   {   
		   Group userGroupLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				userGroupLatest = iUserGroupDao.findOneByUuidAndVersion(appUuid,u.getId(), u.getVersion());
			}
			else
			{
				userGroupLatest = iUserGroupDao.findOneByUuidAndVersion(u.getId(), u.getVersion());
			}
			if(userGroupLatest != null)
			{
			result.add(userGroupLatest);
			}
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<Group> resolveName(List<Group> usergroup) {
		List<Group> userGroupList = new ArrayList<Group>();
		for(Group ug : usergroup)
		{
		 String createdByRefUuid = ug.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			ug.getCreatedBy().getRef().setName(user.getName());
			userGroupList.add(ug);
		}
		return userGroupList;
	}*/

	/********************** UNUSED **********************/
	/*public List<Group> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iUserGroupDao.findAllVersion(appUuid, uuid);
		}
		else
		return iUserGroupDao.findAllVersion(uuid);
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

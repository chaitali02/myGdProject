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
import com.inferyx.framework.dao.ISessionDao;
import com.inferyx.framework.dao.IUserDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Group;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Role;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class UserServiceImpl {
	
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IUserDao iUserDao;
	@Autowired
	ISessionDao iSessionDao;
	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired
	DagExecServiceImpl dagExecImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	SessionServiceImpl sessionServiceImpl;
	@Autowired
	RoleServiceImpl roleServiceImpl;
	@Autowired
	ActivityServiceImpl activityServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	GroupServiceImpl groupServiceImpl;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(UserServiceImpl.class);

	/********************** UNUSED **********************/
	/*public User findLatest() {
		return resolveName(iUserDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}	*/


	/********************** UNUSED **********************/
	/*public List<User> test(String param1) {	
		return iUserDao.test(param1);
	}*/
	/*public User findOneByUuidAndVersion(String uuid,String version){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iUserDao.findOneByUuidAndVersion(appUuid,uuid,version);
		}
		return iUserDao.findOneByUuidAndVersion(uuid,version);
	}*/
	/********************** UNUSED **********************/
	/*public User findOneById(String id){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iUserDao.findOneById(appUuid, id);
		}
		return iUserDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String id){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		User user = null;
		if(appUuid != null)
		{
		user = iUserDao.findOneById(appUuid,id);
		}
		else
		{
			user = iUserDao.findOne(id);
		}			
		user.setActive("N");
		iUserDao.save(user);
//		String ID=user.getId();
//		iUserDao.delete(ID);
		
	}*/
	
	/*public User Save(User user){
		user.exportBaseProperty();
		User userDo=iUserDao.findLatestByUuid(user.getUuid(), new Sort(Sort.Direction.DESC, "version"));
		Session session =iSessionDao.findSessionByUser(userDo.getUuid(),new Sort(Sort.Direction.DESC, "version"));
		User userObj= new User();
		MetaIdentifierHolder mholder= new MetaIdentifierHolder();
		MetaIdentifier mIdentifier = new MetaIdentifier();
		mIdentifier.setType(MetaType.user);
		mIdentifier.setUuid(session.getUserInfo().getRef().getUuid());
		mholder.setRef(mIdentifier);
		user.setCreatedBy(mholder);
		userObj=iUserDao.save(user);
		return userObj;
		}*/
	
	/*public User save(User user) throws Exception {	
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		user.setAppInfo(metaIdentifierHolderList);
		user.setBaseEntity();
		User userDet=iUserDao.save(user);
		registerGraph.updateGraph((Object) userDet, MetaType.user);
		return userDet;
		}*/

	
	/********************** UNUSED **********************/
	/*public List<User> findAllVersion(String userName){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iUserDao.findAllVersion(appUuid,userName);
		}
		return iUserDao.findAllVersion(userName);
	}*/

	/********************** UNUSED **********************/	
	/*public List<User> findAllLatestActive() 	
	{	   
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
	   Aggregation userAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<User> userResults = mongoTemplate.aggregate(userAggr,"user", User.class);	   
	   List<User> userList = userResults.getMappedResults();

	   // Fetch the user details for each id
	   List<User> result=new  ArrayList<User>();
	   for(User u : userList)
	   {   
		   User userLatest = null;
		   if(appUuid != null)
			{
		   userLatest = iUserDao.findOneByUuidAndVersion(appUuid,u.getId(),u.getVersion());
			}
		   else
		   {
			   userLatest = iUserDao.findOneByUuidAndVersion(u.getId(),u.getVersion());
		   }
		   if(userLatest != null)
		   {
		   result.add(userLatest);
		   }
	   }
	   return result;
	}*/
	
	public User findUserByName(String userName)  {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		User user = null;
//		if(appUuid != null)
//			user=iUserDao.findUserByName(appUuid,userName);
//		else
			user=iUserDao.findUserByName(userName);
	if(user==null)
		return null;
	else 
		return user;
	}
	
	public User findLatestByUsername(String userName) 
	{
		// String appUuid = (securityServiceImpl.getAppInfo() != null &&
		// securityServiceImpl.getAppInfo().getRef() != null
		// )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		// if(appUuid != null)
		// {
		// return iUserDao.findLatestByUsername(appUuid, userName,new
		// Sort(Sort.Direction.DESC, "version"));
		// }
		return iUserDao.findLatestByUsername(userName,new Sort(Sort.Direction.DESC, "version"));
	}
	
	/********************** UNUSED **********************/
	/*public List<Application> findAppBySession(String userUuid) throws JsonProcessingException {
		List<Application> appList = new ArrayList<Application>();			
		Session sessionDO = sessionServiceImpl.findSessionByStatus("active");				
		logger.info("useruuid: "+sessionDO.getUserInfo().getRef().getUuid());		
		if(userUuid.equals(sessionDO.getUserInfo().getRef().getUuid())) {
			//User userDO = findLatestByUuid(userUuid);
			User userDO = (User) commonServiceImpl.getLatestByUuid(userUuid, MetaType.user.toString());
			List<MetaIdentifierHolder> metaList = userDO.getAppInfo();
			for(MetaIdentifierHolder appInfo : metaList) {
				String appUuid = appInfo.getRef().getUuid();
				Application appDO = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				appList.add(appDO);
			}
		}
		return appList;
	}*/

	/********************** UNUSED **********************/
	/*public List<Role> findRoleBySession(String userUuid) throws JsonProcessingException {
		List<Role> roleList = new ArrayList<Role>();
		Session sessionDO = sessionServiceImpl.findSessionByStatus("active");
		if(userUuid.equals(sessionDO.getUserInfo().getRef().getUuid())) {
			User userDO = findLatestByUuid(userUuid);
			List<MetaIdentifierHolder> roleInfoList = userDO.getRoleInfo();
			for(MetaIdentifierHolder roleInfo : roleInfoList) {
				String roleUuid = roleInfo.getRef().getUuid();
				//Role roleDO = roleServiceImpl.findLatestByUuid(roleUuid);
				Role roleDO = (Role) commonServiceImpl.getLatestByUuid(roleUuid, MetaType.role.toString());
				roleList.add(roleDO);
			}
		}
		return roleList;
	}*/
	public List<Application> findAppByUser(String userName) throws JsonProcessingException {
		List<Application> appList = new ArrayList<Application>();			
			User userDO = findLatestByUsername(userName);
			List<MetaIdentifierHolder> metaList = userDO.getAppInfo();
			for(MetaIdentifierHolder appInfo : metaList) {
				String appUuid = appInfo.getRef().getUuid();
				Application appDO = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				appList.add(appDO);
			}
		return appList;
	}
	/********************** UNUSED **********************/
	/*public List<Role> findRoleByUser(String userName) throws JsonProcessingException {
		List<Role> roleList = new ArrayList<Role>();
			User userDO = findLatestByUsername(userName);
			List<MetaIdentifierHolder> roleInfoList = userDO.getRoleInfo();
			for(MetaIdentifierHolder roleInfo : roleInfoList) {
				String roleUuid = roleInfo.getRef().getUuid();
				//Role roleDO = roleServiceImpl.findLatestByUuid(roleUuid);
				Role roleDO = (Role) commonServiceImpl.getLatestByUuid(roleUuid, MetaType.role.toString());
				roleList.add(roleDO);
			}
		
		return roleList;
	}*/
	
	/********************** UNUSED **********************/
	/*public User getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iUserDao.findAsOf(appUuid,uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		return iUserDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/
	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(User user) throws Exception{
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		User userNew = new User();
		userNew.setName(user.getName()+"_copy");
		userNew.setActive(user.getActive());		
		userNew.setDesc(user.getDesc());		
		userNew.setTags(user.getTags());	
		userNew.setEmailId(user.getEmailId());
		userNew.setFirstName(user.getFirstName());
		userNew.setGroupInfo(user.getGroupInfo());
		userNew.setLastName(user.getLastName());
		userNew.setPassword(user.getPassword());
		userNew.setRoleInfo(user.getRoleInfo());
		userNew.setMiddleName(user.getMiddleName());
		save(userNew);
		ref.setType(MetaType.user);
		ref.setUuid(userNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> userList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity user : userList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = user.getId();
			String uuid = user.getUuid();
			String version = user.getVersion();
			String name = user.getName();
			String desc = user.getDesc();
			String published=user.getPublished();
			MetaIdentifierHolder createdBy = user.getCreatedBy();
			String createdOn = user.getCreatedOn();
			String[] tags = user.getTags();
			String active = user.getActive();
			List<MetaIdentifierHolder> appInfo = user.getAppInfo();
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
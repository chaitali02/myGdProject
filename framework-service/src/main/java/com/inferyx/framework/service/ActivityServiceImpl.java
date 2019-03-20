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

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IActivityDao;
import com.inferyx.framework.dao.IUserDao;
import com.inferyx.framework.domain.Activity;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.User;

@Service
public class ActivityServiceImpl {

	@Autowired
	IActivityDao iActivityDao;
	@Autowired
	IUserDao iUserDao;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	DagExecServiceImpl dagExecImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	SessionServiceImpl sessionServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(ActivityServiceImpl.class);


	/********************** UNUSED **********************/
	/*public Activity findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iActivityDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		} else
			return iActivityDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public Activity findLatest() {
		return resolveName(iActivityDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/
	
	/********************** UNUSED **********************/
	/*public List<Activity> test(String param1) {
		return iActivityDao.test(param1);
	}*/

	/**********************UNUSED**********************/
	/*public Activity findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iActivityDao.findOneByUuidAndVersion(uuid, version);
		} else
			return iActivityDao.findOneByUuidAndVersion(appUuid, uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Activity findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iActivityDao.findOne(id);
		} else
			return iActivityDao.findOneById(appUuid, id);
	}*/


	/********************** UNUSED **********************/
	/*public void delete(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			Activity activity = iActivityDao.findOne(id);
			activity.setActive("N");
			iActivityDao.save(activity);
			String ID = activity.getId();
			iActivityDao.delete(ID);
			activity.exportBaseProperty();
		} else {
			Activity activity = iActivityDao.findOneById(appUuid, id);
			activity.setActive("N");
			iActivityDao.save(activity);
			String ID = activity.getId();
			iActivityDao.delete(ID);
			activity.exportBaseProperty();
		}

	}*/

	/********************** UNUSED **********************/
	/*public Activity save(Activity activity) throws IOException {
		activity.setBaseEntity();
		Activity activityDet=iActivityDao.save(activity);
	
		return activityDet;
	}*/

		
	/********************** UNUSED **********************/
	/*public List<Activity> findAllLatestActive() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;

		Aggregation activityAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Activity> activityResults = mongoTemplate.aggregate(activityAggr, "activity",
				Activity.class);
		List<Activity> activityList = activityResults.getMappedResults();

		// Fetch the activity details for each id
		List<Activity> result = new ArrayList<Activity>();
		for (Activity a : activityList) {
			Activity activityLatest;
			if (appUuid != null) {
				activityLatest = iActivityDao.findOneByUuidAndVersion(appUuid, a.getId(), a.getVersion());
			} else {
				activityLatest = iActivityDao.findOneByUuidAndVersion(a.getId(), a.getVersion());
			}
			if(activityLatest != null)
			{
			result.add(activityLatest);
			}
		}
		return result;
	}*/

	public List<Activity> findActivityByUser(String UserUUID) throws JsonProcessingException {
		List<Activity> activity = iActivityDao.findActivityByUser(UserUUID, new Sort(Sort.Direction.DESC, "version"));
		List<Activity> Activity = new ArrayList<Activity>();
		for (Activity activityDo : activity) {
			if (activityDo.getCreatedBy() != null) {
				String createdByRefUuid = activityDo.getCreatedBy().getRef().getUuid();
				/*User user = userServiceImpl.findLatestByUuid(createdByRefUuid);*/
				User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, "user");
				activityDo.getCreatedBy().getRef().setName(user.getName());
			}
			Activity.add(activityDo);
		}
		return Activity;
	}

	public Activity createActivity(String UserUUID) throws IOException, JSONException, ParseException {
		User user = iUserDao.findLatestByUuid(UserUUID, new Sort(Sort.Direction.DESC, "version"));
		if (user == null) {
			return null;
		} else {
			User userDO = iUserDao.findOne(user.getId());
			Activity activity = new Activity();
			// MetaIdentifier activityRef = new
			// MetaIdentifier(MetaType.activity, activity.getUuid(),
			// activity.getVersion());
			MetaIdentifierHolder mHolder = new MetaIdentifierHolder();
			MetaIdentifier mIdentifier = new MetaIdentifier();
			mIdentifier.setType(MetaType.user);
			mIdentifier.setUuid(user.getUuid());
			mIdentifier.setVersion(user.getVersion());
			mHolder.setRef(mIdentifier);
			activity.setBaseEntity();
			activity.setName("login");
			activity.setStatus("success");
			activity.setDesc(user.getName() + " logged in successfully");
			activity.setUserInfo(mHolder);
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpSession session = requestAttributes.getRequest().getSession(false);
			Session sessionObj = sessionServiceImpl.findSessionBySessionId(session.getId());
			MetaIdentifierHolder activitySessionInfo = getMetaIdentierInstance(MetaType.session, sessionObj.getUuid(), sessionObj.getVersion());
			activity.setSessionInfo(activitySessionInfo);
			//activity.setSessionId("aca9afde-39db-11e6-ac61-9e71128cae71");
			activity.setRequestUrl(requestAttributes.getRequest().getRequestURL().toString());
			activity.setAppInfo(userDO.getAppInfo());
			activity.setCreatedBy(mHolder);			
			commonServiceImpl.save(MetaType.activity.toString(), activity);
			return activity;
		}
	}
	public MetaIdentifierHolder getMetaIdentierInstance(MetaType type, String uuid, String version) {
		MetaIdentifierHolder holder = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier(type, uuid, version);
		holder.setRef(ref);
		return holder;	
	}
	
	/********************** UNUSED **********************/
	/*public Activity getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iActivityDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iActivityDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/*public MetaIdentifierHolder saveAs(Activity activity) throws IOException {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Activity activityNew = new Activity();
		activityNew.setName(activity.getName()+"_copy");
		activityNew.setActive(activity.getActive());		
		activityNew.setDesc(activity.getDesc());		
		activityNew.setTags(activity.getTags());	
		activityNew.setRequestUrl(activity.getRequestUrl());
		activityNew.setSessionId(activity.getSessionId());
		activityNew.setStatus(activity.getStatus());
		activityNew.setUserInfo(activity.getUserInfo());
		Save(activityNew);
		ref.setType(MetaType.activity);
		ref.setUuid(activityNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> activityList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity activity : activityList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = activity.getId();
			String uuid = activity.getUuid();
			String version = activity.getVersion();
			String name = activity.getName();
			String desc = activity.getDesc();
			String published=activity.getPublished();
			MetaIdentifierHolder createdBy = activity.getCreatedBy();
			String createdOn = activity.getCreatedOn();
			String[] tags = activity.getTags();
			String active = activity.getActive();
			List<MetaIdentifierHolder> appInfo = activity.getAppInfo();
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
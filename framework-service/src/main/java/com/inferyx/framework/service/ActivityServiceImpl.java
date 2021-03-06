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
	SessionServiceImpl sessionServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(ActivityServiceImpl.class);



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
	


	
	
}
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
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.dao.IUserDao;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.Group;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.enums.RunMode;

@Service
public class FrameworkThreadServiceImpl {

	@Autowired
	IUserDao iUserDao;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;

	static final Logger logger = Logger.getLogger(FrameworkThreadServiceImpl.class);

	public FrameworkThreadServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	public void setSession(String userName) throws JSONException, ParseException, IOException {
		MetaIdentifierHolder appInfo = securityServiceImpl.getAppInfo(); 
		setSession(userName, appInfo);
	}
	
	public void setSession(String userName, MetaIdentifierHolder appInfo) throws JSONException, ParseException, IOException {
		User userDO = iUserDao.findLatestByUsername(userName, new Sort(Sort.Direction.DESC, "version"));
		if (userDO != null) {
			// Setting sessionContext object
			SessionContext sessionContext = new SessionContext();
			MetaIdentifier userMeta = new MetaIdentifier(MetaType.user, userDO.getUuid(), userDO.getVersion(),
					userDO.getName());
			MetaIdentifierHolder userInfo = new MetaIdentifierHolder();
			userInfo.setRef(userMeta);
			sessionContext.setUserInfo(userInfo);

			// Set default app and role
			sessionContext.setAppInfo((appInfo != null)? appInfo : userDO.getAppInfo().get(0));
//			List<MetaIdentifierHolder> roleInfoList = new ArrayList<>();
			Group group = (Group) commonServiceImpl.getOneByUuidAndVersion(
					userDO.getGroupInfo().get(0).getRef().getUuid(), userDO.getGroupInfo().get(0).getRef().getVersion(),
					MetaType.group.toString());
			sessionContext.setRoleInfo(group.getRoleId());

			// Add session
			Session sessionDO = new Session();
			MetaIdentifierHolder mHolder = new MetaIdentifierHolder();
			MetaIdentifier mIdentifier = new MetaIdentifier();
			mIdentifier.setType(MetaType.user);
			mIdentifier.setUuid(userDO.getUuid());
			mIdentifier.setVersion(userDO.getVersion());
			mIdentifier.setName(userDO.getName());
			mHolder.setRef(mIdentifier);
			sessionDO.setBaseEntity();
			if (sessionDO.getCreatedBy() == null) {
				MetaIdentifierHolder createdByMeta = new MetaIdentifierHolder();
				MetaIdentifier createdByInfo = new MetaIdentifier(MetaType.user, userDO.getUuid(), userDO.getVersion());
				createdByMeta.setRef(createdByInfo);
				sessionDO.setCreatedBy(createdByMeta);
			}
			sessionDO.setName("sys_" + sessionDO.getUuid());
			sessionDO.setUserInfo(mHolder);
			List<Status> statusList = new ArrayList<Status>();
			Status logoutstatus = new Status(Status.Stage.active, new Date());
			statusList.add(logoutstatus);
			sessionDO.setStatusList(statusList);
//			sessionDO.setSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());

			// Session savedSession = save(sessionDO);
			sessionDO.setType(RunMode.BATCH);
			commonServiceImpl.save(MetaType.session.toString(), sessionDO, appInfo);

			MetaIdentifier sessionMeta = new MetaIdentifier(MetaType.session, sessionDO.getUuid(),
					sessionDO.getVersion());
			MetaIdentifierHolder sessionInfo = new MetaIdentifierHolder();
			sessionInfo.setRef(sessionMeta);
			sessionContext.setSessionInfo(sessionInfo);
			logger.info(new ObjectMapper().writeValueAsString(sessionContext));

			// Add activity
//			Activity activityDO = activityServiceImpl.createActivity(userDO.getUuid());
//			MetaIdentifierHolder appInfo = sessionContext.getAppInfo();
			if (appInfo != null) {
				List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
				metaIdentifierHolderList.add(appInfo);
//				activityDO.setAppInfo(metaIdentifierHolderList);
			}

//			activityDO.setSessionInfo(sessionInfo);
			// iActivityDao.save(activityDO);
//			commonServiceImpl.save(MetaType.activity.toString(), activityDO);
			FrameworkThreadLocal.getSessionContext().set(sessionContext);
			// logger.info("\n\n\n\n\n\tNOW WE ARE USING USER SESSION.\n\n");

		}

	}
}

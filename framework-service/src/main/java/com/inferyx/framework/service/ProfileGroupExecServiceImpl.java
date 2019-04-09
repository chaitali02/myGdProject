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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IProfileGroupExecDao;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ProfileGroupExecServiceImpl extends BaseGroupExecTemplate {

	
		
	@Autowired
	SecurityServiceImpl securityServiceImpl;	
	@Autowired
	protected IProfileGroupExecDao iProfileGroupExecDao;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Resource(name="taskThreadMap")
	ConcurrentHashMap taskThreadMap;
	
	static final Logger logger = Logger.getLogger(ProfileGroupExecServiceImpl.class);

	public List<ProfileGroupExec> resolveName(List<ProfileGroupExec> ProfileGroupExec) throws JsonProcessingException {
		List<ProfileGroupExec> ProfileGroupExecList = new ArrayList<>();
		for(ProfileGroupExec profileGroupE : ProfileGroupExec)
		{
		String createdByRefUuid = profileGroupE.getCreatedBy().getRef().getUuid();
		//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
		profileGroupE.getCreatedBy().getRef().setName(user.getName());
		ProfileGroupExecList.add(profileGroupE);
		}
		return ProfileGroupExecList;
	}


	public List<ProfileGroupExec> findProfileGroupExecByProfileGroup(String profileGroupUUID,
			String profileGroupVersion) throws JsonProcessingException {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		List<ProfileGroupExec> profileGroupExecList = null;
		if(appUuid != null)
		{
		profileGroupExecList = iProfileGroupExecDao.findProfileGroupExecByProfileGroup(profileGroupUUID, profileGroupVersion);
		}
		else{
			profileGroupExecList = iProfileGroupExecDao.findProfileGroupExecByProfileGroup(profileGroupUUID, profileGroupVersion);
		}		
		return resolveName(profileGroupExecList);
	}

	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//ProfileGroupExec profileGroupExec = iProfileGroupExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		ProfileGroupExec profileGroupExec = (ProfileGroupExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.profilegroupExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.profilegroup);
		mi.setUuid(profileGroupExec.getDependsOn().getRef().getUuid());
		mi.setVersion(profileGroupExec.getDependsOn().getRef().getVersion());
		return mi;
	}
	
	/****************************Unused*****************************/
	/*public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.profilegroupExec, MetaType.profileExec);
	}*/
	


}

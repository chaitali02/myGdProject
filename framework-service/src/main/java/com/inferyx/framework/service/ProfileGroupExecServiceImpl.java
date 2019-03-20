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
	GraphRegister<?> registerGraph;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	UserServiceImpl userServiceImpl;	
	@Autowired
	SecurityServiceImpl securityServiceImpl;	
	@Autowired
	protected IProfileGroupExecDao iProfileGroupExecDao;
	@Autowired
	ProfileGroupServiceImpl profileGroupServiceImpl;
	@Autowired
	ProfileExecServiceImpl profileExecServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Resource(name="taskThreadMap")
	ConcurrentHashMap taskThreadMap;
	
	static final Logger logger = Logger.getLogger(ProfileGroupExecServiceImpl.class);

	/********************** UNUSED **********************/
	/*public ProfileGroupExec findLatest() {
		ProfileGroupExec profilegroupexec=null;
		if(iProfileGroupExecDao.findLatest(new Sort(Sort.Direction.DESC, "version"))!=null){
			profilegroupexec=resolveName(iProfileGroupExecDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
		}
		return profilegroupexec ;
	}*/

	/********************** UNUSED **********************/
	/*public ProfileGroupExec findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iProfileGroupExecDao.findOneById(appUuid,id);
		}
		return iProfileGroupExecDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public ProfileGroupExec findLatestByUuid(String ProfileGroupExecUUID) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iProfileGroupExecDao.findLatestByUuid(ProfileGroupExecUUID,new Sort(Sort.Direction.DESC, "version"));
		}
		return iProfileGroupExecDao.findLatestByUuid(appUuid,ProfileGroupExecUUID,new Sort(Sort.Direction.DESC, "version"));		
	}*/

	
	
	/********************** UNUSED **********************/
	/*public List<ProfileGroupExec> findAllLatestActive() 	
	{	   
	   Aggregation profileGroupExecAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<ProfileGroupExec> profileGroupExecResults = mongoTemplate.aggregate(profileGroupExecAggr,"profilegroupexec", ProfileGroupExec.class);	   
	   List<ProfileGroupExec> profileGroupExecList = profileGroupExecResults.getMappedResults();

	   // Fetch the ProfileGroupExec details for each id
	   List<ProfileGroupExec> result=new  ArrayList<ProfileGroupExec>();
	   for(ProfileGroupExec r : profileGroupExecList)
	   {   
		   ProfileGroupExec ProfileGroupExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				ProfileGroupExecLatest = iProfileGroupExecDao.findOneByUuidAndVersion(appUuid,r.getId(), r.getVersion());
			}
			else
			{
				ProfileGroupExecLatest = iProfileGroupExecDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if(ProfileGroupExecLatest != null)
			{
			result.add(ProfileGroupExecLatest);
			}
	   }
	   return result;
	}*/


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

	/********************** UNUSED **********************/
	/*public ProfileGroupExec getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iProfileGroupExecDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iProfileGroupExecDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	

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
	
	/**
	 * Kill group
	 * @param uuid
	 * @param version
	 */
	/*public void kill (String uuid, String version) {
		MetaIdentifier profileGroupExecIdentifier = new MetaIdentifier(MetaType.profilegroupExec, uuid, version);
		ProfileGroupExec profileGroupExec = (ProfileGroupExec) daoRegister.getRefObject(profileGroupExecIdentifier);
		if (profileGroupExec == null) {
			logger.info("Nothing to kill. Aborting ... ");
			return;
		}
		
		if (!Helper.getLatestStatus(profileGroupExec.getStatus()).equals(new Status(Status.Stage.RUNNING, new Date()))) {
			logger.info(" Status is not RUNNING. So aborting ... ");
		}
		try {
			commonServiceImpl.setMetaStatus(profileGroupExec, MetaType.profilegroupExec, Status.Stage.TERMINATING);
			for (MetaIdentifierHolder profileExecHolder : profileGroupExec.getProfileExecList()) {
				ProfileExec profileExec = (ProfileExec) daoRegister.getRefObject(profileExecHolder.getRef());
				if (profileExec == null) {
					continue;
				}
				profileExecServiceImpl.kill (profileExec.getUuid(), profileExec.getVersion());
			}
			commonServiceImpl.setMetaStatus(profileGroupExec, MetaType.profilegroupExec, Status.Stage.KILLED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.profilegroupExec, MetaType.profileExec);
	}
	


}

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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IProfileExecDao;
import com.inferyx.framework.dao.IProfileGroupExecDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.User;

@Service
public class ProfileExecServiceImpl extends BaseRuleExecTemplate {

	
	@Autowired
	IProfileExecDao iProfileExecDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	IProfileGroupExecDao iProfileGroupExecDao;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(ProfileExecServiceImpl.class);


	public ProfileExec resolveName(ProfileExec ProfileExec) {
		try {
			if (ProfileExec.getCreatedBy() != null) {
				String createdByRefUuid = ProfileExec.getCreatedBy().getRef().getUuid();
				User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
				ProfileExec.getCreatedBy().getRef().setName(user.getName());
			}
			if (ProfileExec.getAppInfo() != null) {
				for (int i = 0; i < ProfileExec.getAppInfo().size(); i++) {
					String appUuid = ProfileExec.getAppInfo().get(i).getRef().getUuid();
					Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
					String appName = application.getName();
					ProfileExec.getAppInfo().get(i).getRef().setName(appName);
				}
			}
			String dependsOnRefUuid = ProfileExec.getDependsOn().getRef().getUuid();
			Profile profileDO = (Profile) commonServiceImpl.getLatestByUuid(dependsOnRefUuid, MetaType.profile.toString());
			String profileName = profileDO.getName();
			ProfileExec.getDependsOn().getRef().setName(profileName);
			
			if(ProfileExec.getResult() != null){
				String dataStoreUuid = ProfileExec.getResult().getRef().getUuid();
				DataStore datastore = (DataStore) commonServiceImpl.getLatestByUuid(dataStoreUuid, MetaType.datastore.toString());
				ProfileExec.getResult().getRef().setName(datastore.getName());
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ProfileExec;
	}


	
	
	@SuppressWarnings({ "unchecked", "null" })
	public List<ProfileExec> findProfileExecByDatapod(String datapodUUID,String type) throws JsonProcessingException, ParseException {

		List<String> profileUUIDlist = new ArrayList<String>();
		List<ProfileExec> result = new ArrayList<ProfileExec>();

		List<Profile> profileobj = (List<Profile>) commonServiceImpl
				.getAllLatestCompleteObjects(MetaType.profile.toString(), null);
		for (Profile profile : profileobj) {
			if (datapodUUID.equalsIgnoreCase(profile.getDependsOn().getRef().getUuid())) {
				profileUUIDlist.add(profile.getUuid().toString());
			}
		}

		List<ProfileExec> profileexecobj = (List<ProfileExec>) commonServiceImpl
				.getAllLatestCompleteObjects(MetaType.profileExec.toString(), null);

		for (ProfileExec profileexec : profileexecobj) {

			for (String profileuuid : profileUUIDlist) {
				if (profileuuid.equalsIgnoreCase(profileexec.getDependsOn().getRef().getUuid())) {
					result.add(profileexec);
				}
			}
		}

		return result;
	}
	
	
	
	public List<ProfileExec> findProfileExecByProfile(String profileUuid, String startDate, String endDate, String type, String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException{		
		Query query = new Query();
		query.fields().include("statusList");
		query.fields().include("dependsOn");
		query.fields().include("exec");
		query.fields().include("result");
		query.fields().include("refKeyList");
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("createdBy");
		query.fields().include("createdOn");
		query.fields().include("active");
		query.fields().include("published");
		query.fields().include("appInfo");
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("EEE MMM dd HH:mm:ss yyyy z");// Tue Mar 13 04:15:00 2018 UTC
			
		
		try {
			if ((startDate != null	&& !StringUtils.isEmpty(startDate))
				&& (endDate != null	&& !StringUtils.isEmpty(endDate))) {
			query.addCriteria(Criteria.where("createdOn").gte(simpleDateFormat.parse(startDate))
						.lte(simpleDateFormat.parse(endDate)));
			}
			else if((startDate != null && !startDate.isEmpty()) && StringUtils.isEmpty(endDate)) 
					{
				query.addCriteria(Criteria.where("createdOn").gte(simpleDateFormat.parse(startDate)));
					}
			else if (endDate != null && !endDate.isEmpty())
				query.addCriteria(Criteria.where("createdOn").lte(simpleDateFormat.parse(endDate)));
			
			query.addCriteria(Criteria.where("statusList.stage").in("COMPLETED"));
			query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(profileUuid));
			query.addCriteria(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
			query.addCriteria(Criteria.where("active").is("Y"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		List<ProfileExec> profileExecObjList = new ArrayList<>();
		profileExecObjList = (List<ProfileExec>) mongoTemplate. find(query, ProfileExec.class);
		
		return profileExecObjList;		
	}
	
	public List<ProfileExec> findProfileExecByDatapod(String datapodUUID, String startDate, String endDate, String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException{
		List<ProfileExec> profileExecObjList = new ArrayList<>();
		List<ProfileExec> execObjList = new ArrayList<>();

		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("type");
		query.fields().include("dependsOn");
		query.fields().include("createdOn");
		query.fields().include("appInfo");

		try {
			if ((datapodUUID != null && !StringUtils.isEmpty(datapodUUID)))
				query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(datapodUUID));
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Profile> profileList = new ArrayList<>();
		profileList = (List<Profile>) mongoTemplate.find(query, Profile.class);
		
		for (Profile profile : profileList) {
			profileExecObjList = findProfileExecByProfile(profile.getUuid(), startDate, endDate, null, null);
			if(!profileExecObjList.isEmpty()) {
				execObjList.addAll(profileExecObjList);
			}
			
			}		
		return execObjList;	
	}
	

	public List<ProfileExec> findProfileExecByProfile(String profileUUID) {
		List<ProfileExec> profileExecList=null;
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if (appUuid != null) {
			profileExecList = iProfileExecDao.findOneByProfile(appUuid, profileUUID);
		}else{
			profileExecList = iProfileExecDao.findOneByProfile(profileUUID);
		}
		List<ProfileExec> resolvedProfileExecList = new ArrayList<>();
		for(ProfileExec profileExec : profileExecList) {
			resolveName(profileExec);
			resolvedProfileExecList.add(profileExec);
		}
		return resolvedProfileExecList;
	}
	
	public List<ProfileExec> findProfileExecByProfile(String profileUUID, String startDate, String endDate, String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException{
		List<ProfileExec> profileExecObjList = new ArrayList<>();
		List<ProfileExec> execObjList = new ArrayList<>();
		
			profileExecObjList = findProfileExecByProfile(profileUUID, startDate, endDate, null, null);
			if(!profileExecObjList.isEmpty()) {
				execObjList.addAll(profileExecObjList);
			}
			
					
		return execObjList;	
	}

	public List<ProfileExec> getProfileExecByProfileGroupExec(String profileGroupExecUuid, String profileGroupExecVersion) throws JsonProcessingException {
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		ProfileGroupExec profileGroupExec = null;
		if (appUuid == null) {
			profileGroupExec = iProfileGroupExecDao.findOneByUuidAndVersion(profileGroupExecUuid, profileGroupExecVersion);
		} else {
			profileGroupExec = iProfileGroupExecDao.findOneByUuidAndVersion(appUuid, profileGroupExecUuid, profileGroupExecVersion);
		}*/
		ProfileGroupExec profileGroupExec = (ProfileGroupExec) commonServiceImpl.getOneByUuidAndVersion(profileGroupExecUuid, profileGroupExecVersion, MetaType.profilegroupExec.toString());
		List<MetaIdentifierHolder> profileExecHolderList=profileGroupExec.getExecList();
		ProfileExec profileexec=null;
		List<ProfileExec> profileExecList=new ArrayList<ProfileExec>();
		for(MetaIdentifierHolder profileExecHolder:profileExecHolderList){
			//profileexec=iProfileExecDao.findOneByUuidAndVersion(profileExecHolder.getRef().getUuid(), profileExecHolder.getRef().getVersion());
			profileexec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(profileExecHolder.getRef().getUuid(), profileExecHolder.getRef().getVersion(), MetaType.profileExec.toString());
			resolveName(profileexec);
			profileExecList.add(profileexec);
		}
		return profileExecList;
	}

	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//ProfileExec profileExec = iProfileExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		ProfileExec profileExec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.profileExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.profile);
		mi.setUuid(profileExec.getDependsOn().getRef().getUuid());
		mi.setVersion(profileExec.getDependsOn().getRef().getVersion());
		return mi;
	}
    
	/*************************Unused***********************************/
	/*public void PAUSE (String uuid, String version) throws JsonProcessingException {
	MetaIdentifier profileExecMI = new MetaIdentifier(MetaType.profileExec, uuid, version);
//	ProfileExec profileExec = (ProfileExec) daoRegister.getRefObject(profileExecMI);
	ProfileExec profileExec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(profileExecMI.getUuid(), profileExecMI.getVersion(), profileExecMI.getType().toString(), "N");
	if (profileExec == null) {
		logger.info("ProfileExec not found. Exiting...");
		return;
	}
	if (!Helper.getLatestStatus(profileExec.getStatusList()).equals(new Status(Status.Stage.PENDING, new Date()))) {
		logger.info("Latest Status is not in PENDING. Exiting...");
	}
	try {
		commonServiceImpl.setMetaStatus(profileExec, MetaType.profileExec, Status.Stage.PAUSE);
	} catch (Exception e) {
		e.printStackTrace();
	}
}*/
	
	/*****************************Unused******************************/
	/*public void RESUME (String uuid, String version) throws JsonProcessingException {
		MetaIdentifier profileExecMI = new MetaIdentifier(MetaType.profileExec, uuid, version);
//		ProfileExec profileExec = (ProfileExec) daoRegister.getRefObject(profileExecMI);
		ProfileExec profileExec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(profileExecMI.getUuid(), profileExecMI.getVersion(), profileExecMI.getType().toString(), "N");
		if (profileExec == null) {
			logger.info("ProfileExec not found. Exiting...");
			return;
		}
		if (!Helper.getLatestStatus(profileExec.getStatusList()).equals(new Status(Status.Stage.PAUSE, new Date()))) {
			logger.info("Latest Status is not in PAUSE. Exiting...");
		}
		try {
			commonServiceImpl.setMetaStatus(profileExec, MetaType.profileExec, Status.Stage.RESUME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public ExecStatsHolder getNumRowsbyExec(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//ProfileExec profileExec = iProfileExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		ProfileExec profileExec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.profileExec.toString());
		//com.inferyx.framework.domain.DataStore dataStore=dataStoreServiceImpl.findOneByUuidAndVersion(profileExec.getResult().getRef().getUuid(), profileExec.getResult().getRef().getVersion());
		com.inferyx.framework.domain.DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(profileExec.getResult().getRef().getUuid(), profileExec.getResult().getRef().getVersion(), MetaType.datastore.toString(), "N");
		MetaIdentifier mi = new MetaIdentifier();
		ExecStatsHolder execHolder=new ExecStatsHolder();
		mi.setType(MetaType.datastore);
		mi.setUuid(profileExec.getResult().getRef().getUuid());
		mi.setVersion(profileExec.getResult().getRef().getVersion());
		execHolder.setRef(mi);
		execHolder.setNumRows(dataStore.getNumRows());
		execHolder.setPersistMode(dataStore.getPersistMode());
		execHolder.setRunMode(dataStore.getRunMode());
		return execHolder;
	}
	
	/**
	 * Kill meta thread if RUNNING
	 * @param uuid
	 * @param version
	 */
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.profileExec);
	}
}

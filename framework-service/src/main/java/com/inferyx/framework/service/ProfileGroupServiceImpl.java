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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.dao.IProfileGroupDao;
import com.inferyx.framework.dao.IProfileGroupExecDao;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ProfileGroupServiceImpl extends RuleGroupTemplate {

	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	IProfileGroupDao iProfileGroupDao;
	@Autowired 
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired 
	ProfileServiceImpl profileServiceImpl;
	@Autowired
	IProfileGroupExecDao iProfileGroupExecDao;
	@Autowired 
	ProfileExecServiceImpl profileExecServiceImpl;
	@Autowired
	ProfileGroupExecServiceImpl profileGroupExecServiceImpl;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	@Autowired
	ThreadPoolTaskExecutor metaGroupExecutor;
	@Autowired
	RegisterService registerService;

	static final Logger logger = Logger.getLogger(ProfileGroupServiceImpl.class);

	/********************** UNUSED **********************/
	/*public ProfileGroup findLatest() {
		return resolveName(iProfileGroupDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public ProfileGroup findOneById(String id)
	{
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
		return iProfileGroupDao.findOneById(appUuid,id);
		}
		return iProfileGroupDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public ProfileGroup save(ProfileGroup ProfileGroup) throws Exception{	
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		ProfileGroup.setAppInfo(metaIdentifierHolderList);
		ProfileGroup.setBaseEntity();
		ProfileGroup dqgroup=iProfileGroupDao.save(ProfileGroup);	
		registerGraph.updateGraph((Object) dqgroup, MetaType.profilegroup);
		return dqgroup;
	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileGroup> findAll(){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iProfileGroupDao.findAll(); 
		}
		return iProfileGroupDao.findAll(appUuid);
	}*/
	
	/*public ProfileGroup update(ProfileGroup ProfileGroup) throws IOException{
		ProfileGroup.exportBaseProperty();
		ProfileGroup dqGroup=iProfileGroupDao.save(ProfileGroup);
		registerService.createGraph();
		return dqGroup;
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id){
		return iProfileGroupDao.exists(id);
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String Id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		ProfileGroup ProfileGroup = iProfileGroupDao.findOneById(appUuid,Id);
		String ID=ProfileGroup.getId();
		iProfileGroupDao.delete(ID);
		ProfileGroup.setBaseEntity();
	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileGroup> findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iProfileGroupDao.findAllByUuid(appUuid, uuid);
		
	}*/

	/********************** UNUSED **********************/
	/*public ProfileGroup findOneByUuidAndVersion(String uuid, String version){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
		return iProfileGroupDao.findOneByUuidAndVersion(uuid,version);
		}
		else
			return iProfileGroupDao.findOneByUuidAndVersion(appUuid,uuid,version);
	}*/

	/********************** UNUSED **********************/
	/*public ProfileGroup findLatestByUuid(String uuid){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iProfileGroupDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));
		}
		return iProfileGroupDao.findLatestByUuid(appUuid,uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/
	
	/********************** UNUSED **********************/
	/*public List<ProfileGroup> findAllLatest() 	
	{	   
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
	   Aggregation profileAggr = newAggregation(group("uuid").max("version").as("version"));
	   AggregationResults<ProfileGroup> profileResults = mongoTemplate.aggregate(profileAggr,"profilegroup", ProfileGroup.class);	   
	   List<ProfileGroup> profileList = profileResults.getMappedResults();

	   // Fetch the profilegroup details for each id
	   List<ProfileGroup> result=new  ArrayList<ProfileGroup>();
	   for(ProfileGroup d : profileList)
	   {   
		   ProfileGroup profileGroupLatest;
		   if(appUuid!=null)
		   {
			   profileGroupLatest = iProfileGroupDao.findOneByUuidAndVersion(appUuid,d.getId(),d.getVersion());
		   }
		   else
		   {
			   profileGroupLatest = iProfileGroupDao.findOneByUuidAndVersion(d.getId(),d.getVersion());
		   }
		   if(profileGroupLatest != null)
			{
			result.add(profileGroupLatest);
			}
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileGroup> findAllLatestActive() 	
	{	   
	   Aggregation profileAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<ProfileGroup> profileResults = mongoTemplate.aggregate(profileAggr, "profilegroup", ProfileGroup.class);	   
	   List<ProfileGroup> profileList = profileResults.getMappedResults();

	   // Fetch the profilegroup details for each id
	   List<ProfileGroup> result=new  ArrayList<ProfileGroup>();
	   for(ProfileGroup r : profileList)
	   {   
		   ProfileGroup profileGroupLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				profileGroupLatest = iProfileGroupDao.findOneByUuidAndVersion(appUuid,r.getId(), r.getVersion());
			}
			else
			{
				profileGroupLatest = iProfileGroupDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if(profileGroupLatest != null)
			{
			result.add(profileGroupLatest);
			}
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileGroup> resolveName(List<ProfileGroup> ProfileGroup) {
		List<ProfileGroup> profileGroupList = new ArrayList<>();
		for(ProfileGroup profilegroup : ProfileGroup)
		{
		String createdByRefUuid = profilegroup.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		profilegroup.getCreatedBy().getRef().setName(user.getName());
		profileGroupList.add(profilegroup);
		List<MetaIdentifierHolder> profileInfo = profilegroup.getRuleInfo();
		for(int i=0; i<profileInfo.size(); i++)
		{
			String profileUuid = profileInfo.get(i).getRef().getUuid();
			Profile profile = profileServiceImpl.findLatestByUuid(profileUuid);
			String profileName = profile.getName();
			profileInfo.get(i).getRef().setName(profileName);			
		}
		}
		return profileGroupList;
	}*/

	/********************** UNUSED **********************/
	/*public ProfileGroup resolveName(ProfileGroup ProfileGroup) {
		String createdByRefUuid = ProfileGroup.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		ProfileGroup.getCreatedBy().getRef().setName(user.getName());
		List<MetaIdentifierHolder> profileInfo = ProfileGroup.getRuleInfo();
		for(int i=0; i<profileInfo.size(); i++)
		{
			String profileUuid = profileInfo.get(i).getRef().getUuid();
			Profile profile = profileServiceImpl.findLatestByUuid(profileUuid);
			String profileName = profile.getName();
			profileInfo.get(i).getRef().setName(profileName);			
		}
		ProfileGroup.setRuleInfo(profileInfo);
		return ProfileGroup;
	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileGroup> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		List<ProfileGroup> profileGroupList;
		if(appUuid != null)
		{
			profileGroupList = iProfileGroupDao.findAllVersion(appUuid, uuid);
		}
		else
		{
			profileGroupList = iProfileGroupDao.findAllVersion(uuid);
		}
		return resolveName(profileGroupList);
	}*/

	/********************** UNUSED **********************/
	/*public ProfileGroup getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iProfileGroupDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iProfileGroupDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(ProfileGroup profileGroup) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		ProfileGroup profileGroupNew = new ProfileGroup();
		profileGroupNew.setName(profileGroup.getName()+"_copy");
		profileGroupNew.setActive(profileGroup.getActive());		
		profileGroupNew.setDesc(profileGroup.getDesc());		
		profileGroupNew.setTags(profileGroup.getTags());	
		profileGroupNew.setRuleInfo(profileGroup.getRuleInfo());
		profileGroupNew.setInParallel(profileGroup.getInParallel());
		save(profileGroupNew);
		ref.setType(MetaType.profilegroup);
		ref.setUuid(profileGroupNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> profileGroupList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity profile : profileGroupList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = profile.getId();
			String uuid = profile.getUuid();
			String version = profile.getVersion();
			String name = profile.getName();
			String desc = profile.getDesc();
			String published=profile.getPublished();
			MetaIdentifierHolder createdBy = profile.getCreatedBy();
			String createdOn = profile.getCreatedOn();
			String[] tags = profile.getTags();
			String active = profile.getActive();
			List<MetaIdentifierHolder> appInfo = profile.getAppInfo();
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

	/**
	 * 
	 * @param profileGroupUUID
	 * @param profileGroupVersion
	 * @param execParams
	 * @param datapodList
	 * @param dagExec
	 * @return
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public ProfileGroupExec create(String profileGroupUUID, String profileGroupVersion, ExecParams execParams, List<String> datapodList, DagExec dagExec) throws NumberFormatException, Exception {
		return create(profileGroupUUID, profileGroupVersion, execParams, datapodList, dagExec, null);
	}
	
	/**
	 * 
	 * @param profileGroupUUID
	 * @param profileGroupVersion
	 * @param execParams
	 * @param datapodList
	 * @param dagExec
	 * @param profileGroupExec
	 * @return
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public ProfileGroupExec create(String profileGroupUUID, String profileGroupVersion, ExecParams execParams, List<String> datapodList, DagExec dagExec, ProfileGroupExec profileGroupExec) throws NumberFormatException, Exception {
		return (ProfileGroupExec) super.create(profileGroupUUID, profileGroupVersion, MetaType.profilegroup, MetaType.profilegroupExec, MetaType.profile, MetaType.profileExec, execParams, datapodList, profileGroupExec, dagExec);
	}
	
	/**
	 * 
	 * @param profileGroupUUID
	 * @param profileGroupVersion
	 * @param execParams
	 * @param profileGroupExec
	 * @return
	 * @throws Exception
	 */
	public MetaIdentifier execute(String profileGroupUUID, String profileGroupVersion,ExecParams execParams, ProfileGroupExec profileGroupExec, RunMode runMode) throws Exception {
		return super.execute(profileGroupUUID, profileGroupVersion, MetaType.profilegroup, MetaType.profilegroupExec, MetaType.profile, MetaType.profileExec, execParams, profileGroupExec, runMode);
	}
	
	/**
	 * 
	 * @param execUuid
	 * @param execVersion
	 * @param refKeyMap
	 * @return
	 * @throws Exception
	 */
	public BaseRuleGroupExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {
		return super.parse(execUuid, execVersion, MetaType.profilegroup, MetaType.profilegroupExec, MetaType.profile, MetaType.profileExec, refKeyMap, otherParams, datapodList, dagExec, runMode);
	}
	
	/**
	 * 
	 * @param type
	 * @param uuid
	 * @param version
	 * @throws Exception
	 */
	public void restart(String type,String uuid,String version, RunMode runMode) throws Exception{
		//ProfileGroupExec profileGroupExec= profileGroupExecServiceImpl.findOneByUuidAndVersion(uuid, version);
		ProfileGroupExec profileGroupExec = (ProfileGroupExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.profilegroupExec.toString());
		profileGroupExec = (ProfileGroupExec) parse(profileGroupExec.getUuid(), profileGroupExec.getVersion(), null, null, null, null, runMode);
		execute(profileGroupExec.getDependsOn().getRef().getUuid(), profileGroupExec.getDependsOn().getRef().getVersion(),null, profileGroupExec, runMode);
	}
	
	/**
	 * 
	 * @param baseExec
	 * @return
	 * @throws Exception
	 */
	public Status restart(BaseExec baseExec) throws Exception {
		try {
			return super.restart(baseExec.getUuid(), baseExec.getVersion(), MetaType.profilegroupExec, MetaType.profileExec);
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}


	/**
	 * Override Executable.execute()
	 */
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		execute(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), execParams, (ProfileGroupExec) baseExec, runMode);
		return null;
	}

	/**
	 * Override Parsable.parse()
	 */
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(), MetaType.profilegroup, MetaType.profilegroupExec, MetaType.profile, MetaType.profileExec, 
				DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), null, null, null, runMode);
	}
	
}

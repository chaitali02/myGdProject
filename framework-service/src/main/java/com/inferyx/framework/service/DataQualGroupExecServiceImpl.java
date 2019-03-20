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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IDataQualGroupExecDao;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class DataQualGroupExecServiceImpl  extends BaseGroupExecTemplate {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;	
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	protected IDataQualGroupExecDao iDataQualGroupExecDao;
	@Autowired
	DataQualGroupServiceImpl dataQualGroupServiceImpl;
	@Autowired
	DataQualExecServiceImpl dataQualExecServiceImpl;
	
	static final Logger logger = Logger.getLogger(DataQualGroupExecServiceImpl.class);	

	/********************** UNUSED **********************/
	/*public DataQualGroupExec findLatest() {
		return resolveName(iDataQualGroupExecDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/
	
	/********************** UNUSED **********************/
	/*public DataQualGroupExec findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iDataQualGroupExecDao.findOneById(appUuid,id);
		}
		return iDataQualGroupExecDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public DataQualGroupExec save(DataQualGroupExec DataQualGroupExec) throws Exception {
		if(DataQualGroupExec.getAppInfo() == null)	{
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		DataQualGroupExec.setAppInfo(metaIdentifierHolderList);
		}
		DataQualGroupExec.setBaseEntity();
		DataQualGroupExec dqexecgroup=iDataQualGroupExecDao.save(DataQualGroupExec);
		registerGraph.updateGraph((Object) dqexecgroup, MetaType.dqgroupExec);
		return dqexecgroup;		
	}*/

	/********************** UNUSED **********************/
	/*public DataQualGroupExec findLatestByUuid(String DataQualGroupExecUUID) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iDataQualGroupExecDao.findLatestByUuid(DataQualGroupExecUUID,new Sort(Sort.Direction.DESC, "version"));
		}
		return iDataQualGroupExecDao.findLatestByUuid(appUuid,DataQualGroupExecUUID,new Sort(Sort.Direction.DESC, "version"));		
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		DataQualGroupExec DataQualGroupExec = iDataQualGroupExecDao.findOneById(appUuid,id);
		DataQualGroupExec.setActive("N");
		iDataQualGroupExecDao.save(DataQualGroupExec);
//		String ID=DataQualGroupExec.getId();
//		iDataQualGroupExecDao.delete(ID);		
	}*/

	/********************** UNUSED **********************/
	/*public List<DataQualGroupExec> findAllLatestActive() 	
	{	   
	   Aggregation DataQualGroupExecAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<DataQualGroupExec> DataQualExecResults = mongoTemplate.aggregate(DataQualGroupExecAggr,"dqgroupexec", DataQualGroupExec.class);	   
	   List<DataQualGroupExec> DataQualGroupExecList = DataQualExecResults.getMappedResults();

	   // Fetch the DataQualExec details for each id
	   List<DataQualGroupExec> result=new  ArrayList<DataQualGroupExec>();
	   for(DataQualGroupExec r : DataQualGroupExecList)
	   {   
		   DataQualGroupExec DataQualGroupExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				DataQualGroupExecLatest = iDataQualGroupExecDao.findOneByUuidAndVersion(appUuid,r.getId(), r.getVersion());
			}
			else
			{
				DataQualGroupExecLatest = iDataQualGroupExecDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if(DataQualGroupExecLatest != null)
			{
			result.add(DataQualGroupExecLatest);
			}
	   }
	   return result;
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public DataQualGroupExec findOneByUuidAndVersion(String uuid, String version){
		 DataQualGroupExec DataQualGroupExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				return DataQualGroupExecLatest = iDataQualGroupExecDao.findOneByUuidAndVersion(appUuid,uuid,version);
			}			
				return DataQualGroupExecLatest = iDataQualGroupExecDao.findOneByUuidAndVersion(uuid,version);
	}*/

	public List<DataQualGroupExec> resolveNameGroup(List<DataQualGroupExec> DataQualGroupExec) throws JsonProcessingException {
		List<DataQualGroupExec> DataQualGroupExecList = new ArrayList<>();
		for(DataQualGroupExec DataqualE : DataQualGroupExec)	{
			String createdByRefUuid = DataqualE.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			DataqualE.getCreatedBy().getRef().setName(user.getName());
			DataQualGroupExecList.add(DataqualE);
		}
		return DataQualGroupExecList;
	}

	public List<DataQualExec> resolveName(List<DataQualExec> DataQualExec) throws JsonProcessingException {
		List<DataQualExec> DataQualExecList = new ArrayList<>();
		for(DataQualExec DataqualE : DataQualExec)	{
			String createdByRefUuid = DataqualE.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			DataqualE.getCreatedBy().getRef().setName(user.getName());
			DataQualExecList.add(DataqualE);
		}
		return DataQualExecList;
	}

	/********************** UNUSED **********************/
	/*public DataQualGroupExec resolveName(DataQualGroupExec dataQualGroupExec) {
		
		if (dataQualGroupExec == null)
			return null;
			
		if (dataQualGroupExec.getCreatedBy() != null) {
			String createdByRefUuid = dataQualGroupExec.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			dataQualGroupExec.getCreatedBy().getRef().setName(user.getName());
		}
		if (dataQualGroupExec.getAppInfo() != null) {
			for (int i = 0; i < dataQualGroupExec.getAppInfo().size(); i++) {
				if( dataQualGroupExec.getAppInfo().get(i)!=null){
				String appUuid = dataQualGroupExec.getAppInfo().get(i).getRef().getUuid();
				Application application = applicationServiceImpl.findLatestByUuid(appUuid);
				String appName = application.getName();
				dataQualGroupExec.getAppInfo().get(i).getRef().setName(appName);
				}
			}
		}
		String dependsOnUuid=dataQualGroupExec.getDependsOn().getRef().getUuid();
		DataQualGroup dataQualGroup=dataQualGroupServiceImpl.findLatestByUuid(dependsOnUuid);
		dataQualGroupExec.getDependsOn().getRef().setName(dataQualGroup.getName());

		if(dataQualGroupExec.getExecList()!=null){
		for(int i=0;i<dataQualGroupExec.getExecList().size();i++){
		String dqExecUuid=dataQualGroupExec.getExecList().get(i).getRef().getUuid();
		DataQualExec dataQualExec=dataQualExecServiceImpl.findLatestByUuid(dqExecUuid);
		dataQualGroupExec.getExecList().get(i).getRef().setName(dataQualExec.getName());

		}

		}

		return dataQualGroupExec;

//		String createdByRefUuid = dataQualGroupExec.getCreatedBy().getRef().getUuid();
//		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
//		dataQualGroupExec.getCreatedBy().getRef().setName(user.getName());
//		return dataQualGroupExec;

	}*/

	
	public List<DataQualGroupExec> finddqGroupExecBydqGroup(String dqGroupExecUUID,String dqGroupExecVersion) throws JsonProcessingException {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		List<DataQualGroupExec> dqGroupExecList = null;
		if(appUuid != null)		
			dqGroupExecList = iDataQualGroupExecDao.finddqGroupExecBydqGroup(dqGroupExecUUID, dqGroupExecVersion);	
		else
			dqGroupExecList = iDataQualGroupExecDao.finddqGroupExecBydqGroup(dqGroupExecUUID, dqGroupExecVersion);
		return resolveNameGroup(dqGroupExecList);
	}

	/********************** UNUSED **********************/
	/*public DataQualGroupExec getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iDataQualGroupExecDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iDataQualGroupExecDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(DataQualGroupExec dqgroupExec) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		DataQualGroupExec dqGroupExecNew = new DataQualGroupExec();
		dqGroupExecNew.setName(dqgroupExec.getName()+"_copy");
		dqGroupExecNew.setActive(dqgroupExec.getActive());		
		dqGroupExecNew.setDesc(dqgroupExec.getDesc());		
		dqGroupExecNew.setTags(dqgroupExec.getTags());
		dqGroupExecNew.setStatusList(dqgroupExec.getStatusList());
		dqGroupExecNew.setDependsOn(dqgroupExec.getDependsOn());
		dqGroupExecNew.setExecList(dqgroupExec.getExecList());		
		save(dqGroupExecNew);
		ref.setType(MetaType.dqgroupExec);
		ref.setUuid(dqGroupExecNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> dqGroupExecList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity dqgroup : dqGroupExecList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = dqgroup.getId();
			String uuid = dqgroup.getUuid();
			String version = dqgroup.getVersion();
			String name = dqgroup.getName();
			String desc = dqgroup.getDesc();
			String published=dqgroup.getPublished();
			MetaIdentifierHolder createdBy = dqgroup.getCreatedBy();
			String createdOn = dqgroup.getCreatedOn();
			String[] tags = dqgroup.getTags();
			String active = dqgroup.getActive();
			List<MetaIdentifierHolder> appInfo = dqgroup.getAppInfo();
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

	/********************** UNUSED **********************/
//	public List<DataQualExec> findDataQualExecByDataqualGroupExec(String dataqualGroupExecUuid, String dataqualGroupExecVersion) throws JsonProcessingException {
//		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;*/
//		List<DataQualExec> dataQualExecList = null;
//		DataQualGroupExec dataQualGroupExec = null;		
//		/*if (appUuid == null) {
//			dataQualGroupExec = iDataQualGroupExecDao.findOneByUuidAndVersion(dataqualGroupExecUuid, dataqualGroupExecVersion);
//		} else {
//			dataQualGroupExec = iDataQualGroupExecDao.findOneByUuidAndVersion(appUuid, dataqualGroupExecUuid, dataqualGroupExecVersion);
//		}*/
//		dataQualGroupExec = (DataQualGroupExec) commonServiceImpl.getOneByUuidAndVersion(dataqualGroupExecUuid, dataqualGroupExecVersion, MetaType.dqgroupExec.toString());
//		for (MetaIdentifierHolder dataQualExecHolder : dataQualGroupExec.getExecList()) {
////			dataQualExecList.add((DataQualExec)daoRegister.getRefObject(dataQualExecHolder.getRef()));
//			dataQualExecList.add((DataQualExec)commonServiceImpl.getOneByUuidAndVersion(dataQualExecHolder.getRef().getUuid(), dataQualExecHolder.getRef().getVersion(), dataQualExecHolder.getRef().getType().toString()));			
//		}
//		return resolveName(dataQualExecList);
//	}

	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//DataQualGroupExec dataQualGroupExec = iDataQualGroupExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		DataQualGroupExec dataQualGroupExec = (DataQualGroupExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.dqgroupExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.dqgroup);
		mi.setUuid(dataQualGroupExec.getDependsOn().getRef().getUuid());
		mi.setVersion(dataQualGroupExec.getDependsOn().getRef().getVersion());
		return mi;
	}
	
	/**
	 * Kill group
	 * @param uuid
	 * @param version
	 */
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.dqgroupExec, MetaType.dqExec);
	}	
}

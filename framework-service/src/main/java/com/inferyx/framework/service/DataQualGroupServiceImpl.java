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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IDataQualGroupDao;
import com.inferyx.framework.dao.IDataQualGroupExecDao;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.register.GraphRegister;

@Service
public class DataQualGroupServiceImpl extends RuleGroupTemplate {	
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IDataQualGroupDao iDataQualGroupDao;
	@Autowired 
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired 
	DataQualServiceImpl dataQualServiceImpl;
	@Autowired
	IDataQualGroupExecDao iDataQualGroupExecDao;
	@Autowired 
	DataQualExecServiceImpl dataQualExecServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	DataQualGroupExecServiceImpl dataQualGroupExecServiceImpl;
	
	static final Logger logger = Logger.getLogger(DataQualGroupServiceImpl.class);

	/********************** UNUSED **********************/
	/*public DataQualGroup findLatest() {
		DataQualGroup dataqualgroup=null;
		if(iDataQualGroupDao.findLatest(new Sort(Sort.Direction.DESC, "version"))!=null){
			dataqualgroup=resolveName(iDataQualGroupDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
		}
		return dataqualgroup ;
	}*/

	/********************** UNUSED **********************/
	/*public DataQualGroup findOneById(String id)	{
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
		return iDataQualGroupDao.findOneById(appUuid,id);
		}
		return iDataQualGroupDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public DataQualGroup save(DataQualGroup dataQualGroup) throws Exception{	
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		dataQualGroup.setAppInfo(metaIdentifierHolderList);
		dataQualGroup.setBaseEntity();
		DataQualGroup dqgroup=iDataQualGroupDao.save(dataQualGroup);
		registerGraph.updateGraph((Object) dqgroup, MetaType.dqgroup);
		return dqgroup;
	}*/

	/********************** UNUSED **********************/
	/*public List<DataQualGroup> findAll(){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iDataQualGroupDao.findAll(); 
		}
		return iDataQualGroupDao.findAll(appUuid);
	}*/
	
	/*public DataQualGroup update(DataQualGroup dataQualGroup) throws IOException{
		dataQualGroup.exportBaseProperty();
		DataQualGroup dqGroup=iDataQualGroupDao.save(dataQualGroup);
		registerService.createGraph();
		return dqGroup;
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id){
		return iDataQualGroupDao.exists(id);
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String Id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		DataQualGroup dataQualGroup = iDataQualGroupDao.findOneById(appUuid,Id);
		dataQualGroup.setActive("N");
		iDataQualGroupDao.save(dataQualGroup);
//		String ID=dataQualGroup.getId();
//		iDataQualGroupDao.delete(ID);
//		dataQualGroup.exportBaseProperty();
	}*/

	/********************** UNUSED **********************/
	/*public List<DataQualGroup> findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDataQualGroupDao.findAllByUuid(appUuid, uuid);		
	}*/

	/********************** UNUSED **********************/
	/*public DataQualGroup findOneByUuidAndVersion(String uuid, String version){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
		return iDataQualGroupDao.findOneByUuidAndVersion(uuid,version);
		}
		else
			return iDataQualGroupDao.findOneByUuidAndVersion(appUuid,uuid,version);
	}*/

	/********************** UNUSED **********************/
	/*public DataQualGroup findLatestByUuid(String uuid){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iDataQualGroupDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));
		}
		return iDataQualGroupDao.findLatestByUuid(appUuid,uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/	

	/********************** UNUSED **********************/
	/*public List<DataQualGroup> findAllLatest() 	
	{	   
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
	   Aggregation dqAggr = newAggregation(group("uuid").max("version").as("version"));
	   AggregationResults<DataQualGroup> dqResults = mongoTemplate.aggregate(dqAggr, "dqgroup", DataQualGroup.class);	   
	   List<DataQualGroup> dataQualList = dqResults.getMappedResults();

	   // Fetch the dqgroup details for each id
	   List<DataQualGroup> result=new  ArrayList<DataQualGroup>();
	   for(DataQualGroup d : dataQualList)
	   {   
		   DataQualGroup dqGroupLatest;
		   if(appUuid!=null)
		   {
			   dqGroupLatest = iDataQualGroupDao.findOneByUuidAndVersion(appUuid,d.getId(),d.getVersion());
		   }
		   else
		   {
			   dqGroupLatest = iDataQualGroupDao.findOneByUuidAndVersion(d.getId(),d.getVersion());
		   }
		   if(dqGroupLatest != null)
			{
			result.add(dqGroupLatest);
			}
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<DataQualGroup> findAllLatestActive() 	
	{	   
	   Aggregation dqAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<DataQualGroup> dqResults = mongoTemplate.aggregate(dqAggr, "dqgroup", DataQualGroup.class);	   
	   List<DataQualGroup> dqList = dqResults.getMappedResults();

	   // Fetch the dggroup details for each id
	   List<DataQualGroup> result=new  ArrayList<DataQualGroup>();
	   for(DataQualGroup r : dqList)
	   {   
		   DataQualGroup dqGroupLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				dqGroupLatest = iDataQualGroupDao.findOneByUuidAndVersion(appUuid,r.getId(), r.getVersion());
			}
			else
			{
				dqGroupLatest = iDataQualGroupDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if(dqGroupLatest != null)
			{
			result.add(dqGroupLatest);
			}
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<DataQualGroup> resolveName(List<DataQualGroup> dataQualGroup) {
		List<DataQualGroup> dataQualList = new ArrayList<>();
		for(DataQualGroup dqgroup : dataQualGroup)
		{
		String createdByRefUuid = dqgroup.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		dqgroup.getCreatedBy().getRef().setName(user.getName());
		dataQualList.add(dqgroup);
		List<MetaIdentifierHolder> dqInfo = dqgroup.getRuleInfo();
		for(int i=0; i<dqInfo.size(); i++)
		{
			String dqUuid = dqInfo.get(i).getRef().getUuid();
			DataQual dq = dataQualServiceImpl.findLatestByUuid(dqUuid);
			String dqName = dq.getName();
			dqInfo.get(i).getRef().setName(dqName);			
		}
		}
		return dataQualList;
	}*/
	
	/*public DataQualGroup resolveName(DataQualGroup dataQualGroup) {
		String createdByRefUuid = dataQualGroup.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		dataQualGroup.getCreatedBy().getRef().setName(user.getName());
		List<MetaIdentifierHolder> dqInfo = dataQualGroup.getRuleInfo();
		for(int i=0; i<dqInfo.size(); i++)
		{
			String dqUuid = dqInfo.get(i).getRef().getUuid();
			DataQual dq = dataQualServiceImpl.findLatestByUuid(dqUuid);
			String dqName = dq.getName();
			dqInfo.get(i).getRef().setName(dqName);			
		}
		dataQualGroup.setRuleInfo(dqInfo);
		return dataQualGroup;
	}*/

	/********************** UNUSED **********************/
	/*public List<DataQualGroup> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		List<DataQualGroup> dqGroupList;
		if(appUuid != null)
		{
			dqGroupList = iDataQualGroupDao.findAllVersion(appUuid, uuid);
		}
		else
		{
			dqGroupList = iDataQualGroupDao.findAllVersion(uuid);
		}
		return resolveName(dqGroupList);
	}*/
	
	public DataQualGroupExec create(String dataQualGroupUUID, 
										String dataQualGroupVersion, 
										ExecParams execParams,
										List<String> datapodList, 
										DataQualGroupExec dataQualGroupExec, 
										DagExec dagExec) throws Exception {

		return (DataQualGroupExec) super.create(dataQualGroupUUID, dataQualGroupVersion, MetaType.dqgroup, MetaType.dqgroupExec, MetaType.dq, MetaType.dqExec, execParams, datapodList, dataQualGroupExec, dagExec); 
	}
	
	public DataQualGroupExec parse(MetaIdentifier dqGroupExec, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {
		DataQualGroupExec dataQualGroupExec=null;
		try {
			dataQualGroupExec=(DataQualGroupExec) commonServiceImpl.getOneByUuidAndVersion(dqGroupExec.getUuid(), dqGroupExec.getVersion(), MetaType.dqgroupExec.toString());
			return (DataQualGroupExec) super.parse(dqGroupExec.getUuid(), dqGroupExec.getVersion(), MetaType.dqgroup, MetaType.dqgroupExec, MetaType.dq, MetaType.dqExec, refKeyMap, otherParams, datapodList, dagExec, runMode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
				commonServiceImpl.setMetaStatus(dataQualGroupExec, MetaType.dqgroupExec, Status.Stage.FAILED);
				e.printStackTrace();
		}
		return null;
	}
	
	public MetaIdentifier execute(String dataQualGroupUUID, String dataQualGroupVersion, ExecParams execParams, DataQualGroupExec dataQualGroupExec, RunMode runMode) throws Exception {
				return super.execute(dataQualGroupUUID, dataQualGroupVersion, MetaType.dqgroup, MetaType.dqgroupExec, MetaType.dq, MetaType.dqExec, execParams, dataQualGroupExec, runMode);
	}	

	/********************** UNUSED **********************/
	/*public DataQualGroup getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iDataQualGroupDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iDataQualGroupDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(DataQualGroup dqgroup) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		DataQualGroup dqGroupNew = new DataQualGroup();
		dqGroupNew.setName(dqgroup.getName()+"_copy");
		dqGroupNew.setActive(dqgroup.getActive());		
		dqGroupNew.setDesc(dqgroup.getDesc());		
		dqGroupNew.setTags(dqgroup.getTags());	
		dqGroupNew.setRuleInfo(dqgroup.getRuleInfo());
		dqGroupNew.setInParallel(dqgroup.getInParallel());
		save(dqGroupNew);
		ref.setType(MetaType.dqgroup);
		ref.setUuid(dqGroupNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> dgGroupList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity dq : dgGroupList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = dq.getId();
			String uuid = dq.getUuid();
			String version = dq.getVersion();
			String name = dq.getName();
			String desc = dq.getDesc();
			String published=dq.getPublished();
			MetaIdentifierHolder createdBy = dq.getCreatedBy();
			String createdOn = dq.getCreatedOn();
			String[] tags = dq.getTags();
			String active = dq.getActive();
			List<MetaIdentifierHolder> appInfo = dq.getAppInfo();
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
	
	public void restart(String type,String uuid,String version, RunMode runMode) throws Exception{
		//DataQualGroupExec dataQualGroupExec= dataQualGroupExecServiceImpl.findOneByUuidAndVersion(uuid,version);
		DataQualGroupExec dataQualGroupExec = (DataQualGroupExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dqgroupExec.toString());
		//dataQualGroupExec = create(dataQualGroupExec.getDependsOn().getRef().getUuid(),dataQualGroupExec.getDependsOn().getRef().getVersion(), null, null, dataQualGroupExec, null);
		dataQualGroupExec = parse(dataQualGroupExec.getRef(MetaType.dqgroupExec), null, null, null, null, runMode);
		execute(dataQualGroupExec.getDependsOn().getRef().getUuid(),dataQualGroupExec.getDependsOn().getRef().getVersion(),null,dataQualGroupExec, runMode);
	}
	
	/**
	 * 
	 * @param baseExec
	 * @return
	 * @throws Exception
	 */
	public Status restart(BaseExec baseExec) throws Exception {
		try {
			return super.restart(baseExec.getUuid(), baseExec.getVersion(), MetaType.dqgroupExec, MetaType.dqExec);
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
		execute(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), execParams, (DataQualGroupExec) baseExec, runMode);
		return null;
	}

	/**
	 * Override Parsable.parse()
	 */
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(), MetaType.dqgroup, MetaType.dqgroupExec, MetaType.dq, MetaType.dqExec, 
				DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), null, null, null, runMode);
	}
	
	
	
}

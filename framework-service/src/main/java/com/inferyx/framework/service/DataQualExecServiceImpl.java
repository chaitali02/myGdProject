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
import com.inferyx.framework.dao.IDataQualExecDao;
import com.inferyx.framework.dao.IDataQualGroupExecDao;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.User;

@Service
public class DataQualExecServiceImpl extends BaseRuleExecTemplate {
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private IDataQualExecDao iDataQualExecDao;	
	@Autowired
	UserServiceImpl userServiceImpl;	
	@Autowired
	SecurityServiceImpl securityServiceImpl;	
	@Autowired
	protected IDataQualGroupExecDao iDataQualGroupExecDao;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;	
	@Autowired
	DataQualServiceImpl dataQualServiceImpl;	
	@Autowired
	DatapodServiceImpl datapodServiceImpl;	
	
	static final Logger logger = Logger.getLogger(DataQualExecServiceImpl.class);

	/********************** UNUSED **********************/
	/*public DataQualExec findLatest() {
		DataQualExec dataqualexec=null;
		if(iDataQualExecDao.findLatest(new Sort(Sort.Direction.DESC, "version"))!=null){
			dataqualexec=resolveName(iDataQualExecDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
		}
		return dataqualexec ;
	}*/	

	/********************** UNUSED **********************/
	/*public List<DataQualExec> findOneByDataqual(String dataqualUUID) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDataQualExecDao.findOneByDataQual(appUuid,dataqualUUID);
	}*/

	/********************** UNUSED **********************/
	/*public DataQualExec findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iDataQualExecDao.findOneById(appUuid,id);
		}
		return iDataQualExecDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public DataQualExec save(DataQualExec DataQualExec) {
		if(DataQualExec.getAppInfo() == null)	{
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		DataQualExec.setAppInfo(metaIdentifierHolderList);
		}
		DataQualExec.setBaseEntity();
		return iDataQualExecDao.save(DataQualExec);		
	}*/

	/********************** UNUSED **********************/
	/*public DataQualExec findLatestByUuid(String DataQualExecUUID) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iDataQualExecDao.findLatestByUuid(DataQualExecUUID,new Sort(Sort.Direction.DESC, "version"));
		}
		return iDataQualExecDao.findLatestByUuid(appUuid,DataQualExecUUID,new Sort(Sort.Direction.DESC, "version"));		
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		DataQualExec DataQualExec = iDataQualExecDao.findOneById(appUuid,id);
		DataQualExec.setActive("N");
		iDataQualExecDao.save(DataQualExec);
//		String ID=DataQualExec.getId();
//		iDataQualExecDao.delete(ID);		
	}*/

	/********************** UNUSED **********************/
	/*public List<DataQualExec> findAllLatest()	{		
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Aggregation DataQualExecAggr = newAggregation(group("uuid").max("version").as("version"));
		AggregationResults<DataQualExec> DataQualExecResults = mongoTemplate.aggregate(DataQualExecAggr, "dqexec", DataQualExec.class);
		List<DataQualExec> DataQualExecList = DataQualExecResults.getMappedResults();
		// Fetch the VizExec details for each id
		List<DataQualExec> result = new ArrayList<DataQualExec>();
		for (DataQualExec v : DataQualExecList) {
			DataQualExec DataQualExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
			//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();;
				DataQualExecLatest = iDataQualExecDao.findOneByUuidAndVersion(appUuid,v.getId(), v.getVersion());
			}
			else
			{
				DataQualExecLatest = iDataQualExecDao.findOneByUuidAndVersion(v.getId(), v.getVersion());
			}
			//logger.debug("datapodLatest is " + datapodLatest.getName());
			if(DataQualExecLatest != null)
			{
			result.add(DataQualExecLatest);
			}
		}	
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<DataQualExec> findAllLatestActive() 	
	{	   
	   Aggregation DataQualExecAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<DataQualExec> DataQualExecResults = mongoTemplate.aggregate(DataQualExecAggr,"dqexec", DataQualExec.class);	   
	   List<DataQualExec> DataQualExecList = DataQualExecResults.getMappedResults();

	   // Fetch the DataQualExec details for each id
	   List<DataQualExec> result=new  ArrayList<DataQualExec>();
	   for(DataQualExec r : DataQualExecList)
	   {   
		   DataQualExec DataQualExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				DataQualExecLatest = iDataQualExecDao.findOneByUuidAndVersion(appUuid,r.getId(), r.getVersion());
			}
			else
			{
				DataQualExecLatest = iDataQualExecDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if(DataQualExecLatest != null)
			{
			result.add(DataQualExecLatest);
			}
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public DataQualExec findOneByUuidAndVersion(String uuid, String version){
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		//String appUuid = "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd";
		return iDataQualExecDao.findOneByUuidAndVersion(uuid,version);
	}*/

	public DataQualExec resolveName(DataQualExec DataQualExec) throws JsonProcessingException {
		String createdByRefUuid = DataQualExec.getCreatedBy().getRef().getUuid();
		//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
		DataQualExec.getCreatedBy().getRef().setName(user.getName());
		
		String dependsOnUuid=DataQualExec.getDependsOn().getRef().getUuid();
		//DataQual dataQual=dataQualServiceImpl.findLatestByUuid(dependsOnUuid);
		DataQual dataQual = (DataQual) commonServiceImpl.getLatestByUuid(dependsOnUuid, MetaType.dq.toString());
		DataQualExec.getDependsOn().getRef().setName(dataQual.getName());

		if(DataQualExec.getRefKeyList() !=null){
			for(int i=0;i<DataQualExec.getRefKeyList().size();i++){
				String datapodUuid=DataQualExec.getRefKeyList().get(i).getUuid();
				//Datapod datapod = datapodServiceImpl.findLatestByUuid(datapodUuid);
				Datapod datapod = (Datapod) commonServiceImpl.getLatestByUuid(datapodUuid, MetaType.datapod.toString());
				DataQualExec.getRefKeyList().get(i).setName(datapod.getName());
			}
		}
		return DataQualExec;
	}

	public List<DataQualExec> findDataQualExecByDataqual(String dataqualUuid) throws JsonProcessingException {
		List<DataQualExec> DataQualExecList=null;
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if (appUuid != null) {
			DataQualExecList = iDataQualExecDao.findOneByDataQual(appUuid, dataqualUuid);
		}else{
			DataQualExecList = iDataQualExecDao.findOneByDataQual(dataqualUuid);
		}
		List<DataQualExec> resolvedDataQualExecList = new ArrayList<>();
		for(DataQualExec DataQualExec : DataQualExecList) {
			resolveName(DataQualExec);
			resolvedDataQualExecList.add(DataQualExec);
		}		
		return resolvedDataQualExecList;
	}
	
	public List<DataQualExec> findDataQualExecByDataqual(String dataqualUuid, String startDate, String endDate, String type, String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException{		
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
			
			query.addCriteria(Criteria.where("statusList.stage").in(Status.Stage.COMPLETED.toString()));
			query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(dataqualUuid));
		    query.addCriteria(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
			query.addCriteria(Criteria.where("active").is("Y"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		List<DataQualExec> DataQualExecObjList = new ArrayList<>();
		DataQualExecObjList = (List<DataQualExec>) mongoTemplate.find(query, DataQualExec.class);
		
		return DataQualExecObjList;		
	}
	
	public List<DataQualExec> getdqExecBydqGroupExec(String dqGroupExecUuid, String dqGroupExecVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		*/
		/*if (appUuid == null) {
			dataqualGroupExec = iDataQualGroupExecDao.findOneByUuidAndVersion(dqGroupExecUuid, dqGroupExecVersion);
		} else {
			dataqualGroupExec = iDataQualGroupExecDao.findOneByUuidAndVersion(appUuid, dqGroupExecUuid, dqGroupExecVersion);
		}*/
		DataQualGroupExec dataqualGroupExec = (DataQualGroupExec) commonServiceImpl.getOneByUuidAndVersion(dqGroupExecUuid, dqGroupExecVersion, MetaType.dqgroupExec.toString());
		List<MetaIdentifierHolder> dataQualExecHolderList=dataqualGroupExec.getExecList();
		DataQualExec dqexec=null;
		List<DataQualExec> dataQualExecList=new ArrayList<DataQualExec>();
		for(MetaIdentifierHolder dataQualExecHolder:dataQualExecHolderList)	{
			//dqexec=iDataQualExecDao.findOneByUuidAndVersion(dataQualExecHolder.getRef().getUuid(), dataQualExecHolder.getRef().getVersion());
			dqexec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(dataQualExecHolder.getRef().getUuid(), dataQualExecHolder.getRef().getVersion(), MetaType.dqExec.toString());
			resolveName(dqexec);
			dataQualExecList.add(dqexec);
		}	
		return dataQualExecList;
	}

	/********************** UNUSED **********************/
	/*public DataQualExec getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iDataQualExecDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iDataQualExecDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(DataQualExec dqExec) {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		DataQualExec dqExecNew = new DataQualExec();
		dqExecNew.setName(dqExec.getName()+"_copy");
		dqExecNew.setActive(dqExec.getActive());		
		dqExecNew.setDesc(dqExec.getDesc());		
		dqExecNew.setTags(dqExec.getTags());
		dqExecNew.setStatusList(dqExec.getStatusList());
		dqExecNew.setDependsOn(dqExec.getDependsOn());
		dqExecNew.setExec(dqExec.getExec());
		dqExecNew.setExecParams(dqExec.getExecParams());
		dqExecNew.setResult(dqExec.getResult());
		save(dqExecNew);
		ref.setType(MetaType.dqExec);
		ref.setUuid(dqExecNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> dqExecList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity dqExec : dqExecList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = dqExec.getId();
			String uuid = dqExec.getUuid();
			String version = dqExec.getVersion();
			String name = dqExec.getName();
			String desc = dqExec.getDesc();
			String published=dqExec.getPublished();
			MetaIdentifierHolder createdBy = dqExec.getCreatedBy();
			String createdOn = dqExec.getCreatedOn();
			String[] tags = dqExec.getTags();
			String active = dqExec.getActive();
			List<MetaIdentifierHolder> appInfo = dqExec.getAppInfo();
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

	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//DataQualExec dataQualExec = iDataQualExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.dqExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.dq);
		mi.setUuid(dataQualExec.getDependsOn().getRef().getUuid());
		mi.setVersion(dataQualExec.getDependsOn().getRef().getVersion());
		return mi;
	}
	
	public ExecStatsHolder getNumRowsbyExec(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//DataQualExec dataQualExec = iDataQualExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.dqExec.toString());
		//com.inferyx.framework.domain.DataStore dataStore=dataStoreServiceImpl.findOneByUuidAndVersion(dataQualExec.getResult().getRef().getUuid(), dataQualExec.getResult().getRef().getVersion());		
		com.inferyx.framework.domain.DataStore dataStore =(DataStore) commonServiceImpl.getOneByUuidAndVersion(dataQualExec.getResult().getRef().getUuid(), dataQualExec.getResult().getRef().getVersion(), MetaType.datastore.toString());
		MetaIdentifier mi = new MetaIdentifier();
		ExecStatsHolder execHolder=new ExecStatsHolder();
		mi.setType(MetaType.datastore);
		mi.setUuid(dataQualExec.getResult().getRef().getUuid());
		mi.setVersion(dataQualExec.getResult().getRef().getVersion());
		execHolder.setRef(mi);
		execHolder.setNumRows(dataStore.getNumRows());
		execHolder.setPersistMode(dataStore.getPersistMode());
		execHolder.setRunMode(dataStore.getRunMode());
		return execHolder;
	}
	
	
	@SuppressWarnings({ "unchecked", "null" })
	public List<DataQualExec> finddqExecByDatapod(String datapodUUID, String type)
			throws JsonProcessingException, ParseException {

		List<String> dqUUIDlist = new ArrayList<String>();
		List<DataQualExec> result = new ArrayList<DataQualExec>();

		List<DataQual> dqobj = (List<DataQual>) commonServiceImpl.getAllLatestCompleteObjects(MetaType.dq.toString(),
				null);
		for (DataQual dq : dqobj) {
			if (datapodUUID.equalsIgnoreCase(dq.getDependsOn().getRef().getUuid())) {
				dqUUIDlist.add(dq.getUuid().toString());
			}
		}

		List<DataQualExec> dqexecobj = (List<DataQualExec>) commonServiceImpl
				.getAllLatestCompleteObjects(MetaType.dqExec.toString(), null);

		for (DataQualExec dqexec : dqexecobj) {

			for (String dquuid : dqUUIDlist) {
				if (dquuid.equalsIgnoreCase(dqexec.getDependsOn().getRef().getUuid())) {
					result.add(dqexec);
				}
			}
		}

		return result;
	}
	
	public List<DataQualExec> finddqExecByDatapod(String datapodUUID, String startDate, String endDate, String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException{
		List<DataQualExec> DataQualExecObjList = new ArrayList<>();
		List<DataQualExec> ExecObjList = new ArrayList<>();

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

		    	query.addCriteria(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
		    	query.addCriteria(Criteria.where("active").is("Y"));
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<DataQual> dataQualList = new ArrayList<>();
		dataQualList = (List<DataQual>) mongoTemplate.find(query, DataQual.class);
		
		for (DataQual dq : dataQualList) {
			DataQualExecObjList = findDataQualExecByDataqual(dq.getUuid(), startDate, endDate, null, null);
			if(!DataQualExecObjList.isEmpty()) {
				ExecObjList.addAll(DataQualExecObjList);
			}
			
			}		
		return ExecObjList;	
	}
	
	/**
	 * Put DQ on Hold
	 * @param uuid
	 * @param version
	 */
	public void PAUSE (String uuid, String version) {
		PAUSE(uuid, version, MetaType.dqExec);
	}
	
 	/**
	 * Kill meta thread if RUNNING
	 * @param uuid
	 * @param version
	 */
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.dqExec);
	}
	
	/**
	 * RESUME DQ Execution
	 * @param uuid
	 * @param version
	 */
	public void RESUME (String uuid, String version) {
		super.RESUME(uuid, version, MetaType.dqExec);
	}

	
}

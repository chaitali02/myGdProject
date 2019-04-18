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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IDataQualExecDao;
import com.inferyx.framework.dao.IDataQualGroupExecDao;
import com.inferyx.framework.domain.DQRecExec;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.MapExec;
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
	@Resource(name="taskThreadMap")
	ConcurrentHashMap taskThreadMap;
	
	static final Logger logger = Logger.getLogger(DataQualExecServiceImpl.class);

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
	/**********************Unused***********************/
	public void PAUSE (String uuid, String version) {
		PAUSE(uuid, version, MetaType.dqExec);
	}
	
 	/**
	 * Kill meta thread if RUNNING
	 * @param uuid
	 * @param version
	 */
	/**********************Unused***********************/
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.dqExec);
	}

	
	/**
	 * RESUME DQ Execution
	 * @param uuid
	 * @param version
	 */
	/**********************Unused***********************/
	public void RESUME (String uuid, String version) {
		super.RESUME(uuid, version, MetaType.dqExec);
	}

	  /**
		 * Kill meta thread if RUNNING
		 * @param uuid
		 * @param version
		 */
		public void kill (String uuid, String version, MetaType metaType)  {
			DQRecExec dqRecExec = null;
			try {
				dqRecExec = (DQRecExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dqrecExec.toString(), "N");
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (dqRecExec == null) {
				logger.info("Nothing to kill. Aborting ... ");
				return;
			}
			
			try {
				dqRecExec = (DQRecExec) commonServiceImpl.setMetaStatus(dqRecExec, MetaType.dqrecExec, Status.Stage.TERMINATING);
				if (!Helper.getLatestStatus(dqRecExec.getStatusList()).equals(new Status(Status.Stage.TERMINATING, new Date()))) {
					logger.info(" Status is not TERMINATING. So aborting ... ");
					return;
				}
				FutureTask futureTask = (FutureTask) taskThreadMap.get(MetaType.dqrecExec+"_"+dqRecExec.getUuid()+"_"+dqRecExec.getVersion());
				if (futureTask != null && !futureTask.isDone()) {
					futureTask.cancel(true);
				}
				taskThreadMap.remove(MetaType.dqrecExec+"_"+dqRecExec.getUuid()+"_"+dqRecExec.getVersion());
				commonServiceImpl.setMetaStatus(dqRecExec, MetaType.dqrecExec, Status.Stage.KILLED);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}

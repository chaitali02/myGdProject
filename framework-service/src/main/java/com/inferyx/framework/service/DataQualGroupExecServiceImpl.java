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
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	protected IDataQualGroupExecDao iDataQualGroupExecDao;
	
	static final Logger logger = Logger.getLogger(DataQualGroupExecServiceImpl.class);	

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

	/**************************Unused***********************/
	/*public List<DataQualExec> resolveName(List<DataQualExec> DataQualExec) throws JsonProcessingException {
		List<DataQualExec> DataQualExecList = new ArrayList<>();
		for(DataQualExec DataqualE : DataQualExec)	{
			String createdByRefUuid = DataqualE.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			DataqualE.getCreatedBy().getRef().setName(user.getName());
			DataQualExecList.add(DataqualE);
		}
		return DataQualExecList;
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
	/*************************Unused****************************/
	/*public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.dqgroupExec, MetaType.dqExec);
	}*/	
}

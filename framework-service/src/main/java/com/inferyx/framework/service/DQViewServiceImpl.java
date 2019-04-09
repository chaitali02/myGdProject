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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IDataQualDao;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.view.metadata.DQView;

@Service
public class DQViewServiceImpl {
	
	@Autowired
    CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(DQViewServiceImpl.class);
	
	public DQView findOneById(String id) throws JsonProcessingException {
		DQView dqView = new DQView();
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		DataQual dq;
		if (appUuid != null) {
			dq = iDataQualDao.findOneById(appUuid, id);
		} else {
			dq = iDataQualDao.findOne(id);
		}*/
		DataQual dq = (DataQual) commonServiceImpl.getOneById(id, MetaType.dq.toString());
//		List<AttributeRefHolder> filterList = dq.getFilterInfo();
//		for(int i=0; i<filterList.size(); i++)		{
//			//Filter filter = filterServiceImpl.getAsOf(filterList.get(i).getRef().getUuid(),dq.getVersion());
//			Filter filter = (Filter) commonServiceImpl.getAsOf(filterList.get(i).getRef().getUuid(),dq.getVersion(), MetaType.filter.toString());
//			dqView.setFilter(filter);
//		}
		dqView.setAppInfo(dq.getAppInfo());
		dqView.setName(dq.getName());
		dqView.setDesc(dq.getDesc());
		dqView.setTags(dq.getTags());
		dqView.setUuid(dq.getUuid());
		dqView.setVersion(dq.getVersion());
		dqView.setCreatedOn(dq.getCreatedOn());
		dqView.setCreatedBy(dq.getCreatedBy());
		dqView.setPublished(dq.getPublished());
		dqView.setAttribute(dq.getAttribute());
		dqView.setCustomFormatCheck(dq.getCustomFormatCheck());
		dqView.setDataTypeCheck(dq.getDataTypeCheck());
		dqView.setCustomFormatCheck(dq.getCustomFormatCheck());
		dqView.setDateFormatCheck(dq.getDateFormatCheck());
		dqView.setDependsOn(dq.getDependsOn());
		dqView.setLengthCheck(dq.getLengthCheck());
		dqView.setNullCheck(dq.getNullCheck());
		dqView.setRangeCheck(dq.getRangeCheck());
		dqView.setRefIntegrityCheck(dq.getRefIntegrityCheck());
		//dqView.setStdDevCheck(dq.getStdDevCheck());
		dqView.setValueCheck(dq.getValueCheck());		
		dqView.setTarget(dq.getTarget());		
		return dqView;
	}

	public DQView findOneByUuidAndVersion(String uuid, String version) throws JsonProcessingException {
		DQView dqView = new DQView();
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
		/*if (appUuid != null) {
			dq = iDataQualDao.findOneByUuidAndVersion(appUuid, uuid,version);
		} else {
			dq = iDataQualDao.findOneByUuidAndVersion(uuid, version);
		}*/
		DataQual dq = (DataQual) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dq.toString());
//		List<AttributeRefHolder> filterList = dq.getFilterInfo();
//		if(filterList != null)		{
//			for(int i=0; i<filterList.size(); i++)		{
//				//Filter filter = filterServiceImpl.getAsOf(filterList.get(i).getRef().getUuid(),dq.getVersion());
//				Filter filter = (Filter) commonServiceImpl.getAsOf(filterList.get(i).getRef().getUuid(),dq.getVersion(), MetaType.filter.toString());
//				dqView.setFilter(filter);
//			}
//		}
		dqView.setAppInfo(dq.getAppInfo());
		dqView.setName(dq.getName());
		dqView.setDesc(dq.getDesc());
		dqView.setTags(dq.getTags());
		dqView.setUuid(dq.getUuid());
		dqView.setVersion(dq.getVersion());
		dqView.setCreatedOn(dq.getCreatedOn());
		dqView.setPublished(dq.getPublished());
		dqView.setCreatedBy(dq.getCreatedBy());
		dqView.setAttribute(dq.getAttribute());
		dqView.setCustomFormatCheck(dq.getCustomFormatCheck());
		dqView.setDataTypeCheck(dq.getDataTypeCheck());
		dqView.setCustomFormatCheck(dq.getCustomFormatCheck());
		dqView.setDateFormatCheck(dq.getDateFormatCheck());
		dqView.setDependsOn(dq.getDependsOn());
		dqView.setLengthCheck(dq.getLengthCheck());
		dqView.setDuplicateKeyCheck(dq.getDuplicateKeyCheck());
		dqView.setNullCheck(dq.getNullCheck());
		dqView.setRangeCheck(dq.getRangeCheck());
		dqView.setRefIntegrityCheck(dq.getRefIntegrityCheck());
		//dqView.setStdDevCheck(dq.getStdDevCheck());
		dqView.setValueCheck(dq.getValueCheck());		
		dqView.setTarget(dq.getTarget());	
		return dqView;
	}

	public DQView findLatestByUuid(String uuid) throws JsonProcessingException {
		DQView dqView = new DQView();
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/		
		/*if (appUuid != null) {
			dq = iDataQualDao.findLatestByUuid(appUuid,uuid,new Sort(Sort.Direction.DESC, "version"));
		} else {
			dq = iDataQualDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}*/
		DataQual dq = (DataQual) commonServiceImpl.getLatestByUuid(uuid, MetaType.dq.toString());
//		List<AttributeRefHolder> filterList = dq.getFilterInfo();
//		if(filterList != null)		{
//		for(int i=0; i<filterList.size(); i++)		{
//				//Filter filter = filterServiceImpl.getAsOf(filterList.get(i).getRef().getUuid(),dq.getVersion());
//				Filter filter = (Filter) commonServiceImpl.getAsOf(filterList.get(i).getRef().getUuid(),dq.getVersion(), MetaType.filter.toString());
//				dqView.setFilter(filter);
//			}
//		}
		dqView.setAppInfo(dq.getAppInfo());
		dqView.setName(dq.getName());
		dqView.setDesc(dq.getDesc());
		dqView.setTags(dq.getTags());
		dqView.setUuid(dq.getUuid());
		dqView.setVersion(dq.getVersion());
		dqView.setCreatedOn(dq.getCreatedOn());
		dqView.setCreatedBy(dq.getCreatedBy());
		dqView.setPublished(dq.getPublished());
		dqView.setAttribute(dq.getAttribute());
		dqView.setCustomFormatCheck(dq.getCustomFormatCheck());
		dqView.setDataTypeCheck(dq.getDataTypeCheck());
		dqView.setCustomFormatCheck(dq.getCustomFormatCheck());
		dqView.setDateFormatCheck(dq.getDateFormatCheck());
		dqView.setDependsOn(dq.getDependsOn());
		dqView.setLengthCheck(dq.getLengthCheck());
		dqView.setDuplicateKeyCheck(dq.getDuplicateKeyCheck());
		dqView.setNullCheck(dq.getNullCheck());
		dqView.setRangeCheck(dq.getRangeCheck());
		dqView.setRefIntegrityCheck(dq.getRefIntegrityCheck());
		//dqView.setStdDevCheck(dq.getStdDevCheck());
		dqView.setValueCheck(dq.getValueCheck());		
		dqView.setTarget(dq.getTarget());		
		return dqView;
	}

	public DQView resolveName(DQView dqView) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if(dqView.getCreatedBy() != null){
			String createdByRefUuid = dqView.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuidWithoutAppUuid(createdByRefUuid, MetaType.user.toString());
			dqView.getCreatedBy().getRef().setName(user.getName());
		}
		return dqView;
	}

}

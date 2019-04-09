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


import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Filter;

import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.User;

@Service
public class FilterServiceImpl {	
	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(FilterServiceImpl.class);

	
	public Filter resolveName(Filter filter) throws JsonProcessingException{		
		if (filter == null) {
			return null;
		}
		
		if(filter.getCreatedBy() != null) {
		String createdByRefUuid = filter.getCreatedBy().getRef().getUuid();
		//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
		filter.getCreatedBy().getRef().setName(user.getName());
		}
		if (filter.getAppInfo() != null) {
			for (int i = 0; i < filter.getAppInfo().size(); i++) {
				if(filter.getAppInfo().get(i)!=null){
				String appUuid = filter.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				filter.getAppInfo().get(i).getRef().setName(appName);
				}
			}
		}
		if(filter.getDependsOn().getRef().getType().equals(MetaType.relation.toString())) {
		String dependsOnRefUuid = filter.getDependsOn().getRef().getUuid();
		//Relation relationDO = relationServiceImpl.findLatestByUuid(dependsOnRefUuid);
		Relation relationDO = (Relation) commonServiceImpl.getLatestByUuid(dependsOnRefUuid, MetaType.relation.toString());
		String relationName = relationDO.getName();
		filter.getDependsOn().getRef().setName(relationName);
		} else if(filter.getDependsOn().getRef().getType().equals(MetaType.datapod.toString())) {
			String dependsOnRefUuid = filter.getDependsOn().getRef().getUuid();
			//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(dependsOnRefUuid);	
			Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(dependsOnRefUuid, MetaType.datapod.toString());
			String datapodName = datapodDO.getName();
			filter.getDependsOn().getRef().setName(datapodName);
		} else if(filter.getDependsOn().getRef().getType().equals(MetaType.dataset.toString())) {
			String dependsOnRefUuid = filter.getDependsOn().getRef().getUuid();
			//Dataset datasetDO = datasetServiceImpl.findLatestByUuid(dependsOnRefUuid);
			DataSet datasetDO = (DataSet) commonServiceImpl.getLatestByUuid(dependsOnRefUuid, MetaType.dataset.toString());
			String datasetName = datasetDO.getName();
			filter.getDependsOn().getRef().setName(datasetName);
		}
		
		for(int i=0;i<filter.getFilterInfo().size();i++){	
			for(int j=0;j<filter.getFilterInfo().get(i).getOperand().size();j++){
				MetaType operandRefType = filter.getFilterInfo().get(i).getOperand().get(j).getRef().getType();
	            String operandRefUuid = filter.getFilterInfo().get(i).getOperand().get(j).getRef().getUuid();
	            
				if(operandRefType.toString().equals(MetaType.datapod.toString())) {
					Integer operandAttributeId = filter.getFilterInfo().get(i).getOperand().get(j).getAttributeId();
					//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(operandRefUuid);
					Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(operandRefUuid, MetaType.datapod.toString());
					String datapodName = datapodDO.getName();
					filter.getFilterInfo().get(i).getOperand().get(j).getRef().setName(datapodName);
					List<Attribute> attributeList = datapodDO.getAttributes();
					filter.getFilterInfo().get(i).getOperand().get(j).setAttributeName(attributeList.get(operandAttributeId).getName());
				}
			}	
		}		
		return filter;
	}
	
}

	
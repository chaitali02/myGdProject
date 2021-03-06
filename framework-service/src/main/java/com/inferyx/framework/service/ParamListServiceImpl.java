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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;

@Service
public class ParamListServiceImpl {

    @Autowired
    CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(ParamListServiceImpl.class);


	/******************************Unused**********************/
	/*public String sql(Integer paramId, ParamList paramList) {
		List<Param> list = paramList.getParams();
		for(Param param : list) {
			if(param.getParamId().equalsIgnoreCase(paramId.toString())) {
				//return String.format("%s.%s", paramList.getName(), param.getParamName());
				return param.getParamName();
			}
		}
		return null;
	}*/
	
	/**
	 * 
	 * @param execParams
	 * @param attributeId
	 * @param ref
	 * @return value
	 * @throws JsonProcessingException
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public String getParamValue(ExecParams execParams,  
									Integer attributeId, 
									MetaIdentifier ref, 
									Map<String, String> paramValMap) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {	
		logger.info("Inside ParamListServiceImpl.getParamValue(); "); 
		ParamListHolder paramListHolder = null;
		String paramName = null;
		String refParamValue = null;
//		ParamList paramListRef = (ParamList)daoRegister.getRefObject(ref);
		ParamList paramListRef = (ParamList) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
		Application application = commonServiceImpl.getApp(); 
//		ParamList appParamList = (ParamList)daoRegister.getRefObject(application.getParamList().getRef()); 
		ParamList appParamList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(application.getParamList().getRef().getUuid(), application.getParamList().getRef().getVersion(), application.getParamList().getRef().getType().toString(), "N");
		
		if (execParams != null) {
			paramListHolder = execParams.getParamListHolder();
			/*if (!paramListHolder.getRef().getUuid().equals(ref.getUuid())) {
				paramListHolder = null;
			}*/
		}
	
		
		
		// code added by vaibhav
		if (execParams!=null && execParams.getParamListInfo() != null && execParams.getParamListInfo().size() >= 0)
			for (ParamListHolder holder : execParams.getParamListInfo()) {
				if (ref.getUuid().equalsIgnoreCase(holder.getRef().getUuid()))
					if (holder.getParamId().equalsIgnoreCase(attributeId.toString())) {
						return holder.getParamValue().getValue();
					}
			}

		// Get param from ref
		for (com.inferyx.framework.domain.Param param : paramListRef.getParams()) {
			if (param.getParamId().equalsIgnoreCase(attributeId.toString())) {
				 if (paramListHolder == null && appParamList == null) {
					return param.getParamValue().getValue();	// Nothing in execParams. Send from ref
				} else {	// ExecParams has data. Wait and watch
					paramName = param.getParamName();
					refParamValue = param.getParamValue().getValue();
				}
			}
		}
		
			
		
		
		
		
		logger.info("Param name : " + paramName);
		logger.info("Param value : " + refParamValue);
		
		for(Param param : appParamList.getParams()) {
			if((StringUtils.isBlank(paramName) && param.getParamId().equalsIgnoreCase(attributeId.toString())) 
					|| param.getParamName().equals(paramName)) {
				logger.info("Param name from app paramlist : " + param.getParamName());
				paramValMap.put(param.getParamName(), param.getParamValue().getValue());
				return param.getParamValue().getValue();
			}
		}
		
//		ParamList paramList = (ParamList) daoRegister.getRefObject(paramListHolder.getRef());
		ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(paramListHolder.getRef().getUuid(), paramListHolder.getRef().getVersion(), paramListHolder.getRef().getType().toString(), "N");
		
		for(Param param : paramList.getParams()) {
			if((StringUtils.isBlank(paramName) && param.getParamId().equalsIgnoreCase(attributeId.toString())) 
					|| param.getParamName().equals(paramName)) {
				logger.info("Param name from execParams : " + param.getParamName());
				paramValMap.put(param.getParamName(), param.getParamValue().getValue());
				return param.getParamValue().getValue();
			} 
		}
		if (StringUtils.isNotBlank(paramName)) {
			return refParamValue;
		}
		return "''";
	}// End method	
}

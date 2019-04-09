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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.inferyx.framework.domain.Function;

import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.User;

@Service
public class FunctionServiceImpl {

	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
static final Logger logger = Logger.getLogger(FunctionServiceImpl.class);
	
	public List<Function> resolveName(List<Function> function) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		List<Function> functionList = new ArrayList<Function>();
		for (Function func : function) {
			String createdByRefUuid = func.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuidWithoutAppUuid(createdByRefUuid, MetaType.user.toString());
			func.getCreatedBy().getRef().setName(user.getName());
			functionList.add(func);
		}
		return functionList;
	}

}

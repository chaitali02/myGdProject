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
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IUploadDao;
import com.inferyx.framework.domain.UploadExec;
import com.inferyx.framework.register.GraphRegister;

public class UploadExecServiceImpl {
	
	@Autowired
	IUploadDao dao;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	GraphRegister<?> registerGraph;
	

	static final Logger logger = Logger.getLogger(LogServiceImpl.class);
	
	public List<UploadExec> findAllByDependOn(String uuid) throws JsonProcessingException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException{
		
		return dao.findAllByDependOn(uuid);
		
	}
	
	
	
}

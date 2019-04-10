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
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.ILogDao;
import com.inferyx.framework.domain.Log;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.register.GraphRegister;

@Service
public class LogServiceImpl {
	
	@Autowired
	ILogDao iLogDao;
	@Autowired
	GraphRegister<?> registerGraph;
	

	static final Logger logger = Logger.getLogger(LogServiceImpl.class);
	
	public Log save(Log log) throws JsonProcessingException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException{
		log.setBaseEntity();
		Log savedLog=(Log) iLogDao.save(log);
		registerGraph.updateGraph((Object)savedLog, MetaType.log);
		
		return savedLog;
		
	}
	
	
	
}

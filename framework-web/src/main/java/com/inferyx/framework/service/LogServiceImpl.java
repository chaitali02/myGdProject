package com.inferyx.framework.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.ILogDao;
import com.inferyx.framework.domain.Log;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.register.GraphRegister;

public class LogServiceImpl {
	
	@Autowired
	ILogDao iLogDao;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
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

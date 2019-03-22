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
package com.inferyx.framework.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.domain.BaseEntityStatus;
import com.inferyx.framework.service.SecurityServiceImpl;
import com.inferyx.framework.service.SystemServiceImpl;

import shaded.parquet.org.codehaus.jackson.JsonGenerationException;
import shaded.parquet.org.codehaus.jackson.map.JsonMappingException;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping(value="/system")
public class SystemController {
	@Autowired
	SystemServiceImpl systemServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	
	static final Logger logger = Logger.getLogger(SystemController.class);
	
	@RequestMapping(value="/getActiveSession", method=RequestMethod.GET)
	public @ResponseBody String getActiveSession(@RequestParam(value="appUuid", required=false) String appUuid,
			@RequestParam(value="userName", required=false) String userName,
			@RequestParam(value="startDate", required=false) String startDate,
			@RequestParam(value="endDate", required=false) String endDate,
			@RequestParam(value="tags", required=false) String tags,
			@RequestParam(value="active", required=false) String active,
			@RequestParam(value = "status", required=false) String status,
			@RequestParam(value = "type", required=false) String type,
			@RequestParam(value = "role", required=false) String role) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		role = systemServiceImpl.getRole();
		if(!StringUtils.isBlank(role) && role.equalsIgnoreCase("admin")) {
			List<BaseEntityStatus> activeSessionList = systemServiceImpl.getActiveSession(type, appUuid, userName, startDate, endDate, tags, active, status, role);
			return mapper.writeValueAsString(activeSessionList);
		}else {
			List<BaseEntityStatus> activeSessionList = systemServiceImpl.getActiveSession(type, appUuid, userName, startDate, endDate, tags, active, status, null);
			return mapper.writeValueAsString(activeSessionList);
		}
	}		
	@RequestMapping(value="/getActiveJobByCriteria", method=RequestMethod.GET)
	public @ResponseBody String getActiveJobByCriteria(@RequestParam(value="appUuid", required=false) String appUuid,
			@RequestParam(value="userName", required=false) String userName,
			@RequestParam(value="startDate", required=false) String startDate,
			@RequestParam(value="endDate", required=false) String endDate,
			@RequestParam(value="tags", required=false) String tags,
			@RequestParam(value="active", required=false) String active,
			@RequestParam(value = "status", required=false) String status,
			@RequestParam(value = "type", required=false) String type,
			@RequestParam(value = "role", required=false) String role) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		role = systemServiceImpl.getRole();
		if(!StringUtils.isBlank(role) && role.equalsIgnoreCase("admin")) {
			List<BaseEntityStatus> activeSessionList = systemServiceImpl.getActiveJobByCriteria(type, appUuid, userName, startDate, endDate, tags, active, status, role);
			return mapper.writeValueAsString(activeSessionList);
		}else {
			List<BaseEntityStatus> activeSessionList = systemServiceImpl.getActiveJobByCriteria(type, appUuid, userName, startDate, endDate, tags, active, status, null);
			return mapper.writeValueAsString(activeSessionList);
		}
	}		
	
	@RequestMapping(value="/getActiveThread", method=RequestMethod.GET)
	public @ResponseBody List<com.inferyx.framework.domain.Thread> getActiveThread() throws JsonProcessingException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		List<com.inferyx.framework.domain.Thread> activeThreadList = systemServiceImpl.getActiveThread();
		return activeThreadList;
	}
	@RequestMapping(value="/getJobCountByStatus", method=RequestMethod.GET)
	public @ResponseBody String getjobCountByStatus(@RequestParam(value="appUuid", required=false) String appUuid,
			@RequestParam(value="userName", required=false) String userName,
			@RequestParam(value="startDate", required=false) String startDate,
			@RequestParam(value="endDate", required=false) String endDate,
			@RequestParam(value="tags", required=false) String tags,
			@RequestParam(value="active", required=false) String active,
			@RequestParam(value = "status", required=false) String status,
			@RequestParam(value = "type", required=false) String type,
			@RequestParam(value = "role", required=false) String role) throws JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonGenerationException, JsonMappingException, IOException {
		//System.out.println(new ObjectMapper().writeValueAsString("result: "+systemServiceImpl.getCountByStatus(type, appUuid, userName, startDate, endDate, tags, active, status, role)));
		return new ObjectMapper().writeValueAsString(systemServiceImpl.getJobCountByStatus(type, appUuid, userName, startDate, endDate, tags, active, status, role));
	}
	@RequestMapping(value="/getJobCountByMeta", method=RequestMethod.GET)
	public @ResponseBody String getJobCountByMeta(@RequestParam(value="appUuid", required=false) String appUuid,
			@RequestParam(value="userName", required=false) String userName,
			@RequestParam(value="startDate", required=false) String startDate,
			@RequestParam(value="endDate", required=false) String endDate,
			@RequestParam(value="tags", required=false) String tags,
			@RequestParam(value="active", required=false) String active,
			@RequestParam(value = "status", required=false) String status,
			@RequestParam(value = "type", required=false) String type,
			@RequestParam(value = "role", required=false) String role) throws JsonProcessingException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return new ObjectMapper().writeValueAsString(systemServiceImpl.getJobCountByMeta(type, appUuid, userName, startDate, endDate, tags, active, status, role));
	}
	@RequestMapping(value="/getJobCountByApp", method=RequestMethod.GET)
	public @ResponseBody String getJobCountByApp(@RequestParam(value="appUuid", required=false) String appUuid,
			@RequestParam(value="userName", required=false) String userName,
			@RequestParam(value="startDate", required=false) String startDate,
			@RequestParam(value="endDate", required=false) String endDate,
			@RequestParam(value="tags", required=false) String tags,
			@RequestParam(value="active", required=false) String active,
			@RequestParam(value = "status", required=false) String status,
			@RequestParam(value = "type", required=false) String type,
			@RequestParam(value = "role", required=false) String role) throws JsonProcessingException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return new ObjectMapper().writeValueAsString(systemServiceImpl.getJobCountByApp(type, appUuid, userName, startDate, endDate, tags, active, status,  role));

	}
	@RequestMapping(value="/getJobCountByUser", method=RequestMethod.GET)
	public @ResponseBody String getJobCountByUser(@RequestParam(value="appUuid", required=false) String appUuid,
			@RequestParam(value="userName", required=false) String userName,
			@RequestParam(value="startDate", required=false) String startDate,
			@RequestParam(value="endDate", required=false) String endDate,
			@RequestParam(value="tags", required=false) String tags,
			@RequestParam(value="active", required=false) String active,
			@RequestParam(value = "status", required=false) String status,
			@RequestParam(value = "type", required=false) String type,
			@RequestParam(value = "role", required=false) String role) throws JsonProcessingException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return new ObjectMapper().writeValueAsString(systemServiceImpl.getJobCountByUser(type, appUuid, userName, startDate, endDate, tags, active, status,  role));

	}
	@RequestMapping(value="/getSessionCountByUser", method=RequestMethod.GET)
	public @ResponseBody String getSessionCountByUser(@RequestParam(value="appUuid", required=false) String appUuid,
			@RequestParam(value="userName", required=false) String userName,
			@RequestParam(value="startDate", required=false) String startDate,
			@RequestParam(value="endDate", required=false) String endDate,
			@RequestParam(value="tags", required=false) String tags,
			@RequestParam(value="active", required=false) String active,
			@RequestParam(value = "status", required=false) String status,
			@RequestParam(value = "type", required=false) String type,
			@RequestParam(value = "role", required=false) String role) throws JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonGenerationException, JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(systemServiceImpl.getSessionCountByUser(type, appUuid, userName, startDate, endDate, tags, active, status,  role));
	}
	@RequestMapping(value="/getSessionCountByStatus", method=RequestMethod.GET)
	public @ResponseBody String getSessionCountByStatus(@RequestParam(value="appUuid", required=false) String appUuid,
			@RequestParam(value="userName", required=false) String userName,
			@RequestParam(value="startDate", required=false) String startDate,
			@RequestParam(value="endDate", required=false) String endDate,
			@RequestParam(value="tags", required=false) String tags,
			@RequestParam(value="active", required=false) String active,
			@RequestParam(value = "status", required=false) String status,
			@RequestParam(value = "type", required=false) String type,
			@RequestParam(value = "role", required=false) String role) throws JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonGenerationException, JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(systemServiceImpl.getSessionCountByStatus(type, appUuid, userName, startDate, endDate, tags, active, status,  role));
	}
	@RequestMapping(value="/getTZ", method=RequestMethod.GET)
	public @ResponseBody String getTZ() throws JsonProcessingException, JSONException, ParseException {
		TimeZone tz = Calendar.getInstance().getTimeZone();
		logger.info(tz.getDisplayName());// (i.e. Moscow Standard Time)
		tz.toZoneId();
		logger.info(tz.getID());
		return tz.getDisplayName();
	
	}

	@RequestMapping(value = "/getThreadStats", method=RequestMethod.GET)
	public Object getThreadStats(@RequestParam(value = "type", required = false) String type,
								 @RequestParam(value = "action", required = false) String action) {		
		return systemServiceImpl.getThreadStats();
	}
}

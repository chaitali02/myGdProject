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
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.formula.functions.T;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.operator.ProfileOperator;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.ProfileExecServiceImpl;
import com.inferyx.framework.service.ProfileGroupExecServiceImpl;
import com.inferyx.framework.service.ProfileGroupServiceImpl;
import com.inferyx.framework.service.ProfileServiceImpl;
import com.inferyx.framework.service.RegisterService;
import com.inferyx.framework.service.TaskHolder;

@RestController
@RequestMapping(value = "/profile")
public class ProfileController {
	
	@Autowired 
	ProfileOperator profileOperator;
	@Autowired
	RegisterService registerService;
	@Autowired
	ProfileExecServiceImpl profileExecServiceImpl;
	@Autowired
	ProfileServiceImpl profileServiceImpl;
	@Autowired
	ProfileGroupServiceImpl profileGroupServiceImpl;
	@Autowired
	ProfileGroupExecServiceImpl profileGroupExecServiceImpl;
	@Autowired 
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public ProfileExec execute(@RequestParam("uuid") String profileUUID, 
			@RequestParam("version") String profileVersion, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		ProfileExec profileExec = profileServiceImpl.create(profileUUID, profileVersion,null,null, null, null);
		profileExec = (ProfileExec) profileServiceImpl.parse(profileExec.getUuid(), profileExec.getVersion(), null, null, null, null, runMode);
		List<FutureTask<TaskHolder>> taskList = new ArrayList<FutureTask<TaskHolder>>();
		profileExec = profileServiceImpl.execute(profileUUID, profileVersion, profileExec,metaExecutor,null, taskList, null, runMode);
		commonServiceImpl.completeTaskThread(taskList);
		return profileExec;
	}
	
	@RequestMapping(value = "/executeGroup", method = RequestMethod.POST)
	public MetaIdentifier executeGroup(@RequestParam("uuid") String profileGroupUUID, 
			@RequestParam("version") String profileGroupVersion,
			@RequestBody(required=false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		ProfileGroupExec profileGroupExec = profileGroupServiceImpl.create(profileGroupUUID, profileGroupVersion, execParams, null, null);
		profileGroupExec = (ProfileGroupExec) profileGroupServiceImpl.parse(profileGroupExec.getUuid(), profileGroupExec.getVersion(), null, null, null, null, runMode);
		return profileGroupServiceImpl.execute(profileGroupUUID, profileGroupVersion, execParams, profileGroupExec, runMode);
	}

	@RequestMapping(value = "/getResults", method = RequestMethod.GET/*, params = { "profileExecUUID",
			"profileExecVersion", "offset", "imit", "sortBy", "order", "requestId", "type", "action", "mode" }*/)
	public List<Map<String, Object>> getResults(@RequestParam("uuid") String profileExecUUID,
			@RequestParam("version") String profileExecVersion,
			@RequestParam(value = "offset", defaultValue = "0") int offset,
			@RequestParam(value = "limit", defaultValue = "200") int limit,
			@RequestParam(value = "sortBy", required = false) String sortBy,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "requestId", required = false) String requestId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return profileServiceImpl.getProfileResults(profileExecUUID, profileExecVersion, offset, limit, sortBy, order,
				requestId, runMode);
	}
	
	@RequestMapping(value = "/getProfileExecByProfile", method = RequestMethod.GET)
    public List<ProfileExec> getProfileExecByProfile(@RequestParam("profileUUID") String profileUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) {
        return profileExecServiceImpl.findProfileExecByProfile(profileUUID);
    }
	@RequestMapping(value = "/getProfileExecByProfile", method = RequestMethod.GET, params = {"profileUUID", "startDate", "endDate"}	)
    public List<ProfileExec> getdqExecByProfile(@RequestParam("profileUUID") String profileUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws JsonProcessingException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
        return profileExecServiceImpl.findProfileExecByProfile(profileUUID, startDate, endDate, type);
    }
	@RequestMapping(value = "/getProfileExecByDatapod", method = RequestMethod.GET)
    public List<ProfileExec> getProfileExecByDatapod(@RequestParam("datapodUUID") String datapodUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, ParseException {
        return profileExecServiceImpl.findProfileExecByDatapod(datapodUUID,type);
    }

    @RequestMapping(value = "/getProfileExecByDatapod", method = RequestMethod.GET, params = {"datapodUUID", "startDate", "endDate"}	)
    public List<ProfileExec> getdqExecByDatapod(@RequestParam("datapodUUID") String datapodUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws JsonProcessingException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
        return profileExecServiceImpl.findProfileExecByDatapod(datapodUUID, startDate, endDate, type);
    }
    @RequestMapping(value = "/getProfileGroupExecByProfileGroup", method = RequestMethod.GET)
    public List<ProfileGroupExec> getProfileGroupExecByProfileGroup(@RequestParam("profileGroupUUID") String profileGroupUUID,
    		@RequestParam("profileGroupVersion") String profileGroupVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {        
        return profileGroupExecServiceImpl.findProfileGroupExecByProfileGroup(profileGroupUUID,profileGroupVersion);
    }
    
    @RequestMapping(value = "/getProfileExecByProfileGroupExec", method = RequestMethod.GET)
    public List<ProfileExec> getProfileExecByProfileGroupExec(@RequestParam("profileGroupExecUuid") String profileGroupExecUuid,
    		@RequestParam("profileGroupExecVersion") String profileGroupExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
        
        return profileExecServiceImpl.getProfileExecByProfileGroupExec(profileGroupExecUuid,profileGroupExecVersion);
    }
	
    @RequestMapping(value="/setStatus", method= RequestMethod.PUT)
	public boolean setStatus(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version,
			@RequestParam("status") String status,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		try {
			commonServiceImpl.setStatus(type,uuid,version,status);			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
    
    @RequestMapping(value="/restart", method= RequestMethod.POST)
	public boolean restart(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
			try {
				RunMode runMode = Helper.getExecutionMode(mode);
				if(type.equalsIgnoreCase(MetaType.profileExec.toString())){
					profileServiceImpl.restart(type,uuid,version, null, runMode);
				}
				else{
					profileGroupServiceImpl.restart(type,uuid,version, runMode);
				}
			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
	}	

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public HttpServletResponse download(@RequestParam(value = "profileExecUUID") String profileExecUUID,
			@RequestParam(value = "profileExecVersion") String profileExecVersion,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestParam(value = "rows", defaultValue = "200") int rows,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode,
			HttpServletResponse response,
			@RequestParam(value = "layout", required = false) Layout layout) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		profileServiceImpl.download(profileExecUUID, profileExecVersion, format, null, 0, rows, response, rows, null,
				null, null, runMode, layout);
		return null;
	}
	
	@RequestMapping(value = "/getProfileResults", method = RequestMethod.GET/*, params = { "datapodUuid", "datapodVersion",
			"attributeId", "profileAttrType", "numDays", "startDate", "endDate", "type", "action", "mode" }*/)
	public List<Map<String, Object>> getResults(@RequestParam("datapodUuid") String datapodUuid,
			@RequestParam(value = "datapodVersion", required = false) String datapodVersion,
			@RequestParam(value = "attributeId") String attributeId,
			@RequestParam(value = "profileAttrType") String profileAttrType,
			@RequestParam(value = "numDays", defaultValue = "0") int numDays,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws IOException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NullPointerException, ParseException, SQLException, JSONException {
		RunMode runMode = Helper.getExecutionMode(mode);
		return profileServiceImpl.getProfileResults(datapodUuid, datapodVersion, attributeId, profileAttrType, numDays,
				startDate, endDate);
	}
}

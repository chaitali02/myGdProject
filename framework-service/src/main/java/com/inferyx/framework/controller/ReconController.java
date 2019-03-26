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

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.ReconGroupServiceImpl;
import com.inferyx.framework.service.ReconServiceImpl;
import com.inferyx.framework.service.RegisterService;
import com.inferyx.framework.service.TaskHolder;

@RestController
@RequestMapping(value = "/recon")
public class ReconController {

	@Autowired
	RegisterService registerService;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	@Autowired
	private ReconServiceImpl reconServiceImpl;
	@Autowired
	private ReconGroupServiceImpl reconGroupServiceImpl;

	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public ReconExec execute(@RequestParam("uuid") String reconUuid, 
			@RequestParam("version") String reconVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		List<FutureTask<TaskHolder>> taskList = new ArrayList<FutureTask<TaskHolder>>();
		ReconExec reconExec = reconServiceImpl.create(reconUuid, reconVersion, null, null, null);
		reconExec = (ReconExec) reconServiceImpl.parse(reconExec.getUuid(), reconExec.getVersion(), null, null, null, null,
				runMode);
		reconExec = reconServiceImpl.execute(reconUuid, reconVersion, metaExecutor, reconExec, null, taskList, null, runMode);
		commonServiceImpl.completeTaskThread(taskList);
		return reconExec;
	}

	@RequestMapping(value = "/restart", method = RequestMethod.POST)
	public boolean restart(@RequestParam("uuid") String uuid, @RequestParam("version") String version,
			@RequestParam("type") String type, @RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		try {
			RunMode runMode = Helper.getExecutionMode(mode);
			if (type.equalsIgnoreCase(MetaType.reconExec.toString())) {
				reconServiceImpl.restart(type, uuid, version, null, runMode);
			} else {
				reconGroupServiceImpl.restart(type, uuid, version, runMode);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@RequestMapping(value = "/setStatus", method = RequestMethod.PUT)
	public boolean setStatus(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version,
			@RequestParam("status") String status, 
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		try {
			commonServiceImpl.setStatus(type, uuid, version, status);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public HttpServletResponse download(@RequestParam(value = "reconExecUUID") String reconExecUUID,
			@RequestParam(value = "reconExecVersion") String reconExecVersion,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestParam(value = "rows", defaultValue = "200") int rows,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode,
			HttpServletResponse response,
			@RequestParam(value = "layout", required = false) Layout layout) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		reconServiceImpl.download(reconExecUUID, reconExecVersion, format, null, 0, rows, response, rows,
				null, null, null, runMode, layout);
		return null;
	}

	@RequestMapping(value = "/getReconExecByRGExec", method = RequestMethod.GET)
	public @ResponseBody String getReconExecByRGExec(@RequestParam("reconGroupExecUuid") String reconGroupExecUuid,
			@RequestParam("reconGroupExecVersion") String reconGroupExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return reconServiceImpl.getReconExecByRGExec(reconGroupExecUuid, reconGroupExecVersion);
	}
	 
	@RequestMapping(value = "/getReconExecByRecon", method = RequestMethod.GET, params = {"uuid", "startDate", "endDate"}	)
    public List<ReconExec> getReconExecByRecon(@RequestParam("uuid") String uuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {        
        return reconServiceImpl.findReconExecByRecon(uuid, startDate, endDate, type, action);
    }
	
	@RequestMapping(value = "/getResults", method = RequestMethod.GET)
	public List<Map<String, Object>> getResults(@RequestParam("uuid") String reconExecUuid, 
			@RequestParam("version") String reconExecVersion,
			@RequestParam(value="offset", defaultValue="0") int offset, 
			@RequestParam(value="limit", defaultValue="200") int limit,
			@RequestParam(value="sortBy", required=false) String sortBy,
			@RequestParam(value="order", required=false) String order,
			@RequestParam(value="requestId", required=false) String requestId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, SQLException, JSONException {
		RunMode runMode = Helper.getExecutionMode(mode);
		return reconServiceImpl.getReconResults(reconExecUuid, reconExecVersion, offset, limit, sortBy, order, requestId, runMode);
	}
	
	@RequestMapping(value = "/executeGroup", method = RequestMethod.POST)
	public MetaIdentifier executeGroup(@RequestParam("uuid") String reconGroupUUID,
			@RequestParam("version") String reconGroupVersion, 
			@RequestBody(required=false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		ReconGroupExec reconGroupExec = null;
		reconGroupExec = reconGroupServiceImpl.create(reconGroupUUID, reconGroupVersion, execParams, null, null, null);
		reconGroupExec = reconGroupServiceImpl.parse(reconGroupExec.getUuid(), reconGroupExec.getVersion(), null, null, null, null, runMode);
		return reconGroupServiceImpl.execute(reconGroupUUID, reconGroupVersion, execParams, reconGroupExec, runMode);
	}

	@RequestMapping(value = "/getReconExecByDatapod", method = RequestMethod.GET, params = { "uuid", "startDate",
			"endDate" })
	public List<ReconExec> getReconExecByDatapod(@RequestParam("uuid") String uuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate)
			throws JsonProcessingException, ParseException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return reconServiceImpl.findReconExecByDatapod(uuid, startDate, endDate, type);
	}
}

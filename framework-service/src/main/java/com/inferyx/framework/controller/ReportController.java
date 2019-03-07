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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseEntityStatus;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Report;
import com.inferyx.framework.domain.ReportExec;
import com.inferyx.framework.domain.ReportExecView;
import com.inferyx.framework.domain.SenderInfo;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.ReportServiceImpl;

/**
 * @author Ganesh
 *
 */
@RestController
@RequestMapping(value="/report")
public class ReportController {
	@Autowired
	private ReportServiceImpl reportServiceImpl;
	
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public ReportExec execute(@RequestParam("uuid") String reportUuid, 
			@RequestParam("version") String reportVersion,
			@RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		ReportExec reportExec = reportServiceImpl.create(reportUuid, reportVersion, execParams, null, runMode);
		reportExec = reportServiceImpl.parse(reportExec.getUuid(), reportExec.getVersion(), null, null, null, runMode);
		return reportServiceImpl.execute(reportExec.getUuid(), reportExec.getVersion(), execParams, runMode);
	}
	
	@RequestMapping(value = "/getReportSample", method = RequestMethod.GET)
	public List<Map<String, Object>> getReportSample(@RequestParam(value= "uuid") String reportExecUuid, 
			@RequestParam(value= "version") String reportExecVersion,
			@RequestParam(value ="rows", defaultValue="100") int rows, 
			@RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value = "mode", required = false, defaultValue="ONLINE") String mode) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException, IOException {
		 	RunMode runMode = Helper.getExecutionMode(mode);
		 	return reportServiceImpl.getReportSample(reportExecUuid, reportExecVersion, rows, execParams, runMode);
	}
	
	@RequestMapping(value="/download",method=RequestMethod.GET)
	public HttpServletResponse  download(
			 	@RequestParam(value= "uuid") String reportExecUuid, 
	    		@RequestParam(value= "version") String reportExecVersion,
				@RequestParam(value = "format", defaultValue = "excel") String format,
				@RequestParam(value = "rows", defaultValue = "200") int rows,
				@RequestParam(value = "type", required = false) String type,
				@RequestParam(value = "action", required = false) String action,
				@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode,
				HttpServletResponse response) throws Exception {
		    RunMode runMode = Helper.getExecutionMode(mode);
		    reportServiceImpl.download(reportExecUuid, reportExecVersion, format, 0, rows, response, null, null,
					null, runMode, false);
		    return null;
	   }
	
	
	@RequestMapping(value="/downloadSample",method=RequestMethod.GET)
	public HttpServletResponse  downloadSample(
			 	@RequestParam(value= "uuid") String reportExecUuid, 
	    		@RequestParam(value= "version") String reportExecVersion,
				@RequestParam(value = "format", defaultValue = "excel") String format,
				@RequestParam(value = "rows", defaultValue = "200") int rows,
				@RequestParam(value = "type", required = false) String type,
				@RequestParam(value = "action", required = false) String action,
				@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode,
				HttpServletResponse response) throws Exception {
		    RunMode runMode = Helper.getExecutionMode(mode);
		    reportServiceImpl.downloadSample(reportExecUuid, reportExecVersion, format, 0, rows, response, null, null,
					null, runMode, false);
		    return null;
	   }
	
	@RequestMapping(value="/downloadReport",method=RequestMethod.GET)
	public HttpServletResponse  downloadReport(
			 	@RequestParam(value= "uuid") String reportExecUuid, 
	    		@RequestParam(value= "version") String reportExecVersion,
				@RequestParam(value = "format", defaultValue = "excel") String format,
				@RequestParam(value = "rows", defaultValue = "200") int rows,
				@RequestParam(value = "type", required = false) String type,
				@RequestParam(value = "action", required = false) String action,
				@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode,
				HttpServletResponse response) throws Exception {
		    RunMode runMode = Helper.getExecutionMode(mode);
		    reportServiceImpl.downloadReport(reportExecUuid, reportExecVersion, format, 0, rows, response, null, null,
					null, runMode, false);
		    return null;
	   }
	
	@RequestMapping(value = "/getReportByReportExec", method = RequestMethod.GET)
	public @ResponseBody Report getRuleExecByRule(@RequestParam("uuid") String reportExecUuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return reportServiceImpl.getReportByReportExec(reportExecUuid);
	}
	
	@RequestMapping(value = "/reSendEMail", method = RequestMethod.POST)
	public boolean reSendEMail(@RequestParam(value = "uuid") String reportExecUuid,
			@RequestParam(value = "version", required = false) String reportExecVersion,
			@RequestBody(required = true) SenderInfo senderInfo,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException, IOException {
		RunMode runMode = Helper.getExecutionMode(mode);
		return reportServiceImpl.reSendNotification(reportExecUuid, reportExecVersion, senderInfo, runMode);
	}	

	@RequestMapping(value = "/getReportExecViewByCriteria", method = RequestMethod.GET)
	public @ResponseBody List<ReportExecView> getReportExecViewByCriteria(@RequestParam("type") String type,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "tags", required = false) String tags,
			@RequestParam(value = "active", required = false) String active,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "appUuid", required = false) String appUuid,
			@RequestParam(value = "role", required = false) String role) {

		return reportServiceImpl.getReportExecViewByCriteria(role, appUuid, type, name, userName, startDate, endDate,
				tags, active, status);
	}
}

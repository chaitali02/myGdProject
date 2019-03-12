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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Dashboard;
import com.inferyx.framework.domain.DashboardExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DashboardServiceImpl;
import com.inferyx.framework.service.DashboardViewServiceImpl;
import com.inferyx.framework.view.metadata.DashboardView;

@RestController
@RequestMapping(value="/dashboard")
public class DashboardController {

	@Autowired private DashboardServiceImpl dashboardServiceImpl;
	@Autowired private DashboardViewServiceImpl dashboardViewServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	@RequestMapping(value="/getAll", method=RequestMethod.GET)
	public List<Dashboard> getAllDashboards(HttpServletResponse response,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException{
    	return dashboardServiceImpl.findAll();
	}
    	
	@RequestMapping(value="/getAllViews", method=RequestMethod.GET)
	public List<DashboardView> getAllDashboardViews(HttpServletResponse response,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonProcessingException{
    	List<Dashboard> dashboardList = dashboardServiceImpl.findAll();
    	return dashboardViewServiceImpl.getDashboardViews(dashboardList);
   }
	
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public DashboardExec execute(@RequestParam(value = "uuid") String dashboardUuid,
			@RequestParam(value = "version", required = false) String dashboardVersion,
			@RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		DashboardExec dashboardExec = dashboardServiceImpl.create(dashboardUuid, dashboardVersion, null, execParams, runMode);
		dashboardExec = dashboardServiceImpl.parse(dashboardExec.getUuid(), dashboardExec.getVersion(), execParams, null, null, null, null, runMode);
		return dashboardServiceImpl.execute(dashboardExec.getUuid(), dashboardExec.getVersion(), execParams, runMode);
	}
	
	@RequestMapping(value = "/getDasboardExecBySave", method = RequestMethod.GET)
	public List<DashboardExec> getDasboardExecBySave(@RequestParam(value = "uuid") String dashboardUuid,
			@RequestParam(value = "version", required = false) String dashboardVersion,
			@RequestParam(value = "saveOnRefresh", required = false) String saveonRefresh,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return dashboardServiceImpl.getDasboardExecBySave(dashboardUuid, dashboardVersion, saveonRefresh);
	}
}

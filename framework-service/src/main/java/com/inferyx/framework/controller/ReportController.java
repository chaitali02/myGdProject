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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.ReportExec;
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
	ReportServiceImpl reportServiceImpl;
	
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public ReportExec execute(@RequestParam("uuid") String reportUuid, 
			@RequestParam("version") String reportVersion,
			@RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		ReportExec reportExec = reportServiceImpl.create(reportUuid, reportVersion, execParams, null, runMode);
		reportExec = reportServiceImpl.parse(reportExec.getUuid(), reportExec.getVersion(), null, null, null, runMode);
		return reportServiceImpl.execute(reportUuid, reportVersion, execParams, reportExec, runMode);
	}
}

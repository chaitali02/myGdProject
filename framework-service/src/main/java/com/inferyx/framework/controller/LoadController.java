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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.LoadExecServiceImpl;
import com.inferyx.framework.service.LoadServiceImpl;

@RestController
@RequestMapping(value = "/load")
public class LoadController {
	
	@Autowired
	private LoadServiceImpl loadServiceImpl;
	@Autowired
	private LoadExecServiceImpl loadExecServiceImpl;
	
	@RequestMapping(value = "/getResults", method = RequestMethod.GET)
	public List<Map<String, Object>> getResults (@RequestParam("uuid") String loadExecUUID, 
			@RequestParam("version") String loadExecVersion,
			@RequestParam(value="offset", defaultValue="0") int offset, 
			@RequestParam(value="limit", defaultValue="200") int limit,
			@RequestParam(value="sortBy", required=false) String sortBy,
			@RequestParam(value="order", required=false) String order, 
			@RequestParam(value="requestId", required=false) String requestId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value = "mode", required = false, defaultValue="BATCH") String mode) throws Exception {
			RunMode runMode = Helper.getExecutionMode(mode);
			return loadServiceImpl.getLoadResults(loadExecUUID, loadExecVersion, offset, limit, sortBy, order, requestId, runMode);
	}
	
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public LoadExec execute(@RequestParam(value = "uuid") String loadUuid,
							@RequestParam(value = "version") String loadVersion,
						    @RequestBody (required = false) ExecParams execParams,
							@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);		
		LoadExec loadExec = null;
		loadExec = loadServiceImpl.create(loadUuid, loadVersion, execParams, null, loadExec);
		loadExec = loadServiceImpl.execute(loadUuid, loadVersion, loadExec, execParams, runMode);
		return loadExec;
	}
	
	@RequestMapping(value="/kill", method = RequestMethod.GET)
	public boolean kill(@RequestParam(value = "uuid") String execUuid,
						@RequestParam(value = "version", required = false) String execVersion,
						@RequestParam(value = "type", required = false) String type) {
		try {
			loadExecServiceImpl.kill(execUuid, execVersion);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
}

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
import com.inferyx.framework.domain.IngestExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.IngestServiceImpl;

/**
 * @author Ganesh
 *
 */
@RestController
@RequestMapping(value = "/ingest")
public class IngestController {
	@Autowired
	private IngestServiceImpl ingestServiceImpl;
	
	@RequestMapping(value = "execute", method = RequestMethod.POST)
	public IngestExec execute(@RequestParam(value = "uuid") String ingestUuid,
			@RequestParam(value = "version") String ingestVersion,
			@RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		IngestExec ingestExec = ingestServiceImpl.create(ingestUuid, ingestVersion, execParams, null, runMode);
		return ingestServiceImpl.execute(ingestUuid, ingestVersion, ingestExec, execParams, type, runMode);
	}
	
	@RequestMapping(value = "/getResults", method = RequestMethod.GET)
	public List<Map<String, Object>> getResults(@RequestParam("uuid") String execUuid,
			@RequestParam("version") String execVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "rowLimit", required = false, defaultValue = "1000") int rowLimit) throws Exception {
		rowLimit = Integer.parseInt(Helper.getPropertyValue("framework.result.row.limit"));
		return ingestServiceImpl.getResults(execUuid, execVersion, rowLimit);
	}
}

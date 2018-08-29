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
import com.inferyx.framework.domain.BatchExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.BatchServiceImpl;

/**
 * @author Ganesh
 *
 */
@RestController
@RequestMapping(value = "/batch")
public class BatchController {
	
	@Autowired
	private BatchServiceImpl batchServiceImpl;
	
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public BatchExec execute(@RequestParam(value = "uuid") String batchUuid,
			@RequestParam(value = "version") String batchVersion,
			@RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		BatchExec batchExec = batchServiceImpl.create(batchUuid, batchVersion, execParams, null, runMode);
		return batchServiceImpl.submitBatch(batchUuid, batchVersion, batchExec, execParams, type, runMode);
	}
	
	@RequestMapping(value = "/setStatus", method = RequestMethod.PUT)
	public boolean kill(@RequestParam(value = "uuid") String execUuid,
						@RequestParam(value = "version") String execVersion,
						@RequestParam(value = "type", required = false) String type,
						@RequestParam(value = "action", required = false) String action) {
		try {
			batchServiceImpl.kill(execUuid, execVersion);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@RequestMapping(value = "/restart", method = RequestMethod.POST)
	public boolean restart(@RequestParam(value = "uuid") String execUuid,
						@RequestParam(value = "version") String execVersion,
						@RequestParam(value = "type", required = false) String type,
						@RequestParam(value = "action", required = false) String action, 
						@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) {
		try {
			RunMode runMode = Helper.getExecutionMode(mode);
			batchServiceImpl.restart(execUuid, execVersion, runMode);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}

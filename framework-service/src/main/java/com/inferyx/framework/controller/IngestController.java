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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.IngestExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.IngestGroupExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.IngestExecServiceImpl;
import com.inferyx.framework.service.IngestGroupServiceImpl;
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
	@Autowired
	private IngestGroupServiceImpl ingestGroupServiceImpl;
	@Autowired
	private IngestExecServiceImpl ingestExecServiceImpl;
	
	@RequestMapping(value = "execute", method = RequestMethod.POST)
	public IngestExec execute(@RequestParam(value = "uuid") String ingestUuid,
			@RequestParam(value = "version") String ingestVersion,
			@RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		IngestExec ingestExec = ingestServiceImpl.create(ingestUuid, ingestVersion, execParams, null, runMode);
		ingestExec = (IngestExec) ingestServiceImpl.parse(ingestExec, execParams, runMode);
		return ingestServiceImpl.execute(ingestUuid, ingestVersion, ingestExec, execParams, type, runMode);
	}
	@RequestMapping(value = "/getIngestExecByRGExec", method = RequestMethod.GET)
	public @ResponseBody String getIngestExecByRGExec(@RequestParam("ingestGroupExecUuid") String ingestGroupExecUuid,
			@RequestParam("ingestGroupExecVersion") String ingestGroupExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return ingestServiceImpl.getIngestExecByRGExec(ingestGroupExecUuid, ingestGroupExecVersion);
	}
	
	@RequestMapping(value = "/getIngestByIngestExec", method = RequestMethod.GET)
	public @ResponseBody String getIngestByIngestExec(@RequestParam("uuid") String ingestUuid,
			@RequestParam("version") String ingestVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return ingestServiceImpl.getIngestByIngestExec(ingestUuid, ingestVersion);
	}
	
	@RequestMapping(value = "/executeGroup", method = RequestMethod.POST)
	public MetaIdentifier executeGroup(@RequestParam("uuid") String groupUuid,
			@RequestParam("version") String groupVersion, 
			@RequestBody(required=false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		IngestGroupExec ingestGroupExec = null;
		ingestGroupExec = ingestGroupServiceImpl.create(groupUuid, groupVersion, execParams, null, null, null);
		ingestGroupExec = ingestGroupServiceImpl.parse(ingestGroupExec.getUuid(), ingestGroupExec.getVersion(), null, null, null, null, runMode);
		return ingestGroupServiceImpl.execute(groupUuid, groupVersion, execParams, ingestGroupExec, runMode);
	}
	
	@RequestMapping(value = "/getResults", method = RequestMethod.GET)
	public List<Map<String, Object>> getResults(@RequestParam("uuid") String execUuid,
			@RequestParam("version") String execVersion,
			@RequestParam(value = "offset", defaultValue = "0") int offset,
			@RequestParam(value = "limit", defaultValue = "200") int limit,
			@RequestParam(value = "sortBy", required = false) String sortBy,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "requestId", required = false) String requestId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return ingestServiceImpl.getResults(execUuid, execVersion, offset, limit, sortBy, order, requestId, runMode);
	}
	
	@RequestMapping(value = "/restart", method = RequestMethod.POST)
	public boolean restart(@RequestParam("uuid") String uuid,
			@RequestParam("version") String version,
			@RequestParam("type") String type, 
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode) throws Exception {
		try {
			RunMode runMode = Helper.getExecutionMode(mode);
			if (type.equalsIgnoreCase(MetaType.ingestExec.toString())) {
				ingestServiceImpl.restart(type, uuid, version, null, runMode);
			} else {
				ingestGroupServiceImpl.restart(type, uuid, version, runMode);
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
			ingestExecServiceImpl.kill(uuid, version, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}	

	@RequestMapping(value = "/getTopicList", method = RequestMethod.GET)
	public List<String> getTopicList(@RequestParam("uuid") String dsUuid,
			@RequestParam("version") String dsVersion,		
			@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return ingestServiceImpl.getTopicList(dsUuid, dsVersion, runMode);
	}
}

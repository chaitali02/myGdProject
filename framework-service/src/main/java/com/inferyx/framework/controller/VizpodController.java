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

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.VizExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.DagExecServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.VizpodServiceImpl;

@RestController
@RequestMapping(value = "/vizpod")
public class VizpodController {

	final static Logger logger = Logger.getLogger(VizpodController.class);

	@Autowired
	VizpodServiceImpl vizpodServiceImpl;
	@Autowired
	DagExecServiceImpl dagExecImpl;
	@Autowired
	DataStoreServiceImpl datastoreImpl;
//
//	@RequestMapping(value = "/getVizpodResults/{VizpodUUID}/{VizpodVersion}", method = RequestMethod.GET)
//	public List<Map<String, Object>> getVizpodResults(@PathVariable(value = "VizpodUUID") String vizpodUUID,
//			@PathVariable(value = "VizpodVersion") String vizpodVersion,
//			@RequestParam(value = "DatastoreUUID") String datastoreUUID,
//			@RequestParam(value = "DatastoreVersion") String datastoreVersion,
//			@RequestParam(value ="rows",defaultValue="1000") int rows,
//			@RequestParam(value="offset", defaultValue="0") int offset, 
//			@RequestParam(value="limit", defaultValue="300") int limit,
//			@RequestParam(value="sortBy", required=false) String sortBy,
//			@RequestParam(value="order", required=false) String order, 
//			@RequestParam(value="requestId") String requestId,
//			@RequestParam(value = "type", required = false) String type,
//			@RequestParam(value = "action", required = false) String action) throws IOException, JSONException, ParseException {
//		VizpodResultHolder resultHolder = vizpodServiceImpl.getVizpodResults(vizpodUUID, vizpodVersion, datastoreUUID, datastoreVersion, rows, offset, limit, sortBy, order, requestId);
//		if (resultHolder == null) {
//			return null;
//		}
//		return resultHolder.getVizpodResultDataList();
//	}

//	@RequestMapping(value = "/getVizpodResults/{VizpodUUID}/{VizpodVersion}", method = RequestMethod.POST)
//	public List<Map<String, Object>> getVizpodResults(@PathVariable(value = "VizpodUUID") String vizpodUUID,
//			@PathVariable(value = "VizpodVersion") String vizpodVersion,
//			@RequestBody(required = false) ExecParams execParams) throws IOException {
//		VizpodResultHolder resultHolder = vizpodServiceImpl.getVizpodResults(vizpodUUID, vizpodVersion, execParams);
//		if (resultHolder == null) {
//			return null;
//		}
//		return resultHolder.getVizpodResultDataList();
//	}

	@RequestMapping(value = "/getVizpodResults/{VizpodUUID}/{VizpodVersion}", method = RequestMethod.POST)
	public List<Map<String, Object>> getVizpodResults(@PathVariable(value = "VizpodUUID") String vizpodUUID,
			@PathVariable(value = "VizpodVersion") String vizpodVersion,
			@RequestBody(required = false) ExecParams execParams, 
			@RequestParam(value ="rows",defaultValue="1000") int rows,
			@RequestParam(value="offset", defaultValue="0") int offset, 
			@RequestParam(value="limit", defaultValue="200") int limit,
			@RequestParam(value="sortBy", required=false) String sortBy,
			@RequestParam(value="order", required=false) String order, 
			@RequestParam(value="requestId", required=false) String requestId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		RunMode runMode = RunMode.BATCH;
		VizExec vizExec = vizpodServiceImpl.create(vizpodUUID, vizpodVersion, null, execParams, runMode);
		return vizpodServiceImpl.getVizpodResults(vizpodUUID, vizpodVersion, execParams, vizExec, rows, offset, limit, sortBy, order, requestId, runMode);
	}
	
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public VizExec execute(@RequestParam(value = "uuid") String vizpodUuid,
			@RequestParam(value = "version") String vizpodVersion,
			@RequestParam(value = "saveOnRefresh", defaultValue = "N") String saveOnRefresh,
			@RequestBody(required = false) ExecParams execParams, 
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		VizExec vizExec = vizpodServiceImpl.create(vizpodUuid, vizpodVersion, null, execParams, runMode);
		return vizpodServiceImpl.execute(vizpodUuid, vizpodVersion, execParams, vizExec, saveOnRefresh, runMode);
	}

	@RequestMapping(value = "/getVizpodDetails/{VizpodUUID}/{VizpodVersion}", method = RequestMethod.POST)
	public List<Map<String, Object>> getVizpodDetails(@PathVariable(value = "VizpodUUID") String vizpodUUID,
			@PathVariable(value = "VizpodVersion") String vizpodVersion,
			@RequestBody(required = false) ExecParams execParams, 
			@RequestParam(value ="rows",defaultValue="1000") int rows,
			@RequestParam(value="offset", defaultValue="0") int offset, 
			@RequestParam(value="limit", defaultValue="300") int limit,
			@RequestParam(value="sortBy", required=false) String sortBy,
			@RequestParam(value="order", required=false) String order, 
			@RequestParam(value="requestId", required=false) String requestId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		RunMode runMode = RunMode.BATCH;
		VizExec vizExec = vizpodServiceImpl.create(vizpodUUID, vizpodVersion, null, execParams, runMode);		
		return vizpodServiceImpl.getVizpodDetails(vizpodUUID, vizpodVersion, execParams, vizExec, rows, offset, limit, sortBy, order, requestId, runMode);
	}
	
	@RequestMapping(value = "/getVizpodResults", method = RequestMethod.GET)
	public List<Map<String, Object>> getVizpodResults2(@RequestParam(value = "uuid") String vizExecUuid,
			@RequestParam(value = "version") String vizExecVersion,
			@RequestParam(value ="rows",defaultValue="1000") int rows,
			@RequestParam(value="offset", defaultValue="0") int offset, 
			@RequestParam(value="limit", defaultValue="200") int limit,
			@RequestParam(value="sortBy", required=false) String sortBy,
			@RequestParam(value="order", required=false) String order, 
			@RequestParam(value="requestId", required=false) String requestId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "runMode", required = false, defaultValue = "BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return vizpodServiceImpl.getVizpodResults(vizExecUuid, vizExecVersion, rows, offset, limit, sortBy, order, requestId, runMode);	
	}

//@RequestMapping(value = "/getVizpodResults/{VizpodUUID}/{VizpodVersion}/{VizpodExecUUID}", method = RequestMethod.POST)
//public VizpodResultHolder getVizpodResults(@PathVariable(value = "VizpodUUID") String vizpodUUID,
//		@PathVariable(value = "VizpodVersion") String vizpodVersion, 
//		@PathVariable(value = "VizpodExecUUID") String vizExecUUID,
//		@RequestParam(value ="rows",defaultValue="1000") int rows,
//		@RequestParam(value="offset", defaultValue="0") int offset, 
//		@RequestParam(value="limit", defaultValue="200") int limit,
//		@RequestParam(value="sortBy", required=false) String sortBy,
//		@RequestParam(value="order", required=false) String order, 
//		@RequestParam(value="requestId") String requestId, 
//		@RequestBody(required = false) ExecParams execParams,
//		@RequestParam(value = "type", required = false) String type,
//		@RequestParam(value = "action", required = false) String action) throws IOException, JSONException, ParseException {
//	VizpodResultHolder resultHolder = vizpodServiceImpl.getVizpodResults(vizpodUUID, vizpodVersion, execParams, vizExecUUID, rows, offset, limit, sortBy, order, requestId);
//	if (resultHolder == null) {
//		return null;
//	}
//	return resultHolder;
//}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public HttpServletResponse download(@RequestParam(value = "vizpodUUID") String vizpodUUID,
			@RequestParam(value = "vizpodVersion") String vizpodVersion,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "rows", defaultValue = "200") int rows,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode,
			HttpServletResponse response) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		vizpodServiceImpl.download(vizpodUUID, vizpodVersion, format, execParams, null, 0, rows, response, rows, null,
				null, null, runMode);
		return null;
	}
 
}

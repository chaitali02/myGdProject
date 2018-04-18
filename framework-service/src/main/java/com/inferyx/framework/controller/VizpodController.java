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

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.framework.domain.VizpodDetailsHolder;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.VizpodResultHolder;
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
			@RequestParam(value = "action", required = false) String action) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Mode runMode = Mode.BATCH;
		VizpodResultHolder resultHolder = vizpodServiceImpl.getVizpodResults(vizpodUUID, vizpodVersion, execParams, rows, offset, limit, sortBy, order, requestId, runMode);
		if (resultHolder == null) {
			return null;	
		}
		return resultHolder.getVizpodResultDataList();
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
			@RequestParam(value = "action", required = false) String action) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Mode runMode = Mode.BATCH;
		VizpodDetailsHolder resultHolder = vizpodServiceImpl.getVizpodDetails(vizpodUUID, vizpodVersion, execParams, rows, offset, limit, sortBy, order, requestId, runMode);
		if (resultHolder.getVizpodDetailsDataList() == null) {
			return null;
		}
		return resultHolder.getVizpodDetailsDataList();
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



@RequestMapping(value="/download",method=RequestMethod.GET)
public HttpServletResponse  download(@RequestParam(value= "vizpodUUID") String vizpodUUID, 
    		@RequestParam(value= "vizpodVersion") String vizpodVersion,
    		@RequestParam(value = "format", defaultValue="excel")String format,
    		@RequestBody(required = false) ExecParams execParams, 
			@RequestParam(value ="rows",defaultValue="1000") int rows,
			@RequestParam(value = "download", defaultValue="Y") String download,@RequestParam(value="offset", defaultValue="0") int offset, 
			@RequestParam(value="limit", defaultValue="200") int limit,
			@RequestParam(value="sortBy", required=false) String sortBy,@RequestParam(value="order", required=false) String order,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value="requestId",required = false) String requestId, 
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode, HttpServletResponse response) throws Exception
    		{
		Mode runMode = Helper.getExecutionMode(mode);
		vizpodServiceImpl.download(vizpodUUID, vizpodVersion,format,execParams,download,offset,limit,response,rows,sortBy,order,requestId, runMode);
    	return null;
   }
 
}

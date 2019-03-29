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

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.DataStoreServiceImpl;

@RestController
@RequestMapping(value="/datastore")
public class DatastoreController {
	
	@Autowired private DataStoreServiceImpl dataStoreServiceImpl;
	
	@RequestMapping(value="/dataStoreForDim/{datapodUuid}", method=RequestMethod.POST)
	public List<DataStore> getDataStoreForDim(@PathVariable String datapodUuid, 
			@RequestBody List<AttributeRefHolder> dimInfoList,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) {
		return dataStoreServiceImpl.getDataStoreByDim(datapodUuid,dimInfoList);
	}

	@RequestMapping(value = "/getResult", method = RequestMethod.GET)
	public List<Map<String, Object>> getResult(@RequestParam(value = "uuid") String datastoreUuid,
											   @RequestParam(value = "version") String datastoreVersion,
												@RequestParam(value="offset", defaultValue="0", required=false) int offset, 
												@RequestParam(value="limit", defaultValue="200", required=false) int limit,
												@RequestParam(value="sortBy", required=false) String sortBy,
												@RequestParam(value="order", required=false) String order, 
												@RequestParam(value="requestId", required=false) String requestId,
												@RequestParam(value = "type", required = false) String type,
												@RequestParam(value = "action", required = false) String action,
												@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException, IOException{
		limit = offset+limit;
		offset = offset+1;
		RunMode runMode = Helper.getExecutionMode(mode);		
		return dataStoreServiceImpl.getResultByDatastore(datastoreUuid, datastoreVersion, requestId, offset, limit, sortBy, order, null,runMode);
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public HttpServletResponse download(@RequestParam(value = "uuid") String datastoreUUID,
			@RequestParam(value = "version") String datastoreVersion,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestParam(value = "rows", defaultValue = "1000") int rows,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode,
			HttpServletResponse response,
			@RequestParam(value = "layout", required = false) Layout layout) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		response = dataStoreServiceImpl.download(datastoreUUID, datastoreVersion, format
				, 0, rows, response, rows, null,
				null, null, runMode, layout);
		return null;

	}
	
}

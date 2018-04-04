/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.DataStore;
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
												@RequestParam(value = "action", required = false) String action) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException, IOException{
		limit = offset+limit;
		offset = offset+1;
		return dataStoreServiceImpl.getResultByDatastore(datastoreUuid, datastoreVersion, requestId, offset, limit, sortBy, order);
	}
}

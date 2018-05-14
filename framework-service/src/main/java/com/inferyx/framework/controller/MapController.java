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
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.MapServiceImpl;

@RestController
@RequestMapping(value = "/map")
public class MapController {
	
	@Autowired
	private MapServiceImpl mapServiceImpl;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/findAll", method = RequestMethod.POST)
	public List<com.inferyx.framework.domain.Map> findAll(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) {
		return mapServiceImpl.findAllDemo();
	}*/
	
//	@RequestMapping(value = "/findAllLatest", method = RequestMethod.POST)
//	public String findAllLatest() {
//		Aggregation aggr = newAggregation(group("uuid").max("version").as("version"));
//		try {
//			return commonServiceImpl.getAllLatest(MetaType.map.toString(),null);
//		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
//				| SecurityException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/resolveNameDemo", method = RequestMethod.POST)
	public com.inferyx.framework.domain.Map resolveNameDemo(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ParseException {
		List<com.inferyx.framework.domain.Map> mapList = mapServiceImpl.findAllDemo();
		if (mapList == null || mapList.isEmpty()) {
			return null;
		}
		commonServiceImpl.resolveName(mapList.get(0), MetaType.map);
		return mapList.get(0);		
	}*/
	
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public MetaIdentifierHolder execute(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version, 
			@RequestBody(required=false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) {
		MapExec mapExec = null;
		try {
			RunMode runMode = Helper.getExecutionMode(mode);
			mapExec = mapServiceImpl.generateSql(uuid, version, null, null, null, null, null, null, null, execParams, runMode);
			/*com.inferyx.framework.domain.Map map = mapServiceImpl.findLatestByUuid(uuid);*/
			com.inferyx.framework.domain.Map map = (com.inferyx.framework.domain.Map) commonServiceImpl.getLatestByUuid(uuid, "map");
			OrderKey datapodKey = map.getTarget().getRef().getKey();
			mapExec = mapServiceImpl.executeSql(mapExec, null, datapodKey, null, runMode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new MetaIdentifierHolder(new MetaIdentifier(MetaType.mapExec, mapExec.getUuid(), mapExec.getVersion()));
	}
	@RequestMapping(value="/download",method=RequestMethod.GET)
	public HttpServletResponse  download(@RequestParam(value= "mapExecUUID") String mapExecUUID, 
	    		@RequestParam(value= "mapExecVersion") String mapExecVersion,
	    		@RequestParam(value = "format", defaultValue="excel")String format,
				@RequestParam(value ="rows",defaultValue="1000") int rows,
				@RequestParam(value="offset", defaultValue="0") int offset, 
				@RequestParam(value="limit", defaultValue="200") int limit,
				@RequestParam(value="sortBy", required=false) String sortBy,
				@RequestParam(value="order", required=false) String order,
				@RequestParam(value = "type", required = false) String type,
				@RequestParam(value = "action", required = false) String action,
				@RequestParam(value="requestId",required = false) String requestId, 
				@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode, HttpServletResponse response) throws Exception
	    		{
			RunMode runMode = Helper.getExecutionMode(mode);
			response = mapServiceImpl.download(mapExecUUID, mapExecVersion,format,offset,limit,response,rows,sortBy,order,requestId, runMode);
			return null;
			
	   }
	@RequestMapping(value = "/getResults", method = RequestMethod.GET)
	public List<Map<String, Object>> getResults (@RequestParam("uuid") String mapExecUUID, 
			@RequestParam("version") String mapExecVersion,
			@RequestParam(value="offset", defaultValue="0") int offset, 
			@RequestParam(value="limit", defaultValue="200") int limit,
			@RequestParam(value="sortBy", required=false) String sortBy,
			@RequestParam(value="order", required=false) String order, 
			@RequestParam(value="requestId", required=false) String requestId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws IOException, SQLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException {
		RunMode runMode = Helper.getExecutionMode((mode.equals("undefined")) ? mode="BATCH" : mode);
		return mapServiceImpl.getMapResults(mapExecUUID, mapExecVersion, offset, limit, sortBy, order, requestId, runMode);
	}
}

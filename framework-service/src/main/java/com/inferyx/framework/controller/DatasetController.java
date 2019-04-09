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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.DatasetServiceImpl;

@RestController
@RequestMapping(value="/dataset")
public class DatasetController {

	@Autowired protected DatasetServiceImpl datasetServiceImpl;
	
	@RequestMapping(value="/getDatasetSample", method=RequestMethod.GET)
	public List<Map<String, Object>>  getDatasetSample(@RequestParam(value= "datasetUUID") String datasetUUID, 
			@RequestParam(value= "datasetVersion") String datasetVersion,
			@RequestParam(value ="rows",defaultValue="100") int rows, 
			@RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value = "mode", required = false, defaultValue="BATCH") String mode) throws Exception{
		 	RunMode runMode = Helper.getExecutionMode(mode);
	    	return datasetServiceImpl.getDatasetSample(datasetUUID, datasetVersion, rows, execParams, runMode);	   	
	}
	
	@RequestMapping(value = "/getAttributeValues",  method=RequestMethod.GET)
	public List<Map<String, Object>>  getAttributeValues(@RequestParam(value= "uuid") String datasetUuid,
    		@RequestParam(value= "attributeId") int attributeID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,	    
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws Exception{
	 	RunMode runMode = Helper.getExecutionMode(mode);		 
    	return datasetServiceImpl.getAttributeValues(datasetUuid, attributeID, runMode, new HashMap<String, String>());   	
   }
   
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public HttpServletResponse download(@RequestParam(value = "uuid") String datasetUUID,
			@RequestParam(value = "version") String datasetVersion,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestParam(value = "rows", defaultValue = "200") int rows,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode,
			HttpServletResponse response,
			@RequestParam(value = "layout", required = false) Layout layout) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		response =  datasetServiceImpl.download(datasetUUID, datasetVersion, format, rows, runMode, response, layout);	   	
		return null;
	}
}
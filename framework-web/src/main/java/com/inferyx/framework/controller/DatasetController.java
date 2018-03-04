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
import com.inferyx.framework.domain.Mode;
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
		 	Mode runMode = Helper.getExecutionMode(mode);
	    	return datasetServiceImpl.getDatasetSample(datasetUUID,datasetVersion,rows, execParams, runMode);	   	
	   }
}
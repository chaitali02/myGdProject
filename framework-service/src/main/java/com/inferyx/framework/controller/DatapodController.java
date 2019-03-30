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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.CompareMetaData;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DatapodStatsHolder;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;

@RestController
@RequestMapping(value="/datapod")
public class DatapodController {
	
	@Autowired private DataStoreServiceImpl datastoreServiceImpl;
	@Autowired protected DatapodServiceImpl datapodServiceImpl;
	@RequestMapping(method=RequestMethod.GET)
	public List<Map<String, Object>> getDatapodResults(@RequestParam(value= "dataStoreUUID") String dataStoreUUID, 
	    		@RequestParam(value= "dataStoreVersion") String dataStoreVersion,
	    		@RequestParam(value = "format", defaultValue="excel")String format,
				@RequestParam(value ="rows",defaultValue="1000") int rows,
				@RequestParam(value="offset", defaultValue="0") int offset, 
				@RequestParam(value="limit", defaultValue="200") int limit,
				@RequestParam(value="sortBy", required=false) String sortBy,@RequestParam(value="order", required=false) String order,
				@RequestParam(value = "type", required = false) String type,
				@RequestParam(value = "action", required = false) String action,
				@RequestParam(value="requestId") String requestId, 
				@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode, HttpServletResponse response) throws Exception
	    		{
			RunMode runMode = Helper.getExecutionMode(mode);
	    	return datastoreServiceImpl.getDatapodResults(dataStoreUUID, dataStoreVersion,format,offset,limit,response,rows,sortBy,order,requestId, runMode);
	   	
	   }

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public HttpServletResponse download(@RequestParam(value = "uuid") String datapodUUID,
			@RequestParam(value = "version") String datapodVersion,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestParam(value = "rows", defaultValue = "200") int rows,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode,
			HttpServletResponse response,
			@RequestParam(value = "layout", required = false) Layout layout) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		response = datapodServiceImpl.download(datapodUUID, datapodVersion, format, 0, 
				rows, response, rows, null, null,
				null, runMode, layout);
		return null;

	}
	 
	 @RequestMapping(value="/getDatapodSample", method=RequestMethod.GET)
	    public List<Map<String, Object>>  getDatapodSample(@RequestParam(value= "datapodUUID") String datapodUUID, 
	    		@RequestParam(value= "datapodVersion") String datapodVersion,
				@RequestParam(value ="rows",defaultValue="100") int rows,
				@RequestParam(value = "type", required = false) String type,
				@RequestParam(value = "action", required = false) String action, 
				@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception{
		 	RunMode runMode = Helper.getExecutionMode(mode);
	    	return datapodServiceImpl.getDatapodSample(datapodUUID,datapodVersion,rows, runMode);	   	
	   }
	 
	 @RequestMapping(value="/getAttributeValues1", method=RequestMethod.GET)
	    public List<Map<String, Object>>  getAttributeValues(@RequestParam(value= "datapodUUID") String datapodUUID,
	    		@RequestParam(value= "attributeId") int attributeID,
				@RequestParam(value = "type", required = false) String type,
				@RequestParam(value = "action", required = false) String action,	    
				@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception{
		 	RunMode runMode = Helper.getExecutionMode(mode);		 
	    	return datastoreServiceImpl.getAttributeValues(datapodUUID, attributeID,runMode);   	
	   }
	 
	 @RequestMapping(value="/getAttributeName", method=RequestMethod.GET)
	 public String getAttributeName(@RequestParam(value="datapodUUID") String datapodUUID,
			 @RequestParam(value="attributeId") int attributeId,
			 @RequestParam(value="type", required = false) String type, 
			 @RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		 return datapodServiceImpl.getAttributeName(datapodUUID, attributeId);
	 }

	@RequestMapping(value = "/getDatapodStats", method = RequestMethod.GET)
	public @ResponseBody List<DatapodStatsHolder> getDatapodStats(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return datapodServiceImpl.getDatapodStats();
	}
	
	@RequestMapping(value = "/getDatapodStats", method = RequestMethod.GET,params= {"searchStr"})
	public @ResponseBody List<DatapodStatsHolder> getDatapodStats(@RequestParam(value="searchStr") String searchStr,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return datapodServiceImpl.getDatapodStats(searchStr);
	}
	
	@RequestMapping(value = "/getDatapodStats2", method = RequestMethod.GET)
	public @ResponseBody List<DatapodStatsHolder> getDatapodStats2(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return datapodServiceImpl.getDatapodStats2();
	}
	
	@RequestMapping(value = "/upload", headers = ("content-type=multipart/form-data; boundary=abcd"), method = RequestMethod.POST)
	public boolean upload(@RequestParam("csvFileName") MultipartFile csvFile,
			@RequestParam(value = "datapodUuid") String datapodUuid,
			@RequestParam(value = "desc") String desc,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception{
		try{
		 	RunMode runMode = Helper.getExecutionMode(mode);			
			datapodServiceImpl.upload(csvFile,datapodUuid,desc,runMode);
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/compareMetadata", method = RequestMethod.GET)
	public List<CompareMetaData> compareMetadata(@RequestParam(value = "uuid") String datapodUuid,
					@RequestParam(value = "version")String datapodVersion,
					@RequestParam(value = "type", required = false) String type,
					@RequestParam(value = "action", required = false) String action,	    
					@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return datapodServiceImpl.compareMetadata(datapodUuid, datapodVersion, runMode);
	}
	
	@RequestMapping(value = "/synchronizeMetadata", method = RequestMethod.GET)
	public Datapod synchronizeMetadata(@RequestParam(value = "uuid") String datapodUuid,
					@RequestParam(value = "version")String datapodVersion,
					@RequestParam(value = "type", required = false) String type,
					@RequestParam(value = "action", required = false) String action,	    
					@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return datapodServiceImpl.synchronizeMetadata(datapodUuid, datapodVersion, runMode);
	}
	
	@RequestMapping(value = "/getAttrHistogram", method = RequestMethod.GET)
	public List<Map<String, Object>> getAttrHistogram(@RequestParam(value = "uuid") String datapodUuid,
					@RequestParam(value = "version")String datapodVersion,
					@RequestParam(value = "attributeId")String attributeId,
					@RequestParam(value = "type", required = false) String type,
					@RequestParam(value = "action", required = false) String action,	    
					@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return datapodServiceImpl.getAttrHistogram(datapodUuid, datapodVersion, attributeId, 10, runMode);
	}
	
	@RequestMapping(value = "/getFormulaValues", method = RequestMethod.GET)
	public List<Map<String, Object>> getFormulaValues(@RequestParam(value = "uuid") String formulaUuid,
					@RequestParam(value = "version", required = false) String formulaVersion,
					@RequestParam(value = "type", required = false) String type,
					@RequestParam(value = "action", required = false) String action,	    
					@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return datapodServiceImpl.getFormulaValues(formulaUuid, formulaVersion, runMode);
	}
}
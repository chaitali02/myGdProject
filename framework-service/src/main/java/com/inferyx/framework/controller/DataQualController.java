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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.DQIntelligence;
import com.inferyx.framework.domain.DQRecExec;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataQualExecServiceImpl;
import com.inferyx.framework.service.DataQualGroupExecServiceImpl;
import com.inferyx.framework.service.DataQualGroupServiceImpl;
import com.inferyx.framework.service.DataQualServiceImpl;
import com.inferyx.framework.service.TaskHolder;

/**
 * @author joy
 *
 */
@RestController
@RequestMapping(value = "/dataqual")
public class DataQualController {
	@Autowired
	DataQualServiceImpl dataQualServiceImpl;
	@Autowired
	DataQualGroupServiceImpl dataQualGroupServiceImpl;
	@Autowired
	DataQualExecServiceImpl dataQualExecServiceImpl;
	@Autowired
	DataQualGroupExecServiceImpl dataQualGroupExecServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	

	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public DataQualExec execute(@RequestParam("uuid") String dataQualUUID, 
			@RequestParam("version") String dataQualVersion,
			@RequestBody(required=false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		
//		DataQual dataQual = (DataQual) commonServiceImpl.getOneByUuidAndVersion(dataQualUUID, dataQualVersion, MetaType.dq.toString(), "N");
		
		DataQualExec dataQualExec = null;
//		if(dataQual.getAutoFlag().equalsIgnoreCase("N")) {
			dataQualExec = dataQualServiceImpl.create(dataQualUUID, dataQualVersion, execParams, null, null, null, runMode);
			dataQualExec = (DataQualExec) dataQualServiceImpl.parse(dataQualExec.getUuid(), dataQualExec.getVersion(), null, null, null, null, runMode);
			List<FutureTask<TaskHolder>> taskList = new ArrayList<FutureTask<TaskHolder>>();
			dataQualExec = dataQualServiceImpl.execute(metaExecutor, dataQualExec, taskList, null, runMode);
			commonServiceImpl.completeTaskThread(taskList);
//		} else {
//			MetaIdentifier dqDependsOn = dataQual.getDependsOn().getRef();
//			dataQualExec = dataQualServiceImpl.createCustom(dqDependsOn.getUuid(), dqDependsOn.getVersion(), execParams, null, null, null, runMode);
//			dataQualExec = (DataQualExec) dataQualServiceImpl.parseCustom(dataQualExec.getUuid(), dataQualExec.getVersion(), dataQual.getFilterInfo(), null, null, null, null, runMode);
//			dqRecOperator.genIntelligence(dataQualExec, null, runMode);
//		}
		return dataQualExec;
	}

	@RequestMapping(value = "/executeGroup", method = RequestMethod.POST)
	public MetaIdentifier executeGroup(@RequestParam("uuid") String dataQualGroupUUID,
			@RequestParam("version") String dataQualGroupVersion,
			@RequestBody(required=false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		DataQualGroupExec dataQualGroupExec = dataQualGroupServiceImpl.create(dataQualGroupUUID, dataQualGroupVersion, execParams, null, null, null, RunMode.ONLINE);
		dataQualGroupExec = dataQualGroupServiceImpl.parse(dataQualGroupExec.getRef(MetaType.dqgroupExec), null, null, null, null, runMode);
		return dataQualGroupServiceImpl.execute(dataQualGroupUUID, dataQualGroupVersion, execParams, dataQualGroupExec, runMode);
	}

	@RequestMapping(value = "/getResults", method = RequestMethod.GET)
	public List<Map<String, Object>> getResults(@RequestParam("uuid") String dataQualExecUUID, 
			@RequestParam("version") String dataQualExecVersion,
			@RequestParam(value="offset", defaultValue="0") int offset, 
			@RequestParam(value="limit", defaultValue="200") int limit,
			@RequestParam(value="sortBy", required=false) String sortBy,
			@RequestParam(value="order", required=false) String order,
			@RequestParam(value="requestId") String requestId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return dataQualServiceImpl.getDQResults(dataQualExecUUID, dataQualExecVersion,offset,limit,sortBy,order,requestId, runMode);
	}
	
	@RequestMapping(value = "/getDataQualExecByDataqual", method = RequestMethod.GET, params = {"dataQualUUID"})
    public List<DataQualExec> getDataQualExecByDataqual(@RequestParam("dataQualUUID") String dataQualUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {        
        return dataQualExecServiceImpl.findDataQualExecByDataqual(dataQualUUID);
    }
	
	@RequestMapping(value = "/getDataQualExecByDataqual", method = RequestMethod.GET, params = {"dataQualUUID", "startDate", "endDate"}	)
    public List<DataQualExec> getDataQualExecByDataqual(@RequestParam("dataQualUUID") String dataQualUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {        
        return dataQualExecServiceImpl.findDataQualExecByDataqual(dataQualUUID, startDate, endDate, type, action);
    }
    
    @RequestMapping(value = "/getdqGroupExecBydqGroup", method = RequestMethod.GET)
    public List<DataQualGroupExec> getdqGroupExecBydqGroup(@RequestParam("dqGroupUUID") String dqGroupUUID,
    		@RequestParam("dqGroupVersion") String dqGroupVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {        
        return dataQualGroupExecServiceImpl.finddqGroupExecBydqGroup(dqGroupUUID,dqGroupVersion);
    }
    
    @RequestMapping(value = "/getdqExecBydqGroupExec", method = RequestMethod.GET)
    public List<DataQualExec> getdqExecBydqGroupExec(@RequestParam("dataQualGroupExecUuid") String dataQualGroupExecUuid,
    		@RequestParam("dataQualGroupExecVersion") String dataQualGroupExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {        
        return dataQualExecServiceImpl.getdqExecBydqGroupExec(dataQualGroupExecUuid,dataQualGroupExecVersion);
    }
    
    @RequestMapping(value = "/getdqExecByDatapod", method = RequestMethod.GET, params = {"datapodUUID"})
    public List<DataQualExec> getdqExecByDatapod(@RequestParam("datapodUUID") String datapodUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, ParseException {
        return dataQualExecServiceImpl.finddqExecByDatapod(datapodUUID,type);
    }
    
    @RequestMapping(value = "/getdqExecByDatapod", method = RequestMethod.GET, params = {"datapodUUID", "startDate", "endDate"}	)
    public List<DataQualExec> getdqExecByDatapod(@RequestParam("datapodUUID") String datapodUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws JsonProcessingException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
        return dataQualExecServiceImpl.finddqExecByDatapod(datapodUUID, startDate, endDate, type);
    }

    @RequestMapping(value="/setStatus", method= RequestMethod.PUT)
   	public boolean setStatus(@RequestParam("uuid") String uuid, 
   			@RequestParam("version") String version,
   			@RequestParam("status") String status,
   			@RequestParam("type") String type,
   			@RequestParam(value = "action", required = false) String action) throws Exception {
   		try {
   			commonServiceImpl.setStatus(type,uuid,version,status);
   		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
   	}
    
    @RequestMapping(value="/restart", method= RequestMethod.POST)
   	public boolean restart(@RequestParam("uuid") String execUuid, 
   			@RequestParam("version") String execVersion,
   			@RequestParam("type") String type,
   			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {   			
   			try {
   				RunMode runMode = Helper.getExecutionMode(mode);
   				if(type.equalsIgnoreCase(MetaType.dqExec.toString())){
   	   				dataQualServiceImpl.restart(type, execUuid, execVersion, null, runMode);
   	   			} else if(type.equalsIgnoreCase(MetaType.dqrecExec.toString())){
   	   				dataQualServiceImpl.restartDqRec(type, execUuid, execVersion, null, runMode);
   	   			} else{
   	   				dataQualGroupServiceImpl.restart(type, execUuid, execVersion, runMode);
   	   			}
   			}catch (Exception e) {
   				e.printStackTrace();
   				return false;
   			}
   			return true;
   	}	
    
    
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public HttpServletResponse download(@RequestParam(value = "dataQualExecUUID") String dataQualExecUUID,
			@RequestParam(value = "dataQualExecVersion") String dataQualExecVersion,
			@RequestParam(value = "resultType") String resultType,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestParam(value = "rows", defaultValue = "200") int rows,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode,
			HttpServletResponse response,
			@RequestParam(value = "layout", required = false) Layout layout) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		dataQualServiceImpl.download(dataQualExecUUID, dataQualExecVersion, format, null, 0, rows, response, rows, null,
				null, null, runMode, resultType, layout);
		return null;
	}
	
	@RequestMapping(value="/getSummary",method=RequestMethod.GET)
	public List<Map<String, Object>> getSummary(@RequestParam(value= "dataQualExecUUID") String dqExecUuid, 
	    		@RequestParam(value= "dataQualExecVersion") String dqExecVersion,  
				@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception
	    		{
			RunMode runMode = Helper.getExecutionMode(mode);
			return dataQualServiceImpl.getSummary(dqExecUuid, dqExecVersion, runMode);
	}
	
	@RequestMapping(value = "/getResultDetail", method = RequestMethod.GET)
	public List<Map<String, Object>> getResultDetail(@RequestParam("uuid") String execUuid,
			@RequestParam("version") String execVersion,
			@RequestParam(value = "offset", defaultValue = "0") int offset,
			@RequestParam(value = "limit", defaultValue = "200") int limit,
			@RequestParam(value = "sortBy", required = false) String sortBy,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "requestId", required = false) String requestId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return dataQualServiceImpl.getResultDetails(execUuid, execVersion, offset, limit, sortBy, order, requestId,
				runMode);
	}
	
	@RequestMapping(value = "/getResultSummary", method = RequestMethod.GET)
	public List<Map<String, Object>> getResultSummary(@RequestParam(value = "uuid") String execUuid,
			@RequestParam(value = "version") String execVersion,
			@RequestParam(value = "offset", defaultValue = "0") int offset,
			@RequestParam(value = "limit", defaultValue = "200") int limit,
			@RequestParam(value = "sortBy", required = false) String sortBy,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "requestId", required = false) String requestId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return dataQualServiceImpl.getResultSummary(execUuid, execVersion, offset, limit, sortBy, order, requestId, runMode);
	}
	
	@RequestMapping(value = "/createDQRuleForDatapod", method = RequestMethod.GET)
	public List<BaseEntity> createDQRuleForDatapod(
			@RequestParam(value = "uuid") String datapodUuid,
			@RequestParam(value = "version") String datapodVersion,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return dataQualServiceImpl.createDQRuleForDatapod(datapodUuid,datapodVersion, runMode);
	}
	
	@RequestMapping(value = "/genIntelligence", method = RequestMethod.GET)
	public List<DQIntelligence> genIntelligence(@RequestParam(value = "uuid") String datapodUuid,
			@RequestParam(value = "version", required = false) String datapodVersion,
			@RequestParam(value = "samplePercent", required = false) String samplePercent,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception{
		RunMode runMode = Helper.getExecutionMode(mode);
		DQRecExec dqRecExec = dataQualServiceImpl.createCustom(datapodUuid, datapodVersion, null, null, null, null, runMode, samplePercent);
		dqRecExec = (DQRecExec) dataQualServiceImpl.parseCustom(dqRecExec.getUuid(), dqRecExec.getVersion(), null, null, null, null, null, runMode);
		dqRecExec = dataQualServiceImpl.genRecommendation(dqRecExec, null, runMode);
		return dqRecExec.getIntelligenceResult();
	}
	
	@RequestMapping(value = "/generateDq", method = RequestMethod.POST)
	public void generateDq(@RequestParam(value = "uuid") String dqRecExecUuid,
			@RequestParam(value = "version", required = false) String dqRecExecVersion,
			@RequestBody List<DQIntelligence> checkTypeList,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception{
		RunMode runMode = Helper.getExecutionMode(mode);
		dataQualServiceImpl.generateDq(dqRecExecUuid, dqRecExecVersion, checkTypeList, runMode);
	}
	
	@RequestMapping(value="/getDqStats",method=RequestMethod.GET)
	public List<Map<String, Object>> getDqStats(
			@RequestParam(value = "uuid") String datapodUuid,
			@RequestParam(value = "version", required = false) String datapodVersion,
			@RequestParam(value = "period") String period) throws Exception
	    		{
			return dataQualServiceImpl.getDqStats(datapodUuid, datapodVersion, period);
	}
}

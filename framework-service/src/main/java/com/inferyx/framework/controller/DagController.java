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

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.DagStatusHolder;
import com.inferyx.framework.domain.DagWithParamsHolder;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.DagExecServiceImpl;
import com.inferyx.framework.service.DagServiceImpl;
import com.inferyx.framework.service.RegisterService;



@RestController
@RequestMapping(value="/dag")
public class DagController {
	
	@Autowired
	RegisterService registerService;	

	@Autowired private DagServiceImpl dagServiceImpl;
	@Autowired private DagExecServiceImpl dagExecServiceImpl;
	
	ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
	
	//Map<Key, DataFrame> dfMap = new HashMap<>();
	Integer id = 0;
	
	@RequestMapping(value="/dagWithParams/dagUUID/{uuid}", method=RequestMethod.POST)
    public MetaIdentifierHolder submitDag(@PathVariable String uuid, 
    		@RequestBody ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return dagServiceImpl.submitDag(uuid, execParams, MetaType.dag.toString(), runMode);
	}
	
	@RequestMapping(value="/killTask/", method=RequestMethod.POST)
    public boolean kill (@RequestParam (value = "uuid", required = true) String uuid, 
    		@RequestParam (value = "version", required = true) String version,
			@RequestParam(value = "stageId", required = true) String stageId,
			@RequestParam(value = "taskId", required = true) String taskId) throws Exception {
		try {
			dagServiceImpl.kill(uuid, version, stageId, taskId);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;		
	}
	
	@RequestMapping(value="/dagWithParams", method=RequestMethod.POST)
    public MetaIdentifierHolder submitDag(@RequestBody DagWithParamsHolder dagWithParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return dagServiceImpl.submitDag(dagWithParams.getDag(), null, dagWithParams.getExecParams(), runMode);
	}
	
	@RequestMapping(value="/dagWithParamset", method=RequestMethod.POST)
    public List<MetaIdentifierHolder> submitDagWithParamSet(@PathVariable String uuid, 
    		@PathVariable String version, @RequestBody ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return dagServiceImpl.submitDagWithParamset(uuid, version, execParams, runMode);
	}
	
	@RequestMapping(value="/execute", method=RequestMethod.POST)
    public MetaIdentifierHolder execute(@RequestParam("uuid") String dagUuid, 
    		@RequestParam("version") String dagVersion, 
    		@RequestParam(name="type", required=false, defaultValue="dag") String type, 
    		@RequestBody(required=false)ExecParams execParams,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return dagServiceImpl.submitDag(dagUuid, dagVersion, execParams, type, runMode);
	}
	
	@RequestMapping(value = "/getDagByDagExec", method = RequestMethod.GET)
	public @ResponseBody String getDagByDagExec(@RequestParam("DagExecUuid") String DagExecUuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getDagByDagExec(DagExecUuid);
	}
	
	@RequestMapping(value = "/getStatusByDagExec", method = RequestMethod.GET)
	public @ResponseBody DagStatusHolder getStatusByDagExec(@RequestParam("DagExecUuid") String DagExecUuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getStatusByDagExec(DagExecUuid);
	}

	@RequestMapping(value = "/getStageStatus", method = RequestMethod.GET)
	public @ResponseBody com.inferyx.framework.domain.Status.Stage getStageStatus(@RequestParam("dagExecUuid") String dagExecUuid,
			@RequestParam("dagExecVersion") String dagExecVersion,
			@RequestParam("stageId") String stageId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return dagExecServiceImpl.getStageStatus(dagExecUuid,dagExecVersion,stageId);
	}

	@RequestMapping(value = "/getTaskStatus", method = RequestMethod.GET)
	public @ResponseBody com.inferyx.framework.domain.Status.Stage getTaskStatus(@RequestParam("dagExecUuid") String dagExecUuid,
			@RequestParam("dagExecVersion") String dagExecVersion,
			@RequestParam("stageId") String stageId,
			@RequestParam("taskId") String taskId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return dagExecServiceImpl.getTaskStatus(dagExecUuid,dagExecVersion,stageId,taskId);
	}

    @RequestMapping(value = "/killTask", method = RequestMethod.PUT)
	public String killTask(@RequestParam("uuid") String uuid,
			@RequestParam("version") String version,
			@RequestParam("stageId") String stageId,
			@RequestParam("taskId") String taskId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonParseException, JsonMappingException, IOException {
		return dagExecServiceImpl.kill(uuid, version, stageId, taskId);
	}
	
	@RequestMapping(value = "/fetchAllTaskThread", method = RequestMethod.GET)
	public List<String> allTaskThread(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonParseException, JsonMappingException, IOException {
		return dagExecServiceImpl.fetchAllTaskThread();
	}
	
	@RequestMapping(value="/setTaskPause",method= RequestMethod.PUT)
	public String setTaskPAUSE(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version,
			@RequestParam("stageId") String stageId,
			@RequestParam("taskId")String taskId) throws Exception{
		String ret;
		synchronized (uuid) {
			ret = objectWriter.writeValueAsString(dagExecServiceImpl.setTaskPause(uuid, version, stageId, taskId));
		}
		return ret;
	}
	
	@RequestMapping(value="/setTaskResume",method= RequestMethod.PUT)
	public String setTaskRESUME(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version,
			@RequestParam("stageId") String stageId,
			@RequestParam("taskId")String taskId) throws JsonProcessingException, JSONException, ParseException{
		String ret;
		synchronized (uuid) {		
			ret =  objectWriter.writeValueAsString(dagExecServiceImpl.setTaskResume(uuid, version, stageId, taskId));
		}
		return ret;
	}
    
	@RequestMapping(value="/setStagePause",method= RequestMethod.PUT)
	public String setStagePAUSE(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version,
			@RequestParam("stageId") String stageId) throws Exception{
		return objectWriter.writeValueAsString(dagExecServiceImpl.setStagePause(uuid, version, stageId));
	}
	
	@RequestMapping(value="/setStageResume",method= RequestMethod.PUT)
	public String setStageRESUME(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version,
			@RequestParam("stageId") String stageId) throws Exception{
		return objectWriter.writeValueAsString(dagExecServiceImpl.setStageResume(uuid, version, stageId));
	}
	
	@RequestMapping(value="/setDAGPause",method= RequestMethod.PUT)
	public String setDAGPAUSE(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version,
			@RequestParam("stageId") String stageId) throws Exception{
		return objectWriter.writeValueAsString(dagExecServiceImpl.setDAGPause(uuid, version));
	}
	
	@RequestMapping(value="/setDAGResume",method= RequestMethod.PUT)
	public String setDAGRESUME(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version,
			@RequestParam("stageId") String stageId) throws Exception{
		
		return objectWriter.writeValueAsString(dagExecServiceImpl.setDAGResume(uuid, version));
	}
	
	@RequestMapping(value="/setStatus", method= RequestMethod.PUT)
	public String setStatus(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version,
			@RequestParam(value="stageId", required = false) String stageId,
			@RequestParam(value="taskId", required = false)String taskId,
			@RequestParam(value="status")String status) throws JsonProcessingException, Exception {
		
		return dagExecServiceImpl.setStatusList(uuid, version, stageId, taskId, status);
	}
	@RequestMapping(value="/restart", method= RequestMethod.POST)
	public Boolean restart(@RequestParam("uuid") String dagExecUuid, 
			@RequestParam("version") String dagExecVersion,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="BATCH") String mode) throws Exception {
		try {
			RunMode runMode = Helper.getExecutionMode(mode);
			dagServiceImpl.restart(dagExecUuid,dagExecVersion, runMode);
		}catch (Exception e) {
			e.printStackTrace();
		return false;
		}
		return true;			
	}	
	@RequestMapping(value = "/getDagTemplates", method = RequestMethod.GET)
	public List<BaseEntity> getDagTemplates(@RequestParam("type") String type,
											@RequestParam(value = "action", required = false) String action,
											@RequestParam(value = "templateFlg", required = false, defaultValue = "Y") String templateFlg) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException{
		return dagServiceImpl.getDagTemplates(type, templateFlg);
	}
}
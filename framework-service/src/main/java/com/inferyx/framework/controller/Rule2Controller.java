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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSetHolder;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.Rule2ServiceImpl;
import com.inferyx.framework.service.TaskHolder;

@RestController
@RequestMapping(value = "/rule2")
public class Rule2Controller {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	Rule2ServiceImpl rule2ServiceImpl;

	static final Logger logger = Logger.getLogger(Rule2Controller.class);
	
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public List<MetaIdentifier> execute(@RequestParam("uuid") String ruleUUID,
			@RequestParam("version") String ruleVersion,
		    @RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return null;
	}
	
	@RequestMapping(value = "/executeGroup", method = RequestMethod.POST)
	public MetaIdentifier executeGroup(@RequestParam("uuid") String ruleGroupUUID,
			@RequestParam("version") String ruleGroupVersion, 
			@RequestBody(required=false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		
		return null;
	}
	
	@RequestMapping(value = "/getResults", method = RequestMethod.GET)
	public List<Map<String, Object>> getResults(@RequestParam("uuid") String ruleExecUUID,@RequestParam("version") String ruleExecVersion,
			@RequestParam(value="offset", defaultValue="0") int offset, 
			@RequestParam(value="limit", defaultValue="200") int limit,
			@RequestParam(value="sortBy", required=false) String sortBy,
			@RequestParam(value="order", required=false) String order, 
			@RequestParam(value="requestId") String requestId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return null;
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
	public boolean restart(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
			RunMode runMode = Helper.getExecutionMode(mode);
			try {
				List<FutureTask<TaskHolder>> taskList = new ArrayList<FutureTask<TaskHolder>>();
				if(type.equalsIgnoreCase(MetaType.rule2Exec.toString())){
					
				}
				else{
					
				}
			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
	}	
	
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public MetaIdentifier create(@RequestParam("uuid") String rule2UUID,
			@RequestParam("version") String rule2Version,
		    @RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		RuleExec ruleExec = rule2ServiceImpl.create(rule2UUID, rule2Version, null, null, execParams, null, null);
		return ruleExec.getRef(MetaType.ruleExec);
	}
	
	
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public MetaIdentifier submit(@RequestParam("uuid") String ruleExecUUID,
			@RequestParam("version") String ruleExecVersion,
		    @RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) {
		RunMode runMode = Helper.getExecutionMode(mode);
		RuleExec ruleExec = null;
		List<FutureTask<TaskHolder>> taskList = new ArrayList<FutureTask<TaskHolder>>();
		if (execParams != null) {
			if (execParams.getParamInfo() != null && !execParams.getParamInfo().isEmpty()) {
				logger.info(" ExecParams has paramInfo ");
				for (ParamSetHolder paramSetHolder : execParams.getParamInfo()) {
					execParams.setCurrParamSet(paramSetHolder);
				}
			} else if (execParams.getParamListInfo() != null && !execParams.getParamListInfo().isEmpty()) {
				logger.info(" ExecParams has paramListInfo ");
				for (ParamListHolder paramListHolder : execParams.getParamListInfo()) {
					execParams.setParamListHolder(paramListHolder);
				}
			}
		} 
		
		try {
			ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExecUUID, ruleExecVersion, MetaType.ruleExec.toString());
			ruleExec = rule2ServiceImpl.parse(ruleExec.getUuid(), ruleExec.getVersion(), null, null, null, null, runMode);
			ruleExec = rule2ServiceImpl.execute(null, ruleExec, taskList, execParams, runMode);
		} catch (Exception e) {
			try {
				commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.FAILED);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		commonServiceImpl.completeTaskThread(taskList);
		return ruleExec.getRef(MetaType.ruleExec);
	}

}
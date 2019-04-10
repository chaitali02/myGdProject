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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSetHolder;
import com.inferyx.framework.domain.BusinessRule;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.operator.Rule2Operator;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.RegisterService;
import com.inferyx.framework.service.Rule2ServiceImpl;
import com.inferyx.framework.service.RuleGroupServiceImpl;
import com.inferyx.framework.service.TaskHolder;

@RestController
@RequestMapping(value = "/rule2")
public class Rule2Controller {
	
	@Autowired Rule2Operator rule2Operator;
	@Autowired Rule2ServiceImpl rule2ServiceImpl;
	@Autowired RuleGroupServiceImpl ruleGroupServiceImpl;
	@Autowired RegisterService registerService;
	@Autowired CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	
	static final Logger logger = Logger.getLogger(Rule2Controller.class);
	
	@RequestMapping(value = "/getRule2Sql", method = RequestMethod.POST)
	public String getRule2Sql(@RequestBody BusinessRule rule,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		return rule2Operator.generateDetailSql(rule, null, null, null, null, usedRefKeySet, null, RunMode.ONLINE,null, null, false, new HashMap<String, String>()).get(0);
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public MetaIdentifier create(@RequestParam("uuid") String ruleUUID,
			@RequestParam("version") String ruleVersion,
		    @RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		RuleExec ruleExec = rule2ServiceImpl.create(ruleUUID, ruleVersion, null, null, execParams, null, null, RunMode.ONLINE);
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
			ruleExec = rule2ServiceImpl.execute(metaExecutor, ruleExec, taskList, execParams, runMode);
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

	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public List<MetaIdentifier> execute(@RequestParam("uuid") String ruleUUID,
			@RequestParam("version") String ruleVersion,
		    @RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		return rule2ServiceImpl.prepareRule2(ruleUUID, ruleVersion, execParams, null, runMode);
	}
			
	@RequestMapping(value = "/executeGroup", method = RequestMethod.POST)
	public MetaIdentifier executeGroup(@RequestParam("uuid") String ruleGroupUUID,
			@RequestParam("version") String ruleGroupVersion, 
			@RequestBody(required=false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		RuleGroupExec ruleGroupExec = null;
		ruleGroupExec = ruleGroupServiceImpl.create(ruleGroupUUID, ruleGroupVersion, execParams, null, null, null, RunMode.ONLINE);
		ruleGroupExec = ruleGroupServiceImpl.parse(ruleGroupExec.getUuid(), ruleGroupExec.getVersion(), null, null, null, null, runMode);
		return ruleGroupServiceImpl.execute(ruleGroupUUID, ruleGroupVersion, execParams, ruleGroupExec, runMode);
	}
	
	@RequestMapping(value = "/createGroup", method = RequestMethod.POST)
	public MetaIdentifier createGroup(@RequestParam("uuid") String ruleGroupUUID,
			@RequestParam("version") String ruleGroupVersion, 
			@RequestBody(required=false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		RuleGroupExec ruleGroupExec = ruleGroupServiceImpl.create(ruleGroupUUID, ruleGroupVersion, execParams, null, null, null, RunMode.ONLINE);
		return ruleGroupExec.getRef(MetaType.rulegroupExec);
	}
	
	@RequestMapping(value = "/submitGroup", method = RequestMethod.POST)
	public MetaIdentifier submitGroup(@RequestParam("uuid") String ruleGroupExecUUID,
			@RequestParam("version") String ruleGroupExecVersion, 
			@RequestBody(required=false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		RuleGroupExec ruleGroupExec = null;
		try {
			ruleGroupExec = (RuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(ruleGroupExecUUID, ruleGroupExecVersion, MetaType.rulegroupExec.toString());
			ruleGroupExec = ruleGroupServiceImpl.parse(ruleGroupExecUUID, ruleGroupExecVersion, null, null, null, null, runMode);
			ruleGroupServiceImpl.execute(ruleGroupExec.getDependsOn().getRef().getUuid(), ruleGroupExec.getDependsOn().getRef().getVersion(), execParams, ruleGroupExec, runMode);
		} catch (Exception e) {
			try {
				commonServiceImpl.setMetaStatus(ruleGroupExec, MetaType.rulegroupExec, Status.Stage.FAILED);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return ruleGroupExec.getRef(MetaType.rulegroupExec);
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
		return rule2ServiceImpl.getRule2Results(ruleExecUUID,ruleExecVersion,offset,limit,sortBy,order,requestId, runMode);
	}
	
	@RequestMapping(value = "/fetchRuleGroupExec", method = RequestMethod.GET)
	public RuleGroupExec fetchRuleGroupExec(@RequestParam("ruleGroupExecUUID") String ruleGroupExecUUID,
			@RequestParam("ruleGroupExecVersion") String ruleGroupExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return ruleGroupServiceImpl.fetchRuleGroupExec(ruleGroupExecUUID, ruleGroupExecVersion);
	}
	
	@RequestMapping(value = "/getRuleExecByRule2", method = RequestMethod.GET,  params = {"ruleUuid"})
	public @ResponseBody String getRuleExecByRule2(@RequestParam("ruleUuid") String ruleUuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getRuleExecByRule(ruleUuid);
	}
	
	@RequestMapping(value = "/getRuleExecByRule2", method = RequestMethod.GET,  params = {"ruleUuid", "startDate", "endDate"})
	public @ResponseBody List<RuleExec> getRuleExecByRule2(@RequestParam("ruleUuid") String ruleUuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return registerService.getRuleExecByRule(ruleUuid, startDate, endDate, type, action);
	}
	
	@RequestMapping(value = "/getRuleExecByRGExec", method = RequestMethod.GET)
	public @ResponseBody String getRuleExecByRGExec(@RequestParam("ruleGroupExecUuid") String ruleGroupExecUuid,
			@RequestParam("ruleGroupExecVersion") String ruleGroupExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getRuleExecByRGExec(ruleGroupExecUuid, ruleGroupExecVersion);
	}
	

	@RequestMapping(value = "/getRuleGroupExecByRuleGroup", method = RequestMethod.GET)
	public @ResponseBody String getRuleGroupExecByRuleGroup(@RequestParam("ruleGroupUUID") String ruleGroupUuid,
			@RequestParam("ruleGroupVersion") String ruleGroupVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getRuleGroupExecByRuleGroup(ruleGroupUuid, ruleGroupVersion);
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
				if(type.equalsIgnoreCase(MetaType.ruleExec.toString())){
					rule2ServiceImpl.restart(type,uuid,version, taskList, metaExecutor, null, runMode);
					commonServiceImpl.completeTaskThread(taskList);
				}
				else{
					ruleGroupServiceImpl.restart(type,uuid,version, runMode);
				}
			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
	}	
	
	
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public HttpServletResponse download(@RequestParam(value = "ruleExecUUID") String ruleExecUUID,
			@RequestParam(value = "ruleExecVersion") String ruleExecVersion,
			@RequestParam(value = "resultType") String resultType,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestParam(value = "rows", defaultValue = "200") int rows,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode,
			HttpServletResponse response,
			@RequestParam(value = "layout", required = false) Layout layout) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		rule2ServiceImpl.download(ruleExecUUID, ruleExecVersion, format, null, 0, rows
				, response, rows, null, null, null,
				runMode,resultType, layout);
		return null;
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
		return rule2ServiceImpl.getResultDetail(execUuid, execVersion, offset, limit, sortBy, order, requestId,
				runMode);
	}

	@RequestMapping(value = "/getResultSummary", method = RequestMethod.GET)
	public List<Map<String, Object>> getResultSummary(@RequestParam("uuid") String execUuid,
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
		return rule2ServiceImpl.getResultSummary(execUuid, execVersion, offset, limit, sortBy, order, requestId,
				runMode);
	}
	
	
}
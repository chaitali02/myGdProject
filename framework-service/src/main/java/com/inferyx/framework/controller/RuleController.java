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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONException;
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
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.ParamSetHolder;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.operator.RuleOperator;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.RegisterService;
import com.inferyx.framework.service.RuleExecServiceImpl;
import com.inferyx.framework.service.RuleGroupServiceImpl;
import com.inferyx.framework.service.RuleServiceImpl;
import com.inferyx.framework.service.TaskHolder;

@RestController
@RequestMapping(value = "/rule")
public class RuleController {
	
	@Autowired RuleOperator ruleOperator;
	@Autowired RuleServiceImpl ruleServiceImpl;
	@Autowired RuleGroupServiceImpl ruleGroupServiceImpl;
	@Autowired RegisterService registerService;
	@Autowired CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	
	@RequestMapping(value = "/getRuleSql", method = RequestMethod.POST)
	public String getRuleSql(@RequestBody Rule rule,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		return ruleOperator.generateSql(rule, null, null, usedRefKeySet, null, Mode.ONLINE);
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public MetaIdentifier create(@RequestParam("uuid") String ruleUUID,
			@RequestParam("version") String ruleVersion,
		    @RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		RuleExec ruleExec = ruleServiceImpl.create(ruleUUID, ruleVersion, null, null, execParams, null, null);
		return ruleExec.getRef(MetaType.ruleExec);
	}
	
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public MetaIdentifier submit(@RequestParam("uuid") String ruleExecUUID,
			@RequestParam("version") String ruleExecVersion,
		    @RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) {
		Mode runMode = Helper.getExecutionMode(mode);
		RuleExec ruleExec = null;
		List<FutureTask<TaskHolder>> taskList = new ArrayList<FutureTask<TaskHolder>>();
		if (execParams != null && execParams.getParamInfo() != null && !execParams.getParamInfo().isEmpty()) {
			for (ParamSetHolder paramSetHolder : execParams.getParamInfo()) {
				execParams.setParamSetHolder(paramSetHolder);
			}
		} 
		
		try {
			ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExecUUID, ruleExecVersion, MetaType.ruleExec.toString());
			ruleExec = ruleServiceImpl.parse(ruleExec.getUuid(), ruleExec.getVersion(), null, null, null, runMode);
			ruleExec = ruleServiceImpl.execute(ruleExec.getDependsOn().getRef().getUuid(), ruleExec.getDependsOn().getRef().getVersion(), metaExecutor, ruleExec, null, taskList, execParams, runMode);
		} catch (Exception e) {
			try {
				commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.Failed);
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
		Mode runMode = Helper.getExecutionMode(mode);
		List<FutureTask<TaskHolder>> taskList = new ArrayList<FutureTask<TaskHolder>>();
		RuleExec ruleExec = null;
		List<MetaIdentifier> ruleExecMetaList = new ArrayList<>();
		MetaIdentifierHolder ruleExecMeta = new MetaIdentifierHolder();
		MetaIdentifier ruleExecInfo = new MetaIdentifier(MetaType.rule, ruleUUID, ruleVersion);
		ruleExecMeta.setRef(ruleExecInfo);
		try {
			if (execParams != null && execParams.getParamInfo() != null && !execParams.getParamInfo().isEmpty()) {
				for (ParamSetHolder paramSetHolder : execParams.getParamInfo()) {
					MetaIdentifier ref = paramSetHolder.getRef();
					ref.setType(MetaType.paramset);
					paramSetHolder.setRef(ref);
					execParams.setParamSetHolder(paramSetHolder);
					ruleExec = ruleServiceImpl.create(ruleUUID, ruleVersion, null, null, execParams, null, null);			
					ruleExec = ruleServiceImpl.parse(ruleExec.getUuid(), ruleExec.getVersion(), null, null, null, runMode);
					ruleExec = ruleServiceImpl.execute(ruleUUID, ruleVersion, metaExecutor, ruleExec, null, taskList, execParams, runMode);
				}
			} else {
				ruleExec = ruleServiceImpl.create(ruleUUID, ruleVersion, null, null, execParams, null, null);			
				ruleExec = ruleServiceImpl.parse(ruleExec.getUuid(), ruleExec.getVersion(), null, null, null, runMode);
				ruleExec = ruleServiceImpl.execute(ruleUUID, ruleVersion, metaExecutor, ruleExec, null, taskList, execParams, runMode);
			}
		} catch (Exception e) {
			try {
				commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.Failed);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		ruleExecInfo = new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion());
		ruleExecMetaList.add(ruleExecInfo);
		
		commonServiceImpl.completeTaskThread(taskList);
		return ruleExecMetaList;
	}
			
	@RequestMapping(value = "/executeGroup", method = RequestMethod.POST)
	public MetaIdentifier executeGroup(@RequestParam("uuid") String ruleGroupUUID,
			@RequestParam("version") String ruleGroupVersion, 
			@RequestBody(required=false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		Mode runMode = Helper.getExecutionMode(mode);
		RuleGroupExec ruleGroupExec = null;
		ruleGroupExec = ruleGroupServiceImpl.create(ruleGroupUUID, ruleGroupVersion, execParams, null, null, null);
		ruleGroupExec = ruleGroupServiceImpl.parse(ruleGroupExec.getUuid(), ruleGroupExec.getVersion(), null, null, null, runMode);
		return ruleGroupServiceImpl.execute(ruleGroupUUID, ruleGroupVersion, execParams, ruleGroupExec, runMode);
	}
	
	@RequestMapping(value = "/createGroup", method = RequestMethod.POST)
	public MetaIdentifier createGroup(@RequestParam("uuid") String ruleGroupUUID,
			@RequestParam("version") String ruleGroupVersion, 
			@RequestBody(required=false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		RuleGroupExec ruleGroupExec = ruleGroupServiceImpl.create(ruleGroupUUID, ruleGroupVersion, execParams, null, null, null);
		return ruleGroupExec.getRef(MetaType.rulegroupExec);
	}
	
	@RequestMapping(value = "/submitGroup", method = RequestMethod.POST)
	public MetaIdentifier submitGroup(@RequestParam("uuid") String ruleGroupExecUUID,
			@RequestParam("version") String ruleGroupExecVersion, 
			@RequestBody(required=false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode) throws Exception {
		Mode runMode = Helper.getExecutionMode(mode);
		RuleGroupExec ruleGroupExec = null;
		try {
			ruleGroupExec = (RuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(ruleGroupExecUUID, ruleGroupExecVersion, MetaType.rulegroupExec.toString());
			ruleGroupExec = ruleGroupServiceImpl.parse(ruleGroupExecUUID, ruleGroupExecVersion, null, null, null, runMode);
			ruleGroupServiceImpl.execute(ruleGroupExec.getDependsOn().getRef().getUuid(), ruleGroupExec.getDependsOn().getRef().getVersion(), execParams, ruleGroupExec, runMode);
		} catch (Exception e) {
			try {
				commonServiceImpl.setMetaStatus(ruleGroupExec, MetaType.rulegroupExec, Status.Stage.Failed);
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
		Mode runMode = Helper.getExecutionMode(mode);
		return ruleServiceImpl.getRuleResults(ruleExecUUID,ruleExecVersion,offset,limit,sortBy,order,requestId, runMode);
	}
	
	@RequestMapping(value = "/fetchRuleGroupExec", method = RequestMethod.GET)
	public RuleGroupExec fetchRuleGroupExec(@RequestParam("ruleGroupExecUUID") String ruleGroupExecUUID,
			@RequestParam("ruleGroupExecVersion") String ruleGroupExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return ruleGroupServiceImpl.fetchRuleGroupExec(ruleGroupExecUUID, ruleGroupExecVersion);
	}
	
	@RequestMapping(value = "/getRuleExecByRule", method = RequestMethod.GET,  params = {"ruleUuid"})
	public @ResponseBody String getRuleExecByRule(@RequestParam("ruleUuid") String ruleUuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getRuleExecByRule(ruleUuid);
	}
	
	@RequestMapping(value = "/getRuleExecByRule", method = RequestMethod.GET,  params = {"ruleUuid", "startDate", "endDate"})
	public @ResponseBody List<RuleExec> getRuleExecByRule(@RequestParam("ruleUuid") String ruleUuid,
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
			Mode runMode = Helper.getExecutionMode(mode);
			try {
				List<FutureTask<TaskHolder>> taskList = new ArrayList<FutureTask<TaskHolder>>();
				if(type.equalsIgnoreCase(MetaType.ruleExec.toString())){
					ruleServiceImpl.restart(type,uuid,version, taskList, metaExecutor, null, runMode);
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
	
	
	@RequestMapping(value="/download",method=RequestMethod.GET)
	public HttpServletResponse  download(@RequestParam(value= "ruleExecUUID") String ruleExecUUID, 
	    		@RequestParam(value= "ruleExecVersion") String ruleExecVersion,
	    		@RequestParam(value = "format", defaultValue="excel")String format,
				@RequestParam(value ="rows",defaultValue="1000") int rows,
				@RequestParam(value = "download", defaultValue="Y") String download,@RequestParam(value="offset", defaultValue="0") int offset, 
				@RequestParam(value="limit", defaultValue="200") int limit,
				@RequestParam(value="sortBy", required=false) String sortBy,@RequestParam(value="order", required=false) String order,
				@RequestParam(value = "type", required = false) String type,
				@RequestParam(value = "action", required = false) String action,
				@RequestParam(value="requestId",required = false) String requestId, 
				@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode, HttpServletResponse response) throws Exception
	    		{
			Mode runMode = Helper.getExecutionMode(mode);
			ruleServiceImpl.download(ruleExecUUID, ruleExecVersion,format,download,offset,limit,response,rows,sortBy,order,requestId, runMode);
	    	return null;
	   }
	
	@RequestMapping(value = "/getRuleExeByDatapod", method = RequestMethod.GET)
    public List<RuleExec> getdqExecByDatapod(@RequestParam("datapodUUID") String datapodUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, ParseException {
        return ruleServiceImpl.finddqExecByDatapod(datapodUUID,type);
  }

}
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
package com.inferyx.framework.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseEntityStatus;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Role;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Thread;
import com.inferyx.framework.domain.User;

import shaded.parquet.org.codehaus.jackson.JsonGenerationException;
import shaded.parquet.org.codehaus.jackson.map.JsonMappingException;

public class SystemServiceImpl {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	MetadataServiceImpl metadataServiceImpl;
	@Autowired
	SessionServiceImpl sessionServiceImpl;
	@Autowired
	DagExecServiceImpl dagExecServiceImpl;
	@Autowired
	private BatchExecServiceImpl btchServ;
	@Autowired
	ThreadPoolTaskExecutor stageExecutor;
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	ThreadPoolTaskExecutor dagExecutor;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	
	static final Logger logger = Logger.getLogger(SystemServiceImpl.class);
	/*public List<BaseEntityStatus> getActiveSession(String type, String appUuid, String userName, String startDate, String endDate, String tags, String active, String status, String role) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonGenerationException, JsonMappingException, IOException {
		List<BaseEntityStatus> activeSessionList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, "session", null, userName, startDate, endDate, tags, active, status);
		return getBaseEntityStatusList(activeSessionList, Helper.getStatus(status));
	}*/
	public List<BaseEntityStatus> getActiveSession(String type, String appUuid, String userName, String startDate, String endDate, String tags, String active, String status, String role) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonGenerationException, JsonMappingException, IOException {
		List<BaseEntityStatus> activeSessionList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, MetaType.session.toString(), null, userName, startDate, endDate, tags, active, status);
		if(status != null && !StringUtils.isBlank(status)) {
			return getBaseEntityLatestStatusList(activeSessionList, Helper.getStatus(status));
		}else
			return activeSessionList;
	}
	public List<BaseEntityStatus> getActiveJobByCriteria(String type, String appUuid, String userName, String startDate, String endDate, String tags, String active, String status, String role) throws ParseException, JsonGenerationException, JsonMappingException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException{
		//logger.info("status : "+status+"   Status.Stage.RUNNING: "+Status.Stage.RUNNING+"    Helper.getStatus(status): "+Helper.getStatus(status));
		if(type != null && !StringUtils.isBlank(type)) {
			List<BaseEntityStatus> RUNNINGExecList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, null, userName, startDate, endDate, tags, active, status);
			if(status != null && !StringUtils.isBlank(status))
				return getBaseEntityLatestStatusList(RUNNINGExecList, Helper.getStatus(status));
			else
				return RUNNINGExecList;
		}else {
			List<MetaType> metaExecList = MetaType.getMetaExecList();
			List<BaseEntityStatus> xRUNNINGExecList = new ArrayList<>();
			for(MetaType metaType : metaExecList) {
				List<BaseEntityStatus> RUNNINGExecList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, metaType.toString().toLowerCase(), null, userName, startDate, endDate, tags, active, status);
				if(status != null && !StringUtils.isBlank(status)) {
					List<BaseEntityStatus> newRUNNINGExecList = getBaseEntityLatestStatusList(RUNNINGExecList, Helper.getStatus(status));
					for(BaseEntityStatus baseEntityStatus : newRUNNINGExecList) {
						xRUNNINGExecList.add(baseEntityStatus);
					}
				}else 
					for(BaseEntityStatus baseEntityStatus : RUNNINGExecList) {
						xRUNNINGExecList.add(baseEntityStatus);
					}				
			}
			return xRUNNINGExecList;
		}
	}	
	public List<BaseEntityStatus> getBaseEntityLatestStatusList(List<BaseEntityStatus> baseEntityStatusList, Status.Stage status) throws JsonGenerationException, JsonMappingException, IOException{
		if(baseEntityStatusList != null && !baseEntityStatusList.isEmpty()) {
			List<BaseEntityStatus> newRUNNINGExecList = new ArrayList<>();
			for(BaseEntityStatus baseEntityStatus : baseEntityStatusList) {
				Status latestStatus = Helper.getLatestStatus(baseEntityStatus.getStatus());
				if(latestStatus != null)
					if(latestStatus.getStage().equals(status)) {
						List<Status> statusList = new ArrayList<>();
						statusList.add(latestStatus);
						baseEntityStatus.setStatus(statusList);
						newRUNNINGExecList.add(baseEntityStatus);
					}			
			}
			//logger.info(new ObjectMapper().writeValueAsString(RUNNINGExecList));
			//logger.info(new ObjectMapper().writeValueAsString(newRUNNINGExecList));
			return newRUNNINGExecList;
		}else
			return new ArrayList<>();
	}
	
	public boolean expireSession(String sessionId) throws JsonProcessingException, JSONException, ParseException {
		Session session = sessionServiceImpl.findSessionBySessionId(sessionId);
		if(session != null) {
			Status status = Helper.getLatestStatus(session.getStatusList());
			if(status.getStage().equals(Status.Stage.active)) {
				List<Status> statusList = session.getStatusList();
				statusList.add(new Status(Status.Stage.expired, new Date()));
				session.setStatusList(statusList);
				commonServiceImpl.save(MetaType.session.toString(), session);	
				return true;
			}else
				logger.info("Session alReady invalidated......."); return false;
		}else
			logger.info("Meta type \"Session\" instance is null......."); return false;
	}
	public String getRole() throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String role = null;
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(requestAttributes != null) {
			HttpServletRequest request = requestAttributes.getRequest();
			if(request != null) {
				HttpSession session = request.getSession(false);
				if(session != null){
					SessionContext sessionContext = (SessionContext) session.getAttribute("sessionContext");
					if(sessionContext != null) {
						MetaIdentifierHolder roleHolder = sessionContext.getRoleInfo();
						Role userRole = (Role) commonServiceImpl.getLatestByUuidWithoutAppUuid(roleHolder.getRef().getUuid(), MetaType.role.toString());
						role = userRole.getName();
						logger.info("Role: "+role);
						return role;					
					}
				}else
					logger.info("HttpSession is \""+null+"\"");
			}else
				logger.info("HttpServletResponse response is \""+null+"\"");
		}else
			logger.info("ServletRequestAttributes requestAttributes is \""+null+"\"");	
		return "";
	}
	public List<com.inferyx.framework.domain.Thread> getActiveThread() throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		List<com.inferyx.framework.domain.Thread> activeThreadList = new ArrayList<>();
		List<String> threadList = btchServ.fetchAllTaskThread();
		for(String name : threadList) {
			if(name.toLowerCase().contains(MetaType.dag.toString().toLowerCase()) 
					|| name.toLowerCase().contains(MetaType.dqExec.toString().toLowerCase())
					|| name.toLowerCase().contains(MetaType.dqgroupExec.toString().toLowerCase())
					|| name.toLowerCase().contains(MetaType.mapExec.toString().toLowerCase())
					|| name.toLowerCase().contains(MetaType.modelExec.toString().toLowerCase())
					|| name.toLowerCase().contains(MetaType.profileExec.toString().toLowerCase()) 
					|| name.toLowerCase().contains(MetaType.profilegroupExec.toString().toLowerCase())
					|| name.toLowerCase().contains(MetaType.ruleExec.toString().toLowerCase())
					|| name.toLowerCase().contains(MetaType.rulegroupExec.toString().toLowerCase())) {
				String[] threadDetails = name.split("_");
				if(threadDetails.length>0) {logger.info("thread name: "+name);
					Thread activeThread = new Thread();
					MetaType type = null;
					if(threadDetails[0].equalsIgnoreCase(MetaType.dag.toString()))
						type = Helper.getMetaType(MetaType.dagExec.toString());
					else	
						type = Helper.getMetaType(threadDetails[0]);
					String uuid = (threadDetails.length > 1 ? threadDetails[1] : "");
					String version = (threadDetails.length > 2  ? threadDetails[2] : "");
					activeThread.setName(uuid+((version != null && !StringUtils.isBlank(version)) ? "_" : "")+version);
					MetaIdentifier ref = new MetaIdentifier(type, uuid, version);
					activeThread.setExecInfo(new MetaIdentifierHolder(ref));
					Object metaObject = null;
					if(uuid != null && !StringUtils.isBlank(uuid))
						metaObject = commonServiceImpl.getLatestByUuidWithoutAppUuid(uuid, type.toString());
					else
						continue;
					if(metaObject != null) {
						@SuppressWarnings("unchecked")
						List<MetaIdentifierHolder> appInfo = (List<MetaIdentifierHolder>) Helper.getDomainClass(type).getMethod("getAppInfo").invoke(metaObject);
						activeThread.setAppInfo(appInfo);
						activeThreadList.add(activeThread);
					}
				}
			}
		}
		return activeThreadList;
	}
	public Map<Status.Stage, Integer> getJobCountByStatus(String type, String appUuid, String userName, String startDate, String endDate, String tags, String active, String status, String role) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonGenerationException, JsonMappingException, IOException {
		Map<Status.Stage, Integer> count = new HashMap<>();
		if(type != null && !StringUtils.isBlank(type)) {
			//logger.info("Checking count for meta "+type+"....");
			if(status != null && !StringUtils.isBlank(status)){
				List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, null, userName, startDate, endDate, tags, active, status);
				List<BaseEntityStatus> baseEntityLatestStatusList = getBaseEntityLatestStatusList(baseEntityStatusList, Helper.getStatus(status));
				count = getJobStatusCount(baseEntityLatestStatusList, count);
			}else {
				//logger.info("Checking count for meta "+type+"....");
				List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, null, userName, startDate, endDate, tags, active, status);
				count = getJobStatusCount(baseEntityStatusList, count);
			}
		}else {
			if(status != null && !StringUtils.isBlank(status)){
				List<MetaType> metaExecList = MetaType.getMetaExecList();
				for(MetaType metaType : metaExecList) {
					//logger.info("Checking count for meta "+metaType+"....");
					List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, metaType.toString(), null, userName, startDate, endDate, tags, active, status);
					List<BaseEntityStatus> baseEntityLatestStatusList = getBaseEntityLatestStatusList(baseEntityStatusList, Helper.getStatus(status));
					count = getJobStatusCount(baseEntityLatestStatusList, count);
				}
			}else{
				List<MetaType> metaExecList = MetaType.getMetaExecList();
				for(MetaType metaType : metaExecList) {
					//logger.info("Checking count for meta "+metaType+"....");
					List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, metaType.toString(), null, userName, startDate, endDate, tags, active, status);
					//List<BaseEntityStatus> baseEntityLatestStatusList = getBaseEntityStatusList(baseEntityStatusList, Helper.getStatus(status));
					count = getJobStatusCount(baseEntityStatusList, count);
				}
			}
		}		
		return count;
	}
	public Map<Status.Stage, Integer> getJobStatusCount(List<BaseEntityStatus> baseEntityStatusList, Map<Status.Stage, Integer> count){
		for(BaseEntityStatus baseEntityStatus : baseEntityStatusList) {
			Status latestStatus = Helper.getLatestStatus(baseEntityStatus.getStatus());
			if(latestStatus != null){
				Integer result = count.putIfAbsent(latestStatus.getStage(), 1);
				if(result != null) {
					Integer oldCount = count.get(latestStatus.getStage());
					if(oldCount != null) {
						count.put(latestStatus.getStage(), oldCount+1); 
						//logger.info("("+latestStatus.getStage()+", "+ (oldCount+1)+")" + " added to map.");
					}	
					else
						logger.info("No value is available for the key \""+latestStatus.getStage()+"\"");
				}else
					logger.info("("+latestStatus.getStage()+", 1)" + " added to map.");
			}					
		}
		return count;
	}
	public Map<MetaType, Integer> getJobCountByMeta(String type, String appUuid, String userName, String startDate, String endDate, String tags, String active, String status, String role) throws ParseException, JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Map<MetaType, Integer> count = new HashMap<>();
		if(type != null && !StringUtils.isBlank(type)) {
			if(status != null && !StringUtils.isBlank(status)){
				//logger.info("Checking count for meta "+metaType+"....");
				List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, null, userName, startDate, endDate, tags, active, status);
				if(baseEntityStatusList != null && (baseEntityStatusList.size()>0))					
					count.putIfAbsent(Helper.getMetaType(type), baseEntityStatusList.size());
			}else {
				List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, null, userName, startDate, endDate, tags, active, status);
				if(baseEntityStatusList != null && (baseEntityStatusList.size()>0))
					count.putIfAbsent(Helper.getMetaType(type), baseEntityStatusList.size());
			}
		}else {
			if(status != null && !StringUtils.isBlank(status)){
				List<MetaType> metaExecList = MetaType.getMetaExecList();
				for(MetaType metaType : metaExecList) {
					//logger.info("Checking count for meta "+metaType+"....");
					List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, metaType.toString(), null, userName, startDate, endDate, tags, active, status);
					if(baseEntityStatusList != null && (baseEntityStatusList.size()>0))					
						count.putIfAbsent(metaType, baseEntityStatusList.size());
					/*else
						count.putIfAbsent(metaType, 0);*/
				}
			}else{
				List<MetaType> metaExecList = MetaType.getMetaExecList();
				for(MetaType metaType : metaExecList) {
					//logger.info("Checking count for meta "+metaType+"....");
					List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, metaType.toString(), null, userName, startDate, endDate, tags, active, status);
					if(baseEntityStatusList != null && (baseEntityStatusList.size()>0))
						count.putIfAbsent(metaType, baseEntityStatusList.size());
					/*else
						count.putIfAbsent(metaType, 0);*/
				}
			}
		}		
		return count;
	}
	public Map<String, Integer> getJobCountByUser(String type, String appUuid, String userName, String startDate, String endDate, String tags, String active, String status, String role) throws ParseException, JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Map<String, Integer> count = new HashMap<>();
		if(type != null && !StringUtils.isBlank(type)) {
			if(status != null && !StringUtils.isBlank(status)){
				//logger.info("Checking count for meta "+type+"....");
				List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, null, userName, startDate, endDate, tags, active, status);
				if(baseEntityStatusList != null && (baseEntityStatusList.size() > 0))
					count = getJobUserCount(baseEntityStatusList, count);
			}else {
				//logger.info("Checking count for meta "+type+"....");
				List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, null, userName, startDate, endDate, tags, active, status);
				if(baseEntityStatusList != null && (baseEntityStatusList.size() > 0))
					count = getJobUserCount(baseEntityStatusList, count);
			}
		}else {
			if(status != null && !StringUtils.isBlank(status)){
				List<MetaType> metaExecList = MetaType.getMetaExecList();
				for(MetaType metaType : metaExecList) {
					//logger.info("Checking count for meta "+metaType+"....");
					List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, metaType.toString(), null, userName, startDate, endDate, tags, active, status);
					if(baseEntityStatusList != null && (baseEntityStatusList.size() > 0))
						count = getJobUserCount(baseEntityStatusList, count);
				}
			}else {
				List<MetaType> metaExecList = MetaType.getMetaExecList();
				for(MetaType metaType : metaExecList) {
					//logger.info("Checking count for meta "+metaType+"....");
					List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, metaType.toString(), null, userName, startDate, endDate, tags, active, status);
					if(baseEntityStatusList != null && (baseEntityStatusList.size() > 0))
						count = getJobUserCount(baseEntityStatusList, count);
				}
			}
		}				
		return count;
	}
	public Map<String, Integer> getJobUserCount(List<BaseEntityStatus> baseEntityStatusList, Map<String, Integer> count){
		for(BaseEntityStatus baseEntityStatus : baseEntityStatusList) {
			String name = baseEntityStatus.getCreatedBy().getRef().getName();
			if(name != null && !StringUtils.isBlank(name)) {
				Object obj = count.putIfAbsent(name, 1);
				if(obj != null) {
					Integer oldCount = count.get(name);
					count.put(name, oldCount+1);
				}
			}
		}
		return count;
	}
	public Map<String, Integer> getJobCountByApp(String type, String appUuid, String userName, String startDate, String endDate, String tags, String active, String status, String role) throws ParseException, JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Map<String, Integer> count = new HashMap<>();
		if(type != null && !StringUtils.isBlank(type)) {
			if(status != null && !StringUtils.isBlank(status)){
				//logger.info("Checking count for meta "+type+"....");
				List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, null, userName, startDate, endDate, tags, active, status);
				if(baseEntityStatusList != null && (baseEntityStatusList.size() > 0))
					count = getJobAppCount(baseEntityStatusList, count);
			}else {
				//logger.info("Checking count for meta "+type+"....");
				List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, null, userName, startDate, endDate, tags, active, status);
				if(baseEntityStatusList != null && (baseEntityStatusList.size() > 0))
					count = getJobAppCount(baseEntityStatusList, count);
			}
		}else {
			if(status != null && !StringUtils.isBlank(status)){
				List<MetaType> metaExecList = MetaType.getMetaExecList();
				for(MetaType metaType : metaExecList) {
					//logger.info("Checking count for meta "+metaType+"....");
					List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, metaType.toString(), null, userName, startDate, endDate, tags, active, status);
					if(baseEntityStatusList != null && (baseEntityStatusList.size() > 0))
						count = getJobAppCount(baseEntityStatusList, count);
				}
			}else {
				List<MetaType> metaExecList = MetaType.getMetaExecList();
				for(MetaType metaType : metaExecList) {
					//logger.info("Checking count for meta "+metaType+"....");
					List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, metaType.toString(), null, userName, startDate, endDate, tags, active, status);
					if(baseEntityStatusList != null && (baseEntityStatusList.size() > 0))
						count = getJobAppCount(baseEntityStatusList, count);
				}
			}
		}				
		return count;
	}
	public Map<String, Integer> getJobAppCount(List<BaseEntityStatus> baseEntityStatusList, Map<String, Integer> count){
		for(BaseEntityStatus baseEntityStatus : baseEntityStatusList) {
			List<MetaIdentifierHolder> appList = baseEntityStatus.getAppInfo();
			for(MetaIdentifierHolder appInto : appList) {
				String name=appInto.getRef().getName();
				if(name != null && !StringUtils.isBlank(name)) {
					Object obj = count.putIfAbsent(name, 1);
					if(obj != null) {
						Integer oldCount = count.get(name);
						count.put(name, oldCount+1);
					}
				}
			}
			
		}
		return count;
	}
	public Map<String, Integer> getSessionCountByUser(String type, String appUuid, String userName, String startDate, String endDate, String tags, String active, String status, String role) throws ParseException, JsonGenerationException, JsonMappingException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Map<String, Integer> count = new HashMap<>();
		if(status != null && !StringUtils.isBlank(status)){
			List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, null, userName, startDate, endDate, tags, active, status);
			List<BaseEntityStatus> baseEntityLatestStatusList = getBaseEntityLatestStatusList(baseEntityStatusList, Helper.getStatus(status));
			for(BaseEntity baseEntity : baseEntityLatestStatusList) {
				String createdBy = baseEntity.getCreatedBy().getRef().getName();
				if(createdBy != null && !StringUtils.isBlank(createdBy)) {
					Integer result = count.putIfAbsent(createdBy, 1);
					if(result != null) {
					Integer oldCount = count.get(createdBy);
					count.put(createdBy, (oldCount+1));
					}
				}else {
					MetaIdentifierHolder createdByHolder = baseEntity.getCreatedBy();
					if(createdByHolder != null) {
						User user = (User) commonServiceImpl.getOneByUuidAndVersionWithoutAppUuid(createdByHolder.getRef().getUuid()
																									, createdByHolder.getRef().getVersion()
																									, createdByHolder.getRef().getType().toString());
						if(user != null) {
							createdBy = user.getName();
							Integer result = count.putIfAbsent(createdBy, 1);
							if(result != null) {
							Integer oldCount = count.get(createdBy);
							count.put(createdBy, (oldCount+1));
							}
						}						
					}					
				}
			}
		}else {
			List<BaseEntityStatus> baseEntityList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, null, userName, startDate, endDate, tags, active, status);
			for(BaseEntity baseEntity : baseEntityList) {
				String createdBy = baseEntity.getCreatedBy().getRef().getName();
				if(createdBy != null && !StringUtils.isBlank(createdBy)) {
					Integer result = count.putIfAbsent(createdBy, 1);
					if(result != null) {
					Integer oldCount = count.get(createdBy);
					count.put(createdBy, (oldCount+1));
					}
				}else {
					MetaIdentifierHolder createdByHolder = baseEntity.getCreatedBy();
					if(createdByHolder != null) {
						User user = (User) commonServiceImpl.getOneByUuidAndVersionWithoutAppUuid(createdByHolder.getRef().getUuid()
																									, createdByHolder.getRef().getVersion()
																									, createdByHolder.getRef().getType().toString());
						if(user != null) {
							createdBy = user.getName();
							Integer result = count.putIfAbsent(createdBy, 1);
							if(result != null) {
							Integer oldCount = count.get(createdBy);
							count.put(createdBy, (oldCount+1));
							}
						}						
					}					
				}
			}
		}
		return count;
	}
	public Map<String, Integer> getSessionCountByStatus(String type, String appUuid, String userName, String startDate, String endDate, String tags, String active, String status, String role) throws ParseException, JsonGenerationException, JsonMappingException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Map<String, Integer> count = new HashMap<>();
		if(status != null && !StringUtils.isBlank(status)){
			List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, null, userName, startDate, endDate, tags, active, status);
			if(baseEntityStatusList != null) {
				List<BaseEntityStatus> baseEntityLatestStatusList = getBaseEntityLatestStatusList(baseEntityStatusList, Helper.getStatus(status));
				for(BaseEntityStatus baseEntityStatus : baseEntityLatestStatusList) {
					if(baseEntityStatus.getStatus().size() > 0) {
						String latestStatus = baseEntityStatus.getStatus().get(baseEntityStatus.getStatus().size() - 1).getStage().toString();
						if(latestStatus != null && !StringUtils.isBlank(latestStatus)) {
							Integer result = count.putIfAbsent(latestStatus, 1);
							if(result != null) {
								Integer oldCount = count.get(latestStatus);
								count.put(latestStatus, (oldCount + 1));
							}
						}
					}
				}
			}			
		}else {
			List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, null, userName, startDate, endDate, tags, active, status);
			if(baseEntityStatusList != null) {
				for(BaseEntityStatus baseEntityStatus : baseEntityStatusList) {
					if(baseEntityStatus.getStatus().size() > 0) {
						String latestStatus = baseEntityStatus.getStatus().get(baseEntityStatus.getStatus().size() - 1).getStage().toString();
						if(latestStatus != null && !StringUtils.isBlank(latestStatus)) {
							Integer result = count.putIfAbsent(latestStatus, 1);
							if(result != null) {
								Integer oldCount = count.get(latestStatus);
								count.put(latestStatus, (oldCount + 1));
							}
						}
					}
				}
			}
		}
		return count;
	}

	public Object getThreadStats() {

		Map<String, Map<String, Integer>> counts = new LinkedHashMap<>();
		Map<String, Integer> task = new LinkedHashMap<>();
		Map<String, Integer> stage = new LinkedHashMap<>();
		Map<String, Integer> dag = new LinkedHashMap<>();
		Map<String, Integer> meta = new LinkedHashMap<>();
		
		task.put("Pool Size", taskExecutor.getPoolSize());
		task.put("Active Threads", taskExecutor.getActiveCount());
		task.put("Queued Threads", taskExecutor.getThreadPoolExecutor().getQueue().size());
		
		stage.put("Pool Size", stageExecutor.getPoolSize());
		stage.put("Active Threads", stageExecutor.getActiveCount());
		stage.put("Queued Threads", stageExecutor.getThreadPoolExecutor().getQueue().size());
		
		dag.put("Pool Size", dagExecutor.getPoolSize());
		dag.put("Active Threads", dagExecutor.getActiveCount());
		dag.put("Queued Threads", dagExecutor.getThreadPoolExecutor().getQueue().size());
		
		meta.put("Pool Size", metaExecutor.getPoolSize());
		meta.put("Active Threads", metaExecutor.getActiveCount());
		meta.put("Queued Threads", metaExecutor.getThreadPoolExecutor().getQueue().size());
		
		counts.put("Task", task);
		counts.put("Stage", stage);
		counts.put("Dag", dag);
		counts.put("Meta", meta);
		return counts;
	}
	
}

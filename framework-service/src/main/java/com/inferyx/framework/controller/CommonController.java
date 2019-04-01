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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaStatsHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Organization;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.ImportServiceImpl;
import com.inferyx.framework.service.MessageServiceImpl;
import com.inferyx.framework.service.MessageStatus;
import com.inferyx.framework.service.RegisterService;


@RestController
@RequestMapping(value = "/common")
public class CommonController<T> {

	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	ImportServiceImpl importServiceImpl;
	@Autowired
	MessageServiceImpl messageServiceImpl;
	
	ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
	String result = null;
	static final Logger logger = Logger.getLogger(CommonController.class);
	
	@RequestMapping(value = "/csvToParquet", method = RequestMethod.POST)
	public boolean csvToParquet(@RequestParam("csvFilePath") String csvFilePath,
			@RequestParam("parquetDir") String parquetDir,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws Exception {
		return commonServiceImpl.csvToParquet(csvFilePath, parquetDir);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getOneByUuidAndVersion", method = RequestMethod.GET)
	public Object getOneByUuidAndVersion(@RequestParam("uuid") String uuid,
			@RequestParam("version") String version, @RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if(type.equalsIgnoreCase(MetaType.datasetview.toString()) 
				|| type.equalsIgnoreCase(MetaType.dqview.toString()) 
				|| type.equalsIgnoreCase(MetaType.dashboardview.toString())
				|| type.equalsIgnoreCase(MetaType.reconview.toString()) 
				|| type.equalsIgnoreCase(MetaType.reportview.toString()) 
				|| type.equalsIgnoreCase(MetaType.batchview.toString())
				|| type.equalsIgnoreCase(MetaType.ingestview.toString())
				|| type.equalsIgnoreCase(MetaType.applicationview.toString())
				|| type.equalsIgnoreCase(MetaType.dashboardExecView.toString()))
			if(StringUtils.isBlank(version)) {
				return (T) registerService.getLatestByUuid(uuid, type);
			} else {
				return (T) registerService.getOneByUuidAndVersion(uuid, version, type);
			}
		else {
			T object = (T) commonServiceImpl.getOneByUuidAndVersion(uuid, version, type);
			return objectWriter.writeValueAsString(object);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getLatestByUuid", method = RequestMethod.GET)
	public @ResponseBody String getLatestByUuid(@RequestParam("uuid") String uuid,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if(type.equalsIgnoreCase(MetaType.datasetview.toString()) 
				|| type.equalsIgnoreCase(MetaType.dqview.toString()) 
				|| type.equalsIgnoreCase(MetaType.dashboardview.toString()))
			return  registerService.getLatestByUuid(uuid, type);
		else {
			T object = (T) commonServiceImpl.getLatestByUuid(uuid, type);
			return objectWriter.writeValueAsString(object);
		}
	}
	
	@RequestMapping(value = "/getAllVersionByUuid", method = RequestMethod.GET)
	public @ResponseBody String getAllVersion(@RequestParam("uuid") String uuid, 
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, NullPointerException, ParseException {
		List<BaseEntity> baseEntityList = commonServiceImpl.getAllVersionByUuid(uuid, type);
		return objectWriter.writeValueAsString(baseEntityList);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getOneById", method = RequestMethod.GET, params = { "id", "type" })
	public @ResponseBody String getOneById(@RequestParam("id") String id, 
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if(type.equalsIgnoreCase(MetaType.datasetview.toString()) 
				|| type.equalsIgnoreCase(MetaType.dqview.toString()))
			return registerService.getOneById(id, type);
		else {
			T object = (T) commonServiceImpl.getOneById(id, type);
			return objectWriter.writeValueAsString(object);
		}
	}

	@RequestMapping(value = "/getAllLatest", method = RequestMethod.GET)
	public @ResponseBody String getAllLatest(@RequestParam("type") String type,
			@RequestParam(value="active", required=false) String active,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, ParseException {
		List<BaseEntity> baseEntityList = commonServiceImpl.getAllLatest(type,active);
		return objectWriter.writeValueAsString(baseEntityList);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getAllByUuid", method = RequestMethod.GET)
	public @ResponseBody String getAllByUuid(@RequestParam("uuid") String uuid, 
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		T object = (T) commonServiceImpl.getAllByUuid(uuid, type);
		return objectWriter.writeValueAsString(object);
	}

	@RequestMapping(value = "/getAll", method = RequestMethod.GET)
	public @ResponseBody String getAll(@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action, HttpServletRequest request)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException{
		List<BaseEntity> baseEntityList = commonServiceImpl.getAll(type);
		return objectWriter.writeValueAsString(baseEntityList);
	}
	
	@RequestMapping(value = "/getList", method = RequestMethod.GET)
	public @ResponseBody List<BaseEntity> getList(@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return commonServiceImpl.getList(type);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public String save(@RequestBody Object metaObject,
			@RequestParam("type") String type,
			@RequestParam(value = "upd_tag", required = false, defaultValue = "N") String upd_tag,
			@RequestParam(value = "action", required = false) String action,
			HttpServletRequest request)
			throws Exception {
		if (type.equalsIgnoreCase(MetaType.datasetview.toString()) 
				|| type.equalsIgnoreCase(MetaType.dqview.toString())
				|| type.equalsIgnoreCase(MetaType.dashboardview.toString())
				|| type.equalsIgnoreCase(MetaType.reconview.toString())
				|| type.equalsIgnoreCase(MetaType.reportview.toString())
				|| type.equalsIgnoreCase(MetaType.batchview.toString())
				|| type.equalsIgnoreCase(MetaType.ingestview.toString())
				|| type.equalsIgnoreCase(MetaType.applicationview.toString())) {
			ObjectMapper mapper = new ObjectMapper();
			java.util.Map<String, Object> operator = mapper.convertValue(metaObject, java.util.Map.class);
			 
			BaseEntity baseEntity=  registerService.save(operator, type);
			if (upd_tag.equalsIgnoreCase("Y")) {
				commonServiceImpl.updateLovForTag(baseEntity);
			}
			return baseEntity.getId().toString();
					 
		} else {
			BaseEntity baseEntity = (BaseEntity) commonServiceImpl.save(type, metaObject);
			if (upd_tag.equalsIgnoreCase("Y")) {
				commonServiceImpl.updateLovForTag(baseEntity);
			}
			return baseEntity.getId().toString()/* objectWriter.writeValueAsString(object) */;
		}
	}
		
	@RequestMapping(value = "/saveAs", method = RequestMethod.POST)
	public @ResponseBody String saveAs(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		BaseEntity baseEntity = commonServiceImpl.saveAs(uuid, version, type);
	return objectWriter.writeValueAsString(baseEntity);
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.PUT)
	public String delete(@RequestParam("id") String id, 
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		BaseEntity baseEntity = commonServiceImpl.delete(id, type);
		return objectWriter.writeValueAsString(baseEntity);
	}

	@RequestMapping(value = "/restore", method = RequestMethod.PUT)
	public String restore(@RequestParam("id") String id, 
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		BaseEntity baseEntity = commonServiceImpl.restore(id, type);
		return objectWriter.writeValueAsString(baseEntity);
	}
	
	@RequestMapping(value = "/publish", method = RequestMethod.PUT)
	public String publish(@RequestParam("id") String id, 
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException {
		BaseEntity baseEntity = commonServiceImpl.published(id, type);
		return objectWriter.writeValueAsString(baseEntity);
	}
	
	@RequestMapping(value = "/unPublish", method = RequestMethod.PUT)
	public String unPublish(@RequestParam("id") String id, 
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action,
			HttpServletResponse response) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException {
		BaseEntity baseEntity = commonServiceImpl.unPublished(id, type);
		if(baseEntity != null)
			return objectWriter.writeValueAsString(baseEntity);
		else {
			Message unAuthorised = messageServiceImpl.save(new Message("401", MessageStatus.FAIL.toString(), "Unauthorised to unpublish."));
			response.setStatus(401);
			return objectWriter.writeValueAsString(unAuthorised);
		}			
	}
	@RequestMapping(value = "/lock", method = RequestMethod.PUT)
	public String lock(@RequestParam("id") String id, 
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException {
		BaseEntity baseEntity = commonServiceImpl.locked(id, type);
		return objectWriter.writeValueAsString(baseEntity);
	}
	
	@RequestMapping(value = "/unLock", method = RequestMethod.PUT)
	public String unLock(@RequestParam("id") String id, 
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action,
			HttpServletResponse response) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException {
		BaseEntity baseEntity = commonServiceImpl.unLocked(id, type);
		if(baseEntity != null)
			return objectWriter.writeValueAsString(baseEntity);
		else {
			Message unAuthorised = messageServiceImpl.save(new Message("401", MessageStatus.FAIL.toString(), "Unauthorised to unlock."));
			response.setStatus(401);
			return objectWriter.writeValueAsString(unAuthorised);
		}			
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getAsOf", method = RequestMethod.GET)
	public @ResponseBody String getAsOf(@RequestParam("uuid") String uuid, 
			@RequestParam("asOf") String asOf,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		T object = (T) commonServiceImpl.getAsOf(uuid, asOf, type); 
		return objectWriter.writeValueAsString(object);
	}
	
	@RequestMapping(value = "/getMetaStats", method = RequestMethod.GET)
	public @ResponseBody List<MetaStatsHolder> getMetaStats(@RequestParam(value = "type", required=false) String type,
			@RequestParam(value = "action", required = false) String action) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException, ParseException  {
		return commonServiceImpl.getMetaStats(type);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/resolveName", method = RequestMethod.POST)
	public @ResponseBody String resolveName(@RequestParam(value = "uuid", required=false) String uuid, 
			@RequestParam(value = "type", required=false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		T object = (T) commonServiceImpl.resolveName(uuid, type);
		return objectWriter.writeValueAsString(object);
	}
	
    public HttpServletResponse download(@RequestParam(value = "fileType") String fileType,
    						@RequestParam(value = "fileName") String fileName,
    						HttpServletRequest request,
    						HttpServletResponse response){
		try {
			response = commonServiceImpl.download(fileType, fileName, response);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
    }
	
	
	@RequestMapping(value = "/getAllLatestCompleteObjects", method = RequestMethod.GET)
	public @ResponseBody String getAllLatestCompleteObjects(@RequestParam("type") String type,
			@RequestParam(value="active", required=false) String active,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, ParseException {
		List<?> baseEntityList = commonServiceImpl.getAllLatestCompleteObjects(type,active);
		return objectWriter.writeValueAsString(baseEntityList);
	}
	
	//uploadGenric
	@RequestMapping(value = "/upload", method = RequestMethod.POST, headers = ("content-type=multipart/form-data; boundary=abcd"))
	public @ResponseBody List<MetaIdentifierHolder> uploadGenric(HttpServletRequest request,
											   @RequestParam("file") List<MultipartFile> multiPartFile, 
											   @RequestParam(value = "extension",required = false) String extension,
											   @RequestParam(value = "fileType", required = false) String fileType,
											   @RequestParam(value = "type", required = false) String type,
											   @RequestParam(value = "uuid", required = false) String uuid,
											   @RequestParam(value = "version", required = false) String version,
											   @RequestParam(value = "dataSourceUuid", required = false) String dataSourceUuid,
											   @RequestParam(value = "action", required = false) String action)
											throws Exception {
		List<MetaIdentifierHolder> result = commonServiceImpl.uploadGenric(multiPartFile,extension ,fileType, type,uuid,version,action,dataSourceUuid);
		
		return result;
	}
	//genricDownload
	@RequestMapping(value="/download",method = RequestMethod.GET,params = {"uuid"})
    public HttpServletResponse download(@RequestParam(value = "fileType",required = false) String fileType,
    						@RequestParam(value = "fileName",required = false) String fileName,
    						HttpServletResponse response,
    						@RequestParam(value = "uuid" ) String uuid){
		try {
			response = commonServiceImpl.genricDownload(fileType, fileName, response,uuid);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
    }
	
	@RequestMapping(value = "/getAllLatestParamListByTemplate", method = RequestMethod.GET)
	public List<ParamList> getAllLatestParamListByTemplate(@RequestParam(value = "type", required = false) String type,
														   @RequestParam(value = "action", required = false) String action,
														   @RequestParam(value = "templateFlg") String templateFlg,
														   @RequestParam(value = "paramListType") MetaType paramListType) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException{
		return commonServiceImpl.getAllLatestParamListByTemplate(templateFlg, null, null, paramListType);
	}
	
	@RequestMapping(value = "/getUserByApp", method = RequestMethod.GET)
	public @ResponseBody List<User> getUserByApp(
			@RequestParam(value = "action", required=false) String action,
			@RequestParam(value = "type", required = false) String type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException, ParseException  {
		return commonServiceImpl.getUserByApp();
	}
	
	@RequestMapping(value = "/getAppByOrg", method = RequestMethod.GET)
	public @ResponseBody List<Application> getAppByOrg(
			@RequestParam(value = "uuid") String orgUuid,
			@RequestParam(value = "action", required=false) String action,
			@RequestParam(value = "type", required = false) String type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException, ParseException  {
		return commonServiceImpl.getAppByOrg(orgUuid);
	}
	
	@RequestMapping(value = "/getOrgInfoByApp", method = RequestMethod.GET)
	public @ResponseBody Organization getOrgInfoByApp(
			@RequestParam(value = "action", required=false) String action,
			@RequestParam(value = "type", required = false) String type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException, ParseException  {
		return commonServiceImpl.getOrgInfoByCurrentApp();
	}
	
	
	
}
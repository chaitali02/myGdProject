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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.ServletContext;
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
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.MetaStatsHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
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
		if(type.equalsIgnoreCase(MetaType.datasetview.toString()) || type.equalsIgnoreCase(MetaType.dqview.toString()) || type.equalsIgnoreCase(MetaType.ruleview.toString()) || type.equalsIgnoreCase(MetaType.dashboardview.toString())|| type.equalsIgnoreCase(MetaType.reconview.toString()))
			if(StringUtils.isBlank(version))
				return (T) registerService.getLatestByUuid(uuid, type);
			else
				return (T) registerService.getOneByUuidAndVersion(uuid, version, type);
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
		if(type.equalsIgnoreCase(MetaType.datasetview.toString()) || type.equalsIgnoreCase(MetaType.dqview.toString()) || type.equalsIgnoreCase(MetaType.ruleview.toString()) || type.equalsIgnoreCase(MetaType.dashboardview.toString()))
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
		if(type.equalsIgnoreCase(MetaType.datasetview.toString()) || type.equalsIgnoreCase(MetaType.dqview.toString()))
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
	public String save(@RequestBody Object metaObject, @RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action, HttpServletRequest request)
	throws Exception {
		if(type.equalsIgnoreCase(MetaType.datasetview.toString()) || type.equalsIgnoreCase(MetaType.dqview.toString()) || type.equalsIgnoreCase(MetaType.ruleview.toString()) || type.equalsIgnoreCase(MetaType.dashboardview.toString()) || type.equalsIgnoreCase(MetaType.reconview.toString())   ){
			ObjectMapper mapper = new ObjectMapper();
			java.util.Map<String, Object> operator = mapper.convertValue(metaObject, java.util.Map.class);
			return registerService.save(operator, type);
		}else{
			BaseEntity baseEntity = (BaseEntity) commonServiceImpl.save(type, metaObject );
			return baseEntity.getId().toString()/*objectWriter.writeValueAsString(object)*/;
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
	
	/*@RequestMapping(value="session/invalidate", method = RequestMethod.GET)
    public @ResponseBody String invalidateSession(){
		return commonServiceImpl.invalidateSession();
    }*/
	
	@RequestMapping(value = "/upload", headers = ("content-type=multipart/form-data; boundary=abcd"), method = RequestMethod.POST)
	public @ResponseBody String upload(@RequestParam("file") MultipartFile file,
									   @RequestParam(value = "extension") String extension,
									   @RequestParam(value = "fileType") String fileType,
									   @RequestParam(value = "type", required = false) String type,
									   @RequestParam(value = "fileName", required = false) String fileName) throws FileNotFoundException, IOException, JSONException, ParseException {
					return commonServiceImpl.upload(file, extension, fileType, fileName, type);
	}
	
	@RequestMapping("/download")
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
}
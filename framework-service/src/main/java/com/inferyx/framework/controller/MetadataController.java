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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IDatasourceDao;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseEntityStatus;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaStatsHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Registry;
import com.inferyx.framework.domain.RolePriv;
import com.inferyx.framework.domain.StatusHolder;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.parser.DagParser;
import com.inferyx.framework.parser.FormulaParser;
import com.inferyx.framework.register.CSVRegister;
import com.inferyx.framework.register.HiveRegister;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DashboardServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.DatasourceServiceImpl;
import com.inferyx.framework.service.FormulaServiceImpl;
import com.inferyx.framework.service.ImportServiceImpl;
import com.inferyx.framework.service.MetadataServiceImpl;
import com.inferyx.framework.service.PrivilegeServiceImpl;
import com.inferyx.framework.service.RegisterService;
import com.inferyx.framework.service.SecurityServiceImpl;
import com.inferyx.framework.service.SystemServiceImpl;
import com.inferyx.framework.view.parser.RefParser;

@Controller
@RequestMapping(value = "/metadata")
public class MetadataController {

	@Autowired
	RegisterService registerService;
	@Autowired
	RefParser refParser;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	FormulaServiceImpl formulaServiceImpl;
	@Autowired
	DatasourceServiceImpl datasourceServiceImpl;
	@Autowired
	DagParser parser;
	@Autowired
	HDFSInfo hdfsInfo;
	@Autowired
	HiveRegister hiveRegister;
	@Autowired
	FormulaParser formulaParser;
	@Autowired
	IDatasourceDao iDatasourceDao;
	@Autowired
	private SecurityServiceImpl securityServiceImpl;
	@Autowired
	protected DashboardServiceImpl dashboardServiceImpl;
	@Autowired
	CSVRegister csvRegister;
	@Autowired
	PrivilegeServiceImpl privilegeServiceImpl;
	@Autowired
	MetadataServiceImpl metadataServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ImportServiceImpl importServiceImpl;
	@Autowired
	SystemServiceImpl systemServiceImpl;
	
	ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
	
	@RequestMapping(value = "/getDagExecByDatapod", method = RequestMethod.GET)
	public @ResponseBody String getDagExecByDatapod(@RequestParam("datapodUUID") String datapodUUID,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getDagExecByDatapod(datapodUUID, type);
	}

	@RequestMapping(value = "/getDagExecByDag", method = RequestMethod.GET)
	public @ResponseBody String getDagExecByDag(@RequestParam("dagUUID") String dagUUID,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getDagExecByDag(dagUUID, type);
	}

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getDagByDatapod", method = RequestMethod.GET)
	public @ResponseBody String getDagByDatapod(@RequestParam("datapodUUID") String datapodUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		return registerService.getDagByDatapod(datapodUUID);
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getMapByDatapod", method = RequestMethod.GET)
	public @ResponseBody String getMapByDatapod(@RequestParam("datapodUUID") String datapodUUID,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getMapByDatapod(datapodUUID, type);
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getDimByType", method = RequestMethod.GET)
	public @ResponseBody String getDimensionByDatapod(@RequestParam("typeUUID") String typeUUID,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getDimByType(typeUUID, type);
	}*/


	/*@RequestMapping(value = "/getOneByUuidAndVersion", method = RequestMethod.GET)
	public @ResponseBody String getOneByUuidAndVersion(@RequestParam("uuid") String uuid,
			@RequestParam("version") String version, @RequestParam("type") String type) throws JsonProcessingException {
		return registerService.getOneByUuidAndVersion(uuid, version, type);
	}*/
	
	/*@RequestMapping(value = "/getLatestByUuid", method = RequestMethod.GET)
	public @ResponseBody String getLatestByUuid(@RequestParam("uuid") String uuid, @RequestParam("type") String type)
			throws JsonProcessingException {
		return registerService.getLatestByUuid(uuid, type);
	}*/
	
	/*@RequestMapping(value = "/getAllVersionByUuid", method = RequestMethod.GET)
	public @ResponseBody String getAllVersion(@RequestParam("uuid") String uuid, @RequestParam("type") String type)
			throws JsonProcessingException {
		return registerService.getAllVersion(uuid, type);
	}*/
	
	@RequestMapping(value = "/getLatestByUsername", method = RequestMethod.GET)
	public @ResponseBody String getLatestByUsername(@RequestParam("userName") String userName,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		return registerService.getLatestByUsername(userName);
	}

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getFilterByRelation", method = RequestMethod.GET)
	public @ResponseBody String getFilterByRelation(@RequestParam("relationUUID") String relationUUID,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getFilterByRelation(relationUUID, type);
	}*/

	/*@RequestMapping(value = "/getDimInfoByRelation", method = RequestMethod.GET)
	public @ResponseBody String getDimInfoByRelation(@RequestParam("relationUUID") String relationUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		return registerService.getDimInfoByRelation(relationUUID);
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getMeasureInfoByRelation", method = RequestMethod.GET)
	public @ResponseBody String getMeasureInfoRelation(@RequestParam("relationUUID") String relationUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		return registerService.getMeasureInfoByRelation(relationUUID);
	}*/

//	@RequestMapping(value = "/createActivity", method = RequestMethod.GET)
//	public @ResponseBody String createActivity(@RequestParam("UserUUID") String UserUUID,
//			@RequestParam(value = "type", required = false) String type,
//			@RequestParam(value = "action", required = false) String action) throws IOException {
//		return registerService.createActivity(UserUUID);
//	}

	@RequestMapping(value = "/getUserByName", method = RequestMethod.GET)
	public @ResponseBody String getUserByName(@RequestParam("userName") String userName,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		return registerService.getUserByName(userName);
	}

	@RequestMapping(value = "/getActivityByUser", method = RequestMethod.GET)
	public @ResponseBody String getActivityByUser(@RequestParam("UserUUID") String UserUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		return registerService.getActivityByUser(UserUUID);
	}

	@RequestMapping(value = "/createUserSession", method = RequestMethod.GET)
	public @ResponseBody String createUserSession(@RequestParam("userName") String userName,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) 
					throws IOException, JSONException, java.text.ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return registerService.createUserSession(userName);
	}

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getFilterByDatapod", method = RequestMethod.GET)
	public @ResponseBody String getFilterByDatapod(@RequestParam("datapodUUID") String datapodUUID,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) 
					throws JsonProcessingException {
		return registerService.getFilterByDatapod(datapodUUID, type);
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getOneById", method = RequestMethod.GET, params = { "id", "type" })
	public @ResponseBody String getOneById(@RequestParam("id") String id, @RequestParam("type") String type,
			HttpServletRequest request) throws JsonProcessingException {
		return registerService.getOneById(id, type);
	}*/
	
	@RequestMapping(value = "/getRelationByDatapod", method = RequestMethod.GET)
	public @ResponseBody String getRelationByDatapod(@RequestParam("datapodUUID") String datapodUUID,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) 
					throws JsonProcessingException {
		return registerService.getRelationByDatapod(datapodUUID, type);
	}

	@RequestMapping(value = "/getVizpodByDatapod", method = RequestMethod.GET)
	public @ResponseBody String getVizpodByDatapod(@RequestParam("datapodUUID") String datapodUUID,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) 
					throws JsonProcessingException {
		return registerService.getVizpodByDatapod(datapodUUID, type);
	}

	@RequestMapping(value = "/getVizpodByRelation", method = RequestMethod.GET)
	public @ResponseBody String getVizpodByRealtion(@RequestParam("relationUUID") String relationUUID,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) 
					throws JsonProcessingException {
		return registerService.getVizpodByRelation(relationUUID, type);
	}

	@RequestMapping(value = "/getDataStoreByDatapod", method = RequestMethod.GET)
	public @ResponseBody String getDataStoreByDatapod(@RequestParam("datapodUUID") String datapodUUID,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) 
					throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {

		return registerService.getDataStoreByDatapod(datapodUUID, type);
	}

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getAllLatest", method = RequestMethod.GET)
	public @ResponseBody String getAllLatest(@RequestParam("type") String type, HttpServletRequest request,
			@RequestParam(value = "inputFlag", required = false) String inputFlag) throws JsonProcessingException {
		return registerService.getAllLatest(type, inputFlag);
	}*/

	@RequestMapping(value = "/getAllLatestActive", method = RequestMethod.GET)
	public @ResponseBody String getAllLatestActive(@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) 
					throws JsonProcessingException, java.text.ParseException {
		/*return registerService.getAllLatestActive(type);*/
		return objectWriter.writeValueAsString(commonServiceImpl.getAllLatest(type, "Y"));
	}

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getAllByUuid", method = RequestMethod.GET)
	public @ResponseBody String getAllByUuid(@RequestParam("uuid") String uuid, @RequestParam("type") String type)
			throws JsonProcessingException {
		return registerService.getAllByUuid(uuid, type);
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getAll", method = RequestMethod.GET)
	public @ResponseBody String getAll(@RequestParam("type") String type, HttpServletRequest request)
			throws JsonProcessingException {
		return registerService.getMetaDataList(type, request);
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getList", method = RequestMethod.GET)
	public @ResponseBody List<BaseEntity> getList(@RequestParam("type") String type, HttpServletRequest request)
			throws JsonProcessingException {
		return registerService.getList(type, request);
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/dag", method = RequestMethod.POST)
	public @ResponseBody String executeDag(@RequestBody Dag dag,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) {
		return null;
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public ResponseEntity<String> save(@RequestBody Map<String, Object> document, @RequestParam("type") String type)
			throws Exception {
		String Id = registerService.save(document, type);
		return new ResponseEntity<String>(Id, HttpStatus.OK);
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/saveAs", method = RequestMethod.GET)
	public @ResponseBody String SaveAs(@RequestParam("uuid") String uuid, @RequestParam("version") String version,
			@RequestParam("type") String type) throws Exception {
		return registerService.saveAs(uuid, version, type);
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/resolveName", method = RequestMethod.POST)
	public @ResponseBody Object resolveName(@RequestBody Map<String, Object> document,
			@RequestParam("type") String type) {
		return refParser.resolveName(document, type);
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public ResponseEntity<String> delete(@RequestParam("id") String id, @RequestParam("type") String type) {
		registerService.delete(id, type);
		return new ResponseEntity<String>("deleted successfully", HttpStatus.OK);
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/test", method = RequestMethod.GET)
	public @ResponseBody String test(@RequestParam("param1") String param1, 
			@RequestParam("param2") String param2,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) 
					throws JsonProcessingException {
		return registerService.test(param1, param2, type);
	}*/
	
	@RequestMapping(value = "/getDagExecStatus", method = RequestMethod.GET)
	public @ResponseBody String getDagExecStatus(@RequestParam("uuid") String uuid,
			@RequestParam("version") String version, @RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) 
					throws JsonProcessingException {
		return registerService.getDagExecStatus(uuid, version);

	}

	@RequestMapping(value = "/getDagExecStageStatus", method = RequestMethod.GET)
	public @ResponseBody String getDagExecStageStatus(@RequestParam("uuid") String uuid,
			@RequestParam("version") String version, 
			@RequestParam("stageId") String stageId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
					throws JsonProcessingException {
		return registerService.getDagExecStageStatus(uuid, version, stageId);

	}

	@RequestMapping(value = "/getDagExecTaskStatus", method = RequestMethod.GET)
	public @ResponseBody String getDagExecTaskStatus(@RequestParam("uuid") String uuid,
			@RequestParam("version") String version, 
			@RequestParam("type") String type,
			@RequestParam("taskId") String taskId,
			@RequestParam(value = "action", required = false) String action) 
					throws JsonProcessingException {
		return registerService.getDagExecTaskStatus(uuid, version, taskId);

	}

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/registerFile", method = RequestMethod.GET)
	public @ResponseBody MetaIdentifierHolder loadCsv(@RequestParam("csvFileName") String csvFileName,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value = "mode", required = false) String mode)
			throws Exception {
		Mode runMode;
		if (mode == null) {
			runMode = Mode.ONLINE;
		} else {
			runMode = Mode.valueOf(mode); 
		}
		return datapodServiceImpl.createAndLoad(csvFileName, runMode);
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/file", method = RequestMethod.POST)
	public @ResponseBody String getFileContain(HttpServletRequest request,
			@RequestParam("file") MultipartFile multiPartFile, @RequestParam("fileName") String filename,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws IOException {
		String result=null;
		if(type.equalsIgnoreCase(MetaType.datapod.toString())){
			String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
			List<Datasource> datasourceList = iDatasourceDao.findDatasourceByType(appUuid, "FILE");
			String path = null;
			for (Datasource datasource : datasourceList) {
				path = datasource.getPath();
			}
			String filePath = path + "/" + filename;
			File dest = new File(filePath);
			multiPartFile.transferTo(dest);
			result=dest.getAbsolutePath();
		}else {
			ObjectMapper mapper=new ObjectMapper();
			result=mapper.writeValueAsString(importServiceImpl.uploadFile(multiPartFile,filename));
		}
		return result;
	}*/
	

	@RequestMapping(value = "/uploadProfileImage", method = RequestMethod.POST)
	public @ResponseBody String uploadProfileImage(@RequestParam("file") MultipartFile file, 
			@RequestParam("fileName") String filename,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws IOException {
		String filePath = "src/main/webapp/app/avatars/" + filename;
		File dest = new File(filePath);
		file.transferTo(dest);
		return "";
	}
	
	@RequestMapping(value = "/getAttributesByRelation", method = RequestMethod.GET)
	public @ResponseBody List<AttributeRefHolder> getAttributesByRelation(@RequestParam("uuid") String relationUuid,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, JSONException {

		return registerService.getAttributesByRelation(relationUuid, type);
	}

	@RequestMapping(value = "/getDatapodByRelation", method = RequestMethod.GET)
	public @ResponseBody String getDatapodByRelation(@RequestParam("relationUuid") String relationUuid,
			@RequestParam(value = "version", required = false, defaultValue = "") String version,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, JSONException {
		return registerService.getDatapodByRelation(relationUuid, version, type);
	}

	/*@RequestMapping(value = "/getFormulaByRelation", method = RequestMethod.GET)
	public @ResponseBody String getFormulaByRelation(@RequestParam("uuid") String relationUuid,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, JSONException {
		return registerService.getFormulaByRelation(relationUuid, type);
	}*/

	/*@RequestMapping(value = "/getExpressionByRelation", method = RequestMethod.GET)
	public @ResponseBody String getExpressionByRelation(@RequestParam("uuid") String relationUuid,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, JSONException {
		return registerService.getExpressionByRelation(relationUuid, type);
	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getConditionByRelation", method = RequestMethod.GET)
	public @ResponseBody String getConditionByRelation(@RequestParam("relationUUID") String relationUUID,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getConditionByRelation(relationUUID, type);
	}*/

	@RequestMapping(value = "/genDataDict", method = RequestMethod.GET)
	public boolean genDataDict(HttpServletResponse response, @RequestParam("multiTab") String multitab,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) {
		try {
			datapodServiceImpl.genDataDict(multitab, response);			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String getSessionIdFromHeader(HttpServletRequest request) {
		String sessionId = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("_frameworkSession")) {
					return sessionId;
				}
			}
		}
		return sessionId;
	}

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/createdUserName", method = RequestMethod.POST)
	public @ResponseBody MetaIdentifier getCreatedUserName(@RequestBody MetaIdentifier metaIdentifier,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, JSONException {

		return registerService.getCreatedUserName(metaIdentifier);
	}*/

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public @ResponseBody List<Registry> register(@RequestParam("uuid") String uuid,
			@RequestParam("version") String version, 
			@RequestParam("type") String type,
			@RequestBody List<Registry> registryList,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value = "mode", required = false) String mode) throws Exception {
		Mode runMode;
		if (mode == null) {
			runMode = Mode.ONLINE;
		} else {
			runMode = Mode.valueOf(mode); 
		}
		return registerService.register(uuid, version, type, registryList, runMode);
	}

	/*@RequestMapping(value = "/getPrefixFormula", method = RequestMethod.POST)
	public @ResponseBody DBObject getFormula(@RequestBody Formula formulaInfo,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JSONException, SQLException, IOException, ParseException {
		return formulaParser.ConvertFormula(formulaInfo);
	}*/

	@RequestMapping(value = "/getAppByUser", method = RequestMethod.GET)
	public @ResponseBody String getAppByUser(@RequestParam("userName") String userName,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JSONException, SQLException, IOException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {

		return registerService.getAppByUser(userName);
	}

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getRoleByUser", method = RequestMethod.GET)
	public @ResponseBody String getRoleByUser(@RequestParam("userName") String userName,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JSONException, SQLException, IOException, ParseException {

		return registerService.getRoleByUser(userName);
	}*/

	@RequestMapping(value = "/getDatasourceByType", method = RequestMethod.GET)
	public @ResponseBody List<Datasource> getDatasourceByType(@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		return datasourceServiceImpl.getDatasourceByType(type);
	}

	@RequestMapping(value = "/getAttributesByDataset", method = RequestMethod.GET)
	public @ResponseBody List<AttributeRefHolder> getAttributesByDataset(@RequestParam("uuid") String Uuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, JSONException {

		return registerService.getAttributesByDataset(Uuid);
	}

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getDashboardByName", method = RequestMethod.GET)
	public @ResponseBody Dashboard getDashboardByName(@RequestParam("name") String name,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		return dashboardServiceImpl.getLatestDashboardByName(name);
	}*/

	@RequestMapping(value = "/getAttributesByDatapod", method = RequestMethod.GET)
	public @ResponseBody List<AttributeRefHolder> getAttributesByDatapod(@RequestParam("uuid") String Uuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, JSONException {

		return registerService.getAttributesByDatapod(Uuid);
	}

	@RequestMapping(value = "/getAttributesByRule", method = RequestMethod.GET)
	public @ResponseBody List<AttributeRefHolder> getAttributesByRule(@RequestParam("uuid") String Uuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, JSONException {

		return registerService.getAttributesByRule(Uuid);
	}

	@RequestMapping(value = "/getFormulaByType", method = RequestMethod.GET)
	public @ResponseBody String getFormulaByType(@RequestParam("uuid") String uuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			
			throws JsonProcessingException, JSONException {

		return registerService.getFormulaByType(uuid);
	}

	@RequestMapping(value = "/getVizpodByType", method = RequestMethod.GET)
	public @ResponseBody String getVizpodByType(@RequestParam("uuid") String uuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, JSONException {

		return registerService.getVizpodByType(uuid);
	}

	@RequestMapping(value = "/getFormulaByType2", method = RequestMethod.GET)
	public @ResponseBody String getFormulaByType2(@RequestParam(value="uuid",required = false) String uuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "formulaType", required = false,defaultValue ="") String[] formulaType)
			throws JsonProcessingException, JSONException {

		return registerService.getFormulaByType2(uuid,formulaType);
	}

	@RequestMapping(value = "/getExpressionByType", method = RequestMethod.GET)
	public @ResponseBody String getExpressionByType(@RequestParam("uuid") String uuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, JSONException {
		return registerService.getExpressionByType(uuid);
	}

	@RequestMapping(value = "/getExpressionByType2", method = RequestMethod.GET)
	public @ResponseBody String getExpressionByType2(@RequestParam("uuid") String uuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, JSONException {

		return registerService.getExpressionByType2(uuid);
	}

	/*@RequestMapping(value = "/getAsOf", method = RequestMethod.GET)
	public @ResponseBody String getAsOf(@RequestParam("uuid") String uuid, @RequestParam("asOf") String asOf,
			@RequestParam("type") String type) throws JsonProcessingException {
		return registerService.getAsOf(uuid, asOf, type);
	}*/
	
	@RequestMapping(value = "/getRegistryByDatasource", method = RequestMethod.GET)
	public @ResponseBody List<Registry> getRegistryByDatasource(@RequestParam("datasourceUuid") String datasourceUuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		return registerService.getRegistryByDatasource(datasourceUuid,status);
	}

	@RequestMapping(value = "/getFunctionByFunctionInfo", method = RequestMethod.GET)
	public @ResponseBody List<Function> getFunctionByFunctionInfo(@RequestParam("functionInfo") String functionInfo,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {
		return registerService.getFunctionByFunctionInfo(functionInfo);
	}

	@RequestMapping(value = "/getRolePriv", method = RequestMethod.GET)
	public @ResponseBody List<RolePriv> getRolePriv(@RequestParam("roleUuid") String roleUuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		return privilegeServiceImpl.getRolePriv(roleUuid);
	}

	@RequestMapping(value = "/getParamSetByModel", method = RequestMethod.GET)
	public @ResponseBody String getParamSetByModel(@RequestParam("modelUuid") String modelUuid,
			@RequestParam("modelVersion") String modelVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "modelType", required = false, defaultValue = "spark") String modelType,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
			if(!modelType.equalsIgnoreCase(ExecContext.R.toString()) && !modelType.equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				return registerService.getParamSetByModel(modelUuid, modelVersion);
			} else
				return null;			
	}
	
	@RequestMapping(value = "/getParamSetByTrain", method = RequestMethod.GET)
	public @ResponseBody String getParamSetByTrain(@RequestParam("trainUuid") String trainUuid,
			@RequestParam("trainVersion") String trainVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainUuid, trainVersion, MetaType.train.toString());
		Model model =  (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), train.getDependsOn().getRef().getVersion(), MetaType.model.toString());
		if(!model.getType().equalsIgnoreCase(ExecContext.R.toString()) && !model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				return registerService.getParamSetByTrain(trainUuid, trainVersion);
			} else
				return null;			
	}

	@RequestMapping(value = "/getParamSetByParamList", method = RequestMethod.GET)
	public @ResponseBody String getParamSetByParamList(@RequestParam("paramListUuid") String paramListUuid,
			@RequestParam(value = "paramListVersion", required = false) String paramListVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		return registerService.getParamSetByParamList(paramListUuid, paramListVersion);
	}

	@RequestMapping(value = "/getParamSetByRule", method = RequestMethod.GET)
	public @ResponseBody String getParamSetByRule(@RequestParam("ruleUuid") String ruleUuid,
			@RequestParam(value = "ruleVersion", required = false) String ruleVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getParamSetByRule(ruleUuid, ruleVersion);
	}

	@RequestMapping(value = "/getParamSetByAlgorithm", method = RequestMethod.GET)
	public @ResponseBody String getParamSetByAlgorithm(@RequestParam("algorithmUuid") String algorithmUuid,
			@RequestParam("algorithmVersion") String algorithmVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getParamSetByAlogrithm(algorithmUuid, algorithmVersion);
	}

	@RequestMapping(value = "/getMetaIdByExecId", method = RequestMethod.GET)
	public @ResponseBody String getMetaIdByExecId(@RequestParam("execUuid") String execUuid,
			@RequestParam("execVersion") String execVersion, @RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return registerService.getMetaIdByExecId(execUuid, execVersion, type);
	}

	/*@RequestMapping(value = "/getMetaStats", method = RequestMethod.GET)
	public @ResponseBody List<MetaStatsHolder> getMetaStats() throws Exception {
		return registerService.getMetaStats();
	}
*/
	@RequestMapping(value = "/getMetaStatsByType", method = RequestMethod.GET)
	public @ResponseBody long getMetaStatsByType(@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return registerService.getMetaStatsByType();
	}

	@RequestMapping(value = "/getExecStats", method = RequestMethod.GET)
	public @ResponseBody List<MetaStatsHolder> getExecStats(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return registerService.getExecStats();
	}

	@RequestMapping(value = "/getNumRowsbyExec", method = RequestMethod.GET)
	public @ResponseBody String getNumRowsbyExec(@RequestParam("execUuid") String execUuid,
			@RequestParam("execVersion") String execVersion, @RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return registerService.getNumRowsbyExec(execUuid, execVersion, type);
	}

	@RequestMapping(value = "/getBaseEntityByCriteria", method = RequestMethod.GET)
	public @ResponseBody List<BaseEntity> getBaseEntityByCriteria(@RequestParam("type") String type,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "tags", required = false) String tags,
			@RequestParam(value = "active", required = false) String active,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "published", required = false) String published) throws Exception {		
		List<BaseEntity> baseEntityList =  metadataServiceImpl.getBaseEntityByCriteria(type, name, userName, startDate, endDate, tags, active, null, null, published);
		//List<BaseEntity> newBaseEntityList =  metadataServiceImpl.filterPublished(baseEntityList);
		return baseEntityList;
	}

	@RequestMapping(value = "/getBaseEntityStatusByCriteria", method = RequestMethod.GET)
	public @ResponseBody List<BaseEntityStatus> getBaseEntityStatusByCriteria(@RequestParam("type") String type,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "tags", required = false) String tags,
			@RequestParam(value = "active", required = false) String active,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "appUuid", required = false) String appUuid,
			@RequestParam(value = "role", required=false) String role ) throws Exception {
			if(status != null && !StringUtils.isBlank(status)) {
				List<BaseEntityStatus> baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, name, userName, startDate, endDate, tags, active,
						status);
				return systemServiceImpl.getBaseEntityLatestStatusList(baseEntityStatusList, Helper.getStatus(status));
			}else
				return metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, name, userName, startDate, endDate, tags, active,
						status);
				
	}

//	@RequestMapping(value = "/getGroupExecStatus", method = RequestMethod.GET)
//	public @ResponseBody List<StatusHolder> getGroupExecStatus(@RequestParam("type") String type,
//			@RequestParam(value = "uuid") String uuid, @RequestParam(value = "version") String version,
//			@RequestParam(value = "action", required = false) String action)
//			throws Exception {
//		return metadataServiceImpl.getGroupExecStatus(type, uuid, version);
//	}
//	
	@RequestMapping(value = "/getGroupExecStatus", method = RequestMethod.GET)
	public @ResponseBody List<StatusHolder> getGroupExecStatusReflection(@RequestParam("type") String type,
			@RequestParam(value = "uuid") String uuid, @RequestParam(value = "version") String version,
			@RequestParam(value = "action", required = false) String action)
			throws Exception {
		return metadataServiceImpl.getGroupExecStatusReflection(type, uuid, version);
	}
	
	@RequestMapping(value = "/getMetaExecList", method = RequestMethod.GET)
	public @ResponseBody String getMetaExecList() throws Exception {
		return metadataServiceImpl.getMetaExecList();
	}	
	
	@RequestMapping(value = "/getDatasourceByApp", method = RequestMethod.GET)
	public @ResponseBody List<Datasource> getDatasourceByApp(
			@RequestParam(value = "action") String action ,@RequestParam(value = "type") String type ) throws Exception {
		return datasourceServiceImpl.getDatasourceByApp();
	}	
	
	@RequestMapping(value = "/getFunctionByCategory", method = RequestMethod.GET)
	public @ResponseBody List<Function> getFunctionByType(
			@RequestParam(value ="category" ,defaultValue="aggregate") String category,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return metadataServiceImpl.getFunctionByType(category);
	}
	
	@RequestMapping(value = "/getParamByParamList", method = RequestMethod.GET)
	public @ResponseBody List<ParamListHolder> getParamByParamList(
			@RequestParam(value = "uuid") String paramListUuid ,
			@RequestParam(value = "type", required = false) String type ) throws JsonProcessingException {
		return metadataServiceImpl.getParamByParamList(paramListUuid);
	}
	

	@RequestMapping(value = "/getParamListByFormula", method = RequestMethod.GET)
	public @ResponseBody List<ParamListHolder>  getParamListByFormula(
			@RequestParam(value = "uuid") String formulaUuid ,
			@RequestParam(value = "type", required = false) String type ) throws JsonProcessingException {
		return metadataServiceImpl.getParamListByFormula(formulaUuid);
	}
	
}

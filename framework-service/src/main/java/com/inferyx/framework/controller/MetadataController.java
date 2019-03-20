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
import java.io.FileNotFoundException;
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
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.AppConfig;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseEntityStatus;
import com.inferyx.framework.domain.CommentView;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.Group;
import com.inferyx.framework.domain.Lov;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.MetaStatsHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Registry;
import com.inferyx.framework.domain.RolePriv;
import com.inferyx.framework.domain.StatusHolder;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
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
	HDFSInfo hdfsInfo;
	@Autowired
	HiveRegister hiveRegister;
	@Autowired
	FormulaParser formulaParser;
	@Autowired
	IDatasourceDao iDatasourceDao;
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
	
	@RequestMapping(value = "/getLatestByUsername", method = RequestMethod.GET)
	public @ResponseBody String getLatestByUsername(@RequestParam("userName") String userName,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		return registerService.getLatestByUsername(userName);
	}

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

	@RequestMapping(value = "/getAllLatestActive", method = RequestMethod.GET)
	public @ResponseBody String getAllLatestActive(@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) 
					throws JsonProcessingException, java.text.ParseException {
		/*return registerService.getAllLatestActive(type);*/
		return objectWriter.writeValueAsString(commonServiceImpl.getAllLatest(type, "Y"));
	}


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
			@RequestParam(value = "mode", required = false, defaultValue="ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
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
	@RequestMapping(value = "/getFormulaByApp", method = RequestMethod.GET)
	public @ResponseBody String getFormulaByType(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException, JSONException {
		return registerService.getFormulaByApp();
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
			@RequestParam(value = "formulaType", required = false,defaultValue ="") String[] formulaType, 
			@RequestParam(value = "resolveFlag", required = false, defaultValue = "N") String resolveFlag)
			throws JsonProcessingException, JSONException {

		return registerService.getFormulaByType2(uuid,formulaType, resolveFlag);
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
			throws IOException, SQLException {
		return registerService.getRegistryByDatasource(datasourceUuid, status);
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

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getParamSetByModel", method = RequestMethod.GET)
	public @ResponseBody String getParamSetByModel(@RequestParam("modelUuid") String modelUuid,
			@RequestParam("modelVersion") String modelVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "modelType", required = false, defaultValue = "spark") String modelType,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
			if(!modelType.equalsIgnoreCase(ExecContext.R.toString()) && !modelType.equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				return registerService.getParamSetByModel(modelUuid, modelVersion);
			} else
				return null;			
	}*/
	
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
			@RequestParam(value = "isHyperParam", required = false) String isHyperParam,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return registerService.getParamSetByAlogrithm(algorithmUuid, algorithmVersion, isHyperParam);
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
	
	@RequestMapping(value = "/getParamListByDistribution", method = RequestMethod.GET)
	public @ResponseBody List<ParamListHolder>  getParamListByDistribution(
			@RequestParam(value = "uuid") String distribution ,
			@RequestParam(value = "type", required = false) String type ) throws JsonProcessingException {
		return metadataServiceImpl.getParamListByDistribution(distribution);
	}
	
	@RequestMapping(value = "/getParamListBySimulate", method = RequestMethod.GET)
	public @ResponseBody List<ParamListHolder>  getParamListBySimulate(
			@RequestParam(value = "uuid") String simulateUuid ,
			@RequestParam(value = "type", required = false) String type ) throws JsonProcessingException {
		return metadataServiceImpl.getParamListBySimulate(simulateUuid);
	}
	
	@RequestMapping(value = "/getParamListByOperator", method = RequestMethod.GET)
	public @ResponseBody List<ParamListHolder> getParamListByOperator(
			@RequestParam(value = "uuid") String operatorUuid ,
			@RequestParam(value = "type", required = false) String type ) throws JsonProcessingException {
		return metadataServiceImpl.getParamListByOperator(operatorUuid);
	}
	
	@RequestMapping(value = "/getParamListByTrain", method = RequestMethod.GET)
	public @ResponseBody List<ParamListHolder> getParamListByTrain(
			@RequestParam(value = "uuid") String trainUuid ,
			@RequestParam(value = "version") String trainVersion,
			@RequestParam(value = "type", required = false) String type ) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {
		return metadataServiceImpl.getParamListByTrain(trainUuid, trainVersion);
	}
	
	@RequestMapping(value = "/getParamListByRule", method = RequestMethod.GET)
	public @ResponseBody List<BaseEntity> getParamListByRule(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "tags", required = false) String tags,
			@RequestParam(value = "active", required = false) String active,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "published", required = false) String published,
			@RequestParam(value = "collectionType", required = false,defaultValue="rule") String collectionType)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {		
		return metadataServiceImpl.getParamList(collectionType,type,name, userName, startDate, endDate, tags, active, null, null, published);
	}
	@RequestMapping(value = "/getParamListByReport", method = RequestMethod.GET)
	public @ResponseBody List<BaseEntity> getParamListByReport(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "tags", required = false) String tags,
			@RequestParam(value = "active", required = false) String active,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "published", required = false) String published,
			@RequestParam(value = "collectionType", required = false,defaultValue="report") String collectionType)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {		
		return metadataServiceImpl.getParamList(collectionType,type,name, userName, startDate, endDate, tags, active, null, null, published);
	}
	
	@RequestMapping(value = "/getParamListByRule", method = RequestMethod.GET, params = {"uuid", "version"})
	public @ResponseBody List<ParamListHolder> getParamListByRule(
			@RequestParam(value = "uuid", required = false) String ruleUuid,
			@RequestParam(value = "version", required = false) String ruleVersion,
			@RequestParam(value = "paramListType", required = false) MetaType paramListType,
			@RequestParam(value = "type", required = false) String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {		
		return metadataServiceImpl.getParamListByRule(ruleUuid, ruleVersion, null);
	}
	@RequestMapping(value = "/getParamListByRule2", method = RequestMethod.GET)
	public @ResponseBody List<ParamListHolder> getParamListByRule2(
			@RequestParam(value = "uuid", required = false) String ruleUuid,
			@RequestParam(value = "version", required = false) String ruleVersion,
			@RequestParam(value = "paramListType", required = false) MetaType paramListType,
			@RequestParam(value = "type", required = false) String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {		
		return metadataServiceImpl.getParamListByRule2(ruleUuid, ruleVersion, null);
	}
	
	@RequestMapping(value = "/getParamListByReport", method = RequestMethod.GET, params = {"uuid", "version"})
	public @ResponseBody List<ParamListHolder> getParamListByReport(
			@RequestParam(value = "uuid", required = false) String ruleUuid,
			@RequestParam(value = "version", required = false) String ruleVersion,
			@RequestParam(value = "paramListType", required = false) MetaType paramListType,
			@RequestParam(value = "type", required = false) String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {		
		return metadataServiceImpl.getParamListByReport(ruleUuid, ruleVersion, null);
	}
		
	@RequestMapping(value = "/getParamListByModel", method = RequestMethod.GET)
	public @ResponseBody List<BaseEntity> getParamListByModel(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "tags", required = false) String tags,
			@RequestParam(value = "active", required = false) String active,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "published", required = false) String published,
			@RequestParam(value = "collectionType", required = false,defaultValue="model") String collectionType)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {		
		return metadataServiceImpl.getParamList(collectionType,type,name, userName, startDate, endDate, tags, active, null, null, published);
	}
	/*@RequestMapping(value = "/getParamListBySimulate", method = RequestMethod.GET,params = {"simulate"})
	public @ResponseBody List<ParamList> getParamListBySimulate(	
			@RequestParam(value = "type", required = false,defaultValue = "simulate") String collectionType) throws JsonProcessingException {		
		return metadataServiceImpl.getParamList(collectionType);
	}*/
	@RequestMapping(value = "/getCommentByType", method = RequestMethod.GET)
	public @ResponseBody List<CommentView> getCommentByType(
			@RequestParam(value ="type") String type,
			@RequestParam(value = "uuid") String uuid) throws Exception {
		return metadataServiceImpl.getCommentByType(uuid,type);
	}
	
	@RequestMapping(value = "/getLovByType", method = RequestMethod.GET)
	public @ResponseBody List<Lov> getLovByType(
			@RequestParam(value ="type") String type) throws Exception {
		return metadataServiceImpl.getLovByType(type);
	}
	
	@RequestMapping(value = "/getParamListByAlgorithm", method = RequestMethod.GET)
	public @ResponseBody List<ParamListHolder> getParamListByAlgorithm(
			@RequestParam(value = "uuid", required = false) String algoUuid,
			@RequestParam(value = "version", required = false) String algoVersion,
			@RequestParam(value = "isHyperParam", required = false) String isHyperParam,
			@RequestParam(value = "type", required = false) String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {		
		return metadataServiceImpl.getParamListByAlgorithm(algoUuid, algoVersion, isHyperParam);
	}
	
	@RequestMapping(value = "/getParamListChilds", method = RequestMethod.GET)
	public @ResponseBody List<ParamListHolder> getParamListChilds(
			@RequestParam(value = "uuid", required = false) String plUuid,
			@RequestParam(value = "version", required = false) String plVersion,
			@RequestParam(value = "type", required = false) String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {		
		return metadataServiceImpl.getParamListChilds(plUuid, plVersion);
	}
	
	
	@RequestMapping(value = "/getFunctionByCriteria", method = RequestMethod.GET)
	public @ResponseBody List<Function> getFunctionByCriteria(
			@RequestParam(value ="category" , required = false) String category,
			@RequestParam(value ="inputReq" , required = false ) String inputReq,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return metadataServiceImpl.getFunctionByCriteria(category,inputReq);
	}
	@RequestMapping(value = "/getParamByApp", method = RequestMethod.GET)
	public @ResponseBody List<ParamListHolder> getParamByAppId(
			@RequestParam(value = "uuid", required = false) String uuid ,
			@RequestParam(value = "type", required = false) String type ) throws JsonProcessingException {
		return metadataServiceImpl.getParamByApp(uuid);
	}
	
	@RequestMapping(value = "/getDatastoreByDatapod", method = RequestMethod.GET)
	public @ResponseBody List<DataStore> getDatastoreByDatapod(
			@RequestParam(value ="uuid") String uuid,
			@RequestParam(value ="version" , required = false ) String version,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return metadataServiceImpl.getDatastoreByDatapod(uuid,version);
	}

	@RequestMapping(value = "/getParamListByDag", method = RequestMethod.GET)
	public @ResponseBody List<BaseEntity> getParamListByDag(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "tags", required = false) String tags,
			@RequestParam(value = "active", required = false) String active,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "published", required = false) String published,
			@RequestParam(value = "collectionType", required = false,defaultValue="dag") String collectionType)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {		
		return metadataServiceImpl.getParamList(collectionType,type,name, userName, startDate, endDate, tags, active, null, null, published);
	}
	
	@RequestMapping(value = "/getParamListByDag", method = RequestMethod.GET, params = {"uuid", "version"})
	public @ResponseBody List<ParamListHolder> getParamListByDag(
			@RequestParam(value = "uuid", required = false) String daguid,
			@RequestParam(value = "version", required = false) String dagVersion,
			@RequestParam(value = "paramListType", required = false) MetaType paramListType,
			@RequestParam(value = "type", required = false) String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {		
		return metadataServiceImpl.getParamListByDag(daguid, dagVersion, null);
	}
	
	@RequestMapping(value = "/getExecListByBatchExec", method = RequestMethod.GET)
	public @ResponseBody List<BaseEntityStatus> getBatchMetaInfoByBatchExec(
			@RequestParam(value = "uuid") String uuid,
			@RequestParam(value = "version") String version, 
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) 
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {
		return metadataServiceImpl.getBatchMetaInfoByBatchExec(uuid, version);
	}
	
	@RequestMapping(value = "/getDatasourceForFile", method = RequestMethod.GET)
	public @ResponseBody List<Datasource> getDatasourceForFile(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return metadataServiceImpl.getDatasourceForFile();
	}
	
	@RequestMapping(value = "/getDatasourceForTable", method = RequestMethod.GET)
	public @ResponseBody List<Datasource> getDatasourceForTable( 
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return metadataServiceImpl.getDatasourceForTable();
	}
	@RequestMapping(value = "/getDatasourceForStream", method = RequestMethod.GET)
	public @ResponseBody List<Datasource> getDatasourceForStream( 
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return metadataServiceImpl.getDatasourceForStream();
	}
	
	@RequestMapping(value = "/getDatapodByDatasource", method = RequestMethod.GET)
	public @ResponseBody List<BaseEntity> getDatapodByDatasource(
			@RequestParam(value = "uuid") String datasourceUuid,
			@RequestParam(value = "version", required = false) String datasourceVersion, 
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) {
		return metadataServiceImpl.getDatapodByDatasource(datasourceUuid);
	}
	
	@RequestMapping(value = "/getMessageByUuidAndVersion", method = RequestMethod.GET)
	public @ResponseBody List<Message> getMessageByUuidAndVersion(
			@RequestParam(value = "uuid") String uuid,
			@RequestParam(value = "version", required = false) String version) {
		return metadataServiceImpl.getMessageByUuidAndVersion(uuid, version);
	}
	
	@RequestMapping(value = "/getAlgorithmByLibrary",method=RequestMethod.GET)
	public @ResponseBody List<Algorithm> getAlgorithmByLibrary(@RequestParam(value="libraryType") String libraryType, 
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return metadataServiceImpl.getAlgorithmByLibrary(libraryType);
	}
	
	@RequestMapping(value = "/getDpDatapod",method=RequestMethod.GET)
	public @ResponseBody Datapod getdpByDatapod(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws FileNotFoundException, IOException {
		return metadataServiceImpl.getDatapodByType(MetaType.profile.toString(), null);
	}
	
	@RequestMapping(value = "/getRcDatapod",method=RequestMethod.GET)
	public @ResponseBody Datapod getrcDatapod(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws FileNotFoundException, IOException {
		return metadataServiceImpl.getDatapodByType(MetaType.recon.toString(), null);
	}
	
	@RequestMapping(value = "/getDqDatapod",method=RequestMethod.GET)
	public @ResponseBody Datapod getdqDatapod(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "resultType", required = false,defaultValue="null") String resultType) throws FileNotFoundException, IOException {
		return metadataServiceImpl.getDatapodByType(MetaType.dq.toString(),resultType);
	}

	@RequestMapping(value = "/getRuleDatapod",method=RequestMethod.GET)
	public @ResponseBody Datapod getRuleDatapod(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "resultType", required = false,defaultValue="null") String resultType) throws FileNotFoundException, IOException {
		return metadataServiceImpl.getDatapodByType(MetaType.rule2.toString(),resultType);
	}
	
	@RequestMapping(value = "/getGroupsByOrg",method=RequestMethod.GET)
	public @ResponseBody List<Group> getGroupsByOrg(
			@RequestParam(value = "uuid") String orgUuid, 
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws FileNotFoundException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException, JSONException {
		return  metadataServiceImpl.getGroupsByOrg(orgUuid);
	}
			
	@RequestMapping(value = "/getAppConfigByApp", method = RequestMethod.GET)
	public @ResponseBody String getAppConfigByApp(@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {
		return new ObjectMapper().writeValueAsString(metadataServiceImpl.getAppConfigByCurrentApp());
	}
	
}
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.connector.RConnector;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.DeployExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.SimulateExec;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.domain.TrainExecView;
import com.inferyx.framework.domain.TrainResult;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.RExecutor;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.MetadataServiceImpl;
import com.inferyx.framework.service.ModelExecServiceImpl;
import com.inferyx.framework.service.ModelServiceImpl;

@RestController
@RequestMapping(value = "/model")
public class ModelController {
	@Autowired
	private ModelServiceImpl modelServiceImpl;
	@Autowired
	private ModelExecServiceImpl modelExecServiceImpl;
	@Autowired
	RConnector rConnector;
	@Autowired
	RExecutor rExecutor;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	MetadataServiceImpl metadataServiceImpl;
	@Autowired
	private TrainResultViewServiceImpl trainResultViewServiceImpl;

	@RequestMapping(value = "/train/getResults", method = RequestMethod.GET)
	public String getModelResults(@RequestParam("uuid") String trainExecUUID,
			@RequestParam("version") String trainExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "rowLimit", required = false, defaultValue = "1000") int rowLimit) throws Exception {
//		rowLimit = Integer.parseInt(Helper.getPropertyValue("framework.result.row.limit"));
//		Train train = (Train) commonServiceImpl.getDomainFromDomainExec(MetaType.trainExec.toString(), trainExecUUID,
//				trainExecVersion);
//		Model model =  (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), train.getDependsOn().getRef().getVersion(), train.getDependsOn().getRef().getType().toString());
//		if (model.getType().equalsIgnoreCase(ExecContext.R.toString())
//				|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
			return modelServiceImpl.readLog2(null, MetaType.trainExec.toString(), trainExecUUID, trainExecVersion);
//		} else
//			return modelExecServiceImpl.getModelResults(train, trainExecUUID, trainExecVersion, rowLimit);
	}

	@RequestMapping(value = "/getModelScript", method = RequestMethod.GET)
	public String getModelScript(@RequestParam("uuid") String modelExecUUID,
			@RequestParam("version") String modelExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return modelExecServiceImpl.getModelScript(modelExecUUID, modelExecVersion);
	}

	@RequestMapping(value = "/exportAsPMML", method = RequestMethod.GET)
	public String exportAsPMML(@RequestParam("uuid") String trainExecUUID,
			@RequestParam("version") String trainExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return modelExecServiceImpl.exportAsPMML(trainExecUUID, trainExecVersion);
	}

	@RequestMapping(value = "/uploadCustomScript", headers = ("content-type=multipart/form-data; boundary=abcd"), method = RequestMethod.POST)
	public @ResponseBody String uploadCustomScript(@RequestParam("customScriptFile") MultipartFile customScriptFile,
			@RequestBody(required = false) Model model,
			@RequestParam(value = "modelType", required = false) String modelType,
			@RequestParam(value = "modelUuid", required = false) String modelUuid,
			@RequestParam(value = "modelVersion", required = false) String modelVersion,
			@RequestParam(value = "id", required = false) String modelId,
			@RequestParam(value = "scriptName", required = false) String scriptName)
			throws FileNotFoundException, IOException {
		/*
		 * scriptName = customScriptFile.getOriginalFilename(); Model model = new
		 * Model(); model.setBaseEntity();
		 * model.setType(Helper.getProperty("framework.script.executor"));
		 * model.setName((model.getType().equalsIgnoreCase("python") ? "py_" :
		 * model.getType().equalsIgnoreCase("r") ? "R_" :
		 * "script_")+scriptName.substring(0, scriptName.lastIndexOf("."))); String
		 * fileExtension = scriptName.substring(scriptName.lastIndexOf(".")); scriptName
		 * = model.getUuid()+"_" + model.getVersion() + fileExtension;
		 */
		return modelServiceImpl.uploadScript(customScriptFile, model, modelId, modelUuid, modelVersion, scriptName);
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public void download(@RequestParam(value = "modelExecUUID") String trainExecUuid,
			@RequestParam(value = "modelExecVersion") String trainExecVersion,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode,
			HttpServletResponse response) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		response = modelServiceImpl.download(trainExecUuid, trainExecVersion, response, runMode);
	}
	
	@RequestMapping(value = "/predict/execute", method = RequestMethod.POST)
	public boolean predict(@RequestParam("uuid") String predictUUID, @RequestParam("version") String predictVersion,
			@RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		try {

			RunMode runMode = Helper.getExecutionMode(mode);
			Predict predict = (Predict) commonServiceImpl.getOneByUuidAndVersion(predictUUID, predictVersion,
					MetaType.predict.toString());

			PredictExec predictExec = null;
			predictExec = modelServiceImpl.create(predict, execParams, null, predictExec);
			modelServiceImpl.predict(predict, execParams, predictExec, runMode);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@RequestMapping(value = "/simulate/execute", method = RequestMethod.POST)
	public boolean simulate(@RequestParam("uuid") String simulateUUID, 
			@RequestParam("version") String simulateVersion,
			@RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		try {
			RunMode runMode = Helper.getExecutionMode(mode);
			Simulate simulate = (Simulate) commonServiceImpl.getOneByUuidAndVersion(simulateUUID, simulateVersion,
					MetaType.simulate.toString());

			SimulateExec simulateExec = null;
			simulateExec = modelServiceImpl.create(simulate, execParams, null, simulateExec);
			return modelServiceImpl.simulate(simulate, execParams, simulateExec, runMode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
			
		}
	}
	
	@RequestMapping(value = "/getAllModelByType", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Model> getAllModelByType(@RequestParam("customFlag") String customFlag,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "modelType" ,required = false) String modelType) throws Exception {
		return modelServiceImpl.getAllModelByType(customFlag, modelType);
	}

	@RequestMapping(value = "/train/execute", method = RequestMethod.POST)
	public boolean train(@RequestParam("uuid") String trainUuid, 
			@RequestParam("version") String trainVersion,
			@RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		try {
			RunMode runMode = Helper.getExecutionMode(mode);
			modelServiceImpl.prepareTrain(trainUuid, trainVersion, null, execParams, runMode);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/predict/getResults", method = RequestMethod.GET)
	public List<Map<String, Object>> getPredictResults(@RequestParam("uuid") String predictExecUUID,
			@RequestParam("version") String predictExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "rowLimit", required = false, defaultValue = "1000") int rowLimit) throws Exception {
		rowLimit = Integer.parseInt(Helper.getPropertyValue("framework.result.row.limit"));
		return modelExecServiceImpl.getPredictResults(predictExecUUID, predictExecVersion, rowLimit);
	}
	
	@RequestMapping(value = "/simulate/getResults", method = RequestMethod.GET)
	public List<Map<String, Object>> getSimulateResults(@RequestParam("uuid") String simulateExecUUID,
			@RequestParam("version") String simulateExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "rowLimit", required = false, defaultValue = "1000") int rowLimit) throws Exception {
		rowLimit = Integer.parseInt(Helper.getPropertyValue("framework.result.row.limit"));
		return modelExecServiceImpl.getSimulateResults(simulateExecUUID, simulateExecVersion, rowLimit);
	}
	
	@RequestMapping(value = "/getAlgorithmByTrainExec", method = RequestMethod.GET)
	public com.inferyx.framework.domain.Algorithm getAlgorithmByTrainExec(
			@RequestParam("trainExecUUID") String trainExecUUID,
			@RequestParam("trainExecVersion") String trainExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return modelExecServiceImpl.getAlgorithmByTrainExec(trainExecUUID, trainExecVersion);
	}
	
	@RequestMapping(value = "/predict/download", method = RequestMethod.GET)
	public HttpServletResponse predictDownload(@RequestParam(value = "predictExecUUID") String predictExecUUID,
			@RequestParam(value = "predictExecVersion") String predictExecVersion,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestParam(value = "rows", defaultValue = "200") int rows,
			@RequestParam(value = "type", defaultValue = "predictExec") String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode,
			HttpServletResponse response,
			@RequestParam(value = "layout", required = false) Layout layout) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		modelExecServiceImpl.download(predictExecUUID, predictExecVersion, format, null, 0, rows, response, rows, null,
				type, null, null, runMode, layout);
		return null;
	}

	@RequestMapping(value = "/simulate/download", method = RequestMethod.GET)
	public HttpServletResponse simulateDownload(@RequestParam(value = "simulateExecUUID") String simulateExecUUID,
			@RequestParam(value = "simulateExecVersion") String simulateExecVersion,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestParam(value = "rows", defaultValue = "200") int rows,
			@RequestParam(value = "type", defaultValue = "simulateExec") String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode,
			HttpServletResponse response,
			@RequestParam(value = "layout", required = false) Layout layout) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		modelExecServiceImpl.download(simulateExecUUID, simulateExecVersion, format, null, 0, rows, response, rows,
				null, type, null, null, runMode, layout);
		return null;
	}
	
	@RequestMapping(value = "/getModelByTrainExec", method = RequestMethod.GET)
	public com.inferyx.framework.domain.Model getModelByTrainExec(
			@RequestParam("uuid") String trainExecUUID,
			@RequestParam("version") String trainExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return modelExecServiceImpl.getModelByTrainExec(trainExecUUID, trainExecVersion);
	}

	@RequestMapping(value = "/train/download", method = RequestMethod.GET)
	public void downloadLog(@RequestParam(value = "trainExecUUID") String trainExecUUID,
			@RequestParam(value = "trainExecVersion") String trainExecVersion,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode,
			HttpServletResponse response) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		response = modelServiceImpl.downloadLog(trainExecUUID, trainExecVersion, response, runMode);
	}

	@RequestMapping(value = "/getTrainByModel", method = RequestMethod.GET)
	public List<Train> getTrainByModel(@RequestParam(value = "uuid") String modelUuid,
									   @RequestParam(value = "version", required = false) String modelVersion,
									   @RequestParam(value = "type", required = false) String type,
									   @RequestParam(value = "action", required = false) String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException{
		return modelServiceImpl.getTrainByModel(modelUuid, modelVersion);
	}	

	
	@RequestMapping(value="/setStatus", method= RequestMethod.PUT)
	public boolean setStatus(@RequestParam("uuid") String uuid, 
			@RequestParam("version") String version,
			@RequestParam("status") String status,
			@RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action) {
		try {
			modelExecServiceImpl.setStatus(type,uuid,version,status);			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/train/restart",  method = RequestMethod.GET)
	public void restartTrain(@RequestParam(value = "uuid") String trainExecUuid,
						  @RequestParam(value = "version") String trainExecVersion,
							@RequestBody(required = false) ExecParams execParams,
						  @RequestParam(value = "type", required = false) String type,
						  @RequestParam(value = "action", required = false) String action,
							@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		modelExecServiceImpl.restartTrain(type, trainExecUuid, trainExecVersion, execParams, runMode);
	}
	
	@RequestMapping(value = "/predict/restart",  method = RequestMethod.GET)
	public void restartPredict(@RequestParam(value = "uuid") String trainExecUuid,
						  @RequestParam(value = "version") String trainExecVersion,
							@RequestBody(required = false) ExecParams execParams,
						  @RequestParam(value = "type", required = false) String type,
						  @RequestParam(value = "action", required = false) String action,
							@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		modelExecServiceImpl.restartPredict(type, trainExecUuid, trainExecVersion, execParams, runMode);
	}
	
	@RequestMapping(value = "/simulate/restart",  method = RequestMethod.GET)
	public void restartSimulate(@RequestParam(value = "uuid") String trainExecUuid,
						  @RequestParam(value = "version") String trainExecVersion,
							@RequestBody(required = false) ExecParams execParams,
						  @RequestParam(value = "type", required = false) String type,
						  @RequestParam(value = "action", required = false) String action,
							@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		modelExecServiceImpl.restartSimulate(type, trainExecUuid, trainExecVersion, execParams, runMode);
	}
	
	@RequestMapping(value = "/upload", headers = ("content-type=multipart/form-data; boundary=abcd"), method = RequestMethod.POST)
	public @ResponseBody String upload(@RequestParam("file") MultipartFile file,
									   @RequestParam(value = "extension") String extension,
									   @RequestParam(value = "fileType") String fileType,
									   @RequestParam(value = "type", required = false) String type,
									   @RequestParam(value = "fileName", required = false) String fileName) throws FileNotFoundException, IOException, ParseException, JSONException {
	 return modelServiceImpl.upload(file, extension, fileType, fileName, type);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getPrediction", method = RequestMethod.POST)
	public List<Map<String, Object>> getPredict(@RequestParam("uuid") String trainExecUUID,
			@RequestBody(required = false) Object feature,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		Map<String, List<Map<String, Object>>> featuresMap = new HashMap<>();
		featuresMap.put("features", (List<Map<String, Object>>) ((Map<String, Object>) feature).get("featureList"));
		Map<String, Object> featureWrapper = new HashMap<>();
		featureWrapper.put("features", featuresMap);
		return modelServiceImpl.getPrediction(trainExecUUID, featureWrapper);
	}

	@RequestMapping(value = "/getTrainResultByTrainExec", method = RequestMethod.GET)
	public String getTrainResultByTrainExec(@RequestParam("uuid") String trainExecUuid,
			@RequestParam(value = "version", required = false) String trainExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonGenerationException, JsonMappingException, IOException {
		TrainResult trainResult = trainResultViewServiceImpl.getTrainResultByTrainExec(trainExecUuid, trainExecVersion);
		return new ObjectMapper().writeValueAsString(trainResultViewServiceImpl.getOneByUuidAndVersion(trainResult.getUuid(), trainResult.getVersion()));
	}
	
	@RequestMapping(value = "/train/getTestSet", method = RequestMethod.GET)
	public List<Map<String, Object>> getTestSet(@RequestParam("uuid") String trainExecUuid,
			@RequestParam(value = "version", required = false) String trainExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return modelServiceImpl.getTrainOrTestSet(trainExecUuid, trainExecVersion, "testSet");
	}
	
	@RequestMapping(value = "/train/getTrainExecByModel", method = RequestMethod.GET)
	public List<TrainExec> getTrainExecByModel(@RequestParam("uuid") String modelUuid,
			@RequestParam(value = "version", required = false) String modelVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "active", required = false) String active,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "status", required = false) String status) throws Exception {
		return modelServiceImpl.getTrainExecByModel(modelUuid, modelVersion, active, startDate, endDate, status);
	}
	
	@RequestMapping(value = "/train/getDeployExecByModel", method = RequestMethod.GET)
	public List<DeployExec> getDeployExecByModel(@RequestParam("uuid") String modelUuid,
			@RequestParam(value = "version", required = false) String modelVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "active", required = false) String active,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "status", required = false) String status) throws Exception {
		return modelServiceImpl.getDeployExecByModel(modelUuid, modelVersion, active, startDate, endDate, status);
	}
	

	@RequestMapping(value = "/getTrainExecViewByCriteria", method = RequestMethod.GET)
	public List<TrainExecView> getTrainExecViewByCriteria(
			@RequestParam(value = "trainExecUuid", required = false) String trainExecUuid,
			@RequestParam(value = "uuid", required = false) String modelUuid,
			@RequestParam(value = "version", required = false) String modelVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "active", required = false) String active,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "status", required = false) String status) throws Exception {
		return modelServiceImpl.getTrainExecViewByCriteria(modelUuid, modelVersion, trainExecUuid, active, startDate, endDate, status);
	}
	
	@RequestMapping(value = "/train/getTrainSet", method = RequestMethod.GET)
	public List<Map<String, Object>> getTrainSet(@RequestParam("uuid") String trainExecUuid,
			@RequestParam(value = "version", required = false) String trainExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return modelServiceImpl.getTrainOrTestSet(trainExecUuid, trainExecVersion, "trainSet");
	}
	
	@RequestMapping(value = "train/getTrainSet/download", method = RequestMethod.GET)
	public void getTrainSetDownload(
			@RequestParam(value = "uuid") String trainExecUuid,
			@RequestParam(value = "version", required = false) String trainExecVersion,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestParam(value = "rows", defaultValue = "200") int rows,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode,
			HttpServletResponse response,
			@RequestParam(value = "layout", required = false) Layout layout) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		response = modelServiceImpl.download(trainExecUuid, trainExecVersion, format
				, rows, "trainSet", runMode, response, layout);
	}
	
	@RequestMapping(value = "train/getTestSet/download", method = RequestMethod.GET)
	public void getTestSetDownload(
			@RequestParam(value = "uuid") String trainExecUuid,
			@RequestParam(value = "version", required = false) String trainExecVersion,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestParam(value = "rows", defaultValue = "200") int rows,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode,
			HttpServletResponse response,
			@RequestParam(value = "layout", required = false) Layout layout) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		response = modelServiceImpl.download(trainExecUuid, trainExecVersion, format, rows
				, "testSet", runMode, response, layout);
	}
	
	@RequestMapping(value = "getTrainByTrainExec", method = RequestMethod.GET)
	public Train getTrainByTrainExec(
			@RequestParam(value = "uuid") String trainExecUuid,
			@RequestParam(value = "version", required = false) String trainExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return modelServiceImpl.getTrainByTrainExec(trainExecUuid, trainExecVersion);
	}
	
	@RequestMapping(value = "getAlgorithmByModel", method = RequestMethod.GET)
	public Algorithm getAlgorithmByModel(
			@RequestParam(value = "uuid") String modelUuid,
			@RequestParam(value = "version", required = false) String modelVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return modelServiceImpl.getAlgorithmByModel(modelUuid, modelVersion);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/calCorealtionMatrix", method = RequestMethod.POST)
	public List<Map<String, Object>> calCorealtionMatrix(@RequestBody Object metaObject) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		java.util.Map<String, Object> operator = mapper.convertValue(metaObject, java.util.Map.class);
		List<Map<String, Object>> data = modelServiceImpl.calCorealtionMatrix(operator);
		return data;

	}
	
}

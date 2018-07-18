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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.spark.ml.param.ParamMap;
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
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.connector.RConnector;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.SimulateExec;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
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

	/*@RequestMapping(value = "/train/execute", method = RequestMethod.POST)
	public boolean train(@RequestParam("uuid") String modelUUID, @RequestParam("version") String modelVersion,
			@RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		try {
			Mode runMode = Helper.getExecutionMode(mode);
			modelServiceImpl.setRunMode(runMode);
			ModelExec modelExec = null;
			List<ParamMap> paramMapList = new ArrayList<>();

			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelUUID, modelVersion,
					MetaType.model.toString());
			if (!model.getType().equalsIgnoreCase(ExecContext.R.toString())
					&& !model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				paramMapList = paramSetServiceImpl.getParamMap(execParams, modelUUID, modelVersion);
			}
			if (paramMapList.size() > 0) {

				for (ParamMap paramMap : paramMapList) {
					modelExec = modelServiceImpl.create(modelUUID, modelVersion, execParams, paramMap, modelExec);
					Thread.sleep(1000); // Should be parameterized in a class
					modelServiceImpl.train(modelUUID, modelVersion, modelExec, execParams, paramMap);
				}
			} else {
				modelExec = modelServiceImpl.create(modelUUID, modelVersion, execParams, null, modelExec);
				modelServiceImpl.train(modelUUID, modelVersion, modelExec, execParams, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}*/

	/********************** UNUSED **********************/	
	/*@RequestMapping(value = "/trainCombined", method = RequestMethod.POST)
	public ModelExec trainCombined(@RequestParam("modelUUID") String modelUUID,
			@RequestParam("modelVersion") String modelVersion, @RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		ParamMap paramMap = paramSetServiceImpl.getParamMapCombined(execParams, modelUUID, modelVersion);
		ModelExec modelExec = null;
		modelExec = modelServiceImpl.create(modelUUID, modelVersion, execParams, paramMap, modelExec);
		modelServiceImpl.train(modelUUID, modelVersion, modelExec, execParams, paramMap);
		return modelExec;

	}*/

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getModelExecByModel", method = RequestMethod.GET)
	public List<ModelExec> getModelExecByModel(@RequestParam("modelUUID") String modelUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return modelExecServiceImpl.findModelExecByModel(modelUUID);
	}*/

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

	@RequestMapping(value = "/executeScript", method = RequestMethod.GET)
	public @ResponseBody String executeScript(@RequestParam("scriptName") String scriptName,
			@RequestParam("modelType") String modelType,
			@RequestParam(value = "modelExecUuid", required = false) String modelExecUuid,
			@RequestParam(value = "modelExecVersion", required = false) String modelExecVersion)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return new ObjectMapper().writeValueAsString(
				modelServiceImpl.executeScript(modelType, scriptName, modelExecUuid, modelExecVersion, null));
	}

	/********************** UNUSED **********************/
	/*@RequestMapping(value = "/getAlgorithmByModelExec", method = RequestMethod.GET)
	public com.inferyx.framework.domain.Algorithm getAlgorithmByModelExec(
			@RequestParam("modelExecUUID") String modelExecUUID,
			@RequestParam("modelExecVersion") String modelExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {
		return modelExecServiceImpl.getAlgorithmByModelExec(modelExecUUID, modelExecVersion);
	}*/

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public void download(@RequestParam(value = "modelExecUUID") String ruleExecUUID,
			@RequestParam(value = "modelExecVersion") String ruleExecVersion,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode,
			HttpServletResponse response) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		response = modelServiceImpl.download(ruleExecUUID, ruleExecVersion, response, runMode);
	}
	
	@RequestMapping(value = "/predict/execute", method = RequestMethod.POST)
	public boolean predict(@RequestParam("uuid") String predictUUID, @RequestParam("version") String predictVersion,
			@RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode) throws Exception {
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
			@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode) throws Exception {
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
	
	@RequestMapping(value = "/getAllModelByType", method = RequestMethod.GET)
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

			TrainExec trainExec = null;
			List<ParamMap> paramMapList = new ArrayList<>();

			Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainUuid, trainVersion, MetaType.train.toString());			
			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), train.getDependsOn().getRef().getVersion(), MetaType.model.toString());
			if (train.getUseHyperParams().equalsIgnoreCase("N") 
					&& !model.getType().equalsIgnoreCase(ExecContext.R.toString())
					&& !model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				paramMapList = metadataServiceImpl.getParamMap(execParams, train.getUuid(), train.getVersion());
			}
			if (paramMapList.size() > 0) {
				for (ParamMap paramMap : paramMapList) {
					trainExec = modelServiceImpl.create(train, model, execParams, paramMap, trainExec);
					Thread.sleep(1000); // Should be parameterized in a class
					modelServiceImpl.train(train, model, trainExec, execParams, paramMap, runMode);
					trainExec = null;
				}
			} else {
				trainExec = modelServiceImpl.create(train, model, execParams, null, trainExec);
				modelServiceImpl.train(train, model, trainExec, execParams, null, runMode);
			}
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
	public HttpServletResponse download(@RequestParam(value = "predictExecUUID") String predictExecUUID,
			@RequestParam(value = "predictExecVersion") String predictExecVersion,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestParam(value = "rows", defaultValue = "1000") int rows,
			@RequestParam(value = "download", defaultValue = "Y") String download,
			@RequestParam(value = "offset", defaultValue = "0") int offset,
			@RequestParam(value = "limit", defaultValue = "200") int limit,
			@RequestParam(value = "sortBy", required = false) String sortBy,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "type", defaultValue = "predictExec") String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "requestId", required = false) String requestId,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode,
			HttpServletResponse response) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		modelExecServiceImpl.download(predictExecUUID, predictExecVersion, format, download, offset, limit, response, rows,
				sortBy,type, order, requestId, runMode);
		return null;
	}
	@RequestMapping(value = "/simulate/download", method = RequestMethod.GET)
	public HttpServletResponse download(@RequestParam(value = "simulateExecUUID") String simulateExecUUID,
			@RequestParam(value = "simulateExecVersion") String simulateExecVersion,
			@RequestParam(value = "format", defaultValue = "excel") String format,
			@RequestParam(value = "rows", defaultValue = "1000") int rows,
			@RequestParam(value = "download", defaultValue = "Y") String download,
			@RequestParam(value = "offset", defaultValue = "0") int offset,
			@RequestParam(value = "limit", defaultValue = "200") int limit,
			@RequestParam(value = "sortBy", required = false) String sortBy,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "type", defaultValue = "simulateExec") String type,
			@RequestParam(value = "action", required = false) String action,
			/*@RequestParam(value = "requestId", required = false) String requestId,*/
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode,
			HttpServletResponse response) throws Exception {
		RunMode runMode = Helper.getExecutionMode(mode);
		modelExecServiceImpl.download(simulateExecUUID, simulateExecVersion, format, download, offset, limit, response, rows,
				sortBy,type, order, "1", runMode);
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
	
	@RequestMapping(value = "/train/kill",  method = RequestMethod.GET)
	public void killTrain(@RequestParam(value = "uuid") String trainExecUuid,
						  @RequestParam(value = "version") String trainExecVersion,
						  @RequestParam(value = "type", required = false) String type,
						  @RequestParam(value = "action", required = false) String action) {
		modelExecServiceImpl.kill(trainExecUuid, trainExecVersion, MetaType.trainExec);
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
	
	@RequestMapping(value = "/predict/kill",  method = RequestMethod.GET)
	public void killPredict(@RequestParam(value = "uuid") String trainExecUuid,
						  @RequestParam(value = "version") String trainExecVersion,
						  @RequestParam(value = "type", required = false) String type,
						  @RequestParam(value = "action", required = false) String action) {
		modelExecServiceImpl.kill(trainExecUuid, trainExecVersion, MetaType.predictExec);
	}
	
	@RequestMapping(value = "/simulate/kill",  method = RequestMethod.GET)
	public void killSimulate(@RequestParam(value = "uuid") String trainExecUuid,
						  @RequestParam(value = "version") String trainExecVersion,
						  @RequestParam(value = "type", required = false) String type,
						  @RequestParam(value = "action", required = false) String action) {
		modelExecServiceImpl.kill(trainExecUuid, trainExecVersion, MetaType.simulateExec);
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
	

}
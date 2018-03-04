package com.inferyx.framework.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.ml.param.ParamMap;
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
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ModelExec;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.RExecutor;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.ModelExecServiceImpl;
import com.inferyx.framework.service.ModelServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

@RestController
@RequestMapping(value = "/model")
public class ModelController {
	@Autowired
	private ModelServiceImpl modelServiceImpl;
	@Autowired
	private ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	private ModelExecServiceImpl modelExecServiceImpl;
	@Autowired
	RConnector rConnector;
	@Autowired
	RExecutor rExecutor;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public boolean train(@RequestParam("uuid") String modelUUID, 
			@RequestParam("version")String modelVersion,
		    @RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value="mode", required=false, defaultValue="ONLINE") String mode)
			throws Exception {
		try {
			Mode runMode = Helper.getExecutionMode(mode);
			modelServiceImpl.setRunMode(runMode);
			List<ParamMap> paramMapList = new ArrayList<>();
			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelUUID, modelVersion, MetaType.model.toString());
			if(!model.getType().equalsIgnoreCase(ExecContext.R.toString()) && !model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				paramMapList = paramSetServiceImpl.getParamMap(execParams, modelUUID, modelVersion);
			}
			if(paramMapList.size()>0){
				for(ParamMap paramMap:paramMapList){
					ModelExec modelExec = modelServiceImpl.create(modelUUID, modelVersion, execParams,paramMap, null);
					Thread.sleep(1000); //  Should be parameterized in a class
					modelServiceImpl.train(modelUUID, modelVersion, modelExec, execParams, paramMap);
				}
			} else {
				ModelExec modelExec = modelServiceImpl.create(modelUUID, modelVersion, execParams,null, null);
				modelServiceImpl.train(modelUUID, modelVersion, modelExec, execParams, null);
			}				
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/trainCombined", method = RequestMethod.POST)
	public ModelExec trainCombined(@RequestParam("modelUUID") String modelUUID, 
			@RequestParam("modelVersion")String modelVersion,
		    @RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws Exception {
		ParamMap paramMap = paramSetServiceImpl.getParamMapCombined(execParams, modelUUID, modelVersion);
		
		ModelExec modelExec = modelServiceImpl.create(modelUUID, modelVersion, execParams,paramMap, null);
		modelServiceImpl.train(modelUUID, modelVersion, modelExec, execParams, paramMap);
		return modelExec;
		
	}
	
	@RequestMapping(value = "/getModelExecByModel", method = RequestMethod.GET)
	public List<ModelExec> getModelExecByModel(@RequestParam("modelUUID") String modelUUID,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException {		
		return modelExecServiceImpl.findModelExecByModel(modelUUID);
	}
	
	@RequestMapping(value = "/getResults", method = RequestMethod.GET)
	public List<String> getModelResults(@RequestParam("uuid") String modelExecUUID, 
			@RequestParam("version") String modelExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {	
		Model model = (Model) commonServiceImpl.getDomainFromDomainExec(MetaType.modelExec.toString(), modelExecUUID, modelExecVersion);
		if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
			return modelServiceImpl.readLog(null, MetaType.modelExec.toString(), modelExecUUID, modelExecVersion);
		}else
			return modelExecServiceImpl.getModelResults(modelExecUUID, modelExecVersion);
	}

	@RequestMapping(value = "/getModelScript", method = RequestMethod.GET)
	public String getModelScript(@RequestParam("uuid") String modelExecUUID, 
			@RequestParam("version") String modelExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {	
			return modelExecServiceImpl.getModelScript(modelExecUUID, modelExecVersion);
	}

	@RequestMapping(value = "/exportAsPMML", method = RequestMethod.GET)
	public String exportAsPMML(@RequestParam("uuid") String modelExecUUID, 
			@RequestParam("version") String modelExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {		
		return modelExecServiceImpl.exportAsPMML(modelExecUUID, modelExecVersion);
	}
	
	@RequestMapping(value = "/uploadCustomScript", headers = ("content-type=multipart/form-data; boundary=abcd"), method = RequestMethod.POST)
	public @ResponseBody String uploadCustomScript(@RequestParam("customScriptFile") MultipartFile customScriptFile,
									 @RequestBody(required = false) Model model,
									 @RequestParam(value = "modelType", required = false) String modelType,
									 @RequestParam(value = "modelUuid", required = false) String modelUuid,
									 @RequestParam(value = "modelVersion", required = false) String modelVersion,
									 @RequestParam(value = "id", required = false) String modelId,
									 @RequestParam(value = "scriptName", required = false) String scriptName) throws FileNotFoundException, IOException {
		/*scriptName = customScriptFile.getOriginalFilename();
		Model model = new Model();
		model.setBaseEntity();
		model.setType(Helper.getProperty("framework.script.executor"));
		model.setName((model.getType().equalsIgnoreCase("python") ? "py_" : model.getType().equalsIgnoreCase("r") ? "R_" : "script_")+scriptName.substring(0, scriptName.lastIndexOf(".")));
		String fileExtension = scriptName.substring(scriptName.lastIndexOf("."));
		scriptName = model.getUuid()+"_" + model.getVersion() + fileExtension;*/
		return modelServiceImpl.uploadScript(customScriptFile, model, modelId, modelUuid, modelVersion, scriptName);
	}
	
	@RequestMapping(value = "/executeScript", method = RequestMethod.GET)
	public @ResponseBody String executeScript(@RequestParam("scriptName") String scriptName,
			 						@RequestParam("modelType") String modelType,
			 						@RequestParam(value = "modelExecUuid", required = false) String modelExecUuid,
									@RequestParam(value = "modelExecVersion", required = false) String modelExecVersion) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return new ObjectMapper().writeValueAsString(modelServiceImpl.executeScript(modelType, scriptName, modelExecUuid, modelExecVersion, null));
	}
}
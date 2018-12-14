/**
 * 
 */
package com.inferyx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.FrameworkThreadServiceImpl;
import com.inferyx.framework.service.ModelServiceImpl;

/**
 * @author joy
 *
 */
@RestController
@RequestMapping(value = "/starter/predict")
public class PredictController {
	
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private FrameworkThreadServiceImpl frameworkThreadServiceImpl;
	@Autowired
	private ModelServiceImpl modelServiceImpl;

	/**
	 * 
	 */
	public PredictController() {
		// TODO Auto-generated constructor stub
	}
	
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public boolean predict(@RequestParam("uuid") String predictUUID, @RequestParam("version") String predictVersion,
			@RequestParam("userId") String userUuid,
			@RequestParam("appId") String appId,
			@RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode) throws Exception {
		User user = (User) commonServiceImpl.getOneByUuidAndVersion(userUuid, null, MetaType.user.toString());
		Application app = (Application) commonServiceImpl.getOneByUuidAndVersion(appId, null, MetaType.application.toString());
		MetaIdentifier appRef = new MetaIdentifier(MetaType.application, appId, app.getVersion());
		MetaIdentifierHolder appInfo = new MetaIdentifierHolder(appRef);
		frameworkThreadServiceImpl.setSession(user.getName(), appInfo);
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
	
	@RequestMapping(value = "/sample", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String helloController() {
		return " Hello Controller!!! ";
	}
	
	@RequestMapping(value = "/train/execute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean predictFromTrain(@RequestParam("uuid") String trainExecUUID, @RequestParam("version") String trainExecVersion,
			@RequestParam("sourceType") String sourceType,@RequestParam(value = "sourceUUID", required = false) String sourceUUID, 
			@RequestParam(value = "sourceVersion", required = false) String sourceVersion, 
			@RequestParam("userId") String userUuid,
			@RequestParam("appId") String appId,
			@RequestParam("targetType") String targetType,@RequestParam(value = "targetUUID", required = false) String targetUUID, 
			@RequestParam(value = "targetVersion", required = false) String targetVersion, 
			@RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "BATCH") String mode) throws Exception {
		
		User user = (User) commonServiceImpl.getOneByUuidAndVersion(userUuid, null, MetaType.user.toString());
		Application app = (Application) commonServiceImpl.getOneByUuidAndVersion(appId, null, MetaType.application.toString());
		MetaIdentifier appRef = new MetaIdentifier(MetaType.application, appId, app.getVersion());
		MetaIdentifierHolder appInfo = new MetaIdentifierHolder(appRef);
		frameworkThreadServiceImpl.setSession(user.getName(), appInfo);
		
		try {

			RunMode runMode = Helper.getExecutionMode(mode);
			TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExecUUID, trainExecVersion,
					MetaType.trainExec.toString());
			Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainExec.getDependsOn().getRef().getUuid(), 
					trainExec.getDependsOn().getRef().getVersion(),
					MetaType.train.toString());
			
			// Create Predict object
			MetaIdentifier trainInfoRef = new MetaIdentifier(MetaType.train, train.getUuid(), train.getVersion(), train.getName());
			MetaIdentifierHolder trainInfo = new MetaIdentifierHolder(trainInfoRef);
			MetaIdentifier sourceIdentifier = new MetaIdentifier(Helper.getMetaType(sourceType), sourceUUID, sourceVersion);
			MetaIdentifierHolder sourceIdHolder = new MetaIdentifierHolder(sourceIdentifier);
			MetaIdentifier targetIdentifier = new MetaIdentifier(Helper.getMetaType(targetType), targetUUID, targetVersion);
			MetaIdentifierHolder targetIdHolder = new MetaIdentifierHolder(targetIdentifier);
			
			Predict predict = new Predict();
			predict.setBaseEntity();
			predict.setTrainInfo(trainInfo);
			predict.setFeatureAttrMap(train.getFeatureAttrMap());
			predict.setIncludeFeatures(train.getIncludeFeatures());
			predict.setSource(sourceIdHolder);
			predict.setTarget(targetIdHolder);
			predict.setDependsOn(train.getDependsOn());
			predict.setAppInfo(train.getAppInfo());
			predict.setCreatedBy(train.getCreatedBy());
//			Predict predict = (Predict) commonServiceImpl.getOneByUuidAndVersion(predictUUID, predictVersion,
//					MetaType.predict.toString());

			PredictExec predictExec = null;
			predictExec = modelServiceImpl.create(predict, execParams, null, predictExec);
			modelServiceImpl.predict(predict, execParams, predictExec, runMode);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	

}

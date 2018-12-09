/**
 * 
 */
package com.inferyx.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
	

}

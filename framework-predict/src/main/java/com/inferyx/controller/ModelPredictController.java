/**
 * 
 */
package com.inferyx.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.domain.ModelTrainDomain;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.FrameworkThreadServiceImpl;
import com.inferyx.framework.service.ModelServiceImpl;

/**
 * @author joy
 *
 */
@RestController
@RequestMapping(value = "/starter/model")
public class ModelPredictController {
	
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private FrameworkThreadServiceImpl frameworkThreadServiceImpl;
	@Resource(name="modelMap")
	private ConcurrentHashMap<String, ModelTrainDomain> modelMap;
	
	static final Logger logger = Logger.getLogger(ModelPredictController.class);

	/**
	 * 
	 */
	public ModelPredictController() {
		// TODO Auto-generated constructor stub
	}

	@RequestMapping(value = "/load", method = RequestMethod.POST)
	public boolean load(@RequestParam("uuid") String trainExecUuid, 
						@RequestParam("version") String trainExecVersion, 
						@RequestParam("userId") String userUuid,
						@RequestParam("appId") String appId) throws JSONException, ParseException, IOException {
		
		String modelTrainKey = null;
		User user = (User) commonServiceImpl.getOneByUuidAndVersion(userUuid, null, MetaType.user.toString());
		Application app = (Application) commonServiceImpl.getOneByUuidAndVersion(appId, null, MetaType.application.toString());
		MetaIdentifier appRef = new MetaIdentifier(MetaType.application, appId, app.getVersion());
		MetaIdentifierHolder appInfo = new MetaIdentifierHolder(appRef);
		frameworkThreadServiceImpl.setSession(user.getName(), appInfo);
		
		TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExecUuid, trainExecVersion, MetaType.trainExec.toString(), "N");
		Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainExec.getDependsOn().getRef().getUuid(), 
																		trainExec.getDependsOn().getRef().getVersion(), 
																		MetaType.train.toString(), "N");
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), 
																		train.getDependsOn().getRef().getVersion(), 
																		MetaType.model.toString(), "N");
		ModelTrainDomain modelTrainDomain = new ModelTrainDomain(model, trainExec); 
		modelTrainKey = trainExecUuid+"_"+trainExecVersion+"_"+model.getUuid();
		modelMap.put(modelTrainKey, modelTrainDomain);
		logger.info("Loaded model - " + modelTrainKey);
		return true;
	}
	
}

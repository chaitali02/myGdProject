/**
 * 
 */
package com.inferyx.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.inferyx.module.TomcatStarter;

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
	@Resource(name="trainToModelMap")
	private ConcurrentHashMap<String, String> trainToModelMap;
	
	static final Logger logger = Logger.getLogger(ModelPredictController.class);

	/**
	 * 
	 */
	public ModelPredictController() {
		// TODO Auto-generated constructor stub
	}

	@RequestMapping(value = "/deploy", method = RequestMethod.POST)
	public boolean deploy(@RequestParam("trainExec_uuid") String trainExecUuid, 
						@RequestParam("version") String trainExecVersion, 
						@RequestParam("userId") String userUuid,
						@RequestParam("appId") String appId) throws JSONException, ParseException, IOException {
		
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
//		String modelTrainKey = trainExecUuid+"_"+trainExecVersion+"_"+model.getUuid();
		modelMap.put(model.getUuid(), modelTrainDomain);
		trainToModelMap.put(trainExecUuid, model.getUuid());
		logger.info("Deployed model - " + model.getUuid());
		return true;
	}
	
	@RequestMapping(value = "/undeploy", method = RequestMethod.POST)
	public boolean undeploy(@RequestParam("trainExec_uuid") String trainExecUuid, 
						@RequestParam(name="version", required=false) String trainExecVersion, 
						@RequestParam("userId") String userUuid,
						@RequestParam("appId") String appId) throws JSONException, ParseException, IOException {
		String modelUuid = null;
		if (trainToModelMap.containsKey(trainExecUuid)) {
			modelUuid = trainToModelMap.get(trainExecUuid);
			trainToModelMap.remove(trainExecUuid);
			modelMap.remove(modelUuid);
		}
		logger.info("Undeployed model - " + modelUuid);
		return true;
	}
	
	@RequestMapping(value = "/getDeployStatus", method = RequestMethod.GET)
	public boolean getDeployStatus(@RequestParam("trainExec_uuid") String trainExecUuid, 
						@RequestParam(name="version", required=false) String trainExecVersion, 
						@RequestParam("userId") String userUuid,
						@RequestParam("appId") String appId) throws JSONException, ParseException, IOException {
		String modelUuid = null;
		if (trainToModelMap.containsKey(trainExecUuid)) {
			modelUuid = trainToModelMap.get(trainExecUuid);
			if (modelMap.containsKey(modelUuid)) {
				logger.info(" Model " + modelUuid + " for trainExec " + trainExecUuid + " is deployed ");
				return true;
			}
		}
		logger.info(" Model for trainExec " + trainExecUuid + " is not deployed ");
		return false;
	}	
	
	@RequestMapping(value = "/getDeployStatusByModel", method = RequestMethod.GET)
	public List<String> getDeployStatusByModel(@RequestParam("model_uuid") String model_uuid,
			@RequestParam("trainExecUuid") String trainExecUuid,
			@RequestParam("userId") String userUuid,
			@RequestParam("appId") String appUuid) throws JSONException, ParseException, IOException {
		List<String> trainExecList = new ArrayList<>();
		if(modelMap.containsKey(model_uuid)) {
			ModelTrainDomain modelTrainDomain = modelMap.get(model_uuid);
			boolean trainExecDployStatus = getDeployStatus(modelTrainDomain.getTrainExec().getUuid(), null, userUuid, appUuid);
			if(trainExecDployStatus) {
				trainExecList.add(modelTrainDomain.getTrainExec().getUuid());
			}
			return trainExecList;
		} else {
			return trainExecList;
		}
	}
}

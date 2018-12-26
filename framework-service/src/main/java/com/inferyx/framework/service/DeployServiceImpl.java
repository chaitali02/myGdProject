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
package com.inferyx.framework.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.DeployExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;

/**
 * @author Ganesh
 */
@Service
public class DeployServiceImpl {

	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private SecurityServiceImpl securityServiceImpl;
	@Autowired
	private ModelServiceImpl modelServiceImpl;
//	@Autowired
//	private RestTemplate restTemplate;
	
	static Logger logger = Logger.getLogger(DeployServiceImpl.class);
	
	public boolean deploy(String trainExecUuid, String trainExecVersion) throws Exception {
		DeployExec deployExec = create(trainExecUuid, trainExecVersion);
		boolean response = false;
		try {
			String processStatus = null;
			try {
				processStatus = getProcessStatus(trainExecUuid, trainExecVersion);
				logger.info("Process status : " + processStatus);
			} catch (Exception e) {
				//Process has not started.
				logger.info("Process has not started.");
				if(e.toString().contains("Connection refused")) {
					throw new RuntimeException("Process has not started.");
				} else {
					throw new RuntimeException("Some error occured.");
				}
			}
			Model model = getModelByTrainExec(trainExecUuid, trainExecVersion);
			List<String> trainExecList = getDeployStatusByModel(model.getUuid(), trainExecUuid, trainExecVersion);
			if(!trainExecList.isEmpty()) {
				for(String deployedTrainExecUuid : trainExecList) {
					unDeploy(deployedTrainExecUuid, null);
				}
			}
			boolean deployStatus = getDeployStatus(trainExecUuid, trainExecVersion);
			
			if(!deployStatus && processStatus.equalsIgnoreCase("ALIVE")) {
				deployExec = (DeployExec) commonServiceImpl.setMetaStatus(deployExec, MetaType.deployExec, Status.Stage.InProgress);
				//deploy code
				Application application = commonServiceImpl.getApp();
				MetaIdentifierHolder userInfo = securityServiceImpl.getuserInfo();
				String deployURL = "http://localhost:"+application.getDeployPort()
									+ "/starter/model/deploy?"
									+ "trainExec_uuid="+trainExecUuid
									+ "&version="+trainExecVersion
									+ "&userId="+userInfo.getRef().getUuid()
									+ "&appId="+application.getUuid();
				
				RestTemplate restTemplate = new RestTemplate();
				HashMap<String, String> parameters = new HashMap<>();
				parameters.put("trainExec_uuid", trainExecUuid);
				parameters.put("version", trainExecVersion);
				parameters.put("userId", userInfo.getRef().getUuid());
				parameters.put("appId", application.getUuid());
				response = restTemplate.postForObject(deployURL, null, boolean.class, parameters);
				if(response) {
					deployExec = (DeployExec) commonServiceImpl.setMetaStatus(deployExec, MetaType.deployExec, Status.Stage.Completed);
					
				} else {
					deployExec = (DeployExec) commonServiceImpl.setMetaStatus(deployExec, MetaType.deployExec, Status.Stage.Failed);
				}
				System.out.println("response: "+response);
			} 

			return response;
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.deployExec, deployExec.getUuid(), deployExec.getVersion()));
			deployExec = (DeployExec) commonServiceImpl.setMetaStatus(deployExec, MetaType.deployExec, Status.Stage.Failed);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Some error occured, can not deploy.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Some error occured, can not deploy.");
		}
		
	}
	
	public DeployExec create(String trainExecUuid, String trainExecVersion) throws Exception {
		DeployExec deployExec = new DeployExec();
		deployExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.trainExec, trainExecUuid, trainExecVersion)));
		Application application = commonServiceImpl.getApp();
		MetaIdentifierHolder userInfo = securityServiceImpl.getuserInfo();
		Model model = getModelByTrainExec(trainExecUuid, trainExecVersion);
		
		String url = "http://localhost:"+application.getDeployPort()
					+ "/starter/predict/getPrediction?"
					+ "model_uuid="+model.getUuid()
					+ "&userId="+userInfo.getRef().getUuid()
					+ "&appId="+application.getUuid();
				
		deployExec.setUrl(url);
		deployExec.setBaseEntity();
		deployExec = (DeployExec) commonServiceImpl.setMetaStatus(deployExec, MetaType.deployExec, Status.Stage.NotStarted);
		return deployExec;
	}
	
	public Model getModelByTrainExec(String trainExecUuid, String trainExecVersion) throws JsonProcessingException {
		TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExecUuid, trainExecVersion, MetaType.trainExec.toString(), "N");
		MetaIdentifier trainMI = trainExec.getDependsOn().getRef();
		Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainMI.getUuid(), trainMI.getVersion(), MetaType.train.toString(), "N");
		MetaIdentifier modelMI = train.getDependsOn().getRef();
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelMI.getUuid(), modelMI.getVersion(), MetaType.model.toString(), "N");
		return model;
	}
	
	public boolean getDeployStatus(String trainExecUuid, String trainExecVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Application application = commonServiceImpl.getApp();
		MetaIdentifierHolder userInfo = securityServiceImpl.getuserInfo();
		String deployURL = "http://localhost:"+application.getDeployPort()
							+ "/starter/model/getDeployStatus?"
							+ "trainExec_uuid="+trainExecUuid
							+ "&version="+trainExecVersion
							+ "&userId="+userInfo.getRef().getUuid()
							+ "&appId="+application.getUuid();
		
		RestTemplate restTemplate = new RestTemplate();
		boolean response = restTemplate.getForObject(deployURL, boolean.class);
		System.out.println("response: "+response);
		
		return response;
	}
	
	public List<String> getDeployStatusByModel(String modelUuid, String trainExecUuid, String trainExecVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Application application = commonServiceImpl.getApp();
		MetaIdentifierHolder userInfo = securityServiceImpl.getuserInfo();
		String deployURL = "http://localhost:"+application.getDeployPort()
							+ "/starter/model/getDeployStatusByModel?"
							+ "model_uuid="+modelUuid
							+ "&trainExecUuid="+trainExecUuid
							+ "&userId="+userInfo.getRef().getUuid()
							+ "&appId="+application.getUuid();
		
		RestTemplate restTemplate = new RestTemplate();
		List<String> response = restTemplate.getForObject(deployURL, List.class);
		System.out.println("response: "+response);
		
		return response;
	}
	
	public boolean unDeploy(String trainExecUuid, String trainExecVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		DeployExec deployExec = modelServiceImpl.getDeployExecByTrainExec(trainExecUuid, trainExecVersion, null, null);
		Application application = commonServiceImpl.getApp();
		MetaIdentifierHolder userInfo = securityServiceImpl.getuserInfo();
		String deployURL = "http://localhost:"+application.getDeployPort()
							+ "/starter/model/undeploy?"
							+ "trainExec_uuid="+trainExecUuid
							+ "&version="+trainExecVersion
							+ "&userId="+userInfo.getRef().getUuid()
							+ "&appId="+application.getUuid();

		RestTemplate restTemplate = new RestTemplate();
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("trainExec_uuid", trainExecUuid);
		parameters.put("version", trainExecVersion);
		parameters.put("userId", userInfo.getRef().getUuid());
		parameters.put("appId", application.getUuid());
		boolean response = restTemplate.postForObject(deployURL, null, boolean.class, parameters);
		if(response) {
			deployExec.setActive("N");
			commonServiceImpl.delete(deployExec.getId(), MetaType.deployExec.toString() );
		}
		
		return response;
	}
	
	public String getProcessStatus(String trainExecUuid, String trainExecVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Application application = commonServiceImpl.getApp();
//		InetAddress ip;
//        String hostname;
//        
//        try {
//			ip = InetAddress.getLocalHost();
//            hostname = ip.getHostName();
//            System.out.println("Your current IP address : " + ip);
//            System.out.println("Your current Hostname : " + hostname);
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		String url = "http://localhost:"+application.getDeployPort()+"/starter/monitor/getProcessStatus";
		RestTemplate restTemplate = new RestTemplate();
		return  restTemplate.getForObject(url, String.class);		
	}
	
	/*public String getProcessStatus(String port) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String url = "http://localhost:"+port+"/starter/monitor/getProcessStatus";
		return  this.restTemplate.getForObject(url, String.class);		
	}*/
	
	public String startProcess(String trainExecUuid, String trainExecVersion) throws Exception {
		Application application = commonServiceImpl.getApp();
		String path = "/app/framework_predict";
//		new TomcatStarter(application.getDeployPort());
//		String path=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//		path = path.replaceAll("framework-service", "framework-predict").concat("com/inferyx/module/");
		System.out.println("absolute path: "+path);
		
		ProcessBuilder pb = new ProcessBuilder(path+"/bin/predictStarter", application.getDeployPort(), "&");
//		pb.environment().put("java", path);
		Process p = pb.start();
		BufferedReader errBuffRdrIn = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String errLine = "";
		while((errLine = errBuffRdrIn.readLine()) != null) {
		// display each output line form python script
			logger.error(errLine);
		}
		return "Process started successfully.";	 
	}
	
	public String stopProcess(String trainExecUuid, String trainExecVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
//		Application application = commonServiceImpl.getApp();
//		MetaIdentifierHolder userInfo = securityServiceImpl.getuserInfo();
//		String url = "http://localhost:8088/starter/monitor/getProcessStatus"
//				/*+ "?"
//				+ "trainExec_uuid="+trainExecUuid
//				+ "&version="+trainExecVersion
//				+ "&userId="+userInfo.getRef().getUuid()
//				+ "&appId="+application.getUuid()*/;
//		//httpServletResponse.sendRedirect(url);
//		RestTemplate restTemplate = new RestTemplate();
//		String resp = restTemplate.getForObject(url, String.class);
		
		return "Process stopped successfully.";
	}
}

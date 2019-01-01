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
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
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
	@Resource(name="portPIdMap")
	private ConcurrentHashMap<Integer, Integer> portPIdMap;
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
			logger.info(" Before fetching model from trainExec " );
			Model model = getModelByTrainExec(trainExecUuid, trainExecVersion);
			logger.info(" Model fetched from trainExec " );
			List<String> trainExecList = getDeployStatusByModel(model.getUuid(), trainExecUuid, trainExecVersion);
			logger.info(" Deploy status obtained ");
			if(!trainExecList.isEmpty()) {
				logger.info(" Going to undeploy ");
				for(String deployedTrainExecUuid : trainExecList) {
					unDeploy(deployedTrainExecUuid, null);
				}
				logger.info(" Undeployed ");
			}
			logger.info(" Before fetching deploy status ");
			boolean deployStatus = getDeployStatus(trainExecUuid, trainExecVersion);
			logger.info(" Deploy status : " + deployStatus);
			
			if(!deployStatus && processStatus.equalsIgnoreCase("ALIVE")) {
				deployExec = (DeployExec) commonServiceImpl.setMetaStatus(deployExec, MetaType.deployExec, Status.Stage.InProgress);
				//deploy code
				Application application = commonServiceImpl.getApp();
				MetaIdentifierHolder userInfo = securityServiceImpl.getuserInfo();
				String deployURL = "http://localhost:"+application.getDeployPort()
									+ "/web/predict/starter/model/deploy?"
									+ "trainExec_uuid="+trainExecUuid
									+ "&version="+trainExecVersion
									+ "&userId="+userInfo.getRef().getUuid()
									+ "&appId="+application.getUuid();
				
				logger.info(" Deploy URL : " + deployURL);
				HashMap<String, String> parameters = new HashMap<>();
				parameters.put("trainExec_uuid", trainExecUuid);
				parameters.put("version", trainExecVersion);
				parameters.put("userId", userInfo.getRef().getUuid());
				parameters.put("appId", application.getUuid());
//				AsyncRestTemplate restTemplate = new AsyncRestTemplate();
				RestTemplate restTemplate = new RestTemplate();
//				HttpMethod method = HttpMethod.POST;
				/*Class<Boolean> responseType = Boolean.class;
				//create request entity using HttpHeaders
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<String> requestEntity = new HttpEntity<String>("params", headers);
				ListenableFuture<ResponseEntity<Boolean>> future = restTemplate.exchange(deployURL, method, requestEntity, responseType, parameters);
				try {
					//waits for the result
					ResponseEntity<Boolean> entity = future.get();
					//prints body source code for the given URL
					System.out.println(entity.getBody());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}*/
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
			
			deployExec.setActive("N");
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
					+ "/web/predict/starter/predict/getPrediction?"
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
							+ "/web/predict/starter/model/getDeployStatus?"
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
							+ "/web/predict/starter/model/getDeployStatusByModel?"
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
							+ "/web/predict/starter/model/undeploy?"
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
	
	public String getProcessStatus(String trainExecUuid, String trainExecVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, InterruptedException, ExecutionException {
		Application application = commonServiceImpl.getApp();
		String url = "http://localhost:"+application.getDeployPort()+"/web/predict/starter/monitor/getProcessStatus";
		AsyncRestTemplate restTemplate = new AsyncRestTemplate();
		HttpMethod method = HttpMethod.GET;
		Class<String> responseType = String.class;
		//create request entity using HttpHeaders
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>("params", headers);
		ListenableFuture<ResponseEntity<String>> future = restTemplate.exchange(url, method, requestEntity, responseType);

		//waits for the result
		ResponseEntity<String> entity = future.get();
		//prints body source code for the given URL
		System.out.println(entity.getBody());
		return entity.getBody();
	
	}
	
	public String startProcess(String trainExecUuid, String trainExecVersion) throws Exception {
		Application application = commonServiceImpl.getApp();
		String path = "/app/framework_predict";
		System.out.println("absolute path: "+path);
		
		ProcessBuilder pb = new ProcessBuilder(path+"/bin/predictStarter", application.getDeployPort(), "&");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Process process = null;
				try {
					process = pb.start();
					if(process.getClass().getName().equals("java.lang.UNIXProcess")) {
						  /* get the PID on unix/linux systems */
						  try {
						    Field f = process.getClass().getDeclaredField("pid");
						    f.setAccessible(true);
						    int pid = f.getInt(process);
						    System.out.println(" Process Id : " + pid);
						  } catch (Throwable e) {
								e.printStackTrace();
						  }
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				BufferedReader brErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String line = null;
				String errLine = null;
				try {
					while ((errLine = brErr.readLine()) != null || (line = br.readLine()) != null) {
						System.out.print((StringUtils.isNotBlank(line) ? line + "\n" : "")
								+ (StringUtils.isNotBlank(errLine) ? errLine + "\n" : ""));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		Thread.sleep(18000);
		try {
			int tomcatPId = getProcessIdForDeployPort();
		    if(tomcatPId > 0) {
			    portPIdMap.put(Integer.parseInt(application.getDeployPort()), tomcatPId);
		    }
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException | ParseException
				| InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("Process started successfully.");
		return "Process started successfully.";	 
	}
	
	public int getProcessIdForDeployPort() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException, InterruptedException {
		Application application = commonServiceImpl.getApp();
		Process process = Runtime.getRuntime().exec("lsof -i:"+application.getDeployPort());
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		int count = 0;
		while((line = buffReader.readLine()) != null) {
			String []splits = line.split(" ");
			if(count == 1) {
				for(int i=0; i<splits.length; i++) {
					if(!splits[i].equalsIgnoreCase("") && i>0) {
						logger.info("tomcatPId: "+splits[i]);
						return Integer.parseInt(splits[i]);
					}
				}
			}
			count++;
		}
		return 0;
	}
	
	public String stopProcess(String trainExecUuid, String trainExecVersion) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException, InterruptedException {
		Application application = commonServiceImpl.getApp();
		Integer tomcatPId  = portPIdMap.get(Integer.parseInt(application.getDeployPort()));
		if(tomcatPId != null && tomcatPId > 0) {
			logger.info("stopping process with PId: "+tomcatPId);
			Runtime.getRuntime().exec("kill -9 "+tomcatPId);
			Thread.sleep(3000);
		}		
		return "Process stopped successfully.";
	}
}

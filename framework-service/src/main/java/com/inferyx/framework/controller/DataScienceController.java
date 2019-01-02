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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.service.DeployServiceImpl;

/**
 * @author Ganesh
 *
 */
@RestController
@RequestMapping(value = "/datascience")
public class DataScienceController {
	@Autowired
	private DeployServiceImpl deployServiceImpl;
	
	@RequestMapping(value = "/deploy", method = RequestMethod.GET)
	public boolean deploy(
			@RequestParam(value = "uuid", required = false) String trainExecUuid,
			@RequestParam(value = "version", required = false) String trainExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		
		return deployServiceImpl.deploy(trainExecUuid, trainExecVersion);
	}
	
	@RequestMapping(value = "/undeploy", method = RequestMethod.GET)
	public boolean undeploy(
			@RequestParam(value = "uuid", required = false) String trainExecUuid,
			@RequestParam(value = "version", required = false) String trainExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws Exception {
		return deployServiceImpl.unDeploy(trainExecUuid, trainExecVersion);
	}
	
	@RequestMapping(value = "/startProcess", method = RequestMethod.GET)
	public String startProcess() throws Exception {
		return deployServiceImpl.startProcess(null, null);
	}
	
	@RequestMapping(value = "/stopProcess")
	public String stopProcess() throws Exception {
		return deployServiceImpl.stopProcess(null, null);
	}
	
	@RequestMapping(value = "/getProcessStatus", method = RequestMethod.GET)
	public String getProcessStatus() throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, InterruptedException, ExecutionException {
		return deployServiceImpl.getProcessStatus(null, null);
	}
}

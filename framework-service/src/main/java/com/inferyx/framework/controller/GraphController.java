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

import org.codehaus.jettison.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.register.GraphRegister;
import com.inferyx.framework.service.MessageServiceImpl;
import com.inferyx.framework.service.RegisterService;


@Controller
@RequestMapping(value="/graph")
public class GraphController {
	
	@Autowired
	RegisterService  registerService;
	GraphRegister<?>  graphRegister;
	@Autowired
	MessageServiceImpl messageServiceImpl;
	
	public GraphRegister<?> getGraphRegister() {
		return graphRegister;
	}

	public void setGraphRegister(GraphRegister<?> graphRegister) {
		this.graphRegister = graphRegister;
	}

	@RequestMapping(value="/getGraphResults",method=RequestMethod.GET)
	public @ResponseBody String  getGraphResults(@RequestParam("uuid") String uuid,
			@RequestParam("version") String version,@RequestParam("degree") String degree,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException{
		return registerService.getGraphResults(uuid,version,degree);
		
	}
  
  /*	@RequestMapping(value="/loadGraph",method=RequestMethod.GET)
	public @ResponseBody boolean reloadGraph(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws IOException, ParseException, JSONException{
		try {
			graphRegister.loadGraph();
		}catch (Exception e) {
				e.printStackTrace();
				return false;
		}
		return true;
		
	}*/
  
  @RequestMapping(value="/buildGraph",method=RequestMethod.GET)
	public @ResponseBody boolean  buildGraph(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws IOException, ParseException, JSONException, java.text.ParseException{
		try {
			graphRegister.buildGraph();
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

  /*@RequestMapping(value="/buildAndLoadGraph", method=RequestMethod.GET)
	public @ResponseBody boolean  buildAndLoadGraph(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws IOException, ParseException, JSONException, java.text.ParseException{
	  try {
		  graphRegister.buildGraph();
		  Message message = new Message("200", MessageStatus.SUCCESS.toString(), "Graph build successfully.");
		  messageServiceImpl.save(message);
		  graphRegister.loadGraph();
	  }catch (Exception e) {
			e.printStackTrace();
			return false;
	  }
	  return true;
	}*/
}




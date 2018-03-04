/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inferyx.framework.service.SparkServiceImpl;

@Controller
@RequestMapping(value="/spark")
public class SparkController {
	
	@Autowired SparkServiceImpl sparkServiceImpl;
	
	@RequestMapping(value="/submitQuery",method=RequestMethod.POST)
	public @ResponseBody List<Object> submitQuery(@RequestBody String sql,
			@RequestParam(value ="rows",defaultValue="1000") int rows, 
			@RequestParam(value = "format", required=false)String format,
			@RequestParam(value="header",defaultValue="Y") String header,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JSONException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException{
		return sparkServiceImpl.submitQuery(sql,rows,format,header);
	}

}

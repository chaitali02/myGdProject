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

	@RequestMapping(value = "/dropTempTable", method=RequestMethod.POST)
	public @ResponseBody boolean dropTempTable(@RequestBody List<String> tempTableList,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) {
		try {
			return sparkServiceImpl.dropTempTable(tempTableList);
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
}

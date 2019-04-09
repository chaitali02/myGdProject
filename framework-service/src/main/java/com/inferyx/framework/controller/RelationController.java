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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.RelationServiceImpl;


/**
 * @author Ganesh
 *
 */

@RestController
@RequestMapping(value = "/relation")
public class RelationController {
	@Autowired
	private RelationServiceImpl relationServiceImpl;
	
	@RequestMapping(value = "getSample", method = RequestMethod.GET)
	public List<Map<String, Object>> getSample(@RequestParam(value = "uuid") String relUuid,
			@RequestParam(value = "version", required = false) String relVersion,
			@RequestParam(value ="rows",defaultValue="100") int rows, 
			@RequestBody (required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, 
			@RequestParam(value = "mode", required = false, defaultValue="BATCH") String mode) throws FileNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, IOException, JSONException, ParseException {
		RunMode runMode = Helper.getExecutionMode(mode);
		return relationServiceImpl.getSample(relUuid, relVersion, rows, execParams, runMode);
	}
}

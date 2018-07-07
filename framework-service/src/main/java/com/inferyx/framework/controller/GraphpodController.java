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

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.framework.domain.MetaIdentifierHolder;

@RestController
@RequestMapping(value = "/graphpod")
public class GraphpodController {
    
	
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public MetaIdentifierHolder execute(@RequestParam("uuid") String uuid,
			@RequestParam("version") String version,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) {

		return null;
	}
	
	@RequestMapping(value = "/getResults", method = RequestMethod.GET)
	public List<Map<String, Object>> getResults (@RequestParam("uuid") String uuid,
			@RequestParam("version") String version,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) {
		return null;
	}
}

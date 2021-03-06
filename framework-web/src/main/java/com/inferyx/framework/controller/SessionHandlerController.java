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

import java.text.ParseException;

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.service.SessionHandlerServiceImpl;

@RestController
@RequestMapping(value="/sessionHandler")
public class SessionHandlerController {
	
	@Autowired
	SessionHandlerServiceImpl sessionHandlerServiceImpl;

	public SessionHandlerController() {
		// TODO Auto-generated constructor stub
	}
	
	@RequestMapping(value="/killSession", method=RequestMethod.GET)
	public @ResponseBody boolean killSession(@RequestParam("sessionId") String sessionId) throws JsonProcessingException, JSONException, ParseException {
		return sessionHandlerServiceImpl.killSession(sessionId);
	}
	
	@RequestMapping(value="session/invalidate", method = RequestMethod.GET)
    public @ResponseBody String invalidateSession(){
		return sessionHandlerServiceImpl.invalidateSession();
    }

}

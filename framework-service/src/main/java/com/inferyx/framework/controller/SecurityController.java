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
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.RolePriv;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.service.PrivilegeServiceImpl;
import com.inferyx.framework.service.RegisterService;
import com.inferyx.framework.service.SecurityServiceImpl;
import com.inferyx.framework.service.SessionServiceImpl;

@Controller
@RequestMapping(value = "/security")
public class SecurityController {
	
	@Autowired
	PrivilegeServiceImpl privilegeServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	SessionServiceImpl sessionServiceImpl;
	
	static final Logger logger = Logger.getLogger(SecurityController.class);
	
	@RequestMapping(value = "/getSessionContext", method = RequestMethod.GET)
	public @ResponseBody SessionContext getSessionContext(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = requestAttributes.getRequest().getSession();      
		SessionContext sessionContext = (SessionContext) session.getAttribute("sessionContext");
		return sessionContext;
	}
	
	@RequestMapping(value = "/listLoggedInUsers", method = RequestMethod.GET)
	public @ResponseBody List<SessionInformation> listLoggedInUsers(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws JsonProcessingException {
		return null;/*securityServiceImpl.listLoggedInUsers();*/
	}

	@RequestMapping(value = "/logoutSession", method = RequestMethod.GET)
	public @ResponseBody String logoutSession(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws IOException, JSONException, java.text.ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = requestAttributes.getRequest().getSession(false);
	//	SessionContext sessionContext = (SessionContext) session.getAttribute("sessionContext");
	//	logger.info("logoutsession: session: " + session.toString());							
		//sessionContext = (SessionContext) session.getAttribute("sessionContext");	
		//logger.info("logoutsession: sessionContext: " + sessionContext.toString());							
	
		//HttpSession session = requestAttributes.getRequest().getSession(false);       
	    //session.invalidate();
		//HttpSession session = request.getSession();
		//Session ses = sessionServiceImpl.findLatestByUuid(sessionContext.getSessionInfo().getRef().getUuid());
		String sessionId = (String) session.getAttribute("sessionId");
		//String sessionUuid = sessionContext.getSessionInfo().getRef().getUuid();
		//String appUuid = sessionContext.getAppInfo().getRef().getUuid();
		/*SessionInformation sessionInformation = sessionRegistry.getSessionInformation(sessionId);
		sessionInformation.expireNow();
		sessionRegistry.removeSessionInformation(sessionId);*/
		String logoutActivity = registerService.logoutSession(sessionId);
		session.invalidate();	
		return logoutActivity;
	}

	@RequestMapping(value = "/createUserSession", method = RequestMethod.GET)
	public @ResponseBody String createUserSession(@RequestParam("userName") String userName,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws IOException, JSONException, java.text.ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return registerService.createUserSession(userName);
	}
	
	@RequestMapping(value = "/validateUser", method = RequestMethod.GET)
	public @ResponseBody String validateUser(@RequestParam("userName") String userName,
			@RequestParam("password") String password, HttpServletRequest request,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws IOException, JSONException, java.text.ParseException, ServletException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return registerService.validateUser(userName, password);
	}
	
	@RequestMapping(value = "/getRolePriv", method = RequestMethod.GET)
	public @ResponseBody List<RolePriv> getRolePriv(
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action, HttpServletRequest request) throws JSONException, SQLException, IOException, ParseException {
		MetaIdentifierHolder mih = securityServiceImpl.getRoleInfo();
		String roleUuid = mih.getRef().getUuid();
		return privilegeServiceImpl.getRolePriv(roleUuid);
	}

	@RequestMapping(value = "/setAppRole", method = RequestMethod.GET)
	public @ResponseBody SessionContext SetAppRole(@RequestParam(value="appUUID") String appUuid,
			@RequestParam("roleUUID") String roleUuid, 
			@RequestHeader(value="sessionId") String sessionId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JSONException, java.text.ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonProcessingException {
		return securityServiceImpl.setAppRole(appUuid,roleUuid,sessionId);
	}
	
	@RequestMapping(value = "/unlock", method = RequestMethod.GET)
	public  @ResponseBody Boolean UnlockUser(@RequestParam(value="username") String username,
			@RequestParam("password") String password,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException, JSONException, java.text.ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return securityServiceImpl.UnlockUser(username,password);
	}
	
	@RequestMapping(value = "/setActivity", method = RequestMethod.POST)
    public  @ResponseBody String setActivity(@RequestParam(value="uuid") String uuid,
            @RequestParam("version") String version, @RequestParam(value="type") String type,
            @RequestParam("action") String action) throws IOException, JSONException, java.text.ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
        return securityServiceImpl.setActivity(uuid, version, type, action);
    }
	
	@RequestMapping(value="/getAppRole", method=RequestMethod.GET)
	public @ResponseBody String getAppRole(@RequestParam(value="userName") String userName) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {
		return securityServiceImpl.getAppRole(userName);
	}
	
	@RequestMapping(value = "/getOrgInfo", method = RequestMethod.GET)
	public @ResponseBody MetaIdentifierHolder getOrgInfo(@RequestParam(value = "uuid" , required = false) String uuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws IOException, JSONException, java.text.ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return securityServiceImpl.getOrgInfo();
	}
	
	
}

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
package com.inferyx.framework.security;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.MessageServiceImpl;
import com.inferyx.framework.service.MessageStatus;
import com.inferyx.framework.service.PrivilegeServiceImpl;
import com.inferyx.framework.service.RegisterService;
import com.inferyx.framework.service.RoleServiceImpl;
import com.inferyx.framework.service.SecurityServiceImpl;

public class UserActivityPrivilegeFilter extends GenericFilterBean {

	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	PrivilegeServiceImpl privilegeServiceImpl;
	@Autowired
	RoleServiceImpl roleServiceImpl;
	@Autowired
	RegisterService registerServiceImpl;
	@Autowired
	MessageServiceImpl messageServiceImpl;
	@Autowired
	CommonServiceImpl commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(UserActivityPrivilegeFilter.class);
	@SuppressWarnings({ "unchecked" })
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			String value = commonServiceImpl.getConfigValue("framework.api.checkPriv");
			if(value.equalsIgnoreCase("true")) {
				HttpServletRequest req = (HttpServletRequest) request;
				HttpServletResponse resp = (HttpServletResponse) response;
				logger.info(req.getRequestURL());
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication != null) {
					//logger.info("User name : "+authentication.getName());
				}
				String action = request.getParameter("action");
				String type = request.getParameter("type");
				if (action != null) {
					logger.info("Action: "+action+"   Type : "+type);
//					ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
					MultiValueMap map = securityServiceImpl.getPrivInfo();//(MultiValueMap) servletRequestAttributes.getRequest().getSession(false).getAttribute("mapRolePriv");
					authentication = SecurityContextHolder.getContext().getAuthentication();
					if (authentication != null) {
						//logger.info("User name : " + authentication.getName());
					}
					//for (RolePriv rolePrive : rolePrivList) {
						if(type.contains("view")){
							String[] splits = type.split("view");
							type = splits[0];
						}
					if(map != null)
						if (map.containsKey(type.toLowerCase())) {
							List<String> roles = (List<String>) map.getCollection(type.toLowerCase());
							logger.info("Comparing actions...");
							if (roles.toString().toLowerCase().contains(action.toLowerCase())) {
								logger.info("Comparison result: "+action+" = "+action);
								resp.addHeader(action, action);
								chain.doFilter(request, response);
							} else {
								logger.info("No privilege to "+action+" the "+type+".");
								//resp.setContentType("application/json");
								//resp.sendError(403, "No privilege to "+action+" on "+type);
								Message message = new Message("401", MessageStatus.NO_PRIVILEGE.toString(), ("No privilege to "+action+" the "+type+"."));
								Message savedMessage = messageServiceImpl.save(message);
								
								//PrintWriter out = response.getWriter();
								ObjectMapper mapper = new ObjectMapper();
								String messageJson = mapper.writeValueAsString(savedMessage);
								resp.setContentType("application/json");
								//response.setCharacterEncoding("UTF-8");
								resp.setStatus(401);

								//out.print(messageJson);
								///out.flush();
								resp.getOutputStream().write(messageJson.getBytes());
								//response.getOutputStream().flush();
								resp.getOutputStream().close();

							}
						}else{
							logger.info("No privilege for "+type+".");
							//resp.sendError(403, "No privilege for "+type);
							Message message = new Message("401", MessageStatus.NO_PRIVILEGE.toString(), ("No privilege for "+type+"."));
							Message savedMessage = messageServiceImpl.save(message);
							
							//PrintWriter out = response.getWriter();
							ObjectMapper mapper = new ObjectMapper();
							String messageJson = mapper.writeValueAsString(savedMessage);
							resp.setContentType("application/json");
							//response.setCharacterEncoding("UTF-8");
							resp.setStatus(401);

							//out.print(messageJson);
							///out.flush();
							resp.getOutputStream().write(messageJson.getBytes());
							//response.getOutputStream().flush();
							resp.getOutputStream().close();
						}else{
							logger.info("privInfo instance  is null for the request "+req.getRequestURL());
							chain.doFilter(request, response);
						}
					//}
				} else {
					authentication = SecurityContextHolder.getContext().getAuthentication();
					if (authentication != null) {
						logger.info("Name : "+authentication.getName());
					}
					chain.doFilter(request, response);
				}
			}else
				chain.doFilter(request, response);
			
		} catch (AuthenticationException authExp) {
			SecurityContextHolder.clearContext();
			logger.error("Authentication Exception");
			authExp.printStackTrace();
			return;
		} catch (JSONException 
				| ParseException 
				| IllegalAccessException 
				| IllegalArgumentException 
				| InvocationTargetException 
				| NoSuchMethodException
				| SecurityException
				| NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public void destroy() {}
}

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
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.RolePriv;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.service.AlgorithmServiceImpl;
import com.inferyx.framework.service.PrivilegeServiceImpl;
import com.inferyx.framework.service.RegisterService;
import com.inferyx.framework.service.RoleServiceImpl;
import com.inferyx.framework.service.SecurityServiceImpl;


public class PrivilegeProcessingFilter extends GenericFilterBean {
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	PrivilegeServiceImpl privilegeServiceImpl;
	@Autowired
	RoleServiceImpl roleServiceImpl;
	@Autowired
	RegisterService registerServiceImpl;
	
	static final Logger logger = Logger.getLogger(PrivilegeProcessingFilter.class);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			HttpServletRequest req = (HttpServletRequest) request;
			/*
			 * HttpSession session = req.getSession(false);
			 * System.out.println(req.getRequestURL());
			 */
			logger.info(req.getRequestURL());
			//System.out.println(((SessionContext)req.getSession().getAttribute("sessionContext")).getRoleInfo().getRef().getUuid());
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null) {
				logger.info(" User name : " + authentication.getName());
			}
			/*ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();*/
			//HttpSession session = requestAttributes.getRequest().getSession(false);
			// logger.info("setAppRole: session: " + session.toString());
			SessionContext sessionContext = (SessionContext)req.getSession().getAttribute("sessionContext");

			/*if (roleServiceImpl == null) {
				ServletContext servletContext = req.getServletContext();
				WebApplicationContext webApplicationContext = WebApplicationContextUtils
						.getWebApplicationContext(servletContext);
				roleServiceImpl = webApplicationContext.getBean(RoleServiceImpl.class);
			}*/
			MetaIdentifierHolder mHolder = sessionContext.getRoleInfo();

			String uuid = mHolder.getRef().getUuid();
			String version = mHolder.getRef().getVersion();
			// Role role=roleServiceImpl.findOneByUuidAndVersion(uuid, version);
			String action = request.getParameter("action");
			String metaType = request.getParameter("type");
			if (action != null) {
				logger.info(" Action : " + action + " : type : " + metaType);
				List<RolePriv> rolePriveList = privilegeServiceImpl.getRolePriv(uuid);

				authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication != null) {
					logger.info(" User name 2 : " + authentication.getName());
				}
				
				for (RolePriv rolePrive : rolePriveList) {
					logger.info(rolePrive.getType());
					if (rolePrive.getType().equals(metaType)) {
						List<String> roleprive = (List<String>) rolePrive.getPrivInfo();

						if (roleprive.contains(action)) {
							HttpServletResponse resp = (HttpServletResponse) response;
							resp.addHeader("view", action.toString());
							chain.doFilter(request, response);
							break;
						} else {
							throw new NoPrivilegeExeception(
									"You do not have privilege to " + action + " on " + metaType);
						}
					}

				}
				throw new NoPrivilegeExeception("You do not have privilege on " + metaType);
			} else {
				authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication != null) {
					logger.info(" User name 3 : " + authentication.getName());
				}
				chain.doFilter(request, response);
			}
			// resp.addHeader("view", action.toString());
			// chain.doFilter(request, response);

		} catch (AuthenticationException failed) {
			SecurityContextHolder.clearContext();
			logger.error("Authentication Exception");
			failed.printStackTrace();

			return;

		} catch (NoPrivilegeExeception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}

	}
	
	public void destroy() {
    }

}

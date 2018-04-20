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
package com.inferyx.framework.security;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.LoginStatus;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.service.SessionServiceImpl;
import com.inferyx.framework.service.UserServiceImpl;

public class AuthenticationLoginProcessingFilter extends GenericFilterBean {

	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SessionServiceImpl sessionServiceImpl;

	AuthenticationManager authManager;

	private String credentialsCharset = "UTF-8";

	public AuthenticationLoginProcessingFilter(AuthenticationManager authManager) {
		this.authManager = authManager;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		boolean debug = this.logger.isDebugEnabled();
		logger.info("AuthenticationLoginProcessingFilter.doFilter: Entering");
		ObjectMapper mapper = new ObjectMapper();
		LoginStatus loginStatus=new LoginStatus();
 
		//logger.info("\tSession counter: "+SessionCounter.getActiveSessionNumber());
	
		String authorization = ((HttpServletRequest) request).getHeader("Authorization");
		logger.debug("Request Body: ");
		if (authorization == null || !authorization.startsWith("Basic")) {
			logger.info("AuthenticationLoginProcessingFilter.doFilter: Authorization missing or incorrect");
			return;
		}
		int limit = Integer.parseInt(Helper.getPropertyValue("framework.security.session.counter"));
		if(SessionCounter.getActiveSessionNumber() == limit) {
			SecurityContextHolder.clearContext();

			if (debug) {
				this.logger.debug("Authentication request for failed: ");
			}
			logger.error("Authentication Exception");
			//failed.printStackTrace();
			HttpServletResponse resp = (HttpServletResponse) response;
			resp.addHeader("sessionId", "");
			resp.addHeader("status", "false");
			resp.addHeader("message", "User limit exceeded, Please contact admin.");
			logger.info("Request authenticated successfully.");
			loginStatus.setSessionId("");
			loginStatus.setStatus("false");
			
			return;
		}else
		try {

			// Authorization: Basic base64credentials
			String base64Credentials = authorization.substring("Basic".length()).trim();
			String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
			// credentials = username:password
			String[] tokens = credentials.split(":", 2);
			assert (tokens.length == 2);

			String username = tokens[0];
			String password = tokens[1];
			logger.info("Username:" + username);
			logger.info("Password:" + password);

			/*// Setting sessionContext object
			logger.info("Setting sessionContext");
			SessionContext sessionContext = new SessionContext();
			User userDO = userServiceImpl.findLatestByUsername(username);
			MetaIdentifier userMeta = new MetaIdentifier(MetaType.user, userDO.getUuid(), userDO.getVersion(),
					userDO.getName());
			MetaIdentifierHolder userInfo = new MetaIdentifierHolder();
			// MetaIdentifierHolder sessionInfo = new MetaIdentifierHolder();
			userInfo.setRef(userMeta);
			sessionContext.setUserInfo(userInfo);

			HttpSession session = ((HttpServletRequest) request).getSession();
			session.setAttribute("sessionContext", sessionContext);*/

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,password);
			//logger.info("Setting Auth object: " + authentication);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));

			//logger.info("Authentication Manager Object: " + authManager);

			SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(authentication));
			authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
			
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			
			HttpSession session = requestAttributes.getRequest().getSession(false);
			HttpServletResponse resp = (HttpServletResponse) response;
			if(session !=null){
				
			
			//logger.info("sessioId: "+session.getId());
			
			
			SessionContext sessionContext = (SessionContext) session.getAttribute("sessionContext");
			String userUuid = sessionContext.getUserInfo().getRef().getUuid();
			String sessionUuid;
			Session sessionDO;
			
			//logger.info("Session Object: " + sessionContext.getSessionInfo());

			if(sessionContext.getSessionInfo() == null || sessionContext.getSessionInfo().getRef().getUuid().isEmpty())
			{
				logger.info("Empty Session Object. Unable to set sessionId");
				resp.addHeader("sessionId", null);			
			}
			else
			{
				sessionUuid = sessionContext.getSessionInfo().getRef().getUuid();
				sessionDO = sessionServiceImpl.findLatestByUuid(sessionUuid);
				resp.addHeader("sessionId", sessionDO.getSessionId());
				loginStatus.setSessionId( sessionDO.getSessionId());
			}
			// MetaIdentifier sessionMeta = new MetaIdentifier(MetaType.session,
			// sessionDO.getUuid(),sessionDO.getVersion());
			// sessionInfo.setRef(sessionMeta);
			// ((SessionContext)authentication.getPrincipal()).setSessionInfo(sessionInfo);
			// Send response
					
			resp.addHeader("status", "" + authentication.isAuthenticated());
			if (authentication.isAuthenticated()) {
				resp.addHeader("message", "User authenticated successfully");
				loginStatus.setMessage("User authenticated successfully");
				loginStatus.setStatus("true");
			} else {
				resp.addHeader("message", "Invalid Login");
				loginStatus.setMessage("Invalid Login");
				loginStatus.setStatus("false");
			}
			//logger.info("userName:>--->>"+authentication.getPrincipal().toString());
			resp.addHeader("userName", authentication.getPrincipal().toString());
			resp.addHeader("userUUID", userUuid);
			loginStatus.setUserName(authentication.getPrincipal().toString());
			loginStatus.setUserUuid(userUuid);
			String messageJson = mapper.writeValueAsString(loginStatus);
			resp.setContentType("application/json");
			resp.setStatus(200);
			resp.getOutputStream().write(messageJson.getBytes());
			resp.getOutputStream().close();
			if (debug) {
				this.logger.debug("Login Authentication Authorization header found for user '" + username + "'");
			}

			//logger.info("Response object sessionId: "+resp.getHeader("sessionId"));
		}else{
			loginStatus.setStatus("false");
			loginStatus.setMessage("Invalid Login");
			loginStatus.setUserName(authentication.getPrincipal().toString());
			String messageJson = mapper.writeValueAsString(loginStatus);
			resp.setContentType("application/json");
			resp.setStatus(200);
			resp.getOutputStream().write(messageJson.getBytes());
			resp.getOutputStream().close();
		}
		} catch (AuthenticationException failed) {
			SecurityContextHolder.clearContext();

			if (debug) {
				this.logger.debug("Authentication request for failed: " + failed);
			}
			logger.error("Authentication Exception");
			failed.printStackTrace();
			HttpServletResponse resp = (HttpServletResponse) response;
			resp.addHeader("sessionId", "");
			resp.addHeader("status", "false");
			resp.addHeader("message", "Invalid user");
			return;
			
		}
		
		logger.info("Request authenticated successfully");
	}

	protected String getCredentialsCharset(HttpServletRequest httpRequest) {
		return this.credentialsCharset;
	}

}
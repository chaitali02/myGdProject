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

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.SessionServiceImpl;
import com.inferyx.framework.service.UserServiceImpl;

public class AuthenticationLoginProcessingFilter extends GenericFilterBean {

	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SessionServiceImpl sessionServiceImpl;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;

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

		//logger.info("\tSession counter: "+SessionCounter.getActiveSessionNumber());
	
		String authorization = ((HttpServletRequest) request).getHeader("Authorization");
		logger.debug("Request Body: ");
		if (authorization == null || !authorization.startsWith("Basic")) {
			logger.info("AuthenticationLoginProcessingFilter.doFilter: Authorization missing or incorrect");
			return;
		}
		//This method called only for filter
		int limit = Integer.parseInt(Helper.getPropertyValue("framework.security.session.counter"));
		if(SessionCounter.getActiveSessionNumber() == limit) {
			SecurityContextHolder.clearContext();

			if (debug) {
				this.logger.debug("Authentication request for FAILED: ");
			}
			logger.error("Authentication Exception");
			//FAILED.printStackTrace();
			HttpServletResponse resp = (HttpServletResponse) response;
			resp.addHeader("sessionId", "");
			resp.addHeader("status", "false");
			resp.addHeader(MetaType.message.toString(), "User limit exceeded, Please contact admin.");
			logger.info("Request authenticated successfully.");
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
			authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext()
					.getAuthentication();

			HttpServletResponse resp = (HttpServletResponse) response;
			try {
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
				HttpSession session = requestAttributes.getRequest().getSession(false);
				
				logger.info("sessionId: "+session.getId());
				session.setAttribute("sessionId", session.getId());
				SessionContext sessionContext = (SessionContext) session.getAttribute("sessionContext");
				String userUuid = sessionContext.getUserInfo().getRef().getUuid();
				resp.addHeader("userUUID", userUuid);
				String sessionUuid;
				Session sessionDO;
				
				//logger.info("Session Object: " + sessionContext.getSessionInfo());

				if(sessionContext.getSessionInfo() == null || sessionContext.getSessionInfo().getRef().getUuid().isEmpty()) {
					logger.info("Empty Session Object. Unable to set sessionId");
					resp.addHeader("sessionId", null);			
				} else {
					sessionUuid = sessionContext.getSessionInfo().getRef().getUuid();
					//sessionDO = sessionServiceImpl.findLatestByUuid(sessionUuid);
					sessionDO = (Session) commonServiceImpl.getLatestByUuid(sessionUuid, MetaType.session.toString());
					resp.addHeader("sessionId", sessionDO.getSessionId());
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// MetaIdentifier sessionMeta = new MetaIdentifier(MetaType.session,
			// sessionDO.getUuid(),sessionDO.getVersion());
			// sessionInfo.setRef(sessionMeta);
			// ((SessionContext)authentication.getPrincipal()).setSessionInfo(sessionInfo);
			// Send response
					
			resp.addHeader("status", "" + authentication.isAuthenticated());
			if (authentication.isAuthenticated())
				resp.addHeader(MetaType.message.toString(), "User authenticated successfully");
			else 
				resp.addHeader(MetaType.message.toString(), "Invalid Login");
			
			//logger.info("userName:>--->>"+authentication.getPrincipal().toString());
			resp.addHeader("userName", authentication.getPrincipal().toString());

			if (debug) {
				this.logger.debug("Login Authentication Authorization header found for user '" + username + "'");
			}

			//logger.info("Response object sessionId: "+resp.getHeader("sessionId"));

		} catch (AuthenticationException FAILED) {
			SecurityContextHolder.clearContext();

			if (debug) {
				this.logger.debug("Authentication request for FAILED: " + FAILED);
			}
			logger.error("Authentication Exception");
			FAILED.printStackTrace();
			HttpServletResponse resp = (HttpServletResponse) response;
			resp.addHeader("sessionId", "");
			resp.addHeader("status", "false");
			resp.addHeader(MetaType.message.toString(), "Invalid user");
			return;
			
		}
		logger.info("Request authenticated successfully");
	}

	protected String getCredentialsCharset(HttpServletRequest httpRequest) {
		return this.credentialsCharset;
	}
}
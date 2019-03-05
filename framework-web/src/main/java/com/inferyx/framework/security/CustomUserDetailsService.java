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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.inferyx.framework.dao.ISessionDao;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.service.SessionServiceImpl;

/*
 *  @author Yashvi
 *  code for logged in user authntication
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
	private Logger logger = Logger.getLogger(CustomUserDetailsService.class);
	@Autowired
	SessionServiceImpl sessionServiceImpl;
	@Autowired
	ISessionDao iSessionDao; 

	public UserDetails loadUserByUsername(String sessionId) {
		try {
			//ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			//HttpSession session2 = requestAttributes.getRequest().getSession(false);	
			//logger.info("session2 sessionId:: " + session2.getId());	
			//logger.info("Recieved sessionId:: " + sessionId);			
			Session session = sessionServiceImpl.findSessionBySessionId(sessionId);
			boolean status = true;
			if (session != null && !session.getStatusList().isEmpty()) {
				for (Status s : session.getStatusList()) {
					if ((Status.Stage.expired).equals(s.getStage()) || (Status.Stage.KILLED).equals(s.getStage())
							|| (Status.Stage.PENDING).equals(s.getStage()))
						status = false;
				}
			}

			if (status) {
				UserDetails user = new User(session.getUserInfo().getRef().getUuid(), sessionId, true, true, true, true,
						getAuthorities("Admin"));
				return user;
			} else
				throw new PreAuthenticationUserNotFound("Session not valid or expired:" + sessionId + ".");

		} catch (Exception e) {
			// e.printStackTrace();
			throw new PreAuthenticationUserNotFound("Session not valid or expired");
		}
	}

	public Collection<? extends GrantedAuthority> getAuthorities(String role) {
		//ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		//HttpSession session2 = requestAttributes.getRequest().getSession(false);	
		//logger.info("session3 sessionId:: " + session2.getId());
		List<GrantedAuthority> authList = getGrantedAuthorities(getRoles(role));
		logger.info("authList-------->" + authList);
		return authList;
	}

	public List<String> getRoles(String role) {
		List<String> roles = new ArrayList<String>();
		if (role.trim().equalsIgnoreCase("Admin".trim())) {
			roles.add("ROLE_ADMIN");
		}

		if (role.trim().equalsIgnoreCase(MetaType.user.toString().trim())) {
			roles.add("ROLE_USER");
		}
		return roles;
	}

	public static List<GrantedAuthority> getGrantedAuthorities(List<String> roles) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}
}

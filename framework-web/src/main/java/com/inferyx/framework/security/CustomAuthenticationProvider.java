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
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.service.SessionServiceImpl;
import com.inferyx.framework.service.UserServiceImpl;

/*
 *  @author Yashvi
 *  code for user login
 */
public class CustomAuthenticationProvider implements AuthenticationProvider {
	private Logger logger = Logger.getLogger(CustomAuthenticationProvider.class);

	@Autowired
	SessionServiceImpl sessionServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		logger.info("Validating User...");

		//SessionContext sessionContext = (SessionContext) authentication.getPrincipal();
		String username = (String) authentication.getPrincipal();
		String password = (String) authentication.getCredentials();

		try {
			Boolean authenticated = sessionServiceImpl.validateUser(username,password);
			if (authenticated) {
				authentication = new UsernamePasswordAuthenticationToken(username, password, getAuthorities("Admin"));
				logger.info("User Validated Sucessfully. Setting Authentication Object " + authentication);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);

	}

	public List<? extends GrantedAuthority> getAuthorities(String role) {
		List<GrantedAuthority> authList = getGrantedAuthorities(getRoles(role));
		logger.info("authList-------->" + authList);
		return authList;
	}

	public List<String> getRoles(String role) {
		List<String> roles = new ArrayList<String>();
		if (role.trim().equals("Admin".trim())) {
			roles.add("ROLE_ADMIN");
		} else if (role.trim().equals(MetaType.user.toString().trim())) {
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
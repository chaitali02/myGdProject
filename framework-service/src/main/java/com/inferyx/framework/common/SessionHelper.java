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
package com.inferyx.framework.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.SessionContext;

/**
 * @author joy
 *
 */
@Component
public class SessionHelper {
	
	static final Logger logger = Logger.getLogger(SessionHelper.class);
	
	/**
	 * 
	 */
	public SessionHelper() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Get the sessionContext in which this group is run
	 * @return
	 */
	public SessionContext getSessionContext() {
		SessionContext sessionContext = null;
		ServletRequestAttributes requestAttributes = null;
		try {
			requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if(requestAttributes != null) {
				HttpServletRequest request = requestAttributes.getRequest();
				if(request != null) {
					HttpSession session = request.getSession(false);
					if(session != null) {
						sessionContext = (SessionContext) session.getAttribute("sessionContext");
						if (sessionContext != null) {
							return sessionContext;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("SessionContext is not applicable. Fall back on FrameworkThreadLocal ");
		}
		if (sessionContext == null) {
			sessionContext = FrameworkThreadLocal.getSessionContext().get();
		}
		return sessionContext;
	}

}

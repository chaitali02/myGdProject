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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.SystemServiceImpl;


public class SessionCounter implements HttpSessionListener{
	@Autowired
	private static SystemServiceImpl systemServiceImpl;
	@Autowired
	CommonServiceImpl commonServiceImpl;
	
	private static ConcurrentHashMap<String, HttpSession> sessionMap = new ConcurrentHashMap<>();
    public static final String COUNTER = "session-counter";
    
    public static final Logger  logger = Logger.getLogger(SessionCounter.class);
    
	@Override
	public void sessionCreated(HttpSessionEvent event) throws NumberFormatException {
		HttpSession session = event.getSession();
		logger.info("New session is created: "+session.toString());
        sessionMap.put(session.getId(), session);
        session.setAttribute("session-counter", this);        
        int limit = 100;
		try {
			limit = Integer.parseInt(commonServiceImpl.getConfigValue("framework.security.session.counter"));
		} catch (IOException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
			e.printStackTrace();
		}
        if(sessionMap.size() == limit) {
        	//invalidateSessions();
        }
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        logger.info("Session is Destroyed: sessionId: "+session.getId());
        //logger.info("List size: before: "+sessionMap.size());
        sessionMap.remove(session.getId());
        //logger.info("List size: after: "+sessionMap.size());
        session.setAttribute("session-counter", this);
	}
	
	public static boolean invalidateSessions() {
		try {
			Collection<HttpSession> sessions = sessionMap.values();
			for(HttpSession session : sessions) {
				session.invalidate();
			}
			return true;
		}catch (Exception e) {
			// TODO: handle exception
			return false;
		}		
		/*Set<Entry<String, HttpSession>> entrySet = sessionMap.entrySet();
		Iterator<Entry<String, HttpSession>> iterator = entrySet.iterator();
		while(iterator.hasNext()) {
			Entry<String, HttpSession> sessionEntry = iterator.next();
			HttpSession session = sessionEntry.getKey();
			session.invalidate();
			//logger.info("Invalidated session: "+session.toString());
		}*/
	}
	public static boolean invalidateSession(String sessionId) {
		try {
			HttpSession session = sessionMap.get(sessionId);
			if(session != null) {
				session.invalidate();
				logger.info("Session invalidated.......");
				return true;
			}else
				logger.info("Session alReady invalidated......."); return false;			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	public static int getActiveSessionNumber() {
        return sessionMap.size();
    }
	
	public static ConcurrentHashMap<String, HttpSession> getSessionList(){
		return sessionMap;
	}
	
}

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
package com.inferyx.framework.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.security.SessionCounter;

@Service
public class SessionHandlerServiceImpl {
	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SessionServiceImpl sessionServiceImpl;
	
	static final Logger logger = Logger.getLogger(SystemServiceImpl.class);

	public SessionHandlerServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean killSession(String sessionId) throws JsonProcessingException, JSONException, ParseException {
		//boolean isInvalidated = SessionCounter.invalidateSession(sessionId);
		if(!StringUtils.isBlank(sessionId) && sessionId != null) {
			boolean isUpdated = expireSession(sessionId);
			if(isUpdated) {
				ConcurrentHashMap<String, HttpSession> sessionMap = SessionCounter.getSessionList();
				HttpSession session = sessionMap.get(sessionId);
				if(session != null) {
					session.invalidate();
					logger.info("Session invalidated......."); return true;
				}else
					logger.info("Session already invalidated......."); return false; 
			}else
				logger.info("Unable to update Session object, aborting invalidated operation......."); return false;
		}else
			logger.info("Session ID is null, Session not invalidated......."); return false;
	}
	public boolean expireSession(String sessionId) throws JsonProcessingException, JSONException, ParseException {
		Session session = sessionServiceImpl.findSessionBySessionId(sessionId);
		if(session != null) {
			Status status = Helper.getLatestStatus(session.getStatusList());
			if(status.getStage().equals(Status.Stage.active)) {
				List<Status> statusList = session.getStatusList();
				statusList.add(new Status(Status.Stage.expired, new Date()));
				session.setStatusList(statusList);
				commonServiceImpl.save(MetaType.session.toString(), session);	
				return true;
			}else
				logger.info("Session already invalidated......."); return false;
		}else
			logger.info("Meta type \"Session\" instance is null......."); return false;
	}
	
	public String invalidateSession() {
		String message = null;
		try{
			SessionCounter.invalidateSessions();
			message = "Session(s) destroyed successfully.";
		}catch (NullPointerException e) {
			e.printStackTrace();
			return "Can not destroy session(s).";
		}catch (Exception e) {
			e.printStackTrace();
			return "Can not destroy session(s).";
		}
		return message;
	}


}

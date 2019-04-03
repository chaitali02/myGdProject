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
package com.inferyx.framework.aspect;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.service.MessageServiceImpl;
import com.inferyx.framework.service.MessageStatus;
import com.inferyx.framework.service.SessionServiceImpl;

@Aspect
public class ControllerValidation {
	@Autowired
	MessageServiceImpl messageServiceImpl;
	@Autowired
	SessionServiceImpl sessionServiceImpl;
	
	public static final Logger logger = Logger.getLogger(ControllerValidation.class);
	/*
	 * Pointcuts*
	 */
	/***********************Unused***********************/
	/*@Pointcut("within(com.inferyx.framework.controller.*)")
	public void controllerValidationPointcut() {}*/
	
	
	
	/*
	 * Aspects*
	 */
	/***********************Unused***********************/

	/*@AfterReturning(
		    pointcut="controllerValidationPointcut()", 
		    returning="returnValue")*/
	
	/*public void aspectReturing(JoinPoint joinPoint, Object returnValue) throws Throwable {
		//System.out.println("\n\n");         
		//logger.info("Inside AfterReturningAspect.aspectReturing() method.");
		logger.info("inserted after : " + joinPoint.getSignature());
		if((returnValue == null) 
				|| returnValue.equals("null") 
				|| returnValue.equals("[]")
				|| returnValue.equals("[ ]")) {
			logger.info("Null value returned.");
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if(requestAttributes != null) {
				HttpServletResponse response = requestAttributes.getResponse();
				if(response != null) {
						Message message = new Message("404", MessageStatus.FAIL.toString(), "No data available.");
						Message savedMessage = messageServiceImpl.save(message);
						
						//PrintWriter out = response.getWriter();
						ObjectMapper mapper = new ObjectMapper();
						String messageJson = mapper.writeValueAsString(savedMessage);
						response.setContentType("application/json");
						//response.setCharacterEncoding("UTF-8");
						response.setStatus(404);
						//out.print(messageJson);
						///out.flush();
						response.getOutputStream().write(messageJson.getBytes());
						//response.getOutputStream().flush();
						response.getOutputStream().close();
						//System.out.println("\n\n");
				}else
					logger.info("HttpServletResponse response is \""+null+"\"");
			}else
				logger.info("ServletRequestAttributes requestAttributes is \""+null+"\"");
		}else
			logger.info("Method returned \"non-null vlue\"."+"returnValuealue: >>----->> "+returnValue);
	}*/
	
	/***********************Unused***********************/
	/*@AfterThrowing("controllerValidationPointcut()")
	public void afterThrowingAdvice(JoinPoint joinPoint) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		logger.info("ControllerValidation...");  
		logger.info("Method Signature: "  + joinPoint.getSignature()); 
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(requestAttributes != null) {
			HttpServletResponse response = requestAttributes.getResponse();
			if(response != null) {
				Message message = new Message("500", MessageStatus.EXCEPTION.toString(), "Some error occured, please contact administrator.");
				Message savedMessage = messageServiceImpl.save(message);
				
				//PrintWriter out = response.getWriter();
				ObjectMapper mapper = new ObjectMapper();
				String messageJson = mapper.writeValueAsString(savedMessage);
				response.setContentType("application/json");
				//response.setCharacterEncoding("UTF-8");
				response.setStatus(500);
				//out.print(messageJson);
				///out.flush();
				response.getOutputStream().write(messageJson.getBytes());
				//response.getOutputStream().flush();
				response.getOutputStream().close();
			}else
				logger.info("HttpServletResponse response is \""+null+"\"");
		}else
			logger.info("ServletRequestAttributes requestAttributes is \""+null+"\"");	
	}
	*/
	/*@Before("controllerValidationPointcut()")
	public void validaterequest(JoinPoint joinPoint) throws JSONException, ParseException, IOException {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(requestAttributes != null) {
			HttpServletRequest request = requestAttributes.getRequest();
			HttpServletResponse response = requestAttributes.getResponse();
			if(request != null) {
				
				
				String sessionId = (String) request.getHeader("sessionId");
				if(sessionId != null) {
					String sessionId_2 = request.getSession().getId();
			    	logger.info("sessionId##: "+sessionId+"   sessionstatus: "+session.getActive()+"  sessionId_2: "+sessionId_2);
				}else {
					sessionId = (String) request.getAttribute("sessionId");
					logger.info("sessionId####: "+sessionId);
				}
				
		    	
		    	Session session = sessionServiceImpl.findSessionBySessionId(sessionId);
		    	String isActive = session.getActive();
		    	
		    	if(isActive.equalsIgnoreCase("Y")) {
		    		
		    	}else if(isActive.equalsIgnoreCase("N")) {
		    		Message message = new Message("419", MessageStatus.EXCEPTION.toString(), "Session expired.");
					Message savedMessage = messageServiceImpl.save(message);
					
					//PrintWriter out = response.getWriter();
					ObjectMapper mapper = new ObjectMapper();
					String messageJson = mapper.writeValueAsString(savedMessage);
					response.setContentType("application/json");
					//response.setCharacterEncoding("UTF-8");
					response.setStatus(419);
					//out.print(messageJson);
					///out.flush();
					response.getOutputStream().write(messageJson.getBytes());
					//response.getOutputStream().flush();
					response.getOutputStream().close();
		    	}
		    	String sessionId_2 = request.getSession().getId();
		    	logger.info("sessionId: "+sessionId+"   sessionstatus: "+session.getActive()+"  sessionId_2: "+sessionId_2);
		    	
			}else
				logger.info("HttpServletResponse request is \""+null+"\"");
		}else
			logger.info("ServletRequestAttributes requestAttributes is \""+null+"\"");			
	}*/
}

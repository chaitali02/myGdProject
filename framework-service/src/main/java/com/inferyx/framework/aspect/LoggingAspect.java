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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.Log;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.service.CommonServiceImpl;

@Aspect
public class LoggingAspect {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SessionContext sessionContext;
	
	public static final Logger logger = Logger.getLogger(LoggingAspect.class);
	/*
	 * Pointcuts*
	 */	
//	@Pointcut("within(com.inferyx.framework.security.UserActivityPrivilegeFilter)")
	
	/****************************Unused*******************************/
	/*@Pointcut("within(com.inferyx.framework.controller1.*)")
	public void aroundPointcut() {}*/
	
	
	/*@Pointcut("within(com.inferyx.framework.service.SecurityServiceImpl.getAppInfo())")
	public void afterThrowing() {}*/
	/*
	 * Aspects*
	 */
	
	/****************************Unused*******************************/
	 /* @Around("aroundPointcut()")
	  public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		  
	    StopWatch stopWatch = new StopWatch();
	    stopWatch.start();
	    
	    String startTime = dateFormat.format(new Date());
	    Date sDate = dateFormat.parse(startTime);
	    Object retVal = joinPoint.proceed();
	    String endTime = dateFormat.format(new Date());
	    Date eDate = dateFormat.parse(endTime);
	    stopWatch.stop();
	    long totalTime = getTotalTime(startTime, endTime, dateFormat);
	    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(requestAttributes != null) {
			HttpServletRequest request = requestAttributes.getRequest();
			if(request != null) {
				Log log = new Log(request.getRequestURL().toString(), sDate, eDate, ""+totalTime);
				Log savedLog = (Log) commonServiceImpl.save(MetaType.log.toString(), log);		
				logger.info("saved log: "+savedLog.toString());
			}else
				logger.info("HttpServletResponse request is \""+null+"\"");
		}else
			logger.info("ServletRequestAttributes instance requestAttributes is \""+null+"\"");	
	    
	    return retVal;
	  }*/
	
	/****************************Unused*******************************/
	/*  public long getTotalTime(String startTime, String endTime, DateFormat dateFormat) throws ParseException {
		  long totalTime = 0;
		  if((startTime != null) && (endTime != null) && (dateFormat != null)) {
			  long start = dateFormat.parse(startTime).getTime();
			  long end = dateFormat.parse(endTime).getTime();
			  totalTime = end - start;
		  }
		  return totalTime;
	  }*/
	  
	  /*@AfterThrowing(pointcut = "execution(com.inferyx.framework.service.SecurityServiceImpl.getAppInfo())", throwing = "e")
	  public void getAppInfo(JoinPoint joinPoint, Exception e) {
		  Object object = joinPoint.getTarget();
		  FrameworkThreadLocal.getSessionContext().set(sessionContext);
		  
	  }*/
	  /*@Pointcut("execution(public * com.inferyx.framework.service.SecurityServiceImpl.getAppInfo2(..))")
	  public void beforePiontcut() {}
	  @Before("beforePiontcut()")
	  public void getAppInfoBefore(JoinPoint joinPoint) throws Throwable{
		  Object object = joinPoint.getTarget();
		  ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if(requestAttributes != null) {
				HttpServletRequest request = requestAttributes.getRequest();
				if(request != null) {
					HttpSession session = request.getSession(false);
					if(session != null) {
						sessionContext = (SessionContext) session.getAttribute("sessionContext");
						FrameworkThreadLocal.getSessionContext().set(sessionContext);
					}
					else
						logger.info("HttpSession instance session is \""+null+"\"");
				}else
					logger.info("HttpServletResponse request is \""+null+"\"");
			}else
				logger.info("ServletRequestAttributes instance requestAttributes is \""+null+"\"");	
	  }*/
}

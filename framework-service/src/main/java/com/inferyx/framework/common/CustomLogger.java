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

import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

/**
 * @author Ganesh
 *
 */
public class CustomLogger {

	private static final Logger logger = Logger.getLogger(CustomLogger.class);

	/*****************************Unused********************************/
	/*public void writeLog(Class<?> loggerClass, String msg, String filePath, int lineNumber) {
		logger.info("Inside custom logger class.");
		Logger customLogger = Logger.getLogger(loggerClass);
		Appender fileAppender = null;
		try {
			fileAppender = new FileAppender(new SimpleLayout(), filePath, true);
			fileAppender.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:"+lineNumber+" - %m%n"));
			customLogger.addAppender(fileAppender);

			customLogger.info(msg);
			customLogger.removeAppender(fileAppender);
			//fileAppender.close();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	
	/********************************Unused***************************/
	/*public void writeErrorLog(Class<?> loggerClass, Object obj, String filePath, int lineNumber) {
		logger.info("Inside custom logger class.");
		Logger customLogger = Logger.getLogger(loggerClass);
		Appender fileAppender = null;
		try {
			fileAppender = new FileAppender(new SimpleLayout(), filePath, true);
			fileAppender.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:"+lineNumber+" - %m%n"));
			customLogger.addAppender(fileAppender);

			customLogger.error(obj);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}

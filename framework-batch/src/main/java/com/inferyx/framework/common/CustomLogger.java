/**
 *
 * @Author Ganesh
 *
 */
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

	public void writeLog(Class<?> loggerClass, String msg, String filePath, int lineNumber) {
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
	}
	
	public void writeErrorLog(Class<?> loggerClass, Object obj, String filePath, int lineNumber) {
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
	}
}

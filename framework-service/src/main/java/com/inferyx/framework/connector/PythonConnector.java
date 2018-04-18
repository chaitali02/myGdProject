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
package com.inferyx.framework.connector;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.common.CustomLogger;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.PythonExecutor;
import com.inferyx.framework.service.CommonServiceImpl;

//import py4j.GatewayServer;

/**
 * @author Ganesh
 *
 */
public class PythonConnector implements IConnector {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	PythonExecutor pythonExecutor;
	CustomLogger customLogger = new CustomLogger();
	String logPath;
	
	/**
	 * @Ganesh
	 *
	 * @return the logPath
	 */
	public String getLogPath() {
		return logPath;
	}

	/**
	 * @Ganesh
	 *
	 * @param logPath the logPath to set
	 */
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
	
	//GatewayServer gatewayServer = null;
	
	public PythonConnector() {
		//gatewayServer = new GatewayServer(pythonExecutor);
	}
	@Override
	public ConnectionHolder getConnection() throws IOException {
		ConnectionHolder conholder = new ConnectionHolder();
		//Datasource datasource = commonServiceImpl.getDatasourceByApp();
		/*String host = Helper.getProperty("framework.python.host");
		int port = Integer.parseInt(Helper.getProperty("framework.python.port"));*/
		PythonInterpreter pyInterpreter = null;
		try {
			Properties postProperties = new Properties();

			if(logPath != null && !StringUtils.isBlank(logPath))
				customLogger.writeLog(this.getClass(), "Setting connection properties.", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			
	    	postProperties.put("python.console.encoding", "UTF-8"); // Used to prevent: console: Failed to install '': java.nio.charset.UnsupportedCharsetException: cp0.
	    	postProperties.put("python.security.respectJavaAccessibility", "false"); //don't respect java accessibility, so that we can access protected members on subclasses
	    	postProperties.put("python.import.site","false");

	    	Properties preProperties = System.getProperties();		
	    	PythonInterpreter.initialize(preProperties, postProperties, new String[0]);

			if(logPath != null && !StringUtils.isBlank(logPath))
				customLogger.writeLog(this.getClass(), "Acquiring connection.", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			
	        pyInterpreter = new PythonInterpreter();
	        
	        conholder.setType(ExecContext.PYTHON.toString());
			conholder.setConObject(pyInterpreter);

			if(logPath != null && !StringUtils.isBlank(logPath))
				customLogger.writeLog(this.getClass(), "Connection acquired.", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
		}catch (Exception e) {
			e.printStackTrace();
			if(logPath != null && !StringUtils.isBlank(logPath))
				customLogger.writeErrorLog(this.getClass(), StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), 
						logPath,
						Thread.currentThread().getStackTrace()[1].getLineNumber());
			
			throw new IOException(e.getCause().getMessage());
		}
		return conholder;
	}
}

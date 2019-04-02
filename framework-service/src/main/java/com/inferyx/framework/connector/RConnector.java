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
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.CustomLogger;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author Ganesh
 *
 */
@Component
public class RConnector implements IConnector {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
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
	 * @param logPath
	 *            the logPath to set
	 */
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	@Override
	public ConnectionHolder getConnection() throws IOException {
		ConnectionHolder conholder = new ConnectionHolder();
		try {
			// Datasource datasource = commonServiceImpl.getDatasourceByApp();
			String rHost = commonServiceImpl.getConfigValue("framework.r.host");
			String rPort = commonServiceImpl.getConfigValue("framework.r.port");
			RConnection rCon = null;
			if ((rHost != null && rPort != null) && (!StringUtils.isBlank(rHost) && !StringUtils.isBlank(rPort))) {
				int port = Integer.parseInt(rPort);

//					if(logPath != null && !StringUtils.isBlank(logPath))
//						customLogger.writeLog(this.getClass(), "Acquiring connection from "+rHost+".", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
					
					rCon = new RConnection(rHost, port);
					conholder.setType(ExecContext.R.toString());
					conholder.setConObject(rCon);
					
//					if(logPath != null && !StringUtils.isBlank(logPath))
//						customLogger.writeLog(this.getClass(), "Connection acquired from "+rHost+".", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			} else {
//					if(logPath != null && !StringUtils.isBlank(logPath))
//						customLogger.writeLog(this.getClass(), "Acquiring default connection.", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
					
					rCon = new RConnection();
					conholder.setType(ExecContext.R.toString());
					conholder.setConObject(rCon);
					
//					if(logPath != null && !StringUtils.isBlank(logPath))
//						customLogger.writeLog(this.getClass(), "Connection acquired.", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
			
		} catch (IllegalArgumentException 
				| SecurityException 
				| NullPointerException 
				| RserveException e) {
			e.printStackTrace();
//			if (logPath != null && !StringUtils.isBlank(logPath))
//				customLogger.writeErrorLog(this.getClass(), StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), 
//						logPath,
//						Thread.currentThread().getStackTrace()[1].getLineNumber());
			
			throw new IOException(e.getCause().getMessage());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conholder;
	}

	@Override
	public ConnectionHolder getConnection(Object input, Object input2) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionHolder getConnectionByDatasource(Datasource datasource) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}

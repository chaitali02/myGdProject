/**
 *
 * @Author Ganesh
 *
 */
package com.inferyx.framework.connector;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.common.CustomLogger;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author Ganesh
 *
 */
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
			String rHost = Helper.getPropertyValue("framework.r.host");
			String rPort = Helper.getPropertyValue("framework.r.port");
			RConnection rCon = null;
			if ((rHost != null && rPort != null) && (!StringUtils.isBlank(rHost) && !StringUtils.isBlank(rPort))) {
				int port = Integer.parseInt(rPort);

					if(logPath != null && !StringUtils.isBlank(logPath))
						customLogger.writeLog(this.getClass(), "Acquiring connection from "+rHost+".", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
					
					rCon = new RConnection(rHost, port);
					conholder.setType(ExecContext.R.toString());
					conholder.setConObject(rCon);
					
					if(logPath != null && !StringUtils.isBlank(logPath))
						customLogger.writeLog(this.getClass(), "Connection acquired from "+rHost+".", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			} else {
					if(logPath != null && !StringUtils.isBlank(logPath))
						customLogger.writeLog(this.getClass(), "Acquiring default connection.", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
					
					rCon = new RConnection();
					conholder.setType(ExecContext.R.toString());
					conholder.setConObject(rCon);
					
					if(logPath != null && !StringUtils.isBlank(logPath))
						customLogger.writeLog(this.getClass(), "Connection acquired.", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
			
		} catch (IllegalArgumentException 
				| SecurityException 
				| NullPointerException 
				| RserveException e) {
			e.printStackTrace();
			if (logPath != null && !StringUtils.isBlank(logPath))
				customLogger.writeErrorLog(this.getClass(), StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), 
						logPath,
						Thread.currentThread().getStackTrace()[1].getLineNumber());
			
			throw new IOException(e.getCause().getMessage());
		}
		return conholder;
	}
}

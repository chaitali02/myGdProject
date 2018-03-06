/**
 *
 * @Author Ganesh
 *
 */
package com.inferyx.framework.executor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.CustomLogger;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.connector.RConnector;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.reader.IReader;

/**
 * @author Ganesh
 *
 */
public class RExecutor implements IExecutor {
	@Autowired
	protected ConnectionFactory connectionFactory;

	static final Logger logger = Logger.getLogger(RExecutor.class);
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
	public ResultSetHolder executeSql(String sql) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder executeSql(String sql, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> executeAndFetch(String sql, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder executeAndPersist(String sql, String filePath, Datapod datapod, String saveMode,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder executeAndRegister(String sql, String tableName, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder executeRegisterAndPersist(String sql, String tableName, String filePath, Datapod datapod,
			String saveMode, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean registerTempTable(Dataset<Row> df, String tableName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder registerDataFrameAsTable(ResultSetHolder rsHolder, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerDatapod(String filePath, String tableName, String clientContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object executeScript(String scriptPath, String clientContext) throws IOException {
		IConnector connector = connectionFactory.getConnector(ExecContext.R.toString());
		if (connector instanceof RConnector)
			((RConnector) connector).setLogPath(logPath);

		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getConObject();
		REXP rexp = null;
		Object nativeObject = null;
		if (obj instanceof RConnection) {
			RConnection rCon = (RConnection) obj;
			boolean isSuccessful = false;
			try {
				if (logPath != null && !StringUtils.isBlank(logPath))
					customLogger.writeLog(this.getClass(), "Script execution started.", logPath, 135);
				rCon.voidEval("sink(\""+ logPath +"\", append=TRUE, split=FALSE)");
				rexp = rCon.parseAndEval("source(\"" + scriptPath + "\")");
				nativeObject = rexp.asNativeJavaObject();
				isSuccessful = true;

				logger.info(new ObjectMapper().writeValueAsString(nativeObject));

				if (logPath != null && !StringUtils.isBlank(logPath))
					customLogger.writeLog(this.getClass(),
							"Result: " + new ObjectMapper().writeValueAsString(nativeObject), logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());

				if (logPath != null && !StringUtils.isBlank(logPath))
					customLogger.writeLog(this.getClass(), "Script execution completed.", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			} catch (REngineException | REXPMismatchException e) {
				e.printStackTrace();

				if (logPath != null && !StringUtils.isBlank(logPath))
					customLogger.writeErrorLog(this.getClass(), StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), 
							logPath,
							Thread.currentThread().getStackTrace()[1].getLineNumber());
				
				throw new IOException(e.getCause().getMessage());
			} catch (Exception e) {
				e.printStackTrace();

				if (logPath != null && !StringUtils.isBlank(logPath))
					customLogger.writeErrorLog(this.getClass(), StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), 
							logPath,
							Thread.currentThread().getStackTrace()[1].getLineNumber());
				
				throw new IOException(e.getCause().getMessage());
			} finally {
				if (!isSuccessful)
					if (logPath != null && !StringUtils.isBlank(logPath))
						customLogger.writeErrorLog(this.getClass(), "Script execution failed.", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
				if (rCon != null)
					rCon.close();
			}
		}
		return nativeObject;
	}

	@Override
	public boolean fetchAndTrainModel(Model model, String[] fieldArray, Algorithm algorithm, String modelName,
			String filePath, ParamMap paramMap, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String fetchAndCreatePMML(DataStore datastore, Datapod datapod, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> fetchModelResults(DataStore datastore, Datapod datapod, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long loadAndRegister(Load load, String filePath, String dagExecVer, String loadExecVer,
			String datapodTableName, Datapod datapod, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void registerDatapod(String tableName, Datapod datapod, DataStore dataStore, ExecContext execContext,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Attribute> fetchAttributeList(String csvFileName, String parquetDir, boolean flag,
			boolean writeToParquet, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> submitQuery(String sql, int rowLimit, String format, String header, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}

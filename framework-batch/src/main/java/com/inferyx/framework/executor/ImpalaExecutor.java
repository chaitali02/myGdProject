package com.inferyx.framework.executor;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.ResultType;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.reader.IReader;
import com.inferyx.framework.service.CommonServiceImpl;

public class ImpalaExecutor implements IExecutor {
	
	@Autowired
	ConnectionFactory  connectionFactory;
	@Autowired
	protected CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private DataSourceFactory dataSourceFactory;
	@Autowired
	private MetadataUtil commonActivity;
	
	static final Logger logger = Logger.getLogger(ImpalaExecutor.class);

	@Override
	public ResultSetHolder executeSql(String sql) throws IOException {
		logger.info(" Inside impala executor  for SQL : " + sql);
		// TODO Auto-generated method stub
		ResultSetHolder rsHolder=new ResultSetHolder();
		IConnector connector=connectionFactory.getConnector(ExecContext.IMPALA.toString());
		ConnectionHolder connectionHolder=connector.getConnection();
		Object obj=connectionHolder.getStmtObject();
		long countRows = -1L;
		if(obj instanceof Statement){
			Statement stmt=(Statement) connectionHolder.getStmtObject();
			ResultSet rs = null;
			try {
				if(sql.toUpperCase().contains("INSERT")) {
					countRows = stmt.executeUpdate(sql);
					//countRows = stmt.executeLargeUpdate(sql); Need to check for the large volume of data.
					rsHolder.setCountRows(countRows);
				}
				else {
					rs = stmt.executeQuery(sql);
				}
				rsHolder.setResultSet(rs);
				rsHolder.setType(ResultType.resultset);
			} catch (SQLException 
					| IllegalArgumentException 
					| SecurityException 
					| NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rsHolder;
	}
	@Override
	public Boolean registerTempTable(Dataset<Row> df, String tableName) throws IOException {
		return null;
	}
	@Override
	public ResultSetHolder executeSql(String sql, String clientContext) throws IOException {
		return executeSql(sql);
	}
	@Override
	public List<Map<String, Object>> executeAndFetch(String sql, String clientContext) throws IOException {
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			ResultSetHolder rsHolder = executeSql(sql);
			ResultSet rsSorted = rsHolder.getResultSet();
			ResultSetMetaData rsmd = rsSorted.getMetaData();
			int numOfCols = rsmd.getColumnCount();
			while(rsSorted.next()) {
				Map<String, Object> object = new LinkedHashMap<String, Object>(numOfCols);
				for(int i = 1; i<= numOfCols; i++) {
					//System.out.println(rsmd.getColumnName(i).substring(rsmd.getColumnName(i).indexOf(".")+1) +"  "+ rsSorted.getObject(i).toString());
					if(rsmd.getColumnName(i).contains("."))
						object.put(rsmd.getColumnName(i).substring(rsmd.getColumnName(i).indexOf(".")+1), rsSorted.getObject(i));
					else
						object.put(rsmd.getColumnName(i), rsSorted.getObject(i));
				}
				data.add(object);
			}
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if(requestAttributes != null) {
				HttpServletRequest request = requestAttributes.getRequest();
				if(request != null) {
					HttpSession session = request.getSession();
					if(session != null) {
						session.setAttribute("rsHolder", rsHolder);
					}else
						logger.info("HttpSession is \""+null+"\"");
				}else
					logger.info("HttpServletResponse is \""+null+"\"");
			}else
				logger.info("ServletRequestAttributes requestAttributes is \""+null+"\"");	
		}catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Failed to execute SQL query.");
		}
		return data;
	}
	@Override
	public ResultSetHolder executeAndRegister(String sql, String tableName, String clientContext) throws IOException {
		return executeSql(sql);
	}
	@Override
	public ResultSetHolder executeAndPersist(String sql, String filePath, Datapod datapod, String saveMode,
			String clientContext) throws IOException {
		return executeSql(sql);
	}
	@Override
	public ResultSetHolder executeRegisterAndPersist(String sql, String tableName, String filePath, Datapod datapod,
			String saveMode, String clientContext) throws IOException {
		return executeSql(sql);
	}
	@Override
	public ResultSetHolder registerDataFrameAsTable(ResultSetHolder rsHolder, String tableName) {
		// TODO Auto-generated method stub
		return rsHolder;
	}
	/*@Override
	public ResultSetHolder persistFile(Datapod datapod, String filePath, DataFrame df) {
		try {
			String saveMode = "append";
			System.out.println("************************Impala");
			IWriter datapodWriter = dataSourceFactory.getDatapodWriter(datapod, commonActivity);
			datapodWriter.write(df, filePath, datapod, saveMode);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}*/
	public void registerDatapod(String filePath, String tableName, String clientContext) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Object executeScript(String filePath, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
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

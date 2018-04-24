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
package com.inferyx.framework.executor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.ResultType;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.reader.IReader;
import com.inferyx.framework.service.CommonServiceImpl;

public class OracleExecutor implements IExecutor {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ConnectionFactory  connectionFactory;
	static final Logger logger = Logger.getLogger(OracleExecutor.class);
	@Override
	public ResultSetHolder executeSql(String sql) throws IOException {
		logger.info(" Inside oracle executor for SQL : " + sql);
		ResultSetHolder rsHolder = new ResultSetHolder();
		try {
			IConnector connector = connectionFactory.getConnector(ExecContext.ORACLE.toString());
			ConnectionHolder conHolder = connector.getConnection();
			Object obj = conHolder.getStmtObject();
			long countRows = -1L;
			if(obj instanceof Statement) {
				Statement stmt = (Statement) conHolder.getStmtObject();
				ResultSet rs=null;
				try {
					if(sql.toUpperCase().contains("INSERT")) {
						countRows = stmt.executeUpdate(sql);
						//countRows = stmt.executeLargeUpdate(sql); Need to check for the large volume of data.
						rsHolder.setCountRows(countRows);
					} else 
						rs = stmt.executeQuery(sql);
					rsHolder.setResultSet(rs);
					rsHolder.setType(ResultType.resultset);
				} catch (SQLException e) {				
					e.printStackTrace();
				}		
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
		} catch (IllegalArgumentException | SecurityException | NullPointerException e) {
			e.printStackTrace();
		}
		return rsHolder;
	}

	@Override
	public Boolean registerTempTable(Dataset<Row> df, String tableName) throws IOException {
		// TODO Auto-generated method stub
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
					if(rsmd.getColumnName(i).contains("."))
						object.put((rsmd.getColumnName(i).substring(rsmd.getColumnName(i).indexOf(".")+1)), rsSorted.getObject(i));
					else
						object.put(rsmd.getColumnName(i), rsSorted.getObject(i));
				}
				data.add(object);
			}
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
			System.out.println("************************ORACLE");
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
	public boolean executeScript(String filePath, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object fetchAndTrainModel(Train train, Model model, String[] fieldArray, Algorithm algorithm,
			String modelName, String filePath, ParamMap paramMap, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String fetchAndCreatePMML(DataStore datastore, Datapod datapod, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> fetchModelResults(DataStore datastore, Datapod datapod, int rowLimit, String clientContext) throws Exception {
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

	@Override
	public Object predictModel(Predict predict, String[] fieldArray, Algorithm algorithm, String filePath,
			String tableName, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object simulateModel(Simulate simulate, ExecParams execParams, String[] fieldArray, Algorithm algorithm,
			String filePath, String tableName, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> fetchResults(DataStore datastore, Datapod datapod, int rowLimit, String clientContext)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[][] twoDArrayFromDatapod(String tableName, Datapod factorCovarianceDp, String clientContext)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] oneDArrayFromDatapod(String tableName, Datapod factorMeanDp, String clientContext)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateFeatureData(List<Feature> features, int numIterations, String[] fieldArray, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateFeatureData(Object object, List<Feature> features, int numIterations,
			String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String readFile(String clientContext, Datapod datapod, DataStore datastore, HDFSInfo hdfsInfo,
			Object conObject, Datasource datasource) throws InterruptedException, ExecutionException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String assembleRandomDF(String[] fieldArray, String tableName, boolean isDistribution, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object assembleDF(String[] fieldArray, String tableName, String trainName, String label,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String executePredict(Object trainedModel, Datapod targetDp, String filePathUrl, String tableName,
			String clientContext) throws IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PipelineModel trainModel(ParamMap paramMap, String[] fieldArray, String label, String trainName,
			double trainPercent, double valPercent, String tableName, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean savePMML(Object trngModel, String trainedDSName, String pmmlLocation, String clientContext)
			throws IOException, JAXBException {
		// TODO Auto-generated method stub
		return false;
	}

}

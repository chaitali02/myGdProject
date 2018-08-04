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

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.GraphExec;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.ResultType;
import com.inferyx.framework.domain.RowObj;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.service.CommonServiceImpl;

public class ImpalaExecutor implements IExecutor {
	
	@Autowired
	ConnectionFactory  connectionFactory;
	@Autowired
	protected CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private SparkExecutor<?> sparkExecutor;
	@Autowired
	private Helper helper;
	
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
				} else if(sql.toUpperCase().contains("LOAD DATA")) {
					stmt.execute("SET hive.exec.dynamic.partition="+commonServiceImpl.getSessionParametresPropertyValue("hive.exec.dynamic.partition=", "true")+";");
					stmt.execute("SET hive.exec.dynamic.partition.mode="+commonServiceImpl.getSessionParametresPropertyValue("hive.exec.dynamic.partition.mode", "nonstrict")+";");
					stmt.execute(sql);
				} else {
					rs = stmt.executeQuery(sql);
				}
				rsHolder.setResultSet(rs);
				rsHolder.setType(ResultType.resultset);
			} catch (SQLException 
					| IllegalArgumentException 
					| SecurityException 
					| NullPointerException e) {			
				e.printStackTrace();
				throw new RuntimeException(e);
			}  catch (Exception e) {				
				e.printStackTrace();
				throw new RuntimeException(e);
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
//			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//			if(requestAttributes != null) {
//				HttpServletRequest request = requestAttributes.getRequest();
//				if(request != null) {
//					HttpSession session = request.getSession();
//					if(session != null) {
//						session.setAttribute("rsHolder", rsHolder);
//					}else
//						logger.info("HttpSession is \""+null+"\"");
//				}else
//					logger.info("HttpServletResponse is \""+null+"\"");
//			}else
//				logger.info("ServletRequestAttributes requestAttributes is \""+null+"\"");	
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
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		ResultSetHolder rsHolder = sparkExecutor.uploadCsvToDatabase(load, datasource, datapodTableName, datapod);
		return rsHolder.getCountRows();
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
	public List<Map<String, Object>> fetchResults(DataStore datastore, Datapod datapod, int rowLimit, String targetTable, String clientContext)
			throws Exception {
		String sql = "SELECT * FROM "+targetTable;
		return executeAndFetch(sql, clientContext);
	}
	@Override
	public List<double[]> twoDArray(String sql, List<AttributeRefHolder> attributeInfo, String clientContext)
			throws InterruptedException, ExecutionException, Exception {
		ResultSet resultSet = executeSql(sql, clientContext).getResultSet();		
//		ResultSetMetaData rsmd = resultSet.getMetaData();
//		int numOfCols = rsmd.getColumnCount();
		
		List<String> columnIndexList = new ArrayList<>();
		for(AttributeRefHolder attributeRefHolder : attributeInfo) {
				columnIndexList.add(attributeRefHolder.getAttrName());
		}	
		
		List<double[]> valueList = new ArrayList<>();
		while(resultSet.next()) {
			List<Double> covarsValList = new ArrayList<>();
			for(String col : columnIndexList) {
				covarsValList.add((Double)resultSet.getObject(col));
			}
			valueList.add(ArrayUtils.toPrimitive(covarsValList.toArray(new Double[covarsValList.size()])));
		}	
		return valueList;
	}
	@Override
	public List<Double> oneDArray(String sql, List<AttributeRefHolder> attributeInfo, String clientContext)
			throws InterruptedException, ExecutionException, Exception {
		ResultSet resultSet = executeSql(sql, clientContext).getResultSet();		
//		ResultSetMetaData rsmd = resultSet.getMetaData();
//		int numOfCols = rsmd.getColumnCount();
		
		List<String> columnIndexList = new ArrayList<>();
		for(AttributeRefHolder attributeRefHolder : attributeInfo) {
				columnIndexList.add(attributeRefHolder.getAttrName());
		}	
		
		List<Double> valueList = new ArrayList<>();
		while(resultSet.next()) {
			for(String col : columnIndexList) {
				valueList.add((Double)resultSet.getObject(col));
			}
		}		
		return valueList;
	}
	@Override
	public String generateFeatureData(List<Feature> features, int numIterations, String[] fieldArray, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String readFile(String clientContext, Datapod datapod, DataStore datastore, String tableName,
			HDFSInfo hdfsInfo, Object conObject, Datasource datasource) throws InterruptedException, ExecutionException, Exception {
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
	public ResultSetHolder predict(Object trainedModel, Datapod targetDp, String filePathUrl, String tableName,
			String clientContext) throws IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PipelineModel train(ParamMap paramMap, String[] fieldArray, String label, String trainName,
			double trainPercent, double valPercent, String tableName, String clientContext ,Object algoclass) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean savePMML(Object trngModel, String trainedDSName, String pmmlLocation, String clientContext)
			throws IOException, JAXBException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public ResultSetHolder registerAndPersist(ResultSetHolder rsHolder, String tableName, String filePath,
			Datapod datapod, String saveMode, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String generateFeatureData(Object object, List<Feature> features, int numIterations, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#getDataType(java.lang.String)
	 */
	@Override
	public Object getDataType(String dataType) throws NullPointerException {
		if(dataType == null)
			return null;

		if(dataType.contains("(")) {
			dataType = dataType.substring(0, dataType.indexOf("("));
		}
		
		switch (dataType.toLowerCase()) {
			case "integer": return "INT";
			case "double": return "DOUBLE";
			case "date": return "DATE";
			case "string": return "STRING";
			case "time": return "TIME";
			case "timestamp": return "TIMESTAMP";
			case "long" : return "BIGINT";
			case "binary" : return "BINARY";
			case "boolean" : return "BIT";
			case "byte" : return "TINYINT";
			case "float" : return "FLOAT";
			case "short" : return "SMALLINT";
			case "decimal" : return "DECIMAL";
			case "vector" : return "ARRAY";
			case "array" : return "ARRAY";
			case "null" : return "NULL";
			case "char" : return "CHAR";
			
            default: return null;
		}
	}
	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#joinDf(java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public String joinDf(String joinTabName_1, String joinTabName_2, int i, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#renameColumn(java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public String renameColumn(String tableName, int targetColIndex, String targetColName, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#renameDfColumnName(java.lang.String, java.util.Map, java.lang.String)
	 */
	@Override
	public String renameDfColumnName(String tableName, Map<String, String> mappingList, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder createAndRegister(List<?> data, Class<?> className, String tableName, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ResultSetHolder createRegisterAndPersist(List<RowObj> rowObjList, List<Attribute> attributes,
			String tableName, String filePath, Datapod datapod, String saveMode, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ResultSetHolder generateData(Distribution distribution, Object distributionObject, String methodName, Object[] args, Class<?>[] paramtypes,
			List<Attribute> attributes, int numIterations, String execVersion, String tableName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<String> getCustomDirsFromTrainedModel(Object trngModel) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object loadTrainedModel(Class<?> modelClass, String location)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NullPointerException, ParseException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ResultSetHolder predict2(Object trainedModel, Datapod targetDp, String filePathUrl, String tableName,
			String[] fieldArray, String trainName, String label, Datasource datasource, String clientContext)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NullPointerException, ParseException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long load(Load load, String targetTableName, Datasource datasource, Datapod datapod, String clientContext) throws IOException {
		String sourceTableName = load.getSource().getValue();
		String sql = "SELECT * FROM " + sourceTableName;
		sql = helper.buildInsertQuery(datasource.getType(), targetTableName, datapod, sql);
		ResultSetHolder rsHolder = executeSql(sql, clientContext);
		return rsHolder.getCountRows();
	}
	@Override
	public String createGraphFrame(GraphExec graphExec, DataStore dataStore) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object trainCrossValidation(ParamMap paramMap, String[] fieldArray, String label, String trainName,
			double trainPercent, double valPercent, String tableName, List<Param> hyperParamList, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, Object> summary(Object trndModel, List<String> summaryMethods, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ResultSetHolder create(List<RowObj> rowObjList, List<Attribute> attributes, String tableName,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}

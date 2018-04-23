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
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.inferyx.framework.common.HDFSInfo;
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
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.reader.IReader;

public interface IExecutor {

	ResultSetHolder executeSql(String sql) throws IOException;

	/**
	 * Save sql as per context (shall be used for livy integration)
	 * 
	 * @param sql
	 * @param clientContext
	 * @return
	 * @throws IOException
	 */
	ResultSetHolder executeSql(String sql, String clientContext) throws IOException;

	/**
	 * For executing and fetching results like in getResults
	 * 
	 * @param sql
	 * @param clientContext
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> executeAndFetch(String sql, String clientContext) throws IOException;

	public ResultSetHolder executeAndPersist(String sql, String filePath, Datapod datapod, String saveMode,
			String clientContext) throws IOException;

	/**
	 * For execute and register temp table
	 * 
	 * @param sql
	 * @param tableName
	 * @param clientContext
	 * @return
	 * @throws IOException
	 */
	public ResultSetHolder executeAndRegister(String sql, String tableName, String clientContext) throws IOException;

	/**
	 * For execute, register temp table and persist
	 * 
	 * @param sql
	 * @param tableName
	 * @param clientContext
	 * @return
	 * @throws IOException
	 */
	public ResultSetHolder executeRegisterAndPersist(String sql, String tableName, String filePath, Datapod datapod,
			String saveMode, String clientContext) throws IOException;

	public ResultSetHolder registerDataFrameAsTable(ResultSetHolder rsHolder, String tableName);

	// public ResultSetHolder persistFile(Datapod datapod, String filePath,
	// DataFrame df);

	/**
	 * Register Datapod
	 * 
	 * @param filePath
	 * @param tableName
	 * @param clientContext
	 */
	public void registerDatapod(String filePath, String tableName, String clientContext);

	/**
	 * Execute custom scripts
	 * 
	 * @param filePath
	 * @param clientContext
	 */
	public boolean executeScript(String filePath, String clientContext) throws IOException;

	Boolean registerTempTable(Dataset<Row> df, String tableName) throws IOException;

	/**
	 * Fetch And Train Model
	 * @param train TODO
	 * @param model
	 * @param fieldArray
	 * @param algorithm
	 * @param modelName
	 * @param filePath
	 * @param paramMap
	 * @param clientContext
	 */
	public Object fetchAndTrainModel(Train train, Model model, String[] fieldArray, Algorithm algorithm,
			String modelName, String filePath, ParamMap paramMap, String clientContext) throws Exception;

	/**
	 * Fetch And Create PMML
	 * 
	 * @param datastore
	 * @param datapod
	 * @param clientContext
	 */
	public String fetchAndCreatePMML(DataStore datastore, Datapod datapod, String clientContext) throws Exception;

	/**
	 * Fetch Model Results
	 * @param datapod
	 * @param rowLimit 
	 * @param clientContext
	 * @param modelExec
	 */
	public List<String> fetchModelResults(DataStore datastore, Datapod datapod, int rowLimit, String clientContext) throws Exception;

	/**
	 * Load and Register
	 * 
	 * @param load
	 * @param filePath
	 * @param dagExecVer
	 * @param loadExecVer
	 * @param datapodTableName
	 * @param datapod
	 * @param clientContext
	 */
	public long loadAndRegister(Load load, String filePath, String dagExecVer, String loadExecVer,
			String datapodTableName, Datapod datapod, String clientContext) throws Exception;

	/**
	 * Register Datapod
	 * 
	 * @param tableName
	 * @param datapod
	 * @param dataStore
	 * @param execContext
	 * @param clientContext
	 */
	public void registerDatapod(String tableName, Datapod datapod,
			DataStore dataStore, ExecContext execContext, String clientContext) throws IOException;
	
	/**
	 * Fetch Attribute List
	 * 
	 * @param csvFileName
	 * @param parquetDir
	 * @param flag
	 * @param writeToParquet
	 * @param clientContext
	 */
	public List<Attribute> fetchAttributeList(String csvFileName, String parquetDir, boolean flag,
			boolean writeToParquet, String clientContext) throws Exception;
	
	/**
	 * Fire queryon spark list
	 * 
	 * @param sql
	 * @param rowLimit
	 * @param format
	 * @param header
	 * @param clientContext
	 */
	public List<Object> submitQuery(String sql, int rowLimit, String format, String header, String clientContext)
			throws IOException;
	
	
	/**
	 * Fire predict on model
	 * 
	 * @param predict
	 * @param fieldArray
	 * @param algorithm
	 * @param filePath
	 * @param tableName
	 * @param clientContext
	 */
	public Object predictModel(Predict predict,  String[] fieldArray, Algorithm algorithm, 
			String filePath, String tableName, String clientContext) throws Exception;
	
	/**
	 * Fire simulate on model
	 * 
	 * @param simulate
	 * @param execParams TODO
	 * @param fieldArray
	 * @param algorithm
	 * @param filePath
	 * @param tableName 
	 * @param clientContext
	 */
	public Object simulateModel(Simulate simulate,  ExecParams execParams, String[] fieldArray, 
			Algorithm algorithm, String filePath, String tableName, String clientContext) throws Exception;
	
	/**
	 * Fetch Prediction and Simulation Results
	 * 
	 * @param datastore
	 * @param datapod
	 * @param rowLimit 
	 * @param clientContext
	 */
	public List<Map<String, Object>> fetchResults(DataStore datastore, Datapod datapod, int rowLimit, String clientContext) throws Exception;

	/**
	 * 
	 * @param tableName TODO
	 * @param factorCovarianceDp
	 * @param clientContext TODO
	 * @return
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NullPointerException
	 * @throws ParseException
	 */
	double[][] twoDArrayFromDatapod(String tableName, Datapod factorCovarianceDp, String clientContext)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException;

	/**
	 * 
	 * @param tableName 
	 * @param factorMeanDp
	 * @param clientContext TODO
	 * @return
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NullPointerException
	 * @throws ParseException
	 */
	double[] oneDArrayFromDatapod(String tableName, Datapod factorMeanDp, String clientContext)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException;
	
	/**
	 * 
	 * @param features
	 * @param numIterations
	 * @param fieldArray TODO
	 * @param tableName
	 * @return
	 */
	public String generateFeatureData(List<Feature> features, int numIterations, String[] fieldArray, String tableName) ;
	
	/**
	 * 
	 * @param object
	 * @param features
	 * @param numIterations
	 * @param tableName
	 * @return
	 */
	public String generateFeatureData(Object object, List<Feature> features, int numIterations, String tableName);
	
	/**
	 * 
	 * @param clientContext
	 * @param datapod
	 * @param datastore
	 * @param hdfsInfo
	 * @param conObject
	 * @param datasource
	 * @return
	 */
	public String readFile(String clientContext, Datapod datapod, DataStore datastore, HDFSInfo hdfsInfo,
			Object conObject, Datasource datasource) throws InterruptedException, ExecutionException, Exception;
}

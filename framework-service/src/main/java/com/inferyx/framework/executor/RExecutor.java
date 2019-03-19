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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.CustomLogger;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.connector.RConnector;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.CompareMetaData;
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
import com.inferyx.framework.domain.RowObj;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainResult;
import com.inferyx.framework.enums.EncodingType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.factory.ConnectionFactory;

/**
 * @author Ganesh
 *
 */
@Service
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
			String saveMode, boolean formPath, String clientContext) throws IOException {
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
	public boolean executeScript(String scriptPath, String clientContext) throws IOException {
		IConnector connector = connectionFactory.getConnector(ExecContext.R.toString());
		if (connector instanceof RConnector)
			((RConnector) connector).setLogPath(logPath);

		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getConObject();
		REXP rexp = null;
		Object nativeObject = null;
		boolean isSuccessful = false;
		if (obj instanceof RConnection) {
			RConnection rCon = (RConnection) obj;
			try {
//				if (logPath != null && !StringUtils.isBlank(logPath))
//					customLogger.writeLog(this.getClass(), "Script execution started.", logPath, 135);
				rCon.voidEval("sink(\""+ logPath +"\", append=TRUE, split=FALSE)");
				byte[] encoded = Files.readAllBytes(Paths.get(scriptPath));				
				String fileContent = new String(encoded, Charset.defaultCharset());
				rexp = rCon.eval(fileContent);
				//rexp = rCon.parseAndEval("try(source(\"" + scriptPath + "\"),silent=TRUE)");
				/*if (rexp.inherits("try-error")) {
					  logger.error("R Serve Eval Exception : "+rexp.asString());
					}*/
				try {
					nativeObject = rexp.asNativeJavaObject();
				}catch (REXPMismatchException e) {
					// TODO: handle exception
					//nativeObject = new Object();
				}catch (Exception e) {
					// TODO: handle exception
					//nativeObject = new Object();
				}
				logger.info(new ObjectMapper().writeValueAsString(nativeObject));

//				if (logPath != null && !StringUtils.isBlank(logPath))
//					customLogger.writeLog(this.getClass(),
//							"Result: " + new ObjectMapper().writeValueAsString(nativeObject), logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
//
//				if (logPath != null && !StringUtils.isBlank(logPath))
//					customLogger.writeLog(this.getClass(), "Script execution completed.", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
				isSuccessful = true;
			} catch (REngineException e) {
				e.printStackTrace();
//
//				if (logPath != null && !StringUtils.isBlank(logPath))
//					customLogger.writeErrorLog(this.getClass(), StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), 
//							logPath,
//							Thread.currentThread().getStackTrace()[1].getLineNumber());
				
				throw new IOException(e.getCause().getMessage());
			} catch (Exception e) {
				e.printStackTrace();

//				if (logPath != null && !StringUtils.isBlank(logPath))
//					customLogger.writeErrorLog(this.getClass(), StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), 
//							logPath,
//							Thread.currentThread().getStackTrace()[1].getLineNumber());
				
				throw new IOException(e.getCause().getMessage());
			} finally {
//				if (!isSuccessful)
//					if (logPath != null && !StringUtils.isBlank(logPath))
//						customLogger.writeErrorLog(this.getClass(), "Script execution failed.", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
				if (rCon != null)
					rCon.close();
			}
		}
		return isSuccessful;
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
	public List<Map<String, Object>> fetchResults(DataStore datastore, Datapod datapod, int rowLimit, String targetTable, String clientContext)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<double[]> twoDArray(String sql, List<AttributeRefHolder> attributeInfo, String clientContext)
			throws InterruptedException, ExecutionException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Double> oneDArray(String sql, List<AttributeRefHolder> attributeInfo, String clientContext)
			throws InterruptedException, ExecutionException, Exception {
		// TODO Auto-generated method stub
		return null;
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
			String clientContext, Map<String, EncodingType> encodingDetails) throws IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PipelineModel train(ParamMap paramMap, String[] fieldArray, String label, String trainName,
			double trainPercent, double valPercent, String tableName, String clientContext,Object algoclass, Map<String, Object> trainOtherParam, TrainResult trainResult, String testSetPath, List<String> rowIdentifierCols, String includeFeatures, String trainingDfSql, String validationDfSql, Map<String, EncodingType> encodingDetails, String saveTrainingSet, String trainingSetPath, Datapod testLocationDP, Datasource testLocationDS, String testLocationTableName, String testLFilePathUrl, Datapod trainLocationDP, Datasource trainLocationDS, String trainLocationTableName, String trainFilePathUrl) throws IOException {
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
		// TODO Auto-generated method stub
		return null;
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
	public String renameDfColumnName(String sql, String tableName, Map<String, String> mappingList, String clientContext)
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
	public ResultSetHolder generateData(Distribution distribution, Object distributionObject, String methodName,
			Object[] args, Class<?>[] paramtypes, List<Attribute> attributes, int numIterations, String execVersion,
			String tableName) throws IOException, ClassNotFoundException {
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

	/********************** UNUSED **********************/
//	@Override
//	public ResultSetHolder predict2(Object trainedModel, Datapod targetDp, String filePathUrl, String tableName,
//			String[] fieldArray, String trainName, String label, Datasource datasource, String clientContext)
//			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
//			SecurityException, NullPointerException, ParseException, IOException {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public long load(Load load, String datapodTableName, Datasource datasource, Datapod datapod, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String createGraphFrame(GraphExec graphExec, DataStore dataStore) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder create(List<RowObj> rowObjList, List<Attribute> attributes, String tableName,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder histogram(Datapod locationDatapod, String locationTableName, String sql, String key,
			int numBuckets, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder mattrix(Datapod locationDatapod, String operation, String lhsTableName, String rhsTableName,
			String lhsSql, String rhsSql, String saveTableName, BaseExec baseExec, Map<String, String> otherParams,
			RunMode runMode) throws AnalysisException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CompareMetaData> compareMetadata(Datapod targetDatapod, Datasource datasource, String sourceTableName)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder executeSqlByDatasource(String sql, Datasource datasource, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIncrementalLastValue(ResultSetHolder rsHolder, String clientContext) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Double> featureImportance(Object trainedModel, String clientContext)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> calculateConfusionMatrixAndRoc(Map<String, Object> summary, String tableName,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PipelineModel trainDL(ExecParams execParams, String[] fieldArray, String label, String trainName,
			double trainPercent, double valPercent, String tableName, String clientContext, Object algoClass,
			Map<String, Object> trainOtherParam) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean saveDataframeAsCSV(String tableName, String saveFileName, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> executeAndFetchByDatasource(String sql, Datasource datasource,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder executeAndRegisterByDatasource(String sql, String tableName, Datasource datasource,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder persistDataframe(ResultSetHolder rsHolder, Datasource datasource, Datapod targetDatapod,
			String saveMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> fetchTrainOrTestSet(String location) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder replaceNullValByDoubleValFromDF(ResultSetHolder rsHolder, String sql, Datasource datasource,
			String tableName, boolean registerTempTable, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object assembleDF(String[] fieldArray, ResultSetHolder rsHolder, String sql, String tempTableName,
			Datasource datasource, boolean registerTempTable, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder createAndRegister(List<Row> data, StructType structType, String tableName,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> summary(Object trndModel, String trainClass, List<String> summaryMethods, String clientContext)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedHashMap<String, Object> getImputeValue(ResultSetHolder rsHolder) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetHolder applyAttrImputeValuesToData(ResultSetHolder rsHolder,
			LinkedHashMap<String, Object> imputeAttributeNameWithValues, boolean registerTempTable,
			String tempTableName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object trainCrossValidation(ParamMap paramMap, String[] fieldArray, String label, String trainName,
			double trainPercent, double valPercent, String tableName, List<Param> hyperParamList, String clientContext,
			Map<String, Object> trainOtherParam, TrainResult trainResult, String testSetPath,
			List<String> rowIdentifierCols, String includeFeatures, String trainingDfSql, String validationDfSql,
			Map<String, EncodingType> encodingDetails, String saveTrainingSet, String trainingSetPath,
			Datapod testLocationDP, Datasource testLocationDs, String testLocationTableName, String testLFilePathUrl,
			Datapod trainLocationDP, Datasource trainLocationDS, String trainLocationTableName,
			String trainLocationFilePathUrl, Object algoclass) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}

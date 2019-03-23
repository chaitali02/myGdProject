/**
 * 
 */
package com.inferyx.framework.executor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.collection.ListStringRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.ListStringSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.Builder;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.BaseLayer;
import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.SerializableHelper;
import com.inferyx.framework.connector.IConnector;
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
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class DL4JExecutor implements IExecutor {
	
	@Autowired
	private Helper helper;
	@Autowired
	private ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	private ConnectionFactory connectionFactory;
	@Autowired
	private SparkExecutor<?> sparkExecutor;
	@Autowired
	private SerializableHelper serializableHelper; 
	
	static final Logger logger = Logger.getLogger(DL4JExecutor.class);
	
	/**
	 * 
	 */
	public DL4JExecutor() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#executeSql(java.lang.String)
	 */
	@Override
	public ResultSetHolder executeSql(String sql) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#executeSql(java.lang.String, java.lang.String)
	 */
	@Override
	public ResultSetHolder executeSql(String sql, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#executeAndFetch(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> executeAndFetch(String sql, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#executeAndPersist(java.lang.String, java.lang.String, com.inferyx.framework.domain.Datapod, java.lang.String, java.lang.String)
	 */
	@Override
	public ResultSetHolder executeAndPersist(String sql, String filePath, Datapod datapod, String saveMode,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#executeAndRegister(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ResultSetHolder executeAndRegister(String sql, String tableName, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#executeRegisterAndPersist(java.lang.String, java.lang.String, java.lang.String, com.inferyx.framework.domain.Datapod, java.lang.String, boolean, java.lang.String)
	 */
	@Override
	public ResultSetHolder executeRegisterAndPersist(String sql, String tableName, String filePath, Datapod datapod,
			String saveMode, boolean formPath, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#registerDataFrameAsTable(com.inferyx.framework.domain.ResultSetHolder, java.lang.String)
	 */
	@Override
	public ResultSetHolder registerDataFrameAsTable(ResultSetHolder rsHolder, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#registerDatapod(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void registerDatapod(String filePath, String tableName, String clientContext) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#executeScript(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean executeScript(String filePath, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#registerTempTable(org.apache.spark.sql.Dataset, java.lang.String)
	 */
	@Override
	public Boolean registerTempTable(Dataset<Row> df, String tableName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#fetchAndTrainModel(com.inferyx.framework.domain.Train, com.inferyx.framework.domain.Model, java.lang.String[], com.inferyx.framework.domain.Algorithm, java.lang.String, java.lang.String, org.apache.spark.ml.param.ParamMap, java.lang.String)
	 */
	@Override
	public Object fetchAndTrainModel(Train train, Model model, String[] fieldArray, Algorithm algorithm,
			String modelName, String filePath, ParamMap paramMap, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#fetchAndCreatePMML(com.inferyx.framework.domain.DataStore, com.inferyx.framework.domain.Datapod, java.lang.String)
	 */
	@Override
	public String fetchAndCreatePMML(DataStore datastore, Datapod datapod, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#fetchModelResults(com.inferyx.framework.domain.DataStore, com.inferyx.framework.domain.Datapod, int, java.lang.String)
	 */
	@Override
	public List<String> fetchModelResults(DataStore datastore, Datapod datapod, int rowLimit, String clientContext)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#loadAndRegister(com.inferyx.framework.domain.Load, java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.inferyx.framework.domain.Datapod, java.lang.String)
	 */
	@Override
	public long loadAndRegister(Load load, String filePath, String dagExecVer, String loadExecVer,
			String datapodTableName, Datapod datapod, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#registerDatapod(java.lang.String, com.inferyx.framework.domain.Datapod, com.inferyx.framework.domain.DataStore, com.inferyx.framework.executor.ExecContext, java.lang.String)
	 */
	@Override
	public void registerDatapod(String tableName, Datapod datapod, DataStore dataStore, ExecContext execContext,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#fetchAttributeList(java.lang.String, java.lang.String, boolean, boolean, java.lang.String)
	 */
	@Override
	public List<Attribute> fetchAttributeList(String csvFileName, String parquetDir, boolean flag,
			boolean writeToParquet, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#submitQuery(java.lang.String, int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Object> submitQuery(String sql, int rowLimit, String format, String header, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#predictModel(com.inferyx.framework.domain.Predict, java.lang.String[], com.inferyx.framework.domain.Algorithm, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Object predictModel(Predict predict, String[] fieldArray, Algorithm algorithm, String filePath,
			String tableName, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#simulateModel(com.inferyx.framework.domain.Simulate, com.inferyx.framework.domain.ExecParams, java.lang.String[], com.inferyx.framework.domain.Algorithm, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Object simulateModel(Simulate simulate, ExecParams execParams, String[] fieldArray, Algorithm algorithm,
			String filePath, String tableName, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#fetchResults(com.inferyx.framework.domain.DataStore, com.inferyx.framework.domain.Datapod, int, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> fetchResults(DataStore datastore, Datapod datapod, int rowLimit,
			String targetTable, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#twoDArray(java.lang.String, java.util.List, java.lang.String)
	 */
	@Override
	public List<double[]> twoDArray(String sql, List<AttributeRefHolder> attributeInfo, String clientContext)
			throws InterruptedException, ExecutionException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#oneDArray(java.lang.String, java.util.List, java.lang.String)
	 */
	@Override
	public List<Double> oneDArray(String sql, List<AttributeRefHolder> attributeInfo, String clientContext)
			throws InterruptedException, ExecutionException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#generateFeatureData(java.util.List, int, java.lang.String[], java.lang.String)
	 */
	@Override
	public String generateFeatureData(List<Feature> features, int numIterations, String[] fieldArray,
			String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#generateFeatureData(java.lang.Object, java.util.List, int, java.lang.String)
	 */
	@Override
	public String generateFeatureData(Object object, List<Feature> features, int numIterations, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#readFile(java.lang.String, com.inferyx.framework.domain.Datapod, com.inferyx.framework.domain.DataStore, java.lang.String, com.inferyx.framework.common.HDFSInfo, java.lang.Object, com.inferyx.framework.domain.Datasource)
	 */
	@Override
	public String readFile(String clientContext, Datapod datapod, DataStore datastore, String tableName,
			HDFSInfo hdfsInfo, Object conObject, Datasource datasource)
			throws InterruptedException, ExecutionException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#assembleRandomDF(java.lang.String[], java.lang.String, boolean, java.lang.String)
	 */
	@Override
	public String assembleRandomDF(String[] fieldArray, String tableName, boolean isDistribution, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#assembleDF(java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Object assembleDF(String[] fieldArray, String tableName, String trainName, String label,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#predict(java.lang.Object, com.inferyx.framework.domain.Datapod, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ResultSetHolder predict(Object trainedModel, Datapod targetDp, String filePathUrl, String tableName,
			String clientContext, Map<String, EncodingType> encodingDetails) throws IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#train(org.apache.spark.ml.param.ParamMap, java.lang.String[], java.lang.String, java.lang.String, double, double, java.lang.String, java.lang.String, java.lang.Object, java.util.Map)
	 */
	@Override
	public PipelineModel train(ParamMap paramMap, String[] fieldArray, String label, String trainName,
			double trainPercent, double valPercent, String tableName, String clientContext, Object algoClass,
			Map<String, Object> trainOtherParam, TrainResult trainResult, String testSetPath, List<String> rowIdentifierCols, String includeFeatures, String trainingDfSql, String validationDfSql, Map<String, EncodingType> encodingDetails, String saveTrainingSet, String trainingSetPath, Datapod testLocationDP, Datasource testLocationDS, String testLocationTableName, String testLFilePathUrl, Datapod trainLocationDP, Datasource trainLocationDS, String trainLocationTableName, String trainFilePathUrl) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#savePMML(java.lang.Object, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean savePMML(Object trngModel, String trainedDSName, String pmmlLocation, String clientContext)
			throws IOException, JAXBException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#registerAndPersist(com.inferyx.framework.domain.ResultSetHolder, java.lang.String, java.lang.String, com.inferyx.framework.domain.Datapod, java.lang.String, java.lang.String)
	 */
	@Override
	public ResultSetHolder registerAndPersist(ResultSetHolder rsHolder, String tableName, String filePath,
			Datapod datapod, String saveMode, String clientContext) throws IOException {
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

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#createAndRegister(java.util.List, java.lang.Class, java.lang.String, java.lang.String)
	 */
	@Override
	public ResultSetHolder createAndRegister(List<?> data, Class<?> className, String tableName, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#createRegisterAndPersist(java.util.List, java.util.List, java.lang.String, java.lang.String, com.inferyx.framework.domain.Datapod, java.lang.String, java.lang.String)
	 */
	@Override
	public ResultSetHolder createRegisterAndPersist(List<RowObj> rowObjList, List<Attribute> attributes,
			String tableName, String filePath, Datapod datapod, String saveMode, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#generateData(com.inferyx.framework.domain.Distribution, java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Class[], java.util.List, int, java.lang.String, java.lang.String)
	 */
	@Override
	public ResultSetHolder generateData(Distribution distribution, Object distributionObject, String methodName,
			Object[] args, Class<?>[] paramtypes, List<Attribute> attributes, int numIterations, String execVersion,
			String tableName) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#getCustomDirsFromTrainedModel(java.lang.Object)
	 */
	@Override
	public List<String> getCustomDirsFromTrainedModel(Object trngModel) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#loadTrainedModel(java.lang.Class, java.lang.String)
	 */
	@Override
	public Object loadTrainedModel(Class<?> modelClass, String location)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NullPointerException, ParseException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public long load(Load load, String targetTableName, Datasource datasource, Datapod datapod, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#createGraphFrame(com.inferyx.framework.domain.GraphExec, com.inferyx.framework.domain.DataStore)
	 */
	@Override
	public String createGraphFrame(GraphExec graphExec, DataStore dataStore) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#create(java.util.List, java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public ResultSetHolder create(List<RowObj> rowObjList, List<Attribute> attributes, String tableName,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#histogram(com.inferyx.framework.domain.Datapod, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public ResultSetHolder histogram(Datapod locationDatapod, String locationTableName, String sql, String key,
			int numBuckets, String clientContext, Datasource datasource) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#mattrix(com.inferyx.framework.domain.Datapod, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.inferyx.framework.domain.BaseExec, java.util.Map, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public ResultSetHolder mattrix(Datapod locationDatapod, String operation, String lhsTableName, String rhsTableName,
			String lhsSql, String rhsSql, String saveTableName, BaseExec baseExec, Map<String, String> otherParams,
			RunMode runMode) throws AnalysisException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#compareMetadata(com.inferyx.framework.domain.Datapod, com.inferyx.framework.domain.Datasource, java.lang.String)
	 */
	@Override
	public List<CompareMetaData> compareMetadata(Datapod targetDatapod, Datasource datasource, String sourceTableName)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#executeSqlByDatasource(java.lang.String, com.inferyx.framework.domain.Datasource, java.lang.String)
	 */
	@Override
	public ResultSetHolder executeSqlByDatasource(String sql, Datasource datasource, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#getIncrementalLastValue(com.inferyx.framework.domain.ResultSetHolder, java.lang.String)
	 */
	@Override
	public String getIncrementalLastValue(ResultSetHolder rsHolder, String clientContext) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#featureImportance(java.lang.Object, java.lang.String)
	 */
	@Override
	public List<Double> featureImportance(Object trainedModel, String clientContext)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#calculateConfusionMatrixAndRoc(java.util.Map, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> calculateConfusionMatrixAndRoc(Map<String, Object> summary, String tableName,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Create a neural net Configuration
	 * @param seed
	 * @param iterations
	 * @param learningRate
	 * @param optimizationAlgo
	 * @param weightInit
	 * @param updater
	 * @param momentum
	 * @return
	 */
	public Builder neuralNetConf(int seed, int iterations, double learningRate, String optimizationAlgo, String weightInit, String updater, float momentum) {
		/*return new NeuralNetConfiguration.Builder().seed(seed)
				.iterations(iterations).optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.learningRate(learningRate).weightInit(WeightInit.XAVIER).updater(Updater.NESTEROVS).momentum(0.9);*/
		NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
		if (seed >= 0) {
			builder = builder.seed(seed);
		}
		if (iterations >= 0) {
			builder = builder.iterations(iterations);
		}
		if (StringUtils.isNotBlank(optimizationAlgo)) {
			builder = builder.optimizationAlgo(helper.getOptimizationAlgorithm(optimizationAlgo));
		}
		if (learningRate >= 0) {
			builder = builder.learningRate(learningRate);
		}
		builder = builder.weightInit(WeightInit.XAVIER).updater(Updater.NESTEROVS);
		if (momentum >= 0) {
			builder = builder.momentum(0.9);
		}
		return builder;
	}
	
	/**
	 * 
	 * @param numInput
	 * @param numOutputs
	 * @param numHidden
	 * @param numLayers
	 * @param layerNames
	 * @param activation
	 * @param lossFunction
	 * @return
	 */
	public List<Layer> createLayers (int numInput, int numOutputs, int numHidden, int numLayers, List<String> layerNames, List<String> activation, List<String> lossFunction) {
		List<Layer> layers = new ArrayList<Layer>();
		for (int i = 0; i < numLayers; i++) {
			Layer.Builder builder = helper.getLayerBuilders(layerNames.get(i));
			if (builder instanceof BaseLayer.Builder) {
				if (StringUtils.isNotBlank(activation.get(i))) {
					builder = ((BaseLayer.Builder) builder).activation(activation.get(i));
				}
				if (builder instanceof FeedForwardLayer.Builder) {
					builder = ((FeedForwardLayer.Builder) builder).nIn(numInput).nOut(numOutputs);
				}
			}
			layers.add(builder.build());
		}
		return layers;
	}
	
	/**
	 * 
	 * @param builder
	 * @param layers
	 * @return
	 */
	public MultiLayerConfiguration multLayerNetConf(Builder builder, List<Layer> layers) {
		Layer[] layerArr = new Layer[layers.size()];
		MultiLayerConfiguration.Builder multBuilder = builder.list(layers.toArray(layerArr)).pretrain(false).backprop(true);
		return multBuilder.build();
	}

	@Override
	public PipelineModel trainDL(ExecParams execParams, String[] fieldArray, String label, String trainName,
			double trainPercent, double valPercent, String tableName, String clientContext, Object algoClass,
			Map<String, Object> trainOtherParam) throws IOException {
		
		int batchSize = 50;

		int nEpochs = Integer.parseInt(paramSetServiceImpl.getParamByName(execParams, "nEpochs").getParamValue().getValue());
		int seed = Integer.parseInt(paramSetServiceImpl.getParamByName(execParams, "seed").getParamValue().getValue());
		int iterations = Integer.parseInt(paramSetServiceImpl.getParamByName(execParams, "iterations").getParamValue().getValue());
		double learningRate = Double.parseDouble(paramSetServiceImpl.getParamByName(execParams, "learningRate").getParamValue().getValue());
		String optimizationAlgo = paramSetServiceImpl.getParamByName(execParams, "optimizationAlgo").getParamValue().getValue();
		String weightInit = paramSetServiceImpl.getParamByName(execParams, "weightInit").getParamValue().getValue();
		String updater = paramSetServiceImpl.getParamByName(execParams, "updater").getParamValue().getValue();
		float momentum = Float.parseFloat(paramSetServiceImpl.getParamByName(execParams, "momentum").getParamValue().getValue());
		int numInput = Integer.parseInt(paramSetServiceImpl.getParamByName(execParams, "numInput").getParamValue().getValue());
		int numOutputs = Integer.parseInt(paramSetServiceImpl.getParamByName(execParams, "numOutputs").getParamValue().getValue());
		int numHidden = Integer.parseInt(paramSetServiceImpl.getParamByName(execParams, "numHidden").getParamValue().getValue());
		int numLayers = Integer.parseInt(paramSetServiceImpl.getParamByName(execParams, "numLayers").getParamValue().getValue());
		List<String> layerNames = Arrays.asList(paramSetServiceImpl.getParamByName(execParams, "layerNames").getParamValue().getValue().split(","));
		List<String> activation = Arrays.asList(paramSetServiceImpl.getParamByName(execParams, "activation").getParamValue().getValue().split(","));
		List<String> lossFunction = Arrays.asList(paramSetServiceImpl.getParamByName(execParams, "lossFunction").getParamValue().getValue().split(","));
		
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		String assembledDFSQL = "SELECT * FROM " + tableName;
		Dataset<Row> df = sparkExecutor.executeSql(assembledDFSQL, clientContext).getDataFrame();
		df.printSchema();
		try {
			Dataset<Row>[] splits = df.randomSplit(new double[] { trainPercent / 100, valPercent / 100 }, 12345);
			Dataset<Row> trngDf = splits[0];
			Dataset<Row> valDf = splits[1];
			Dataset<Row> trainingDf = null;
			Dataset<Row> validateDf = null;
			
			VectorAssembler vectorAssembler = new VectorAssembler();
			vectorAssembler.setInputCols(fieldArray).setOutputCol("features");
			
			/*Class<?> dynamicClass = Class.forName(trainName);
			Object obj = dynamicClass.newInstance();*/
			Method method = null;
			if (trainName.contains("LinearRegression")
					|| trainName.contains("LogisticRegression")
					|| trainName.contains("LinearSVC")
					|| trainName.contains("RandomForest")
					|| trainName.contains("AFTSurvivalRegression")
					|| trainName.contains("DecisionTree")
					|| trainName.contains("NaiveBayes")) {
				method = algoClass.getClass().getMethod("setLabelCol", String.class);
				method.invoke(algoClass, "label");
				
				trainingDf = trngDf.withColumn("label", trngDf.col(label).cast("Double")).select("label", vectorAssembler.getInputCols());
				validateDf = valDf.withColumn("label", valDf.col(label).cast("Double")).select("label", vectorAssembler.getInputCols());
			} else {
				trainingDf = trngDf;
				validateDf = valDf;
			}
			
			// Create DataSetIterator
			
			DataSetIterator iterator = getDataSet(trainingDf, batchSize, trainingDf.schema().fieldIndex("label"), (int)trainingDf.select("label").distinct().count());
			

			for(String col : trainingDf.columns())
				trainingDf = trainingDf.withColumn(col, trainingDf.col(col).cast(DataTypes.DoubleType));

			for(String col : validateDf.columns())
				validateDf = validateDf.withColumn(col, validateDf.col(col).cast(DataTypes.DoubleType));
			
			Builder builder = neuralNetConf(seed, iterations, learningRate, optimizationAlgo, weightInit, updater, momentum);
			List<Layer> layers = createLayers(numInput, numOutputs, numHidden, numLayers, layerNames, activation, lossFunction);
			MultiLayerConfiguration multiLayerConfiguration = multLayerNetConf(builder, layers);
			MultiLayerNetwork net = new MultiLayerNetwork(multiLayerConfiguration);
			net.init();
			net.setListeners(new ScoreIterationListener(1));
	
			// Train the network on the full data set, and evaluate in periodically
			for (int i = 0; i < nEpochs; i++) {
				iterator.reset();
				net.fit(iterator);
			}
		} catch (IllegalAccessException 
				| IllegalArgumentException
				| SecurityException
				| NoSuchMethodException
				| InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return null;
	}
	
	/**
	 * 
	 * @param df
	 * @param reader
	 * @return
	 */
	private RecordReader transform(Dataset<Row> df, RecordReader reader) {
		
		Schema.Builder schemaBuilder = new Schema.Builder();
		StructType sparkSchema = df.schema();
		String colName = null;
		int count = 0;
		for (String col : df.columns()) {
			if (count>=4) {
				break;
			}
			count++;
			if (col.equals("label")) {
				colName = "label";
				serializableHelper.addColToSchemaBuilder(colName, schemaBuilder, df);
			} else { 
				schemaBuilder.addColumnDouble(col);
			}

		}
		Schema schema = schemaBuilder.build();
                

		TransformProcess transformProcess = new TransformProcess.Builder(schema)  
                .categoricalToOneHot("label")//Applying one-hot encoding                                        
                .build();
		RecordReader transformProcessRecordReader = new TransformProcessRecordReader(reader,transformProcess);
		return transformProcessRecordReader;
	}
	
	/**
	 * 
	 * @param df
	 * @param batchSize
	 * @param labelIndex
	 * @param numPossibleLabels
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private DataSetIterator getDataSet(Dataset<Row> df, int batchSize, int labelIndex, int numPossibleLabels) throws IOException, InterruptedException {
		logger.info("Inside getDataSet ");
		int nSamples = (int) df.count();
		logger.info("Number of samples : " + nSamples);
		List<String> rows = df.map(row -> row.mkString(), Encoders.STRING()).collectAsList();
		logger.info("After map number of rows : " + rows.size());
		ListStringSplit input = new ListStringSplit(Collections.singletonList(rows));
		logger.info("After split");
        ListStringRecordReader rr = new ListStringRecordReader();
        rr.initialize(input);
        logger.info("After initialization");
        RecordReader transformedRR = transform(df, rr);
        logger.info("After transformation");
        DataSetIterator iterator = new RecordReaderDataSetIterator(transformedRR, batchSize, labelIndex, numPossibleLabels);
        logger.info("After iterator creation");
		return iterator;

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

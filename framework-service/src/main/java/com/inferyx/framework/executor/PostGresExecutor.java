/**
 * 
 */
package com.inferyx.framework.executor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBException;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
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

/**
 * @author Ganesh
 *
 */
public class PostGresExecutor implements IExecutor {

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
	 * @see com.inferyx.framework.executor.IExecutor#executeRegisterAndPersist(java.lang.String, java.lang.String, java.lang.String, com.inferyx.framework.domain.Datapod, java.lang.String, java.lang.String)
	 */
	@Override
	public ResultSetHolder executeRegisterAndPersist(String sql, String tableName, String filePath, Datapod datapod,
			String saveMode, String clientContext) throws IOException {
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
	 * @see com.inferyx.framework.executor.IExecutor#fetchResults(com.inferyx.framework.domain.DataStore, com.inferyx.framework.domain.Datapod, int, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> fetchResults(DataStore datastore, Datapod datapod, int rowLimit,
			String clientContext) throws Exception {
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
	 * @see com.inferyx.framework.executor.IExecutor#generateData(java.lang.Object, java.util.List, int, java.lang.String)
	 */
	@Override
	public ResultSetHolder generateData(Object distributionObject, List<Attribute> attributes, int numIterations,
			String execVersion) throws Exception {
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
	 * @see com.inferyx.framework.executor.IExecutor#executePredict(java.lang.Object, com.inferyx.framework.domain.Datapod, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String executePredict(Object trainedModel, Datapod targetDp, String filePathUrl, String tableName,
			String clientContext) throws IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.executor.IExecutor#trainModel(org.apache.spark.ml.param.ParamMap, java.lang.String[], java.lang.String, java.lang.String, double, double, java.lang.String, java.lang.String)
	 */
	@Override
	public PipelineModel trainModel(ParamMap paramMap, String[] fieldArray, String label, String trainName,
			double trainPercent, double valPercent, String tableName, String clientContext) throws IOException {
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

}

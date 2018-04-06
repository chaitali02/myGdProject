/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.List;

import org.apache.spark.SparkContext;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.storage.StorageLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.ModelServiceImpl;
import com.inferyx.framework.writer.IWriter;

/**
 * @author joy
 *
 */
@Component
public class PredictMLOperator {
	
	@Autowired
	private SparkContext sparkContext;
	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private SQLContext sqlContext;
	@Autowired
	private MetadataUtil daoRegister;
	@Autowired
	private DataSourceFactory datasourceFactory;
	@Autowired
	private ConnectionFactory connectionFactory;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private ModelServiceImpl modelServiceImpl; 
	/**
	 * 
	 */
	public PredictMLOperator() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public Object predict(Predict predict, Model model, Algorithm algorithm, Datapod targetDp, Dataset<Row> df, String[] fieldArray, TrainExec latestTrainExec,
			VectorAssembler va, String targetType, String tableName, String filePathUrl, String filePath, String clientContext) throws Exception {

			Vector features = null; // extract feature column as vector from df
			String modelName = algorithm.getModelName();
			Class<?> modelClass = Class.forName(modelName);

			MetaIdentifierHolder datastoreHolder = latestTrainExec.getResult();
			DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(datastoreHolder.getRef().getUuid(),
					datastoreHolder.getRef().getVersion(), datastoreHolder.getRef().getType().toString());
			if (dataStore == null)
				throw new NullPointerException("No datastore available");
			String location = dataStore.getLocation();

			if (location.contains("/data"))
				location = location.replaceAll("/data", "");
			
			location = hdfsInfo.getHdfsURL() + location;

			Object trainedModel = modelClass.getMethod("load", String.class).invoke(modelClass, location);
			Dataset<Row> predictionDf = (Dataset<Row>) trainedModel.getClass().getMethod("transform", Dataset.class)
					.invoke(trainedModel, df);
			predictionDf.show();

			String uid = (String) trainedModel.getClass().getMethod("uid").invoke(trainedModel);

			if (targetType.equalsIgnoreCase(MetaType.datapod.toString())) {
				Datasource datasource = commonServiceImpl.getDatasourceByApp();

				List<Attribute> attributeList = targetDp.getAttributes();
				Attribute version = new Attribute();
				version.setActive("Y");
				version.setAttributeId(attributeList.size());
				version.setDesc("version");
				version.setDispName("version");
				version.setName("version");
				version.setType("String");
				version.setPartition("N");
				attributeList.add(version);
				targetDp.setAttributes(attributeList);

				df.createOrReplaceGlobalTempView("tempPredictResult");
				IConnector connector = connectionFactory.getConnector(datasource.getType().toLowerCase());
				ConnectionHolder conHolder = connector.getConnection();
				if (conHolder.getStmtObject() instanceof SparkSession) {
					SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
					predictionDf.persist(StorageLevel.MEMORY_AND_DISK());
					sparkSession.sqlContext().registerDataFrameAsTable(predictionDf, "tempPredictResult");
				}
				IExecutor exec = execFactory.getExecutor(datasource.getType());
				String columns = "";
				for (String col : predictionDf.columns())
					columns = columns.concat(col).concat(" AS ").concat(col).concat(",");
				columns = columns.substring(0, columns.length() - 2);
				String sql = "SELECT " + columns + " FROM " + "tempPredictResult";
				ResultSetHolder rsHolder = exec.executeSql(sql, commonServiceImpl.getApp().getUuid());

				Dataset<Row> dfTask = rsHolder.getDataFrame();
				dfTask.cache();

				sqlContext.registerDataFrameAsTable(dfTask, tableName.replaceAll("-", "_"));

				dfTask.printSchema();
				IWriter datapodWriter = datasourceFactory.getDatapodWriter(targetDp, daoRegister);
				datapodWriter.write(dfTask, filePathUrl, targetDp, SaveMode.Append.toString());
				return filePathUrl;
			} else {
				if (modelServiceImpl.save(modelName, trainedModel, sparkContext, filePathUrl))
					return filePathUrl + "/data";
				else
					return null;
			}	
	}


}

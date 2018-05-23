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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.spark.SparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.feature.RFormula;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.DenseVector;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.VectorUDT;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.storage.StorageLevel;
import org.dmg.pmml.PMML;
import org.jpmml.model.JAXBUtil;
import org.jpmml.model.MetroJAXBUtil;
import org.jpmml.sparkml.ConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.DataFrameHolder;
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
import com.inferyx.framework.service.ModelExecServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;
import com.inferyx.framework.writer.IWriter;

import scala.collection.Iterator;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.collection.mutable.WrappedArray;

@Component
public class SparkExecutor implements IExecutor {

	/*
	 * @Autowired HiveContext hiveContext;
	 */
	@Autowired
	ConnectionFactory connectionFactory;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SQLContext sqlContext;
	@Autowired
	DataSourceFactory dataSourceFactory;
	@Autowired
	MetadataUtil commonActivity;
	@Autowired
	HDFSInfo hdfsInfo;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	SparkContext sparkContext;
	@Autowired
	ModelExecServiceImpl modelExecServiceImpl;
	@Autowired
	private ConnectionFactory connFactory;
	/*
	 * @Autowired HiveContext hiveContext;
	 */
	@Autowired
	private SparkSession sparkSession;
	@Autowired
	private MetadataUtil daoRegister;
	@Autowired
	private DataSourceFactory datasourceFactory;

	static final Logger logger = Logger.getLogger(SparkExecutor.class);

	@Override
	public ResultSetHolder executeSql(String sql) throws IOException {
		return executeSql(sql, null);
	}

	public ResultSetHolder executeSql(String sql, String clientContext) throws IOException {
		logger.info(" Inside spark executor  for SQL : " + sql);
		ResultSetHolder rsHolder = new ResultSetHolder();
		try {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			if (datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
					|| datasource.getType().toLowerCase().equalsIgnoreCase(ExecContext.FILE.toString())
					|| datasource.getType().toLowerCase().equalsIgnoreCase(ExecContext.HIVE.toString())
					|| datasource.getType().toLowerCase().equalsIgnoreCase(ExecContext.IMPALA.toString())) {
				IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
				ConnectionHolder conHolder = connector.getConnection();
				Object obj = conHolder.getStmtObject();
				if (obj instanceof SparkSession) {
					SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
					Dataset<Row> df = sparkSession.sql(sql);
					df.show(true);
					rsHolder.setDataFrame(df);
					rsHolder.setType(ResultType.dataframe);
				}
			} else if (datasource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString())) {
				Dataset<Row> dataFrame = sqlContext.read().format("jdbc")
						.option("spark.driver.extraClassPath", datasource.getDriver())
						.option("spark.executor.extraClassPath", datasource.getDriver())
						.option("driver", datasource.getDriver())
						.option("url",
								"jdbc:mysql://" + datasource.getHost() + ":" + datasource.getPort() + "/"
										+ datasource.getDbname())
						.option("user", datasource.getUsername()).option("password", datasource.getPassword())
						.option("dbtable", "(" + sql + ") as mysql_table").load();
				rsHolder.setDataFrame(dataFrame);
				rsHolder.setType(ResultType.dataframe);
			} else if (datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
				Dataset<Row> dataFrame = sqlContext.read().format("jdbc")
						.option("driver", datasource.getDriver())
						.option("url",
								"jdbc:oracle:thin:@" + datasource.getHost() + ":" + datasource.getPort() + ":"
										+ datasource.getDbname())
						.option("user", datasource.getUsername()).option("password", datasource.getPassword())
						.option("dbtable", "(" + sql + ")  oracle_table").load();
				rsHolder.setDataFrame(dataFrame);
				rsHolder.setType(ResultType.dataframe);
			} else if (datasource.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) {
				Dataset<Row> dataFrame = sqlContext.read().format("jdbc")
						.option("driver", datasource.getDriver())
						.option("url",
								"jdbc:postgresql://" + datasource.getHost() + ":" + datasource.getPort() + "/"
										+ datasource.getDbname())
						.option("user", datasource.getUsername()).option("password", datasource.getPassword())
						.option("dbtable", "(" + sql + ") as postgres_table").load();
				rsHolder.setDataFrame(dataFrame);
				rsHolder.setType(ResultType.dataframe);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rsHolder;
	}

	@Override
	public List<Map<String, Object>> executeAndFetch(String sql, String clientContext) throws IOException {
		logger.info(" Inside spark executor  for SQL : " + sql);
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		List<Map<String, Object>> data = new ArrayList<>();
		Object obj = conHolder.getStmtObject();
		Dataset<Row> dfSorted = null;
		if (obj instanceof SparkSession) {
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			dfSorted = sparkSession.sql(sql);

			Row[] rows = (Row[]) dfSorted.head(Integer.parseInt("" + dfSorted.count()));
			String[] columns = dfSorted.columns();
			for (Row row : rows) {
				Map<String, Object> object = new LinkedHashMap<String, Object>(columns.length);
				for (String column : columns) {
					//object.put(column, row.getAs(column));
					object.put(column, (row.getAs(column) == null ? "" :
						(row.getAs(column) instanceof Vector) ? Arrays.toString((double[])((Vector)row.getAs(column)).toArray()) : row.getAs(column)));
				}
				data.add(object);
			}
			/*
			 * ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
			 * RequestContextHolder .getRequestAttributes(); if (requestAttributes != null)
			 * { HttpServletRequest request = requestAttributes.getRequest(); if (request !=
			 * null) { HttpSession session = request.getSession(); if (session != null) {
			 * ResultSetHolder rsHolder = new ResultSetHolder();
			 * rsHolder.setDataFrame(dfSorted); rsHolder.setType(ResultType.dataframe);
			 * session.setAttribute("rsHolder", rsHolder); } else
			 * logger.info("HttpSession is \"" + null + "\""); } else
			 * logger.info("HttpServletResponse is \"" + null + "\""); } else
			 * logger.info("ServletRequestAttributes requestAttributes is \"" + null +
			 * "\"");
			 */
		} // if SparkSession
		return data;
	}

	public ResultSetHolder executeAndRegister(String sql, String tableName, String clientContext) throws IOException {
		ResultSetHolder resHolder = executeSql(sql, clientContext);
		Dataset<Row> df = resHolder.getDataFrame();
		long countRows = df.count();
		resHolder.setCountRows(countRows);
		df.createOrReplaceGlobalTempView(tableName);
		registerTempTable(df, tableName);
		logger.info("temp table registered: " + tableName);
		return resHolder;
	}
	
	@Override
	public ResultSetHolder createAndRegister(List<?> data, Class<?> className, String tableName, String clientContext) throws IOException {
		ResultSetHolder rsHolder = new ResultSetHolder();
		try {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			if (datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
					|| datasource.getType().toLowerCase().equalsIgnoreCase(ExecContext.FILE.toString())
					|| datasource.getType().toLowerCase().equalsIgnoreCase(ExecContext.HIVE.toString())
					|| datasource.getType().toLowerCase().equalsIgnoreCase(ExecContext.IMPALA.toString())) {
				IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
				ConnectionHolder conHolder = connector.getConnection();
				Object obj = conHolder.getStmtObject();
				if (obj instanceof SparkSession) {
					SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
					Dataset<Row> df = sparkSession.createDataFrame(data, className);
					df.show();
					rsHolder.setDataFrame(df);
					rsHolder.setType(ResultType.dataframe);
					long countRows = df.count();
					rsHolder.setCountRows(countRows);
					df.createOrReplaceGlobalTempView(tableName);
					registerTempTable(df, tableName);
					logger.info("temp table registered: " + tableName);
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rsHolder;
	}

	@Override
	/**
	 * Register table as dataframe
	 */
	public Boolean registerTempTable(Dataset<Row> dfTemp, String tableName) throws IOException {
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession) {
			sparkSession = (SparkSession) conHolder.getStmtObject();
			// dfTemp.registerTempTable("temp");
			// DataFrame df = hiveContext.sql("Select Row_Number() Over() as rownum,* from
			// temp");
			dfTemp.persist(StorageLevel.MEMORY_AND_DISK());
			sparkSession.sqlContext().registerDataFrameAsTable(dfTemp, tableName);
			// dfTemp.cache();
			// hiveContext.registerDataFrameAsTable(dfTemp, tableName);
		}
		return true;
	}

	/**
	 * Get all table names
	 * 
	 * @param clientContext
	 * @return
	 */
	public String[] getTableNames(String clientContext) {
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = null;
		try {
			conHolder = connector.getConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession) {
			return ((SparkSession) obj).sqlContext().tableNames();
		} /*
			 * else if (obj instanceof LivyClient) { return
			 * livyClientImpl.getTableNames(clientContext); }
			 */
		return null;
	}

	/**
	 * Read a file
	 * 
	 * @param clientContext
	 * @param datapod
	 * @param datastore
	 * @param hdfsInfo
	 * @param conObject
	 * @param datasource
	 * @return table name of registered dataframe
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws Exception
	 */
	@Override
	public String readFile(String clientContext, Datapod datapod, DataStore datastore, String tableName,
			HDFSInfo hdfsInfo, Object conObject, Datasource datasource) throws InterruptedException, ExecutionException, Exception {
		if(tableName == null)
			tableName = datapod.getName();
		
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getStmtObject();
		IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);
		String filePath = datastore.getLocation();
		String hdfsLocation = String.format("%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath());
		if (!filePath.contains(hdfsLocation)) {
			filePath = String.format("%s%s", hdfsLocation, filePath);
		}
		if (obj instanceof SparkSession) {
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			DataFrameHolder dfHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
			sparkSession.sqlContext().registerDataFrameAsTable(dfHolder.getDataframe(), tableName);
			return tableName;
		} /*
			 * else if (obj instanceof LivyClient) { LivyClient client = (LivyClient)
			 * conHolder.getStmtObject(); // Need to think of persist return
			 * livyClientImpl.readFile(clientContext, filePath); }
			 */
		return null;
	}

	@Override
	public ResultSetHolder executeAndPersist(String sql, String filePath, Datapod datapod, String saveMode,
			String clientContext) throws IOException {
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getStmtObject();
		Dataset<Row> df = null;
		IWriter datapodWriter = null;
		// HiveContext hiveContext = null;
		ResultSetHolder rsHolder = executeSql(sql);
		if (obj instanceof SparkSession) {
			df = rsHolder.getDataFrame();
			try {
				datapodWriter = dataSourceFactory.getDatapodWriter(datapod, commonActivity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException("Can not write data.");
			}
			datapodWriter.write(df, filePathUrl, datapod, saveMode);
		}
		return rsHolder;
	}

	@Override
	public ResultSetHolder registerAndPersist(ResultSetHolder rsHolder, String tableName, String filePath, Datapod datapod, String saveMode,
			String clientContext) throws IOException {
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getStmtObject();
		Dataset<Row> df = null;
		IWriter datapodWriter = null;
		if (obj instanceof SparkSession) {
			df = rsHolder.getDataFrame();
			registerTempTable(df, tableName);
			logger.info("temp table registered: " + tableName);
			try {
				datapodWriter = dataSourceFactory.getDatapodWriter(datapod, commonActivity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException("Can not write data.");
			}
			datapodWriter.write(df, filePathUrl, datapod, saveMode);
		}
		return rsHolder;
	}
	
	@Override
	public ResultSetHolder executeRegisterAndPersist(String sql, String tableName, String filePath, Datapod datapod,
			String saveMode, String clientContext) throws IOException {
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getStmtObject();
		Dataset<Row> df = null;
		IWriter datapodWriter = null;
		// HiveContext hiveContext = null;
		ResultSetHolder rsHolder = null;
		if (obj instanceof SparkSession) {
			// hiveContext = (HiveContext) conHolder.getStmtObject();
			rsHolder = executeAndRegister(sql, tableName, clientContext);
			df = rsHolder.getDataFrame();
			df.show(false);
			try {
				datapodWriter = dataSourceFactory.getDatapodWriter(datapod, commonActivity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException("Can not write data.");
			}
			datapodWriter.write(df, filePathUrl, datapod, saveMode);
		}
		return rsHolder;
	}

	@Override
	public ResultSetHolder registerDataFrameAsTable(ResultSetHolder rsHolder, String tableName) {
		try {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
			Object obj = conHolder.getStmtObject();
			SparkSession sparkSession = null;
			if (obj instanceof SparkSession) {
				sparkSession = (SparkSession) conHolder.getStmtObject();
				// sparkSession.registerDataFrameAsTable(rsHolder.getDataFrame(), tableName);
				sparkSession.sqlContext().registerDataFrameAsTable(rsHolder.getDataFrame(), tableName);
				rsHolder.setCountRows(rsHolder.getDataFrame().count());
				rsHolder.setTableName(tableName);
			}
		} catch (NullPointerException e) {
			throw new RuntimeException("Failed to register Dataframe as Table.");
		} catch (Exception e) {
			throw new RuntimeException("Failed to register Dataframe as Table.");
		}
		return rsHolder;
	}

	/*
	 * @Override public ResultSetHolder persistFile(Datapod datapod, String
	 * filePath, DataFrame df ) { System.out.println("********Spark"); String
	 * saveMode = "append";
	 * 
	 * String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(),
	 * hdfsInfo.getSchemaPath(), filePath); IConnector connector =
	 * connectionFactory.getConnector("spark"); DataFrame df1 = null; IWriter
	 * datapodWriter = null;
	 * 
	 * HiveContext hiveContext = null; IWriter datapodWriter1 =
	 * dataSourceFactory.getDatapodWriter(datapod, commonActivity);
	 * datapodWriter1.write(df, filePathUrl, datapod, saveMode);
	 * 
	 * return null;
	 * 
	 * String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(),
	 * hdfsInfo.getSchemaPath()+"/", filePath); IConnector connector =
	 * connectionFactory.getConnector(ExecContext.spark.toString());
	 * ConnectionHolder conHolder; try { conHolder = connector.getConnection();
	 * Object obj = conHolder.getStmtObject(); //DataFrame df = null; IWriter
	 * datapodWriter = null; //HiveContext hiveContext = null; //ResultSetHolder
	 * rsHolder = null; if (obj instanceof HiveContext) {
	 * 
	 * datapodWriter = dataSourceFactory.getDatapodWriter(datapod, commonActivity);
	 * datapodWriter.write(df, filePathUrl, datapod, saveMode); } } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * return null;
	 * 
	 * }
	 */

	@Override
	public void registerDatapod(String filePath, String tableName, String clientContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean executeScript(String filePath, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	/********************** UNUSED **********************/
	@Override
	public Object fetchAndTrainModel(Train train, Model model, String[] fieldArray, Algorithm algorithm,
			String trainName, String filePath, ParamMap paramMap, String clientContext) throws Exception {
//		Datasource datasource = commonServiceImpl.getDatasourceByApp();
//		Dataset<Row> df = null;
//		Object result = null;
//
//		// Fetch data/dataframe/dataset
//		if (train.getSource().getRef().getType().toString().equals(MetaType.datapod.toString())) {
//			Datapod datapod = new Datapod();
//			if (train.getSource().getRef().getVersion() != null) {
//				// datapod =
//				// datapodServiceImpl.findOneByUuidAndVersion(datapodUUID,datapodVersion);
//				datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(train.getSource().getRef().getUuid(),
//						train.getSource().getRef().getVersion(), MetaType.datapod.toString());
//			} else {
//				// datapod = datapodServiceImpl.findLatestByUuid(datapodUUID);
//				datapod = (Datapod) commonServiceImpl.getLatestByUuid(train.getSource().getRef().getUuid(),
//						MetaType.datapod.toString());
//			}
//			DataStore datastore = dataStoreServiceImpl.findLatestByMeta(datapod.getUuid(), datapod.getVersion());
//			if (datastore == null) {
//				logger.error("Datastore is not available for this datapod");
//				throw new Exception();
//			}
//			IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);
//			IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
//			ConnectionHolder conHolder = conn.getConnection();
//			Object obj = conHolder.getStmtObject();
//			DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
//			df = dataFrameHolder.getDataframe();
//		} else if (train.getSource().getRef().getType().toString().equals(MetaType.dataset.toString())) {
//			DataSet dataset = new DataSet();
//			if (train.getSource().getRef().getVersion() != null) {
//				// dataset = datasetServiceImpl.findOneByUuidAndVersion(uuid,version);
//				dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(train.getSource().getRef().getUuid(),
//						train.getSource().getRef().getVersion(), MetaType.dataset.toString());
//			} else {
//				// dataset = datasetServiceImpl.findLatestByUuid(uuid);
//				dataset = (DataSet) commonServiceImpl.getLatestByUuid(train.getSource().getRef().getUuid(),
//						MetaType.dataset.toString());
//			}
//
//			// List<Map<String, Object>> data = new ArrayList<>();
//			String sql = datasetOperator.generateSql(dataset, null, null, new HashSet<>(), null, Mode.BATCH);
//			ResultSetHolder rsHolder = executeSql(sql);
//			df = rsHolder.getDataFrame();
//
//		} else if (train.getSource().getRef().getType().toString().equals(MetaType.rule.toString())) {
//			Rule rule = new Rule();
//			if (train.getSource().getRef().getVersion() != null) {
//				rule = (Rule) commonServiceImpl.getOneByUuidAndVersion(train.getSource().getRef().getUuid(),
//						train.getSource().getRef().getVersion(), MetaType.rule.toString());
//			} else {
//				rule = (Rule) commonServiceImpl.getLatestByUuid(train.getSource().getRef().getUuid(),
//						MetaType.rule.toString());
//			}
//
//			String sql = ruleOperator.generateSql(rule, null, null, new HashSet<>(), null, Mode.BATCH);
//			ResultSetHolder rsHolder = executeSql(sql);
//			df = rsHolder.getDataFrame();
//		}
//
//		// train model
//		VectorAssembler va = new VectorAssembler();
//		Dataset<Row> training = null;
//
//		if (algorithm.getTrainName().contains("LinearRegression")
//				|| algorithm.getTrainName().contains("LogisticRegression")) {
//			va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
//			Dataset<Row> trainingTmp = va.transform(df);
//			// training = trainingTmp.withColumnRenamed(fieldArray[0],"label");
//			training = trainingTmp.withColumn("label", trainingTmp.col(model.getLabel()).cast("Double")).select("label",
//					"features");
//
//			logger.info("DataFrame count for training: " + training.count());
//
//		} /*
//			 * else if (algorithm.getTrainName().contains("DecisionTreeClassifier") ||
//			 * algorithm.getTrainName().contains("NaiveBayes") ||
//			 * algorithm.getTrainName().contains("RandomForestClassifier") ||
//			 * algorithm.getTrainName().contains("LinearSVC")) { va = (new
//			 * VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
//			 * Dataset<Row> trainingTmp = va.transform(df); training =
//			 * trainingTmp.withColumnRenamed(fieldArray[0], "label"); }
//			 */else {
//			va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
//			training = va.transform(df);
//		}
//		training.printSchema();
//		logger.info("tableName--Algo:" + trainName);
//		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), Helper.getPropertyValue("framework.model.train.path"), filePath);
//		result = trainAndValidateOperator.execute(train, model, algorithm, algorithm.getTrainName(), algorithm.getModelName(), df, va, paramMap, filePathUrl, filePath);
//		return result;
		return null;
	}

	@Override
	public String fetchAndCreatePMML(DataStore datastore, Datapod datapod, String clientContext) throws Exception {
		Dataset<Row> df = null;
		IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
		ConnectionHolder conHolder = conn.getConnection();

		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession) {
			DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
			df = dataFrameHolder.getDataframe();
		}
		RFormula formula = new RFormula().setFormula("Species ~ .");
		DecisionTreeClassifier classifier = new DecisionTreeClassifier().setLabelCol(formula.getLabelCol())
				.setFeaturesCol(formula.getFeaturesCol());
		Pipeline pipeline = new Pipeline().setStages(new PipelineStage[] { formula, classifier });
		PipelineModel pipelineModel = pipeline.fit(df);

		PMML pmml = ConverterUtil.toPMML(df.schema(), pipelineModel);
		JAXBUtil.marshalPMML(pmml, new StreamResult(System.out));
		return null;
	}

	@Override
	public List<String> fetchModelResults(DataStore datastore, Datapod datapod, int rowLimit, String clientContext) throws Exception {
		List<String> strList = new ArrayList<>();
		Dataset<Row> df = null;
		if (datastore == null) {
			logger.error("Datastore is not available for this datapod");
			throw new Exception();
		}
		IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
		ConnectionHolder conHolder = conn.getConnection();

		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession) {
			DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
			df = dataFrameHolder.getDataframe();
		}
		
		df.show(false);
		Row [] rows = (Row[]) df.head(rowLimit);
		for (Row row : rows) {
			strList.add(row.toString());
		}
		return strList;
	}

	@Override
	public long loadAndRegister(Load load, String filePath, String dagExecVer, String loadExecVer,
			String datapodTableName, Datapod datapod, String clientContext) throws Exception {
		Dataset<Row> dfTmp = sparkSession.read().format("com.databricks.spark.csv")
												.option("dateFormat", "dd-MM-yyyy")
												.option("inferSchema", "false")
												.option("header", "true").load(load.getSource().getValue());
										long count = dfTmp.count();
		// sparkSession.registerDataFrameAsTable(dfTmp, "dfLoadTemp");
		sparkSession.sqlContext().registerDataFrameAsTable(dfTmp, "dfLoadTemp");
		ResultSetHolder rsHolder = executeSql(
				"SELECT *, " + ((dagExecVer == null) ? loadExecVer : dagExecVer) + " AS version FROM dfLoadTemp",
				clientContext);
		Dataset<Row> dfTask = rsHolder.getDataFrame();

		// dfTask = hiveContext.sql("select *, "+ dagExecVer + " as version from
		// dfLoadTemp").coalesce(4);
		dfTask.cache();
		// sparkSession.registerDataFrameAsTable(dfTask, datapodTableName);
		sparkSession.sqlContext().registerDataFrameAsTable(dfTask, datapodTableName);

		logger.info("Going to datapodWriter");
		
		// Datapod datapod = (Datapod) daoRegister.getRefObject(new
		// MetaIdentifier(MetaType.datapod, datapodKey.getUUID(),
		// datapodKey.getVersion()));
		IWriter datapodWriter = datasourceFactory.getDatapodWriter(datapod, daoRegister);
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
		datapodWriter.write(dfTask, filePathUrl, datapod, SaveMode.Overwrite.toString());
		return count;
	}

	@Override
	public void registerDatapod(String tableName, Datapod datapod, DataStore dataStore, ExecContext execContext,
			String clientContext) throws IOException {
		Datasource datasource = null;
		try {
			datasource = commonServiceImpl.getDatasourceByApp();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException e) {
			e.printStackTrace();
		}
		IConnector connection = connFactory.getConnector(datasource.getType().toLowerCase());
		IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);

		ConnectionHolder conHolder = connection.getConnection();
		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession && !execContext.equals(ExecContext.livy_spark)) {
			DataFrameHolder dataFrameHolder = iReader.read(datapod, dataStore, hdfsInfo, obj, datasource);
			Dataset<Row> df = dataFrameHolder.getDataframe();
			
			tableName = dataFrameHolder.getTableName();
			String[] tablenameList = ((SparkSession) obj).sqlContext().tableNames();
			boolean tableFound = false;
			if (tablenameList != null && tablenameList.length > 0) {
				for (String tname : tablenameList) {
					if (tname.equals(tableName)) {
						tableFound = true;
						break;
					}
				}
			}
			if (!tableFound) {
				df.persist(StorageLevel.MEMORY_AND_DISK());
				// df.cache();
				SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
				// sparkSession.registerDataFrameAsTable(df, tableName);
				df.createOrReplaceGlobalTempView(tableName);
				sparkSession.sqlContext().registerDataFrameAsTable(df, tableName);
				// hiveContext.registerDataFrameAsTable(df, tableName);
				logger.info("datapodRegister: Registering datapod " + tableName);
				// hiveContext.registerDataFrameAsTable(df, tableName);
				
				df.show(true);
			}
		}
	}

	@Override
	public List<Attribute> fetchAttributeList(String csvFileName, String parquetDir, boolean flag,
			boolean writeToParquet, String clientContext) throws Exception {
		Dataset<Row> df = sparkSession.read().format("com.databricks.spark.csv").option("inferSchema", "true")
				.option("header", "true").load(csvFileName);
		df.printSchema();
		StructType st = df.schema();
		Seq<StructField> seqFields = st.thisCollection();
		Iterator<StructField> iter = st.iterator();
		List<Attribute> attributes = new ArrayList<Attribute>();
		int i = 0;
		while (iter.hasNext()) {
			StructField sf = iter.next();
			Attribute attr1 = new Attribute();
			attr1.setAttributeId(i++);
			attr1.setType(sf.dataType().typeName());
			attr1.setName(sf.name());
			attr1.setDesc(sf.name());
			attr1.setDispName(sf.name());
			attr1.setActive("Y");
			attributes.add(attr1);

		}
		if (flag) {
			Attribute attr2 = new Attribute();
			attr2.setAttributeId(i++);
			attr2.setType("Integer");
			attr2.setName("version");
			attr2.setDesc("version");
			attr2.setDispName("version");
			attr2.setActive("Y");
			attributes.add(attr2);
		}
		if (writeToParquet) {
			df.write().parquet(parquetDir);

		}
		logger.info("Length of seq:" + seqFields.length());
		return attributes;
	}

	@Override
	public List<Object> submitQuery(String sql, int rowLimit, String format, String header, String clientContext)
			throws IOException {
		List<Object> data = new ArrayList<>();
		String finalCsv = null;
		String columnHeaderStr = null;
		StringBuilder columnName = new StringBuilder();

		// Execute SQL
		logger.info("inside SparkExecutor for the quiery: " + sql);
		Dataset<Row> df = sparkSession.sql(sql).coalesce(10);
		df.show(Integer.parseInt(""+df.count()));
		df.persist(StorageLevel.MEMORY_AND_DISK());
		// df.cache();

		Row[] rows = (Row[]) df.limit(rowLimit).collect();
		
		if (format == null) {
			format = "";
		}

		// Create header row
		String[] columns = df.columns();
		for (String columnHeader : columns) {
			columnName.append(columnHeader);
			columnName.append(",");
		}
		if (columnName.charAt(columnName.length() - 1) == ',') {
			columnHeaderStr = columnName.substring(0, columnName.length() - 1);
		}
		if (format != null & format.equalsIgnoreCase("CSV")) {
			if (header.equalsIgnoreCase("Y")) {
				data.add(columnHeaderStr);
			}
		}

		// Generate output format
		for (Row row : rows) {
			java.util.TreeMap<String, Object> object = new TreeMap<String, Object>();
			if (format.equals("") || !format.equalsIgnoreCase("csv")) {
				for (String column : columns) {
					object.put(column, row.getAs(column));
				}
				data.add(object);
			}
			if (format != null & format.equalsIgnoreCase("CSV")) {
				StringBuilder sbr = new StringBuilder();
				for (String column : columns) {
					Object csvRow = row.getAs(column);
					sbr.append(csvRow);
					sbr.append(",");
				}

				if (sbr.charAt(sbr.length() - 1) == ',') {
					finalCsv = sbr.substring(0, sbr.length() - 1);
				}
				data.add(finalCsv);
			}
		}
		return data;
	}

	/********************** UNUSED **********************/
	@Override
	public Object predictModel(Predict predict, String[] fieldArray, Algorithm algorithm, String filePath,
			String tableName, String clientContext) throws Exception {

		try {
			// fieldArray = modelExecServiceImpl.getAttributeNames(predict);
/*
			MetaIdentifierHolder modelHolder = predict.getDependsOn();
			MetaIdentifierHolder sourceHolder = predict.getSource();
			MetaIdentifierHolder targetHolder = predict.getTarget();

			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelHolder.getRef().getUuid(),
					modelHolder.getRef().getVersion(), modelHolder.getRef().getType().toString());
			Object source = (Object) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
					sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
			Datapod target = null;
			if (targetHolder.getRef().getType() != null && targetHolder.getRef().getType().equals(MetaType.datapod))
				target = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(),
						targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());

			Dataset<Row> df = null;
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			if (source instanceof Datapod) {
				Datapod datapod = (Datapod) source;
				DataStore datastore = dataStoreServiceImpl.findLatestByMeta(datapod.getUuid(), datapod.getVersion());
				if (datastore == null) {
					logger.error("Datastore is not available for this datapod");
					throw new Exception();
				}
				IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);
				IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
				ConnectionHolder conHolder = conn.getConnection();
				Object obj = conHolder.getStmtObject();
				DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
				df = dataFrameHolder.getDataframe();
			} else if (source instanceof Dataset) {
				DataSet dataset = (DataSet) source;
				if (predict.getSource().getRef().getVersion() != null) {
					dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(predict.getSource().getRef().getUuid(),
							predict.getSource().getRef().getVersion(), MetaType.dataset.toString());
				} else {
					dataset = (DataSet) commonServiceImpl.getLatestByUuid(predict.getSource().getRef().getUuid(),
							MetaType.dataset.toString());
				}

				String sql = datasetOperator.generateSql(dataset, null, null, new HashSet<>(), null, Mode.BATCH);
				ResultSetHolder rsHolder = executeSql(sql);
				df = rsHolder.getDataFrame();

			} else if (source instanceof Rule) {
				Rule rule = (Rule) source;
				if (predict.getSource().getRef().getVersion() != null) {
					rule = (Rule) commonServiceImpl.getOneByUuidAndVersion(predict.getSource().getRef().getUuid(),
							predict.getSource().getRef().getVersion(), MetaType.rule.toString());
				} else {
					rule = (Rule) commonServiceImpl.getLatestByUuid(predict.getSource().getRef().getUuid(),
							MetaType.rule.toString());
				}

				String sql = ruleOperator.generateSql(rule, null, null, new HashSet<>(), null, Mode.BATCH);
				ResultSetHolder rsHolder = executeSql(sql);
				df = rsHolder.getDataFrame();
			}

			String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), Helper.getPropertyValue("framework.model.predict.path"), filePath);

			if(model.getDependsOn().getRef().getType().equals(MetaType.formula)) {
				sparkSession.sqlContext().registerDataFrameAsTable(df, tableName.replaceAll("-", "_"));
				String sql = predictMLOperator.generateSql(predict, tableName);				
				return predictMLOperator.execute(sql, tableName, filePathUrl, filePath, commonServiceImpl.getApp().getUuid());
			} else if(model.getDependsOn().getRef().getType().equals(MetaType.algorithm)) {
				VectorAssembler va = new VectorAssembler();
				Dataset<Row> transformedDf = null;

				if (algorithm.getTrainName().contains("LinearRegression")
						|| algorithm.getTrainName().contains("LogisticRegression")) {
					va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
					Dataset<Row> trainingTmp = va.transform(df);
					transformedDf = trainingTmp.withColumn("label", trainingTmp.col(model.getLabel()).cast("Double"))
							.select("label", "features");

					logger.info("DataFrame count for training: " + transformedDf.count());

				} else {
					va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
					transformedDf = va.transform(df);
				}

				TrainExec latestTrainExec = modelExecServiceImpl.getLatestTrainExecByModel(model.getUuid(),
						model.getVersion());
				if (latestTrainExec == null)
					throw new Exception("Executed model not found.");

				transformedDf.printSchema();
				return predictMLOperator.execute(predict, model, algorithm, target, transformedDf, fieldArray, latestTrainExec, targetHolder.getRef().getType().toString(), tableName, filePathUrl, filePath, commonServiceImpl.getApp().getUuid());
			} else */
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	/********************** UNUSED **********************/
	@Override
	public Object simulateModel(Simulate simulate, ExecParams execParams, String[] fieldArray, Algorithm algorithm,
			String filePath, String tableName, String clientContext) throws Exception {
		try {
			/*MetaIdentifierHolder modelHolder = simulate.getDependsOn();
			MetaIdentifierHolder targetHolder = simulate.getTarget();
			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelHolder.getRef().getUuid(),
					modelHolder.getRef().getVersion(), modelHolder.getRef().getType().toString());
			Datapod target = null;
			if (targetHolder.getRef().getType() != null && targetHolder.getRef().getType().equals(MetaType.datapod))
				target = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(),
						targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());

			// fieldArray = modelExecServiceImpl.getAttributeNames(model);

			String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), Helper.getPropertyValue("framework.model.simulate.path"), filePath);
						
			if(model.getDependsOn().getRef().getType().equals(MetaType.formula)) {
				Distribution distribution = (Distribution) daoRegister.getRefObject(simulate.getDistributionTypeInfo().getRef());
				
				int seed = Integer.parseInt(""+execParams.getParamListInfo().get(0).getParamValue().getValue());
				int numTrials = simulate.getNumIterations();
				Dataset<Row> dfTemp = executeDistribution(distribution, numTrials, seed, execParams.getParamListInfo().get(1).getParamValue(), execParams.getParamListInfo().get(2).getParamValue());
				
				Dataset<Row> df = dfTemp.withColumnRenamed("value", fieldArray[0]);
				
				VectorAssembler va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
				Dataset<Row> assembledDf = va.transform(df);
				assembledDf.show(false);
				sparkSession.sqlContext().registerDataFrameAsTable(assembledDf, tableName);
				String sql = simulateMLOperator.parse(simulate, model, assembledDf, fieldArray, tableName, filePathUrl, filePath);
				logger.info("Parsed sql : " + sql);
				return simulateMLOperator.execute(sql, filePathUrl, filePath, commonServiceImpl.getApp().getUuid());
			} else if(model.getDependsOn().getRef().getType().equals(MetaType.algorithm)) {
				
				TrainExec latestTrainExec = modelExecServiceImpl.getLatestTrainExecByModel(model.getUuid(),
					model.getVersion());
				if (latestTrainExec == null)
					throw new Exception("Executed model not found.");
				
				Dataset<Row> df = simulateMLOperator.generateDataframe(simulate, model, tableName);
				VectorAssembler va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
				Dataset<Row> assembledDf = va.transform(df);
				assembledDf.show(false);
				
				return simulateMLOperator.execute(simulate, model, algorithm, target, latestTrainExec, fieldArray,
						targetHolder.getRef().getType().toString(), tableName, filePathUrl, filePath, assembledDf, clientContext);
			} else */
				return null;			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public List<Map<String, Object>> fetchResults(DataStore datastore, Datapod datapod, int rowLimit, String clientContext)
			throws Exception {
		List<Map<String, Object>> data = new ArrayList<>();
		Dataset<Row> df = null;
		if (datastore == null) {
			logger.error("Datastore is not available for this datapod");
			throw new Exception();
		}
		IReader iReader = dataSourceFactory.getDatapodReader();
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
		ConnectionHolder conHolder = conn.getConnection();

		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession) {
			DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
			df = dataFrameHolder.getDataframe();
		}
		
		df.show(false);
		String[] columns = df.columns();
		Row [] rows = (Row[]) df.head(rowLimit);
		for (Row row : rows) {
			Map<String, Object> object = new LinkedHashMap<String, Object>(columns.length);
			for (String column : columns) {
//				object.put(column, (row.getAs(column)==null ? "":
//					(row.getAs(column) instanceof WrappedArray<?>) ? Arrays.toString((Double[])((WrappedArray<?>)row.getAs(column)).array()) : row.getAs(column).toString()) );
				object.put(column, (row.getAs(column) == null ? "" :
					(row.getAs(column) instanceof Vector) ? Arrays.toString((double[])((Vector)row.getAs(column)).toArray()) : row.getAs(column)));
			}
			data.add(object);
		}

		return data;
	}


	/********************** UNUSED **********************/
	/*public Dataset<Row> executeDistribution(Distribution distribution, int numTrials, long seed, MetaIdentifierHolder factorMeansInfo, MetaIdentifierHolder factorCovariancesInfo) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, NullPointerException, ParseException {
		return simulateMLOperator.executeDistribution(distribution, numTrials, seed, factorMeansInfo, factorCovariancesInfo);
	}*/

	@Override
	public ResultSetHolder generateData(Object distributionObject, List<Attribute> attributes, int numIterations, String execVersion, String tableName) throws Exception {
		StructField[] fieldArray = new StructField[attributes.size()];
		int count = 0;
		
		Class<?> returnType = distributionObject.getClass().getMethod("sample").getReturnType();
		if(returnType.isArray()) {
			double[] trialSample = (double[]) distributionObject.getClass().getMethod("sample").invoke(distributionObject);
			int expectedNumcols = trialSample.length + 2;
			if(attributes.size() != expectedNumcols)
				throw new RuntimeException("Insufficient number of columns.");
		} else if(returnType.isPrimitive()) {
			int expectedNumcols = 3;
			if(attributes.size() != expectedNumcols)
				throw new RuntimeException("Insufficient number of columns.");
		}

//		StructField idField = new StructField("id", DataTypes.IntegerType, true, Metadata.empty());
//		fieldArray[count] = idField;
//		count++;
		for(Attribute attribute : attributes){						
			StructField field = new StructField(attribute.getName(), (DataType)getDataType(attribute.getType()), true, Metadata.empty());
//			StructField field = new StructField(attribute.getName(), DataTypes.DoubleType, true, Metadata.empty());
			fieldArray[count] = field;
			count ++;
		}
		StructType schema = new StructType(fieldArray);		
		
		List<Row> rowList = new ArrayList<>();
		for(int i=0; i<numIterations; i++) {
			int genId = i+1;
			Object obj = null;

			try {
				obj = distributionObject.getClass().getMethod("sample").invoke(distributionObject);
				//Class<?> returnType = object.getClass().getMethod("sample").getReturnType();
				if(returnType.isArray()) {
					double[] trial = (double[]) obj;
					List<Object> datasetList = new ArrayList<>();
					datasetList.add(genId);
					for(double val : trial) {
						datasetList.add(val);
					}
					datasetList.add(Integer.parseInt(execVersion));
					rowList.add(RowFactory.create(datasetList.toArray()));
					genId++;
				} else if(returnType.isPrimitive()) {
					if(!returnType.getName().equalsIgnoreCase("double")) {
						List<Object> datasetList = new ArrayList<>();
						datasetList.add(genId);
						datasetList.add(Double.parseDouble(""+obj));
						datasetList.add(Integer.parseInt(execVersion));
						rowList.add(RowFactory.create(datasetList.toArray()));
						genId++;
					} else {
						List<Object> datasetList = new ArrayList<>();
						datasetList.add(genId);
						datasetList.add((Double) obj);
						datasetList.add(Integer.parseInt(execVersion));
						rowList.add(RowFactory.create(datasetList.toArray()));
						genId++;
					}
				}
				
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		Dataset<Row> df = sparkSession.sqlContext().createDataFrame(rowList, schema);
		df.printSchema();
		df.show(true);
		registerTempTable(df, tableName);
		ResultSetHolder rsHolder = new ResultSetHolder();
		rsHolder.setDataFrame(df);
		rsHolder.setType(ResultType.dataframe);	
		rsHolder.setCountRows(df.count());
		rsHolder.setTableName(tableName);
		return rsHolder;
	}

	@Override
	public String generateFeatureData(Object object, List<Feature> features, int numIterations, String tableName) {
		StructField[] fieldArray = new StructField[features.size()+1];
		int count = 0;
		
		try {
			double[] trialTemp = (double[]) object.getClass().getMethod("sample").invoke(object);
			if(features.size() != trialTemp.length)
				throw new RuntimeException("Insufficient number of columns.");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StructField idField = new StructField("id", DataTypes.IntegerType, true, Metadata.empty());
		fieldArray[count] = idField;
		count++;
		for(Feature feature : features){
			StructField field = new StructField(feature.getName(), /*DataTypes.createArrayType(*/DataTypes.DoubleType/*)*/, true, Metadata.empty());
			
			fieldArray[count] = field;
			count ++;
		}
		StructType schema = new StructType(fieldArray);
		
		List<Row> rowList = new ArrayList<>();
		for(int i=0; i<numIterations; i++) {
			//List<Double> colList = new ArrayList<>();
			int genId = i;
			for(int j=0; j<features.size(); j++) {	
				try {
					//Double totalVal = 0.0;
					//double[] trial = (double[]) object.getClass().getMethod("sample").invoke(object);
					Object obj = object.getClass().getMethod("sample").invoke(object);
					//Class<?> returnType = object.getClass().getMethod("sample").getReturnType();
					/*if(returnType.isArray()) {*/
						double[] trial = (double[]) obj;
						List<Object> datasetList = new ArrayList<>();
						datasetList.add(genId);
						for(double val : trial)
							datasetList.add(val);
						//rowList.add(RowFactory.create(genId, trial[0], trial[1], trial[2]));
						rowList.add(RowFactory.create(datasetList.toArray()));
						genId++;
					/*for(double val : trial)
						totalVal +=val;
					} else if(returnType.isPrimitive()) {
						if(!returnType.getName().equalsIgnoreCase("double"))
								totalVal = Double.parseDouble(""+obj);
						else
							totalVal = (Double) obj;
					}
					colList.add(totalVal);*/
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		
		Dataset<Row> df = sparkSession.sqlContext().createDataFrame(rowList, schema);
		
		df.show(false);
		sparkSession.sqlContext().registerDataFrameAsTable(df, tableName);
		return tableName;
	}
	
	@Override
	public String generateFeatureData(List<Feature> features, int numIterations, String[] fieldArray, String tableName) {
		Dataset<Row> df = null;
		df = sparkSession.sqlContext().range(0, numIterations);
		df.createOrReplaceTempView(tableName);
		
		StringBuilder sb = new StringBuilder();
		for (Feature feature : features) {
			sb.append("(" + feature.getMinVal() + " + rand()*(" + feature.getMaxVal() + "-" + feature.getMinVal()
					+ ")) AS " + feature.getName() + ", ");
		}
		
		String query = "SELECT id, " + sb.toString().substring(0, sb.toString().length() - 2) + " FROM " + tableName;
		logger.info("query : "+query);
		
		sparkSession.sqlContext().registerDataFrameAsTable(df, tableName);
		df = sparkSession.sqlContext().sql(query);
		df.show(false);
		return tableName;
	}
	
	@Override
	public String assembleRandomDF(String[] fieldArray, String tableName, boolean isDistribution, String clientContext) throws IOException{
		String sql = "SELECT * FROM " + tableName;
		Dataset<Row> df = executeSql(sql, clientContext).getDataFrame();
		
		if(isDistribution)
			df = df.withColumnRenamed(df.columns()[1], fieldArray[0]);
		else
			fieldArray = df.columns();

		df.printSchema();
		df.show(true);
		
		VectorAssembler va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
		Dataset<Row> assembledDf = va.transform(df);
		assembledDf.printSchema();
		assembledDf.show(false);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		tableName = tableName + "_" + Helper.getVersion();
		sparkSession.sqlContext().registerDataFrameAsTable(assembledDf, tableName);
		return tableName;
	}
	
	@Override
	public Object assembleDF(String[] fieldArray, String tableName, String trainName, String label, String clientContext) throws IOException{
		String sql = "SELECT * FROM " + tableName;
		Dataset<Row> df = executeSql(sql, clientContext).getDataFrame();
		
		VectorAssembler va = new VectorAssembler();
		Dataset<Row> transformedDf = null;

		if (trainName.contains("LinearRegression")
				|| trainName.contains("LogisticRegression")) {
			va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
			Dataset<Row> trainingTmp = va.transform(df);
			transformedDf = trainingTmp.withColumn("label", trainingTmp.col(label).cast("Double"))
					.select("label", "features");

			logger.info("DataFrame count: " + transformedDf.count());

		} else {
			va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
			transformedDf = va.transform(df);
		}
		
		transformedDf.show(false);
		sparkSession.sqlContext().registerDataFrameAsTable(transformedDf, tableName);
		return va;
	}

	/********************** UNUSED **********************/
	/**
	 * 
	 * @param factorCovarianceDp
	 * @param factorCovarianceDs
	 * @param hdfsInfo
	 * @param datasource
	 * @return
	 * @throws IOException
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 *//*
	@Override
	public double[][] twoDArrayFromDatapod (Datapod factorCovarianceDp, DataStore factorCovarianceDs, HDFSInfo hdfsInfo) 
						throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IReader datapodReader = dataSourceFactory.getDatapodReader(factorCovarianceDp, commonActivity);
		DataFrameHolder covsHolder = datapodReader.read(factorCovarianceDp, factorCovarianceDs, hdfsInfo, sparkSession, datasource);
		
		Dataset<Row> covarsDf = covsHolder.getDataframe();
		covarsDf.show(false);
		
		List<String> covarColList = new ArrayList<>();
		for(int i=0; i<factorCovarianceDp.getAttributes().size(); i++) {
			covarColList.add(factorCovarianceDp.getAttributes().get(i).getName());
		}	
		
		List<double[]> covarsRowList = new ArrayList<>();
		for(Row row : covarsDf.collectAsList()) {
			List<Double> covarsValList = new ArrayList<>();
				for(String col : covarColList)
					covarsValList.add(row.getAs(col));
				covarsRowList.add(ArrayUtils.toPrimitive(covarsValList.toArray(new Double[covarsValList.size()])));
		}
		
		double[][] factorCovariances = covarsRowList.stream().map(lineStrArray -> ArrayUtils.toPrimitive(lineStrArray)).toArray(double[][]::new);
		return factorCovariances;
	}*/
	

	/********************** UNUSED **********************/
	/**
	 * 
	 * @param factorMeanDp
	 * @param factorMeanDs
	 * @param hdfsInfo
	 * @return
	 *//*
	@Override
	public double[] oneDArrayFromDatapod (Datapod factorMeanDp, DataStore factorMeanDs, HDFSInfo hdfsInfo) 
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		List<String> meanColList = new ArrayList<>();
		
		IReader datapodReader = dataSourceFactory.getDatapodReader(factorMeanDp, commonActivity);
		DataFrameHolder meansHolder = datapodReader.read(factorMeanDp, factorMeanDs, hdfsInfo, sparkSession, datasource);
		Dataset<Row> meansDf = meansHolder.getDataframe();
		meansDf.show(false);
		
		for(int i=0; i<factorMeanDp.getAttributes().size(); i++) {
			meanColList.add(factorMeanDp.getAttributes().get(i).getName());
		}			
		
		List<Double> meansValList = new ArrayList<>();
		for(Row row : meansDf.collectAsList()) {
				for(String col : meanColList)
					meansValList.add(row.getAs(col));
		}		
		double[] factorMeans = ArrayUtils.toPrimitive(meansValList.toArray(new Double[meansValList.size()]));
		return factorMeans;
	
	}*/
	
	@Override
	public List<double[]> twoDArray(String sql, List<AttributeRefHolder> attributeInfo, String clientContext)
			throws InterruptedException, ExecutionException, Exception {

		Dataset<Row> df = executeSql(sql, clientContext).getDataFrame();
		df.show(false);
		
		
		List<String> columnList = new ArrayList<>();
//		for(Attribute attribute : paramDp.getAttributes())
			for(AttributeRefHolder attributeRefHolder : attributeInfo) {
//				if(attribute.getAttributeId().equals(Integer.parseInt(attributeRefHolder.getAttrId()))) {
					columnList.add(attributeRefHolder.getAttrName());
				//}
			}
		
		List<double[]> valueList = new ArrayList<>();
		for(Row row : df.collectAsList()) {
			List<Double> covarsValList = new ArrayList<>();
				for(String col : columnList)
//					covarsValList.add(Double.parseDouble(row.getAs(col)));
					covarsValList.add(row.getAs(col));
				valueList.add(ArrayUtils.toPrimitive(covarsValList.toArray(new Double[covarsValList.size()])));
		}
		
		return valueList;
	}

	@Override
	public List<Double> oneDArray(String sql, List<AttributeRefHolder> attributeInfo, String clientContext)
			throws InterruptedException, ExecutionException, Exception {
			
		Dataset<Row> df = executeSql(sql, clientContext).getDataFrame();
		
		df.show(false);

		List<String> columnList = new ArrayList<>();
		//for(Attribute attribute : paramDp.getAttributes())
			for(AttributeRefHolder attributeRefHolder : attributeInfo) {
				//if(attribute.getAttributeId().equals(Integer.parseInt(attributeRefHolder.getAttrId()))) {
					columnList.add(attributeRefHolder.getAttrName());
				//}
			}			
		
		List<Double> valueList = new ArrayList<>();
		for(Row row : df.collectAsList()) 
			for(String col : columnList)
				valueList.add(row.getAs(col));
				
		return valueList;
	}
	
	@Override
	public ResultSetHolder executePredict(Object trainedModel, Datapod targetDp, String filePathUrl, String tableName, String clientContext) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String assembledDFSQL = "SELECT * FROM " + tableName;
		Dataset<Row> df = executeSql(assembledDFSQL, clientContext).getDataFrame();
		//df.show(true);
		@SuppressWarnings("unchecked")
		Dataset<Row> predictionDf = (Dataset<Row>) trainedModel.getClass().getMethod("transform", Dataset.class)
				.invoke(trainedModel, df);
		predictionDf.show(true);

		//String uid = (String) trainedModel.getClass().getMethod("uid").invoke(trainedModel);

		//if (targetType.equalsIgnoreCase(MetaType.datapod.toString())) {
			/*Datasource datasource = commonServiceImpl.getDatasourceByApp();

			df.createOrReplaceGlobalTempView("tempPredictResult");
			IConnector connector = connectionFactory.getConnector(datasource.getType().toLowerCase());
			ConnectionHolder conHolder = connector.getConnection();
			if (conHolder.getStmtObject() instanceof SparkSession) {
				SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
				predictionDf.persist(StorageLevel.MEMORY_AND_DISK());
				//sparkSession.sqlContext().registerDataFrameAsTable(predictionDf, "tempPredictResult");
			}*/

//			String columns = "";
//			for (String col : predictionDf.columns())
//				columns = columns.concat(col).concat(" AS ").concat(col).concat(",");
//			columns = columns.substring(0, columns.length() - 2);
//			String sql = "SELECT " + columns + " FROM " + "tempPredictResult";
//			ResultSetHolder rsHolder = executeSql(sql, commonServiceImpl.getApp().getUuid());
//
//			Dataset<Row> dfTask = rsHolder.getDataFrame();
//			dfTask.show(true);
//			dfTask.cache();

			sqlContext.registerDataFrameAsTable(predictionDf, "tempPredictResult");
//			IWriter datapodWriter = datasourceFactory.getDatapodWriter(targetDp, daoRegister);
//			datapodWriter.write(predictionDf, filePathUrl + "/data", targetDp, SaveMode.Append.toString());
			ResultSetHolder rsHolder = new ResultSetHolder();
			rsHolder.setType(ResultType.dataframe);
			rsHolder.setDataFrame(predictionDf);
			rsHolder.setCountRows(predictionDf.count());
			rsHolder.setTableName("tempPredictResult");
			return rsHolder;
	}

	@Override
	public PipelineModel trainModel(ParamMap paramMap, String[] fieldArray, String label, String trainName, double trainPercent, double valPercent, String tableName, String clientContext) throws IOException {
		PipelineModel trngModel = null;
		String assembledDFSQL = "SELECT * FROM " + tableName;
		Dataset<Row> df = executeSql(assembledDFSQL, clientContext).getDataFrame();
		df.printSchema();
		try {
			Dataset<Row>[] splits = df
					.randomSplit(new double[] { trainPercent / 100, valPercent / 100 }, 12345);
			Dataset<Row> trngDf = splits[0];
			Dataset<Row> valDf = splits[1];
			Dataset<Row> trainingDf = null;
			Dataset<Row> validateDf = null;
			
			//VectorAssembler vectorAssembler = (VectorAssembler) va;
			VectorAssembler vectorAssembler = new VectorAssembler();
			vectorAssembler.setInputCols(fieldArray).setOutputCol("features");
			if (trainName.contains("LinearRegression")
					|| trainName.contains("LogisticRegression")) {
				trainingDf = trngDf.withColumn("label", trngDf.col(label).cast("Double")).select("label", vectorAssembler.getInputCols());
				//trainingDf.show(true);
				validateDf = valDf.withColumn("label", valDf.col(label).cast("Double")).select("label", vectorAssembler.getInputCols());
				//validateDf.show(true);
			} else {
				trainingDf = trngDf;
				validateDf = valDf;
			}

			Dataset<Row> trainedDataSet = null;
			@SuppressWarnings("unused")
			StringIndexer labelIndexer = null;
			@SuppressWarnings("unused")
			String labelColName = (trainName.contains("classification")) ? "indexedLabel" : "label";
			
			Class<?> dynamicClass = Class.forName(trainName);
			Object obj = dynamicClass.newInstance();
			Method method = null;
			if (trainName.contains("LinearRegression")
					|| trainName.contains("LogisticRegression")) {
				method = dynamicClass.getMethod("setLabelCol", String.class);
				method.invoke(obj, "label");
			}

			method = dynamicClass.getMethod("setFeaturesCol", String.class);
			method.invoke(obj, "features");
			Pipeline pipeline = new Pipeline()
					.setStages(new PipelineStage[] { /* labelIndexer, */vectorAssembler, (PipelineStage) obj });
			if (null != paramMap) {
				trngModel = pipeline.fit(trainingDf, paramMap);
			} else {
				trngModel = pipeline.fit(trainingDf);
			}
			
			trainedDataSet = trngModel.transform(validateDf);
			sparkSession.sqlContext().registerDataFrameAsTable(trainedDataSet, "trainedDataSet");
			trainedDataSet.show(false);
			return trngModel;
		} catch (ClassNotFoundException
				| IllegalAccessException 
				| IllegalArgumentException 
				| InstantiationException 
				| SecurityException
				| NoSuchMethodException
				| InvocationTargetException e) {
			e.printStackTrace();
		} 
		return trngModel; 
	}

	@Override
	public boolean savePMML(Object trngModel, String trainedDSName, String pmmlLocation, String clientContext) throws IOException, JAXBException {
		
		String sql = "SELECT * FROM " + trainedDSName;
		Dataset<Row> trainedDataSet = executeSql(sql, clientContext).getDataFrame();
		trainedDataSet.printSchema();
		PMML pmml = ConverterUtil.toPMML(trainedDataSet.schema(), (PipelineModel)trngModel);
		MetroJAXBUtil.marshalPMML(pmml, new FileOutputStream(new File(pmmlLocation), true));					
		return true;
	}

	@Override
	public Object getDataType(String dataType) throws NullPointerException {
		if(dataType == null)
			return null;

		if(dataType.contains("(")) {
			dataType = dataType.substring(0, dataType.indexOf("("));
		}
		
		switch (dataType.toLowerCase()) {
			case "integer": return DataTypes.IntegerType;
			case "double": return DataTypes.DoubleType;
			case "date": return DataTypes.DateType;
			case "string": return DataTypes.StringType;
			case "timestamp": return DataTypes.TimestampType;
			case "decimal" : return DataTypes.createDecimalType();
			case "vector" : return new VectorUDT();
			
            default: return null;
		}
	}

	@Override
	public String joinDf(String joinTabName_1, String joinTabName_2, int i, String clientContext) throws IOException {
		String sql_1 = "SELECT * FROM " + joinTabName_1;
		Dataset<Row> df_1 = executeSql(sql_1, clientContext).getDataFrame();
		
		String sql_2 = "SELECT * FROM " + joinTabName_2;
		Dataset<Row> df_2 = executeSql(sql_2, clientContext).getDataFrame();
//		df_2 = df_2.withColumnRenamed("features", "features_"+i);
	
		List<String> joinColumns = new ArrayList<>();
		joinColumns.add("id");
		joinColumns.add("version");
		df_1 = df_1.join(df_2,JavaConverters.asScalaBufferConverter(joinColumns).asScala());
//		df_1 = df_1.crossJoin(df_2);
		df_1.printSchema();
		df_1.show(true);
		
		registerTempTable(df_1, joinTabName_1);
		return joinTabName_1;
	}

	@Override
	public String renameColumn(String tableName, int targetColIndex, String targetColName, String clientContext) throws IOException {
		String sql = "SELECT * FROM " + tableName;
		Dataset<Row> df = executeSql(sql, clientContext).getDataFrame();
		
		df = df.withColumnRenamed(df.columns()[targetColIndex], targetColName);
		df.printSchema();
		df.show(true);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tableName = tableName + "_" + Helper.getVersion();
		sparkSession.sqlContext().registerDataFrameAsTable(df, tableName);
		return tableName;
	}
	
	@Override
	public String renameDfColumnName(String tableName, Map<String, String> mappingList, String clientContext) throws IOException {
		
		String sql = "SELECT * FROM " + tableName;
		Dataset<Row> df = executeSql(sql, clientContext).getDataFrame();
		
		/*
		 * map: key=oldColName, value=newColName
		 */
		for(Entry<String, String> entry : mappingList.entrySet()) {
			df = df.withColumnRenamed(entry.getKey(), entry.getValue());
		}
		df.show(true);
		sparkSession.sqlContext().registerDataFrameAsTable(df, tableName);
		return tableName;
	}
}

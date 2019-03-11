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
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.log4j.Logger;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.Transformer;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.classification.LogisticRegressionTrainingSummary;
import org.apache.spark.ml.clustering.KMeansSummary;
import org.apache.spark.ml.clustering.LocalLDAModel;
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator;
import org.apache.spark.ml.evaluation.Evaluator;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.feature.OneHotEncoder;
import org.apache.spark.ml.feature.RFormula;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.VectorUDT;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.ml.param.BooleanParam;
import org.apache.spark.ml.param.DoubleParam;
import org.apache.spark.ml.param.FloatParam;
import org.apache.spark.ml.param.IntParam;
import org.apache.spark.ml.param.LongParam;
import org.apache.spark.ml.param.Param;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.regression.LinearRegressionTrainingSummary;
import org.apache.spark.ml.stat.Correlation;
import org.apache.spark.ml.tuning.CrossValidator;
import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.distributed.BlockMatrix;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.expressions.Window;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.storage.StorageLevel;
import org.dmg.pmml.PMML;
import org.graphframes.GraphFrame;
import org.jpmml.model.JAXBUtil;
import org.jpmml.model.MetroJAXBUtil;
import org.jpmml.sparkml.ConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.HistogramUtil;
import com.inferyx.framework.connector.ConnectionHolder;
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
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.GraphExec;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.ResultType;
import com.inferyx.framework.domain.RowObj;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainResult;
import com.inferyx.framework.enums.Compare;
import com.inferyx.framework.enums.EncodingType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.helper.SparkExecHelper;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.MatrixToRddConverter;
import com.inferyx.framework.reader.IReader;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.ModelExecServiceImpl;
import com.inferyx.framework.service.ModelServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;
import com.inferyx.framework.writer.IWriter;

import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.JavaConverters;
import scala.collection.Seq;

@Component
public class SparkExecutor<T> implements IExecutor {

	@Autowired
	ConnectionFactory connectionFactory;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	DataSourceFactory dataSourceFactory;
	@Autowired
	HDFSInfo hdfsInfo;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	ModelExecServiceImpl modelExecServiceImpl;
	@Autowired
	private ConnectionFactory connFactory;
	@Autowired
	Engine engine;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private DataSourceFactory datasourceFactory;
	@Autowired
	private ModelServiceImpl modelServiceImpl;
	@Resource
	private ConcurrentHashMap<String, GraphFrame> graphpodMap;
	@Autowired
	private MatrixToRddConverter matrixToRddConverter;
	@Autowired
	private HistogramUtil histogramUtil;
	@Autowired
	private SparkExecHelper sparkExecHelper;
	
	static final Logger logger = Logger.getLogger(SparkExecutor.class);
	
	/**
	 * 
	 * @param distributionObject
	 * @param methodName
	 * @param args
	 * @param attributes
	 * @param numIterations
	 * @param execVersion
	 * @param tableName
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultSetHolder generateData(Distribution distribution, Object distributionObject, String methodName, Object[] args, Class<?>[] paramtypes, List<Attribute> attributes, int numIterations, String execVersion, String tableName) throws IOException, ClassNotFoundException {
		RDD<Double> obj = null;
		SparkSession sparkSession = null;
		ResultSetHolder resultSetHolder = new ResultSetHolder();
		Object[] arguments = new Object[args.length+1];
		Class<?>[] modParamTypes = new Class<?>[paramtypes.length+1];
		Dataset<Row> df = null;
		try {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
			Object object = conHolder.getStmtObject();
			if (object instanceof SparkSession) {
				sparkSession = (SparkSession) conHolder.getStmtObject();
				arguments[0] = sparkSession.sparkContext();
				modParamTypes[0] = SparkContext.class;
				int count = 1;
				for (Object arg : args) {
					arguments[count] = arg;
					modParamTypes[count]=paramtypes[count-1];
					logger.info("Argument arguments["+count+"] = "+arg);
					logger.info("Type modParamTypes["+count+"] = "+modParamTypes[count]);
					count++;
				}
				count=0;
				StructField[] fieldArray = new StructField[attributes.size()];
				for(Attribute attribute : attributes){						
					StructField field = new StructField(attribute.getName(), (DataType)getDataType(attribute.getType()), true, Metadata.empty());
					fieldArray[count] = field;
					count ++;
				}
//				StructType schema = new StructType(fieldArray);
				
//				StructField[] randFieldArray = new StructField[1];
//				randFieldArray[0] = new StructField(attributes.get(1).getName(), (DataType)getDataType(attributes.get(1).getType()), true, Metadata.empty());
//				schema = new StructType(randFieldArray);
				
				
				obj = (RDD<Double>)(Class.forName(distribution.getClassName())).getMethod(methodName, modParamTypes).invoke(null, arguments);
				df = sparkSession.createDataset(obj, Encoders.DOUBLE()).toDF(attributes.get(1).getName());
			}
//			df.show();
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			String tempTableName = tableName;
			if(tempTableName.contains(datasource.getDbname()+"."))
				tempTableName = tempTableName.replaceAll(datasource.getDbname()+".", "");
			df.createOrReplaceTempView(tempTableName+"_vw");
			df = sparkSession.sql("select row_number() over(ORDER BY 1) as "+attributes.get(0).getDispName()+", "+attributes.get(1).getDispName()+", " + execVersion + " as "+attributes.get(2).getDispName()+" from " + tempTableName+"_vw");
			resultSetHolder.setCountRows(df.count());
			resultSetHolder.setType(ResultType.dataframe);
			resultSetHolder.setDataFrame(df);
			resultSetHolder.setTableName(tableName);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return resultSetHolder;
	}
	
	/**
	 * 
	 * @param rowRDD
	 * @param schema
	 * @param tableName
	 * @throws AnalysisException
	 */
	public ResultSetHolder createAndRegisterDataset(JavaRDD<Row> rowRDD, StructType schema, String tableName) throws AnalysisException {
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = null;
		try {
			sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Dataset<Row> dataset = sparkSession.createDataFrame(rowRDD, schema);
//		dataset.show(false);
		dataset.createOrReplaceTempView(tableName);
		ResultSetHolder rsHolder = new ResultSetHolder();
		rsHolder.setCountRows(dataset.count());
		rsHolder.setDataFrame(dataset);
		rsHolder.setTableName(tableName);
		rsHolder.setType(ResultType.dataframe);
		return rsHolder;
	}
	
	@Override
	public ResultSetHolder executeSql(String sql) throws IOException {
		return executeSql(sql, null);
	}

	public ResultSetHolder executeSql(String sql, String clientContext) throws IOException {
		logger.info(" Inside spark executor  for SQL : " + sql);
		ResultSetHolder rsHolder = new ResultSetHolder();
		try {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			Dataset<Row> df = null;
			if (datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
					|| datasource.getType().toLowerCase().equalsIgnoreCase(ExecContext.FILE.toString())
					|| datasource.getType().toLowerCase().equalsIgnoreCase(ExecContext.HIVE.toString())) {
				for (String sessionParam : commonServiceImpl.getAllDSSessionParams()) {
					sparkSession.sql("SET "+sessionParam);
				}
				df = sparkSession.sql(sql);
			} else if (datasource.getType().equalsIgnoreCase(ExecContext.IMPALA.toString())) {
				df = sparkSession.sqlContext().read().format("jdbc")
						.option("spark.driver.extraClassPath", datasource.getDriver())
						.option("spark.executor.extraClassPath", datasource.getDriver())
						.option("driver", datasource.getDriver())
						.option("url", Helper.genUrlByDatasource(datasource))
						.option("user", datasource.getUsername())
						.option("password", datasource.getPassword())
						.option("dbtable", "(" + sql + ") as impala_table").load();
			} else if (datasource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString())) {
				df = sparkSession.sqlContext().read().format("jdbc")
						.option("spark.driver.extraClassPath", datasource.getDriver())
						.option("spark.executor.extraClassPath", datasource.getDriver())
						.option("driver", datasource.getDriver())
						.option("url", Helper.genUrlByDatasource(datasource))
						.option("user", datasource.getUsername())
						.option("password", datasource.getPassword())
						.option("dbtable", "(" + sql + ") as mysql_table").load();
			} else if (datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
				df = sparkSession.sqlContext().read().format("jdbc")
						.option("driver", datasource.getDriver())
						.option("url", Helper.genUrlByDatasource(datasource))
						.option("user", datasource.getUsername())
						.option("password", datasource.getPassword())
						.option("dbtable", "(" + sql + ")  oracle_table").load();
			} else if (datasource.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) {				
				df = sparkSession.sqlContext().read().format("jdbc")
						.option("driver", datasource.getDriver())
						.option("url", Helper.genUrlByDatasource(datasource))
						.option("lazyInit", "true")
						.option("user", datasource.getUsername())
						.option("password", datasource.getPassword())
						.option("dbtable", "(" + sql + ") as postgres_table").load();
			}
//			df.show(true);
			rsHolder.setCountRows(df.count());
			rsHolder.setDataFrame(df);
			rsHolder.setType(ResultType.dataframe);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException e) {			
			e.printStackTrace();
			throw new RuntimeException(e);
		}  catch (Exception e) {				
			e.printStackTrace();
			throw new RuntimeException(e);
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
			dfSorted.printSchema();
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

	@Override
	public List<Map<String, Object>> executeAndFetchByDatasource(String sql, Datasource datasource, String clientContext) throws IOException {
		logger.info(sql);
		logger.info("Started SQL Execution");
		List<Map<String, Object>> data = new ArrayList<>();
		ResultSetHolder rsHolder = executeSqlByDatasource(sql, datasource, clientContext);
		Dataset<Row> dfSorted = rsHolder.getDataFrame();
		dfSorted.printSchema();
		Row[] rows = (Row[]) dfSorted.head(Integer.parseInt("" + dfSorted.count()));
		String[] columns = dfSorted.columns();
		for (Row row : rows) {
			Map<String, Object> object = new LinkedHashMap<String, Object>(columns.length);
			for (String column : columns) {
				object.put(column, (row.getAs(column) == null ? "" :
					(row.getAs(column) instanceof Vector) ? Arrays.toString((double[])((Vector)row.getAs(column)).toArray()) : row.getAs(column)));
			}
			data.add(object);
		}
		logger.info("Completed SQL Execution");		
		return data;
	}
	
	public List<Map<String, Object>> executeAndFetchFromTempTable(String sql, String clientContext) throws IOException {
		logger.info(" Inside executeAndFetchFromTempTable  for SQL : " + sql);
		List<Map<String, Object>> data = new ArrayList<>();
		ResultSetHolder rsHolder = readTempTable(sql, clientContext);
		Dataset<Row> dfSorted = rsHolder.getDataFrame();
		dfSorted.printSchema();
		Row[] rows = (Row[]) dfSorted.head(Integer.parseInt("" + dfSorted.count()));
		String[] columns = dfSorted.columns();
		for (Row row : rows) {
			Map<String, Object> object = new LinkedHashMap<String, Object>(columns.length);
			for (String column : columns) {
				object.put(column, (row.getAs(column) == null ? "" :
					(row.getAs(column) instanceof Vector) ? Arrays.toString((double[])((Vector)row.getAs(column)).toArray()) : row.getAs(column)));
			}
			data.add(object);
		}
		return data;
	}
	
	@Override
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
	public ResultSetHolder executeAndRegisterByDatasource(String sql, String tableName, Datasource datasource, String clientContext) throws IOException {
		ResultSetHolder rsHolder = executeSqlByDatasource(sql, datasource, clientContext);
		Dataset<Row> df = rsHolder.getDataFrame();		
		long countRows = df.count();

		rsHolder.setCountRows(countRows);
		rsHolder.setTableName(tableName);
		df.createOrReplaceGlobalTempView(tableName);
		registerTempTable(df, tableName);
		logger.info("temp table registered: " + tableName);
		return rsHolder;
	}
	
	public ResultSetHolder castDFCloumnsToDoubleType(ResultSetHolder rsHolder, String sql, Datasource datasource, String tableName, boolean registerTempTable, String clientContext) throws IOException {
		if(rsHolder == null || rsHolder.getDataFrame() == null) {
			rsHolder = executeSqlByDatasource(sql, datasource, clientContext);
			rsHolder.setTableName(tableName);
		} 
		Dataset<Row> df = rsHolder.getDataFrame();
		
		for(String colName : df.columns()) {
			df = df.withColumn(colName, df.col(colName).cast(DataTypes.DoubleType));
		}
		
		if(registerTempTable) {
			registerTempTable(df, tableName);
		}
		
		return rsHolder;
	}
	
	
	@Override
	public ResultSetHolder replaceNullValByDoubleValFromDF(ResultSetHolder rsHolder, String sql, Datasource datasource, String tableName, boolean registerTempTable, String clientContext) throws IOException {
		if(rsHolder == null || rsHolder.getDataFrame() == null) {
			rsHolder = executeSqlByDatasource(sql, datasource, clientContext);
			rsHolder.setTableName(tableName);
		} 
		Dataset<Row> df = rsHolder.getDataFrame();
		df = df.na().fill(0.0, df.columns());
		rsHolder.setDataFrame(df);
		if(registerTempTable) {
			registerTempTable(df, tableName);
		}
		return rsHolder;
	}
	
	@Override
	public ResultSetHolder createAndRegister(List<Row> data, StructType structType, String tableName, String clientContext) throws IOException {
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
					Dataset<Row> df = sparkSession.createDataFrame(data, structType);
//					df.show();
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
//					df.show();
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
		SparkSession sparkSession;
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

	public ResultSetHolder readTempTable(String sql, String clientContext) throws IOException {
		ResultSetHolder rsHolder = new ResultSetHolder();
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
		Dataset<Row> df = sparkSession.sql(sql);
		
		rsHolder.setDataFrame(df);
		rsHolder.setCountRows(df.count());
		rsHolder.setType(ResultType.dataframe);
		return rsHolder;
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
		IReader iReader = dataSourceFactory.getDatapodReader(datapod);
		String filePath = datastore.getLocation();
		String hdfsLocation = String.format("%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath());
		if (!filePath.contains(hdfsLocation)) {
			filePath = String.format("%s%s", hdfsLocation, filePath);
		}
		if (obj instanceof SparkSession) {
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			ResultSetHolder rsHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
			sparkSession.sqlContext().registerDataFrameAsTable(rsHolder.getDataFrame(), tableName);
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
		IWriter datapodWriter = null;
		ResultSetHolder rsHolder = executeSql(sql);

		try {
			datapodWriter = dataSourceFactory.getDatapodWriter(datapod);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException("Can not write data.");
		}
		datapodWriter.write(rsHolder, filePathUrl, datapod, saveMode);
		return rsHolder;
	}
	
	/**
	 * 
	 * @param rowObjList
	 * @param attributes
	 * @param tableName
	 * @param filePath
	 * @param datapod
	 * @param saveMode
	 * @param clientContext
	 * @return
	 * @throws IOException 
	 */
	@Override
	public ResultSetHolder createRegisterAndPersist(List<RowObj> rowObjList, List<Attribute> attributes, String tableName, String filePath, Datapod datapod, String saveMode,
			String clientContext) throws IOException {
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
		int count = 0;
		StructField[] fieldArray = new StructField[attributes.size()];
		for(Attribute attribute : attributes){						
			StructField field = new StructField(attribute.getName(), (DataType)getDataType(attribute.getType()), true, Metadata.empty());
//			StructField field = new StructField(attribute.getName(), DataTypes.DoubleType, true, Metadata.empty());
			fieldArray[count] = field;
			count ++;
		}
		StructType schema = new StructType(fieldArray);	
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		Dataset<Row> df = sparkSession.sqlContext().createDataFrame(createRowList(rowObjList), schema);
		df.printSchema();
//		df.show(true);
		registerTempTable(df, tableName);
		ResultSetHolder rsHolder = new ResultSetHolder();
		rsHolder.setDataFrame(df);
		rsHolder.setType(ResultType.dataframe);	
		rsHolder.setCountRows(df.count());
		rsHolder.setTableName(tableName);
		
		// Write datapod
		IWriter datapodWriter = null;
		try {
			datapodWriter = dataSourceFactory.getDatapodWriter(datapod);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException("Can not write data.");
		}
		
		datapodWriter.write(rsHolder, filePathUrl, datapod, saveMode);
		return rsHolder;
	}
	
	/**
	 * 
	 * @param rowObjList
	 * @return
	 */
	private List<Row> createRowList(List<RowObj> rowObjList) {
		List<Row> rowList = new ArrayList<>();
		if (rowObjList == null || rowObjList.isEmpty()) {
			return null;
		}
		
		for (RowObj rowObj : rowObjList) {
			rowList.add(RowFactory.create(rowObj.getRowData()));
		}
		return rowList;
	}

	@Override
	public ResultSetHolder registerAndPersist(ResultSetHolder rsHolder, String tableName, String filePath, Datapod datapod, String saveMode,
			String clientContext) throws IOException {
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getStmtObject();
		IWriter datapodWriter = null;
		if (obj instanceof SparkSession) {
			registerTempTable(rsHolder.getDataFrame(), tableName);
			logger.info("temp table registered: " + tableName);
			try {
				datapodWriter = dataSourceFactory.getDatapodWriter(datapod);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException("Can not write data.");
			}
			datapodWriter.write(rsHolder, filePathUrl, datapod, saveMode);
		}
		return rsHolder;
	}
	
	@Override
	public ResultSetHolder executeRegisterAndPersist(String sql, String tableName, String filePath, Datapod datapod,
			String saveMode, boolean formPath, String clientContext) throws IOException {
		String filePathUrl = filePath;
		if(formPath) {
			filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
		} 
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getStmtObject();
		IWriter datapodWriter = null;
		// HiveContext hiveContext = null;
		ResultSetHolder rsHolder = null;
		if (obj instanceof SparkSession) {
			// hiveContext = (HiveContext) conHolder.getStmtObject();
			rsHolder = executeAndRegister(sql, tableName, clientContext);
			try {
				datapodWriter = dataSourceFactory.getDatapodWriter(datapod);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException("Can not write data.");
			}
			rsHolder.setTableName(tableName);
			datapodWriter.write(rsHolder, filePathUrl, datapod, saveMode);
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
//			ResultSetHolder rsHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
//			df = rsHolder.getDataFrame();
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
		IReader iReader = dataSourceFactory.getDatapodReader(datapod);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
		ConnectionHolder conHolder = conn.getConnection();

		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession) {
			ResultSetHolder rsHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
			df = rsHolder.getDataFrame();
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
			logger.error("Datastore is not available for this datapod.");
			throw new Exception("Datastore is not available for this datapod.");
		}
		IReader iReader = dataSourceFactory.getDatapodReader(datapod);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
		ConnectionHolder conHolder = conn.getConnection();

		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession) {
			ResultSetHolder rsHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
			df = rsHolder.getDataFrame();
		}
		
//		df.show(false);
		Row [] rows = (Row[]) df.head(rowLimit);
		for (Row row : rows) {
			strList.add(row.toString());
		}
		return strList;
	}
	
	public List<Map<String, Object>> fetchModelResults2(DataStore datastore, Datapod datapod, int rowLimit, String clientContext) throws Exception {
		
		List<Map<String, Object>> data = new ArrayList<>();
		Dataset<Row> df = null;
		if (datastore == null) {
			logger.error("Datastore is not available for this datapod.");
			throw new Exception("Datastore is not available for this datapod.");
		}
		IReader iReader = dataSourceFactory.getDatapodReader();
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
		ConnectionHolder conHolder = conn.getConnection();

		ResultSetHolder rsHolder = iReader.read(datapod, datastore, hdfsInfo, conHolder.getStmtObject(), datasource);
		df = rsHolder.getDataFrame();		
		
//		df.show(false);
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

	@Override
	public long loadAndRegister(Load load, String filePath, String dagExecVer, String loadExecVer,
			String datapodTableName, Datapod datapod, String clientContext) throws Exception {
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		Dataset<Row> dfTmp = sparkSession.read().format("com.databricks.spark.csv")
												.option("dateFormat", "dd-MM-yyyy")
												.option("inferSchema", "false")
												.option("header", "true").load(load.getSource().getValue());
		long count = dfTmp.count();
		
		// sparkSession.registerDataFrameAsTable(dfTmp, "dfLoadTemp");
		String tempTableName = "dfLoadTemp"+"_"+loadExecVer;
		sparkSession.sqlContext().registerDataFrameAsTable(dfTmp, tempTableName);
		
		ResultSetHolder rsHolder = null;		
		String sqlWiVersion = "SELECT *, " + ((dagExecVer == null) ? loadExecVer : dagExecVer)
				+ " AS version FROM "+tempTableName;
		String sqlWoVersion = "SELECT * FROM "+tempTableName;

		String[] columns = dfTmp.columns();
		List<String> list = Arrays.asList(columns);
		if (list.contains("version")) {
			rsHolder = executeSql(sqlWoVersion, clientContext);
		} else {
			if (!datapod.getAttributes().get(datapod.getAttributes().size() - 1).getName()
					.equalsIgnoreCase("version")) {
				rsHolder = executeSql(sqlWoVersion, clientContext);
			} else
				rsHolder = executeSql(sqlWiVersion, clientContext);
		}
		
		Dataset<Row> dfTask = rsHolder.getDataFrame();

		// dfTask = hiveContext.sql("select *, "+ dagExecVer + " as version from
		// dfLoadTemp").coalesce(4);
//		dfTask.cache();
		// sparkSession.registerDataFrameAsTable(dfTask, datapodTableName);
		sparkSession.sqlContext().registerDataFrameAsTable(dfTask, datapodTableName);
		logger.info("Going to datapodWriter");
		
		// Datapod datapod = (Datapod) daoRegister.getRefObject(new
		// MetaIdentifier(MetaType.datapod, datapodKey.getUUID(),
		// datapodKey.getVersion()));
		
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
		ResultSetHolder rsHolder2 = new ResultSetHolder();
		if(datapod != null) {
			rsHolder2 = applySchema(rsHolder, datapod, null, datapodTableName, true);
		} else {
			rsHolder2.setDataFrame(dfTask);
			rsHolder2.setType(ResultType.dataframe);
			
		}
		rsHolder2.setTableName(datapodTableName);
		IWriter datapodWriter = datasourceFactory.getDatapodWriter(datapod);
		datapodWriter.write(rsHolder2, filePathUrl, datapod, SaveMode.Overwrite.toString());
		return count;
	}

	@Override
	public void registerDatapod(String tableName, Datapod datapod, DataStore dataStore, ExecContext execContext,
			String clientContext) throws IOException {
		Datasource datasource = null;
		try {
			datasource = commonServiceImpl.getDatasourceByApp();
		
			IConnector connection = connFactory.getConnector(datasource.getType().toLowerCase());
			IReader iReader;
			try {
				iReader = dataSourceFactory.getDatapodReader(datapod);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException | NullPointerException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
	
			ConnectionHolder conHolder = connection.getConnection();
			Object obj = conHolder.getStmtObject();
			if (obj instanceof SparkSession && !execContext.equals(ExecContext.livy_spark)) {
				ResultSetHolder rsHolder = iReader.read(datapod, dataStore, hdfsInfo, obj, datasource);
				Dataset<Row> df = rsHolder.getDataFrame();
				
				tableName = rsHolder.getTableName();
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
					
//					df.show(true);
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Attribute> fetchAttributeList(String csvFileName, String parquetDir, boolean flag,
			boolean writeToParquet, String clientContext) throws Exception {
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		Dataset<Row> df = sparkSession.read()
						.format("com.databricks.spark.csv")
						.option("inferSchema", "true")
						.option("header", "true")
						.load(csvFileName);
		df.printSchema();
		
		StructType st = df.schema();
		
		Seq<StructField> seqFields = st.thisCollection();
		Iterator<StructField> iter = st.iterator();
		List<Attribute> attributes = new ArrayList<Attribute>();
		int i = 0;
		while (iter.hasNext()) {
			StructField sf = iter.next();			
			if(!sf.dataType().typeName().equalsIgnoreCase("version")) {
				Attribute attr1 = new Attribute();
				attr1.setAttributeId(i++);
				if (!sf.dataType().typeName().equalsIgnoreCase("timestamp")) {
					attr1.setType((sf.dataType().typeName()));
				} else {
					attr1.setType("string");
				}
				attr1.setName(sf.name().toLowerCase());
				attr1.setDesc(sf.name().toLowerCase());
				attr1.setDispName(sf.name().toLowerCase());
				attr1.setActive("Y");
				attr1.setLength(null);
				attributes.add(attr1);
			}
		}
		/*if (flag) {
			Attribute attr2 = new Attribute();
			attr2.setAttributeId(i++);
			attr2.setType("Integer");
			attr2.setName("version");
			attr2.setDesc("version");
			attr2.setDispName("version");
			attr2.setActive("Y");
			attr2.setLength(null);
			attributes.add(attr2);
		}*/
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

		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		// Execute SQL
		logger.info("inside SparkExecutor for the quiery: " + sql);
		Dataset<Row> df = sparkSession.sql(sql).coalesce(10);
//		df.show(Integer.parseInt(""+df.count()));
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
				ResultSetHolder rsHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
				df = rsHolder.getDataFrame();
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
	public List<Map<String, Object>> fetchResults(DataStore datastore, Datapod datapod, int rowLimit, String targetTable, String clientContext)
			throws Exception {
		List<Map<String, Object>> data = new ArrayList<>();
		Dataset<Row> df = null;
		if (datastore == null) {
			logger.error("Datastore is not available for this datapod");
			throw new Exception();
		}
//		IReader iReader = dataSourceFactory.getDatapodReader(datapod, null);
//		Datasource datasource = null;
//		if(datapod != null) {
//			datasource = commonServiceImpl.getDatasourceByDatapod(datapod);
//		} else {
//		Datasource datasource = commonServiceImpl.getDatasourceByApp();
//		}
		
		IConnector conn = connFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = conn.getConnection();

//		Object obj = conHolder.getStmtObject();
//		if (obj instanceof SparkSession) {
//			ResultSetHolder rsHolder = iReader.read(datastore.getLocation(), obj);
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			DataFrameReader reader = sparkSession.read();
			df = reader.load(datastore.getLocation());
			String tableName = Helper.genTableName(datastore.getLocation());
			ResultSetHolder rsHolder = new ResultSetHolder();
			rsHolder.setDataFrame(df);
			rsHolder.setCountRows(df.count());
			rsHolder.setType(ResultType.dataframe);
			rsHolder.setTableName(tableName);
//		}
		
//		df.show(false);
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

	
	
	
	/**
	 * 
	 *  To transpose : 
	 *  
	 * SELECT t.iteration_id, yn.custid as cust_id, t.val_column as trial_value FROM 
	 * yn JOIN 
	 * (SELECT row_number() over(partition by seq_column) as iteration_id, seq_column, val_column  FROM 
	 * (SELECT array(1, 2) as seq_r, array(yes, no) arr_r
	 * FROM generate_yn_data ) arrayrec
	 * LATERAL VIEW posexplode(arrayrec.seq_r) EXPLODED_rec as seqs, seq_column 
	 * LATERAL VIEW posexplode(arrayrec.arr_r) EXPLODED_rec as seqv, val_column 
	 * where seqs = seqv) t 
	 * ON (t.seq_column = yn.id)
	 * @param resultSetHolder
	 * @param locationDatapod
	 * @param execVersion
	 * @return
	 */
	/*public ResultSetHolder transposeData(ResultSetHolder resultSetHolder, Datapod locationDatapod, String execVersion) {
		Dataset<Row> df = resultSetHolder.getDataFrame();
		String []columns = df.columns();
		
		StringBuilder sb = new StringBuilder(ConstantsUtil.SELECT);
		
		for (Attribute attribute : keyAttrList) {
			sb.append(attribute.getName()).append(", ");
		}
		int count = 0;
		String arrSeq = " array(";
		String arrCol = " array(";
		for (int i=0; i< columns.length; i++) {
			if (i==0) {
				// Column is id column
				sb.append(" array ").append(columns[i]).append(") ");
				sb.append(locationDatapod.getAttributeName(i)).append(", ");
				continue;
			}
			sb.append(columns[i]).append(" ");
			sb.append(locationDatapod.getAttributeName(i)).append(", ");
		}
		
		sb.append("tranpose_column ");
		sb.append(locationDatapod.getAttributeName(count++));
		sb.append(", transpose_value ");
		sb.append(locationDatapod.getAttributeName(count++));
		sb.append(", " + execVersion + " ");
		sb.append(locationDatapod.getAttributeName(count++));
		sb.append(" FROM (");
		sb.append(ConstantsUtil.SELECT);
		
		for (Attribute attribute : keyAttrList) {
			sb.append(attribute.getName()).append(", ");
		}
		for (String columnName : keyAttrList) {
			sb.append(columnName).append(", ");
		}
		
		sb.append(" MAP (");
		count = 0;
		for(Attribute attribute : attrList) {
			isAttrFound = Boolean.TRUE;
			sb.append("'"+attribute.getName() + "', " + attribute.getName());
			sb.append((count < attrList.size()-1)?", ":"");
			count++;
			
		}
		for(String columnName : attrList) {
			isAttrFound = Boolean.TRUE;
			sb.append("'"+columnName + "', " + columnName);
			sb.append((count < attrList.size()-1)?", ":"");
			count++;
			
		}
		
		sb.append(") AS tmp_column FROM ");
		sb.append(sourceTableName);
		sb.append("  ) x LATERAL VIEW EXPLODE(tmp_column) exptbl AS tranpose_column, transpose_value ");
	}
*/
	@Override
	public String generateFeatureData(Object object, List<Feature> features, int numIterations, String tableName) {
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = null;
		try {
			sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
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
		
//		df.show(false);
		sparkSession.sqlContext().registerDataFrameAsTable(df, tableName);
		return tableName;
	}
	
	@Override
	public String generateFeatureData(List<Feature> features, int numIterations, String[] fieldArray, String tableName) {
		Dataset<Row> df = null;
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = null;
		try {
			sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
//		df.show(false);
		return tableName;
	}
	
	@Override
	public String assembleRandomDF(String[] fieldArray, String tableName, boolean isDistribution, String clientContext) throws IOException{
		String sql = "SELECT * FROM " + tableName;
		Dataset<Row> df = executeSql(sql, clientContext).getDataFrame();
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		
		if(isDistribution)
			df = df.withColumnRenamed(df.columns()[1], fieldArray[0]);
		else
			fieldArray = df.columns();

		df.printSchema();
		
		VectorAssembler va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
		Dataset<Row> assembledDf = va.transform(df);
		assembledDf.printSchema();
//		assembledDf.show(false);
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
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		
		VectorAssembler va = new VectorAssembler();
		

		va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
		Dataset<Row> transformedDf = va.transform(df).select("features");
				
		sparkSession.sqlContext().registerDataFrameAsTable(transformedDf, tableName);
		return va;
	}

	@Override
	public Object assembleDF(String[] fieldArray, ResultSetHolder rsHolder, String sql, String tempTableName, Datasource datasource, boolean registerTempTable, String clientContext) throws IOException{
		//get dataframe as per the provided option
		Dataset<Row> df = null;
		if(rsHolder != null && rsHolder.getDataFrame() != null) {
			df = rsHolder.getDataFrame();
		} else {
			df = executeSqlByDatasource(sql, datasource, clientContext).getDataFrame();			
		}
		
		//assemble dataframe
		VectorAssembler va = new VectorAssembler();
		va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
		Dataset<Row> transformedDf = va.transform(df).select("features");
		
		rsHolder.setDataFrame(df);
		
		//register temp table if flag, registerTempTable, is set
		if(registerTempTable) {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
			sparkSession.sqlContext().registerDataFrameAsTable(transformedDf, tempTableName);
		}
		return rsHolder;
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
//		df.show(false);
		
		
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
		
//		df.show(false);

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

	/********************** UNUSED **********************/
//	public ResultSetHolder preparePredictDfForEncoding(ResultSetHolder rsHolder, Map<String, EncodingType> encodingDetails, boolean registerTempTable, String tempTableName) throws IOException {
//			Dataset<Row> df = rsHolder.getDataFrame();
//			logger.info("df 1");
//			df.printSchema();
//			List<PipelineStage> pipelineStagesTrng = new ArrayList<>();
//			for(String colName : encodingDetails.keySet()) {
//				encodeDataframe(colName, colName.concat("_vec"), encodingDetails.get(colName), pipelineStagesTrng);
//			}
//			
//			//fitting training dataframe
//			Pipeline pipelineTrng = new Pipeline().setStages(pipelineStagesTrng.toArray(new PipelineStage[pipelineStagesTrng.size()]));
//			PipelineModel trngModel = pipelineTrng.fit(df);
//			df = trngModel.transform(df);
//			logger.info("df 2");
//			df.printSchema();
//			
//			List<String> columnNames = new ArrayList<>(Arrays.asList(df.columns()));
//			
//			for(String colName : encodingDetails.keySet()) {
//				logger.info("colName : " + colName);
////				df = df.drop(colName);
//				df = columnNames.contains(colName+"_vec")?df.withColumnRenamed(colName, colName+"_raw"):df;
//				df = df.withColumnRenamed(colName+"_vec", colName);
//			}
//			
//			for(String colName : df.columns()) {
//				if(colName.endsWith("_category_index")) {
//					df = df.drop(colName);
//				}
//			}	
//			logger.info("df 3");
//			df.printSchema();
//			rsHolder.setDataFrame(df);
//			if(registerTempTable) {
//				IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
//				SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
//				sparkSession.sqlContext().registerDataFrameAsTable(df, tempTableName);
//			}
//			return rsHolder;
//	}
	
	@Override
	public ResultSetHolder predict(Object trainedModel, Datapod targetDp, String filePathUrl, String tableName, String clientContext, Map<String, EncodingType> encodingDetails) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		//getting data to be predicted
		String assembledDFSQL = "SELECT * FROM " + tableName;
		Dataset<Row> df = executeSql(assembledDFSQL, clientContext).getDataFrame();
//		df = df.na().fill(0.0, df.columns());
		logger.info("After executeSql");
		df.printSchema();
		
		//performing prediction
		@SuppressWarnings("unchecked")
		Dataset<Row> predictionDf = (Dataset<Row>) trainedModel.getClass().getMethod("transform", Dataset.class).invoke(trainedModel, df);
		logger.info("After obtaining predictionDf");
		predictionDf.printSchema();
		logger.info("Going to register temp table");
		
		//registring temp table
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		sparkSession.sqlContext().registerDataFrameAsTable(predictionDf, tableName);
		logger.info("After registering prediction DataFrame as temp table");
		
		//creating and returning ResultSetHolder 
		ResultSetHolder rsHolder = new ResultSetHolder();
		rsHolder.setType(ResultType.dataframe);
		rsHolder.setDataFrame(predictionDf);
		rsHolder.setCountRows(predictionDf.count());
		rsHolder.setTableName(tableName);
		logger.info("Return RSHolder");
		return rsHolder;
	}
	
	@Override
	public List<Double> featureImportance(Object trainedModel, String clientContext) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Vector vector = (Vector) trainedModel.getClass().getMethod("featureImportances").invoke(trainedModel);
		List<Double> featureWeightList = new ArrayList<>();
		for(double featureWeight : vector.toArray()) {
			featureWeightList.add(featureWeight);
		}
		return featureWeightList;
	}
	
	@Override
	public ResultSetHolder persistDataframe(ResultSetHolder rsHolder, Datasource datasource, Datapod targetDatapod, String saveMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		logger.info("inside method persistDataframe");
		Dataset<Row> df = rsHolder.getDataFrame();
//		df.show(false);
		df.printSchema();
		
		datasource = commonServiceImpl.getDatasourceByDatapod(targetDatapod);		
		if(datasource.getType().equalsIgnoreCase(ExecContext.HIVE.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.IMPALA.toString())) {
				String sessionParameters = datasource.getSessionParameters();
				if(sessionParameters != null && !StringUtils.isBlank(sessionParameters)) {
					for(String sessionParam :sessionParameters.split(",")) {
						df.sparkSession().sql("SET "+sessionParam);
					}
				}

				Map<String, String> options = new HashMap<>();
				options.put("driver", datasource.getDriver());
				options.put("user", datasource.getUsername());
				options.put("password", datasource.getPassword());
				df.write().mode(saveMode).options(options).insertInto(rsHolder.getTableName());
		} else {
			String url = Helper.genUrlByDatasource(datasource);
			Properties connectionProperties = new Properties();
			connectionProperties.put("driver", datasource.getDriver());
			connectionProperties.put("user", datasource.getUsername());
			connectionProperties.put("password", datasource.getPassword());
			List<String> vectorFields = new ArrayList<>(); 
			
			// Find out vector type columns
			Tuple2<String, String>[] tuple2 = df.dtypes();
			for(Tuple2<String, String> tuple22 : tuple2) {
				if(tuple22._2().toLowerCase().contains("vector")) {
//					df = df.withColumn(tuple22._1(), df.col(tuple22._1()).cast(DataTypes.StringType));
					vectorFields.add(tuple22._1());
				}
			}
			
//			if(Arrays.asList(df.columns()).contains("features")) {
				/*df = df.withColumn("features", df.col("features").cast(DataTypes.StringType));*/
			if(!vectorFields.isEmpty()) {
				String []fieldNames = df.schema().fieldNames();
				StructField []fields = df.schema().fields();
				StructField []newFields = new StructField[fields.length];
			
			
				for (int i = 0; i < fieldNames.length; i++) {
					if (vectorFields.contains(fieldNames[i])) {
						newFields[i] = new StructField(fieldNames[i],
								DataTypes.StringType, true,
								Metadata.empty());
					} else {
						newFields[i] = fields[i];
					}
				}
				
				df = sparkExecHelper.parseFeatures(df, newFields, vectorFields);
			}

			df.printSchema();
//			}
			/*Tuple2<String, String>[] tuple2 = df.dtypes();
			for(Tuple2<String, String> tuple22 : tuple2) {
				if(tuple22._2().toLowerCase().contains("vector")) {
					df = df.withColumn(tuple22._1(), df.col(tuple22._1()).cast(DataTypes.StringType));
				}
			}*/
			
//			if(partitionColList.size() > 0) {
//				df.write().mode(SaveMode.Append)/*.partitionBy(partitionColList.toArray(new String[partitionColList.size()]))*/.jdbc(url, rsHolder.getTableName(), connectionProperties);
//			} else {
				df.write().mode(saveMode).jdbc(url, rsHolder.getTableName(), connectionProperties);
//			}
		}		
		return rsHolder;
	}
	
	@Override
	public Boolean saveDataframeAsCSV(String tableName, String saveFileName, String clientContext) throws IOException {		
		try {
			String assembledDFSQL = "SELECT * FROM " + tableName;
			Dataset<Row> df = executeSql(assembledDFSQL, clientContext).getDataFrame();
			df.coalesce(1).write().format("com.databricks.spark.csv").option("header", "true").csv(saveFileName);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public ResultSetHolder mapFieldsTypeByFeatures(String tableName, Dataset<Row> df, Datasource datasource, List<String> vectorFields, boolean registerTempTable, String tempTableName, String clientContext) throws IOException {		
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
//		String sql = "SELECT * FROM " + tableName;
//		Dataset<Row> df = executeSqlByDatasource(sql, datasource, clientContext).getDataFrame();
		
		
			String []fieldNames = df.schema().fieldNames();
			StructField []fields = df.schema().fields();
			StructField []newFields = new StructField[fields.length];
		
		
			for (int i = 0; i < fieldNames.length; i++) {
				if (vectorFields.contains(fieldNames[i])) {
					newFields[i] = new StructField(fieldNames[i],
							(VectorUDT)getDataType("vector"), true,
							Metadata.empty());
				} else {
					newFields[i] = fields[i];
				}
			}
			
			df = sparkExecHelper.parseVectorFeatures(df, newFields, vectorFields);
			ResultSetHolder rsHolder = new ResultSetHolder();
			rsHolder.setDataFrame(df);
			rsHolder.setType(ResultType.dataframe);
			rsHolder.setCountRows(df.count());
			rsHolder.setTableName(tempTableName);
			if(registerTempTable) {
				sparkSession.sqlContext().registerDataFrameAsTable(df, tempTableName);
			}
		return rsHolder;
	}
	
	@Override
	public PipelineModel train(ParamMap paramMap, String[] fieldArray, String label, String trainName
			, double trainPercent, double valPercent, String tableName, String clientContext
			, Object algoClass, Map<String, Object> trainOtherParam, TrainResult trainResult
			, String testSetPath, List<String> rowIdentifierCols, String includeFeatures
			, String trainingDfSql, String validationDfSql, Map<String, EncodingType> encodingDetails,
			String saveTrainingSet, String trainingSetPath, Datapod testLocationDP, Datasource testLocationDs, 
			String testLocationTableName, String testLFilePathUrl,
			Datapod trainLocationDP, Datasource trainLocationDS, String trainLocationTableName, String trainFilePathUrl) throws IOException {

		String []origFieldArray = new String[fieldArray.length];
		origFieldArray = fieldArray;
		
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		String assembledDFSQL = "SELECT * FROM " + tableName;
		Dataset<Row> df = executeSql(assembledDFSQL, clientContext).getDataFrame();
		df.printSchema();
		
		trainResult.setTotalRecords(df.count());
		trainResult.setNumFeatures(fieldArray.length);
		
		try {
			Dataset<Row> trngDf = null;
			Dataset<Row> valDf = null;
			Dataset<Row> valDf2 = null;
			

			List<String> tempTableList = new ArrayList<>();
			
			if (trainName.contains("PCA")) {
				valDf2 = df;
				
				registerTempTable(df, "tempTrngDf");
				trngDf = readTempTable(trainingDfSql, clientContext).getDataFrame();
				trngDf = mapFieldsTypeByFeatures("tempTrngDf", trngDf, trainLocationDS, (List<String>) trainOtherParam.get("vectorFields"), true, "", clientContext).getDataFrame();
				trngDf = applyModelSchema(null, "SELECT * FROM "+"tempTrngDf", (Model) trainOtherParam.get("model"), null, false, clientContext).getDataFrame();
				
				//dropping temptables tempTrngDf and tempValDf
				tempTableList.add("tempTrngDf");
				
				trainResult.setTrainingSet(trngDf.count());
			} else {
				Dataset<Row>[] splits = df.randomSplit(new double[] { trainPercent / 100, valPercent / 100 }, 12345);
				trngDf = splits[0];
				valDf = splits[1];
				valDf2 = splits[1];

				registerTempTable(trngDf, "tempTrngDf");
				trngDf = readTempTable(trainingDfSql, clientContext).getDataFrame();
				trngDf.printSchema();
				trngDf = mapFieldsTypeByFeatures("tempTrngDf", trngDf, (Datasource) trainOtherParam.get("sourceDs"), (List<String>) trainOtherParam.get("vectorFields"), true, "tempTrngDf", clientContext).getDataFrame();
				trngDf = applyModelSchema(null, "SELECT * FROM "+"tempTrngDf", (Model) trainOtherParam.get("model"), null, false, clientContext).getDataFrame();

				trngDf.printSchema();
				registerTempTable(valDf, "tempValDf");
				valDf = readTempTable(validationDfSql, clientContext).getDataFrame();
				valDf = mapFieldsTypeByFeatures("tempValDf", valDf, (Datasource) trainOtherParam.get("sourceDs"), (List<String>) trainOtherParam.get("vectorFields"), true, "tempValDf", clientContext).getDataFrame();
				valDf = applyModelSchema(null, "SELECT * FROM "+"tempValDf", (Model) trainOtherParam.get("model"), null, false, clientContext).getDataFrame();
				
				//dropping temptables tempTrngDf and tempValDf
				tempTableList.add("tempTrngDf");
				tempTableList.add("tempValDf");
				
				//saving training set 
				if(saveTrainingSet.equalsIgnoreCase("Y")) {
					//trngDf.write().mode(SaveMode.Append).parquet(trainingSetPath);
					saveTrainDataset(trngDf, trainingSetPath, trainLocationDP, trainLocationDS ,trainLocationTableName, trainFilePathUrl);
				}

				trainResult.setTrainingSet(trngDf.count());
				trainResult.setValidationSet(valDf.count());
			}
			
			dropTempTable(tempTableList);
			
			List<PipelineStage> pipelineStagesTrng = new ArrayList<>();
			if(encodingDetails != null && !encodingDetails.isEmpty()) {
				for(String colName : encodingDetails.keySet()) {
					encodeDataframe(colName, colName.concat("_vec"), encodingDetails.get(colName), pipelineStagesTrng);
				}
				
				List<String> encoderCols = new ArrayList<>();
				for(String col : encodingDetails.keySet()) {
					encoderCols.add(col+"_vec");
				}
				for(String nonStrngCol : fieldArray) {
					if(!encodingDetails.keySet().contains(nonStrngCol)) {
						encoderCols.add(nonStrngCol);
					}
				}

				logger.info("Printing trained training and validation dataset schema");
				trngDf.printSchema();
				
				if(valDf != null) {
					valDf.printSchema();
				}
				
				fieldArray = encoderCols.toArray(new String[encoderCols.size()]);
			}
			
			VectorAssembler vectorAssembler = new VectorAssembler();
			if (trainName.contains("PCA")) {
				vectorAssembler.setInputCols(fieldArray).setOutputCol("pcaInputFeatures");
			} else {
				vectorAssembler.setInputCols(fieldArray).setOutputCol("features");
			}
			for (String colname : vectorAssembler.getInputCols()) {
				logger.info("Vecassembler input col : " + colname);
			}
								
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
				
				logger.info("Printing trained training and validation dataset schema 1");
				trngDf.printSchema();

				if(valDf != null) {
					valDf.printSchema();
				}
			} else if (trainName.contains("PCA")) {
				method = algoClass.getClass().getMethod("setInputCol", String.class);
				method.invoke(algoClass, "pcaInputFeatures");

				method = algoClass.getClass().getMethod("setOutputCol", String.class);
				method.invoke(algoClass, "features");
			}
			
			Set<String> encodingCols = null;
			if(encodingDetails != null && !encodingDetails.isEmpty()) {
				encodingCols = encodingDetails.keySet();
			}
			
			trngDf = castUnEncodedCols(trngDf, encodingDetails, encodingCols);	
			trngDf = trngDf.na().fill(0.0,trngDf.columns());
			
			if(valDf != null) {
				valDf = castUnEncodedCols(valDf, encodingDetails, encodingCols);				
				valDf = valDf.na().fill(0.0,valDf.columns());
			}
			
			logger.info("Printing trained training and validation dataset schema 3");
			trngDf.printSchema();
			if(valDf != null) {
				valDf.printSchema();
			}
					
			pipelineStagesTrng.add(vectorAssembler);
			pipelineStagesTrng.add((PipelineStage) algoClass);
//			Pipeline pipeline = new Pipeline().setStages(new PipelineStage[] {vectorAssembler, (PipelineStage) algoClass});
			logger.info("Before train pipeline creation");
			Pipeline pipeline = new Pipeline().setStages(pipelineStagesTrng.toArray(new PipelineStage[pipelineStagesTrng.size()]));
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			trainResult.setStartTime(simpleDateFormat.parse((new Date()).toString()));
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			PipelineModel trngModel = null;
			
			Dataset<Row> trainedDataSet = null;
			if (trainName.contains("PCA")) {
				if (null != paramMap) {
					trngModel = pipeline.fit(trngDf, paramMap);
				} else {
					trngModel = pipeline.fit(trngDf);
				}
				
				trainedDataSet = trngModel.transform(trngDf);
				testSetPath = trainingSetPath;
				testLocationDP = trainLocationDP;
				testLocationDs = trainLocationDS;
				testLFilePathUrl = trainFilePathUrl;
			} else {
				logger.info("Before train pipeline fit");
				if (null != paramMap) {
					trngModel = pipeline.fit(trngDf, paramMap);
				} else {
					trngModel = pipeline.fit(trngDf);
				}
				logger.info("After train pipeline fit");
				trainedDataSet = trngModel.transform(valDf);
				
				logger.info("Printing trained training and validation dataset schema 4");
				trainedDataSet.printSchema();
				valDf.printSchema();
				
			}
			stopWatch.stop();
			trainResult.setTimeTaken(stopWatch.getTotalTimeMillis()+" ms");
			trainResult.setEndTime(simpleDateFormat.parse((new Date()).toString()));
			
			if(trainOtherParam != null) {
				String cMTableName = (String) trainOtherParam.get("confusionMatrixTableName");
				sparkSession.sqlContext().registerDataFrameAsTable(trainedDataSet, cMTableName);
			}			
			
			sparkSession.sqlContext().registerDataFrameAsTable(trainedDataSet, "trainedDataSet");
			if (trainName.contains("PCA")) {
				savePCAResult(trainedDataSet, valDf2, rowIdentifierCols, trainLocationDP, trainLocationDS, trainLocationTableName, trainFilePathUrl);
			} else {
				saveTrainedTestDataset(trainedDataSet, valDf2, testSetPath, rowIdentifierCols, includeFeatures, origFieldArray, trainName, testLocationDP, testLocationDs, testLocationTableName, testLFilePathUrl);
			}
			return trngModel;			
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
		} catch (Error e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public StructType createSchemaByModel(Model model) {
		List<StructField> fieldList = new ArrayList<>();		
		for(Feature feature : model.getFeatures()) {
			fieldList.add(new StructField(feature.getName(), (DataType)getDataType(feature.getType()), true, Metadata.empty()));
		}
		return new StructType(fieldList.toArray(new StructField[fieldList.size()]));
	}
	
	public StructType createSchemaByDatapod(Datapod datapod) {
		List<StructField> fieldList = new ArrayList<>();		
		for(Attribute attribute : datapod.getAttributes()) {
			fieldList.add(new StructField(attribute.getName(), (DataType)getDataType(attribute.getType()), true, Metadata.empty()));
		}
		return new StructType(fieldList.toArray(new StructField[fieldList.size()]));
	}
	
	public ResultSetHolder applyModelSchema(ResultSetHolder rsHolder, String sql, Model model, String tempTableName, boolean registerTempTable, String clientContext) throws IOException {
		Dataset<Row> df = null;
		if(sql != null && !sql.isEmpty()) {
			rsHolder = readTempTable(sql, clientContext);
			df = rsHolder.getDataFrame();
		} 

		df = rsHolder.getDataFrame();
		
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
		
		for(Feature feature : model.getFeatures()) {
			df = df.withColumn(feature.getName(), df.col(feature.getName()).cast((DataType)getDataType(feature.getType())));
		}
		
		//df = sparkSession.createDataFrame(rsHolder.getDataFrame().collectAsList(), createSchemaByModel(model));
		rsHolder.setDataFrame(df);
		
		if(registerTempTable) {
			sparkSession.sqlContext().registerDataFrameAsTable(df, tempTableName);
		}
		return rsHolder;
	}
	
	public void savePCAResult(Dataset<Row> df, Dataset<Row> keyAttrDf, List<String> colList, Datapod datapod, Datasource datasource, String tableName, String filePathUrl) {
		if(colList != null && !colList.isEmpty()) {
			keyAttrDf = keyAttrDf.withColumn("rowNum", functions.row_number().over(Window.orderBy(keyAttrDf.columns()[keyAttrDf.columns().length-1])));
			keyAttrDf = keyAttrDf.select("rowNum", colList.toArray(new String[colList.size()]));
		}	
		
		df = df.withColumn("rowNum", functions.row_number().over(Window.orderBy(df.columns()[df.columns().length-1]))).select("rowNum", "features");
		
		Dataset<Row> joinedDf = null;
		if(colList != null && !colList.isEmpty()) {
			joinedDf = keyAttrDf.join(df, "rowNum");
		} else {
			joinedDf = df;
		}
		
		joinedDf = joinedDf.drop("rowNum");		
		if(datapod !=null) {
			if(joinedDf.columns().length != datapod.getAttributes().size()) {
				throw new RuntimeException("Datapod '" + datapod.getName() + "' column size(" + datapod.getAttributes().size() + ") does not match with column size("+ joinedDf.columns().length +") of dataframe");
			}
		}
		joinedDf.write().mode(SaveMode.Append).parquet(filePathUrl);
	}
	
	/**
	 * 
	 * @param df
	 * @param encodingDetails
	 * @param encodingCols
	 * @return
	 */
	public Dataset<Row> castUnEncodedCols(Dataset<Row> df, Map<String, EncodingType> encodingDetails, Set<String> encodingCols) {
		Map<String, String> colTypeMap = new HashMap<>();
		for (Tuple2<String, String> dtype : df.dtypes()) {
			colTypeMap.put(dtype._1, dtype._2);
		}
		logger.info("ColTypeMap : " + colTypeMap);
		for(String col : df.columns()) {
			if(encodingCols != null 
					&& !encodingCols.isEmpty() ) {
				if(!encodingCols.contains(col)
					&& !encodingDetails.keySet().contains(col) 
					&& !colTypeMap.get(col).contains("Vector")) {
					df = df.withColumn(col, df.col(col).cast(DataTypes.DoubleType));
				}
			} else if (!colTypeMap.get(col).contains("Vector")) {
				df = df.withColumn(col, df.col(col).cast(DataTypes.DoubleType));
			}
		}
		return df;
	}
	
//		if(inputCols.length != outputCols.length) {
	public List<PipelineStage> encodeDataframe(String inputCol, String outputCol, EncodingType encodingType,  List<PipelineStage> pipelineStageList) throws IOException {
//			throw new RuntimeException("The number of input columns "+inputCols.length+" must be the same as the number of output columns "+outputCols.length+".");
//		}

		if(encodingType.equals(EncodingType.ONEHOT)) {
			StringIndexer stringIndexer = new StringIndexer()
					  .setInputCol(inputCol)
					  .setOutputCol(inputCol+"_category_index")
					  .setHandleInvalid("keep");
			pipelineStageList.add(stringIndexer);
		
			OneHotEncoder encoder = new OneHotEncoder().setInputCol(inputCol+"_category_index").setOutputCol(outputCol);
			pipelineStageList.add(encoder);

		}
		return pipelineStageList;
	}
	
	public void saveTrainDataset(Dataset<Row> trainedDataSet,String defaultPath, Datapod trainLocationDP, Datasource trainLocationDs, String trainLTableName , String tLFilePathUrl) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if(trainLocationDP != null) {
			ResultSetHolder rsHolder = new ResultSetHolder();
			rsHolder.setDataFrame(trainedDataSet);
			rsHolder.setTableName(trainLTableName);
			
			//check whether the number of attributes of datapod and dataframe
			//are equal if not then throw exception
			if(trainLocationDP !=null) {
				if(trainedDataSet.columns().length != trainLocationDP.getAttributes().size()) {
					throw new RuntimeException("Datapod '" + trainLocationDP.getName() + "' column size(" + trainLocationDP.getAttributes().size() + ") does not match with column size("+ trainedDataSet.columns().length +") of dataframe");
				}
				
				if(trainLTableName != null && !trainLTableName.isEmpty()) {
					rsHolder = applySchema(rsHolder, trainLocationDP, null, trainLTableName, false);
					trainedDataSet = rsHolder.getDataFrame();
				}
			}
			
			//write according to datasource
			if(!trainLocationDs.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
				persistDataframe(rsHolder, trainLocationDs, trainLocationDP,SaveMode.Append.toString());
			} else {
				
				trainedDataSet.write().mode(SaveMode.Append).parquet(tLFilePathUrl);
			}
			
			
		}else {
			trainedDataSet.write().mode(SaveMode.Append).parquet(defaultPath);
		}
	}
	
	public void saveTrainedTestDataset(Dataset<Row> trainedDataSet, Dataset<Row> valDf
			, String defaultPath, List<String> rowIdentifierCols, String includeFeatures
			, String[] fieldArray, String trainName, Datapod testLocationDP, Datasource testLocationDs, String testLTableName , String tLFilePathUrl) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if(includeFeatures.equalsIgnoreCase("Y")) {
			rowIdentifierCols = removeDuplicateColNames(fieldArray, rowIdentifierCols);
		}
		if(rowIdentifierCols != null && !rowIdentifierCols.isEmpty()) {
			valDf = valDf.withColumn("rowNum", functions.row_number().over(Window.orderBy(valDf.columns()[valDf.columns().length-1])));
			valDf = valDf.select("rowNum", rowIdentifierCols.toArray(new String[rowIdentifierCols.size()]));
		}		
		logger.info("trainedDataSet show 0");
		trainedDataSet.printSchema();
		
		List<String> predDFCols = Arrays.asList(trainedDataSet.columns());
		
//		sourceDF = sourceDF.na().fill(0.0,sourceDF.columns());
		trainedDataSet = trainedDataSet.withColumn("rowNum", functions.row_number().over(Window.orderBy(trainedDataSet.columns()[trainedDataSet.columns().length-1])));
		
		List<String> colNameList = new ArrayList<>();
		if(includeFeatures.equalsIgnoreCase("Y")) {
			for(String cloName : fieldArray) {
				colNameList.add(cloName);
			}
		} 
		if (!trainName.contains("PCA")) {
			colNameList.add("label");
		}
		if(predDFCols.contains("prediction")) {
			colNameList.add("prediction");
		}
		if (trainName.contains("LogisticRegression")
				|| trainName.contains("GBTClassifier")) {
			colNameList.add("probability");
		}
		if (trainName.contains("PCA")) {
			colNameList.add("features");
		}

		logger.info("trainedDataset >>>>>>>>>>> ");
		trainedDataSet.printSchema();
		for (String colName : colNameList) {
			logger.info("colName : " + colName);
		}
		trainedDataSet = trainedDataSet.select("rowNum", colNameList.toArray(new String[colNameList.size()]));
		trainedDataSet.printSchema();
		//creating column prediction_status
		Dataset<Row> predictionStatusDF = null;
		if(predDFCols.contains("prediction")) {
			predictionStatusDF = sparkExecHelper.getPredictionCompareStatus(trainedDataSet);
		}
		//joining dataframes
		Dataset<Row> joinedDF = null;
		if(rowIdentifierCols != null && !rowIdentifierCols.isEmpty()) {
			if(predictionStatusDF != null) {
				joinedDF = valDf.join(trainedDataSet, "rowNum").join(predictionStatusDF, "rowNum");
			} else {
				joinedDF = valDf.join(trainedDataSet, "rowNum");
			}
		} else {
			joinedDF = trainedDataSet.join(predictionStatusDF, "rowNum");
		}		
		
		joinedDF = joinedDF.drop("rowNum");
		joinedDF.printSchema();
		if(testLocationDP != null) {
			ResultSetHolder rsHolder = new ResultSetHolder();
			rsHolder.setDataFrame(joinedDF);
			rsHolder.setTableName(testLTableName);
			
			//check whether the number of attributes of datapod and dataframe
			//are equal if not then throw exception
			if(testLocationDP !=null) {
				if(joinedDF.columns().length != testLocationDP.getAttributes().size()) {
					throw new RuntimeException("Datapod '" + testLocationDP.getName() + "' column size(" + testLocationDP.getAttributes().size() + ") does not match with column size("+ joinedDF.columns().length +") of dataframe");
				}
				
				if(testLTableName != null && !testLTableName.isEmpty()) {
					rsHolder.getDataFrame().printSchema();
					rsHolder = applySchema(rsHolder, testLocationDP, null, testLTableName, false);
					rsHolder.getDataFrame().printSchema();
					joinedDF = rsHolder.getDataFrame();
				}
			}
			
			//write according to datasource
			if(!testLocationDs.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
				persistDataframe(rsHolder, testLocationDs, testLocationDP,SaveMode.Append.toString());
			} else {				
				joinedDF.write().mode(SaveMode.Append).parquet(tLFilePathUrl);
			}
		}else {
			joinedDF.write().mode(SaveMode.Append).parquet(defaultPath);
		}
	}

	public boolean savePredictionResult(Dataset<Row> predictedDF, Dataset<Row> featureDF
			, Dataset<Row> sourceDF, String targetPath, List<String> rowIdentifierCols
			, String includeFeatures, String[] fieldArray, String trainName
			, Datapod targetDp, Datasource targetDS, String targetTableName
			, String saveMode) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if(includeFeatures.equalsIgnoreCase("Y")) {
			rowIdentifierCols = removeDuplicateColNames(fieldArray, rowIdentifierCols);
		}
		if(rowIdentifierCols != null && !rowIdentifierCols.isEmpty()) {
			sourceDF = sourceDF.withColumn("rowNum", functions.row_number().over(Window.orderBy(sourceDF.columns()[sourceDF.columns().length-1])));
			sourceDF = sourceDF.select("rowNum", rowIdentifierCols.toArray(new String[rowIdentifierCols.size()]));
		}		
		
		featureDF = featureDF.withColumn("rowNum", functions.row_number().over(Window.orderBy(featureDF.columns()[featureDF.columns().length-1])));
		
//		sourceDF = sourceDF.na().fill(0.0,sourceDF.columns());
		if (trainName.contains("LogisticRegression")
				|| trainName.contains("GBTClassifier")) {
			predictedDF = predictedDF.withColumn("rowNum", functions.row_number().over(Window.orderBy(predictedDF.columns()[predictedDF.columns().length-1]))).select("rowNum", "features", "probability", "prediction");
		} else {
			predictedDF = predictedDF.withColumn("rowNum", functions.row_number().over(Window.orderBy(predictedDF.columns()[predictedDF.columns().length-1]))).select("rowNum", "features", "prediction");
		}
		predictedDF = featureDF.join(predictedDF, "rowNum");
		
		List<String> colNameList = new ArrayList<>();
		if(includeFeatures.equalsIgnoreCase("Y")) {
			for(String cloName : fieldArray) {
				colNameList.add(cloName);
			}
		} 
		
		
		if (trainName.contains("LogisticRegression")
				|| trainName.contains("GBTClassifier")) {
			colNameList.add("probability");
		}
		if(!trainName.contains("LDA"))
		colNameList.add("prediction");

		predictedDF = predictedDF.select("rowNum", colNameList.toArray(new String[colNameList.size()]));
		
		Dataset<Row> joinedDF = null;
		if(rowIdentifierCols != null && !rowIdentifierCols.isEmpty()) {
			joinedDF = sourceDF.join(predictedDF, "rowNum");
		} else {
			joinedDF = predictedDF;
		}
		
		//droping rowNum column
		joinedDF = joinedDF.drop("rowNum");
		
		//saving result
		if(targetDp != null) {
			ResultSetHolder rsHolder = new ResultSetHolder();
			rsHolder.setDataFrame(joinedDF);
			rsHolder.setTableName(targetTableName);
			
			//check whether the number of attributes of datapod and dataframe
			//are equal if not then throw exception
			if(targetDp !=null) {
				if(joinedDF.columns().length != targetDp.getAttributes().size()) {
					throw new RuntimeException("Datapod '" + targetDp.getName() + "' column size(" + targetDp.getAttributes().size() + ") does not match with column size("+ joinedDF.columns().length +") of dataframe");
				}
				
				if(targetTableName != null && !targetTableName.isEmpty()) {
					rsHolder = applySchema(rsHolder, targetDp, null, targetTableName, false);
					joinedDF = rsHolder.getDataFrame();
				}
			}
			
			//write according to datasource
			if(!targetDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
				persistDataframe(rsHolder, targetDS, targetDp, saveMode);
			} else {
				joinedDF.write().mode(SaveMode.Append).parquet(targetPath);
			}
			
			return true;
		} else {
			joinedDF.write().mode(SaveMode.Append).parquet(targetPath);
			return true;
		}
	}
	
	public List<String> removeDuplicateColNames(String[] fieldArray, List<String> rowIdentifierCols ) {
		if(rowIdentifierCols != null && !rowIdentifierCols.isEmpty()) {
			List<String> colNameList = Arrays.asList(fieldArray);
			List<String> uniqueRowIdentifierCols = new ArrayList<>();
			for(String colName : rowIdentifierCols) {
				if(!colNameList.contains(colName)) {
					uniqueRowIdentifierCols.add(colName);
				} 
				if(colNameList.contains(colName)) {
					logger.info("duplicate column: "+colName);
				}
			}
			return uniqueRowIdentifierCols;
		}
		return null;
	}
	
	@Override
	public boolean savePMML(Object trngModel, String trainedDSName, String pmmlLocation, String clientContext) throws IOException, JAXBException {
		logger.info("Inside savePMML");
		String sql = "SELECT * FROM " + trainedDSName;
		Dataset<Row> trainedDataSet = executeSql(sql, clientContext).getDataFrame();
		trainedDataSet.printSchema();
		PMML pmml = null;
		if(trngModel instanceof CrossValidatorModel) {
			pmml = ConverterUtil.toPMML(trainedDataSet.schema(), (PipelineModel)((CrossValidatorModel)trngModel).bestModel());
		} else if(trngModel instanceof PipelineModel) {
			pmml = ConverterUtil.toPMML(trainedDataSet.schema(), (PipelineModel)trngModel);
		}
		
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
			case "varchar": return DataTypes.StringType;
			case "varchar2": return DataTypes.StringType;
			case "timestamp": return DataTypes.TimestampType;
			case "long" : return DataTypes.LongType;
			case "binary" : return DataTypes.BinaryType;
			case "boolean" : return DataTypes.BooleanType;
			case "byte" : return DataTypes.ByteType;
			case "float" : return DataTypes.FloatType;
			case "null" : return DataTypes.NullType;
			case "short" : return DataTypes.ShortType;
			case "calendarInterval" : return DataTypes.CalendarIntervalType;
			case "decimal" : return DataTypes.createDecimalType();
			case "vector" : return new VectorUDT();
			case "array" : return DataTypes.createArrayType(DataTypes.DoubleType);
			
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
		df_1 = df_1.join(df_2, JavaConverters.asScalaBufferConverter(joinColumns).asScala());
//		df_1 = df_1.crossJoin(df_2);
		df_1.printSchema();
//		df_1.show(true);
		
		registerTempTable(df_1, joinTabName_1);
		return joinTabName_1;
	}

	@Override
	public String renameColumn(String tableName, int targetColIndex, String targetColName, String clientContext) throws IOException {
		String sql = "SELECT * FROM " + tableName;
		Dataset<Row> df = executeSql(sql, clientContext).getDataFrame();
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		
		df = df.withColumnRenamed(df.columns()[targetColIndex], targetColName);
		df.printSchema();
//		df.show(true);
		
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
	public String renameDfColumnName(String sql, String tableName, Map<String, String> mappingList, String clientContext) throws IOException {
		Dataset<Row> df = executeSql(sql, clientContext).getDataFrame();
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		/*
		 * map: key=oldColName, value=newColName
		 */
		for(Entry<String, String> entry : mappingList.entrySet()) {
			df = df.withColumnRenamed(entry.getKey(), entry.getValue());
		}
//		df.show(true);
		sparkSession.sqlContext().registerDataFrameAsTable(df, tableName);
		return tableName;
	}
	
	@Override
	public List<String> getCustomDirsFromTrainedModel(Object trngModel){
		List<String> customDirectories = new ArrayList<>();
		PipelineModel pipelineModel = null;
		if(trngModel instanceof PipelineModel) {
			pipelineModel = (PipelineModel)trngModel;
		} else if(trngModel instanceof CrossValidatorModel) {
			pipelineModel = (PipelineModel)((CrossValidatorModel)trngModel).bestModel();
		}
		Transformer[] transformers = pipelineModel.stages();
		for (int i = 0; i < transformers.length; i++) {
			customDirectories.add(i + "_" + transformers[i].uid());
		}
		return customDirectories;
	}
	
	@Override
	public Object loadTrainedModel(Class<?> modelClass, String location) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
		Object trainedModel = null;
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
		Object mlReader = null;
		if(modelClass.getName().toLowerCase().contains("ldamodel")) {
			mlReader = LocalLDAModel.read();
		} else {
			mlReader = modelClass.getMethod("read").invoke(modelClass);
		}
		logger.info("Model location : " + location);
		Object mlReader2 = mlReader.getClass().getMethod("context", SQLContext.class).invoke(mlReader, sparkSession.sqlContext());
		trainedModel = mlReader2.getClass().getMethod("load", String.class).invoke(mlReader2, location);
		//LinearRegressionModel.read().context(sparkSession.sqlContext()).load(arg0)
		return trainedModel;
	}

	@Override
	public ResultSetHolder predict2(Object trainedModel, Datapod targetDp, String filePathUrl, String tableName,
			String[] fieldArray, String trainName, String label, Datasource datasource, String clientContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException{
		String sql = "SELECT * FROM " + tableName /*+ " LIMIT 100"*/;
		Dataset<Row> df1 = executeSql(sql, clientContext).getDataFrame();
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		
		VectorAssembler va = new VectorAssembler();
		Dataset<Row> transformedDf = null;

		if (trainName.contains("LinearRegression")
				|| trainName.contains("LogisticRegression")
				|| trainName.contains("LinearSVC")
				|| trainName.contains("RandomForest")
				|| trainName.contains("AFTSurvivalRegression")
				|| trainName.contains("DecisionTree")
				|| trainName.contains("NaiveBayes")) {
			va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
			Dataset<Row> trainingTmp = va.transform(df1);
			transformedDf = trainingTmp.withColumn("label", trainingTmp.col(label).cast("Double")).select("label", "features");
			logger.info("DataFrame count: " + transformedDf.count());
		} else {
			va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
			transformedDf = va.transform(df1);
		}
		
//		transformedDf.show(false);
		@SuppressWarnings("unchecked")
		Dataset<Row> predictedDf = (Dataset<Row>) trainedModel.getClass().getMethod("transform", Dataset.class).invoke(trainedModel, transformedDf);
//		predictedDf.show(true);

		String targetTableName = null;
		if(targetDp != null)
			targetTableName = modelServiceImpl.getTableNameByObject(targetDp);
		sparkSession.sqlContext().registerDataFrameAsTable(predictedDf, "tempPredictResult");
		ResultSetHolder rsHolder = new ResultSetHolder();
		rsHolder.setType(ResultType.dataframe);
		rsHolder.setDataFrame(predictedDf);
		rsHolder.setCountRows(predictedDf.count());
		rsHolder.setTableName(targetTableName);
		String dsType = datasource.getType();
		if ((!engine.getExecEngine().equalsIgnoreCase("livy-spark")
				|| !engine.getExecEngine().equalsIgnoreCase(ExecContext.livy_spark.toString()))
				&& !dsType.equalsIgnoreCase(ExecContext.spark.toString()) 
				&& !dsType.equalsIgnoreCase(ExecContext.FILE.toString())) {
			persistDataframe(rsHolder, datasource, targetDp, null);	
		}
		
		rsHolder.setTableName("tempPredictResult");
		return rsHolder;
	}
	
	public ResultSetHolder uploadCsvToDatabase(Load load, Datasource datasource, String targetTableName, Datapod datapod) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
		ResultSetHolder rsHolder = new ResultSetHolder();
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		Dataset<Row> df = sparkSession.read()
									  .format("com.databricks.spark.csv")
									  .option("dateFormat", "dd-MM-yyyy")
									  .option("inferSchema", "false")
									  .option("header", "true")
									  .option("treatEmptyValuesAsNulls", true)
									  .option("nullValue", "0")
									  .load(load.getSource().getValue());
//		df.show(false);
		rsHolder.setDataFrame(df);
		rsHolder.setTableName(targetTableName);
//		String schema = createTableSchema(df.schema().fields(), datasource, tableName);
//		createTable(schema, datasource);
		rsHolder = persistDataframe(rsHolder, datasource, datapod, "append");
		rsHolder.setCountRows(df.count());
		return rsHolder;
	}
	
	public String createTableSchema(StructField[] fields, Datasource datasource, String tableName) {
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		StringBuilder schema = new StringBuilder();
		schema.append("CREATE TABLE IF NOT EXISTS ");
		schema.append(tableName);
		schema.append(" ( ");
		for(StructField field : fields) {
			schema.append(field.name())
			.append(" ")
			.append(exec.getDataType(field.dataType().toString()));
			schema.append(", ");
		}
		schema = new StringBuilder(schema.toString().substring(0, schema.lastIndexOf(",")));
		schema.append(" )");
		return schema.toString();
	}
	
	public String createTable(String sql, Datasource datasource) throws IOException {
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		exec.executeSql(sql, null);
		return null;
	}
	
	/**
	 * 
	 * @param nodeSql
	 * @param edgeSql
	 * @param datasource
	 * @return
	 * @throws IOException
	 */
	@Override
	public String createGraphFrame (GraphExec graphExec, DataStore dataStore) throws IOException {
		logger.info("exec inside sparkExecutor : " + graphExec.getExec());
		String []sqlList = graphExec.getExec().split("\\|\\|\\|");
		logger.info("exec0 inside sparkExecutor : " + sqlList[0]);
		logger.info("exec1 inside sparkExecutor : " + sqlList[1]);
		String nodeSql = sqlList[0];
		String edgeSql = sqlList[1];
		ResultSetHolder nodeRsHolder = executeSql(nodeSql, null);
		ResultSetHolder edgeRsHolder = executeSql(edgeSql, null);
		GraphFrame graphFrame = new GraphFrame(nodeRsHolder.getDataFrame(), edgeRsHolder.getDataFrame());
		String graphExecKey = graphExec.getDependsOn().getRef().getUuid()+"_"+graphExec.getDependsOn().getRef().getVersion()+"_"+graphExec.getVersion();
		graphpodMap.put(graphExecKey, graphFrame);
//		graphFrame.vertices().show();
//		graphFrame.edges().show();
		return graphExecKey;
	}

	@Override
	public long load(Load load, String targetTableName, Datasource datasource, Datapod datapod, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public Object trainCrossValidation(ParamMap paramMap, String[] fieldArray, String label, String trainName
			, double trainPercent, double valPercent, String tableName
			, List<com.inferyx.framework.domain.Param> hyperParamList, String clientContext
			, Map<String, Object> trainOtherParam, TrainResult trainResult, String testSetPath
			, List<String> rowIdentifierCols, String includeFeatures, String trainingDfSql, String validationDfSql,
			Map<String, EncodingType> encodingDetails, String saveTrainingSet, String trainingSetPath, Datapod testLocationDP, 
			Datasource testLocationDs, String testLocationTableName, String testLFilePathUrl, Datapod trainLocationDP, 
			Datasource trainLocationDS, String trainLocationTableName, String trainLocationFilePathUrl
			, Object algoclass) throws IOException {
//		String []origFieldArray = fieldArray;
		String assembledDFSQL = "SELECT * FROM " + tableName;
		Dataset<Row> df = executeSql(assembledDFSQL, clientContext).getDataFrame();
		
		trainResult.setTotalRecords(df.count());
		trainResult.setNumFeatures(fieldArray.length);
		
//		df.show(false);
		df.printSchema();
		try {
			Dataset<Row> trngDf = null;
			Dataset<Row> valDf = null;
			Dataset<Row> valDf2 = null;
			

			List<String> tempTableList = new ArrayList<>();
					
			if (trainName.contains("PCA")) {
				valDf2 = df;

				registerTempTable(df, "tempTrngDf");
				trngDf = readTempTable(trainingDfSql, clientContext).getDataFrame();
				trngDf = mapFieldsTypeByFeatures("tempTrngDf", trngDf, (Datasource) trainOtherParam.get("sourceDs"), (List<String>) trainOtherParam.get("vectorFields"), true, "tempTrngDf", clientContext).getDataFrame();
				trngDf = applyModelSchema(null, "SELECT * FROM "+"tempTrngDf", (Model) trainOtherParam.get("model"), null, false, clientContext).getDataFrame();
								
				//dropping temptables tempTrngDf and tempValDf
				tempTableList.add("tempTrngDf");
				
				trainResult.setTrainingSet(trngDf.count());
			} else {
				Dataset<Row>[] splits = df.randomSplit(new double[] { trainPercent / 100, valPercent / 100 }, 12345);
				trngDf = splits[0];
				valDf = splits[1];
				valDf2 = splits[1];				

				registerTempTable(trngDf, "tempTrngDf");
				trngDf = readTempTable(trainingDfSql, clientContext).getDataFrame();
				trngDf = mapFieldsTypeByFeatures("tempTrngDf", trngDf, (Datasource) trainOtherParam.get("sourceDs"), (List<String>) trainOtherParam.get("vectorFields"), true, "tempTrngDf", clientContext).getDataFrame();
				trngDf = applyModelSchema(null, "SELECT * FROM "+"tempTrngDf", (Model) trainOtherParam.get("model"), null, false, clientContext).getDataFrame();
				
				registerTempTable(valDf, "tempValDf");
				valDf = readTempTable(validationDfSql, clientContext).getDataFrame();
				valDf = mapFieldsTypeByFeatures("tempValDf", valDf, (Datasource) trainOtherParam.get("sourceDs"), (List<String>) trainOtherParam.get("vectorFields"), true, "tempValDf", clientContext).getDataFrame();
				valDf = applyModelSchema(null, "SELECT * FROM "+"tempValDf", (Model) trainOtherParam.get("model"), null, false, clientContext).getDataFrame();
								
				//dropping temptables tempTrngDf and tempValDf
				tempTableList.add("tempTrngDf");
				tempTableList.add("tempValDf");

				if(saveTrainingSet.equalsIgnoreCase("Y")) {
					//trngDf.write().mode(SaveMode.Append).parquet(trainingSetPath);
					saveTrainDataset(trngDf,trainingSetPath, trainLocationDP, trainLocationDS ,trainLocationTableName, trainLocationFilePathUrl);
				}

				trainResult.setTrainingSet(trngDf.count());
				trainResult.setValidationSet(valDf.count());
			}
			
			dropTempTable(tempTableList);	
			List<PipelineStage> pipelineStagesTrng = new ArrayList<>();
			if(encodingDetails != null && !encodingDetails.isEmpty()) {
				for(String colName : encodingDetails.keySet()) {
					encodeDataframe(colName, colName.concat("_vec"), encodingDetails.get(colName), pipelineStagesTrng);
				}
				
				//fitting training dataframe
				List<String> encoderCols = new ArrayList<>();
				for(String col : encodingDetails.keySet()) {
					encoderCols.add(col+"_vec");
				}
				for(String nonStrngCol : fieldArray) {
					if(!encodingDetails.keySet().contains(nonStrngCol)) {
						encoderCols.add(nonStrngCol);
					}
				}
				
				logger.info("Printing trained training and validation dataset schema");
				trngDf.printSchema();
				if(valDf != null) {
					valDf.printSchema();
				}
				
//				origFieldArray = fieldArray;
				fieldArray = encoderCols.toArray(new String[encoderCols.size()]);
			}									
			
			VectorAssembler vectorAssembler = new VectorAssembler();
			if (trainName.contains("PCA")) {
				vectorAssembler.setInputCols(fieldArray).setOutputCol("pcaInputFeatures");
			} else {
				vectorAssembler.setInputCols(fieldArray).setOutputCol("features");
			}
//			List<String> selectEncodedCols = new ArrayList<String>();
			
			Method method = null;
			if (trainName.contains("LinearRegression")
					|| trainName.contains("LogisticRegression")
					|| trainName.contains("LinearSVC")
					|| trainName.contains("RandomForest")
					|| trainName.contains("AFTSurvivalRegression")
					|| trainName.contains("DecisionTree")
					|| trainName.contains("NaiveBayes")) {
				method = algoclass.getClass().getMethod("setLabelCol", String.class);
				method.invoke(algoclass, "label");
				
				logger.info("Printing trained training and validation dataset schema 1");
				trngDf.printSchema();
				if(valDf != null) {
					valDf.printSchema();
				}
			} else if (trainName.contains("PCA")) {
					method = algoclass.getClass().getMethod("setInputCol", String.class);
					method.invoke(algoclass, "pcaInputFeatures");

					method = algoclass.getClass().getMethod("setOutputCol", String.class);
					method.invoke(algoclass, "features");

//					method = algoclass.getClass().getMethod("setK", Integer.class);
//					method.invoke(algoclass, 3);
					
			}

			logger.info("Printing trained training and validation dataset schema 2");
			trngDf.printSchema();
			if(valDf != null) {
				valDf.printSchema();
			}
			
			Set<String> encodingCols = null;
			if(encodingDetails != null && !encodingDetails.isEmpty()) {
				encodingCols = encodingDetails.keySet();
			}
			
			trngDf = castUnEncodedCols(trngDf, encodingDetails, encodingCols);						
			trngDf = trngDf.na().fill(0.0,trngDf.columns());
			
			if(valDf != null) {
				valDf = castUnEncodedCols(valDf, encodingDetails, encodingCols);			
				valDf = valDf.na().fill(0.0, valDf.columns());
			}

			logger.info("Printing trained training and validation dataset schema 3");
			trngDf.printSchema();
			if(valDf != null) {
				valDf.printSchema();
			}

			pipelineStagesTrng.add(vectorAssembler);
			pipelineStagesTrng.add((PipelineStage) algoclass);			
			Pipeline pipeline = new Pipeline().setStages(pipelineStagesTrng.toArray(new PipelineStage[pipelineStagesTrng.size()]));
//			Pipeline pipeline = new Pipeline().setStages(new PipelineStage[] {vectorAssembler, (PipelineStage) obj });
			
			int numFolds = 3;
			for(com.inferyx.framework.domain.Param param : hyperParamList) {
				if(param.getParamName().equalsIgnoreCase("numFolds")) {
					numFolds = Integer.parseInt(param.getParamValue().getValue());
					break;
				}
			}
			
			CrossValidator cv = new CrossValidator()
					.setEstimator(pipeline)
					.setEvaluator(getEvaluatorByTrainClass(trainName))
					.setEstimatorParamMaps(getHyperParams(hyperParamList, algoclass))
					.setNumFolds(numFolds);
			CrossValidatorModel cvModel = null;
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			
			Dataset<Row> trainedDataSet = null;
			if (trainName.contains("PCA")) {
//				if (null != paramMap) {
//					cvModel = cv.fit(trngDf, paramMap);
//				} else {
					cvModel = cv.fit(trngDf);
//				}
				
				trainedDataSet = cvModel.transform(trngDf);
				testSetPath = trainingSetPath;
				testLocationDP = trainLocationDP;
				testLocationDs = trainLocationDS;
				testLFilePathUrl = trainLocationFilePathUrl;
			} else {
//				if (null != paramMap) {
//					cvModel = cv.fit(trngDf, paramMap);
//				} else {
					cvModel = cv.fit(trngDf);
//				}	
			}		
			trainedDataSet = cvModel.transform(valDf);
			stopWatch.stop();
			trainResult.setTimeTaken(stopWatch.getTotalTimeMillis()+" ms");

			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
			if(trainOtherParam !=null) {
				String cMTableName = (String) trainOtherParam.get("confusionMatrixTableName");
				sparkSession.sqlContext().registerDataFrameAsTable(trainedDataSet, cMTableName);
			}
			sparkSession.sqlContext().registerDataFrameAsTable(trainedDataSet, "trainedDataSet");
			saveTrainedTestDataset(trainedDataSet, valDf2, testSetPath, rowIdentifierCols, includeFeatures, fieldArray, trainName,testLocationDP, testLocationDs, testLocationTableName, testLFilePathUrl);
			return cvModel;
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
		} catch (Error e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public Evaluator getEvaluatorByTrainClass(String trainName) {
		
		if(trainName.contains("RandomForestClassifier") || trainName.contains("DecisionTreeClassifier")) {
			return new  MulticlassClassificationEvaluator();
		} else if(trainName.contains("regression") || trainName.contains("Regressor")) {
			return new RegressionEvaluator();
		} else if(trainName.contains("classification") || trainName.contains("Classifier")) {
			return new BinaryClassificationEvaluator() ;
		} else if(trainName.contains("clustering")) {
			return null;//new ClusteringEvaluator();
		}
		return null;		
	}
	

	/********************** UNUSED **********************/
//	@SuppressWarnings("unchecked")
//	public ParamMap[] getHyperParamsByAlgo(String algoName, Object trainClassObject) {
//		if(algoName.contains("LinearRegression")) {
//			LinearRegression linearRegression = (LinearRegression) trainClassObject;
//			ParamMap[] paramMap = new ParamGridBuilder()
//										.addGrid(linearRegression.regParam(), new double[] {0.3, 0.1, 0.05, 0.01, 0.005})
//										.addGrid(linearRegression.elasticNetParam(), new double[] {0.1, 0.05, 0.01, 0.005, 0.001})
//										.build();
//			return paramMap;
//		} else if(algoName.contains("LogisticRegression")) {
//			LogisticRegression logisticRegression = (LogisticRegression) trainClassObject;
//			ParamMap[] paramMap = new ParamGridBuilder()
//										.addGrid(logisticRegression.regParam(), new double[] {0.3, 0.1, 0.05, 0.01, 0.005})
//										.addGrid(logisticRegression.elasticNetParam(), new double[] {0.1, 0.05, 0.01, 0.005, 0.001})
//										.build();
//			return paramMap;
//		} else if(algoName.contains("NaiveBayes")) {
//			NaiveBayes naiveBayes = (NaiveBayes) trainClassObject;
//			List<String> moadelTypesnew = new ArrayList<>();
//			moadelTypesnew.add("bernoulli");
//			moadelTypesnew.add("multinomial");
//			Seq<T> seq = (Seq<T>) JavaConverters.asScalaIteratorConverter(moadelTypesnew.iterator()).asScala().toSeq();
// 			ParamMap[] paramMap = new ParamGridBuilder()
//										.addGrid(naiveBayes.smoothing(), new double[] {0.3, 1.0, 2.5})
//										.addGrid((org.apache.spark.ml.param.Param<T>) naiveBayes.modelType(), seq)
//										.build();
//			return paramMap;
//		} else if(algoName.contains("LinearSVC") || algoName.contains("LinearSVM")) {
//			LinearSVC linearSVC = (LinearSVC) trainClassObject;
// 			ParamMap[] paramMap = new ParamGridBuilder()
//										.addGrid(linearSVC.regParam(), new double[] {0.3, 0.1, 0.05, 0.01, 0.005})
//										.addGrid(linearSVC.aggregationDepth(), new int[] {2, 3, 4})
//										.addGrid(linearSVC.threshold(), new double[] {0.5, 0.02, 1.0})
//										.build();
//			return paramMap;
//		} else if(algoName.contains("RandomForest")) {
//			RandomForestClassifier randomForest = (RandomForestClassifier) trainClassObject;
// 			ParamMap[] paramMap = new ParamGridBuilder()
//										.addGrid(randomForest.numTrees(), new int[] {15, 20, 30})
//										.addGrid(randomForest.maxDepth(), new int[] {5, 8, 10})
//										.addGrid(randomForest.minInstancesPerNode(), new int[] {1, 2, 3})
//										.addGrid(randomForest.maxBins(), new int[] {32, 35, 38})
//										.addGrid(randomForest.seed(), new long[] {159147643, 1000, 100000})
//										.build();
//			return paramMap;
//		} else if(algoName.contains("AFTSurvivalRegression")) {
//			AFTSurvivalRegression aftSurvivalRegression = (AFTSurvivalRegression) trainClassObject;
// 			ParamMap[] paramMap = new ParamGridBuilder()
//										.addGrid(aftSurvivalRegression.aggregationDepth(), new int[] {2, 4, 6})
//										.build();
//			return paramMap;
//		} else if(algoName.contains("DecisionTreeClassifier")) {
//			DecisionTreeClassifier decisionTreeClassifier = (DecisionTreeClassifier) trainClassObject;
// 			ParamMap[] paramMap = new ParamGridBuilder()
//					 					.addGrid(decisionTreeClassifier.minInfoGain(), new double[] {0.0, 0.5, 1.0})
//										.addGrid(decisionTreeClassifier.maxDepth(), new int[] {5, 8, 10})
//										.addGrid(decisionTreeClassifier.minInstancesPerNode(), new int[] {1, 2, 3})
//										.addGrid(decisionTreeClassifier.maxBins(), new int[] {32, 35, 38})
//										.addGrid(decisionTreeClassifier.seed(), new long[] {159147643, 1000, 100000})
//										.build();
//			return paramMap;
//		} else if(algoName.contains("KMeans")) {
//			KMeans kMeans = (KMeans) trainClassObject;
// 			ParamMap[] paramMap = new ParamGridBuilder()
//					 					.addGrid(kMeans.k(), new int[] {2, 5, 10})
//										.addGrid(kMeans.initSteps(), new int[] {5, 8, 10})
//										.addGrid(kMeans.seed(), new long[] {-1689246527, -1000, 100000})
//										.build();
//			return paramMap;
//		} else if(algoName.contains("LDA")) {
//			LDA lda = (LDA) trainClassObject;
// 			ParamMap[] paramMap = new ParamGridBuilder()
//					 					.addGrid(lda.k(), new int[] {10, 15, 20})
//										.addGrid(lda.learningDecay(), new double[] {0.5, 0.8, 1.0})
//										.addGrid(lda.learningOffset(), new double[] {1024.0, 1500.0, 950.0})
//										.addGrid(lda.seed(), new long[] {1435876747, 1000, 100000})
//										.build();
//			return paramMap;
//		}
//		return new ParamMap[] {};
//	}
	
	public ParamMap[] getHyperParams(List<com.inferyx.framework.domain.Param> hyperParamList, Object trainClassObject) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JsonProcessingException {
		Param<?>[] params = (Param<?>[]) trainClassObject.getClass().getMethod("params").invoke(trainClassObject);
		ParamGridBuilder paramGridBuilder = new ParamGridBuilder();
		for(com.inferyx.framework.domain.Param param : hyperParamList) {
				for(Param<?> hyperParam : params) {
					if(hyperParam.name().equalsIgnoreCase(param.getParamName())) {
						String paramValue = param.getParamValue().getValue();
						String[] splits = paramValue.split(",");
						String paramClassName = trainClassObject.getClass().getMethod(hyperParam.name()).invoke(trainClassObject).getClass().getSimpleName();
						paramGridBuilder = resolveHyperParam(trainClassObject, paramGridBuilder, paramClassName, splits, hyperParam.name());
						break;
					}
				}
		} 
		ParamMap[] paramMaps = paramGridBuilder.build();
		return paramMaps;
	}

	@SuppressWarnings("unchecked")
	private ParamGridBuilder resolveHyperParam(Object trainClassObject, ParamGridBuilder paramGridBuilder, String paramDataType, String[] splits, String paramName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		switch(paramDataType) {
			case "DoubleParam" : 
				double[] doubleParam = new double[splits.length];
				int i = 0; 
				for(String split : splits) {
					doubleParam[i] = Double.parseDouble(split.trim());
					i++;
				}
				paramGridBuilder.addGrid((DoubleParam) trainClassObject.getClass().getMethod(paramName).invoke(trainClassObject), doubleParam);
				return paramGridBuilder;
				
			case "IntParam" : 
				int[] intParam = new int[splits.length];
				int j = 0; 
				for(String split : splits) {
					intParam[j] = Integer.parseInt(split.trim());
					j++;
				}
				paramGridBuilder.addGrid((IntParam) trainClassObject.getClass().getMethod(paramName).invoke(trainClassObject), intParam);
				return paramGridBuilder;
				
			case "FloatParam" :
				float[] floatParam = new float[splits.length];
				int k = 0; 
				for(String split : splits) {
					floatParam[k] = Float.parseFloat(split.trim());
					k++;
				}
				paramGridBuilder.addGrid((FloatParam) trainClassObject.getClass().getMethod(paramName).invoke(trainClassObject), floatParam);
				return paramGridBuilder;
				
			case "LongParam" :
				long[] longParam = new long[splits.length];
				int l = 0; 
				for(String split : splits) {
					longParam[l] = Long.parseLong(split.trim());
					l++;
				}
				paramGridBuilder.addGrid((LongParam) trainClassObject.getClass().getMethod(paramName).invoke(trainClassObject), longParam);
				return paramGridBuilder;
				
			case "BooleanParam" :
				paramGridBuilder.addGrid((BooleanParam) trainClassObject.getClass().getMethod(paramName).invoke(trainClassObject));
				return paramGridBuilder;
				
			case "Param" :
				List<String> paramValues = new ArrayList<>(splits.length);
				for(String split : splits) {
					paramValues.add(split.trim());
				}
				Seq<T> seq = (Seq<T>) JavaConverters.asScalaIteratorConverter(paramValues.iterator()).asScala().toSeq();
				paramGridBuilder.addGrid((Param<T>)trainClassObject.getClass().getMethod(paramName).invoke(trainClassObject), seq);
				return paramGridBuilder;
		}
		return paramGridBuilder;
	}
	
//	@Override
//	public List<Map<String, Object>> summary(Object trndModel, String clientContext) throws IOException {
//		List<Map<String, Object>> summary = null;
//		PipelineModel pipelineModel = null;
//		if(trndModel instanceof PipelineModel) {
//			pipelineModel = (PipelineModel)trndModel;
//		} else if(trndModel instanceof CrossValidatorModel) {
//			pipelineModel = (PipelineModel)((CrossValidatorModel)trndModel).bestModel();
//		}
//		Transformer[] transformers = pipelineModel.stages();
//		for (Transformer transformer : transformers) {
//			if(transformer instanceof LinearRegressionModel) {
//				summary = linearRegressionSummay((LinearRegressionModel)transformer);
//			} else if(transformer instanceof LogisticRegressionModel) {
//				summary = logisticRegressionSummay((LogisticRegressionModel)transformer);
//			} else if(transformer instanceof KMeansModel) {
//				summary = kmeansClusteringModelSummay((KMeansModel)transformer);
//			} 
//		}
//		
//		return summary;
//	}
	
	@Override
	public Map<String, Object> summary(Object trndModel, String trainClass, List<String> summaryMethods, String clientContext) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		Map<String, Object> outPutMap = new HashMap<>();
		PipelineModel pipelineModel = null;
		if(trndModel instanceof PipelineModel) {
			pipelineModel = (PipelineModel)trndModel;
		} else if(trndModel instanceof CrossValidatorModel) {
			pipelineModel = (PipelineModel)((CrossValidatorModel)trndModel).bestModel();
		}
		Transformer[] transformers = pipelineModel.stages();
		for (Transformer transformer : transformers) {
			if(transformer instanceof org.apache.spark.ml.Model<?> && Class.forName(trainClass).isInstance(transformer)) {
				org.apache.spark.ml.Model<?> model = (org.apache.spark.ml.Model<?>)transformer;
				for(String method : summaryMethods) {
					Object result = model.getClass().getMethod(method.trim()).invoke(model);

					if(method.equalsIgnoreCase("summary")) {
						outPutMap = getSummaryFromSummaryMethod(outPutMap, result);
					}
					
					String key = method.toLowerCase();
					if(method.startsWith("get")) {
						key = method.substring(3).toLowerCase();
					}
					
					/*if(result.getClass().isArray()) {
						outPutMap.put(key, Arrays.toString((double[])result));
					}else*/ if(result instanceof Vector) {
						outPutMap.put(key, ((Vector)result).toArray());
					} else {
						outPutMap.put(key, result);						
					}
				}
			}
		}		
		logger.info("outPutMap : " + outPutMap);
		return outPutMap;
	}

	private Map<String, Object> linearRegressionSummay(Map<String, Object> outPutMap, Object result) throws JsonProcessingException {
		
		LinearRegressionTrainingSummary summary = (LinearRegressionTrainingSummary) result;
//		double[] coefficientStandardErrors = summary.coefficientStandardErrors();
		long degreesOfFreedom = summary.degreesOfFreedom();
		outPutMap.put("degreesOfFreedom", degreesOfFreedom);		
		
		double[] devianceResiduals = summary.devianceResiduals();
		outPutMap.put("devianceResiduals", devianceResiduals);		
		
		double explainedVariance = summary.explainedVariance();
		outPutMap.put("explainedVariance", explainedVariance);		
		
		double meanAbsoluteError = summary.meanAbsoluteError();
		outPutMap.put("meanAbsoluteError", meanAbsoluteError);		
		
		double meanSquaredError = summary.meanSquaredError();
		outPutMap.put("meanSquaredError", meanSquaredError);		
		
		long numInstances = summary.numInstances();
		outPutMap.put("numInstances", numInstances);		
		
		double[] objectiveHistory = summary.objectiveHistory();
		outPutMap.put("objectiveHistory", objectiveHistory);		
		
		double r2 = summary.r2();
		outPutMap.put("r2", r2);		

//<<<<<<<<<</*r2adj() method is from spark-2.3.0*/>>>>>>>>>>>>
//		double r2adj = summary.r2adj();
//		outPutMap.put("r2adj", r2adj);		
		
		Dataset<Row> residuals = summary.residuals();		
//		residuals.show(false);
		int size = (Integer.parseInt(""+residuals.count()) > 20) ? 20 : Integer.parseInt(""+residuals.count());
		Object[] residualVals = new Object[size];
		Row[] rows = (Row[]) residuals.head(size);
		int i = 0;
		for(Row row : rows) {
			residualVals[i] = row.get(0);
			i++;
		}
		outPutMap.put("residuals", residualVals);		
		
		double rootMeanSquaredError = summary.rootMeanSquaredError();
		outPutMap.put("rootMeanSquaredError", rootMeanSquaredError);		
		
		int totalIterations = summary.totalIterations();
		outPutMap.put("totalIterations", totalIterations);
		
//		double[] tValues = summary.tValues();
		return outPutMap;
	}

	private  Map<String, Object> logisticRegressionSummay(Map<String, Object> outPutMap, Object result) {
		LogisticRegressionTrainingSummary summary = (LogisticRegressionTrainingSummary) result;

		//<<<<<<<<<</*r2adj() method is from spark-2.3.0*/>>>>>>>>>>>>		
//		double accuracy = summary.accuracy();
//		outPutMap.put("accuracy", accuracy);		

		//<<<<<<<<<</*r2adj() method is from spark-2.3.0*/>>>>>>>>>>>>
//		double[] falsePositiveRateByLabel = summary.falsePositiveRateByLabel();
//		outPutMap.put("falsePositiveRateByLabel", falsePositiveRateByLabel);		

		//<<<<<<<<<</*r2adj() method is from spark-2.3.0*/>>>>>>>>>>>>
//		double[] fMeasureByLabel = summary.fMeasureByLabel();
//		outPutMap.put("fMeasureByLabel", fMeasureByLabel);		

		//<<<<<<<<<</*r2adj() method is from spark-2.3.0*/>>>>>>>>>>>>
//		double[] labels = summary.labels();
//		outPutMap.put("labels", Arrays.toString(labels));		
		
		double[] objectiveHistory = summary.objectiveHistory();
		outPutMap.put("objectiveHistory", objectiveHistory);		

		//<<<<<<<<<</*r2adj() method is from spark-2.3.0*/>>>>>>>>>>>>
//		double[] precisionByLabel = summary.precisionByLabel();
//		outPutMap.put("precisionByLabel", precisionByLabel);		

		//<<<<<<<<<</*r2adj() method is from spark-2.3.0*/>>>>>>>>>>>>
//		double[] recallByLabel = summary.recallByLabel();
//		outPutMap.put("recallByLabel", recallByLabel);		
		
		int totalIterations = summary.totalIterations();
		outPutMap.put("totalIterations", totalIterations);		

		//<<<<<<<<<</*r2adj() method is from spark-2.3.0*/>>>>>>>>>>>>
//		double[] truePositiveRateByLabel = summary.truePositiveRateByLabel();
//		outPutMap.put("truePositiveRateByLabel",  truePositiveRateByLabel);		

		//<<<<<<<<<</*r2adj() method is from spark-2.3.0*/>>>>>>>>>>>>
//		double weightedFMeasure = summary.weightedFMeasure();
//		outPutMap.put("weightedFMeasure", weightedFMeasure);		

		//<<<<<<<<<</*r2adj() method is from spark-2.3.0*/>>>>>>>>>>>>		
//		double weightedPrecision = summary.weightedPrecision();
//		outPutMap.put("weightedPrecision", weightedPrecision);		

		//<<<<<<<<<</*r2adj() method is from spark-2.3.0*/>>>>>>>>>>>>		
//		double weightedRecall = summary.weightedRecall();
//		outPutMap.put("weightedRecall", weightedRecall);		

		//<<<<<<<<<</*r2adj() method is from spark-2.3.0*/>>>>>>>>>>>>		
//		double weightedTruePositiveRate = summary.weightedTruePositiveRate();
//		outPutMap.put("weightedTruePositiveRate", weightedTruePositiveRate);		
		
		return outPutMap;
	}
	
	private  Map<String, Object> kmeansClusteringModelSummay(Map<String, Object> outPutMap, Object result) {
		KMeansSummary summary = (KMeansSummary) result;
		long[] clusterSizes = summary.clusterSizes();
		outPutMap.put("clusterSizes", clusterSizes);
		
		Dataset<Row> cluster = summary.cluster();		
//		cluster.show(false);
		int size = (Integer.parseInt(""+cluster.count()) > 20) ? 20 : Integer.parseInt(""+cluster.count());
		Object[] clusterVals = new Object[size];
		Row[] rows = (Row[]) cluster.head(size);
		int i = 0;
		for(Row row : rows) {
			clusterVals[i] = row.get(0);
			i++;
		}
		outPutMap.put("cluster", clusterVals);
		return outPutMap;
	}
	
	private Map<String, Object> getSummaryFromSummaryMethod(Map<String, Object> outPutMap, Object result) throws JsonProcessingException {
		if(result instanceof KMeansSummary) {
			outPutMap = kmeansClusteringModelSummay(outPutMap, result);
		} else if(result instanceof LogisticRegressionTrainingSummary) {
			logger.info("Inside LogisticRegressionTrainingSummary >>>>");
			outPutMap = logisticRegressionSummay(outPutMap, result);
		} else if(result instanceof LinearRegressionTrainingSummary) {
			outPutMap = linearRegressionSummay(outPutMap, result);
		}
		logger.info("outPutMap : " + outPutMap);
		return outPutMap;
	}
	

	/**
	 * 
	 * @param rowObjList
	 * @param attributes
	 * @param tableName
	 * @param clientContext
	 * @return ResultSetHolder
	 * @throws IOException 
	 */
	@Override
	public ResultSetHolder create(List<RowObj> rowObjList, List<Attribute> attributes, String tableName, String clientContext) throws IOException {
		logger.info(" Inside method create.");
		int count = 0;
		StructField[] fieldArray = new StructField[attributes.size()];
		for(Attribute attribute : attributes){	
			StructField field = new StructField(attribute.getName(), (DataType)getDataType(attribute.getType()), true, Metadata.empty());
			fieldArray[count] = field;
			count ++;
		}
		StructType schema = new StructType(fieldArray);	
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		Dataset<Row> df = sparkSession.sqlContext().createDataFrame(createRowList(rowObjList), schema);
		df.printSchema();
//		df.show(true);
		ResultSetHolder rsHolder = new ResultSetHolder();
		rsHolder.setDataFrame(df);
		rsHolder.setType(ResultType.dataframe);	
		rsHolder.setCountRows(df.count());
		rsHolder.setTableName(tableName);
		return rsHolder;
	}
	
	@Override
	public ResultSetHolder histogram(Datapod locationDatapod, String locationTableName, String sql, String key, int numBuckets, String clientContext) throws IOException {
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		
		StructType schema = null;
		if(locationDatapod != null) {
			StructField[] fieldArray = new StructField[locationDatapod.getAttributes().size()];
			schema = new StructType(fieldArray);	
		
			if(locationDatapod.getAttributes().size() > 4) {
				throw new RuntimeException("Datapod '" + locationDatapod.getName() + "' column size(" + locationDatapod.getAttributes().size() + ") must be 4");
			} else {
				int count = 0;
				for(Attribute attribute : locationDatapod.getAttributes()) {
					StructField field = new StructField(attribute.getName(), (DataType)getDataType(attribute.getType()), true, Metadata.empty());
					fieldArray[count] = field;
					count++;
				}
			}
		} else {
			StructField[] fields = new StructField[2];
			fields[0] = new StructField("bucket", DataTypes.StringType, true, Metadata.empty());
			fields[1] = new StructField("frequency", DataTypes.IntegerType, true, Metadata.empty());
			schema = new StructType(fields);
		}
		

		ResultSetHolder rsHolder = executeAndRegister(sql, "tempHistogram", clientContext);
		
		Tuple2<double[], long[]> histogramTuples = histogramUtil.fetchHistogramTuples(rsHolder.getDataFrame(), numBuckets);
		/*DoubleRDDFunctions doubleRDDFunctions = new DoubleRDDFunctions(rsHolder.getDataFrame().toJavaRDD().map(row -> row.get(0)).rdd());	
		Tuple2<double[], long[]> histogramTuples = doubleRDDFunctions.histogram(numBuckets);*/
		double[] ds = histogramTuples._1();
		long[] ls = histogramTuples._2();
		List<Row> rowList = new ArrayList<>();
		for(int i=0; i<ds.length; i++) {
			if(i<ds.length-1) {
				String bucket = ds[i]+" - "+ds[i+1];
				int frequency = (int) ls[i];
				int version = Integer.parseInt(Helper.getVersion());
				if(key != null) {
				rowList.add(RowFactory.create(key, bucket, frequency, version));
				} else {
					rowList.add(RowFactory.create(bucket, frequency));
				}
			}
		}
		
		Dataset<Row> df = sparkSession.sqlContext().createDataFrame(rowList, schema);
		df.printSchema();
//		df.show(false);
		ResultSetHolder rsHolder2 = new ResultSetHolder();
		rsHolder2.setCountRows(df.count());
		rsHolder2.setDataFrame(df);
		rsHolder2.setTableName(locationTableName);
		rsHolder2.setType(ResultType.dataframe);
		
		return rsHolder2;
	}
	
	@Override
	public ResultSetHolder mattrix(Datapod locationDatapod, String operation, String lhsTableName, String rhsTableName, 
			String lhsSql, String rhsSql, String saveTableName,
			BaseExec baseExec, Map<String, String> otherParams, RunMode runMode) throws AnalysisException, IOException {
		Dataset<Row> lhsDf = executeSql(lhsSql).getDataFrame();
		Dataset<Row> rhsDf = executeSql(rhsSql).getDataFrame();

		rhsDf = rhsDf.dropDuplicates();
		rhsDf = rhsDf.limit(2);
		
//		rhsDf.show(false);
		JavaRDD<MatrixEntry> lhsMatrixEntry = lhsDf.toJavaRDD().map(data -> {
			return new MatrixEntry(new Long(data.get(0) + ""), new Long(data.get(1) + ""), data.getDouble(2));
		});
		JavaRDD<MatrixEntry> rhsMatrixEntry = rhsDf.toJavaRDD().map(data -> {
			return new MatrixEntry(new Long(data.get(0) + ""), new Long(data.get(1) + ""), data.getDouble(2));
		});
		CoordinateMatrix lhsCoMat = new CoordinateMatrix(lhsMatrixEntry.rdd());
		CoordinateMatrix rhsCoMat = new CoordinateMatrix(rhsMatrixEntry.rdd());
		BlockMatrix resultMatrix = null;
		switch (operation) {
		case "ADD":
			resultMatrix = lhsCoMat.toBlockMatrix().add(rhsCoMat.toBlockMatrix());
			break;
		case "SUB":
			resultMatrix = lhsCoMat.toBlockMatrix().subtract(rhsCoMat.toBlockMatrix());
			break;
		case "MUL":
			resultMatrix = lhsCoMat.toBlockMatrix().multiply(rhsCoMat.toBlockMatrix());
			break;
		default:
			resultMatrix = lhsCoMat.toBlockMatrix().multiply(rhsCoMat.toBlockMatrix());
			break;
		}
		JavaRDD<Row> rowRdd = printResult(resultMatrix, baseExec, otherParams, runMode);
		// Convert Rdd to Dataframe and register Dataframe
		// Obtain list of attr names from locationDatapod
		String[] columns = new String[locationDatapod.getAttributes().size()];
		int count = 0;
		for (Attribute attr : locationDatapod.getAttributes()) {
			columns[count] = attr.getName();
			count++;
		}
//		List<Object> tableColumns = Arrays.asList(columns);
		StructType schema = createSchema(locationDatapod.getAttributes());
		
		// register temp table		
		return createAndRegisterDataset(rowRdd, schema, saveTableName+"_df");
	}
	
	/**
	 * 
	 * @param resultMatrix
	 * @param baseExec
	 * @param otherParams
	 * @param runMode
	 * @param exec
	 * @return
	 */
	private JavaRDD<Row> printResult(BlockMatrix resultMatrix, BaseExec baseExec, Map<String, String> otherParams, RunMode runMode) {
		resultMatrix.toCoordinateMatrix().toIndexedRowMatrix().rows().toJavaRDD().collect().forEach(t -> logger.info(t.vector()));
		JavaRDD<Row> rowRdd = matrixToRddConverter.convertToRows(resultMatrix);
		return rowRdd;
	}
	
	/**
	 * 
	 * @param tableColumns
	 * @return
	 */
	public StructType createSchema(List<Attribute> attributes){
        List<StructField> fields  = new ArrayList<StructField>();
        for(Attribute attr  : attributes){     
                fields.add(DataTypes.createStructField(attr.getName(),(DataType)getDataType(attr.getType()), true));  
        }
        return DataTypes.createStructType(fields);
    }

	/**
	 * 
	 * @Ganesh
	 * 
	 * @param targetDatapod
	 * @param datasource
	 * @param sourceTableName
	 * @return List<CompareMetaData>
	 */
	@Override
	public List<CompareMetaData> compareMetadata(Datapod targetDatapod, Datasource datasource, String sourceTableName) throws IOException {
		Map<String, CompareMetaData> comparisonResultMap = new LinkedHashMap<>();
		if(sourceTableName != null) {
			String sql = "SELECT * FROM " + sourceTableName + " LIMIT 2";
			Dataset<Row> df = executeSql(sql).getDataFrame(); 
			Tuple2<String, String>[] dTypes = df.dtypes();
			
			List<String> sourceAttrList = Arrays.asList(df.columns());
			List<String> targetAttrList = new ArrayList<>();
			for(Attribute attribute : targetDatapod.getAttributes()) {
				targetAttrList.add(attribute.getName());
			}
			
			for(Attribute attribute : targetDatapod.getAttributes()) {
				for(Tuple2<String, String> dType : dTypes) {						
					comparisonResultMap = compareAttr(comparisonResultMap, attribute, dType, sourceAttrList, targetAttrList);					
				}
			}
		} else {
			for(Attribute attribute : targetDatapod.getAttributes()) {
				CompareMetaData comparison = new CompareMetaData();
				comparison.setSourceAttribute("");
				comparison.setSourceLength("");
				comparison.setSourceType("");
				
				comparison.setTargetAttribute(attribute.getName());
				comparison.setTargetLength(attribute.getLength() != null ? attribute.getLength().toString() : "");
				comparison.setTargetType(attribute.getType());
				
				comparison.setStatus("");	
				comparisonResultMap.put(attribute.getName(), comparison);
			}
		}
		return Arrays.asList(comparisonResultMap.values().toArray(new CompareMetaData[comparisonResultMap.values().size()]));
	}
	
	public Map<String, CompareMetaData> compareAttr(Map<String, CompareMetaData> comparisonResultMap, Attribute attribute, Tuple2<String, String> dType, List<String> sourceAttrList, List<String> targetAttrList) {
		CompareMetaData comparison = new CompareMetaData();
		String attrLength = attribute.getLength() != null ? attribute.getLength().toString() : "";
		
		String dataType = dType._2().toLowerCase();
		if(dataType.contains("type")) {
			dataType = dataType.replaceAll("type", "");
		} 
		
		String sourceAttrLength = null;
		if(dataType.contains("(")) {
			sourceAttrLength = dataType.substring(dataType.indexOf("("));
			dataType = dataType.substring(0, dataType.indexOf("("));
		}
		
		if(attribute.getName().equalsIgnoreCase(dType._1())) {	
			String status = null;
			if(dataType.toLowerCase().contains(attribute.getType().toLowerCase())) {
				status = Compare.NOCHANGE.toString();
			} else {
				status = Compare.MODIFIED.toString();
			}
//			if(attribute.getLength() != null && !attribute.getLength().toString().equalsIgnoreCase("")){
//				status = Compare.MODIFIED.toString();
//			}			
			
			comparison.setSourceAttribute(dType._1());
			comparison.setSourceLength(sourceAttrLength != null ? sourceAttrLength : "");
			comparison.setSourceType(dataType);
			
			comparison.setTargetAttribute(attribute.getName());
			comparison.setTargetLength(attrLength);
			comparison.setTargetType(attribute.getType());
			
			comparison.setStatus(status);
			comparisonResultMap.put(attribute.getName(), comparison);
		} else if(!sourceAttrList.contains(attribute.getName())) {
			comparison.setSourceAttribute("");
			comparison.setSourceLength("");
			comparison.setSourceType("");
			
			comparison.setTargetAttribute(attribute.getName());
			comparison.setTargetLength(attrLength);
			comparison.setTargetType(attribute.getType());
			
			comparison.setStatus(Compare.NEW.toString());
			comparisonResultMap.put(attribute.getName(), comparison);
		} else if(!targetAttrList.contains(dType._1())) {
			comparison.setSourceAttribute(dType._1());
			comparison.setSourceLength(sourceAttrLength != null ? sourceAttrLength : "");
			comparison.setSourceType(dataType);
			
			comparison.setTargetAttribute("");
			comparison.setTargetLength("");
			comparison.setTargetType("");
			
			comparison.setStatus(Compare.DELETED.toString());
			comparisonResultMap.put(dType._1(), comparison);
		}
		return comparisonResultMap;
	}
	
	public ResultSetHolder readAndRegisterFile(String tableName, List<String> filePath, String format, String header, String clientContext, boolean registerTempTable) throws IOException {		
		logger.info("Inside readAndRegisterFile....");
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		
		//reading file
		SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
		Dataset<Row> df = null;
		if(format == null) {
			RemoteIterator<LocatedFileStatus> fileStatus = FileSystem.get(sparkSession.sparkContext().hadoopConfiguration()).listFiles(new Path(filePath.get(0)), false);
			
			while(fileStatus.hasNext()) {
				LocatedFileStatus status = fileStatus.next();
				String fileName2 = status.getPath().getName();
				String filePath2 = new String(filePath.get(0));
				filePath2 = filePath2+"/"+fileName2;
				List<String> filePaths = new ArrayList<>();
				if(!fileName2.contains("_SUCCESS")) {
					filePaths.add(filePath2);
				}
				df = sparkSession.read().option("header", "true").load(filePaths.toArray(new String[filePaths.size()]));			
			}
		} else if(!format.equalsIgnoreCase(FileType.PARQUET.toString())) {
			df = sparkSession.read().format("csv").option("delimiter", format).option("header", header).load(filePath.toArray(new String[filePath.size()]));
		} else {
			df = sparkSession.read().parquet(filePath.toArray(new String[filePath.size()]));
		}
		
		//creating rsHolder
		ResultSetHolder rsHolder = new ResultSetHolder();
		rsHolder.setDataFrame(df);
		rsHolder.setCountRows(df.count());
		rsHolder.setType(ResultType.dataframe);
		rsHolder.setTableName(tableName);
		
		//registering temp table
		if(registerTempTable) {
			sparkSession.sqlContext().registerDataFrameAsTable(rsHolder.getDataFrame(), tableName);
		}
		return rsHolder;
	}
	
	public ResultSetHolder registerAndPersistDataframe(ResultSetHolder rsHolder, Datapod datapod, String saveMode, String filePathUrl, String tableName, String header, boolean registerTempTable) throws IOException {
		logger.info("inside method registerAndPersistDataframe");

		Dataset<Row> df = rsHolder.getDataFrame();
//		df.show(true);
		if(datapod !=null) {
			if(df.columns().length != datapod.getAttributes().size())
				throw new RuntimeException("Datapod '" + datapod.getName() + "' column size(" + datapod.getAttributes().size() + ") does not match with column size("+ df.columns().length +") of dataframe");
			
			List<Attribute> attributes = datapod.getAttributes();
			for(Attribute attribute : attributes){
				df = df.withColumn(attribute.getName(), df.col(attribute.getName()).cast((DataType)getDataType(attribute.getType())));
			} 				
		} 
		df.printSchema();
//		df.show(true);
		df.coalesce(1).write().mode(saveMode).option("header", header).parquet(filePathUrl);
		
		if(registerTempTable) {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			sparkSession.sqlContext().registerDataFrameAsTable(rsHolder.getDataFrame(), tableName);
		}
		return rsHolder;
	}
	
	public ResultSetHolder applySchema(ResultSetHolder rsHolder, Datapod datapod, String[] targetColList, String tableName, boolean registerTempTable) throws IOException {

		logger.info("inside method applySchema");
		Dataset<Row> df = rsHolder.getDataFrame();
     	df.show(true);
	   df.printSchema();
		String[] dfColumns = df.columns();
		if(datapod != null && df.columns().length != datapod.getAttributes().size()) {
			throw new RuntimeException("Datapod '" + datapod.getName() + "' column size(" + datapod.getAttributes().size() + ") does not match with column size("+ df.columns().length +") of dataframe");
		}
		
		int i = 0;
		if(datapod != null) {
			List<Attribute> attributes = datapod.getAttributes();
			for(Attribute attribute : attributes){
				if ((dfColumns.length - 1) < i) {
					break;
				}
				df = df.withColumnRenamed(dfColumns[i], attribute.getName());
				df = df.withColumn(attribute.getName(), df.col(attribute.getName()).cast((DataType)getDataType(attribute.getType())));
				i++;
			} 
		} else {
			for(String colName : targetColList){
				if ((dfColumns.length - 1) < i) {
					break;
				}
				df = df.withColumnRenamed(dfColumns[i], colName);
				i++;
			}
		}				
	 
		if(registerTempTable) {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			sparkSession.sqlContext().registerDataFrameAsTable(df, tableName);
		}
		rsHolder.setDataFrame(df);
		return rsHolder;
	}
	
	public ResultSetHolder applyIngestDatapodSchema(ResultSetHolder rsHolder, Datapod datapod, String[] targetColList, String tableName, boolean registerTempTable) throws IOException {
		//special method for ingest target/source datapod 
		logger.info("inside method applyIngestDatapodSchema");
		Dataset<Row> df = rsHolder.getDataFrame();

		String[] dfColumns = df.columns();
				
		int i = 0;
		if(datapod != null) {
			List<Attribute> attributes = datapod.getAttributes();
			for(Attribute attribute : attributes){
				if ((dfColumns.length - 1) < i) {
					break;
				}
				df = df.withColumnRenamed(dfColumns[i], attribute.getName());
				df = df.withColumn(attribute.getName(), df.col(attribute.getName()).cast((DataType)getDataType(attribute.getType())));
				i++;
			} 
		} else {
			for(String colName : targetColList){
				if ((dfColumns.length - 1) < i) {
					break;
				}
				df = df.withColumnRenamed(dfColumns[i], colName);
				i++;
			}
		}				
	 
		if(registerTempTable) {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			sparkSession.sqlContext().registerDataFrameAsTable(df, tableName);
		}
		rsHolder.setDataFrame(df);
		return rsHolder;
	}
	
	public ResultSetHolder writeFileByFormat(ResultSetHolder rsHolder, Datapod datapod, String targetPath, String tableName, String saveMode, String fileFormat, String header) throws IOException {

		logger.info("inside method writeFileByFormat");
		Dataset<Row> df = rsHolder.getDataFrame();
		
		if(datapod != null && df.columns().length != datapod.getAttributes().size()) {
			throw new RuntimeException("Datapod '" + datapod.getName() + "' column size(" + datapod.getAttributes().size() + ") does not match with column size("+ df.columns().length +") of dataframe");
		}
		
		if(fileFormat == null) {
			df.coalesce(1).write().mode(saveMode).format("csv").option("delimiter", ",").option("header", header).csv(targetPath);
		} else if(fileFormat.equalsIgnoreCase(FileType.CSV.toString())) {
			df.coalesce(1).write().mode(saveMode).option("delimiter", ",").option("header", header).csv(targetPath);
		} else if(fileFormat.equalsIgnoreCase(FileType.TSV.toString())) {
			df.coalesce(1).write().mode(saveMode).option("delimiter", "\t").option("header", header).csv(targetPath);
		} else if(fileFormat.equalsIgnoreCase(FileType.PSV.toString())) {
			df.coalesce(1).write().mode(saveMode).option("delimiter", "|").option("header", header).csv(targetPath);
		} else if(fileFormat.equalsIgnoreCase(FileType.PARQUET.toString())) {
			rsHolder = registerAndPersistDataframe(rsHolder, datapod, "append", targetPath, tableName, header, false);
		}
		return rsHolder;
	}
	
	public List<Map<String, Object>> fetchIngestResult(Datapod datapod, String tableName, String filePath, String format, String header, int rowLimit, String clientContext) throws IOException {

		logger.info("inside method fetchIngestResult");
		List<Map<String, Object>> data = new ArrayList<>();
		List<String> location = new ArrayList<>();
		location.add(filePath);
		Dataset<Row> df = readAndRegisterFile(tableName, location, format, header, clientContext, false).getDataFrame();

		String[] columns = df.columns();
		List<Attribute> attributes = null;
		if(datapod != null) {
			attributes = datapod.getAttributes();
		}
		
//		df.show(false);
		Row [] rows = (Row[]) df.head(rowLimit);
		for (Row row : rows) {
			int i = 0;
			Map<String, Object> object = new LinkedHashMap<String, Object>(columns.length);
			for (String column : columns) {
				object.put(attributes != null ? attributes.get(i).getName() : column, (row.getAs(column) == null ? "" :
					(row.getAs(column) instanceof Vector) ? Arrays.toString((double[])((Vector)row.getAs(column)).toArray()) : row.getAs(column)));
				i++;
			}
			data.add(object);
		}
		return data;
	}
	
	public ResultSetHolder addVersionColToDf(ResultSetHolder rsHolder, String tableName, String version) throws IOException {

		logger.info("inside method addVersionColToDf");
		String[] columns = rsHolder.getDataFrame().columns();
		List<String> columnList = Arrays.asList(columns);
		if(!columnList.contains("version")) {
			String sql = "SELECT *, "+version+" AS version FROM "+tableName;
			ResultSetHolder rsHolder2 = executeSql(sql);
			rsHolder.setDataFrame(rsHolder2.getDataFrame());
		} 	
		return rsHolder;
	}

	@Override
	public ResultSetHolder executeSqlByDatasource(String sql, Datasource datasource, String clientContext)
			throws IOException {
		logger.info(" Inside spark executor  for SQL : " + sql);
		ResultSetHolder rsHolder = new ResultSetHolder();
		try {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			Dataset<Row> df = null;
			if (datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
					|| datasource.getType().toLowerCase().equalsIgnoreCase(ExecContext.FILE.toString())
					|| datasource.getType().toLowerCase().equalsIgnoreCase(ExecContext.HIVE.toString())) {
				for (String sessionParam : commonServiceImpl.getAllDSSessionParams()) {
					sparkSession.sql("SET "+sessionParam);
				}
				df = sparkSession.sql(sql);
			} else if (datasource.getType().equalsIgnoreCase(ExecContext.IMPALA.toString())) {
				df = sparkSession.sqlContext().read().format("jdbc")
						.option("spark.driver.extraClassPath", datasource.getDriver())
						.option("spark.executor.extraClassPath", datasource.getDriver())
						.option("driver", datasource.getDriver())
						.option("url", Helper.genUrlByDatasource(datasource))
						.option("user", datasource.getUsername())
						.option("password", datasource.getPassword())
						.option("dbtable", "(" + sql + ") as impala_table").load();
			} else if (datasource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString())) {
				df = sparkSession.sqlContext().read().format("jdbc")
						.option("spark.driver.extraClassPath", datasource.getDriver())
						.option("spark.executor.extraClassPath", datasource.getDriver())
						.option("driver", datasource.getDriver())
						.option("url", Helper.genUrlByDatasource(datasource))
						.option("user", datasource.getUsername())
						.option("password", datasource.getPassword())
						.option("dbtable", "(" + sql + ") as mysql_table").load();
			} else if (datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
				df = sparkSession.sqlContext().read().format("jdbc")
						.option("driver", datasource.getDriver())
						.option("url", Helper.genUrlByDatasource(datasource))
						.option("user", datasource.getUsername())
						.option("password", datasource.getPassword())
						.option("dbtable", "(" + sql + ")  oracle_table").load();
			} else if (datasource.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) {				
				df = sparkSession.sqlContext().read().format("jdbc")
						.option("driver", datasource.getDriver())
						.option("url", Helper.genUrlByDatasource(datasource))
						.option("lazyInit", "true")
						.option("user", datasource.getUsername())
						.option("password", datasource.getPassword())
						.option("dbtable", "(" + sql + ") as postgres_table").load();
			}
//			df.show(true);
			rsHolder.setCountRows(df.count());
			rsHolder.setDataFrame(df);
			rsHolder.setType(ResultType.dataframe);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException e) {			
			e.printStackTrace();
			throw new RuntimeException(e);
		}  catch (Exception e) {				
			e.printStackTrace();
			throw new RuntimeException(e);
		}	
		return rsHolder;
	}

	@Override
	public String getIncrementalLastValue(ResultSetHolder rsHolder, String clientContext) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
//	public ResultSetHolder registerAndPersistDataframeTOHDFS(ResultSetHolder rsHolder, Datapod datapod, String saveMode, String filePathUrl, String tableName, boolean registerTempTable) throws IOException {
//		
//		rsHolder.getDataFrame().write().sa
//		return rsHolder;
//	}
	
	public ResultSetHolder writeResult(String sql, ResultSetHolder rsHolder, String filePathUrl, Datapod datapod, String saveMode, String tableName, String header, String clientContext) throws IOException {
		if(rsHolder == null && sql == null) {
			throw new RuntimeException("Please provide source(sql or ResultSetHolder) source to presist.");
		} else if(sql != null && !sql.isEmpty()) {
			rsHolder = executeSql(sql, clientContext);
			rsHolder.setTableName(tableName);
		} 
		rsHolder = registerAndPersistDataframe(rsHolder, datapod, saveMode, filePathUrl, tableName, header, true);
		return rsHolder;
	}
	
	public ResultSetHolder mapSchema(ResultSetHolder rsHolder, String query, List<String> columnList, String tableName, boolean registerTempTable) throws IOException {
		logger.info("inside method mapSchema");
		Dataset<Row> df = null;
		if(query != null) {
			rsHolder = executeSql(query, null);
		} else {
			df = rsHolder.getDataFrame();
			df.printSchema();
			
			List<Column> columns = new ArrayList<>();
			for(String colName : columnList) {
				columns.add(new Column(colName));
			}
			
			df = df.select(columns.toArray(new Column[columns.size()]));
			rsHolder.setDataFrame(df);
		}		
	 
		if(registerTempTable) {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			sparkSession.sqlContext().registerDataFrameAsTable(df, tableName);
		}
		return rsHolder;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Map<String, Object>  calculateConfusionMatrixAndRoc(Map<String, Object> summary, String tableName, String clientContext) throws IOException{
		logger.info("Calculating confusion metrics and roc");
		String assembledDFSQL = "SELECT * FROM " + tableName;
		Dataset<Row> trainedDataSet = executeSql(assembledDFSQL, clientContext).getDataFrame();
		trainedDataSet.printSchema();
		
//		trainedDataSet = trainedDataSet.na().fill(0.0,trainedDataSet.columns());
		
		List<String> predDFCols = Arrays.asList(trainedDataSet.columns());
		if(predDFCols.contains("prediction")) {
			MulticlassMetrics metrics = new MulticlassMetrics(trainedDataSet.map((MapFunction<Row, Row>) row -> 
			RowFactory.create( 
					Double.parseDouble(""+row.get(row.fieldIndex("prediction"))),
					Double.parseDouble(""+row.get(row.fieldIndex("label")))), 
					Encoders.kryo(Row.class)));

			Matrix confusion = metrics.confusionMatrix();	
			
			
			int size = metrics.confusionMatrix().numCols();
			if (size < 2) {
				size = 2;
			}
			double[] matrixArray = metrics.confusionMatrix().toArray();
			int matArrLen = matrixArray.length;
			double[][] matrix = new double[size][size];
			// set values of matrix into a 2D array
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (((j * size) + i) > matArrLen-1) {
						matrix[i][j] = 0.0;
					} else {
						matrix[i][j] = matrixArray[(j * size) + i];
					}
				}
			}
			System.out.println("Confusion matrix: \n" + confusion);
			summary.put("confusionMatrix", matrix);
			summary.put("accuracy",metrics.accuracy());
			
			
			// Stats by labels
			/*for (int i = 0; i < metrics.labels().length; i++) {
			summary.put("precision",metrics.precision(metrics.labels()[i]));
			System.out.format("Class %f precision = %f\n", metrics.labels()[i],metrics.precision(
			metrics.labels()[i]));
			summary.put("accuracy",metrics.recall(metrics.labels()[i]));
			System.out.format("Class %f recall = %f\n", metrics.labels()[i], metrics.recall(
			metrics.labels()[i]));
			summary.put("recall",metrics.accuracy());
			System.out.format("Class %f F1 score = %f\n", metrics.labels()[i], metrics.fMeasure(
			metrics.labels()[i]));
			summary.put("f1Score", metrics.fMeasure(metrics.labels()[i]));
			
			}*/
			summary.put("precision",metrics.precision());
			summary.put("accuracy",metrics.accuracy());
			summary.put("recall",metrics.recall());
			summary.put("f1Score",metrics.fMeasure());
			
			//Weighted stats
			System.out.format("Weighted precision = %f\n", metrics.weightedPrecision());
			System.out.format("Weighted recall = %f\n", metrics.weightedRecall());
			System.out.format("Weighted F1 score = %f\n", metrics.weightedFMeasure());
			System.out.format("Weighted false positive rate = %f\n", metrics.weightedFalsePositiveRate());
		} 
		
	    //For Roc
		BinaryClassificationMetrics binaryClassificationMetrics = null;
		if(Arrays.asList(trainedDataSet.columns()).contains("prediction")) {
		    binaryClassificationMetrics = new BinaryClassificationMetrics(trainedDataSet.map((MapFunction<Row, Row>) row -> 
			RowFactory.create(Double.parseDouble(""+row.get(row.fieldIndex("label"))), 
					Double.parseDouble(""+row.get(row.fieldIndex("prediction")))), 
					Encoders.kryo(Row.class)));
		    System.out.println("Roc: \n" + binaryClassificationMetrics.roc().toJavaRDD().collect());
			System.out.println("Area Roc: \n" + binaryClassificationMetrics.areaUnderROC());
			
			double TP = trainedDataSet.select("label", "prediction").filter("label = prediction and prediction > 0").count();
			double TN = trainedDataSet.select("label", "prediction").filter("label = prediction and prediction = 0").count();
			double FP = trainedDataSet.select("label", "prediction").filter("label <> prediction and prediction > 0").count();
			double FN = trainedDataSet.select("label", "prediction").filter("label <> prediction and prediction = 0").count();
			
//			double[] confMat = {TP, FN, FP, TN};
			double[][] confusionMatrix = {{TP, FN}, {FP, TN}};
			System.out.println("Confusion matrix: \n" + confusionMatrix);
			summary.put("confusionMatrix", confusionMatrix);
			
			List<Map<String, Object>> rocList = new ArrayList<>();
//			List<Map<String, Object>> rocList = new ArrayList<>();
//			rocList = sparkExecHelper.getRoc(binaryClassificationMetrics.roc().toJavaRDD());
			for(Tuple2<?, ?> tuple2 : binaryClassificationMetrics.roc().toJavaRDD().collect()) {
				logger.info("{\"specificity\":"+tuple2._1()+", \"sensitivity\":"+tuple2._2()+"}");
				Map<String, Object> rocMap = new LinkedHashMap<>();
				rocMap.put("specificity", tuple2._1());
				rocMap.put("sensitivity", tuple2._2());
				rocList.add(rocMap);
			}
			if(!rocList.isEmpty()) {
				summary.put("roc", rocList);
			}
			
			// AUROC 
			List<Double> auRoc = new ArrayList<>();
			auRoc.add(binaryClassificationMetrics.areaUnderROC());
			summary.put("auroc", auRoc);
		}
				
		// AUPRC
	//	System.out.println("Area under precision-recall curve = " + binaryClassificationMetrics.areaUnderPR());
		return summary ;
	}

	@Override
	public PipelineModel trainDL(ExecParams execParams, String[] fieldArray, String label, String trainName,
			double trainPercent, double valPercent, String tableName, String clientContext, Object algoClass,
			Map<String, Object> trainOtherParam) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSetHolder createAndRegisterDataset(List<Row> rowRDD, StructType schema, String tableName)
			throws AnalysisException {
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		SparkSession sparkSession = null;
		try {
			sparkSession = (SparkSession) connector.getConnection().getStmtObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Dataset<Row> dataset = sparkSession.createDataFrame(rowRDD, schema);
		for (String col : dataset.columns()) {
			dataset = dataset.withColumn(col, dataset.col(col).cast(DataTypes.DoubleType));
		}
		dataset.createOrReplaceTempView(tableName);
		ResultSetHolder rsHolder = new ResultSetHolder();
		rsHolder.setCountRows(dataset.count());
		rsHolder.setDataFrame(dataset);
		rsHolder.setTableName(tableName);
		rsHolder.setType(ResultType.dataframe);
		return rsHolder;
	}
	
	public boolean dropTempTable(List<String> tempTableList) {
		try {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			for(String tempTableName : tempTableList) {
				sparkSession.sqlContext().dropTempTable(tempTableName);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<Map<String, Object>> fetchTrainOrTestSet(String location) throws IOException {
		List<Map<String, Object>> data = new ArrayList<>();
		
		IConnector conn = connFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = conn.getConnection();

		SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
		DataFrameReader reader = sparkSession.read();
		Dataset<Row> df = reader.load(location);

		String[] columns = df.columns();
		Row [] rows = (Row[]) df.head(Integer.parseInt(""+df.count()));
		for (Row row : rows) {
			Map<String, Object> object = new LinkedHashMap<String, Object>(columns.length);
			for (String column : columns) {
				object.put(column, (row.getAs(column) == null ? "" :
					(row.getAs(column) instanceof Vector) ? Arrays.toString((double[])((Vector)row.getAs(column)).toArray()) : row.getAs(column)));
			}
			data.add(object);
		}

		return data;
	}	

	@Override
	public LinkedHashMap<String, Object> getImputeValue(ResultSetHolder rsHolder) throws Exception{
		LinkedHashMap<String, Object> resolvedAttrImputeValues = new LinkedHashMap<>();
		Dataset<Row> df = rsHolder.getDataFrame();	

		Row[] rows = (Row[]) df.head(Integer.parseInt(""+df.count()));
		for (Row row : rows) {
			resolvedAttrImputeValues.put((String) row.get(0), row.get(1));
		}
		return resolvedAttrImputeValues;
	}

	@Override
	public ResultSetHolder applyAttrImputeValuesToData(ResultSetHolder rsHolder, LinkedHashMap<String, Object> imputeAttributeNameWithValues, boolean registerTempTable, String tempTableName) throws IOException {
		Dataset<Row> df = rsHolder.getDataFrame();	
		
//		df.show(Integer.parseInt(""+df.count()), false);
		df = df.na().fill(imputeAttributeNameWithValues);
//		df.show(Integer.parseInt(""+df.count()), false);
		
		if(registerTempTable) {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			sparkSession.sqlContext().registerDataFrameAsTable(df, tempTableName);
		}
		rsHolder.setDataFrame(df);
		return rsHolder;
	}
	
	@SuppressWarnings("null")
	public List<Map<String, Object>> corelationMatrix(ResultSetHolder rsHolder) throws IOException {

		Dataset<Row> df = castDFCloumnsToDoubleType(rsHolder);
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();

		List<Row> dynamicList = createRowListdense(df.collectAsList(), df.columns().length);
		/*
		 * List<Row> list = Arrays.asList( RowFactory.create(Vectors.sparse(3, new int[]
		 * { 0,2 }, new double[] { 0.0, 1.0 })), RowFactory.create(Vectors.dense(4.0,
		 * 5.0, 0.0)), RowFactory.create(Vectors.dense(5.0, 5.0, 0.0)),
		 * RowFactory.create(Vectors.dense(6.0, 5.0, 0.0)),
		 * RowFactory.create(Vectors.dense(7.0, 7.0, 0.0)),
		 * RowFactory.create(Vectors.sparse(3, new int[] { 0, 2 }, new double[] { 0.0,
		 * 1.0 })) );
		 * 
		 * 
		 * 
		 */
		StructType schema = new StructType(
				new StructField[] { new StructField("features", new VectorUDT(), false, Metadata.empty()), });

		Dataset<Row> df1 = sparkSession.createDataFrame(dynamicList, schema);
		Row r1 = Correlation.corr(df1, "features").head();
		Row r2 = Correlation.corr(df1, "features", "spearman").head();
		System.out.println("spearman correlation matrix:\n" + r2.get(0).toString());
		List<Row> dynamic = new ArrayList<Row>();
		dynamic.add(r1);
		System.out.println("Pearson correlation matrix:\n" + r1.get(0).toString());
		Dataset<Row> corelationMdf = sparkSession.createDataFrame(dynamic, r1.schema());
     	return ConvertDfToSample(corelationMdf);
	}
	
	private List<Row> createRowListdense(List<Row> rowObjList, int a) {
		List<Row> rowList = new ArrayList<>();
		if (rowObjList == null || rowObjList.isEmpty()) {
			return null;
		}
		int count = 0;
		rowList.add(RowFactory.create(Vectors.sparse(a, new int[] { 0, a - 1 }, new double[] { 1.0, -2.0 })));

		double value;

		for (Row rowObj : rowObjList) {
			/*
			 * //Object[] d=rowObj.getRowData();
			 * 
			 * double[] rr = ((Vector) rowObj).toArray(); for (double node : rr ){
			 * System.out.println(node); //rowObj.getDouble(arg0) } rowObj.schema();
			 * 
			 * System.out.println((double)((Object)rowObj.getDouble(count))); double r =
			 * (double)((Object)rowObj.getDouble(count));
			 */
			double[] doubleArray = new double[rowObjList.get(0).size()];
			int count2 = 0;
			for (int i = 0; i < rowObj.size(); i++) {
				value = rowObj.getDouble(count2);
				doubleArray[i] = value;
				count2++;
			}

			rowList.add(RowFactory.create(Vectors.dense(doubleArray)));
			count++;
		}
		rowList.add(RowFactory.create(Vectors.sparse(a, new int[] { 0, a - 1 }, new double[] { 9.0, 1.0 })));

		return rowList;
	}	
	
	public Dataset<Row> castDFCloumnsToDoubleType(ResultSetHolder rsHolder) throws IOException {
		if(rsHolder == null || rsHolder.getDataFrame() == null) {
			return null;
		} 
		Dataset<Row> df = rsHolder.getDataFrame();
		
		for(String colName : df.columns()) {
			df = df.withColumn(colName, df.col(colName).cast(DataTypes.DoubleType));
		}
		return df;
	}
	
	
	public List<Map<String, Object>> ConvertDfToSample(Dataset<Row> df) throws IOException {
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		List<Map<String, Object>> data = new ArrayList<>();
		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession) {
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			Row[] rows = (Row[]) df.head(Integer.parseInt("" + df.count()));
			String[] columns = df.columns();
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
}

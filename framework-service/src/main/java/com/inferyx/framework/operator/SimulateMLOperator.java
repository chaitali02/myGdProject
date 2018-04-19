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
package com.inferyx.framework.operator;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.distribution.MultiNormalDist;
import com.inferyx.framework.distribution.MultivariateMapFunction;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.FeatureRefHolder;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.writer.IWriter;

/**
 * @author joy
 *
 */
public class SimulateMLOperator implements Serializable {

	@Autowired
	private SparkSession sparkSession;
	@Autowired
	private PredictMLOperator predictMLOperator;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private FormulaOperator formulaOperator;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private MetadataUtil daoRegister;
	@Autowired
	private DataSourceFactory datasourceFactory;
	@Autowired
	private MultiNormalDist multiNormalDist;
	
	static final Logger LOGGER = Logger.getLogger(SimulateMLOperator.class);
	
	/**
	 * 
	 */
	public SimulateMLOperator() {
		// TODO Auto-generated constructor stub
	}
	
	public Object execute(Simulate simulate, Model model, Algorithm algorithm, Datapod targetDp, TrainExec latestTrainExec, String[] fieldArray, String targetType,
			String tableName, String filePathUrl, String filePath, Dataset<Row> assembledDf, String clientContext) throws Exception {
		return predictMLOperator.execute(null, model, algorithm, targetDp, assembledDf, fieldArray, latestTrainExec, targetType, tableName, filePathUrl, filePath, clientContext);
	}
	
	public String execute(String sql, String filePathUrl, String filePath, String uuid)
			throws Exception {
		
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			ResultSetHolder rsHolder = exec.executeSql(sql);
			Dataset<Row> resultDf = rsHolder.getDataFrame();
			resultDf.printSchema();
			resultDf.show();
			IWriter datapodWriter = datasourceFactory.getDatapodWriter(null, daoRegister);
			datapodWriter.write(resultDf, filePathUrl + "/data", null, SaveMode.Append.toString());
		return filePathUrl  + "/data";
	}
	
	public String parse(Simulate simulate, Model model, Dataset<Row> df, String[] fieldArray, String tableName,
			String filePathUrl, String filePath) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder builder = new StringBuilder();
		String aliaseName = "";
		builder.append("SELECT ");
		MetaIdentifierHolder dependsOn = model.getDependsOn();
		Object object = commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
		int i = 0;
		if(object instanceof Formula) {
			Formula formula = (Formula) object;
			for (Feature feature : model.getFeatures()) {
				builder.append(feature.getName()).append(" AS ").append(feature.getName()).append(", ");
				i++;
			}
			
			builder.append(formulaOperator.generateSql(formula, null, null, null)).append(" AS ").append(model.getLabel());
			builder.append(" FROM ");
			builder.append(tableName).append(" ").append(aliaseName);
			
			LOGGER.info("query : "+builder);
		}
		
		return builder.toString();
	}
	
	public Dataset<Row> generateDataframe(Simulate simulate, Model model, String tableName) throws Exception{
		int numIterations = simulate.getNumIterations();
		Dataset<Row> df = null;
		StringBuilder sb = new StringBuilder();
		// write code
		for (Feature feature : model.getFeatures()) {
			sb.append("(" + feature.getMinVal() + " + rand()*(" + feature.getMaxVal() + "-" + feature.getMinVal()
					+ ")) AS " + feature.getName() + ", ");
		}
		// df = sqlContext.range(0,numIterations).select("id",
		// sb.toString().substring(0, sb.toString().length()-2));

		df = sparkSession.sqlContext().range(0, numIterations);
		// df.registerTempTable(tableName);
		df.createOrReplaceTempView(tableName);
		
		sparkSession.sqlContext().registerDataFrameAsTable(df, tableName);
		df = sparkSession.sqlContext()
				.sql("SELECT id, " + sb.toString().substring(0, sb.toString().length() - 2) + " FROM " + tableName);
		df.show();
		return df;
	}
	
	/*public Double[] trialValues(long seed, String className, int numTrials, Row dataset, double[] factorMeans,
			double[][] factorCovariances) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		RandomGenerator rand = new MersenneTwister(seed);
		Double[] trialValues = new Double[numTrials] ;
		
		Class<?> distributorClass = Class.forName(className);
		Class<?>[] type = { RandomGenerator.class, double[].class, double[][].class};
		Constructor<?> cons = distributorClass.getConstructor(type);
		Object[] obj = {rand, factorMeans, factorCovariances};
		Object object = cons.newInstance(obj);
		
		for (int i = 0; i < numTrials; i++) {
			double[] trial = (double[]) object.getClass().getMethod("sample").invoke(object);
			Double totalValue = 0.0;
			for (int j=0; j<dataset.length(); j++) {			
				totalValue = trial[j] * (Double)dataset.get(j);
			}
			trialValues[i] = totalValue;
		}
		return trialValues;
	}*/
	
	public Dataset<Row> generateRandomSampler(long seed, String className, int numTrials) {
		Dataset<Row> df = null;
		RandomGenerator rand = new MersenneTwister(seed);
		// Create numTrials rows
		df = sparkSession.sqlContext().range(0, numTrials);
		return df;
	}
	
	public Dataset<Row> simulateMultiVarNormDist(long seed, String className, int numTrials, Row dataset, double[] factorMeans,
			double[][] factorCovariances) throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		Dataset<Row> df = generateRandomSampler(seed, className, numTrials);
		Class<?> distributorClass = Class.forName(className);
		Class<?>[] type = { RandomGenerator.class, double[].class, double[][].class};
		Constructor<?> cons = distributorClass.getConstructor(type);
//		df.withColumn("trialValues", functions.callUDF("multivarnormdist", seed, cons, factorMeans, factorCovariances));
		/*MultivariateMapFunction mapFunc = new MultivariateMapFunction(factorMeans, factorCovariances, cons, seed, dataset);
		JavaRDD<Row> rowRDD = df.javaRDD().map(mapFunc);*/
		/*JavaRDD<Row> rowRDD = df.javaRDD().map(new Function<Row, Row>() {

			@Override
			public Row call(Row inputRow) throws Exception {
				RandomGenerator rand = new MersenneTwister(seed);
				Object[] obj = {rand, factorMeans, factorCovariances};
				Object object = cons.newInstance(obj);
				Double totalValue = 0.0;
				double[] trial = (double[]) object.getClass().getMethod("sample").invoke(object);
				for (int j=0; j<dataset.length(); j++) {			
					totalValue += trial[j] * (Double)dataset.get(j);
				}
				return RowFactory.create(inputRow.getLong(0), totalValue);
			}
		});
*/		
		// Generate the schema based on the string of schema
		List<StructField> fields = new ArrayList<>();
		fields.add(DataTypes.createStructField("slNo", DataTypes.LongType, true));
		fields.add(DataTypes.createStructField("trialValue", DataTypes.LongType, true));
		StructType schema = DataTypes.createStructType(fields);
		// Apply the schema to the RDD
//		df = sparkSession.createDataFrame(rowRDD, schema);
		return df;
	}
	
	public Dataset<Row> executeDistribution(Distribution distribution, int numTrials, long seed, MetaIdentifierHolder factorMeansInfo, MetaIdentifierHolder factorCovariancesInfo) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, NullPointerException, ParseException {
		return multiNormalDist.executeDistribution(distribution, numTrials, seed, factorMeansInfo, factorCovariancesInfo);
	}
	
	public String generateSql(Simulate simulate, String tableName) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(simulate.getDependsOn().getRef().getUuid(), simulate.getDependsOn().getRef().getVersion(), simulate.getDependsOn().getRef().getType().toString());
		StringBuilder builder = new StringBuilder();
		String aliaseName = "";
		builder.append("SELECT ");
		MetaIdentifierHolder dependsOn = model.getDependsOn();
		Object object = commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
		int i = 0;
		if(object instanceof Formula) {
			Formula formula = (Formula) object;
			for (Feature feature : model.getFeatures()) {
				builder.append(feature.getName()).append(" AS ").append(feature.getName()).append(", ");
				i++;
			}
			
			builder.append(formulaOperator.generateSql(formula, null, null, null)).append(" AS ").append(model.getLabel());
			builder.append(" FROM ");
			builder.append(tableName).append(" ").append(aliaseName);
			
			LOGGER.info("query : "+builder);
		}
		
		return builder.toString();
	}
	
}

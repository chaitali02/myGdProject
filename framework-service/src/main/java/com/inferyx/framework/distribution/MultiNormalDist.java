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
package com.inferyx.framework.distribution;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.DataFrameHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.reader.IReader;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;

import scala.collection.JavaConversions;
import scala.reflect.ClassTag;

/**
 * @author joy
 *
 */
@Service
public class MultiNormalDist {
	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	DataSourceFactory dataSourceFactory;
	@Autowired
	MetadataUtil commonActivity;
	@Autowired
	HDFSInfo hdfsInfo;
	@Autowired
	private SparkSession sparkSession;
	
	/**
	 * 
	 */
	public MultiNormalDist() {
		// TODO Auto-generated constructor stub
	}

	/********************** UNUSED **********************/
	/**
	 * 
	 * @param distribution
	 * @return
	 * @throws JsonProcessingException
	 *//*
	public Row getInstruments(List<Param> params) throws JsonProcessingException {
		Row dataset = null;
		
		int parallelism = Integer.parseInt(params.get(3).getParamValue());
		String str = params.get(4).getParamValue();
		String[] splits = str.split(",");
		List<Double> datasetList = new ArrayList<>();
		for(String split : splits)
			datasetList.add(Double.parseDouble(split));
		dataset = RowFactory.create(datasetList.toArray());
		return dataset;
	}*/

	/********************** UNUSED **********************/
	/**
	 * 
	 * @param factorMeansInfo
	 * @return
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws IOException 
	 *//*
	public double[] getMeans(MetaIdentifierHolder factorMeansInfo) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		Datapod factorMeanDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(factorMeansInfo.getRef().getUuid(), factorMeansInfo.getRef().getVersion(), factorMeansInfo.getRef().getType().toString());
		DataStore factorMeanDs = dataStoreServiceImpl.findDataStoreByMeta(factorMeanDp.getUuid(), factorMeanDp.getVersion());
		List<String> meanColList = new ArrayList<>();
		
		IReader datapodReader = dataSourceFactory.getDatapodReader(factorMeanDp, commonActivity);
		DataFrameHolder meansHolder = datapodReader.read(factorMeanDp, factorMeanDs, hdfsInfo, sparkSession, datasource);
		Dataset<Row> meansDf = meansHolder.getDataframe();
		meansDf.show();
		
		for(int i=0; i<factorMeanDp.getAttributes().size(); i++) {
			if(i>0)
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

	/********************** UNUSED **********************/
	/**
	 * 
	 * @param factorCovariancesInfo
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NullPointerException
	 * @throws ParseException
	 * @throws IOException
	 *//*
	public double[][] getCovs(MetaIdentifierHolder factorCovariancesInfo) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		Datapod factorCovarianceDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(factorCovariancesInfo.getRef().getUuid(), factorCovariancesInfo.getRef().getVersion(), factorCovariancesInfo.getRef().getType().toString());
		DataStore factorCovarianceDs = dataStoreServiceImpl.findDataStoreByMeta(factorCovarianceDp.getUuid(), factorCovarianceDp.getVersion());
		IReader datapodReader = dataSourceFactory.getDatapodReader(factorCovarianceDp, commonActivity);
		DataFrameHolder covsHolder = datapodReader.read(factorCovarianceDp, factorCovarianceDs, hdfsInfo, sparkSession, datasource);
		Dataset<Row> covarsDf = covsHolder.getDataframe();
		covarsDf.show();
		
		List<String> covarColList = new ArrayList<>();
		for(int i=0; i<factorCovarianceDp.getAttributes().size(); i++) {
			if(i>0)
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
	 * @param distribution
	 * @param numTrials
	 * @param seed
	 * @param factorMeansInfo
	 * @param factorCovariancesInfo
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NullPointerException
	 * @throws ParseException
	 *//*
	public Dataset<Row> executeDistribution(Distribution distribution, int numTrials, long seed, MetaIdentifierHolder factorMeansInfo, MetaIdentifierHolder factorCovariancesInfo) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, NullPointerException, ParseException {
		ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(distribution.getParamList().getRef().getUuid(), distribution.getParamList().getRef().getVersion(), distribution.getParamList().getRef().getType().toString());
		List<Param> params = paramList.getParams();
		Row instruments = getInstruments(params);
		double[] factorMeans = getMeans(factorMeansInfo);
		double[][] factorCovariances = getCovs(factorCovariancesInfo);
		int parallelism = Integer.parseInt(params.get(3).getParamValue());
		
		ClassTag<Row> classTagRow = scala.reflect.ClassTag$.MODULE$.apply(Row.class);
		Broadcast<Row> broadcastInstruments = sparkSession.sparkContext().broadcast(instruments, classTagRow);
		
		// Generate different seeds so that our simulations don't all end up with the same results
		List<Long> seeds = LongStream.range(seed, seed + parallelism).boxed().collect(Collectors.toList());
		
		ClassTag<Long> classTagLong = scala.reflect.ClassTag$.MODULE$.apply(Long.class);
		JavaRDD<Long> seedRdd = sparkSession.sparkContext().parallelize(JavaConversions.asScalaBuffer(seeds), parallelism, classTagLong).toJavaRDD();
		
		// Main computation: run simulations and compute aggregate return for each
		JavaRDD<Double> trialsRdd = seedRdd.flatMap(sdd -> 
			Arrays.asList(ArrayUtils.toObject(trialValues(sdd, numTrials/parallelism, broadcastInstruments.value(), 
														factorMeans, factorCovariances))).iterator());
		
		JavaRDD<Row> trialsRowRdd = trialsRdd.map(new DoubleToRowFunction());

		// Cache the results so that we don't recompute for both of the summarizations below
	    trialsRdd.cache();
	    
		
		return sparkSession.sqlContext().createDataset(trialsRdd.rdd(), Encoders.DOUBLE()).toDF();
	}*/

	/********************** UNUSED **********************/
	/**
	 * 
	 * @param seed
	 * @param numTrials
	 * @param instruments
	 * @param factorMeans
	 * @param factorCovariances
	 * @return
	 *//*
	public static double[] trialValues(long seed, int numTrials, Row instruments, 
			double[] factorMeans, double[][] factorCovariances) {

		MersenneTwister rand = new MersenneTwister();
		MultivariateNormalDistribution multivariateNormal = new MultivariateNormalDistribution(rand, 
				factorMeans, factorCovariances);
		
		double trialValues[] = new double[numTrials];
		
		IntStream.range(0,  numTrials).forEach(i -> {
			double[] trial = multivariateNormal.sample();
			trialValues[i] = trialValue(trial, instruments);
		});
		
		return trialValues;
	}*/

	/********************** UNUSED **********************/
	/**
	 * Calculate the full value of the portfolio under particular trial conditions.
	 *//*
	public static double trialValue(double[] trial, Row instruments) {
		double totalValue = 0;
		for (int i = 0; i < instruments.length(); i++) {
			totalValue += instrumentTrialValue((Double)instruments.get(i), trial);
		}
		return totalValue;
	}*/

	/********************** UNUSED **********************/
	 /**
	  * Calculate the value of a particular instrument under particular trial conditions.
	  *//*
	public static double instrumentTrialValue(Double instrument, double[] trial) {
		double instrumentTrialValue = 0;
		int i=0;
		while (i < trial.length) {
			instrumentTrialValue += trial[i] * instrument;
			i += 1;
		}
//		return Math.min(Math.max(instrumentTrialValue, instrument.getMinValue()), instrument.getMaxValue());
		return instrumentTrialValue;
	}*/

	/********************** UNUSED **********************/
	/**
	 * 
	 * @param seed
	 * @param factorMeans
	 * @param factorCovariances
	 * @return
	 *//*
	public MultivariateNormalDistribution generateMultivariateNormDist(int seed, double[] factorMeans, double[][] factorCovariances) {
		MersenneTwister rand = new MersenneTwister();
		MultivariateNormalDistribution multivariateNormal = new MultivariateNormalDistribution(rand, factorMeans, factorCovariances);

		return multivariateNormal;
	}*/
}

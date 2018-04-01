package com.inferyx.framework.test;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.spark.SparkContext;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.inferyx.framework.algo.IAlgorithm;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.service.ParamListServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

public class SparkMonteCarloOperator implements IAlgorithm{
	
	private ParamSetServiceImpl paramSetServiceImpl;
	private ParamListServiceImpl paramListServiceImpl;
	private SparkContext sparkContext;
	private String filePathUrl;
	private Dataset<Row> dataframe;
	private Model model;
	
	
	public Dataset<Row> getDataframe() {
		return dataframe;
	}
	public void setDataframe(Dataset<Row> dataframe) {
		this.dataframe = dataframe;
	}
	public ParamListServiceImpl getParamListServiceImpl() {
		return paramListServiceImpl;
	}
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public void setParamListServiceImpl(ParamListServiceImpl paramListServiceImpl) {
		this.paramListServiceImpl = paramListServiceImpl;
	}
	public ParamSetServiceImpl getParamSetServiceImpl() {
		return paramSetServiceImpl;
	}
	public void setParamSetServiceImpl(ParamSetServiceImpl paramSetServiceImpl) {
		this.paramSetServiceImpl = paramSetServiceImpl;
	}
	public SparkContext getSparkContext() {
		return sparkContext;
	}
	public void setSparkContext(SparkContext sparkContext) {
		this.sparkContext = sparkContext;
	}
	public String getFilePathUrl() {
		return filePathUrl;
	}
	public void setFilePathUrl(String filePathUrl) {
		this.filePathUrl = filePathUrl;
	}
	
	public Double[] trialValues(long seed, int numTrials, Instrument[] instruments, double[] factorMeans,
			double[][] factorCovariances) {
		RandomGenerator rand = new MersenneTwister(seed);
		Double[] trialValues = new Double[] { (double) numTrials };
		MultivariateNormalDistribution multivariateNormal = new MultivariateNormalDistribution(rand, factorMeans,
				factorCovariances);

		for (int i = 0; i <= numTrials; i++) {
			double[] trial = multivariateNormal.sample();
			trialValues[i] = trialValue(trial, instruments);
		}

		return trialValues;
	}

	public Double trialValue(double[] trial, Instrument[] instruments) {
		Double totalValue = 0.0;
		for (Instrument instrument : instruments) {
			totalValue += instrumentTrialValue(instrument, trial);
		}
		return totalValue;
	}

	public Double instrumentTrialValue(Instrument instrument, double[] trial) {
		Double instrumentTrialValue = 0.0;
		int i = 0;
		while (i < trial.length) {
			instrumentTrialValue += trial[i] * instrument.factorWeights[i];
			i += 1;
		}
		return Math.min(Math.max(instrumentTrialValue, instrument.minValue), instrument.maxValue);
	}

	@Override
	public boolean train(String className, String modelName, Dataset<Row> df, ParamMap paramMap) {
		/*Broadcast<?> broadcastInstruments = sparkContext.broadcast(parallelism);
	    long seeds = (seed until seed + parallelism);

	    RDD<?> seedRdd = sparkContext.parallelize(seeds, parallelism);
	    RDD<?> trialsRdd = seedRdd.flatMap(trialValues(seeds, numTrials / parallelism,
	    broadcastInstruments.getValue(), factorMeans, factorCovariances));
	    
	    Object varFivePercent = trialsRdd.takeOrdered(numTrials / 20).last*/
		return false;
	}

	@Override
	public Object validate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object predict() {
		// TODO Auto-generated method stub
		return null;
	}
}

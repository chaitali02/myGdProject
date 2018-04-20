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

import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

/**
 * @author joy
 *
 */
public class MultivariateMapFunction implements Function<Row, Row> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 329038594226801317L;
	/*private double[] factorMeans;
	private double[][] factorCovariances;
	private Constructor<?> cons;
	private Long seed;*/
	private Object object;
	private Row dataset;

	/**
	 * 
	 */
	public MultivariateMapFunction() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param factorMeans2
	 * @param factorCovariances2
	 * @param cons
	 * @param seed
	 * @param dataset
	 */
	public MultivariateMapFunction(Object object, 
			Row dataset) {
		super();
		/*this.factorMeans = factorMeans2;
		this.factorCovariances = factorCovariances2;
		this.cons = cons;
		this.seed = seed;*/
		this.object = object;
		this.dataset = dataset;
	}
	
	@Override
	public Row call(Row inputRow) throws Exception {
		/*RandomGenerator rand = new MersenneTwister(seed);
		Object[] obj = {rand, factorMeans, factorCovariances};
		Object object = cons.newInstance(obj);*/
		Double totalValue = 0.0;
		double[] trial = (double[]) object.getClass().getMethod("sample").invoke(object);
		for (int j=0; j<dataset.length(); j++) {			
			totalValue += trial[j] * (Double)dataset.get(j);
		}
		return RowFactory.create(inputRow.getLong(0), totalValue);
	}

}

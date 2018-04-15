/**
 * 
 */
package com.inferyx.framework.distribution;

import java.lang.reflect.Constructor;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
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
	private double[] factorMeans;
	private double[][] factorCovariances;
	private Constructor<?> cons;
	private Long seed;
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
	public MultivariateMapFunction(double[] factorMeans2, double[][] factorCovariances2, Constructor<?> cons, Long seed,
			Row dataset) {
		super();
		this.factorMeans = factorMeans2;
		this.factorCovariances = factorCovariances2;
		this.cons = cons;
		this.seed = seed;
		this.dataset = dataset;
	}
	
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

}

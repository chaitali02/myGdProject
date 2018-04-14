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
	
	private Double[] factorMeans;
	private Double[][] factorCovariances;
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
	 * @param factorMeans
	 * @param factorCovariances
	 * @param cons
	 * @param seed
	 * @param dataset
	 */
	public MultivariateMapFunction(Double[] factorMeans, Double[][] factorCovariances, Constructor<?> cons, Long seed,
			Row dataset) {
		super();
		this.factorMeans = factorMeans;
		this.factorCovariances = factorCovariances;
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

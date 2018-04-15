/**
 * 
 */
package com.inferyx.framework.distribution;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

/**
 * @author joy
 *
 */
public class DoubleToRowFunction implements Function<Double, Row> {

	/**
	 * 
	 */
	public DoubleToRowFunction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Row call(Double value) throws Exception {
		return RowFactory.create(value);
	}

}

/**
 * 
 */
package com.inferyx.framework.common;

import java.io.Serializable;

import org.apache.spark.rdd.DoubleRDDFunctions;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Service;

import scala.Tuple2;

/**
 * @author joy
 *
 */
@Service
public class HistogramUtil implements Serializable {

	/**
	 * 
	 */
	public HistogramUtil() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param ds
	 * @param numBuckets
	 * @return
	 */
	public Tuple2<double[], long[]> fetchHistogramTuples (Dataset<Row> ds, int numBuckets) {
		DoubleRDDFunctions doubleRDDFunctions = new DoubleRDDFunctions(ds.toJavaRDD().map(row -> row.get(0)).rdd());	
		return doubleRDDFunctions.histogram(numBuckets);
	}

}

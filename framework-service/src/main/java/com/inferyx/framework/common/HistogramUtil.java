/**
 * 
 */
package com.inferyx.framework.common;

import java.io.Serializable;

import org.apache.spark.rdd.DoubleRDDFunctions;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;
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
		String newDataType = DataTypes.createDecimalType().toString();
		newDataType = newDataType.substring(0, newDataType.lastIndexOf("("));
		if(ds.dtypes()[0]._2().contains(newDataType)) {
			ds = ds.withColumn(ds.columns()[0], ds.col(ds.columns()[0]).cast(DataTypes.DoubleType));
		}
		DoubleRDDFunctions doubleRDDFunctions = new DoubleRDDFunctions(ds.toJavaRDD().map(row -> row.get(0)).rdd());	
		return doubleRDDFunctions.histogram(numBuckets);
	}

}

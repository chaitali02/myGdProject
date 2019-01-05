/**
 * 
 */
package com.inferyx.framework.common;

import java.io.Serializable;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.datavec.api.transform.schema.Schema;
import org.springframework.stereotype.Component;

/**
 * @author joy
 *
 */
@Component
public class SerializableHelper implements Serializable {

	/**
	 * 
	 */
	public SerializableHelper() {
		// TODO Auto-generated constructor stub
	}

	public void addColToSchemaBuilder(String colName, Schema.Builder schemaBuilder, Dataset<Row> df) {
		schemaBuilder.addColumnCategorical(colName, df.select(colName).distinct().map(new MapFunction<Row, String>() {

			@Override
			public String call(Row row) throws Exception {
				return row.mkString();
			}
		}, Encoders.STRING()).collectAsList());
	}
	
}

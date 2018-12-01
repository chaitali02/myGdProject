/**
 * 
 */
package com.inferyx.framework.executor.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * @author joy
 *
 */
@Component
public class SparkExecHelper implements Serializable {
	
	public static final Logger logger = LoggerFactory.getLogger(SparkExecHelper.class);

	/**
	 * 
	 */
	public SparkExecHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public Dataset<Row> parseFeatures(Dataset<Row> df, StructField []newFields) {
		return df.sparkSession().createDataFrame(df.javaRDD().map(new Function<Row, Row>() {

			@Override
			public Row call(Row v1) throws Exception {
				logger.info("Inside call : feature index : v1 length " + v1.fieldIndex("features") + " : " +v1.length());
				int featuresPos = v1.fieldIndex("features");
				List<Object> values = new ArrayList<>();
				for (int i = 0; i < v1.length(); i++) {
					if (i == featuresPos && v1.get(i) != null) {
						logger.info(v1.get(i) + ":" + v1.get(i).toString());
						values.add(v1.get(i).toString());
					} else {
						values.add(v1.get(i));
					}
				}
				
				return RowFactory.create(values.toArray());
			}
		}), new StructType(newFields));
	}

}

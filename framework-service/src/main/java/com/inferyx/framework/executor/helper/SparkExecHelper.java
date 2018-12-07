/**
 * 
 */
package com.inferyx.framework.executor.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;


/**
 * @author joy
 *
 */
@Component
public class SparkExecHelper implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final Logger logger = Logger.getLogger(SparkExecHelper.class);

	/**
	 * 
	 */
	public SparkExecHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public Dataset<Row> parseFeatures(Dataset<Row> df, StructField []newFields, List<String> vectorFields) {
		List<Integer> vectorFieldPos = new ArrayList<>();
		// Get respective field indices
		JavaRDD<Row> rowRdd = df.javaRDD();
		Row row = rowRdd.first();
		if (row == null || row.length() <= 0) {
			return df;
		}
		for (String vectorField : vectorFields) {
			vectorFieldPos.add(row.fieldIndex(vectorField));
		}
		// All positions of vector collected. Let's proceed
		
		return df.sparkSession().createDataFrame(df.javaRDD().map(new Function<Row, Row>() {

			@Override
			public Row call(Row v1) throws Exception {
				logger.info("Inside call : feature index : v1 length " + v1.fieldIndex("features") + " : " +v1.length());
//				int featuresPos = v1.fieldIndex("features");
				List<Object> values = new ArrayList<>();
				for (int i = 0; i < v1.length(); i++) {
					if (vectorFieldPos.contains(i) && v1.get(i) != null) {
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

	public Dataset<Row> getPredictionCompareStatus(Dataset<Row> trainedDataSet) {
		StructField[] structFields = new StructField[] {new StructField("rowNum", DataTypes.IntegerType, true, Metadata.empty())
				, new StructField("prediction_status", DataTypes.StringType, true, Metadata.empty())};
		
		return trainedDataSet.sparkSession().createDataFrame(trainedDataSet.toJavaRDD().map(new Function<Row, Row>() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 7912176441289830983L;
			int rowNum = 0;
			@Override
			public Row call(Row row) throws Exception {
				Double lableVal = (Double)row.getAs("label");
				Double predictionVal = (Double)row.getAs("prediction");
				logger.info("labelVal : predictionVal :::: "+lableVal+" : "+predictionVal);
				if(lableVal.equals(predictionVal)) {
					rowNum++;
					return RowFactory.create(rowNum, "true");
				} else {
					rowNum++;
					return RowFactory.create(rowNum, "false");
				}	
			}
		}), new StructType(structFields));
	}
}

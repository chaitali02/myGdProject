/**
 * 
 */
package com.inferyx.framework.executor.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.linalg.VectorUDT;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.stereotype.Component;

import scala.Tuple2;


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

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Row call(Row v1) throws Exception {
//				logger.info("Inside call : feature index : v1 length " + v1.fieldIndex("features") + " : " +v1.length());
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

	public Dataset<Row> parseVectorFeatures(Dataset<Row> df, StructField []newFields, List<String> vectorFields) {
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

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Row call(Row v1) throws Exception {
				List<Object> values = new ArrayList<>();
				for (int i = 0; i < v1.length(); i++) {
					if (vectorFieldPos.contains(i) && v1.get(i) != null) {
						String tempStr = v1.get(i).toString().startsWith("[") ? v1.get(i).toString().substring(1) : v1.get(i).toString();
						tempStr = tempStr.endsWith("]") ? tempStr.substring(0, tempStr.length()-2) : tempStr;
						String[] colData = tempStr.split(",");
						double[] data = new double[colData.length];
						int j = 0;
						for(String val : colData) {
							val = val.replaceAll("[^0-9]","");
							data[j] = Double.parseDouble(val.trim());
							j++;
						}
						values.add(Vectors.dense(data));
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
				Object labelObj = row.getAs("label");
				Double lableVal = Double.parseDouble(""+labelObj);
				
				Object predObj = row.getAs("prediction");
				Double predictionVal = Double.parseDouble(""+predObj);
				
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
	
	public List<Map<String, Object>> getRoc(JavaRDD<Tuple2<Object, Object>> rocRDD) {
		return rocRDD.map(new Function<Tuple2<Object,Object>, Map<String, Object>>() {

			@Override
			public Map<String, Object> call(Tuple2<Object, Object> v1) throws Exception {
				Map<String, Object> returnMap = new HashMap<>();
				returnMap.put((String)v1._1, v1._2);
				return returnMap;
			}
			
		}).collect();
	}
}

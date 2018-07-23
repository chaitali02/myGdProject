/**
 * 
 */
package com.inferyx.framework.operator;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.distributed.BlockMatrix;
import org.apache.spark.mllib.linalg.distributed.IndexedRow;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.springframework.stereotype.Service;

/**
 * @author joy
 *
 */
@Service
public class MatrixToRddConverter implements Serializable {

	/**
	 * 
	 */
	public MatrixToRddConverter() {
		// TODO Auto-generated constructor stub
	}

	public JavaRDD<Row> convertToRows(BlockMatrix resultMatrix) {
		JavaRDD<Row> rowRDD = (JavaRDD<Row>) resultMatrix.toCoordinateMatrix().toIndexedRowMatrix().rows().toJavaRDD().map(new Function<IndexedRow, Row>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -8449857364802065673L;

			@Override
			public Row call(IndexedRow v1) throws Exception {
				double[] vecArray = v1.vector().toArray();
				Object[] obj = new Object[vecArray.length];
				for (int i = 0; i < vecArray.length; i++) {
					obj[i] = vecArray[i];
				}
				return RowFactory.create(obj);
			}
		});
		rowRDD.foreach(t -> System.out.println(t.get(0)));
		return rowRDD;
	}
	
}

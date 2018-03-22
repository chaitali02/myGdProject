/**
 * 
 */
package com.inferyx.framework.test;

import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * @author joy
 *
 */
public class TestSpark2 {

	/**
	 * 
	 */
	public TestSpark2() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String []args) throws IOException {
		/*IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getStmtObject();*/
		SparkConf config = new SparkConf();
		config.setAppName("Test").setMaster("local[*]");
		SparkContext sc = new SparkContext(config);
		SparkSession sparkSession = new SparkSession(sc);
		Dataset<Row> df = sparkSession.sql("select 1");
		df.show();
		Dataset<Row> df1 = sparkSession.read().parquet("/user/hive/warehouse/framework/0c9b5122-ced3-404d-a22b-ff1105e1f9ac/1488623530/1502030752");
		df1.show();
	}

}

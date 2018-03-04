/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.test;


import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class SparkTest {
		
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		
		SparkConf conf = new SparkConf().setAppName("test").setMaster("local");
		
		JavaSparkContext sc = new JavaSparkContext(conf);
		HiveContext hiveContext = new org.apache.spark.sql.hive.HiveContext(sc);

		Dataset<Row> df = hiveContext.read().parquet("/user/hive/warehouse/framework/01459dc3-a6e4-4c8c-acf6-5cfec0605801/1467582678/1467582678");
		df.printSchema();
		hiveContext.registerDataFrameAsTable(df, "test");

		Dataset<Row> ver = hiveContext.sql("select As_Of_Year,Rating from test");
		System.out.println(ver.head());
	
	}
}

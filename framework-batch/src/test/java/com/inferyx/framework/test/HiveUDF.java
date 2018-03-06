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
import java.math.BigDecimal;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.api.java.UDF3;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.DataTypes;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class HiveUDF {
		
	@SuppressWarnings("serial")
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		
		BetaDistribution bd = new BetaDistribution(60.47823256,68.56493791);
		System.out.println(bd.cumulativeProbability(0.39));
		
		SparkConf conf = new SparkConf().setAppName("org.sparkexample.WordCount").setMaster("local");
		
		JavaSparkContext sc = new JavaSparkContext(conf);
		HiveContext hiveContext = new org.apache.spark.sql.hive.HiveContext(sc);
		hiveContext.udf().register("betaInv",new UDF3<BigDecimal,Double,Double,Double>() {
			@Override
				public Double call(BigDecimal x,Double alpha,Double beta) throws Exception {
				BetaDistribution bd = new BetaDistribution(alpha,beta);
				return bd.cumulativeProbability(x.doubleValue());
			}
		}, DataTypes.DoubleType);
		String sql = "select betaInv(0.39,60.47823256,68.56493791)";

		Dataset<Row> df = hiveContext.sql(sql);
		System.out.println(df.head());
	}
}

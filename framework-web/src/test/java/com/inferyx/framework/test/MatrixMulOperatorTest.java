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
/**
 * 
 */
package com.inferyx.framework.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;

import com.inferyx.framework.operator.MatrixMulOperator;

/**
 * @author netsafe
 *
 */
public class MatrixMulOperatorTest {
	

	public static void main(String[] args){
		// Create a JavaRDD<Row>
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("matrixMul");
		JavaSparkContext sc = new JavaSparkContext(conf);
		List<Row> rowList = new ArrayList<Row>();
		List<String> yearsList = new ArrayList<String>();
		/*populateRowList();
		populateMatrixnameList();*/
		JavaRDD<Row> rowRDD = sc.parallelize(rowList);
		MatrixMulOperator operator = new MatrixMulOperator();
		operator.getMatrixMul(rowRDD, yearsList);
		
		
	}

}

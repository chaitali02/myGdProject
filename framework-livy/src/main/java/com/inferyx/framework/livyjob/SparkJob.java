/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.livyjob;

import org.apache.livy.Job;
import org.apache.livy.JobContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;

/**
 * @author joy
 *
 */
public class SparkJob implements Job<Dataset<Row>
> {
	
	private String sql;

	/**
	 * 
	 */
	public SparkJob() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sql
	 */
	public SparkJob(String sql) {
		super();
		this.sql = sql;
	}
	
	@Override
	public Dataset<Row> call(JobContext jc) throws Exception {
		HiveContext hiveContext = jc.hivectx();
		Dataset<Row> df = hiveContext.sql(sql);
		return df;
	}
	
}

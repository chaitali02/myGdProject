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
package com.inferyx.framework.connector;

import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.SparkInfo;
import com.inferyx.framework.executor.ExecContext;

@Component
public class SparkConnector implements IConnector{
	
	@Autowired
	SparkInfo sparkInfo;
	/*@Autowired
	HiveContext hiveContext;*/
//	@Autowired @Lazy
	private SparkSession sparkSession;
	
	private SparkSession getSparkSession() throws IOException {
		if (this.sparkSession == null) {
			synchronized (this) {
				if (this.sparkSession == null) {
					SparkConf sparkConf = sparkInfo.getSparkConfiguration();
					SparkContext sparkContext = new SparkContext(sparkConf);
					this.sparkSession = new SparkSession(sparkContext);
				}
			}
		} 
		return this.sparkSession;
	}
	
	@Override
	public ConnectionHolder getConnection() throws IOException {
		ConnectionHolder conHolder = new ConnectionHolder();
		//SparkInfo sparkInfo = new SparkInfo();
		/*SparkConf sparkConf = sparkInfo.getSparkConfiguration();
		SparkContext sparkContext = new SparkContext(sparkConf);
		HiveContext hiveContext = new HiveContext(sparkContext);*/
		conHolder.setType(ExecContext.spark.toString());
		conHolder.setStmtObject(getSparkSession());
		return conHolder;
	}

	
}

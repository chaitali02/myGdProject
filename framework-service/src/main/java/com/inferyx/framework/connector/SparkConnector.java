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
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Enumeration;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.SparkInfo;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.register.UDFRegister;
import com.inferyx.framework.service.CommonServiceImpl;

@Component
public class SparkConnector implements IConnector{
	
	@Autowired
	SparkInfo sparkInfo;
	@Autowired
	UDFRegister registerUDF;
	@Autowired
	CommonServiceImpl commonServiceImpl;
	
	/*@Autowired
	HiveContext hiveContext;*/
//	@Autowired @Lazy
	private SparkSession sparkSession;
	
	@SuppressWarnings({ "resource", "static-access" })
	private SparkSession getSparkSession() throws IOException {
		String key;
		String value;
		if (this.sparkSession == null) {
			synchronized ("1") {
				if (this.sparkSession == null) {
					SparkConf sparkConf = sparkInfo.getSparkConfiguration();
					System.out.println("Creating a spark context as sparkSession is : " + this.sparkSession);
					SparkContext sparkContext = new SparkContext(sparkConf);
					try {
						sparkContext.hadoopConfiguration().set("fs.s3n.awsAccessKeyId",commonServiceImpl.getConfigValue("fs.s3n.awsAccessKeyId"));
						sparkContext.hadoopConfiguration().set("fs.s3n.awsSecretAccessKey",commonServiceImpl.getConfigValue("fs.s3n.awsSecretAccessKey"));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//					Enumeration<?> e = sparkInfo.getProp().propertyNames();
//					while (e.hasMoreElements()) {
//						key = (String) e.nextElement();
//						value = sparkInfo.getProp().getProperty(key);							
//						if(key.contains("s3")) {					
//							String []hadoopConf = key.split("=");
//							sparkContext.hadoopConfiguration().set(hadoopConf[0], hadoopConf[1]);				
//						}
//					}
					this.sparkSession = new SparkSession(sparkContext).builder().enableHiveSupport().getOrCreate();
					registerUDF.register(sparkSession);
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

	@Override
	public ConnectionHolder getConnection(Object input, Object input2) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionHolder getConnectionByDatasource(Datasource datasource) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	
}

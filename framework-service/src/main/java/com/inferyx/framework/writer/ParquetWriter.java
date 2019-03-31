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
package com.inferyx.framework.writer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.MessageStatus;

@Component
public class ParquetWriter implements IWriter {

	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ExecutorFactory execFactory;
	@Autowired
	private SparkExecutor<?> sparkExecutor;
	
	@Override
	public void write(ResultSetHolder rsHolder, String filePathUrl, Datapod datapod, String saveMode) throws IOException {
		try { 
//			IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
			Dataset<Row> df = rsHolder.getDataFrame();
			SparkSession sparkSession = df.sparkSession();
			Datasource ds = (Datasource) commonServiceImpl.getOneByUuidAndVersion(datapod.getDatasource().getRef().getUuid(), 
																					datapod.getDatasource().getRef().getVersion(), 
																					datapod.getDatasource().getRef().getType().toString(), "N");
			String sessionParameters = ds.getSessionParameters();
			if(sessionParameters != null && !StringUtils.isBlank(sessionParameters)) {
//				Configuration config = sparkSession.sparkContext().hadoopConfiguration();
				for(String sessionParam :sessionParameters.split(",")) {
					sparkSession.sql("SET "+sessionParam);
					if (sessionParam.contains("s3")) {
						String []hadoopConf = sessionParam.split("=");
						sparkSession.sparkContext().hadoopConfiguration().set(hadoopConf[0], hadoopConf[1]);
					}
				}
			}
//			df.show(false);
			if(datapod !=null) {
				if(df.columns().length != datapod.getAttributes().size())
					throw new RuntimeException("Datapod '" + datapod.getName() + "' column size(" + datapod.getAttributes().size() + ") does not match with column size("+ df.columns().length +") of dataframe");
				
//				List<Attribute> attributes = datapod.getAttributes();
//				for(Attribute attribute : attributes){
//					df = df.withColumn(attribute.getName(), df.col(attribute.getName()).cast((DataType)exec.getDataType(attribute.getType())));
//				}
				
				if(rsHolder.getTableName() != null && !rsHolder.getTableName().isEmpty()) {
					rsHolder = sparkExecutor.applySchema(rsHolder, datapod, null, rsHolder.getTableName(), false);
					df = rsHolder.getDataFrame();
				}
			} 
			df.printSchema();
//			df.show(false);
			if(saveMode.equalsIgnoreCase("append"))	{
				df.write().mode(SaveMode.Append).parquet(filePathUrl);
			}else if(saveMode.equalsIgnoreCase("overwrite")) {
				df.write().mode(SaveMode.Overwrite).parquet(filePathUrl);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			String errorMessage = e.getMessage();
			if(errorMessage.contains("Path does not exist:")) {
				e.printStackTrace();
				String message = null;
				try {
					message = e.getMessage();
				}catch (Exception e2) {
					// TODO: handle exception
				}
				try {
					commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "File path not exist.", null);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException | NullPointerException | JSONException
						| ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					throw new RuntimeException((message != null) ? message : "File path not exist.");
				}
				throw new RuntimeException((message != null) ? message : "File path not exist.");
			} else 
				throw new RuntimeException(e);
		}
	}
}
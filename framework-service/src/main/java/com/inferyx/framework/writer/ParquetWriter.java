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

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Datapod;
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
//			df.show(true);
			if(datapod !=null) {
				if(df.columns().length != datapod.getAttributes().size())
					throw new RuntimeException("Datapod '" + datapod.getName() + "' column size(" + datapod.getAttributes().size() + ") does not match with column size("+ df.columns().length +") of dataframe");
				
//				List<Attribute> attributes = datapod.getAttributes();
//				for(Attribute attribute : attributes){
//					df = df.withColumn(attribute.getName(), df.col(attribute.getName()).cast((DataType)exec.getDataType(attribute.getType())));
//				}
				
				if(rsHolder.getTableName() != null && !rsHolder.getTableName().isEmpty()) {
					rsHolder = sparkExecutor.applySchema(rsHolder, datapod, null, rsHolder.getTableName(), true);
					df = rsHolder.getDataFrame();
				}
			} 
			df.printSchema();
//			df.show(true);
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
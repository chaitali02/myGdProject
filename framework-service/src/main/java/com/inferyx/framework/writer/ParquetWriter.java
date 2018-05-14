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
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
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
	SparkSession sparkSession;
	
	@Override
	public void write(Dataset<Row> df, String filePathUrl, Datapod datapod, String saveMode) throws IOException {
		try { 
			IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
			if(datapod !=null) {
				List<Attribute> attributes=datapod.getAttributes();
				for(Attribute attribute : attributes){	
					df = df.withColumn(attribute.getName(), df.col(attribute.getName()).cast((DataType)exec.getDataType(attribute.getType())));
				} 				
			} 

			df.show(true);
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
					commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "File path not exist.");
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException | NullPointerException | JSONException
						| ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					throw new RuntimeException((message != null) ? message : "File path not exist.");
				}
				throw new RuntimeException((message != null) ? message : "File path not exist.");
			}
		}
	}
}
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
package com.inferyx.framework.reader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.ParseException;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.log4j.Logger;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.ResultType;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataFrameService;
import com.inferyx.framework.service.MessageServiceImpl;
import com.inferyx.framework.service.MessageStatus;

@Component
public class ParquetReader implements IReader
{
	/*@Autowired
	ExecutorFactory execFactory;*/
	@Autowired
	DataFrameService dataFrameService;
	@Autowired
	MessageServiceImpl messageServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	/*DataFrame df;*/
	/*HiveContext hiveContext;*/
	/*String tableName="";
	DataFrameHolder dfm;*/

	static final Logger logger = Logger.getLogger(ParquetReader.class);
	
	@Override
	public ResultSetHolder read(Datapod datapod, DataStore datastore, Object conObject, Datasource ds) 
			throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
		String tableName="";
		ResultSetHolder rsHolder = new ResultSetHolder();
		try {
			String filePath = null;
			
			filePath = Helper.getPath(datastore, ds);
			
			/*DataFrameHolder dfm = dataFrameService.getaDataFrameHolder(filePath, conObject);*/
			Dataset<Row> df = null;
			SparkSession sparkSession = (SparkSession) conObject;
			String sessionParameters = ds.getSessionParameters();
			if(sessionParameters != null && !StringUtils.isBlank(sessionParameters)) {
//				Configuration config = sparkSession.sparkContext().hadoopConfiguration();
				for(String sessionParam :sessionParameters.split(",")) {
					sparkSession.sql("SET "+sessionParam);
					if (sessionParam.contains("s3")) {
						String []hadoopConf = sessionParam.split("=");
						sparkSession.sparkContext().hadoopConfiguration().set(hadoopConf[0], hadoopConf[1]);
						logger.info(hadoopConf[0] + ":" + sparkSession.sparkContext().hadoopConfiguration().get(hadoopConf[0]));
//						hadoopConf = null;
					}
				}
				for (String param : sparkSession.sparkContext().hadoopConfiguration().getFinalParameters()) {
					logger.info(param + ":" + sparkSession.sparkContext().hadoopConfiguration().get(param));
				}
			}
			logger.info("File Path : " + filePath);
			// Check if path exists
			/*logger.info("Path exists : " + FileSystem.get(new URI("s3://inferyx"), sparkSession.sparkContext().hadoopConfiguration()).exists(new Path(filePath+"/")));
			RemoteIterator<LocatedFileStatus> it = FileSystem.get(new URI("s3://inferyx"), sparkSession.sparkContext().hadoopConfiguration()).listFiles(new Path("s3://inferyx/user/hive/warehouse/framework/data/"), true);// ed47f654-2d4b-483c-971f-804ee88f092f/1488620292/1540987853/
			while(it.hasNext()) {
				logger.info(it.next().getPath().getName());
			}*/
			logger.info("Going to fetch file ");
			df = sparkSession.read().load(filePath);
			tableName = Helper.genTableName(filePath);
			rsHolder.setDataFrame(df);
			rsHolder.setCountRows(df.count());
			rsHolder.setType(ResultType.dataframe);
			rsHolder.setTableName(tableName);
		}catch (Exception e) {
			e.printStackTrace();
			String errorMessage = e.getMessage();
			if(errorMessage.contains("Path does not exist:")) {
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
			}
		}		
		return rsHolder;
	}	

	public ResultSetHolder read(String filePath, Object conObject) throws IOException {
		String tableName = "";
		ResultSetHolder rsHolder = new ResultSetHolder();
		try {			
			Dataset<Row> df = null;
			SparkSession sparkSession = (SparkSession) conObject;
			DataFrameReader reader = sparkSession.read();
			df = reader.load(filePath);
			tableName = Helper.genTableName(filePath);
			rsHolder.setDataFrame(df);
			rsHolder.setCountRows(df.count());
			rsHolder.setType(ResultType.dataframe);
			rsHolder.setTableName(tableName);
			return rsHolder;
		}catch (Exception e) {
			e.printStackTrace();
			String errorMessage = e.getMessage();
			if(errorMessage.contains("Path does not exist:")) {
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
			}
			throw new RuntimeException("Can not read file.");
		}	
	}
}

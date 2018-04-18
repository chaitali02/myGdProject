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
import java.text.ParseException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.NestedServletException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.DataFrameHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataFrameService;
import com.inferyx.framework.service.MessageServiceImpl;
import com.inferyx.framework.service.MessageStatus;
import com.inferyx.framework.service.ProfileServiceImpl;

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
	public DataFrameHolder read(Datapod dp, DataStore datastore, HDFSInfo hdfsInfo, Object conObject, Datasource ds) throws IOException {
		String tableName="";
		DataFrameHolder dfmh = new DataFrameHolder();
		try {
			String filePath = datastore.getLocation();
			String hdfsLocation = String.format("%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath());
			
			if (!filePath.contains(hdfsInfo.getHdfsURL()) && !filePath.contains(hdfsInfo.getSchemaPath())) 
				filePath = String.format("%s%s", hdfsLocation, filePath);
			else if(!filePath.contains(hdfsInfo.getHdfsURL()))
				filePath = String.format("%s%s", hdfsInfo.getHdfsURL(), filePath);
			else if(!filePath.contains(hdfsInfo.getSchemaPath()))
				filePath = String.format("%s%s", hdfsInfo.getSchemaPath(), filePath);
			
			/*DataFrameHolder dfm = dataFrameService.getaDataFrameHolder(filePath, conObject);*/
			Dataset<Row> df = null;
			SparkSession sparkSession = (SparkSession) conObject;
			DataFrameReader reader = sparkSession.read();
			df = reader.load(filePath);
			tableName = Helper.genTableName(filePath);
			dfmh.setDataframe(df);
			dfmh.setTableName(tableName);
		}catch (Exception e) {
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
		
		return dfmh;
	}	

}

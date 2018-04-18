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

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.DataFrameHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.SecurityServiceImpl;

@Component
public class HiveReader implements IReader
{
	static final Logger logger=Logger.getLogger(HiveReader.class);
	
	@Autowired
	protected MetadataUtil daoRegister;
	@Autowired
	protected ExecutorFactory execFactory;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;

	@Override
	public DataFrameHolder read(Datapod datapod, DataStore datastore, HDFSInfo hdfsInfo, Object conObject, Datasource dataSource) throws IOException {
		Dataset<Row> dataFrame = null;
		String tableName="";
		DataFrameHolder dataFrameHolder = new DataFrameHolder();
		try {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			String filepath = datastore.getLocation();
			String dbName = dataSource.getDbname();		
			ResultSetHolder rsHolder = exec.executeSql("SELECT * FROM "+dbName+"."+datapod.getName());
			dataFrame = rsHolder.getDataFrame();
			
			tableName = Helper.genTableName(filepath);		
			dataFrameHolder.setDataframe(dataFrame);
			dataFrameHolder.setTableName(tableName);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException e) {
			e.printStackTrace();
		}		
		return dataFrameHolder;		
	}
}

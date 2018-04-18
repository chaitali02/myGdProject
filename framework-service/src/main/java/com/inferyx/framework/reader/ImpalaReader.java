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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;

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

public class ImpalaReader implements IReader {
	Logger logger=Logger.getLogger(ImpalaReader.class);
	@Autowired
	protected MetadataUtil daoRegister;
	@Autowired
	protected ExecutorFactory execFactory;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private SecurityServiceImpl securityServiceImpl;

	
	@Override
	public DataFrameHolder read(Datapod datapod, DataStore datastore, HDFSInfo hdfsInfo, Object conObject, Datasource dataSource)
			throws IOException {
		Dataset<Row> dataFrame = null;
		DataFrameHolder dataFrameHolder = new DataFrameHolder();
		try {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			String filepath = datastore.getLocation();
			String dbName = dataSource.getDbname();		
			ResultSetHolder rsHolder = exec.executeSql("SELECT * FROM "+dbName+"."+datapod.getName());
			if(dataFrame ==null){
				JavaRDD  rdd=(JavaRDD) rsHolder.getResultSet();
				SQLContext sqlContext=null;
				List<StructField> fields = new ArrayList<StructField>();
				ResultSetMetaData metaData;
				try {
					metaData = rsHolder.getResultSet().getMetaData();
					for (int i = 1; i <= metaData.getColumnCount(); i++)
					{  
						fields.add(DataTypes.createStructField(metaData.getColumnLabel(i), DataTypes.StringType, true));
					}
					StructType schema = DataTypes.createStructType(fields);
					dataFrame = sqlContext.createDataFrame(rdd, schema);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			dataFrameHolder.setDataframe(dataFrame);
			dataFrameHolder.setTableName(Helper.genTableName(filepath));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		return dataFrameHolder;	
	}

}

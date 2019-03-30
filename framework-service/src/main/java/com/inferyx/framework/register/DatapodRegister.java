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
package com.inferyx.framework.register;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataFrameService;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.DatasourceServiceImpl;

@Component
public class DatapodRegister {
	Logger logger = Logger.getLogger(DatapodRegister.class);
	// for datastore service implementation
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	DatasourceServiceImpl dataSourceServiceImpl;
	@Autowired
	DataSourceFactory dataSourceFactory;
	@Autowired
	HDFSInfo hdfsInfo;
	@Autowired
	ConnectionFactory connFactory;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	Engine engine;
	@Autowired
	Helper helper;
	@Autowired
	protected ExecutorFactory execFactory;
	@Autowired
	protected DataFrameService dataFrameService;
	
	public void registerDatapod (DataStore datastore, Datapod datapod, RunMode runMode) throws Exception {
		try {
			/*IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);*/
			/*String datasourceUUID = datapod.getDatasource().getRef().getUuid();
			String datasourceVersion = datapod.getDatasource().getRef().getVersion();*/
			//Datasource datasource = (Datasource) commonActivity.getRefObject(new MetaIdentifier(MetaType.datasource, datasourceUUID, datasourceVersion));
//			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			
			String filepath = datastore.getLocation();
			/*String dbName = datasource.getDbname();*/	
			IExecutor executor = null;
			String tableName = Helper.genTableName(filepath);
			ExecContext execContext = null;
			String appUuid = null;
			appUuid = commonServiceImpl.getApp().getUuid();
			
//			if (runMode.equals(RunMode.ONLINE)) {
//				execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark") || engine.getExecEngine().equalsIgnoreCase("livy_spark"))
//								? helper.getExecutorContext(engine.getExecEngine()) : helper.getExecutorContext(ExecContext.spark.toString());
//			} else {
//				execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
//			}
			
//			execContext = commonServiceImpl.getExecContext(runMode);
//			executor = execFactory.getExecutor(execContext.toString());
			execContext = ExecContext.spark;
			executor = execFactory.getExecutor(execContext.toString());
			
//			exec = execFactory.getExecutor(execContext.toString());
//			String hdfsLocation = String.format("%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath());
			Datasource datasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(datapod.getDatasource().getRef().getUuid(), 
																							datapod.getDatasource().getRef().getVersion(), 
																							datapod.getDatasource().getRef().getType().toString(), "N");
			String hdfsLocation = datasource.getPath();
			if (filepath != null && !filepath.contains(hdfsLocation)) {
				filepath = String.format("%s%s", hdfsLocation, filepath);
			}
			
			/*IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());*/	
			executor.registerDatapod(tableName, datapod, datastore, execContext, appUuid);
			/*dataFrameService.registerDatapod(conn, tableName, datapod, datasource, datastore, iReader, execContext);*/
			/*ConnectionHolder conHolder = conn.getConnection();
			Object obj = conHolder.getStmtObject();
			if(obj instanceof HiveContext  && !execContext.equals(ExecContext.livy_spark))	{

				//DataFrameHolder dataFrameHolder = iReader.read(dp, dataStoreDetails, hdfsInfo,obj, ds);
				DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo,obj, datasource_2);
				DataFrame df = dataFrameHolder.getDataframe();
				tableName = dataFrameHolder.getTableName();
				String []tablenameList = ((HiveContext)obj).tableNames();
				boolean tableFound = false;
				if (tablenameList != null && tablenameList.length > 0) {
					for (String tname : tablenameList) {
						if (tname.equals(tableName)) {
							tableFound = true;
							break;
						}
					}
				} 
				if (!tableFound) {
				    df.persist(StorageLevel.MEMORY_AND_DISK());
					HiveContext hiveContext = (HiveContext) conHolder.getStmtObject();
					hiveContext.registerDataFrameAsTable(df, tableName);
					logger.info("datapodRegister: Registering datapod " + tableName);
					df.printSchema();
				}
			}*/ 
			if (execContext.equals(ExecContext.livy_spark)) {
				executor.registerDatapod(filepath, tableName, appUuid);
			}
		} catch (Exception e) {
			logger.error("Error registering datastore", e);
			e.printStackTrace();
			throw e;
		} catch (AssertionError e) {
			logger.error("Error registering datastore");
			e.printStackTrace();
			throw e;
		}

	}
	
}

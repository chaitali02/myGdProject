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
	
	/*public DatapodRegister(MetadataUtil commonActivity, DataSourceFactory dataSourceFactory, HDFSInfo hdfsInfo, ConnectionFactory conFactory)
			throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		
		 * Loader load = (Loader) loader; // Bhanu - JIRA FW-3 - Remove cache
		 * //load.load(); // End JIRA FW-3 Changes load.regiterDataPod();
		 

		
		 * List<DagExec> dagExecList = commonActivity.getDagExecDao().findAll();
		 * for(DagExec dagExec:dagExecList){ for(DagExec.Stage
		 * stage:dagExec.getStages()){ for( DagExec.Stage.Task task :
		 * stage.getTasks()) {
		 

		// Looping the datastore instead of DagExec
		MetaIdentifier metaIdentifier = new MetaIdentifier();
		List<Datapod> datapodList = commonActivity.getDatapodDao().findAll();
		// List<DataStore> dataStoreList =
		// commonActivity.getDatastoreDao().findAll();
		// for(DataStore datastores : dataStoreList){
		for (Datapod datapod : datapodList) {
			String tableName = "";
			try {
				String datapodUUID = datapod.getUuid();
				String datapodVersion = datapod.getVersion();
				List<DataStore> dataStoreList = commonActivity.getDatastoreDao().findAllDataStoreByMeta(datapodUUID);
				for (DataStore datastore : dataStoreList) {
				//DataStore datastore = commonActivity.getDatastoreDao().findDataStoreByMeta(datapodUUID, datapodVersion);
				metaIdentifier.setType(MetaType.datastore);
				metaIdentifier.setUuid(datastore.getUuid());
				metaIdentifier.setVersion(datastore.getVersion());
				DataStore dataStoreDetails = (DataStore) commonActivity.getRefObject(metaIdentifier);
				
				 * String filePath = dataStoreDetails.getLocation(); String
				 * hdfsLocation = String.format("%s%s", hdfsInfo.getHdfsURL(),
				 * hdfsInfo.getSchemaPath());
				 

				Datapod dp = (Datapod) commonActivity
						.getRefObject(new MetaIdentifier(MetaType.datapod, datapodUUID, datapodVersion));
				String dataSourceUUID = dp.getDatasource().getRef().getUuid();
				String dataSourceVersion = dp.getDatasource().getRef().getVersion();
				Datasource ds = (Datasource) commonActivity
						.getRefObject(new MetaIdentifier(MetaType.datasource, dataSourceUUID, dataSourceVersion));
				// Datasource dataSourceType =
				// dataSourceServiceImpl.getDatasourceByType(dataSourceUUID);
				// String dataSourceType = ds.getType();
			//	DataSourceFactory dataSourcefactory = new DataSourceFactory();
//				DataframeHolder dataFrameHolder = dataSourcefactory.getDatapodReader(dp, ds, dataStoreDetails, hdfsInfo,
//						sqlContext);
				IReader iReader = dataSourceFactory.getDatapodReader(dp, commonActivity);	
				String type = ds.getType();
				IConnector conn = conFactory.getConnector(ExecContext.spark.toString());
				ConnectionHolder conHolder = conn.getConnection();
				Object obj = conHolder.getStmtObject();
				if(obj instanceof HiveContext)
				{
				DataFrameHolder dataFrameHolder = iReader.read(dp, dataStoreDetails, hdfsInfo,obj, ds);
				DataFrame df = dataFrameHolder.getDataframe();
				tableName = dataFrameHolder.getTableName();				
			    df.persist(StorageLevel.MEMORY_AND_DISK());
				//df.cache();
				System.out.println("datapodRegister: Registering datapod " + tableName);
				HiveContext hiveContext = (HiveContext) conHolder.getStmtObject();
				hiveContext.registerDataFrameAsTable(df, tableName);
				logger.info("datapodRegister: Registering datapod " + tableName);
				//hiveContext.registerDataFrameAsTable(df, tableName);
				df.printSchemowgli.inferyx.comma();
				}
				}// End for dataStore

				// DataFrame df = null;
				
				 * if(dataSourceType.equals("HIVE")) { String dbName =
				 * ds.getDbname(); df =
				 * sqlContext.sql("select * from "+dbName+"."+dp.getName());
				 * tableName =
				 * Helper.generateTableName(dp.getUuid(),dp.getVersion()); }
				 * else{
				 
				
				 * if (!filePath.contains(hdfsLocation)) { filePath =
				 * String.format("%s%s", hdfsLocation, filePath); }
				 

				// if(tableName.contains("6f4c3ae9_f431_413a_9b5c_dabe15ccd6a6_1467825815_1467825815")
				// ||
				// tableName.contains("9e1e7356_1652_47b8_a853_0cf31b010ed6_1467586144")){
				
				 * df = sqlContext.read().load(filePath); tableName =
				 * Helper.generateTableName(filePath);
				 
				// }

				// df.cache();

				
				 * System.out.println("datapodRegister: Registering datapod "+
				 * tableName); sqlContext.registerDataFrameAsTable(df,
				 * tableName);
				 mowgli.inferyx.com
				/// df.printSchema();
				// }
				// System.out.println("datapodRegister: Count "+df.count());

			} catch (Exception e) {
				logger.error("datapodRegister: Error registering datapod:" + tableName);
				e.printStackTrace();

			} catch (AssertionError e) {
				logger.error("datapodRegister: Error registering datapod:" + tableName);
				e.printStackTrace();
			}

		}// End for all datapods
		// }
		// }
	}mowgli.inferyx.com
*/	
	/********************** UNUSED **********************/
	/*public void registerDatapod (String tableName, Datapod dp) {
		try {
			IReader iReader = dataSourceFactory.getDatapodReader(dp, commonActivity);
			String dataSourceUUID = dp.getDatasource().getRef().getUuid();
			String dataSourceVersion = dp.getDatasource().getRef().getVersion();
			Datasource ds = (Datasource) commonActivity.getRefObject(new MetaIdentifier(MetaType.datasource, dataSourceUUID, dataSourceVersion));
			DataStore dataStoreDetails = (DataStore) commonActivity.getDatastoreDao().findDataStoreByMeta(dp.getUuid(), dp.getVersion()); 
			//Datasource datasource = commonServiceImpl.getDatasourceByApp();
			ExecContext execContext = ExecContext.spark;
			IConnector conn = connFactory.getConnector(execContext.toString());			
			dataFrameService.registerDatapod(conn, tableName, dp, ds, dataStoreDetails, iReader, execContext);			
//			ConnectionHolder conHolder = conn.getConnection();
//			Object obj = conHolder.getStmtObject();
//			if(obj instanceof HiveContext)	{
//				DataFrameHolder dataFrameHolder = iReader.read(dp, dataStoreDetails, hdfsInfo,obj, ds);
//				DataFrame df = dataFrameHolder.getDataframe();
//				tableName = dataFrameHolder.getTableName();
//				String []tablenameList = ((HiveContext)obj).tableNames();
//				boolean tableFound = false;
//				if (tablenameList != null && tablenameList.length > 0) {
//					for (String tname : tablenameList) {
//						if (tname.equals(tableName)) {
//							tableFound = true;
//							break;
//						}
//					}
//				}
//				if (!tableFound) {
//				    df.persist(StorageLevel.MEMORY_AND_DISK());
//					//df.cache();
//					HiveContext hiveContext = (HiveContext) conHolder.getStmtObject();
//					hiveContext.registerDataFrameAsTable(df, tableName);
//					logger.info("datapodRegister: Registering datapod " + tableName);
//					//hiveContext.registerDataFrameAsTable(df, tableName);
//					df.printSchema();
//				}
//			}
		} catch (Exception e) {
			logger.error("datapodRegister: Error registering datapod:" + tableName);
			e.printStackTrace();

		} catch (AssertionError e) {
			logger.error("datapodRegister: Error registering datapod:" + tableName);
			e.printStackTrace();
		}
	}*/
	
	/********************** UNUSED 
	 * @throws Exception **********************/
	/*public void registerDatapod (DataStore datastore, Datapod datapod) {
		registerDatapod(datastore, datapod, Mode.BATCH);
	}*/
	
	public void registerDatapod (DataStore datastore, Datapod datapod, RunMode runMode) throws Exception {
		try {
			/*IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);*/
			/*String datasourceUUID = datapod.getDatasource().getRef().getUuid();
			String datasourceVersion = datapod.getDatasource().getRef().getVersion();*/
			//Datasource datasource = (Datasource) commonActivity.getRefObject(new MetaIdentifier(MetaType.datasource, datasourceUUID, datasourceVersion));
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			
			String filepath = datastore.getLocation();
			/*String dbName = datasource.getDbname();*/	
			IExecutor exec = null;
			String tableName = Helper.genTableName(filepath);
			ExecContext execContext = null;
			String appUuid = null;
			appUuid = commonServiceImpl.getApp().getUuid();
			
			if (runMode.equals(RunMode.ONLINE)) {
				execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark") || engine.getExecEngine().equalsIgnoreCase("livy_spark"))
								? helper.getExecutorContext(engine.getExecEngine()) : helper.getExecutorContext(ExecContext.spark.toString());
			} else {
				execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
			}
			exec = execFactory.getExecutor(execContext.toString());
			String hdfsLocation = String.format("%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath());
			if (filepath != null && !filepath.contains(hdfsLocation)) {
				filepath = String.format("%s%s", hdfsLocation, filepath);
			}
			
			/*IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());*/	
			exec.registerDatapod(tableName, datapod, datastore, execContext, appUuid);
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
				exec.registerDatapod(filepath, tableName, appUuid);
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

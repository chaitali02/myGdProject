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
package com.inferyx.framework.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IMapExecDao;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.ResultType;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SaveMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.StorageContext;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.ExecutorFactory;

public class RunMapServiceImpl implements Callable<TaskHolder> {
	@Autowired
	ConnectionFactory connFactory;
	
	protected MapExec mapExec;
	protected List<java.util.Map<String, Object>> data;
	private HDFSInfo hdfsInfo;
	protected DataStoreServiceImpl dataStoreServiceImpl;
	protected Map map;
	protected Dataset<Row> df;
	private ExecutorFactory execFactory;
	private OrderKey datapodKey;
	protected MapExecServiceImpl mapExecServiceImpl;
	protected CommonServiceImpl<?> commonServiceImpl;
	private SecurityServiceImpl securityServiceImpl;
	private RunMode runMode;
	private	Engine engine;
	protected Helper helper;
	protected SessionContext sessionContext;
	private String name;
	private MetaType execType;
	private DatapodServiceImpl datapodServiceImpl;
	
	static final Logger logger = Logger.getLogger(RunMapServiceImpl.class);
	
	/**
	 * @Ganesh
	 *
	 * @return the datapodServiceImpl
	 */
	public DatapodServiceImpl getDatapodServiceImpl() {
		return datapodServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param datapodServiceImpl the datapodServiceImpl to set
	 */
	public void setDatapodServiceImpl(DatapodServiceImpl datapodServiceImpl) {
		this.datapodServiceImpl = datapodServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @Ganesh
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @Ganesh
	 *
	 * @return the execType
	 */
	public MetaType getExecType() {
		return execType;
	}

	/**
	 * @Ganesh
	 *
	 * @param execType the execType to set
	 */
	public void setExecType(MetaType execType) {
		this.execType = execType;
	}

	/**
	 * @return the sessionContext
	 */
	public SessionContext getSessionContext() {
		return sessionContext;
	}

	/**
	 * @param sessionContext the sessionContext to set
	 */
	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	/**
	 * @return the engine
	 */
	public Engine getEngine() {
		return engine;
	}

	/**
	 * @param engine the engine to set
	 */
	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	/**
	 * @return the runMode
	 */
	public RunMode getRunMode() {
		return runMode;
	}

	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

	public SecurityServiceImpl getSecurityServiceImpl() {
		return securityServiceImpl;
	}

	public void setSecurityServiceImpl(SecurityServiceImpl securityServiceImpl) {
		this.securityServiceImpl = securityServiceImpl;
	}

	public MapExecServiceImpl getMapExecServiceImpl() {
		return mapExecServiceImpl;
	}

	public void setMapExecServiceImpl(MapExecServiceImpl mapExecServiceImpl) {
		this.mapExecServiceImpl = mapExecServiceImpl;
	}

	public OrderKey getDatapodKey() {
		return datapodKey;
	}

	public void setDatapodKey(OrderKey datapodKey) {
		this.datapodKey = datapodKey;
	}

	public ExecutorFactory getExecFactory() {
		return execFactory;
	}

	public void setExecFactory(ExecutorFactory execFactory) {
		this.execFactory = execFactory;
	}

	public MapExec getMapExec() {
		return mapExec;
	}

	public void setMapExec(MapExec mapExec) {
		this.mapExec = mapExec;
	} 
	
	public List<java.util.Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<java.util.Map<String, Object>> data) {
		this.data = data;
	}	

	public HDFSInfo getHdfsInfo() {
		return hdfsInfo;
	}

	public void setHdfsInfo(HDFSInfo hdfsInfo) {
		this.hdfsInfo = hdfsInfo;
	}

	public DataStoreServiceImpl getDataStoreServiceImpl() {
		return dataStoreServiceImpl;
	}

	public void setDataStoreServiceImpl(DataStoreServiceImpl dataStoreServiceImpl) {
		this.dataStoreServiceImpl = dataStoreServiceImpl;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}
	
	/**
	 * @return the helper
	 */
	public Helper getHelper() {
		return helper;
	}

	/**
	 * @param helper the helper to set
	 */
	public void setHelper(Helper helper) {
		this.helper = helper;
	}
	@Override
	public TaskHolder call() throws Exception {
		try {
			FrameworkThreadLocal.getSessionContext().set(sessionContext);
			execute();
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			//commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Map execution FAILED.");
			throw new Exception((message != null) ? message : "Map execution FAILED.");
		}
		TaskHolder taskHolder = new TaskHolder(name, new MetaIdentifier(execType, mapExec.getUuid(), mapExec.getVersion()));
		return taskHolder;
	}
		
//	public void run() {
//		try {
//			executeSql();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public CommonServiceImpl<?> getCommonServiceImpl() {
		return commonServiceImpl;
	}

	public void setCommonServiceImpl(CommonServiceImpl<?> commonServiceImpl) {
		this.commonServiceImpl = commonServiceImpl;
	}

	public void execute() throws Exception {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		FrameworkThreadLocal.getSessionContext().set(sessionContext);
		List<Status> statusList = null;
		try{			
			if (Helper.getLatestStatus(statusList) != null 
					&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.RUNNING, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.COMPLETED, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.PAUSE, new Date())))) {
				logger.info(" This process is RUNNING or has been COMPLETED previously or is On Hold. Hence it cannot be rerun. ");
				return;
			}
			long countRows = -1L;
			// Set status to RUNNING
			mapExec = (MapExec) commonServiceImpl.setMetaStatus(mapExec, MetaType.mapExec, Status.Stage.RUNNING);
			
			if (datapodKey.getVersion() == null) {
//				MetaIdentifier datapodKeyRef = new MetaIdentifier(MetaType.datapod, datapodKey.getUUID(), datapodKey.getVersion());
//			    daoRegister.getRefObject(datapodKeyRef);
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodKey.getUUID(), datapodKey.getVersion(), MetaType.datapod.toString(), "N");
			    datapodKey.setVersion(datapod.getVersion());
			}
			// Form file and table name
			String filePath = String.format("/%s/%s/%s", datapodKey.getUUID(), datapodKey.getVersion(), mapExec.getVersion());
			String mapTableName  = null;
			mapTableName = String.format("%s_%s_%s", datapodKey.getUUID().replace("-", "_"), datapodKey.getVersion(), mapExec.getVersion());
			Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersionWithoutAppUuid(datapodKey.getUUID(), datapodKey.getVersion(), MetaType.datapod.toString());
			ExecContext execContext = commonServiceImpl.getExecContext(runMode);
			IExecutor executor = execFactory.getExecutor(execContext.toString());
//			Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
//			IExecutor exec = execFactory.getExecutor(appDatasource.getType());
			Datasource targetDatasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(targetDatapod.getDatasource().getRef().getUuid(), targetDatapod.getDatasource().getRef().getVersion(), targetDatapod.getDatasource().getRef().getType().toString());
			StorageContext storageContext = commonServiceImpl.getStorageContext(targetDatasource);
			
			logger.info("Before map execution ");
			String sql = mapExec.getExec();
         //commented following line for 
		//	mapTableName = datapodServiceImpl.getTableNameByDatapodKey(datapodKey, runMode);
			ResultSetHolder rsHolder = null;		
			Datasource mapDatasource = commonServiceImpl.getDatasourceByObject(map);		
			if (execContext.equals(ExecContext.FILE)) {
				if (storageContext.equals(StorageContext.FILE)) { //For Spark + Spark
					rsHolder = executor.executeRegisterAndPersist(sql, mapTableName, filePath, targetDatapod, SaveMode.APPEND.toString(), true, appUuid);
				}
				else { //For Spark + Mysql
					rsHolder = executor.executeSqlByDatasource(sql, mapDatasource, appUuid);
					if(storageContext.equals(StorageContext.ORACLE)) {
						mapTableName = targetDatasource.getSid().concat(".").concat(targetDatapod.getName());
					} else {
						mapTableName = targetDatasource.getDbname().concat(".").concat(targetDatapod.getName());					
					}
					rsHolder.setTableName(mapTableName);
					rsHolder = executor.persistDataframe(rsHolder, targetDatasource, targetDatapod, SaveMode.APPEND.toString());
					}
			} else { //For Mysql + Mysql
				sql = helper.buildInsertQuery(execContext.toString(), mapTableName, targetDatapod, sql);
				rsHolder = executor.executeSql(sql, appUuid);
			}
			
////			if (!targetDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())
////					&& appDatasource.getType().equalsIgnoreCase(targetDatasource.getType())) {
//			if (!execContext.equals(ExecContext.FILE)) {
//				mapTableName = datapodServiceImpl.getTableNameByDatapodKey(datapodKey, runMode);
//				logger.info("Datapod: "+datapodKey.getUUID());
//				
//				String partitionColls = Helper.getPartitionColumns(targetDatapod);				
//				logger.info("Partition collumns: "+partitionColls);
//				
//				String partitionClause = "";
//				if(partitionColls != null && !partitionColls.isEmpty()) {
//					partitionClause = " PARTITION ( " + partitionColls +" ) ";
//				}
//				
////				if(targetDatasource.getType().equalsIgnoreCase(ExecContext.HIVE.toString())
////						|| targetDatasource.getType().equalsIgnoreCase(ExecContext.IMPALA.toString())) {
////					sql = "INSERT OVERWRITE TABLE " + mapTableName +" "+ partitionClause + " " + sql;
//				if(storageContext.equals(ExecContext.HIVE) || storageContext.equals(StorageContext.IMPALA)) {
//					sql = "INSERT INTO TABLE " + mapTableName +" "+ partitionClause + " " + sql;
//				} else if(storageContext.equals(StorageContext.MYSQL)
//						|| storageContext.equals(StorageContext.ORACLE)
//						|| storageContext.equals(StorageContext.POSTGRES)) {
//						sql = "INSERT INTO " + mapTableName + " " + sql;
//				}
//			}
//
//			logger.info("Running SQL : " + sql);
//			ResultSetHolder rsHolder = null;
//
//			Datasource mapDatasource = commonServiceImpl.getDatasourceByObject(map);
////			if(appDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())
////					&& !targetDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
//			if(execContext.equals(ExecContext.FILE) && !storageContext.equals(StorageContext.FILE)) {
//				rsHolder = exec.executeSqlByDatasource(sql, mapDatasource, appUuid);
//				if(storageContext.equals(StorageContext.ORACLE)) {
//					mapTableName = targetDatasource.getSid().concat(".").concat(targetDatapod.getName());
//				} else {
//					mapTableName = targetDatasource.getDbname().concat(".").concat(targetDatapod.getName());					
//				}
//				rsHolder.setTableName(mapTableName);
//				rsHolder = exec.persistDataframe(rsHolder, targetDatasource, targetDatapod, SaveMode.APPEND.toString());
////			} else if(targetDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
//			} else if(execContext.equals(ExecContext.FILE) && storageContext.equals(StorageContext.FILE)) {
//				rsHolder = exec.executeRegisterAndPersist(sql, mapTableName, filePath, targetDatapod, SaveMode.APPEND.toString(), true, appUuid);
//			} else {
//				rsHolder = exec.executeSqlByDatasource(sql, mapDatasource, appUuid);
//			}
//			
//			if(rsHolder.getType().equals(ResultType.resultset) 
//					&& targetDatasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
//				rsHolder = exec.executeSqlByDatasource("SELECT * FROM " + mapTableName + " WHERE rownum <= " + 100, mapDatasource, appUuid);
//			} else if(rsHolder.getType().equals(ResultType.resultset) &&
//					!targetDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
//					rsHolder = exec.executeSqlByDatasource("SELECT * FROM " + mapTableName + " LIMIT " + 100, mapDatasource, appUuid);
//			}
			
			logger.info("After map execution.");
			// Persist dataStore
			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
//			Map map = (Map) daoRegister.getRefObject(new MetaIdentifier(MetaType.map,mapExec.getDependsOn().getRef().getUuid(),mapExec.getDependsOn().getRef().getVersion()));
			Map map = (Map) commonServiceImpl.getOneByUuidAndVersion(mapExec.getDependsOn().getRef().getUuid(), mapExec.getDependsOn().getRef().getVersion(), MetaType.map.toString(), "N");
			logger.info("Before map persist ");
			dataStoreServiceImpl.setRunMode(runMode);

			map.getTarget().getRef().setVersion(targetDatapod.getVersion());
			countRows = rsHolder.getCountRows();
			dataStoreServiceImpl.create(filePath, mapTableName
					, new MetaIdentifier(MetaType.datapod, map.getTarget().getRef().getUuid(), map.getTarget().getRef().getVersion())
					, new MetaIdentifier(MetaType.mapExec, mapExec.getUuid(), mapExec.getVersion())
					, map.getAppInfo(), map.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, countRows
					, commonServiceImpl.getPersistMode(storageContext),null);
//					, Helper.getPersistModeFromRunMode(runMode.toString()), null);
			logger.info("After map persist ");
			mapExec.setResult(resultRef);
			mapExec = (MapExec) commonServiceImpl.setMetaStatus(mapExec, MetaType.mapExec, Status.Stage.COMPLETED);			
		} catch (Exception e) {
			mapExec = (MapExec) commonServiceImpl.setMetaStatus(mapExec, MetaType.mapExec, Status.Stage.FAILED);			
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.mapExec, mapExec.getUuid(), mapExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Map execution FAILED.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Map execution FAILED.");			
		} 		
	}
	
	/**
	 * Persists to dataStore and construct the Task result 
	 * @param dfTask
	 * @param mapTableName
	 * @param metaDetails
	 * @param resultRef
	 * @throws Exception 
	 */

	/********************** UNUSED **********************/	
	/*private void persistDataStore(DataFrame dfMap, 
									String mapTableName, 
									ExecParams execParams,
									String filePath,
									MetaIdentifier mapKey,
									MapExec mapExec,
									BaseEntity map) throws Exception {
		MetaIdentifierHolder metaDetails = new MetaIdentifierHolder();
		MetaIdentifier metaRef = new MetaIdentifier();
		MetaIdentifier execRef = new MetaIdentifier();
		MetaIdentifierHolder execDetails = new MetaIdentifierHolder();
		MetaIdentifier resultDetails = new MetaIdentifier();
		long count = dfMap.count();		
		// Check if DataSaveMode is Overwrite then overwrite data else append to parquet
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(),filePath);
		IWriter datapodWriter = datasourceFactory.getDatapodWriter(datapod,daoRegister);
		datapodWriter.write(dfMap, filePathUrl,datapod);
		System.out.println("FilePath:"+filePathUrl);
		
		DataStore dataStore = new DataStore();
		
		//Saving dataStore Obj		
	    dataStore.setDesc("Creating datastore for " + mapTableName);
	    dataStore.setName(mapTableName);	
	    dataStore.setPersistMode(ConstantsUtil.PERSIST_MODE_MEMORY_ONLY);
	    metaRef.setType(MetaType.map);
	    metaRef.setUuid(mapKey.getUuid());
	    metaRef.setVersion(mapKey.getVersion());
	    metaDetails.setRef(metaRef);
	    dataStore.setMetaId(metaDetails);
	    dataStore.setNumRows(count);
	    
	    execRef.setType(MetaType.mapExec);
	    execRef.setUuid(mapExec.getUuid());
	    execRef.setVersion(mapExec.getVersion());
	    execDetails.setRef(execRef);
	    dataStore.setExecId(execDetails);
	    List<MetaIdentifierHolder> appInfoList = new ArrayList<MetaIdentifierHolder>();
	    MetaIdentifierHolder appInfo = new MetaIdentifierHolder();
	    MetaIdentifier appMeta = null;
	    if (map.getAppInfo() != null && map.getAppInfo().get(0) != null) {
	    	appMeta = new MetaIdentifier(MetaType.application,map.getAppInfo().get(0).getRef().getUuid(),map.getAppInfo().get(0).getRef().getVersion());
	    }
	    appInfo.setRef(appMeta);
	    appInfoList.add(appInfo);
	    dataStore.setAppInfo(appInfoList);
		
		DataStore ds = dataStoreServiceImpl.save(dataStore);
		
		//Set Task result attr for dagExec
        
        resultDetails.setType(MetaType.datastore);
        resultDetails.setUuid(ds.getUuid());
        resultDetails.setVersion(ds.getVersion());
	
	}// End persistDataStore
*/
}
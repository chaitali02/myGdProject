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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.SaveMode;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.ILoadDao;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.LoadOperator;
import com.inferyx.framework.register.GraphRegister;

@Service
public class LoadServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	ILoadDao iLoadDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	DataSourceFactory datasourceFactory;
	@Autowired
	DataFrameService dataFrameService;
	@Autowired
	private LoadOperator loadOperator;
	@Autowired
	private SparkExecutor<?> sparkExecutor;

	static final Logger logger = Logger.getLogger(LoadServiceImpl.class);

	java.util.Map<String, String> requestMap = new HashMap<String, String>();

	/********************** UNUSED **********************/
	/*
	 * public Load findLatest() { return resolveName(iLoadDao.findLatest(new
	 * Sort(Sort.Direction.DESC, "version"))); }
	 */

	/********************** UNUSED **********************/
	/*
	 * public Load findLatestByUuid(String uuid){ String appUuid =
	 * (securityServiceImpl.getAppInfo() != null &&
	 * securityServiceImpl.getAppInfo().getRef() != null
	 * )?securityServiceImpl.getAppInfo().getRef().getUuid():null; if(appUuid ==
	 * null) { return iLoadDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC,
	 * "version")); } return iLoadDao.findLatestByUuid(appUuid,uuid,new
	 * Sort(Sort.Direction.DESC, "version")); }
	 */

	/********************** UNUSED **********************/
	/*
	 * public Load findOneByUuidAndVersion(String uuid,String version){ String
	 * appUuid = (securityServiceImpl.getAppInfo() != null &&
	 * securityServiceImpl.getAppInfo().getRef() != null
	 * )?securityServiceImpl.getAppInfo().getRef().getUuid():null; if(appUuid !=
	 * null) { if (StringUtils.isBlank(version)) { return
	 * iLoadDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC,
	 * "version")); } return iLoadDao.findOneByUuidAndVersion(appUuid,
	 * uuid,version); } else if (StringUtils.isBlank(version)) { return
	 * iLoadDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version")); }
	 * return iLoadDao.findOneByUuidAndVersion(uuid,version); }
	 */

	public Load save(Load load) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		load.setAppInfo(metaIdentifierHolderList);
		load.setBaseEntity();
		Load loadDet = (Load) iLoadDao.save(load);
		registerGraph.updateGraph((Object) loadDet, MetaType.load);
		return loadDet;
	}

	/********************** UNUSED **********************/
	/*
	 * public List<Load> resolveName(List<Load> load) { List<Load> loadList = new
	 * ArrayList<>(); for(Load loads : load) { String createdByRefUuid =
	 * loads.getCreatedBy().getRef().getUuid(); User user =
	 * userServiceImpl.findLatestByUuid(createdByRefUuid);
	 * loads.getCreatedBy().getRef().setName(user.getName()); loadList.add(loads); }
	 * return loadList; }
	 */

	/********************** UNUSED **********************/
	/*
	 * public Load resolveName(Load load) { String createdByRefUuid =
	 * load.getCreatedBy().getRef().getUuid(); User user =
	 * userServiceImpl.findLatestByUuid(createdByRefUuid);
	 * load.getCreatedBy().getRef().setName(user.getName());
	 * if(load.getTarget().getRef().getType().equals(MetaType.datapod)) { Datapod
	 * datapod=datapodServiceImpl.findLatestByUuid(load.getTarget().getRef().getUuid
	 * ()); load.getTarget().getRef().setName(datapod.getName()); } return load; }
	 */

	/********************** UNUSED **********************/
	/*
	 * public List<Load> findAllLatest() { { //String appUuid =
	 * securityServiceImpl.getAppInfo().getRef().getUuid(); Aggregation LoadAggr =
	 * newAggregation(group("uuid").max("version").as("version"));
	 * AggregationResults<Load> GroupResults =
	 * mongoTemplate.aggregate(LoadAggr,"load", Load.class); List<Load> LoadList =
	 * GroupResults.getMappedResults();
	 * 
	 * // Fetch the relation details for each id List<Load> result=new
	 * ArrayList<Load>(); for(Load gi :LoadList) { Load loadLatest; String appUuid =
	 * (securityServiceImpl.getAppInfo() != null &&
	 * securityServiceImpl.getAppInfo().getRef() != null
	 * )?securityServiceImpl.getAppInfo().getRef().getUuid():null; if(appUuid !=
	 * null) { //String appUuid =
	 * securityServiceImpl.getAppInfo().getRef().getUuid();; loadLatest =
	 * iLoadDao.findOneByUuidAndVersion(appUuid,gi.getId(), gi.getVersion()); } else
	 * { loadLatest = iLoadDao.findOneByUuidAndVersion(gi.getId(), gi.getVersion());
	 * } //logger.debug("datapodLatest is " + datapodLatest.getName());
	 * if(loadLatest != null) { result.add(loadLatest); } } return result; } }
	 */

	/********************** UNUSED **********************/
	/*
	 * public List<Load> findAllLatestActive() { Aggregation loadAggr =
	 * newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where(
	 * "name").ne(null)),group("uuid").max("version").as("version"));
	 * AggregationResults<Load> loadResults =
	 * mongoTemplate.aggregate(loadAggr,"load", Load.class); List<Load> loadList =
	 * loadResults.getMappedResults();
	 * 
	 * // Fetch the load details for each id List<Load> result=new
	 * ArrayList<Load>(); for(Load l : loadList) { Load loadLatest; String appUuid =
	 * (securityServiceImpl.getAppInfo() != null &&
	 * securityServiceImpl.getAppInfo().getRef() != null
	 * )?securityServiceImpl.getAppInfo().getRef().getUuid():null; if(appUuid !=
	 * null) { loadLatest = iLoadDao.findOneByUuidAndVersion(appUuid,l.getId(),
	 * l.getVersion()); } else { loadLatest =
	 * iLoadDao.findOneByUuidAndVersion(l.getId(), l.getVersion()); } if(loadLatest
	 * != null) { result.add(loadLatest); } } return result; }
	 */

	/********************** UNUSED **********************/
	/*
	 * public List<Load> findAll(){ String appUuid =
	 * (securityServiceImpl.getAppInfo() != null &&
	 * securityServiceImpl.getAppInfo().getRef() != null
	 * )?securityServiceImpl.getAppInfo().getRef().getUuid():null; if(appUuid ==
	 * null) { return iLoadDao.findAll(); } return iLoadDao.findAll(appUuid); }
	 */

	/********************** UNUSED **********************/
	/*
	 * public List<Load> findAllByVersion(String uuid) { String appUuid =
	 * (securityServiceImpl.getAppInfo() != null &&
	 * securityServiceImpl.getAppInfo().getRef() != null
	 * )?securityServiceImpl.getAppInfo().getRef().getUuid():null; List<Load>
	 * loadList; if(appUuid != null) { loadList = iLoadDao.findAllVersion(appUuid,
	 * uuid); } else { loadList = iLoadDao.findAllVersion(uuid); } return
	 * resolveName(loadList); }
	 */

	/**********************
	 * UNUSED
	 * @param desc 
	 * 
	 * @throws ParseException
	 * @throws JSONException
	 * @throws JsonProcessingException
	 **********************/
	/*
	 * public MetaIdentifierHolder saveAs(Load load) throws Exception {
	 * MetaIdentifierHolder refMeta = new MetaIdentifierHolder(); MetaIdentifier ref
	 * = new MetaIdentifier(); Load loadNew = new Load();
	 * loadNew.setName(load.getName()+"_copy"); loadNew.setActive(load.getActive());
	 * loadNew.setDesc(load.getDesc()); loadNew.setTags(load.getTags());
	 * loadNew.setAppend(load.getAppend()); loadNew.setHeader(load.getHeader());
	 * loadNew.setSource(load.getSource()); loadNew.setTarget(load.getTarget());
	 * save(loadNew); ref.setType(MetaType.load); ref.setUuid(loadNew.getUuid());
	 * refMeta.setRef(ref); return refMeta; }
	 */

	public void executeSql(LoadExec loadExec, String dagExecVer, String targetTableName, OrderKey datapodKey, RunMode runMode, String desc)
			throws JsonProcessingException, JSONException, ParseException {
		List<Status> statusList = new ArrayList<>();
		Status status = new Status(Status.Stage.PENDING, new Date());
		statusList.add(status);
		Datapod datapod=null;
		try {
			loadExec.setBaseEntity();
			status = new Status(Status.Stage.RUNNING, new Date());
			statusList.add(status);
			loadExec.setStatusList(statusList);
			commonServiceImpl.save(MetaType.loadExec.toString(), loadExec);
//			Load load = (Load) daoRegister.getRefObject(loadExec.getDependsOn().getRef());
			Load load = (Load) commonServiceImpl.getOneByUuidAndVersion(loadExec.getDependsOn().getRef().getUuid(), loadExec.getDependsOn().getRef().getVersion(), loadExec.getDependsOn().getRef().getType().toString(), "N");

			// Form file and table name
			String filePath = String.format("/%s/%s/%s", datapodKey.getUUID(), datapodKey.getVersion(),
					StringUtils.isNotBlank(dagExecVer) ? dagExecVer : loadExec.getVersion());

			String appUuid = commonServiceImpl.getApp().getUuid();
//			datapod = (Datapod) daoRegister.getRefObject(new MetaIdentifier(MetaType.datapod, datapodKey.getUUID(), datapodKey.getVersion()));
			datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodKey.getUUID(), datapodKey.getVersion(), MetaType.datapod.toString(), "N");

			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			Datasource datapodDS = commonServiceImpl.getDatasourceByDatapod(datapod);
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			long count = 0; 
			if(datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())
					|| datasource.getType().equalsIgnoreCase(ExecContext.spark.toString()))	{
				if(datapodDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
					count = exec.loadAndRegister(load, filePath, dagExecVer, loadExec.getVersion(), targetTableName,
							datapod, appUuid);
				}
				else if(datapodDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())  || datapodDS.getType().equalsIgnoreCase(ExecContext.IMPALA.toString())){
					loadExec = (LoadExec) loadOperator.parse(loadExec, null, runMode);
					exec.executeSql(loadExec.getExec(), appUuid);
				}
				else {
					ResultSetHolder rsHolder  = sparkExecutor.uploadCsvToDatabase(load, datapodDS, targetTableName, datapod);				
					count = rsHolder.getCountRows();
				}				
			} else if(datasource.getType().equalsIgnoreCase(ExecContext.HIVE.toString())
					|| datasource.getType().equalsIgnoreCase(ExecContext.IMPALA.toString())
					|| datasource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString())
					|| datasource.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) {
				loadExec = (LoadExec) loadOperator.parse(loadExec, null, runMode);
				exec.executeSql(loadExec.getExec(), appUuid);
				ResultSetHolder rsHolder = exec.executeSql("SELECT COUNT(*) FROM " + targetTableName, appUuid);
				rsHolder.getResultSet().next();
				count = rsHolder.getResultSet().getLong(1);
			} else if(datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {				
				ResultSetHolder rsHolder  = sparkExecutor.uploadCsvToDatabase(load, datapodDS, targetTableName, datapod);				
				count = rsHolder.getCountRows();
			}
			
			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			dataStoreServiceImpl.setRunMode(runMode);
			dataStoreServiceImpl.create(filePath, targetTableName,
					new MetaIdentifier(MetaType.datapod, datapodKey.getUUID(), datapodKey.getVersion()),
					new MetaIdentifier(MetaType.loadExec, loadExec.getUuid(), loadExec.getVersion()), load.getAppInfo(),
					load.getCreatedBy(), SaveMode.Overwrite.toString(), resultRef, count,
					Helper.getPersistModeFromRunMode(runMode.toString()), desc);				

			status = new Status(Status.Stage.COMPLETED, new Date());
			statusList.add(status);
			loadExec.setStatusList(statusList);
			loadExec.setResult(resultRef);
			commonServiceImpl.save(MetaType.loadExec.toString(), loadExec);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			status = new Status(Status.Stage.FAILED, new Date());
			statusList.add(status);
			loadExec.setStatusList(statusList);
			commonServiceImpl.save(MetaType.loadExec.toString(), loadExec);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Persists to dataStore and construct the Task result
	 * 
	 * @param dfTask
	 * @param datapodTableName
	 * @param metaDetails
	 * @param resultRef
	 * @throws IOException
	 * @throws ParseException
	 * @throws NullPointerException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	/*
	 * private void persistDataStore(DataFrame dfDataqual, String datapodTableName,
	 * ExecParams execParams, String filePath, OrderKey datapodKey, LoadExec
	 * loadExec, Load load) throws IOException { MetaIdentifierHolder metaDetails =
	 * new MetaIdentifierHolder(); MetaIdentifier metaRef = new MetaIdentifier();
	 * MetaIdentifier execRef = new MetaIdentifier(); MetaIdentifierHolder
	 * execDetails = new MetaIdentifierHolder(); MetaIdentifier resultDetails = new
	 * MetaIdentifier(); // Check if DataSaveMode is Overwrite then overwrite data
	 * else append to parquet String filePathUrl = String.format("%s%s%s",
	 * hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(),filePath); MetaIdentifier
	 * datapodRef = new MetaIdentifier(MetaType.datapod, datapodKey.getUUID(),
	 * datapodKey.getVersion()); Datapod datapod =
	 * (Datapod)daoRegister.getRefObject(datapodRef); IWriter datapodWriter =
	 * datasourceFactory.getDatapodWriter(datapod,daoRegister);
	 * datapodWriter.write(dfDataqual, filePathUrl, datapod);
	 * System.out.println("FilePath:"+filePathUrl); long count = dfDataqual.count();
	 * DataStore dataStore = new DataStore();
	 * 
	 * //Saving dataStore Obj dataStore.setDesc("Creating datastore for " +
	 * datapodTableName); dataStore.setName(datapodTableName);
	 * metaRef.setType(MetaType.datapod); metaRef.setUuid(datapodRef.getUuid());
	 * metaRef.setVersion(datapodRef.getVersion()); metaDetails.setRef(metaRef);
	 * dataStore.setMetaId(metaDetails); dataStore.setNumRows(count);
	 * 
	 * execRef.setType(MetaType.loadExec); execRef.setUuid(loadExec.getUuid());
	 * execRef.setVersion(loadExec.getVersion()); execDetails.setRef(execRef);
	 * dataStore.setExecId(execDetails); List<MetaIdentifierHolder> appInfoList =
	 * new ArrayList<MetaIdentifierHolder>(); MetaIdentifierHolder appInfo = new
	 * MetaIdentifierHolder(); MetaIdentifier appMeta = null; if (load.getAppInfo()
	 * != null && load.getAppInfo().get(0) != null) { appMeta = new
	 * MetaIdentifier(MetaType.application,
	 * load.getAppInfo().get(0).getRef().getUuid(),
	 * load.getAppInfo().get(0).getRef().getVersion()); } appInfo.setRef(appMeta);
	 * appInfoList.add(appInfo); dataStore.setAppInfo(appInfoList);
	 * dataStore.setLocation(filePath); if (execParams != null &&
	 * execParams.getDimInfo() != null && execParams.getDimInfo().size() > 0) {
	 * dataStore.setDimInfo(execParams.getDimInfo()); // Including Dimension in
	 * DataStore }
	 * 
	 * DataStore ds = dataStoreServiceImpl.Save(dataStore);
	 * 
	 * //Set Task result attr for dagExec
	 * 
	 * resultDetails.setType(MetaType.datastore);
	 * resultDetails.setUuid(ds.getUuid());
	 * resultDetails.setVersion(ds.getVersion());
	 * 
	 * }// End persistDataStore
	 */

	public List<java.util.Map<String, Object>> getLoadResults(String loadExecUUID, String loadExecVersion, int offset,
			int limit, String sortBy, String order, String requestId, RunMode runMode)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {

		List<java.util.Map<String, Object>> data = new ArrayList<>();
		limit = offset + limit;
		offset = offset + 1;
		boolean requestIdExistFlag = false;

		StringBuilder orderBy = new StringBuilder();
		
		LoadExec loadExec = (LoadExec) commonServiceImpl.getOneByUuidAndVersion(loadExecUUID, loadExecVersion,
				MetaType.loadExec.toString());
		DataStore datastore = dataStoreServiceImpl.getDatastore(loadExec.getResult().getRef().getUuid(),
				loadExec.getResult().getRef().getVersion());
		dataStoreServiceImpl.setRunMode(runMode);
		String tableName = dataStoreServiceImpl.getTableNameByDatastore(datastore.getUuid(), datastore.getVersion(),
				runMode);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());

		if (requestId == null || requestId.equals("null") || requestId.isEmpty()) {
			if (datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
					|| datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString()))
//				data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM "
//						+ tableName + ") AS tab WHERE rownum >= " + offset + " AND rownum <= " + limit, null);
				data = exec.executeAndFetch("SELECT * FROM " + tableName + " AS tab limit " + limit, null);
			else if (datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString()))
				data = exec.executeAndFetch("SELECT * FROM " + tableName + " AS tab WHERE rownum <= " + limit, null);
			else
				data = exec.executeAndFetch("SELECT * FROM " + tableName + " AS tab limit " + limit, null);
		} else {
			List<String> orderList = Arrays.asList(order.split("\\s*,\\s*"));
			List<String> sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));

			if (StringUtils.isNotBlank(sortBy) || StringUtils.isNotBlank(order)) {
				for (int i = 0; i < sortList.size(); i++) {
					orderBy.append(sortList.get(i)).append(" ").append(orderList.get(i));
				}
				if (requestId != null) {
					String tabName = null;
					for (java.util.Map.Entry<String, String> entry : requestMap.entrySet()) {
						String id = entry.getKey();
						if (id.equals(requestId)) {
							requestIdExistFlag = true;
						}
					}
					if (requestIdExistFlag) {
						tabName = requestMap.get(requestId);
						if (datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
								|| datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString()))
							data = exec.executeAndFetch("SELECT * FROM " + tabName + " WHERE rownum >= " + offset
									+ " AND rownum <= " + limit, null);
						else if (datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString()))
							data = exec.executeAndFetch(
									"SELECT * FROM " + tableName + " AS tab WHERE rownum <= " + limit, null);
						else
							data = exec.executeAndFetch("SELECT * FROM " + tableName + " AS tab limit " + limit, null);
					} else {
						if (datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
								|| datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString()))
//							data = exec.executeAndFetch(
//									"SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM (SELECT * FROM "
//											+ tableName + " ORDER BY " + orderBy.toString() + ") AS tab) AS tab1",
//									null);
							data = exec.executeAndFetch("SELECT * FROM (SELECT * FROM " + tableName
									+ " AS tab ORDER BY " + orderBy.toString() + ") AS tab1 limit " + limit, null);
						else if (datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString()))
							data = exec.executeAndFetch("SELECT * FROM (SELECT * FROM " + tableName
									+ " AS tab ORDER BY " + orderBy.toString() + ") AS tab1 WHERE rownum <= " + limit,
									null);
						else
							data = exec.executeAndFetch("SELECT * FROM (SELECT * FROM " + tableName
									+ " AS tab ORDER BY " + orderBy.toString() + ") AS tab1 limit " + limit, null);
						tabName = requestId.replace("-", "_");
						requestMap.put(requestId, tabName);
						if (datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
								|| datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString()))
							data = exec.executeAndFetch("SELECT * FROM " + tabName + " WHERE rownum >= " + offset
									+ " AND rownum <= " + limit, null);
						else if (datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString()))
							data = exec.executeAndFetch(
									"SELECT * FROM " + tableName + " AS tab WHERE rownum <= " + limit, null);
						else
							data = exec.executeAndFetch("SELECT * FROM " + tableName + " AS tab limit " + limit, null);
					}
				}
			}
		}
		return data;
	}

	public LoadExec create(String loadUUID, String loadVersion, ExecParams execParams, ParamMap paramMap,
			LoadExec loadExec) throws Exception {
		MetaIdentifierHolder loadRef = new MetaIdentifierHolder();
		Load load = null;

		if (StringUtils.isBlank(loadVersion)) {
			// load = iLoadDao.findLatestByUuid(loadUUID, new Sort(Sort.Direction.DESC,
			// "version"));
			load = (Load) commonServiceImpl.getLatestByUuid(loadUUID, MetaType.load.toString());
			loadVersion = load.getVersion();
		} else {
			// load = iLoadDao.findOneByUuidAndVersion(loadUUID, loadVersion);
			load = (Load) commonServiceImpl.getOneByUuidAndVersion(loadUUID, loadVersion, MetaType.load.toString());
		}
		if (loadExec == null) {
			loadExec = new LoadExec();
			loadRef.setRef(new MetaIdentifier(MetaType.load, loadUUID, loadVersion));
			loadExec.setDependsOn(loadRef);
			loadExec.setBaseEntity();
		}
		loadExec.setExecParams(execParams);
		try {
			//Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			List<Status> statusList = loadExec.getStatusList();
			if (statusList == null) {
				statusList = new ArrayList<Status>();
			}
			loadExec.setName(load.getName());
			loadExec.setAppInfo(load.getAppInfo());
			// iLoadExecDao.save(loadExec);
			commonServiceImpl.save(MetaType.loadExec.toString(), loadExec);

			if (Helper.getLatestStatus(statusList) != null && (Helper.getLatestStatus(statusList)
					.equals(new Status(Status.Stage.RUNNING, new Date()))
					|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.COMPLETED, new Date()))
					|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.PAUSE, new Date())))) {
				logger.info(
						" This process is RUNNING or has been COMPLETED previously or is On Hold. Hence it cannot be rerun. ");
				return loadExec;
			}

			if (Helper.getLatestStatus(statusList) != null 
					&& Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.READY, new Date()))) {
				logger.info("loadExec is in READY state. Run directly. Don't set it to PENDING state again. ");
				return loadExec;
			}
			
			synchronized (loadExec.getUuid()) {
				loadExec = (LoadExec) commonServiceImpl.setMetaStatus(loadExec, MetaType.loadExec, Status.Stage.PENDING);
				loadExec = (LoadExec) commonServiceImpl.setMetaStatus(loadExec, MetaType.loadExec, Status.Stage.STARTING);
				loadExec = (LoadExec) commonServiceImpl.setMetaStatus(loadExec, MetaType.loadExec, Status.Stage.READY);
			}
			// loadExec.setExec(dqOperator.generateSql(dataQual, datapodList, dataQualExec,
			// dagExec, usedRefKeySet));
			// loadExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		} catch (Exception e) {
			logger.error(e);
			loadExec = (LoadExec) commonServiceImpl.setMetaStatus(loadExec, MetaType.loadExec, Status.Stage.FAILED);
			throw new Exception("Load creation FAILED");
		}
		return loadExec;
	}

	/*@Override
	public void execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// Validate input
		if (baseExec == null) {
			throw new Exception("No executable, cannot execute. ");
		}
		// Create datastore 
		DataStore dataStore = new DataStore();
		dataStore.setCreatedBy(baseExec.getCreatedBy());
		// Fetch Load
		Load load = (Load) commonServiceImpl.getOneByUuidAndVersion(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), baseExec.getDependsOn().getRef().getType().toString());
		// Fetch target datapod
		OrderKey datapodKey = load.getTarget().getRef().getKey();
		if (DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()).get(MetaType.datapod + "_" + datapodKey.getUUID()) != null) {
			datapodKey.setVersion(DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()).get(MetaType.datapod + "_" + datapodKey.getUUID()).getVersion());
		} else {
			Datapod targetDatapod = (Datapod) commonServiceImpl
					.getOneByUuidAndVersion(load.getTarget().getRef().getUuid(), load.getTarget().getRef().getVersion(), MetaType.datapod.toString());
			datapodKey.setVersion(targetDatapod.getVersion());
		}
		executeSql((LoadExec) baseExec, datapodTableName, null, datapodKey, dataStore, runMode);
	}*/
	

	public LoadExec execute(String loadUuid, String loadVersion, LoadExec loadExec, ExecParams execParams, RunMode runMode) throws Exception, AnalysisException {
		try {
			Load load = (Load) commonServiceImpl.getOneByUuidAndVersion(loadUuid, loadVersion, MetaType.load.toString());
			MetaIdentifier targetMI = load.getTarget().getRef();
			Datapod targetDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetMI.getUuid(), targetMI.getVersion(), targetMI.getType().toString());
			String targetDpTableName = datapodServiceImpl.genTableNameByDatapod(targetDp, loadExec.getVersion(), runMode);
			executeSql(loadExec, null, targetDpTableName, new OrderKey(targetDp.getUuid(), targetDp.getVersion()), runMode, null);
			return loadExec;
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			loadExec = (LoadExec) commonServiceImpl.setMetaStatus(loadExec, MetaType.loadExec, Status.Stage.FAILED);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.loadExec, loadExec.getUuid(), loadExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Load execution FAILED.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Load execution FAILED.");
		}
	}
}

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.SaveMode;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.dao.ILoadDao;
import com.inferyx.framework.dao.ILoadExecDao;
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
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.register.GraphRegister;

@Service
public class LoadServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	/*
	 * @Autowired JavaSparkContext javaSparkContext;
	 */
	/*@Autowired
	HiveContext hiveContext;*/
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
	private MetadataUtil daoRegister;
	@Autowired
	private ILoadExecDao iLoadExecDao;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired
	DataSourceFactory datasourceFactory;
	@Autowired
	DataFrameService dataFrameService;

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

	public void executeSql(LoadExec loadExec, String dagExecVer, String datapodTableName, OrderKey datapodKey,
			DataStore dataStore/*, Dataset<Row> dfTask*/, RunMode runMode)
			throws JsonProcessingException, JSONException, ParseException {
		List<Status> statusList = new ArrayList<>();
		Status status = new Status(Status.Stage.NotStarted, new Date());
		statusList.add(status);
		try {
			loadExec.setBaseEntity();
			loadExec.setStatusList(statusList);
			status = new Status(Status.Stage.InProgress, new Date());
			statusList.add(status);
			loadExec.setStatusList(statusList);
			// iLoadExecDao.save(loadExec);
			commonServiceImpl.save(MetaType.loadExec.toString(), loadExec);
			Load load = (Load) daoRegister.getRefObject(loadExec.getDependsOn().getRef());

			/*
			 * Dataset<Row> dfTmp =
			 * dataFrameService.getDataFrame(load.getSource().getValue()); // DataFrame
			 * dfTmp =
			 * hiveContext.read().format("com.databricks.spark.csv").option("inferSchema",
			 * "true") // .option("header", "true").load(load.getSource().getValue()); long
			 * count = dfTmp.count(); dataFrameService.registerDataFrameAsTable(dfTmp,
			 * "dfLoadTemp"); //hiveContext.registerDataFrameAsTable(dfTmp, "dfLoadTemp");
			 * 
			 * Datasource datasource = commonServiceImpl.getDatasourceByApp(); //IExecutor
			 * exec = execFactory.getExecutor(ExecContext.spark.toString()); IExecutor exec
			 * = execFactory.getExecutor(datasource.getType()); ResultSetHolder rsHolder =
			 * null; if (datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
			 * || datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
			 * rsHolder = exec.executeSql("SELECT *, "+ ((dagExecVer == null) ?
			 * loadExec.getVersion():dagExecVer) + " AS version FROM dfLoadTemp", null);
			 * dfTask = rsHolder.getDataFrame(); } else { dfTask = dfTmp; } //dfTask =
			 * hiveContext.sql("select *, "+ dagExecVer +
			 * " as version from dfLoadTemp").coalesce(4); dfTask.cache();
			 * hiveContext.registerDataFrameAsTable(dfTask, datapodTableName);
			 */

			// Form file and table name
			String filePath = String.format("/%s/%s/%s", datapodKey.getUUID(), datapodKey.getVersion(),
					StringUtils.isNotBlank(dagExecVer) ? dagExecVer : loadExec.getVersion());

			/*
			 * logger.info("Going to datapodWriter"); dfTask.printSchema(); Datapod datapod
			 * = (Datapod) daoRegister.getRefObject(new MetaIdentifier(MetaType.datapod,
			 * datapodKey.getUUID(), datapodKey.getVersion())); IWriter datapodWriter =
			 * datasourceFactory.getDatapodWriter(datapod, daoRegister); String filePathUrl
			 * = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(),
			 * filePath); datapodWriter.write(dfTask, filePathUrl, datapod,
			 * SaveMode.Overwrite.toString());
			 */

			Datapod datapod = (Datapod) daoRegister
					.getRefObject(new MetaIdentifier(MetaType.datapod, datapodKey.getUUID(), datapodKey.getVersion()));
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			long count = exec.loadAndRegister(load, filePath, dagExecVer, loadExec.getVersion(), datapodTableName,
					datapod, securityServiceImpl.getAppInfo().getRef().getUuid());

			dataStoreServiceImpl.setRunMode(runMode);
			dataStoreServiceImpl.create(filePath, datapodTableName,
					new MetaIdentifier(MetaType.datapod, datapodKey.getUUID(), datapodKey.getVersion()),
					new MetaIdentifier(MetaType.loadExec, loadExec.getUuid(), loadExec.getVersion()), load.getAppInfo(),
					load.getCreatedBy(), SaveMode.Overwrite.toString(), new MetaIdentifierHolder(), count,
					Helper.getPersistModeFromRunMode(runMode.toString()));

			/*
			 * dataStoreServiceImpl.persistDataStore(dfTask, datapodTableName, null,
			 * filePath, new MetaIdentifier(MetaType.datapod, datapodKey.getUUID(),
			 * datapodKey.getVersion()), new MetaIdentifier(MetaType.loadExec,
			 * loadExec.getUuid(), loadExec.getVersion()), null, load.getAppInfo(),
			 * SaveMode.Overwrite.toString(), null, dataStore, runMode);
			 */

			// persistDataStore(dfTask, datapodTableName, null, filePath, datapodKey,
			// loadExec, load);
			status = new Status(Status.Stage.Completed, new Date());
			statusList.add(status);
			loadExec.setStatusList(statusList);
			// iLoadExecDao.save(loadExec);
			commonServiceImpl.save(MetaType.loadExec.toString(), loadExec);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			status = new Status(Status.Stage.Failed, new Date());
			statusList.add(status);
			loadExec.setStatusList(statusList);
			// iLoadExecDao.save(loadExec);
			commonServiceImpl.save(MetaType.loadExec.toString(), loadExec);
			;
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
		DataStore datastore = dataStoreServiceImpl.findDatastoreByExec(loadExecUUID, loadExecVersion);
		String tableName = dataStoreServiceImpl.getTableNameByDatastore(datastore.getUuid(), datastore.getVersion(),
				runMode);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());

		if (requestId == null || requestId.equals("null") || requestId.isEmpty()) {
			if (datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
					|| datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString()))
				data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM "
						+ tableName + ") AS tab WHERE rownum >= " + offset + " AND rownum <= " + limit, null);
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
							data = exec.executeAndFetch(
									"SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM (SELECT * FROM "
											+ tableName + " ORDER BY " + orderBy.toString() + ") AS tab) AS tab1",
									null);
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
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			List<Status> statusList = loadExec.getStatusList();
			if (statusList == null) {
				statusList = new ArrayList<Status>();
			}
			loadExec.setName(load.getName());
			loadExec.setAppInfo(load.getAppInfo());
			// iLoadExecDao.save(loadExec);
			commonServiceImpl.save(MetaType.loadExec.toString(), loadExec);

			if (Helper.getLatestStatus(statusList) != null && (Helper.getLatestStatus(statusList)
					.equals(new Status(Status.Stage.InProgress, new Date()))
					|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date()))
					|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
				logger.info(
						" This process is In Progress or has been completed previously or is On Hold. Hence it cannot be rerun. ");
				return loadExec;
			}

			// loadExec.setExec(dqOperator.generateSql(dataQual, datapodList, dataQualExec,
			// dagExec, usedRefKeySet));
			// loadExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
			loadExec = (LoadExec) commonServiceImpl.setMetaStatus(loadExec, MetaType.loadExec, Status.Stage.NotStarted);
		} catch (Exception e) {
			logger.error(e);
			loadExec = (LoadExec) commonServiceImpl.setMetaStatus(loadExec, MetaType.loadExec, Status.Stage.Failed);
			throw new Exception("Load creation failed");
		}
		return loadExec;
	}

}

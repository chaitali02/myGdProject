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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IDataStoreDao;
import com.inferyx.framework.dao.IDatapodDao;
import com.inferyx.framework.dao.IDatasourceDao;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Key;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Operator;
import com.inferyx.framework.domain.Recon;
import com.inferyx.framework.domain.Report;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.Rule2;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.register.DatapodRegister;
import com.inferyx.framework.register.GraphRegister;

@Service
public class DataStoreServiceImpl {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	/*@Autowired
	HiveContext hiveContext;*/
	@Autowired
	IDatasourceDao iDatasourceDao;
	@Autowired
	IDataStoreDao iDataStoreDao;
	@Autowired
	IDatapodDao iDatapodDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	DagExecServiceImpl dagExecImpl;
	@Autowired
	DatasourceServiceImpl datasourceServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	ExecutorFactory execFactory;
	@Autowired
	DataSourceFactory datasourceFactory;
	@Autowired
	LoadExecServiceImpl loadExecServiceImpl;
	@Autowired
	MapExecServiceImpl mapExecServiceImpl;
	@Autowired
	RuleServiceImpl ruleServiceImpl;
	@Autowired
	RuleExecServiceImpl ruleExecServiceImpl;
	@Autowired
	DataQualExecServiceImpl dqExecServiceImpl;
	@Autowired
	DatapodRegister datapodRegister;
	@Autowired
	DataSourceFactory dataSourceFactory;
	@Autowired
	ConnectionFactory connFactory;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	RunMode runMode;
	@Autowired
	DataFrameService dataFrameService;
	@Autowired
	Engine engine;
	@Autowired
	Helper helper;
	@Autowired
	private SparkExecutor<?> sparkExecutor;
	
	Map<String, String> requestMap = new HashMap<String, String>();
	
	Map<String, List<Map<String, Object>>> requestNewMap = new HashMap<>();

	static final Logger logger = Logger.getLogger(DataStoreServiceImpl.class);
    
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

	/********************** UNUSED **********************/
	/*public DataStore findLatest() {
		return resolveName(iDataStoreDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}
	
	public DataStore findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDataStoreDao.findAllByUuid(appUuid, uuid);
	}

	public DataStore findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDataStoreDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iDataStoreDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}

	public List<DataStore> test(String param1) {
		return iDataStoreDao.test(param1);
	}

	public DataStore findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iDataStoreDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} else {
			return iDataStoreDao.findOneByUuidAndVersion(uuid, version);
		}
	}

	public DataStore getOneByUuidAndVersion(String uuid, String version) {

		return iDataStoreDao.findOneByUuidAndVersion(uuid, version);
	}

	public DataStore findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iDataStoreDao.findOneById(appUuid, id);
		} else
			return iDataStoreDao.findOne(id);
	}

	public List<DataStore> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDataStoreDao.findAll();
		}
		return iDataStoreDao.findAll(appUuid);
	}

	public void delete(String id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		DataStore dataStore = iDataStoreDao.findOneById(appUuid, id);
		dataStore.setActive("N");
		iDataStoreDao.save(dataStore);
		
		 * String ID = dataStore.getId(); iDataStoreDao.delete(ID);
		 * dataStore.exportBaseProperty();
		 

	}
*/
	public DataStore save(DataStore datastore) throws Exception {

		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		if (meta != null) {
			List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
			metaIdentifierHolderList.add(meta);
			datastore.setAppInfo(metaIdentifierHolderList);
		}
		datastore.setBaseEntity();
		DataStore dataStore = iDataStoreDao.save(datastore);
		registerGraph.updateGraph((Object) dataStore, MetaType.datastore);
		return dataStore;
	}

	/*public DataStore resolveName(DataStore dataStore) {
		if (dataStore.getCreatedBy() != null) {
			String createdByRefUuid = dataStore.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			dataStore.getCreatedBy().getRef().setName(user.getName());
		}
		if (dataStore.getAppInfo() != null) {
			for (int i = 0; i < dataStore.getAppInfo().size(); i++) {
				String appUuid = dataStore.getAppInfo().get(i).getRef().getUuid();
				Application application = applicationServiceImpl.findLatestByUuid(appUuid);
				String appName = application.getName();
				dataStore.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		String metaIdType = dataStore.getMetaId().getRef().getType().toString();
		String metaName = null;
		String MetaIdRefUuid = dataStore.getMetaId().getRef().getUuid();
		if (metaIdType.equalsIgnoreCase("datapod")) {
			Datapod datapodDO = datapodServiceImpl.findLatestByUuid(MetaIdRefUuid);
			metaName = datapodDO.getName();
		} else if (metaIdType.equalsIgnoreCase("rule")) {
			Rule ruleDO = ruleServiceImpl.findLatestByUuid(MetaIdRefUuid);
			metaName = ruleDO.getName();
		}
		dataStore.getMetaId().getRef().setName(metaName);
		if (dataStore.getExecId() != null) {
			String ExecIdRefUuid = dataStore.getExecId().getRef().getUuid();
			if (dataStore.getExecId().getRef().getType() == MetaType.loadExec) {
				LoadExec loadexecDO = commonSer.findLatestByUuid(ExecIdRefUuid);
				String loadexecName = loadexecDO.getName();
				dataStore.getExecId().getRef().setName(loadexecName);
			} else if (dataStore.getExecId().getRef().getType() == MetaType.mapExec) {
				MapExec mapexecDO = mapExecServiceImpl.findLatestByUuid(ExecIdRefUuid);
				String mapexecName = mapexecDO.getName();
				dataStore.getExecId().getRef().setName(mapexecName);
			} else if (dataStore.getExecId().getRef().getType() == MetaType.dagExec) {
				DagExec dagexecDO = dagexecServiceImpl.findLatestByUuid(ExecIdRefUuid);
				String dagexecName = dagexecDO.getName();
				dataStore.getExecId().getRef().setName(dagexecName);
			} else if (dataStore.getExecId().getRef().getType() == MetaType.ruleExec) {
				RuleExec ruleExecDO = ruleExecServiceImpl.findLatestByUuid(ExecIdRefUuid);
				String dagexecName = ruleExecDO.getName();
				dataStore.getExecId().getRef().setName(dagexecName);
			} else if (dataStore.getExecId().getRef().getType() == MetaType.dqExec) {
				DataQualExec dqExecDO = dqExecServiceImpl.findLatestByUuid(ExecIdRefUuid);
				String dagexecName = dqExecDO.getName();
				dataStore.getExecId().getRef().setName(dagexecName);
			}
		}
		return dataStore;
	}
*/
	public DataStore findDataStoreByMeta(String uuid, String version)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");

		if (appUuid != null && version != null)
			query.addCriteria(Criteria.where("metaId.ref.uuid").is(uuid)
					.andOperator(Criteria.where("metaId.ref.version").is(version)));
		else if (appUuid != null)
			query.addCriteria(Criteria.where("appInfo.ref.uuid").is(appUuid));

		query.with(new Sort(Sort.Direction.DESC, "version"));
		query.addCriteria(Criteria.where("active").is("Y"));

		List<DataStore> datastoreList = mongoTemplate.find(query, DataStore.class);
		DataStore dataStore = null;
		try {
			if (datastoreList.isEmpty()) {
				Aggregation dataStoreAggr = newAggregation(match(Criteria.where("metaId.ref.uuid").is(uuid)),
						group("uuid").first("uuid").as("uuid").max("version").as("version"));

				AggregationResults<DataStore> datastoreResults = mongoTemplate.aggregate(dataStoreAggr, "datastore",
						DataStore.class);
				datastoreList = datastoreResults.getMappedResults();
			}

			if (datastoreList.size() > 0)
			dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(datastoreList.get(0).getUuid(),
					datastoreList.get(0).getVersion(), MetaType.datastore.toString());

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataStore;
		// return iDataStoreDao.findDataStoreByMeta(appUuid, uuid, version);
	}

	public List<DataStore> findAllLatest() {
		{
			// String appUuid =
			// securityServiceImpl.getAppInfo().getRef().getUuid();
			Aggregation dataStoreAggr = newAggregation(group("uuid").max("version").as("version"));
			AggregationResults<DataStore> datastoreResults = mongoTemplate.aggregate(dataStoreAggr, "datastore",
					DataStore.class);
			List<DataStore> datastoreList = datastoreResults.getMappedResults();

			// Fetch the relation details for each id
			List<DataStore> result = new ArrayList<DataStore>();
			for (DataStore s : datastoreList) {
				DataStore datastoreLatest;
				String appUuid = (securityServiceImpl.getAppInfo() != null
						&& securityServiceImpl.getAppInfo().getRef() != null)
								? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
				if (appUuid != null) {
					// String appUuid =
					// securityServiceImpl.getAppInfo().getRef().getUuid();;
					datastoreLatest = iDataStoreDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
				} else {
					datastoreLatest = iDataStoreDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
				}
				// logger.debug("datapodLatest is " + datapodLatest.getName());
				if (datastoreLatest != null) {
					result.add(datastoreLatest);
				}
			}
			return result;
		}
	}

	public List<DataStore> findAllLatestActive() {
		Aggregation dataStoreAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<DataStore> dataStoreResults = mongoTemplate.aggregate(dataStoreAggr, "dataStore",
				DataStore.class);
		List<DataStore> dataStoreList = dataStoreResults.getMappedResults();

		// Fetch the datastore details for each id
		List<DataStore> result = new ArrayList<DataStore>();
		for (DataStore d : dataStoreList) {
			DataStore dataStoreLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				dataStoreLatest = iDataStoreDao.findOneByUuidAndVersion(appUuid, d.getId(), d.getVersion());
			} else {
				dataStoreLatest = iDataStoreDao.findOneByUuidAndVersion(d.getId(), d.getVersion());
			}
			if (dataStoreLatest != null) {
				result.add(dataStoreLatest);
			}
		}
		return result;
	}

	public List<DataStore> findDataStoreByDatapod(String datapodUUID) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonProcessingException, ParseException {
		// String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Aggregation datastoreAggr = newAggregation(match(Criteria.where("metaId.ref.uuid").is(datapodUUID)),
				group("uuid").max("version").as("version"));
		AggregationResults<DataStore> datastoreResults = mongoTemplate.aggregate(datastoreAggr, "datastore",
				DataStore.class);
		List<DataStore> datastoreList = datastoreResults.getMappedResults();

		// Fetch the datapod details for each id

		List<DataStore> result = new ArrayList<DataStore>();
		for (DataStore datastore : datastoreList) {
			DataStore resolvedDs = (DataStore) commonServiceImpl.getOneByUuidAndVersion(datastore.getId(), datastore.getVersion(),MetaType.datastore.toString());
			result.add(resolvedDs);
		}
		return result;
	}

	// DataStore implementation

	/*
	 * public DataStore findDataStoreByMeta(String metaUUID, String metaVersion)
	 * { return iDataStoreDao.findDataStoreByMeta(metaUUID,metaVersion); }
	 */
	
	public String getTableNameByDatapod(Key sourceAttr, RunMode runMode) throws Exception {
		//logger.info("sourceAttr ::: " + sourceAttr);
		DataStore datastore = new DataStore();
		String dataStoreMetaUUID = sourceAttr.getUUID();
		String dataStoreMetaVer = sourceAttr.getVersion();
		
		Datapod dp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dataStoreMetaUUID, dataStoreMetaVer, MetaType.datapod.toString());
		if (dp == null) {
			//dp = datapodServiceImpl.findLatestByUuid(dataStoreMetaUUID);
			dp = (Datapod) commonServiceImpl.getLatestByUuid(dataStoreMetaUUID, MetaType.datapod.toString());
		}
		String datasource = dp.getDatasource().getRef().getUuid();
		//Datasource ds = datasourceServiceImpl.findLatestByUuid(datasource);
		Datasource ds = (Datasource) commonServiceImpl.getLatestByUuid(datasource, MetaType.datasource.toString());
		String dsType = ds.getType();
		if (/*!engine.getExecEngine().equalsIgnoreCase("livy-spark")
				&& !dsType.equalsIgnoreCase(ExecContext.spark.toString()) 
				&& */!dsType.equalsIgnoreCase(ExecContext.FILE.toString())) {
			String tableName = ds.getDbname() + "." + dp.getName();
			return tableName;
		} 
		datastore = findLatestByMeta(dataStoreMetaUUID, dataStoreMetaVer);
		if (datastore == null) {
			//logger.error("No data found for datapod " + dp.getUuid());
			logger.error("No data found for datapod "+dp.getName()+".");
			throw new Exception("No data found for datapod "+dp.getName()+".");
		}
		return getTableNameByDatastore(datastore.getUuid(), datastore.getVersion(), runMode);
	}

	/*public DataFrame getDataFrameByDatapod(String datapodUUID , String datapodVersion) throws Exception {
		Datapod datapod = new Datapod();
		if (datapodVersion != null) {
			//datapod = datapodServiceImpl.findOneByUuidAndVersion(datapodUUID,datapodVersion);
			datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodUUID, datapodVersion, MetaType.datapod.toString());
		}
		else {
			//datapod = datapodServiceImpl.findLatestByUuid(datapodUUID);
			datapod = (Datapod) commonServiceImpl.getLatestByUuid(datapodUUID, MetaType.datapod.toString());
		}
		DataStore datastore = findLatestByMeta(datapod.getUuid(), datapod.getVersion());
		if (datastore == null) {
			logger.error("Datastore is not available for this datapod");
			throw new Exception();
		}
			IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);
			String datasourceUUID = datapod.getDatasource().getRef().getUuid();
			String datasourceVersion = datapod.getDatasource().getRef().getVersion();
			Datasource datasource = (Datasource) commonActivity.getRefObject(new MetaIdentifier(MetaType.datasource, datasourceUUID, datasourceVersion));
			Datasource datasource_2 = commonServiceImpl.getDatasourceByApp();
			IConnector conn = connFactory.getConnector(datasource_2.getType().toLowerCase());
			ConnectionHolder cPAUSEer = conn.getConnection();
			Object obj = cPAUSEer.getConObject();
			if(obj instanceof HiveContext)
			{
				DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo,obj, datasource);
				DataFrame df = dataFrameHolder.getDataframe();
				return df;
			}
		return null;
	}*/
	
	/*public DataFrame getDataFrameByDataset(String uuid, String version) throws Exception {
		// TODO Auto-generated method stub
		Dataset dataset = new Dataset();
		if (version != null) {
			//dataset = datasetServiceImpl.findOneByUuidAndVersion(uuid,version);
			dataset = (Dataset) commonServiceImpl.getOneByUuidAndVersion(uuid,version, MetaType.dataset.toString());
		}
		else {
			//dataset = datasetServiceImpl.findLatestByUuid(uuid);
			dataset = (Dataset) commonServiceImpl.getLatestByUuid(uuid, MetaType.dataset.toString());
		}
		
		List<Map<String, Object>> data = new ArrayList<>();	
		String sql = datasetOperator.generateSql(dataset, null, null,new HashSet<>(), null);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		ResultSetHolder rsHolder = null;
		rsHolder = exec.executeSql(sql);
		DataFrame df = rsHolder.getDataFrame();	
		return null;
	}*/
	
	/*public DataFrame getDataFrameByDataStore(String datastoreUUID, String datastoreVersion, Datapod datapod) throws Exception {
		//DataStore datastore = findOneByUuidAndVersion(datastoreUUID, datastoreVersion);
		DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(datastoreUUID, datastoreVersion, MetaType.datastore.toString());
		if (datastore == null) {
			logger.error("Datastore is not available for this datapod");
			throw new Exception();
		}
			IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);
			String datasourceUUID = datapod.getDatasource().getRef().getUuid();
			String datasourceVersion = datapod.getDatasource().getRef().getVersion();
			Datasource datasource = (Datasource) commonActivity.getRefObject(new MetaIdentifier(MetaType.datasource, datasourceUUID, datasourceVersion));
			Datasource datasource_2 = commonServiceImpl.getDatasourceByApp();
			IConnector conn = connFactory.getConnector(datasource_2.getType().toLowerCase());
			ConnectionHolder cPAUSEer = conn.getConnection();
			
			Object obj = cPAUSEer.getConObject();
			if(obj instanceof HiveContext)
			{
				DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo,obj, datasource);
				DataFrame df = dataFrameHolder.getDataframe();
				return df;
			}
		return null;
	}*/
	

	/**
	 * 
	 * @param dataStoreUUID
	 * @param dataStoreVersion
	 * @param runMode
	 * @return
	 * @throws JsonProcessingException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NullPointerException
	 * @throws ParseException
	 */
	public Datasource getDatapodByDatastore(String dataStoreUUID, String dataStoreVersion, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		DataStore dataStore = null;
		dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(dataStoreUUID, dataStoreVersion, MetaType.datastore.toString());
		String metaid = dataStore.getMetaId().getRef().getUuid();
		String metaV = dataStore.getMetaId().getRef().getVersion();
		MetaType metaType = dataStore.getMetaId().getRef().getType();
		Datasource datasource = null;
		if (metaType == MetaType.datapod) {
			Datapod dp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(metaid, metaV, MetaType.datapod.toString());
			if (dp == null) {
				dp = (Datapod) commonServiceImpl.getLatestByUuid(metaid, MetaType.datapod.toString());
			}
			datasource = commonServiceImpl.getDatasourceByDatapod(dp);
		}
		return datasource;
	}
	// generating table Name from dataSource
	public String getTableNameByDatastore(String dataStoreUUID, String dataStoreVersion, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String tableName = null;
		DataStore dataStore = null;
		dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(dataStoreUUID, dataStoreVersion, MetaType.datastore.toString());
		String filePath = dataStore.getLocation();
		String hdfsLocation = String.format("%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath());
		String metaid = dataStore.getMetaId().getRef().getUuid();
		String metaV = dataStore.getMetaId().getRef().getVersion();
		MetaType metaType = dataStore.getMetaId().getRef().getType();
		Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
		String dsType = appDatasource.getType();
		if (metaType == MetaType.datapod) {
			Datapod dp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(metaid, metaV, MetaType.datapod.toString());
			if (dp == null) {
				dp = (Datapod) commonServiceImpl.getLatestByUuid(metaid, MetaType.datapod.toString());
			}
			Datasource dpDatasource = commonServiceImpl.getDatasourceByDatapod(dp);
			dsType = dpDatasource.getType();
			/*if((engine.getExecEngine().equalsIgnoreCase("livy-spark"))) {
				filePath = String.format("%s%s", hdfsLocation, filePath);
				tableName = Helper.genTableName(filePath);
			} else */if(runMode != null && runMode.equals(RunMode.ONLINE)) {
				if((appDatasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
							|| appDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString()))
						&& !(dpDatasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
							|| dpDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString()))) {
					filePath = String.format("%s%s", hdfsLocation, filePath);
					tableName = Helper.genTableName(filePath);
				} /*else if(!(dpDatasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
							|| dpDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString()))) {
					tableName = dpDatasource.getDbname() + "." + dp.getName();
				}*/ else {
					filePath = String.format("%s%s", hdfsLocation, filePath);
					tableName = Helper.genTableName(filePath);
				}
			} else {
				if ((/*dsType.equalsIgnoreCase(ExecContext.spark.toString()) 
						||*/ dsType.equalsIgnoreCase(ExecContext.FILE.toString()))) {
					if (!filePath.contains(hdfsLocation)) {
						filePath = String.format("%s%s", hdfsLocation, filePath);
					}
					tableName = Helper.genTableName(filePath);				
				} else {
					tableName = dpDatasource.getDbname() + "." + dp.getName();
					if(tableName.startsWith("."))
						tableName = tableName.substring(1);
				}
			}
			if (dataStore.getPersistMode() == null || !dataStore.getPersistMode().equals("MEMORY_ONLY")) {
				datapodRegister.registerDatapod(dataStore, dp, runMode);
			}	
		} else if (metaType == MetaType.rule) {
			Rule rule = (Rule) commonServiceImpl.getOneByUuidAndVersion(metaid, metaV, MetaType.rule.toString());
			String ruleName = rule.getName();
			if(ruleName.toLowerCase().contains("rule_"))
				ruleName = ruleName.replace("rule_", "");
			
			/*if((engine.getExecEngine().equalsIgnoreCase("livy-spark"))) {
				tableName = Helper.genTableName(filePath);
			} else*/ if(runMode != null && runMode.equals(RunMode.ONLINE)) {
				tableName = Helper.genTableName(filePath);
			}else
			if ((/*dsType.equalsIgnoreCase(ExecContext.spark.toString()) 
					||*/ dsType.equalsIgnoreCase(ExecContext.FILE.toString())))
					tableName = Helper.genTableName(filePath);
				else
					tableName = appDatasource.getDbname() + "." + ruleName;
		}else if (metaType == MetaType.rule2) {
			Rule2 rule2 = (Rule2) commonServiceImpl.getOneByUuidAndVersion(metaid, metaV, MetaType.rule2.toString());
			String ruleName = rule2.getName();
			if(ruleName.toLowerCase().contains("rule2_"))
				ruleName = ruleName.replace("rule2_", "");
			
			/*if((engine.getExecEngine().equalsIgnoreCase("livy-spark"))) {
				tableName = Helper.genTableName(filePath);
			} else*/ if(runMode != null && runMode.equals(RunMode.ONLINE)) {
				tableName = Helper.genTableName(filePath);
			}else
			if ((/*dsType.equalsIgnoreCase(ExecContext.spark.toString()) 
					||*/ dsType.equalsIgnoreCase(ExecContext.FILE.toString())))
					tableName = Helper.genTableName(filePath);
				else
					tableName = appDatasource.getDbname() + "." + ruleName;
		} else if (metaType == MetaType.recon) {
			Recon recon = (Recon) commonServiceImpl.getOneByUuidAndVersion(metaid, metaV, MetaType.recon.toString());
			String reconName = recon.getName();
			if(reconName.toLowerCase().contains("recon_"))
				reconName = reconName.replace("recon_", "");
			
			/*if((engine.getExecEngine().equalsIgnoreCase("livy-spark"))) {
				tableName = Helper.genTableName(filePath);
			} else */if(runMode != null && runMode.equals(RunMode.ONLINE)) {
				tableName = Helper.genTableName(filePath);
			}else
			if ((/*dsType.equalsIgnoreCase(ExecContext.spark.toString()) 
					||*/ dsType.equalsIgnoreCase(ExecContext.FILE.toString())))
					tableName = Helper.genTableName(filePath);
				else
					tableName = appDatasource.getDbname() + "." + reconName;
		} else if (metaType == MetaType.operator) {
			Operator recon = (Operator) commonServiceImpl.getOneByUuidAndVersion(metaid, metaV, MetaType.operator.toString());
			String operatorName = recon.getName();
			if(operatorName.toLowerCase().contains("operator_"))
				operatorName = operatorName.replace("operator_", "");
			
			/*if((engine.getExecEngine().equalsIgnoreCase("livy-spark"))) {
				tableName = Helper.genTableName(filePath);
			} else */if(runMode != null && runMode.equals(RunMode.ONLINE)) {
				tableName = Helper.genTableName(filePath);
			}else
			if ((/*dsType.equalsIgnoreCase(ExecContext.spark.toString()) 
					||*/ dsType.equalsIgnoreCase(ExecContext.FILE.toString())))
					tableName = Helper.genTableName(filePath);
				else
					tableName = appDatasource.getDbname() + "." + operatorName;
		} else if (metaType == MetaType.report) {
			Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(metaid, metaV, MetaType.report.toString());
			String reportName = report.getName();
			if(reportName.toLowerCase().contains("report"))
				reportName = reportName.replace("report_", "");
			
			if(runMode != null && runMode.equals(RunMode.ONLINE)) {
				tableName = Helper.genTableName(filePath);
			} else
			if ((dsType.equalsIgnoreCase(ExecContext.spark.toString()) 
					|| dsType.equalsIgnoreCase(ExecContext.FILE.toString())))
					tableName = Helper.genTableName(filePath);
				else
					tableName = appDatasource.getDbname() + "." + reportName;
		} else if (metaType == MetaType.ingest) {
			String[] list = filePath.split("/");	
//			Ingest ingest = (Ingest) commonServiceImpl.getLatestByUuid( dataStore.getMetaId().getRef().getUuid(), dataStore.getMetaId().getRef().getType().toString());
			if(runMode != null && runMode.equals(RunMode.ONLINE)) {
				tableName = String.format("%s_%s_%s", list[list.length-3].replaceAll("-", "_"), list[list.length-2], list[list.length-1]);
			} else {
				tableName = String.format("%s_%s_%s", list[list.length-3].replaceAll("-", "_"), list[list.length-2], list[list.length-1]);
			}
		} else if (metaType == MetaType.vizpod) {
			tableName = Helper.genTableName(dataStore.getLocation());
		}
		return tableName;
	}

	public List<Map<String, Object>> getDatapodSample(String datapodUUID, String datapodVersion, int rows, RunMode runMode) throws Exception {
		setRunMode(runMode);
		DataStore ds = findDataStoreByMeta(datapodUUID, datapodVersion);
	
		if (ds == null) {
			logger.error("Datastore is not available for this datapod");			
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.datastore, datapodUUID, datapodVersion));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Datastore is not available for this datapod.", dependsOn);
			throw new RuntimeException("Datastore is not available for this datapod.");
			
		}
		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.sample.maxrows"));
		if(rows > maxRows) {
			logger.error("Number of rows "+rows+" exceeded. Max row allow "+maxRows);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.datastore, datapodUUID, datapodVersion));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Number of rows "+rows+" exceeded. Max row allow "+maxRows, dependsOn);
			throw new RuntimeException("Number of rows "+rows+" exceeded. Max row allow "+maxRows);
		}
		List<Map<String, Object>> results = getDatapodResultsByDatapodDatasource(ds.getUuid(), ds.getVersion(), null, 0, rows,rows, null, null, null, runMode);
		return results;

	}	

	public List<Map<String, Object>> getDatapodResults(String uuid, String version, String format,
			int offset, int limit, HttpServletResponse response, int rowLimit, String sortBy, String order, String requestId, RunMode runMode) throws Exception {
		/*if (download == null) {
			download = "N";
		}*/		
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			//DataStore ds = findOneByUuidAndVersion(uuid, version);
			DataStore ds = (DataStore) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.datastore.toString());
			String tn = getTableNameByDatastore(ds.getUuid(), ds.getVersion(), runMode);
			Datasource datasource = getDatapodByDatastore(ds.getUuid(), ds.getVersion(), runMode);
			logger.info("Table name:" + tn);
			//String dpUuuid = ds.getMetaId().getRef().getUuid();
			//String dsType = null;
			boolean requestIdExistFlag = false;
			
			List<String> orderList = new ArrayList<>();
			List<String> sortList = new ArrayList<>();
			if(StringUtils.isNotBlank(order)) {	
			 orderList = Arrays.asList(order.split("\\s*,\\s*"));
			}
			if(StringUtils.isNotBlank(sortBy)) {
			 sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));
			}
			
			StringBuilder orderBy = new StringBuilder();
			
			ExecContext execContext = null;
			
			/*if (ds.getMetaId().getRef().getType() == MetaType.datapod) {
				//Datapod dp = datapodServiceImpl.findLatestByUuid(dpUuuid);
				//Datapod dp = (Datapod) commonServiceImpl.getLatestByUuid(dpUuuid, MetaType.datapod.toString());
				//String dsUuid = dp.getDatasource().getRef().getUuid();
				//Datasource dataSource = datasourceServiceImpl.findLatestByUuid(dsUuid);
				//Datasource dataSource = (Datasource) commonServiceImpl.getLatestByUuid(dsUuid, MetaType.datasource.toString());
				//dsType = dataSource.getType();
			}*/
			if (datasource == null) {
				datasource = commonServiceImpl.getDatasourceByApp();
			}
			if (runMode == null || runMode.equals(RunMode.ONLINE)) {
				execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark") || engine.getExecEngine().equalsIgnoreCase("livy_spark"))
						? helper.getExecutorContext(engine.getExecEngine()) : helper.getExecutorContext(ExecContext.spark.toString());
			} else {
				execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
			}
			IExecutor exec = execFactory.getExecutor(execContext.toString());

			limit = offset + limit;
			offset = offset + 1;
			String appUuid = null;
			appUuid = commonServiceImpl.getApp().getUuid();

//			if (sortBy.equals("null") || sortBy.isEmpty() && order.equals("null") || order.isEmpty() && requestId.equals("null") || requestId.isEmpty()) {
			if (StringUtils.isNotBlank(sortBy) || StringUtils.isNotBlank(order) ) {
				for (int i = 0; i < sortList.size(); i++) {
					orderBy.append(sortList.get(i)).append(" ").append(orderList.get(i));
				}
				if (requestId != null) {
					//String tabName = null;
					for (Map.Entry<String, String> entry : requestMap.entrySet()) {
						String id = entry.getKey();
						if (id.equals(requestId)) 
							requestIdExistFlag = true;
					}
					if (requestIdExistFlag) {
						tn = requestMap.get(requestId);
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
//							data = exec.executeAndFetch("SELECT * FROM " + tn + " WHERE rownum >= " + offset + " AND rownum <= " + limit, appUuid);
							data = exec.executeAndFetch("SELECT * FROM " + tn + " LIMIT " + limit, appUuid);
						} else 
							if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
								if(engine.getExecEngine().equalsIgnoreCase("livy-spark")
										|| datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
										|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
									data = exec.executeAndFetch("SELECT * FROM " + tn + " LIMIT " + limit, appUuid);
								} else {
									data = exec.executeAndFetch("SELECT * FROM " + tn + " WHERE rownum< " + limit, appUuid);
								}
							} else {
								data = exec.executeAndFetch("SELECT * FROM " + tn + " LIMIT " + limit, appUuid);
							}
					} else {
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
//							data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM (SELECT * FROM "
//									+ tn + " ORDER BY " + orderBy.toString() + ") AS tab) AS tab1", appUuid);
							data = exec.executeAndFetch("SELECT * FROM " + tn + " LIMIT " + limit, appUuid);
						} else {
							if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
								if(engine.getExecEngine().equalsIgnoreCase("livy-spark")
										|| datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
										|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
									data = exec.executeAndFetch("SELECT * FROM " + tn + " LIMIT " + limit, appUuid);
								} else {
									data = exec.executeAndFetch("SELECT * FROM " + tn + " WHERE rownum< " + limit, appUuid);
								}
							} else {
								data = exec.executeAndFetch("SELECT * FROM "+ tn + " LIMIT ", appUuid);
							}
						}
						// tabName = exec.registerTempTable(dfSorted,
						// requestId.replace("-", "_"));
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							tn = requestId.replace("-", "_");
						}
						requestMap.put(requestId, tn);
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
//							data = exec.executeAndFetch("SELECT * FROM " + tn + " WHERE rownum >= " + offset + " AND rownum <= " + limit, null);
							data = exec.executeAndFetch("SELECT * FROM " + tn + " LIMIT " + limit, appUuid);
						} else {
							if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
								if(engine.getExecEngine().equalsIgnoreCase("livy-spark")
										|| datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
										|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
									data = exec.executeAndFetch("SELECT * FROM " + tn + " LIMIT " + limit, appUuid);
								} else {
									data = exec.executeAndFetch("SELECT * FROM " + tn + " WHERE rownum< " + limit, appUuid);
								}
							} else {
								data = exec.executeAndFetch("SELECT * FROM " + tn + " LIMIT " + limit, null);
							}
						}
					}
				}
			} else {
				if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
//					data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM " + tn
//							+ ") AS tab WHERE rownum >= " + offset + " AND rownum <= " + limit, appUuid);
					data = exec.executeAndFetch("SELECT * FROM " + tn + " LIMIT " + limit, appUuid);
				} else {
					if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
						if(engine.getExecEngine().equalsIgnoreCase("livy-spark")
								|| datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							data = exec.executeAndFetch("SELECT * FROM " + tn + " LIMIT " + limit, appUuid);
						} else {
							data = exec.executeAndFetch("SELECT * FROM " + tn + " WHERE rownum< " + limit, appUuid);
						}
					} else {
						data = exec.executeAndFetch("SELECT * FROM " + tn + " LIMIT " + limit, appUuid);
					}
				}
			}
			/*if (download.equalsIgnoreCase("n")) {
				
				 * DataStore ds = findDataStoreByMeta(uuid, version); String tn =
				 * getTableName(ds.getUuid(), ds.getVersion());
				 * logger.info("Datastore - Table name:"+tn); DataFrame df =
				 * sqlContext.sql("select * from "+tn);
				 
				if(!isDataFrame) {
					ResultSetMetaData rsmd = rsSorted.getMetaData();
					int numOfCols = rsmd.getColumnCount();
					while(rsSorted.next()) {
						Map<String, Object> object = new LinkedHashMap<String, Object>(numOfCols);
						for(int i = 1; i<= numOfCols; i++) {
							//System.out.println(rsmd.getColumnName(i).substring(rsmd.getColumnName(i).indexOf(".")+1) +"  "+ rsSorted.getObject(i).toString());
							if(rsmd.getColumnName(i).contains("."))
								object.put(rsmd.getColumnName(i).substring(rsmd.getColumnName(i).indexOf(".")+1), rsSorted.getObject(i));
							else
								object.put(rsmd.getColumnName(i), rsSorted.getObject(i));
						}
						data.add(object);
					}
				}else {
					Row[] rows = dfSorted.collect();
					String[] columns = dfSorted.columns();
					for (Row row : rows) {
						Map<String, Object> object = new LinkedHashMap<String, Object>(columns.length);
						for (String column : columns) {
							object.put(column, row.getAs(column));
						}
						data.add(object);
					}
				}
			}
			if (download.equalsIgnoreCase("y")) {
				response.setContentType("application/xml charset=utf-16");
				response.setHeader("Content-type", "application/xml");
				@SuppressWarnings("resource")
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = workbook.createSheet("Datapod");
				ArrayList<String> al = null;
				ArrayList<ArrayList<String>> arlist = new ArrayList<ArrayList<String>>();
				if(rsHolder.getType().equals(ResultType.resultset)) {
					workbook = dataFrameService.getDownloadableContentsForDatapodResults(response, rowLimit);
					ResultSetMetaData rsmd = rsSorted.getMetaData();
					int numOfCols = rsmd.getColumnCount();
					String[] columns = new String[numOfCols];
					for(int i=1; i<=numOfCols; i++)
						columns[i] = rsmd.getColumnName(i);
					try {
						al = new ArrayList<>();
						for (String column : columns) {
							if(column.contains("."))
								al.add(column.substring(column.indexOf(".")+1));
							else
								al.add(column);
						}
						arlist.add(al);
						while(rsSorted.next()) {
							al = new ArrayList<>();
							for (int i=0; i<numOfCols; i++) {
								al.add(String.valueOf(rsSorted.getObject(i)));
								for (int k = 0; k < arlist.size(); k++) {
									ArrayList<String> ardata = (ArrayList<String>) arlist.get(k);
									HSSFRow hssfRow = sheet.createRow((short) k);
									for (int p = 0; p < ardata.size(); p++) {
										HSSFCell cell = hssfRow.createCell((short) p);
										cell.setCellValue(ardata.get(p).toString());
									}
								}
							}
							arlist.add(al);
						}
						response.addHeader("Content-Disposition", "attachment; filename=Datapod.xlsx");
						ServletOutputStream sos = response.getOutputStream();
						workbook.write(sos);
						sos.write(workbook.getBytes());
						sos.close();
					}catch (IOException e1) {
						e1.printStackTrace();
						logger.info("exception caught while download file");
					}
				}else {
					workbook = dataFrameService.getDownloadableContentsForDatapodResults(response, rowLimit);
//					
//					 * DataStore dds = findDataStoreByMeta(uuid, version); String dtn =
//					 * getTableName(ds.getUuid(), dds.getVersion());
//					 * logger.info("Datastore - Table name:"+dtn);
//					 
					// DataFrame df = sqlContext.sql("select * from "+tn);
					Row[] drows = dfSorted.head(rowLimit);
					// logger.info("Rows"+df.count());
					String[] dcolumns = dfSorted.columns();
					try {
						al = new ArrayList<>();
						for (String column : dcolumns) {
							al.add(column);
						}
						arlist.add(al);
						for (Row row : drows) {
							al = new ArrayList<>();
							for (int i = 0; i < dcolumns.length; i++) {
								al.add(String.valueOf(row.get(i)));

								for (int k = 0; k < arlist.size(); k++) {
									ArrayList<String> ardata = (ArrayList<String>) arlist.get(k);
									HSSFRow hssfRow = sheet.createRow((short) k);

									for (int p = 0; p < ardata.size(); p++) {
										HSSFCell cell = hssfRow.createCell((short) p);
										cell.setCellValue(ardata.get(p).toString());
									}
								}
							}
							arlist.add(al);
						}
						response.addHeader("Content-Disposition", "attachment; filename=Datapod.xlsx");
						ServletOutputStream sos = response.getOutputStream();
						workbook.write(sos);
						sos.write(workbook.getBytes());
						sos.close();
					}catch (IOException e1) {
						e1.printStackTrace();
						logger.info("exception caught while download file");
					}
				}
			}*/

			return data;
		}catch (IOException | NullPointerException e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.", null);
			throw new RuntimeException((message != null) ? message : "Table not found.");
		}catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.", null);
			throw new RuntimeException((message != null) ? message : "Table not found.");
		}
	}
	
	public List<Map<String, Object>> getDatapodResultsByDatapodDatasource(String uuid, String version, String format,
			int offset, int limit, int rowLimit, String sortBy, String order, String requestId, RunMode runMode) throws Exception {
			
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			DataStore ds = (DataStore) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.datastore.toString());
			String tn = getTableNameByDatastore(ds.getUuid(), ds.getVersion(), runMode);
			logger.info("Table name:" + tn);
			boolean requestIdExistFlag = false;
			
			List<String> orderList = new ArrayList<>();
			List<String> sortList = new ArrayList<>();
			if(StringUtils.isNotBlank(order)) {	
			 orderList = Arrays.asList(order.split("\\s*,\\s*"));
			}
			if(StringUtils.isNotBlank(sortBy)) {
			 sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));
			}
			
			StringBuilder orderBy = new StringBuilder();
			
			ExecContext execContext = null;
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			if (runMode == null || runMode.equals(RunMode.ONLINE)) {
				execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark") || engine.getExecEngine().equalsIgnoreCase("livy_spark"))
						? helper.getExecutorContext(engine.getExecEngine()) : helper.getExecutorContext(ExecContext.spark.toString());
			} else {
				execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
			}
			IExecutor exec = execFactory.getExecutor(execContext.toString());

			limit = offset + limit;
			offset = offset + 1;
			String appUuid = null;
			appUuid = commonServiceImpl.getApp().getUuid();

			MetaIdentifier datapodMI = ds.getMetaId().getRef();
			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodMI.getUuid(), datapodMI.getVersion(), datapodMI.getType().toString());
			Datasource datapodDS = commonServiceImpl.getDatasourceByDatapod(datapod);
			if (StringUtils.isNotBlank(sortBy) || StringUtils.isNotBlank(order) ) {
				for (int i = 0; i < sortList.size(); i++) {
					orderBy.append(sortList.get(i)).append(" ").append(orderList.get(i));
				}
				if (requestId != null) {
					for (Map.Entry<String, String> entry : requestMap.entrySet()) {
						String id = entry.getKey();
						if (id.equals(requestId)) 
							requestIdExistFlag = true;
					}
					if (requestIdExistFlag) {
						tn = requestMap.get(requestId);
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							if(datapodDS.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
								data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " WHERE rownum< " + limit, datapodDS, appUuid);
							} else {
								data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
							}	
						} else 
							if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
								if(engine.getExecEngine().equalsIgnoreCase("livy-spark")
										|| datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
										|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
									data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
								} else {
									data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " WHERE rownum< " + limit, datapodDS, appUuid);
								}
							} else {
								data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
							}
					} else {
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							if(datapodDS.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
								data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " WHERE rownum< " + limit, datapodDS, appUuid);
							} else {
								data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
							}	
						} else {
							if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
								if(engine.getExecEngine().equalsIgnoreCase("livy-spark")
										|| datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
										|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
									data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
								} else {
									data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " WHERE rownum< " + limit, datapodDS, appUuid);
								}
							} else {
								data = exec.executeAndFetchByDatasource("SELECT * FROM "+ tn + " LIMIT ", datapodDS, appUuid);
							}
						}
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							tn = requestId.replace("-", "_");
						}
						requestMap.put(requestId, tn);
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							if(datapodDS.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
								data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " WHERE rownum< " + limit, datapodDS, appUuid);
							} else {
								data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
							}	
						} else {
							if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
								if(engine.getExecEngine().equalsIgnoreCase("livy-spark")
										|| datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
										|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
									data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
								} else {
									data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " WHERE rownum< " + limit, datapodDS, appUuid);
								}
							} else {
								data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
							}
						}
					}
				}
			} else {
				if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
					if(datapodDS.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
						data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " WHERE rownum< " + limit, datapodDS, appUuid);
					} else {
						data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
					}					
				} else {
					if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
						if(engine.getExecEngine().equalsIgnoreCase("livy-spark")
								|| datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
						} else {
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " WHERE rownum< " + limit, datapodDS, appUuid);
						}
					} else {
						data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
					}
				}
			}
			return data;
		}catch (IOException | NullPointerException e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.", null);
			throw new RuntimeException((message != null) ? message : "Table not found.");
		}catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.", null);
			throw new RuntimeException((message != null) ? message : "Table not found.");
		}
	}

	public DataStore findLatestByMeta(String datapodUuid, String datapodVersion) {

		DataStore ds = null;
		try {
			ds = findDataStoreByMeta(datapodUuid, datapodVersion);
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ds;
		
//		Commenting old code.		
//		String appUuid = null;
//		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
//				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
//		Aggregation datastoreAggr = newAggregation(match(Criteria.where("metaId.ref.uuid").is(dataStoreMetaUUID)),
//				match(Criteria.where("metaId.ref.version").is(dataStoreMetaVer)),
//				group("uuid").max("version").as("version"), sort(Sort.Direction.DESC, "version"), limit(1));
//		AggregationResults<DataStore> datastoreResults = mongoTemplate.aggregate(datastoreAggr, "datastore",
//				DataStore.class);
//		List<DataStore> datastoreList = datastoreResults.getMappedResults();
//
//		DataStore datastoreLatest = null;
//		for (DataStore s : datastoreList) {
//			if (appUuid != null) {
//				datastoreLatest = iDataStoreDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
//			} else
//				datastoreLatest = iDataStoreDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
//		}
//		return datastoreLatest;
		
	}
	
	/********************** UNUSED **********************/
	/*public List<String> getAtributeValues(String datapodUUID, String type, int attributeID) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {

		List<String> result = new ArrayList<>();
		//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(datapodUUID);
				Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(datapodUUID, MetaType.datapod.toString());
		String attributeName = datapodDO.getAttributes().get(attributeID).getName();
		DataStore datastore = findLatestByMeta(datapodDO.getUuid(), datapodDO.getVersion());
		String tableName = getTableNameByDatastore(datastore.getUuid(), datastore.getVersion());
		// DataFrame df = sqlContext.sql("select " + attributeName + " from " +
		// tableName);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		ResultSetHolder rsHolder = exec.executeSql("SELECT " + attributeName + " FROM " + tableName);
		result = dataFrameService.getAtributeValues(rsHolder, attributeName);

		return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<DataStore> resolveName(List<DataStore> datastore) {
		List<DataStore> dataStoreList = new ArrayList<>();
		for (DataStore dStore : datastore) {
			String createdByRefUuid = dStore.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			dStore.getCreatedBy().getRef().setName(user.getName());
			dataStoreList.add(dStore);
		}
		return dataStoreList;
	}*/

	@SuppressWarnings("static-access")
	public List<DataStore> getDataStoreByDim(String uuid, List<AttributeRefHolder> dimensionList) {
		String META_TYPE = "metaId.ref.type";
		String META_UUID = "metaId.ref.uuid";
		String DIMINFO = "dimInfo";
		String REF_TYPE = "ref.type";
		String REF_UUID = "ref.uuid";
		String VALUE = "value";
		String ATTRIBUTE_ID = "attributeId";
		String DATAPOD = "datapod";
		String VERSION = "version";

		MongoOperations mongoOperations = mongoTemplate;

		if (dimensionList == null || dimensionList.size() <= 0) {
			return null;
		}

		Query dimMatchQuery = new Query();
		Criteria crit = new Criteria();
		crit = crit.where(META_TYPE).is(DATAPOD).and(META_UUID).is(uuid);
		crit = crit.and(DIMINFO);
		for (AttributeRefHolder dimension : dimensionList) {
			crit = crit.elemMatch(Criteria.where(REF_TYPE).is(dimension.getRef().getType()).and(REF_UUID)
					.is(dimension.getRef().getUuid()).and(VALUE).is(dimension.getValue()).and(ATTRIBUTE_ID)
					.is(dimension.getAttrId()));
		}
		dimMatchQuery.addCriteria(crit);
		dimMatchQuery.limit(1);
		dimMatchQuery.with(new Sort(Sort.Direction.DESC, VERSION));
		return mongoOperations.find(dimMatchQuery, DataStore.class);
	}

	public List<Map<String, Object>> getAttributeValues(String datapodUUID, int attributeID, RunMode runMode) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		setRunMode(runMode);
		List<Map<String, Object>> data = new ArrayList<>();
		//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(datapodUUID);
		Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(datapodUUID, MetaType.datapod.toString());
//		String id = datapodDO.getAttributes().get(0).getName();
		String attributeName = datapodDO.getAttributes().get(attributeID).getName();
		DataStore datastore = findLatestByMeta(datapodDO.getUuid(), datapodDO.getVersion());
		String tableName = getTableNameByDatastore(datastore.getUuid(), datastore.getVersion(), runMode);
		// DataFrame df = sqlContext.sql("select " + id + " AS id," +
		// attributeName + " AS value from " + tableName);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		Datasource datapodDS = commonServiceImpl.getDatasourceByObject(datapodDO);
		IExecutor exec = execFactory.getExecutor(datasource.getType());
//		data = exec.executeAndFetch("SELECT DISTINCT " + attributeName + " AS value FROM " + tableName, commonServiceImpl.getApp().getUuid());
		data = exec.executeAndFetchByDatasource("SELECT DISTINCT " + attributeName + " AS value FROM " + tableName, datapodDS, commonServiceImpl.getApp().getUuid());
//				.executeSql("select distinct " + id + " AS id," + attributeName + " AS value from " + tableName);
		/*DataFrame df = rsHolder.getDataFrame();
		Row[] rows = df.head(100);
		String[] columns = df.columns();
		for (Row row : rows) {
			java.util.TreeMap<String, Object> object = new TreeMap<String, Object>();
			for (String column : columns) {
				object.put(column, row.getAs(column));
			}
			data.add(object);
		}*/
		return data;
	}

	public List<DataStore> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iDataStoreDao.findAllVersion(appUuid, uuid);
		} else
			return iDataStoreDao.findAllVersion(uuid);
	}

	public DataStore getDatastore(String uuid, String version) throws JsonProcessingException {
		return (DataStore) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.datastore.toString());
	}

	public DataStore getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iDataStoreDao.findAsOf(appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
		} else
			return iDataStoreDao.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}

	public MetaIdentifierHolder saveAs(DataStore datastore) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();
		DataStore dsNew = new DataStore();
		dsNew.setName(datastore.getName() + "_copy");
		dsNew.setActive(datastore.getActive());
		dsNew.setDesc(datastore.getDesc());
		dsNew.setTags(datastore.getTags());
		dsNew.setDimInfo(datastore.getDimInfo());
		dsNew.setMetaId(datastore.getMetaId());
		dsNew.setExecId(datastore.getExecId());
		dsNew.setLocation(datastore.getLocation());
		dsNew.setPersistMode(datastore.getPersistMode());
		save(dsNew);
		ref.setType(MetaType.datastore);
		ref.setUuid(dsNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}
	
	public void create(String filePath,String fileName, MetaIdentifier metaId, MetaIdentifier execId,List<MetaIdentifierHolder> appInfo, MetaIdentifierHolder createdBy,
			String saveMode, MetaIdentifierHolder resultRef) throws Exception{
		create( filePath, fileName, metaId, execId, appInfo, createdBy, saveMode, resultRef, -1L, null, null);
	}
	
	public void create(String filePath, String fileName, MetaIdentifier metaId, MetaIdentifier execId,List<MetaIdentifierHolder> appInfo, MetaIdentifierHolder createdBy,
			String saveMode, MetaIdentifierHolder resultRef, long count, String persistMode, String desc) throws Exception{
		BaseExec baseExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(execId.getUuid(), execId.getVersion(), execId.getType().toString());
		DataStore dataStore = new DataStore();
		dataStore.setBaseEntity();
		MetaIdentifierHolder metaDetails = new MetaIdentifierHolder();
		MetaIdentifierHolder execDetails = new MetaIdentifierHolder();
		MetaIdentifier resultDetails = new MetaIdentifier();
		if(desc == null || desc.isEmpty())
			dataStore.setDesc("Creating datastore for "+fileName);
		else
			dataStore.setDesc(desc);
		dataStore.setName(fileName);
		metaDetails.setRef(metaId);
		dataStore.setMetaId(metaDetails);
		execDetails.setRef(execId);
		dataStore.setExecId(execDetails);
		dataStore.setLocation(filePath);
		dataStore.setCreatedBy(createdBy);
		if (count >= 0) {
			dataStore.setNumRows(count);
		}
		dataStore.setPersistMode(persistMode);
		if (appInfo == null) {
			List<MetaIdentifierHolder> appInfoList = new ArrayList<MetaIdentifierHolder>();
//			MetaIdentifierHolder appInfo1 = securityServiceImpl.getAppInfo();		
			appInfoList.addAll(baseExec.getAppInfo());
			dataStore.setAppInfo(appInfoList);

		} else {
			dataStore.setAppInfo(appInfo);
		}
		dataStore.setRunMode(runMode.toString());
		DataStore ds = save(dataStore);

		resultDetails.setType(MetaType.datastore);
		resultDetails.setUuid(ds.getUuid());
		resultDetails.setVersion(ds.getVersion());
		if (resultRef != null) {
			resultRef.setRef(resultDetails);
		}
	}
	
	/*public void persistDataStore(DataFrame df, String tableName, ExecParams execParams, String filePath,
			MetaIdentifier metaId, MetaIdentifier execId, String consUtil, List<MetaIdentifierHolder> appInfo,
			String saveMode, MetaIdentifierHolder resultRef, DataStore dataStore, Mode runMode) throws Exception {
		MetaIdentifierHolder metaDetails = new MetaIdentifierHolder();
		MetaIdentifier metaRef = new MetaIdentifier();
		MetaIdentifier execRef = new MetaIdentifier();
		MetaIdentifierHolder execDetails = new MetaIdentifierHolder();
		MetaIdentifier resultDetails = new MetaIdentifier();
		long count = 0;
		if (df != null) {
			count = df.count();
		}
		// Check if DataSaveMode is Overwrite then overwrite data else append to
		// parquet
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
		if (StringUtils.isBlank(consUtil) && metaId.getType() == MetaType.datapod) {
			Datapod datapod = (Datapod) daoRegister.getRefObject(metaId);
			IWriter datapodWriter = datasourceFactory.getDatapodWriter(datapod, daoRegister);
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			if (Helper.getExecutorContext(datasource.getType().toLowerCase()).equals(ExecContext.spark)
					|| Helper.getExecutorContext(datasource.getType().toLowerCase()).equals(ExecContext.file)) {
				logger.info(" Going to datapodWriter");
				datapodWriter.write(df, filePathUrl, datapod, saveMode);
			}
		}

		// sqlContext.dropTempTable("temp");
//		
//		 * IWriter datapodWriter =
//		 * datasourceFactory.getDatapodWriter(datapod,daoRegister);
//		 * datapodWriter.write(dfRule, filePathUrl,datapod);
		 
		logger.info("FilePath:" + filePathUrl);

		if (dataStore == null) {
			dataStore = new DataStore();
		}

		// Saving dataStore Obj
		dataStore.setNumRows(count);
		dataStore.setDesc("Creating datastore for " + tableName);
		dataStore.setName(tableName);
		dataStore.setPersistMode(Helper.getPersistModeFromRunMode(runMode.toString()));
		dataStore.setRunMode(runMode.toString());
//		
//		 * metaRef.setType(MetaType.rule); metaRef.setUuid(metaId.getUuid());
//		 * metaRef.setVersion(metaId.getVersion());
		 
		metaDetails.setRef(metaId);
		dataStore.setMetaId(metaDetails);
		dataStore.setNumRows(count);

//		
//		 * execRef.setType(MetaType.ruleExec);
//		 * execRef.setUuid(execId.getUuid());
//		 * execRef.setVersion(execId.getVersion());
		 
		execDetails.setRef(execId);
		dataStore.setExecId(execDetails);
		dataStore.setLocation(filePath);
		if (appInfo == null) {
			List<MetaIdentifierHolder> appInfoList = new ArrayList<MetaIdentifierHolder>();
			MetaIdentifierHolder appInfo1 = securityServiceImpl.getAppInfo();			
			//MetaIdentifier appMeta = new MetaIdentifier(MetaType.application, "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd",null);
//			
//			 * if (rule.getAppInfo() != null && rule.getAppInfo().get(0) !=
//			 * null) { appMeta = new MetaIdentifier(MetaType.application,
//			 * rule.getAppInfo().get(0).getRef().getUuid(),
//			 * rule.getAppInfo().get(0).getRef().getVersion()); }
			 
			//appInfo1.setRef(appMeta);
			appInfoList.add(appInfo1);
			dataStore.setAppInfo(appInfoList);

		} else {
			dataStore.setAppInfo(appInfo);
		}

//		
//		 * if (execParams != null && execParams.getDimInfo() != null &&
//		 * execParams.getDimInfo().size() > 0) {
//		 * dataStore.setDimInfo(execParams.getDimInfo()); // Including Dimension
//		 * in DataStore }
		 

		DataStore ds = save(dataStore);
		// Set Task result attr for dagExec

		resultDetails.setType(MetaType.datastore);
		resultDetails.setUuid(ds.getUuid());
		resultDetails.setVersion(ds.getVersion());
		if (resultRef != null) {
			resultRef.setRef(resultDetails);
		}

	}// End persistDataStore
*/
	
	/*
	 * public void persistDataStoreNew(DataFrame df, String tableName,
	 * ExecParams execParams, String filePath, MetaIdentifier
	 * metaId,MetaIdentifierHolder user, MetaIdentifier execId, String consUtil,
	 * List<MetaIdentifierHolder> appInfo, String saveMode, MetaIdentifierHolder
	 * resultRef) throws IOException { MetaIdentifierHolder metaDetails = new
	 * MetaIdentifierHolder(); MetaIdentifier metaRef = new MetaIdentifier();
	 * MetaIdentifier execRef = new MetaIdentifier(); MetaIdentifierHolder
	 * execDetails = new MetaIdentifierHolder(); MetaIdentifier resultDetails =
	 * new MetaIdentifier(); long count = df.count(); // Check if DataSaveMode
	 * is Overwrite then overwrite data else append to // parquet String
	 * filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(),
	 * hdfsInfo.getSchemaPath(), filePath); if(StringUtils.isBlank(consUtil) &&
	 * metaId.getType() == MetaType.datapod) { Datapod datapod =
	 * (Datapod)daoRegister.getRefObject(metaId); IWriter datapodWriter =
	 * datasourceFactory.getDatapodWriter(datapod,daoRegister);
	 * datapodWriter.write(df, filePathUrl, datapod, saveMode); }
	 * 
	 * //sqlContext.dropTempTable("temp");
	 * 
	 * IWriter datapodWriter =
	 * datasourceFactory.getDatapodWriter(datapod,daoRegister);
	 * datapodWriter.write(dfRule, filePathUrl,datapod);
	 * 
	 * logger.info("FilePath:" + filePathUrl);
	 * 
	 * DataStore dataStore = new DataStore(); dataStore.setCreatedBy(user); //
	 * Saving dataStore Obj dataStore.setDesc("Creating datastore for " +
	 * tableName); dataStore.setName(tableName);
	 * dataStore.setPersistMode(consUtil); metaRef.setType(MetaType.rule);
	 * metaRef.setUuid(metaId.getUuid());
	 * metaRef.setVersion(metaId.getVersion()); metaDetails.setRef(metaId);
	 * dataStore.setMetaId(metaDetails); dataStore.setNumRows(count);
	 * 
	 * execRef.setType(MetaType.ruleExec); execRef.setUuid(execId.getUuid());
	 * execRef.setVersion(execId.getVersion()); execDetails.setRef(execId);
	 * dataStore.setExecId(execDetails); dataStore.setLocation(filePath);
	 * if(appInfo == null) { List<MetaIdentifierHolder> appInfoList = new
	 * ArrayList<MetaIdentifierHolder>(); MetaIdentifierHolder appInfo1 = new
	 * MetaIdentifierHolder(); MetaIdentifier appMeta = new
	 * MetaIdentifier(MetaType.application,
	 * "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd", null); if (rule.getAppInfo() !=
	 * null && rule.getAppInfo().get(0) != null) { appMeta = new
	 * MetaIdentifier(MetaType.application,
	 * rule.getAppInfo().get(0).getRef().getUuid(),
	 * rule.getAppInfo().get(0).getRef().getVersion()); }
	 * appInfo1.setRef(appMeta); appInfoList.add(appInfo1);
	 * dataStore.setAppInfo(appInfoList);
	 * 
	 * } else { dataStore.setAppInfo(appInfo); }
	 * 
	 * 
	 * if (execParams != null && execParams.getDimInfo() != null &&
	 * execParams.getDimInfo().size() > 0) {
	 * dataStore.setDimInfo(execParams.getDimInfo()); // Including Dimension in
	 * DataStore }
	 * 
	 * 
	 * DataStore ds = Save(dataStore);
	 * 
	 * // Set Task result attr for dagExec
	 * 
	 * resultDetails.setType(MetaType.datastore);
	 * resultDetails.setUuid(ds.getUuid());
	 * resultDetails.setVersion(ds.getVersion());
	 * resultRef.setRef(resultDetails);
	 * 
	 * }// End persistDataStore
	 */
	/*public List<BaseEntity> findList(List<? extends BaseEntity> datastoreList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for (BaseEntity datastore : datastoreList) {
			BaseEntity baseEntity = new BaseEntity();
			String id = datastore.getId();
			String uuid = datastore.getUuid();
			String version = datastore.getVersion();
			String name = datastore.getName();
			String desc = datastore.getDesc();
			String published=datastore.getPublished();
			MetaIdentifierHolder createdBy = datastore.getCreatedBy();
			String createdOn = datastore.getCreatedOn();
			String[] tags = datastore.getTags();
			String active = datastore.getActive();
			List<MetaIdentifierHolder> appInfo = datastore.getAppInfo();
			baseEntity.setId(id);
			baseEntity.setUuid(uuid);
			baseEntity.setVersion(version);
			baseEntity.setName(name);
			baseEntity.setDesc(desc);
			baseEntity.setCreatedBy(createdBy);
			baseEntity.setCreatedOn(createdOn);
			baseEntity.setPublished(published);
			baseEntity.setTags(tags);
			baseEntity.setActive(active);
			baseEntity.setAppInfo(appInfo);
			baseEntityList.add(baseEntity);
		}
		return baseEntityList;
	}*/

	public List<Map<String, Object>> getResultByDatastore(String datastoreUuid, String datastoreVersion, String requestId, int offset,
			int limit, String sortBy, String order, String execVersion) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException, IOException{
		List<Map<String, Object>> data = new ArrayList<>();
		
		try {
			DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(datastoreUuid, datastoreVersion, MetaType.datastore.toString());
			StringBuilder orderBy = new StringBuilder();
			RunMode runMode = Helper.getExecutionMode(dataStore.getRunMode());
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			ExecContext execContext = null;
			IExecutor exec = null;
			String appUuid = null;
			if (runMode.equals(RunMode.ONLINE)) {
				execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark")
						|| engine.getExecEngine().equalsIgnoreCase("livy_spark"))
								? helper.getExecutorContext(engine.getExecEngine())
								: helper.getExecutorContext(ExecContext.spark.toString());
				appUuid = commonServiceImpl.getApp().getUuid();
			} else {
				execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
			}
			exec = execFactory.getExecutor(execContext.toString());
			appUuid = commonServiceImpl.getApp().getUuid();
		
		
			boolean requestIdExistFlag = false;
			String tableName = getTableNameByDatastore(dataStore.getUuid(),
					dataStore.getVersion(), runMode); 

			StringBuilder customCondition = new StringBuilder(" ");
			if((!execContext.equals(ExecContext.FILE) || !execContext.equals(ExecContext.spark)) 
					&& execVersion != null 
					&& !execVersion.isEmpty()) {
				customCondition.append(" WHERE version = "+execVersion); 
			} else {
				customCondition.append(" WHERE 1 = 1"); 
			}
			
			StringBuilder limitBuilder = new StringBuilder(" ");
			if(limit > 0) {
				limitBuilder.append(" LIMIT " + limit);
			}
			Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(dataStore);
			MetaType metaType = dataStore.getMetaId().getRef().getType();
			if(runMode.equals(RunMode.ONLINE) && (metaType.equals(MetaType.rule)
					||  metaType.equals(MetaType.datapod) || metaType.equals(MetaType.report))) {
				customCondition.append(limitBuilder);
				data = sparkExecutor.executeAndFetchFromTempTable("SELECT * FROM " + tableName + customCondition.toString(), appUuid);
			} else if (requestId == null|| requestId.equals("null") || requestId.isEmpty()) {
				if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
					customCondition.append(limitBuilder);
					 data = exec.executeAndFetchByDatasource("SELECT * FROM " + tableName + customCondition.toString(), mapSourceDS, appUuid);
				} else {
					if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
						if (runMode.equals(RunMode.ONLINE)) {	
							customCondition.append(limitBuilder);						
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tableName + customCondition.toString(), mapSourceDS, appUuid);
						} else {
							if(limit > 0) {
								limitBuilder.append(" AND rownum < " + limit);
							}
							customCondition.append(limitBuilder);
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tableName + customCondition.toString(), mapSourceDS, appUuid);
						}
					} else {
						customCondition.append(limitBuilder);
						data = exec.executeAndFetchByDatasource("SELECT * FROM " + tableName + customCondition.toString(), mapSourceDS, appUuid);
					}
				}
			} else {
				List<String> orderList = Arrays.asList(order.split("\\s*,\\s*"));
				List<String> sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));

				if (StringUtils.isNotBlank(sortBy) || StringUtils.isNotBlank(order)) {
					for (int i = 0; i < sortList.size(); i++)
						orderBy.append(sortList.get(i)).append(" ").append(orderList.get(i));
						for (Map.Entry<String, List<Map<String, Object>>> entry : requestNewMap.entrySet()) {
							String id = entry.getKey();
							if (id.equals(requestId)) {
								requestIdExistFlag = true;
							}
						}
						if (requestIdExistFlag) {
							data = requestNewMap.get(requestId);
						} else {
							if(runMode.equals(RunMode.ONLINE) && (metaType.equals(MetaType.rule)
									||  metaType.equals(MetaType.datapod) || metaType.equals(MetaType.report))) {
								data = sparkExecutor.executeAndFetchFromTempTable("SELECT * FROM " + tableName + customCondition.toString(), appUuid);
							} else if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
								customCondition.append(limitBuilder);
								data = exec.executeAndFetchByDatasource("SELECT * FROM " + tableName + customCondition.toString(), mapSourceDS,appUuid);
							} else {
								if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
									if (runMode.equals(RunMode.ONLINE)) {
										customCondition.append(limitBuilder);
										data = exec.executeAndFetchByDatasource("SELECT * FROM " + tableName + customCondition.toString(), mapSourceDS, appUuid);
									} else {
										if(limit > 0) {
											limitBuilder.append(" AND rownum< " + limit);
										}
										customCondition.append(limitBuilder);
										data = exec.executeAndFetchByDatasource(
												"SELECT * FROM " + tableName + customCondition.toString(), mapSourceDS, appUuid);
									}
								else {
									customCondition.append(limitBuilder);
									data = exec.executeAndFetchByDatasource("SELECT * FROM " + tableName + customCondition.toString(), mapSourceDS, appUuid);
								}
							}

//							tabName = requestId.replace("-", "_");
							requestNewMap.put(requestId, data);							
						}
				} else {
					if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
						limitBuilder.append(" AND rownum >= " + offset);
						if(limit > 0) {
							limitBuilder.append(" AND rownum <= " + limit);
						}
						customCondition.append(limitBuilder);
						data = exec.executeAndFetchByDatasource("SELECT * FROM " + tableName + customCondition.toString(), mapSourceDS, appUuid);
					} else {
						if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
							if (runMode.equals(RunMode.ONLINE)) {
								customCondition.append(limitBuilder);
								data = exec.executeAndFetchByDatasource("SELECT * FROM " + tableName + customCondition.toString(), mapSourceDS, appUuid);
							} else {
								if(limit > 0) {
									limitBuilder.append(" AND rownum< " + limit);
								}
								customCondition.append(limitBuilder);
								data = exec.executeAndFetchByDatasource(
										"SELECT * FROM " + tableName + customCondition.toString(), mapSourceDS, appUuid);
							}
						else {
							customCondition.append(limitBuilder);
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tableName + customCondition.toString(), mapSourceDS, appUuid);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "FAILED to fetch data.", new MetaIdentifierHolder(new MetaIdentifier(MetaType.datastore, datastoreUuid, datastoreVersion)));
		}
		
		return data;
	}
	
	public HttpServletResponse download(String datastoreUuid, String datastoreVersion, String format, int offset,
			int limit, HttpServletResponse response, int rowLimit, String sortBy, String order, String requestId,
			RunMode runMode) throws Exception {
		setRunMode(runMode);
		DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(datastoreUuid, datastoreVersion, MetaType.datastore.toString());
		if (datastore == null) {
			logger.error("Datastore is not available for this datapod");
			throw new Exception();
		}
		
		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
		if(rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of "+maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Requested rows exceeded the limit of "+maxRows ,null);
			throw new RuntimeException("Requested rows exceeded the limit of "+maxRows);
		}
		
		List<Map<String, Object>> results = getDatapodResults(datastore.getUuid(), datastore.getVersion(), null,
				0, limit, response, rowLimit, null, null, null, runMode);
		response = commonServiceImpl.download(format, response, runMode, results, datastore.getExecId());	
		return response;

	}
	
}
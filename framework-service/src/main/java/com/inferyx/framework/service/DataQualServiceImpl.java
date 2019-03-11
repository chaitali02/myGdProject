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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DQInfo;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IDataQualDao;
import com.inferyx.framework.dao.IDataQualExecDao;
import com.inferyx.framework.dao.IFilterDao;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.operator.DQOperator;
import com.inferyx.framework.register.GraphRegister;
import com.inferyx.framework.view.metadata.DQView;

@Service
public class DataQualServiceImpl  extends RuleTemplate{

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IDataQualDao iDataQualDao;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	DQOperator dqOperator;
	@Autowired
	protected DataSourceFactory datasourceFactory;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	DQInfo dqInfo;
	@Autowired
	FilterServiceImpl filterServiceImpl;
	@Autowired 
	RegisterService registerService;
	@Autowired
	IDataQualExecDao iDataQualExecDao;
	@Autowired
	IFilterDao ifilterDao;	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ConnectionFactory connFactory;
	@Autowired
	MessageServiceImpl messageServiceImpl;
	@Autowired
	Engine engine;
	@Autowired
	ExecutorServiceImpl executorServiceImpl;
	
	Map<String, String> requestMap = new HashMap<String, String>();

	static final Logger logger = Logger.getLogger(DataQualServiceImpl.class);
	
	public DataQual save(DQView dqView) throws Exception {
		DataQual dq = new DataQual();		
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		if(dqView.getUuid() != null)
		{
			dq.setUuid(dqView.getUuid());
		}
		dq.setAppInfo(metaIdentifierHolderList);
		dq.setName(dqView.getName());
		dq.setPublished(dqView.getPublished());
		dq.setDesc(dqView.getDesc());
		dq.setTags(dqView.getTags());
		dq.setAttribute(dqView.getAttribute());
		dq.setCustomFormatCheck(dqView.getCustomFormatCheck());
		dq.setDataTypeCheck(dqView.getDataTypeCheck());
		dq.setCustomFormatCheck(dqView.getCustomFormatCheck());
		dq.setDateFormatCheck(dqView.getDateFormatCheck());
		dq.setDependsOn(dqView.getDependsOn());
		dq.setLengthCheck(dqView.getLengthCheck());
		dq.setDuplicateKeyCheck(dqView.getDuplicateKeyCheck());
		dq.setNullCheck(dqView.getNullCheck());
		dq.setRangeCheck(dqView.getRangeCheck());
		dq.setRefIntegrityCheck(dqView.getRefIntegrityCheck());
		//dq.setStdDevCheck(dqView.getStdDevCheck());
		dq.setValueCheck(dqView.getValueCheck());
		dq.setTarget(dqView.getTarget());
		Filter filter = null;
//		List<AttributeRefHolder> filterList = new ArrayList<AttributeRefHolder>();
//		AttributeRefHolder filterMeta = new AttributeRefHolder();
		if(dqView.getFilter() != null) {
		filter = dqView.getFilter();
		filter.setName(dqView.getName());
		filter.setDesc(dqView.getDesc());
		filter.setTags(dqView.getTags());
		MetaIdentifierHolder filterInfo = new MetaIdentifierHolder();
		filterInfo.setRef(dqView.getDependsOn().getRef());
		filter.setDependsOn(filterInfo);		
		}
		if (dqView.getFilterChg().equalsIgnoreCase("y")) {
			//filterServiceImpl.save(filter);
			commonServiceImpl.save(MetaType.filter.toString(), filter);
		}		
//		if(filter != null)
//		{
//		MetaIdentifier filterInfo = new MetaIdentifier(MetaType.filter, filter.getUuid(), null);
//		filterMeta.setRef(filterInfo);
//		filterList.add(filterMeta);
//		dq.setFilterInfo(filterList);
//		}
		dq.setBaseEntity();
		dq.setPublished(dqView.getPublished());
		DataQual dataqual=iDataQualDao.save(dq);
		registerGraph.updateGraph((Object) dataqual, MetaType.dq);
		return dataqual;
	}

	public List<DataQual> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDataQualDao.findAll();
		}
		return iDataQualDao.findAll(appUuid);
	}

	public boolean isExists(String id) {
		return iDataQualDao.exists(id);
	}

	public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		DataQual dataQual = iDataQualDao.findOneById(appUuid, Id);
		dataQual.setActive("N");
		iDataQualDao.save(dataQual);
	}

	public List<DataQual> findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDataQualDao.findAllByUuid(appUuid, uuid);

	}

	public DataQual findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDataQualDao.findOneByUuidAndVersion(uuid, version);
		} else
			return iDataQualDao.findOneByUuidAndVersion(appUuid, uuid, version);
	}

	public DataQual findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDataQualDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iDataQualDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}

	public List<DataQual> findAllLatest() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Aggregation dqAggr = newAggregation(group("uuid").max("version").as("version"));
		AggregationResults<DataQual> dqResults = mongoTemplate.aggregate(dqAggr, "dq", DataQual.class);
		List<DataQual> dataQualList = dqResults.getMappedResults();

		List<DataQual> result = new ArrayList<DataQual>();
		for (DataQual d : dataQualList) {
			DataQual dqLatest;
			if (appUuid != null) {
				dqLatest = iDataQualDao.findOneByUuidAndVersion(appUuid, d.getId(), d.getVersion());
			} else {
				dqLatest = iDataQualDao.findOneByUuidAndVersion(d.getId(), d.getVersion());
			}
			if(dqLatest != null)
			{
			result.add(dqLatest);
			}
		}
		return result;
	}

	public List<DataQual> findAllLatestActive() {
		Aggregation dqAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<DataQual> dqResults = mongoTemplate.aggregate(dqAggr, "dq", DataQual.class);
		List<DataQual> dqList = dqResults.getMappedResults();

		List<DataQual> result = new ArrayList<DataQual>();
		for (DataQual r : dqList) {
			DataQual dqLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				dqLatest = iDataQualDao.findOneByUuidAndVersion(appUuid, r.getId(), r.getVersion());
			} else {
				dqLatest = iDataQualDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if(dqLatest != null)
			{
			result.add(dqLatest);
			}
		}
		return result;
	}

	public DataQualExec create(String dataQualUUID, String dataQualVersion,
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception {
		try {
			return (DataQualExec) super.create(dataQualUUID, dataQualVersion, MetaType.dq, MetaType.dqExec, null, refKeyMap, datapodList, dagExec);
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dagExec, dagExec.getUuid(), dagExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create DQExec.", dependsOn);
			throw new Exception((message != null) ? message : "Can not create DQExec.");
		}
	}
	
	public DataQualExec create(String dataQualUUID, String dataQualVersion, DataQualExec dataQualExec,
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception {
		try {
			return (DataQualExec) super.create(dataQualUUID, dataQualVersion, MetaType.dq, MetaType.dqExec, dataQualExec, refKeyMap, datapodList, dagExec);
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dagExec, dagExec.getUuid(), dagExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create DQExec.", dependsOn);
			throw new Exception((message != null) ? message : "Can not create DQExec.");
		}
	}
	
	public DataQualExec execute(String dataqualUUID, String dataqualVersion, DataQualExec dataqualExec,
			DataQualGroupExec dataqualGroupExec, ExecParams execParams, RunMode runMode) throws Exception {
		execute(null, dataqualExec, null, execParams, runMode);
		return dataqualExec;
	}

	public DataQualExec execute(ThreadPoolTaskExecutor metaExecutor, DataQualExec dataqualExec, List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
//		Datapod targetDatapod = (Datapod) daoRegister.getRefObject(new MetaIdentifier(MetaType.datapod, dqInfo.getDqTargetUUID(), null));
//		Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dqInfo.getDqTargetUUID(), null, MetaType.datapod.toString(), "N");
		Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dqInfo.getDq_result_detail(), null, MetaType.datapod.toString(), "N");
		MetaIdentifier targetDatapodKey = new MetaIdentifier(MetaType.datapod, targetDatapod.getUuid(),
				targetDatapod.getVersion());		
		try {
			return (DataQualExec) super.execute(MetaType.dq, MetaType.dqExec, metaExecutor, dataqualExec, targetDatapodKey, taskList, execParams, runMode);
		} catch (Exception e) {
			e.printStackTrace();
			dataqualExec = (DataQualExec) commonServiceImpl.setMetaStatus(dataqualExec, MetaType.dqExec,
					Status.Stage.FAILED);
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dqExec, dataqualExec.getUuid(), dataqualExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Data Quality execution FAILED.", dependsOn);
			throw new Exception((message != null) ? message : "Data Quality execution FAILED.");
		}
	}
	
	@Override
	public BaseRuleExec execute(ThreadPoolTaskExecutor metaExecutor,
			BaseRuleExec baseRuleExec, MetaIdentifier datapodKey,
			List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
			return execute(metaExecutor, (DataQualExec) baseRuleExec, taskList, execParams, runMode);
	}
	
	
	
	public String getTableName(Datapod datapod, RunMode runMode) throws Exception {
		return dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
	}

	public List<Map<String, Object>> getDQResults(String dataQualExecUUID, String dataQualExecVersion, int offset, int limit, String sortBy, String order, String requestId, RunMode runMode) throws Exception {
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			limit = offset+limit;
			offset = offset+1;			
			DataQualExec dqExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(dataQualExecUUID,
					dataQualExecVersion, MetaType.dqExec.toString());
			dataStoreServiceImpl.setRunMode(runMode);
			DataStore datastore = dataStoreServiceImpl.getDatastore(dqExec.getResult().getRef().getUuid(),
					dqExec.getResult().getRef().getVersion());
	
			data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order, dqExec.getVersion());	
		}catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dqExec, dataQualExecUUID, dataQualExecVersion));
			commonServiceImpl.sendResponse("402", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.", dependsOn);
			throw new Exception((message != null) ? message : "Table not found.");
		}
		return data;
	}

	public List<Map<String, Object>> getResultDetail(String execUuid, String execVersion, int offset, int limit, String sortBy, String order, String requestId, RunMode runMode) throws Exception {
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			limit = offset+limit;
			offset = offset+1;			
			DataQualExec dqExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(execUuid,
					execVersion, MetaType.dqExec.toString());
			dataStoreServiceImpl.setRunMode(runMode);
			DataStore datastore = dataStoreServiceImpl.getDatastore(dqExec.getResult().getRef().getUuid(),
					dqExec.getResult().getRef().getVersion());
	
			data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order, dqExec.getVersion());	
		}catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dqExec, execUuid, execVersion));
			commonServiceImpl.sendResponse("402", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not fetch data.", dependsOn);
			throw new Exception((message != null) ? message : "Can not fetch data.");
		}
		return data;
	}
	
	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		DataQualExec dataQualExec = iDataQualExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.dq);
		mi.setUuid(dataQualExec.getDependsOn().getRef().getUuid());
		mi.setVersion(dataQualExec.getDependsOn().getRef().getVersion());
		return mi;
	}
	
	/**
	 * 
	 * @param baseExec
	 * @return
	 * @throws Exception
	 */
	public Status restart(BaseExec baseExec) throws Exception {
		try {
			return super.restart(baseExec.getUuid(), baseExec.getVersion(), MetaType.dqExec);
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	public void restart(String type,String uuid,String version, ExecParams execParams, RunMode runMode) throws Exception{
		//DataQualExec dataQualExec= dataQualExecServiceImpl.findOneByUuidAndVersion(uuid,version);
		DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(uuid,version, MetaType.dqExec.toString());
		try {
			HashMap<String, String> otherParams = null;
			if(execParams != null) 
				otherParams  = execParams.getOtherParams();
			
			dataQualExec = (DataQualExec) parse(uuid,version, null, otherParams, null, null, runMode);
			execute(dataQualExec.getDependsOn().getRef().getUuid(),dataQualExec.getDependsOn().getRef().getVersion(),dataQualExec,null, execParams, runMode);
		
		} catch (Exception e) {
			synchronized (dataQualExec.getUuid()) {
				try {
					commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.FAILED);
				} catch (Exception e1) {
					e1.printStackTrace();
					String message = null;
					try {
						message = e1.getMessage();
					}catch (Exception e2) {
						// TODO: handle exception
					}
					MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
					dependsOn.setRef(new MetaIdentifier(MetaType.dqExec,dataQualExec.getDependsOn().getRef().getUuid(),dataQualExec.getDependsOn().getRef().getVersion()));
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Data Quality.", dependsOn);
					throw new Exception((message != null) ? message : "Can not parse Data Quality.");
				}
			}
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dqExec, dataQualExec.getDependsOn().getRef().getUuid(),dataQualExec.getDependsOn().getRef().getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Data Quality.", dependsOn);
			throw new Exception((message != null) ? message : "Can not parse Data Quality.");
		}
		
	}

	@Override
	public BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode)
			throws Exception {
		logger.info("Inside dataQualServiceImpl.parse");
		if (datapodList != null) {
			logger.info(" Size of datapodList : " + datapodList.size());
		}

		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.dqExec.toString(), "N");
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.STARTING);
		}
		DataQual dataQual = (DataQual) commonServiceImpl.getOneByUuidAndVersion(dataQualExec.getDependsOn().getRef().getUuid(), dataQualExec.getDependsOn().getRef().getVersion(), MetaType.dq.toString(), "N");
		try{
			dataQualExec.setExec(dqOperator.generateSql(dataQual, datapodList, dataQualExec, dagExec, usedRefKeySet, otherParams, runMode));
			dataQualExec.setSummaryExec(dqOperator.generateSummarySql(dataQual, datapodList, dataQualExec, dagExec, usedRefKeySet, otherParams, runMode));
			dataQualExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
			logger.info(String.format("DQ Result sql for DQExec : %s is : ", execUuid, dataQualExec.getExec()));
			synchronized (dataQualExec.getUuid()) {
//				DataQualExec dataQualExec1 = (DataQualExec) daoRegister.getRefObject(new MetaIdentifier(MetaType.dqExec, dataQualExec.getUuid(), dataQualExec.getVersion()));
				DataQualExec dataQualExec1 = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(dataQualExec.getUuid(), dataQualExec.getVersion(), MetaType.dqExec.toString(), "N");
				dataQualExec1.setExec(dataQualExec.getExec());
				dataQualExec1.setRefKeyList(dataQualExec.getRefKeyList());
				commonServiceImpl.save(MetaType.dqExec.toString(), dataQualExec1);
				dataQualExec1 = null;
			}
		}catch(Exception e){
			commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.FAILED);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dqExec, dataQualExec.getUuid(), dataQualExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "FAILED data quality parsing.", dependsOn);
			throw new Exception((message != null) ? message : "FAILED data quality parsing.");
		}
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.READY);
		}
		return dataQualExec;
	}

	public HttpServletResponse download(String dqExecUuid, String dqExecVersion, String format,
			String download, int offset, int limit, HttpServletResponse response, int rowLimit, String sortBy,
			String order, String requestId, RunMode runMode) throws Exception {

		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
		if (rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of " + maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					"Requested rows exceeded the limit of " + maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of " + maxRows);
		}

		List<Map<String, Object>> results = getDQResults(dqExecUuid, dqExecVersion, offset, limit, sortBy,
				order, requestId, runMode);
		response = commonServiceImpl.download(format, response, runMode, results,
				new MetaIdentifierHolder(new MetaIdentifier(MetaType.dqExec, dqExecUuid, dqExecVersion)));
		return response;
	}
	
	public String getSummarySql(String tableName, String execVersion, ExecContext execContext) {
		StringBuilder filterBuilder = new StringBuilder(" ");
		if(!execContext.equals(ExecContext.FILE)
				|| !execContext.equals(ExecContext.spark)) {
			filterBuilder.append(" AND version = "+execVersion);
		}
		String sql = "SELECT datapodname, attributeId, attributename, 'NULL CHECK' as check_type, 'PASS' as result_type, count(nullCheck_pass) AS count FROM " + tableName + " WHERE nullCheck_pass = 'Y' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL " 
					+ "SELECT datapodname, attributeId, attributename, 'VALUE CHECK' as check_type, 'PASS' as result_type, count(valueCheck_pass) AS count FROM " + tableName + " WHERE valueCheck_pass = 'Y' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+  "SELECT datapodname, attributeId, attributename, 'RANGE CHECK' as check_type, 'PASS' as result_type, count(rangeCheck_pass) AS count FROM " + tableName + " WHERE rangeCheck_pass = 'Y' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'DATA TYPE CHECK' as check_type, 'PASS' as result_type, count(dataTypeCheck_pass) AS count FROM " + tableName + " WHERE dataTypeCheck_pass = 'Y' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'DATA FORMAT CHECK' as check_type, 'PASS' as result_type, count(dataFormatCheck_pass) AS count FROM " + tableName + " WHERE dataFormatCheck_pass = 'Y' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'LENGTH CHECK' as check_type, 'PASS' as result_type, count(lengthCheck_pass) AS count FROM " + tableName + " WHERE lengthCheck_pass = 'Y' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'REF INT CHECK' as check_type, 'PASS' as result_type, count(refIntegrityCheck_pass) AS count FROM " + tableName + " WHERE refIntegrityCheck_pass = 'Y' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'DUP CHECK' as check_type, 'PASS' as result_type, count(dupCheck_pass) AS count FROM " + tableName + " WHERE dupCheck_pass = 'Y' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'CUSTOM CHECK' as check_type, 'PASS' as result_type, count(customCheck_pass) AS count FROM " + tableName + " WHERE customCheck_pass = 'Y' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename "
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'NULL CHECK' as check_type, 'FAIL' as result_type, count(nullCheck_pass) AS count FROM " + tableName + " WHERE nullCheck_pass = 'N' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL " 
					+ "SELECT datapodname, attributeId, attributename, 'VALUE CHECK' as check_type, 'FAIL' as result_type, count(valueCheck_pass) AS count FROM " + tableName + " WHERE valueCheck_pass = 'N' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+  "SELECT datapodname, attributeId, attributename, 'RANGE CHECK' as check_type, 'FAIL' as result_type, count(rangeCheck_pass) AS count FROM " + tableName + " WHERE rangeCheck_pass = 'N' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'DATA TYPE CHECK' as check_type, 'FAIL' as result_type, count(dataTypeCheck_pass) AS count FROM " + tableName + " WHERE dataTypeCheck_pass = 'N' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'DATA FORMAT CHECK' as check_type, 'FAIL' as result_type, count(dataFormatCheck_pass) AS count FROM " + tableName + " WHERE dataFormatCheck_pass = 'N' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'LENGTH CHECK' as check_type, 'FAIL' as result_type, count(lengthCheck_pass) AS count FROM " + tableName + " WHERE lengthCheck_pass = 'N' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'REF INT CHECK' as check_type, 'FAIL' as result_type, count(refIntegrityCheck_pass) AS count FROM " + tableName + " WHERE refIntegrityCheck_pass = 'N' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'DUP CHECK' as check_type, 'FAIL' as result_type, count(dupCheck_pass) AS count FROM " + tableName + " WHERE dupCheck_pass = 'N' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'CUSTOM CHECK' as check_type, 'FAIL' as result_type, count(customCheck_pass) AS count FROM " + tableName + " WHERE customCheck_pass = 'N' "+filterBuilder.toString()+" GROUP BY datapodname, attributeId, attributename ";
		return sql;
	}
	
	
	public List<Map<String, Object>> getResultSummary(String dataQualExecUUID, String dataQualExecVersion, RunMode runMode) throws JsonProcessingException {
		DataQualExec dqExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(dataQualExecUUID,
				dataQualExecVersion, MetaType.dqExec.toString());
		DataStore datastore = dataStoreServiceImpl.getDatastore(dqExec.getResult().getRef().getUuid(),
				dqExec.getResult().getRef().getVersion());

		dataStoreServiceImpl.setRunMode(runMode);
		String tableName = null;
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			tableName = dataStoreServiceImpl.getTableNameByDatastore(datastore.getUuid(), datastore.getVersion(),
					runMode);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		ExecContext execContext = null;
		IExecutor exec = null;
		// String sql = null;
		String appUuid = null;
		if (runMode.equals(RunMode.ONLINE)) {
			execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark")
					|| engine.getExecEngine().equalsIgnoreCase("livy_spark"))
							? helper.getExecutorContext(engine.getExecEngine())
							: helper.getExecutorContext(ExecContext.spark.toString());
		} else {
			execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
		}
		exec = execFactory.getExecutor(execContext.toString());
		appUuid = commonServiceImpl.getApp().getUuid();
		data = exec.executeAndFetch(getSummarySql(tableName, dqExec.getVersion(), execContext), appUuid);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException | ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		ThreadPoolTaskExecutor metaExecutor = (execParams != null && execParams.getExecutionContext() != null && execParams.getExecutionContext().containsKey("EXECUTOR")) ? (ThreadPoolTaskExecutor)(execParams.getExecutionContext().get("EXECUTOR")) : null;
		@SuppressWarnings("unchecked")
		List<FutureTask<TaskHolder>> taskList = (execParams != null && execParams.getExecutionContext() != null && execParams.getExecutionContext().containsKey("TASKLIST")) ? (List<FutureTask<TaskHolder>>)(execParams.getExecutionContext().get("TASKLIST")) : null;
		execute(metaExecutor, (DataQualExec)baseExec, taskList, execParams, runMode);
		return null;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(), DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(), null, null, runMode);
	}

/*	@Override
	public Datasource getDatasource(BaseRule baseRule) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		MetaIdentifier datapodRef = ((DataQual)baseRule).getDependsOn().getRef();
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodRef.getUuid(), datapodRef.getVersion(), datapodRef.getType().toString());
		return commonServiceImpl.getDatasourceByDatapod(datapod);
	}*/
}

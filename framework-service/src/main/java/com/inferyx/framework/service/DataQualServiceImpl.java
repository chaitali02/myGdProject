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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Row;
import org.codehaus.jettison.json.JSONException;
import org.datavec.api.transform.quality.DataQualityAnalysis;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.DQInfo;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IDataQualDao;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseEntity;
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
import com.inferyx.framework.domain.RefIntegrity;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Threshold;
import com.inferyx.framework.enums.AbortConditionType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.ThresholdType;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.operator.DQOperator;
import com.inferyx.framework.register.GraphRegister;
import com.inferyx.framework.view.metadata.DQView;

@Service
public class DataQualServiceImpl extends RuleTemplate {

	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	IDataQualDao iDataQualDao;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	DQOperator dqOperator;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	DQInfo dqInfo;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	Engine engine;
	
	public IDataQualDao getiDataQualDao() {
		return iDataQualDao;
	}

	public void setiDataQualDao(IDataQualDao iDataQualDao) {
		this.iDataQualDao = iDataQualDao;
	}

	Map<String, String> requestMap = new HashMap<String, String>();

	static final Logger logger = Logger.getLogger(DataQualServiceImpl.class);

	public DataQual save(DQView dqView) throws Exception {
		DataQual dq = new DataQual();
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		if (dqView.getUuid() != null) {
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
		// dq.setStdDevCheck(dqView.getStdDevCheck());
		dq.setValueCheck(dqView.getValueCheck());
		dq.setTarget(dqView.getTarget());
		Filter filter = null;
//		List<AttributeRefHolder> filterList = new ArrayList<AttributeRefHolder>();
//		AttributeRefHolder filterMeta = new AttributeRefHolder();
		if (dqView.getFilter() != null) {
			filter = dqView.getFilter();
			filter.setName(dqView.getName());
			filter.setDesc(dqView.getDesc());
			filter.setTags(dqView.getTags());
			MetaIdentifierHolder filterInfo = new MetaIdentifierHolder();
			filterInfo.setRef(dqView.getDependsOn().getRef());
			filter.setDependsOn(filterInfo);
		}
		if (dqView.getFilterChg().equalsIgnoreCase("y")) {
			// filterServiceImpl.save(filter);
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
		DataQual dataqual = iDataQualDao.save(dq);
		registerGraph.updateGraph((Object) dataqual, MetaType.dq);
		return dataqual;
	}

	/********************** UNUSED **********************/
//	public boolean isExists(String id) {
//		return iDataQualDao.exists(id);
//	}

	public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		DataQual dataQual = iDataQualDao.findOneById(appUuid, Id);
		dataQual.setActive("N");
		iDataQualDao.save(dataQual);
	}

	/********************** UNUSED **********************/
//	public List<DataQual> findAllByUuid(String uuid) {
//		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
//		return iDataQualDao.findAllByUuid(appUuid, uuid);
//
//	}

	
	/********************** UNUSED **********************/
//	public DataQual findLatestByUuid(String uuid) {
//		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
//				? securityServiceImpl.getAppInfo().getRef().getUuid()
//				: null;
//		if (appUuid == null) {
//			return iDataQualDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
//		}
//		return iDataQualDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
//	}

	/********************** UNUSED **********************/
//	public List<DataQual> findAllLatest() {
//		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
//				? securityServiceImpl.getAppInfo().getRef().getUuid()
//				: null;
//		Aggregation dqAggr = newAggregation(group("uuid").max("version").as("version"));
//		AggregationResults<DataQual> dqResults = mongoTemplate.aggregate(dqAggr, "dq", DataQual.class);
//		List<DataQual> dataQualList = dqResults.getMappedResults();
//
//		List<DataQual> result = new ArrayList<DataQual>();
//		for (DataQual d : dataQualList) {
//			DataQual dqLatest;
//			if (appUuid != null) {
//				dqLatest = iDataQualDao.findOneByUuidAndVersion(appUuid, d.getId(), d.getVersion());
//			} else {
//				dqLatest = iDataQualDao.findOneByUuidAndVersion(d.getId(), d.getVersion());
//			}
//			if (dqLatest != null) {
//				result.add(dqLatest);
//			}
//		}
//		return result;
//	}

	/********************** UNUSED **********************/
//	public List<DataQual> findAllLatestActive() {
//		Aggregation dqAggr = newAggregation(match(Criteria.where("active").is("Y")),
//				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
//		AggregationResults<DataQual> dqResults = mongoTemplate.aggregate(dqAggr, "dq", DataQual.class);
//		List<DataQual> dqList = dqResults.getMappedResults();
//
//		List<DataQual> result = new ArrayList<DataQual>();
//		for (DataQual r : dqList) {
//			DataQual dqLatest;
//			String appUuid = (securityServiceImpl.getAppInfo() != null
//					&& securityServiceImpl.getAppInfo().getRef() != null)
//							? securityServiceImpl.getAppInfo().getRef().getUuid()
//							: null;
//			if (appUuid != null) {
//				dqLatest = iDataQualDao.findOneByUuidAndVersion(appUuid, r.getId(), r.getVersion());
//			} else {
//				dqLatest = iDataQualDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
//			}
//			if (dqLatest != null) {
//				result.add(dqLatest);
//			}
//		}
//		return result;
//	}

	public DataQualExec create(String dataQualUUID, String dataQualVersion, Map<String, MetaIdentifier> refKeyMap,
			List<String> datapodList, DagExec dagExec) throws Exception {
		try {
			return (DataQualExec) super.create(dataQualUUID, dataQualVersion, MetaType.dq, MetaType.dqExec, null,
					refKeyMap, datapodList, dagExec);
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dagExec, dagExec.getUuid(), dagExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Can not create DQExec.", dependsOn);
			throw new Exception((message != null) ? message : "Can not create DQExec.");
		}
	}

	public DataQualExec create(String dataQualUUID, String dataQualVersion, DataQualExec dataQualExec,
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception {
		try {
			return (DataQualExec) super.create(dataQualUUID, dataQualVersion, MetaType.dq, MetaType.dqExec,
					dataQualExec, refKeyMap, datapodList, dagExec);
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dagExec, dagExec.getUuid(), dagExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Can not create DQExec.", dependsOn);
			throw new Exception((message != null) ? message : "Can not create DQExec.");
		}
	}

	public DataQualExec execute(String dataqualUUID, String dataqualVersion, DataQualExec dataqualExec,
			DataQualGroupExec dataqualGroupExec, ExecParams execParams, RunMode runMode) throws Exception {
		execute(null, dataqualExec, null, execParams, runMode);
		return dataqualExec;
	}
	
	/**
	 * 
	 * @param dataqualUUID
	 * @param dataqualVersion
	 * @param dataqualExec
	 * @param dataqualGroupExec
	 * @param execParams
	 * @param runMode
	 * @return
	 * @throws JsonProcessingException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NullPointerException
	 * @throws IOException
	 * @throws ParseException
	 */
	
	/**
	 * 
	 */
	protected MetaIdentifier getTargetSummaryDp () throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return getSummaryOrDetail("framework.dataqual.summary.uuid", (dqInfo != null) ? dqInfo.getDq_result_summary() : null);
	}

	/**
	 * 
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
	protected MetaIdentifier getTargetResultDp () throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return getSummaryOrDetail("framework.dataqual.detail.uuid", (dqInfo != null) ? dqInfo.getDq_result_detail() : null);
	}


	public DataQualExec execute(ThreadPoolTaskExecutor metaExecutor, DataQualExec dataqualExec,
			List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
		try {
			dataqualExec = (DataQualExec) super.execute(MetaType.dq, MetaType.dqExec, metaExecutor, dataqualExec,
												getTargetResultDp(), taskList, execParams, runMode);
			return dataqualExec;
		} catch (Exception e) {
			e.printStackTrace();
			dataqualExec = (DataQualExec) commonServiceImpl.setMetaStatus(dataqualExec, MetaType.dqExec,
					Status.Stage.FAILED);
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dqExec, dataqualExec.getUuid(), dataqualExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Data Quality execution FAILED.", dependsOn);
			throw new Exception((message != null) ? message : "Data Quality execution FAILED.");
		}
	}

	@Override
	public BaseRuleExec execute(ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec,
			MetaIdentifier datapodKey, List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode)
			throws Exception {
		return execute(metaExecutor, (DataQualExec) baseRuleExec, taskList, execParams, runMode);
	}

	/********************** UNUSED **********************/
//	public String getTableName(Datapod datapod, RunMode runMode) throws Exception {
//		return datapodServiceImpl.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()),
//				runMode);
//	}

	public List<Map<String, Object>> getDQResults(String dataQualExecUUID, String dataQualExecVersion, int offset,
			int limit, String sortBy, String order, String requestId, RunMode runMode) throws Exception {
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			limit = offset + limit;
			offset = offset + 1;
			DataQualExec dqExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(dataQualExecUUID,
					dataQualExecVersion, MetaType.dqExec.toString());
			dataStoreServiceImpl.setRunMode(runMode);
			DataStore datastore = dataStoreServiceImpl.getDatastore(dqExec.getResult().getRef().getUuid(),
					dqExec.getResult().getRef().getVersion());

			data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId,
					offset, limit, sortBy, order, dqExec.getVersion());
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dqExec, dataQualExecUUID, dataQualExecVersion));
			commonServiceImpl.sendResponse("402", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Table not found.", dependsOn);
			throw new Exception((message != null) ? message : "Table not found.");
		}
		return data;
	}

	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.dqExec.toString());
//		MetaIdentifier mi = new MetaIdentifier();
//		mi.setType(MetaType.dq);
//		mi.setUuid(dataQualExec.getDependsOn().getRef().getUuid());
//		mi.setVersion(dataQualExec.getDependsOn().getRef().getVersion());
		return new MetaIdentifier(MetaType.dq, dataQualExec.getDependsOn().getRef().getUuid(), dataQualExec.getDependsOn().getRef().getVersion());
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

	public void restart(String type, String uuid, String version, ExecParams execParams, RunMode runMode)
			throws Exception {
		// DataQualExec dataQualExec=
		// dataQualExecServiceImpl.findOneByUuidAndVersion(uuid,version);
		DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version,
				MetaType.dqExec.toString());
		try {
			HashMap<String, String> otherParams = null;
			if (execParams != null)
				otherParams = execParams.getOtherParams();

			dataQualExec = (DataQualExec) parse(uuid, version, null, otherParams, null, null, runMode);
			execute(dataQualExec.getDependsOn().getRef().getUuid(), dataQualExec.getDependsOn().getRef().getVersion(),
					dataQualExec, null, execParams, runMode);

		} catch (Exception e) {
			synchronized (dataQualExec.getUuid()) {
				try {
					commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.FAILED);
				} catch (Exception e1) {
					e1.printStackTrace();
					String message = null;
					try {
						message = e1.getMessage();
					} catch (Exception e2) {
						// TODO: handle exception
					}
					MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
					dependsOn.setRef(new MetaIdentifier(MetaType.dqExec, dataQualExec.getDependsOn().getRef().getUuid(),
							dataQualExec.getDependsOn().getRef().getVersion()));
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
							(message != null) ? message : "Can not parse Data Quality.", dependsOn);
					throw new Exception((message != null) ? message : "Can not parse Data Quality.");
				}
			}
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dqExec, dataQualExec.getDependsOn().getRef().getUuid(),
					dataQualExec.getDependsOn().getRef().getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Can not parse Data Quality.", dependsOn);
			throw new Exception((message != null) ? message : "Can not parse Data Quality.");
		}

	}

	@Override
	public BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode)
			throws Exception {
		logger.info("Inside dataQualServiceImpl.parse");
		logger.info("OtherParams : " + otherParams);
		if (datapodList != null) {
			logger.info(" Size of datapodList : " + datapodList.size());
		}

		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
				MetaType.dqExec.toString(), "N");
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.STARTING);
		}
		DataQual dataQual = (DataQual) commonServiceImpl.getOneByUuidAndVersion(
				dataQualExec.getDependsOn().getRef().getUuid(), dataQualExec.getDependsOn().getRef().getVersion(),
				MetaType.dq.toString(), "Y");
		try {
			dataQualExec.setExec(dqOperator.generateSql(dataQual, datapodList, dataQualExec, dagExec, usedRefKeySet,
					otherParams, runMode, null));
//			dataQualExec.setExec(dqOperator.generateResFilteredSql(dataQual, datapodList, dataQualExec, dagExec, usedRefKeySet,
//					otherParams, runMode));
			dataQualExec.setSummaryExec(dqOperator.generateSummarySql(dataQual, datapodList, dataQualExec, dagExec, getTargetSummaryDp(), 
					usedRefKeySet, otherParams, runMode));
			dataQualExec.setAbortExec(dqOperator.generateAbortQuery(dataQual, datapodList, dataQualExec, dagExec, getTargetSummaryDp(), otherParams, runMode));
			dataQualExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
			logger.info(String.format("DQ Result sql for DQExec : %s is : %s", execUuid, dataQualExec.getExec()));
			synchronized (dataQualExec.getUuid()) {
//				DataQualExec dataQualExec1 = (DataQualExec) daoRegister.getRefObject(new MetaIdentifier(MetaType.dqExec, dataQualExec.getUuid(), dataQualExec.getVersion()));
				DataQualExec dataQualExec1 = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(
						dataQualExec.getUuid(), dataQualExec.getVersion(), MetaType.dqExec.toString(), "N");
				dataQualExec1.setExec(dataQualExec.getExec());
				dataQualExec1.setRefKeyList(dataQualExec.getRefKeyList());
				commonServiceImpl.save(MetaType.dqExec.toString(), dataQualExec1);
				dataQualExec1 = null;
			}
		} catch (Exception e) {
			commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.FAILED);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dqExec, dataQualExec.getUuid(), dataQualExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "FAILED data quality parsing.", dependsOn);
			throw new Exception((message != null) ? message : "FAILED data quality parsing.");
		}
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.READY);
		}
		return dataQualExec;
	}

	public HttpServletResponse download(String dqExecUuid, String dqExecVersion, String format, String download,
			int offset, int limit, HttpServletResponse response, int rowLimit, String sortBy, String order,
			String requestId, RunMode runMode, String resultType) throws Exception {

		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
		if (rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of " + maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					"Requested rows exceeded the limit of " + maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of " + maxRows);
		}

		List<Map<String, Object>> results = null;
		if(resultType == null || (resultType != null && resultType.equalsIgnoreCase("summary"))) {
			results = getResultSummary(dqExecUuid, dqExecVersion, offset, rowLimit, sortBy, order, requestId, runMode);
		} else {
			results = getResultDetails(dqExecUuid, dqExecVersion, offset, rowLimit, sortBy, order, requestId, runMode);
		}
		response = commonServiceImpl.download(format, response, runMode, results,
				new MetaIdentifierHolder(new MetaIdentifier(MetaType.dqExec, dqExecUuid, dqExecVersion)));
		return response;
	}

	public String getSummarySql(String tableName, String execVersion, ExecContext execContext) {
		StringBuilder filterBuilder = new StringBuilder(" ");
		if (!execContext.equals(ExecContext.FILE) || !execContext.equals(ExecContext.spark)) {
			filterBuilder.append(" AND version = " + execVersion);
		}
		String sql = "SELECT datapodname, attributeId, attributename, 'NULL CHECK' as check_type, 'PASS' as result_type, count(nullCheck_pass) AS count FROM "
				+ tableName + " WHERE nullCheck_pass = 'Y' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'VALUE CHECK' as check_type, 'PASS' as result_type, count(valueCheck_pass) AS count FROM "
				+ tableName + " WHERE valueCheck_pass = 'Y' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'RANGE CHECK' as check_type, 'PASS' as result_type, count(rangeCheck_pass) AS count FROM "
				+ tableName + " WHERE rangeCheck_pass = 'Y' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'DATA TYPE CHECK' as check_type, 'PASS' as result_type, count(dataTypeCheck_pass) AS count FROM "
				+ tableName + " WHERE dataTypeCheck_pass = 'Y' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'DATA FORMAT CHECK' as check_type, 'PASS' as result_type, count(dataFormatCheck_pass) AS count FROM "
				+ tableName + " WHERE dataFormatCheck_pass = 'Y' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'LENGTH CHECK' as check_type, 'PASS' as result_type, count(lengthCheck_pass) AS count FROM "
				+ tableName + " WHERE lengthCheck_pass = 'Y' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'REF INT CHECK' as check_type, 'PASS' as result_type, count(refIntegrityCheck_pass) AS count FROM "
				+ tableName + " WHERE refIntegrityCheck_pass = 'Y' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'DUP CHECK' as check_type, 'PASS' as result_type, count(dupCheck_pass) AS count FROM "
				+ tableName + " WHERE dupCheck_pass = 'Y' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'CUSTOM CHECK' as check_type, 'PASS' as result_type, count(customCheck_pass) AS count FROM "
				+ tableName + " WHERE customCheck_pass = 'Y' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'NULL CHECK' as check_type, 'FAIL' as result_type, count(nullCheck_pass) AS count FROM "
				+ tableName + " WHERE nullCheck_pass = 'N' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'VALUE CHECK' as check_type, 'FAIL' as result_type, count(valueCheck_pass) AS count FROM "
				+ tableName + " WHERE valueCheck_pass = 'N' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'RANGE CHECK' as check_type, 'FAIL' as result_type, count(rangeCheck_pass) AS count FROM "
				+ tableName + " WHERE rangeCheck_pass = 'N' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'DATA TYPE CHECK' as check_type, 'FAIL' as result_type, count(dataTypeCheck_pass) AS count FROM "
				+ tableName + " WHERE dataTypeCheck_pass = 'N' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'DATA FORMAT CHECK' as check_type, 'FAIL' as result_type, count(dataFormatCheck_pass) AS count FROM "
				+ tableName + " WHERE dataFormatCheck_pass = 'N' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'LENGTH CHECK' as check_type, 'FAIL' as result_type, count(lengthCheck_pass) AS count FROM "
				+ tableName + " WHERE lengthCheck_pass = 'N' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'REF INT CHECK' as check_type, 'FAIL' as result_type, count(refIntegrityCheck_pass) AS count FROM "
				+ tableName + " WHERE refIntegrityCheck_pass = 'N' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'DUP CHECK' as check_type, 'FAIL' as result_type, count(dupCheck_pass) AS count FROM "
				+ tableName + " WHERE dupCheck_pass = 'N' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename " + " UNION ALL "
				+ "SELECT datapodname, attributeId, attributename, 'CUSTOM CHECK' as check_type, 'FAIL' as result_type, count(customCheck_pass) AS count FROM "
				+ tableName + " WHERE customCheck_pass = 'N' " + filterBuilder.toString()
				+ " GROUP BY datapodname, attributeId, attributename ";
		return sql;
	}

	public List<Map<String, Object>> getSummary(String dataQualExecUUID, String dataQualExecVersion, RunMode runMode)
			throws Exception {
		DataQualExec dqExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(dataQualExecUUID,
				dataQualExecVersion, MetaType.dqExec.toString());
		DataStore datastore = dataStoreServiceImpl.getDatastore(dqExec.getResult().getRef().getUuid(),
				dqExec.getResult().getRef().getVersion());

		dataStoreServiceImpl.setRunMode(runMode);
		String tableName = null;
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			tableName = dataStoreServiceImpl.getTableNameByDatastoreKey(datastore.getUuid(), datastore.getVersion(),
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
		} catch (Exception e) {
			logger.error("Exception in DataQualServiceImpl", e);
			e.printStackTrace();
			throw e;
		}
		return data;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		ThreadPoolTaskExecutor metaExecutor = (execParams != null && execParams.getExecutionContext() != null
				&& execParams.getExecutionContext().containsKey("EXECUTOR"))
						? (ThreadPoolTaskExecutor) (execParams.getExecutionContext().get("EXECUTOR"))
						: null;
		@SuppressWarnings("unchecked")
		List<FutureTask<TaskHolder>> taskList = (execParams != null && execParams.getExecutionContext() != null
				&& execParams.getExecutionContext().containsKey("TASKLIST"))
						? (List<FutureTask<TaskHolder>>) (execParams.getExecutionContext().get("TASKLIST"))
						: null;
		execute(metaExecutor, (DataQualExec) baseExec, taskList, execParams, runMode);
		return null;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(),
				DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(), null, null,
				runMode);
	}

	public List<Map<String, Object>> getResultSummary(String execUuid, String execVersion, int offset, int limit,
			String sortBy, String order, String requestId, RunMode runMode)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NullPointerException, JSONException, ParseException, IOException {
		DataQualExec dqExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
				MetaType.dqExec.toString());
		try {
//			Datapod summaryDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
//					Helper.getPropertyValue("framework.dataqual.summary.uuid"), null, MetaType.datapod.toString(), "N");

			limit = offset + limit;
			offset = offset + 1;
			dataStoreServiceImpl.setRunMode(runMode);
			DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(dqExec.getSummaryResult().getRef().getUuid(),
					dqExec.getSummaryResult().getRef().getVersion(),MetaType.datastore.toString());
//			DataStore datastore = dataStoreServiceImpl.findDataStoreByMeta(summaryDp.getUuid(), summaryDp.getVersion());

			return dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId,
					offset, limit, sortBy, order, dqExec.getVersion());
//			Datasource summaryDpDs = commonServiceImpl.getDatasourceByDatapod(summaryDp);
//
//			String tableName = getTableName(summaryDpDs, summaryDp, dqExec, runMode);
//
//			String sql = "SELECT * FROM " + tableName;
//
//			Datasource appDS = commonServiceImpl.getDatasourceByApp();
//			IExecutor exec = execFactory.getExecutor(appDS.getType().toLowerCase());
//
//			String appUuid = commonServiceImpl.getApp().getUuid();
//			if (runMode.equals(RunMode.ONLINE)) {
//				return sparkExecutor.executeAndFetchFromTempTable(sql, appUuid);
//			} else {
//				if (summaryDpDs.getType().equalsIgnoreCase(ExecContext.FILE.toString())
//						|| summaryDpDs.getType().equalsIgnoreCase(ExecContext.spark.toString())) {
//					String dafaultPath = Helper.getPropertyValue("framework.schema.Path");
//					dafaultPath = dafaultPath.endsWith("/") ? dafaultPath : dafaultPath.concat("/");
//					String filePath = String.format("%s/%s/%s", summaryDp.getUuid(), summaryDp.getVersion(),
//							dqExec.getVersion());
//					String filePathUrl = hdfsInfo.getHdfsURL().concat(dafaultPath).concat(filePath);
//					List<String> filePathUrlList = new ArrayList<>();
//					filePathUrlList.add(filePathUrl);
//					sparkExecutor.readAndRegisterFile(tableName, filePathUrlList, FileType.PARQUET.toString(), "true",
//							appUuid, true);
//				}
//				return exec.executeAndFetchByDatasource(sql, summaryDpDs, appUuid);
//			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Can not fetch summary.",
					new MetaIdentifierHolder(new MetaIdentifier(MetaType.dqExec, execUuid, execVersion)));
			throw new RuntimeException((message != null) ? message : "Can not fetch summary.");
		}
	}

	/*public String getTableName(Datasource datasource, Datapod datapod, DataQualExec dqExec, RunMode runMode) {
		if (runMode.equals(RunMode.ONLINE)) {
			return Helper.genTableName(datapod.getUuid(), datapod.getVersion(), dqExec.getVersion());
		} else {
			if (datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())) {
				return Helper.genTableName(datapod.getUuid(), datapod.getVersion(), dqExec.getVersion());
		} else {
			if (datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
				return datasource.getSid().concat(".").concat(datapod.getName());
			} else {
				return datasource.getDbname().concat(".").concat(datapod.getName());
			}
		}
		}
	}*/

	public List<Map<String, Object>> getResultDetails(String execUuid, String execVersion, int offset, int limit,
			String sortBy, String order, String requestId, RunMode runMode)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NullPointerException, JSONException, ParseException, IOException {
		DataQualExec dqExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
				MetaType.dqExec.toString());
		try {
//			Datapod detailsDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
//					Helper.getPropertyValue("framework.dataqual.detail.uuid"), null, MetaType.datapod.toString(), "N");
			
			limit = offset + limit;
			offset = offset + 1;
			dataStoreServiceImpl.setRunMode(runMode);
			DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(dqExec.getResult().getRef().getUuid(),
					dqExec.getResult().getRef().getVersion(),MetaType.datastore.toString());
//			DataStore datastore = dataStoreServiceImpl.findDataStoreByMeta(detailsDp.getUuid(), detailsDp.getVersion());

			return dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId,
					offset, limit, sortBy, order, dqExec.getVersion());
//			Datasource detailsDpDs = commonServiceImpl.getDatasourceByDatapod(detailsDp);
//
//			String tableName = getTableName(detailsDpDs, detailsDp, dqExec, runMode);
//
//			String sql = "SELECT * FROM " + tableName;
//
//			Datasource appDS = commonServiceImpl.getDatasourceByApp();
//			IExecutor exec = execFactory.getExecutor(appDS.getType().toLowerCase());
//
//			String appUuid = commonServiceImpl.getApp().getUuid();
//			if (runMode.equals(RunMode.ONLINE)) {
//				return sparkExecutor.executeAndFetchFromTempTable(sql, appUuid);
//			} else {
//				if (detailsDpDs.getType().equalsIgnoreCase(ExecContext.FILE.toString())
//						|| detailsDpDs.getType().equalsIgnoreCase(ExecContext.spark.toString())) {
//					String dafaultPath = Helper.getPropertyValue("framework.schema.Path");
//					dafaultPath = dafaultPath.endsWith("/") ? dafaultPath : dafaultPath.concat("/");
//					String filePath = String.format("%s/%s/%s", detailsDp.getUuid(), detailsDp.getVersion(),
//							dqExec.getVersion());
//					String filePathUrl = hdfsInfo.getHdfsURL().concat(dafaultPath).concat(filePath);
//					List<String> filePathUrlList = new ArrayList<>();
//					filePathUrlList.add(filePathUrl);
//					sparkExecutor.readAndRegisterFile(tableName, filePathUrlList, FileType.PARQUET.toString(), "true",
//							appUuid, true);
//				}
//				return exec.executeAndFetchByDatasource(sql, detailsDpDs, appUuid);
//			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException | IOException e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Can not fetch result details.",
					new MetaIdentifierHolder(new MetaIdentifier(MetaType.dqExec, execUuid, execVersion)));
			throw new RuntimeException((message != null) ? message : "Can not fetch result details");
		}
	}
	/*
	 * @Override public Datasource getDatasource(BaseRule baseRule) throws
	 * JsonProcessingException, IllegalAccessException, IllegalArgumentException,
	 * InvocationTargetException, NoSuchMethodException, SecurityException,
	 * NullPointerException, ParseException { MetaIdentifier datapodRef =
	 * ((DataQual)baseRule).getDependsOn().getRef(); Datapod datapod = (Datapod)
	 * commonServiceImpl.getOneByUuidAndVersion(datapodRef.getUuid(),
	 * datapodRef.getVersion(), datapodRef.getType().toString()); return
	 * commonServiceImpl.getDatasourceByDatapod(datapod); }
	 */

	public List<BaseEntity> createDQRuleForDatapod(String datapodUuid, String datapodVersion, RunMode runMode)
			throws JSONException, ParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException,
			NullPointerException, IOException {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();

		Datapod dp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodUuid, datapodVersion,
				MetaType.datapod.toString(), "Y");
		MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
		dependsOn.setRef(new MetaIdentifier());
		dependsOn.getRef().setUuid(dp.getUuid());
		dependsOn.getRef().setType(MetaType.datapod);
		dependsOn.getRef().setVersion(dp.getVersion());

		MetaIdentifier miIdentifier = new MetaIdentifier();
		miIdentifier.setUuid(dp.getUuid());
		miIdentifier.setType(MetaType.datapod);
		miIdentifier.setVersion(dp.getVersion());
		List<Attribute> attrList = dp.getAttributes();
		List<BaseEntity> baseEntities = new ArrayList<BaseEntity>();
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> appInfo = new ArrayList<MetaIdentifierHolder>();
		appInfo.add(meta);
		int count=1;
		for (Attribute attr : attrList) {
			DataQual dataqual = new DataQual();
			dataqual.setName("dqr_" + dp.getName() + "_rule" +count);
			count++;
			dataqual.setPublicFlag("Y");
			dataqual.setPublicFlag("N");
			dataqual.setDependsOn(dependsOn);

			AttributeRefHolder attributeRefHolder = new AttributeRefHolder();
			attributeRefHolder.setRef(miIdentifier);
			attributeRefHolder.setAttrId(String.valueOf(attr.getAttributeId()));
			attributeRefHolder.setAttrName(attr.getName());

			dataqual.setAttribute(attributeRefHolder);
			dataqual.setValueCheck(new ArrayList<String>());
			dataqual.setDataTypeCheck(new String());
			dataqual.setCaseCheck(null);
			dataqual.setDateFormatCheck(null);

			dataqual.setDuplicateKeyCheck("N");
			dataqual.setNullCheck("Y");
			
			dataqual.setAppInfo(appInfo);
			dataqual.setRangeCheck(new HashMap<String, String>());
			dataqual.setLengthCheck(new HashMap<String, Long>());
			dataqual.setRefIntegrityCheck(new RefIntegrity());
			dataqual.setActive("Y");
			dataqual.setLocked("N");

			Threshold thresholdInfo = new Threshold();
			thresholdInfo.setHigh("75");
			thresholdInfo.setLow("25");
			thresholdInfo.setMedium("50");
			thresholdInfo.setType(ThresholdType.NUMERIC);
			dataqual.setThresholdInfo(thresholdInfo);
			Object obje = dataqual;
			Object metaObj = mapper.convertValue(obje, Helper.getDomainClass(MetaType.dq));

			// }
			Helper.getDomainClass(MetaType.dq).getSuperclass().getMethod("setBaseEntity").invoke(metaObj);

			Object iDao = commonServiceImpl.getClass().getMethod("get" + Helper.getDaoClass(MetaType.dq))
					.invoke(commonServiceImpl);
			BaseEntity objDet = (BaseEntity) (iDao.getClass().getMethod("save", Object.class).invoke(iDao, metaObj));
		
			String jsonInString =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metaObj);
	    	String qq =jsonInString.substring(2, jsonInString.indexOf(",")+1);
			
	    	jsonInString=jsonInString.replace(qq, "");
			FileUtils.writeStringToFile(new File("/home/gridedge-1/git/inferyx/framework-web/app/edw/meta/dqnew/"+dataqual.getName()+".json"), jsonInString);
		
		   //	registerGraph.updateGraph((Object) objDet, MetaType.dq);
			// BaseEntity baseEntity = (BaseEntity)
			// commonServiceImpl.save(MetaType.dq.toString(), obje);
			baseEntities.add(objDet);
		}

		return baseEntities;
	}
}

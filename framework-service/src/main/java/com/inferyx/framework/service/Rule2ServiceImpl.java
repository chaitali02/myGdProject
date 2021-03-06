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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSetHolder;
import com.inferyx.framework.domain.BusinessRule;
import com.inferyx.framework.domain.Rule2Exec;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SaveMode;
import com.inferyx.framework.enums.ScoringMethod;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.operator.Rule2Operator;

@Service
public class Rule2ServiceImpl extends RuleTemplate {
	@Autowired
	private Rule2Operator rule2Operator;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private DownloadServiceImpl downloadServiceImpl;

	Map<String, List<Map<String, Object>>> requestMap = new HashMap<>();


	static final Logger logger = Logger.getLogger(Rule2ServiceImpl.class);


	/********************************Unused******************************/
	/*public BusinessRule resolveName(BusinessRule rule2) throws JsonProcessingException {
		String createdByRefUuid = rule2.getCreatedBy().getRef().getUuid();
		// User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
		rule2.getCreatedBy().getRef().setName(user.getName());
		return rule2;
	}*/

	public void restart(String type, String uuid, String version, List<FutureTask<TaskHolder>> taskList,
			ThreadPoolTaskExecutor metaExecutor, ExecParams execParams, RunMode runMode) throws Exception {
		// RuleExec ruleExec= ruleExecServiceImpl.findOneByUuidAndVersion(uuid,
		// version);
		RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version,
				MetaType.ruleExec.toString());		
		try {
			HashMap<String, String> otherParams = null;
			if(execParams != null) 
				otherParams = execParams.getOtherParams();
			
			ruleExec = parse(ruleExec.getUuid(), ruleExec.getVersion(), null, otherParams, null, null, runMode);			
			execute(metaExecutor, ruleExec, null, taskList, execParams, runMode);
			
		} catch (Exception e) {
			synchronized (ruleExec.getUuid()) {
				try {
					commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.FAILED);
				} catch (Exception e1) {
					e1.printStackTrace();
					String message = null;
					try {
						message = e1.getMessage();
					}catch (Exception e2) {
						// TODO: handle exception
					}
					MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
					dependsOn.setRef(new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion()));
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Business Rule2.", dependsOn);
					throw new Exception((message != null) ? message : "Can not parse Business Rule.");
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
			dependsOn.setRef(new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Business Rule.", dependsOn);
			throw new Exception((message != null) ? message : "Can not parse Business Rule.");
		}
	}
	
	/**
	 * 
	 * @param baseExec
	 * @return
	 * @throws Exception
	 */
	public Status restart(BaseExec baseExec) throws Exception {
		try {
			return super.restart(baseExec.getUuid(), baseExec.getVersion(), MetaType.ruleExec);
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	/**
	 * Create using template method pattern
	 * 
	 * @param ruleUUID
	 * @param ruleVersion
	 * @param baseExec
	 * @param refKeyMap
	 * @param execParams
	 * @param datapodList
	 * @param dagExec
	 * @param batch 
	 * @return
	 * @throws Exception 
	 */
	public RuleExec create(String ruleUUID, String ruleVersion, RuleExec baseExec,
			java.util.Map<String, MetaIdentifier> refKeyMap, ExecParams execParams, List<String> datapodList,
			DagExec dagExec, RunMode runmode) throws Exception {
		try {			
			baseExec = (RuleExec) super.create(ruleUUID, ruleVersion, MetaType.rule2, MetaType.ruleExec, baseExec,
					refKeyMap, datapodList, dagExec, runmode);
			if(execParams != null) {
				baseExec.setExecParams(execParams);
				commonServiceImpl.save(MetaType.ruleExec.toString(), baseExec);
			}else {
				baseExec.setExecParams(execParams);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.ruleExec, baseExec.getUuid(), baseExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create Business Rule.", dependsOn);
			throw new Exception((message != null) ? message : "Can not create Business Rule.");
		}
		return baseExec;
	}

	
	/**
	 * Executes a rule
	 * 
	 * @param uuid
	 * @param version
	 * @param metaExecutor
	 * @param ruleExec
	 * @param ruleGroupExec
	 * @param taskList
	 * @return
	 * @throws Exception
	 */
	public RuleExec execute(ThreadPoolTaskExecutor metaExecutor, RuleExec ruleExec,
			List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
		logger.info("Inside ruleServiceImpl.execute");
		try {
			ruleExec = (RuleExec) super.execute(MetaType.rule2, MetaType.ruleExec, metaExecutor, ruleExec,
												getTargetResultDp(), taskList, execParams, runMode);
		} catch (Exception e) {
			synchronized (ruleExec.getUuid()) {
				commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.FAILED);
			}
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Business Rule2 execution FAILED.", dependsOn);
			throw new Exception((message != null) ? message : "Business Rule2 execution FAILED.");
		}
		return ruleExec;
	}

	
	public List<Map<String, Object>> getRule2Results(String ruleExecUUID, String ruleExecVersion, int offset, int limit,
			String sortBy, String order, String requestId, RunMode runMode) throws Exception {
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			limit = offset + limit;
			offset = offset + 1;
			RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExecUUID, ruleExecVersion,
					MetaType.ruleExec.toString());
			DataStore datastore = dataStoreServiceImpl.getDatastore(ruleExec.getSummaryResult().getRef().getUuid(),
					ruleExec.getSummaryResult().getRef().getVersion());

			data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order, null,runMode);
			
			/*boolean requestIdExistFlag = false;
			StringBuilder orderBy = new StringBuilder();
			dataStoreServiceImpl.setRunMode(runMode);
			String tableName = dataStoreServiceImpl.getTableNameByDatastoreKey(datastore.getUuid(),
					datastore.getVersion(), runMode);
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			ExecContext execContext = null;
			IExecutor exec = null;
			//String sql = null;
			String appUuid = null;
			if (runMode.equals(Mode.ONLINE)) {
				execContext = helper.getExecutorContext(engine.getExecEngine());
				appUuid = commonServiceImpl.getApp().getUuid();
			} else {
				execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
			}
			exec = execFactory.getExecutor(execContext.toString());
			if (requestId == null|| requestId.equals("null") || requestId.isEmpty()) {
				if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
					data = exec.executeAndFetch("Select * from (Select Row_Number() Over(ORDER BY 1) as rownum, * from "
							+ tableName + ")as tab where rownum >= " + offset + " AND rownum <= " + limit, appUuid);
				} else {
					if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
						if (runMode.equals(Mode.ONLINE))
							data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit, appUuid);
						else
							data = exec.executeAndFetch("Select * from " + tableName + " where rownum< " + limit,
									appUuid);
					else {
						data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit, appUuid);
					}
				}
			} else {
				List<String> orderList = Arrays.asList(order.split("\\s*,\\s*"));
				List<String> sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));

				if (StringUtils.isNotBlank(sortBy) || StringUtils.isNotBlank(order)) {
					for (int i = 0; i < sortList.size(); i++)
						orderBy.append(sortList.get(i)).append(" ").append(orderList.get(i));
					///if (requestId != null) {
						String tabName = null;
						for (Map.Entry<String, List<Map<String, Object>>> entry : requestMap.entrySet()) {
							String id = entry.getKey();
							if (id.equals(requestId)) {
								requestIdExistFlag = true;
							}
						}
						if (requestIdExistFlag) {
							data = requestMap.get(requestId);
//							if (datasource.getType().toLowerCase().toLowerCase().contains("spark")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("file")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("hive")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("impala")) {
//								data = exec.executeAndFetch("Select * from " + tabName + " where rownum >= " + offset
//										+ " AND rownum <= " + limit, appUuid);
//							} else {
//								if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
//									if (runMode.equals(Mode.ONLINE))
//										data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
//												appUuid);
//									else
//										data = exec.executeAndFetch(
//												"Select * from " + tableName + " where  rownum<" + limit, appUuid);
//								else {
//									data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
//											appUuid);
//								}
//							}
						} else {
							if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
								data = exec.executeAndFetch(
										"SELECT * FROM (SELECT Row_Number() Over(ORDER BY "+ orderBy.toString()+") AS rownum, * FROM (SELECT * FROM "
												+ tableName +") as tab) as tab1",
												appUuid);
							} else {
								if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
									if (runMode.equals(Mode.ONLINE))
										data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
												appUuid);
									else
										data = exec.executeAndFetch(
												"Select * from " + tableName + " where  rownum<" + limit, appUuid);
								else {
									data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
											appUuid);
								}
							}

							tabName = requestId.replace("-", "_");
							requestMap.put(requestId, data);
//							if (datasource.getType().toLowerCase().toLowerCase().contains("spark")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("file")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("hive")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("impala")) {
//								data = exec.executeAndFetch("Select * from " + tabName + " where rownum >= " + offset
//										+ " AND rownum <= " + limit, appUuid);
//							} else {
//								if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
//									if (runMode.equals(Mode.ONLINE))
//										data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
//												appUuid);
//									else
//										data = exec.executeAndFetch(
//												"Select * from " + tableName + " where  rownum<" + limit, appUuid);
//								else {
//									data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
//											appUuid);
//								}
//							}
						}
					//}
				}else {
					if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
						data = exec.executeAndFetch("SELECT * FROM " + tableName + " WHERE rownum >= " + offset
								+ " AND rownum <= " + limit, appUuid);
					} else {
						if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
							if (runMode.equals(Mode.ONLINE))
								data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit,
										appUuid);
							else
								data = exec.executeAndFetch(
										"SELECT * FROM " + tableName + " WHERE  rownum<" + limit, appUuid);
						else {
							data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit,
									appUuid);
						}
					}
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.ruleExec, ruleExecUUID, ruleExecVersion));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.", dependsOn);
			throw new Exception((message != null) ? message : "Table not found.");
		}
		return data;
	}

	/**
	 * Get the rulename.attributename for use of rule as a dependency of map
	 * 
	 * @param daoRegister
	 * @param rule
	 * @param attributeId
	 * @return
	 */
	public String getAttributeSql(BusinessRule rule2, String attributeId) {
	/*	List<AttributeSource> sourceAttrs = rule2.getAttributeInfo();
		for (AttributeSource sourceAttr : sourceAttrs) {
			if (sourceAttr.getSourceAttr() != null && sourceAttr.getAttrSourceId() != null
					&& sourceAttr.getAttrSourceId().equals(attributeId)) {
				// logger.info("getAttributeSql(): "+rule.getName() + "." +
				// sourceAttr.getAttrSourceName());
				return rule2.getName() + "." + sourceAttr.getAttrSourceName();
			}
		}*/
		return null;
	}
	
	/**
	 * This is an override of BaseRuleService.parse for rule
	 */
	@Override
	public RuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode)
			throws Exception {
		logger.info("Inside ruleServiceImpl.parse");
		BusinessRule rule2 = null;
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		// List<Status> statusList = null;
		RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
				MetaType.ruleExec.toString(), "N");
		/***************  Initializing paramValMap - START ****************/
		Map<String, String> paramValMap = null;
		ExecParams execParams = ruleExec.getExecParams();
		if (execParams.getParamValMap() == null) {
			execParams.setParamValMap(new HashMap<String, Map<String, String>>());
		}
		if (!execParams.getParamValMap().containsKey(ruleExec.getUuid())) {
			execParams.getParamValMap().put(ruleExec.getUuid(), new HashMap<String, String>());
		}
		paramValMap = execParams.getParamValMap().get(ruleExec.getUuid());
		/***************  Initializing paramValMap - END ****************/
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.STARTING);
		}
		// rule = iRuleDao.findLatestByUuid(ruleExec.getDependsOn().getRef().getUuid(),
		// new Sort(Sort.Direction.DESC, "version"));
		rule2 = (BusinessRule) commonServiceImpl.getLatestByUuid(ruleExec.getDependsOn().getRef().getUuid(),
				MetaType.rule2.toString(), "N");
		
		List<String> listSql=new ArrayList<String>();
		List<String> listSqlWithFilter=new ArrayList<String>();

		String withSql = null;
		String detailSelectSql=null;
		Boolean filterFlag=false;
		// ruleExec.setExec(rule2Operator.generateSql(rule2, refKeyMap, otherParams,
		// usedRefKeySet, ruleExec.getExecParams(), runMode));
		listSql = rule2Operator.generateDetailSql(rule2,withSql,detailSelectSql,refKeyMap, otherParams, usedRefKeySet,
				ruleExec.getExecParams(), runMode, ruleExec, rule2.getFilterInfo(), filterFlag, paramValMap);
        String detailsql=listSql.get(0);
		
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(rule2Info.getRule_result_details(), null,
				MetaType.datapod.toString(), "N");
		// String filePath = "/" + datapod.getUuid()+ "/" + datapod.getVersion() + "/"+
		// ruleExec.getVersion();
		String filePath = Helper.getFileName(datapod.getUuid(), datapod.getVersion(), ruleExec.getVersion());
		String tableName = Helper.genTableName(filePath);

		/*//***************************************************  persist datapod 

		Application application = commonServiceImpl.getApp();
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(rule2Info.getRule_result_details(), null,
				MetaType.datapod.toString(), "N");
		// String filePath = "/" + datapod.getUuid()+ "/" + datapod.getVersion() + "/"+
		// ruleExec.getVersion();
		String filePath = Helper.getFileName(datapod.getUuid(), datapod.getVersion(), ruleExec.getVersion());
		String tableName = Helper.genTableName(filePath);

		ResultSetHolder rsHolder;

		long countRows = 0;

		ExecContext execContext = null;

		execContext = executorServiceImpl.getExecContext(runMode, datasource);

		Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
		Datasource ruleDatasource = commonServiceImpl.getDatasourceByObject(rule2);
		if (runMode != null && runMode.equals(RunMode.BATCH)) {
			MetaIdentifier targetDsMI = datapod.getDatasource().getRef();
			Datasource targetDatasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(targetDsMI.getUuid(),
					targetDsMI.getVersion(), targetDsMI.getType().toString());
			if (appDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())
					&& !targetDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
				rsHolder = exec.executeSqlByDatasource(listSql.get(0), ruleDatasource, application.getUuid());
				if (targetDatasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
					tableName = targetDatasource.getSid().concat(".").concat(datapod.getName());
				} else {
					tableName = targetDatasource.getDbname().concat(".").concat(datapod.getName());
				}
				rsHolder.setTableName(tableName);
				rsHolder = exec.persistDataframe(rsHolder, targetDatasource, datapod, SaveMode.APPEND.toString());
			} else if (targetDatasource.getType().equals(ExecContext.FILE.toString())) {
				exec.executeRegisterAndPersist(listSql.get(0), tableName, filePath, datapod, "overwrite", true,
						application.getUuid());
			} else {
				String sql = helper.buildInsertQuery(execContext.toString(), tableName, datapod, listSql.get(0));
				exec.executeSql(sql, application.getUuid());
			}
		} else {
			rsHolder = exec.executeAndRegisterByDatasource(listSql.get(0), tableName, ruleDatasource, application.getUuid());

			countRows = rsHolder.getCountRows();
		}

		logger.info("temp table registered: " + tableName);

		Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(rule2Info.getRule_result_details(),
				null, MetaType.datapod.toString(), "N");

		MetaIdentifier targetDatapodKey = new MetaIdentifier(MetaType.datapod, targetDatapod.getUuid(),
				targetDatapod.getVersion());

	//	persistDatastore(tableName, filePath, ruleExec, null, targetDatapodKey, countRows, runMode);

		//*******************************************************
		
		*/
//		String filterExpr;
		listSqlWithFilter = rule2Operator.generateDetailSql(rule2,withSql,detailSelectSql,refKeyMap, otherParams, usedRefKeySet,
				ruleExec.getExecParams(), runMode, ruleExec, rule2.getFilterInfo(), true, paramValMap);
        String detailsqlwithfilter=listSql.get(0);
		ScoringMethod scoringMethod=rule2.getScoringMethod();
		String summarysql = rule2Operator.generateSummarySql(rule2,listSqlWithFilter, scoringMethod, tableName, datapod, refKeyMap, otherParams,
				usedRefKeySet, new ExecParams(), runMode);

		ruleExec.setExec(detailsql);
		ruleExec.setSummaryExec(summarysql);
		if (rule2.getParamList() != null) {
			MetaIdentifier mi = rule2.getParamList().getRef();
			ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(mi.getUuid(), mi.getVersion(),
					mi.getType().toString(), "N");
			usedRefKeySet.add(new MetaIdentifier(MetaType.paramlist, paramList.getUuid(), paramList.getVersion()));
		}
		ruleExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		logger.info("sql_generated: " + ruleExec.getExec());
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.READY);
		}
		synchronized (ruleExec.getUuid()) {
			// RuleExec ruleExec1 = (RuleExec) daoRegister.getRefObject(new
			// MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(),
			// ruleExec.getVersion()));
			RuleExec ruleExec1 = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExec.getUuid(),
					ruleExec.getVersion(), MetaType.ruleExec.toString(), "N");
			ruleExec1.setExec(ruleExec.getExec());
			ruleExec1.setRefKeyList(ruleExec.getRefKeyList());
			// iRuleExecDao.save(ruleExec1);
			commonServiceImpl.save(MetaType.ruleExec.toString(), ruleExec1);
			ruleExec1 = null;
		}

		return ruleExec;
	}
	
	protected void persistDatastore(String tableName, String filePath,RuleExec ruleExec ,MetaIdentifierHolder resultRef,MetaIdentifier datapodKey, long countRows, RunMode runMode) throws Exception {
		/*DataStore ds = new DataStore();
		ds.setCreatedBy(baseRuleExec.getCreatedBy());*/
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, tableName, datapodKey, ruleExec.getRef(MetaType.reconExec), ruleExec.getAppInfo(), ruleExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, countRows, Helper.getPersistModeFromRunMode(runMode.toString()), null);
		/*dataStoreServiceImpl.persistDataStore(df, tableName, null, filePath,datapodKey, baseRuleExec.getRef(ruleExecType),
				null,baseRuleExec.getAppInfo(),SaveMode.Append.toString(), resultRef,ds);*/
	}

	@Override
	public BaseRuleExec execute(ThreadPoolTaskExecutor metaExecutor,
			BaseRuleExec baseRuleExec, MetaIdentifier datapodKey,
			List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
		return execute(metaExecutor, (RuleExec) baseRuleExec, taskList, execParams, 
				runMode);
	}
	
	public HttpServletResponse download(String ruleExecUUID, String ruleExecVersion, String format, String download,
			int offset, int limit, HttpServletResponse response, int rowLimit, String sortBy, String order,
			String requestId, RunMode runMode,String resultType, Layout layout) throws Exception {

		int maxRows = Integer.parseInt(commonServiceImpl.getConfigValue("framework.download.maxrows"));
		if (rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of " + maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					"Requested rows exceeded the limit of " + maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of " + maxRows);
		}
		List<Map<String, Object>> results = null;
		if(resultType == null || (resultType != null && resultType.equalsIgnoreCase("summary"))) {
			results = getResultSummary(ruleExecUUID, ruleExecVersion, offset, rowLimit, sortBy, order, requestId, runMode);
		} else {
			results = getResultDetail(ruleExecUUID, ruleExecVersion, offset, rowLimit, sortBy, order, requestId, runMode);
		}
		
		RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExecUUID, ruleExecVersion, MetaType.ruleExec.toString(), "N");
		
		response = downloadServiceImpl.download(format, response, runMode, results,
				new MetaIdentifierHolder(new MetaIdentifier(MetaType.ruleExec, ruleExecUUID, ruleExecVersion)), layout,
				null, false, "framework.file.download.path", null, ruleExec.getDependsOn(), null);
		return response;
	}
	
	@SuppressWarnings({ "unchecked"})
	public List<RuleExec> findrule2ExecByDatapod(String datapodUUID,String type) throws JsonProcessingException, ParseException {

		List<String> ruleUUIDlist = new ArrayList<String>();
		List<RuleExec> result = new ArrayList<RuleExec>();

		List<BusinessRule> rule2obj = (List<BusinessRule>) commonServiceImpl
				.getAllLatestCompleteObjects(MetaType.rule.toString(), null);
		for (BusinessRule rule2: rule2obj) {
			if (datapodUUID.equalsIgnoreCase( rule2.getSourceInfo().getRef().getUuid())) {
				ruleUUIDlist.add(rule2.getUuid().toString());
			}
		}

		List<RuleExec> ruleexecobj = (List<RuleExec>) commonServiceImpl
				.getAllLatestCompleteObjects(MetaType.ruleExec.toString(), null);

		for (RuleExec rulexec : ruleexecobj) {

			for (String profileuuid : ruleUUIDlist) {
				if (profileuuid.equalsIgnoreCase(rulexec.getDependsOn().getRef().getUuid())) {
					result.add(rulexec);
				}
			}
		}

		return result;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		ThreadPoolTaskExecutor metaExecutor = (execParams != null && execParams.getExecutionContext() != null && execParams.getExecutionContext().containsKey("EXECUTOR")) ? (ThreadPoolTaskExecutor)(execParams.getExecutionContext().get("EXECUTOR")) : null;
		@SuppressWarnings("unchecked")
		List<FutureTask<TaskHolder>> taskList = (execParams != null && execParams.getExecutionContext() != null && execParams.getExecutionContext().containsKey("TASKLIST")) ? (List<FutureTask<TaskHolder>>)(execParams.getExecutionContext().get("TASKLIST")) : null;
		execute(metaExecutor, (RuleExec)baseExec, taskList, execParams, runMode);
		return null;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(), DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(), null, null, runMode);
	}
	
	public List<MetaIdentifier> prepareRule2(String ruleUuid, String ruleVersion, ExecParams execParams, RuleExec ruleExec, RunMode runMode) {
		List<FutureTask<TaskHolder>> taskList = new ArrayList<FutureTask<TaskHolder>>();
		List<MetaIdentifier> ruleExecMetaList = new ArrayList<>();
		MetaIdentifierHolder ruleExecMeta = new MetaIdentifierHolder();
		MetaIdentifier ruleExecInfo = new MetaIdentifier(MetaType.rule2, ruleUuid, ruleVersion);
		ruleExecMeta.setRef(ruleExecInfo);
		try {
			if (execParams != null) {
				if(execParams.getParamInfo() != null && !execParams.getParamInfo().isEmpty()) {
					for (ParamSetHolder paramSetHolder : execParams.getParamInfo()) {
						if(ruleExec == null) {
							MetaIdentifier ref = paramSetHolder.getRef();
							ref.setType(MetaType.paramset);
							paramSetHolder.setRef(ref);
							execParams.setCurrParamSet(paramSetHolder);
						}
					}
						ruleExec = create(ruleUuid, ruleVersion, null, null, execParams, null, null, runMode);		

						ruleExec = parse(ruleExec.getUuid(), ruleExec.getVersion(), null, null, null, null, runMode);
						ruleExec = execute(metaExecutor, ruleExec, taskList, execParams, runMode);
						ruleExecInfo = new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion());
						ruleExecMetaList.add(ruleExecInfo);
						ruleExec = null;
					
				} else if(execParams.getParamListInfo() != null && !execParams.getParamListInfo().isEmpty()) {
					for (ParamListHolder paramListHolder : execParams.getParamListInfo()) {
						if(ruleExec == null) {
							execParams.setParamListHolder(paramListHolder);
						}
					}
						ruleExec = create(ruleUuid, ruleVersion, null, null, execParams, null, null, runMode);

						ruleExec = parse(ruleExec.getUuid(), ruleExec.getVersion(), null, null, null, null, runMode);
						ruleExec = execute(metaExecutor, ruleExec, taskList, execParams, runMode);
						ruleExecInfo = new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion());
						ruleExecMetaList.add(ruleExecInfo);
						ruleExec = null;
					
				} else {
					if(ruleExec == null)
						ruleExec = create(ruleUuid, ruleVersion, null, null, execParams, null, null, runMode);			
					ruleExec = parse(ruleExec.getUuid(), ruleExec.getVersion(), null, null, null, null, runMode);
					ruleExec = execute(metaExecutor, ruleExec, taskList, execParams, runMode);
					ruleExecInfo = new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion());
					ruleExecMetaList.add(ruleExecInfo);
					ruleExec = null;
				}				
			} else {
				if(ruleExec == null)
			    ruleExec = create(ruleUuid, ruleVersion, null, null, execParams, null, null, runMode);			
				ruleExec = parse(ruleExec.getUuid(), ruleExec.getVersion(), null, null, null, null, runMode);
				ruleExec = execute(metaExecutor, ruleExec, taskList, execParams, runMode);
				ruleExecInfo = new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion());
				ruleExecMetaList.add(ruleExecInfo);
				ruleExec = null;
			}
		} catch (Exception e) {
			try {
				commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.FAILED);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		commonServiceImpl.completeTaskThread(taskList);
		return ruleExecMetaList;
	}

	public List<Map<String, Object>> getDetailResults(String rule2ExecUUID, String rule2ExecVersion, RunMode runMode, Map<String, String> paramValMap) throws Exception {
		RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(rule2ExecUUID, rule2ExecVersion,
				MetaType.ruleExec.toString());
//		DataStore datastore = dataStoreServiceImpl.getDatastore(ruleExec.getResult().getRef().getUuid(),
//				ruleExec.getResult().getRef().getVersion());
		BusinessRule rule2 = (BusinessRule) commonServiceImpl.getOneByUuidAndVersion(ruleExec.getDependsOn().getRef().getUuid(),
				ruleExec.getDependsOn().getRef().getVersion(), MetaType.rule2.toString());
		dataStoreServiceImpl.setRunMode(runMode);
//		String tableName = null;
		List<Map<String, Object>> data = new ArrayList<>();
		try {
//			tableName = dataStoreServiceImpl.getTableNameByDatastoreKey(datastore.getUuid(), datastore.getVersion(),
//					runMode);
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
		List<String> listSql=rule2Operator.generateDetailSql(rule2, null, null, null,
				null, null, new ExecParams(), runMode,ruleExec, null, false, paramValMap);
		data = exec.executeAndFetch(listSql.get(0), appUuid);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException | ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	
	
	
	public List<Map<String, Object>> getSummaryResults(String ruleExecUUID, String ruleExecVersion, int offset,
			int limit, String sortBy, String order, String requestId, RunMode runMode) throws Exception {
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			limit = offset + limit;
			offset = offset + 1;
			RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExecUUID, ruleExecVersion,
					MetaType.ruleExec.toString());
			dataStoreServiceImpl.setRunMode(runMode);
			DataStore datastore = dataStoreServiceImpl.getDatastore(ruleExec.getSummaryResult().getRef().getUuid(),
					ruleExec.getSummaryResult().getRef().getVersion());

			data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order, null,runMode);
			
			/*boolean requestIdExistFlag = false;
			StringBuilder orderBy = new StringBuilder();
			dataStoreServiceImpl.setRunMode(runMode);
			String tableName = dataStoreServiceImpl.getTableNameByDatastoreKey(datastore.getUuid(),
					datastore.getVersion(), runMode);
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			ExecContext execContext = null;
			IExecutor exec = null;
			//String sql = null;
			String appUuid = null;
			if (runMode.equals(Mode.ONLINE)) {
				execContext = helper.getExecutorContext(engine.getExecEngine());
				appUuid = commonServiceImpl.getApp().getUuid();
			} else {
				execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
			}
			exec = execFactory.getExecutor(execContext.toString());
			if (requestId == null|| requestId.equals("null") || requestId.isEmpty()) {
				if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
					data = exec.executeAndFetch("Select * from (Select Row_Number() Over(ORDER BY 1) as rownum, * from "
							+ tableName + ")as tab where rownum >= " + offset + " AND rownum <= " + limit, appUuid);
				} else {
					if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
						if (runMode.equals(Mode.ONLINE))
							data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit, appUuid);
						else
							data = exec.executeAndFetch("Select * from " + tableName + " where rownum< " + limit,
									appUuid);
					else {
						data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit, appUuid);
					}
				}
			} else {
				List<String> orderList = Arrays.asList(order.split("\\s*,\\s*"));
				List<String> sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));

				if (StringUtils.isNotBlank(sortBy) || StringUtils.isNotBlank(order)) {
					for (int i = 0; i < sortList.size(); i++)
						orderBy.append(sortList.get(i)).append(" ").append(orderList.get(i));
					///if (requestId != null) {
						String tabName = null;
						for (Map.Entry<String, List<Map<String, Object>>> entry : requestMap.entrySet()) {
							String id = entry.getKey();
							if (id.equals(requestId)) {
								requestIdExistFlag = true;
							}
						}
						if (requestIdExistFlag) {
							data = requestMap.get(requestId);
//							if (datasource.getType().toLowerCase().toLowerCase().contains("spark")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("file")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("hive")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("impala")) {
//								data = exec.executeAndFetch("Select * from " + tabName + " where rownum >= " + offset
//										+ " AND rownum <= " + limit, appUuid);
//							} else {
//								if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
//									if (runMode.equals(Mode.ONLINE))
//										data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
//												appUuid);
//									else
//										data = exec.executeAndFetch(
//												"Select * from " + tableName + " where  rownum<" + limit, appUuid);
//								else {
//									data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
//											appUuid);
//								}
//							}
						} else {
							if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
								data = exec.executeAndFetch(
										"SELECT * FROM (SELECT Row_Number() Over(ORDER BY "+ orderBy.toString()+") AS rownum, * FROM (SELECT * FROM "
												+ tableName +") as tab) as tab1",
												appUuid);
							} else {
								if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
									if (runMode.equals(Mode.ONLINE))
										data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
												appUuid);
									else
										data = exec.executeAndFetch(
												"Select * from " + tableName + " where  rownum<" + limit, appUuid);
								else {
									data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
											appUuid);
								}
							}

							tabName = requestId.replace("-", "_");
							requestMap.put(requestId, data);
//							if (datasource.getType().toLowerCase().toLowerCase().contains("spark")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("file")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("hive")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("impala")) {
//								data = exec.executeAndFetch("Select * from " + tabName + " where rownum >= " + offset
//										+ " AND rownum <= " + limit, appUuid);
//							} else {
//								if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
//									if (runMode.equals(Mode.ONLINE))
//										data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
//												appUuid);
//									else
//										data = exec.executeAndFetch(
//												"Select * from " + tableName + " where  rownum<" + limit, appUuid);
//								else {
//									data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
//											appUuid);
//								}
//							}
						}
					//}
				}else {
					if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
						data = exec.executeAndFetch("SELECT * FROM " + tableName + " WHERE rownum >= " + offset
								+ " AND rownum <= " + limit, appUuid);
					} else {
						if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
							if (runMode.equals(Mode.ONLINE))
								data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit,
										appUuid);
							else
								data = exec.executeAndFetch(
										"SELECT * FROM " + tableName + " WHERE  rownum<" + limit, appUuid);
						else {
							data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit,
									appUuid);
						}
					}
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.ruleExec, ruleExecUUID, ruleExecVersion));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.", dependsOn);
			throw new Exception((message != null) ? message : "Table not found.");
		}
		return data;
	}


	

	/**
	 * @param execUuid
	 * @param execVersion
	 * @param offset
	 * @param limit
	 * @param sortBy
	 * @param order
	 * @param requestId
	 * @param runMode
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws JSONException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public List<Map<String, Object>> getResultDetail(String execUuid, String execVersion, int offset, int limit,
			String sortBy, String order, String requestId, RunMode runMode) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException, IOException {
		RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.ruleExec.toString(), "N");
		try {
			Datapod detailsDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
					commonServiceImpl.getConfigValue("framework.rule2.detail.uuid"), null, MetaType.datapod.toString(), "N");

			limit = offset + limit;
			offset = offset + 1;
			dataStoreServiceImpl.setRunMode(runMode);
			DataStore datastore = dataStoreServiceImpl.findDataStoreByMeta(detailsDp.getUuid(), detailsDp.getVersion());

			return dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId,
					offset, limit, sortBy, order, ruleExec.getVersion(),runMode);
//			Datasource detailsDpDs = commonServiceImpl.getDatasourceByDatapod(detailsDp);
//
//			String tableName = getTableName(detailsDpDs, detailsDp, ruleExec, runMode);
//
//			String sql = "SELECT * FROM " + tableName;
//
//			Datasource appDS = commonServiceImpl.getDatasourceByApp();
//			IExecutor exec = execFactory.getExecutor(appDS.getType().toLowerCase());
//
//			String appUuid = commonServiceImpl.getApp().getUuid();
//			
//			if (runMode.equals(RunMode.ONLINE)) {
//				return sparkExecutor.executeAndFetchFromTempTable(sql, appUuid);
//			} else {
//				if (detailsDpDs.getType().equalsIgnoreCase(ExecContext.FILE.toString())
//						|| detailsDpDs.getType().equalsIgnoreCase(ExecContext.spark.toString())) {
//					String dafaultPath = commonServiceImpl.getConfigValue("framework.schema.Path");
//					dafaultPath = dafaultPath.endsWith("/") ? dafaultPath : dafaultPath.concat("/");
//					String filePath = String.format("%s/%s/%s", detailsDp.getUuid(), detailsDp.getVersion(),
//							ruleExec.getVersion());
//					String filePathUrl = hdfsInfo.getHdfsURL().concat(dafaultPath).concat(filePath);
//					List<String> filePathUrlList = new ArrayList<>();
//					filePathUrlList.add(filePathUrl);
//					sparkExecutor.readAndRegisterFile(tableName, filePathUrlList, FileType.PARQUET.toString(), "true",
//							appUuid, true);
//				}
//				return exec.executeAndFetchByDatasource(sql, detailsDpDs, appUuid);
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
					(message != null) ? message : "Can not fetch result details.",
					new MetaIdentifierHolder(new MetaIdentifier(MetaType.ruleExec, execUuid, execVersion)));
			throw new RuntimeException((message != null) ? message : "Can not fetch result details");
		}
	}

	/**
	 * @param execUuid
	 * @param execVersion
	 * @param offset
	 * @param limit
	 * @param sortBy
	 * @param order
	 * @param requestId
	 * @param runMode
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws JSONException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public List<Map<String, Object>> getResultSummary(String execUuid, String execVersion, int offset, int limit,
			String sortBy, String order, String requestId, RunMode runMode) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException, IOException {
		RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.ruleExec.toString(), "N");
		try {
			List<Map<String, Object>> data = new ArrayList<>();

			Datapod summaryDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
					commonServiceImpl.getConfigValue("framework.rule2.summary.uuid"), null, MetaType.datapod.toString(), "N");

			limit = offset + limit;
			offset = offset + 1;
			dataStoreServiceImpl.setRunMode(runMode);
			DataStore datastore = dataStoreServiceImpl.findDataStoreByMeta(summaryDp.getUuid(), summaryDp.getVersion());
            data=dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId,
					offset, limit, sortBy, order,ruleExec.getVersion(),runMode);
			return data;
//			Datasource summaryDpDs = commonServiceImpl.getDatasourceByDatapod(summaryDp);
//
//			String tableName = getTableName(summaryDpDs, summaryDp, ruleExec, runMode);
//
//			String sql = "SELECT * FROM " + tableName;
//
//			Datasource appDS = commonServiceImpl.getDatasourceByApp();
//			IExecutor exec = execFactory.getExecutor(appDS.getType().toLowerCase());
//
//			String appUuid = commonServiceImpl.getApp().getUuid();
//			
//			if (runMode.equals(RunMode.ONLINE)) {
//				return sparkExecutor.executeAndFetchFromTempTable(sql, appUuid);
//			} else {
//				if (summaryDpDs.getType().equalsIgnoreCase(ExecContext.FILE.toString())
//						|| summaryDpDs.getType().equalsIgnoreCase(ExecContext.spark.toString())) {
//					String dafaultPath = commonServiceImpl.getConfigValue("framework.schema.Path");
//					dafaultPath = dafaultPath.endsWith("/") ? dafaultPath : dafaultPath.concat("/");
//					String filePath = String.format("%s/%s/%s", summaryDp.getUuid(), summaryDp.getVersion(),
//							ruleExec.getVersion());
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
					new MetaIdentifierHolder(new MetaIdentifier(MetaType.ruleExec, execUuid, execVersion)));
			throw new RuntimeException((message != null) ? message : "Can not fetch summary.");
		}
	}

	/**
	 * @throws IOException 
	 * 
	 */
	protected MetaIdentifier getTargetSummaryDp () throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
		return getSummaryOrDetail("framework.rule2.summary.uuid");
	}

	/**
	 * 
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NullPointerException
	 * @throws ParseException
	 * @throws IOException 
	 */
	protected MetaIdentifier getTargetResultDp () throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
		return getSummaryOrDetail("framework.rule2.detail.uuid");
	}


}
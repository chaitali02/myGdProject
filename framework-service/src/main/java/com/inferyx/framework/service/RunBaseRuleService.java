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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Row;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseRule;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.AbortConditionType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SaveMode;
import com.inferyx.framework.enums.SysVarType;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.register.DatapodRegister;

/**
 * @author joy
 *
 */
@Service
public class RunBaseRuleService implements Callable<TaskHolder> {

	protected BaseRuleExec baseRuleExec;
	protected HDFSInfo hdfsInfo;
	protected DataStoreServiceImpl dataStoreServiceImpl;
	protected BaseRule baseRule;
	protected ExecutorFactory execFactory;
	protected Authentication authentication;
	protected CommonServiceImpl<?> commonServiceImpl;
	protected MetaIdentifier datapodKey;
	protected MetaIdentifier summaryDatapodKey;
	protected String name;
	protected MetaType ruleExecType;
	protected SessionContext sessionContext;
	protected RunMode runMode;
	protected Datasource datasource;
	protected DatapodRegister datapodRegister;
	ConnectionFactory connFactory;
	protected Engine engine;
	protected Helper helper;
	protected ExecParams execParams;
	protected ExecutorServiceImpl executorServiceImpl;
	protected DatapodServiceImpl datapodService;

	static final Logger logger = Logger.getLogger(RunBaseRuleService.class);

	/**
	 * @return the datasource
	 */
	public Datasource getDatasource() {
		return datasource;
	}

	/**
	 * @param datasource the datasource to set
	 */
	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}

	/**
	 * @return the executorServiceImpl
	 */
	public ExecutorServiceImpl getExecutorServiceImpl() {
		return executorServiceImpl;
	}

	/**
	 * @param executorServiceImpl the executorServiceImpl to set
	 */
	public void setExecutorServiceImpl(ExecutorServiceImpl executorServiceImpl) {
		this.executorServiceImpl = executorServiceImpl;
	}

	/**
	 * @return the connFactory
	 */
	public ConnectionFactory getConnFactory() {
		return connFactory;
	}

	/**
	 * @param connFactory the connFactory to set
	 */
	public void setConnFactory(ConnectionFactory connFactory) {
		this.connFactory = connFactory;
	}

	/**
	 * 
	 */
	public RunBaseRuleService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the datapodRegister
	 */
	public DatapodRegister getDatapodRegister() {
		return datapodRegister;
	}

	/**
	 * @param datapodRegister the datapodRegister to set
	 */
	public void setDatapodRegister(DatapodRegister datapodRegister) {
		this.datapodRegister = datapodRegister;
	}

	/**
	 * @return the baseRuleExec
	 */
	public BaseRuleExec getBaseRuleExec() {
		return baseRuleExec;
	}

	/**
	 * @param baseRuleExec the baseRuleExec to set
	 */
	public void setBaseRuleExec(BaseRuleExec baseRuleExec) {
		this.baseRuleExec = baseRuleExec;
	}

	/**
	 * @return the hdfsInfo
	 */
	public HDFSInfo getHdfsInfo() {
		return hdfsInfo;
	}

	/**
	 * @param hdfsInfo the hdfsInfo to set
	 */
	public void setHdfsInfo(HDFSInfo hdfsInfo) {
		this.hdfsInfo = hdfsInfo;
	}

	/**
	 * @return the dataStoreServiceImpl
	 */
	public DataStoreServiceImpl getDataStoreServiceImpl() {
		return dataStoreServiceImpl;
	}

	/**
	 * @param dataStoreServiceImpl the dataStoreServiceImpl to set
	 */
	public void setDataStoreServiceImpl(DataStoreServiceImpl dataStoreServiceImpl) {
		this.dataStoreServiceImpl = dataStoreServiceImpl;
	}

	/**
	 * @return the baseRule
	 */
	public BaseRule getBaseRule() {
		return baseRule;
	}

	/**
	 * @param baseRule the baseRule to set
	 */
	public void setBaseRule(BaseRule baseRule) {
		this.baseRule = baseRule;
	}

	/**
	 * @return the execFactory
	 */
	public ExecutorFactory getExecFactory() {
		return execFactory;
	}

	/**
	 * @param execFactory the execFactory to set
	 */
	public void setExecFactory(ExecutorFactory execFactory) {
		this.execFactory = execFactory;
	}

	/**
	 * @return the authentication
	 */
	public Authentication getAuthentication() {
		return authentication;
	}

	/**
	 * @param authentication the authentication to set
	 */
	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

	/**
	 * @return the commonServiceImpl
	 */
	public CommonServiceImpl<?> getCommonServiceImpl() {
		return commonServiceImpl;
	}

	/**
	 * @param commonServiceImpl the commonServiceImpl to set
	 */
	public void setCommonServiceImpl(CommonServiceImpl<?> commonServiceImpl) {
		this.commonServiceImpl = commonServiceImpl;
	}

	/**
	 * @return the datapodKey
	 */
	public MetaIdentifier getDatapodKey() {
		return datapodKey;
	}

	/**
	 * @param datapodKey the datapodKey to set
	 */
	public void setDatapodKey(MetaIdentifier datapodKey) {
		this.datapodKey = datapodKey;
	}

	/**
	 * @return the summaryDatapodKey
	 */
	public MetaIdentifier getSummaryDatapodKey() {
		return summaryDatapodKey;
	}

	/**
	 * @param summaryDatapodKey the summaryDatapodKey to set
	 */
	public void setSummaryDatapodKey(MetaIdentifier summaryDatapodKey) {
		this.summaryDatapodKey = summaryDatapodKey;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ruleExecType
	 */
	public MetaType getRuleExecType() {
		return ruleExecType;
	}

	/**
	 * @param ruleExecType the ruleExecType to set
	 */
	public void setRuleExecType(MetaType ruleExecType) {
		this.ruleExecType = ruleExecType;
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

	/**
	 * @return the execParams
	 */
	public ExecParams getExecParams() {
		return execParams;
	}

	/**
	 * @param execParams the execParams to set
	 */
	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}

	/**
	 * @return the datapodService
	 */
	public DatapodServiceImpl getDatapodService() {
		return datapodService;
	}

	/**
	 * @param datapodService the datapodService to set
	 */
	public void setDatapodService(DatapodServiceImpl datapodService) {
		this.datapodService = datapodService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public TaskHolder call() throws Exception {
		return execute();
	}

	/**
	 * Create file name
	 * 
	 * @param baseGroupExec
	 * @param baseRule
	 * @param baseRuleExec
	 * @param datapodKey
	 * @return
	 */
	protected String getFileName(BaseRule baseRule, BaseRuleExec baseRuleExec, MetaIdentifier datapodKey) {
		return String.format("/%s/%s/%s", baseRule.getUuid(), baseRule.getVersion(), baseRuleExec.getVersion());
	}

	/**
	 * Create table name
	 * 
	 * @param baseGroupExec
	 * @param baseRule
	 * @param baseRuleExec
	 * @param datapodKey
	 * @return
	 * @throws Exception 
	 */
	protected String genTableNameByRule(BaseRule baseRule, BaseRuleExec baseRuleExec, MetaIdentifier datapodKey,
			ExecContext execContext, RunMode runMode) throws Exception {
		if (datapodKey.getType().equals(MetaType.rule)) {
			return String.format("%s_%s_%s", baseRule.getUuid().replace("-", "_"), baseRule.getVersion(),
					baseRuleExec.getVersion());

		} 
		else if (execContext.equals(ExecContext.FILE)) {
			return String.format("%s_%s_%s", baseRule.getUuid().replace("-", "_"), baseRule.getVersion(),
					baseRuleExec.getVersion());
		}

		try {
			Datapod datapod = (Datapod) commonServiceImpl.getLatestByUuid(datapodKey.getUuid(), MetaType.datapod.toString(), "N");
			return datapodService.getTableNameByDatapod(datapod, runMode);
			/*Datasource datasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(
					datapod.getDatasource().getRef().getUuid(), datapod.getDatasource().getRef().getVersion(),
					MetaType.datasource.toString(), "N");
			if (datasource.getType().equals(ExecContext.FILE.toString())) {
				return String.format("%s_%s_%s", baseRule.getUuid().replace("-", "_"), baseRule.getVersion(),
						baseRuleExec.getVersion());
			} else {
				return datasource.getDbname() + "." + datapod.getName();
			}*/
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Persist Datastore
	 * 
	 * @param df
	 * @param tableName
	 * @param filePath
	 * @param resultRef
	 * @throws Exception
	 */
	protected void persistDatastore(String tableName, String filePath, MetaIdentifierHolder resultRef,
			MetaIdentifier datapodKey, long countRows, RunMode runMode) throws Exception {
		/*
		 * DataStore ds = new DataStore(); ds.setCreatedBy(baseRuleExec.getCreatedBy());
		 */
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, tableName, datapodKey, baseRuleExec.getRef(ruleExecType),
				baseRuleExec.getAppInfo(), baseRuleExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef,
				countRows, Helper.getPersistModeFromRunMode(runMode.toString()), null);
		/*
		 * dataStoreServiceImpl.persistDataStore(df, tableName, null,
		 * filePath,datapodKey, baseRuleExec.getRef(ruleExecType),
		 * null,baseRuleExec.getAppInfo(),SaveMode.Append.toString(), resultRef,ds);
		 */
	}

	protected String getSaveMode() {
		return SaveMode.APPEND.toString();
	}

	/**
	 * 
	 * @param execParams
	 * @param baseRuleExec
	 */
	protected void checkInternalVarMap(ExecParams execParams, BaseRuleExec baseRuleExec) {
		if (execParams == null) {
			return;
		}
		if (execParams.getInternalVarMap() == null) {
			execParams.setInternalVarMap(new HashMap<>());
		}

		if (!execParams.getInternalVarMap().containsKey("\\$".concat(SysVarType.exec_version.toString()))) {
			execParams.getInternalVarMap().put("\\$".concat(SysVarType.exec_version.toString()),
					baseRuleExec.getVersion());
		}
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public TaskHolder execute() throws Exception {
		// Set status to RUNNING
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		MetaIdentifierHolder summaryResultRef = new MetaIdentifierHolder();
		long countRows = -1L;
		FrameworkThreadLocal.getSessionContext().set(sessionContext);
		List<Status> ruleExecStatusList = baseRuleExec.getStatusList();
		if (Helper.getLatestStatus(ruleExecStatusList) != null && (Helper.getLatestStatus(ruleExecStatusList)
				.equals(new Status(Status.Stage.RUNNING, new Date()))
				|| Helper.getLatestStatus(ruleExecStatusList).equals(new Status(Status.Stage.COMPLETED, new Date()))
				|| Helper.getLatestStatus(ruleExecStatusList).equals(new Status(Status.Stage.PAUSE, new Date())))) {
			logger.info(
					" This process is RUNNING or has been COMPLETED previously or is PAUSE. Hence it cannot be rerun. ");
			return null;
		}
		// Set RUNNING status and save

		try {
			synchronized (baseRuleExec.getUuid()) {
				baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, ruleExecType,
						Status.Stage.RUNNING);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			// Form file and table name
			String filePath = getFileName(baseRule, baseRuleExec, datapodKey);
			String tableName = null;

			if (StringUtils.isBlank(baseRuleExec.getExec())) {
				throw new Exception("sql not generated");
			}
			IExecutor exec = null;
			ExecContext execContext = null;
			String appUuid = null;

//			Datapod targetDP = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodKey.getUuid(), datapodKey.getVersion(), MetaType.datapod.toString());
//			Datasource targetDS = commonServiceImpl.getDatasourceByDatapod(targetDP);
//			execContext = executorServiceImpl.getExecContext(runMode, targetDS);
//			exec = execFactory.getExecutor(execContext.toString());
			
//			execContext = executorServiceImpl.getExecContext(runMode, datasource);
			
			execContext = helper.getExecutorContext(datasource.getType().toLowerCase()); // This comes from app datasource.
			exec = execFactory.getExecutor(execContext.toString());

			tableName = genTableNameByRule(baseRule, baseRuleExec, datapodKey, execContext, runMode);
			logger.info("Table name in RunBaseruleServiceImpl : " + tableName);
			logger.info("execContext : " + execContext);

			ResultSetHolder rsHolder = null;
			appUuid = commonServiceImpl.getApp().getUuid();

			/***** Replace internalVarMap - START *****/
			checkInternalVarMap(execParams, baseRuleExec);
			baseRuleExec.setExec(DagExecUtil.replaceInternalVarMap(execParams, baseRuleExec.getExec()));
			baseRuleExec.setSummaryExec(DagExecUtil.replaceInternalVarMap(execParams, baseRuleExec.getSummaryExec()));
			/***** Replace internalVarMap - END *****/

//			Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
			Datasource ruleDatasource = commonServiceImpl.getDatasourceByObject(baseRule);
			// Actual execution happens here - START
			logger.info("Before execution result : " + baseRuleExec.getExec());
			rsHolder = execute(baseRuleExec.getExec(), ruleDatasource, tableName, filePath, appUuid,
					exec, execContext);

			if (rsHolder != null) {
				countRows = rsHolder.getCountRows();
			}

			// ******Adding one more parameter in persistDataStorenew
			persistDatastore(tableName, filePath, resultRef, datapodKey, countRows, runMode);
			baseRuleExec.setResult(resultRef);

			logger.info("Temp table registered: " + tableName);

//			Starting Summary business
			if (baseRuleExec.getDependsOn().getRef().getType().equals(MetaType.dq)
					|| baseRuleExec.getDependsOn().getRef().getType().equals(MetaType.rule2)) {
//				Datapod summaryDatapod = null;
				MetaIdentifier summaryDatapodKey = this.summaryDatapodKey;
				
				datapodKey = summaryDatapodKey;
				filePath = getFileName(baseRule, baseRuleExec, summaryDatapodKey);
				tableName = genTableNameByRule(baseRule, baseRuleExec, summaryDatapodKey, execContext, runMode);
				
				logger.info("Table name registered : " + tableName);
				logger.info("Before execution summary : " + baseRuleExec.getSummaryExec());
				rsHolder = execute(baseRuleExec.getSummaryExec(), ruleDatasource, tableName, filePath,
						appUuid, exec, execContext);

				if (rsHolder != null) {
					countRows = rsHolder.getCountRows();
				}

				// ******Adding one more parameter in persistDataStorenew
				persistDatastore(tableName, filePath, summaryResultRef, summaryDatapodKey, countRows, runMode);
				baseRuleExec.setSummaryResult(summaryResultRef);
				
				if (baseRuleExec.getDependsOn().getRef().getType()==MetaType.dq && isAbort(baseRule.getUuid(), baseRule.getVersion(), baseRuleExec, runMode)) {
					synchronized (baseRuleExec.getUuid()) {
						baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, ruleExecType,
								Status.Stage.ABORTED);
					}
					logger.error("Rule to be Aborted ");
//					throw new Exception("Rule execution Aborted.");
				}
				
				if (rsHolder != null) {
					countRows = rsHolder.getCountRows();
				}
			}
//			Ending Summary business

//			//******setting createdBy in datastore***********
//			MetaIdentifierHolder user = new MetaIdentifierHolder();
//			if (authentication != null) {
//				MetaIdentifier u = new MetaIdentifier();
//				u.setUuid(authentication.getName());
//				u.setType(MetaType.user);
//				user.setRef(u);
//			}

			// Set status to COMPLETED
			synchronized (baseRuleExec.getUuid()) {
				baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, ruleExecType,
						Status.Stage.COMPLETED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Set status to FAILED
			try {
				synchronized (baseRuleExec.getUuid()) {
					baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, ruleExecType,
							Status.Stage.FAILED);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(ruleExecType, baseRuleExec.getUuid(), baseRuleExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Rule execution failed.", dependsOn);
			throw new java.lang.Exception((message != null) ? message : "Rule execution failed.");
		}
		TaskHolder taskHolder = new TaskHolder(name,
				new MetaIdentifier(ruleExecType, baseRuleExec.getUuid(), baseRuleExec.getVersion()));
		return taskHolder;

	}

	private ResultSetHolder execute(String execSql, Datasource ruleDatasource,
			String tableName, String filePath, String appUuid, IExecutor exec, ExecContext execContext)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Datapod targetDp = null;
		ResultSetHolder rsHolder = null;
		
//		 Special handling for Rule until it is retired.
		if (baseRuleExec.getDependsOn().getRef().getType().equals(MetaType.rule)) {
			rsHolder = exec.executeAndRegisterByDatasource(execSql, Helper.genTableName(filePath), ruleDatasource,
					appUuid);
			return rsHolder;
		}
		
		targetDp = (Datapod) commonServiceImpl.getLatestByUuid(datapodKey.getUuid(), MetaType.datapod.toString(),
					"N");
		MetaIdentifier targetDsMI = targetDp.getDatasource().getRef();
		logger.info("Target datasource : " + targetDsMI);
		logger.info("Target tableName : " + tableName);
		Datasource targetDatasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(targetDsMI.getUuid(),
				targetDsMI.getVersion(), targetDsMI.getType().toString(), "N");
//		MetaType ruleType = Helper.getMetaTypeByExecType(ruleExecType);
//		String executionEngine = commonServiceImpl.getConfigValue("framework." + ruleType.toString() + ".execution.engine");
//		logger.info("framework execution engine : " + executionEngine);
		logger.info("Execution engine : " + execContext);
		if (execContext != null && execContext.equals(ExecContext.FILE)) {
			if (targetDatasource.getType().equals(ExecContext.FILE.toString())) {
				rsHolder = exec.executeRegisterAndPersist(execSql, tableName, filePath, targetDp,
						SaveMode.APPEND.toString(), true, appUuid);
			}
			else {
				rsHolder = exec.executeSqlByDatasource(execSql, ruleDatasource, appUuid);
				if(targetDatasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
					tableName = targetDatasource.getSid().concat(".").concat(targetDp.getName());
				} else {
					tableName = targetDatasource.getDbname().concat(".").concat(targetDp.getName());					
				}
				rsHolder.setTableName(tableName);			
				rsHolder = exec.persistDataframe(rsHolder, targetDatasource, targetDp, SaveMode.APPEND.toString());				
				}
		} else {
			String sql = helper.buildInsertQuery(execContext.toString(), tableName, targetDp, execSql);
			rsHolder = exec.executeSql(sql, appUuid);
		}
		/*if (runMode != null && runMode.equals(RunMode.BATCH)) {
			targetDp = (Datapod) commonServiceImpl.getLatestByUuid(datapodKey.getUuid(), MetaType.datapod.toString(),
					"N");
			MetaIdentifier targetDsMI = targetDp.getDatasource().getRef();
			Datasource targetDatasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(targetDsMI.getUuid(),
					targetDsMI.getVersion(), targetDsMI.getType().toString(), "N");
			if (appDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())
					&& !targetDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
				rsHolder = exec.executeSqlByDatasource(execSql, ruleDatasource, appUuid);
//				if(targetDatasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
//					tableName = targetDatasource.getSid().concat(".").concat(targetDp.getName());
//				} else {
//					tableName = targetDatasource.getDbname().concat(".").concat(targetDp.getName());					
//				}
				rsHolder.setTableName(tableName);
				rsHolder = exec.persistDataframe(rsHolder, targetDatasource, targetDp, SaveMode.APPEND.toString());
			} else if (targetDatasource.getType().equals(ExecContext.FILE.toString())) {
				rsHolder = exec.executeRegisterAndPersist(execSql, tableName, filePath, targetDp,
						SaveMode.APPEND.toString(), true, appUuid);
			} else {
				String sql = helper.buildInsertQuery(execContext.toString(), tableName, targetDp, execSql);
				rsHolder = exec.executeSql(sql, appUuid);
			}
		} else {
			rsHolder = exec.executeAndRegisterByDatasource(execSql, Helper.genTableName(filePath), ruleDatasource,
					appUuid);
		}*/
		return rsHolder;
	}
	
	public Boolean isAbort(String uuid, String version, BaseRuleExec baseruleExec, RunMode runMode)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, IOException, ParseException {

		logger.info("Inside isAbort ");
		BaseRule baseRule = (BaseRule) commonServiceImpl.getOneByUuidAndVersion(uuid, version,
				MetaType.dq.toString(), "N");
		Datasource ruleDatasource = commonServiceImpl.getDatasourceByObject(baseRule);

		ResultSetHolder rsHolder = execFactory.getExecutor(ExecContext.spark.toString()).executeAndRegisterByDatasource(
				baseruleExec.getAbortExec(), baseruleExec.getUuid().replaceAll("-", "_") + "_" + baseruleExec.getVersion(),
				ruleDatasource, commonServiceImpl.getApp().getUuid());
		List<Row> resultList = (rsHolder.getDataFrame() == null) ? null : rsHolder.getDataFrame().collectAsList();
		String abortThreshold = (resultList == null || resultList.isEmpty()) ? null : resultList.get(0).getString(0);

		int abortThresholdOrdinal = -1;

		if (baseRule.getAbortCondition() != null && StringUtils.isNotBlank(abortThreshold)) {
			if (AbortConditionType.LOW.toString().equals(abortThreshold)) {
				abortThresholdOrdinal = AbortConditionType.LOW.ordinal();
			} else if (AbortConditionType.MEDIUM.toString().equals(abortThreshold)) {
				abortThresholdOrdinal = AbortConditionType.MEDIUM.ordinal();
			} else {
				abortThresholdOrdinal = AbortConditionType.HIGH.ordinal();
			}
			if (abortThresholdOrdinal >= baseRule.getAbortCondition().ordinal()) {
				logger.info("Rule to be aborted ");
				return Boolean.TRUE;
			}
		}
		logger.info("Rule not to be aborted ");
		return Boolean.FALSE;
	}

	
}

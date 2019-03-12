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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DQInfo;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.Rule2Info;
import com.inferyx.framework.domain.BaseRule;
import com.inferyx.framework.domain.BaseRuleExec;
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
	protected DQInfo dqInfo;
	protected Rule2Info rule2Info;

	
	public Rule2Info getRule2Info() {
		return rule2Info;
	}



	public void setRule2Info(Rule2Info rule2Info) {
		this.rule2Info = rule2Info;
	}



	public DQInfo getDqInfo() {
		return dqInfo;
	}



	public void setDqInfo(DQInfo dqInfo) {
		this.dqInfo = dqInfo;
	}

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



	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public TaskHolder call() throws Exception {
		return execute();
	}

	/**
	 * Create file name
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
	 * @param baseGroupExec
	 * @param baseRule
	 * @param baseRuleExec
	 * @param datapodKey
	 * @return
	 * @throws JsonProcessingException 
	 */
	protected String getTableName(BaseRule baseRule, BaseRuleExec baseRuleExec, MetaIdentifier datapodKey, ExecContext execContext, RunMode runMode) throws JsonProcessingException {
		if(datapodKey.getType().equals(MetaType.rule)) {
			return String.format("%s_%s_%s", baseRule.getUuid().replace("-", "_"), baseRule.getVersion(), baseRuleExec.getVersion());

		}
		
	    else if (execContext == null /*|| execContext.equals(ExecContext.spark)*/ || runMode.equals(RunMode.ONLINE) && execContext.equals(ExecContext.FILE) 
				/*|| execContext.equals(ExecContext.livy_spark)*/) {
			return String.format("%s_%s_%s", baseRule.getUuid().replace("-", "_"), baseRule.getVersion(), baseRuleExec.getVersion());
		}

		Datapod dp = null;
		try {
			dp = (Datapod) commonServiceImpl.getLatestByUuid(datapodKey.getUuid(), MetaType.datapod.toString(), "N");
			Datasource datasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(dp.getDatasource().getRef().getUuid(), dp.getDatasource().getRef().getVersion(), MetaType.datasource.toString(), "N");
			if (datasource.getType().equals(ExecContext.FILE.toString())) {
				return String.format("%s_%s_%s", baseRule.getUuid().replace("-", "_"), baseRule.getVersion(), baseRuleExec.getVersion());
			} else {
				return datasource.getDbname() + "." + dp.getName();
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Persist Datastore
	 * @param df
	 * @param tableName
	 * @param filePath
	 * @param resultRef
	 * @throws Exception
	 */
	protected void persistDatastore(String tableName, String filePath, MetaIdentifierHolder resultRef,MetaIdentifier datapodKey, long countRows, RunMode runMode) throws Exception {
		/*DataStore ds = new DataStore();
		ds.setCreatedBy(baseRuleExec.getCreatedBy());*/
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, tableName, datapodKey, baseRuleExec.getRef(ruleExecType), baseRuleExec.getAppInfo(), baseRuleExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, countRows, Helper.getPersistModeFromRunMode(runMode.toString()), null);
		/*dataStoreServiceImpl.persistDataStore(df, tableName, null, filePath,datapodKey, baseRuleExec.getRef(ruleExecType),
				null,baseRuleExec.getAppInfo(),SaveMode.Append.toString(), resultRef,ds);*/
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
		if ( execParams.getInternalVarMap() == null ) {
			execParams.setInternalVarMap(new HashMap<>());
		}
		
		if (!execParams.getInternalVarMap().containsKey("\\$".concat(SysVarType.exec_version.toString()))) {
			execParams.getInternalVarMap().put("\\$".concat(SysVarType.exec_version.toString()), baseRuleExec.getVersion());
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
		if (Helper.getLatestStatus(ruleExecStatusList) != null 
				&& (Helper.getLatestStatus(ruleExecStatusList).equals(new Status(Status.Stage.RUNNING, new Date())) 
						|| Helper.getLatestStatus(ruleExecStatusList).equals(new Status(Status.Stage.COMPLETED, new Date()))
						|| Helper.getLatestStatus(ruleExecStatusList).equals(new Status(Status.Stage.PAUSE, new Date())))) {
			logger.info(" This process is RUNNING or has been COMPLETED previously or is PAUSE. Hence it cannot be rerun. ");
			return null;
		}
		// Set RUNNING status and save

		try {
			synchronized (baseRuleExec.getUuid()) {
				baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, ruleExecType, Status.Stage.RUNNING);
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
			
			execContext = executorServiceImpl.getExecContext(runMode, datasource);
			exec = execFactory.getExecutor(execContext.toString());
			
			tableName = getTableName(baseRule, baseRuleExec, datapodKey, execContext, runMode);
			logger.info("Table name in RunBaseruleServiceImpl : " + tableName);
			logger.info("execContext : " + execContext);
			
			
			ResultSetHolder rsHolder = null;
			appUuid = commonServiceImpl.getApp().getUuid();
			
			/***** Replace internalVarMap - START *****/
			checkInternalVarMap(execParams, baseRuleExec);
			baseRuleExec.setExec(DagExecUtil.replaceInternalVarMap(execParams, baseRuleExec.getExec()));
			baseRuleExec.setSummaryExec(DagExecUtil.replaceInternalVarMap(execParams, baseRuleExec.getSummaryExec()));
			/***** Replace internalVarMap - END *****/
			
			Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
			Datasource ruleDatasource = commonServiceImpl.getDatasourceByObject(baseRule);
			// Actual execution happens here - START
			logger.info("Before execution result : " + baseRuleExec.getExec());
			rsHolder = execute(baseRuleExec.getExec(), appDatasource, ruleDatasource, tableName, filePath, appUuid, exec, execContext);

			if (rsHolder != null) {
				countRows = rsHolder.getCountRows();
			}

			//******Adding one more parameter in persistDataStorenew
			persistDatastore(tableName, filePath, resultRef, datapodKey, countRows, runMode);

			baseRuleExec.setResult(resultRef);

			logger.info("Temp table registered: "+tableName);
			
//			Starting Summary business
			if (baseRuleExec.getDependsOn().getRef().getType().equals(MetaType.dq) || baseRuleExec.getDependsOn().getRef().getType().equals(MetaType.rule2)) {
				Datapod summaryDatapod = null;
				MetaIdentifier summaryDatapodKey = null;
				if (baseRuleExec.getDependsOn().getRef().getType().equals(MetaType.dq)) {
					summaryDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dqInfo.getDq_result_summary(), null,
							MetaType.datapod.toString(), "N");
					summaryDatapodKey = new MetaIdentifier(MetaType.datapod, summaryDatapod.getUuid(),
							summaryDatapod.getVersion());
					filePath = getFileName(baseRule, baseRuleExec, summaryDatapodKey);
					tableName = getTableName(baseRule, baseRuleExec, summaryDatapodKey, execContext, runMode);
				} else if (baseRuleExec.getDependsOn().getRef().getType().equals(MetaType.rule2)) {
					summaryDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(rule2Info.getRule_result_summary(),
							null, MetaType.datapod.toString(), "N");
					summaryDatapodKey = new MetaIdentifier(MetaType.datapod, summaryDatapod.getUuid(),
							summaryDatapod.getVersion());
					filePath = getFileName(baseRule, baseRuleExec, summaryDatapodKey);
					tableName = getTableName(baseRule, baseRuleExec, summaryDatapodKey, execContext, runMode);
				}
				logger.info("Table name registered : " + tableName);
				logger.info("Before execution summary : " + baseRuleExec.getSummaryExec());
				rsHolder = execute(baseRuleExec.getSummaryExec(), appDatasource, ruleDatasource, tableName, filePath , appUuid, exec, execContext);

				if (rsHolder != null) {
					countRows = rsHolder.getCountRows();
				}
				
				//******Adding one more parameter in persistDataStorenew
				persistDatastore(tableName, filePath, summaryResultRef, summaryDatapodKey, countRows, runMode);
	
				baseRuleExec.setSummaryResult(summaryResultRef);
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
				baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, ruleExecType, Status.Stage.COMPLETED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Set status to FAILED
			try {
				synchronized (baseRuleExec.getUuid()) {
					baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, ruleExecType, Status.Stage.FAILED);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}			
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(ruleExecType, baseRuleExec.getUuid(), baseRuleExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Execution FAILED.", dependsOn);
			throw new java.lang.Exception((message != null) ? message : "Execution FAILED.");
		} 
		TaskHolder taskHolder = new TaskHolder(name, new MetaIdentifier(ruleExecType, baseRuleExec.getUuid(), baseRuleExec.getVersion())); 
		return taskHolder;

	}
	
	private ResultSetHolder execute(String execSql
									, Datasource appDatasource
									, Datasource ruleDatasource
									, String tableName
									, String filePath
									, String appUuid
									, IExecutor exec
									, ExecContext execContext) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Datapod targetDp = null;
		ResultSetHolder rsHolder = null;
		if (runMode!= null && runMode.equals(RunMode.BATCH)) {
			targetDp = (Datapod) commonServiceImpl.getLatestByUuid(datapodKey.getUuid(), MetaType.datapod.toString(), "N");
			MetaIdentifier targetDsMI = targetDp.getDatasource().getRef();
			Datasource targetDatasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(targetDsMI.getUuid(), targetDsMI.getVersion(), targetDsMI.getType().toString(), "N");
			if(appDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())
					&& !targetDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
				rsHolder = exec.executeSqlByDatasource(execSql, ruleDatasource, appUuid);
//				if(targetDatasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
//					tableName = targetDatasource.getSid().concat(".").concat(targetDp.getName());
//				} else {
//					tableName = targetDatasource.getDbname().concat(".").concat(targetDp.getName());					
//				}
				rsHolder.setTableName(tableName);
				rsHolder = exec.persistDataframe(rsHolder, targetDatasource, targetDp, SaveMode.APPEND.toString());
			} else if(targetDatasource.getType().equals(ExecContext.FILE.toString())) {
				rsHolder = exec.executeRegisterAndPersist(execSql, tableName, filePath, targetDp, SaveMode.APPEND.toString(), true, appUuid);
			} else {
				String sql = helper.buildInsertQuery(execContext.toString(), tableName, targetDp, execSql);
				rsHolder = exec.executeSql(sql, appUuid);
			}
		} else {
			rsHolder = exec.executeAndRegisterByDatasource(execSql, Helper.genTableName(filePath), ruleDatasource, appUuid);
		}
		return rsHolder;
	}
	
}

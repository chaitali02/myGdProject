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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SaveMode;
import org.python.antlr.ast.operatorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.dao.IAlgorithmDao;
import com.inferyx.framework.dao.IModelDao;
import com.inferyx.framework.dao.IModelExecDao;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Operator;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.OperatorType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.CustomOperatorFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.IExecutable;
import com.inferyx.framework.operator.IParsable;
import com.inferyx.framework.operator.TransposeOldOperator;
import com.inferyx.framework.register.GraphRegister;

@Service
public class CustomOperatorServiceImpl implements IParsable, IExecutable {

	@Autowired
	CustomOperatorFactory operatorFactory;
	@Autowired
	GraphRegister<?> registerGraph;
	/*
	 * @Autowired JavaSparkContext javaSparkContext;
	 */
	@Autowired
	IModelDao iModelDao;
	@Autowired
	IAlgorithmDao iAlgorithmDao;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	IModelExecDao iModelExecDao;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	ModelExecServiceImpl modelExecServiceImpl;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	ParamListServiceImpl paramListServiceImpl;
	@Autowired
	SparkContext sparkContext;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	DatasetServiceImpl datasetServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SessionHelper sessionHelper;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	DataSourceFactory dataSourceFactory;
	@Autowired
	MetadataUtil commonActivity;
	@Autowired
	private TransposeOldOperator transposeOldOperator;
	@Autowired
	Helper helper;

	static final Logger logger = Logger.getLogger(CustomOperatorServiceImpl.class);

	/**
	 * 
	 * @param operatorExec
	 * @param execParams
	 * @param runMode
	 * @return
	 * @throws Exception
	 */
	public OperatorExec create(OperatorExec operatorExec, ExecParams execParams,
								RunMode runMode) throws Exception {
		logger.info("Inside CustomOperatorServiceImpl.create ");
		List<Status> statusList = null;
		if (operatorExec == null) {
			logger.info(" Nothing to create exec upon. Aborting ... ");
			return null;
		}
		statusList = operatorExec.getStatusList();
		if (Helper.getLatestStatus(statusList) != null
				&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date()))
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date()))
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Terminating, new Date()))
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
			logger.info(
					" This process is In Progress or has been completed previously or is Terminating or is On Hold. Hence it cannot be rerun. ");
			return operatorExec;
		}
		logger.info(" Set not started status");
		synchronized (operatorExec.getUuid()) {
			operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec,
					Status.Stage.NotStarted);
		}
		Operator operator = (Operator) commonServiceImpl.getOneByUuidAndVersion(
				operatorExec.getDependsOn().getRef().getUuid(), operatorExec.getDependsOn().getRef().getVersion(),
				MetaType.operator.toString());
		com.inferyx.framework.operator.IOperator newOperator = operatorFactory
				.getOperator(helper.getOperatorType(operator.getOperatorType()));
		Map<String, String> otherParams = newOperator.customCreate(operatorExec, execParams, runMode);
		logger.info(" After Set not started status");
		return operatorExec;
	}

	/**
	 * @Ganesh
	 *
	 * @param operatorExecUuid
	 * @param operatorExecVersion
	 * @param rowLimit
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getOperatorResults(String operatorExecUuid, String operatorExecVersion,
			int rowLimit) throws Exception {
		List<Map<String, Object>> data = null;
		try {
			OperatorExec operatorExec = (OperatorExec) commonServiceImpl.getOneByUuidAndVersion(operatorExecUuid,
					operatorExecVersion, MetaType.operatorExec.toString());
			logger.info(" Inside getOperatorResults : " + operatorExecUuid+":"+operatorExecVersion+":"+MetaType.operatorExec.toString());
			DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(
					operatorExec.getResult().getRef().getUuid(), operatorExec.getResult().getRef().getVersion(),
					MetaType.datastore.toString());
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			data = exec.fetchResults(datastore, null, rowLimit, null, commonServiceImpl.getApp().getUuid());
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}

			commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(),
					(message != null) ? message : "No data found.");
			throw new RuntimeException((message != null) ? message : "No data found.");
		}

		return data;
	}

	/**
	 * @Ganesh
	 *
	 * @param operator
	 * @param execParams
	 * @param operatorExec
	 * @return
	 * @throws Exception
	 */
	public boolean operator(Operator operator, ExecParams execParams, OperatorExec operatorExec) throws Exception {
		boolean isSuccess = false;
		Object result = null;
		try {
			operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec,
					Status.Stage.InProgress);

			String operatorName = String.format("%s_%s_%s", operator.getUuid().replace("-", "_"), operator.getVersion(),
					operatorExec.getVersion());
			String filePath = String.format("/%s/%s/%s", operator.getUuid().replace("-", "_"), operator.getVersion(),
					operatorExec.getVersion());
			String tableName = String.format("%s_%s_%s", operator.getUuid().replace("-", "_"), operator.getVersion(),
					operatorExec.getVersion());

			String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);

			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();

			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			String appUuid = commonServiceImpl.getApp().getUuid();

			MetaIdentifierHolder paramListHolder = operator.getParamList();
			ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(
					paramListHolder.getRef().getUuid(), paramListHolder.getRef().getVersion(),
					paramListHolder.getRef().getType().toString());
			List<Param> params = paramList.getParams();
			if (execParams != null) {
				List<ParamListHolder> paramListInfo = execParams.getParamListInfo();
				for (ParamListHolder holder : paramListInfo) {
					if (holder.getParamValue().getRef().getType().equals(MetaType.datapod)) {
						MetaIdentifierHolder datapodHolder = holder.getParamValue();
						Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
								datapodHolder.getRef().getUuid(), datapodHolder.getRef().getVersion(),
								datapodHolder.getRef().getType().toString());
						DataStore datastore = dataStoreServiceImpl.findDataStoreByMeta(datapod.getUuid(),
								datapod.getVersion());
						String tabName = exec.readFile(appUuid, datapod, datastore, tableName, hdfsInfo, null,
								datasource);
						String sql = transposeOldOperator.generateSql(datapod, tabName);
						result = exec.executeRegisterAndPersist(sql, tabName, filePath, datapod,
								SaveMode.Append.toString(), appUuid);
					}
				}
			}

			dataStoreServiceImpl.setRunMode(RunMode.BATCH);
			dataStoreServiceImpl.create(filePathUrl, operatorName,
					new MetaIdentifier(MetaType.operator, operator.getUuid(), operator.getVersion()),
					new MetaIdentifier(MetaType.operatorExec, operatorExec.getUuid(), operatorExec.getVersion()),
					operatorExec.getAppInfo(), operatorExec.getCreatedBy(), SaveMode.Append.toString(), resultRef);

			operatorExec.setResult(resultRef);
			commonServiceImpl.save(MetaType.operatorExec.toString(), operatorExec);
			if (result != null) {
				isSuccess = true;
				operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec,
						Status.Stage.Completed);
			} else {
				isSuccess = false;
				operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec,
						Status.Stage.Failed);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec,
					Status.Stage.Failed);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Operator execution failed.");
			throw new RuntimeException((message != null) ? message : "Operator execution failed.");
		}
		return isSuccess;
	}

	/**
	 * @Ganesh
	 *
	 * @param operator
	 * @param execParams
	 * @param paramMap
	 * @param operatorExec
	 * @return
	 * @throws Exception
	 */
	public OperatorExec create(Operator operator, ExecParams execParams, Object paramMap, OperatorExec operatorExec)
			throws Exception {
		try {
			if (operatorExec == null) {
				MetaIdentifierHolder operatorRef = new MetaIdentifierHolder();
				operatorExec = new OperatorExec();
				operatorRef.setRef(new MetaIdentifier(MetaType.operator, operator.getUuid(), operator.getVersion()));
				operatorExec.setDependsOn(operatorRef);
				operatorExec.setBaseEntity();
			}

			operatorExec.setName(operator.getName());
			operatorExec.setAppInfo(operator.getAppInfo());
			commonServiceImpl.save(MetaType.operatorExec.toString(), operatorExec);

			List<Status> statusList = operatorExec.getStatusList();
			if (statusList == null)
				statusList = new ArrayList<Status>();

			if (Helper.getLatestStatus(statusList) != null && (Helper.getLatestStatus(statusList)
					.equals(new Status(Status.Stage.InProgress, new Date()))
					|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date()))
					|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
				logger.info(
						" This process is In Progress or has been completed previously or is On Hold. Hence it cannot be rerun. ");
				return operatorExec;
			}
			operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec,
					Status.Stage.NotStarted);
		} catch (Exception e) {
			logger.error(e);
			operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec,
					Status.Stage.Failed);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Can not create executable Operator.");
			throw new RuntimeException((message != null) ? message : "Can not create executable Operator.");
		}
		return operatorExec;
	}

	public List<Operator> getOperatorByOperatorType(String type) {
		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("type");
		query.fields().include("createdOn");
		query.fields().include("appInfo");
		query.fields().include("active");
		query.fields().include("desc");
		query.fields().include("published");
		query.fields().include("paramList");
		query.fields().include("operatorType");

		/** this if condition is handeled temporarily, it can be changed in future **/
		if (type.equalsIgnoreCase(OperatorType.generateData.toString())) {
			query.addCriteria(Criteria.where("operatorType").in("GenerateData", "GenDataAttr","genDataValList"));
		} else {
			query.addCriteria(Criteria.where("operatorType").is(type));
		}

		List<Operator> operators = new ArrayList<>();
		operators = (List<Operator>) mongoTemplate.find(query, Operator.class);
		return operators;

	}
	
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		OperatorExec operatorExec = (OperatorExec) commonServiceImpl.getOneByUuidAndVersion(baseExec.getUuid(),
				baseExec.getVersion(), MetaType.operatorExec.toString());
		logger.info("Inside CustomOperatorServiceImpl.execute");
		Operator operator = (Operator) commonServiceImpl.getOneByUuidAndVersion(
				operatorExec.getDependsOn().getRef().getUuid(), operatorExec.getDependsOn().getRef().getVersion(),
				MetaType.operator.toString());
		logger.info("Operator type in execute : " + operator.getOperatorType());
		com.inferyx.framework.operator.IOperator newOperator = operatorFactory
				.getOperator(helper.getOperatorType(operator.getOperatorType()));
		commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec, Status.Stage.InProgress);
		synchronized (operatorExec) {
			commonServiceImpl.save(MetaType.operatorExec.toString(), operatorExec);
		}
		newOperator.execute(operatorExec, execParams, runMode);
		operatorExec = (OperatorExec) commonServiceImpl.getOneByUuidAndVersion(baseExec.getUuid(),
				baseExec.getVersion(), MetaType.operatorExec.toString());
		commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec, Status.Stage.Completed);
		synchronized (operatorExec) {
			commonServiceImpl.save(MetaType.operatorExec.toString(), operatorExec);
		}
		return null;
		// Provide operatorType in Factory and get operator executor
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		Operator operator = (Operator) commonServiceImpl.getOneByUuidAndVersion(
				baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(),
				MetaType.operator.toString());
		logger.info("Operator type in execute : " + operator.getOperatorType());
		com.inferyx.framework.operator.IOperator newOperator = operatorFactory
				.getOperator(helper.getOperatorType(operator.getOperatorType()));
		return newOperator.parse(baseExec, execParams, runMode);
	}

}

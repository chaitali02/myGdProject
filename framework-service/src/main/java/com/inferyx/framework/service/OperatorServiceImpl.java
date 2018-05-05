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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.dao.IAlgorithmDao;
import com.inferyx.framework.dao.IModelDao;
import com.inferyx.framework.dao.IModelExecDao;
import com.inferyx.framework.datascience.Math3Distribution;
import com.inferyx.framework.datascience.MonteCarloSimulation;
import com.inferyx.framework.datascience.Operator;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.OperatorType;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.factory.OperatorFactory;
import com.inferyx.framework.operator.DatasetOperator;
import com.inferyx.framework.operator.PredictMLOperator;
import com.inferyx.framework.operator.TransposeOperator;
import com.inferyx.framework.register.GraphRegister;

@Service
public class OperatorServiceImpl {
	
	@Autowired
	OperatorFactory operatorFactory;
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
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
	private TransposeOperator transposeOperator;
	
	static final Logger logger = Logger.getLogger(OperatorServiceImpl.class);
	
	
	public OperatorExec create(String uuid, String version, MetaType type, MetaType execType, OperatorExec operatorExec, 
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception {
		logger.info("Inside OperatorServiceImpl.create ");
		List<Status> statusList = null;
		OperatorType operatorType = null;
		if (StringUtils.isBlank(uuid)) {
			logger.info(" Nothing to create exec upon. Aborting ... ");
			return null;
		}
		operatorType = (OperatorType) commonServiceImpl.getOneByUuidAndVersion(uuid, version, type.toString());
		if (operatorType == null || type == null || execType == null) {
			logger.info(" Nothing to create exec upon. Aborting ... ");
			return null;
		}
		MetaIdentifierHolder baseRuleMeta = new MetaIdentifierHolder(new MetaIdentifier(type, operatorType.getUuid(), operatorType.getVersion()));
		if (operatorExec == null) {
			operatorExec = new OperatorExec();
			operatorExec.setDependsOn(baseRuleMeta);
			operatorExec.setBaseEntity();
			operatorExec.setName(operatorType.getName());
			operatorExec.setAppInfo(operatorType.getAppInfo());
			synchronized (operatorExec.getUuid()) {
				commonServiceImpl.save(execType.toString(), operatorExec);
			}
		}
		MetaIdentifier baseruleExecInfo = new MetaIdentifier(execType, operatorExec.getUuid(), operatorExec.getVersion());
		
		statusList = operatorExec.getStatusList();
		if (Helper.getLatestStatus(statusList) != null 
				&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date())) 
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date())) 
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Terminating, new Date()))
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
			logger.info(" This process is In Progress or has been completed previously or is Terminating or is On Hold. Hence it cannot be rerun. ");
			return operatorExec;
		}
		logger.info(" Set not started status");
		synchronized (operatorExec.getUuid()) {
			operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, execType, Status.Stage.NotStarted);
		}
		logger.info(" After Set not started status");
		return operatorExec;
	}


	public void execute(String uuid, 
						String version, 
						ThreadPoolTaskExecutor metaExecutor, 
						OperatorExec operatorExec, 
						List<FutureTask<TaskHolder>> taskList, 
						ExecParams execParams, 
						Mode runMode) throws Exception {
		logger.info("Inside OperatorServiceImpl.execute");
		commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec, Status.Stage.NotStarted);
		OperatorType operatorType = (OperatorType) commonServiceImpl.getOneByUuidAndVersion(operatorExec.getDependsOn().getRef().getUuid(), 
																				operatorExec.getDependsOn().getRef().getVersion(), 
																				MetaType.operatortype.toString());
		com.inferyx.framework.operator.Operator newOperator =  operatorFactory.getOperator(operatorType.getName());
		commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec, Status.Stage.InProgress);
		synchronized (operatorExec) {
			commonServiceImpl.save(MetaType.operatorExec.toString(), operatorExec);
		}
		newOperator.execute(operatorType, execParams, operatorExec, null, null, new HashSet<>(), runMode);
		commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec, Status.Stage.Completed);
		synchronized (operatorExec) {
			commonServiceImpl.save(MetaType.operatorExec.toString(), operatorExec);
		}
		// Provide operatorType in Factory and get operator executor
	}

	
	/*public DataFrame matrixMulOperator(DataFrame df,HashMap<String,Object> operParams,
			OrderKey dpKey) throws JsonProcessingException{
		MatrixMulOperator mmo = new MatrixMulOperator();
		StructType schema = datapodServiceImpl.populateSchema(dpKey.getUUID(), dpKey.getVersion());
		JavaRDD<Row> finalOutput = mmo.populateNFetch(df, operParams);
		DataFrame dfTask = hiveContext.createDataFrame(finalOutput, schema);
		return dfTask;
	}*/
	
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
			OperatorExec operatorExec = (OperatorExec) commonServiceImpl.getOneByUuidAndVersion(operatorExecUuid, operatorExecVersion,
					MetaType.operatorExec.toString());

			DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(
					operatorExec.getResult().getRef().getUuid(), operatorExec.getResult().getRef().getVersion(),
					MetaType.datastore.toString());
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			data = exec.fetchResults(datastore, null, rowLimit, commonServiceImpl.getApp().getUuid());
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "No data found.");
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
			operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec, Status.Stage.InProgress);
			

			String operatorName = String.format("%s_%s_%s", operator.getUuid().replace("-", "_"), operator.getVersion(), operatorExec.getVersion());
			String filePath = String.format("/%s/%s/%s", operator.getUuid().replace("-", "_"), operator.getVersion(), operatorExec.getVersion());
			String tableName = String.format("%s_%s_%s", operator.getUuid().replace("-", "_"), operator.getVersion(), operatorExec.getVersion());

			String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);

			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			String appUuid = commonServiceImpl.getApp().getUuid();
			
			MetaIdentifierHolder operatorTypeHolder = operator.getOperatorType();
			OperatorType operatorType = (OperatorType) commonServiceImpl.getOneByUuidAndVersion(operatorTypeHolder.getRef().getUuid(), operatorTypeHolder.getRef().getVersion(), operatorTypeHolder.getRef().getType().toString());
			MetaIdentifierHolder paramListHolder = operatorType.getParamList();
			ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(paramListHolder.getRef().getUuid(), paramListHolder.getRef().getVersion(), paramListHolder.getRef().getType().toString());
			List<Param> params = paramList.getParams();
			if(execParams != null) {
				List<ParamListHolder> paramListInfo = execParams.getParamListInfo();
				for(ParamListHolder holder : paramListInfo) {
					if(holder.getParamValue().getRef().getType().equals(MetaType.datapod)) {
						MetaIdentifierHolder datapodHolder = holder.getParamValue();
						Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodHolder.getRef().getUuid(), datapodHolder.getRef().getVersion(), datapodHolder.getRef().getType().toString());
						DataStore datastore = dataStoreServiceImpl.findDataStoreByMeta(datapod.getUuid(), datapod.getVersion());
						String tabName = exec.readFile(appUuid, datapod, datastore, tableName, hdfsInfo, null, datasource);
						String sql = transposeOperator.generateSql(datapod, tabName);
						result = exec.executeRegisterAndPersist(sql, tabName, filePath, datapod, SaveMode.Append.toString(), appUuid);
					}
				}
			}
			
			dataStoreServiceImpl.setRunMode(Mode.BATCH);
			dataStoreServiceImpl.create(filePathUrl, operatorName,
					new MetaIdentifier(MetaType.operator, operator.getUuid(), operator.getVersion()),
					new MetaIdentifier(MetaType.operatorExec, operatorExec.getUuid(), operatorExec.getVersion()),
					operatorExec.getAppInfo(), operatorExec.getCreatedBy(), SaveMode.Append.toString(), resultRef);

			operatorExec.setResult(resultRef);
			commonServiceImpl.save(MetaType.operatorExec.toString(), operatorExec);
			if (result != null) {
				isSuccess = true;
				operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec, Status.Stage.Completed);
			}else {
				isSuccess = false;
				operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec, Status.Stage.Failed);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec, Status.Stage.Failed);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Operator execution failed.");
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
	public OperatorExec create(Operator operator, ExecParams execParams, Object paramMap, OperatorExec operatorExec) throws Exception {
		try {
			if(operatorExec == null) {
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
			
			if (Helper.getLatestStatus(statusList) != null 
					&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
				logger.info(" This process is In Progress or has been completed previously or is On Hold. Hence it cannot be rerun. ");
				return operatorExec;
			}
			operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec, Status.Stage.NotStarted);			
		} catch (Exception e) {
			logger.error(e);	
			operatorExec = (OperatorExec) commonServiceImpl.setMetaStatus(operatorExec, MetaType.operatorExec, Status.Stage.Failed);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable Operator.");
			throw new RuntimeException((message != null) ? message : "Can not create executable Operator.");
		}
		return operatorExec;
	}	
}

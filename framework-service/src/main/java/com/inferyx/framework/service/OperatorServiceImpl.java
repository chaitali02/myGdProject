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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.datascience.Operator;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.OperatorType;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.factory.OperatorFactory;

@Service
public class OperatorServiceImpl {
	/*@Autowired
	private HiveContext hiveContext;*/
	@Autowired
    private DatapodServiceImpl datapodServiceImpl;
	@Autowired
	CommonServiceImpl commonServiceImpl;
	@Autowired
	OperatorFactory operatorFactory;
	
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
}

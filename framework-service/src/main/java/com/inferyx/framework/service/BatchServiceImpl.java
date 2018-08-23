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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.Batch;
import com.inferyx.framework.domain.BatchExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;

/**
 * @author Ganesh
 *
 */
@Service
public class BatchServiceImpl {

	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private DagServiceImpl dagServiceImpl;
	
	static final Logger logger = Logger.getLogger(BatchServiceImpl.class);
	
	public BatchExec create(String batchUuid, String batchVersion, ExecParams execParams, BatchExec batchExec, RunMode runMode) throws Exception {
		try {
			Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchUuid, batchVersion, MetaType.batch.toString());
			if(batchExec == null) {
				MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.batch, batchUuid, batchUuid));
				batchExec = new BatchExec();
				batchExec.setDependsOn(dependsOn);
				batchExec.setBaseEntity();
			}
			
			batchExec.setExecParams(execParams);
			
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			List<Status> statusList = batchExec.getStatusList();
			if (statusList == null) {
				statusList = new ArrayList<Status>();
			}
			batchExec.setName(batch.getName());
			batchExec.setAppInfo(batch.getAppInfo());	
			
			commonServiceImpl.save(MetaType.batchExec.toString(), batchExec);
			
			batchExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
	
			batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.NotStarted);
		} catch (Exception e) {
			logger.error(e);
			batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.Failed);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable batch.");
			throw new RuntimeException((message != null) ? message : "Can not create executable batch.");
		}			
		return batchExec;
	}

	public BatchExec execute(String batchUuid, String batchVersion, BatchExec batchExec, ExecParams execParams, String type, RunMode runMode) throws Exception {
		Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchUuid, batchVersion, MetaType.batch.toString());
		batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.InProgress);
		List<MetaIdentifierHolder> execList = new ArrayList<>();
		for(MetaIdentifierHolder dagIM : batch.getMetaList()) {
			switch(dagIM.getRef().getType()) {
				case dag : execList.add(dagServiceImpl.submitDag(dagIM.getRef().getUuid(), dagIM.getRef().getVersion(), execParams, type, runMode));
					break;
			default:
				break;
			}
			
		}
		batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.InProgress);
		batchExec.setExecList(execList);
		batchExec = (BatchExec) commonServiceImpl.setMetaStatus(batchExec, MetaType.batchExec, Status.Stage.Completed);
//		for(MetaIdentifierHolder exec : execList) {
//			
//		}
//		int i =0;
//		while(!execList.isEmpty() 
//				&& ((BaseExec)commonServiceImpl.getOneByUuidAndVersion(execList.get(i).getRef().getUuid(), execList.get(i).getRef().getVersion(), execList.get(i).getRef().getType().toString())).getStatusList().get(0).getStage().equals(Status.Stage.Completed)) {
//			
//			i++;
//		}
		return batchExec;
	}

}

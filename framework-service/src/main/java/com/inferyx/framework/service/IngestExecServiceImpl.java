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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.Ingest;
import com.inferyx.framework.domain.IngestExec;
import com.inferyx.framework.domain.IngestGroupExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.enums.IngestionType;
import com.inferyx.framework.executor.SparkStreamingExecutor;

/**
 * @author Ganesh
 *
 */
@Service
public class IngestExecServiceImpl extends BaseRuleExecTemplate {
	@Autowired
	private SparkStreamingExecutor<?, ?> sparkStreamingExecutor;
	
	/**
	 * Kill meta thread if In Progress
	 * @param uuid
	 * @param version
	 * @param status TODO
	 * @param type TODO
	 * @throws Exception 
	 * @throws JsonProcessingException 
	 */
	public void kill(String uuid, String version, String status, String type) throws JsonProcessingException, Exception {
		IngestExec ingestExec = (IngestExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.ingestExec.toString());
		MetaIdentifier dependsOnMI = ingestExec.getDependsOn().getRef();
		Ingest ingest =  (Ingest) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(), dependsOnMI.getVersion(), dependsOnMI.getType().toString());
		IngestionType ingestionType = Helper.getIngestionType(ingest.getType());
		
		if(ingestionType.equals(IngestionType.STREAMTOFILE)
				|| ingestionType.equals(IngestionType.STREAMTOTABLE)) {
			MetaIdentifier sourceDSMI = ingest.getSourceDatasource().getRef();
			Datasource sourceDS = (Datasource) commonServiceImpl.getLatestByUuid(sourceDSMI.getUuid(), sourceDSMI.getType().toString());
			sparkStreamingExecutor.stop(sourceDS);
		} else {
			commonServiceImpl.setStatus(type, uuid, version, status);	
		}
	}
	
	public ExecStatsHolder getNumRowsbyExec(String execUuid, String execVersion, String type) throws Exception {
		
		Object exec = commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, type);
		MetaIdentifierHolder resultHolder = (MetaIdentifierHolder) exec.getClass().getMethod("getResult").invoke(exec);
		com.inferyx.framework.domain.DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(resultHolder.getRef().getUuid(), resultHolder.getRef().getVersion(), MetaType.datastore.toString());
		MetaIdentifier mi = new MetaIdentifier();
		ExecStatsHolder execHolder=new ExecStatsHolder();
		mi.setType(MetaType.datastore);
		mi.setUuid(resultHolder.getRef().getUuid());
		mi.setVersion(resultHolder.getRef().getVersion());
		execHolder.setRef(mi);
		execHolder.setNumRows(dataStore.getNumRows());
		execHolder.setPersistMode(dataStore.getPersistMode());
		execHolder.setRunMode(dataStore.getRunMode());
		return execHolder;
	}

	public Object findReconExecByReconGroupExec(String reconGroupExecUuid, String reconGroupExecVersion) throws JsonProcessingException {
		List<IngestExec> ingestExecList = new ArrayList<>();
		IngestGroupExec ingestGroupExec = (IngestGroupExec) commonServiceImpl.getOneByUuidAndVersion(reconGroupExecUuid, reconGroupExecVersion, MetaType.ingestgroupExec.toString());
		for (MetaIdentifierHolder ingestExecHolder : ingestGroupExec.getExecList()) {
//			ingestExecList.add((IngestExec)daoRegister.getRefObject(ingestExecHolder.getRef()));
			ingestExecList.add((IngestExec)commonServiceImpl.getOneByUuidAndVersion(ingestExecHolder.getRef().getUuid(), ingestExecHolder.getRef().getVersion(), ingestExecHolder.getRef().getType().toString()));
		}
		return resolveName(ingestExecList);
	}
	
	public List<IngestExec> resolveName(List<IngestExec> ingestExec) throws JsonProcessingException {
		List<IngestExec> ingestExecList = new ArrayList<>();
		for(IngestExec ingestE : ingestExec)	{
			IngestExec ingestExecLatest = (IngestExec) commonServiceImpl.getOneByUuidAndVersion(ingestE.getUuid(), ingestE.getVersion(), MetaType.ingestExec.toString());
			String createdByRefUuid = ingestE.getCreatedBy().getRef().getUuid();
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			ingestExecLatest.getCreatedBy().getRef().setName(user.getName());
//			Ingest ingest = (Ingest)daoRegister.getRefObject(ingestExecLatest.getDependsOn().getRef());
			Ingest ingest = (Ingest)commonServiceImpl.getOneByUuidAndVersion(ingestExecLatest.getDependsOn().getRef().getUuid(), ingestExecLatest.getDependsOn().getRef().getVersion(), ingestExecLatest.getDependsOn().getRef().getType().toString());
			ingestExecLatest.getDependsOn().getRef().setName(ingest.getName());
			ingestExecList.add(ingestExecLatest);
		}
		return ingestExecList;
	}
}

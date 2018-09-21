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
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.Ingest;
import com.inferyx.framework.domain.IngestExec;
import com.inferyx.framework.domain.IngestGroupExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Recon;
import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.User;

/**
 * @author Ganesh
 *
 */
@Service
public class IngestExecServiceImpl extends BaseRuleExecTemplate {
	
	@Autowired
	private MetadataUtil daoRegister;
	
	/**
	 * Kill meta thread if In Progress
	 * @param uuid
	 * @param version
	 */
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.ingestExec);
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
			ingestExecList.add((IngestExec)daoRegister.getRefObject(ingestExecHolder.getRef()));
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
			Ingest recon = (Ingest)daoRegister.getRefObject(ingestExecLatest.getDependsOn().getRef());
			ingestExecLatest.getDependsOn().getRef().setName(recon.getName());
			ingestExecList.add(ingestExecLatest);
		}
		return ingestExecList;
	}
}

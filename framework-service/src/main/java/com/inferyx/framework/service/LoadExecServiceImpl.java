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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;

@Service
public class LoadExecServiceImpl  extends BaseRuleExecTemplate {
	
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(LoadExecServiceImpl.class);


	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//LoadExec loadExec = iLoadExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		LoadExec loadExec = (LoadExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.loadExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.load);
		mi.setUuid(loadExec.getDependsOn().getRef().getUuid());
		mi.setVersion(loadExec.getDependsOn().getRef().getVersion());
		return mi;
	}

	public ExecStatsHolder getNumRowsbyExec(String execUuid, String execVersion) throws JsonProcessingException {
		LoadExec loadExec = (LoadExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.loadExec.toString());
		com.inferyx.framework.domain.DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(loadExec.getResult().getRef().getUuid(), loadExec.getResult().getRef().getVersion(), MetaType.datastore.toString());
		MetaIdentifier mi = new MetaIdentifier();
		ExecStatsHolder execHolder=new ExecStatsHolder();
		mi.setType(MetaType.datastore);
		mi.setUuid(loadExec.getResult().getRef().getUuid());
		mi.setVersion(loadExec.getResult().getRef().getVersion());
		execHolder.setRef(mi);
		execHolder.setNumRows(dataStore.getNumRows());
		execHolder.setPersistMode(dataStore.getPersistMode());
		execHolder.setRunMode(dataStore.getRunMode());
		return execHolder;
	}	
	
	/**
	 * Kill meta thread if RUNNING
	 * @param uuid
	 * @param version
	 */
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.loadExec);
	}
}

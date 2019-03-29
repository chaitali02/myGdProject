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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IRuleGroupExecDao;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.register.GraphRegister;

@Service
public class MapExecServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	UserServiceImpl userServiceImpl;	
	@Autowired
	SecurityServiceImpl securityServiceImpl;	
	@Autowired
	protected IRuleGroupExecDao iRuleGroupExecDao;	
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	RelationServiceImpl relationServiceImpl;	
	@Autowired
	DatapodServiceImpl datapodServiceImpl;	
	@Autowired
	MapServiceImpl mapServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Resource(name="taskThreadMap")
	ConcurrentHashMap taskThreadMap;
	
	static final Logger logger = Logger.getLogger(MapExecServiceImpl.class);


	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//MapExec mapExec = iMapExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		MapExec mapExec = (MapExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.mapExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.map);
		mi.setUuid(mapExec.getDependsOn().getRef().getUuid());
		mi.setVersion(mapExec.getDependsOn().getRef().getVersion());
		return mi;
	}
	
	/**
	 * Kill meta thread if RUNNING
	 * @param uuid
	 * @param version
	 * @throws JsonProcessingException 
	 */
	public void kill (String uuid, String version) throws JsonProcessingException {
		MetaIdentifier mapExecMI = new MetaIdentifier(MetaType.mapExec, uuid, version);
//		MapExec mapExec = (MapExec) daoRegister.getRefObject(mapExecMI);
		MapExec mapExec = (MapExec) commonServiceImpl.getOneByUuidAndVersion(mapExecMI.getUuid(), mapExecMI.getVersion(), mapExecMI.getType().toString(), "N");
		if (mapExec == null) {
			logger.info("Nothing to kill. Aborting ... ");
			return;
		}
		
		try {
			mapExec = (MapExec) commonServiceImpl.setMetaStatus(mapExec, MetaType.mapExec, Status.Stage.TERMINATING);
			if (!Helper.getLatestStatus(mapExec.getStatusList()).equals(new Status(Status.Stage.TERMINATING, new Date()))) {
				logger.info(" Status is not TERMINATING. So aborting ... ");
				return;
			}
			FutureTask futureTask = (FutureTask) taskThreadMap.get("Map_" + mapExec.getUuid());
			if (futureTask != null && !futureTask.isDone()) {
				futureTask.cancel(true);
			}
			taskThreadMap.remove("Map_" + mapExec.getUuid());
			commonServiceImpl.setMetaStatus(mapExec, MetaType.mapExec, Status.Stage.KILLED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}			

	public ExecStatsHolder getNumRowsbyExec(String execUuid, String execVersion, String type) throws Exception {

		Object exec = commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, type);
		MetaIdentifierHolder resultHolder = (MetaIdentifierHolder) exec.getClass().getMethod("getResult").invoke(exec);
		com.inferyx.framework.domain.DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(
				resultHolder.getRef().getUuid(), resultHolder.getRef().getVersion(), MetaType.datastore.toString());
		MetaIdentifier mi = new MetaIdentifier();
		ExecStatsHolder execHolder = new ExecStatsHolder();
		mi.setType(MetaType.datastore);
		mi.setUuid(resultHolder.getRef().getUuid());
		mi.setVersion(resultHolder.getRef().getVersion());
		execHolder.setRef(mi);
		execHolder.setNumRows(dataStore.getNumRows());
		execHolder.setPersistMode(dataStore.getPersistMode());
		execHolder.setRunMode(dataStore.getRunMode());
		return execHolder;
	}

}

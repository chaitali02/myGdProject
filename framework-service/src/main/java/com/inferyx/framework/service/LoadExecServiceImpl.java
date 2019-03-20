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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.ILoadExecDao;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;

@Service
public class LoadExecServiceImpl  extends BaseRuleExecTemplate {
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ILoadExecDao iLoadExecDao;
	@Autowired
	LoadServiceImpl loadServiceImpl;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(LoadExecServiceImpl.class);

	/********************** UNUSED **********************/
	/*public LoadExec findLatest() {
		LoadExec loadexec=null;
		if(iLoadExecDao.findLatest(new Sort(Sort.Direction.DESC, "version"))!=null){
			loadexec=resolveName(iLoadExecDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
		}
		return loadexec ;
	}*/

	/********************** UNUSED **********************/
	/*public List<LoadExec> findOneByrule(String loadUuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iLoadExecDao.findOneByLoad(appUuid, loadUuid);
	}*/

	/********************** UNUSED **********************/
	/*public LoadExec findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iLoadExecDao.findOneById(appUuid, id);
		}
		return iLoadExecDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public LoadExec findLatestByUuid(String loadExecUUID) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iLoadExecDao.findLatestByUuid(loadExecUUID, new Sort(Sort.Direction.DESC, "version"));
		}
		return iLoadExecDao.findLatestByUuid(appUuid, loadExecUUID, new Sort(Sort.Direction.DESC, "version"));
	}*/

	

	/********************** UNUSED **********************/
/*	public List<LoadExec> findAllLatestActive() {
		Aggregation loadExecAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<LoadExec> loadExecResults = mongoTemplate.aggregate(loadExecAggr, "loadexec",
				LoadExec.class);
		List<LoadExec> loadExecList = loadExecResults.getMappedResults();

		// Fetch the LoadExec details for each id
		List<LoadExec> result = new ArrayList<LoadExec>();
		for (LoadExec r : loadExecList) {
			LoadExec loadExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				loadExecLatest = iLoadExecDao.findOneByUuidAndVersion(appUuid, r.getId(), r.getVersion());
			} else {
				loadExecLatest = iLoadExecDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if (loadExecLatest != null) {
				result.add(loadExecLatest);
			}
		}
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public LoadExec findOneByUuidAndVersion(String uuid, String version) {
		// String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iLoadExecDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} else {
			return iLoadExecDao.findOneByUuidAndVersion(uuid, version);
		}
	}*/

	/********************** UNUSED **********************/
	/*public List<LoadExec> findLoadExecByRule(String loadUuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		// String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		List<LoadExec> loadExecList = null;
		if (appUuid == null) {
			loadExecList = iLoadExecDao.findOneByLoad(loadUuid);
		} else {
			loadExecList = iLoadExecDao.findOneByLoad(appUuid, loadUuid);
		}
		List<LoadExec> resolvedLoadExecList = new ArrayList<>();
		for (LoadExec loadExec : loadExecList) {
			resolveName(loadExec);
			resolvedLoadExecList.add(loadExec);
		}
		return resolvedLoadExecList;
	}*/

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

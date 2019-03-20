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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IRuleExecDao;
import com.inferyx.framework.dao.IRuleGroupExecDao;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.User;

@Service
public class RuleExecServiceImpl extends BaseRuleExecTemplate {
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private IRuleExecDao iRuleExecDao;	
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
	RuleServiceImpl ruleServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	
	
	static final Logger logger = Logger.getLogger(RuleExecServiceImpl.class);

	/********************** UNUSED **********************/
	/*public RuleExec findLatest() {
		RuleExec ruleexec=null;
		if(iRuleExecDao.findLatest(new Sort(Sort.Direction.DESC, "version"))!=null){
			ruleexec=resolveName(iRuleExecDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
		}
		return ruleexec ;
	}*/
	
	/********************** UNUSED **********************/
	/*public List<RuleExec> findOneByrule(String ruleUUID) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iRuleExecDao.findOneByrule(appUuid,ruleUUID);
	}*/

	/********************** UNUSED **********************/
	/*public RuleExec findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iRuleExecDao.findOneById(appUuid,id);
		}
		return iRuleExecDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public RuleExec save(RuleExec ruleExec) {
		if(ruleExec.getAppInfo() == null)
		{
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		ruleExec.setAppInfo(metaIdentifierHolderList);
		}
		ruleExec.setBaseEntity();
		return iRuleExecDao.save(ruleExec);		
	}*/

	/********************** UNUSED **********************/
	/*public RuleExec findLatestByUuid(String RuleExecUUID) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iRuleExecDao.findLatestByUuid(RuleExecUUID,new Sort(Sort.Direction.DESC, "version"));
		}
		return iRuleExecDao.findLatestByUuid(appUuid,RuleExecUUID,new Sort(Sort.Direction.DESC, "version"));		
	}*/	

	/********************** UNUSED **********************/
	/*public void  delete(String id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		RuleExec ruleExec = iRuleExecDao.findOneById(appUuid,id);
		String ID=ruleExec.getId();
		iRuleExecDao.delete(ID);		
	}*/
	/********************** UNUSED **********************/
	/*public RuleExec findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iRuleExecDao.findAllByUuid(appUuid,uuid);
		
	}*/

	/********************** UNUSED **********************/
	/*public List<RuleExec> findAllLatestActive() 	
	{	   
	   Aggregation ruleExecAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<RuleExec> ruleExecResults = mongoTemplate.aggregate(ruleExecAggr,"ruleexec", RuleExec.class);	   
	   List<RuleExec> ruleExecList = ruleExecResults.getMappedResults();

	   // Fetch the ruleExec details for each id
	   List<RuleExec> result=new  ArrayList<RuleExec>();
	   for(RuleExec r : ruleExecList)
	   {   
		   RuleExec ruleExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				ruleExecLatest = iRuleExecDao.findOneByUuidAndVersion(appUuid,r.getId(), r.getVersion());
			}
			else
			{
				ruleExecLatest = iRuleExecDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
		
			result.add(ruleExecLatest);
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public RuleExec findOneByUuidAndVersion(String uuid, String version){
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if (appUuid != null) {
			return iRuleExecDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} else {
			return iRuleExecDao.findOneByUuidAndVersion(uuid, version);
		}
	}*/

	public List<RuleExec> resolveName(List<RuleExec> ruleExec) throws JsonProcessingException {
		List<RuleExec> ruleExecList = new ArrayList<>();
		for(RuleExec ruleE : ruleExec)	{
		//RuleExec ruleExecLatest = findOneByUuidAndVersion(ruleE.getUuid(), ruleE.getVersion());
		RuleExec ruleExecLatest = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleE.getUuid(), ruleE.getVersion(), MetaType.ruleExec.toString(), "N");
		String createdByRefUuid = ruleE.getCreatedBy().getRef().getUuid();
		//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
		ruleExecLatest.getCreatedBy().getRef().setName(user.getName());
//		Rule rule = (Rule)daoRegister.getRefObject(ruleExecLatest.getDependsOn().getRef());
		Rule rule = (Rule) commonServiceImpl.getOneByUuidAndVersion(ruleExecLatest.getDependsOn().getRef().getUuid(), ruleExecLatest.getDependsOn().getRef().getVersion(), ruleExecLatest.getDependsOn().getRef().getType().toString(), "N");
		ruleExecLatest.getDependsOn().getRef().setName(rule.getName());
		ruleExecList.add(ruleExecLatest);
		}
		return ruleExecList;
	}

	public RuleExec resolveName(RuleExec ruleExec) {
		try {
			String createdByRefUuid = ruleExec.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			ruleExec.getCreatedBy().getRef().setName(user.getName());
			
			if(ruleExec.getDependsOn() != null){
				String dependsOnUuid=ruleExec.getDependsOn().getRef().getUuid();
				//com.inferyx.framework.domain.Rule rule = ruleServiceImpl.findLatestByUuid(dependsOnUuid);
				com.inferyx.framework.domain.Rule rule = (Rule) commonServiceImpl.getLatestByUuid(dependsOnUuid, MetaType.rule.toString());
				ruleExec.getDependsOn().getRef().setName(rule.getName());
			}
			
			if(ruleExec.getResult() != null){
				String dataStoreUuid=ruleExec.getResult().getRef().getUuid();
				//com.inferyx.framework.domain.DataStore datastore=dataStoreServiceImpl.findLatestByUuid(dataStoreUuid);
				com.inferyx.framework.domain.DataStore datastore = (DataStore) commonServiceImpl.getLatestByUuid(dataStoreUuid, MetaType.datastore.toString());
				ruleExec.getResult().getRef().setName(datastore.getName());
			}
			
			if(ruleExec.getRefKeyList() !=null){
			    for(int i=0;i<ruleExec.getRefKeyList().size();i++) {
			    	MetaType type=ruleExec.getRefKeyList().get(i).getType();
				    if(type.toString().equals(MetaType.relation.toString())){
				         String relationUuid=ruleExec.getRefKeyList().get(i).getUuid();
				         //com.inferyx.framework.domain.Relation relation=relationServiceImpl.findLatestByUuid(relationUuid);
				         com.inferyx.framework.domain.Relation relation = (Relation) commonServiceImpl.getLatestByUuid(relationUuid, MetaType.relation.toString());
				         ruleExec.getRefKeyList().get(i).setName(relation.getName());    
				    } else if(type.toString().equals(MetaType.datapod.toString())){
					    String datapodUuid=ruleExec.getRefKeyList().get(i).getUuid();
					    //com.inferyx.framework.domain.Datapod datapod=datapodServiceImpl.findLatestByUuid(datapodUuid);
					    com.inferyx.framework.domain.Datapod datapod = (Datapod) commonServiceImpl.getLatestByUuid(datapodUuid, MetaType.datapod.toString());
					    ruleExec.getRefKeyList().get(i).setName(datapod.getName());				   
				    }
			   }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return ruleExec;		
	}

	public List<RuleExec> findRuleExecByRuleGroupExec(String ruleGroupExecUuid, String ruleGroupExecVersion) throws JsonProcessingException {
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;*/
		List<RuleExec> ruleExecList = new ArrayList<>();
		/*RuleGroupExec ruleGroupExec = null;
		if (appUuid == null) {
			ruleGroupExec = iRuleGroupExecDao.findOneByUuidAndVersion(ruleGroupExecUuid, ruleGroupExecVersion);
		} else {
			ruleGroupExec = iRuleGroupExecDao.findOneByUuidAndVersion(appUuid, ruleGroupExecUuid, ruleGroupExecVersion);
		}*/
		RuleGroupExec ruleGroupExec = (RuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(ruleGroupExecUuid, ruleGroupExecVersion, MetaType.rulegroupExec.toString(), "N");
		for (MetaIdentifierHolder ruleExecHolder : ruleGroupExec.getExecList()) {
//			ruleExecList.add((RuleExec)daoRegister.getRefObject(ruleExecHolder.getRef()));
			ruleExecList.add((RuleExec)commonServiceImpl.getOneByUuidAndVersion(ruleExecHolder.getRef().getUuid(), ruleExecHolder.getRef().getVersion(), ruleExecHolder.getRef().getType().toString(), "N"));
		}
		return resolveName(ruleExecList);
	}
		
	public List<RuleExec> findRuleExecByRule(String ruleUuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		List<RuleExec> ruleExecList = null;
		if (appUuid == null) {
			ruleExecList = iRuleExecDao.findOneByrule(ruleUuid);
		} else {
			ruleExecList = iRuleExecDao.findOneByrule(appUuid, ruleUuid);
		}
		List<RuleExec> RuleExecList = new ArrayList<>();
		for(RuleExec ruleExec : ruleExecList) {
			resolveName(ruleExec);
			RuleExecList.add(ruleExec);
		}
		return RuleExecList;
	}

	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//RuleExec ruleExec = iRuleExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.ruleExec.toString());
//		MetaIdentifier mi = new MetaIdentifier();
//		mi.setType(MetaType.rule);
//		mi.setUuid(ruleExec.getDependsOn().getRef().getUuid());
//		mi.setVersion(ruleExec.getDependsOn().getRef().getVersion());
		return ruleExec.getDependsOn().getRef();
	}
	
	public ExecStatsHolder getNumRowsbyExec(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//RuleExec ruleExec = iRuleExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.ruleExec.toString());
		//com.inferyx.framework.domain.DataStore dataStore=dataStoreServiceImpl.findOneByUuidAndVersion(ruleExec.getResult().getRef().getUuid(), ruleExec.getResult().getRef().getVersion());
		com.inferyx.framework.domain.DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(ruleExec.getResult().getRef().getUuid(), ruleExec.getResult().getRef().getVersion(), MetaType.datastore.toString());
		MetaIdentifier mi = new MetaIdentifier();
		ExecStatsHolder execHolder=new ExecStatsHolder();
		mi.setType(MetaType.datastore);
		mi.setUuid(ruleExec.getResult().getRef().getUuid());
		mi.setVersion(ruleExec.getResult().getRef().getVersion());
		execHolder.setRef(mi);
		execHolder.setNumRows(dataStore.getNumRows());
		execHolder.setPersistMode(dataStore.getPersistMode());
		execHolder.setRunMode(dataStore.getRunMode());
		return execHolder;
	}

	/**
	 * Put Rule on Hold
	 * @param uuid
	 * @param version
	 */
	public void PAUSE (String uuid, String version) {
		PAUSE(uuid, version, MetaType.ruleExec);
	}
	
	/**
	 * RESUME Rule Execution
	 * @param uuid
	 * @param version
	 */
	public void RESUME (String uuid, String version) {
		super.RESUME(uuid, version, MetaType.ruleExec);
	}

	
	/**
	 * Kill RuleExec
	 * @param uuid
	 * @param version
	 */
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.ruleExec);
	}
}

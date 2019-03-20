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
import com.inferyx.framework.dao.IRuleGroupExecDao;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.User;

@Service
public class RuleGroupExecServiceImpl extends BaseGroupExecTemplate {	
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private IRuleGroupExecDao iRuleGroupExecDao;	
	@Autowired
	UserServiceImpl userServiceImpl;	
	@Autowired
	SecurityServiceImpl securityServiceImpl;	
	@Autowired
	RuleGroupServiceImpl ruleGroupServiceImpl;
	@Autowired
	RuleExecServiceImpl ruleExecServiceImpl;

	static final Logger logger = Logger.getLogger(RuleGroupExecServiceImpl.class);

	/********************** UNUSED **********************/
	/*public RuleGroupExec findLatest() {
		RuleGroupExec rulegroupexec=null;
		if(iRuleGroupExecDao.findLatest(new Sort(Sort.Direction.DESC, "version"))!=null){
			rulegroupexec=resolveName(iRuleGroupExecDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
		}
		return rulegroupexec ;
	}*/

	/********************** UNUSED **********************/
	/*public RuleGroupExec findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iRuleGroupExecDao.findOneById(appUuid,id);
		}
		return iRuleGroupExecDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public RuleGroupExec save(RuleGroupExec ruleGroupExec) {
		if(ruleGroupExec.getAppInfo() == null)
		{
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		ruleGroupExec.setAppInfo(metaIdentifierHolderList);
		}
		ruleGroupExec.setBaseEntity();
		return iRuleGroupExecDao.save(ruleGroupExec);		
	}*/

	/********************** UNUSED **********************/
	/*public RuleGroupExec findLatestByUuid(String RuleGroupExecUUID) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iRuleGroupExecDao.findLatestByUuid(RuleGroupExecUUID,new Sort(Sort.Direction.DESC, "version"));
		}
		return iRuleGroupExecDao.findLatestByUuid(appUuid,RuleGroupExecUUID,new Sort(Sort.Direction.DESC, "version"));		
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		RuleGroupExec ruleGroupExec = iRuleGroupExecDao.findOneById(appUuid,id);
		String ID=ruleGroupExec.getId();
		iRuleGroupExecDao.delete(ID);		
	}*/
	/********************** UNUSED **********************/
	/*public List<RuleGroupExec> findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iRuleGroupExecDao.findAllByUuid(appUuid,uuid);
		
	}*/

	/********************** UNUSED **********************/
	/*public List<RuleGroupExec> findAllLatestActive() 	{	   
	   Aggregation ruleGroupExecAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<RuleGroupExec> ruleGroupExecResults = mongoTemplate.aggregate(ruleGroupExecAggr,"rulegroupexec", RuleGroupExec.class);	   
	   List<RuleGroupExec> ruleGroupExecList = ruleGroupExecResults.getMappedResults();

	   // Fetch the ruleExec details for each id
	   List<RuleGroupExec> result=new  ArrayList<RuleGroupExec>();
	   for(RuleGroupExec r : ruleGroupExecList)
	   {   
		   RuleGroupExec ruleGroupExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				ruleGroupExecLatest = iRuleGroupExecDao.findOneByUuidAndVersion(appUuid,r.getId(), r.getVersion());
			}
			else
			{
				ruleGroupExecLatest = iRuleGroupExecDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
		
			result.add(ruleGroupExecLatest);
	   }
	   return result;
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/	
	/*public RuleGroupExec findOneByUuidAndVersion(String uuid, String version){
		String appUuid = null;
		if (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null) {
			appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		}
		if (StringUtils.isNotBlank(appUuid)) {
			return iRuleGroupExecDao.findOneByUuidAndVersion(appUuid,uuid,version);
		}
		return iRuleGroupExecDao.findOneByUuidAndVersion(uuid,version);
	}*/

	public List<RuleGroupExec> resolveName(List<RuleGroupExec> ruleGroupExecList) throws JsonProcessingException {
		List<RuleGroupExec> ruleGroupExecListNew = new ArrayList<>();
		for(RuleGroupExec ruleGroupExec : ruleGroupExecList)
		{
		String createdByRefUuid = ruleGroupExec.getCreatedBy().getRef().getUuid();
		//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
		ruleGroupExec.getCreatedBy().getRef().setName(user.getName());
		ruleGroupExecListNew.add(ruleGroupExec);
		}
		return ruleGroupExecListNew;
	}
	public List<RuleGroupExec> findRuleGroupExecByRuleGroup(String ruleGroupUuid, String ruleGroupVersion) throws JsonProcessingException {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		List<RuleGroupExec> ruleGroupExecList = null;
		if(appUuid != null)	
			ruleGroupExecList = iRuleGroupExecDao.findRuleGroupExecByRuleGroup(ruleGroupUuid, ruleGroupVersion);
		else
			ruleGroupExecList = iRuleGroupExecDao.findRuleGroupExecByRuleGroup(ruleGroupUuid, ruleGroupVersion);
		return resolveName(ruleGroupExecList);
	}	

	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//RuleGroupExec ruleGroupExec = iRuleGroupExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		RuleGroupExec ruleGroupExec = (RuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.rulegroupExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.rulegroup);
		mi.setUuid(ruleGroupExec.getDependsOn().getRef().getUuid());
		mi.setVersion(ruleGroupExec.getDependsOn().getRef().getVersion());
		return mi;
	}

//	public void PAUSE (String uuid, String version) {
//		MetaIdentifier ruleGroupExecIdentifier = new MetaIdentifier(MetaType.rulegroupExec, uuid, version);
//		RuleGroupExec ruleGroupExec = (RuleGroupExec) daoRegister.getRefObject(ruleGroupExecIdentifier);
//		if (ruleGroupExec == null) {
//			logger.info("RuleGroupExec not found. Exiting...");
//			return;
//		}
//		
//		if (!Helper.getLatestStatus(ruleGroupExec.getStatus()).equals(new Status(Status.Stage.PENDING, new Date()))) {
//			logger.info("Latest Status is not in PENDING. Exiting...");
//			return;
//		}
//		try {
//			for (MetaIdentifierHolder ruleExecHolder : ruleGroupExec.getRuleExecList()) {
//				RuleExec ruleExec = (RuleExec) daoRegister.getRefObject(ruleExecHolder.getRef());
//				if (ruleExec == null) {
//					continue;
//				}
//				ruleExecServiceImpl.PAUSE (ruleExec.getUuid(), ruleExec.getVersion());
//			}
//			commonServiceImpl.setMetaStatus(ruleGroupExec, MetaType.rulegroupExec, Status.Stage.PAUSE);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public void RESUME (String uuid, String version) {
//		MetaIdentifier ruleGroupExecIdentifier = new MetaIdentifier(MetaType.rulegroupExec, uuid, version);
//		RuleGroupExec ruleGroupExec = (RuleGroupExec) daoRegister.getRefObject(ruleGroupExecIdentifier);
//		if (ruleGroupExec == null) {
//			logger.info("RuleGroupExec not found. Exiting...");
//			return;
//		}
//				
//		if (!Helper.getLatestStatus(ruleGroupExec.getStatus()).equals(new Status(Status.Stage.PAUSE, new Date()))) {
//			logger.info("Latest Status is not in PAUSE. Exiting...");
//			return;
//		}
//
//		try {
//			for (MetaIdentifierHolder ruleExecHolder : ruleGroupExec.getRuleExecList()) {
//				RuleExec ruleExec = (RuleExec) daoRegister.getRefObject(ruleExecHolder.getRef());
//				if (ruleExec == null) {
//					continue;
//				}
//				ruleExecServiceImpl.RESUME (ruleExec.getUuid(), ruleExec.getVersion());
//			}
//			commonServiceImpl.setMetaStatus(ruleGroupExec, MetaType.rulegroupExec, Status.Stage.RESUME);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}	
//	
	/**
	 * Kill group
	 * @param uuid
	 * @param version
	 */
	/*public void kill (String uuid, String version) {
		boolean killComplete = false;
		MetaIdentifier ruleGroupExecIdentifier = new MetaIdentifier(MetaType.rulegroupExec, uuid, version);
		RuleGroupExec ruleGroupExec = (RuleGroupExec) daoRegister.getRefObject(ruleGroupExecIdentifier);
		if (ruleGroupExec == null) {
			logger.info("RuleGroupExec not found. Exiting...");
			return;
		}
		
		if (!Helper.getLatestStatus(ruleGroupExec.getStatusList()).equals(new Status(Status.Stage.RUNNING, new Date()))) {
			logger.info("Latest Status is not in RUNNING. Exiting...");
			return;			
		}
		try {
			synchronized (ruleGroupExec.getUuid()) {
				commonServiceImpl.setMetaStatus(ruleGroupExec, MetaType.rulegroupExec, Status.Stage.TERMINATING);
			}
			
			FutureTask futureTask = (FutureTask) taskThreadMap.get(MetaType.rulegroupExec+"_"+ruleGroupExec.getUuid()+"_"+ruleGroupExec.getVersion());
			try {
				if (futureTask != null && !futureTask.isDone() && !futureTask.isCancelled()) {
					futureTask.cancel(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			taskThreadMap.remove(MetaType.rulegroupExec+"_"+ruleGroupExec.getUuid()+"_"+ruleGroupExec.getVersion());
			
			while (!killComplete) {
				killComplete = true;
				for (MetaIdentifierHolder ruleExecHolder : ruleGroupExec.getExecList()) {
					RuleExec ruleExec = (RuleExec) daoRegister.getRefObject(ruleExecHolder.getRef());
					if (ruleExec == null) {
						continue;
					}
					ruleExecServiceImpl.kill (ruleExec.getUuid(), ruleExec.getVersion());
				}
				logger.info("Check whether all rules were processed ");
				// Check whether kill is complete
				for (MetaIdentifierHolder ruleExecHolder : ruleGroupExec.getExecList()) {
					RuleExec ruleExec = (RuleExec) daoRegister.getRefObject(ruleExecHolder.getRef());
					if (ruleExec == null) {
						continue;
					}
					List<Status> statusList = ruleExec.getStatus();
					Status latestStatus = Helper.getLatestStatus(statusList);
					if (!latestStatus.getStage().equals(Status.Stage.COMPLETED) 
							&& !latestStatus.getStage().equals(Status.Stage.KILLED) 
							&& !latestStatus.getStage().equals(Status.Stage.FAILED) 
							&& !latestStatus.getStage().equals(Status.Stage.PENDING)) {
						killComplete = false;
						Thread.sleep(5000);
						break;
					}
				}
			}	// While Not killComplete
			logger.info("Rules kill COMPLETED >>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
			
			ruleGroupExec = findOneByUuidAndVersion(ruleGroupExec.getUuid(), ruleGroupExec.getVersion());
			Status.Stage latestStatus = Helper.getLatestStatus(ruleGroupExec.getStatusList()).getStage();
			Status status=commonServiceImpl.getGroupStatus(ruleGroupExec,MetaType.rulegroupExec);
			synchronized (ruleGroupExec.getUuid()) {
			 if(!Helper.getLatestStatus(ruleGroupExec.getStatusList()).equals(status.getStage())){
				 ruleGroupExec = (RuleGroupExec) commonServiceImpl.setMetaStatus(ruleGroupExec, MetaType.rulegroupExec,status.getStage());
				 
			 }
			}
			synchronized (ruleGroupExec.getUuid()) {
				commonServiceImpl.setMetaStatus(ruleGroupExec, MetaType.rulegroupExec, Status.Stage.KILLED);
			}
		} catch (Exception e) {
			logger.error("Exception while killing rule group >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
			e.printStackTrace();
		}
	}*/

	/**
	 * 
	 * @param uuid
	 * @param version
	 */
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.rulegroupExec, MetaType.ruleExec);
	}
	
}

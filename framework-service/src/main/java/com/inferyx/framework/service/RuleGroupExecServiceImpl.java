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

	/**
	 * 
	 * @param uuid
	 * @param version
	 */
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.rulegroupExec, MetaType.ruleExec);
	}
	
}

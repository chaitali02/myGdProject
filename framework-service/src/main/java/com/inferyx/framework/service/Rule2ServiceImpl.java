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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;


import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.dao.IRuleDao;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.Rule2;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.register.GraphRegister;

@Service
public class Rule2ServiceImpl extends RuleTemplate {

	@Autowired
	GraphRegister<?> registerGraph;
	/*
	 * @Autowired JavaSparkContext javaSparkContext;
	 */
	@Autowired
	IRuleDao iRuleDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	MapServiceImpl mapServiceImpl;
	@Autowired
	ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	FilterServiceImpl filterServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	HDFSInfo hdfsInfo;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	/*
	 * @Autowired private IRuleExecDao iRuleExecDao;
	 */
	/*
	 * @Autowired private IRuleGroupExecDao iRuleGroupExecDao;
	 */
	/*
	 * @Autowired private ParamSetServiceImpl paramSetServiceImpl;
	 */
	@Autowired
	RuleExecServiceImpl ruleExecServiceImpl;
	@Autowired
	ConnectionFactory connFactory;
	@Autowired
	MessageServiceImpl messageServiceImpl;

	Map<String, List<Map<String, Object>>> requestMap = new HashMap<>();


	static final Logger logger = Logger.getLogger(Rule2ServiceImpl.class);


	

	



	public RuleExec create(String rule2UUID, String rule2Version, RuleExec ruleExec,
			java.util.Map<String, MetaIdentifier> refKeyMap, ExecParams execParams, List<String> datapodList,
			DagExec dagExec) throws Exception {
		try {			
			ruleExec = (RuleExec) super.create(rule2UUID, rule2Version, MetaType.rule2, MetaType.ruleExec, ruleExec,
					refKeyMap, datapodList, dagExec);
			if(execParams != null) {
				ruleExec.setExecParams(execParams);
				commonServiceImpl.save(MetaType.ruleExec.toString(), ruleExec);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create Business Rule.", dependsOn);
			throw new Exception((message != null) ? message : "Can not create Business Rule.");
		}
		return ruleExec;
	}








	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}








	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}








	








	public RuleExec execute(ThreadPoolTaskExecutor metaExecutor, RuleExec ruleExec,
			List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
		logger.info("Inside ruleServiceImpl.execute");
		try {
			ruleExec = (RuleExec) super.execute(MetaType.rule2, MetaType.ruleExec, metaExecutor, ruleExec,
					ruleExec.getDependsOn().getRef(), taskList, execParams, runMode);
		} catch (Exception e) {
			synchronized (ruleExec.getUuid()) {
				commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.FAILED);
			}
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Business Rule execution FAILED.", dependsOn);
			throw new Exception((message != null) ? message : "Business Rule execution FAILED.");
		}
		return ruleExec;
	}
	@Override
	public RuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {
		logger.info("Inside ruleServiceImpl.parse");
		Rule2 rule2 = null;
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		// List<Status> statusList = null;
		RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
				MetaType.ruleExec.toString(), "N");
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.STARTING);
		}
		// rule = iRuleDao.findLatestByUuid(ruleExec.getDependsOn().getRef().getUuid(),
		// new Sort(Sort.Direction.DESC, "version"));
		rule2 = (Rule2) commonServiceImpl.getLatestByUuid(ruleExec.getDependsOn().getRef().getUuid(),
				MetaType.rule2.toString(), "N");
		
		//set sql
		ruleExec.setExec(null);
		if(rule2.getParamList() != null) {
			MetaIdentifier mi = rule2.getParamList().getRef();
			ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(mi.getUuid(), mi.getVersion(), mi.getType().toString(), "N");
			usedRefKeySet.add(new MetaIdentifier(MetaType.paramlist, paramList.getUuid(), paramList.getVersion()));
		}
		ruleExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		logger.info("sql_generated: " + ruleExec.getExec());
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.READY);
		}
		synchronized (ruleExec.getUuid()) {
//			RuleExec ruleExec1 = (RuleExec) daoRegister.getRefObject(new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion()));
			RuleExec ruleExec1 = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExec.getUuid(), ruleExec.getVersion(), MetaType.ruleExec.toString(), "N");
			ruleExec1.setExec(ruleExec.getExec());
			ruleExec1.setRefKeyList(ruleExec.getRefKeyList());
			// iRuleExecDao.save(ruleExec1);
			commonServiceImpl.save(MetaType.ruleExec.toString(), ruleExec1);
			ruleExec1 = null;
		}
		
		return ruleExec;
	}








	@Override
	public BaseRuleExec execute(ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec,
			MetaIdentifier datapodKey, List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	

}
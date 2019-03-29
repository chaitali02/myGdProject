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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.dao.IRuleGroupDao;
import com.inferyx.framework.dao.IRuleGroupExecDao;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.register.GraphRegister;

@Service
public class RuleGroupServiceImpl extends RuleGroupTemplate {
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IRuleGroupDao iRuleGroupDao;
	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired 
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	protected RuleServiceImpl ruleServiceImpl;
	@Autowired
	protected RuleExecServiceImpl ruleExecServiceImpl;
	@Autowired
	protected IRuleGroupExecDao iRuleGroupExecDao;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	@Autowired
	ThreadPoolTaskExecutor metaGroupExecutor;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired 
	RuleGroupExecServiceImpl ruleGroupExecServiceImpl ;
	@Resource(name="taskThreadMap")
	ConcurrentHashMap taskThreadMap;
	
	static final Logger logger = Logger.getLogger(RuleGroupServiceImpl.class);



	public RuleGroupExec create(String ruleGroupUUID, 
										String ruleGroupVersion, 
										ExecParams execParams,
										List<String> datapodList, 
										RuleGroupExec ruleGroupExec, 
										DagExec dagExec, RunMode runmode) throws Exception {

		return (RuleGroupExec) super.create(ruleGroupUUID, ruleGroupVersion, MetaType.rulegroup, MetaType.rulegroupExec, MetaType.rule, 
							MetaType.ruleExec, execParams, datapodList, ruleGroupExec, dagExec, runmode);
	}	
	
	public MetaIdentifier execute(String ruleGroupUUID, String ruleGroupVersion, ExecParams execParams, RuleGroupExec ruleGroupExec, RunMode runMode) throws Exception {
		return super.execute(ruleGroupUUID, ruleGroupVersion, MetaType.rulegroup, MetaType.rulegroupExec, MetaType.rule, MetaType.ruleExec, execParams, ruleGroupExec, runMode);
	}

	
	
	public RuleGroupExec fetchRuleGroupExec(String ruleGroupExecUUID, String ruleGroupExecVersion) throws JsonProcessingException {
		//return iRuleGroupExecDao.findOneByUuidAndVersion(ruleGroupExecUUID, ruleGroupExecVersion);
		return (RuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(ruleGroupExecUUID, ruleGroupExecVersion, MetaType.rulegroupExec.toString());
	}

	
	public void restart(String type,String uuid,String version, RunMode runMode) throws Exception{
		//RuleGroupExec ruleGroupExec= ruleGroupExecServiceImpl.findOneByUuidAndVersion(uuid, version);
		RuleGroupExec ruleGroupExec = (RuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.rulegroupExec.toString());
//		try {
//			ruleGroupExec = create(ruleGroupExec.getDependsOn().getRef().getUuid(), ruleGroupExec.getDependsOn().getRef().getVersion(), null, null, ruleGroupExec, null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		ruleGroupExec = parse(ruleGroupExec.getUuid(), ruleGroupExec.getVersion(), null, null, null, null, runMode);
		execute(ruleGroupExec.getDependsOn().getRef().getUuid(), ruleGroupExec.getDependsOn().getRef().getVersion(), null,ruleGroupExec, runMode);
		
	}
	
	/**
	 * 
	 * @param baseExec
	 * @return
	 * @throws Exception
	 */
	public Status restart(BaseExec baseExec) throws Exception {
		try {
			return super.restart(baseExec.getUuid(), baseExec.getVersion(), MetaType.rulegroupExec, MetaType.ruleExec);
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	public RuleGroupExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode)
			throws Exception {
		return (RuleGroupExec) super.parse(execUuid, execVersion, MetaType.rulegroup, MetaType.rulegroupExec, MetaType.rule, MetaType.ruleExec, refKeyMap, otherParams, datapodList, dagExec, runMode);
	}

	/**
	 * Override Executable.execute()
	 */
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		execute(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), execParams, (RuleGroupExec) baseExec, runMode);
		return null;
	}

	/**
	 * Override Parsable.parse()
	 */
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(), MetaType.rulegroup, MetaType.rulegroupExec, MetaType.rule, MetaType.ruleExec, 
				DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), null, null, null, runMode);
	}
}

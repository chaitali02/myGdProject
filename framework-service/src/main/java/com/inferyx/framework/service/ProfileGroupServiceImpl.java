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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.dao.IProfileGroupDao;
import com.inferyx.framework.dao.IProfileGroupExecDao;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ProfileGroupServiceImpl extends RuleGroupTemplate {

	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	IProfileGroupDao iProfileGroupDao;
	@Autowired 
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired 
	ProfileServiceImpl profileServiceImpl;
	@Autowired
	IProfileGroupExecDao iProfileGroupExecDao;
	@Autowired 
	ProfileExecServiceImpl profileExecServiceImpl;
	@Autowired
	ProfileGroupExecServiceImpl profileGroupExecServiceImpl;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	@Autowired
	ThreadPoolTaskExecutor metaGroupExecutor;
	@Autowired
	RegisterService registerService;

	static final Logger logger = Logger.getLogger(ProfileGroupServiceImpl.class);


	/**
	 * 
	 * @param profileGroupUUID
	 * @param profileGroupVersion
	 * @param execParams
	 * @param datapodList
	 * @param dagExec
	 * @return
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public ProfileGroupExec create(String profileGroupUUID, String profileGroupVersion, ExecParams execParams, List<String> datapodList, DagExec dagExec, RunMode runmode) throws NumberFormatException, Exception {
		return create(profileGroupUUID, profileGroupVersion, execParams, datapodList, dagExec, runmode);
	}
	
	/**
	 * 
	 * @param profileGroupUUID
	 * @param profileGroupVersion
	 * @param execParams
	 * @param datapodList
	 * @param dagExec
	 * @param profileGroupExec
	 * @return
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public ProfileGroupExec create(String profileGroupUUID, String profileGroupVersion, ExecParams execParams, List<String> datapodList, DagExec dagExec, ProfileGroupExec profileGroupExec, RunMode runmode) throws NumberFormatException, Exception {
		return (ProfileGroupExec) super.create(profileGroupUUID, profileGroupVersion, MetaType.profilegroup, MetaType.profilegroupExec, MetaType.profile, MetaType.profileExec, execParams, datapodList, profileGroupExec, dagExec, runmode);
	}
	
	/**
	 * 
	 * @param profileGroupUUID
	 * @param profileGroupVersion
	 * @param execParams
	 * @param profileGroupExec
	 * @return
	 * @throws Exception
	 */
	public MetaIdentifier execute(String profileGroupUUID, String profileGroupVersion,ExecParams execParams, ProfileGroupExec profileGroupExec, RunMode runMode) throws Exception {
		return super.execute(profileGroupUUID, profileGroupVersion, MetaType.profilegroup, MetaType.profilegroupExec, MetaType.profile, MetaType.profileExec, execParams, profileGroupExec, runMode);
	}
	
	/**
	 * 
	 * @param execUuid
	 * @param execVersion
	 * @param refKeyMap
	 * @return
	 * @throws Exception
	 */
	public BaseRuleGroupExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {
		return super.parse(execUuid, execVersion, MetaType.profilegroup, MetaType.profilegroupExec, MetaType.profile, MetaType.profileExec, refKeyMap, otherParams, datapodList, dagExec, runMode);
	}
	
	/**
	 * 
	 * @param type
	 * @param uuid
	 * @param version
	 * @throws Exception
	 */
	public void restart(String type,String uuid,String version, RunMode runMode) throws Exception{
		//ProfileGroupExec profileGroupExec= profileGroupExecServiceImpl.findOneByUuidAndVersion(uuid, version);
		ProfileGroupExec profileGroupExec = (ProfileGroupExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.profilegroupExec.toString());
		profileGroupExec = (ProfileGroupExec) parse(profileGroupExec.getUuid(), profileGroupExec.getVersion(), null, null, null, null, runMode);
		execute(profileGroupExec.getDependsOn().getRef().getUuid(), profileGroupExec.getDependsOn().getRef().getVersion(),null, profileGroupExec, runMode);
	}
	
	/**
	 * 
	 * @param baseExec
	 * @return
	 * @throws Exception
	 */
	public Status restart(BaseExec baseExec) throws Exception {
		try {
			return super.restart(baseExec.getUuid(), baseExec.getVersion(), MetaType.profilegroupExec, MetaType.profileExec);
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}


	/**
	 * Override Executable.execute()
	 */
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		execute(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), execParams, (ProfileGroupExec) baseExec, runMode);
		return null;
	}

	/**
	 * Override Parsable.parse()
	 */
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(), MetaType.profilegroup, MetaType.profilegroupExec, MetaType.profile, MetaType.profileExec, 
				DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), null, null, null, runMode);
	}
	
}

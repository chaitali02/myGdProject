package com.inferyx.framework.service;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ProfileGroup;
import com.inferyx.framework.domain.ProfileGroupExec;

public class RunProfileGroupServiceImpl extends RunBaseGroupService {
	
	static final Logger logger = Logger.getLogger(RunProfileGroupServiceImpl.class);
	
	public RunProfileGroupServiceImpl() {
		super();
	}

	public RunProfileGroupServiceImpl(ThreadPoolTaskExecutor metaExecutor, CommonServiceImpl<?> commonServiceImpl,
			ConcurrentHashMap taskThreadMap, String name, ProfileGroup profileGroup,
			ProfileGroupExec profileGroupExec, ProfileServiceImpl profileServiceImpl,
			ProfileExecServiceImpl profileExecServiceImpl, ProfileGroupExecServiceImpl profileGroupExecServiceImpl) {
		super();
		this.metaExecutor = metaExecutor;
		this.commonServiceImpl = commonServiceImpl;
		this.taskThreadMap = taskThreadMap;
		this.name = name;
		this.baseGroup = profileGroup;
		this.baseGroupExec = profileGroupExec;
		this.baseRuleService = profileServiceImpl;
		this.ruleType = MetaType.profile;
		this.execType = MetaType.profileExec;
		this.groupExecType = MetaType.profilegroupExec;
	}
}

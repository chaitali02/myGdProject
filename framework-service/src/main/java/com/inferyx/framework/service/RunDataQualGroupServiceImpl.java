package com.inferyx.framework.service;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.inferyx.framework.domain.DataQualGroup;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.MetaType;

public class RunDataQualGroupServiceImpl extends RunBaseGroupService {
	
	static final Logger logger = Logger.getLogger(RunDataQualGroupServiceImpl.class);
	
	public RunDataQualGroupServiceImpl() {
		super();
	}

	public RunDataQualGroupServiceImpl(ThreadPoolTaskExecutor metaExecutor, CommonServiceImpl<?> commonServiceImpl,
			ConcurrentHashMap taskThreadMap, String name, DataQualGroup dqGroup, DataQualGroupExec dqGroupExec,
			RuleServiceImpl ruleServiceImpl) {
		super();
		this.metaExecutor = metaExecutor;
		this.commonServiceImpl = commonServiceImpl;
		this.taskThreadMap = taskThreadMap;
		this.name = name;
		this.baseGroup = dqGroup;
		this.baseGroupExec = dqGroupExec;
		this.baseRuleService = ruleServiceImpl;
		this.ruleType = MetaType.dq;
		this.execType = MetaType.dqExec;
		this.groupExecType = MetaType.dqgroupExec;
	}

	public RunDataQualGroupServiceImpl(ThreadPoolTaskExecutor metaExecutor, CommonServiceImpl<?> commonServiceImpl,
			ConcurrentHashMap taskThreadMap, String name, DataQualGroup dataQualGroup,
			DataQualGroupExec dataQualGroupExec, DataQualServiceImpl dataQualServiceImpl,
			DataQualExecServiceImpl dataQualExecServiceImpl,
			DataQualGroupExecServiceImpl dataQualGroupExecServiceImpl) {
		super();
		this.metaExecutor = metaExecutor;
		this.commonServiceImpl = commonServiceImpl;
		this.taskThreadMap = taskThreadMap;
		this.name = name;
		this.baseGroup = dataQualGroup;
		this.baseGroupExec = dataQualGroupExec;
		this.baseRuleService = dataQualServiceImpl;
		this.ruleType = MetaType.dq;
		this.execType = MetaType.dqExec;
		this.groupExecType = MetaType.dqgroupExec;
	}

}

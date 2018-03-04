package com.inferyx.framework.service;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.RuleGroup;
import com.inferyx.framework.domain.RuleGroupExec;


public class RunRuleGroupServiceImpl extends RunBaseGroupService {
	
	static final Logger logger = Logger.getLogger(RunRuleGroupServiceImpl.class);
	
	
	public RunRuleGroupServiceImpl() {
		super();
		this.ruleType = MetaType.rule;
		this.execType = MetaType.ruleExec;
		this.groupExecType = MetaType.rulegroupExec;
	}

	public RunRuleGroupServiceImpl(ThreadPoolTaskExecutor metaExecutor, CommonServiceImpl<?> commonServiceImpl,
			ConcurrentHashMap taskThreadMap, String name, RuleGroup ruleGroup, RuleGroupExec ruleGroupExec,
			RuleServiceImpl ruleServiceImpl) {
		super();
		this.metaExecutor = metaExecutor;
		this.commonServiceImpl = commonServiceImpl;
		this.taskThreadMap = taskThreadMap;
		this.name = name;
		this.baseGroup = ruleGroup;
		this.baseGroupExec = ruleGroupExec;
		this.baseRuleService = ruleServiceImpl;
		this.ruleType = MetaType.rule;
		this.execType = MetaType.ruleExec;
		this.groupExecType = MetaType.rulegroupExec;
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}

}

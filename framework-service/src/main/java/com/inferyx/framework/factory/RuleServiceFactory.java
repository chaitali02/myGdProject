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
package com.inferyx.framework.factory;

import org.apache.hadoop.hive.ql.parse.PrunedPartitionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.service.BaseGroupExecTemplate;
import com.inferyx.framework.service.BaseRuleExecTemplate;
import com.inferyx.framework.service.DataQualExecServiceImpl;
import com.inferyx.framework.service.DataQualGroupExecServiceImpl;
import com.inferyx.framework.service.DataQualGroupServiceImpl;
import com.inferyx.framework.service.DataQualServiceImpl;
import com.inferyx.framework.service.ProfileExecServiceImpl;
import com.inferyx.framework.service.ProfileGroupExecServiceImpl;
import com.inferyx.framework.service.ProfileGroupServiceImpl;
import com.inferyx.framework.service.ProfileServiceImpl;
import com.inferyx.framework.service.ReconExecServiceImpl;
import com.inferyx.framework.service.ReconGroupExecServiceImpl;
import com.inferyx.framework.service.ReconGroupServiceImpl;
import com.inferyx.framework.service.ReconServiceImpl;
import com.inferyx.framework.service.RuleExecServiceImpl;
import com.inferyx.framework.service.RuleGroupExecServiceImpl;
import com.inferyx.framework.service.RuleGroupServiceImpl;
import com.inferyx.framework.service.RuleGroupTemplate;
import com.inferyx.framework.service.RuleServiceImpl;
import com.inferyx.framework.service.RuleTemplate;

/**
 * @author joy
 *
 */
@Service
public class RuleServiceFactory {
	
	@Autowired
	RuleServiceImpl ruleServiceImpl;
	@Autowired
	RuleExecServiceImpl ruleExecServiceImpl;
	@Autowired
	RuleGroupServiceImpl ruleGroupServiceImpl;
	@Autowired
	RuleGroupExecServiceImpl ruleGroupExecServiceImpl;
	@Autowired
	DataQualServiceImpl dataQualServiceImpl;
	@Autowired
	DataQualExecServiceImpl dataQualExecServiceImpl;
	@Autowired
	DataQualGroupServiceImpl dataQualGroupServiceImpl;
	@Autowired
	DataQualGroupExecServiceImpl dataQualGroupExecServiceImpl;
	@Autowired
	ProfileServiceImpl profileServiceImpl;
	@Autowired
	ProfileExecServiceImpl profileExecServiceImpl;
	@Autowired
	ProfileGroupServiceImpl profileGroupServiceImpl;
	@Autowired
	ProfileGroupExecServiceImpl profileGroupExecServiceImpl;
	@Autowired
	ReconServiceImpl reconServiceImpl;
	@Autowired
	ReconExecServiceImpl reconExecServiceImpl;
	@Autowired
	ReconGroupServiceImpl reconGroupServiceImpl;
	@Autowired
	ReconGroupExecServiceImpl reconGroupExecServiceImpl;
	
	/**
	 * 
	 */
	public RuleServiceFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public RuleTemplate getRuleService(MetaType type) {
		if (type == null) {
			return null;
		} 
		if (type == MetaType.rule) {
			return ruleServiceImpl;
		}
		if (type == MetaType.dq) {
			return dataQualServiceImpl;
		}
		if (type == MetaType.profile) {
			return profileServiceImpl;
		}
		if (type == MetaType.recon) {
			return reconServiceImpl;
		}
		return null;
	}
	
	public BaseRuleExecTemplate getRuleExecService(MetaType type) {
		if (type == null) {
			return null;
		} 
		if (type == MetaType.ruleExec) {
			return ruleExecServiceImpl;
		}
		if (type == MetaType.dqExec) {
			return dataQualExecServiceImpl;
		}
		if (type == MetaType.profileExec) {
			return profileExecServiceImpl;
		}
		if (type == MetaType.reconExec) {
			return reconExecServiceImpl;
		}
		return null;
	}
	
	public RuleGroupTemplate getGroupService(MetaType type) {
		if (type == null) {
			return null;
		} 
		if (type == MetaType.rulegroup) {
			return ruleGroupServiceImpl;
		}
		if (type == MetaType.dqgroup) {
			return dataQualGroupServiceImpl;
		}
		if (type == MetaType.profilegroup) {
			return profileGroupServiceImpl;
		}
		if (type == MetaType.recongroup) {
			return reconGroupServiceImpl;
		}
		
		return null;
	}
	
	public BaseGroupExecTemplate getGroupExecService(MetaType type) {
		if (type == null) {
			return null;
		} 
		if (type == MetaType.rulegroupExec) {
			return ruleGroupExecServiceImpl;
		}
		if (type == MetaType.dqgroupExec) {
			return dataQualGroupExecServiceImpl;
		}
		if (type == MetaType.profilegroupExec) {
			return profileGroupExecServiceImpl;
		}
		if (type == MetaType.recongroupExec) {
			return reconGroupExecServiceImpl;
		}
		return null;
	}

}

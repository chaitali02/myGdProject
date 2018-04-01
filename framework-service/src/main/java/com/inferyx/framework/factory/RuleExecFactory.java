package com.inferyx.framework.factory;

import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.RuleGroupExec;

@Service
public class RuleExecFactory {
	
	public RuleExecFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public BaseRuleExec getRuleExec(MetaType type) {
		if (type == null) {
			return null;
		} 
		if (type == MetaType.ruleExec) {
			return new RuleExec();
		}
		if (type == MetaType.dqExec) {
			return new DataQualExec();
		}
		if (type == MetaType.profileExec) {
			return new ProfileExec();
		}
		if (type == MetaType.reconExec) {
			return new ReconExec();
		}
		return null;
	}

	public BaseRuleGroupExec getGroupExec(MetaType type) {
		if (type == null) {
			return null;
		} 
		if (type == MetaType.rulegroupExec) {
			return new RuleGroupExec();
		}
		if (type == MetaType.dqgroupExec) {
			return new DataQualGroupExec();
		}
		if (type == MetaType.profilegroupExec) {
			return new ProfileGroupExec();
		}
		if (type == MetaType.recongroupExec) {
			return new ReconGroupExec();
		}
		return null;
	}
}

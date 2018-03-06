package com.inferyx.framework.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ruleexec")
public class RuleExec extends BaseRuleExec {
	private ExecParams execParams;	
	private MetaIdentifier paramSet;
	
	public ExecParams getExecParams() {
		return execParams;
	}
	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}
	public MetaIdentifier getParamSet() {
		return paramSet;
	}
	public void setParamSet(MetaIdentifier paramSet) {
		this.paramSet = paramSet;
	}
		
}
 
package com.inferyx.framework.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dqexec")
public class DataQualExec extends BaseRuleExec {
	
	private ExecParams execParams;
	
	public ExecParams getExecParams() {
		return execParams;
	}
	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}
	
}

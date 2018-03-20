package com.inferyx.framework.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="profileexec")
public class ProfileExec extends BaseRuleExec {
	
	private ExecParams execParams;

	/**
	 * @return the execParams
	 */
	public ExecParams getExecParams() {
		return execParams;
	}

	/**
	 * @param execParams the execParams to set
	 */
	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}	
	
}

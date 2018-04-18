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
 
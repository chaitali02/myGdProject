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
package com.inferyx.framework.operator;

import java.util.HashMap;
import java.util.Set;

import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.OperatorType;
import com.inferyx.framework.enums.RunMode;

public interface Operator {
	
	public String execute(OperatorType operatorType, 
			ExecParams execParams, 
			MetaIdentifier execIdentifier, 
			java.util.Map<String, MetaIdentifier> refKeyMap, 
			HashMap<String, String> otherParams, 
			Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception;
	
}

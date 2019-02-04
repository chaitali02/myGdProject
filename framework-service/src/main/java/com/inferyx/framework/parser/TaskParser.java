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
package com.inferyx.framework.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.Stage;
import com.inferyx.framework.domain.TaskExec;
import com.inferyx.framework.enums.RunMode;

@Component
public class TaskParser {
	
	
	public static MetaIdentifier populateRefVersion(MetaIdentifier ref, java.util.Map<String, MetaIdentifier> refKeyMap) {
		if (ref == null || refKeyMap == null 
				|| !(refKeyMap.containsKey(ref.getType() + "_" + ref.getUuid())) 
				||  ((refKeyMap.containsKey(ref.getType() + "_" + ref.getUuid()) 
						&& refKeyMap.get(ref.getType() + "_" + ref.getUuid()).getVersion() == null))) {
			return ref;
		}
			return  refKeyMap.get(ref.getType() + "_" + ref.getUuid());
	}

	public StringBuilder parseTask(DagExec dagExec, Stage stage, TaskExec indvExecTask, List<String> datapodList,
			ExecParams execParams, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, RunMode runMode)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}

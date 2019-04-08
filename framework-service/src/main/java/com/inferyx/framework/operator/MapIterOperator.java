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
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.enums.RunMode;

@Component
public class MapIterOperator extends MapOperator{
	
	public String generateSql(Map map, java.util.Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, HashMap<String, Object> operatorParams, ExecParams execParams, Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
		StringBuilder builder = new StringBuilder();
		StringBuilder finalBuilder = new StringBuilder();
		StringBuilder selectScopeBuilder = new StringBuilder();
		String modifiedKey = null;
		int iterParamStart = 0;
		int iterParamEnd = 0;

		String replacedString = null;
		int i = 0;
		// Map<String, Object> operatorParams = new HashMap<>();
		/*
		 * operatorParams.put("$iterAlias", "marketcap");
		 * operatorParams.put("$on_balance|ead", "on_balance|ead");
		 */

		if (otherParams == null) {
			otherParams = new HashMap<>();
		}

		if (operatorParams.containsKey("IterNumStart")) {
			iterParamStart = (int) operatorParams.get("IterNumStart");
		}
		if (operatorParams.containsKey("IterNumEnd")) {
			iterParamEnd = (int) operatorParams.get("IterNumEnd");
		}

		for (i = iterParamStart; i <= iterParamEnd; i++) {
			if (i == iterParamStart) {
				builder = builder.append("WITH iter").append(i).append(" AS (")
						.append(super.generateSql(map, refKeyMap, otherParams, execParams, usedRefKeySet, runMode, new HashMap<String, String>()))
						.append(")");
				selectScopeBuilder = selectScopeBuilder.append(iterParamStart);

				finalBuilder.append(StringUtils.replace(builder.toString(), "$iterVal", new Integer(i).toString()));

				continue;
			}
			otherParams.put("iterStep", "TRUE");
			builder = new StringBuilder();
			builder = builder.append("\n, iter").append(i).append(" AS (")
					.append(super.generateSql(map, refKeyMap, otherParams, execParams, usedRefKeySet, runMode, new HashMap<String, String>()))
					.append(") ");
			selectScopeBuilder = selectScopeBuilder.append(",").append(i);

			replacedString = StringUtils.replace(builder.toString(), "$iterVal", new Integer(i).toString());
			replacedString = StringUtils.replace(replacedString, "FROM", "FROM iter" + (i-1) + " ");
			replacedString = replacedString.replaceAll("[a-zA-Z_]+\\.", "");

			finalBuilder.append(replacedString);
		}
		builder = new StringBuilder();
		builder = builder.append("\n SELECT * FROM iter").append((i - 1));
		// Replace
		Iterator<String> iter = null;
		
		if (operatorParams != null) {
			iter = operatorParams.keySet().iterator();
			replacedString = finalBuilder.toString() + builder.toString();
			while (iter.hasNext()) {
				String key = iter.next();
				if (key.startsWith("\\$")) {
					modifiedKey = key.substring(1, key.length());
				} else {
					modifiedKey = key;
				}
				if (!key.startsWith("$") && !modifiedKey.startsWith("$")) {
					continue;
				}
				if (key.contains("|")) {
					replacedString = StringUtils.replaceOnce(replacedString, modifiedKey,
							((String) operatorParams.get(key)).split("\\|")[0]);
					replacedString = StringUtils.replace(replacedString, modifiedKey,
							((String) operatorParams.get(key)).split("\\|")[1]);
					continue;
				}
				replacedString = StringUtils.replace(replacedString, modifiedKey,
						(String) operatorParams.get(key));
			}
		}
		
		finalBuilder = new StringBuilder();
		finalBuilder.append(replacedString);

		return finalBuilder.toString();
	}

}

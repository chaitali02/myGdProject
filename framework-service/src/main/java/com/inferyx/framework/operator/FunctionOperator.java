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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.FunctionInfo;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamInfoHolder;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DatasetServiceImpl;

@Component
public class FunctionOperator {
	@Autowired
	protected DatasetServiceImpl datasetServiceImpl;
	@Autowired
	protected CommonServiceImpl<?> commonServiceImpl;
	private RunMode runMode;
	

	/**
	 * @Ganesh
	 *
	 * @return the runMode
	 */
	public RunMode getRunMode() {
		return runMode;
	}

	/**
	 * @Ganesh
	 *
	 * @param runMode the runMode to set
	 */
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

	
	public String generateSql(Function function, java.util.Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, Datasource datasource)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return generateSql(function, refKeyMap, otherParams, datasource, new ArrayList<String>());
	}
	
	public String generateSql(Function function, java.util.Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, Datasource datasource, List<String> attributeList)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {

		List<FunctionInfo> list = function.getFunctionInfo();
		String functionName = "";
		for (FunctionInfo fun : list) {
			if (datasource.getType().equalsIgnoreCase(fun.getType())) {
				if(runMode != null && runMode.equals(RunMode.BATCH) && datasource.getType().equalsIgnoreCase(ExecContext.IMPALA.toString()) && fun.getName().equalsIgnoreCase("reflect")) {
					functionName = "uuid()";
					break;
				}else
					functionName = fun.getName();
                
				List<ParamInfoHolder> paramInfoHolderList = fun.getParamInfoHolder();
				if (paramInfoHolderList != null && paramInfoHolderList.size() > 0) { // paramInfoHolderList.length()<=0
					functionName += "(";

					if (paramInfoHolderList.size() == 1) {
						ParamInfoHolder functionInfo =paramInfoHolderList.get(0);
						String concatName = "";
						if (functionInfo.getParamType().equalsIgnoreCase(MetaType.function.toString())) {
							concatName = functionInfo.getParamName().concat("()");															
						}else {
							attributeList.add(functionInfo.getParamName());
							concatName = functionInfo.getParamName();
						}
						functionName = functionName.concat(concatName);	
						functionName += ")";						
					} else {
						int count = paramInfoHolderList.size();
						for (ParamInfoHolder functionInfo : paramInfoHolderList) {
							String concatName = "";
							if (functionInfo.getParamType().equalsIgnoreCase(MetaType.function.toString())) {
								concatName = functionInfo.getParamName().concat("()");								
							} else {
								attributeList.add(functionInfo.getParamName());
								concatName = functionInfo.getParamName();								
							}
							functionName = functionName.concat(concatName);
							count--;
							if (count == 0) {
								break;
							} else
								functionName += ",";
						}
						functionName += ")";
					}
				} else {
					if(function.getInputReq().equalsIgnoreCase("N")) {
						functionName += "";
					}
				}
				break;
			} else {
				if(datasource.getType().equalsIgnoreCase(MetaType.file.toString()) && fun.getName().contains("rand"))
					functionName = fun.getName();
			}
		}
		return functionName;
	}
}
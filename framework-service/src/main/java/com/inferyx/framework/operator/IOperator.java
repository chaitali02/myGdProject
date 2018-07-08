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

import java.util.Map;

import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.enums.RunMode;

public interface IOperator extends IParsable, IExecutable{

	/**
	 * 
	 * @param baseExec
	 * @param execParams
	 * @param runMode
	 * @return
	 * @throws Exception
	 */
	BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception;
	
	/**
	 * 
	 * @param baseExec
	 * @param execParams
	 * @param runMode
	 * @return
	 * @throws Exception
	 */
	Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception;
	
}
	
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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.service.ModelServiceImpl;

/**
 * @author joy
 *
 */
@Component
public class TrainAndValidateOperator {

	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired
	private ModelServiceImpl modelServiceImpl; 

	static final Logger logger = Logger.getLogger(SparkMLOperator.class);

	/**
	 * 
	 */
	public TrainAndValidateOperator() {
		// TODO Auto-generated constructor stub
	}

	


}

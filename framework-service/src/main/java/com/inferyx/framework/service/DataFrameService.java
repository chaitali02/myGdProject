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
package com.inferyx.framework.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.factory.DataSourceFactory;

@Service
public class DataFrameService {
	@Autowired
	DataSourceFactory dataSourceFactory;
	/*@Autowired
	private HiveContext hiveContext;*/

	private static final Logger logger = Logger.getLogger(DataFrameService.class);

	
}

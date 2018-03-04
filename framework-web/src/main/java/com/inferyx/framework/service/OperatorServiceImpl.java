/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperatorServiceImpl {
	/*@Autowired
	private HiveContext hiveContext;*/
	@Autowired
    private DatapodServiceImpl datapodServiceImpl;
	
	static final Logger logger = Logger.getLogger(OperatorServiceImpl.class);
	
	/*public DataFrame matrixMulOperator(DataFrame df,HashMap<String,Object> operParams,
			OrderKey dpKey) throws JsonProcessingException{
		MatrixMulOperator mmo = new MatrixMulOperator();
		StructType schema = datapodServiceImpl.populateSchema(dpKey.getUUID(), dpKey.getVersion());
		JavaRDD<Row> finalOutput = mmo.populateNFetch(df, operParams);
		DataFrame dfTask = hiveContext.createDataFrame(finalOutput, schema);
		return dfTask;
	}*/
}

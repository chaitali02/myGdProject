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
package com.inferyx.framework.livyjob;

import org.apache.livy.Job;
import org.apache.livy.JobContext;

/**
 * @author joy
 *
 */
public class TableListJob implements Job<String[]> {

	/**
	 * 
	 */
	public TableListJob() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.cloudera.livy.Job#call(com.cloudera.livy.JobContext)
	 */
	@Override
	public String[] call(JobContext jc) throws Exception {
		return jc.hivectx().tableNames();
	}

}

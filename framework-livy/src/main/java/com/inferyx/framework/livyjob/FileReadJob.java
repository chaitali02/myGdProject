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
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * @author joy
 *
 */
public class FileReadJob implements Job<Dataset<Row>> {
	
	private String filePath;
	
	public FileReadJob(String filePath) {
		super();
		this.filePath = filePath;
	}

	/**
	 * 
	 */
	public FileReadJob() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Dataset<Row> call(JobContext jc) throws Exception {
		return jc.hivectx().read().load(filePath);
	}

}

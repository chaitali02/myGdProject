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
public class RegisterDataFrameAsTableJob implements Job<Void> {
	
	private Dataset<Row> df;
	private String tableName;
	
	/**
	 * @return the df
	 */
	public Dataset<Row> getDf() {
		return df;
	}

	/**
	 * @param df the df to set
	 */
	public void setDf(Dataset<Row> df) {
		this.df = df;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * 
	 */
	public RegisterDataFrameAsTableJob() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 */
	public RegisterDataFrameAsTableJob(Dataset<Row> df, String tableName) {
		this.df = df;
		this.tableName = tableName;
	}

	/* (non-Javadoc)
	 * @see com.cloudera.livy.Job#call(com.cloudera.livy.JobContext)
	 */
	@Override
	public Void call(JobContext jc) throws Exception {
		jc.hivectx().registerDataFrameAsTable(df, tableName);
		return null;
	}

}

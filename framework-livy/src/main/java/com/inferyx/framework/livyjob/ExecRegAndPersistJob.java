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

import org.apache.commons.lang3.StringUtils;
import org.apache.livy.Job;
import org.apache.livy.JobContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.hive.HiveContext;

/**
 * @author joy
 *
 */
public class ExecRegAndPersistJob implements Job<Long> {

	private String sql;
	private String registerTableName;
	private String filePathUrl;
	private String saveMode;

	public ExecRegAndPersistJob(String sql) {
		this.sql = sql;
	}

	/**
	 * @param sql
	 * @param registerTableName
	 */
	public ExecRegAndPersistJob(String sql, String registerTableName) {
		super();
		this.sql = sql;
		this.registerTableName = registerTableName;
	}

	/**
	 * @param sql
	 * @param registerTableName
	 * @param filePathUrl
	 */
	public ExecRegAndPersistJob(String sql, String registerTableName, String filePathUrl) {
		super();
		this.sql = sql;
		this.registerTableName = registerTableName;
		this.filePathUrl = filePathUrl;
	}

	/**
	 * @param sql
	 * @param registerTableName
	 * @param filePathUrl
	 * @param saveMode
	 */
	public ExecRegAndPersistJob(String sql, String registerTableName, String filePathUrl, String saveMode) {
		super();
		this.sql = sql;
		this.registerTableName = registerTableName;
		this.filePathUrl = filePathUrl;
		this.saveMode = saveMode;
	}

	/**
	 * 
	 */
	public ExecRegAndPersistJob() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Long call(JobContext jc) throws Exception {
		Long countRows = -1L;
		HiveContext hiveContext = jc.hivectx();
		Dataset<Row> df = hiveContext.sql(sql);
		// Register if registerTableName is present
		if (StringUtils.isNotBlank(registerTableName)) {
			df.registerTempTable(registerTableName);
		}
		countRows = df.count();
		// Persist job if filePathUrl and/or saveMode exists
		if (StringUtils.isBlank(saveMode)) {
			saveMode = "append";
		}
		if (saveMode.equalsIgnoreCase("append")) {
			df.write().mode(SaveMode.Append).parquet(filePathUrl);
		} else if (saveMode.equalsIgnoreCase("overwrite")) {
			df.write().mode(SaveMode.Overwrite).parquet(filePathUrl);
		}
		// Return count of records executed
		return countRows;
	}

}

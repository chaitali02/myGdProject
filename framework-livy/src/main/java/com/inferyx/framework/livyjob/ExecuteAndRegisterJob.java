package com.inferyx.framework.livyjob;

import org.apache.commons.lang3.StringUtils;
import org.apache.livy.Job;
import org.apache.livy.JobContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;

public class ExecuteAndRegisterJob implements Job<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7900278619662845928L;
	private String sql;
	private String registerTableName;
	
	public ExecuteAndRegisterJob(String sql) {
		this.sql = sql;
	}
	
	/**
	 * @param sql
	 * @param registerTableName
	 */
	public ExecuteAndRegisterJob(String sql, String registerTableName) {
		super();
		this.sql = sql;
		this.registerTableName = registerTableName;
	}

	public ExecuteAndRegisterJob() {}

	public Long call(JobContext jc) throws Exception {
		Long countRows = -1L;
		HiveContext hiveContext = jc.hivectx();
		Dataset<Row> df = hiveContext.sql(sql);
		 if (StringUtils.isNotBlank(registerTableName)) {
			 df.registerTempTable(registerTableName);
		 }
		 countRows = df.count();
		 return countRows;
	}
	
	

}

/**
 * 
 */
package com.inferyx.framework.livyjob;

import org.apache.livy.Job;
import org.apache.livy.JobContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * @author joy
 *
 */
public class RegisterDataFrameJob implements Job<Void> {

	private Dataset<Row> df;
	private String tableName;
	
	/**
	 * 
	 */
	public RegisterDataFrameJob() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * @param df
	 */
	public RegisterDataFrameJob(Dataset<Row> df) {
		super();
		this.df = df;
	}

	/**
	 * @param df
	 * @param tableName
	 */
	public RegisterDataFrameJob(Dataset<Row> df, String tableName) {
		super();
		this.df = df;
		this.tableName = tableName;
	}


	@Override
	public Void call(JobContext jc) throws Exception {
		df.registerTempTable(tableName);
		return null;
	}

}

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

/**
 * 
 */
package com.inferyx.framework.livyjob;

import org.apache.livy.Job;
import org.apache.livy.JobContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.storage.StorageLevel;

/**
 * @author joy
 *
 */
public class PersistJob implements Job<Void> {
	
	private Dataset<Row> df;
	private StorageLevel storageLevel;

	/**
	 * 
	 */
	public PersistJob() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param df
	 */
	public PersistJob(Dataset<Row> df, StorageLevel storageLevel) {
		super();
		this.df = df;
		this.storageLevel = storageLevel;
	}



	/* (non-Javadoc)
	 * @see com.cloudera.livy.Job#call(com.cloudera.livy.JobContext)
	 */
	@Override
	public Void call(JobContext jc) throws Exception {
		df.persist(storageLevel);
		return null;
	}

}

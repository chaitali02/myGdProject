/**
 * 
 */
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

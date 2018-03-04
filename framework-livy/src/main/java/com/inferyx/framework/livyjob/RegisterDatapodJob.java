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
public class RegisterDatapodJob implements Job<Dataset<Row>> {
	
	private StorageLevel strorageLevel;
	private String filePath;
	private String tableName;

	/**
	 * 
	 */
	public RegisterDatapodJob() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dataFrame
	 * @param strorageLevel
	 */
	public RegisterDatapodJob(StorageLevel strorageLevel) {
		super();
		this.strorageLevel = strorageLevel;
	}
	
	/**
	 * @param dataFrame
	 * @param strorageLevel
	 * @param filePath
	 * @param tableName
	 */
	public RegisterDatapodJob(StorageLevel strorageLevel, String filePath, String tableName) {
		super();
		this.strorageLevel = strorageLevel;
		this.filePath = filePath;
		this.tableName = tableName;
	}

	@Override
	public Dataset<Row> call(JobContext jc) throws Exception {
		Dataset<Row> df = jc.hivectx().read().load(filePath);
		String []tablenameList = jc.hivectx().tableNames();
		boolean tableFound = false;
		if (tablenameList != null && tablenameList.length > 0) {
			for (String tname : tablenameList) {
				if (tname.equals(tableName)) {
					tableFound = true;
					break;
				}
			}
		}
		if (!tableFound) {
		    df.persist(StorageLevel.MEMORY_AND_DISK());
		    jc.hivectx().registerDataFrameAsTable(df, tableName);
		}
		df.printSchema();
		return df;
	}
	
	
	

}

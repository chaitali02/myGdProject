/**
 * 
 */
package com.inferyx.framework.livyjob;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.livy.Job;
import org.apache.livy.JobContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;

/**
 * @author joy
 *
 */
public class ExecuteAndResult implements Job<List<Map<String, Object>>> {
	
	private String sql;

	/**
	 * 
	 */
	public ExecuteAndResult() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param sql
	 */
	public ExecuteAndResult(String sql) {
		super();
		this.sql = sql;
	}

	@Override
	public List<Map<String, Object>> call(JobContext jc) throws Exception {
		List<Map<String, Object>> data = new ArrayList<>();
		HiveContext hiveContext = jc.hivectx();
		hiveContext.sql("show tables").show();
		Dataset<Row> dfSorted = hiveContext.sql(sql);
		Row[] rows = (Row[]) dfSorted.collect();
		String[] columns = dfSorted.columns();
		for (Row row : rows) {
			Map<String, Object> object = new LinkedHashMap<String, Object>(columns.length);
			for (String column : columns) {
				object.put(column, row.getAs(column));
			}
			data.add(object);
		}		
		return data;
	}

}

package com.inferyx.framework.domain;

import java.sql.ResultSet;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row; 

public class ResultSetHolder {
	
	private ResultType type;
	private ResultSet resultSet;
	private Dataset<Row> dataFrame;
	private long countRows;
	
	/**
	 * @return the countRows
	 */
	public long getCountRows() {
		return countRows;
	}
	/**
	 * @param countRows the countRows to set
	 */
	public void setCountRows(long countRows) {
		this.countRows = countRows;
	}
	/**
	 * @return the type
	 */
	public ResultType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(ResultType type) {
		this.type = type;
	}
	public ResultSet getResultSet() {
		return resultSet;
	}
	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}
	public Dataset<Row> getDataFrame() {
		return dataFrame;
	}
	public void setDataFrame(Dataset<Row> dataFrame) {
		this.dataFrame = dataFrame;
	}
	

}

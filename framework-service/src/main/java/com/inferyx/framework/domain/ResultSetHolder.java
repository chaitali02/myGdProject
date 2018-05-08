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
package com.inferyx.framework.domain;

import java.sql.ResultSet;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row; 

public class ResultSetHolder {
	
	private ResultType type;
	private ResultSet resultSet;
	private Dataset<Row> dataFrame;
	private long countRows;
	private String tableName;
	
	/**
	 * @Ganesh
	 *
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @Ganesh
	 *
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
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

package com.inferyx.framework.domain;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DataFrameHolder 
{
	String tableName;
	Dataset<Row> dataframe;
	
	public String getTableName() 
	{
		return tableName;
	}
	public void setTableName(String tableName) 
	{
		this.tableName = tableName;
	}
	public Dataset<Row> getDataframe() 
	{
		return dataframe;
	}
	public void setDataframe(Dataset<Row> dataframe) 
	{
		this.dataframe = dataframe;
	}
	
}

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

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
package com.inferyx.framework.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.connector.HiveConnector;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.connector.ImpalaConnector;
import com.inferyx.framework.connector.MySqlConnector;
import com.inferyx.framework.connector.OracleConnector;
import com.inferyx.framework.connector.PostGresConnector;
import com.inferyx.framework.connector.PythonConnector;
import com.inferyx.framework.connector.RConnector;
import com.inferyx.framework.connector.S3Connector;
import com.inferyx.framework.connector.SparkConnector;

@Service
public class ConnectionFactory {

	@Autowired 
	HiveConnector hiveConnector;
	@Autowired
	SparkConnector sparkConnector;
	@Autowired
	ImpalaConnector impalaConnector;
	@Autowired
	OracleConnector oracleConnector;
	@Autowired
	MySqlConnector mySqlConnector;
	@Autowired
	RConnector rConnector;
	@Autowired
	PythonConnector pythonConnector;
	@Autowired
	PostGresConnector postGresConnector;
	@Autowired
	S3Connector s3Connector;
	
	public IConnector getConnector(String connection)
	{
		switch(connection.toLowerCase())
		{
		case "spark": return sparkConnector;
		case "hive": return hiveConnector;
		case "impala": return impalaConnector;
		case "oracle": return oracleConnector;
		case "mysql": return mySqlConnector;
		case "file": return sparkConnector;
		case "r" : return rConnector;
		case "python" : return pythonConnector;
		case "postgres" : return postGresConnector;
		case "s3" : return s3Connector;
		default:				
		}
		return null;
	}
}

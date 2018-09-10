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
package com.inferyx.framework.connector;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.common.SparkInfo;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.SecurityServiceImpl;

public class Connector implements IConnector {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SparkInfo sparkInfo;
	/*@Autowired
	HiveContext hiveContext;*/
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	SparkSession sparkSession;
	
	@Override
	public ConnectionHolder getConnection() throws IOException {
		ConnectionHolder connHolder = new ConnectionHolder();
		try{
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			Class.forName(datasource.getDriver());
			 Connection con = null;
			 String preUrl = null;
			 switch(datasource.getType().toLowerCase()) {
				case "hive":  preUrl = "jdbc:hive2://";
				  			  break;
				case "impala": preUrl = "jdbc:impala://";
				  			   break;
				case "mysql": preUrl = "jdbc:mysql://";
				  			  break;
				case "oracle": preUrl = "jdbc:oracle:thin:@";
				  			   break;
				default:
			}
			try {
				con = DriverManager.getConnection(preUrl+datasource.getHost()+":"
															+datasource.getPort()
															+(datasource.getType().toLowerCase().equalsIgnoreCase("oracle") ? ":" : "/")
															+datasource.getDbname(),
															 datasource.getUsername(),
															 datasource.getPassword());
			} catch (SQLException e) {				
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			switch(datasource.getType().toLowerCase()) {
				case "spark": connHolder.setType(ExecContext.spark.toString());
							  connHolder.setStmtObject(sparkSession);
							  return connHolder;
				case "hive":  connHolder.setType(ExecContext.HIVE.toString());
							  connHolder.setStmtObject(stmt);
							  return connHolder;
				case "impala": connHolder.setType(ExecContext.IMPALA.toString());
							   connHolder.setStmtObject(stmt);
							   return connHolder;
				case "mysql": connHolder.setType(ExecContext.MYSQL.toString());
							  connHolder.setStmtObject(stmt);
							  return connHolder;
				case "oracle": connHolder.setType(ExecContext.ORACLE.toString());
							   connHolder.setStmtObject(stmt);
							   return connHolder;
				default:
			}
		}catch (ClassNotFoundException 
				| IllegalAccessException 
				| IllegalArgumentException 
				| InvocationTargetException
				| NoSuchMethodException 
				| SecurityException
				| NullPointerException
				| ParseException
				| SQLException e) {			
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return connHolder;
	}

	@Override
	public ConnectionHolder getConnection(Object input) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
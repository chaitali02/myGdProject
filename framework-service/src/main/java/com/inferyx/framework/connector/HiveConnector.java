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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;

@Component
public class HiveConnector implements IConnector {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	

	@Override
	public ConnectionHolder getConnection() throws IOException {
		ConnectionHolder conholder = new ConnectionHolder();
		try {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			Class.forName(datasource.getDriver());
			Connection con = null;
			try {
				con = DriverManager.getConnection("jdbc:hive2://" + datasource.getHost() + ":" + datasource.getPort()
						+ "/" + datasource.getDbname(), datasource.getUsername(), datasource.getPassword());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			conholder.setType(ExecContext.HIVE.toString());
			conholder.setStmtObject(stmt);
		} catch (ClassNotFoundException 
				| SQLException 
				| IllegalAccessException 
				| IllegalArgumentException
				| InvocationTargetException 
				| NoSuchMethodException 
				| SecurityException 
				| NullPointerException
				| ParseException 
				| IOException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		return conholder;
	}

	@Override
	public ConnectionHolder getConnection(Object input, Object input2) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionHolder getConnectionByDatasource(Datasource datasource) throws IOException {
		ConnectionHolder conholder = new ConnectionHolder();
		try {
			Class.forName(datasource.getDriver());
			Connection con = null;
			try {
				con = DriverManager.getConnection("jdbc:hive2://" + datasource.getHost() + ":" + datasource.getPort()
						+ "/" + datasource.getDbname(), datasource.getUsername(), datasource.getPassword());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			conholder.setType(ExecContext.HIVE.toString());
			conholder.setStmtObject(stmt);
		} catch (ClassNotFoundException 
				| SQLException 
				| IllegalArgumentException
				| SecurityException 
				| NullPointerException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		return conholder;
	}
}
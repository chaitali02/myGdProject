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
import com.inferyx.framework.service.SecurityServiceImpl;

@Component
public class OracleConnector implements IConnector {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;

	@Override
	public ConnectionHolder getConnection() throws IOException {
		ConnectionHolder connHolder = new ConnectionHolder();
		
			Datasource datasource = null;
			try {
				datasource = commonServiceImpl.getDatasourceByApp();

				Class.forName(datasource.getDriver());
				Connection con = null;
				try {
					con = DriverManager.getConnection("jdbc:oracle:thin:@" + datasource.getHost() + ":"
							+ datasource.getPort() + ":" + datasource.getSid(), datasource.getUsername(),
							datasource.getPassword());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Statement stmt = con.createStatement();
				connHolder.setType(ExecContext.ORACLE.toString());
				connHolder.setStmtObject(stmt);
			} catch (IllegalAccessException 
					| IllegalArgumentException 
					| InvocationTargetException
					| NoSuchMethodException 
					| SecurityException 
					| NullPointerException 
					| ParseException 
					| ClassNotFoundException 
					| SQLException e) {
				e.printStackTrace();
			}
		 
		return connHolder;
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
				con = DriverManager.getConnection("jdbc:oracle:thin:@" + datasource.getHost() + ":"
						+ datasource.getPort() + ":" + datasource.getSid(), datasource.getUsername(),
						datasource.getPassword());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			conholder.setType(ExecContext.ORACLE.toString());
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
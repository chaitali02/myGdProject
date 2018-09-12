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
import java.sql.*;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.SecurityServiceImpl;

public class OracleConnector implements IConnector {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;

	@Override
	public ConnectionHolder getConnection() throws IOException {
		ConnectionHolder connHolder = new ConnectionHolder();
		try {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
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
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NullPointerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return connHolder;
	}

	@Override
	public ConnectionHolder getConnection(Object input, Object input2) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
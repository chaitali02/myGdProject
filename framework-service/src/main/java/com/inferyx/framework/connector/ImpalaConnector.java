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
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

//import com.cloudera.impala.jdbc41.Driver;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.SecurityServiceImpl;

public class ImpalaConnector implements IConnector {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;

	@Override
	public ConnectionHolder getConnection() throws IOException {
		ConnectionHolder conholder = new ConnectionHolder();
		 try {
			 Datasource datasource = commonServiceImpl.getDatasourceByApp();
			 Class.forName(datasource.getDriver());
			 Connection con=null;			
			 try {
				 con = DriverManager.getConnection("jdbc:impala://"+datasource.getHost()+":"+datasource.getPort()+"/"+datasource.getDbname(), datasource.getUsername(), datasource.getPassword());
			 } catch (SQLException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 }
			 Statement stmt=con.createStatement();
			 conholder.setType(ExecContext.IMPALA.toString());
			 conholder.setStmtObject(stmt);			
		 	}catch (ClassNotFoundException | SQLException | SecurityException | NullPointerException | IllegalAccessException | IllegalArgumentException |InvocationTargetException | NoSuchMethodException | ParseException e) {
			e.printStackTrace();
		} 
		return conholder;
	}

	@Override
	public ConnectionHolder getConnection(Object input) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
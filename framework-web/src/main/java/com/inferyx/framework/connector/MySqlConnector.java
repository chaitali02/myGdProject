package com.inferyx.framework.connector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.SecurityServiceImpl;

public class MySqlConnector implements IConnector {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;

	@Override
	public ConnectionHolder getConnection() {
		ConnectionHolder conholder = new ConnectionHolder();
		
		try {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			
			Class.forName(datasource.getDriver());
			Connection con = null;
			try {
				con = DriverManager.getConnection("jdbc:mysql://" + datasource.getHost() + ":" + datasource.getPort()
						+ "/" + datasource.getDbname(), datasource.getUsername(), datasource.getPassword());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			conholder.setType(ExecContext.MYSQL.toString());
			conholder.setStmtObject(stmt);
			conholder.setConObject(con);
		} catch (ClassNotFoundException | SQLException | IllegalAccessException | IllegalArgumentException
				| ParseException | InvocationTargetException | NoSuchMethodException | SecurityException
				| NullPointerException | IOException e) {
			e.printStackTrace();
		}
		return conholder;
	}
}
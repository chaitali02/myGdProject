/**
 * 
 */
package com.inferyx.framework.connector;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author Ganesh
 *
 */
@Component
public class PostGresConnector implements IConnector {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static Logger logger = Logger.getLogger(PostGresConnector.class);
	@Override
	public ConnectionHolder getConnection() throws IOException {
		ConnectionHolder conholder = new ConnectionHolder();
		
		try {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			
			Class.forName(datasource.getDriver());
			Connection con = null;
			
			try {
				con = DriverManager.getConnection("jdbc:postgresql://" + datasource.getHost() + ":" + datasource.getPort()
						+ "/" + datasource.getDbname(), datasource.getUsername(), datasource.getPassword());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			conholder.setType(ExecContext.POSTGRES.toString());
			conholder.setStmtObject(stmt);
			conholder.setConObject(con);
		} catch (ClassNotFoundException 
				| IllegalAccessException 
				| IllegalArgumentException
				| ParseException 
				| InvocationTargetException 
				| NoSuchMethodException 
				| SecurityException
				| NullPointerException 
				| IOException 
				| SQLException e) {
			e.printStackTrace();
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
				con = DriverManager.getConnection("jdbc:postgresql://" + datasource.getHost() + ":" + datasource.getPort()
				+ "/" + datasource.getDbname(), datasource.getUsername(), datasource.getPassword());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Statement stmt = con.createStatement();
			conholder.setType(ExecContext.POSTGRES.toString());
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

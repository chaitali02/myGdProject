package com.inferyx.framework.test;

import java.sql.*;

class TestOracleConnection {
	public static void main(String args[]) {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("Driver sucess..");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@10.10.0.30:1521/framework","framework","admin");
			//"jdbc:mysql://10.10.0.30:3306/framework?autoReconnect=true&useSSL=false", "root", "root");
			// here sonoo is database name, root is username and password
			System.out.println("connection sucess..");
			Statement stmt = con.createStatement();
			System.out.println("stmt sucess..");
			ResultSet rs = stmt.executeQuery("SELECT owner, table_name FROM dba_tables where owner='FRAMEWORK'");
			//ResultSet rs = stmt.executeQuery("select data_type, column_name from all_tab_columns where owner='XE' and table_name='BANK'");
			//ResultSet rs = stmt.executeQuery("Select * from INFERYX.BANK");		
			while (rs.next())
				System.out.println(rs.getString(1)+"\t"+rs.getString(2));
			
		    System.out.println("connection close");
		    rs.close();
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
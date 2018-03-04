package com.inferyx.framework.test;

import java.sql.*;

class TestMysqlConnection {
	public static void main(String args[]) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver success");
			Connection con = DriverManager.getConnection("jdbc:mysql://10.10.0.30:3306/framework", "inferyx", "inferyx");
			System.out.println("Connection success");
			//"jdbc:mysql://10.10.0.30:3306/framework?autoReconnect=true&useSSL=false", "root", "root");
			// here sonoo is database name, root is username and password
			Statement stmt = con.createStatement();
			System.out.println("statment success");
			ResultSet rs = stmt.executeQuery("select * from branch limit 10");
			while (rs.next())
				System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
			

			 DatabaseMetaData meta = con.getMetaData();
		      ResultSet res = meta.getTables(null, null, null, 
		         new String[] {"TABLE"});
		      System.out.println("List of tables: "); 
		      while (res.next()) {
		         System.out.println(
		            "   "+res.getString("TABLE_CAT") 
		           + ", "+res.getString("TABLE_SCHEM")
		           + ", "+res.getString("TABLE_NAME")
		           + ", "+res.getString("TABLE_TYPE")
		           + ", "+res.getString("REMARKS")); 
		      }
		      res.close();

		      con.close();
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
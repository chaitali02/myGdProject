package com.inferyx.framework.test;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCExample {

	public static void main(String[] argv) {

		System.out.println("-------- PostgreSQL "
				+ "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		Connection connection = null;

		try {

			connection = DriverManager.getConnection(
					"jdbc:postgresql://10.10.0.6:5432/framework", "postgres",
					"admin");
			
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM framework.bank");
			while (rs.next())
			{
			    System.out.println(rs.getString(1));
			    System.out.println(rs.getString(2));
			    System.out.println(rs.getString(3));
			    System.out.println(rs.getString(4));
			    System.out.println(rs.getString(5));
			    
			    System.out.println(rs.getString(6));
			}
			rs.close();
			st.close();

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
	}

}
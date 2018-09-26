package com.inferyx.framework.test;

import java.sql.*;

class TestPostgreConnection {
	public static void main(String args[]) {

		final String url = "jdbc:postgresql://localhost/edw_small";
		final String user = "inferyx";
		final String password = "inferyx";

		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("Connected to the PostgreSQL server successfully.");
			
			String sql = "SELECT * FROM edw_small.account_type LIMIT 100";
			Statement statement = conn.createStatement();

			ResultSet resultSet = statement.executeQuery(sql);

			while (resultSet.next()) {
				System.out.println("Enter....");
				//System.out.printf("%-30.30s  %-30.30s%n", resultSet.getString("model"), resultSet.getString("price"));

			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

}
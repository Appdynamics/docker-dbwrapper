package com.dbwrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainController {

	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/test";
	private static final String USERNAME = "root";
//	private static String inputQuery = "CREATE TABLE `city` ("
//			+ "`ID` int(11) NOT NULL AUTO_INCREMENT,"
//			+ "`Name` char(35) NOT NULL DEFAULT '',"
//			+ "`CountryCode` char(3) NOT NULL DEFAULT '',"
//			+ "`District` char(20) NOT NULL DEFAULT '',"
//			+ "`Population` int(11) NOT NULL DEFAULT '0',"
//			+ "PRIMARY KEY (`ID`),"
//			+ "KEY `city_idx` (`Name`)"
//			+ ")";
	

	/**
	 * Creates and returns a connection to the Mysql database
	 * 
	 * @param
	 * @return conn
	 */
	public static Connection createConnection() {
		Connection conn = null;
		try {

			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USERNAME, "");
			return conn;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return conn;
	}
	
	/*public static String getInputQuery() {
		return inputQuery;
	}

	public static void setInputQuery(String inputQuery) {
		MysqlJDBCConnection.inputQuery = inputQuery;
	}*/
}

package com.dbwrapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainController {

	private static final Logger logger = LoggerFactory
			.getLogger(MainController.class);
	Properties props = new Properties();
	InputStream input = null;
	private String dbName, driver, dbUrl, username, password;
	Connection conn = null;

	public void setDBProperties(String db) {

		this.dbName = db;
		switch (dbName) {
		case "oracle":
			try {
				this.driver = "com.jdbc.driver.OracleDriver";
				this.dbUrl = "jdbc:oracle:thin:@localdocker:1521:XE";
				this.username = "system";
				this.password = "oracle";
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			break;
		case "mysql":
			try {

				this.driver = "com.mysql.jdbc.Driver";
				this.dbUrl = "jdbc:mysql://ecommerce-worlddb:3306/test";
				this.username = "root";
				this.password = "tiger";
				logger.info(this.dbName + " " + this.dbUrl + " "
						+ this.username + " " + this.password);

			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			break;

		default:
			break;
		}
	}

	/**
	 * Creates and returns a connection to the Mysql database
	 * 
	 * @param
	 * @return conn
	 */
	public Connection establishConnection() {

		try {
			Class.forName(this.driver);
			conn = DriverManager.getConnection(this.dbUrl, this.username,
					this.password);
			return conn;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return conn;
	}
}

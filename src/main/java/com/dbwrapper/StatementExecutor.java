package com.dbwrapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/execute")
public class StatementExecutor {

	Statement stmt = null;
	JSONArray result;
	String queryString = null;
	String slowQueryString = null;
	String db = null;
	private static final Logger logger = LoggerFactory
			.getLogger(StatementExecutor.class);
	MainController controller = new MainController();
	private long startTime, endTime, responseTime;


	@GET
	@Path("/join/{slow}/{dbName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String executeCartesianQuery(@PathParam("slow") boolean flag,
			@PathParam("dbName") String dbName) {

		logger.info("executing join query" + " " + flag + " " + dbName);
		queryString = "select c.name, cl.language from world.country c, world.countrylanguage cl "
				+ "where c.code = cl.countrycode and c.name = 'United States'";
		slowQueryString = "select c.name, c.capital, cl.language, cl.percentage, cu.customerid, "
				+ "cu.address from world.country c, world.countrylanguage cl, world.customers cu where c.name = 'United States'";

		// checks if the slow query flag is set and
		if (flag) {

			startTime = System.currentTimeMillis();
			result = processQuery(slowQueryString, dbName);
			endTime = System.currentTimeMillis();
			responseTime = endTime - startTime;
			logger.info("The response time is: " +responseTime);

		} else {

			startTime = System.currentTimeMillis();
			result = processQuery(queryString, dbName);
			endTime = System.currentTimeMillis();
			responseTime = endTime - startTime;
			logger.info("The response time is: " +responseTime);

		}
		return result.toString();
	}



	protected JSONArray processQuery(String query, String dbName) {
		Connection conn = null;
		ResultSet rs = null;
		Integer rsLength = 0;
		JSONArray parsedResultSet = null;
		this.db = dbName;
		ResultSetParser rsParser = new ResultSetParser();
		try {
			// set the database properties.
			controller.setDBProperties(db);
			
			// create the connection with the database/driver
			conn = controller.establishConnection();
			
			// creates statement with the open connection
			stmt = conn.createStatement();
			
			// get the result set of the executed query
			rs = stmt.executeQuery(query);
			
			//return the result set in JSON format
			parsedResultSet = rsParser.convertToJSON(rs);

		} catch (SQLException e) {
			logger.error("Sorry, an exception has occurred", e);
		} catch (Exception e) {
			logger.error("Sorry, an exception has occurred", e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.error("Sorry, an exception has occurred", e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error("Sorry, an exception has occurred", e);
				}
			}
		}
		return parsedResultSet;
	}
}

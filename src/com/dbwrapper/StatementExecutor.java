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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/execute")
public class StatementExecutor {

	Statement stmt = null;
	int return_value;
	String queryString = null;
	String slowQueryString = null;
	Logger logger = LoggerFactory.getLogger(StatementExecutor.class);

	@GET
	@Path("/getPopulation/{slow}")
	@Produces(MediaType.TEXT_HTML)
	public String executeSelectQuery(@PathParam("slow") boolean flag) {

		queryString = "select state, sum(pop) from zips group by state;";
		slowQueryString = "select * from zips where upper(city) = 'SPRINGFIELD';";
		System.out.println(flag);
		if (flag) {
			return_value = executeQuery(slowQueryString);
		} else {
			return_value = executeQuery(queryString);
		}
		return "<html> " + "<title>" + "Hello Jersey" + "</title>"
				+ "<body><h1>" + "The number of rows retrieved is: "
				+ return_value + "</h1></body>" + "</html> ";
	}

	@GET
	@Path("/getJoin/{slow}")
	@Produces(MediaType.TEXT_HTML)
	public String executeCartesianQuery(@PathParam("slow") boolean flag) {

		queryString = "select c.name, cl.language from country c, countrylanguage cl "
				+ "where c.code = cl.countrycode and c.name = 'Spain';";
		slowQueryString = "select c.name, cl.language from country c, countrylanguage cl where c.name = 'Spain';";
		System.out.println(flag);
		// checks if the slow query flag is set and
		if (flag) {
			return_value = executeQuery(slowQueryString);
		} else {
			return_value = executeQuery(queryString);
		}
		return "<html> " + "<title>" + "Executed join query" + "</title>"
				+ "<body><h1>" + "The number of rows retrieved is: "
				+ return_value + "</h1></body>" + "</html> ";
	}

	protected Integer executeQuery(String query) {
		Connection conn = null;
		ResultSet rs = null;
		Integer rsLength = 10;
		try {
			// create connection with database
			conn = MainController.createConnection();
			// creates statement with the open connection
			stmt = conn.createStatement();
			// get the result set of the executed query
			rs = stmt.executeQuery(query);
			// returns length of the result set.
			boolean b = rs.last();
			rsLength = 0;
			if (b) {
				rsLength = rs.getRow();
			}

			logger.info(rsLength.toString());
			System.out.println(rsLength);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return rsLength;
	}
}

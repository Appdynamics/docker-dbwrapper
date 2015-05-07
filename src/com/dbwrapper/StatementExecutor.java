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
	private static final Logger logger = LoggerFactory
			.getLogger(StatementExecutor.class);
	MainController controller = new MainController();

	@GET
	@Path("/population/{slow}")
	@Produces(MediaType.APPLICATION_JSON)
	public String executeSelectQuery(@PathParam("slow") boolean flag) {

		queryString = "select state, sum(pop) from zips group by state;";
		slowQueryString = "select * from zips where upper(city) = 'SPRINGFIELD';";
		if (flag) {
			result = processQuery(slowQueryString);
		} else {
			result = processQuery(queryString);
		}
		return result.toString();
	}

	@GET
	@Path("/join/{slow}")
	@Produces(MediaType.APPLICATION_JSON)
	public String executeCartesianQuery(@PathParam("slow") boolean flag) {

		queryString = "select c.name, cl.language from country c, countrylanguage cl "
				+ "where c.code = cl.countrycode and c.name = 'Spain';";
		slowQueryString = "select c.name, cl.language from country c, countrylanguage cl where c.name = 'Spain';";
		// checks if the slow query flag is set and
		if (flag) {
			result = processQuery(slowQueryString);

		} else {
			result = processQuery(queryString);
		}
		return result.toString();
	}

	@GET
	@Path("/inclause/{slow}")
	@Produces(MediaType.APPLICATION_JSON)
	public String executeInClauseQuery(@PathParam("slow") boolean flag) {

		queryString = "select c.name, cl.language from country c, countrylanguage cl "
				+ "where c.code = cl.countrycode and cl.language in ('Spanish', 'Portuguese');";

		result = processQuery(queryString);
		logger.info(result.toString());
		return result.toString();
	}

	@GET
	@Path("/simplesubquery/{slow}")
	@Produces(MediaType.APPLICATION_JSON)
	public String executeSimpleSubquery(@PathParam("slow") boolean flag) {

		queryString = "select * from city where countrycode in "
				+ "(select code from country where gnp > 1350000) "
				+ "order by population desc limit 10";

		slowQueryString = "select country_name, city_name, city_pop, (city_pop/total_pop)*100 as perc_of_country "
				+ "from (select c.name as country_name, ci.name as city_name,"
				+ " ci.population as city_pop, "
				+ "(select max(surfacearea) from country) as "
				+ "total_pop from city ci, country c where "
				+ "ci.countrycode = c.code and c.surfacearea = "
				+ "(select max(surfacearea) from country)) "
				+ "as subQ order by city_pop desc;";

		if (flag) {
			result = processQuery(queryString);
		} else {
			result = processQuery(slowQueryString);
		}
		return result.toString();
	}

	@GET
	@Path("/indexrange/{slow}")
	@Produces(MediaType.APPLICATION_JSON)
	public String executeIndexRangeQuery(@PathParam("slow") boolean flag) {

		queryString = "select distinct state from zips where lng between -70 ad -65;";
		slowQueryString = "select distinct state from zips where lng between -170 and -65;";

		if (flag) {
			result = processQuery(slowQueryString);
		} else {
			result = processQuery(queryString);
		}

		return result.toString();
	}

	protected JSONArray processQuery(String query) {
		Connection conn = null;
		ResultSet rs = null;
		Integer rsLength = 0;
		JSONArray parsedArray = null;
		ResultSetParser rsParser = new ResultSetParser();
		try {
			// create connection with database
			controller.setDBProperties("mysql");

			conn = controller.establishConnection();
			// creates statement with the open connection
			stmt = conn.createStatement();
			// get the result set of the executed query
			rs = stmt.executeQuery(query);
			parsedArray = rsParser.convertToJSON(rs);

			// returns length of the result set.
			boolean b = rs.last();
			rsLength = 0;
			if (b) {
				rsLength = rs.getRow();
			}

			logger.info(rsLength.toString());

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
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
		return parsedArray;
	}
}

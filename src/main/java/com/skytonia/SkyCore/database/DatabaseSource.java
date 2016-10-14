package com.skytonia.SkyCore.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public class DatabaseSource
{
	
	private final HikariDataSource dataSource;
	
	/**
	 * Single Execution Thread for SQL Executions
	 */
	private final ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
	
	/*
	 * Attempts to set up a database connection object with the following url:
	 * jdbc:mysql://{server}:{port}/{database}
	 *
	 * And authenticate with the provided details
	 *
	 * @param server
	 * @param port
	 * @param database
	 * @param username
	 * @param password
	 *
	 * @throws SQLException
	 */
	public DatabaseSource(String server, String port, String database,
	                      String username,
	                      String password) throws SQLException
	{
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://" + server + ":" + port + "/" + database);
		config.setUsername(username);
		config.setPassword(password);
		
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		
		dataSource = new HikariDataSource(config);
		
		//Attempt a test query
		executeQuery("SELECT 1", null, null);
	}
	
	public void executeAsync(String sqlQuery)
	{
		executeQueryAsync(sqlQuery, null, null);
	}
	
	public void execute(String sqlQuery) throws SQLException
	{
		executeQuery(sqlQuery, null, null);
	}
	
	public void executeQueryAsync(String sqlQuery, QueryAction queryAction, ResultAction resultAction)
	{
		threadExecutor.submit(() ->
		{
			try
			{
				executeQuery(sqlQuery, queryAction, resultAction);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		});
	}
	
	public void executeQuery(String sqlQuery, QueryAction queryAction, ResultAction resultAction) throws SQLException
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try
		{
			connection = getConnection();
			
			statement = connection.prepareStatement(sqlQuery);
			
			if(queryAction != null)
			{
				queryAction.query(statement);
			}
			
			resultSet = statement.executeQuery();
			
			if(resultAction != null && resultSet != null && resultSet.next())
			{
				resultAction.processResults(resultSet);
			}
		}
		finally
		{
			cleanupSQL(connection, resultSet, statement);
		}
	}
	
	/*
	 * Helper Methods
	 */
	
	public Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}
	
	public void cleanupSQL(Connection connection, ResultSet result, PreparedStatement statement)
	{
		closeSQL(statement);
		closeSQL(result);
		closeSQL(connection);
	}
	
	public static void closeSQL(AutoCloseable object)
	{
		if(object != null)
		{
			try
			{
				(object).close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
}

package com.skytonia.SkyCore.database;

import com.skytonia.SkyCore.util.BUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.Getter;

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
	
	private HikariDataSource dataSource;
	
	/**
	 * Single Execution Thread for SQL Executions
	 */
	@Getter(AccessLevel.PROTECTED)
	private final ExecutorService threadExecutor = Executors.newCachedThreadPool();
	
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
		executeQuery("SELECT 1", null, null, null);
	}
	
	/**
	 * Must be called on plugin disable to avoid connection leaks
	 */
	public void close()
	{
		dataSource.close();
		dataSource = null;
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		if(dataSource != null)
		{
			close();
		}
		
		super.finalize();
	}
	
	/**
	 * MySQL Execution API containing proper setup and cleanup for ease-of-use.
	 * Completes on this class's dedicated SQL Execution Thread.
	 *
	 * @param sqlQuery Basic SQL Query to be sent.
	 * @param queryAction QueryAction which fills in the blanks of the Statement
	 * @param postTransaction Runnable which will always run post-transaction, whether a result is returned or error occurs.
	 * @throws SQLException
	 */
	public void executeAsync(String sqlQuery, QueryAction queryAction, Runnable postTransaction)
	{
		threadExecutor.submit(() ->
		{
			try
			{
				executeQuery(sqlQuery, queryAction, null, postTransaction, QueryType.EXECUTE);
			}
			catch(SQLException e)
			{
				BUtil.logError("Unhandled SQL Exception on Query: " + sqlQuery);
				e.printStackTrace();
			}
			catch(Exception e)
			{
				BUtil.logStackTrace(e);
			}
		});
	}
	
	/**
	 * MySQL Execution API containing proper setup and cleanup for ease-of-use.
	 * Completes on the current thread.
	 *
	 * @param sqlQuery Basic SQL Query to be sent.
	 * @param queryAction QueryAction which fills in the blanks of the Statement
	 * @param postTransaction Runnable which will always run post-transaction, whether a result is returned or error occurs.
	 * @throws SQLException
	 */
	public void execute(String sqlQuery, QueryAction queryAction, Runnable postTransaction) throws SQLException
	{
		executeQuery(sqlQuery, queryAction, null, postTransaction, QueryType.EXECUTE);
	}
	
	/**
	 * MySQL Query API containing proper setup and cleanup for ease-of-use.
	 * Completes on this class's dedicated SQL Execution Thread.
	 *
	 * @param sqlQuery Basic SQL Query to be sent.
	 * @param queryAction QueryAction which fills in the blanks of the Statement
	 * @param resultAction ResultAction which is called if a ResultSet is found and contains at least 1 row
	 * @param postTransaction Runnable which will always run post-transaction, whether a result is returned or error occurs.
	 * @throws SQLException
	 */
	public void executeQueryAsync(String sqlQuery, QueryAction queryAction, ResultAction resultAction, Runnable postTransaction)
	{
		threadExecutor.submit(() ->
		{
			try
			{
				executeQuery(sqlQuery, queryAction, resultAction, postTransaction, QueryType.QUERY);
			}
			catch(SQLException e)
			{
				BUtil.logError("Unhandled SQL Exception on Query: " + sqlQuery);
				e.printStackTrace();
			}
			catch(Exception e)
			{
				BUtil.logStackTrace(e);
			}
		});
	}
	
	/**
	 * MySQL Query API containing proper setup and cleanup for ease-of-use.
	 * Completes on the current thread.
	 *
	 * @param sqlQuery Basic SQL Query to be sent.
	 * @param queryAction QueryAction which fills in the blanks of the Statement
	 * @param resultAction ResultAction which is called if a ResultSet is found and contains at least 1 row
	 * @param postTransaction Runnable which will always run post-transaction, whether a result is returned or error occurs.
	 * @throws SQLException
	 */
	public void executeQuery(String sqlQuery, QueryAction queryAction, ResultAction resultAction, Runnable postTransaction) throws SQLException
	{
		executeQuery(sqlQuery, queryAction, resultAction, postTransaction, QueryType.QUERY);
	}
	
	private void executeQuery(String sqlQuery, QueryAction queryAction, ResultAction resultAction, Runnable postTransaction, QueryType queryType) throws SQLException
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
				try
				{
					queryAction.query(statement);
				}
				catch(SQLException e)
				{
					BUtil.logError("Unhandled SQL Exception on Query: " + sqlQuery);
					e.printStackTrace();
				}
				catch(Exception e)
				{
					BUtil.logError("Unhandled Exception:");
					BUtil.logStackTrace(e);
				}
			}
			
			if(queryType == QueryType.QUERY)
			{
				resultSet = statement.executeQuery();
			}
			else //if(queryType == QueryType.EXECUTE)
			{
				statement.execute();
			}
			
			if(resultAction != null && resultSet != null && resultSet.next())
			{
				try
				{
					resultAction.processResults(resultSet);
				}
				catch(Exception e)
				{
					BUtil.logError("Unhandled SQL Exception on Query: " + sqlQuery);
					e.printStackTrace();
				}
			}
		}
		finally
		{
			cleanupSQL(connection, resultSet, statement);
			
			if(postTransaction != null)
			{
				try
				{
					postTransaction.run();
				}
				catch(Exception e)
				{
					BUtil.logStackTrace(e);
				}
			}
		}
	}
	
	/*
	 * Helper Methods
	 */
	
	/**
	 * @return An available pooled thread
	 * @throws SQLException
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

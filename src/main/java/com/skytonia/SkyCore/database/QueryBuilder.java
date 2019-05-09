package com.skytonia.SkyCore.database;

import com.skytonia.SkyCore.util.BUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

public class QueryBuilder
{

	private final DatabaseSource source;
	private Connection connection;

	private String queryString = null;
	private PreparedStatement statement = null;
	private ResultSet resultSet = null;

	private ResultAction resultAction = null;
	private Runnable completionRunnable = null;

	private boolean async = false;

	// Auto Close Timeout
	private long autoCloseTimeout;
	private long createTime;

	private Timer timer;

	// Debug Helper
	private Throwable callingTrace = null; //Assists when finding where an async task has been called from

	private boolean statementModified = false;

	public QueryBuilder(DatabaseSource source)
	{
		this.source = source;
		try
		{
			this.connection = source.getConnection();
		}
		catch(SQLException e)
		{
			BUtil.log("Failed to get connection for QueryBuilder.");
			e.printStackTrace();
		}

		timeout(60000L); //60 Seconds
	}

	public QueryBuilder timeout(long autoCloseTimeout)
	{
		this.autoCloseTimeout = autoCloseTimeout;
		this.createTime = System.currentTimeMillis(); //Reset start time

		//Reset existing cleanup timer
		if(timer != null)
		{
			timer.purge();
		}

		timer = new Timer();

		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				cleanup();
			}
		}, autoCloseTimeout);

		return this;
	}

	public QueryBuilder query(String queryString) throws SQLException
	{
		this.queryString = queryString;
		this.statement = connection.prepareStatement(queryString);

		return this;
	}

	public QueryBuilder queryAction(QueryAction queryAction) throws SQLException
	{
		queryAction.query(statement);

		statementModified = true;

		return this;
	}

	public QueryBuilder resultAction(ResultAction resultAction)
	{
		this.resultAction = resultAction;

		return this;
	}

	public QueryBuilder onComplete(Runnable completionRunnable)
	{
		this.completionRunnable = completionRunnable;

		return this;
	}

	public QueryBuilder async()
	{
		this.async = true;

		return this;
	}

	public QueryBuilder returnKeys() throws SQLException
	{
		if(statementModified)
		{
			BUtil.log("-----------------------------------------------------------------------------------");
			BUtil.log("PreparedStatement Modified and replaced by returnKeys(). Note: Modified ? variables have been reset.");
			BUtil.log("-----------------------------------------------------------------------------------");
		}

		this.statement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);

		return this;
	}

	public void execute() throws SQLException
	{
		if(connection == null)
		{
			printCallingTrace(new IllegalStateException("No Connection available to continue query: '" + queryString + "'"));
			return;
		}

		if(statement == null)
		{
			printCallingTrace(new IllegalStateException("No Statement/Query Provided!"));
			return;
		}

		if (async)
		{
			callingTrace = new Throwable();
			source.getThreadExecutor().submit(() ->
			{
				this.async = false;

				//Re-execute within this method
				try
				{
					execute();
				}
				catch(SQLException e)
				{
					printCallingTrace(e);
				}
			});
		}
		else
		{
			if(callingTrace == null)
			{
				callingTrace = new Throwable();
			}

			if(isQuery())
			{
				resultSet = statement.executeQuery();

				if(resultAction != null && resultSet != null)
				{
					while(resultSet.next())
					{
						try
						{
							resultAction.processResults(resultSet);
						}
						catch(Exception e)
						{
							printCallingTrace(e);
						}
					}
				}

				if(completionRunnable != null)
				{
					try
					{
						completionRunnable.run();
					}
					catch(Exception e)
					{
						printCallingTrace(e);
					}
				}
			}
			else
			{
				statement.execute();

				if(completionRunnable != null)
				{
					try
					{
						completionRunnable.run();
					}
					catch(Exception e)
					{
						printCallingTrace(e);
					}
				}
			}
		}
	}

	// Helper Methods (Internal)

	private boolean isQuery()
	{
		return queryString.toLowerCase().startsWith("SELECT");
	}

	private void printCallingTrace(Throwable e)
	{
		if(callingTrace == null)
		{
			callingTrace = new Throwable();
		}

		BUtil.log("SQL Query '" + queryString + "'");
		BUtil.log("Called from: ");
		callingTrace.printStackTrace();

		BUtil.log("Experienced " + e.getClass().getSimpleName());
		e.printStackTrace();
	}

	private void cleanup()
	{
		closeSQL(connection);
		closeSQL(statement);
		closeSQL(resultSet);

		connection = null;
		statement = null;
		resultSet = null;
	}

	private void closeSQL(AutoCloseable object)
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

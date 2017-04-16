package com.skytonia.SkyCore.database;

import com.skytonia.SkyCore.util.BUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by Chris Brown (OhBlihv) on 14/10/2016.
 */
@RequiredArgsConstructor
public class BatchQuery<T>
{
	
	private final String sqlQuery;
	private final BatchAction<T> batchAction;
	private final Runnable completeAction;
	private final Collection<T> batchItems;
	
	//Avoid sending all in a batch at once.
	//Space out our queries to avoid data loss from a single query/group
	@Getter @Setter
	private int batchSize = 1000;
	
	private int batchCount = 0;
	
	public void executeBatchASync(DatabaseSource databaseSource)
	{
		databaseSource.getThreadExecutor().submit(() ->
		{
			try
			{
				executeBatch(databaseSource);
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
	
	public void executeBatch(DatabaseSource databaseSource) throws SQLException
	{
		if(batchAction == null)
		{
			throw new IllegalArgumentException("No Batch Action provided, yet method requires one.");
		}
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try
		{
			connection = databaseSource.getConnection();
			
			statement = connection.prepareStatement(sqlQuery);
			
			//Allows either a collection of batch items to be parsed,
			//or the BatchAction can import its own objects to parse
			int batchCount = 0;
			if(batchItems != null)
			{
				for(T batchItem : batchItems)
				{
					try
					{
						if(!batchAction.addItemBatch(statement, batchItem))
						{
							continue;
						}
						statement.addBatch();
						
						if(++batchCount % batchSize == 0)
						{
							statement.executeBatch();
						}
					}
					catch(SQLException e)
					{
						BUtil.logError("Unhandled SQL Exception on Query: " + sqlQuery);
						e.printStackTrace();
						//continue;
					}
					catch(Exception e)
					{
						BUtil.logError("Unhandled Exception:");
						BUtil.logStackTrace(e);
						return;
					}
				}
			}
			else
			{
				//Only 'one object to iterate'.
				//None are provided, the BatchAction must provide it's own.
				try
				{
					if(batchAction.addItemBatch(statement, null))
					{
						batchCount++;
					}
				}
				catch(Exception e)
				{
					//Ignore. BatchAction must have assumed we'd provide our own objects or something.
				}
			}
			
			if(batchCount == 0)
			{
				return;
			}
			
			statement.executeBatch();
			
			if(completeAction != null)
			{
				try
				{
					completeAction.run();
				}
				catch(Exception e)
				{
					BUtil.logError("Unhandled Exception:");
					BUtil.logStackTrace(e);
				}
			}
		}
		finally
		{
			databaseSource.cleanupSQL(connection, resultSet, statement);
		}
	}
	
}

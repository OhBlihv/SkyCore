package com.skytonia.SkyCore.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Chris Brown (OhBlihv) on 14/10/2016.
 */
public interface BatchAction<T>
{
	
	/**
	 *
	 * @param statement
	 * @param batchitem
	 * @return False if a batch item was not added. True if the batch was appended
	 * @throws SQLException
	 */
	boolean addItemBatch(PreparedStatement statement, T batchitem) throws SQLException;
	
}

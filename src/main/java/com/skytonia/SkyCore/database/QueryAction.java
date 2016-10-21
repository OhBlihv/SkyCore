package com.skytonia.SkyCore.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public interface QueryAction
{
	
	void query(PreparedStatement statement) throws SQLException;
	
}

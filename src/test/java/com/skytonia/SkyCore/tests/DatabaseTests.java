package com.skytonia.SkyCore.tests;

import com.skytonia.SkyCore.database.DatabaseSource;
import com.skytonia.SkyCore.util.BUtil;
import org.junit.Test;

import java.sql.SQLException;

public class DatabaseTests
{

	@Test
	public void builderTest()
	{
		DatabaseSource source = null;

		try
		{
			source.getConnection();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		try
		{
			source.build()
				.query("SELECT `wins` FROM `reaction_wins` WHERE `uuid`=?;")
				.async()
				.queryAction((statement) ->
				{
					statement.setString(1, "UUID GOES HERE");
				})
				.resultAction((resultSet) ->
				{
					BUtil.log("Wins: " + resultSet.getString(1));
				})
				.execute();

			source.executeQueryAsync(
				"SELECT `wins` FROM `reaction_wins` WHERE `uuid`=?;",
				(statement) ->
				{
					statement.setString(1, "UUID GOES HERE");
				},
				(resultSet) ->
				{
					BUtil.log("Wins: " + resultSet.getString(1));
				},
				null
			);

			source.executeQueryAsync(
				"SELECT * FROM `reaction_wins`;",
				null, null, null
			);
		}
		catch(SQLException e)
		{
			//TODO: Handle
		}


	}

}

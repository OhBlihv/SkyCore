package com.skytonia.SkyCore.movement;

import com.skytonia.SkyCore.redis.RedisManager;
import com.skytonia.SkyCore.util.RunnableShorthand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

/**
 * Created by Chris Brown (OhBlihv) on 11/17/2016.
 */
public class PlayerCount
{
	
	private static final String PLAYER_COUNT_KEY = "_PLAYERCOUNT";
	
	public PlayerCount(JavaPlugin plugin)
	{
		RunnableShorthand.forPlugin(plugin).with(PlayerCount::updatePlayerCount).runTimerASync(1, TimeUnit.SECONDS, 1, TimeUnit.SECONDS);
	}
	
	public static void updatePlayerCount()
	{
		updatePlayerCount(Bukkit.getOnlinePlayers().size());
	}
	
	public static void updatePlayerCount(int players)
	{
		RedisManager.accessConnection((jedis) ->
		{
			jedis.set(RedisManager.getServerName() + PLAYER_COUNT_KEY, String.valueOf(players));
		});
	}
	
	public static int getPlayerCount(String server)
	{
		final int[] playerCount = new int[] {0};
		RedisManager.accessConnection((jedis) ->
		{
			String playerCountString = jedis.get(server + PLAYER_COUNT_KEY);
			if(playerCountString != null && !playerCountString.isEmpty())
			{
				try
				{
					playerCount[0] = Integer.parseInt(playerCountString);
				}
				catch(NumberFormatException e)
				{
					//
				}
			}
		});
		
		return playerCount[0];
	}
	
}

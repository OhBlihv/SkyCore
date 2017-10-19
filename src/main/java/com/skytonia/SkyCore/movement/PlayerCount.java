package com.skytonia.SkyCore.movement;

import com.skytonia.SkyCore.cosmetics.util.ParticleEffect;
import com.skytonia.SkyCore.movement.handlers.LilypadMovementHandler;
import com.skytonia.SkyCore.movement.handlers.RedisMovementHandler;
import com.skytonia.SkyCore.redis.RedisManager;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.RunnableShorthand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

/**
 * Created by Chris Brown (OhBlihv) on 11/17/2016.
 */
@Deprecated
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
		try
		{
			RedisManager.accessConnection((jedis) ->
			{
				jedis.set(RedisManager.getServerName() + PLAYER_COUNT_KEY, String.valueOf(players));
			});
		}
		catch(Exception e)
		{
			//Silence - Deprecated
		}
	}
	
	private static boolean printedUnsupportedPlayerCount = false;
	
	public static int getPlayerCount(String server)
	{
		if(MovementManager.getMovementHandler() instanceof RedisMovementHandler)
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
		else if(MovementManager.getMovementHandler() instanceof LilypadMovementHandler)
		{
			//return ArkhamServerSync.lily_server_map.getCurrentPlayers(server);
			return 0;
		}
		else
		{
			if(!printedUnsupportedPlayerCount)
			{
				printedUnsupportedPlayerCount = true;
				BUtil.logInfo("Unsupported system. Cannot retrieve player count without Lilypad/Redis support.");
			}
			
			return 0;
		}
	}
	
}

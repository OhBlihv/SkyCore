package com.skytonia.SkyCore.packets;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Chris Brown (OhBlihv) on 12/20/2016.
 */
public abstract class PacketLibrary
{
	
	private static Map<UUID, Persistence> persistenceMap = new HashMap<>();
	
	public static void startPersistingTask(UUID player, Persistence.PersistingType persistingType, long tickDelay, int executions, final Runnable runnable)
	{
		startPersistingTask(player, persistingType, tickDelay, executions,
		                    () ->
		                    {
			                    runnable.run();
			                    return true;
		                    });
	}
	
	public static void startPersistingTask(UUID player, Persistence.PersistingType persistingType, long tickDelay, int executions, TextRunnable runnable)
	{
		Persistence playerPersistence;
		if((playerPersistence = persistenceMap.get(player)) == null)
		{
			playerPersistence = new Persistence();
			persistenceMap.put(player, playerPersistence);
		}
		
		playerPersistence.addOrReplaceRunnable(persistingType, tickDelay, executions, runnable);
	}
	
	public static void cancelPersistingTask(UUID player, Persistence.PersistingType persistingType)
	{
		Persistence playerPersistence = persistenceMap.get(player);
		if(playerPersistence != null)
		{
			playerPersistence.removeRunnable(persistingType);
			if(playerPersistence.isEmpty())
			{
				persistenceMap.remove(player);
			}
		}
	}
	
	public abstract void sendActionBar(Player player, String message, int lifespan);
	
	public abstract void sendTitle(Player player, String title, String subTitle, int persistTime, int fadeIn, int fadeOut);
	
}

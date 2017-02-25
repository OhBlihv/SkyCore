package com.skytonia.SkyCore.packets;

import com.skytonia.SkyCore.util.StaticNMS;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Chris Brown (OhBlihv) on 2/10/2016.
 */
public class PacketUtil
{
	
	private static Map<UUID, Persistence> persistenceMap = new HashMap<>();
	
	public static void startPersistingTask(UUID player, Persistence.PersistingType persistingType, long tickDelay, int executions, Runnable runnable)
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
		}
		
		if(playerPersistence.isEmpty())
		{
			persistenceMap.remove(player);
		}
	}
	
	public static void sendActionBar(Player player, String message, int lifespan)
	{
		StaticNMS.getPacketLibrary().sendActionBar(player, message, lifespan);
	}
	
	public static void sendTitle(Player player, String title, String subTitle, int persistTime, int fadeIn, int fadeOut)
	{
		StaticNMS.getPacketLibrary().sendTitle(player, title, subTitle, persistTime, fadeIn, fadeOut);
	}
	
}

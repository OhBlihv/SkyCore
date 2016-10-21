package com.skytonia.SkyCore.util;

import com.skytonia.SkyCore.SkyCore;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Chris Brown (OhBlihv) on 2/10/2016.
 */
public class PacketUtil
{
	
	public enum PersistingType
	{
		
		ACTION_BAR,
		TITLE;
		
	}
	
	public static class PersistingRunnable
	{
		
		private int taskId;
		private int executionCount = 0,
					maxExecutions;
		
		public PersistingRunnable(long tickDelay, int executions, Runnable runnable)
		{
			//No executions necessary.
			if(executions <= 0)
			{
				return;
			}
			
			this.maxExecutions = executions;
			
			//Run immediately, then every 'tickDelay' ticks.
			this.taskId = Bukkit.getScheduler().runTaskTimer(SkyCore.getPluginInstance(), () ->
			{
				try
				{
					runnable.run();
					
					if(++executionCount == maxExecutions)
					{
						cancelRunnable();
					}
				}
				//Just in case.
				catch(Exception e)
				{
					cancelRunnable();
				}
			}, 1, tickDelay).getTaskId();
		}
		
		public void cancelRunnable()
		{
			if(taskId != -1)
			{
				Bukkit.getScheduler().cancelTask(taskId);
				taskId = -1;
			}
		}
		
	}
	
	public static class Persistence
	{
		
		private final Map<PersistingType, PersistingRunnable> persistingRunnables = new EnumMap<>(PersistingType.class);
		
		public void addOrReplaceRunnable(PersistingType persistingType, long tickDelay, int executions, Runnable runnable)
		{
			if(persistingRunnables.containsKey(persistingType))
			{
				persistingRunnables.get(persistingType).cancelRunnable();
			}
			
			persistingRunnables.put(persistingType, new PersistingRunnable(tickDelay, executions, runnable));
		}
		
	}
	
	private static Map<UUID, Persistence> persistenceMap = new HashMap<>();
	
	private static void sendPacket(PlayerConnection playerConnection, Packet packet)
	{
		playerConnection.sendPacket(packet);
	}
	
	public static void startPersistingTask(UUID player, PersistingType persistingType, long tickDelay, int executions, Runnable runnable)
	{
		Persistence playerPersistence;
		if((playerPersistence = persistenceMap.get(player)) == null)
		{
			playerPersistence = new Persistence();
			persistenceMap.put(player, playerPersistence);
		}
		
		playerPersistence.addOrReplaceRunnable(persistingType, tickDelay, executions, runnable);
	}
	
	/*
	 *  Title/ActionBar Sending
	 */
	
	public static void sendActionBar(Player player, String message, int lifespan)
	{
		PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
		
		//Action bar lasts about 2-3 seconds. Send an update after 2 seconds to ensure it does not disappear
		startPersistingTask(player.getUniqueId(), PersistingType.ACTION_BAR, 40L, lifespan / 2,
		                    () -> sendPacket(playerConnection, packetPlayOutChat));
	}
	
	public static void sendTitle(Player player, String title, String subTitle, int persistTime, int fadeIn, int fadeOut)
	{
		PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
		
		PacketPlayOutTitle timesPacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, persistTime, fadeOut);
		playerConnection.sendPacket(timesPacket);
		
		if(subTitle != null)
		{
			sendTitlePacket(playerConnection, PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subTitle);
		}
		
		if(title != null)
		{
			sendTitlePacket(playerConnection, PacketPlayOutTitle.EnumTitleAction.TITLE, title);
		}
	}
	
	private static void sendTitlePacket(PlayerConnection playerConnection, PacketPlayOutTitle.EnumTitleAction titleAction, String message)
	{
		playerConnection.sendPacket(new PacketPlayOutTitle(titleAction,
		                       IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}")));
	}
	
}

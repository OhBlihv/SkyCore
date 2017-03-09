package com.skytonia.SkyCore.movement;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.redis.RedisManager;
import com.skytonia.SkyCore.redis.RedisMessage;
import com.skytonia.SkyCore.util.BUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public class HubManager
{
	
	private class HubServer
	{
		
		private boolean online = false;
		
		private int updateTaskId = -1;
		
		public void receivePing()
		{
			if(updateTaskId != -1)
			{
				Bukkit.getScheduler().cancelTask(updateTaskId);
				
				updateTaskId = -1;
			}
			
			online = true;
		}
		
		public void setOffline()
		{
			online = false;
			updateTaskId = -1;
		}
		
	}
	
	private static HubManager instance = null;
	public static HubManager getInstance()
	{
		if(instance == null)
		{
			instance = new HubManager();
		}
		return instance;
	}
	
	private static final String CHANNEL_INIT =          "SkyCore_Hub_Init",
								CHANNEL_INIT_RESTART =  "SkyCore_Hub_Restart",
								CHANNEL_PING =          "SkyCore_Hub_Ping",
								CHANNEL_PING_RECV =     "SkyCore_Hub_PingR";
	
	private final Map<String, HubServer> hubServers = new ConcurrentHashMap<>();
	
	private boolean isHub = false;
	
	private HubManager()
	{
		if(RedisManager.getServerName().toLowerCase().startsWith("hub"))
		{
			isHub = true;
			
			RedisManager.registerSubscription(this::onMessage, false,
			                                  CHANNEL_INIT_RESTART,
			                                  RedisManager.getServerName() + ">" + CHANNEL_INIT_RESTART);
			
			//Alert all servers we're a hub and we're alive!
			RedisManager.sendMessageToAll(CHANNEL_INIT, RedisManager.getServerName());
		}
		else
		{
			RedisManager.registerSubscription(this::onMessage, false,
			                                  CHANNEL_INIT,
			                                  RedisManager.getServerName() + ">" + CHANNEL_INIT);
			
			RedisManager.sendMessageToAll(CHANNEL_INIT_RESTART, RedisManager.getServerName());
		}
		
		RedisManager.registerSubscription(this::onMessage, true, CHANNEL_PING, CHANNEL_PING_RECV);
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(SkyCore.getPluginInstance(), () ->
		{
			BukkitScheduler scheduler = Bukkit.getScheduler();
			Plugin plugin = SkyCore.getPluginInstance();
			
			for(Map.Entry<String, HubServer> entry : hubServers.entrySet())
			{
				RedisManager.sendMessage(entry.getKey(), CHANNEL_PING, RedisManager.getServerName());
				
				entry.getValue().updateTaskId = scheduler.runTaskLaterAsynchronously(plugin, () -> entry.getValue().setOffline(), 200L).getTaskId();
			}
			
		}, 20L, 300L); //15 Seconds
	}
	
	public Set<String> getAllHubs()
	{
		return hubServers.keySet();
	}
	
	public String getHubServer()
	{
		List<Map.Entry<String, HubServer>> entrySet = new ArrayList<>(hubServers.entrySet());
		
		Collections.shuffle(entrySet);
		
		for(Map.Entry<String, HubServer> entry : entrySet)
		{
			//Avoid sending us to offline hubs and the hub we're on!
			if(entry.getValue().online && !entry.getKey().equalsIgnoreCase(RedisManager.getServerName()))
			{
				if(entry.getKey().contains("beta") && !RedisManager.getServerName().contains("beta"))
				{
					continue;
				}
				
				return entry.getKey();
			}
		}
		
		return "hub1";
	}
	
	public void onMessage(RedisMessage message)
	{
		String channel = message.getChannel();
		if(    !channel.equals(CHANNEL_INIT) &&
			   !channel.equals(CHANNEL_INIT_RESTART) &&
			   !channel.equals(CHANNEL_PING) &&
			   !channel.equals(CHANNEL_PING_RECV))
		{
			return;
		}
		
		String[] messageSplit = message.getMessage().split("[|]");
		
		switch(channel)
		{
			case CHANNEL_INIT:
			{
				String hubName = messageSplit[0];
				//Don't register the current server
				if(hubName.equals(RedisManager.getServerName()))
				{
					return;
				}
				
				if(hubServers.containsKey(hubName))
				{
					//BUtil.logInfo("Already registered Hub Server '" + hubName + "'");
					hubServers.get(hubName).receivePing();
				}
				else
				{
					hubServers.put(hubName, new HubServer());
					BUtil.logInfo("Registered new Hub Server '" + hubName + "'");
				}
				break;
			}
			case CHANNEL_INIT_RESTART:
			{
				if(isHub)
				{
					//Respond that we're alive!
					RedisManager.sendMessage(message.getMessage(), CHANNEL_INIT, RedisManager.getServerName());
				}
				
				break;
			}
			case CHANNEL_PING:
			{
				if(!isHub)
				{
					return;
				}
				
				String targetServer = messageSplit[0];
				
				RedisManager.sendMessage(targetServer, CHANNEL_PING_RECV, RedisManager.getServerName());
				break;
			}
			case CHANNEL_PING_RECV:
			{
				String hubName = messageSplit[0];
				
				HubServer hubServer = hubServers.get(hubName);
				if(hubServer == null)
				{
					hubServer = new HubServer();
					
					hubServers.put(hubName, hubServer);
				}
				
				hubServer.receivePing();
				break;
			}
		}
	}
	
}

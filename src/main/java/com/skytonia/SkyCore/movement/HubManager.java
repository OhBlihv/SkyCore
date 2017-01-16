package com.skytonia.SkyCore.movement;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.sockets.SocketManager;
import com.skytonia.SkyCore.sockets.events.BukkitSocketHandshakeEvent;
import com.skytonia.SkyCore.sockets.events.BukkitSocketJSONEvent;
import com.skytonia.SkyCore.util.BUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public class HubManager implements Listener
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
		Bukkit.getPluginManager().registerEvents(this, SkyCore.getPluginInstance());
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(SkyCore.getPluginInstance(), () ->
		{
			
			BukkitScheduler scheduler = Bukkit.getScheduler();
			Plugin plugin = SkyCore.getPluginInstance();
			
			for(Map.Entry<String, HubServer> entry : hubServers.entrySet())
			{
				SocketManager.sendMessageTo(entry.getKey(), CHANNEL_PING);
				
				entry.getValue().updateTaskId = scheduler.runTaskLaterAsynchronously(plugin, () ->
				{
					
					entry.getValue().setOffline();
					
				}, 200L).getTaskId();
			}
			
		}, 20L, 300L); //15 Seconds
	}
	
	public String getHubServer()
	{
		List<Map.Entry<String, HubServer>> entrySet = new ArrayList<>(hubServers.entrySet());
		
		Collections.shuffle(entrySet);
		
		for(Map.Entry<String, HubServer> entry : entrySet)
		{
			if(entry.getValue().online)
			{
				return entry.getKey();
			}
		}
		
		return "hub1";
	}
	
	@EventHandler
	public void onSocketHandshake(BukkitSocketHandshakeEvent event)
	{
		if(SocketManager.getServerName().toLowerCase().startsWith("hub"))
		{
			isHub = true;
			
			//Alert all servers we're a hub and we're alive!
			SocketManager.sendMessageTo(null, CHANNEL_INIT, SocketManager.getServerName());
		}
		else
		{
			SocketManager.sendMessageTo(null, CHANNEL_INIT_RESTART, SocketManager.getServerName());
		}
	}
	
	@EventHandler
	public void onSocketJSONMessage(BukkitSocketJSONEvent event)
	{
		String channel = event.getChannel();
		
		if(!channel.equals(CHANNEL_INIT) &&
			   !channel.equals(CHANNEL_INIT_RESTART) &&
			   !channel.equals(CHANNEL_PING) &&
			   !channel.equals(CHANNEL_PING_RECV))
		{
			return;
		}
		
		String hubName = event.getData().replace("|", "");
		
		switch(channel)
		{
			case CHANNEL_INIT:
			{
				hubServers.put(hubName, new HubServer());
				BUtil.logInfo("Registered new Hub Server '" + hubName + "'");
				break;
			}
			case CHANNEL_INIT_RESTART:
			{
				//Re-Register ourselves as a hub server
				isHub = true;
				
				//Alert all servers we're a hub and we're alive!
				SocketManager.sendMessageTo(null, CHANNEL_INIT, SocketManager.getServerName());
				
				break;
			}
			case CHANNEL_PING:
			{
				if(!isHub)
				{
					return;
				}
				
				SocketManager.sendMessageTo(hubName, CHANNEL_PING_RECV, SocketManager.getServerName());
				break;
			}
			case CHANNEL_PING_RECV:
			{
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

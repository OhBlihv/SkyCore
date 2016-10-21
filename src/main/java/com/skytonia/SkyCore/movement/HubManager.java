package com.skytonia.SkyCore.movement;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.sockets.events.BukkitSocketHandshakeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public class HubManager implements Listener
{
	
	private static HubManager instance = null;
	public static HubManager getInstance()
	{
		if(instance == null)
		{
			instance = new HubManager();
		}
		return instance;
	}
	
	private HubManager()
	{
		Bukkit.getPluginManager().registerEvents(this, SkyCore.getPluginInstance());
	}
	
	@EventHandler
	public void onSocketHandshake(BukkitSocketHandshakeEvent event)
	{
		
	}
	
}

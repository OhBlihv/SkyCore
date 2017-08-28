package com.skytonia.SkyCore.servers;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.servers.handlers.CommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.LilypadCommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.LilypadRedisCommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.NullCommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.RedisCommunicationHandler;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.StaticNMS;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Created by Chris Brown (OhBlihv) on 5/16/2017.
 */
public class ServerController
{
	
	private final SkyCore plugin;
	
	@Getter
	private static CommunicationHandler communicationHandler = null;
	
	public ServerController(SkyCore plugin)
	{
		this.plugin = plugin;
		
		switch(StaticNMS.getServerName())
		{
			case "TWSpigot":
			case "SkySpigot":
				BUtil.log("Initializing Server Messaging System");
				break;
			default:
			{
				if (!BUtil.getNMSVersion().equals("v1_12_R1"))
				{
					BUtil.log("Using Null Server Messaging System on unsupported software");
					communicationHandler = new NullCommunicationHandler();
					return;
				}
				BUtil.log("Initializing Server Messaging System");
				break;
			}
		}
		
		/*
		 * Priority - Redis, Lilypad, BungeeCord
		 */
		boolean hasLilypad;
		{
			Plugin connectPlugin = Bukkit.getPluginManager().getPlugin("LilyPad-Connect");
			hasLilypad = connectPlugin != null && connectPlugin.isEnabled();
		}
		
		//Redis
		try
		{
			if(hasLilypad)
			{
				communicationHandler = new LilypadRedisCommunicationHandler();
				BUtil.log("Initialised Redis/Lilypad Communication Handler");
			}
			else
			{
				communicationHandler = new RedisCommunicationHandler();
				BUtil.log("Initialised Redis Communication Handler");
			}
		}
		catch(Exception e)
		{
			if(hasLilypad)
			{
				communicationHandler = new LilypadCommunicationHandler();
				BUtil.log("Initialised Lilypad Communication Handler");
			}
			else
			{
				//TODO: Bungeecord Support
				BUtil.log("Lilypad-Connect not found - Redis Handler failed with the following stack trace:");
				e.printStackTrace();
				
				throw new IllegalArgumentException("Redis/Lilypad not found. Cannot initiate cross-server communication.");
			}
		}
		
		((Thread) communicationHandler).start();
	}
	
}

package com.skytonia.SkyCore.servers;

import com.skytonia.SkyCore.servers.handlers.CommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.LilypadCommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.LilypadRedisCommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.NullCommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.RedisCommunicationHandler;
import com.skytonia.SkyCore.util.BUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Created by Chris Brown (OhBlihv) on 5/16/2017.
 */
public class ServerController
{
	
	@Getter
	private static CommunicationHandler communicationHandler = null;
	
	public ServerController()
	{
		BUtil.log("Initializing Server Messaging System");
		
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
				BUtil.log("Lilypad-Connect not found - Redis Handler failed with the following stack trace:");
				e.printStackTrace();

				BUtil.log("The server messaging system required a working setup of:");
				BUtil.log("- Bungeecord + Redis");
				BUtil.log("- Lilypad + Redis");
				BUtil.log("- Lilypad");
				BUtil.log("The messaging system cannot fully run on any other setup and will fail to launch.");

				communicationHandler = new NullCommunicationHandler();
				BUtil.log("Using empty handler.");
			}
		}

		try
		{
			communicationHandler.registerChannels();

			((Thread) communicationHandler).start();
		}
		catch(Exception e)
		{
			BUtil.log(communicationHandler.getClass().getSimpleName() + " failed to register channels with the following stack trace:");
			e.printStackTrace();

			communicationHandler = new NullCommunicationHandler();
			BUtil.log("Using empty handler.");
		}
	}
	
}

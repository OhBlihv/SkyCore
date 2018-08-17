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
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.EnumSet;
import java.util.Set;

/**
 * Created by Chris Brown (OhBlihv) on 5/16/2017.
 */
public class ServerController
{

	private enum HandlerCapability
	{

		REDIS,
		LILYPAD

	}
	
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

		//Assume Redis, but there is no simple way to test for this without explicitly connecting later on.
		Set<HandlerCapability> capabilities = EnumSet.of(HandlerCapability.REDIS);
		if(hasLilypad)
		{
		    capabilities.add(HandlerCapability.LILYPAD);
		}

		initialiseHandler(capabilities);
	}

	private void initialiseHandler(Set<HandlerCapability> capabilities)
	{
		try
		{
			//Lilypad + Redis Hybrid
			if(capabilities.contains(HandlerCapability.LILYPAD) && capabilities.contains(HandlerCapability.REDIS))
			{
				communicationHandler = new LilypadRedisCommunicationHandler();
			}
			//Bungee + Redis
			else if(capabilities.contains(HandlerCapability.REDIS))
			{
				communicationHandler = new RedisCommunicationHandler();
			}
			//Standalone Lilypad
			else if(capabilities.contains(HandlerCapability.LILYPAD))
			{
				communicationHandler = new LilypadCommunicationHandler();
			}
		}
		catch(Exception e)
		{
			//
		}

		if(communicationHandler == null || capabilities.isEmpty())
		{
			BUtil.log("The server messaging system requires a working setup of:");
			BUtil.log("- Lilypad + Redis");
			BUtil.log("- Bungeecord + Redis");
			BUtil.log("- Lilypad");
			BUtil.log("The messaging system cannot fully run on any other setup and will fail to launch.");

			communicationHandler = new NullCommunicationHandler();
			BUtil.log("Using empty handler.");
			return;
		}

		try
		{
			communicationHandler.registerChannels();

			((Thread) communicationHandler).start();

			BUtil.log("Using " + String.join(" ", communicationHandler.getClass().getSimpleName().split("(?=[A-Z])")));
		}
		catch(Exception e)
		{
			if(capabilities.isEmpty())
			{
				BUtil.log(communicationHandler.getClass().getSimpleName() + " failed to register channels with the following stack trace:");
				e.printStackTrace();
			}
			else
			{
				if(e instanceof JedisConnectionException)
				{
					capabilities.remove(HandlerCapability.REDIS);
				}
				//TODO: Find general causes of other handlers
			}

			initialiseHandler(capabilities);
		}
	}
	
}

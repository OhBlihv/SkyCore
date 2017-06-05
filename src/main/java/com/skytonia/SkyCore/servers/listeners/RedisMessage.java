package com.skytonia.SkyCore.servers.listeners;

import com.skytonia.SkyCore.redis.RedisManager;
import lombok.Getter;

/**
 * Created by Chris Brown (OhBlihv) on 3/4/2017.
 */
public class RedisMessage
{
	
	@Getter
	private final String channel;
	
	@Getter
	private final String server;
	
	@Getter
	private final String message;
	
	public RedisMessage(String channel, String data)
	{
		String tempChannel;
		
		int splitLoc = channel.indexOf('>');
		if(splitLoc >= 0)
		{
			this.server = channel.substring(0, splitLoc);
			tempChannel = channel.substring(splitLoc + 1, channel.length());
		}
		else
		{
			this.server = null;
			tempChannel = channel;
		}
		
		if(RedisManager.isBeta() && tempChannel.startsWith("beta_"))
		{
			tempChannel = tempChannel.substring(5);
		}
		
		this.channel = tempChannel;
		
		this.message = data;
	}
	
	public boolean hasServer()
	{
		return server != null && !server.isEmpty();
	}
	
}

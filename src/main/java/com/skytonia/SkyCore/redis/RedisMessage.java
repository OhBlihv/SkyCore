package com.skytonia.SkyCore.redis;

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
		this.channel = channel;
		
		int splitLoc = data.indexOf('_');
		if(splitLoc >= 0)
		{
			this.server = data.substring(0, splitLoc);
			this.message = data.substring(splitLoc + 1, data.length());
		}
		else
		{
			this.server = null;
			this.message = data;
		}
	}
	
	public boolean hasServer()
	{
		return server != null && !server.isEmpty();
	}
	
}

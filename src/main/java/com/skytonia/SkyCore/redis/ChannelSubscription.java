package com.skytonia.SkyCore.redis;

/**
 * Created by Chris Brown (OhBlihv) on 3/4/2017.
 */
public interface ChannelSubscription
{

	void onMessage(RedisMessage message);
	
}

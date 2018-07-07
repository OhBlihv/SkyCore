package com.skytonia.SkyCore.servers.handlers.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by Chris Brown (OhBlihv) on 5/25/2017.
 */
public interface RedisRunnable
{
	
	void run(Jedis jedis) throws Exception;
	
}

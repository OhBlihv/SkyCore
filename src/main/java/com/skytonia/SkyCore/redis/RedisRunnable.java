package com.skytonia.SkyCore.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by Chris Brown (OhBlihv) on 4/22/2017.
 */
public interface RedisRunnable
{
	
	void run(Jedis jedis);
	
}

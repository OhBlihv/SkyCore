package com.skytonia.SkyCore.redis;

import lombok.Getter;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 3/3/2017.
 */
public class RedisManager
{
	
	private static final String CHANNEL_REGISTRATION = "SkyCore_Init";
	
	@Getter
	private static String serverName;
	
	private static final Map<String, ChannelSubscriber> subscriptionMap = new HashMap<>();
	
	private static JedisPool jedisPool;
	
	static
	{
		RedisFlatFile redisFlatFile = RedisFlatFile.getInstance();
		
		serverName = redisFlatFile.getString("server-name");
		
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMinIdle(8);
		poolConfig.setMaxTotal(128); //Lower?
		
		jedisPool = new JedisPool(poolConfig, "184.164.136.211", 6379, 5000);
		
		sendMessage(CHANNEL_REGISTRATION, serverName + "|" + Bukkit.getIp() + ":" + Bukkit.getPort());
	}
	
	public static void shutdown()
	{
		jedisPool.destroy();
	}
	
	public static Jedis getConnection()
	{
		return jedisPool.getResource();
	}
	
	public static void registerSubscription(ChannelSubscription subscriber, String... channels)
	{
		registerSubscription(subscriber, true, channels);
	}
	
	public static void registerSubscription(ChannelSubscription subscriber, boolean prefixWithServerName, String... channels)
	{
		ChannelSubscriber channelSubscriber = new ChannelSubscriber(getConnection(), Arrays.asList(channels), subscriber);
		
		for(String channel : channels)
		{
			if(prefixWithServerName)
			{
				channel = serverName + channel;
			}
			
			if(subscriptionMap.containsKey(channel))
			{
				throw new IllegalArgumentException("Duplicate Channel '" + channel + "'");
			}
			
			subscriptionMap.put(channel, channelSubscriber);
		}
	}
	
	public static void sendMessageToAll(String channel, String... message)
	{
		sendMessage(null, channel, message);
	}
	
	public static void sendMessage(String server, String channel, String... message)
	{
		try(Jedis jedis = jedisPool.getResource())
		{
			if(server != null)
			{
				channel = server + "_" + channel;
			}

			//Server channel format: '<server>_<channel>'
			jedis.publish(channel, String.join("|", message));
		}
	}
	
}

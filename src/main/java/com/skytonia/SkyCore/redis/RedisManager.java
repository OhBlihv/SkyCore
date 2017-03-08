package com.skytonia.SkyCore.redis;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 3/3/2017.
 */
public class RedisManager
{
	
	private static final String CHANNEL_REGISTRATION = "SkyCore_Init";
	
	@Getter
	private static String serverName;
	
	private static final Multimap<String, ChannelSubscriber> subscriptionMap = MultimapBuilder.hashKeys().arrayListValues().build();
	
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
		List<String> channelList = new ArrayList<>();
		for(String channel : channels)
		{
			if(prefixWithServerName)
			{
				channel = serverName + ">" + channel;
			}
			
			channelList.add(channel);
		}
		
		ChannelSubscriber channelSubscriber = new ChannelSubscriber(getConnection(), channelList, subscriber);
		
		for(String channel : channelList)
		{
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
				channel = server + ">" + channel;
			}
			
			//BUtil.logInfo("Publishing on Channel '" + channel + "' -> '" + String.join("|", message));

			//Server channel format: '<server>_<channel>'
			jedis.publish(channel, String.join("|", message));
		}
	}
	
}

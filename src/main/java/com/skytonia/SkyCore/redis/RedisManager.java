package com.skytonia.SkyCore.redis;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.movement.MovementManager;
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
	
	@Getter
	private static boolean isBeta = false;
	
	private static final Multimap<String, ChannelSubscriber> subscriptionMap = MultimapBuilder.hashKeys().arrayListValues().build();
	
	private static JedisPool jedisPool;
	
	static
	{
		RedisFlatFile redisFlatFile = RedisFlatFile.getInstance();
		
		serverName = redisFlatFile.getString("server-name");
		isBeta = serverName.contains("beta");
		
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMinIdle(8);
		poolConfig.setMaxTotal(256);
		poolConfig.setMaxWaitMillis(15000); //15 second max wait time
		poolConfig.setTestWhileIdle(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setNumTestsPerEvictionRun(10);
		poolConfig.setTimeBetweenEvictionRunsMillis(60000); //60 seconds
		
		if(SkyCore.isSkytonia())
		{
			jedisPool = new JedisPool(poolConfig, "184.164.136.211", 6379, 5000);
		}
		else
		{
			jedisPool = new JedisPool(poolConfig, "localhost", 6379, 5000);
		}
		
		//Handle this manually to avoid server/beta case handling
		try(Jedis jedis = jedisPool.getResource())
		{
			//Server channel format: '<server>_<channel>'
			jedis.publish(CHANNEL_REGISTRATION, serverName + "|" + Bukkit.getIp() + ":" + Bukkit.getPort());
		}
	}
	
	public static void shutdown()
	{
		for(ChannelSubscriber subscriber : subscriptionMap.values())
		{
			subscriber.cancel();
		}
		subscriptionMap.clear();
		
		jedisPool.destroy();
	}
	
	@Deprecated
	public static Jedis getConnection()
	{
		return jedisPool.getResource();
	}
	
	public static void accessConnection(RedisRunnable runnable)
	{
		try(Jedis jedis = jedisPool.getResource())
		{
			runnable.run(jedis);
		}
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
			if(isBeta &&
				   !channel.equals(MovementManager.CHANNEL_MOVE_PLAYER_REQ) &&
				   !channel.equals(MovementManager.CHANNEL_MOVE_PLAYER_REPLY))
			{
				int serverPrefixIdx = channel.indexOf('>');
				if(serverPrefixIdx != -1)
				{
					channel = channel.substring(0, serverPrefixIdx + 1) + "beta_" + channel.substring(serverPrefixIdx + 1);
				}
				else
				{
					channel = "beta_" + channel;
				}
			}
			
			if(prefixWithServerName)
			{
				channel = serverName + ">" + channel;
			}
			
			//BUtil.logInfo("Registering listener on '" + channel + "'");
			
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
			if(isBeta &&
				   !channel.equals(MovementManager.CHANNEL_MOVE_PLAYER_REQ) &&
				   !channel.equals(MovementManager.CHANNEL_MOVE_PLAYER_REPLY))
			{
				channel = "beta_" + channel;
			}
			
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

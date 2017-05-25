package com.skytonia.SkyCore.servers.handlers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.servers.MovementAction;
import com.skytonia.SkyCore.servers.handlers.exception.MessageException;
import com.skytonia.SkyCore.servers.handlers.processing.AbstractCommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.processing.InboundCommunicationMessage;
import com.skytonia.SkyCore.servers.handlers.processing.OutboundCommunicationMessage;
import com.skytonia.SkyCore.servers.handlers.redis.RedisRunnable;
import com.skytonia.SkyCore.servers.listeners.ChannelSubscriber;
import com.skytonia.SkyCore.servers.listeners.ChannelSubscription;
import com.skytonia.SkyCore.servers.listeners.RedisChannelSubscriber;
import com.skytonia.SkyCore.servers.util.MessageUtil;
import com.skytonia.SkyCore.util.file.FlatFile;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 5/25/2017.
 */
public class RedisCommunicationHandler extends AbstractCommunicationHandler implements CommunicationHandler, ChannelSubscription
{
	
	private static final String CHANNEL_REGISTRATION = "SC_Init";
	
	private final JedisPool jedisPool;
	
	public RedisCommunicationHandler()
	{
		super();
		
		FlatFile commFile = FlatFile.forFileName("communications.yml");
		
		//Attempt to set up our Pool
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMinIdle(8);
		poolConfig.setMaxTotal(256);
		poolConfig.setMaxWaitMillis(15000); //15 second max wait time
		poolConfig.setTestWhileIdle(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setNumTestsPerEvictionRun(10);
		poolConfig.setTimeBetweenEvictionRunsMillis(60000); //60 seconds
		
		jedisPool = new JedisPool(poolConfig, commFile.getString("communication.redis.host"), commFile.getInt("communication.redis.port"), 5000);
		
		currentServer = commFile.getString("communication.redis.server");
		
		registerSubscription(this, true, CHANNEL_MOVE_REQ, CHANNEL_MOVE_REPL);
		
		try(Jedis jedis = jedisPool.getResource())
		{
			jedis.publish(CHANNEL_REGISTRATION, currentServer);
		}
	}
	
	@Override
	public void requestPlayerTransfer(Player player, String serverName, MovementAction movementAction)
	{
		super.requestPlayerTransfer(player, serverName, movementAction);
		
		accessConnection((jedis) ->
		{
			jedis.publish(CHANNEL_MOVE_REQ, MessageUtil.mergeArguments(serverName, player.getName()));
		});
	}
	
	@Override
	public void transferPlayer(Player player, String serverName)
	{
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);
		
		player.sendPluginMessage(SkyCore.getPluginInstance(), "BungeeCord", out.toByteArray());
	}
	
	@Override
	public void sendMessage(OutboundCommunicationMessage message) throws MessageException
	{
		sendMessage(message.getServer(), message.getChannel(), message.getMessage());
	}
	
	@Override
	public void registerSubscription(ChannelSubscription subscriber, boolean prefixWithServerName, String... channels)
	{
		List<String> channelList = new ArrayList<>();
		for(String channel : channels)
		{
			if(prefixWithServerName)
			{
				channel = currentServer + ">" + channel;
			}
			
			channelList.add(channel);
		}
		
		ChannelSubscriber channelSubscriber = new RedisChannelSubscriber(jedisPool.getResource(), channelList, subscriber);
		
		for(String channel : channelList)
		{
			subscriptionMap.put(channel, channelSubscriber);
		}
	}
	
	public void accessConnection(RedisRunnable runnable)
	{
		try(Jedis jedis = jedisPool.getResource())
		{
			runnable.run(jedis);
		}
	}
	
	public void sendMessage(String server, final String channel, String... message)
	{
		accessConnection((jedis) ->
		{
			String serverChannel = channel;
			if(server != null)
			{
				serverChannel = server + ">" + serverChannel;
			}
			
			jedis.publish(serverChannel, MessageUtil.mergeArguments(message));
		});
	}
	
	@Override
	public void onMessage(InboundCommunicationMessage message)
	{
		addMessage(message);
	}
}

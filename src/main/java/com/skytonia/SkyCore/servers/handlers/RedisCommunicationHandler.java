package com.skytonia.SkyCore.servers.handlers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.servers.MovementAction;
import com.skytonia.SkyCore.servers.events.PlayerChangeServerEvent;
import com.skytonia.SkyCore.servers.handlers.debug.DebugJedisPool;
import com.skytonia.SkyCore.servers.handlers.processing.AbstractCommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.processing.InboundCommunicationMessage;
import com.skytonia.SkyCore.servers.handlers.processing.OutboundCommunicationMessage;
import com.skytonia.SkyCore.servers.handlers.redis.RedisRunnable;
import com.skytonia.SkyCore.servers.listeners.ChannelSubscriber;
import com.skytonia.SkyCore.servers.listeners.ChannelSubscription;
import com.skytonia.SkyCore.servers.listeners.RedisChannelSubscriber;
import com.skytonia.SkyCore.servers.util.MessageUtil;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.file.FlatFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 5/25/2017.
 */
public class RedisCommunicationHandler extends AbstractCommunicationHandler implements CommunicationHandler, ChannelSubscription
{
	
	private static final String CHANNEL_REGISTRATION = "SC_Init";

	@Getter
	private final DebugJedisPool jedisPool;
	
	public RedisCommunicationHandler()
	{
		super();
		
		FlatFile commFile = FlatFile.forFileName("communication.yml");

		final int maxConnections = 500;
		//final int maxConnections = 5;

		//Attempt to set up our Pool
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMinIdle(1);
		poolConfig.setMaxIdle(maxConnections);
		poolConfig.setMaxTotal(maxConnections);
		poolConfig.setMaxWaitMillis(1000); //1 second max wait time
		poolConfig.setTestWhileIdle(true);
		poolConfig.setTestOnReturn(true);

		poolConfig.setNumTestsPerEvictionRun(10);
		poolConfig.setTimeBetweenEvictionRunsMillis(60000); //60 seconds

		jedisPool = new DebugJedisPool(poolConfig, commFile.getString("communication.redis.host"), commFile.getInt("communication.redis.port"), 5000);
		//jedisPool = new JedisPool(poolConfig, commFile.getString("communication.redis.host"), commFile.getInt("communication.redis.port"), 5000);

		//Only overwrite if we don't already have a name to use
		currentServer = commFile.getString("communication.redis.server");
		BUtil.log("Current Server: " + currentServer);
	}

	@Override
	public void registerChannels()
	{
		registerSubscription(this, true, CHANNEL_MOVE_FORCE, CHANNEL_MOVE_REQ, CHANNEL_MOVE_REPL);
		registerSubscription(this, false, CHANNEL_INFO_REPL);

		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(SkyCore.getPluginInstance(), "BungeeCord");

		try(Jedis jedis = jedisPool.getResource())
		{
			jedis.publish(CHANNEL_REGISTRATION, currentServer);
		}
		catch(Throwable e)
		{
			throw new IllegalArgumentException("Redis Not Supported");
		}
	}

	@Override
	public void shutdown()
	{
		super.shutdown();

		try
		{
			jedisPool.close();
		}
		catch(JedisException e)
		{
			if(e.getCause() instanceof IllegalStateException)
			{
				//Ignore. Object already returned to pool.
			}
			else
			{
				e.printStackTrace();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void requestPlayerTransfer(Player player, String serverName, MovementAction movementAction)
	{
		super.requestPlayerTransfer(player, serverName, movementAction);
		
		sendMessage(serverName, CHANNEL_MOVE_REQ, currentServer, player.getName(), player.getUniqueId().toString());
	}
	
	@Override
	public void transferPlayer(Player player, String serverName)
	{
		PlayerChangeServerEvent event = new PlayerChangeServerEvent(player, serverName);
		Bukkit.getPluginManager().callEvent(event);

		//Alert the other server of an incoming player
		sendMessage(new OutboundCommunicationMessage(
			serverName, CHANNEL_MOVE_FORCE, MessageUtil.mergeArguments(player.getName(), player.getUniqueId().toString())
		));

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);
		
		player.sendPluginMessage(SkyCore.getPluginInstance(), "BungeeCord", out.toByteArray());
	}
	
	@Override
	public void sendMessage(OutboundCommunicationMessage message)
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
			BUtil.log("Registering channel '" + channel + "'");
		}
	}
	
	public void accessConnection(RedisRunnable runnable)
	{
		if(jedisPool.isClosed())
		{
			return; //Cannot access. Server shutting down.
		}

		try(Jedis jedis = jedisPool.getResource())
		{
			runnable.run(jedis);
		}
		catch (Exception e)
		{
			BUtil.log("Exception occurred during redis connection execution");
			e.printStackTrace();
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

			String[] messageArr = new String[message.length + 1];
			System.arraycopy(message, 0, messageArr, 1, message.length);
			messageArr[0] = currentServer;

			jedis.publish(serverChannel, MessageUtil.mergeArguments(messageArr));
		});
	}
	
	@Override
	public void onMessage(InboundCommunicationMessage message)
	{
		addMessage(message);
	}

}

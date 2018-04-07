package com.skytonia.SkyCore.servers.handlers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.servers.MovementAction;
import com.skytonia.SkyCore.servers.ServerController;
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
import com.skytonia.SkyCore.util.RunnableShorthand;
import com.skytonia.SkyCore.util.file.FlatFile;
import javafx.util.Pair;
import lombok.Getter;
import me.theminecoder.bug.BukkitBugSnag;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

		currentServer = commFile.getString("communication.redis.server");
		BUtil.log("Current Server: " + currentServer);
		
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

		int[] issueCounter = new int[]{0};
		RunnableShorthand.forPlugin(SkyCore.getInstance()).with(() ->
		{

			if(jedisPool.getNumActive() == maxConnections)
			{
				BUtil.log("Currently Used Redis Connections (" + jedisPool.getNumActive() + "/" + jedisPool.getNumIdle() + "/" + maxConnections + ") (Active/Idle/Max)");

				if(issueCounter[0]++ % 40 == 0)
				{
					String fileName = "redis-connections-" + (System.currentTimeMillis() / 1000L) + ".debug";

					File debugFile = new File(SkyCore.getInstance().getDataFolder(), fileName);
					try(PrintWriter writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(debugFile))))
					{
						Map<?, PooledObject<Jedis>> allObjects;
						AbstractQueue<PooledObject<Jedis>> idleObjects;

						{
							Field internalPoolMethod = Pool.class.getDeclaredField("internalPool");
							internalPoolMethod.setAccessible(true);

							GenericObjectPool<Jedis> internalPool = (GenericObjectPool<Jedis>) internalPoolMethod.get(
								((RedisCommunicationHandler) ServerController.getCommunicationHandler()).getJedisPool()
							);

							BUtil.log("Pool class: " + internalPool.getClass().getSimpleName());

							Field field = internalPool.getClass().getDeclaredField("allObjects");
							field.setAccessible(true);
							allObjects = (Map<?, PooledObject<Jedis>>) field.get(internalPool);

							field = internalPool.getClass().getDeclaredField("idleObjects");
							field.setAccessible(true);
							idleObjects = (AbstractQueue<PooledObject<Jedis>>) field.get(internalPool);
						}

						Set<Integer> toRemoveConnections = new HashSet<>();
						for(Map.Entry<Integer, Pair<Jedis, Throwable>> entry : DebugJedisPool.registeredConnections.entrySet())
						{
							Object identityWrapper;

							Constructor<?> wrapperConstructor = Class.forName("org.apache.commons.pool2.impl.BaseGenericObjectPool$IdentityWrapper").getConstructors()[0];
							wrapperConstructor.setAccessible(true);
							identityWrapper = wrapperConstructor.newInstance(entry.getValue().getKey());

							PooledObject<Jedis> pooledJedis = allObjects.get(identityWrapper);
							if(idleObjects.contains(pooledJedis))
							{
								BUtil.log("Skipping idle connection...");
								toRemoveConnections.add(entry.getKey());
								continue;
							}

							if(!entry.getValue().getKey().isConnected())
							{
								BUtil.log("Skipping dead connection...");
								toRemoveConnections.add(entry.getKey());
							}
							else
							{
								writer.println("Connection ID: (" + entry.getKey() + ")");
								entry.getValue().getValue().printStackTrace(writer);
								writer.println(">----<");
							}
						}

						DebugJedisPool.registeredConnections.keySet().removeAll(toRemoveConnections);

						BUtil.log("Logged current jedis connection stack traces to log at " + fileName);
						//sender.sendMessage("Logged current jedis connection stack traces to log at " + fileName);
					}
					catch(Exception e)
					{
						BUtil.log("Failed to log jedis connection stack traces to file.");
						//sender.sendMessage("Failed to log jedis connection stack traces to file.");
						e.printStackTrace();
					}

					BukkitBugSnag.getBugsnagClient().notify(new IllegalStateException("Pool at maximum capacity."));
				}
			}

		}).runTimerASync(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS);
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

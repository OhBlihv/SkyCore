package com.skytonia.SkyCore.servers.listeners;

import com.skytonia.SkyCore.servers.handlers.processing.InboundCommunicationMessage;
import com.skytonia.SkyCore.servers.util.MessageUtil;
import com.skytonia.SkyCore.util.BUtil;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 3/3/2017.
 */
public class RedisChannelSubscriber extends ChannelSubscriber
{

	private static int threadId = 0;
	
	@Getter
	private final JedisPubSub subscription;
	
	private final Thread subscriptionThread;
	
	private final Jedis connection;
	
	public RedisChannelSubscriber(Jedis jedis, List<String> channels, ChannelSubscription channelSubscription)
	{
		this(jedis, channels, channelSubscription, null);
	}
	
	public RedisChannelSubscriber(Jedis connection, List<String> channels, ChannelSubscription channelSubscription, Thread subscriptionThread)
	{
		super(channels, channelSubscription);
		
		this.connection = connection;
		
		this.subscription = new JedisPubSub()
		{
			@Override
			public void onMessage(String channel, String message)
			{
				try
				{
					if(!channels.contains(channel))
					{
						//BUtil.log("Ignoring channel '" + channel + "'");
						return;
					}
					//BUtil.log("Listening to channel '" + channel + "'");

					String[] messageSplit = MessageUtil.splitArguments(message);
					
					String[] messageArr = new String[messageSplit.length - 1];
					System.arraycopy(messageSplit, 1, messageArr, 0, messageSplit.length - 1);

					{
						String[] channelSplit = channel.split("[>]");
						if(channelSplit.length > 1)
						{
							channel = channelSplit[1];
						}
					}

					channelSubscription.onMessage(new InboundCommunicationMessage(messageSplit[0], channel, MessageUtil.mergeArguments(messageArr)));
				}
				catch(Throwable e)
				{
					BUtil.log("Unable to handle messaging on channel '" + channel + "' with '" + message + "'");
					e.printStackTrace();
				}
			}
		};
		
		if(subscriptionThread == null)
		{
			subscriptionThread = new Thread(null, () ->
			{
				try
				{
					connection.subscribe(subscription, channels.toArray(new String[channels.size()]));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						connection.close();
					}
					catch(Exception e)
					{
						//Already returned to pool/closed
					}
				}

			}, "Redis-Subscription-Thread-" + (++threadId));
		}
		
		this.subscriptionThread = subscriptionThread;
		subscriptionThread.start();
	}
	
	@Override
	public void cancel()
	{
		//We register multiple channels to the same object
		//Don't throw exceptions - this is intended behaviour
		if(!subscription.isSubscribed())
		{
			return;
		}
		
		subscription.unsubscribe();
		subscriptionThread.interrupt();
		
		if(connection != null)
		{
			try
			{
				connection.close();
			}
			catch(IllegalStateException e)
			{
				//
			}
		}
	}
	
}

package com.skytonia.SkyCore.redis;

import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 3/3/2017.
 */
public class ChannelSubscriber
{
	
	@Getter
	private final List<String> channels;
	
	@Getter
	private final JedisPubSub subscription;
	
	private final Thread subscriptionThread;
	
	public ChannelSubscriber(List<String> channels, ChannelSubscription channelSubscription)
	{
		this(channels, channelSubscription, null);
	}
	
	public ChannelSubscriber(List<String> channels, ChannelSubscription channelSubscription, Thread subscriptionThread)
	{
		this.channels = channels;
		
		this.subscription = new JedisPubSub()
		{
			@Override
			public void onMessage(String channel, String message)
			{
				channelSubscription.onMessage(new RedisMessage(channel, message));
			}
		};
		
		if(subscriptionThread == null)
		{
			subscriptionThread = new Thread(() -> subscription.subscribe(channels.toArray(new String[channels.size()])));
		}
		
		this.subscriptionThread = subscriptionThread;
		subscriptionThread.start();
	}
	
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
	}
	
}

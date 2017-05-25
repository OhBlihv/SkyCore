package com.skytonia.SkyCore.servers.listeners;

import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 5/25/2017.
 */
public abstract class ChannelSubscriber
{
	
	protected final List<String> channels;
	
	protected final ChannelSubscription channelSubscription;
	
	public ChannelSubscriber(List<String> channels, ChannelSubscription channelSubscription)
	{
		this.channels = channels;
		this.channelSubscription = channelSubscription;
	}
	
	public abstract void cancel();
	
}

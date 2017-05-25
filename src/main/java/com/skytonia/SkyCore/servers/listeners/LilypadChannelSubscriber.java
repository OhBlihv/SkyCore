package com.skytonia.SkyCore.servers.listeners;

import com.skytonia.SkyCore.servers.handlers.processing.InboundCommunicationMessage;
import com.skytonia.SkyCore.util.BUtil;
import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.event.MessageEvent;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 5/25/2017.
 */
public class LilypadChannelSubscriber extends ChannelSubscriber
{
	
	private final Connect lilypad;
	
	private final Listener listener;
	
	public LilypadChannelSubscriber(Connect lilypad, List<String> channels, ChannelSubscription channelSubscription)
	{
		super(channels, channelSubscription);
		
		this.lilypad = lilypad;
		
		listener = new Listener()
		{
			
			public void onMessage(MessageEvent event)
			{
				try
				{
					channelSubscription.onMessage(new InboundCommunicationMessage(event.getSender(), event.getChannel(), event.getMessageAsString()));
				}
				catch(Throwable e)
				{
					BUtil.log("Unable to handle message on channel '" + event.getChannel() + "' with '" + Arrays.toString(event.getMessage()) + "'");
					e.printStackTrace();
				}
			}
			
		};
		
		this.lilypad.registerEvents(listener);
	}
	
	@Override
	public void cancel()
	{
		this.lilypad.unregisterEvents(listener);
	}
	
}

package com.skytonia.SkyCore.servers.listeners;

import com.skytonia.SkyCore.servers.handlers.processing.InboundCommunicationMessage;

/**
 * Created by Chris Brown (OhBlihv) on 3/4/2017.
 */
public interface ChannelSubscription
{

	void onMessage(InboundCommunicationMessage message);
	
}

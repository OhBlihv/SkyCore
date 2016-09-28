package com.skytonia.SkyCore.sockets.events;

import com.skytonia.SkyCore.sockets.client.SocketClient;
import org.bukkit.event.HandlerList;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public class BukkitSocketConnectEvent extends BukkitSocketEvent
{
	
	private final static HandlerList handlers = new HandlerList();
	
	public BukkitSocketConnectEvent(SocketClient client)
	{
		super(client);
	}
	
	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList()
	{
		return handlers;
	}
	
}

package com.skytonia.SkyCore.sockets.events;

import com.skytonia.SkyCore.sockets.client.SocketClient;
import lombok.Getter;
import org.bukkit.event.Event;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public abstract class BukkitSocketEvent extends Event
{
	
	@Getter
	SocketClient client;
	
	public BukkitSocketEvent(SocketClient socketClient)
	{
		this.client = socketClient;
	}
	
}

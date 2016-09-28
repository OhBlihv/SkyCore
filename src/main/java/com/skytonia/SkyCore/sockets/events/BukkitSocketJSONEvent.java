package com.skytonia.SkyCore.sockets.events;

import com.skytonia.SkyCore.sockets.client.SocketClient;
import org.bukkit.event.HandlerList;

import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public class BukkitSocketJSONEvent extends BukkitSocketEvent
{
	
	private final static HandlerList handlers = new HandlerList();
	private final Map<String, String> map;
	
	public BukkitSocketJSONEvent(SocketClient client, Map<String, String> map)
	{
		super(client);
		
		this.map = map;
	}
	
	public String getChannel()
	{
		return map.get("channel");
	}
	
	public String getData()
	{
		return map.get("data");
	}
	
	public void write(String data)
	{
		client.writeJSON(getChannel(), data);
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

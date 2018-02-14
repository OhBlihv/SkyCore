package com.skytonia.SkyCore.servers.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Created by Chris Brown (OhBlihv) on 12/19/2016.
 */
public class PlayerEnterServerEvent extends Event
{
	
	private static final HandlerList handlers = new HandlerList();

	@Getter
	private final String serverName;
	
	@Getter
	private final String playerName;

	@Getter
	private final UUID playerUUID;
	
	public PlayerEnterServerEvent(String serverName, String playerName, UUID playerUUID)
	{
		super(true);

		this.serverName = serverName;
		this.playerName = playerName;
		this.playerUUID = playerUUID;
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

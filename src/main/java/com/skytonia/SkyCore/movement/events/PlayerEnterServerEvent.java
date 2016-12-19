package com.skytonia.SkyCore.movement.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Chris Brown (OhBlihv) on 12/19/2016.
 */
public class PlayerEnterServerEvent extends Event
{
	
	private static final HandlerList handlers = new HandlerList();
	
	@Getter
	private final String playerName;
	
	public PlayerEnterServerEvent(String playerName)
	{
		this.playerName = playerName;
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

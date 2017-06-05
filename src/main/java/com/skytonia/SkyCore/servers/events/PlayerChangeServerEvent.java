package com.skytonia.SkyCore.servers.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Chris Brown (OhBlihv) on 12/19/2016.
 */
public class PlayerChangeServerEvent extends Event implements Cancellable
{
	
	private static final HandlerList handlers = new HandlerList();
	
	private boolean cancelled = false;
	
	@Getter
	@Setter
	private String cancelReason = "§cServer Movement Cancelled.";
	
	@Getter
	private final Player player;
	
	@Getter
	private final String targetServer;
	
	public PlayerChangeServerEvent(Player player, String targetServer)
	{
		this.player = player;
		this.targetServer = targetServer;
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
	
	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancel)
	{
		cancelled = cancel;
	}
	
}

package com.skytonia.SkyCore.servers.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Chris Brown (OhBlihv) on 2/28/2017.
 */
public class PlayerServerChangeRequestEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	
	private boolean cancelled = false;
	
	@Getter
	@Setter
	private String cancelReason = "§cServer Movement Cancelled.";
	
	@Getter
	private final String playerName;
	
	public PlayerServerChangeRequestEvent(String playerName, String cancelReason, boolean cancelled)
	{
		this.playerName = playerName;
		
		if(cancelReason != null && !cancelReason.isEmpty())
		{
			this.cancelReason = cancelReason;
		}
		
		this.cancelled = cancelled;
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

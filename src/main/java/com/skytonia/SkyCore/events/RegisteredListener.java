package com.skytonia.SkyCore.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Created by Chris Brown (OhBlihv) on 1/22/2017.
 */
@RequiredArgsConstructor
public class RegisteredListener<T extends Event>
{
	
	@Getter
	private boolean registered = true;
	
	public final Class<T>[] events;
	
	public final Listener listener; //Blank
	
	private EventAction<T> eventAction = null;
	
	private World forWorld = null;
	
	private boolean cancel;
	
	public RegisteredListener<T> cancel()
	{
		this.cancel = true;
		
		return this;
	}
	
	public RegisteredListener<T> forWorld(World world)
	{
		this.forWorld = world;
		
		return this;
	}
	
	public RegisteredListener<T> action(EventAction<T> eventAction)
	{
		this.eventAction = eventAction;
		
		return this;
	}
	
	public void doAction(T event)
	{
		if(eventAction == null)
		{
			if(!cancel)
			{
				throw new IllegalArgumentException("Event called with no registered Event Action! (And without being cancelled)");
			}
			return;
		}
		
		if(cancel && event instanceof Cancellable)
		{
			((Cancellable) event).setCancelled(true);
		}
		
		eventAction.call(event);
	}
	
	public void unregisterListener()
	{
		if(!isRegistered())
		{
			throw new IllegalStateException("Listener already disabled!");
		}
		
		registered = false;
		
		//System.out.println("Unregistering '" + Arrays.toString(events) + "'");
		HandlerList.unregisterAll(listener);
	}
	
}

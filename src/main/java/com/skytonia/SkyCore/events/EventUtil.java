package com.skytonia.SkyCore.events;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.util.BUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.weather.WeatherEvent;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Created by Chris Brown (OhBlihv) on 1/22/2017.
 */
public class EventUtil
{
	
	private static final Plugin plugin = SkyCore.getPluginInstance();

	@SafeVarargs
	public static <T extends Event> RegisteredListener<T> cancelEvents(Class<T>... events)
	{
		return cancelEvents(null, events);
	}
	
	@SafeVarargs
	public static <T extends Event> RegisteredListener<T> cancelEvents(World world, Class<T>... events)
	{
		return registerEvent(world, EventPriority.LOWEST, true, events).cancel();
	}

	public static <T extends Event> RegisteredListener<T> cancelEvents(World world, Class<T> event)
	{
		return registerEvent(world, EventPriority.LOWEST, true, event).cancel();
	}

	@SafeVarargs
	public static <T extends Event> RegisteredListener<T> registerEvent(final EventPriority priority, final boolean ignoreCancelled, final Class<T>... events)
	{
		return registerEvent(null, priority, ignoreCancelled, events);
	}

	@SafeVarargs
	public static <T extends Event> RegisteredListener<T> registerEvent(final World world, final EventPriority priority, final boolean ignoreCancelled,
	                                                                    final Class<T>... events)
	{
		RegisteredListener<T> registeredListener = new RegisteredListener<>(events, new Listener() {});
		
		//Create our event executor
		EventExecutor executor = (listener1, event) ->
		{
			//Search to see if the event provided is what we've requested
			boolean isofEventType = false;
			
			Class<? extends Event> eventClass = event.getClass();
			for(Class<? extends T> expectedEvent : events)
			{
				if(expectedEvent.isAssignableFrom(eventClass))
				{
					isofEventType = true;
					break;
				}
			}
			
			//This class is not of our type, but was still sent. Ignore.
			if(!isofEventType)
			{
				return;
			}
			
			//Ensure we restrict this event to the world we're listening to
			if(world != null)
			{
				World eventWorld = null;

				if(event instanceof PlayerEvent)
				{
					eventWorld = ((PlayerEvent) event).getPlayer().getWorld();
				}
				else if(event instanceof EntityEvent)
				{
					eventWorld = ((EntityEvent) event).getEntity().getWorld();
				}
				else if(event instanceof BlockEvent)
				{
					eventWorld = ((BlockEvent) event).getBlock().getWorld();
				}
				else if(event instanceof WorldEvent)
				{
					eventWorld = ((WorldEvent) event).getWorld();
				}
				else if(event instanceof WeatherEvent)
				{
					eventWorld = ((WeatherEvent) event).getWorld();
				}
				else if(event instanceof InventoryInteractEvent)
				{
					eventWorld = ((InventoryInteractEvent) event).getWhoClicked().getWorld();
				}
				else
				{
					BUtil.log("Unsupported world-supporting event: '" + event.getClass().getSimpleName() + "'");
				}
				
				if(eventWorld == null || !eventWorld.getName().equals(world.getName()))
				{
					//BUtil.log((eventWorld == null ? "event world null" : "'" + eventWorld.getName() + "' != '" + world.getName() + "'"));
					//No message. Just don't listen.
					return;
				}
			}
			
			try
			{
				registeredListener.doAction((T) event);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		};
		
		//Register our executor/listener to the events
		PluginManager pluginManager = Bukkit.getPluginManager();
		for(Class<? extends T> event : events)
		{
			pluginManager.registerEvent(event, registeredListener.listener, priority, executor, plugin, ignoreCancelled);
		}
		
		//Unregister when the plugin disables
		pluginManager.registerEvent(PluginDisableEvent.class, registeredListener.listener, EventPriority.MONITOR, (listener12, event) ->
		{
			
			PluginDisableEvent disableEvent = (PluginDisableEvent) event;
			if(disableEvent.getPlugin().equals(plugin))
			{
				registeredListener.unregisterListener();
			}
			
		}, plugin, false);
		
		return registeredListener;
	}
	
}

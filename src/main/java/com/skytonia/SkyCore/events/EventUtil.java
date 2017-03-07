package com.skytonia.SkyCore.events;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.util.BUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Chris Brown (OhBlihv) on 1/22/2017.
 */
public class EventUtil
{
	
	private static final Plugin plugin = SkyCore.getPluginInstance();
	
	public static <T extends Event> RegisteredListener<T> cancelEvents(Class<T>... events)
	{
		return cancelEvents(null, events);
	}
	
	public static <T extends Event> RegisteredListener<T> cancelEvents(World world, Class<T>... events)
	{
		return registerEvent(world, EventPriority.LOWEST, true, events).cancel();
	}
	
	public static <T extends Event> RegisteredListener<T> registerEvent(final EventPriority priority, final boolean ignoreCancelled, final Class<T>... events)
	{
		return registerEvent(null, priority, ignoreCancelled, events);
	}
	
	public static <T extends Event> RegisteredListener<T> registerEvent(final World world, final EventPriority priority, final boolean ignoreCancelled, final Class<T>... events)
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
				
				//Attempt to directly get the world
				{
					Method worldMethod;
					try
					{
						worldMethod = event.getClass().getMethod("getWorld");
						
						eventWorld = (World) worldMethod.invoke(event);
					}
					catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
					{
						//getWorld Not Found
					}
				}
				
				//Search for getPlayer() to run through
				if(eventWorld == null)
				{
					Method playerMethod;
					try
					{
						try
						{
							playerMethod = event.getClass().getMethod("getPlayer");
						}
						catch(NoSuchMethodException e)
						{
							//Some events have their player method at 'getEntity'
							playerMethod = event.getClass().getMethod("getEntity");
						}
						
						eventWorld = ((Entity) playerMethod.invoke(event)).getWorld();
					}
					catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
					{
						//getWorld Not Found
					}
				}
				
				//Block-Based events
				if(eventWorld == null)
				{
					Method blockMethod;
					try
					{
						try
						{
							blockMethod = event.getClass().getMethod("getBlock");
						}
						catch(NoSuchMethodException e)
						{
							//Some events have their player method at 'getEntity'
							blockMethod = event.getClass().getMethod("getSource");
						}
						
						eventWorld = ((Block) blockMethod.invoke(event)).getWorld();
					}
					catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
					{
						//getWorld Not Found
					}
				}
				
				if(eventWorld == null)
				{
					BUtil.logInfo("World was enforced for event " + event.getClass().getSimpleName() + " but the world could not be found.");
					for(Method method : event.getClass().getMethods())
					{
						BUtil.logInfo(method.getName() + "(" + Arrays.toString(method.getParameterTypes()) + ")");
					}
					BUtil.logInfo("-----------------------");
					for(Method method : event.getClass().getDeclaredMethods())
					{
						BUtil.logInfo(method.getName() + "(" + Arrays.toString(method.getParameterTypes()) + ")");
					}
					return; //Ignore event.
				}
				
				if(!eventWorld.getName().equals(world.getName()))
				{
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

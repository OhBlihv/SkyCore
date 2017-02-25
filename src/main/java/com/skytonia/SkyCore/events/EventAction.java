package com.skytonia.SkyCore.events;

import org.bukkit.event.Event;

/**
 * Created by Chris Brown (OhBlihv) on 1/22/2017.
 */
public interface EventAction<T extends Event>
{
	
	void call(T event);
	
}

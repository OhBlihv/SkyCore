package com.skytonia.SkyCore.packets;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 12/20/2016.
 */
public class Persistence
{
	
	public enum PersistingType
	{
		
		ACTION_BAR,
		TITLE;
		
	}
	
	private final Map<PersistingType, PersistingRunnable> persistingRunnables = new EnumMap<>(PersistingType.class);
	
	public void addOrReplaceRunnable(PersistingType persistingType, long tickDelay, int executions, Runnable runnable)
	{
		if(persistingRunnables.containsKey(persistingType))
		{
			persistingRunnables.get(persistingType).cancelRunnable();
		}
		
		persistingRunnables.put(persistingType, new PersistingRunnable(tickDelay, executions, runnable));
	}
	
	public void removeRunnable(PersistingType persistingType)
	{
		PersistingRunnable runnable = persistingRunnables.remove(persistingType);
		if(runnable != null)
		{
			runnable.cancelRunnable();
		}
	}
	
	public boolean isEmpty()
	{
		return persistingRunnables.isEmpty();
	}
	
}

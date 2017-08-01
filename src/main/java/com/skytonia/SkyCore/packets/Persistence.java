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
	//private final Map<PersistingType, TreeSet<PersistingRunnable>> queuedRunnables = new EnumMap<>(PersistingType.class);
	
	public void addOrReplaceRunnable(PersistingType persistingType, long tickDelay, int executions, TextRunnable runnable)
	{
		if(executions <= 0)
		{
			return; //No possible executions.
		}
		
		if(persistingRunnables.containsKey(persistingType))
		{
			persistingRunnables.get(persistingType).cancelRunnable();
		}
		
		PersistingRunnable persistingRunnable = new PersistingRunnable(this, persistingType, tickDelay, executions, runnable);
		persistingRunnables.put(persistingType, persistingRunnable);
		
		persistingRunnable.startRunnable();
	}
	
	public void removeRunnable(PersistingType persistingType)
	{
		if(persistingType == null)
		{
			return;
		}
		
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

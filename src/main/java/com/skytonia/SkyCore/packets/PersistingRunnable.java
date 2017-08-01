package com.skytonia.SkyCore.packets;

import com.skytonia.SkyCore.SkyCore;
import org.bukkit.Bukkit;

/**
 * Created by Chris Brown (OhBlihv) on 12/20/2016.
 */
public class PersistingRunnable
{
	
	private final Persistence parent;
	private final Persistence.PersistingType persistingType;
	
	private int taskId = -1;
	private int executionCount = 0;
	private final int maxExecutions;
	
	private final long tickDelay;
	private final TextRunnable runnable;
	
	public PersistingRunnable(Persistence parent, Persistence.PersistingType persistingType,
	                          long tickDelay, int executions, TextRunnable runnable)
	{
		this.parent = parent;
		this.persistingType = persistingType;
		
		this.maxExecutions = executions;
		
		this.tickDelay = tickDelay;
		this.runnable = runnable;
	}
	
	public void startRunnable()
	{
		if(taskId != -1)
		{
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
		
		//Run immediately, then every 'tickDelay' ticks.
		this.taskId = Bukkit.getScheduler().runTaskTimer(SkyCore.getPluginInstance(), () ->
		{
			try
			{
				if(!runnable.run())
				{
					cancelRunnable();
					return;
				}
				
				if(++executionCount == maxExecutions)
				{
					cancelRunnable();
				}
			}
			//Just in case.
			catch(Exception e)
			{
				cancelRunnable();
			}
		}, 1, tickDelay).getTaskId();
	}
	
	public void cancelRunnable()
	{
		if(taskId != -1)
		{
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
		
		parent.removeRunnable(persistingType);
	}
	
}

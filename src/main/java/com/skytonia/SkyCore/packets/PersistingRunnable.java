package com.skytonia.SkyCore.packets;

import com.skytonia.SkyCore.SkyCore;
import org.bukkit.Bukkit;

/**
 * Created by Chris Brown (OhBlihv) on 12/20/2016.
 */
public class PersistingRunnable
{
	
	private int taskId;
	private int executionCount = 0,
				maxExecutions;
	
	public PersistingRunnable(long tickDelay, int executions, Runnable runnable)
	{
		//No executions necessary.
		if(executions <= 0)
		{
			return;
		}
		
		this.maxExecutions = executions;
		
		//Run immediately, then every 'tickDelay' ticks.
		this.taskId = Bukkit.getScheduler().runTaskTimer(SkyCore.getPluginInstance(), () ->
		{
			try
			{
				runnable.run();
				
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
	}
	
}

package com.skytonia.SkyCore.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.TimeUnit;

/**
 * Created by Chris Brown (OhBlihv) on 1/20/2017.
 */
@RequiredArgsConstructor
public class RunnableShorthand
{
	
	private static final BukkitScheduler scheduler = Bukkit.getScheduler();

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RunnableBuilder
	{
		private final Plugin assignedPlugin;
		
		public RunnableShorthand with(Runnable runnable)
		{
			return new RunnableShorthand(assignedPlugin, runnable);
		}
		
	}
	
	private final Plugin assignedPlugin;
	private final Runnable runnable;
	
	@Getter
	private int taskId = -1;
	
	public static RunnableBuilder forThis()
	{
		return new RunnableBuilder(BUtil.getCallingJavaPlugin());
	}
	
	public static RunnableBuilder forPlugin(Plugin plugin)
	{
		return new RunnableBuilder(plugin);
	}
	
	public boolean isRunning()
	{
		return taskId != -1 && scheduler.isCurrentlyRunning(taskId);
	}
	
	public boolean isCancelled()
	{
		return taskId == -1;
	}
	
	public void ensureSync()
	{
		if(!Bukkit.isPrimaryThread())
		{
			scheduler.runTask(assignedPlugin, runnable);
		}
		else
		{
			runnable.run();
		}
	}
	
	public void ensureASync()
	{
		if(Bukkit.isPrimaryThread())
		{
			scheduler.runTaskAsynchronously(assignedPlugin, runnable);
		}
		else
		{
			runnable.run();
		}
	}
	
	public int runTimer(int delay, TimeUnit delayUnit, int timer, TimeUnit timerUnit)
	{
		return runTimer(delayUnit.toSeconds(delay) * 20L, timerUnit.toSeconds(timer) * 20L);
	}
	
	public int runTimer(long delay, long timer)
	{
		return scheduler.runTaskTimer(assignedPlugin, runnable, delay, timer).getTaskId();
	}
	
	public int runNextTick()
	{
		return runTask(1L);
	}
	
	public int runTask(long delay)
	{
		return scheduler.runTaskLater(assignedPlugin, runnable, delay).getTaskId();
	}
	
	public int runTaskLater(long delay)
	{
		return runTask(delay);
	}
	
	public int runASync()
	{
		return runTaskASync(0L);
	}
	
	public int runTaskASync(long delay)
	{
		return scheduler.runTaskLaterAsynchronously(assignedPlugin, runnable, delay).getTaskId();
	}
	
	public int runTimerASync(int delay, TimeUnit delayUnit, int timer, TimeUnit timerUnit)
	{
		switch(timerUnit)
		{
			case MICROSECONDS:
			case NANOSECONDS:
			case MILLISECONDS:
				throw new IllegalArgumentException("Unable to convert from " + timerUnit.name() + " to seconds! Ignoring Task.");
		}
		
		return runTimerASync(delayUnit.toSeconds(delay) * 20L, timerUnit.toSeconds(timer) * 20L);
	}
	
	public int runTimerASync(long delay, long timer)
	{
		return scheduler.runTaskTimerAsynchronously(assignedPlugin, runnable, delay, timer).getTaskId();
	}
	
}

package com.skytonia.SkyCore.cosmetics;

import com.skytonia.SkyCore.cosmetics.objects.ActiveCosmetic;
import com.skytonia.SkyCore.util.BUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Chris Brown (OhBlihv) on 26/05/2016.
 */
public class CosmeticThread extends Thread
{
	
	private static CosmeticThread instance = null;
	public static CosmeticThread getInstance()
	{
		if(instance == null)
		{
			instance = new CosmeticThread();
		}
		return instance;
	}

	private static final String THREAD_PREFIX = "[SkyCore-Cosmetic-Thread] ";

	@Setter
	private volatile boolean isRunning = false;

	//Thread Variables
	private static final long   WAIT_MILLIS = 50L, //Wait 1 tick (MC) between loops
								EMPTY_TIMEOUT_TICKS = 100L; //If we're empty for 5 seconds (100 ticks) close.

	@Getter
	private long currentTick = 1L;
	private long startEmptyTick = 0L; //Ticks since we've had nothing to process
	private long lastStartTick = 0L;

	//Cosmetics
	@Getter
	private final CopyOnWriteArrayList<ActiveCosmetic> cosmeticSet = new CopyOnWriteArrayList<>();

	private CosmeticThread()
	{
		super("SkyCore-Cosmetic-Thread");
	}

	@Override
	public void run()
	{
		if(isRunning)
		{
			return;
		}
		
		isRunning = true;
		
		while(isRunning)
		{
			if(cosmeticSet.isEmpty())
			{
				if(startEmptyTick == 0)
				{
					startEmptyTick = currentTick;
				}
				else if(currentTick - startEmptyTick > EMPTY_TIMEOUT_TICKS)
				{
					//We've been empty for two long.
					//Stop thrashing and sleep forever.
					printMessage("Cosmetic Thread Empty, Sleeping Permanently.");
					isRunning = false;
					this.interrupt();
				}
				
				doSleep();
				continue;
			}
			
			startEmptyTick = 0;
			lastStartTick = System.currentTimeMillis();
			
			Deque<ActiveCosmetic> cosmeticsToRemove = new ArrayDeque<>();

			for(ActiveCosmetic cosmetic : cosmeticSet)
			{
				if(currentTick % cosmetic.updateRate != 0)
				{
					continue;
				}
				
				if(cosmetic.canUpdateNearbyPlayers(currentTick))
				{
					cosmetic.updateNearbyPlayers();
				}

				try
				{
					cosmetic.onTick(currentTick);
					
					//If this cosmetic is a single-use cosmetic
					if(cosmetic.shouldRemove(currentTick))
					{
						cosmeticsToRemove.add(cosmetic);
					}
				}
				catch(Throwable e)
				{
					printMessage("An unexpected error occurred while ticking cosmetic.");
					e.printStackTrace();
					
					cosmeticsToRemove.add(cosmetic);
				}
			}
			
			//Clean up any completed/invalid cosmetics
			if(!cosmeticsToRemove.isEmpty())
			{
				cosmeticSet.removeAll(cosmeticsToRemove);
				for(ActiveCosmetic activeCosmetic : cosmeticsToRemove)
				{
					activeCosmetic.onRemove();
				}
			}

			++currentTick;
			doSleep(); //Wait until the next tick
		}
		this.interrupt();
		
		isRunning = false;
	}
	
	public void doSleep()
	{
		try
		{
			long sleepTime = WAIT_MILLIS - (System.currentTimeMillis() - lastStartTick);
			if(sleepTime <= 0)
			{
				BUtil.logInfo("Target Sleep Time <= 0! Expect Performance Drop on Cosmetics (Value=" + sleepTime + ")");
			}
			else
			{
				sleep(sleepTime);
			}
		}
		catch(InterruptedException e)
		{
			//Ignore the exception if the thread has already been set to terminate
			if(isRunning)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void printMessage(String message)
	{
		System.out.println(THREAD_PREFIX + message);
	}
	
	public void addCosmetic(ActiveCosmetic activeCosmetic)
	{
		try
		{
			//Allow the first tick to process correctly
			activeCosmetic.updateNearbyPlayers();
			activeCosmetic.onTick(0L);
			
			cosmeticSet.add(activeCosmetic);
			
			//Kickstart!
			if(!isRunning)
			{
				printMessage("Thread Halted. Kickstarting...");
				start();
			}
		}
		catch(Throwable e)
		{
			printMessage("Error occurred adding cosmetic for " +
				             (activeCosmetic != null && activeCosmetic.getActivatingPlayer() != null ? activeCosmetic.getActivatingPlayer().getName() : "'null player'") +
				             ". This cosmetic will not be applied.");
			e.printStackTrace();
		}
	}
	
	public void removeCosmetic(ActiveCosmetic activeCosmetic)
	{
		cosmeticSet.remove(activeCosmetic);
		activeCosmetic.onRemove();
	}
	
}

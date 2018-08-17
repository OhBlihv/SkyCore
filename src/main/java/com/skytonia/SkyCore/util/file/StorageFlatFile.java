package com.skytonia.SkyCore.util.file;

import com.skytonia.SkyCore.util.BUtil;
import org.bukkit.Bukkit;

/**
 * Created by Chris Brown (OhBlihv) on 12/28/2016.
 */
public abstract class StorageFlatFile extends FlatFile
{
	
	protected StorageFlatFile(String fileName, String owningPlugin, long saveTicks)
	{
		super(fileName, owningPlugin);
		
		load();
		
		if(saveTicks <= 300L)
		{
			BUtil.log("(" + fileName + ") given saveTicks below 15 seconds '" + saveTicks + " ticks'");
			saveTicks = 300L;
		}
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::save, saveTicks, saveTicks);
	}
	
	public abstract void save();
	
	public abstract void load();
	
}

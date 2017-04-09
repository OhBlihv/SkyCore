package com.skytonia.SkyCore.cosmetics.pets;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 4/9/2017.
 */
public class PetUtil
{
	
	private static final Map<EntityType, WrappedDataWatcher> watcherCache = new HashMap<>();
	
	public static WrappedDataWatcher getDefaultWatcher(World world, EntityType type)
	{
		WrappedDataWatcher watcher = watcherCache.get(type);
		if(watcher != null)
		{
			return watcher.deepClone();
		}
		
		Entity entity = world.spawnEntity(new Location(world, 0, 256, 0), type);
		watcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();
		watcherCache.put(type, watcher);
		
		entity.remove();
		return watcher;
	}
	
	public static int getNextEntityId()
	{
		int tempId;
		try
		{
			Field field = Entity.class.getDeclaredField("entityCount");
			field.setAccessible(true);
			
			tempId = field.getInt(null);
			field.setInt(null, tempId + 1);
		}
		catch(NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
			tempId = 1;
		}
		return tempId;
	}
	
}

package com.skytonia.SkyCore.cosmetics.pets.fakeentities;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

/**
 * Created by Chris Brown (OhBlihv) on 4/9/2017.
 */
public class FakeZombie extends FakeLivingEntity
{
	
	private final boolean isBaby;
	
	public FakeZombie(EntityType entityType, Location location, boolean isBaby)
	{
		super(entityType, location);
		
		this.isBaby = isBaby;
	}
	
	@Override
	public WrappedDataWatcher getMetadata()
	{
		WrappedDataWatcher metadata = super.getMetadata();
		
		//Defaults to off
		if(isBaby)
		{
			metadata.setObject(12, Boolean.TRUE);
		}
		
		return metadata;
	}
	
}

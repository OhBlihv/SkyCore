package com.skytonia.SkyCore.cosmetics.pets.fakeentities;

import com.comphenix.packetwrapper.AbstractPacket;
import com.skytonia.SkyCore.cosmetics.pets.fakeentities.pathfinders.FakePathfinderGoal;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

/**
 * Created by Chris Brown (OhBlihv) on 4/9/2017.
 */
public class FakeLivingEntity extends FakeEntity
{
	
	@Getter
	private FakePathfinderGoal pathfinderGoal;
	
	public FakeLivingEntity(EntityType entityType, Location location)
	{
		super(entityType, location);
	}
	
	public void setPathfinderGoal(FakePathfinderGoal pathfinderGoal)
	{
		this.pathfinderGoal = pathfinderGoal;
		
		updateNavigation();
	}
	
	public AbstractPacket updateNavigation()
	{
		return pathfinderGoal.updateNav();
	}
	
	
}

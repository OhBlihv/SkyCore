package com.skytonia.SkyCore.cosmetics.pets.fakeentities.pathfinders;

import com.comphenix.packetwrapper.AbstractPacket;

/**
 * Created by Chris Brown (OhBlihv) on 4/9/2017.
 */
public abstract class FakePathfinderGoal
{
	
	public FakePathfinderGoal()
	{
	
	}
	
	public abstract boolean canUpdate();
	
	public AbstractPacket updateNav()
	{
		return null;
	}
	
}

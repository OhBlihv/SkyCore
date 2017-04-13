package com.skytonia.SkyCore.cosmetics.pets.fakeentities.pathfinders;

import com.comphenix.packetwrapper.AbstractPacket;
import com.skytonia.SkyCore.cosmetics.pets.fakeentities.FakeLivingEntity;
import net.minecraft.server.v1_9_R2.EntityPlayer;

/**
 * Created by Chris Brown (OhBlihv) on 4/9/2017.
 */

public class FakePathfinderGoalFollowOwner extends FakePathfinderGoal
{
	
	private static final int UPDATE_DELAY = 10;
	
	private final FakeLivingEntity entity;
	private final EntityPlayer owner;
	private final float maxRange;
	private final float minRange;
	
	private int lastUpdate = 0;
	
	public FakePathfinderGoalFollowOwner(FakeLivingEntity entity, EntityPlayer player, float minRange, float maxRange)
	{
		this.entity = entity;
		this.owner = player;
		this.minRange = minRange;
		this.maxRange = maxRange;
	}
	
	public boolean canUpdate()
	{
		double distance;
		
		return owner != null && !owner.isSpectator() &&
			       (distance = this.entity.getLocation().distance(owner.getBukkitEntity().getLocation())) >= (double) this.minRange &&
			        distance < (double) maxRange;
	}
	
	public AbstractPacket updateNav()
	{
		float yaw   = (float) Math.atan2((this.entity.getCurrentX() - this.owner.locX), (this.entity.getCurrentZ() - this.owner.locZ));
		float pitch = (float) Math.atan2(0, (this.entity.getCurrentY() - this.owner.locY));
		
		//Only update target location every 10 ticks
		if(--lastUpdate <= 0)
		{
			lastUpdate = UPDATE_DELAY;
			
			//Update entities movement goal/target
			this.entity.updateTargetLocation(this.owner.getWorld().getWorld(),
			                                 this.owner.locX,
			                                 this.owner.locY - 1.0D,
			                                 this.owner.locZ,
			                                 yaw,
			                                 pitch);
		}
		else
		{
			//Move entity
			this.entity.updateCurrentLocation();
		}
		
		return this.entity.getTeleportPacket();
		
		/*return this.entity.moveEntity(this.owner.locX,
		                              this.owner.locY - 1.0D,
		                              this.owner.locZ,
		                              yaw,
		                              (float) this.entity.getCurrentPitch());*/
	}
}


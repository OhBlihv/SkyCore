package com.skytonia.SkyCore.cosmetics.pets.pathfinders;

import net.minecraft.server.v1_9_R2.EntityInsentient;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.PathEntity;
import net.minecraft.server.v1_9_R2.PathfinderGoal;
import org.bukkit.Location;

/**
 * Created by Chris Brown (OhBlihv) on 4/10/2017.
 */
public class PathfinderGoalFollowOwner extends PathfinderGoal implements PetPathfinder
{
	
	private final EntityInsentient entity;
	private final EntityPlayer owningPlayer;
	private final double speed;
	private int d;
	
	private PathEntity path;
	
	public PathfinderGoalFollowOwner(EntityInsentient var1, EntityPlayer owningPlayer, double speed)
	{
		this.entity = var1;
		this.owningPlayer = owningPlayer;
		this.speed = speed;
	}
	
	public boolean a()
	{
		if(this.owningPlayer == null)
		{
			return path != null;
		}
		
		Location playerLocation = owningPlayer.getBukkitEntity().getLocation();
		
		path = entity.getNavigation().a(playerLocation.getX() + 1.0D, playerLocation.getY(), playerLocation.getZ() + 1.0D);
		
		return path != null;
	}
	
	public boolean b()
	{
		if(!this.owningPlayer.isAlive())
		{
			return false;
		}
		else
		{
			double var1 = this.entity.h(this.owningPlayer);
			return var1 >= 6.0D && var1 <= 256.0D;
		}
	}
	
	public void c()
	{
		this.d = 0;
	}
	
	public void d()
	{
		//
	}
	
	public void e()
	{
		if(--this.d <= 0)
		{
			this.d = 5;
			this.entity.getNavigation().a(path, this.speed);
		}
	}
}


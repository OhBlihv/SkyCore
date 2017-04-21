package com.skytonia.SkyCore.cosmetics.pets.pathfinders;

import net.minecraft.server.v1_9_R2.EntityInsentient;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.PathfinderGoal;

public class PathfinderGoalLookAtOwner extends PathfinderGoal implements PetPathfinder
{
	
	protected EntityInsentient entity;
	protected EntityPlayer owningPlayer;
	protected float c;
	private int e;
	private final float LOOK_CHANCE = 0.25f;
	
	public PathfinderGoalLookAtOwner(EntityInsentient entity, EntityPlayer owningPlayer, float var3)
	{
		this.entity = entity;
		this.owningPlayer = owningPlayer;
		this.c = var3;
		this.a(2);
	}
	
	public boolean a()
	{
		return this.entity.getRandom().nextFloat() < this.LOOK_CHANCE && this.owningPlayer != null;
	}
	
	public boolean b()
	{
		return this.owningPlayer.isAlive() && (!(this.entity.h(this.owningPlayer) > (double) (this.c * this.c)) && this.e > 0);
	}
	
	public void c()
	{
		this.e = 40 + this.entity.getRandom().nextInt(40);
	}
	
	public void d()
	{
		this.owningPlayer = null;
	}
	
	public void e()
	{
		this.entity.getControllerLook().a(this.owningPlayer.locX,
		                                  this.owningPlayer.locY + (double) this.owningPlayer.getHeadHeight(),
		                                  this.owningPlayer.locZ,
		                                  (float) this.entity.cF(), (float) this.entity.N());
		--this.e;
	}
}

package com.skytonia.SkyCore.firework;

import net.minecraft.server.v1_11_R1.EntityFireworks;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;

/**
 * Created by Chris Brown (OhBlihv) on 18/09/2016.
 */
public class CustomEntityFirework_1_11_R1 extends EntityFireworks implements ICustomEntityFirework
{
	
	public CustomEntityFirework_1_11_R1(World world, int tickDuration)
	{
		super(((CraftWorld) world).getHandle());
		
		this.expectedLifespan = tickDuration;
		
		a(0.25F, 0.25F);
	}
	
	@Override
	public void A_()
	{
		if(this.expectedLifespan <= 1)
		{
			this.world.broadcastEntityEffect(this, (byte) 17);
			this.die();
		}
		else
		{
			super.A_();
		}
	}
	
	/*
	 * Interface Methods
	 */
	
	@Override
	public void addFirework()
	{
		world.addEntity(this);
	}
	
}

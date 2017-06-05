package com.skytonia.SkyCore.firework;

import net.minecraft.server.v1_12_R1.EntityFireworks;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

/**
 * Created by Chris Brown (OhBlihv) on 5/27/2017.
 */
public class CustomEntityFirework_1_12_R1 extends EntityFireworks implements ICustomEntityFirework
{
	
	public CustomEntityFirework_1_12_R1(World world, int tickDuration)
	{
		super(((CraftWorld) world).getHandle());
		
		this.expectedLifespan = tickDuration;
		
		a(0.25F, 0.25F);
	}
	
	@Override
	public void B_()
	{
		if(this.expectedLifespan <= 1)
		{
			this.world.broadcastEntityEffect(this, (byte) 17);
			this.die();
		}
		else
		{
			super.B_();
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

package com.skytonia.SkyCore.firework;

import net.minecraft.server.v1_13_R1.EntityFireworks;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;

public class CustomEntityFirework_1_13_R1 extends EntityFireworks implements ICustomEntityFirework
{

	public CustomEntityFirework_1_13_R1(World world, int tickDuration)
	{
		super(((CraftWorld) world).getHandle());

		this.expectedLifespan = tickDuration;

		a(0.25F, 0.25F);
	}

	@Override
	public void tick()
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


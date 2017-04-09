package com.skytonia.SkyCore.cosmetics.pets.entities.pathfinders;

import com.skytonia.SkyCore.cosmetics.pets.entities.FakeLivingEntity;
import net.minecraft.server.v1_9_R2.Block;
import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.Blocks;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.IBlockData;
import net.minecraft.server.v1_9_R2.MathHelper;
import net.minecraft.server.v1_9_R2.World;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;

/**
 * Created by Chris Brown (OhBlihv) on 4/9/2017.
 */

public class FakePathfinderGoalFollowOwner extends FakePathfinderGoal
{
	
	private FakeLivingEntity entity;
	private EntityPlayer owner;
	private final World world;
	private int lastNav;
	float b;
	float c;
	
	public FakePathfinderGoalFollowOwner(FakeLivingEntity entity, EntityPlayer player, float minRange, float maxRange)
	{
		this.entity = entity;
		this.owner = player;
		this.world = ((CraftWorld) entity.getWorld()).getHandle();
		this.c = minRange;
		this.b = maxRange;
		this.a(3);
	}
	
	public boolean a()
	{
		return owner != null && !owner.isSpectator() && !(this.entity.getLocation().distance(owner.getBukkitEntity().getLocation()) < (double) (this.c * this.c));
	}
	
	public boolean b()
	{
		return this.entity.getLocation().distance(owner.getBukkitEntity().getLocation()) < (double) (this.b * this.b);
	}
	
	private boolean isNavigatable(BlockPosition var1)
	{
		IBlockData var2 = this.world.getType(var1);
		Block var3 = var2.getBlock();
		return var3 == Blocks.AIR || !var2.h();
	}
	
	public void updateNav()
	{
		if(--this.lastNav <= 0)
		{
			this.lastNav = 10;
			if(this.entity.getLocation().distance(owner.getBukkitEntity().getLocation()) >= 144.0D)
			{
				int var1 = MathHelper.floor(this.owner.locX) - 2;
				int var2 = MathHelper.floor(this.owner.locZ) - 2;
				int var3 = MathHelper.floor(this.owner.getBoundingBox().b);
				
				for(int var4 = 0; var4 <= 4; ++var4)
				{
					for(int var5 = 0; var5 <= 4; ++var5)
					{
						if((var4 < 1 || var5 < 1 || var4 > 3 || var5 > 3) &&
							   this.world.getType(new BlockPosition(var1 + var4, var3 - 1, var2 + var5)).q() &&
							   this.isNavigatable(new BlockPosition(var1 + var4, var3, var2 + var5)) &&
							   this.isNavigatable(new BlockPosition(var1 + var4, var3 + 1, var2 + var5)))
						{
							this.entity.moveEntity((double) ((float) (var1 + var4) + 0.5F),
							                       (double) var3,
							                       (double) ((float) (var2 + var5) + 0.5F),
							                       (float) this.entity.getCurrentYaw(),
							                       (float) this.entity.getCurrentPitch());
							return;
						}
					}
				}
			}
		}
	}
}


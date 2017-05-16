package com.skytonia.SkyCore.firework;

import net.minecraft.server.v1_7_R4.EntityFireworks;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityStatus;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CustomEntityFirework_1_7_R4 extends EntityFireworks implements ICustomEntityFirework
{
	
	Player[] players = null;
	
	public CustomEntityFirework_1_7_R4(World world, Player... p)
	{
		super(((CraftWorld) world).getHandle());
		players = p;
		a(0.25F, 0.25F);
	}
	
	boolean gone = false;
	
	@Override
	public void h()
	{
		if(gone)
		{
			return;
		}
		
		if(!world.isStatic)
		{
			gone = true;
			
			if(players != null)
			{
				if(players.length > 0)
				{
					for(Player player : players)
					{
						(((CraftPlayer) player).getHandle()).playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte) 17));
					}
				}
				else
				{
					world.broadcastEntityEffect(this, (byte) 17);
				}
			}
			
			this.die();
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

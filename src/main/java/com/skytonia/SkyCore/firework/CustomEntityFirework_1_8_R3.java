package com.skytonia.SkyCore.firework;

import net.minecraft.server.v1_8_R3.EntityFireworks;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CustomEntityFirework_1_8_R3 extends EntityFireworks implements ICustomEntityFirework
{
	
	Player[] players = null;
	
	public CustomEntityFirework_1_8_R3(World world, Player... p)
	{
		super(((CraftWorld) world).getHandle());
		players = p;
		a(0.25F, 0.25F);
	}
	
	boolean gone = false;
	
	@Override
	public void t_()
	{
		if(gone)
		{
			return;
		}
		
		if(!world.isClientSide)
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

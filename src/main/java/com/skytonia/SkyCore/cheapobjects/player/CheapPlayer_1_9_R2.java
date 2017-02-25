package com.skytonia.SkyCore.cheapobjects.player;

import lombok.Getter;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.Packet;
import net.minecraft.server.v1_9_R2.PlayerConnection;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 7/08/2016.
 */
public class CheapPlayer_1_9_R2 extends CheapPlayer
{
	
	//Provide quick access to networking
	@Getter
	private PlayerConnection playerConnection;
	
	private EntityPlayer entityPlayer;
	
	public CheapPlayer_1_9_R2(Player player)
	{
		super(player);
		
		entityPlayer = ((CraftPlayer) player).getHandle();
		
		playerConnection = entityPlayer.playerConnection;
	}
	
	public void queuePacket(Object packet)
	{
		if(!(packet instanceof Packet))
		{
			//Ignore this for now.
			//Possibly throw an exception to narrow down the illegal calls?
			throw new IllegalArgumentException("queuePacket() expects a Packet, and was given a " + (packet != null ? packet.getClass().getName() : "null"));
		}
		
		playerConnection.sendPacket((Packet) packet);
	}
}

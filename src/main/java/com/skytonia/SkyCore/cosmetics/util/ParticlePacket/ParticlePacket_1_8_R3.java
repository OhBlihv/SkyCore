package com.skytonia.SkyCore.cosmetics.util.ParticlePacket;

import com.skytonia.SkyCore.cosmetics.util.ParticleEffect;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by Chris Brown (OhBlihv) on 8/08/2016.
 */
public class ParticlePacket_1_8_R3 extends ParticlePacket
{
	
	private PacketPlayOutWorldParticles particlePacket;
	
	public ParticlePacket_1_8_R3(ParticleEffect effect, float offsetX, float offsetY, float offsetZ, float speed, int amount, boolean longDistance, ParticleEffect.ParticleData data) throws IllegalArgumentException
	{
		super(effect, offsetX, offsetY, offsetZ, speed, amount, longDistance, data);
	}
	
	public ParticlePacket_1_8_R3(ParticleEffect effect, Vector direction, float speed, boolean longDistance, ParticleEffect.ParticleData data) throws IllegalArgumentException
	{
		super(effect, (float) direction.getX(), (float) direction.getY(), (float) direction.getZ(), speed, 0, longDistance, data);
	}
	
	public ParticlePacket_1_8_R3(ParticleEffect effect, ParticleEffect.ParticleColor color, boolean longDistance)
	{
		super(effect, color, longDistance);
	}
	
	@Override
	public void initialize(Location center)
	{
		//Only initialize once.
		if(particlePacket != null)
		{
			return;
		}
		
		int[] packetData = null;
		if(data != null)
		{
			packetData = data.getPacketData();
			if(effect != ParticleEffect.ITEM_CRACK)
			{
				packetData = new int[] { packetData[0] | (packetData[1] << 12) };
			}
		}
		
		particlePacket = new PacketPlayOutWorldParticles(
			                                                EnumParticle.a(effect.getId()),                 //a) Particle Name
			                                                longDistance,                                   //j) Long Distance Particle
			                                                (float) center.getX(),                          //b) X
			                                                (float) center.getY(),                          //c) Y
			                                                (float) center.getZ(),                          //d) Z
			                                                offsetX,                                        //updateNav) X Offset
			                                                offsetY,                                        //f) Y Offset
			                                                offsetZ,                                        //g) Z Offset
			                                                speed,                                          //h) Particle Speed
			                                                amount,                                         //i) Particle Amount
			                                                packetData                                      //k) Particle Data
		);
		
		serverVersion = 8;
	}
	
	@Override
	public void sendTo(Location center, Player player) throws PacketInstantiationException, PacketSendingException
	{
		initialize(center);
		
		//PlayerConnection is only used once per packet, so retrieving it here is a non-issue
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(particlePacket);
	}
	
}

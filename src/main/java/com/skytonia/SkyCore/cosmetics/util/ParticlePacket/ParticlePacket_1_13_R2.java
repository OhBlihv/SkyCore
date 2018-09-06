package com.skytonia.SkyCore.cosmetics.util.ParticlePacket;

import com.skytonia.SkyCore.cosmetics.util.ParticleEffect;
import net.minecraft.server.v1_13_R2.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_13_R2.CraftParticle;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by Chris Brown (OhBlihv) on 5/27/2017.
 */
public class ParticlePacket_1_13_R2 extends ParticlePacket
{

	private PacketPlayOutWorldParticles particlePacket;

	public ParticlePacket_1_13_R2(ParticleEffect effect, float offsetX, float offsetY, float offsetZ, float speed, int amount, boolean longDistance, ParticleEffect.ParticleData data) throws IllegalArgumentException
	{
		super(effect, offsetX, offsetY, offsetZ, speed, amount, longDistance, data);
	}

	public ParticlePacket_1_13_R2(ParticleEffect effect, Vector direction, float speed, boolean longDistance, ParticleEffect.ParticleData data) throws IllegalArgumentException
	{
		super(effect, (float) direction.getX(), (float) direction.getY(), (float) direction.getZ(), speed, 0, longDistance, data);
	}

	public ParticlePacket_1_13_R2(ParticleEffect effect, ParticleEffect.ParticleColor color, boolean longDistance)
	{
		super(effect, color, longDistance);
	}

	public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
		if (data != null && !particle.getDataType().isInstance(data)) {
			throw new IllegalArgumentException("data should be " + particle.getDataType() + " got " + data.getClass());
		}
		PacketPlayOutWorldParticles packetplayoutworldparticles = new PacketPlayOutWorldParticles(
			CraftParticle.toNMS(particle, data), true, (float) x, (float) y, (float) z,
			(float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count);

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
			CraftParticle.toNMS(Particle.valueOf(effect.name())),    //a) Particle Name
			longDistance,                                   //j) Long Distance Particle
			(float) center.getX(),                          //b) X
			(float) center.getY(),                          //c) Y
			(float) center.getZ(),                          //d) Z
			offsetX,                                        //?) X Offset
			offsetY,                                        //f) Y Offset
			offsetZ,                                        //g) Z Offset
			speed,                                          //h) Particle Speed
			amount//,                                         //i) Particle Amount
			//packetData                                      //k) Particle Data
		);

		serverVersion = 13;
	}

	@Override
	public void sendTo(Location center, Player player) throws PacketInstantiationException, PacketSendingException
	{
		initialize(center);

		//PlayerConnection is only used once per packet, so retrieving it here is a non-issue
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(particlePacket);
	}

}

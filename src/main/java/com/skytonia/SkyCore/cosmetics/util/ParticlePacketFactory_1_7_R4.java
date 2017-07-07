package com.skytonia.SkyCore.cosmetics.util;

import com.skytonia.SkyCore.cosmetics.util.ParticlePacket.ParticlePacket;
import com.skytonia.SkyCore.cosmetics.util.ParticlePacket.ParticlePacket_1_7_R4;
import org.bukkit.util.Vector;

/**
 * Created by Chris Brown (OhBlihv) on 8/08/2016.
 */
public class ParticlePacketFactory_1_7_R4 implements IParticlePacketFactory
{
	
	@Override
	public ParticlePacket getParticlePacket(ParticleEffect effect, float offsetX, float offsetY, float offsetZ,
	                                        float speed, int amount, boolean longDistance,
	                                        ParticleEffect.ParticleData data) throws IllegalArgumentException
	{
		return new ParticlePacket_1_7_R4(effect, offsetX, offsetY, offsetZ,
		                                 speed, amount, longDistance, data);
	}
	
	@Override
	public ParticlePacket getParticlePacket(ParticleEffect effect, Vector direction, float speed, boolean longDistance,
	                                        ParticleEffect.ParticleData data) throws IllegalArgumentException
	{
		return new ParticlePacket_1_7_R4(effect, direction, speed, longDistance, data);
	}
	
	@Override
	public ParticlePacket getParticlePacket(ParticleEffect effect, ParticleEffect.ParticleColor color, boolean longDistance)
	{
		return new ParticlePacket_1_7_R4(effect, color, longDistance);
	}
	
	@Override
	public int getServerVersion()
	{
		return 7;
	}
	
}

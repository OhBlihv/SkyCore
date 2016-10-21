package com.skytonia.SkyCore.cosmetics.util;

import com.skytonia.SkyCore.cosmetics.util.ParticlePacket.ParticlePacket;
import org.bukkit.util.Vector;

/**
 * Created by Chris Brown (OhBlihv) on 8/08/2016.
 */
public interface IParticlePacketFactory
{
	
	ParticlePacket getParticlePacket(
		                                ParticleEffect effect, float offsetX, float offsetY, float offsetZ,
		                                float speed, int amount, boolean longDistance,
		                                ParticleEffect.ParticleData data) throws IllegalArgumentException;
	
	ParticlePacket getParticlePacket(
		                                ParticleEffect effect, Vector direction, float speed, boolean longDistance,
		                                ParticleEffect.ParticleData data) throws IllegalArgumentException;
	
	ParticlePacket getParticlePacket(ParticleEffect effect, ParticleEffect.ParticleColor color, boolean longDistance);
	
}

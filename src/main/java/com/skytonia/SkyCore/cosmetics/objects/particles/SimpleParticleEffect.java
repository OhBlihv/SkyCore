package com.skytonia.SkyCore.cosmetics.objects.particles;

import com.skytonia.SkyCore.cheapobjects.player.CheapPlayer;
import com.skytonia.SkyCore.cosmetics.util.ParticleEffect;
import org.bukkit.Location;

import java.util.Collection;

/**
 * Created by Chris Brown (OhBlihv) on 8/08/2016.
 */
public class SimpleParticleEffect extends BaseParticleEffect
{
	
	public SimpleParticleEffect(String displayName, ParticleEffect particleEffect)
	{
		super(displayName, particleEffect);
	}
	
	@Override
	public void onTick(long tick, Location location)
	{
		//Any player-directed particle effects should be using CheapPlayers managed by an ActiveCosmetic and called by the CosmeticThread
		throw new IllegalArgumentException("This effect type does not support this call!");
	}
	
	@Override
	public void onTick(long tick, Location location, Collection<CheapPlayer> nearbyPlayers)
	{
		particleEffect.displayToCheapPlayer(0F, 0F, 0F, 0F, 1, location.add(0, height, 0), nearbyPlayers);
		
		if(length > 1)
		{
			
		}
	}
	
}

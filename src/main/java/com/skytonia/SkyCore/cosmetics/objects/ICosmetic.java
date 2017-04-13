package com.skytonia.SkyCore.cosmetics.objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Created by Chris Brown (OhBlihv) on 21/08/2016.
 */
public interface ICosmetic
{
	
	void onTick(long tick, Location location);
	
	void onTick(long tick, Location location, Collection<Player> nearbyPlayers);
	
	@Override
	boolean equals(Object object);
	
}

package com.skytonia.SkyCore.cosmetics.objects;

import com.skytonia.SkyCore.cheapobjects.player.CheapPlayer;
import org.bukkit.Location;

import java.util.Collection;

/**
 * Created by Chris Brown (OhBlihv) on 21/08/2016.
 */
public interface ICosmetic
{
	
	void onTick(long tick, Location location);
	
	void onTick(long tick, Location location, Collection<CheapPlayer> nearbyPlayers);
	
	@Override
	boolean equals(Object object);
	
}

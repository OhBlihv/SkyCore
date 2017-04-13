package com.skytonia.SkyCore.cosmetics.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 6/08/2016.
 */
@RequiredArgsConstructor
public abstract class BaseCosmetic implements ICosmetic
{
	
	@Getter
	private final String cosmeticName;
	
	@Getter
	private final int updateRate;
	
	public Location getLocation()
	{
		return null;
	}
	
	public boolean showToNearbyPlayer(Player player)
	{
		return true;
	}
	
	public boolean removeFromNearbyPlayer(Player player)
	{
		return true;
	}
	
	public abstract void removeCosmetic();
	
}

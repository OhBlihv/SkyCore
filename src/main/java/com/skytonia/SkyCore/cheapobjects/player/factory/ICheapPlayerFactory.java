package com.skytonia.SkyCore.cheapobjects.player.factory;

import com.skytonia.SkyCore.cheapobjects.player.CheapPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 9/08/2016.
 */
public interface ICheapPlayerFactory
{
	
	CheapPlayer getCheapPlayer(Player player);
	
}

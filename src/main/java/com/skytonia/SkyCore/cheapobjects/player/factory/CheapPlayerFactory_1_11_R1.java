package com.skytonia.SkyCore.cheapobjects.player.factory;

import com.skytonia.SkyCore.cheapobjects.player.CheapPlayer;
import com.skytonia.SkyCore.cheapobjects.player.CheapPlayer_1_11_R1;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 9/08/2016.
 */
public class CheapPlayerFactory_1_11_R1 implements ICheapPlayerFactory
{
	
	@Override
	public CheapPlayer getCheapPlayer(Player player)
	{
		return new CheapPlayer_1_11_R1(player);
	}
	
}

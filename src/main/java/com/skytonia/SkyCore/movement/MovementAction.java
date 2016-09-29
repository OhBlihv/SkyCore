package com.skytonia.SkyCore.movement;

import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public abstract class MovementAction
{
	
	public void onSuccessReceive(Player player, String server)
	{
		//TODO: Bungeecord send
	}
	
	public abstract void onFailReceive(Player player, String server, String response);
	
}

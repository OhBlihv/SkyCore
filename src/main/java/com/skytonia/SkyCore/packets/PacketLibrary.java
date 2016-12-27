package com.skytonia.SkyCore.packets;

import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 12/20/2016.
 */
public abstract class PacketLibrary
{
	
	public abstract void sendActionBar(Player player, String message, int lifespan);
	
	public abstract void sendTitle(Player player, String title, String subTitle, int persistTime, int fadeIn, int fadeOut);
	
}

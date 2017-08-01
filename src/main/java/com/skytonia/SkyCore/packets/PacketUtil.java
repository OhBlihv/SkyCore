package com.skytonia.SkyCore.packets;

import com.skytonia.SkyCore.util.StaticNMS;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 2/10/2016.
 */
public class PacketUtil
{
	
	public static void sendActionBar(Player player, String message, int lifespan)
	{
		StaticNMS.getPacketLibrary().sendActionBar(player, message, lifespan);
	}
	
	public static void sendTitle(Player player, String title, String subTitle, int persistTime, int fadeIn, int fadeOut)
	{
		StaticNMS.getPacketLibrary().sendTitle(player, title, subTitle, persistTime, fadeIn, fadeOut);
	}
	
}

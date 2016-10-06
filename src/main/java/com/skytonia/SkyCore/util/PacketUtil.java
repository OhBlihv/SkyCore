package com.skytonia.SkyCore.util;

import com.skytonia.SkyCore.SkyCore;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 2/10/2016.
 */
public class PacketUtil
{
	
	public static void sendActionBar(Player player, String message, int lifespan)
	{
		PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
		
		sendPacket(playerConnection, packetPlayOutChat);
		//Since we're updating every 2 seconds, we only need to send half as many updates
		for(int i = 1;i < ((lifespan / 2) + 1);i++)
		{
			Bukkit.getScheduler().runTaskLater(SkyCore.getInstance(), () ->
			{
				sendPacket(playerConnection, packetPlayOutChat);
			}, 40L * i); //Action bar lasts about 2-3 seconds. Send an update after 2 seconds to ensure it does not disappear
		}
	}
	
	private static void sendPacket(PlayerConnection playerConnection, Packet packet)
	{
		playerConnection.sendPacket(packet);
	}
	
}

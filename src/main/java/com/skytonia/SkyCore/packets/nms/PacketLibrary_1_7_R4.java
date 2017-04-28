package com.skytonia.SkyCore.packets.nms;

import com.skytonia.SkyCore.packets.PacketLibrary;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 12/20/2016.
 */
public class PacketLibrary_1_7_R4 extends PacketLibrary
{
	
	private void sendPacket(PlayerConnection playerConnection, Packet packet)
	{
		playerConnection.sendPacket(packet);
	}
	
	/*
	 *  Title/ActionBar Sending
	 */
	
	public void sendActionBar(Player player, String message, int lifespan)
	{
		throw new UnsupportedOperationException("1.8 Required for this functionality");
	}
	
	public void sendTitle(Player player, String title, String subTitle, int persistTime, int fadeIn, int fadeOut)
	{
		throw new UnsupportedOperationException("1.8 Required for this functionality");
	}
	
	private void sendTitlePacket(PlayerConnection playerConnection, PacketPlayOutTitle.EnumTitleAction titleAction, String message,
	                             int persistTime, int fadeIn, int fadeOut)
	{
		throw new UnsupportedOperationException("1.8 Required for this functionality");
	}
	
}

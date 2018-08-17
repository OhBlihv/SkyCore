package com.skytonia.SkyCore.packets.nms;

import com.skytonia.SkyCore.packets.PacketLibrary;
import com.skytonia.SkyCore.packets.Persistence;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 12/20/2016.
 */
public class PacketLibrary_1_8_R3 extends PacketLibrary
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
		PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
		
		//Action bar lasts about 2-3 seconds. Send an update after 2 seconds to ensure it does not disappear
		startPersistingTask(player.getUniqueId(), Persistence.PersistingType.ACTION_BAR, 40L, lifespan / 2,
		                    () -> sendPacket(playerConnection, packetPlayOutChat));
	}
	
	public void sendTitle(Player player, String title, String subTitle, int persistTime, int fadeIn, int fadeOut)
	{
		PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
		
		//Title is required to send subtitle. Send both when possible.
		if(title == null)
		{
			title = "";
		}
		
		if(subTitle == null)
		{
			subTitle = "";
		}
		
		sendTitlePacket(playerConnection, PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subTitle, persistTime, fadeIn, fadeOut);
		sendTitlePacket(playerConnection, PacketPlayOutTitle.EnumTitleAction.TITLE, title, persistTime, fadeIn, fadeOut);
	}
	
	private void sendTitlePacket(PlayerConnection playerConnection, PacketPlayOutTitle.EnumTitleAction titleAction, String message,
	                                    int persistTime, int fadeIn, int fadeOut)
	{
		//BUtil.log("Printing " + titleAction.name() + " with '" + "{\"text\": \"" + message + "\"}" + "'");
		playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE,
		                                                   IChatBaseComponent.ChatSerializer.a("{\"text\": \" \"}"),
		                                                   persistTime, fadeIn, fadeOut));
		playerConnection.sendPacket(new PacketPlayOutTitle(titleAction,
		                                                   IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}"),
		                                                   persistTime, fadeIn, fadeOut));
	}
	
}

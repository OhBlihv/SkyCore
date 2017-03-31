package com.skytonia.SkyCore.packets.nms;

import com.skytonia.SkyCore.packets.PacketLibrary;
import com.skytonia.SkyCore.packets.PacketUtil;
import com.skytonia.SkyCore.packets.Persistence;
import net.minecraft.server.v1_9_R2.ChatComponentText;
import net.minecraft.server.v1_9_R2.IChatBaseComponent;
import net.minecraft.server.v1_9_R2.Packet;
import net.minecraft.server.v1_9_R2.PacketPlayOutChat;
import net.minecraft.server.v1_9_R2.PacketPlayOutTitle;
import net.minecraft.server.v1_9_R2.PlayerConnection;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 12/20/2016.
 */
public class PacketLibrary_1_9_R2 extends PacketLibrary
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
		if(message.length() > 32)
		{
			message = message.substring(0, 32) + "~";
		}
		
		PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
		
		//Action bar lasts about 2-3 seconds. Send an update after 2 seconds to ensure it does not disappear
		PacketUtil.startPersistingTask(player.getUniqueId(), Persistence.PersistingType.ACTION_BAR, 40L, lifespan / 2,
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
		else if(title.length() > 32)
		{
			title = title.substring(0, 32) + "~";
		}
		
		if(subTitle == null)
		{
			subTitle = "";
		}
		else if(subTitle.length() > 32)
		{
			subTitle = subTitle.substring(0, 32) + "~";
		}
		
		sendTitlePacket(playerConnection, PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subTitle, persistTime, fadeIn, fadeOut);
		sendTitlePacket(playerConnection, PacketPlayOutTitle.EnumTitleAction.TITLE, title, persistTime, fadeIn, fadeOut);
	}
	
	private void sendTitlePacket(PlayerConnection playerConnection, PacketPlayOutTitle.EnumTitleAction titleAction, String message,
	                                    int persistTime, int fadeIn, int fadeOut)
	{
		//BUtil.logInfo("Printing " + titleAction.name() + " with '" + "{\"text\": \"" + message + "\"}" + "'");
		playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE,
		                                                   IChatBaseComponent.ChatSerializer.a("{\"text\": \" \"}"),
		                                                   fadeIn, persistTime, fadeOut));
		playerConnection.sendPacket(new PacketPlayOutTitle(titleAction,
		                                                   IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}"),
		                                                   fadeIn, persistTime, fadeOut));
	}
	
}

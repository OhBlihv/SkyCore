package com.skytonia.SkyCore.packets.nms;

import com.skytonia.SkyCore.packets.PacketLibrary;
import com.skytonia.SkyCore.packets.Persistence;
import net.minecraft.server.v1_13_R1.ChatComponentText;
import net.minecraft.server.v1_13_R1.ChatMessageType;
import net.minecraft.server.v1_13_R1.IChatBaseComponent;
import net.minecraft.server.v1_13_R1.Packet;
import net.minecraft.server.v1_13_R1.PacketPlayOutChat;
import net.minecraft.server.v1_13_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_13_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketLibrary_1_13_R1 extends PacketLibrary
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
		PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(new ChatComponentText(message), ChatMessageType.a((byte) 2));

		//Action bar lasts about 2-3 seconds. Send an update after 2 seconds to ensure it does not disappear
		startPersistingTask(player.getUniqueId(), Persistence.PersistingType.ACTION_BAR, 40L, lifespan / 2,
			() -> sendPacket(playerConnection, packetPlayOutChat));
	}

	public void sendTitle(Player player, String title, String subTitle, int persistTime, int fadeIn, int fadeOut)
	{
		PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

		//Reset player titles first
		sendTitlePacket(playerConnection, PacketPlayOutTitle.EnumTitleAction.RESET, null, -1, -1, -1);

		//Title is required to send subtitle. Send both when possible.
		if(title == null)
		{
			title = "";
		}

		if(persistTime != -1 && fadeIn != -1 && fadeOut != -1)
		{
			sendTitlePacket(playerConnection, PacketPlayOutTitle.EnumTitleAction.TIMES, null, persistTime, fadeIn, fadeOut);
		}

		if(subTitle != null)
		{
			sendTitlePacket(playerConnection, PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subTitle, persistTime, fadeIn, fadeOut);
		}

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

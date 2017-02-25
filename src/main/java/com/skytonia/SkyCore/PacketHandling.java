package com.skytonia.SkyCore;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.event.Listener;

/**
 * Created by Chris Brown (OhBlihv) on 2/8/2017.
 */
public class PacketHandling implements Listener
{
	
	public PacketHandling(SkyCore plugin)
	{
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.SCOREBOARD_TEAM)
		{
			
			@Override
			public void onPacketReceiving(PacketEvent event)
			{
					//
			}
			
			@Override
			public void onPacketSending(PacketEvent event)
			{
				WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam(event.getPacket());
				int packetMode = packet.getMode();
				if(packetMode == 0 || packetMode == 2)
				{
					if(!packet.getCollisionRule().equals("never"))
					{
						packet.setCollisionRule("never");
					}
				}
			}
			
		});
	}
	
}

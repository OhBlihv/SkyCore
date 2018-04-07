package com.skytonia.SkyCore.titles;

import com.comphenix.packetwrapper.AbstractPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Deque;

/**
 * Created by Chris Brown (OhBlihv) on 4/10/2017.
 */
@RequiredArgsConstructor
public class ComparisonPlayer
{

	@Getter
	private final Player player;
	
	@Getter
	@Setter
	private DirtyPlayerType dirtyPlayerType = DirtyPlayerType.ADD;

	public boolean isOnline()
	{
		return player.isOnline();
	}

	public void sendPacket(AbstractPacket packet)
	{
		packet.sendPacket(player);
	}

	public void sendPackets(Deque<AbstractPacket>... packets)
	{
		for(Deque<AbstractPacket> packetSet : packets)
		{
			for(AbstractPacket packet : packetSet)
			{
				packet.sendPacket(player);
			}
		}
	}
	
	@Override
	public int hashCode()
	{
		return player.getUniqueId().hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Player && player.getUniqueId().equals(((Player) obj).getUniqueId());
	}

	@Override
	public String toString()
	{
		return "Comp::" + player.getName();
	}
}

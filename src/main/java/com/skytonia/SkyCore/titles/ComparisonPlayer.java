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

	private final TaggedPlayer parent;
	
	@Getter
	private final Player player;
	
	@Getter
	@Setter
	private DirtyPlayerType dirtyPlayerType = DirtyPlayerType.ADD;

	@Getter
	/*
	 * True = Visible
	 * False = Invisible
	 * null = No Change
	 */
	private Boolean forcedVisibility = null;

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

	public void setForcedVisibility(Boolean value)
	{
		if(parent.isHideTags())
		{
			//New value is 'visible', current value is 'none' or 'invisibile'
			if((value != null && value) && (forcedVisibility == null || !forcedVisibility))
			{
				//Add tags - weren't previously visible
				dirtyPlayerType = DirtyPlayerType.ADD;
			}
			else if(value == null || !value && forcedVisibility)
			{
				dirtyPlayerType = DirtyPlayerType.REMOVE;
			}
		}
		else
		{
			//
			if(value != null && !value && (forcedVisibility == null || forcedVisibility))
			{
				dirtyPlayerType = DirtyPlayerType.REMOVE;
			}
		}

		forcedVisibility = value;
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

}

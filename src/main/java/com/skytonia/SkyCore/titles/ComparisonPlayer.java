package com.skytonia.SkyCore.titles;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

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

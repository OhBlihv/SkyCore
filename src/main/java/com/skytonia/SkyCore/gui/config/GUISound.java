package com.skytonia.SkyCore.gui.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 26/09/2016.
 */
@RequiredArgsConstructor
public  class GUISound
{
	
	private final Sound sound;
	private final float volume,
						pitch;
	
	public void playSound(Player player)
	{
		player.playSound(player.getLocation(), sound, volume, pitch);
	}
	
}

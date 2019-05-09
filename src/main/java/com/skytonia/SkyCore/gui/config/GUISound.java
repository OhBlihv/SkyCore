package com.skytonia.SkyCore.gui.config;

import com.skytonia.SkyCore.util.BUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 26/09/2016.
 */
@RequiredArgsConstructor
public class GUISound
{
	
	private final Sound sound;
	private final float volume,
						pitch;
	
	public void playSound(Player player)
	{
		player.playSound(player.getLocation(), sound, volume, pitch);
	}

	public static GUISound load(ConfigurationSection soundSection)
	{
		if(soundSection == null)
		{
			return null;
		}

		Sound sound = null;
		try
		{
			sound = Sound.valueOf(soundSection.getString("sound"));
		}
		catch(IllegalArgumentException e)
		{
			//Check if it's ITEM_PICKUP and translate
			if(soundSection.getString("sound").equals("ITEM_PICKUP"))
			{
				sound = Sound.ENTITY_ITEM_PICKUP;
			}
		}
		catch(NullPointerException e)
		{
			//Sound stays null and is caught by the next check
		}

		if(sound == null)
		{
			BUtil.log("Could not load sound '" + (soundSection.contains("sound") ? soundSection.getString("sound", "none") : "null") + "'");
			return null;
		}
		else
		{
			return new GUISound(sound, (float) soundSection.getDouble("volume", 10F), (float) soundSection.getDouble("pitch", 1F));
		}
	}
	
}

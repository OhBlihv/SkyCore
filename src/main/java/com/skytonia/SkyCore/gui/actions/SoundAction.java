package com.skytonia.SkyCore.gui.actions;

import com.skytonia.SkyCore.gui.GUISound;
import com.skytonia.SkyCore.util.BUtil;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public class SoundAction extends ElementAction
{
	
	private GUISound sound;
	
	@Override
	public boolean onClick(Player player, ClickType clickType, int slot)
	{
		sound.playSound(player);
		return true;
	}
	
	@Override
	public ElementAction loadAction(ConfigurationSection configurationSection)
	{
		Sound sound = null;
		if(configurationSection.contains("sound") && configurationSection.isString("sound"))
		{
			try
			{
				sound = Sound.valueOf(configurationSection.getString("sound"));
			}
			catch(IllegalArgumentException e)
			{
				//Sound stays null and is caught by the next check
			}
		}
		
		if(sound == null)
		{
			BUtil.logInfo("Could not load sound '" + (configurationSection.contains("sound") ? configurationSection.getString("sound", "none") : "null") + "'");
			//Default to the first sound. This must simply be a valid sound since the issue is already indicated in console.
			this.sound = new GUISound(Sound.values()[0], 10F, 1F);
		}
		else
		{
			this.sound = new GUISound(sound, (float) configurationSection.getDouble("volume", 10F), (float) configurationSection.getDouble("pitch", 1F));
		}
		
		return this;
	}
	
}

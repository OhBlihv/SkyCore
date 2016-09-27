package com.skytonia.SkyCore.gui.actions;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public class CloseInventoryAction extends ElementAction
{
	
	@Override
	public boolean onClick(Player player, int slot)
	{
		player.closeInventory();
		return true;
	}
	
	@Override
	public ElementAction loadAction(ConfigurationSection configurationSection)
	{
		return this;
	}
	
}

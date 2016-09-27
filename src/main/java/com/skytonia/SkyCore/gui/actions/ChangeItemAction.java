package com.skytonia.SkyCore.gui.actions;

import com.skytonia.SkyCore.items.ItemContainer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public class ChangeItemAction extends ElementAction
{
	
	private ItemContainer toItem;
	
	@Override
	public boolean onClick(Player player, int slot)
	{
		player.getOpenInventory().getTopInventory().setItem(slot,
		                                                    toItem.replaceItemStack(player.getOpenInventory().getTopInventory().getItem(slot), player.getName()));
		player.updateInventory();
		return true;
	}
	
	@Override
	public ElementAction loadAction(ConfigurationSection configurationSection)
	{
		this.toItem = ItemContainer.buildItemContainer(configurationSection.getConfigurationSection("item"));
		return this;
	}
	
}

package com.skytonia.SkyCore.gui.actions;

import com.skytonia.SkyCore.items.construction.ItemContainer;
import com.skytonia.SkyCore.items.construction.ItemContainerConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public class ChangeItemAction extends ElementAction
{
	
	private ItemContainer toItem;
	
	@Override
	public boolean onClick(Player player, ClickType clickType, int slot)
	{
		player.getOpenInventory().getTopInventory().setItem(slot,
		                                                    toItem.replaceItemStack(player.getOpenInventory().getTopInventory().getItem(slot), player.getName()));
		player.updateInventory();
		return true;
	}
	
	@Override
	public ElementAction loadAction(ConfigurationSection configurationSection)
	{
		this.toItem = ItemContainerConstructor.buildItemContainer(configurationSection.getConfigurationSection("item"));
		return this;
	}
	
}

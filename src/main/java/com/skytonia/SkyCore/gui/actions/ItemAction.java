package com.skytonia.SkyCore.gui.actions;

import com.skytonia.SkyCore.items.construction.ItemContainer;
import com.skytonia.SkyCore.items.construction.ItemContainerConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public class ItemAction extends ElementAction
{
	
	//Possibly allow for more than one item to be stored?
	@Getter
	private ItemContainer item;
	
	@Override
	public boolean onClick(Player player, ClickType clickType, int slot)
	{
		Map<Integer, ItemStack> leftOver = player.getInventory().addItem(item.toItemStack(player.getName()));
		//TODO: Configurable value allowing the plugin to decide whether to drop the item or not
		if(!leftOver.isEmpty())
		{
			Location playerLocation = player.getLocation();
			for(ItemStack itemStack : leftOver.values())
			{
				playerLocation.getWorld().dropItem(playerLocation, itemStack);
			}
		}
		
		return true;
	}
	
	@Override
	public ElementAction loadAction(ConfigurationSection configurationSection)
	{
		this.item = ItemContainerConstructor.buildItemContainer(configurationSection.getConfigurationSection("item"));
		return this;
	}
	
}

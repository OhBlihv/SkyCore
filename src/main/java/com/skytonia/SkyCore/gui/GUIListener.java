package com.skytonia.SkyCore.gui;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.util.BUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by Chris Brown (OhBlihv) on 10/28/2016.
 */
public class GUIListener implements Listener
{
	
	public GUIListener()
	{
		Bukkit.getPluginManager().registerEvents(this, SkyCore.getPluginInstance());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event)
	{
		//If the inventory is modified, attempt to cancel all events to avoid players
		//taking items out of the inventory/gui.
		//Handle this early (LOWEST) so other plugins can re-enable this event.
		if(event.getInventory() != null && event.getInventory().getTitle() != null &&
			   isInventoryModified(event.getInventory().getTitle()))
		{
			BUtil.logInfo("Blocking: " + event.getInventory().getTitle());
			event.setCancelled(true);
		}
	}
	
	private boolean isInventoryModified(String title)
	{
		switch(title)
		{
			case "Inventory":
			case "container.crafting":
			case "Chest":
			case "Large Chest":
			case "Furnace":
			case "Dispenser":
			case "Enchant":
			case "Brewing Stand":
			case "Villager":
			case "Repair & Name":
			case "Horse":
			case "Donkey":
			case "Item Hopper":
				return false;
			default: return true;
		}
	}
	
}

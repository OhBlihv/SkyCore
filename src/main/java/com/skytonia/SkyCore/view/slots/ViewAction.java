package com.skytonia.SkyCore.view.slots;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ViewAction
{

	void onClick(Player player, ClickType clickType, int slot, InventoryClickEvent event);

}

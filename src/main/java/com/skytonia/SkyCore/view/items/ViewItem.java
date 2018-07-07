package com.skytonia.SkyCore.view.items;

import com.skytonia.SkyCore.view.View;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ViewItem
{

	ItemStack getItem(Player player, View view, Object... args);

}

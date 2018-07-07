package com.skytonia.SkyCore.view.items;

import com.skytonia.SkyCore.view.View;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class BukkitViewItem implements ViewItem
{

	private final ItemStack itemStack;

	@Override
	public ItemStack getItem(Player player, View view, Object... args)
	{
		return itemStack;
	}

}

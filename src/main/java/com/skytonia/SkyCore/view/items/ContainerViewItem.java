package com.skytonia.SkyCore.view.items;

import com.skytonia.SkyCore.items.construction.ItemContainer;
import com.skytonia.SkyCore.view.View;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class ContainerViewItem implements ViewItem
{

	private final ItemContainer itemContainer;

	@Override
	public ItemStack getItem(Player player, View view, Object... args)
	{
		return itemContainer.toItemStack(player.getName());
	}

}

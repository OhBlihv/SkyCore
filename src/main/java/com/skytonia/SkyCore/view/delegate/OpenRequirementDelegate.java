package com.skytonia.SkyCore.view.delegate;

import org.bukkit.entity.Player;

public interface OpenRequirementDelegate
{

	boolean canOpenInventory(Player player, Object... args);

}

package com.skytonia.SkyCore.view;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;

@RequiredArgsConstructor
public class PlayerView
{

	@Getter
	private final View parentView;

	@Getter
	private final Inventory inventory;

}

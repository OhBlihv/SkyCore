package com.skytonia.SkyCore.gui.nms;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * Created by OhBlihv (Chris) on 12/1/2016.
 * This file is part of a project created for SkyCore
 */
public class GUICreationFactory_1_11_R1 implements GUICreationFactory
{
	
	@Override
	public Inventory createInventory(int guiSize, String guiTitle)
	{
		if(guiSize == 5)
		{
			return Bukkit.createInventory(null, InventoryType.HOPPER, guiTitle);
		}
		else
		{
			return Bukkit.createInventory(null, guiSize, guiTitle);
		}
	}
	
}

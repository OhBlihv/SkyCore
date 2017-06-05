package com.skytonia.SkyCore.gui.nms;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * Created by Chris Brown (OhBlihv) on 5/27/2017.
 */
public class GUICreationFactory_1_12_R1 implements GUICreationFactory
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

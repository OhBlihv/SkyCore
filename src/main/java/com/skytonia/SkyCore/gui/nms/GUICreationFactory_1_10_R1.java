package com.skytonia.SkyCore.gui.nms;

import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftInventoryCustom;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * Created by Chris Brown (OhBlihv) on 10/28/2016.
 */
public class GUICreationFactory_1_10_R1 implements GUICreationFactory
{
	
	@Override
	public Inventory createInventory(int guiSize, String guiTitle)
	{
		if(guiSize == 5)
		{
			return new CraftInventoryCustom(null, InventoryType.HOPPER, guiTitle);
		}
		else
		{
			return new CraftInventoryCustom(null, guiSize, guiTitle);
		}
	}
}

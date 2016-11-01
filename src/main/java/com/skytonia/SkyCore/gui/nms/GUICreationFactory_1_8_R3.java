package com.skytonia.SkyCore.gui.nms;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.inventory.Inventory;

/**
 * Created by Chris Brown (OhBlihv) on 10/28/2016.
 */
public class GUICreationFactory_1_8_R3 implements GUICreationFactory
{
	
	@Override
	public Inventory createInventory(int guiSize, String guiTitle)
	{
		return new CraftInventoryCustom(null, guiSize, guiTitle);
	}
}

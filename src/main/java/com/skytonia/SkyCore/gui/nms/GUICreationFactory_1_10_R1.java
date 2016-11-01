package com.skytonia.SkyCore.gui.nms;

import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftInventoryCustom;
import org.bukkit.inventory.Inventory;

/**
 * Created by Chris Brown (OhBlihv) on 10/28/2016.
 */
public class GUICreationFactory_1_10_R1 implements GUICreationFactory
{
	
	@Override
	public Inventory createInventory(int guiSize, String guiTitle)
	{
		return new CraftInventoryCustom(null, guiSize, guiTitle);
	}
}

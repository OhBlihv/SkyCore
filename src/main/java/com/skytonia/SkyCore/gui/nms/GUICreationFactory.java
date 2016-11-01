package com.skytonia.SkyCore.gui.nms;

import org.bukkit.inventory.Inventory;

/**
 * Created by Chris Brown (OhBlihv) on 10/28/2016.
 */
public interface GUICreationFactory
{
	
	Inventory createInventory(int guiSize, String guiTitle);
	
}

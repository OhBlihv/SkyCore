package com.skytonia.SkyCore.items.nms;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Chris Brown (OhBlihv) on 5/6/2017.
 */
public interface NMSItemUtil
{
	
	ItemStack setSpawnedEntity(ItemStack itemStack, int damage);
	
	Object addEnchantmentEffect(Object enchTag);
	
}

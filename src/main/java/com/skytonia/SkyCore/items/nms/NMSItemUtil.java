package com.skytonia.SkyCore.items.nms;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.INBTBase;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Chris Brown (OhBlihv) on 5/6/2017.
 */
public interface NMSItemUtil
{
	
	ItemStack setSpawnedEntity(ItemStack itemStack, int damage);
	
	Object addEnchantmentEffect(Object enchTag);

	Material getDefaultMaterial();

	Material getSkullMaterial();

	boolean isSkullMaterial(Material material);

	Material getSpawnerMaterial();

	boolean isMonsterEggMaterial(Material material);

	default void addNBTFlag(ItemMeta itemMeta, String key, INBTBase value)
	{
		throw new UnsupportedOperationException("NBT Flags are unsupported outside of TW 1.8.8");
	}
	
}

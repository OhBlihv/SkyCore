package com.skytonia.SkyCore.items;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Chris Brown (OhBlihv) on 26/09/2016.
 */
public enum EnchantStatus
{
	
	NO_CHANGE,
	ADD,
	REMOVE;
	
	public ItemStack alterEnchantmentStatus(ItemStack itemStack)
	{
		switch(this)
		{
			case ADD: return GUIUtil.addEnchantmentEffect(itemStack);
			case REMOVE: return GUIUtil.removeEnchantmentEffect(itemStack);
		}
		return itemStack; //NO_CHANGE
	}
	
	public static EnchantStatus getEnchantStatus(int enchantValue)
	{
		//+1 since the old NO_CHANGE was -1, and the ordinal values start at 0
		return values()[enchantValue + 1];
	}
	
}

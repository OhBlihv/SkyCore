package com.skytonia.SkyCore.items.nms;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Chris Brown (OhBlihv) on 5/6/2017.
 */
public class NMSItemUtil_1_7_R4 implements NMSItemUtil
{
	
	@Override
	public ItemStack setSpawnedEntity(ItemStack itemStack, int damage)
	{
		net.minecraft.server.v1_7_R4.ItemStack stack = org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack.asNMSCopy(itemStack);
		net.minecraft.server.v1_7_R4.NBTTagCompound tagCompound = stack.getTag();
		if(tagCompound == null)
		{
			tagCompound = new net.minecraft.server.v1_7_R4.NBTTagCompound();
		}
		
		net.minecraft.server.v1_7_R4.NBTTagCompound id = new net.minecraft.server.v1_7_R4.NBTTagCompound();
		id.setString("id", EntityType.fromId(damage).getName());
		tagCompound.set("EntityTag", id);
		stack.setTag(tagCompound);
		return org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack.asBukkitCopy(stack);
	}
	
}

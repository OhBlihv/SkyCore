package com.skytonia.SkyCore.items.nms;

import net.minecraft.server.v1_11_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Chris Brown (OhBlihv) on 5/6/2017.
 */
public class NMSItemUtil_1_11_R1 implements NMSItemUtil
{
	
	@Override
	public ItemStack setSpawnedEntity(ItemStack itemStack, int damage)
	{
		net.minecraft.server.v1_11_R1.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound tagCompound = stack.getTag();
		if(tagCompound == null)
		{
			tagCompound = new NBTTagCompound();
		}
		
		NBTTagCompound id = new NBTTagCompound();
		id.setString("id", EntityType.fromId(damage).getName());
		tagCompound.set("EntityTag", id);
		stack.setTag(tagCompound);
		return CraftItemStack.asBukkitCopy(stack);
	}
	
}

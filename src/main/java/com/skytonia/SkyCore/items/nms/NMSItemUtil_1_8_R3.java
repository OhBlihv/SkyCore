package com.skytonia.SkyCore.items.nms;

import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 5/27/2017.
 */
public class NMSItemUtil_1_8_R3 implements NMSItemUtil
{
	
	@Override
	public ItemStack setSpawnedEntity(ItemStack itemStack, int damage)
	{
		net.minecraft.server.v1_8_R3.ItemStack stack = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(itemStack);
		net.minecraft.server.v1_8_R3.NBTTagCompound tagCompound = stack.getTag();
		if(tagCompound == null)
		{
			tagCompound = new net.minecraft.server.v1_8_R3.NBTTagCompound();
		}
		
		net.minecraft.server.v1_8_R3.NBTTagCompound id = new net.minecraft.server.v1_8_R3.NBTTagCompound();
		id.setString("id", EntityType.fromId(damage).getName());
		tagCompound.set("EntityTag", id);
		stack.setTag(tagCompound);
		return org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asBukkitCopy(stack);
	}
	
	@Override
	public Object addEnchantmentEffect(Object enchTag)
	{
		NBTTagList enchTagList = (NBTTagList) enchTag;
		if(!enchTagList.isEmpty())
		{
			try
			{
				Field listField = NBTTagList.class.getField("list");
				listField.setAccessible(true);
				
				((List<NBTBase>) listField.get(enchTagList)).clear();
			}
			catch(NoSuchFieldException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		
		((NBTTagList) enchTag).add(new NBTTagInt(-1));
		
		return enchTag;
	}
	
}

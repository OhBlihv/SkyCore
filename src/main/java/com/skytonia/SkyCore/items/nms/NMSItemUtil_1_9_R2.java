package com.skytonia.SkyCore.items.nms;

import net.minecraft.server.v1_9_R2.NBTTagCompound;
import net.minecraft.server.v1_9_R2.NBTTagList;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Chris Brown (OhBlihv) on 5/6/2017.
 */
public class NMSItemUtil_1_9_R2 implements NMSItemUtil
{
	
	@Override
	public ItemStack setSpawnedEntity(ItemStack itemStack, int damage)
	{
		net.minecraft.server.v1_9_R2.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound tagCompound = stack.getTag();
		if(tagCompound == null)
		{
			tagCompound = new NBTTagCompound();
		}
		
		NBTTagCompound id = new NBTTagCompound();
		{
			EntityType entityType = EntityType.fromId(damage);
			if(entityType == null)
			{
				throw new IllegalArgumentException("Entity does not exist with ID(" + damage + "). Ensure this entity exists within 1.9");
			}

			id.setString("id", entityType.getName());
		}

		tagCompound.set("EntityTag", id);
		stack.setTag(tagCompound);
		return CraftItemStack.asBukkitCopy(stack);
	}
	
	@Override
	public Object addEnchantmentEffect(Object enchTag)
	{
		NBTTagList enchTagList = (NBTTagList) enchTag;
		while(!enchTagList.isEmpty())
		{
			enchTagList.remove(0);
		}
		((net.minecraft.server.v1_9_R2.NBTTagList) enchTag).add(new net.minecraft.server.v1_9_R2.NBTTagInt(-1));
		
		return enchTag;
	}

	@Override
	public Material getDefaultMaterial()
	{
		return Material.POTATO_ITEM;
	}

	@Override
	public Material getSkullMaterial()
	{
		return Material.SKULL_ITEM;
	}

	@Override
	public boolean isSkullMaterial(Material material)
	{
		return material == Material.SKULL || material == Material.SKULL_ITEM;
	}

	@Override
	public Material getSpawnerMaterial()
	{
		return Material.MOB_SPAWNER;
	}

	@Override
	public boolean isMonsterEggMaterial(Material material)
	{
		return material == Material.MONSTER_EGGS;
	}
	
}

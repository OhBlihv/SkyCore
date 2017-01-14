package com.skytonia.SkyCore.items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Created by Chris Brown (OhBlihv) on 12/29/2016.
 */
public class HeadUtil
{
	
	public static ItemStack getHead(String texture)
	{
		ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		
		itemStack = setHeadTexture(itemStack, texture);
		
		return itemStack;
	}
	
	public static ItemStack setHeadTexture(ItemStack itemStack, String texture)
	{
		SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
		GameProfile skinProfile = new GameProfile(UUID.randomUUID(), null);
		
		skinProfile.getProperties().put("textures", new Property("textures", texture, "signed"));
		
		try
		{
			Field profileField = skullMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(skullMeta, skinProfile);
		}
		catch(IllegalAccessException | NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		
		itemStack.setItemMeta(skullMeta);
		return itemStack;
	}
	
}

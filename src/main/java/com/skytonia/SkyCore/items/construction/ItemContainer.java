package com.skytonia.SkyCore.items.construction;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.skytonia.SkyCore.items.EnchantStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Material.AIR;

/**
 * Created by Chris Brown (OhBlihv) on 26/09/2016.
 */
@RequiredArgsConstructor
public class ItemContainer
{
	
	//Set all to a 'default' unusable value to indicate if it needs changing
	@Getter
	private final Material material;
	
	@Getter
	private final int   damage,
						amount;
	@Getter
	private final String displayName;
	
	@Getter
	private final List<String> lore;
	
	@Getter
	private final EnchantStatus enchantStatus;
	
	@Getter
	private final Map<Enchantment, Integer> enchantmentMap;
	
	@Getter
	private final String owner;
	
	@Getter
	private final String skullTexture;
	
	private Object getOverriddenValue(Map<ItemContainerVariable, Object> overriddenValues, ItemContainerVariable itemVariable, Object defaultValue)
	{
		Object returningValue;
		if(itemVariable.isNumber())
		{
			returningValue = overriddenValues.getOrDefault(itemVariable, 0);
		}
		else
		{
			returningValue = overriddenValues.get(itemVariable);
		}
		
		if(returningValue != null)
		{
			return returningValue;
		}
		
		return defaultValue;
	}
	
	public ItemStack toItemStack()
	{
		return toItemStack(null);
	}
	
	public ItemStack toItemStack(String playerName)
	{
		return toItemStack(playerName, new HashMap<>());
	}
	
	public ItemStack toItemStack(String playerName, Map<ItemContainerVariable, Object> overriddenValues)
	{
		if(material == AIR && !overriddenValues.containsKey(ItemContainerVariable.MATERIAL))
		{
			return null;
		}
		
		//Load any overridden values
		Material        material = (Material) getOverriddenValue(overriddenValues, ItemContainerVariable.MATERIAL, this.material);
		int             amount = (int) getOverriddenValue(overriddenValues, ItemContainerVariable.AMOUNT, this.amount),
						damage = (int) getOverriddenValue(overriddenValues, ItemContainerVariable.DAMAGE, this.damage);
		String          displayName = (String) getOverriddenValue(overriddenValues, ItemContainerVariable.DISPLAYNAME, this.displayName);
		List<String>    lore = (List<String>) getOverriddenValue(overriddenValues, ItemContainerVariable.LORE, new ArrayList<>(this.lore));
		EnchantStatus   enchantStatus = (EnchantStatus) getOverriddenValue(overriddenValues, ItemContainerVariable.ENCHANTED, this.enchantStatus);
		Map<Enchantment, Integer> enchantmentMap =
						(Map<Enchantment, Integer>) getOverriddenValue(overriddenValues, ItemContainerVariable.ENCHANTMENTS, this.enchantmentMap);
		String          owner = (String) getOverriddenValue(overriddenValues, ItemContainerVariable.OWNER, this.owner);
		
		ItemStack itemStack = new ItemStack(material, amount, (short) damage);
		ItemMeta itemMeta = itemStack.getItemMeta();
		
		if(displayName != null)
		{
			itemMeta.setDisplayName(displayName);
		}
		if((material == Material.SKULL_ITEM || material == Material.SKULL) && damage == 3)
		{
			if(skullTexture == null)
			{
				if(playerName != null || owner != null)
				{
					String skullOwner;
					//Allow the input player to override the owner for this skull
					if(owner == null || owner.equals("PLAYER"))
					{
						skullOwner = playerName;
					}
					else
					{
						skullOwner = owner;
					}
					
					if(skullOwner != null && !skullOwner.isEmpty())
					{
						((SkullMeta) itemMeta).setOwner(skullOwner);
						if(itemMeta.hasDisplayName())
						{
							itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{player}", skullOwner));
						}
					}
				}
			}
			else
			{
				GameProfile skinProfile = new GameProfile(UUID.randomUUID(), null);
				
				
				skinProfile.getProperties().put("textures", new Property("textures",
				                                                         skullTexture,
				                                                         "signed"));
				
				try
				{
					Field profileField = itemMeta.getClass().getDeclaredField("profile");
					profileField.setAccessible(true);
					profileField.set(itemMeta, skinProfile);
				}
				catch(IllegalAccessException | NoSuchFieldException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		if(lore != null)
		{
			if(playerName != null && !playerName.isEmpty())
			{
				int lineNum = 0;
				for(String line : lore)
				{
					if(line.contains("{player}"))
					{
						line = line.replace("{player}", playerName);
					}
					
					lore.set(lineNum, line);
					
					lineNum++;
				}
			}
			
			itemMeta.setLore(lore);
		}
		
		itemStack.setItemMeta(itemMeta);
		
		if(enchantmentMap != null)
		{
			itemStack.addUnsafeEnchantments(enchantmentMap);
		}
		
		return enchantStatus.alterEnchantmentStatus(itemStack);
	}
	
	public int getMaxStackSize()
	{
		return material.getMaxStackSize();
	}
	
	public int getMaxDurability()
	{
		return material.getMaxDurability();
	}
	
	public ItemStack replaceItemStack(ItemStack original, String playerName)
	{
		ItemMeta meta = original.getItemMeta();
		//Cannot clone, since it loses attributes for some reason.
		if(material != null)
		{
			original.setType(material);
		}
		if((material == Material.SKULL_ITEM || material == Material.SKULL) && playerName != null)
		{
			((SkullMeta) meta).setOwner(playerName);
		}
		if(damage != -1)
		{
			original.setDurability((short) damage);
		}
		if(amount != -1)
		{
			original.setAmount(amount);
		}
		if(displayName != null)
		{
			meta.setDisplayName(displayName);
		}
		if(playerName != null && meta.hasDisplayName())
		{
			meta.setDisplayName(meta.getDisplayName().replace("{player}", playerName));
		}
		if(lore != null)
		{
			meta.setLore(lore);
		}
		
		original.setItemMeta(meta);
		
		original = enchantStatus.alterEnchantmentStatus(original);
		
		if(enchantmentMap != null && !enchantmentMap.isEmpty())
		{
			for(Enchantment enchantment : original.getEnchantments().keySet())
			{
				original.removeEnchantment(enchantment);
			}
			original.addEnchantments(enchantmentMap);
		}
		
		return original;
	}
	
	@Override
	public String toString()
	{
		return material + ":" + damage + " (" + amount + ") \"" + displayName + "\"";
	}
	
}

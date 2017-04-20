package com.skytonia.SkyCore.items.construction;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.items.EnchantStatus;
import com.skytonia.SkyCore.util.BUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.SpawnEgg;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.bukkit.Material.*;

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
	
	@Getter
	private final Color armorColor;
	
	private Object getOverriddenValue(Map<ItemContainerVariable, Object> overriddenValues, ItemContainerVariable itemVariable, Object defaultValue)
	{
		Object returningValue;
		if(itemVariable.isNumber())
		{
			returningValue = overriddenValues.getOrDefault(itemVariable, -1);
			
			if(((Number) returningValue).intValue() == -1)
			{
				returningValue = defaultValue;
			}
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
		List<String>    lore = (List<String>) getOverriddenValue(overriddenValues, ItemContainerVariable.LORE,
		                                                         this.lore == null ? null : new ArrayList<>(this.lore));
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
		
		if(armorColor != null && material.name().contains("LEATHER_"))
		{
			LeatherArmorMeta leatherMeta = (LeatherArmorMeta) itemMeta;
			leatherMeta.setColor(armorColor);
			
			leatherMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		}
		
		if(SkyCore.isSkytonia() && material == DIAMOND_PICKAXE)
		{
			itemMeta.spigot().setUnbreakable(true);
			itemMeta.addItemFlags(
				ItemFlag.HIDE_ENCHANTS,
				ItemFlag.HIDE_ATTRIBUTES,
				ItemFlag.HIDE_UNBREAKABLE,
				ItemFlag.HIDE_DESTROYS,
				ItemFlag.HIDE_PLACED_ON,
				ItemFlag.HIDE_POTION_EFFECTS);
		}
		
		itemStack.setItemMeta(itemMeta);
		
		if(enchantmentMap != null)
		{
			itemStack.addUnsafeEnchantments(enchantmentMap);
		}
		
		if(material == MONSTER_EGG && damage > 0)
		{
			SpawnEgg spawnEgg = (SpawnEgg) itemStack.getData();
			
			spawnEgg.setSpawnedType(EntityType.fromId(damage));
			
			itemStack.setData(spawnEgg);
		}
		
		if(enchantStatus != null)
		{
			return enchantStatus.alterEnchantmentStatus(itemStack);
		}
		else
		{
			return itemStack;
		}
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
	
	public boolean equals(ItemStack itemStack)
	{
		return equals(itemStack, null);
	}
	
	public boolean equals(ItemStack itemStack, Set<ItemContainerVariable> ignoredChecks)
	{
		if(ignoredChecks == null)
		{
			ignoredChecks = EnumSet.noneOf(ItemContainerVariable.class);
		}
		
		if(itemStack == null ||
			   (!ignoredChecks.contains(ItemContainerVariable.MATERIAL) && itemStack.getType() != material) ||
			   (!ignoredChecks.contains(ItemContainerVariable.DAMAGE) && itemStack.getDurability() != damage))
		{
			return false;
		}
		
		ItemMeta itemMeta = itemStack.getItemMeta();
		
		//Expecting a displayname
		if(!ignoredChecks.contains(ItemContainerVariable.DISPLAYNAME) && displayName != null && !displayName.isEmpty())
		{
			if(!itemMeta.hasDisplayName() || !itemMeta.getDisplayName().equals(displayName))
			{
				return false;
			}
		}
		
		//Expecting Lore
		if(!ignoredChecks.contains(ItemContainerVariable.LORE) && lore != null && !lore.isEmpty())
		{
			if(!itemMeta.hasLore() || itemMeta.getLore().isEmpty() || !itemMeta.getLore().equals(lore))
			{
				return false;
			}
		}
		
		if(itemMeta instanceof SkullMeta)
		{
			if(owner != null && owner.equals("player"))
			{
				return true;
			}
			
			if(skullTexture != null)
			{
				//Find our texture
				GameProfile skullProfile = null;
				try
				{
					Class skullClass = Class.forName("org.bukkit.craftbukkit." + BUtil.getNMSVersion() + ".inventory.CraftMetaSkull");
					
					Field profileField = skullClass.getDeclaredField("profile");
					profileField.setAccessible(true);
					
					skullProfile = (GameProfile) profileField.get(itemMeta);
				}
				catch(ClassNotFoundException | NoSuchFieldException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
				
				try
				{
					if(skullProfile == null || !skullProfile.getProperties().containsKey("textures") ||
						   !skullProfile.getProperties().get("textures").iterator().next().getValue().equals(skullTexture))
					{
						return false;
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				return ((SkullMeta) itemMeta).getOwner().equals(owner);
			}
		}
		
		return true;
	}
	
	@Override
	public String toString()
	{
		return material + ":" + damage + " (" + amount + ") \"" + displayName + "\"";
	}
	
	public void saveItem(ConfigurationSection configurationSection)
	{
		//Clear this section
		if(!configurationSection.getKeys(false).isEmpty())
		{
			String[] pathSplit = configurationSection.getCurrentPath().split("[.]");
			
			ConfigurationSection parentSection = configurationSection.getParent();
			parentSection.set(pathSplit[pathSplit.length - 1], null);
			configurationSection = parentSection.createSection(pathSplit[pathSplit.length - 1]);
		}
		
		configurationSection.set("material", material.name());
		configurationSection.set("damage", damage);
		configurationSection.set("amount", amount);
		
		if(displayName != null)
		{
			configurationSection.set("displayname", null);
		}
		
		if(lore != null)
		{
			configurationSection.set("lore", lore);
		}
		
		if((enchantmentMap == null || enchantmentMap.isEmpty()) && enchantStatus != null)
		{
			configurationSection.set("enchanted", enchantStatus.name());
		}
		else if(enchantmentMap != null && !enchantmentMap.isEmpty())
		{
			List<String> enchantStringList = new ArrayList<>();
			for(Map.Entry<Enchantment, Integer> entry : enchantmentMap.entrySet())
			{
				enchantStringList.add(entry.getKey().getName() + ":" + entry.getValue());
			}
			
			configurationSection.set("enchanted", enchantStringList);
		}
		
		if(owner != null)
		{
			configurationSection.set("owner", owner);
		}
		
		if(skullTexture != null)
		{
			configurationSection.set("texture", skullTexture);
		}
		
		if(armorColor != null)
		{
			configurationSection.set("color", armorColor.asRGB());
		}
		
	}
	
}

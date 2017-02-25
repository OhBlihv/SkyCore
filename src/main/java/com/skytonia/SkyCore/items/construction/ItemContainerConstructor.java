package com.skytonia.SkyCore.items.construction;

import com.mojang.authlib.GameProfile;
import com.skytonia.SkyCore.items.EnchantStatus;
import com.skytonia.SkyCore.items.GUIUtil;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.file.FlatFile;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.SKULL_ITEM;

/**
 * Created by Chris Brown (OhBlihv) on 1/20/2017.
 */
public class ItemContainerConstructor
{
	
	public static class ItemContainerBuilder
	{
		
		@Getter
		private Material material = AIR;
		
		@Getter
		private int   damage = 0,
					  amount = 1;
		@Getter
		private String displayName = null;
		
		@Getter
		private List<String> lore = null;
		
		@Getter
		private EnchantStatus enchantStatus = EnchantStatus.NO_CHANGE;
		
		@Getter
		private Map<Enchantment, Integer> enchantments = null;
		
		@Getter
		private String owner = "player";
		
		@Getter
		private String skullTexture = null;
		
		public ItemContainerBuilder material(Material material)
		{
			this.material = material;
			
			return this;
		}
		
		public ItemContainerBuilder damage(int damage)
		{
			this.damage = damage;
			
			return this;
		}
		
		public ItemContainerBuilder amount(int amount)
		{
			this.amount = amount;
			
			return this;
		}
		
		public ItemContainerBuilder displayname(String displayName)
		{
			this.displayName = displayName;
			
			return this;
		}
		
		public ItemContainerBuilder lore(List<String> lore)
		{
			this.lore = lore;
			
			return this;
		}
		
		public ItemContainerBuilder enchantStatus(EnchantStatus enchantStatus)
		{
			this.enchantStatus = enchantStatus;
			
			return this;
		}
		
		public ItemContainerBuilder enchantments(Map<Enchantment, Integer> enchantments)
		{
			this.enchantments = enchantments;
			
			return this;
		}
		
		public ItemContainerBuilder owner(String owner)
		{
			this.owner = owner;
			
			return this;
		}
		
		public ItemContainerBuilder skullTexture(String skullTexture)
		{
			this.skullTexture = skullTexture;
			
			return this;
		}
		
		public ItemContainer build()
		{
			return new ItemContainer(material, damage, amount,
			                         displayName, lore,
			                         enchantStatus, enchantments,
			                         owner, skullTexture, null);
		}
		
	}
	
	public static ItemContainer fromItemStack(ItemStack itemStack)
	{
		if(itemStack == null || itemStack.getType() == AIR)
		{
			return null;
		}
		
		ItemMeta itemMeta = itemStack.getItemMeta();
		
		String displayname = null;
		List<String> lore = null;
		Map<Enchantment, Integer> enchantmentMap = null;
		
		if(itemMeta.hasDisplayName())
		{
			displayname = itemMeta.getDisplayName();
		}
		
		if(itemMeta.hasLore())
		{
			lore = itemMeta.getLore();
		}
		
		if(itemMeta.hasEnchants())
		{
			enchantmentMap = itemMeta.getEnchants();
		}
		
		String owner = null;
		String skullTexture = null;
		Color armorColor = null;
		if(itemStack.getType() == SKULL_ITEM)
		{
			try
			{
				Field profileField = itemMeta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				GameProfile gameProfile = (GameProfile) profileField.get(itemMeta);
				
				if(gameProfile.getProperties().containsKey("textures"))
				{
					skullTexture = gameProfile.getProperties().get("textures").iterator().next().getValue();
				}
			}
			catch(IllegalAccessException | NoSuchFieldException e)
			{
				e.printStackTrace();
			}
			
			if(skullTexture == null)
			{
				owner = ((SkullMeta) itemMeta).getOwner();
			}
		}
		else if(itemStack.getType().name().contains("LEATHER_"))
		{
			LeatherArmorMeta leatherMeta = (LeatherArmorMeta) itemMeta;
			armorColor = leatherMeta.getColor();
		}
		
		return new ItemContainer(itemStack.getType(), itemStack.getDurability(), itemStack.getAmount(), displayname, lore,
		                         EnchantStatus.NO_CHANGE, enchantmentMap, owner, skullTexture, armorColor);
	}
	
	public static ItemContainer buildItemContainer(ConfigurationSection configurationSection)
	{
		return buildItemContainer(configurationSection,
		                          EnumSet.allOf(ItemContainerVariable.class),
		                          null);
	}
	
	//Use the same method, but with a control flag to avoid the bad material check.
	//Useful for creating new items, but not useful for replacing old ones
	@SuppressWarnings("unchecked")
	public static ItemContainer buildItemContainer(ConfigurationSection configurationSection,
	                                               EnumSet<ItemContainerVariable> checkedErrors,
	                                               Map<ItemContainerVariable, Object> overriddenValues)
		throws IllegalArgumentException
	{
		if(configurationSection == null)
		{
			BUtil.logError("One of the gui-item's configuration sections is invalid! Please check your configs for any blank sections.");
			throw new IllegalArgumentException("Invalid Configuration Section");
		}
		
		if(overriddenValues == null)
		{
			overriddenValues = Collections.emptyMap();
		}
		if(checkedErrors == null)
		{
			checkedErrors = EnumSet.allOf(ItemContainerVariable.class);
		}
		
		Material material = null;
		if(overriddenValues.containsKey(ItemContainerVariable.MATERIAL))
		{
			material = (Material) overriddenValues.get(ItemContainerVariable.MATERIAL);
		}
		else
		{
			String materialString = configurationSection.getString("material", null);
			if(materialString != null)
			{
				material = Material.getMaterial(materialString);
			}
			
			if(material == null)
			{
				if(checkedErrors.contains(ItemContainerVariable.MATERIAL))
				{
					BUtil.logError(GUIUtil.getErrorMessage(configurationSection, "material", materialString));
				}
				
				//Still override the material for now, even if we aren't asked to
				material = Material.POTATO_ITEM;
			}
		}
		int damage = overriddenValues.containsKey(ItemContainerVariable.DAMAGE) ?
			             (int) overriddenValues.get(ItemContainerVariable.DAMAGE) : configurationSection.getInt("damage", 0),
			amount = overriddenValues.containsKey(ItemContainerVariable.AMOUNT) ?
				         (int) overriddenValues.get(ItemContainerVariable.AMOUNT) : configurationSection.getInt("amount", 1);
		
		String displayName = overriddenValues.containsKey(ItemContainerVariable.DISPLAYNAME) ?
			                     BUtil.translateColours((String) overriddenValues.get(ItemContainerVariable.DISPLAYNAME)) :
				                                                                                                              BUtil.translateColours(configurationSection.getString("displayname", ""));
		
		Map<Enchantment, Integer> enchantmentMap = (Map<Enchantment, Integer>) overriddenValues.get(ItemContainerVariable.ENCHANTMENTS);
		int isEnchanted = (int) overriddenValues.getOrDefault(ItemContainerVariable.ENCHANTED, -1);
		//If either one of these values are overridden, do not read from config
		if((isEnchanted != -1 || enchantmentMap == null) && configurationSection.get("enchanted") != null)
		{
			if(configurationSection.isBoolean("enchanted"))
			{
				isEnchanted = configurationSection.getBoolean("enchanted", false) ? 1 : 0;
			}
			else
			{
				enchantmentMap = GUIUtil.addEnchantments(configurationSection.getStringList("enchanted"));
			}
		}
		
		List<String> lore = overriddenValues.containsKey(ItemContainerVariable.LORE) ?
			                    BUtil.translateColours((List<String>) overriddenValues.get(ItemContainerVariable.LORE)) :
				                                                                                                            BUtil.translateColours(configurationSection.getStringList("lore"));
		
		String owner = (String) overriddenValues.get(ItemContainerVariable.OWNER);
		if(owner == null && configurationSection.contains("owner"))
		{
			owner = configurationSection.getString("owner");
		}
		
		String skullTexture = (String) overriddenValues.get(ItemContainerVariable.SKULL_TEXTURE);
		if(skullTexture == null && configurationSection.contains("texture"))
		{
			skullTexture = configurationSection.getString("texture");
		}
		
		Color armorColor = null;
		if(armorColor == null && configurationSection.contains("color"))
		{
			armorColor = Color.fromRGB(configurationSection.getInt("color"));
		}
		
		return new ItemContainer(material, damage, amount, displayName, lore,
		                         EnchantStatus.getEnchantStatus(isEnchanted), enchantmentMap,
		                         owner, skullTexture, armorColor);
	}
	
	private static final Pattern PATTERN_SEPERATOR = Pattern.compile(":");
	
	/**
	 *
	 * @param configurationMap Map containing item structure
	 * @return ItemContainer
	 */
	public static ItemContainer buildItemContainer(Map<String, Object> configurationMap) throws IllegalArgumentException
	{
		if(configurationMap == null || configurationMap.isEmpty())
		{
			BUtil.logError("One of the gui-item's configuration sections is invalid! Please check your configs for any blank sections.");
			throw new IllegalArgumentException("Invalid Configuration Section");
		}
		Material material = FlatFile.getMaterial(configurationMap, "material", null);
		if(material == null)
		{
			BUtil.logError("Material: '" + FlatFile.getString(configurationMap, "material", "null") + "' is not a valid material");
			return null;
		}
		
		String  displayName     =  BUtil.translateColours(FlatFile.getString(configurationMap, "displayname", null)),
			owner           =  FlatFile.getString(configurationMap, "owner",   null),
			skullTexture    =  FlatFile.getString(configurationMap, "texture", null);
		int     damage = FlatFile.getInt(configurationMap, "damage", 0),
			amount = FlatFile.getInt(configurationMap, "amount", 1);
		List<String> lore = FlatFile.getStringList(configurationMap, "lore");
		
		Map<Enchantment, Integer> enchantmentMap = null;
		int enchanted = -1;
		{
			Object enchantmentObj = configurationMap.get("enchanted");
			//Instanceof wont detect a List object type if it is null
			if(enchantmentObj instanceof List)
			{
				enchantmentMap = new HashMap<>();
				if(!((List<String>) enchantmentObj).isEmpty())
				{
					List<String> enchantmentList = (List<String>) enchantmentObj;
					for(String enchantmentLine : enchantmentList)
					{
						Enchantment enchantment = Enchantment.getByName(PATTERN_SEPERATOR.split(enchantmentLine)[0]);
						if(enchantment == null)
						{
							BUtil.logError("Enchantment '" + enchantmentLine + "' is not a valid enchantment configuration. Check your config.");
							continue;
						}
						
						try
						{
							enchantmentMap.put(enchantment, Integer.parseInt(PATTERN_SEPERATOR.split(enchantmentLine)[1]));
						}
						catch(NumberFormatException e)
						{
							BUtil.logError("Enchantment " + enchantment.getName() + " has an invalid level");
						}
					}
					
					if(enchantmentList.isEmpty())
					{
						enchantmentMap = null;
					}
				}
			}
			else if(enchantmentObj instanceof Boolean)
			{
				enchanted = Boolean.parseBoolean(enchantmentObj.toString()) ? 1 : 0;
			}
		}
		
		return new ItemContainer(material, damage, amount, displayName, lore,
		                         EnchantStatus.getEnchantStatus(enchanted), enchantmentMap,
		                         owner, skullTexture, null);
	}
	
}

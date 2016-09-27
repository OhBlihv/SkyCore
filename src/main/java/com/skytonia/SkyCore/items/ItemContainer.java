package com.skytonia.SkyCore.items;

import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.FlatFile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.SKULL_ITEM;

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
	private final MaterialData storedData;
	
	@Getter
	private final String owner;
	
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
		Material        material = (material = (Material) overriddenValues.get(ItemContainerVariable.MATERIAL)) != null ? material : this.material;
		int             amount = (amount = (int) overriddenValues.getOrDefault(ItemContainerVariable.AMOUNT, 0)) > 0 ? amount : this.amount,
						damage = (damage = (int) overriddenValues.getOrDefault(ItemContainerVariable.DAMAGE, 0)) > 0 ? damage : this.damage;
		String          displayName = (displayName = (String) overriddenValues.get(ItemContainerVariable.DISPLAYNAME)) != null ? displayName : this.displayName;
		List<String>    lore = (lore = (List<String>) overriddenValues.get(ItemContainerVariable.LORE)) != null ? lore : this.lore;
		EnchantStatus   enchantStatus = (enchantStatus = (EnchantStatus) overriddenValues.get(ItemContainerVariable.ENCHANTED)) != null ? enchantStatus : this.enchantStatus;
		Map<Enchantment, Integer> enchantmentMap =
						(enchantmentMap = (Map<Enchantment, Integer>) overriddenValues.get(ItemContainerVariable.ENCHANTMENTS)) != null ? enchantmentMap : this.enchantmentMap;
		String          owner = (owner = (String) overriddenValues.get(ItemContainerVariable.OWNER)) != null ? owner : this.owner;
		
		ItemStack itemStack = new ItemStack(material, amount, (short) damage);
		ItemMeta itemMeta = itemStack.getItemMeta();
		
		if(displayName != null)
		{
			itemMeta.setDisplayName(displayName);
		}
		if((material == Material.SKULL_ITEM || material == Material.SKULL) && (playerName != null || owner != null))
		{
			String skullOwner;
			//Allow the input player to override the owner for this skull
			if(this.owner == null || this.owner.equals("PLAYER"))
			{
				skullOwner = playerName;
			}
			else
			{
				skullOwner = this.owner;
			}
			
			if(skullOwner != null)
			{
				((SkullMeta) itemMeta).setOwner(skullOwner);
				if(itemMeta.hasDisplayName())
				{
					itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{player}", skullOwner));
				}
			}
		}
		if(lore != null)
		{
			itemMeta.setLore(lore);
		}
		
		itemStack.setItemMeta(itemMeta);
		
		if(this.storedData != null)
		{
			itemStack.setData(storedData);
		}
		
		if(enchantmentMap != null)
		{
			itemStack.addEnchantments(enchantmentMap);
		}
		
		return enchantStatus.alterEnchantmentStatus(itemStack);
	}
	
	public static ItemContainer fromItemStack(ItemStack itemStack)
	{
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
		if(itemStack.getType() == SKULL_ITEM)
		{
			owner = ((SkullMeta) itemMeta).getOwner();
		}
		
		return new ItemContainer(itemStack.getType(), itemStack.getDurability(), itemStack.getAmount(), displayname, lore,
		                         EnchantStatus.NO_CHANGE, enchantmentMap, itemStack.getData(), owner);
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
			             (int) overriddenValues.get(ItemContainerVariable.DAMAGE) :
				                                                                      configurationSection.getInt("damage", 0),
			amount = overriddenValues.containsKey(ItemContainerVariable.AMOUNT) ?
				         (int) overriddenValues.get(ItemContainerVariable.AMOUNT) :
					                                                                  configurationSection.getInt("amount", 1);
		
		String displayName = overriddenValues.containsKey(ItemContainerVariable.DISPLAYNAME) ?
			                     BUtil.translateColours((String) overriddenValues.get(ItemContainerVariable.DISPLAYNAME)) :
				                                                                                                              BUtil.translateColours(configurationSection.getString("displayname", ""));
		
		Map<Enchantment, Integer> enchantmentMap = (Map<Enchantment, Integer>) overriddenValues.get(ItemContainerVariable.ENCHANTMENTS);
		int isEnchanted = (int) overriddenValues.getOrDefault(ItemContainerVariable.ENCHANTED, -1);
		if((isEnchanted != -1 || enchantmentMap != null) && configurationSection.get("enchanted") != null)
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
		
		return new ItemContainer(material, damage, amount, displayName, lore, EnchantStatus.getEnchantStatus(isEnchanted), enchantmentMap, null, owner);
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
		
		String  displayName = BUtil.translateColours(FlatFile.getString(configurationMap, "displayname", null)),
				owner = FlatFile.getString(configurationMap, "owner", null);
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
		
		return new ItemContainer(material, damage, amount, displayName, lore, EnchantStatus.getEnchantStatus(enchanted), enchantmentMap, null, owner);
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
	
}

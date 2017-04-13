package com.skytonia.SkyCore.items;

import com.skytonia.SkyCore.items.construction.ItemContainer;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.ReflectionUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.bukkit.Material.AIR;

public class GUIUtil
{

	//Used for Unit Testing outside CraftBukkit
	private static class CustomItemStack extends ItemStack
	{

		private Material material;
		private int amount;

		public CustomItemStack(Material material, int amount)
		{
			this.material = material;
			this.amount = amount;
		}

		@Override
		public int getMaxStackSize()
		{
			return 64;
		}

		@Override
		public int getAmount()
		{
			return this.amount;
		}

		@Override
		public String toString()
		{
			return material.name() + " " + amount;
		}

	}

	private static final int STACK_SIZE = 64;
	public static final ItemStack DEFAULT_ITEMSTACK = addEnchantmentEffect(new ItemStack(Material.POTATO_ITEM, 1, (short) 15));

	public static String getErrorMessage(ConfigurationSection configurationSection, String path, String inputValue)
	{
		return "Error reading " + path + " at (" + configurationSection.getCurrentPath() + ")\n" +
				       "Make sure this is a valid entry: " + (inputValue != null ? String.valueOf(inputValue) : "<BLANK>");
	}

	private static Class<?>     NMS_ItemStack = null,
								NMS_NBTTagCompound = null,
								NMS_NBTTagList = null,
								CRAFT_ItemStack = null;

	private static Method       NMS_ItemStack_hasTag = null,
						        NMS_ItemStack_setTag = null,
						        NMS_ItemStack_getTag = null,
								NMS_NBTTagCompound_set = null,
								NMS_NBTTagCompound_remove = null,
								CRAFT_ItemStack_asNMSCopy = null,
								CRAFT_ItemStack_asCraftMirror = null;

	/**
	 *
	 * @return False if this did not init correctly
	 */
	private static boolean initEnchantmentNMS()
	{
		try
		{
			if(NMS_ItemStack == null) //Attempt to resolve one, if one is null they all should be un-initialized
			{
				NMS_ItemStack = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("ItemStack");
				NMS_NBTTagCompound = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("NBTTagCompound");
				NMS_NBTTagList = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("NBTTagList");

				CRAFT_ItemStack = ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack");

				//Only used for getting methods below
				Class NMS_NBTBase = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("NBTBase");

				NMS_ItemStack_hasTag = NMS_ItemStack.getMethod("hasTag");
				NMS_ItemStack_setTag = NMS_ItemStack.getMethod("setTag", NMS_NBTTagCompound);
				NMS_ItemStack_getTag = NMS_ItemStack.getMethod("getTag");
				NMS_NBTTagCompound_set = NMS_NBTTagCompound.getMethod("set", String.class, NMS_NBTBase);
				NMS_NBTTagCompound_remove = NMS_NBTTagCompound.getMethod("remove", String.class);
				CRAFT_ItemStack_asNMSCopy = CRAFT_ItemStack.getMethod("asNMSCopy", ItemStack.class);
				CRAFT_ItemStack_asCraftMirror = CRAFT_ItemStack.getMethod("asCraftMirror", NMS_ItemStack);
			}
			return true;
		}
		catch(ClassNotFoundException | NoSuchMethodException e)
		{
			e.printStackTrace();
			BUtil.logError("Your minecraft version seems to be modded. Enchantment effects will not be supported in this version.");
			return false;
		}
	}

	public static ItemStack addEnchantmentEffect(ItemStack itemStack)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		
		itemMeta.addEnchant(Enchantment.MENDING, 1, true);
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES);
		
		itemStack.setItemMeta(itemMeta);
		
		return itemStack;
		
		/*if(!initEnchantmentNMS())
		{
			BUtil.logInfo("Could not initialize NMS");
			return item; //Return the item without enchantment effects
		}

		try
		{
			Object  nmsStack = CRAFT_ItemStack_asNMSCopy.invoke(null, item),
					tag = null, //NBTTagCompound
					enchTag;    //NBTTagList

			if(!((boolean) NMS_ItemStack_hasTag.invoke(nmsStack)))
			{
				tag = NMS_NBTTagCompound.newInstance();
				NMS_ItemStack_setTag.invoke(nmsStack, tag);
			}

			if (tag == null)
			{
				tag = NMS_ItemStack_getTag.invoke(nmsStack);
			}

			enchTag = NMS_NBTTagList.newInstance();
			
			//TODO: Properly Test this and streamline it?
			/*try
			{
				switch(BUtil.getNMSVersion())
				{
					case "v1_9_R2":
					{
						BUtil.logInfo(enchTag.toString());
						net.minecraft.server.v1_9_R2.NBTTagList enchTagList = (net.minecraft.server.v1_9_R2.NBTTagList) enchTag;
						while(!enchTagList.isEmpty())
						{
							enchTagList.remove(0);
						}
						((net.minecraft.server.v1_9_R2.NBTTagList) enchTag).add(new net.minecraft.server.v1_9_R2.NBTTagInt(-1));
						BUtil.logInfo(enchTag.toString());
						break;
					}
					default: break;
				}
			}
			catch(Exception updateNav)
			{
				updateNav.printStackTrace();
				return item;
			}*
			
			NMS_NBTTagCompound_set.invoke(tag, "ench", enchTag);
			NMS_ItemStack_setTag.invoke(nmsStack, tag);

			return (ItemStack) CRAFT_ItemStack_asCraftMirror.invoke(null, nmsStack);
		}
		catch(ClassCastException | IllegalAccessException | InvocationTargetException | InstantiationException updateNav)
		{
			updateNav.printStackTrace();
			BUtil.logError("Your minecraft version seems to be modded. Enchantment effects will not be supported in this version.");
			return tiem;
		}*/
	}

	public static ItemStack removeEnchantmentEffect(ItemStack item)
	{
		if(!initEnchantmentNMS())
		{
			return item; //Return the item without enchantment effects
		}

		try
		{
			Object  nmsStack = CRAFT_ItemStack_asNMSCopy.invoke(null, item),
					tag = null;

			if(!((boolean) NMS_ItemStack_hasTag.invoke(nmsStack)))
			{
				tag = NMS_NBTTagCompound.newInstance();
				NMS_ItemStack_setTag.invoke(nmsStack, tag);
			}

			if (tag == null)
			{
				tag = NMS_ItemStack_getTag.invoke(nmsStack);
			}

			NMS_NBTTagCompound_remove.invoke(tag, "ench");
			NMS_ItemStack_setTag.invoke(nmsStack, tag);
			return (ItemStack) CRAFT_ItemStack_asCraftMirror.invoke(null, nmsStack);
		}
		catch(InvocationTargetException | IllegalAccessException | InstantiationException e)
		{
			e.printStackTrace();
			BUtil.logError("Your minecraft version seems to be modded. Enchantment effects will not be supported in this version.");
			return item;
		}
	}

	private static final Pattern COLON_SPLIT = Pattern.compile("[:]");
	
	public static Map<Enchantment, Integer> addEnchantments(List<String> enchantList)
	{
		if(enchantList == null || enchantList.isEmpty())
		{
			return null;
		}
		
		HashMap<Enchantment, Integer> enchantMap = new HashMap<>();
		for(String enchantLine : enchantList)
		{
			String[] split = COLON_SPLIT.split(enchantLine);
			
			Enchantment enchantment = Enchantment.getByName(split[0]);
			if(enchantment == null)
			{
				BUtil.logError("Loaded invalid Enchantment: '" + split[0] + "'");
				continue;
			}
			enchantMap.put(enchantment, Integer.parseInt(split[1]));
		}
		
		return enchantMap;
	}
	
	public static int countEmpty(Inventory inventory)
	{
		int emptySlots = 0;
		
		//CraftInventoryView inventoryView = new CraftInventoryView(null, inventory, null);
		
		boolean hardLimit = inventory instanceof PlayerInventory;
		
		//InventoryType.SlotType slotType;
		ItemStack itemStack;
		for(int slot = 0;slot < inventory.getSize();slot++)
		{
			if(hardLimit && slot > 35)
			{
				break;
			}
			
			//slotType = CraftInventoryView.getSlotType(inventoryView, slot);
			
			if(//( slotType == InventoryType.SlotType.QUICKBAR ||
			   //  slotType == InventoryType.SlotType.CONTAINER) &&
				 ((itemStack = inventory.getItem(slot)) == null || itemStack.getType() == AIR))
			{
				emptySlots++;
			}
		}
		
		return emptySlots;
	}
	
	public static int countEmpty(ItemStack[] itemStacks)
	{
		int count = 0;
		for(ItemStack item : itemStacks)
		{
			if(item == null || item.getType() == AIR)
			{
				count++;
			}
		}
		return count;
	}

	public static int countMaterial(Inventory inventory, Material material)
	{
		int count = 0;
		for(ItemStack item : inventory.all(material).values())
		{
			count += item.getAmount();
		}
		return count;
	}

	public static int countItem(Inventory inventory, Material material, short durability)
	{
		int count = 0;
		for(ItemStack item : inventory.all(material).values())
		{
			if(item.getDurability() == durability)
			{
				count += item.getAmount();
			}
		}
		return count;
	}
	
	public static int countItem(Inventory inventory, ItemStack itemStack)
	{
		ItemStack comparedItemStack = itemStack.clone();
		comparedItemStack.setAmount(1);
		
		int count = 0;
		for(ItemStack item : inventory.all(itemStack.getType()).values())
		{
			if(item.equals(comparedItemStack))
			{
				count += item.getAmount();
			}
		}
		return count;
	}
	
	public static int countItem(Inventory inventory, Material material, int damage, final int minRange, final int maxRange)
	{
		int count = 0;
		for(int i = minRange;i <= maxRange;i++)
		{
			ItemStack item = inventory.getItem(i);
			if(item == null || item.getType() == Material.AIR)
			{
				continue;
			}
			
			if(item.getType() == material && (damage < 0 || item.getDurability() == damage))
			{
				count += item.getAmount();
			}
		}
		return count;
	}
	
	public static int countItem(Inventory inventory, ItemContainer itemContainer)
	{
		int count = 0;
		for(ItemStack item : inventory.all(itemContainer.getMaterial()).values())
		{
			if(itemContainer.equals(item))
			{
				count += item.getAmount();
			}
		}
		return count;
	}

	/**
	 * Overly simplistic 'merge stacks' function.
	 * Required improvements:
	 *          - .isSimilar() checks
	 *          - Stricter requirements for equality
	 *
	 * @param itemStacks Input ItemStacks
	 * @return Minimum ItemStacks required
	 */
	public static ItemStack[] mergeStacks(Collection<? extends ItemStack> itemStacks)
	{
		if(itemStacks.isEmpty())
		{
			return null;
		}

		if(itemStacks.size() == 1)
		{
			//Only way to access the first element outside anything overly complicated for this use-case
			return new ItemStack[] {itemStacks.iterator().next()};
		}

		int currentStackCount = 0, leftOver = 0, stackSize;
		Material material;
		//Seems to be the only way to get any item of a 'Collection'
		{
			ItemStack item = itemStacks.iterator().next();
			stackSize = STACK_SIZE;
			material = item.getType();
		}

		if(material == null)
		{
			return null;
		}

		//Create a larger array the size of the collection, so that is is definitely large enough. Resize it later.
		ItemStack[] mergedStacks = new ItemStack[itemStacks.size() + 1];
		for(ItemStack item : itemStacks)
		{
			//BUtil.logInfo("Processing: " + item.toString());
			//Items shouldn't be over their stack size
			if(item.getAmount() >= stackSize)
			{
				ItemStack tempItem = mergedStacks[currentStackCount];
				mergedStacks[currentStackCount++] = item;
				mergedStacks[currentStackCount] = tempItem;
				//BUtil.logInfo("Over Stack Size!");
				continue;
			}

			ItemStack currentStack = mergedStacks[currentStackCount];

			if(currentStack == null)
			{
				mergedStacks[currentStackCount] = item;
				//BUtil.logInfo("Replacing Null Object");
				continue;
			}

			int newAmount = currentStack.getAmount() + item.getAmount();
			//BUtil.logInfo("New Amount " + newAmount + " | Current Stack: " + currentStack.getAmount() + " | Item " + item.getAmount());
			if(newAmount <= stackSize)
			{
				mergedStacks[currentStackCount].setAmount(newAmount);
				if(newAmount == stackSize)
				{
					currentStackCount++;
				}
			}
			else //Add any left over items to the leftOver variable, to be compiled later
			{
				int tempLeftOver = newAmount - stackSize;
				mergedStacks[currentStackCount++].setAmount(newAmount - tempLeftOver);
				leftOver += tempLeftOver;
				//BUtil.logInfo("Adding " + tempLeftOver + " to leftOver equalling " + leftOver);
			}
		}

		while(leftOver > 0)
		{
			if(leftOver >= stackSize)
			{
				mergedStacks[currentStackCount++] = new ItemStack(material, stackSize);
				leftOver -= stackSize;
			}
			else
			{
				mergedStacks[currentStackCount++] = new ItemStack(material, leftOver);
				leftOver = 0;
			}
		}

		//Move the array into the most appropriate size (no empty/null elements)
		int usedElements = 0;
		for(ItemStack stack : mergedStacks)
		{
			if(stack == null)
			{
				break;
			}
			usedElements++;
		}

		if(usedElements != mergedStacks.length)
		{
			ItemStack[] mergedStacksTemp = new ItemStack[usedElements];
			//Copy the array to a proper size
			System.arraycopy(mergedStacks, 0, mergedStacksTemp, 0, usedElements);
			mergedStacks = mergedStacksTemp;
		}

		return mergedStacks;
	}

	/**
	 * Returns an array of slots vertical to the slot input.
	 * Useful for stacking multiple stacks of the same material
	 * in easy to reach slots.
	 *
	 * @param slot (Most likely) Hotbar slot
	 * @return Array of slots above (and including) the input slot
	 */
	public static int[] getVerticalSlots(int slot, int requiredSlots)
	{
		if(requiredSlots <= 1)
		{
			return new int[] {slot};
		}

		int[] verticalSlots = new int[requiredSlots];
		verticalSlots[0] = slot;

		//Is on the hotbar
		if(slot < 9)
		{
			int currentSlot = slot;
			for(int invSlot = 1;invSlot < verticalSlots.length;invSlot++)
			{
				if(currentSlot == slot)
				{
					currentSlot = 27 + slot; //Receive the slot directly above the hotbar
				}
				else
				{
					//If the current slot is already on the top bar, move down to the next column
					if(currentSlot <= 17)
					{
						currentSlot = 27 + (slot + 1);
					}
					else
					{
						//Else, move up one row
						currentSlot -= 9;
					}
				}

				verticalSlots[invSlot] = currentSlot;
			}
		}

		//BUtil.logInfo("Vertical Slots: " + Arrays.toString(verticalSlots));
		return verticalSlots;
	}
	
	public static boolean canAddItem(Inventory inventory, ItemStack itemStack)
	{
		return getAvailableSlots(inventory, itemStack) > itemStack.getAmount();
	}
	
	public static int getAvailableSlots(Inventory inventory, ItemStack itemStack)
	{
		int emptySlots = countEmpty(inventory);
		if(emptySlots > 0)
		{
			return emptySlots * itemStack.getMaxStackSize();
		}
		
		int amountToAdd = itemStack.getAmount();
		
		for(ItemStack itemLoop : inventory)
		{
			if(itemLoop != null)
			{
				if(itemLoop.isSimilar(itemStack))
				{
					if(itemLoop.getAmount() < itemLoop.getMaxStackSize())
					{
						amountToAdd -= (itemLoop.getMaxStackSize() - itemLoop.getAmount());
					}
				}
				
				if(amountToAdd <= 0)
				{
					return itemStack.getAmount();
				}
			}
		}
		
		return -1;
	}

	public static final ItemStack BLANK_ITEM = new ItemStack(AIR, 0);

	public static void removeItemCount(Inventory inventory, Material material, short damage, int count)
	{
		Iterator<? extends Map.Entry<Integer, ? extends ItemStack>> itemStackItr = inventory.all(material).entrySet().iterator();
		while(count > 0 && itemStackItr.hasNext())
		{
			Map.Entry<Integer, ? extends ItemStack> entry = itemStackItr.next();
			if(entry.getValue().getDurability() != damage)
			{
				continue; //Only remove what is matched
			}

			if(count > entry.getValue().getAmount())
			{
				count -= entry.getValue().getAmount();
				inventory.setItem(entry.getKey(), BLANK_ITEM);
			}
			else //Set
			{
				int setAmount = entry.getValue().getAmount() - count;
				count -= entry.getValue().getAmount();
				entry.getValue().setAmount(setAmount);
				inventory.setItem(entry.getKey(), entry.getValue()); //May be useless, find out.
				break;
			}
		}
	}
	
	public static void removeItemCount(Inventory inventory, ItemStack itemStack, int count)
	{
		Iterator<? extends Map.Entry<Integer, ? extends ItemStack>> itemStackItr = inventory.all(itemStack.getType()).entrySet().iterator();
		while(count > 0 && itemStackItr.hasNext())
		{
			Map.Entry<Integer, ? extends ItemStack> entry = itemStackItr.next();
			if(!entry.getValue().equals(itemStack))
			{
				continue; //Only remove what is matched
			}
			
			if(count > entry.getValue().getAmount())
			{
				count -= entry.getValue().getAmount();
				inventory.setItem(entry.getKey(), BLANK_ITEM);
			}
			else //Set
			{
				int setAmount = entry.getValue().getAmount() - count;
				count -= entry.getValue().getAmount();
				entry.getValue().setAmount(setAmount);
				inventory.setItem(entry.getKey(), entry.getValue()); //May be useless, find out.
				break;
			}
		}
	}

}

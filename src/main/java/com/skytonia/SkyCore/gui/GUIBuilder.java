package com.skytonia.SkyCore.gui;

import com.skytonia.SkyCore.gui.actions.ElementAction;
import com.skytonia.SkyCore.gui.actions.ElementActions;
import com.skytonia.SkyCore.gui.config.GUISound;
import com.skytonia.SkyCore.gui.config.InventorySize;
import com.skytonia.SkyCore.gui.variables.GUIVariable;
import com.skytonia.SkyCore.gui.variables.GUIVariables;
import com.skytonia.SkyCore.items.construction.ItemContainer;
import com.skytonia.SkyCore.items.construction.ItemContainerConstructor;
import com.skytonia.SkyCore.items.construction.ItemContainerVariable;
import com.skytonia.SkyCore.util.BUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 1/20/2017.
 */
public class GUIBuilder<T>
{
	
	@RequiredArgsConstructor
	public static class GUIElementInfo
	{
		
		@Getter
		private final GUIElement guiElement;
		
		@Getter
		private final ConfigurationSection elementConfiguration;
		
		@Getter
		private final int slot;
		
	}
	
	final Class<T> guiTypeClass;
	
	private String guiTitle;
	
	public GUIBuilder(Class<T> guiTypeClass)
	{
		this.guiTypeClass = guiTypeClass;
	}
	
	public T build(ConfigurationSection configurationSection)
	{
		guiTitle = BUtil.translateColours(configurationSection.getString("title", "Inventory"));
		
		InventorySize inventorySize;
		{
			//Legacy Support for number sizes
			if(configurationSection.isInt("size"))
			{
				inventorySize = InventorySize.ofSize(configurationSection.getInt("size", 54));
			}
			else
			{
				try
				{
					inventorySize = InventorySize.valueOf(configurationSection.getString("size"));
				}
				//Catch Everything we can
				catch(Exception e)
				{
					String inventoryString;
					try
					{
						inventoryString = configurationSection.getString("size");
					}
					catch(NullPointerException e2)
					{
						inventoryString = "null";
					}
					catch(Exception e2)
					{
						inventoryString = ">unknown<";
						e.printStackTrace();
					}
					
					BUtil.logInfo("Unknown Inventory Size '" + inventoryString + "'. Defaulting to SIX_LINE");
					BUtil.logInfo("Options: " + Arrays.toString(InventorySize.values()) + " (Or a slot amount, eg '18')");
					inventorySize = InventorySize.SIX_LINE;
				}
			}
		}
		
		String 	requiredPermission = configurationSection.getString("permission", null),
			noPermissionMessage = BUtil.translateColours(configurationSection.getString("no-permission", ""));
		
		GUISound openSound = null;
		if(configurationSection.contains("open-sound") && configurationSection.isConfigurationSection("open-sound"))
		{
			ConfigurationSection soundSection = configurationSection.getConfigurationSection("open-sound");
			Sound sound = null;
			try
			{
				sound = Sound.valueOf(soundSection.getString("sound"));
			}
			catch(IllegalArgumentException e)
			{
				//Check if it's ITEM_PICKUP and translate
				if(soundSection.getString("sound").equals("ITEM_PICKUP"))
				{
					sound = Sound.ENTITY_ITEM_PICKUP;
				}
			}
			catch(NullPointerException e)
			{
				//Sound stays null and is caught by the next check
			}
			
			if(sound == null)
			{
				BUtil.logInfo("Could not load sound '" + (soundSection.contains("sound") ? soundSection.getString("sound", "none") : "null") + "'");
			}
			else
			{
				openSound = new GUISound(sound, (float) soundSection.getDouble("volume", 10F), (float) soundSection.getDouble("pitch", 1F));
			}
		}
		
		ItemStack fillerItem = null;
		if(configurationSection.contains("filler"))
		{
			fillerItem = ItemContainerConstructor.buildItemContainer(configurationSection.getConfigurationSection("filler")).toItemStack();
		}
		/*else
		{
			//If we're SkyCore and not another plugin, use a default filler item based on config
			//Plugin pluginInstance = SkyCore.getPluginInstance();
			/*if(pluginInstance != null && pluginInstance.getClass().getSimpleName().equals("SkyCore"))
			{
				fillerItem = new ItemContainerBuilder()
					             .material(Material.STAINED_GLASS_PANE)
					             .damage(7)
					             .displayname("§8" + FlatFile.getInstance().getString("options.server-name"))
					             .build().toItemStack();
			}*/
		//}*/
		
		Deque<GUIElementInfo> guiElements = loadGUIElements(configurationSection, inventorySize.getSize());
		
		Deque<GUIVariable> guiVariables = new ArrayDeque<>();
		guiVariables.addAll(GUIVariables.getInstance().getAllRegisteredVariables());
		
		try
		{
			Constructor<T> constructor =  guiTypeClass.getConstructor( String.class, InventorySize.class,
			                                                           String.class, String.class,
			                                                           GUISound.class, ItemStack.class, Deque.class, Deque.class,
			                                                           ConfigurationSection.class);
			
			return constructor.newInstance(guiTitle, inventorySize,
			                               requiredPermission, noPermissionMessage,
			                               openSound, fillerItem, guiElements, guiVariables,
			                               configurationSection);
		}
		catch(InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e)
		{
			if(e instanceof InvocationTargetException)
			{
				BUtil.logInfo("Unable to construct GUI of type " + guiTypeClass.getSimpleName() + ((InvocationTargetException) e).getTargetException().getMessage());
			}
			else
			{
				BUtil.logInfo("Unable to construct GUI of type " + guiTypeClass.getSimpleName() + ". Does it extend GUIContainer.class correctly?");
			}
			
			e.printStackTrace();
			return null;
		}
	}
	
	private Deque<GUIElementInfo> loadGUIElements(ConfigurationSection configurationSection, int maxSize)
	{
		Deque<GUIElementInfo> guiElements = new ArrayDeque<>(maxSize);
		
		if(configurationSection.contains("elements") && configurationSection.isConfigurationSection("elements"))
		{
			for(String slotString : configurationSection.getConfigurationSection("elements").getKeys(false))
			{
				if(StringUtils.isNumeric(slotString))
				{
					int slot = Integer.parseInt(slotString);
					if(slot < maxSize)
					{
						GUIElement guiElement = loadGUIElement(configurationSection, "elements." + slotString, slot);
						if(guiElement == GUIElement.DEFAULT_GUI_ELEMENT)
						{
							continue;
						}
						
						//Assume the GUIElement Configuration is valid since the default element was not returned.
						guiElements.add(new GUIElementInfo(guiElement, configurationSection.getConfigurationSection("elements." + slotString), slot));
					}
					else
					{
						BUtil.logInfo("Slot '" + slotString + "' in " + configurationSection.getCurrentPath() + " " + slotString + " is outside the gui! (" + maxSize + ")");
					}
				}
				else
				{
					BUtil.logInfo("Slot '" + slotString + "' in " + configurationSection.getCurrentPath() + " " + slotString + " is not a valid integer");
				}
			}
		}
		return guiElements;
	}
	
	public static GUIElement loadGUIElement(ConfigurationSection baseSection, String subSection, int slot)
	{
		return loadGUIElement(baseSection, subSection, slot, EnumSet.allOf(ItemContainerVariable.class), new HashMap<>());
	}
	
	public static GUIElement loadGUIElement(ConfigurationSection baseSection, String subSection, int slot,
		/*ItemContainer Specifics*/         EnumSet<ItemContainerVariable> checkedErrors,
		                                Map<ItemContainerVariable, Object> overriddenValues)
	{
		ConfigurationSection subConfigurationSection = baseSection.getConfigurationSection(subSection);
		if(subConfigurationSection == null)
		{
			BUtil.logError("Error loading GUI Element at slot '" + slot + "' in GUI (" + baseSection.getCurrentPath() + "." + subSection + ")");
			return GUIElement.DEFAULT_GUI_ELEMENT;
		}
		
		return loadGUIElement(subConfigurationSection, slot, checkedErrors, overriddenValues);
	}
	
	private static GUIElement loadGUIElement(ConfigurationSection configurationSection, int slot,
	                                  EnumSet<ItemContainerVariable> checkedErrors,
	                                  Map<ItemContainerVariable, Object> overriddenValues)
	{
		ItemContainer itemContainer;
		try
		{
			itemContainer = ItemContainerConstructor.buildItemContainer(configurationSection, checkedErrors, overriddenValues);
		}
		catch(IllegalArgumentException e)
		{
			BUtil.logStackTrace(e);
			
			//An error occurred and was printed to console
			return GUIElement.DEFAULT_GUI_ELEMENT;
		}
		
		Deque<ElementAction> elementActionDeque;
		try
		{
			elementActionDeque = ElementActions.getInstance().getElementActions(configurationSection, slot);
		}
		catch(Exception e)
		{
			BUtil.logInfo("Error occurred loading element actions for " + configurationSection.getCurrentPath() + ". No actions will be loaded for this entry.");
			elementActionDeque = new ArrayDeque<>();
			BUtil.logStackTrace(e);
		}
		
		return new GUIElement(itemContainer, elementActionDeque);
	}
	
}

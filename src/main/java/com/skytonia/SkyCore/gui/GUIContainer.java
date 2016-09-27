package com.skytonia.SkyCore.gui;

import com.skytonia.SkyCore.gui.actions.ElementAction;
import com.skytonia.SkyCore.gui.actions.ElementActions;
import com.skytonia.SkyCore.gui.variables.GUIVariable;
import com.skytonia.SkyCore.gui.variables.GUIVariables;
import com.skytonia.SkyCore.items.GUIUtil;
import com.skytonia.SkyCore.items.ItemContainer;
import com.skytonia.SkyCore.items.ItemContainerVariable;
import com.skytonia.SkyCore.util.BUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by OhBlihv (Chris) on 22/11/2015.
 */
@RequiredArgsConstructor
public class GUIContainer implements Listener
{

	@NonNull
	protected Pattern guiTitlePattern;

	@Getter
	@NonNull
	protected String guiTitle;

	@Getter
	protected int guiSize;
	
	@Getter
	protected String requiredPermission;
	
	@Getter
	protected String noPermissionMessage;
	
	@Getter
	protected GUISound openSound;

	@Getter
	@Setter
	protected ItemStack fillerItem;

	@Getter
	protected GUIElement[] guiElements;

	@Getter
	protected Deque<GUIVariable> guiVariables = null;

	public GUIContainer(ConfigurationSection configurationSection)
	{
		loadGUI(configurationSection);

		Plugin registeringPlugin = BUtil.getCallingJavaPlugin();
		
		Bukkit.getServer().getPluginManager().registerEvents(this, registeringPlugin);
		
		BUtil.logMessageAsPlugin("SkyCore", "Registered new " + getClass().getSimpleName() + " to: " + registeringPlugin);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if(event.getInventory() != null && event.getInventory().getTitle() != null &&
				   compareGUITitle(event.getInventory().getTitle()) &&
			event.getRawSlot() != -999 && event.getRawSlot() < guiSize)
		{
			event.setCancelled(true);
			doActions((Player) event.getWhoClicked(), event.getClick(), event.getSlot());
		}
	}

	public boolean compareGUITitle(String inventoryTitle)
	{
		return guiTitlePattern.matcher(inventoryTitle).find();
	}

	public void openInventory(Player player)
	{
		if(requiredPermission != null && !player.hasPermission(requiredPermission))
		{
			player.sendMessage(noPermissionMessage);
			return;
		}
		
		Inventory inventory = Bukkit.createInventory(null, guiSize, guiTitle);
		inventory = getInventory(inventory, player);
		player.openInventory(inventory);
		
		if(openSound != null)
		{
			openSound.playSound(player);
		}
	}

	public boolean updateInventory(Player player)
	{
		Inventory inventory = player.getOpenInventory().getTopInventory();
		if(inventory == null || inventory.getSize() != guiSize)
		{
			return false;
		}
		inventory.clear();

		getInventory(inventory, player);
		player.updateInventory();
		return true;
	}

	protected Inventory getInventory(Inventory inventory, Player player)
	{
		if(fillerItem != null)
		{
			//Don't use guiSize here to preserve compatibility with updateInventory and missing guiSizes in the config
			for(int slot = 0;slot < inventory.getSize();slot++)
			{
				inventory.setItem(slot, fillerItem);
			}
		}

		if(guiElements != null)
		{
			for(int slot = 0; slot < guiElements.length;slot++)
			{
				if(guiElements[slot] == null)
				{
					continue;
				}

				inventory.setItem(slot, guiElements[slot].toItemStack(guiVariables, player));
			}
		}

		return inventory;
	}

	public void doActions(Player player, ClickType clickAction, int slot)
	{
		if( slot < 0 || slot > guiSize || guiElements[slot] == null || guiElements[slot].getElementActions() == null)
		{
			return;
		}
		
		for(ElementAction elementAction : guiElements[slot].getElementActions())
		{
			//Only process actions tied to this click type
			if(elementAction.hasClickType() && elementAction.clickType != clickAction)
			{
				continue;
			}
			
			//Only do actions while the previous one succeeded
			if(!elementAction.onClick(player, slot))
			{
				break;
			}
		}
	}

	/*
	 * -------------------------------------------------------
	 *                  Loading Methods
	 * -------------------------------------------------------
	 */

	public void loadGUI(ConfigurationSection configurationSection)
	{
		if(configurationSection == null || configurationSection.getKeys(false).isEmpty())
		{
			BUtil.logError("Error loading GUI named: " + (configurationSection == null ? "NULL" : configurationSection.getName()));
			effectiveConstructor("INVALID CONFIG", 54, null, null, null, GUIUtil.DEFAULT_ITEMSTACK, null, null);
			return;
		}

		guiTitle = BUtil.translateColours(configurationSection.getString("title", "Inventory"));
		int guiSize = configurationSection.getInt("size", 54);
		
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
			catch(IllegalArgumentException | NullPointerException e)
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
			fillerItem = ItemContainer.buildItemContainer(configurationSection.getConfigurationSection("filler")).toItemStack();
		}

		GUIElement[] guiElements = loadGUIElements(configurationSection, new GUIElement[guiSize]);

		Deque<GUIVariable> guiVariables = new ArrayDeque<>();
		guiVariables.addAll(GUIVariables.getInstance().getAllRegisteredVariables());

		effectiveConstructor(guiTitle, guiSize, requiredPermission, noPermissionMessage, openSound, fillerItem, guiElements, guiVariables);
	}

	public GUIElement[] loadGUIElements(ConfigurationSection configurationSection, GUIElement[] guiElements)
	{
		if(configurationSection.contains("elements") && configurationSection.isConfigurationSection("elements"))
		{
			for(String slotString : configurationSection.getConfigurationSection("elements").getKeys(false))
			{
				if(StringUtils.isNumeric(slotString))
				{
					int slot = Integer.parseInt(slotString);
					if(slot < guiElements.length)
					{
						GUIElement guiElement = loadGUIElement(configurationSection, "elements." + slotString, slot);
						if(guiElement == GUIElement.DEFAULT_GUI_ELEMENT)
						{
							continue;
						}
						
						guiElements[slot] = guiElement;
					}
					else
					{
						BUtil.logInfo("Slot '" + slotString + "' in " + configurationSection.getCurrentPath() + " " + slotString + " is outside the gui! (" + guiElements.length + ")");
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
	
	protected GUIElement loadGUIElement(ConfigurationSection baseSection, String subSection, int slot)
	{
		return loadGUIElement(baseSection, subSection, slot, null, null);
	}
	
	protected GUIElement loadGUIElement(ConfigurationSection baseSection, String subSection, int slot,
	/*ItemContainer Specifics*/         EnumSet<ItemContainerVariable> checkedErrors,
	                                    Map<ItemContainerVariable, Object> overriddenValues)
	{
		ConfigurationSection subConfigurationSection = baseSection.getConfigurationSection(subSection);
		if(subConfigurationSection == null)
		{
			BUtil.logError("Error loading GUI Element at slot '" + slot + "' in GUI " + guiTitle + " (" + baseSection.getCurrentPath() + "." + subSection + ")");
			return GUIElement.DEFAULT_GUI_ELEMENT;
		}
		
		//By default use all warnings
		if(checkedErrors == null)
		{
			checkedErrors = EnumSet.allOf(ItemContainerVariable.class);
		}
		
		return loadGUIElement(subConfigurationSection, slot, checkedErrors, overriddenValues);
	}
	
	private GUIElement loadGUIElement(ConfigurationSection configurationSection, int slot,
	                                  EnumSet<ItemContainerVariable> checkedErrors,
	                                  Map<ItemContainerVariable, Object> overriddenValues)
	{
		ItemContainer itemContainer =
			ItemContainer.buildItemContainer(configurationSection, checkedErrors, overriddenValues);
		if(itemContainer == null)
		{
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

	/*Simple Setters since I can't use Lombok's auto-generated constructor*/

	private void effectiveConstructor(String guiTitle, int guiSize,
	                                  String requiredPermission, String noPermissionMessage,
	                                  GUISound openSound, ItemStack fillerItem, GUIElement[] guiElements, Deque<GUIVariable> guiVariables)
	{
		this.guiTitlePattern = Pattern.compile(guiTitle.replaceAll("\\{.*\\}", ".*"));
		this.guiTitle = guiTitle;
		this.guiSize = guiSize;
		this.requiredPermission = requiredPermission;
		this.noPermissionMessage = noPermissionMessage;
		this.openSound = openSound;
		this.fillerItem = fillerItem;
		this.guiElements = guiElements;
		this.guiVariables = guiVariables;
	}

}

package com.skytonia.SkyCore.gui;

import com.skytonia.SkyCore.gui.actions.ElementAction;
import com.skytonia.SkyCore.gui.config.GUISound;
import com.skytonia.SkyCore.gui.config.InventorySize;
import com.skytonia.SkyCore.gui.variables.GUIVariable;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.RunnableShorthand;
import com.skytonia.SkyCore.util.StaticNMS;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Deque;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by OhBlihv (Chris) on 22/11/2015.
 */
public class GUIContainer implements Listener
{
	
	private static GUIListener guiListener = null;

	@NonNull
	protected final Pattern guiTitlePattern;

	@Getter
	@NonNull
	protected final String guiTitle;

	@Getter
	protected final InventorySize guiSize;
	
	@Getter
	protected final String requiredPermission;
	
	@Getter
	protected final String noPermissionMessage;
	
	@Getter
	protected final GUISound openSound;

	@Getter
	protected final ItemStack fillerItem;

	@Getter
	protected final GUIElement[] guiElements;

	@Getter
	protected final Deque<GUIVariable> guiVariables;
	
	public GUIContainer(String guiTitle, InventorySize guiSize,
	                    String requiredPermission, String noPermissionMessage,
	                    GUISound openSound, ItemStack fillerItem, Deque<GUIBuilder.GUIElementInfo> guiElementInfos, Deque<GUIVariable> guiVariables,
	                    //Include ConfigurationSection used for loading to load gui-specific extras
	                    ConfigurationSection configurationSection)
	{
		/*if(guiListener == null)
		{
			guiListener = new GUIListener();
			
			Bukkit.getPluginManager().registerEvents(guiListener, SkyCore.getPluginInstance());
		}*/
		
		Pattern tempTitlePattern = null;
		try
		{
			tempTitlePattern = Pattern.compile(guiTitle.replaceAll("\\{.*\\}", ".*"));
		}
		catch(PatternSyntaxException e)
		{
			//
		}
		this.guiTitlePattern = tempTitlePattern;
		this.guiTitle = guiTitle;
		this.guiSize = guiSize;
		this.requiredPermission = requiredPermission;
		this.noPermissionMessage = noPermissionMessage;
		this.openSound = openSound;
		this.fillerItem = fillerItem;
		this.guiVariables = guiVariables;
		
		//Allow subclasses to override the gui element creation process
		GUIElement[] guiElements = new GUIElement[guiSize.getSize()];
		
		for(GUIBuilder.GUIElementInfo guiElementInfo : guiElementInfos)
		{
			guiElements[guiElementInfo.getSlot()] = updateGUIEElement(guiElementInfo);
		}
		
		this.guiElements = guiElements;
		
		//Run this once both this and extending classes have initialized.
		RunnableShorthand.forThis().with(() -> loadExtras(configurationSection)).runNextTick();
		
		Plugin registeringPlugin = BUtil.getCallingJavaPlugin();
		
		Bukkit.getServer().getPluginManager().registerEvents(this, registeringPlugin);
		
		BUtil.logMessageAsPlugin("SkyCore", "Registered new " + getClass().getSimpleName() + " to: " + registeringPlugin);
	}
	
	public void loadExtras(ConfigurationSection configurationSection)
	{
		//
	}
	
	public GUIElement updateGUIEElement(GUIBuilder.GUIElementInfo guiElementInfo)
	{
		return guiElementInfo.getGuiElement();
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if(event.getInventory() != null && event.getInventory().getTitle() != null &&
				   compareGUITitle(event.getInventory().getTitle()) &&
					guiSize.isWithinRange(event.getRawSlot()))
		{
			event.setCancelled(true);
			doActions((Player) event.getWhoClicked(), event.getClick(), event.getSlot());
		}
	}

	public boolean compareGUITitle(String inventoryTitle)
	{
		if(guiTitlePattern == null)
		{
			return guiTitle.equals(inventoryTitle);
		}
		else
		{
			return guiTitlePattern.matcher(inventoryTitle).find();
		}
	}

	public void openInventory(Player player)
	{
		if(requiredPermission != null && !requiredPermission.isEmpty() && !player.hasPermission(requiredPermission))
		{
			if(noPermissionMessage != null && !noPermissionMessage.isEmpty())
			{
				player.sendMessage(noPermissionMessage);
			}
			return;
		}
		
		boolean newOpen = showOpenSound(player);
		
		Inventory inventory = createInventory(guiSize, guiTitle);
		inventory = getInventory(inventory, player);
		player.openInventory(inventory);
		
		if(newOpen) //OpenSound check is done above
		{
			openSound.playSound(player);
		}
	}
	
	protected boolean showOpenSound(Player player)
	{
		return openSound != null && player.getOpenInventory().getTopInventory().getClass().getSimpleName().equals("CraftInventoryCrafting");
	}
	
	protected Inventory createInventory(InventorySize guiSize, String guiTitle)
	{
		return StaticNMS.createInventory(guiSize.getSize(), guiTitle);
	}

	public boolean updateInventory(Player player)
	{
		Inventory inventory = player.getOpenInventory().getTopInventory();
		if(inventory == null || !guiSize.matchesSize(inventory.getSize()))
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
		GUIElement guiElement;
		if( !guiSize.isWithinRange(slot) || (guiElement = guiElements[slot]) == null || guiElement.getElementActions() == null)
		{
			return;
		}
		
		for(ElementAction elementAction : guiElement.getElementActions())
		{
			//Only process actions tied to this click type
			if(elementAction.hasClickType() && elementAction.clickType != clickAction)
			{
				continue;
			}
			
			//Only do actions while the previous one succeeded
			if(!elementAction.onClick(player, clickAction, slot))
			{
				break;
			}
		}
	}

}

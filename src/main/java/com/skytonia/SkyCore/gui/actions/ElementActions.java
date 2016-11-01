package com.skytonia.SkyCore.gui.actions;

import com.skytonia.SkyCore.common.addons.AddonRegistry;
import com.skytonia.SkyCore.common.addons.ClassAddon;
import com.skytonia.SkyCore.common.addons.SkyAddon;
import com.skytonia.SkyCore.common.addons.identifiers.AddonIdentifier;
import com.skytonia.SkyCore.common.addons.identifiers.StringAddonIdentifier;
import com.skytonia.SkyCore.util.BUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by OhBlihv (Chris) on 23/11/2015.
 */
public class ElementActions extends AddonRegistry implements Listener
{
	
	private static ElementActions instance = null;
	public static ElementActions getInstance()
	{
		if(instance == null)
		{
			instance = new ElementActions();
		}
		return instance;
	}
	
	public ElementActions() throws IllegalArgumentException
	{
		//Trigger loading of all ElementActions included in this plugin
		registerAddon("CHANGE_ITEM", ChangeItemAction.class);
		registerAddon("CLOSE_INVENTORY", CloseInventoryAction.class);
		registerAddon("COMMAND", CommandAction.class);
		registerAddon("ITEM", ItemAction.class);
		registerAddon("SOUND", SoundAction.class);
	}
	
	public void registerAddon(String actionName, Class<? extends ElementAction> elementClass) throws IllegalArgumentException
	{
		registerAddon(new StringAddonIdentifier(actionName), new ClassAddon(elementClass));
	}
	
	@Override
	protected void cleanupAddon(SkyAddon registeredAddon)
	{
		//TODO: Search all registered GUIs -
	}
	
	@Override
	protected Class<?> getAssociatedAddonClass()
	{
		return ElementAction.class;
	}
	
	/*
	 * Retrieval/Initialization
	 */
	
	public Deque<ElementAction> getElementActions(ConfigurationSection configurationSection, int slot)
	{
		Deque<ElementAction> elementActionDeque = new ArrayDeque<>();
		if(configurationSection == null || !configurationSection.contains("actions") || !configurationSection.isConfigurationSection("actions"))
		{
			//Quick Exit since there are no actions defined, yet we still want to return the new deque
			return elementActionDeque;
		}
		
		ConfigurationSection actionSection = configurationSection.getConfigurationSection("actions");
		//Support both action names, and click-types
		for(String actionKey : actionSection.getKeys(false))
		{
			ConfigurationSection subSection = null;
			//'ALL" is counted as an 'UNKNOWN' ClickType
			ClickType clickType = ClickType.UNKNOWN;
			
			if(actionKey.equals("ALL"))
			{
				//clickType = ClickType.UNKNOWN;
				subSection = actionSection.getConfigurationSection("ALL");
			}
			else
			{
				try
				{
					clickType = ClickType.valueOf(actionKey);
					subSection = actionSection.getConfigurationSection(actionKey);
				}
				catch(IllegalArgumentException e)
				{
					//Actions are configured under actions:, and not a clickType.
				}
			}
			
			//If the subSection has not been defined,
			//a ClickType was not provided and is assume to be 'ALL'/'UNKNOWN'
			//This means the actions are defined on this level and do not use the ClickType system
			elementActionDeque.addAll(getSubElementActions(subSection != null ? subSection : actionSection, slot, clickType));
			
			if(subSection == null)
			{
				//We can safely assume all elements are on this level and are loaded using the above method.
				break;
			}
		}
		
		return elementActionDeque;
	}
	
	/**
	 * Loads individual ElementActions.
	 * This method is provided with the ConfigurationSection on the same level as the defined actions.
	 *
	 * @param subSection
	 * @param slot
	 * @param clickType
	 * @return
	 */
	private Deque<ElementAction> getSubElementActions(ConfigurationSection subSection, int slot, ClickType clickType)
	{
		Deque<ElementAction> elementActionDeque = new ArrayDeque<>();
		
		String elementActionOrder = subSection.getString("actions", "");
		//Split by comma and any number of spaces
		for(String actionName : elementActionOrder.split(",[ ]*"))
		{
			//Wrap the action name in an Identifier
			AddonIdentifier actionIdentifier = new StringAddonIdentifier(actionName);
			
			SkyAddon elementActionClass = registeredAddons.get(actionIdentifier);
			if(elementActionClass == null)
			{
				if(actionName == null)
				{
					BUtil.logInfo("Slot '" + slot + "' in " + subSection.getCurrentPath() + " contains an actions section, but no defined actions!");
					continue;
				}
				
				BUtil.logInfo("Slot '" + slot + "' in " + subSection.getCurrentPath() + " uses an un-recognised action: '" + actionName + "'");
				continue;
			}
			else if(!(elementActionClass instanceof ClassAddon))
			{
				BUtil.logInfo("Slot '" + slot + "' in " + subSection.getCurrentPath() + " attempted to load a non-class GUI Action: '" + actionName + "'");
				continue;
			}
			
			ConfigurationSection actionConfiguration = subSection.getConfigurationSection(actionName);
			//TODO: Include, but only for actions that REQUIRE configuration
			/*if(actionConfiguration == null || actionConfiguration.getKeys(false).isEmpty())
			{
				BUtil.logInfo("Slot '" + slot + "' in " + subSection.getCurrentPath() + " is missing configuration for: '" + actionName + "'");
			}*/
			
			try
			{
				//Initialize and begin loading this ElementAction
				ElementAction elementAction = (ElementAction) (Class.forName(((ClassAddon) elementActionClass).getFullyQualifiedName())).newInstance();
				elementAction.setClickType(clickType);
				elementActionDeque.add(elementAction.loadAction(actionConfiguration));
			}
			catch(IllegalArgumentException e)
			{
				BUtil.logError("Action '" + actionName + "' Failed loading at '" + subSection.getCurrentPath() + ". Please correct the configuration for this action.");
				BUtil.logStackTrace(e);
			}
			catch(InstantiationException | IllegalAccessException | ClassNotFoundException e)
			{
				BUtil.logError("Action '" + actionName + "' Failed loading at '" + subSection.getCurrentPath() + ". INTERNAL ERROR.");
				BUtil.logStackTrace(e);
			}
		}
		
		return elementActionDeque;
	}

}

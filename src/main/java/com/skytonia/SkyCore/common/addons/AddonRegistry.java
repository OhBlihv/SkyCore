package com.skytonia.SkyCore.common.addons;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.common.addons.identifiers.AddonIdentifier;
import com.skytonia.SkyCore.util.BUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Chris Brown (OhBlihv) on 26/09/2016.
 */
public abstract class AddonRegistry implements Listener
{
	
	protected Map<AddonIdentifier, SkyAddon> registeredAddons = new HashMap<>();
	
	public AddonRegistry()
	{
		//Register our plugin disable listener
		Bukkit.getPluginManager().registerEvents(this, SkyCore.getInstance());
	}
	
	/**
	 * Contains references of classes and the plugin that registered them
	 */
	protected Map<String, Set<AddonIdentifier>> pluginRegistry = new HashMap<>();
	
	public void registerAddon(AddonIdentifier addonIdentifier, SkyAddon skyAddon) throws IllegalArgumentException
	{
		if(registeredAddons.containsKey(addonIdentifier))
		{
			throw new IllegalArgumentException("Class '" + skyAddon.getClass().getSimpleName() + "' already registered!" +
				                                   " (Is '" + registeredAddons.get(addonIdentifier).getAddon().getClass().getSimpleName() + "' the class you are registering?)");
		}
		
		addRegisteredClassToPlugin(BUtil.getCallingPlugin(), addonIdentifier);
		registeredAddons.put(addonIdentifier, skyAddon);
		
		BUtil.logMessageAsPlugin("SkyCore", "Registered " + getAssociatedAddonClass().getSimpleName() + " of type " + skyAddon.getName());
	}
	
	private void addRegisteredClassToPlugin(String plugin, AddonIdentifier addonIdentifier)
	{
		Set<AddonIdentifier> registeredActions = pluginRegistry.get(plugin);
		if(registeredActions == null)
		{
			registeredActions = new HashSet<>();
		}
		
		registeredActions.add(addonIdentifier);
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent event)
	{
		//TODO: If a plugin unloads with the same name as one in our list, find all registered actions and unload them
		
		Set<AddonIdentifier> pluginRegisteredActions = pluginRegistry.remove(event.getPlugin().getName());
		if(pluginRegisteredActions != null)
		{
			for(AddonIdentifier addonIdentifier : pluginRegisteredActions)
			{
				SkyAddon registeredAddon = registeredAddons.remove(addonIdentifier);
				
				//TODO: Search all registered GUIs for this action and attempt to remove it to avoid 'zombie' classes
				unloadAddon(registeredAddon);
			}
		}
	}
	
	private void unloadAddon(SkyAddon registeredAddon)
	{
		cleanupAddon(registeredAddon);
	}
	
	protected abstract void cleanupAddon(SkyAddon skyAddon);
	
	protected abstract Class<?> getAssociatedAddonClass();
	
}

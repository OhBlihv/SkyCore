package com.skytonia.SkyCore.gui.variables;

import com.skytonia.SkyCore.common.addons.AddonRegistry;
import com.skytonia.SkyCore.common.addons.ObjectAddon;
import com.skytonia.SkyCore.common.addons.SkyAddon;
import com.skytonia.SkyCore.common.addons.identifiers.AddonIdentifier;
import com.skytonia.SkyCore.common.addons.identifiers.IntegerAddonIdentifier;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Chris Brown (OhBlihv) on 26/09/2016.
 */
public class GUIVariables extends AddonRegistry
{
	
	//TODO: Change AddonRegistry to accept Objects in the map instead of just Classes to ensure
	//TODO: We can store full GUIVariable objects for easy insertion in GUIContainers
	
	private static GUIVariables instance = null;
	public static GUIVariables getInstance()
	{
		if(instance == null)
		{
			instance = new GUIVariables();
		}
		return instance;
	}
	
	private int nextId = 0;
	private int getNextId()
	{
		return nextId++;
	}
	
	public GUIVariables() throws IllegalArgumentException
	{
		//Trigger loading of all GUIVariables included in this plugin;
		registerAddon(new PlayerNameVariable());
	}
	
	public void registerAddon(GUIVariable guiVariable) throws IllegalArgumentException
	{
		registerAddon(new IntegerAddonIdentifier(getNextId()), new ObjectAddon(guiVariable));
	}
	
	/*
	 * This class RELIES on the fact that all addons stored are of ObjectAddon type.
	 * Ensure this is the case through this method.
	 */
	@Override
	public void registerAddon(AddonIdentifier addonIdentifier, SkyAddon skyAddon) throws IllegalArgumentException
	{
		//TODO: Ensure this holds.
		if(!(skyAddon instanceof ObjectAddon)/* || !skyAddon.getAddon().getClass().isAssignableFrom(GUIVariable.class)*/)
		{
			//BUtil.logInfo("1: " + !(skyAddon instanceof ObjectAddon));
			//BUtil.logInfo("2: " + !skyAddon.getAddon().getClass().isAssignableFrom(GUIVariable.class) + " | " + skyAddon.getAddon().getClass().getSimpleName());
			
			throw new IllegalArgumentException("Class '" + skyAddon.getAddon().getClass().getSimpleName() + "' attempted to be registered, but was not an extending class of GUIVariable.");
		}
		
		super.registerAddon(addonIdentifier, skyAddon);
	}
	
	@Override
	protected void cleanupAddon(SkyAddon skyAddon)
	{
		
	}
	
	protected Class<?> getAssociatedAddonClass()
	{
		return GUIVariable.class;
	}
	
	public Set<GUIVariable> getAllRegisteredVariables()
	{
		Set<GUIVariable> allVariables = new HashSet<>();
		
		for(SkyAddon skyAddon : registeredAddons.values())
		{
			//Assumed that all 'SkyAddons' in the registeredAddons map are of type GUIVariable or an extension of
			//based on the restriction given in #registerAddon
			allVariables.add((GUIVariable) skyAddon.getAddon());
		}
		
		return allVariables;
	}
	
}

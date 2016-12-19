package com.skytonia.SkyCore.gui.actions;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 *
 * This class REQUIRED a blank constructor, since construction is handled
 * by a blank constructor through reflection, and all loading is
 * completed through the abstracted 'loadAction' method below.
 */
@NoArgsConstructor
public abstract class ElementAction
{
	
	/*
	 * If an action includes a click type, this action will only be performed when that click is provided.
	 * Actions without click types are executed regardless of the click type.
	 */
	@Setter
	public ClickType clickType = ClickType.UNKNOWN;
	
	public abstract boolean onClick(Player player, ClickType clickType, int slot);
	
	public boolean hasClickType()
	{
		return clickType != ClickType.UNKNOWN;
	}
	
	public abstract ElementAction loadAction(ConfigurationSection configurationSection) throws IllegalArgumentException;
	
}

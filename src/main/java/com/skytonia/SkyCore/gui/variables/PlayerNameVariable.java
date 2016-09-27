package com.skytonia.SkyCore.gui.variables;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public class PlayerNameVariable extends GUIVariable
{
	
	@Override
	public GUIVariable loadGUIVariable(Object... objects)
	{
		this.variable = "{player}";
		
		return this;
	}
	
	@Override
	public List<String> doReplacement(List<String> line, Player player)
	{
		return doReplacement(line, player, player.getName());
	}
	
}

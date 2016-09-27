package com.skytonia.SkyCore.gui.variables;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public class PermissionVariable extends GUIVariable
{
	
	private static final Pattern PERMISSION_SEPARATOR = Pattern.compile("-");
	
	@Getter
	private Map<String, List<String>> replacementMap;
	
	@Override
	public GUIVariable loadGUIVariable(Object... objects)
	{
		this.variable = (String) objects[0];
		this.replacementMap = (Map<String, List<String>>) objects[1];
		
		return this;
	}
	
	@Override
	public List<String> doReplacement(List<String> line, Player player)
	{
		for(Map.Entry<String, List<String>> entry : replacementMap.entrySet())
		{
			if(player.hasPermission(PERMISSION_SEPARATOR.matcher(entry.getKey()).replaceAll(".")))
			{
				return super.doReplacement(line, player);
			}
		}
		return line;
	}
	
}
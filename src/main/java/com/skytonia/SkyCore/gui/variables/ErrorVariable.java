package com.skytonia.SkyCore.gui.variables;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public class ErrorVariable extends GUIVariable
{
	
	private static final List<String> nullReplacement = new ArrayList<>();
	static
	{
		nullReplacement.add(null);
	}
	
	@Override
	public GUIVariable loadGUIVariable(Object... objects)
	{
		this.variable = (String) objects[0];
		
		return this;
	}
}
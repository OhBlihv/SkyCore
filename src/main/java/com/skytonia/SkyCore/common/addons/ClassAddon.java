package com.skytonia.SkyCore.common.addons;

import lombok.RequiredArgsConstructor;

/**
 * Created by Chris Brown (OhBlihv) on 26/09/2016.
 */
@RequiredArgsConstructor
public class ClassAddon implements SkyAddon
{
	
	private final Class<?> addonClass;
	
	@Override
	public Object getAddon()
	{
		return addonClass;
	}
	
	@Override
	public String getName()
	{
		String[] fullyQualifiedName = getFullyQualifiedName().split("[.]");
		
		//Return the Simple Class Name only.
		return fullyQualifiedName[fullyQualifiedName.length - 1];
	}
	
	public String getFullyQualifiedName()
	{
		return addonClass.getTypeName();
	}
	
	
}

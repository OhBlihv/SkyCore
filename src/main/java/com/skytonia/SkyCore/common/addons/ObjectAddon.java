package com.skytonia.SkyCore.common.addons;

import lombok.RequiredArgsConstructor;

/**
 * Created by Chris Brown (OhBlihv) on 26/09/2016.
 */
@RequiredArgsConstructor
public class ObjectAddon implements SkyAddon
{
	
	private final Object addonObject;
	
	@Override
	public Object getAddon()
	{
		return addonObject;
	}
	
	@Override
	public String getName()
	{
		return addonObject.getClass().getSimpleName();
	}
	
}

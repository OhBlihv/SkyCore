package com.skytonia.SkyCore.common.addons.identifiers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Chris Brown (OhBlihv) on 26/09/2016.
 */
@RequiredArgsConstructor
public class StringAddonIdentifier implements AddonIdentifier
{
	
	@Getter
	private final String addonName;
	
	@Override
	public int hashCode()
	{
		return addonName.hashCode();
	}
	
	@Override
	public boolean equals(Object object)
	{
		return object instanceof StringAddonIdentifier && ((StringAddonIdentifier) object).addonName.equals(this.addonName);
	}
	
}

package com.skytonia.SkyCore.common.addons.identifiers;

/**
 * Created by Chris Brown (OhBlihv) on 26/09/2016.
 */
public class IntegerAddonIdentifier implements AddonIdentifier
{
	
	//Store this integer as an Object for easy comparisons
	private final Integer value;
	
	public IntegerAddonIdentifier(int value)
	{
		this.value = value;
	}
	
	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
	
	@Override
	public boolean equals(Object object)
	{
		return object instanceof IntegerAddonIdentifier && ((IntegerAddonIdentifier) object).value.equals(this.value);
	}
	
}

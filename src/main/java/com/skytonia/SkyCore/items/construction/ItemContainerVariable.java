package com.skytonia.SkyCore.items.construction;

import lombok.Getter;

/**
 * Created by Chris Brown (OhBlihv) on 26/09/2016.
 */
public enum ItemContainerVariable
{
	
	MATERIAL,
	DAMAGE(true),
	AMOUNT(true),
	DISPLAYNAME,
	LORE,
	ENCHANTMENTS,
	ENCHANTED,
	OWNER,
	SKULL_TEXTURE;
	
	@Getter
	private final boolean isNumber;
	
	ItemContainerVariable()
	{
		this(false);
	}
	
	ItemContainerVariable(boolean isNumber)
	{
		this.isNumber = isNumber;
	}
	
}

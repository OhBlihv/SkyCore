package com.skytonia.SkyCore.materials;

import lombok.Getter;

/**
 * Created by Chris Brown (OhBlihv) on 5/24/2017.
 */
public enum WoolColours
{

	WHITE(0),
	ORANGE(1),
	MAGENTA(2),
	LIGHT_BLUE(3),
	YELLOW(4),
	LIME(5),
	PINK(6),
	DARK_GREY(7),
	LIGHT_GREY(8),
	CYAN(9),
	PURPLE(10),
	BLUE(11),
	BROWN(12),
	GREEN(13),
	RED(14),
	BLACK(15);
	
	@Getter
	private final int colourId;
	
	WoolColours(int colourId)
	{
		this.colourId = colourId;
	}
	
	public static WoolColours getColourForId(int colourId) throws IllegalArgumentException
	{
		if(colourId < 0 || colourId > 15)
		{
			throw new IllegalArgumentException("Colour outside visible range");
		}
		
		for(WoolColours woolColour : values())
		{
			if(woolColour.colourId == colourId)
			{
				return woolColour;
			}
		}
		
		throw new IllegalArgumentException("Colour " + colourId + " not found");
	}

}

package com.skytonia.SkyCore.titles;

import lombok.Getter;

/**
 * Created by Chris Brown (OhBlihv) on 4/21/2017.
 */
public enum SpacerType
{
	
	NONE(0D),
	HALF(0.5D),
	FULL(1D);
	
	@Getter
	final double height;
	
	SpacerType(double height)
	{
		this.height = height;
	}
	
}

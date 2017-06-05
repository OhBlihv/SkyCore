package com.skytonia.SkyCore.servers.handlers.processing;

import lombok.Getter;

/**
 * Created by Chris Brown (OhBlihv) on 5/25/2017.
 */
public enum CommunicationDirection
{
	
	INBOUND("=>()"),
	OUTBOUND("<=()");
	
	@Getter
	final String directionString;
	
	CommunicationDirection(String directionString)
	{
		this.directionString = directionString;
	}
	
}

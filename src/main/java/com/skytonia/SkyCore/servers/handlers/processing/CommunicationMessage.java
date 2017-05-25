package com.skytonia.SkyCore.servers.handlers.processing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Chris Brown (OhBlihv) on 5/25/2017.
 */
@RequiredArgsConstructor
public abstract class CommunicationMessage
{
	
	@Getter
	private final String server;
	
	@Getter
	private final String channel;
	
	@Getter
	private final CommunicationDirection direction;
	
	@Override
	public String toString()
	{
		return "Message => {<" + direction + ">'" + server + "','" + channel + "','" + direction.directionString + "',";
	}
	
}
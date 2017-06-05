package com.skytonia.SkyCore.servers.handlers.processing;

import lombok.Getter;

/**
 * Created by Chris Brown (OhBlihv) on 5/25/2017.
 */
public class OutboundCommunicationMessage extends CommunicationMessage
{
	
	@Getter
	private final String message;
	
	public OutboundCommunicationMessage(String server, String channel, String message)
	{
		super(server, channel, CommunicationDirection.OUTBOUND);
		
		this.message = message;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "'<>" + message + "<>'}";
	}
	
}

package com.skytonia.SkyCore.servers.handlers.processing;

import com.skytonia.SkyCore.servers.util.MessageUtil;
import lombok.Getter;

import java.util.Arrays;

/**
 * Created by Chris Brown (OhBlihv) on 5/25/2017.
 */
public class InboundCommunicationMessage extends CommunicationMessage
{
	
	@Getter
	private final String[] messageArgs;
	
	public InboundCommunicationMessage(String server, String channel, String message)
	{
		super(server, channel, CommunicationDirection.INBOUND);
		
		this.messageArgs = MessageUtil.splitArguments(message);
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "'<>" + Arrays.toString(messageArgs) + "<>'}";
	}
	
}

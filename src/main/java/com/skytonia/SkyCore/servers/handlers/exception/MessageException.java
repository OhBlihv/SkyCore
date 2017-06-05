package com.skytonia.SkyCore.servers.handlers.exception;

import com.skytonia.SkyCore.servers.handlers.processing.CommunicationMessage;
import lombok.Getter;

/**
 * Created by Chris Brown (OhBlihv) on 5/25/2017.
 */
public class MessageException extends Exception
{
	
	@Getter
	private final CommunicationMessage communicationMessage;
	
	public MessageException(CommunicationMessage communicationMessage, Throwable parent)
	{
		super(parent);
		
		this.communicationMessage = communicationMessage;
	}
	
}

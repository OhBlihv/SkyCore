package com.skytonia.SkyCore.servers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 5/25/2017.
 */
@RequiredArgsConstructor
public class MovementInfo
{
	
	@Getter
	private final Player player;
	
	@Getter
	private final String targetServer;
	
	@Getter
	@Setter
	private String responseMessage;
	
	private final MovementAction movementAction;
	
	@Getter
	private long initialTimestamp = System.currentTimeMillis();
	
	public boolean hasTimedOut()
	{
		//Ignore times below 0, which have been disabled.
		if(initialTimestamp < 0)
		{
			return false;
		}
		
		return System.currentTimeMillis() > initialTimestamp + 5000L;
	}
	
	public void cancelTimeout()
	{
		initialTimestamp = -1;
	}
	
	public void sendPlayer()
	{
		cancelTimeout();
		movementAction.onSend(player, targetServer);
	}
	
	public void failPlayer()
	{
		cancelTimeout();
		movementAction.onFail(player, targetServer, responseMessage);
	}
	
}

package com.skytonia.SkyCore.movement.handlers;

import com.skytonia.SkyCore.movement.MovementManager;
import com.skytonia.SkyCore.redis.RedisManager;

/**
 * Created by Chris Brown (OhBlihv) on 4/28/2017.
 */
public class RedisMovementHandler implements MovementHandler
{
	
	private static final String SPLITTER = "|";
	
	@Override
	public void sendPlayerTo(String player, String server)
	{
		RedisManager.sendMessage(server, MovementManager.CHANNEL_MOVE_PLAYER_REQ, RedisManager.getServerName() + SPLITTER + player);
	}
}

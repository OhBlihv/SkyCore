package com.skytonia.SkyCore.movement.handlers;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.util.BUtil;
import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.request.RequestException;
import lilypad.client.connect.api.request.impl.RedirectRequest;

/**
 * Created by Chris Brown (OhBlihv) on 4/28/2017.
 */
public class LilypadMovementHandler implements MovementHandler
{
	
	private Connect connect = null;
	
	@Override
	public void sendPlayerTo(String player, String server)
	{
		if(connect == null)
		{
			connect = SkyCore.getPluginInstance().getServer().getServicesManager().getRegistration(Connect.class).getProvider();
		}
		
		try
		{
			connect.request(new RedirectRequest(server, player));
		}
		catch(RequestException e)
		{
			BUtil.logInfo("Unable to transfer player " + player + " to " + server);
			e.printStackTrace();
		}
	}
	
}

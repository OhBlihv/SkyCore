package com.skytonia.SkyCore.servers;

import com.skytonia.SkyCore.util.BUtil;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 5/24/2017.
 */
public class MovementAction
{
	
	public void onSend(Player player, String serverName)
	{
		//
	}
	
	public void onFail(Player player, String serverName, String response)
	{
		String formattedServer = BUtil.capitaliseAllFirst(serverName.replace("_", " ").replace("-", " "));
		
		String formattedResponse = response;
		
		if(response.isEmpty())
		{
			formattedResponse = "§c§l(!) §cAn unexpected error occurred while moving you to " + formattedServer;
		}
		else
		{
			switch(response)
			{
				case "WHITELIST":   formattedResponse = "§c§l(!) §cYou do not have access to " + formattedServer + "!"; break;
				case "BANNED":      formattedResponse = "§c§l(!) §cYou are banned from " + formattedServer + "!"; break;
				case "TIMEOUT":     formattedResponse = "§c§l(!) §cFailed to connect to " + formattedServer + "..."; break;
				case "OFFLINE":     formattedResponse = "§c§l(!) §c" + formattedServer + " is currently offline."; break;
				case "DONATOR":     formattedResponse = "§c§l(!) §c" + formattedServer + " requires a donator rank to join."; break;
				case "VIP_JOIN":    formattedResponse = "§c§l(!) §c" + formattedServer + " is currently FULL! Purchase a donator rank to join."; break;
				case "FULL":        formattedResponse = "§c§l(!) §c" + formattedServer + " is currently full."; break;
				case "REBOOTING":   formattedResponse = "§c§l(!) §c" + formattedServer + " is currently rebooting."; break;
				case "LOCAL_SERVER":formattedResponse = "§c§l(!) §cYou are already connected to " + formattedServer + "."; break;
			}
		}
		
		player.sendMessage(formattedResponse);
	}
	
}

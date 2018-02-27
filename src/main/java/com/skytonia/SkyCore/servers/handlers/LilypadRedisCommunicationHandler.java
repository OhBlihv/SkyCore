package com.skytonia.SkyCore.servers.handlers;

import com.skytonia.SkyCore.servers.events.PlayerChangeServerEvent;
import com.skytonia.SkyCore.servers.handlers.exception.MessageException;
import com.skytonia.SkyCore.servers.handlers.processing.OutboundCommunicationMessage;
import com.skytonia.SkyCore.servers.util.MessageUtil;
import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.request.RequestException;
import lilypad.client.connect.api.request.impl.RedirectRequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Chris Brown (OhBlihv) on 5/25/2017.
 */
public class LilypadRedisCommunicationHandler extends RedisCommunicationHandler
{
	
	private final Connect lilypad;
	
	public LilypadRedisCommunicationHandler()
	{
		super();
		
		lilypad = Bukkit.getServer().getServicesManager().getRegistration(Connect.class).getProvider();
		//lilypad.registerEvents(this);
		
		currentServer = lilypad.getSettings().getUsername();
	}
	
	@Override
	public void transferPlayer(Player player, String serverName)
	{
		try
		{
			PlayerChangeServerEvent event = new PlayerChangeServerEvent(player, serverName);
			Bukkit.getPluginManager().callEvent(event);

			//Alert the other server of an incoming player
			sendMessage(new OutboundCommunicationMessage(
				serverName, CHANNEL_MOVE_FORCE, MessageUtil.mergeArguments(player.getName(), player.getUniqueId().toString())
			));

			lilypad.request(new RedirectRequest(serverName, player.getName()));
		}
		catch(RequestException | MessageException e)
		{
			e.printStackTrace();
		}
	}
	
}

package com.skytonia.SkyCore.servers.handlers;

import com.skytonia.SkyCore.movement.events.PlayerEnterServerEvent;
import com.skytonia.SkyCore.movement.events.PlayerServerChangeRequestEvent;
import com.skytonia.SkyCore.servers.MovementAction;
import com.skytonia.SkyCore.servers.ServerInfo;
import com.skytonia.SkyCore.servers.ServerStatus;
import com.skytonia.SkyCore.servers.handlers.exception.MessageException;
import com.skytonia.SkyCore.servers.handlers.processing.AbstractCommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.processing.InboundCommunicationMessage;
import com.skytonia.SkyCore.servers.handlers.processing.OutboundCommunicationMessage;
import com.skytonia.SkyCore.servers.util.MessageUtil;
import com.skytonia.SkyCore.util.BUtil;
import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.event.MessageEvent;
import lilypad.client.connect.api.request.RequestException;
import lilypad.client.connect.api.request.impl.MessageRequest;
import lilypad.client.connect.api.request.impl.RedirectRequest;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 5/24/2017.
 */
public class LilypadCommunicationHandler extends AbstractCommunicationHandler implements CommunicationHandler, Listener
{
	
	private static final String CHANNEL_MOVE_REQ  = "SC_MoveReq",
								CHANNEL_MOVE_REPL = "SC_MoveRep";
	
	private final Connect lilypad;
	
	public LilypadCommunicationHandler()
	{
		lilypad = Bukkit.getServer().getServicesManager().getRegistration(Connect.class).getProvider();
		lilypad.registerEvents(this);
		
		currentServer = lilypad.getSettings().getUsername();
	}
	
	@Override
	public int getPlayerCount(String serverName)
	{
		return 0;
	}
	
	@Override
	public void requestPlayerTransfer(Player player, String serverName)
	{
		requestPlayerTransfer(player, serverName, null);
	}
	
	@Override
	public void requestPlayerTransfer(Player player, String serverName, MovementAction movementAction)
	{
		super.requestPlayerTransfer(player, serverName, movementAction);
		
		try
		{
			lilypad.request(new MessageRequest(serverName, CHANNEL_MOVE_REQ, player.getName()));
		}
		catch(RequestException | UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void transferPlayer(Player player, String serverName)
	{
		try
		{
			lilypad.request(new RedirectRequest(serverName, player.getName()));
		}
		catch(RequestException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public String getOnlineHub()
	{
		return null;
	}
	
	@Override
	public List<String> getServersMatching(String searchPhrase)
	{
		return null;
	}
	
	@Override
	public ServerInfo getServer(String serverName)
	{
		return null;
	}

	public void sendMessage(OutboundCommunicationMessage message) throws MessageException
	{
		if(!lilypad.isConnected() || lilypad.isClosed())
		{
			throw new IllegalArgumentException("Lilypad Inaccessible. (Offline?)");
		}
		
		try
		{
			lilypad.request(new MessageRequest(message.getServer(), message.getChannel(), message.getMessage()));
		}
		catch(RequestException | UnsupportedEncodingException e)
		{
			throw new MessageException(message, e);
		}
	}
	
	@Override
	public void receiveMessage(InboundCommunicationMessage message) throws MessageException
	{
		switch(message.getChannel())
		{
			case CHANNEL_MOVE_REQ:
			{
				String serverName = message.getMessageArgs()[0],
					   playerName = message.getMessageArgs()[1];
				
				//No response is success.
				String response = "";
				
				if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers())
				{
					response = "FULL";
				}
				else if(Bukkit.hasWhitelist())
				{
					OfflinePlayer offlinePlayer = null;
					try
					{
						offlinePlayer = Bukkit.getOfflinePlayer(playerName);
						if(offlinePlayer == null)
						{
							throw new IllegalArgumentException();
						}
					}
					catch(Exception e)
					{
						//
					}
					
					if(offlinePlayer == null || !Bukkit.getWhitelistedPlayers().contains(offlinePlayer))
					{
						response = ServerStatus.WHITELIST.name();
					}
				}
				
				PlayerServerChangeRequestEvent requestEvent = new PlayerServerChangeRequestEvent(playerName, response, !response.isEmpty());
				Bukkit.getPluginManager().callEvent(requestEvent);
				if(requestEvent.isCancelled())
				{
					response = requestEvent.getCancelReason();
				}
				else
				{
					response = ""; //Reset the response
				}
				
				sendMessage(new OutboundCommunicationMessage(
					serverName, CHANNEL_MOVE_REPL, MessageUtil.mergeArguments(serverName, playerName, response)
				));
				
				if(!requestEvent.isCancelled())
				{
					//Ensure the player can join once their request has been accepted
					incomingPlayers.add(playerName);
					
					Bukkit.getPluginManager().callEvent(new PlayerEnterServerEvent(playerName));
				}
				
				break;
			}
			case CHANNEL_MOVE_REPL:
			{
				String targetServer = message.getMessageArgs()[0],
					   playerName   = message.getMessageArgs()[1],
				       response     = "";
					   
				OfflinePlayer offlinePlayer = null;
				try
				{
					offlinePlayer = Bukkit.getOfflinePlayer(playerName);
					if(offlinePlayer == null || !offlinePlayer.isOnline())
					{
						offlinePlayer = null;
					}
				}
				catch(Exception e)
				{
					//
				}
				
				if(offlinePlayer == null)
				{
					BUtil.log("Player " + playerName + " was not online and could not be moved to " + targetServer);
					return;
				}
				
				if(message.getMessageArgs().length >= 3)
				{
					response = message.getMessageArgs()[2];
				}
				
				if(response.isEmpty())
				{
					BUtil.log("Received successful reply for " + playerName + "'s transfer to " + targetServer);
					setPlayerMovementStatusSuccess(playerName);
					
					transferPlayer(offlinePlayer.getPlayer(), targetServer);
				}
				else
				{
					BUtil.log("Received unsuccessful reply for " + playerName + "'s transfer to " + targetServer);
					setPlayerMovementStatusFailure(playerName);
				}
				
				break;
			}
		}
	}
	
	@EventHandler
	public void onMessage(MessageEvent event)
	{
		try
		{
			addIncomingMessage(event.getSender(), event.getChannel(), event.getMessageAsString());
		}
		catch(Exception e)
		{
			BUtil.log("Unable to receive incoming message from '" + event.getSender() + "' on channel " + event.getChannel() + " => '" +
				          Arrays.toString(event.getMessage()) + "'");
			e.printStackTrace();
		}
	}
	
}

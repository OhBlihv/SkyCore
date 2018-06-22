package com.skytonia.SkyCore.servers.handlers;

import com.skytonia.SkyCore.servers.MovementAction;
import com.skytonia.SkyCore.servers.ServerInfo;
import com.skytonia.SkyCore.servers.handlers.exception.MessageException;
import com.skytonia.SkyCore.servers.handlers.processing.InboundCommunicationMessage;
import com.skytonia.SkyCore.servers.handlers.processing.OutboundCommunicationMessage;
import com.skytonia.SkyCore.servers.listeners.ChannelSubscription;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 6/10/2017.
 */
public class NullCommunicationHandler implements CommunicationHandler
{

	@Override
	public void registerChannels()
	{

	}

	@Override
	public void requestPlayerTransfer(Player player, String serverName)
	{
		//
	}
	
	@Override
	public void requestPlayerTransfer(Player player, String serverName, MovementAction movementAction)
	{
		//
	}
	
	@Override
	public void transferPlayer(Player player, String serverName)
	{
		//
	}

	@Override
	public Pair<String, ServerInfo> getPlayerServer(String playerName)
	{
		return null;
	}

	@Override
	public String getCurrentServer()
	{
		return Bukkit.getServerName();
	}
	
	@Override
	public int getPlayerCount(String serverName)
	{
		return Bukkit.getOnlinePlayers().size();
	}
	
	@Override
	public String getOnlineHub()
	{
		return "hub";
	}
	
	@Override
	public Collection<Map.Entry<String, ServerInfo>> getAllServers()
	{
		return Collections.emptyList();
	}
	
	@Override
	public List<String> getServersMatching(String... searchPhrases)
	{
		return Collections.emptyList();
	}
	
	@Override
	public List<String> getAvailableServersMatching(String... searchPhrases)
	{
		return Collections.emptyList();
	}
	
	@Override
	public ServerInfo getServer(String serverName)
	{
		return new ServerInfo();
	}

	@Override
	public String getFormattedServerName(String serverName)
	{
		return serverName;
	}

	@Override
	public String getFormattedCurrentServer()
	{
		return getCurrentServer();
	}

	@Override
	public void receiveMessage(InboundCommunicationMessage message) throws MessageException
	{
		//
	}
	
	@Override
	public void sendMessage(OutboundCommunicationMessage message) throws MessageException
	{
		//
	}
	
	@Override
	public void addIncomingMessage(String sendingServer, String channel, String message)
	{
		//
	}
	
	@Override
	public void addOutgoingMessage(String targetServer, String channel, String message)
	{
		//
	}
	
	@Override
	public void registerSubscription(ChannelSubscription subscriber, String... channels)
	{
		//
	}
	
	@Override
	public void registerSubscription(ChannelSubscription subscriber, boolean prefixWithServerName, String... channels)
	{
		//
	}
	
}

package com.skytonia.SkyCore.servers.handlers;

import com.skytonia.SkyCore.servers.MovementAction;
import com.skytonia.SkyCore.servers.ServerInfo;
import com.skytonia.SkyCore.servers.handlers.exception.MessageException;
import com.skytonia.SkyCore.servers.handlers.processing.InboundCommunicationMessage;
import com.skytonia.SkyCore.servers.handlers.processing.OutboundCommunicationMessage;
import com.skytonia.SkyCore.servers.listeners.ChannelSubscription;
import javafx.util.Pair;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 5/24/2017.
 */
public interface CommunicationHandler
{

	/*
	 * Setup
	 */

	void registerChannels();
	
	/*
	 * Player Movement
	 */
	
	/**
	 *
	 * @param player
	 * @param serverName
	 */
	void requestPlayerTransfer(Player player, String serverName);
	
	/**
	 *
	 * @param player
	 * @param serverName
	 * @param movementAction
	 */
	void requestPlayerTransfer(Player player, String serverName, MovementAction movementAction);
	
	/**
	 *
	 * @param player
	 * @param serverName
	 */
	void transferPlayer(Player player, String serverName);
	
	/*
	 * Server Management
	 */

	Pair<String, ServerInfo> getPlayerServer(String playerName);
	
	/**
	 *
	 * @return
	 */
	String getCurrentServer();
	
	/**
	 *
	 * @param serverName
	 * @return
	 */
	int getPlayerCount(String serverName);
	
	/**
	 *
	 * @return
	 */
	String getOnlineHub();
	
	/**
	 *
	 * @return
	 */
	Collection<Map.Entry<String, ServerInfo>> getAllServers();
	
	/**
	 *
	 * @param searchPhrases
	 * @return
	 */
	List<String> getServersMatching(String... searchPhrases);
	
	/**
	 *
	 * @param searchPhrases
	 * @return
	 */
	List<String> getAvailableServersMatching(String... searchPhrases);
	
	/**
	 *
	 * @param serverName
	 * @return
	 */
	ServerInfo getServer(String serverName);

	/**
	 *
	 * @param serverName
	 * @return
	 */
	String getFormattedServerName(String serverName);

	String getFormattedCurrentServer();

	/*
	 * Messaging
	 */
	
	/**
	 *
	 * @param message
	 * @throws MessageException
	 */
	void receiveMessage(InboundCommunicationMessage message) throws MessageException;
	
	/**
	 *
	 * @param message
	 * @throws MessageException
	 */
	void sendMessage(OutboundCommunicationMessage message) throws MessageException;
	
	/**
	 *
	 * @param sendingServer
	 * @param channel
	 * @param message
	 */
	void addIncomingMessage(String sendingServer, String channel, String message);
	
	/**
	 *
	 * @param sendingServer
	 * @param channel
	 * @param message
	 */
	void addOutgoingMessage(String targetServer, String channel, String message);
	
	/**
	 *
	 * @param subscriber
	 * @param channels
	 */
	void registerSubscription(ChannelSubscription subscriber, String... channels);
	
	/**
	 *
	 * @param subscriber
	 * @param prefixWithServerName
	 * @param channels
	 */
	void registerSubscription(ChannelSubscription subscriber, boolean prefixWithServerName, String... channels);
	
}

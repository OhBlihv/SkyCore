package com.skytonia.SkyCore.servers.handlers;

import com.skytonia.SkyCore.servers.MovementAction;
import com.skytonia.SkyCore.servers.ServerInfo;
import com.skytonia.SkyCore.servers.handlers.exception.MessageException;
import com.skytonia.SkyCore.servers.handlers.processing.InboundCommunicationMessage;
import com.skytonia.SkyCore.servers.handlers.processing.OutboundCommunicationMessage;
import com.skytonia.SkyCore.servers.listeners.ChannelSubscription;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Chris Brown (OhBlihv) on 5/24/2017.
 */
public interface CommunicationHandler
{
	
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

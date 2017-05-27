package com.skytonia.SkyCore.servers.handlers;

import com.skytonia.SkyCore.servers.MovementAction;
import com.skytonia.SkyCore.servers.ServerInfo;
import com.skytonia.SkyCore.servers.handlers.exception.MessageException;
import com.skytonia.SkyCore.servers.handlers.processing.InboundCommunicationMessage;
import com.skytonia.SkyCore.servers.handlers.processing.OutboundCommunicationMessage;
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
	
	void requestPlayerTransfer(Player player, String serverName);
	
	void requestPlayerTransfer(Player player, String serverName, MovementAction movementAction);
	
	void transferPlayer(Player player, String serverName);
	
	/*
	 * Server Management
	 */
	
	int getPlayerCount(String serverName);
	
	String getOnlineHub();
	
	List<String> getServersMatching(String... searchPhrases);
	
	List<String> getAvailableServersMatching(String... searchPhrases);
	
	ServerInfo getServer(String serverName);
	
	/*
	 * Messaging
	 */
	
	void receiveMessage(InboundCommunicationMessage message) throws MessageException;
	
	void sendMessage(OutboundCommunicationMessage message) throws MessageException;
	
}

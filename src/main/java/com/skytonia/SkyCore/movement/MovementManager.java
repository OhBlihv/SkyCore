package com.skytonia.SkyCore.movement;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.sockets.SocketManager;
import com.skytonia.SkyCore.sockets.events.BukkitSocketJSONEvent;
import com.skytonia.SkyCore.util.BUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public class MovementManager implements Listener
{
	
	private static MovementManager instance = null;
	public static MovementManager getInstance()
	{
		if(instance == null)
		{
			instance = new MovementManager();
		}
		return instance;
	}
	
	private static final MovementAction DEFAULT_MOVEMENT_ACTION = new MovementAction()
	{
		
		@Override
		public void onFailReceive(Player player, String server, String response)
		{
			String formattedResponse = response;
			
			switch(response)
			{
				case "WHITELIST": formattedResponse = "§c" + BUtil.capitaliseFirst(server) + " is currently locked."; break;
				//TODO: Allow the banned message to be suffixed by the ban reason?
				case "BANNED": formattedResponse = "§cYou are currently banned from " + BUtil.capitaliseFirst(server) + "."; break;
				case "TIMEOUT": formattedResponse = "§cFailed to connect to " + BUtil.capitaliseFirst(server) + ". (Timeout)"; break;
			}
			
			player.sendMessage(formattedResponse);
		}
		
	};
	
	private static final String SPLITTER = "|";
	
	private static final String
			//SocketAPI (Bungee) Channels
			/*
			 *  Initial request from a Bukkit server to send a player to another server
			 *
			 *  Format: {target-server}|{player-name}
			 *  - {target-server}: Server this request attempts to reach
			 *  - {player-name}: Name of the player requesting the move
			 */
			CHANNEL_MOVE_PLAYER_REQ = "MOVE_PLAYER_REQ",
	
			/*
			 *  Response from target Bukkit server.
			 *
			 *  Format: {sending-server}|{player-name}|{response}
			 *  - {sending-server}: Server that initially requested the move
			 *  - {player-name}: Name of the player requesting the move
			 *  - {response}:   Response received from target server.
			 *                  If blank, no error occurred.
			 *                  Otherwise, a plain-text error message.
			 */
			CHANNEL_MOVE_PLAYER_REPLY = "MOVE_PLAYER_REPLY";
	
	private static final Map<String, MovementInfo> movementMap = new HashMap<>();
	private static final Set<String> incomingPlayers = new HashSet<>();
	
	protected static long timeoutDelay = 40L; //2 second timeout as default (TODO: Configurable)
	
	private MovementManager()
	{
		SocketManager.getInstance();
		
		Bukkit.getMessenger().registerOutgoingPluginChannel(SkyCore.getPluginInstance(), "BungeeCord");
		
		Bukkit.getPluginManager().registerEvents(this, SkyCore.getPluginInstance());
	}
	
	public static void requestMove(String server, Player player) throws IllegalArgumentException
	{
		requestMove(server, player, DEFAULT_MOVEMENT_ACTION);
	}
	
	/**
	 *
	 * @param server Server name to attempt transfer to
	 * @param player Player to transfer
	 * @param movementAction If a move cannot be completed, the MovementAction will be executed
	 *                       This can be used to print error messages or attempt a re-connect.
	 *
	 * @throws IllegalStateException If the player is already in an attempted transfer/move.
	 */
	public static void requestMove(String server, Player player, MovementAction movementAction) throws IllegalStateException
	{
		if(movementAction == null)
		{
			BUtil.logMessage("requestMove() called with null MovementAction!");
			movementAction = DEFAULT_MOVEMENT_ACTION;
		}
		
		BUtil.logMessage("Requesting move of " + player.getName() + " to " + server);
		movementMap.put(player.getName(), new MovementInfo(player, server, movementAction));
		
		SocketManager.getSocketClient().writeJSON(CHANNEL_MOVE_PLAYER_REQ, server + SPLITTER + player.getName());
	}
	
	protected static MovementInfo removePlayer(Player player)
	{
		MovementInfo movementInfo = movementMap.remove(player.getName());
		
		//Ensure we don't try to remove this twice.
		movementInfo.cancelTimeout();
		
		return movementInfo;
	}
	
	protected static void onSuccessfulTransfer(Player player)
	{
		removePlayer(player).processSuccess();
	}
	
	protected static void onFailTransfer(Player player, String response)
	{
		MovementInfo movementInfo = removePlayer(player);
		movementInfo.setResponse(response);
			
		movementInfo.processFailure();
	}
	
	@EventHandler
	public void onPlayerReceived(BukkitSocketJSONEvent event)
	{
		String channel = event.getChannel();
		
		if( !channel.equals(CHANNEL_MOVE_PLAYER_REQ) &&
			!channel.equals(CHANNEL_MOVE_PLAYER_REPLY))
		{
			return;
		}
		
		String[] splitData = event.getData().split("[" + SPLITTER + "]");
		
		if(event.getChannel().equals(CHANNEL_MOVE_PLAYER_REQ))
		{
			//TODO: Check whitelist, if the player is banned, and other reasons
			String  serverName = splitData[0],
					playerName = splitData[1];
			String response = ""; //Blank message for success
			if(Bukkit.hasWhitelist())
			{
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
				if(!Bukkit.getWhitelistedPlayers().contains(offlinePlayer))
				{
					response = "WHITELIST";
				}
			}
			
			//BUtil.logMessage("Responding to " + playerName + "'s request with '" + response + "'");
			event.getClient().writeJSON(CHANNEL_MOVE_PLAYER_REPLY, serverName + SPLITTER + playerName + SPLITTER + response);
			
			//Ensure the player can join once their request has been accepted
			incomingPlayers.add(playerName);
		}
		else if(event.getChannel().equals(CHANNEL_MOVE_PLAYER_REPLY))
		{
			String  targetServer = splitData[0],
					playerName   = splitData[1];
			Player player;
			{
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
				if(offlinePlayer == null || !offlinePlayer.isOnline())
				{
					BUtil.logError("Attempted to move " + playerName + " to " + targetServer + ", yet the player was not online.");
					return;
				}
				
				player = offlinePlayer.getPlayer();
			}
			
			String response = null;
			if(splitData.length >= 3)
			{
				response = splitData[2];
			}
			
			if(response != null && !response.isEmpty())
			{
				BUtil.logMessage("Received Failed Reply for " + playerName + " to " + targetServer + " (" + response + ")");
				onFailTransfer(player, response);
			}
			else
			{
				//BUtil.logMessage("Received Successful reply for " + playerName);
				onSuccessfulTransfer(player);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreJoin(AsyncPlayerPreLoginEvent event)
	{
		if(event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED && event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.KICK_BANNED)
		{
			//Attempt to get our player on!
			if(incomingPlayers.contains(event.getName()))
			{
				//BUtil.logMessage("Expected " + event.getName() + ". Allowing...");
				event.allow();
			}
			/*else
			{
				BUtil.logMessage("Did not expect " + event.getName() + ". Ignoring...");
			}*/
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		//Once the player has arrived, clear them from incoming players.
		if(incomingPlayers.contains(event.getPlayer().getName()))
		{
			incomingPlayers.remove(event.getPlayer().getName());
		}
	}
	
}

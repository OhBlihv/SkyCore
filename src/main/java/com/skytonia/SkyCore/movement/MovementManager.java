package com.skytonia.SkyCore.movement;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.movement.events.PlayerChangeServerEvent;
import com.skytonia.SkyCore.movement.events.PlayerEnterServerEvent;
import com.skytonia.SkyCore.movement.events.PlayerServerChangeRequestEvent;
import com.skytonia.SkyCore.movement.handlers.LilypadMovementHandler;
import com.skytonia.SkyCore.movement.handlers.MovementHandler;
import com.skytonia.SkyCore.movement.handlers.RedisMovementHandler;
import com.skytonia.SkyCore.redis.RedisManager;
import com.skytonia.SkyCore.redis.RedisMessage;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.SupportedVersion;
import lilypad.client.connect.api.Connect;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
@Deprecated
public class MovementManager
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
				case "WHITELIST": formattedResponse = "§c§l(!) §cYou do not have access to this server!"; break;
				//TODO: Allow the banned message to be suffixed by the ban reason?
				case "BANNED": formattedResponse = "§c§l(!) §cYou are banned from this server!"; break;
				case "TIMEOUT": formattedResponse = "§c§l(!) §cFailed to connect to... " + BUtil.capitaliseFirst(server); break;
				case "OFFLINE": formattedResponse = "§c§l(!) §cThis server is currently offline."; break;
				case "DONATOR": formattedResponse = "§c§l(!) §cThis server is reserved for Donators."; break;
				case "FULL": formattedResponse = "§c§l(!) §cThis server is currently full."; break;
			}
			
			player.sendMessage(formattedResponse);
		}
		
	};
	
	private static final String SPLITTER = "|";
	
	public static final String
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
	
	@Getter
	private static MovementHandler movementHandler;
	
	protected static long timeoutDelay = 40L; //2 second timeout as default (TODO: Configurable)
	
	private MovementManager()
	{
		HubManager.getInstance();
		
		MovementHandler tempMovementHandler = null;
		try
		{
			if(!RedisManager.getServerName().isEmpty())
			{
				SkyCore.getPluginInstance().getServer().getMessenger().registerOutgoingPluginChannel(SkyCore.getPluginInstance(), "BungeeCord");
				tempMovementHandler = new RedisMovementHandler();
				
				RedisManager.registerSubscription(this::onMessage, CHANNEL_MOVE_PLAYER_REQ, CHANNEL_MOVE_PLAYER_REPLY);
			}
		}
		catch(Exception e)
		{
			//
		}
		
		try
		{
			if(tempMovementHandler == null && SkyCore.getPluginInstance().getServer().getServicesManager().getRegistration(Connect.class).getProvider() != null)
			{
				tempMovementHandler = new LilypadMovementHandler();
			}
		}
		catch(Exception e)
		{
			//
		}
		
		if(tempMovementHandler == null)
		{
			throw new IllegalArgumentException("Unable to select movement handler. Server Movement will not work");
		}
		
		movementHandler = tempMovementHandler;
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
	public static String requestMove(String server, Player player, MovementAction movementAction) throws IllegalStateException
	{
		if(movementAction == null)
		{
			BUtil.logMessage("requestMove() called with null MovementAction!");
			movementAction = DEFAULT_MOVEMENT_ACTION;
		}
		
		if(movementMap.containsKey(player.getName()))
		{
			return null;
		}
		
		PlayerChangeServerEvent event = new PlayerChangeServerEvent(player, server);
		Bukkit.getPluginManager().callEvent(event);
		
		if(event.isCancelled())
		{
			if(event.getCancelReason() != null && !event.getCancelReason().isEmpty())
			{
				player.sendMessage(event.getCancelReason());
			}
		
			return null;
		}
		
		//If the server requested is not specific, pick one for them
		if(server.toLowerCase().equals("hub"))
		{
			server = HubManager.getInstance().getHubServer();
		}
		
		BUtil.logMessage("Requesting move of " + player.getName() + " to " + server);
		movementMap.put(player.getName(), new MovementInfo(player, server, movementAction));
		
		if(movementHandler == null)
		{
			if(SkyCore.getCurrentVersion().isAtLeast(SupportedVersion.ONE_NINE))
			{
				movementHandler = new RedisMovementHandler();
			}
			else
			{
				movementHandler = new LilypadMovementHandler();
			}
		}
		
		movementHandler.sendPlayerTo(player.getName(), server);
		
		return server;
	}
	
	protected static MovementInfo removePlayer(Player player)
	{
		MovementInfo movementInfo;
		if((movementInfo = movementMap.remove(player.getName())) == null)
		{
			return null;
		}
		
		//Ensure we don't try to remove this twice.
		movementInfo.cancelTimeout();
		
		return movementInfo;
	}
	
	protected static void onSuccessfulTransfer(Player player)
	{
		MovementInfo movementInfo = removePlayer(player);
		if(movementInfo == null)
		{
			return;
		}
		
		movementInfo.processSuccess();
	}
	
	protected static void onFailTransfer(Player player, String response)
	{
		MovementInfo movementInfo = removePlayer(player);
		if(movementInfo == null)
		{
			return;
		}
		
		movementInfo.setResponse(response);
			
		movementInfo.processFailure();
	}
	
	public void onMessage(RedisMessage message)
	{
		String channel = message.getChannel();
		if( !channel.equals(CHANNEL_MOVE_PLAYER_REQ) &&
			!channel.equals(CHANNEL_MOVE_PLAYER_REPLY))
		{
			return;
		}
		
		String[] splitData = message.getMessage().split("[" + SPLITTER + "]");
		
		if(channel.equals(CHANNEL_MOVE_PLAYER_REQ))
		{
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
			
			if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers())
			{
				response = "FULL";
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
			
			String data = serverName + SPLITTER + playerName;
			if(!response.isEmpty())
			{
				data += SPLITTER + response;
			}
			
			RedisManager.sendMessage(serverName, CHANNEL_MOVE_PLAYER_REPLY, data);
			
			if(!requestEvent.isCancelled())
			{
				//Ensure the player can join once their request has been accepted
				incomingPlayers.add(playerName);
				
				Bukkit.getPluginManager().callEvent(new PlayerEnterServerEvent(playerName));
			}
		}
		else if(channel.equals(CHANNEL_MOVE_PLAYER_REPLY))
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
			
			BUtil.logInfo("Received reply - " + playerName + " WITH " + response);
			
			if(response != null && !response.isEmpty())
			{
				BUtil.logMessage("Received Failed Reply for " + playerName + " to " + targetServer + " (" + response + ")");
				onFailTransfer(player, response);
				
				return;
			}
			
			onSuccessfulTransfer(player);
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
				event.allow();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		//Once the player has arrived, clear them from incoming players.
		if(incomingPlayers.contains(event.getPlayer().getName()))
		{
			event.allow();
			incomingPlayers.remove(event.getPlayer().getName());
		}
	}
	
}

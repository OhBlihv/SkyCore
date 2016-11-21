package com.skytonia.SkyCore.movement;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.sockets.SocketManager;
import com.skytonia.SkyCore.sockets.events.BukkitSocketJSONEvent;
import com.skytonia.SkyCore.util.BUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 11/17/2016.
 */
public class PlayerCount implements Listener
{
	
	@RequiredArgsConstructor
	private static class PlayerCountRequest
	{
		
		public final PlayerCountAction playerCountAction;
		
		public int taskId = -1;
		
	}
	
	public interface PlayerCountAction
	{
		
		void onPlayerCountReceive(int playerCount);
		
	}
	
	private static PlayerCount instance = null;
	public static PlayerCount getInstance()
	{
		if(instance == null)
		{
			instance = new PlayerCount();
		}
		return instance;
	}
	
	private static final String SPLITTER = "|";
	
	private static final String CHANNEL_REQ_PLAYER_COUNT = "PLAYER_COUNT_REQ",
								CHANNEL_RECV_PLAYER_COUNT = "PLAYER_COUNT_RECV";
	
	private static Map<String, PlayerCountRequest> playerCountRequests = new HashMap<>();
	
	private PlayerCount()
	{
		SocketManager.getInstance();
		
		Bukkit.getPluginManager().registerEvents(this, SkyCore.getPluginInstance());
	}
	
	@EventHandler
	public void onPlayerCountReceived(BukkitSocketJSONEvent event)
	{
		String channel = event.getChannel();
		
		if( !channel.equals(CHANNEL_REQ_PLAYER_COUNT) &&
			!channel.equals(CHANNEL_RECV_PLAYER_COUNT))
		{
			return;
		}
		
		String[] splitData = event.getData().split("[" + SPLITTER + "]");
		
		if(event.getChannel().equals(CHANNEL_REQ_PLAYER_COUNT))
		{
			//BUtil.logInfo("REQ: " + Arrays.toString(splitData) + " >> '" + splitData[0] + "'");
			SocketManager.sendMessageTo(splitData[0], CHANNEL_RECV_PLAYER_COUNT, SocketManager.getServerName(),
			                            String.valueOf(Bukkit.getOnlinePlayers().size()));
		}
		else if(event.getChannel().equalsIgnoreCase(CHANNEL_RECV_PLAYER_COUNT))
		{
			//BUtil.logInfo("RECV: " + Arrays.toString(splitData));
			
			//playerCountRequests.remove(splitData[2]).onPlayerCountReceive(Integer.parseInt(splitData[3]));
			try
			{
				receiveReply(splitData[0], Integer.parseInt(splitData[1]));
			}
			catch(Exception e)
			{
				BUtil.logStackTrace(e);
			}
		}
	}
	
	private void receiveReply(String server, int playerCount)
	{
		PlayerCountRequest playerRequest = playerCountRequests.remove(server);
		if(playerRequest == null)
		{
			throw new IllegalArgumentException("Received Player Count Reply from '" + server + "' but did not expect one!");
		}
		
		if(playerRequest.taskId != -1)
		{
			Bukkit.getScheduler().cancelTask(playerRequest.taskId);
			playerRequest.taskId = -1;
		}
		
		playerRequest.playerCountAction.onPlayerCountReceive(playerCount);
	}
	
	public static void requestPlayerCount(String server, PlayerCountAction playerCountAction)
	{
		if(playerCountAction == null)
		{
			throw new IllegalArgumentException("Expected NonNull PlayerCountAction");
		}
		
		PlayerCountRequest playerCountRequest = new PlayerCountRequest(playerCountAction);
		playerCountRequest.taskId = Bukkit.getScheduler().runTaskLater(SkyCore.getPluginInstance(), () ->
		{
			
			playerCountRequest.taskId = -1;
			playerCountRequest.playerCountAction.onPlayerCountReceive(-1);
			
		}, 60L).getTaskId(); //Delay 3 seconds. If a server doesn't reply in 3 seconds they deserve to be offline.
		
		playerCountRequests.put(server, playerCountRequest);
		
		SocketManager.sendMessageTo(server, CHANNEL_REQ_PLAYER_COUNT, SocketManager.getServerName());
	}
	
}

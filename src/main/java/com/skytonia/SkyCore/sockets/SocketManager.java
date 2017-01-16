package com.skytonia.SkyCore.sockets;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.sockets.client.SocketClient;
import com.skytonia.SkyCore.sockets.client.SocketClientApp;
import com.skytonia.SkyCore.sockets.events.BukkitSocketConnectEvent;
import com.skytonia.SkyCore.sockets.events.BukkitSocketDisconnectEvent;
import com.skytonia.SkyCore.sockets.events.BukkitSocketHandshakeEvent;
import com.skytonia.SkyCore.sockets.events.BukkitSocketJSONEvent;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.FlatFile;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Brown (OhBlihv) on 28/09/2016.
 */
public class SocketManager implements SocketClientApp
{
	
	private static SocketManager instance = null;
	public static SocketManager getInstance()
	{
		if(instance == null)
		{
			instance = new SocketManager();
		}
		return instance;
	}
	
	@Getter
	private static SocketClient socketClient;
	
	private final KeyPair keys = SocketUtil.RSA.generateKeys();
	
	private FlatFile socketConfig;
	
	//Any message
	private final Map<Integer, Runnable> requestWaitMap = new HashMap<>();
	
	@Getter
	private static String serverName;
	
	@Getter
	private static boolean isConnected = false;
	
	private SocketManager()
	{
		socketConfig = SocketFlatFile.getInstance();
	}
	
	public void start()
	{
		if(socketConfig.getSave().isString("port"))
		{
			BUtil.logMessage("WARNING: Sockets Configuration has not been set up!");
			BUtil.logMessage("Sockets will not function until the config is correctly set up.");
			return;
		}
		
		serverName = socketConfig.getString("name");
		String hostName = socketConfig.getString("host");
		
		int port = socketConfig.getInt("port");
		
		socketClient = new SocketClient(this, serverName, hostName, port, keys);
		
		Bukkit.getScheduler().runTaskAsynchronously(SkyCore.getPluginInstance(), socketClient);
		BUtil.logMessage("Started socket client listener on " + hostName + ":" + port + " as '" + serverName + "'.");
		
		isConnected = true;
	}
	
	public boolean stop()
	{
		//Socket client was not set up. Assume 'safe'
		if(socketClient == null)
		{
			return true;
		}
		
		isConnected = false;
		
		IOException exception = socketClient.interrupt();
		if(exception != null)
		{
			BUtil.logMessage("Hit exception while disabling Socket Client on port " + socketClient.getPort());
			BUtil.logStackTrace(exception);
			return false;
		}
		else
		{
			BUtil.logMessage("Stopped Socket Client on port " + socketClient.getPort());
			return true;
		}
	}
	
	private static String concatenateData(String... data)
	{
		if(data.length == 0)
		{
			return "";
		}
		
		StringBuilder stringData = new StringBuilder();
		for(String dataLoop : data)
		{
			stringData.append(dataLoop).append("|");
		}
		
		return stringData.toString().substring(0, stringData.length() - 1);
	}
	
	public static void sendMessage(String channel, String... data)
	{
		if(!isConnected())
		{
			throw new IllegalStateException("Not Connected");
		}
		
		socketClient.writeJSON(channel, concatenateData(data));
	}
	
	public static void sendMessageTo(String targetServer, String channel, String... data)
	{
		if(!isConnected())
		{
			throw new IllegalStateException("Not Connected");
		}
		
		socketClient.writeJSON("PASSTHROUGH", stripSplitters(targetServer) + "|" + stripSplitters(channel) + "|" + concatenateData(data));
	}
	
	private static String stripSplitters(String inputString)
	{
		if(inputString == null)
		{
			return "";
		}
		
		if(inputString.contains("|"))
		{
			inputString = inputString.replaceAll("[|]", "");
		}
		
		return inputString;
	}
	
	public void restart()
	{
		if(stop())
		{
			start();
		}
	}
	
	@Override
	public void onConnect(SocketClient client)
	{
		BUtil.logMessage("Connected to " + client.getHost() + ":" + client.getPort() + " successfully.");
		Bukkit.getPluginManager().callEvent(new BukkitSocketConnectEvent(client));
	}
	
	@Override
	public void onDisconnect(SocketClient client)
	{
		BUtil.logMessage("Disconnected from " + client.getHost() + ":" + client.getPort() + ".");
		Bukkit.getPluginManager().callEvent(new BukkitSocketDisconnectEvent(client));
	}
	
	@Override
	public void onHandshake(SocketClient client)
	{
		Bukkit.getPluginManager().callEvent(new BukkitSocketHandshakeEvent(client));
	}
	
	@Override
	public void onJSON(SocketClient client, Map<String, String> map)
	{
		Bukkit.getPluginManager().callEvent(new BukkitSocketJSONEvent(client, map));
	}
	
}

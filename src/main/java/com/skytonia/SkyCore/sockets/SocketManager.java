package com.skytonia.SkyCore.sockets;

import com.google.common.collect.Iterators;
import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.sockets.client.SocketClient;
import com.skytonia.SkyCore.sockets.client.SocketClientApp;
import com.skytonia.SkyCore.sockets.events.BukkitSocketConnectEvent;
import com.skytonia.SkyCore.sockets.events.BukkitSocketDisconnectEvent;
import com.skytonia.SkyCore.sockets.events.BukkitSocketHandshakeEvent;
import com.skytonia.SkyCore.sockets.events.BukkitSocketJSONEvent;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.RunnableShorthand;
import com.skytonia.SkyCore.util.file.FlatFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Iterator;
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
	
	private final JavaPlugin plugin;
	
	@Getter
	private static SocketClient socketClient;
	
	private final KeyPair keys = SocketUtil.RSA.generateKeys();
	
	private FlatFile socketConfig;
	
	@Getter
	private static String serverName;
	
	@Getter
	private static boolean isConnected = false;
	
	private SocketManager()
	{
		socketConfig = SocketFlatFile.getInstance();
		
		this.plugin = SkyCore.getPluginInstance();
	}
	
	public boolean start()
	{
		if(socketConfig.getSave().isString("port"))
		{
			BUtil.logMessage("WARNING: Sockets Configuration has not been set up!");
			BUtil.logMessage("Sockets will not function until the config is correctly set up.");
			return false;
		}
		
		serverName = socketConfig.getString("name");
		String hostName = socketConfig.getString("host");
		
		int port = socketConfig.getInt("port");
		
		socketClient = new SocketClient(this, serverName, hostName, port, keys);
		
		//Bukkit.getScheduler().runTaskAsynchronously(SkyCore.getPluginInstance(), socketClient);
		socketClient.start();
		BUtil.logMessage("Started socket client listener on " + hostName + ":" + port + " as '" + serverName + "'.");
		
		isConnected = true;
		
		return true;
	}
	
	public boolean stop()
	{
		//Socket client was not set up. Assume 'safe'
		if(socketClient == null)
		{
			return true;
		}
		
		isConnected = false;
		
		IOException exception = socketClient.interruptClient();
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
		for(Iterator<String> dataItr = Iterators.forArray(data);dataItr.hasNext();)
		{
			stringData.append(dataItr.next());
			
			if(dataItr.hasNext())
			{
				stringData.append("|");
			}
		}
		
		return stringData.toString();
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
		
		String message = concatenateData(data);
		if(message.endsWith("|"))
		{
			message = message.substring(0, message.length() - 1);
		}
		
		socketClient.writeJSON("PASSTHROUGH", stripSplitters(targetServer) + "|" +
			                                            stripSplitters(channel) + "|" +
			                                            message);
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
		RunnableShorthand.forPlugin(plugin).with(() ->
		{
			BUtil.logMessage("Connected to " + client.getHost() + ":" + client.getPort() + " successfully.");
			Bukkit.getPluginManager().callEvent(new BukkitSocketConnectEvent(client));
		}).ensureASync();
	}
	
	@Override
	public void onDisconnect(SocketClient client)
	{
		RunnableShorthand.forPlugin(plugin).with(() ->
		{
			BUtil.logMessage("Disconnected from " + client.getHost() + ":" + client.getPort() + ".");
			Bukkit.getPluginManager().callEvent(new BukkitSocketDisconnectEvent(client));
		}).ensureASync();
	}
	
	@Override
	public void onHandshake(SocketClient client)
	{
		RunnableShorthand.forPlugin(plugin).with(() -> Bukkit.getPluginManager().callEvent(new BukkitSocketHandshakeEvent(client))).ensureASync();
	}
	
	@Override
	public void onJSON(SocketClient client, Map<String, String> map)
	{
		RunnableShorthand.forPlugin(plugin).with(() -> Bukkit.getPluginManager().callEvent(new BukkitSocketJSONEvent(client, map))).ensureASync();
	}
	
}

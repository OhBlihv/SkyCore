package com.skytonia.SkyCore;

import com.skytonia.SkyCore.gui.actions.ElementActions;
import com.skytonia.SkyCore.gui.variables.GUIVariables;
import com.skytonia.SkyCore.movement.MovementManager;
import com.skytonia.SkyCore.movement.PlayerCount;
import com.skytonia.SkyCore.redis.RedisManager;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.file.FlatFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Chris Brown (OhBlihv) on 25/09/2016.
 */
public class SkyCore extends JavaPlugin implements Listener
{
	
	@Getter
	private static SkyCore instance = null;
	public static JavaPlugin getPluginInstance()
	{
		//We're shaded!
		if(instance == null)
		{
			String callingPlugin = BUtil.getCallingPlugin();
			
			Plugin plugin = Bukkit.getPluginManager().getPlugin(callingPlugin);
			if(plugin != null)
			{
				return (JavaPlugin) plugin;
			}
			else
			{
				BUtil.logError("Could not find calling plugin for getInstance()");
				return null;
			}
		}
		else
		{
			return instance;
		}
	}
	
	private static boolean isSkytonia = false;
	public static boolean isSkytonia()
	{
		return isSkytonia;
	}
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		getServer().getPluginManager().registerEvents(this, this);
		BUtil.logInfo("Using Spigot flavour '" + getServer().getName() + "'");
		if(getServer().getName().equals("SkyPaper"))
		{
			BUtil.logInfo("Enabling Skytonia-specific features.");
			isSkytonia = true;
			
			if(getServer().getPluginManager().getPlugin("ProtocolLib") != null)
			{
				BUtil.logInfo("Enabling Packet Handling");
				try
				{
					getServer().getPluginManager().registerEvents(new PacketHandling(this), this);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			//Set up Redis/Jedis
			try
			{
				new PlayerCount(this);   //Start Player Count Updater
				MovementManager.getInstance();  //Register Listeners
			}
			catch(Exception e)
			{
				BUtil.logInfo("Could not set up Redis Connection");
				e.printStackTrace();
			}
		}
		
		//Initialize Addon Registries
		try
		{
			ElementActions.getInstance();   //Initialise Stored ElementActions
			GUIVariables.getInstance();     //Initialise Stored GUIVariables
		}
		catch(IllegalArgumentException e)
		{
			BUtil.logError("An issue occurred while initializing stored variables. Refer to the stack trace below.");
			BUtil.logStackTrace(e);
		}
	}
	
	@Override
	public void onDisable()
	{
		PlayerCount.updatePlayerCount(0); //Show we're offline
		
		RedisManager.shutdown();
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent event)
	{
		//Attempt to strip the version number from the plugin name and just retrieve the initial name
		FlatFile.unregisterFlatFile(event.getPlugin().getName().split("[ ]")[0]);
	}
	
	/*@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(args.length > 0 && args[0].equalsIgnoreCase("start"))
		{
			RunnableShorthand.forPlugin(this).with(() ->
			{
				SocketClient socketClient = SocketManager.getSocketClient();
				
				long startTime = System.currentTimeMillis();
				
				int i = 0;
				while(true)
				{
					while(!socketClient.isConnectedAndOpened())
					{
						//Thrash
					}
					
					socketClient.writeJSON("PASSTHROUGH", "Data" + (++i));
					System.out.print(i + " in " + ((System.currentTimeMillis() - startTime) / 1000D) + " seconds.");
				}
				
			}).runASync();
			BUtil.logInfo("Started Socket Flooder");
		}
		
		return true;
	}*/
	
}

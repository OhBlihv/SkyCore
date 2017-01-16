package com.skytonia.SkyCore;

import com.skytonia.SkyCore.gui.actions.ElementActions;
import com.skytonia.SkyCore.gui.variables.GUIVariables;
import com.skytonia.SkyCore.movement.MovementManager;
import com.skytonia.SkyCore.movement.PlayerCount;
import com.skytonia.SkyCore.sockets.SocketManager;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.FlatFile;
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
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		getServer().getPluginManager().registerEvents(this, this);
		
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
		
		Bukkit.getScheduler().runTaskLater(this, () ->
		{
			SocketManager.getInstance().start();
			MovementManager.getInstance();  //Register Listeners
			PlayerCount.getInstance();      //Register Listeners
		}, 20L); //Allow 1 second after the server has started to start accepting players/messages
	}
	
	@Override
	public void onDisable()
	{
		SocketManager.getInstance().stop();
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent event)
	{
		//Attempt to strip the version number from the plugin name and just retrieve the initial name
		FlatFile.unregisterFlatFile(event.getPlugin().getName().split("[ ]")[0]);
	}
	
}

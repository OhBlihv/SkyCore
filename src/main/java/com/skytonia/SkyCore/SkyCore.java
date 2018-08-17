package com.skytonia.SkyCore;

import com.skytonia.SkyCore.gui.actions.ElementActions;
import com.skytonia.SkyCore.gui.variables.GUIVariables;
import com.skytonia.SkyCore.servers.ServerController;
import com.skytonia.SkyCore.servers.handlers.CommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.NullCommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.RedisCommunicationHandler;
import com.skytonia.SkyCore.servers.handlers.debug.DebugJedisPool;
import com.skytonia.SkyCore.servers.handlers.processing.AbstractCommunicationHandler;
import com.skytonia.SkyCore.titles.TagController;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.ReflectionUtils;
import com.skytonia.SkyCore.util.SupportedVersion;
import com.skytonia.SkyCore.util.file.FlatFile;
import javafx.util.Pair;
import lombok.Getter;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.AbstractQueue;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
				BUtil.log("Could not find calling plugin for getInstance()");
				return null;
			}
		}
		else
		{
			return instance;
		}
	}
	
	private static SupportedVersion currentVersion = null;
	public static SupportedVersion getCurrentVersion()
	{
		if(currentVersion == null)
		{
			currentVersion = SupportedVersion.getVersionForNumber(
				Integer.parseInt(ReflectionUtils.PackageType.getServerVersion().length() > 7 ?
					                 ReflectionUtils.PackageType.getServerVersion().substring(3, 5) : //1.10+
				                     Character.toString(ReflectionUtils.PackageType.getServerVersion().charAt(3)))
			);
		}
		
		return currentVersion;
	}
	
	private static boolean isSkytonia = false;
	public static boolean isSkytonia()
	{
		return isSkytonia;
	}
	
	@Getter
	private ServerController serverController = null;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		getServer().getPluginManager().registerEvents(this, this);

		serverController = new ServerController();
		
		BUtil.log("Using Spigot flavour '" + getServer().getName() + "'");
		if(getServer().getName().equals("SkyPaper"))
		{
			BUtil.log("Enabling Skytonia-specific features.");
			isSkytonia = true;
			
			if(getServer().getPluginManager().getPlugin("ProtocolLib") != null)
			{
				BUtil.log("Enabling Packet Handling");
				try
				{
					getServer().getPluginManager().registerEvents(new PacketHandling(this), this);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			TagController.getInstance(); //Enable Tags
		}
		
		//Initialize Addon Registries
		try
		{
			ElementActions.getInstance();   //Initialise Stored ElementActions
			GUIVariables.getInstance();     //Initialise Stored GUIVariables
		}
		catch(IllegalArgumentException e)
		{
			BUtil.log("An issue occurred while initializing stored variables. Refer to the stack trace below.");
			BUtil.logStackTrace(e);
		}
	}
	
	@Override
	public void onDisable()
	{
		final CommunicationHandler commHandler = ServerController.getCommunicationHandler();
		if(commHandler != null && !(commHandler instanceof NullCommunicationHandler))
		{
			try
			{
				((AbstractCommunicationHandler) ServerController.getCommunicationHandler()).shutdown();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent event)
	{
		//Attempt to strip the version number from the plugin name and just retrieve the initial name
		FlatFile.unregisterFlatFile(event.getPlugin().getName().split("[ ]")[0]);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		/*if(args.length > 0 && args[0].equalsIgnoreCase("start"))
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
			BUtil.log("Started Socket Flooder");
		}*/

		if(sender.isOp())
		{
			String fileName = "redis-connections-" + (System.currentTimeMillis() / 1000L) + ".debug";

			File debugFile = new File(SkyCore.getInstance().getDataFolder(), fileName);
			try(PrintWriter writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(debugFile))))
			{
				Map<?, PooledObject<Jedis>> allObjects;
				AbstractQueue<PooledObject<Jedis>> idleObjects;

				{
					Field internalPoolMethod = Pool.class.getDeclaredField("internalPool");
					internalPoolMethod.setAccessible(true);

					GenericObjectPool<Jedis> internalPool = (GenericObjectPool<Jedis>) internalPoolMethod.get(
						((RedisCommunicationHandler) ServerController.getCommunicationHandler()).getJedisPool()
					);

					BUtil.log("Pool class: " + internalPool.getClass().getSimpleName());

					Field field = internalPool.getClass().getDeclaredField("allObjects");
					field.setAccessible(true);
					allObjects = (Map<?, PooledObject<Jedis>>) field.get(internalPool);

					field = internalPool.getClass().getDeclaredField("idleObjects");
					field.setAccessible(true);
					idleObjects = (AbstractQueue<PooledObject<Jedis>>) field.get(internalPool);
				}

				Set<Integer> toRemoveConnections = new HashSet<>();
				for(Map.Entry<Integer, Pair<Jedis, Throwable>> entry : DebugJedisPool.registeredConnections.entrySet())
				{
					Object identityWrapper;

					Constructor<?> wrapperConstructor = Class.forName("org.apache.commons.pool2.impl.BaseGenericObjectPool$IdentityWrapper").getConstructors()[0];
					wrapperConstructor.setAccessible(true);
					identityWrapper = wrapperConstructor.newInstance(entry.getValue().getKey());

					PooledObject<Jedis> pooledJedis = allObjects.get(identityWrapper);
					if(idleObjects.contains(pooledJedis))
					{
						BUtil.log("Skipping idle connection...");
						toRemoveConnections.add(entry.getKey());
						continue;
					}

					if(!entry.getValue().getKey().isConnected())
					{
						BUtil.log("Skipping dead connection...");
						toRemoveConnections.add(entry.getKey());
					}
					else
					{
						writer.println("Connection ID: (" + entry.getKey() + ")");
						entry.getValue().getValue().printStackTrace(writer);
						writer.println(">----<");
					}
				}

				DebugJedisPool.registeredConnections.keySet().removeAll(toRemoveConnections);

				BUtil.log("Logged current jedis connection stack traces to log at " + fileName);
				sender.sendMessage("Logged current jedis connection stack traces to log at " + fileName);
			}
			catch(Exception e)
			{
				BUtil.log("Failed to log jedis connection stack traces to file.");
				sender.sendMessage("Failed to log jedis connection stack traces to file.");
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
}

package com.skytonia.SkyCore.util.file;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.util.BUtil;
import com.skytonia.SkyCore.util.LocationUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlatFile
{
	
	private static class FlatFileHandler implements Listener
	{
		
		@EventHandler
		public void onPluginUnload(PluginDisableEvent event)
		{
			String pluginName = event.getPlugin().getName().split("[ ]")[0];
			FlatFile oldFlatFile;
			//Remove old instance of flatfile to support plugin reloads
			if((oldFlatFile = flatfileInstances.remove(pluginName)) != null)
			{
				BUtil.logMessageAsPlugin("SkyCore", "Unloaded " + oldFlatFile.getClass().getSimpleName() +
					                                    " from: " + pluginName + " (" + oldFlatFile.fileName + ")");
			}
		}
		
	}
	
	private static FlatFileHandler fileHandler = null;

	protected File saveFile = null;
	protected FileConfiguration save = null;
	
	protected final String pluginString;
	protected final JavaPlugin plugin;
	
	@Getter
	String fileName = "config.yml";
	
	private static final Map<String, FlatFile> flatfileInstances = new HashMap<>();
	
	public static void unregisterFlatFile(String plugin)
	{
		flatfileInstances.remove(plugin);
	}
	
	public static FlatFile forFileName(String fileName)
	{
		return new FlatFile(fileName, null);
	}

	public static FlatFile forFileName(String fileName, boolean suppressWarnings)
	{
		return new FlatFile(fileName, null, suppressWarnings);
	}
	
	public static FlatFile getInstance()
	{
		if(fileHandler == null)
		{
			//Register plugin unload listener
			Bukkit.getPluginManager().registerEvents((fileHandler = new FlatFileHandler()), SkyCore.getPluginInstance());
		}
		
		FlatFile instance;
		String owningPlugin = BUtil.getCallingPlugin();
		
		if((instance = flatfileInstances.get(owningPlugin)) == null)
		{
			instance = new FlatFile(null, owningPlugin);
			flatfileInstances.put(owningPlugin, instance);
		}
		
		return instance;
	}

	//Default to config.yml. Allow other filenames through the other constructor
	FlatFile()
	{
		this("config.yml", null);
	}

	protected FlatFile(String fileName, String owningPlugin)
	{
		this(fileName, owningPlugin, false);
	}

	private boolean suppressWarnings = false;

	protected FlatFile(String fileName, String owningPlugin, boolean suppressWarnings)
	{
		this.suppressWarnings = suppressWarnings;

		if(fileName == null)
		{
			this.fileName = "config.yml";
		}
		else
		{
			this.fileName = fileName;
		}

		JavaPlugin tempOwningPlugin;
		String tempPluginString;

		if(owningPlugin != null)
		{
			tempPluginString = owningPlugin;
			tempOwningPlugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin(owningPlugin);
		}
		else
		{
			tempPluginString = BUtil.getCallingPlugin();
			tempOwningPlugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin(tempPluginString);
		}

		if(tempOwningPlugin == null)
		{
			tempPluginString = "SkyCore";
			tempOwningPlugin = SkyCore.getInstance();
		}

		this.pluginString = tempPluginString;
		this.plugin = tempOwningPlugin;
		
		//Support extending classes
		BUtil.logMessageAsPlugin("SkyCore", "Registered new " + getClass().getSimpleName() + " to: " + plugin + " (" + this.fileName + ")");
		saveDefaultConfig();
		getSave();

		suppressWarnings = false;
	}

	public FileConfiguration getSave() 
	{
	    if (save == null)
	    {
	        reloadFile();
	    }
	    return save;
	}
	
	public void reloadFile() 
	{
	    if (saveFile == null) 
	    {
	    	saveFile = new File(plugin.getDataFolder(), fileName);
	    }
	    save = YamlConfiguration.loadConfiguration(saveFile);
	}
	
	public void saveDefaultConfig() 
	{
	    if (saveFile == null)
	    {
		    try
		    {
			    saveFile = new File(plugin.getDataFolder(), fileName);
		    }
		    catch(Exception e)
		    {
			    //
			}
	    }
		
		if (!saveFile.exists())
		{
		    try
		    {
		        plugin.saveResource(fileName, false);
		    }
		    catch(Exception e)
		    {
		        try
			    {
			    	saveFile.createNewFile();
			    }
			    catch(Exception e2)
			    {
			    	if(!suppressWarnings)
				    {
					    BUtil.logError("Could not set up " + fileName + " registered to plugin '" + pluginString + "'. Is the plugin/fileName correct?");
					    e.printStackTrace();
				    }
			    }
		    }
		}
	}
	
	public void saveToFile() 
	{
	    if (save == null || saveFile == null) 
	    {
	        return;
	    }
	    try
	    {
	        save.save(saveFile);
	    }
	    catch (IOException ex) 
	    {
	    	BUtil.logError("Could not save config to " + saveFile);
	    }
	}
	
	public Set<String> getChildren(String path)
	{
		return save.getConfigurationSection(path).getKeys(false);
	}

	public String getString(String path)
	{
		return getString(path, "");
	}

	public String getString(String path, String defaultString)
	{
		return save.getString(path, defaultString);
	}
	
	public String getFormattedString(String path)
	{
		return getFormattedString(path, "");
	}
	
	public String getFormattedString(String path, String defaultString)
	{
		return BUtil.translateColours(save.getString(path, defaultString));
	}
	
	public List<String> getStringList(String path)
	{
		return save.getStringList(path);
	}
	
	public List<String> getFormattedStringList(String path)
	{
		return BUtil.translateColours(save.getStringList(path));
	}
	
	public int getInt(String path)
	{
		return save.getInt(path);
	}

	public boolean getBoolean(String path)
	{
		return getBoolean(path, false);
	}

	public boolean getBoolean(String path, boolean defaultBoolean)
	{
		return save.getBoolean(path, defaultBoolean);
	}
	
	public long getLong(String path)
	{
		return save.getLong(path);
	}
	
	public void saveValue(String path, Object value)
	{
		save.set(path, value);
		saveToFile();
	}

	public double getDouble(String path)
	{
		return save.getDouble(path);
	}
	
	public float getFloat(String path)
	{
		return (float) save.getDouble(path);
	}

	public ConfigurationSection getConfigurationSection(String path)
	{
		return save.getConfigurationSection(path);
	}

	public Location getLocation(String path)
	{
		return LocationUtil.parseLocation(save.getString(path));
	}
	
	public Location getLocation(ConfigurationSection configurationSection)
	{
		if(configurationSection == null)
		{
			BUtil.logError("Configuration Section linked null!");
			
			new Exception().printStackTrace();
			
			return LocationUtil.DEFAULT_LOCATION;
		}
		
		World world = null;
		try
		{
			world = Bukkit.getWorld(configurationSection.getString("world"));
		}
		catch(Exception e)
		{
			//Ignore
		}
		
		if(world == null)
		{
			BUtil.logError("Could not find world named '" + configurationSection.getString("world") + "'");
			return LocationUtil.DEFAULT_LOCATION;
		}
		
		int x = configurationSection.getInt("x"),
			y = configurationSection.getInt("y"),
			z = configurationSection.getInt("z");
		
		float   yaw = (float) configurationSection.getDouble("yaw", 0),
				pitch = (float) configurationSection.getDouble("pitch", 0);
		
		return new Location(world, x, y, z, yaw, pitch);
	}

	/*
	 * List/Map Operations
	 */

	/**
	 *
	 * @param path Path to the List<Map>
	 * @return The List<Map> if the object at #path is a List Map
	 *          Else NULL
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getListMap(String path)
	{
		Object listMap = save.get(path);
		if(listMap instanceof List)
		{
			return (List<Map<String, Object>>) listMap;
		}

		return null;
	}

	public static String getString(Map<String, Object> configurationMap, String path, String def)
	{
		Object object = configurationMap.get(path);
		if(object instanceof String && !((String) object).isEmpty())
		{
			return (String) object;
		}
		return def;
	}

	public static List<String> getStringList(Map<String, Object> configurationMap, String path)
	{
		Object object = configurationMap.get(path);
		if(object instanceof List && !((List) object).isEmpty())
		{
			return (List<String>) object;
		}
		return new ArrayList<>();
	}

	public static int getInt(Map<String, Object> configurationMap, String path, int def)
	{
		Object object = configurationMap.get(path);
		if(object instanceof Integer)
		{
			return (Integer) object;
		}
		return def;
	}

	public static Material getMaterial(Map<String, Object> configurationMap, String path, Material def)
	{
		Object object = configurationMap.get(path);
		if(object instanceof String)
		{
			Material toReturn = Material.getMaterial((String) object);
			if(toReturn != null)
			{
				return toReturn;
			}

			BUtil.logError("Invalid material: " + object);
		}
		return def;
	}

}

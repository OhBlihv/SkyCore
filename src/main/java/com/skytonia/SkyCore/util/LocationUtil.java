package com.skytonia.SkyCore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Chris Brown (OhBlihv) on 26/09/2016.
 */
public class LocationUtil
{
	
	public static final Location DEFAULT_LOCATION = new Location(Bukkit.getWorlds().get(0), 0, 128, 0);
	
	/**
	 * Returns a location stored in a ConfigurationSection in the format:
	 *
	 *  config-key:
	 *      world: world
	 *      x: 14
	 *      y: 128
	 *      z: 44
	 *      yaw: 270
	 *      pitch: 90
	 *
	 * @param configurationSection
	 * @return
	 */
	public static Location getLocation(ConfigurationSection configurationSection)
	{
		if(configurationSection == null || configurationSection.getKeys(false).isEmpty())
		{
			BUtil.logInfo("Configuration Section: " + (configurationSection == null ? "NULL" : configurationSection.getCurrentPath()) + " is NULL or empty!");
			return DEFAULT_LOCATION;
		}
		
		World world;
		if(configurationSection.contains("world"))
		{
			String worldName = configurationSection.getString("world", null);
			if(worldName != null && Bukkit.getWorld(worldName) != null)
			{
				world = Bukkit.getWorld(worldName);
			}
			else
			{
				BUtil.logError("World name '" + worldName + "' does not correspond to a valid world on this server. (" + configurationSection.getCurrentPath() + ")");
				return DEFAULT_LOCATION;
			}
		}
		else
		{
			world = Bukkit.getWorlds().get(0);
			BUtil.logError("No world defined in '" + configurationSection.getCurrentPath() + "'. Defaulting to " + world.getName() + ". Please define a world next time.");
		}
		double  x = configurationSection.getDouble("x", 0D),
			y = configurationSection.getDouble("y", 128D),
			z = configurationSection.getDouble("z", 0D);
		float   pitch = (float) configurationSection.getDouble("pitch", 0D),
			yaw = (float) configurationSection.getDouble("yaw", 0D);
		
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	public static Location parseLocationWithWorld(String locationString)
	{
		String[] locationSplit = locationString.split("[:]");
		
		World world = Bukkit.getWorld(locationSplit[0]);
		
		return parseLocation(locationString, world, 1);
	}
	
	public static Location parseLocation(String locationString)
	{
		return parseLocation(locationString, null, 0);
	}
	
	public static Location parseLocation(String locationString, World world, int startVal)
	{
		String[] locationSplit = locationString.split("[:]");
		try
		{
			double 	x = Double.parseDouble(locationSplit[startVal]),
					y = Double.parseDouble(locationSplit[startVal + 1]),
					z = Double.parseDouble(locationSplit[startVal + 2]);
			float 	yaw = 0, pitch = 0;
			if(locationSplit.length > (3 + startVal))
			{
				yaw = Float.parseFloat(locationSplit[startVal + 3]);
				pitch = Float.parseFloat(locationSplit[startVal + 4]);
			}
			
			if(world == null)
			{
				if(startVal > 0)
				{
					world = Bukkit.getWorld(locationSplit[0]);
				}
				else
				{
					world = Bukkit.getWorlds().get(0);
				}
			}
			
			return new Location(world, x, y, z, yaw, pitch);
		}
		catch(NumberFormatException e)
		{
			//TODO:
			BUtil.logError("Invalid location given by '" + locationString + "'. Returning default location.");
		}
		
		return DEFAULT_LOCATION;
	}
	
	/*
	 * Saving Methods
	 */
	
	public static String serialiseLocation(Location location)
	{
		String locationString =
			location.getWorld().getName()   + ":" +
			location.getBlockX()            + ":" +
			location.getBlockY()            + ":" +
			location.getBlockZ();
		
		if(location.getYaw() == 0 && location.getPitch() == 0)
		{
			return locationString;
		}
		
		return location + ":" + location.getYaw() + ":" + location.getPitch();  //If one is non-zero, we have to add both per convention.
	}
	
}

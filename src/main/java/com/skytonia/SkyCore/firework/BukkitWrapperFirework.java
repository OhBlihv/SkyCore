package com.skytonia.SkyCore.firework;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by Chris Brown (OhBlihv) on 16/10/2016.
 */
public class BukkitWrapperFirework implements ICustomEntityFirework
{
	
	private Firework firework;
	
	public BukkitWrapperFirework(Firework firework)
	{
		this.firework = firework;
	}
	
	@Override
	public Entity getBukkitEntity()
	{
		return firework;
	}
	
	@Override
	public void setPosition(double x, double y, double z)
	{
		firework.teleport(new Location(Bukkit.getWorlds().get(0), x, y, z), PlayerTeleportEvent.TeleportCause.PLUGIN);
	}
	
	@Override
	public void setInvisible(boolean invisible)
	{
		//Ignore
	}
	
	@Override
	public void addFirework()
	{
		//Ignore
	}
}

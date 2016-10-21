package com.skytonia.SkyCore.firework;

import org.bukkit.entity.Entity;

/**
 * Created by Chris Brown (OhBlihv) on 16/10/2016.
 */
public interface ICustomEntityFirework
{
	
	Entity getBukkitEntity();
	
	void setPosition(double x, double y, double z);
	
	void setInvisible(boolean invisible);
	
	void addFirework();
	
}

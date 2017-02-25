package com.skytonia.SkyCore.firework;

import com.skytonia.SkyCore.util.BUtil;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * Created by Chris Brown (OhBlihv) on 16/10/2016.
 */
public class CustomEntityFirework
{
	
	public static void spawn(Location location, FireworkEffect effect, int tickDuration)
	{
		try
		{
			ICustomEntityFirework firework = getNMSFirework(location, tickDuration);
			FireworkMeta meta = ((Firework) firework.getBukkitEntity()).getFireworkMeta();
			meta.addEffect(effect);
			((Firework) firework.getBukkitEntity()).setFireworkMeta(meta);
			firework.setPosition(location.getX(), location.getY(), location.getZ());
			
			firework.addFirework();
			firework.setInvisible(true);
			
			if(tickDuration <= 1)
			{
				((Firework) firework.getBukkitEntity()).detonate();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static ICustomEntityFirework getNMSFirework(Location location, int tickDuration)
	{
		ICustomEntityFirework nmsFirework;
		if(tickDuration > 1)
		{
			return new BukkitWrapperFirework(location.getWorld().spawn(location, Firework.class));
		}
		
		switch(BUtil.getNMSVersion())
		{
			case "v1_8_R3":  nmsFirework = new CustomEntityFirework_1_8_R3(location.getWorld()); break;
			case "v1_9_R2":  nmsFirework = new CustomEntityFirework_1_9_R2(location.getWorld()); break;
			case "v1_10_R1": nmsFirework = new CustomEntityFirework_1_10_R1(location.getWorld(), tickDuration); break;
			case "v1_11_R1": nmsFirework = new CustomEntityFirework_1_11_R1(location.getWorld(), tickDuration); break;
			default:
				throw new IllegalArgumentException("NMS Version '" + BUtil.getNMSVersion() + "' not supported!");
		}
		return nmsFirework;
	}
	
}

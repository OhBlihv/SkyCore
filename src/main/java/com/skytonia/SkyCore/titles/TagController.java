package com.skytonia.SkyCore.titles;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.util.RunnableShorthand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Chris Brown (OhBlihv) on 4/10/2017.
 */
public class TagController
{
	
	private static TagController instance = null;
	public static TagController getInstance()
	{
		if(instance == null)
		{
			instance = new TagController();
		}
		return instance;
	}
	
	private final Map<UUID, TaggedPlayer> playerTagMap = new HashMap<>();
	
	TagController()
	{
		RunnableShorthand.forPlugin(SkyCore.getPluginInstance()).with(() ->
		{
			for(Player player : Bukkit.getOnlinePlayers())
			{
				TaggedPlayer taggedPlayer = playerTagMap.get(player.getUniqueId());
				if(taggedPlayer == null)
				{
					taggedPlayer = new TaggedPlayer(((CraftPlayer) player).getHandle());
					playerTagMap.put(player.getUniqueId(), taggedPlayer);
				}
				
				try
				{
					taggedPlayer.setLine(1, "§7Hours: §f" + 420);
					
					taggedPlayer.setLine(2, "aaaa");
					taggedPlayer.setLine(3, "aaaa");
					taggedPlayer.setLine(4, "aaaa");
					
					if(new Random().nextInt(10) < 1)
					{
						taggedPlayer.setLine(5, "b");
					}
					
					if(new Random().nextInt(10) < 1)
					{
						taggedPlayer.setLine(3, "b");
					}
					
					if(new Random().nextInt(10) < 1)
					{
						taggedPlayer.removeLine(2);
					}
				}
				catch(Throwable e)
				{
					//
				}
				
				Location playerLocation = player.getLocation();
				for(Player playerLoop : Bukkit.getOnlinePlayers())
				{
					//Ignore host player
					if(playerLoop == player)
					{
						continue;
					}
					
					//TODO: Configuration for Distance (5)
					if(playerLoop.getLocation().distance(playerLocation) < 5)
					{
						taggedPlayer.addNearbyPlayer(playerLoop);
					}
					else
					{
						//TODO: loop through already nearby players and check their distance?
						taggedPlayer.removeNearbyPlayer(playerLoop);
					}
				}
				
				taggedPlayer.update();
			}
		}).runTimer(20, 20);
	}
	
}

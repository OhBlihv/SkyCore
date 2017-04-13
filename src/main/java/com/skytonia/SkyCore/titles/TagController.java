package com.skytonia.SkyCore.titles;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.cosmetics.pets.PetUtil;
import com.skytonia.SkyCore.util.RunnableShorthand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Chris Brown (OhBlihv) on 4/10/2017.
 */
public class TagController implements Listener
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
	
	private final Map<UUID, TaggedPlayer> playerTagMap = new ConcurrentHashMap<>();
	
	TagController()
	{
		//Pre-Register our DataWatchers on the main thread
		PetUtil.getDefaultWatcher(Bukkit.getWorlds().get(0), EntityType.AREA_EFFECT_CLOUD);
		PetUtil.getDefaultWatcher(Bukkit.getWorlds().get(0), EntityType.SNOWBALL);
		PetUtil.getDefaultWatcher(Bukkit.getWorlds().get(0), EntityType.RABBIT);
		
		SkyCore.getPluginInstance().getServer().getPluginManager().registerEvents(this, SkyCore.getPluginInstance());
		
		RunnableShorthand.forPlugin(SkyCore.getPluginInstance()).with(() ->
		{
			for(Player player : Bukkit.getOnlinePlayers())
			{
				TaggedPlayer taggedPlayer = getPlayerTag(player);
				
				Location playerLocation = player.getLocation();
				for(Player playerLoop : Bukkit.getOnlinePlayers())
				{
					//Ignore host player
					if(playerLoop == player)
					{
						continue;
					}
					
					//TODO: Configuration for Distance (20)
					if(playerLoop.getLocation().distance(playerLocation) < 20)
					{
						taggedPlayer.addNearbyPlayer(playerLoop);
					}
					else
					{
						//TODO: loop through already nearby players and check their distance?
						taggedPlayer.removeNearbyPlayer(playerLoop);
					}
				}
			}
			
			List<UUID> toRemove = new ArrayList<>();
			for(Map.Entry<UUID, TaggedPlayer> entry : playerTagMap.entrySet())
			{
				if(!entry.getValue().update())
				{
					toRemove.add(entry.getKey());
				}
			}
			
			playerTagMap.keySet().removeAll(toRemove);
			
		}).runTimerASync(10, 10);
	}
	
	//
	
	public void setPlayerTagStatus(UUID playerUUID, boolean hidden)
	{
		playerTagMap.get(playerUUID).setHideTags(hidden);
	}
	
	public TaggedPlayer getPlayerTag(Player player)
	{
		TaggedPlayer taggedPlayer = playerTagMap.get(player.getUniqueId());
		//Handle reconnecting players as well as new players
		if(taggedPlayer == null || !taggedPlayer.isOnline())
		{
			taggedPlayer = new TaggedPlayer(((CraftPlayer) player).getHandle());
			playerTagMap.put(player.getUniqueId(), taggedPlayer);
		}
		
		return taggedPlayer;
	}
	
	//
	
	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event)
	{
		playerTagMap.get(event.getPlayer().getUniqueId()).setSneaking(!event.getPlayer().isSneaking());
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		playerTagMap.get(event.getPlayer().getUniqueId()).setOnline(false);
	}
	
}

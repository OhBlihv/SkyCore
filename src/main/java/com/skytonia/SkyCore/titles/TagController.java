package com.skytonia.SkyCore.titles;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.cosmetics.pets.PetUtil;
import com.skytonia.SkyCore.util.RunnableShorthand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;

import java.util.Iterator;
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
	
	private Plugin plugin = SkyCore.getPluginInstance();
	
	private final Map<UUID, TaggedPlayer> entityTagMap = new ConcurrentHashMap<>();
	
	TagController()
	{
		//Pre-Register our DataWatchers on the main thread
		PetUtil.getDefaultWatcher(Bukkit.getWorlds().get(0), EntityType.AREA_EFFECT_CLOUD);
		PetUtil.getDefaultWatcher(Bukkit.getWorlds().get(0), EntityType.SNOWBALL);
		PetUtil.getDefaultWatcher(Bukkit.getWorlds().get(0), EntityType.RABBIT);
		
		plugin.getServer().getPluginManager().registerEvents(this, SkyCore.getPluginInstance());
		
		RunnableShorthand.forPlugin(SkyCore.getPluginInstance()).with(() ->
		{
			for(Player player : Bukkit.getOnlinePlayers())
			{
				//Enforce all players have their tags
				getPlayerTag(player);
			}
			
			for(Iterator<TaggedPlayer> tagItr = entityTagMap.values().iterator();tagItr.hasNext();)
			{
				TaggedPlayer taggedPlayer = tagItr.next();
				
				updateNearbyPlayers(taggedPlayer);
				
				if(!taggedPlayer.update())
				{
					tagItr.remove();
				}
			}
			
		}).runTimerASync(10, 10);
	}
	
	private void updateNearbyPlayers(TaggedPlayer taggedPlayer)
	{
		Entity entity = taggedPlayer.getEntity().getBukkitEntity();
		Location entityLocation = entity.getLocation();
		
		for(Player playerLoop : Bukkit.getOnlinePlayers())
		{
			//Ignore host player
			if(playerLoop == entity)
			{
				continue;
			}
			
			//TODO: Configuration for Distance (20)
			if(entityLocation.getWorld() == playerLoop.getWorld() && playerLoop.getLocation().distance(entityLocation) < 20)
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
	
	//
	
	public void setPlayerTagStatus(Player player, boolean hidden)
	{
		getPlayerTag(player).setHideTags(hidden);
	}
	
	public TaggedPlayer getTagForEntity(Entity entity)
	{
		TaggedPlayer taggedPlayer = entityTagMap.get(entity.getUniqueId());
		if(taggedPlayer == null)
		{
			taggedPlayer = new TaggedPlayer(((CraftEntity) entity).getHandle());
			entityTagMap.put(entity.getUniqueId(), taggedPlayer);
		}
		
		return taggedPlayer;
	}
	
	public TaggedPlayer getPlayerTag(Player player)
	{
		TaggedPlayer taggedPlayer = entityTagMap.get(player.getUniqueId());
		//Handle reconnecting players as well as new players
		if(taggedPlayer == null || !taggedPlayer.isOnline())
		{
			taggedPlayer = new TaggedPlayer(((CraftEntity) player).getHandle());
			entityTagMap.put(player.getUniqueId(), taggedPlayer);
		}
		
		return taggedPlayer;
	}
	
	//
	
	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event)
	{
		RunnableShorthand.forPlugin(plugin).with(() -> entityTagMap.get(event.getPlayer().getUniqueId()).setSneaking(/*!*/event.getPlayer().isSneaking())).runASync();
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		RunnableShorthand.forPlugin(plugin).with(() -> entityTagMap.get(event.getPlayer().getUniqueId()).setOnline(false)).runASync();
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(!entityTagMap.isEmpty() && event.getEntityType() != EntityType.PLAYER)
		{
			RunnableShorthand.forPlugin(plugin).with(() ->
			{
				TaggedPlayer taggedPlayer = entityTagMap.get(event.getEntity().getUniqueId());
				if(taggedPlayer != null)
				{
					taggedPlayer.setOnline(false);
				}
			}).runASync();
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		RunnableShorthand.forPlugin(plugin).with(() ->
		{
			TaggedPlayer taggedPlayer = getPlayerTag(event.getPlayer());
			taggedPlayer.clearNearbyPlayers();
			taggedPlayer.updateLastRelocation();
		}).runASync();
	}
	
}

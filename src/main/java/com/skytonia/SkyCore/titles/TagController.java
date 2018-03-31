package com.skytonia.SkyCore.titles;

import com.skytonia.SkyCore.SkyCore;
import com.skytonia.SkyCore.cosmetics.pets.PetUtil;
import com.skytonia.SkyCore.util.RunnableShorthand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSpawnTrackerEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
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
			for(Iterator<TaggedPlayer> tagItr = entityTagMap.values().iterator();tagItr.hasNext();)
			{
				TaggedPlayer taggedPlayer = tagItr.next();
				
				if(!taggedPlayer.update())
				{
					tagItr.remove();
				}
			}
			
		}).runTimerASync(10, 10);
	}

	@EventHandler
	public void onPlayerTrackEvent(PlayerSpawnTrackerEvent event)
	{
		TaggedPlayer taggedPlayer = getPlayerTag(event.getPlayer());
		if(taggedPlayer == null)
		{
			return;
		}

		if(event.isTracked())
		{
			taggedPlayer.addNearbyPlayer(event.getVisiblePlayer());
		}
		else
		{
			taggedPlayer.hideNearbyPlayer(event.getVisiblePlayer());
		}
	}
	
	//
	
	public void setPlayerTagStatus(Player player, boolean hidden)
	{
		getPlayerTag(player).setHideTags(hidden);
	}

	public void setPlayerVisibility(Player player, Player target, Boolean visibilityStatus)
	{
		TaggedPlayer taggedTarget = getPlayerTag(target);
		ComparisonPlayer comparisonPlayer = taggedTarget.getNearbyPlayer(target.getUniqueId());
		if(comparisonPlayer != null)
		{
			comparisonPlayer.setForcedVisibility(visibilityStatus);
			taggedTarget.update(Collections.singletonList(comparisonPlayer));
		}
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
		RunnableShorthand.forPlugin(plugin).with(() ->
		{
			TaggedPlayer taggedPlayer = entityTagMap.get(event.getPlayer().getUniqueId());
			if(taggedPlayer != null)
			{
				taggedPlayer.setSneaking(/*!*/event.getPlayer().isSneaking());
			}
		}).runASync();
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		RunnableShorthand.forPlugin(plugin).with(() ->
		{
			final UUID playerUUID = event.getPlayer().getUniqueId();
			for(Map.Entry<UUID, TaggedPlayer> entry : entityTagMap.entrySet())
			{
				if(entry.getKey().equals(playerUUID))
				{
					entry.getValue().setOnline(false);
				}
				else
				{
					entry.getValue().removeNearbyPlayer(event.getPlayer());
				}
			}
		}).runASync();
	}

	@EventHandler
	public void onPlayerWorldChange(PlayerChangedWorldEvent event)
	{
		RunnableShorthand.forPlugin(plugin).with(() ->
		{
			final World worldFrom = event.getFrom();
			//Remove this player from all player tags in the world they've come from.
			for(Map.Entry<UUID, TaggedPlayer> entry : entityTagMap.entrySet())
			{
				if(entry.getValue().getEntity().getBukkitEntity().getWorld() == worldFrom)
				{
					entry.getValue().removeNearbyPlayer(event.getPlayer());
				}
			}
		}).runASync();
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
	
}

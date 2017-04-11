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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
	
	private final Map<UUID, TaggedPlayer> playerTagMap = new HashMap<>();
	
	TagController()
	{
		//Pre-Register our DataWatchers on the main thread
		PetUtil.getDefaultWatcher(Bukkit.getWorlds().get(0), EntityType.AREA_EFFECT_CLOUD);
		PetUtil.getDefaultWatcher(Bukkit.getWorlds().get(0), EntityType.SNOWBALL);
		
		SkyCore.getPluginInstance().getServer().getPluginManager().registerEvents(this, SkyCore.getPluginInstance());
		
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
				
				/*try
				{
					taggedPlayer.setLine(1, "§7Hours: §f" + 420);
					
					/*taggedPlayer.setLine(2, "aaaa");
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
				}*/
				
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
				
				taggedPlayer.update();
				
				if(!taggedPlayer.getPlayer().getBukkitEntity().isOnline())
				{
					playerTagMap.remove(taggedPlayer.getPlayer().getUniqueID());
				}
			}
		}).runTimerASync(10, 10);
	}
	
	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event)
	{
		playerTagMap.get(event.getPlayer().getUniqueId()).setSneaking(!event.getPlayer().isSneaking());
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		playerTagMap.get(event.getPlayer().getUniqueId()).setAllNearbyDirty(DirtyPlayerType.REMOVE);
	}
	
}

package com.skytonia.SkyCore.cosmetics.objects;

import com.skytonia.SkyCore.cosmetics.CosmeticThread;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Chris Brown (OhBlihv) on 6/08/2016.
 */
public class ActiveCosmetic
{
	
	private final CopyOnWriteArraySet<Player> nearbyPlayers = new CopyOnWriteArraySet<>();
	
	@Getter
	final BaseCosmetic cosmetic;
	
	@Getter
	private final Player activatingPlayer;
	private final Location staticLocation;
	
	private final CosmeticExpiry expiryAction;
	
	//Variables
	@Getter
	final int viewDistance;
	
	public final long endAtTick;
	
	public final int updateRate;
	
	public ActiveCosmetic(BaseCosmetic cosmetic, Player activatingPlayer, int viewDistance)
	{
		this(cosmetic, activatingPlayer, viewDistance, 0L);
	}
	
	public ActiveCosmetic(BaseCosmetic cosmetic, Player activatingPlayer, int viewDistance, long endAtTick)
	{
		this(cosmetic, activatingPlayer, null, null, viewDistance, endAtTick);
	}
	
	public ActiveCosmetic(BaseCosmetic cosmetic, Location staticLocation, int viewDistance, long endAtTick)
	{
		this(cosmetic, null, staticLocation, null, viewDistance, endAtTick);
	}
	
	public ActiveCosmetic(BaseCosmetic cosmetic, Player activatingPlayer, CosmeticExpiry expiryAction, int viewDistance, long endAtTick)
	{
		this(cosmetic, activatingPlayer, null, expiryAction, viewDistance, endAtTick);
	}
	
	public ActiveCosmetic(BaseCosmetic cosmetic, Location staticLocation, CosmeticExpiry expiryAction, int viewDistance, long endAtTick)
	{
		this(cosmetic, null, staticLocation, expiryAction, viewDistance, endAtTick);
	}
	
	public ActiveCosmetic(BaseCosmetic cosmetic,
	                      Player activatingPlayer, Location staticLocation, CosmeticExpiry expiryAction, int viewDistance, long endAtTick)
	{
		this.cosmetic = cosmetic;
		
		this.activatingPlayer = activatingPlayer;
		this.staticLocation = staticLocation;
		
		this.viewDistance = viewDistance;
		if(endAtTick > 0L)
		{
			this.endAtTick = CosmeticThread.getInstance().getCurrentTick() + endAtTick;
		}
		else
		{
			this.endAtTick = 0L;
		}
		
		this.updateRate = cosmetic.getUpdateRate();
		
		this.expiryAction = expiryAction;
		
		//Add the player to their own nearby players list to ensure they can view their own cosmetic
		if(activatingPlayer != null)
		{
			addNearbyPlayer(activatingPlayer);
		}
		
		//Trigger a nearby players update early
		updateNearbyPlayers();
	}
	
	private Location getLocation()
	{
		if(activatingPlayer == null)
		{
			return staticLocation;
		}
		else
		{
			return activatingPlayer.getLocation();
		}
	}
	
	public void onRemove()
	{
		if(expiryAction != null)
		{
			expiryAction.expire(this);
		}
		
		cosmetic.removeCosmetic();
	}
	
	public void remove()
	{
		CosmeticThread.getInstance().removeCosmetic(this);
	}
	
	/**
	 * Used for temporary cosmetics (that may last for a couple of seconds or a single tick)
	 * @param tick Tick the CosmeticThread is currently at
	 * @return Removal status
	 */
	public boolean shouldRemove(long tick)
	{
		return isTemporary() && tick >= endAtTick;
	}
	
	public boolean isTemporary()
	{
		return endAtTick > 0;
	}
	
	public boolean canUpdateNearbyPlayers(long ticks)
	{
		return ticks % cosmetic.getUpdateRate() == 0;
	}
	
	public void updateNearbyPlayers()
	{
		Location cosmeticLocation = getLocation();
		
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(player == null || player == activatingPlayer)
			{
				continue;
			}
			
			boolean inRange = true;
			
			Location playerLocation = player.getLocation();
			
			if(!player.isOnline() || cosmeticLocation.getWorld() != playerLocation.getWorld() || getDistance(cosmeticLocation, playerLocation) > viewDistance)
			{
				inRange = false;
			}
			
			//Create this for insertion and contains checks
			if(inRange)
			{
				addNearbyPlayer(player);
			}
			//Remove the player if
			else if(nearbyPlayers.contains(player))
			{
				removeNearbyPlayer(player);
			}
		}
	}
	
	public void addNearbyPlayer(Player player)
	{
		if(cosmetic.showToNearbyPlayer(player))
		{
			nearbyPlayers.add(player);
		}
	}
	
	public void removeNearbyPlayer(Player player)
	{
		if(cosmetic.removeFromNearbyPlayer(player))
		{
			nearbyPlayers.remove(player);
		}
	}
	
	private static int getDistance(Location location, Location comparedLocation)
	{
		int blockX = location.getBlockX(), comparedBlockX = comparedLocation.getBlockX(),
			blockZ = location.getBlockZ(), comparedBlockZ = comparedLocation.getBlockZ();
		
		double squaredDistance = NumberConversions.square(blockX - comparedBlockX) + NumberConversions.square(blockZ - comparedBlockZ);
		
		return (int) Math.sqrt(squaredDistance);
	}
	
	public void onTick(long tick)
	{
		if(nearbyPlayers == null || nearbyPlayers.isEmpty())
		{
			return;
		}
		
		cosmetic.onTick(tick, getLocation(), nearbyPlayers);
	}
	
	@Override
	public boolean equals(Object object)
	{
		return object instanceof ActiveCosmetic &&
			       (activatingPlayer != null ? (activatingPlayer == ((ActiveCosmetic) object).activatingPlayer) :
				                               (staticLocation == ((ActiveCosmetic) object).staticLocation)) &&
			       cosmetic.equals(((ActiveCosmetic) object).cosmetic);
	}
	
}

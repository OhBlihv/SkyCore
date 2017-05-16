package com.skytonia.SkyCore.titles;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerMount;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.skytonia.SkyCore.cosmetics.pets.PetUtil;
import com.skytonia.SkyCosmetics.SkyCosmetics;
import com.skytonia.SkyCosmetics.cosmetics.CosmeticType;
import com.skytonia.SkyCosmetics.cosmetics.types.WearableHeadCosmetic;
import com.skytonia.SkyCosmetics.cosmetics.wrappers.WrappedBasicCosmetic;
import com.skytonia.SkyCosmetics.storage.PlayerCosmetics;
import com.skytonia.SkyPerms.SkyPerms;
import lombok.Getter;
import net.minecraft.server.v1_9_R2.Entity;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Chris Brown (OhBlihv) on 4/10/2017.
 */
public class TaggedPlayer
{
	
	private static final int    SNEAKING_FLAG = 0x02,
								INVISIBILITY_FLAG = 0x20;
	
	private boolean sneaking = false;
	
	@Getter
	private boolean hideTags = false;
	
	private TagLine spacerLine = new TagLine(PetUtil.getNextEntityId(), "");
	private final List<TagLine> playerTags = new ArrayList<>();
	
	private final Map<UUID, ComparisonPlayer> nearbyPlayers = new ConcurrentHashMap<>();
	
	@Getter
	private final Entity entity;
	
	@Getter
	private boolean online = true;
	
	private long lastRelocation = -1;
	
	public TaggedPlayer(Entity player)
	{
		this.entity = player;
		
		if(entity instanceof EntityPlayer)
		{
			//TODO: Update this at a regular interval?
			//TODO: Remove after testing
			String prefix;
			try
			{
				prefix = SkyPerms.getInstance().getPermissionManager().getPermissionUser(player.getUniqueID()).getPrefix();
			}
			catch(NoClassDefFoundError e)
			{
				//Ignore
				prefix = null;
			}
			
			if(prefix == null || prefix.isEmpty() || prefix.equals("null"))
			{
				prefix = "§7";
			}
			
			setLine(0, prefix + player.getName());
		}
	}
	
	public UUID getUUID()
	{
		return entity.getUniqueID();
	}
	
	public double getHatHeight()
	{
		if(entity instanceof EntityPlayer)
		{
			try
			{
				PlayerCosmetics playerCosmetics = SkyCosmetics.getInstance().getPlayerManager().getCosmetics(entity.getUniqueID());
				if(playerCosmetics != null)
				{
					WrappedBasicCosmetic cosmetic = playerCosmetics.getActiveCosmetic(CosmeticType.WEARABLE_HEAD);
					if(cosmetic != null)
					{
						return ((WearableHeadCosmetic) cosmetic.getCosmetic()).getSpacerType().getHeight();
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return 0;
	}
	
	public void updateLastRelocation()
	{
		lastRelocation = System.currentTimeMillis();
	}
	
	public boolean isRecentlyRelocated()
	{
		if(lastRelocation != -1)
		{
			if(System.currentTimeMillis() - lastRelocation < 1000)
			{
				return true;
			} else
			{
				lastRelocation = -1;
			}
		}
		
		return false;
	}
	
	public void setSneaking(boolean sneaking)
	{
		this.sneaking = sneaking;
		
		setAllNearbyDirty(DirtyPlayerType.UPDATE);
		update(); //Force an update to ensure the sneaking lines up with their actual sneak status
	}
	
	public void setHideTags(boolean hideTags)
	{
		if(hideTags != isHideTags())
		{
			this.hideTags = hideTags;
			setAllNearbyDirty(DirtyPlayerType.ADD);
		}
	}
	
	public void setOnline(boolean online)
	{
		this.online = online;
		
		if(!online)
		{
			setAllNearbyDirty(DirtyPlayerType.REMOVE);
			update(); //Force an update to avoid the tags hovering for ~0.5 seconds
		}
	}
	
	public TagLine getLine(int lineNum)
	{
		return playerTags.get(lineNum);
	}
	
	public void setLine(int lineNum, String tagLine)
	{
		if(lineNum < 0)
		{
			throw new IllegalArgumentException("Out Of Range (" + lineNum + " < 0)");
		}
		
		if(lineNum >= playerTags.size())
		{
			playerTags.add(new TagLine(PetUtil.getNextEntityId(), tagLine));
			setAllNearbyDirty(DirtyPlayerType.ADD);
		}
		else
		{
			//Reuse old tag ID when overwriting line
			playerTags.set(lineNum, new TagLine(playerTags.get(lineNum).getTagId(), tagLine));
			setAllNearbyDirty(DirtyPlayerType.UPDATE);
		}
	}
	
	public TagLine removeLine(int lineNum)
	{
		if(lineNum < 0 || lineNum >= playerTags.size())
		{
			throw new IllegalArgumentException("Out Of Bounds (" + lineNum + " < 0 OR >= " + playerTags.size() + ")");
		}
		
		TagLine removedLine = playerTags.get(lineNum);
		if(removedLine != null)
		{
			removedLine.setDirtyPlayerType(DirtyPlayerType.REMOVE);
			setAllNearbyDirty(DirtyPlayerType.ADD);
		}
		
		return removedLine;
	}
	
	public List<TagLine> getLines()
	{
		return playerTags;
	}
	
	/**
	 *
	 * @param edit True If the lines were edited, False if the structure changed
	 */
	public void setAllNearbyDirty(DirtyPlayerType dirtyType)
	{
		for(ComparisonPlayer player : nearbyPlayers.values())
		{
			//All other types handle correctly even with dirty titles
			if(player.getDirtyPlayerType() == DirtyPlayerType.CLEAN)
			{
				player.setDirtyPlayerType(dirtyType);
			}
		}
	}
	
	public boolean addNearbyPlayer(Player player)
	{
		if(!nearbyPlayers.containsKey(player.getUniqueId()))
		{
			nearbyPlayers.put(player.getUniqueId(), new ComparisonPlayer(player));
			return true;
		}
		
		return false;
	}
	
	public void removeNearbyPlayer(Player player)
	{
		ComparisonPlayer nearbyPlayer = nearbyPlayers.get(player.getUniqueId());
		if(nearbyPlayer != null)
		{
			nearbyPlayer.setDirtyPlayerType(DirtyPlayerType.REMOVE);
		}
	}
	
	public void clearNearbyPlayers()
	{
		for(ComparisonPlayer nearbyPlayer : nearbyPlayers.values())
		{
			nearbyPlayer.setDirtyPlayerType(DirtyPlayerType.REMOVE);
		}
		
		update();
		
		nearbyPlayers.clear();
	}
	
	public boolean update()
	{
		//Avoid updating if there are no players that require it
		if(nearbyPlayers.isEmpty() || playerTags.isEmpty())
		{
			return true;
		}
		
		//Spawn Packets
		List<AbstractPacket> spawnPackets = new ArrayList<>();
		
		//Update Packets
		List<AbstractPacket> updatePackets = new ArrayList<>();
		
		//Mount Packets
		List<AbstractPacket> mountPackets = new ArrayList<>();
		
		//Destroy IDs/Packets
		List<Integer> tagIds = new ArrayList<>();
		
		List<TagLine> tagsToRemove = new ArrayList<>();
		
		int lineHeight = 3,
			contentLineNum = 0;
		//Thanks to cyberpwn for the following values
		final double amx = 0.375, amv = -0.161;
		
		boolean hasSpacer = false;
		int lastVehicleId = entity.getId();
		List<TagLine> visibleTags = new ArrayList<>();
		if(getHatHeight() > 0)
		{
			visibleTags.add(spacerLine);
			hasSpacer = true;
		}
		else
		{
			//Ensure the spacer gets destroyed if not used
			tagIds.add(spacerLine.getTagId());
		}
		visibleTags.addAll(playerTags);
		
		List<Integer> lastPassengers = new ArrayList<>();
		for(TagLine tagLine : visibleTags)
		{
			if(tagLine.getDirtyPlayerType() == DirtyPlayerType.REMOVE)
			{
				WrapperPlayServerEntityDestroy destroyPacket = new WrapperPlayServerEntityDestroy();
				
				destroyPacket.setEntityIds(new int[] {tagLine.getTagId()});
				
				//Ensure this goes out to updating players
				spawnPackets.add(destroyPacket);
				updatePackets.add(destroyPacket);
				
				tagsToRemove.add(tagLine);
			}
			else
			{
				if(!hideTags)
				{
					if(tagLine.getLineEntity().isAlive())
					{
						WrapperPlayServerSpawnEntityLiving spawnPacket = new WrapperPlayServerSpawnEntityLiving();
						
						spawnPacket.setEntityID(tagLine.getTagId());
						
						spawnPacket.setType(tagLine.getLineEntity());
						
						spawnPacket.setX(entity.locX);
						
						//AreaEffectClouds are only used on the first line. This spawns them on the right height
						double yHeight = entity.locY;
						switch(lineHeight)
						{
							//Spacer or initial
							case 3:
							{
								if(hasSpacer)
								{
									yHeight += ((++lineHeight * amx) + amv);
								}
								else
								{
									yHeight += ((++lineHeight * amx));
								}
								break;
							}
							case 4: yHeight += ((++lineHeight * amx)); break;
							case 5: yHeight += ((++lineHeight * amx)); break;
						}
						
						spawnPacket.setY(yHeight);
						spawnPacket.setZ(entity.locZ);
						
						WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
						
						metadataPacket.setEntityID(tagLine.getTagId());
						
						WrappedDataWatcher metadata = tagLine.getMetadata();
						
						if(sneaking)
						{
							metadata.setObject(0, (byte) SNEAKING_FLAG);
						}
						else
						{
							metadata.setObject(0, (byte) 0);
						}
						
						//Set Baby Rabbit
						metadata.setObject(11, contentLineNum == 0);
						
						metadataPacket.setMetadata(metadata.getWatchableObjects());
						spawnPacket.setMetadata(metadata);
						
						spawnPackets.add(spawnPacket);
						
						updatePackets.add(metadataPacket);
						
						contentLineNum++;
					}
					else
					{
						WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity();
						
						spawnPacket.setEntityID(tagLine.getTagId());
						
						int entityTypeId = tagLine.getLineEntity().getTypeId();
						switch(tagLine.getLineEntity())
						{
							case AREA_EFFECT_CLOUD: entityTypeId = 3; break;
							case SNOWBALL: entityTypeId = 61; break;
						}
						
						spawnPacket.setType(entityTypeId);
						
						spawnPacket.setX(entity.locX);
						spawnPacket.setY(entity.locY + ((++lineHeight * amx) + amv));
						spawnPacket.setZ(entity.locZ);
						
						spawnPackets.add(spawnPacket);
						
						WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
						
						metadataPacket.setEntityID(tagLine.getTagId());
						
						WrappedDataWatcher metadata = tagLine.getMetadata();
						
						if(sneaking)
						{
							metadata.setObject(0, (byte) SNEAKING_FLAG);
						}
						else
						{
							metadata.setObject(0, (byte) 0);
						}
						
						metadataPacket.setMetadata(metadata.getWatchableObjects());
						
						updatePackets.add(metadataPacket);
					}
					
					lastPassengers.add(tagLine.getTagId());
					
					if(!tagLine.getLineEntity().isAlive())
					{
						lastPassengers.add(tagLine.getTagId());
						
						mountPackets.add(getMountPacket(lastVehicleId, lastPassengers));
						
						lastVehicleId = tagLine.getTagId();
					}
				}
			}
			
			tagIds.add(tagLine.getTagId());
		}
		
		if(!lastPassengers.isEmpty())
		{
			mountPackets.add(getMountPacket(lastVehicleId, lastPassengers));
		}
		
		playerTags.removeAll(tagsToRemove);
		
		WrapperPlayServerEntityDestroy destroyPacket = new WrapperPlayServerEntityDestroy();
		{
			int[] destroyIds = new int[tagIds.size()];
			
			int i = 0;
			for(int destroyId : tagIds)
			{
				destroyIds[i++] = destroyId;
			}
			destroyPacket.setEntityIds(destroyIds);
		}
		
		boolean recentlyRelocated = isRecentlyRelocated();
		
		List<UUID> playersToRemove = new ArrayList<>();
		for(ComparisonPlayer nearbyPlayer : nearbyPlayers.values())
		{
			Player nearbyBukkitPlayer = nearbyPlayer.getPlayer();
			if(!isOnline() || !nearbyBukkitPlayer.isOnline())
			{
				nearbyPlayer.setDirtyPlayerType(DirtyPlayerType.REMOVE);
			}
			else if(recentlyRelocated)
			{
				nearbyPlayer.setDirtyPlayerType(DirtyPlayerType.ADD);
			}
			
			switch(nearbyPlayer.getDirtyPlayerType())
			{
				case ADD:
				{
					//Destroy any old titles before spawning in new ones
					destroyPacket.sendPacket(nearbyBukkitPlayer);
					
					for(AbstractPacket packet : spawnPackets)
					{
						packet.sendPacket(nearbyBukkitPlayer);
					}
					
					for(AbstractPacket packet : updatePackets)
					{
						packet.sendPacket(nearbyBukkitPlayer);
					}
					
					for(AbstractPacket packet : mountPackets)
					{
						packet.sendPacket(nearbyBukkitPlayer);
					}
					
					nearbyPlayer.setDirtyPlayerType(DirtyPlayerType.CLEAN);
					break;
				}
				case REMOVE:
				{
					destroyPacket.sendPacket(nearbyBukkitPlayer);
					playersToRemove.add(nearbyBukkitPlayer.getUniqueId());
					break;
				}
				case UPDATE:
				{
					for(AbstractPacket packet : updatePackets)
					{
						packet.sendPacket(nearbyBukkitPlayer);
					}
					nearbyPlayer.setDirtyPlayerType(DirtyPlayerType.CLEAN);
					break;
				}
			}
		}
		
		nearbyPlayers.keySet().removeAll(playersToRemove);
		
		return isOnline();
	}
	
	public AbstractPacket getMountPacket(int lastVehicleId, List<Integer> passengerIds)
	{
		WrapperPlayServerMount mountPacket = new WrapperPlayServerMount();
		
		mountPacket.setEntityID(lastVehicleId);
		
		int[] passengerArr = new int[passengerIds.size()];
		int i = 0;
		for(int passengerId : passengerIds)
		{
			passengerArr[i++] = passengerId;
		}
		
		mountPacket.setPassengerIds(passengerArr);
		
		passengerIds.clear();
		
		return mountPacket;
	}
	
}

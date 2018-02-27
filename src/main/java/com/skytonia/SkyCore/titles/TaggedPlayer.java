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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumMap;
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
	private String prefixIcon = "";
	public void setPrefixIcon(String prefixIcon)
	{
		//Avoid null. Add an empty string instead if icon is not present.
		if(prefixIcon == null)
		{
			prefixIcon = "";
		}

		if(this.prefixIcon == null || this.prefixIcon.isEmpty())
		{
			//Add as new
			setLine(0, prefixIcon + getLine(0).getText());
		}
		else
		{
			//Replace the old icon
			setLine(0, prefixIcon + getLine(0).getText().substring(1));
		}

		this.prefixIcon = prefixIcon;
	}
	
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
				//TODO: Cache?
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
			catch(NoClassDefFoundError e)
			{
				//SkyCosmetics not installed.
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return 0;
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

	public ComparisonPlayer getNearbyPlayer(UUID uuid)
	{
		return nearbyPlayers.get(uuid);
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
	
	public boolean update()
	{
		//Avoid updating if there are no players that require it
		if (nearbyPlayers.isEmpty() || playerTags.isEmpty())
		{
			return true;
		}

		return update(nearbyPlayers.values());
	}

	public boolean update(Collection<ComparisonPlayer> players)
	{
		//Spawn Packets
		Deque<AbstractPacket> spawnPackets = new ArrayDeque<>();
		
		//Update Packets
		Deque<AbstractPacket> updatePackets = new ArrayDeque<>();
		
		//Mount Packets
		Deque<AbstractPacket> mountPackets = new ArrayDeque<>();
		
		//Destroy IDs/Packets
		Deque<Integer> tagIds = new ArrayDeque<>();

		Deque<TagLine> tagsToRemove = new ArrayDeque<>();
		
		int lineHeight = 3,
			contentLineNum = 0;
		//Thanks to cyberpwn for the following values
		final double amx = 0.375, amv = -0.161;
		
		boolean hasSpacer = false;
		int lastVehicleId = entity.getId();
		Deque<TagLine> visibleTags = new ArrayDeque<>();
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

		Map<DirtyPlayerType, Deque<ComparisonPlayer>> playerStatusMap = new EnumMap<>(DirtyPlayerType.class);
		playerStatusMap.put(DirtyPlayerType.ADD, new ArrayDeque<>());
		playerStatusMap.put(DirtyPlayerType.REMOVE, new ArrayDeque<>());
		playerStatusMap.put(DirtyPlayerType.UPDATE, new ArrayDeque<>());
		playerStatusMap.put(DirtyPlayerType.CLEAN, new ArrayDeque<>());

		boolean anyVisibleTags = !hideTags;
		//Search for at least one player who can see this player's tags
		//to ensure they are not being generated for no use.
		for(ComparisonPlayer player : players)
		{
			if(player.getForcedVisibility())
			{
				anyVisibleTags = true;
			}

			playerStatusMap.get(player.getDirtyPlayerType()).add(player);
		}
		
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
				if(anyVisibleTags)
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
									break;
								}
							}
							case 4:
							case 5:
							{
								yHeight += ((++lineHeight * amx));
								break;
							}
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

		for(Map.Entry<DirtyPlayerType, Deque<ComparisonPlayer>> entry : playerStatusMap.entrySet())
		{
			switch(entry.getKey())
			{
				case ADD:
				{
					for(ComparisonPlayer player : entry.getValue())
					{
						//Tags hidden by default
						if(hideTags)
						{
							//Player overridden visibility
							if(player.getForcedVisibility() == null || !player.getForcedVisibility())
							{
								continue;
							}
						}
						else
						{
							//Player overridden invisibility
							if(player.getForcedVisibility() != null && !player.getForcedVisibility())
							{
								continue;
							}
						}

						//Destroy any old titles before spawning in new ones
						player.sendPacket(destroyPacket);

						player.sendPackets(spawnPackets, updatePackets, mountPackets);

						if(!isOnline() || !player.isOnline())
						{
							player.setDirtyPlayerType(DirtyPlayerType.REMOVE);
						}
						else
						{
							player.setDirtyPlayerType(DirtyPlayerType.CLEAN);
						}
					}
					break;
				}
				case REMOVE:
				{
					Deque<UUID> toRemovePlayers = new ArrayDeque<>();
					for(ComparisonPlayer player : entry.getValue())
					{
						player.sendPacket(destroyPacket);
						toRemovePlayers.add(player.getPlayer().getUniqueId());
					}

					nearbyPlayers.keySet().removeAll(toRemovePlayers);
					break;
				}
				case UPDATE:
				{
					for(ComparisonPlayer player : entry.getValue())
					{
						player.sendPackets(updatePackets);
						player.setDirtyPlayerType(DirtyPlayerType.CLEAN);
					}
					break;
				}
			}
		}
		
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

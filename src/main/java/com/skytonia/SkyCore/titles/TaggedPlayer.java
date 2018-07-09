package com.skytonia.SkyCore.titles;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerMount;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.skytonia.SkyCore.cosmetics.pets.PetUtil;
import com.skytonia.SkyPerms.SkyPerms;
import lombok.Getter;
import lombok.Setter;
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
	
	public TaggedPlayer(Entity player)
	{
		this.entity = player;
		
		if(entity instanceof EntityPlayer)
		{
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

	@Getter
	@Setter
	private double hatHeight = 0;
	
	public void setSneaking(boolean sneaking)
	{
		this.sneaking = sneaking;
		
		setAllNearbyDirty(DirtyPlayerType.UPDATE);
		update(); //Force an update to ensure the sneaking lines up with their actual sneak status
	}
	
	public void setHideTags(boolean hideTags)
	{
		if(hideTags != this.hideTags)
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

	public void showToNonVisiblePlayers()
	{
		for(ComparisonPlayer comparisonPlayer : nearbyPlayers.values())
		{
			switch(comparisonPlayer.getDirtyPlayerType())
			{
				case INDIV_HIDDEN:
				{
					//Re-add if not visible
					comparisonPlayer.setDirtyPlayerType(DirtyPlayerType.ADD);
					break;
				}
				case INDIV_REMOVE_QUEUE:
				{
					//Set clean (no changes required) if player was ready to be marked INDIV_HIDDEN
					comparisonPlayer.setDirtyPlayerType(DirtyPlayerType.CLEAN);
				}
			}
		}
	}

	public void hideFromVisiblePlayers()
	{
		for(ComparisonPlayer comparisonPlayer : nearbyPlayers.values())
		{
			switch(comparisonPlayer.getDirtyPlayerType())
			{
				case INDIV_HIDDEN:
				case INDIV_REMOVE_QUEUE:
					break;
				default:
					comparisonPlayer.setDirtyPlayerType(DirtyPlayerType.INDIV_REMOVE_QUEUE);
			}
		}
	}

	/**--
	 *
	 * @param edit True If the lines were edited, False if the structure changed
	 */
	public void setAllNearbyDirty(DirtyPlayerType dirtyType)
	{
		for(ComparisonPlayer player : nearbyPlayers.values())
		{
			//All other types handle correctly even with dirty titles
			//NOTE: Force remove all dirty types.
			//if(player.getDirtyPlayerType() == DirtyPlayerType.CLEAN)
			{
				player.setDirtyPlayerType(dirtyType);
			}
		}
	}

	public ComparisonPlayer getNearbyPlayer(UUID uuid)
	{
		return nearbyPlayers.get(uuid);
	}

	public void setPlayerStatus(Player player, DirtyPlayerType dirtyPlayerType)
	{
		ComparisonPlayer comparisonPlayer = getNearbyPlayer(player.getUniqueId());
		if(comparisonPlayer == null)
		{
			nearbyPlayers.put(player.getUniqueId(), comparisonPlayer = new ComparisonPlayer(player));
		}

		comparisonPlayer.setDirtyPlayerType(dirtyPlayerType);
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

	public Collection<ComparisonPlayer> getAllNearbyPlayers()
	{
		return nearbyPlayers.values();
	}

	public void removeNearbyPlayer(Player player)
	{
		//nearbyPlayers.remove(player.getUniqueId());
		ComparisonPlayer comparisonPlayer = getNearbyPlayer(player.getUniqueId());
		if(comparisonPlayer != null)
		{
			//Set remove for next update tick to ensure removal packets go through
			comparisonPlayer.setDirtyPlayerType(DirtyPlayerType.REMOVE);
		}
	}
	
	public boolean update()
	{
		//Avoid updating if there are no players that require it
		//But force an update if the player is offline
		if (online && (nearbyPlayers.isEmpty() || playerTags.isEmpty()))
		{
			return true;
		}

		return update(nearbyPlayers.values());
	}

	private int tick = 0;

	public boolean update(Collection<ComparisonPlayer> players)
	{
		boolean canRemove = false;
		if(!isOnline())
		{
			//BUtil.log("Is offline - forcing all to remove");
			setAllNearbyDirty(DirtyPlayerType.REMOVE);
			canRemove = true;
		}

		Deque<AbstractPacket>
			spawnPackets = new ArrayDeque<>(),  //Spawn Packets
			updatePackets = new ArrayDeque<>(), //Update Packets
			mountPackets = new ArrayDeque<>();  //Mount Packets
		
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

		boolean anyVisibleTags = !hideTags;
		//Search for at least one player who can see this player's tags
		//to ensure they are not being generated for no use.
		for(ComparisonPlayer player : players)
		{
			DirtyPlayerType dirtyPlayerType = player.getDirtyPlayerType();

			if(!anyVisibleTags && dirtyPlayerType == DirtyPlayerType.ADD)
			{
				continue; //Don't send updates to a player who can't see them
			}

			if(dirtyPlayerType == DirtyPlayerType.INDIV_REMOVE_QUEUE ||
				entity.getWorld().getWorld() != player.getPlayer().getWorld())
			{
				//Enforce all as 'remove'
				dirtyPlayerType = DirtyPlayerType.REMOVE;
			}
			else if(dirtyPlayerType == DirtyPlayerType.INDIV_ADD_QUEUE)
			{
				//Group under 'ADD'
				dirtyPlayerType = DirtyPlayerType.ADD;
			}

			Deque<ComparisonPlayer> typeQueue = playerStatusMap.get(dirtyPlayerType);
			if(typeQueue == null)
			{
				playerStatusMap.put(dirtyPlayerType, (typeQueue = new ArrayDeque<>()));
			}

			typeQueue.add(player);
		}

		//No visible tags, but the ADD group has players
		//These players are specifically receiving tags against the hideTags guideline
		if(!anyVisibleTags && playerStatusMap.get(DirtyPlayerType.ADD) != null)
		{
			//At least one player is scheduled to receive personalised tags
			anyVisibleTags = true;
		}

		//Ensure we aren't ticking for no player updates
		boolean isEmpty = false;
		{
			int emptySets = 0;
			for(Map.Entry<DirtyPlayerType, Deque<ComparisonPlayer>> entry : playerStatusMap.entrySet())
			{
				//Avoid populating add packets if tags are hidden anyway
				if (entry.getKey() == DirtyPlayerType.CLEAN || entry.getValue().isEmpty())
				{
					emptySets++;
				}
			}

			if(emptySets == (playerStatusMap.size()))
			{
				if(++tick % 2 == 0)
				{
					return !canRemove;
				}

				isEmpty = true;
			}
		}
		
		List<Integer> lastPassengers = new ArrayList<>();
		for(TagLine tagLine : visibleTags)
		{
			if(tagLine.getDirtyPlayerType() == DirtyPlayerType.REMOVE || !isOnline())
			{
				WrapperPlayServerEntityDestroy destroyPacket = new WrapperPlayServerEntityDestroy();
				
				destroyPacket.setEntityIds(new int[] {tagLine.getTagId()});
				
				//Ensure this goes out to updating players
				spawnPackets.add(destroyPacket);
				updatePackets.add(destroyPacket);
				
				tagsToRemove.add(tagLine);
			}
			else if(anyVisibleTags && !isEmpty)
			{
				if(tagLine.getLineEntity().isAlive())
				{
					WrapperPlayServerSpawnEntityLiving spawnPacket = new WrapperPlayServerSpawnEntityLiving();

					spawnPacket.setEntityID(tagLine.getTagId());
					spawnPacket.setType(tagLine.getLineEntity());
					spawnPacket.setX(entity.locX);
					spawnPacket.setZ(entity.locZ);

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

					WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();

					metadataPacket.setEntityID(tagLine.getTagId());

					WrappedDataWatcher metadata = tagLine.getMetadata();

					//Match player's sneaking status
					metadata.setObject(0, (byte) (sneaking ? SNEAKING_FLAG : 0));

					//Set Baby Rabbit
					metadata.setObject(11, contentLineNum == 0);

					metadataPacket.setMetadata(metadata.getWatchableObjects());
					spawnPacket.setMetadata(metadata);

					//Add all packets together
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

					WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();

					metadataPacket.setEntityID(tagLine.getTagId());

					WrappedDataWatcher metadata = tagLine.getMetadata();

					metadata.setObject(0, (byte) (sneaking ? SNEAKING_FLAG : 0));
					metadataPacket.setMetadata(metadata.getWatchableObjects());

					//Add all packets together
					spawnPackets.add(spawnPacket);
					updatePackets.add(metadataPacket);
				}

				//Build mounted/passenger entity stack regardless of empty status
				lastPassengers.add(tagLine.getTagId());

				if(!tagLine.getLineEntity().isAlive())
				{
					mountPackets.add(getMountPacket(lastVehicleId, lastPassengers));

					lastVehicleId = tagLine.getTagId();
				}
			}
			
			tagIds.add(tagLine.getTagId());
		}

		if(!lastPassengers.isEmpty())
		{
			mountPackets.add(getMountPacket(lastVehicleId, lastPassengers));
		}

		try
		{
			playerTags.removeAll(tagsToRemove);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			//Ignore
		}
		
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
			if(entry.getValue().isEmpty())
			{
				continue;
			}

			/*if(entry.getKey() != DirtyPlayerType.CLEAN)
			{
				BUtil.log("STATUS (" + entry.getKey() + ") for (" + entity.getName() + ") as (" + entry.getValue() + ")");
			}*/

			switch(entry.getKey())
			{
				case ADD:
				{
					for(ComparisonPlayer player : entry.getValue())
					{
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

						//Set the player as 'not visible'
						if(player.getDirtyPlayerType() == DirtyPlayerType.INDIV_REMOVE_QUEUE)
						{
							player.setDirtyPlayerType(DirtyPlayerType.INDIV_HIDDEN);
						}
						else
						{
							toRemovePlayers.add(player.getPlayer().getUniqueId());
						}
					}

					nearbyPlayers.keySet().removeAll(toRemovePlayers);
					break;
				}
				case UPDATE:
				{
					for(ComparisonPlayer player : entry.getValue())
					{
						//Destroy any old titles before spawning in new ones
						player.sendPacket(destroyPacket);

						player.sendPackets(updatePackets);
						player.setDirtyPlayerType(DirtyPlayerType.CLEAN);
					}

					break;
				}
			}

			if(entry.getKey() != DirtyPlayerType.REMOVE)
			{
				for(ComparisonPlayer player : entry.getValue())
				{
					player.sendPackets(mountPackets);
				}
			}
		}

		return !canRemove;
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

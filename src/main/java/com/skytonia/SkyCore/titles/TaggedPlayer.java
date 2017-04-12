package com.skytonia.SkyCore.titles;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerMount;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.skytonia.SkyCore.cosmetics.pets.PetUtil;
import com.skytonia.SkyCore.redis.RedisManager;
import com.skytonia.SkyPerms.SkyPerms;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Chris Brown (OhBlihv) on 4/10/2017.
 */
public class TaggedPlayer
{
	
	private static final int    SNEAKING_FLAG = 0x02,
								INVISIBILITY_FLAG = 0x20;
	
	private boolean sneaking = false;
	
	private TagLine spacerLine = new TagLine(PetUtil.getNextEntityId(), "");
	private final List<TagLine> playerTags = new ArrayList<>();
	
	private final Map<UUID, ComparisonPlayer> nearbyPlayers = new HashMap<>();
	
	@Getter
	private final EntityPlayer player;
	
	@Getter
	@Setter
	private boolean online = true;
	
	public TaggedPlayer(EntityPlayer player)
	{
		this.player = player;
		
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
	
	public double getHatHeight()
	{
		//TODO: Read off worn hat
		if(RedisManager.getServerName().contains("hub"))
		{
			//Hubs contain rabbit ears for now
			return 1;
		}
		
		return 0;
	}
	
	public void setSneaking(boolean sneaking)
	{
		this.sneaking = sneaking;
		
		setAllNearbyDirty(DirtyPlayerType.UPDATE);
		update(); //Force an update to ensure the sneaking lines up with their actual sneak status
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
		
		//Destroy IDs/Packets
		List<Integer> tagIds = new ArrayList<>();
		
		List<TagLine> tagsToRemove = new ArrayList<>();
		
		int lineHeight = 3;
		//Thanks to cyberpwn for the following values
		final double amx = 0.375, amv = -0.161;
		
		int lastVehicleId = player.getId();
		List<TagLine> visibleTags = new ArrayList<>();
		if(getHatHeight() > 0)
		{
			visibleTags.add(spacerLine);
		}
		else
		{
			//Ensure the spacer gets destroyed if not used
			tagIds.add(spacerLine.getTagId());
		}
		visibleTags.addAll(playerTags);
		
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
				WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity();
				
				spawnPacket.setEntityID(tagLine.getTagId());
				
				int entityTypeId = tagLine.getLineEntity().getTypeId();
				switch(tagLine.getLineEntity())
				{
					case AREA_EFFECT_CLOUD: entityTypeId = 3; break;
					case SNOWBALL: entityTypeId = 61; break;
				}
				
				spawnPacket.setType(entityTypeId);
				
				spawnPacket.setX(player.locX);
				spawnPacket.setY(player.locY + ((++lineHeight * amx) + amv));
				spawnPacket.setZ(player.locZ);
				
				spawnPackets.add(spawnPacket);
				
				WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
				
				metadataPacket.setEntityID(tagLine.getTagId());
				
				{
					List<WrappedWatchableObject> metadataObjects = tagLine.getMetadata().getWatchableObjects();
					
					WrappedWatchableObject entityFlags = metadataObjects.get(0);
					
					if(sneaking)
					{
						entityFlags.setValue((byte) SNEAKING_FLAG);
					}
					else
					{
						entityFlags.setValue((byte) 0);
					}
					
					metadataPacket.setMetadata(metadataObjects);
				}
				
				updatePackets.add(metadataPacket);
				
				WrapperPlayServerMount mountPacket = new WrapperPlayServerMount();
				
				mountPacket.setEntityID(lastVehicleId);
				mountPacket.setPassengerIds(new int[] {tagLine.getTagId()});
				
				spawnPackets.add(mountPacket);
				
				lastVehicleId = tagLine.getTagId();
			}
			
			tagIds.add(tagLine.getTagId());
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
		
		//TODO: Force respawn after a few ticks?
		List<UUID> playersToRemove = new ArrayList<>();
		for(ComparisonPlayer nearbyPlayer : nearbyPlayers.values())
		{
			Player nearbyBukkitPlayer = nearbyPlayer.getPlayer();
			if(!isOnline() || !nearbyBukkitPlayer.isOnline())
			{
				nearbyPlayer.setDirtyPlayerType(DirtyPlayerType.REMOVE);
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
	
}

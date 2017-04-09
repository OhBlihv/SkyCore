package com.skytonia.SkyCore.cosmetics.pets.entities;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerMount;
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMoveLook;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.skytonia.SkyCore.cosmetics.pets.PetUtil;
import lombok.Getter;
import net.minecraft.server.MathHelper;
import net.minecraft.server.v1_9_R2.Entity;
import net.minecraft.server.v1_9_R2.EntityLiving;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Chris Brown (OhBlihv) on 4/9/2017.
 */
public class FakeEntity
{
	
	private final EntityType entityType;
	
	@Getter
	private final int entityId = PetUtil.getNextEntityId();
	private final UUID entityUUID = UUID.randomUUID();
	
	private int[] passengerIds = new int[] {};
	
	private World currentWorld;
	
	private WrappedDataWatcher metadata = null;
	
	@Getter
	private double  currentX = 0,
					currentY = 0,
					currentZ = 0,
					currentYaw = 0,
					currentPitch = 0;
	
	public FakeEntity(EntityType entityType, Location location)
	{
		if(entityType == null)
		{
			throw new IllegalArgumentException("Unknown Entity Type Provided.");
		}
		
		this.entityType = entityType;
		
		updateCurrentLocation(location);
	}
	
	public WrappedDataWatcher getMetadata()
	{
		return PetUtil.getDefaultWatcher(currentWorld, entityType);
	}
	
	public AbstractPacket getSpawnPacket()
	{
		if(metadata == null)
		{
			metadata = getMetadata();
		}
		
		AbstractPacket abstractPacket;
		
		if(EntityLiving.class.isAssignableFrom(entityType.getEntityClass()))
		{
			WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
			
			packet.setMetadata(metadata);
			
			packet.setEntityID(entityId);
			packet.setUniqueId(entityUUID);
			
			packet.setType(entityType);
			
			packet.setX(currentX);
			packet.setY(currentY);
			packet.setZ(currentZ);
			
			packet.setYaw((float) currentYaw);
			packet.setPitch((float) currentPitch);
			packet.setHeadPitch((float) currentPitch);
			
			packet.setVelocityX(0);
			packet.setVelocityY(0);
			packet.setVelocityZ(0);
			
			abstractPacket = packet;
		}
		else if(Entity.class.isAssignableFrom(entityType.getEntityClass()))
		{
			WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity();
			
			packet.setEntityID(entityId);
			packet.setUniqueId(entityUUID);
			
			packet.setType(entityType.getTypeId());
			
			packet.setX(currentX);
			packet.setY(currentY);
			packet.setZ(currentZ);
			
			packet.setYaw((float) currentYaw);
			packet.setPitch((float) currentPitch);
			
			abstractPacket = packet;
		}
		else
		{
			throw new IllegalArgumentException("Unsupported Entity Type '" + entityType + "' of class " + entityType.getEntityClass().getSimpleName());
		}
		
		return abstractPacket;
	}
	
	public AbstractPacket getDestroyPacket()
	{
		if(EntityLiving.class.isAssignableFrom(entityType.getEntityClass()) ||
			   Entity.class.isAssignableFrom(entityType.getEntityClass()))
		{
			WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
			
			packet.setEntityIds(new int[] {entityId});
			
			return packet;
		}
		else
		{
			throw new IllegalArgumentException("Unsupported Entity Type '" + entityType + "' of class " + entityType.getEntityClass().getSimpleName());
		}
	}
	
	public Location getLocation()
	{
		return new Location(currentWorld, currentX, currentY, currentZ, (float) currentYaw, (float) currentPitch);
	}
	
	public World getWorld()
	{
		return currentWorld;
	}
	
	protected void updateCurrentLocation(Location location)
	{
		updateCurrentLocation(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}
	
	protected void updateCurrentLocation(World world, double x, double y, double z, float yaw, float pitch)
	{
		currentWorld = world;
		
		currentX = x;
		currentY = y;
		currentZ = z;
		
		currentYaw = yaw;
		currentPitch = pitch;
	}
	
	private static final long MAX_MOVE_PACKET_DISTANCE = 32768L;
	private static final long MIN_MOVE_PACKET_DISTANCE = 0 - MAX_MOVE_PACKET_DISTANCE;
	
	public AbstractPacket moveEntity(double x, double y, double z, float yaw, float pitch)
	{
		AbstractPacket abstractPacket = null;
		
		//Correct move distance numbers for packets
		double  deltaX = MathHelper.d((currentX - x) * 4096.0D),
				deltaY = MathHelper.d((currentY - y) * 4096.0D),
				deltaZ = MathHelper.d((currentZ - z) * 4096.0D);
		
		//Entity Relative Move
		if (    deltaX >= MIN_MOVE_PACKET_DISTANCE && deltaX < MAX_MOVE_PACKET_DISTANCE &&
			    deltaY >= MIN_MOVE_PACKET_DISTANCE && deltaY < MAX_MOVE_PACKET_DISTANCE &&
			    deltaZ >= MIN_MOVE_PACKET_DISTANCE && deltaZ < MAX_MOVE_PACKET_DISTANCE)
		{
			//Lazily always send the entity look/move packet
			WrapperPlayServerRelEntityMoveLook packet = new WrapperPlayServerRelEntityMoveLook();
			
			packet.setEntityID(entityId);
			
			packet.setDx(deltaX);
			packet.setDy(deltaY);
			packet.setDz(deltaZ);
			
			packet.setOnGround(true);
			packet.setYaw((float) currentYaw);
			packet.setPitch((float) currentPitch);
			
			abstractPacket = packet;
		}
			
		updateCurrentLocation(this.currentWorld, x, y, z, yaw, pitch);
		
		//Process teleport after we've updated the location for all
		if(abstractPacket == null)
		{
			abstractPacket = getTeleportPacket();
		}
		
		return abstractPacket;
	}
	
	public AbstractPacket getTeleportPacket()
	{
		WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport();
		
		packet.setEntityID(entityId);
		
		packet.setX(currentX);
		packet.setY(currentY);
		packet.setZ(currentZ);
		
		packet.setYaw((float) currentYaw);
		packet.setPitch((float) currentPitch);
		
		return packet;
	}
	
	public void addPassenger(int entityId)
	{
		passengerIds = Arrays.copyOf(passengerIds, passengerIds.length + 1);
		passengerIds[passengerIds.length - 1] = entityId;
	}
	
	public void removePassenger(int entityId)
	{
		int[] newPassengerArray = new int[passengerIds.length - 1];
		
		if(newPassengerArray.length > 0)
		{
			int newArrIdx = 0;
			for(int passengerId : passengerIds)
			{
				if(passengerId != entityId)
				{
					newPassengerArray[newArrIdx++] = passengerId;
				}
			}
		}
		
		this.passengerIds = newPassengerArray;
	}
	
	public WrapperPlayServerMount getPassengerPacket()
	{
		WrapperPlayServerMount packet = new WrapperPlayServerMount();
		
		packet.setEntityID(entityId);
		
		packet.setPassengerIds(passengerIds);
		
		return packet;
	}
	
}

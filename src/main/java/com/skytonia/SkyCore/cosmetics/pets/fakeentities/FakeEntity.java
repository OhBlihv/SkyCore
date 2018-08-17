package com.skytonia.SkyCore.cosmetics.pets.fakeentities;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerMount;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.skytonia.SkyCore.cosmetics.pets.PetUtil;
import com.skytonia.SkyCore.util.BUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.NumberConversions;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Chris Brown (OhBlihv) on 4/9/2017.
 */
public class FakeEntity
{
	
	//Movement Static Fields
	
	private static final double MOVEMENT_SPEED = 1.2D;
	private static final double MIN_DISTANCE = 1.0D;
	
	//
	
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
					currentZ = 0;
	
	@Getter
	@Setter
	private double  currentYaw = 0,
					currentPitch = 0;
	
	private double  targetX = 0,
					targetY = 0,
					targetZ = 0;
	
	public FakeEntity(EntityType entityType, Location location)
	{
		if(entityType == null)
		{
			throw new IllegalArgumentException("Unknown Entity Type Provided.");
		}
		
		this.entityType = entityType;
		
		//Make sure this entity appears where it should immediately
		teleport(location.getX(), location.getY(), location.getZ());
		updateTargetLocation(location);
	}
	
	public WrappedDataWatcher getMetadata()
	{
		WrappedDataWatcher metadata = PetUtil.getDefaultWatcher(currentWorld, entityType);
		
		metadata.setObject(2, "Example Name");
		metadata.setObject(3, Boolean.TRUE);
		
		return metadata;
	}
	
	public AbstractPacket getSpawnPacket()
	{
		if(metadata == null)
		{
			metadata = getMetadata();
		}
		
		AbstractPacket abstractPacket;
		
		if(LivingEntity.class.isAssignableFrom(entityType.getEntityClass()))
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
		/*if(EntityLiving.class.isAssignableFrom(entityType.getEntityClass()) ||
			   Entity.class.isAssignableFrom(entityType.getEntityClass()))
		{
			WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
			
			packet.setEntityIds(new int[] {entityId});
			
			return packet;
		}
		else
		{
			throw new IllegalArgumentException("Unsupported Entity Type '" + entityType + "' of class " + entityType.getEntityClass().getSimpleName());
		}*/
		
		WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
		
		packet.setEntityIds(new int[] {entityId});
		
		return packet;
	}
	
	public Location getLocation()
	{
		return new Location(currentWorld, currentX, currentY, currentZ, (float) currentYaw, (float) currentPitch);
	}
	
	public World getWorld()
	{
		return currentWorld;
	}
	
	public void updateTargetLocation(Location location)
	{
		updateTargetLocation(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}
	
	public void updateTargetLocation(World world, double x, double y, double z, float yaw, float pitch)
	{
		currentWorld = world;
		
		//Ignore target requests if they're too close
		if(Math.sqrt(NumberConversions.square(currentX - x) +
			         NumberConversions.square(currentY - y) +
			         NumberConversions.square(currentZ - z)) > MIN_DISTANCE)
		{
			targetX = x;
			targetY = y;
			targetZ = z;
		}
		
		currentYaw = yaw;
		currentPitch = pitch;
		
		updateCurrentLocation();
	}
	
	public void updateCurrentLocation()
	{
		//TODO: Make this more advanced. Currently goes directly to the target
		currentX = getMovementOnPlane(currentX, targetX);
		currentY = getMovementOnPlane(currentY, targetY);
		currentZ = getMovementOnPlane(currentZ, targetZ);
	}
	
	public void teleport(double x, double y, double z)
	{
		currentX = x;
		currentY = y;
		currentZ = z;
		
		targetX = currentX;
		targetY = currentY;
		targetZ = currentZ;
	}
	
	private double getMovementOnPlane(double currentLoc, double targetLoc)
	{
		double expectedMovementSpeed = currentLoc - targetLoc;
		if(expectedMovementSpeed > MOVEMENT_SPEED)
		{
			expectedMovementSpeed = MOVEMENT_SPEED;
		}
		else if(expectedMovementSpeed < (0 - MOVEMENT_SPEED))
		{
			expectedMovementSpeed = 0 - MOVEMENT_SPEED;
		}
		
		return expectedMovementSpeed;
	}
	
	private static final long MAX_MOVE_PACKET_DISTANCE = 32768L;
	private static final long MIN_MOVE_PACKET_DISTANCE = 0 - MAX_MOVE_PACKET_DISTANCE;
	
	public AbstractPacket moveEntity(final double x, final double y, final double z, float yaw, float pitch)
	{
		AbstractPacket abstractPacket = null;
		
		//Correct move distance numbers for packets
		/*double  deltaX = (currentX - x),
				deltaY = (currentY - y),
				deltaZ = (currentZ - z);
		
		BUtil.log("DeltaX=" + deltaX + " DeltaZ=" + deltaZ);
		
		if(deltaX == 0.0D && deltaY == 0.0D && deltaZ == 0.0D)
		{
			BUtil.log("Nothing.");
			return null;
		}
		
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
			
			packet.setOnGround(false);
			packet.setYaw((float) currentYaw);
			packet.setPitch((float) currentPitch);
			
			abstractPacket = packet;
			BUtil.log("Movement.");
		}*/
			
		updateTargetLocation(this.currentWorld, x, y, z, yaw, pitch);
		
		//Process teleport after we've updated the location for all
		if(abstractPacket == null)
		{
			BUtil.log("Teleporting.");
			abstractPacket = getTeleportPacket();
		}
		
		return abstractPacket;
	}
	
	public AbstractPacket getTeleportPacket()
	{
		WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport();
		
		packet.setEntityID(entityId);
		
		packet.setX((int) currentX);
		packet.setY((int) currentY);
		packet.setZ((int) currentZ);
		
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

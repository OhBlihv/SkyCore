package com.skytonia.SkyCore.cosmetics.util.ParticlePacket;

import com.skytonia.SkyCore.cheapobjects.player.CheapPlayer;
import com.skytonia.SkyCore.cosmetics.util.ParticleEffect;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

/**
 * Represents a particle effect packet with all attributes which is used for sending packets to the players
 * <p>
 * This class is part of the <b>ParticleEffect Library</b> and follows the same usage conditions
 *
 * @author DarkBlade12
 * @since 1.5
 *
 * First Edited by Chris Brown (OhBlihv) on 8/08/2016.
 * Uses DarkBlade12's original code adjusted for a multi-version modular project.
 */
public abstract class ParticlePacket
{
	
	/**
	 * While this is -1, this is assumed un-set.
	 * Upon selection of the correct NMS ParticlePacket class, this value
	 * is to be set to the correct value aligning with the server's Bukkit version.
	 */
	private static final int serverVersion = -1;
	
	//NMS Fields
	@Getter
	//Defines whether the NMS class implementation has been defined
	static boolean initialized = false;
	
	//Particle Fields
	final ParticleEffect effect;
	float offsetX;
	final float offsetY;
	final float offsetZ;
	final float speed;
	final int amount;
	final boolean longDistance;
	final ParticleEffect.ParticleData data;
	
	/* - - - - - - - - - - - - - - - - -
	 *          Constructors
	 * - - - - - - - - - - - - - - - - -*/
	
	/**
	 * Construct a new particle packet
	 *
	 * @param effect       Particle effect
	 * @param offsetX      Maximum distance particles can fly away from the center on the x-axis
	 * @param offsetY      Maximum distance particles can fly away from the center on the y-axis
	 * @param offsetZ      Maximum distance particles can fly away from the center on the z-axis
	 * @param speed        Display speed of the particles
	 * @param amount       Amount of particles
	 * @param longDistance Indicates whether the maximum distance is increased from 256 to 65536
	 * @param data         Data of the effect
	 * @throws IllegalArgumentException If the speed or amount is lower than 0
	 * @see #initialize()
	 */
	public ParticlePacket(ParticleEffect effect, float offsetX, float offsetY, float offsetZ, float speed, int amount, boolean longDistance, ParticleEffect.ParticleData data) throws IllegalArgumentException
	{
		if(speed < 0)
		{
			throw new IllegalArgumentException("The speed is lower than 0");
		}
		if(amount < 0)
		{
			throw new IllegalArgumentException("The amount is lower than 0");
		}
		this.effect = effect;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.speed = speed;
		this.amount = amount;
		this.longDistance = longDistance;
		this.data = data;
	}
	
	/**
	 * Construct a new particle packet of a single particle flying into a determined direction
	 *
	 * @param effect       Particle effect
	 * @param direction    Direction of the particle
	 * @param speed        Display speed of the particle
	 * @param longDistance Indicates whether the maximum distance is increased from 256 to 65536
	 * @param data         Data of the effect
	 * @throws IllegalArgumentException If the speed is lower than 0
	 * @see #ParticleEffect(ParticleEffect, float, float, float, float, int, boolean, ParticleEffect.ParticleData)
	 */
	public ParticlePacket(ParticleEffect effect, Vector direction, float speed, boolean longDistance, ParticleEffect.ParticleData data) throws IllegalArgumentException
	{
		this(effect, (float) direction.getX(), (float) direction.getY(), (float) direction.getZ(), speed, 0, longDistance, data);
	}
	
	/**
	 * Construct a new particle packet of a single colored particle
	 *
	 * @param effect       Particle effect
	 * @param color        Color of the particle
	 * @param longDistance Indicates whether the maximum distance is increased from 256 to 65536
	 * @see #ParticleEffect(ParticleEffect, float, float, float, float, int, boolean, ParticleEffect.ParticleData)
	 */
	public ParticlePacket(ParticleEffect effect, ParticleEffect.ParticleColor color, boolean longDistance)
	{
		this(effect, color.getValueX(), color.getValueY(), color.getValueZ(), 1, 0, longDistance, null);
		if(effect == ParticleEffect.REDSTONE && color instanceof ParticleEffect.OrdinaryColor && ((ParticleEffect.OrdinaryColor) color).getRed() == 0)
		{
			offsetX = 1 / 255F;
		}
	}
	
	public static int getVersion()
	{
		return serverVersion;
	}
	
	/* - - - - - - - - - - - - - - - - -
	 *    NMS ParticlePacket methods
	 * - - - - - - - - - - - - - - - - -*/
	
	/**
	 * Initializes the NMS packet and prepares the packet for sending by a #sendTo() function
	 */
	public abstract void initialize(Location center);
	
	/**
	 * Sends the packet to a single player and caches it
	 *
	 * @param center Center location of the effect
	 * @param player Receiver of the packet
	 * @throws PacketInstantiationException If instantion fails due to an unknown error
	 * @throws PacketSendingException       If sending fails due to an unknown error
	 * @see #initializePacket(Location)
	 */
	public abstract void sendTo(Location center, Player player) throws PacketInstantiationException, PacketSendingException;
	
	/**
	 * Sends the packet to a single player and caches it
	 *
	 * @param center Center location of the effect
	 * @param player Receiver of the packet
	 * @throws PacketInstantiationException If instantion fails due to an unknown error
	 * @throws PacketSendingException       If sending fails due to an unknown error
	 * @see #initializePacket(Location)
	 */
	public abstract void sendToCheapPlayer(Location center, CheapPlayer player) throws PacketInstantiationException, PacketSendingException;
	
	/**
	 * Sends the packet to all players in the list
	 *
	 * @param center  Center location of the effect
	 * @param players Receivers of the packet
	 * @throws IllegalArgumentException If the player list is empty
	 * @see #sendTo(Location center, Player player)
	 */
	public void sendTo(Location center, Collection<Player> players) throws IllegalArgumentException
	{
		if(players.isEmpty())
		{
			throw new IllegalArgumentException("The player list is empty");
		}
		
		for(Player player : players)
		{
			sendTo(center, player);
		}
	}
	
	/**
	 * Sends the packet to all players in the list
	 *
	 * @param center  Center location of the effect
	 * @param players Receivers of the packet
	 * @throws IllegalArgumentException If the player list is empty
	 * @see #sendTo(Location center, Player player)
	 */
	public void sendToCheapPlayer(Location center, Collection<CheapPlayer> players) throws IllegalArgumentException
	{
		if(players.isEmpty())
		{
			throw new IllegalArgumentException("The player list is empty");
		}
		
		for(CheapPlayer player : players)
		{
			sendToCheapPlayer(center, player);
		}
	}
	
	/**
	 * Sends the packet to all players in a certain range
	 *
	 * @param center Center location of the effect
	 * @param range  Range in which players will receive the packet (Maximum range for particles is usually 16, but it can differ for some types)
	 * @throws IllegalArgumentException If the range is lower than 1
	 * @see #sendTo(Location center, Player player)
	 */
	public void sendTo(Location center, double range) throws IllegalArgumentException
	{
		if(range < 1)
		{
			throw new IllegalArgumentException("The range is lower than 1");
		}
		double squared = range * range;
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(center.getWorld() != player.getWorld() ||
				   player.getLocation().distanceSquared(center) > squared)
			{
				continue;
			}
			sendTo(center, player);
		}
	}
	
	/* - - - - - - - - - - - - - - - - -
	 *    ParticlePacket Exceptions
	 * - - - - - - - - - - - - - - - - -*/
	
	/**
	 * Represents a runtime exception that is thrown if a bukkit version is not compatible with this library
	 * <p>
	 * This class is part of the <b>ParticleEffect Library</b> and follows the same usage conditions
	 *
	 * @author DarkBlade12
	 * @since 1.5
	 */
	public static final class VersionIncompatibleException extends RuntimeException
	{
		
		private static final long serialVersionUID = 3203085387160737484L;
		
		/**
		 * Construct a new version incompatible exception
		 *
		 * @param message Message that will be logged
		 * @param cause   Cause of the exception
		 */
		public VersionIncompatibleException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
	
	/**
	 * Represents a runtime exception that is thrown if packet instantiation fails
	 * <p>
	 * This class is part of the <b>ParticleEffect Library</b> and follows the same usage conditions
	 *
	 * @author DarkBlade12
	 * @since 1.4
	 */
	public static final class PacketInstantiationException extends RuntimeException
	{
		
		private static final long serialVersionUID = 3203085387160737484L;
		
		/**
		 * Construct a new packet instantiation exception
		 *
		 * @param message Message that will be logged
		 * @param cause   Cause of the exception
		 */
		public PacketInstantiationException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
	
	/**
	 * Represents a runtime exception that is thrown if packet sending fails
	 * <p>
	 * This class is part of the <b>ParticleEffect Library</b> and follows the same usage conditions
	 *
	 * @author DarkBlade12
	 * @since 1.4
	 */
	public static final class PacketSendingException extends RuntimeException
	{
		
		private static final long serialVersionUID = 3203085387160737484L;
		
		/**
		 * Construct a new packet sending exception
		 *
		 * @param message Message that will be logged
		 * @param cause   Cause of the exception
		 */
		public PacketSendingException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
	
}

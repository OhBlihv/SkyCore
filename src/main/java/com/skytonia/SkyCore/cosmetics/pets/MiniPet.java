package com.skytonia.SkyCore.cosmetics.pets;

import com.skytonia.SkyCore.cosmetics.objects.BaseCosmetic;
import com.skytonia.SkyCore.cosmetics.pets.entities.FakeEntity;
import com.skytonia.SkyCore.cosmetics.pets.entities.FakeLivingEntity;
import com.skytonia.SkyCore.cosmetics.pets.entities.FakeZombie;
import com.skytonia.SkyCore.cosmetics.pets.entities.pathfinders.FakePathfinderGoalFollowOwner;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Created by Chris Brown (OhBlihv) on 4/9/2017.
 */
public class MiniPet extends BaseCosmetic
{
	
	private static final int UPDATE_RATE = 1;
	private static final String COSMETIC_PREFIX = "PET_";
	
	//
	
	private final Player attachedPlayer;
	
	private final PetConfiguration petConfiguration;
	
	private FakeLivingEntity petEntity;
	private FakeEntity  snowBallOne,
						snowBallTwo;
	
	public MiniPet(Player attachedPlayer, PetConfiguration petConfiguration)
	{
		super(COSMETIC_PREFIX, UPDATE_RATE);
		
		this.attachedPlayer = attachedPlayer;
		this.petConfiguration = petConfiguration;
		
		petEntity = new FakeZombie(EntityType.ZOMBIE, attachedPlayer.getLocation(), false);
		petEntity.setPathfinderGoal(new FakePathfinderGoalFollowOwner(petEntity, ((CraftPlayer) attachedPlayer).getHandle(),
		                                                              2.0F, Integer.MAX_VALUE)); //TODO: Remove max range?
		
		snowBallOne = new FakeEntity(EntityType.SNOWBALL, attachedPlayer.getLocation());
		snowBallTwo = new FakeEntity(EntityType.SNOWBALL, attachedPlayer.getLocation());
	}
	
	@Override
	public void onTick(long tick, Location location)
	{
		throw new IllegalArgumentException("Unsupported Operation onTick() - Nearby Players are not tracked, please provide Nearby Players.");
	}
	
	@Override
	public void onTick(long tick, Location location, Collection<Player> nearbyPlayers)
	{
		petEntity.getPathfinderGoal().updateNav();
	}
	
	@Override
	public void removeCosmetic()
	{
	
	}
	
	@Override
	public boolean showToNearbyPlayer(Player player)
	{
		petEntity.getSpawnPacket().sendPacket(player);
		snowBallOne.getSpawnPacket().sendPacket(player);
		snowBallTwo.getSpawnPacket().sendPacket(player);
		
		petEntity.addPassenger(snowBallOne.getEntityId());
		petEntity.addPassenger(snowBallTwo.getEntityId());
		
		return true;
	}
	
	@Override
	public boolean removeFromNearbyPlayer(Player player)
	{
		petEntity.getDestroyPacket().sendPacket(player);
		snowBallOne.getDestroyPacket().sendPacket(player);
		snowBallTwo.getDestroyPacket().sendPacket(player);
		
		return true;
	}
}
